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

import java.util.Optional;
import java.util.stream.Stream;

import org.jenetics.util.ISeq;

/**
 * Helper methods concerning Java reflection.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.6
 * @version 3.0
 */
public class reflect {
	private reflect() {require.noInstance();}

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
			return Optional.of((T)type.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			return Optional.empty();
		}
	}

	@SuppressWarnings("unchecked")
	public static <A, B extends A> ISeq<A> cast(final ISeq<B> seq) {
		return (ISeq<A>)seq;
	}
}
