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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.util;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.function.Supplier;

/**
 * Object wrapper, which makes the wrapped value {@link Comparable}, by defining
 * a separate {@link Comparator}.
 *
 * @param <T> the type of the wrapped object
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.3
 * @since 6.3
 */
public final class Ordered<T> implements Comparable<Ordered<T>>, Supplier<T> {
	private final T _value;
	private final Comparator<? super T> _comparator;

	private Ordered(final T value, final Comparator<? super T> comparator) {
		_value = value;
		_comparator = requireNonNull(comparator);
	}

	/**
	 * Return the wrapped value.
	 *
	 * @return the wrapped value
	 */
	@Override
	public T get() {
		return _value;
	}

	@Override
	public int compareTo(final Ordered<T> other) {
		return _comparator.compare(_value, other._value);
	}

	/**
	 * Make the given {@code value} comparable, by using the given
	 * {@code comparator}.
	 *
	 * @param value the wrapped object, may be {@code null}
	 * @param comparator the comparator used for comparing two value objects
	 * @param <T> the type of the wrapped object
	 * @return a new ordered object
	 * @throws NullPointerException if the given {@code comparator} is
	 *         {@code null}
	 */
	public static <T> Ordered<T> of(
		final T value,
		final Comparator<? super T> comparator
	) {
		return new Ordered<>(value, comparator);
	}

}
