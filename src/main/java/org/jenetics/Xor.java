/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version $Id$
 */
class Xor extends Recombinator<BitGene> {

	public Xor(double probability, int order) {
		super(probability, order);
	}

	@Override
	protected <C extends Comparable<? super C>> int recombine(
		final Population<BitGene, C> population,
		final int[] individuals,
		final int generation
	) {
		Chromosome<BitGene> c = population.get(0).getGenotype().getChromosome(0);
		if (c instanceof BitChromosome) {
			xor((BitChromosome)c, (BitChromosome)c);
		} else {
			xor(c, c);
		}

		return 0;
	}

	Chromosome<BitGene> xor(final Chromosome<BitGene> a, final Chromosome<BitGene> b) {
		return a;
	}

	BitChromosome xor(final BitChromosome a, final BitChromosome b) {
		return a;
	}

}
