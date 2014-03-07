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
package org.jenetics.internal.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.jenetics.util.StaticObject;

/**
 * Helper methods concerning Java reflection.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.6 &mdash; <em>$Date: 2014-02-02 $</em>
 * @since 1.6
 */
public class reflect extends StaticObject {
	private reflect() {}

	/**
	 * Return all declared classes of the given class, with arbitrary nested
	 * level.
	 *
	 * @param cls the class for which the declared classes are retrieved.
	 * @return all nested classes
	 */
	public static List<Class<?>> allDeclaredClasses(final Class<?> cls) {
		final Deque<Class<?>> stack = new LinkedList<>();
		stack.addFirst(cls);

		final List<Class<?>> result = new ArrayList<>();
		while (!stack.isEmpty()) {
			final Class<?>[] classes = stack.pollFirst().getDeclaredClasses();
			for (final Class<?> c : classes) {
				result.add(c);
				stack.addFirst(c);
			}
		}

		return Collections.unmodifiableList(result);
	}

	/**
	 * Returns a Method object that reflects the specified public member method
	 * of the class or interface represented by this Class object.
	 *
	 * @param type the class for getting the desired method.
	 * @param name the method name
	 * @param parameterTypes the method parameter types.
	 * @return the method, or {@code null} if no such method can be found.
	 */
	public static Method getMethod(
		final Class<?> type,
		final String name,
		Class<?>[] parameterTypes
	) {
		Method method = null;
		final Method[] methods = type.getMethods();

		for (int i = 0; i < methods.length && method == null; ++i) {
			if (name.equals(methods[i].getName()) &&
				equals(parameterTypes, methods[i].getParameterTypes()))
			{
				method = methods[i];
			}
		}

		return method;
	}

	private static boolean equals(final Class<?>[] p1, final Class<?>[] p2) {
		boolean equals = p1.length == p2.length;
		for (int i = 0; i < p1.length && equals; ++i) {
			equals = toClassType(p1[i]) == toClassType(p2[i]);
		}

		return equals;
	}

	private static Class<?> toClassType(final Class<?> type) {
		switch (type.getCanonicalName()) {
			case "void": return Void.class;
			case "boolean": return Boolean.class;
			case "byte": return Byte.class;
			case "char": return Character.class;
			case "short": return Short.class;
			case "int": return Integer.class;
			case "long": return Long.class;
			case "float": return Float.class;
			case "double": return Double.class;
			default: return type;
		}
	}

	/**
	 * Return the class of the given value or the value if it is already from
	 * the type {@code Class}.
	 *
	 * @param value the value to get the class from.
	 * @return the class from the given value, or {@code value} if it is already
	 *         a {@code Class}.
	 */
	public static Class<?> classOf(final Object value) {
		return value instanceof Class<?> ? (Class<?>)value : value.getClass();
	}

}
