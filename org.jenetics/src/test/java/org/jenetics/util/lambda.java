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
package org.jenetics.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jenetics.internal.util.reflect;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-02-15 $</em>
 */
public final class lambda extends StaticObject {
	private lambda() {}


	public static <T> Factory<T> factory(
		final Object object,
		final String name,
		final Object... parameter
	) {
		final Class<?> type = object instanceof Class<?> ?
			(Class<?>)object : object.getClass();

		final Object value = object instanceof Class<?> ? null : object;

		final Class<?>[] ptypes = new Class<?>[parameter.length];
		for (int i = 0; i < ptypes.length; ++i) {
			ptypes[i] = parameter[i].getClass();
		}

		final Method method = reflect.getMethod(type, name, ptypes);
		if (method == null) {
			throw new RuntimeException(String.format(
				"Method %s.%s%s not found.",
				type.getCanonicalName(), name, argumentTypesToString(ptypes)
			));
		}

		return new FactoryMethod<>(
			value,
			method,
			parameter
		);
	}

	private static String argumentTypesToString(Class<?>[] argTypes) {
		final StringBuilder buf = new StringBuilder();
		buf.append("(");
		if (argTypes != null) {
			for (int i = 0; i < argTypes.length; i++) {
				if (i > 0) {
					buf.append(", ");
				}
				Class<?> c = argTypes[i];
				buf.append((c == null) ? "null" : c.getName());
			}
		}
		buf.append(")");
		return buf.toString();
	}

	@SuppressWarnings("unchecked")
	private static final class FactoryMethod<T> implements Factory<T> {
		private final Object _object;
		private final Method _method;
		private final Object[] _parameters;

		FactoryMethod(
			final Object object,
			final Method method,
			final Object... parameters
		) {
			_object = object;
			_method = method;
			_parameters = parameters;
		}

		@Override
		public T newInstance() {
			try {
				return (T)_method.invoke(_object, _parameters);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof RuntimeException) {
					throw (RuntimeException)e.getTargetException();
				} else {
					throw new RuntimeException(e);
				}
			}
		}
	}

}
