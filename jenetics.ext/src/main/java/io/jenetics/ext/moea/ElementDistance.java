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
package io.jenetics.ext.moea;

import java.util.function.Function;
import java.util.function.ToDoubleBiFunction;

/**
 * Defines the distance of two elements of a given <em>vector</em> type {@code V}.
 * The following example creates an {@code ElementDistance} function for a
 * {@code double[] array}:
 * {@snippet lang="java":
 * final ElementDistance<double[]> dist = (u, v, i) -> u[i] - v[i];
 * }
 *
 * @param <V> the vector type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 4.1
 */
@FunctionalInterface
public interface ElementDistance<V> {

	/**
	 * Calculates the distance of two vector elements at the given {@code index}.
	 * E.g.
	 * {@snippet lang="java":
	 * final ElementDistance<double[]> dist = (u, v, i) -> u[i] - v[i];
	 * }
	 *
	 * @param u the first vector
	 * @param v the second vector
	 * @param index the vector index
	 * @return the distance of the two element vectors
	 */
	double distance(final V u, final V v, final int index);

	/**
	 * Return an element distance function for the mapped type {@code T}.
	 *
	 * @param mapper the mapper function
	 * @param <T> the new distance type
	 * @return an element distance function for the mapped type {@code T}
	 */
	default <T> ElementDistance<T>
	map(final Function<? super T, ? extends V> mapper) {
		return (u, v, i) -> distance(mapper.apply(u), mapper.apply(v), i);
	}

	/**
	 * Return a function which calculates the distance of two vector elements at
	 * a given {@code index}.
	 *
	 * @param index the vector index
	 * @return a function which calculates the distance of two vector elements
	 */
	default ToDoubleBiFunction<V, V> ofIndex(final int index) {
		return (u, v) -> distance(u, v, index);
	}

}
