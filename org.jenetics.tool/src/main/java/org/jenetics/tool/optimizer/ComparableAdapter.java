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
package org.jenetics.tool.optimizer;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Comparator;

/**
 * Wraps an (possible) non {@link Comparable} object of type {@code T} into an
 * {@link Comparable} object. The comparison is performed by the given comparator.
 *
 * @param <T> the adapted object type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ComparableAdapter<T>
	implements Comparable<ComparableAdapter<T>>
{

	private final T _adoptee;
	private final Comparator<? super T> _comparator;

	private ComparableAdapter(
		final T adoptee,
		final Comparator<? super T> comparator
	) {
		_adoptee = requireNonNull(adoptee);
		_comparator = requireNonNull(comparator);
	}

	/**
	 * Return the adopted object.
	 *
	 * @return the adopted object
	 */
	public T getAdoptee() {
		return _adoptee;
	}

	@Override
	public int compareTo(final ComparableAdapter<T> other) {
		return _comparator.compare(_adoptee, other._adoptee);
	}

	@Override
	public int hashCode() {
		return _adoptee.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof ComparableAdapter<?> &&
			_adoptee.equals(((ComparableAdapter<?>)obj)._adoptee);
	}

	@Override
	public String toString() {
		return format("ComparableAdapter[%s]", _adoptee);
	}

	/**
	 * Create a new {@link Comparable} adapter from the given {@code adoptee}
	 * and {@code comparator}.
	 *
	 * @param adoptee the value to adopt
	 * @param comparator the comparator used for comparing two adapted values
	 * @param <T> the adoptee type
	 * @return a new {@link Comparable} adapter
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T> ComparableAdapter<T> of(
		final T adoptee,
		final Comparator<? super T> comparator
	) {
		return new ComparableAdapter<>(adoptee, comparator);
	}

}
