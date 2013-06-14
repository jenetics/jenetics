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

import static java.lang.String.format;
import static org.jenetics.util.object.hashCodeOf;

import java.util.Random;

import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;


/**
 * <strong><p>Single point crossover</p></strong>
 *
 * <p>
 * One or two children are created by taking two parent strings and cutting
 * them at some randomly chosen site. E.g.
 * </p>
 * <div align="center">
 *	<img src="doc-files/SinglePointCrossover.svg" width="400" >
 * </div>
 * <p>
 * If we create a child and its complement we preserving the total number of
 * genes in the population, preventing any genetic drift.
 * Single-point crossover is the classic form of crossover. However, it produces
 * very slow mixing compared with multi-point crossover or uniform crossover.
 * For problems where the site position has some intrinsic meaning to the
 * problem single-point crossover can lead to small disruption than multi-point
 * or uniform crossover.
 * </p>
 *
 * @see MultiPointCrossover
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.2 &mdash; <em>$Date$</em>
 */
public class SinglePointCrossover<G extends Gene<?, G>>
	extends MultiPointCrossover<G>
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
		assert (that.length() == other.length());

		final Random random = RandomRegistry.getRandom();
		crossover(that, other, random.nextInt(that.length()));
		return 2;
	}

	// Package private for testing purpose.
	static <T> void crossover(
		final MSeq<T> that,
		final MSeq<T> other,
		final int index
	) {
		assert (index >= 0) : format(
			"Crossover index must be within [0, %d) but was %d",
			that.length(), index
		);

		that.swap(index, that.length(), other, index);
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		return super.equals(obj);
	}

	@Override
	public String toString() {
		return format("%s[p=%f]", getClass().getSimpleName(), _probability);
	}

}

