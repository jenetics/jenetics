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

import static org.jenetics.TestUtils.newDoubleGenePopulation;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class SwapMutatorTest extends MutatorTester {

	@Override
	public Alterer<DoubleGene, Double> newAlterer(double p) {
		return new SwapMutator<>(p);
	}

	@Override
	@Test(dataProvider = "alterCountParameters")
	public void alterCount(
		final Integer ngenes,
		final Integer nchromosomes,
		final Integer npopulation
	) {
		final Population<DoubleGene, Double> p1 =
			newDoubleGenePopulation(ngenes, nchromosomes, npopulation);
		final Population<DoubleGene, Double> p2 = p1.copy();
		Assert.assertEquals(p2, p1);

		final Alterer<DoubleGene, Double> mutator = newAlterer(0.01);

		final int alterations = mutator.alter(p1, 1);
		//final int diff = diff(p1, p2);

		if (ngenes == 1) {
			Assert.assertEquals(alterations, 0);
		} else {
			//Assert.assertTrue(alterations >= diff/2, String.format("%d >= %d", alterations, diff/2));
			//Assert.assertTrue(alterations <= 2*diff, String.format("%d < %d", alterations, 2*diff));

		}
	}

	@Override
	@Test(dataProvider = "alterProbabilityParameters", groups = {"statistics"})
	public void alterProbability(
		final Integer ngenes,
		final Integer nchromosomes,
		final Integer npopulation,
		final Double p
	) {
		super.alterProbability(ngenes, nchromosomes, npopulation, p);
	}

	@Override
	@DataProvider(name = "alterProbabilityParameters")
	public Object[][] alterProbabilityParameters() {
		return new Object[][] {
			//    ngenes,       nchromosomes     npopulation
			{180, 1,  150, 0.15},
			{180, 2,  150, 0.15},
			{180, 15, 150, 0.15},

			{180, 1,  150, 0.5},
			{180, 2,  150, 0.5},
			{180, 15, 150, 0.5},

			{180, 1,  150, 0.85},
			{180, 2,  150, 0.85},
			{180, 15, 150, 0.85}
		};
	}

}
