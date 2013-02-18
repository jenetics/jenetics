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

import java.util.Random;

import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.2
 * @version 1.2 &mdash; <em>$Date$</em>
 */
public class NPointCrossover<G extends Gene<?, G>> extends Crossover<G> {

	private final int _n;

	public NPointCrossover(final double probability, final int n) {
		super(probability);
		_n = n;
	}

	public NPointCrossover(final double probability) {
		this(probability, 2);
	}

	public NPointCrossover(final int n) {
		this(0.05, n);
	}

	public NPointCrossover() {
		this(0.05, 2);
	}

	@Override
	protected int crossover(final MSeq<G> that, final MSeq<G> other) {
		assert (that.length() == other.length());

		final Random random = RandomRegistry.getRandom();
		final int index = random.nextInt(that.length());

		that.swap(index, that.length(), other, index);

		return 2;
	}

}










