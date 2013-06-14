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

import static java.lang.Math.min;
import static java.lang.String.format;
import static org.jenetics.util.object.hashCodeOf;

import java.util.Random;

import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.arrays;

/**
 * <strong><p>Multiple point crossover</p></strong>
 *
 * If the {@code MultiPointCrossover} is created with one crossover point, it
 * behaves exactly like the {@link SinglePointCrossover}. The following picture
 * shows how the {@code MultiPointCrossover} works with two crossover points,
 * defined at index 1 and 4.
 * <p><div align="center">
 *	<img src="doc-files/2PointCrossover.svg" width="400" >
 * </div></p>
 *
 * If the number of crossover points is odd, the crossover looks like in the
 * following figure.
 *
 * <p><div align="center">
 *	<img src="doc-files/3PointCrossover.svg" width="400" >
 * </div></p>
 *
 * @see SinglePointCrossover
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.2
 * @version 1.2 &mdash; <em>$Date: 2013-06-14 $ </em>
 */
public class MultiPointCrossover<G extends Gene<?, G>> extends Crossover<G> {

	private final int _n;

	/**
	 * Create a new crossover instance.
	 *
	 * @param probability the recombination probability.
	 * @param n the number of crossover points.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *         valid range of {@code [0, 1]} or {@code n < 1}.
	 */
	public MultiPointCrossover(final double probability, final int n) {
		super(probability);
		if (n < 1) {
			throw new IllegalArgumentException(format(
				"n must be at least 1 but was %d.", n
			));
		}
		_n = n;
	}

	/**
	 * Create a new crossover instance with two crossover points.
	 *
	 * @param probability the recombination probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *         valid range of {@code [0, 1]}.
	 */
	public MultiPointCrossover(final double probability) {
		this(probability, 2);
	}

	/**
	 * Create a new crossover instance with default crossover probability of
	 * 0.05.
	 *
	 * @param n the number of crossover points.
	 * @throws IllegalArgumentException if {@code n < 1}.
	 */
	public MultiPointCrossover(final int n) {
		this(0.05, n);
	}

	/**
	 * Create a new crossover instance with two crossover points and crossover
	 * probability 0.05.
	 */
	public MultiPointCrossover() {
		this(0.05, 2);
	}

	/**
	 * Return the number of crossover points.
	 *
	 * @return the number of crossover points.
	 */
	public int getN() {
		return _n;
	}

	@Override
	protected int crossover(final MSeq<G> that, final MSeq<G> other) {
		assert (that.length() == other.length());

		final int n = that.length();
		final int k = min(n, _n);

		final Random random = RandomRegistry.getRandom();
		final int[] points = k > 0 ? arrays.subset(n, k, random) : new int[0];

		crossover(that, other, points);
		return 2;
	}

	// Package private for testing purpose.
	static <T> void crossover(
		final MSeq<T> that,
		final MSeq<T> other,
		final int[] indexes
	) {

		for (int i = 0; i < indexes.length - 1; i += 2) {
			final int start = indexes[i];
			final int end = indexes[i + 1];
			that.swap(start, end, other, start);
		}
		if (indexes.length%2 == 1) {
			final int index = indexes[indexes.length - 1];
			that.swap(index, that.length(), other, index);
		}
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).
				and(super.hashCode()).
				and(_n).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		final MultiPointCrossover<?> mpc = (MultiPointCrossover<?>)obj;
		return _n == mpc._n && super.equals(obj);
	}

	@Override
	public String toString() {
		return format(
			"%s[p=%f, n=%d]",
			getClass().getSimpleName(), _probability, _n
		);
	}

	//public static <G extends Gene<?, G>> MultiPointCrossover<G> zip() {
	//	return new MultiPointCrossover<>(Integer.MAX_VALUE);
	//}

}










