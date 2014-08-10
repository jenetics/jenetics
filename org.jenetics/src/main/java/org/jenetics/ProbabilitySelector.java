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
package org.jenetics;

import static java.lang.Math.abs;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.util.math.pow;
import static org.jenetics.util.math.statistics.sum;
import static org.jenetics.util.math.ulpDistance;

import java.util.Random;

import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;


/**
 * Probability selectors are a variation of fitness proportional selectors and
 * selects individuals from a given population based on it's selection
 * probability <i>P(i)</i>.
 * <p>
 * <img src="doc-files/FitnessProportionalSelection.svg" width="400" alt="Selection">
 * <p>
 * Fitness proportional selection works as shown in the figure above. The
 * runtime complexity of the implemented probability selectors is
 * <i>O(n+</i>log<i>(n))</i> instead of <i>O(n<sup>2</sup>)</i> as for the naive
 * approach: <i>A binary (index) search is performed on the summed probability
 * array.</i>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2014-08-10 $</em>
 */
public abstract class ProbabilitySelector<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Selector<G, C>
{
	private static final long MAX_ULP_DISTANCE = pow(10, 10);

	protected ProbabilitySelector() {
	}

	@Override
	public Population<G, C> select(
		final Population<G, C> population,
		final int count,
		final Optimize opt
	) {
		requireNonNull(population, "Population");
		requireNonNull(opt, "Optimization");
		if (count < 0) {
			throw new IllegalArgumentException(format(
				"Selection count must be greater or equal then zero, but was %s.",
				count
			));
		}

		final Population<G, C> selection = new Population<>(count);

		if (count > 0) {
			final double[] probabilities = probabilities(population, count, opt);
			assert (population.size() == probabilities.length) :
				"Population size and probability length are not equal.";
			assert (sum2one(probabilities)) : "Probabilities doesn't sum to one.";

			incremental(probabilities);
			final Factory<Phenotype<G, C>> factory = factory(
				population, probabilities, RandomRegistry.getRandom()
			);

			selection.fill(factory, count);
			assert (count == selection.size());
		}

		return selection;
	}

	private static <
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
	Factory<Phenotype<G, C>> factory(
		final Population<G, C> population,
		final double[] probabilities,
		final Random random
	) {
		return new Factory<Phenotype<G, C>>() {
			@Override
			public Phenotype<G, C> newInstance() {
				return select(population, probabilities, random);
			}
		};
	}

	private static <
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
	Phenotype<G, C> select(
		final Population<G, C> population,
		final double[] probabilities,
		final Random random
	) {
		final double value = random.nextDouble();
		return population.get(indexOf(probabilities, value));
	}

	/**
	 * This method takes the probabilities from the
	 * {@link #probabilities(Population, int)} method and inverts it if needed.
	 *
	 * @param population The population.
	 * @param count The number of phenotypes to select.
	 * @param opt Determines whether the individuals with higher fitness values
	 *        or lower fitness values must be selected. This parameter
	 *        determines whether the GA maximizes or minimizes the fitness
	 *        function.
	 * @return Probability array.
	 */
	protected final double[] probabilities(
		final Population<G, C> population,
		final int count,
		final Optimize opt
	) {
		return opt == Optimize.MINIMUM ?
			revert(probabilities(population, count)) :
			probabilities(population, count);
	}

	// Package private for testing.
	static double[] revert(final double[] probabilities) {
		final int N = probabilities.length;
		final int[] indexes = sort(probabilities);
		final double[] result = new double[N];

		for (int i = 0; i < N; ++i) {
			result[indexes[N - i - 1]] = probabilities[indexes[i]];
		}

		return result;
	}

	private static final int INSERTION_SORT_THRESHOLD = 75;

	// Package private for testing.
	static int[] sort(final double[] values) {
		return values.length < INSERTION_SORT_THRESHOLD ?
			insertionSort(values) :
			quickSort(values);
	}

