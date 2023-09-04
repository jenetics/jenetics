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

import static java.lang.Double.POSITIVE_INFINITY;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;

import io.jenetics.util.BaseSeq;
import io.jenetics.util.ProxySorter;

/**
 * Implementation of methods used for the NSGA2 algorithm.
 *
 * @see <a href="https://www.iitk.ac.in/kangal/Deb_NSGA-II.pdf">
 *     A Fast and Elitist Multiobjective Genetic Algorithm: NSGA-II</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class NSGA2 {
	private NSGA2() {
	}

	/**
	 * The crowding distance value of a solution provides an estimate of the
	 * solution density surrounding that solution. The <em>crowding
	 * distance</em> value of a particular solution is the average distance of
	 * its two neighboring solutions.
	 *
	 * @apiNote
	 * Calculating the crowding distance has a time complexity of
	 * {@code O(d*n*log(n))}, where {@code d} is the number of dimensions and
	 * {@code n} the {@code set} size.
	 *
	 * @param set the point set used for calculating the <em>crowding distance</em>
	 * @param comparator the comparator which defines the (total) order of the
	 *        vector elements of {@code T}
	 * @param distance the distance of two vector elements
	 * @param objectives the dimension of vector type {@code T}
	 * @param <T> the vector type
	 * @return the crowded distances of the {@code set} points
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T> double[] crowdingDistance(
		final BaseSeq<? extends T> set,
		final ElementComparator<? super T> comparator,
		final ElementDistance<? super T> distance,
		final int objectives
	) {
		requireNonNull(set);
		requireNonNull(comparator);
		requireNonNull(distance);

		final double[] result = new double[set.length()];

		if (set.length() < 3) {
			Arrays.fill(result, POSITIVE_INFINITY);
		} else {
			for (int m = 0; m < objectives; ++m) {
				final int[] idx = ProxySorter.sort(
					set,
					comparator.ofIndex(m).reversed()
				);

				result[idx[0]] = POSITIVE_INFINITY;
				result[idx[set.length() - 1]] = POSITIVE_INFINITY;

				final T max = set.get(idx[0]);
				final T min = set.get(idx[set.length() - 1]);
				final double dm = distance.distance(max, min, m);

				if (Double.compare(dm, 0) > 0) {
					for (int i = 1, n = set.length() - 1; i < n; ++i) {
						final double dist = distance.distance(
							set.get(idx[i - 1]),
							set.get(idx[i + 1]),
							m
						);

						result[idx[i]] += dist/dm;
					}
				}
			}
		}

		return result;
	}


	/**
	 * The crowding distance value of a solution provides an estimate of the
	 * density of solutions surrounding that solution. The <em>crowding
	 * distance</em> value of a particular solution is the average distance of
	 * its two neighboring solutions.
	 *
	 * @apiNote
	 * Calculating the crowding distance has a time complexity of
	 * {@code O(d*n*log(n))}, where {@code d} is the number of dimensions and
	 * {@code n} the {@code set} size.
	 *
	 * @see #crowdingDistance(BaseSeq, ElementComparator, ElementDistance, int)
	 *
	 * @param set the point set used for calculating the <em>crowding distance</em>
	 * @param <T> the vector type
	 * @return the crowded distances of the {@code set} points
	 * @throws NullPointerException if the input {@code set} is {@code null}
	 * @throws IllegalArgumentException if {@code set.get(0).length() < 2}
	 */
	public static <T> double[]
	crowdingDistance(final BaseSeq<? extends Vec<T>> set) {
		if (set.isEmpty()) {
			return new double[0];
		} else {
			return crowdingDistance(
				set,
				Vec::compare,
				Vec::distance,
				set.get(0).length()
			);
		}
	}

}
