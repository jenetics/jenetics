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

import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.internal.util.Concurrency;

import org.jenetics.Statistics.Calculator;
import org.jenetics.stat.Variance;
import org.jenetics.util.accumulators;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-04-05 $</em>
 */
public class StatisticsCalculatorTest {

	public Calculator<DoubleGene, Double> newCalculator() {
		return new Calculator<>();
	}

	public Iterable<Phenotype<DoubleGene, Double>> population(final int size) {
		return new Iterable<Phenotype<DoubleGene,Double>>() {
			@Override
			public Iterator<Phenotype<DoubleGene, Double>> iterator() {
				return new Iterator<Phenotype<DoubleGene,Double>>() {
					private final Double MIN = Double.valueOf(0);
					private final Double MAX = Double.valueOf(size);

					private int _pos = -1;

					@Override
					public boolean hasNext() {
						return _pos < size - 1;
					}

					@Override
					public Phenotype<DoubleGene, Double> next() {
						++_pos;
						final DoubleGene gene = DoubleGene.of(_pos, MIN, MAX);
						return Phenotype.of(
							Genotype.of(DoubleChromosome.of(gene)),
							TestUtils.FF, 0
						);
					}

					@Override
					public void remove() {
					}
				};
			}
		};
	}

	@Test(dataProvider = "size_gen")
	public void evaluate(final Integer size, final Integer gen) {
		final Calculator<DoubleGene, Double> calculator = newCalculator();
		final Statistics.Builder<DoubleGene, Double>
		builder = calculator.evaluate(
			Concurrency.commonPool(),
			population(size),
			gen,
			Optimize.MAXIMUM
		);
		final Statistics<DoubleGene, Double> statistics = builder.build();

		final Variance<Integer> ageVariance = new Variance<>();
		accumulators.accumulate(population(size), ageVariance.map(Phenotype.Age(gen)));

		Assert.assertEquals(statistics.getAgeMean(), ageVariance.getMean());
		Assert.assertEquals(statistics.getAgeVariance(), ageVariance.getVariance());
		Assert.assertEquals(statistics.getSamples(), size.intValue());
		Assert.assertEquals(statistics.getGeneration(), gen.intValue());
		Assert.assertEquals(statistics.getBestFitness(), size - 1.0);
		Assert.assertEquals(statistics.getWorstFitness(), 0.0);
	}

	@DataProvider(name = "size_gen")
	public Object[][] sizeGen() {
		return new Object[][] {
				{1000, 23},
				{5000, 100},
				{1000, 312}
		};
	}

}