	private static int[] indexes(final int length) {
		final int[] indexes = new int[length];
		for (int i = 0; i < indexes.length; ++i) {
			indexes[i] = i;
		}
		return indexes;
	}

	// Package private for testing.
	static int[] quickSort(final double[] array) {
		final int[] indexes = indexes(array.length);
		quickSort(array, indexes, 0, array.length - 1);
		return indexes;
	}

	private static void quickSort(
		final double[] array,
		final int[] indexes,
		final int left, final int right
	) {
		if (right > left) {
			final int j = partition(array, indexes, left, right);
			quickSort(array, indexes, left, j - 1);
			quickSort(array, indexes, j + 1, right);
		}
	}

	private static int partition(
		final double[] array, final int[] indexes,
		final int left, final int right
	) {
		final double pivot = array[indexes[left]];
		int i = left;
		int j = right + 1;

		while (true) {
			do ++i; while (i < right && array[indexes[i]] < pivot);
			do --j; while (j > left && array[indexes[j]] > pivot);
			if (j <= i) break;
			swap(indexes, i, j);
		}
		swap(indexes, left, j);

		return j;
	}

	private static void swap(final int[] indexes, final int i, final int j) {
		final int temp = indexes[i];
		indexes[i] = indexes[j];
		indexes[j] = temp;
	}

	// Package private for testing.
	static int[] insertionSort(final double[] array) {
		final int[] indexes = indexes(array.length);

		for (int sz = array.length, i = 1; i < sz; ++i) {
			int j = i;
			while (j > 0) {
				if (array[indexes[j - 1]] > array[indexes[j]]) {
					swap(indexes, j - 1, j);
				} else {
					break;
				}
				--j;
			}
		}

		return indexes;
	}

	/**
	 * <p>
	 * Return an Probability array, which corresponds to the given Population.
	 * The probability array and the population must have the same size. The
	 * population is not sorted. If a subclass needs a sorted population, the
	 * subclass is responsible to sort the population.
	 * </p>
	 * The implementer always assumes that higher fitness values are better. The
	 * base class inverts the probabilities ({@code p = 1.0 - p }) if the GA is
	 * supposed to minimize the fitness function.
	 *
	 * @param population The <em>unsorted</em> population.
	 * @param count The number of phenotypes to select. <i>This parameter is not
	 *        needed for most implementations.</i>
	 * @return Probability array. The returned probability array must have the
	 *         length {@code population.size()} and <strong>must</strong> sum to
	 *         one. The returned value is checked with
	 *         {@code assert(Math.abs(math.sum(probabilities) - 1.0) < 0.0001)}
	 *         in the base class.
	 */
	protected abstract double[] probabilities(
		final Population<G, C> population,
		final int count
	);

	/**
	 * Check if the given probabilities sum to one.
	 *
	 * @param probabilities the probabilities to check.
	 * @return {@code true} if the sum of the probabilities are within the error
	 *         range, {@code false} otherwise.
	 */
	static boolean sum2one(final double[] probabilities) {
		final double sum = sum(probabilities);
		return abs(ulpDistance(sum, 1.0)) < MAX_ULP_DISTANCE;
	}

	/**
	 * Perform a binary-search on the summed probability array.
	 */
	static int indexOf(final double[] incremental, final double v) {
		int imin = 0;
		int imax = incremental.length;

		while (imax > imin) {
			int imid = (imin + imax) >>> 1;

			if (imid == 0) {
				return imid;
			} else if (incremental[imid] >= v && incremental[imid - 1] < v) {
				return imid;
			} else if (incremental[imid] <= v) {
				imin = imid + 1;
			} else if (incremental[imid] > v) {
				imax = imid;
			}
		}

		return incremental.length - 1;
	}

	/**
	 * In-place summation of the probability array.
	 */
	static double[] incremental(final double[] values) {
		for (int i = 1; i < values.length; ++i) {
			values[i] = values[i - 1] + values[i];
		}
		return values;
	}

}
