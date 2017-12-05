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

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Comparator;

import io.jenetics.internal.util.IndexSorter;
import io.jenetics.util.Seq;

/**
 * Defines the needed methods for a multi-objective fitness value.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface MOV<T> extends ComponentComparable<T> {

	/**
	 * Return the underlying data structure.
	 *
	 * @return the underlying data structure
	 */
	public T value();

	/**
	 * Return the number of objectives.
	 *
	 * @return the number of objectives
	 */
	public int size();

	/**
	 *
	 * @param index
	 * @param other
	 * @return
	 */
	public default int compareTo(final int index, final T other) {
		return 0;
	}

	/**
	 *
	 * @param other
	 * @return
	 */
	public int domination(final T other);

	public default boolean dominates(final T other) {
		return domination(other) > 0;
	}

	public default boolean dominated(final T other) {
		return domination(other) < 0;
	}

	default int rank(final Seq<T> population) {
		domination(population.get(0));
		return 0;
	}

	default int[] distances(final Seq<T> population) {
		return null;
	}

	public static <T> double[] crowdingDistances(final Seq<? extends MOV<T>> front) {
		final double[] distances = new double[front.size()];

		return distances;
	}

	public static <T> double[] crowdingDistances(
		final Seq<? extends T> front,
		final int dimensions,
		final ComponentComparator<? super T> comparator
	) {
		final double[] distances = new double[front.size()];

		if (distances.length < 3) {
			Arrays.fill(distances, Double.POSITIVE_INFINITY);
		} else {
			Arrays.fill(distances, 0);
			final IndexSorter sorter = IndexSorter.sorter(front.size());
			final int[] indexes = new int[front.size()];

			for (int i = 0; i < dimensions; ++i) {
				final int objective = i;
				sorter.sort(
					front,
					IndexSorter.init(indexes),
					(a, b) -> comparator.compare(objective, a, b)
				);

				distances[indexes[0]] = Double.POSITIVE_INFINITY;
				distances[indexes[indexes.length - 1]] = Double.POSITIVE_INFINITY;

				for (int j = 1; j < indexes.length - 1; ++j) {
					distances[indexes[j]] += 1;
				}
			}
		}

		return distances;
	}

	public static MOV<double[]> of(final double[] value) {
		return new DoubleMOV(value);
	}

}

final class DoubleMOV implements MOV<double[]> {

	private final double[] _value;

	DoubleMOV(final double[] value) {
		_value = requireNonNull(value);
	}

	@Override
	public double[] value() {
		return _value;
	}

	@Override
	public int size() {
		return _value.length;
	}

	@Override
	public int domination(final double[] other) {
		return 0;
	}
}
