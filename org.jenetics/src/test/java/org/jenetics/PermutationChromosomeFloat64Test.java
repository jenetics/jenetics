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

import java.util.Random;

import org.jscience.mathematics.number.Float64;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
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

