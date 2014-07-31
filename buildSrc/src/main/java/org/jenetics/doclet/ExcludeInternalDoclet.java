/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.doclet;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.standard.Standard;
import com.sun.tools.javadoc.Main;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-07-31 $</em>
 */
public class ExcludeInternalDoclet {

	private static final String EXCLUDE_TAG = "@internal";

	private static final Predicate<Type> TYPE_FILTER = type -> (
		!type.qualifiedTypeName().startsWith("org.jenetics.internal") &&
		!type.qualifiedTypeName().startsWith("org.jenetix.internal")
	);

	public static void main(String[] args) {
		String name = ExcludeInternalDoclet.class.getName();
		Main.execute(name, name, args);
	}

	public static boolean validOptions(
		final String[][] options,
		final DocErrorReporter reporter
	)
		throws IOException
	{
		return Standard.validOptions(options, reporter);
	}

	public static LanguageVersion languageVersion() {
		return LanguageVersion.JAVA_1_5;
	}

	public static int optionLength(final String option) {
		return Standard.optionLength(option);
	}

	public static boolean start(final RootDoc root) throws IOException {
		return Standard.start((RootDoc)process(root, RootDoc.class));
	}

	private static boolean exclude(final Doc doc) {
		if (doc.name().contains("UnitTest")) {
			return true;
		} else if (doc.tags(EXCLUDE_TAG).length > 0) {
			return true;
		} else if (doc instanceof Type) {
			if (!TYPE_FILTER.test((Type)doc)) {
				System.out.println("Excluded: '" + doc);
				return true;
			}
		}

		return false;
	}

	private static Object process(final Object obj, final Class expect) {
		if (obj == null) {
			return null;
		}

		final Class cls = obj.getClass();
		if (cls.getName().startsWith("com.sun.")) {
			return Proxy.newProxyInstance(
				cls.getClassLoader(),
				cls.getInterfaces(),
				new ExcludeHandler(obj)
			);
		} else if (obj instanceof Object[]) {
			final Class<?> componentType = expect.getComponentType();
			final Object[] array = (Object[]) obj;
			final List<Object> list = new ArrayList<>(array.length);

			for (int i = 0; i < array.length; i++) {
				Object entry = array[i];
				if (entry instanceof Type) {
					if ((entry instanceof Doc) && exclude((Doc)entry)) {
						continue;
					}
				}

				list.add(process(entry, componentType));
			}

			return list.toArray(
				(Object[])Array.newInstance(componentType, list.size())
			);
		} else {
			return obj;
		}
	}

	private static class ExcludeHandler implements InvocationHandler {
		private Object _target;

		public ExcludeHandler(final Object target) {
			_target = target;
		}

		public Object invoke(final Object proxy, final Method method, final Object[] args)
			throws Throwable
		{
			if (args != null) {
				final String methodName = method.getName();
				if (methodName.equals("compareTo") ||
					methodName.equals("equals") ||
					methodName.equals("overrides") ||
					methodName.equals("subclassOf"))
				{
					args[0] = unwrap(args[0]);
				}
			}

			try {
				return process(method.invoke(_target, args), method.getReturnType());
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}

		private Object unwrap(final Object proxy) {
			if (proxy instanceof Proxy) {
				return ((ExcludeHandler)Proxy.getInvocationHandler(proxy))._target;
			}
			return proxy;
		}
	}
}
