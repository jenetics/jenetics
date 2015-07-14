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

import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class GaussianMutatorTest extends MutatorTester {

	@Override
	public Alterer<DoubleGene, Double> newAlterer(double p) {
		return new GaussianMutator<>(p);
	}

	@Test(invocationCount = 20, successPercentage = 95)
	public void mutate() {
		final Random random = RandomRegistry.getRandom();

		final double min = 0;
		final double max = 10;
		final double mean = 5;
		final double var = Math.pow((max - min)/4.0, 2);

		final DoubleGene gene = DoubleGene.of(mean, min, max);
		final GaussianMutator<DoubleGene, Double> mutator = new GaussianMutator<>();

		final Histogram<Double> histogram = Histogram.ofDouble(0.0, 10.0, 10);

		for (int i = 0; i < 10000; ++i) {
			final double value = mutator.mutate(gene, random).getAllele();
			histogram.accept(value);
		}

		final Range<Double> domain = new Range<>(min, max);
		// TODO: Implement test
		//assertDistribution(histogram, new NormalDistribution<>(domain, mean, var));
	}

}

