/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import static java.lang.Math.min;

import java.util.Random;

import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.arrays;

/**
 * <strong><p>Multiple point crossover</p></strong>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.2
 * @version 1.2 &mdash; <em>$Date: 2013-02-20 $</em>
 */
public class MultiplePointCrossover<G extends Gene<?, G>> extends Crossover<G> {

	private final int _n;

	/**
	 * Create a new crossover instance.
	 *
	 * @param probability the recombination probability.
	 * @param n the number of crossover points.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *         valid range of {@code [0, 1]} or {@code n < 1}.
	 */
	public MultiplePointCrossover(final double probability, final int n) {
		super(probability);
		if (n < 1) {
			throw new IllegalArgumentException(String.format(
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
	public MultiplePointCrossover(final double probability) {
		this(probability, 2);
	}

	/**
	 * Create a new crossover instance with default crossover probability of
	 * 0.05.
	 *
	 * @param n the number of crossover points.
	 * @throws IllegalArgumentException if {@code n < 1}.
	 */
	public MultiplePointCrossover(final int n) {
		this(0.05, n);
	}

	/**
	 * Create a new crossover instance with two crossover points and crossover
	 * probability 0.05.
	 */
	public MultiplePointCrossover() {
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

		final Random random = RandomRegistry.getRandom();
		final int[] points = arrays.subset(
			that.length(), min(that.length(), _n), random
		);

		crossover(that, other, points);

		return 2;
	}

	// Package private for testing purpose.
	void crossover(final MSeq<G> that, final MSeq<G> other, final int[] points) {
		for (int i = 1; i < points.length; i += 2) {
			final int start = points[i - 1];
			final int end = points[i];
			that.swap(start, end, other, start);
		}
		if (points.length%2 == 1) {
			final int index = points[points.length - 1];
			that.swap(index, that.length(), other, index);
		}
	}

}










