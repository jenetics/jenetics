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

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public abstract class AltererTester {

	public abstract Alterer<DoubleGene, Double> newAlterer(final double p);

	@Test(dataProvider = "parameters")
	public void alterParameters(
		final int ngenes,
		final int nchromosomes,
		final int npopulation
	) {
		final Population<DoubleGene, Double> population = newDoubleGenePopulation(
			ngenes, nchromosomes, npopulation
		);

		final Alterer<DoubleGene, Double> alterer = newAlterer(1);

		// Must perform the alteration without exception.
		alterer.alter(population, 1);
	}

	@DataProvider(name = "parameters")
	public Object[][] parameters() {
		return new Object[][] {
			//    ngenes,       nchromosomes     npopulation
			{ 20,  20, 0},
			{ 1,   1,  0},
			{ 5,   1,  0},
			{ 80,  1,  0},
			{ 20,  20, 1},
			{ 1,   1,  1},
			{ 5,   1,  1},
			{ 80,  1,  1},
			{ 20,  20, 2},
			{ 1,   1,  2},
			{ 5,   1,  2},
			{ 80,  1,  2},
			{ 20,  20, 3},
			{ 1,   1,  3},
			{ 5,   1,  3},
			{ 80,  1,  3},
		};
	}

}
