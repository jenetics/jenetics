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

import java.util.Random;

import org.jscience.mathematics.number.Float64;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2012-11-30 $</em>
 */
public class PermutationChromosomeFloat64Test
	extends ChromosomeTester<EnumGene<Float64>>
{

	private final Factory<Chromosome<EnumGene<Float64>>>
	_factory = new Factory<Chromosome<EnumGene<Float64>>>() {
		private final ISeq<Float64> _alleles = new Array<Float64>(100).fill(new Factory<Float64>() {
			private final Random _random = RandomRegistry.getRandom();
			@Override
			public Float64 newInstance() {
				return Float64.valueOf(_random.nextGaussian()*1000);
			}

		}).toISeq();

		@Override
		public PermutationChromosome<Float64> newInstance() {
			return new PermutationChromosome<>(_alleles);
		}
	};

	@Override
	protected Factory<Chromosome<EnumGene<Float64>>> getFactory() {
		return _factory;
	}

}

