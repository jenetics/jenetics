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

import java.util.Random;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;

/**
 * <strong>Single point crossover</strong>
 *
 * <p>
 * One or two children are created by taking two parent strings and cutting
 * them at some randomly chosen site. E.g.
 * <p>
 *	<img src="doc-files/SinglePointCrossover.svg" width="400"
 *	     alt="Single-point crossover" >
 * <p>
 * If we create a child and its complement we preserving the total number of
 * genes in the population, preventing any genetic drift.
 * Single-point crossover is the classic form of crossover. However, it produces
 * very slow mixing compared with multi-point crossover or uniform crossover.
 * For problems where the site position has some intrinsic meaning to the
 * problem single-point crossover can lead to small disruption than multi-point
 * or uniform crossover.
 *
 * @see MultiPointCrossover
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.6
 */
public class SinglePointCrossover<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends MultiPointCrossover<G, C>
{

	/**
	 * Constructs an alterer with a given recombination probability.
	 *
	 * @param probability the crossover probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *         valid range of {@code [0, 1]}.
	 */
	public SinglePointCrossover(final double probability) {
		super(probability, 1);
	}

	/**
	 * Create a new single point crossover object with crossover probability of
	 * {@code 0.05}.
	 */
	public SinglePointCrossover() {
		this(0.05);
	}

	@Override
	protected int crossover(final MSeq<G> that, final MSeq<G> other) {
		final Random random = RandomRegistry.getRandom();

		final int index = random.nextInt(min(that.length(), other.length()));
		crossover(that, other, index);
		return 2;
	}

	// Package private for testing purpose.
	static <T> void crossover(
		final MSeq<T> that,
		final MSeq<T> other,
		final int index
	) {
		assert index >= 0 :
			format(
				"Crossover index must be within [0, %d) but was %d",
				that.length(), index
			);

		that.swap(index, min(that.length(), other.length()), other, index);
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(super::equals);
	}

	@Override
	public String toString() {
		return format("%s[p=%f]", getClass().getSimpleName(), _probability);
	}

}
