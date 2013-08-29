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

//import static org.jenetics.TestUtils.diff;
import static org.jenetics.TestUtils.newFloat64GenePopulation;

import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class SwapMutatorTest extends MutatorTestBase {

	@Override
	public Alterer<Float64Gene> newAlterer(double p) {
		return new SwapMutator<>(p);
	}

	@Override
	@Test(dataProvider = "alterCountParameters")
	public void alterCount(
		final Integer ngenes,
		final Integer nchromosomes,
		final Integer npopulation
	) {
		final Population<Float64Gene, Float64> p1 = newFloat64GenePopulation(
					ngenes, nchromosomes, npopulation
				);
		final Population<Float64Gene, Float64> p2 = p1.copy();
		Assert.assertEquals(p2, p1);

		final Alterer<Float64Gene> mutator = newAlterer(0.01);

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
	@Test(dataProvider = "alterProbabilityParameters")
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
				{ new Integer(180),  new Integer(1),  new Integer(150), new Double(0.15) },
				{ new Integer(180),  new Integer(2),  new Integer(150), new Double(0.15) },
				{ new Integer(180),  new Integer(15), new Integer(150), new Double(0.15) },

				{ new Integer(180),  new Integer(1),  new Integer(150), new Double(0.5) },
				{ new Integer(180),  new Integer(2),  new Integer(150), new Double(0.5) },
				{ new Integer(180),  new Integer(15), new Integer(150), new Double(0.5) },

				{ new Integer(180),  new Integer(1),  new Integer(150), new Double(0.85) },
				{ new Integer(180),  new Integer(2),  new Integer(150), new Double(0.85) },
				{ new Integer(180),  new Integer(15), new Integer(150), new Double(0.85) }
		};
	}

}
