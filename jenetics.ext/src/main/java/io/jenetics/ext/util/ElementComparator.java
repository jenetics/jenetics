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
package io.jenetics.ext.util;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Defines the order of two elements of a given <em>vector</em> type {@code V}.
 * The following example creates an {@code ElementComparator} function for a
 * {@code double[] array}:
 * <pre>{@code
 * final ElementComparator<double[]> comp =
 *     (u, v, i) -> Double.compare(u[i], v[i]);
 * }</pre>
 *
 * @param <V> the vector type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface ElementComparator<V> {

	/**
	 * Compares the components of {@code a} and {@code b} at the given
	 * {@code index}. E.g.
	 * <pre>{@code
	 * final ElementComparator<double[]> comp =
	 *     (u, v, i) -> Double.compare(u[i], v[i]);
	 * }</pre>
	 *
	 * @param u the first vector
	 * @param v the second vector
	 * @param index the vector index
	 * @return a negative integer, zero, or a positive integer as the
	 *         first argument is less than, equal to, or greater than the
	 *         second.
	 * @throws NullPointerException if either {@code a} or {@code b} is
	 *        {@code null}
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code (index < 0 || index >= length(a)  || index >= length(b))}
	 */
	public int compare(final V u, final V v, final int index);

	public default <T> ElementComparator<T>
	map(final Function<? super T, ? extends V> mapper) {
		return (u, v, i) -> compare(mapper.apply(u), mapper.apply(v), i);
	}

	/**
	 * Return a comparator which takes the component at the give {@code index}
	 * for comparison two objects of type {@code T}.
	 *
	 * @param index the component index
	 * @return the component comparator for the given {@code index}
	 */
	public default Comparator<V> ofIndex(final int index) {
		return (a, b) -> compare(a, b, index);
	}

}
