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

import java.util.function.ToDoubleBiFunction;

/**
 * Defines the distance of two elements of a given <em>vector</em> type {@code V}.
 * The following example creates an {@code ElementDistance} function for a
 * {@code double[] array}:
 * <pre>{@code
 * final ElementDistance<double[]> dist = (u, v, i) -> u[i] - v[i];
 * }</pre>
 *
 * @param <V> the vector type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface ElementDistance<V> {

	/**
	 * Calculates the distance of two vector elements at the given {@code index}.
	 * E.g.
	 * <pre>{@code
	 * final ElementDistance<double[]> dist = (u, v, i) -> u[i] - v[i];
	 * }</pre>
	 *
	 * @param u the first vector
	 * @param v the second vector
	 * @param index the vector index
	 * @return the distance of the two element vectors
	 */
	public double distance(final V u, final V v, final int index);

	/**
	 * Return a function which calculates the distance of two vector elements.
	 *
	 * @param index the vector index
	 * @return a function which calculates the distance of two vector elements
	 */
	public default ToDoubleBiFunction<V, V> ofIndex(final int index) {
		return (u, v) -> distance(u, v, index);
	}

}
