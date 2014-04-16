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

import java.util.Collections;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.Factory;
import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-04-13 $</em>
 */
public class PermutationChromosomeTest
	extends ChromosomeTester<EnumGene<Integer>>
{

	private final Factory<Chromosome<EnumGene<Integer>>>
	_factory = new Factory<Chromosome<EnumGene<Integer>>>() {
		@Override
		public PermutationChromosome<Integer> newInstance() {
			return PermutationChromosome.ofInteger(100);
		}
	};

	@Override
	protected Factory<Chromosome<EnumGene<Integer>>> getFactory() {
		return _factory;
	}

	@Test
	public void ofIntegerLength() {
		final PermutationChromosome<Integer> c = PermutationChromosome.ofInteger(100);
		final MSeq<Integer> genes = c.getValidAlleles().copy();
		Collections.sort(genes.asList());

		for (int i = 0; i < c.length(); ++i) {
			Assert.assertEquals(genes.get(i).intValue(), i);
		}
	}

	@Test
	public void ofIntegerStartEnd() {
		final PermutationChromosome<Integer> c = PermutationChromosome.ofInteger(100, 200);
		final MSeq<Integer> genes = c.getValidAlleles().copy();
		Collections.sort(genes.asList());

		for (int i = 0; i < c.length(); ++i) {
			Assert.assertEquals(genes.get(i).intValue(), i + 100);
		}
	}

}
