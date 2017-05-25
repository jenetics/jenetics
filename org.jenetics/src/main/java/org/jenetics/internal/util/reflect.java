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

import static java.util.Arrays.stream;
import static java.util.stream.Stream.concat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Helper methods concerning Java reflection.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.6
 * @version 3.7
 */
public class reflect {
	private reflect() {require.noInstance();}

	/**
	 * Reflectively sets the field with the given {@code name} to the new
	 * {@code value}. The new value is set to the first found field in the
	 * whole class hierarchy.
	 *
	 * @since 3.7
	 *
	 * @param target the object which owns the field
	 * @param name the field name
	 * @param value the new field value
	 * @throws IllegalArgumentException if no field with the given {@code name}
	 *         can be found or it's not allowed to set the field
	 */
	public static void setField(
		final Object target,
		final String name,
		final Object value
	) {
		final Field field = findField(target.getClass(), name)
			.orElseThrow(() -> new IllegalArgumentException(name + " not found."));

		try {
			field.setAccessible(true);
			field.set(target, value);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static Optional<Field> findField(
		final Class<?> cls,
		final String name
	) {
		return allFields(cls)
			.filter(f -> f.getName().equals(name))
			.findFirst();
	}

	private static Stream<Field> allFields(final Class<?> cls) {
		return allClasses(cls).flatMap(c -> Stream.of(c.getDeclaredFields()));
	}

	private static Stream<Class<?>> allClasses(final Class<?> cls) {
		return Stream.concat(
			Stream.of(cls),
			Optional.ofNullable(cls.getSuperclass())
				.map(reflect::allClasses)
				.orElse(Stream.empty())
		);
	}

	/**
	 * Return all declared classes of the given class, with arbitrary nested
	 * level.
	 *
	 * @param cls the class for which the declared classes are retrieved.
	 * @return all nested classes
	 */
	public static Stream<Class<?>> innerClasses(final Class<?> cls) {
		return concat(
			stream(cls.getDeclaredClasses()).flatMap(reflect::innerClasses),
			stream(cls.getDeclaredClasses())
		);
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

	@SuppressWarnings("unchecked")
	public static <T> Optional<T> newInstance(final Class<?> type) {
		try {
			return Optional.of((T)type.getConstructor().newInstance());
		} catch (NoSuchMethodException |
				InvocationTargetException |
				InstantiationException |
				IllegalAccessException e)
		{
			return Optional.empty();
		}
	}

}
