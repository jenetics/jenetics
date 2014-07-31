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

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.PackageDoc;
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

	private static final Predicate<Type> IsPublic = type -> (
		!type.qualifiedTypeName().startsWith("org.jenetics.internal") &&
		!type.qualifiedTypeName().startsWith("org.jenetix.internal")
	);

	private static final Set<PackageDoc> includedPackages = new HashSet<PackageDoc>();

	public static void main(final String[] args) {
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

	public static int optionLength(final String option) {
		return Standard.optionLength(option);
	}

	public static boolean start(final RootDoc root) throws IOException {
		final RootDoc newRootDoc = (RootDoc)process(root, RootDoc.class);

		for (PackageDoc packageDoc : root.specifiedPackages()) {
			if (containsPublicClasses(packageDoc)) {
				System.out.println("--------------------Package '" + packageDoc.name() + "' contains Public APIs");
				includedPackages.add(packageDoc);
			}
		}

		return Standard.start(newRootDoc);
	}

	private static boolean exclude(final Doc doc) {
		boolean exclude = false;

		if (doc instanceof ClassDoc) {
			exclude = !IsPublic.test((ClassDoc) doc);
		} else if (doc instanceof PackageDoc) {
			exclude = !includedPackages.contains(doc);
		}

		return exclude;
	}

	private static boolean containsPublicClasses(final PackageDoc doc) {
		boolean hasPublic = false;
		for (ClassDoc classDoc : doc.allClasses()) {
			hasPublic = IsPublic.test(classDoc);
			if (hasPublic) {
				//System.out.println("Excluding package '" + packageDoc.name() + "' as it contains no Classes that are Public APIs");
				break;
			}
		}
		return hasPublic;
	}

	private static Object process(final Object obj, final Class expect) {
		if (obj == null) return null;

		final Class cls = obj.getClass();
		if (cls.getName().startsWith("com.sun.")) {
			return Proxy.newProxyInstance(
				cls.getClassLoader(),
				cls.getInterfaces(),
				new ExcludeHandler(obj)
			);
		} else if (obj instanceof Object[]) {
			final Class componentType = expect.getComponentType();
			final Object[] array = (Object[]) obj;
			final List<Object> list = new ArrayList<>(array.length);

			for (int i = 0; i < array.length; i++) {
				final Object entry = array[i];
				if ((entry instanceof Doc) && exclude((Doc) entry)) {
					continue;
				}
				list.add(process(entry, componentType));
			}
			return list.toArray((Object[]) Array.newInstance(componentType, list.size()));
		} else {
			return obj;
		}
	}

	private static class ExcludeHandler implements InvocationHandler {
		private final Object _target;

		public ExcludeHandler(final Object target) {
			_target = requireNonNull(target);
		}

		public Object invoke(Object proxy, Method method, Object[] args)
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
			return proxy instanceof Proxy ?
				((ExcludeHandler)Proxy.getInvocationHandler(proxy))._target :
				proxy;
		}
	}
}
