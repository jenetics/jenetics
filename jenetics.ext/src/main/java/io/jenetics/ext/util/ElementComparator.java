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

/**
 * Defines the order of the components of a given type {@code T}.
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
	 * {@code index}.
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

	/**
	 * Return a comparator which takes the component at the give {@code index}
	 * for comparison two objects of type {@code T}.
	 *
	 * @param index the component index
	 * @return the component comparator for the given {@code index}
	 */
	public default Comparator<V> curry(final int index) {
		return (a, b) -> compare(a, b, index);
	}

}
