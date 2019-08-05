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

/**
 * The comparator used for comparing two array elements at the specified
 * indexes.
 * <pre>{@code
 * final ProxyComparator<double[]> comparator =
 *     (a, i, j) -> Double.compare(a[i], a[j]);
 * }</pre>
 * The example above shows how to create a comparator for {@code double[]}
 * arrays.
 *
 * @see ProxySorter
 *
 * @param <T> the array type, e.g. {@code int[]}, {@code double[]} or
 *            {@code Seq<String>}
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface ProxyComparator<T> {

	/**
	 * Compares the two array elements, specified by its indices, for order.
	 * Returns a negative integer, zero, or a positive integer as the first
	 * argument is less than, equal to, or greater than the second.
	 *
	 * @see java.util.Comparator#compare(Object, Object)
	 *
	 * @param array the array where the two comparing elements are fetched
	 * @param i the index of the first array element
	 * @param j the index of the second array element
	 * @return a negative integer, zero, or a positive integer as the first
	 *         argument is less than, equal to, or greater than the second.
	 * @throws NullPointerException if an argument is null and this
	 *         comparator does not permit null arguments
	 */
	public int compare(final T array, final int i, final int j);

	/**
	 * Returns a comparator that imposes the reverse ordering of this
	 * comparator.
	 *
	 * @return a comparator that imposes the reverse ordering of this
	 *         comparator.
	 */
	public default ProxyComparator<T> reversed() {
		return (a, i, j) -> compare(a, j, i);
	}

}
