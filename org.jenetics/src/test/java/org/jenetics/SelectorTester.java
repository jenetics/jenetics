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

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.stat.Distribution;
import org.jenetics.stat.Histogram;
import org.jenetics.stat.StatisticsAssert;
import org.jenetics.util.Factory;
import org.jenetics.util.ObjectTester;
import org.jenetics.util.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-08-14 $</em>
 */
public abstract class SelectorTester<S extends Selector<DoubleGene, Double>>
	extends ObjectTester<S>
{

	private final Range<Double> _domain = new Range<>(0.0, 100.0);
	protected Range<Double> getDomain() {
		return _domain;
	}

	protected abstract Distribution<Double> getDistribution();

	private final int _histogramSize = 37;
	protected int getHistogramSize() {
		return _histogramSize;
	}

	protected S selector() {
		return factory().newInstance();
	}

	protected boolean isCheckEnabled() {
		return true;
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void selectNegativeCountArgument() {
		final Factory<Genotype<DoubleGene>> gtf =
			Genotype.of(new DoubleChromosome(0.0, 1.0));

		final Population<DoubleGene, Double> population =
			new Population<>(2);

		for (int i = 0, n = 2; i < n; ++i) {
			population.add(Phenotype.of(gtf.newInstance(), TestUtils.FF, 12));
		}

		selector().select(population, -1, Optimize.MAXIMUM);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void selectNullPopulationArgument() {
		selector().select(null, 23, Optimize.MAXIMUM);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void selectNullOptimizeArgument() {
		final Factory<Genotype<DoubleGene>> gtf =
			Genotype.of(new DoubleChromosome(0.0, 1.0));

		final Population<DoubleGene, Double> population =
			new Population<>(2);

		for (int i = 0, n = 2; i < n; ++i) {
			population.add(Phenotype.of(gtf.newInstance(), TestUtils.FF, 12));
		}

		selector().select(population, 1, null);
	}

	@Test(dataProvider = "selectParameters")
	public void select(final Integer size, final Integer count, final Optimize opt) {
		final Factory<Genotype<DoubleGene>> gtf =
			Genotype.of(new DoubleChromosome(0.0, 1_000.0));

		final Population<DoubleGene, Double> population =
			new Population<>(size);

		for (int i = 0, n = size; i < n; ++i) {
			population.add(Phenotype.of(gtf.newInstance(), TestUtils.FF, 12));
		}

		final Population<DoubleGene, Double> selection = selector()
			.select(population, count, opt);

		Assert.assertEquals(selection.size(), count.intValue());
		for (Object gt : selection) {
			Assert.assertTrue(
				population.contains(gt),
				format("Population doesn't contain %s.", gt)
			);
		}
	}


	@DataProvider(name = "selectParameters")
	public Object[][] selectParameters() {
		final List<Integer> sizes = Arrays.asList(1, 2, 3, 5, 11, 50, 100, 10_000);
		final List<Integer> counts = Arrays.asList(0, 1, 2, 3, 5, 11, 50, 100, 10_000);

		final List<Object[]> result = new ArrayList<>();
		for (Integer size : sizes) {
			for (Integer count : counts) {
				result.add(new Object[]{size, count, Optimize.MINIMUM});
				result.add(new Object[]{size, count, Optimize.MAXIMUM});
			}
		}

		return result.toArray(new Object[0][]);
	}

	@Test(invocationCount = 20, successPercentage = 95)
	public void selectDistribution() {
		final int npopulation = 101;
		final int loops = 500;

		final Double min = getDomain().getMin();
		final Double max = getDomain().getMax();
		final Histogram<Double> histogram = Histogram.of(min, max, getHistogramSize());

		final Factory<Phenotype<DoubleGene, Double>> ptf = () ->
			Phenotype.of(Genotype.of(DoubleChromosome.of(min, max)), TestUtils.FF, 12);

		final S selector = selector();

		for (int j = 0; j < loops; ++j) {
			final Population<DoubleGene, Double> population = IntStream.range(0, npopulation)
				.mapToObj(i -> ptf.newInstance())
				.collect(Population.toPopulation());

			selector.select(population, npopulation, Optimize.MAXIMUM).stream()
				.map(pt -> pt.getGenotype().getGene().getAllele())
				.forEach(histogram);
		}

		check(histogram, getDistribution());
	}

	protected double χ2(
		final Histogram<Double> histogram,
		final Distribution<Double> distribution
	) {
		return histogram.χ2(distribution.getCDF());
	}

	protected void check(
		final Histogram<Double> histogram,
		final Distribution<Double> distribution
	) {
		if (isCheckEnabled()) {
			final double χ2 =  χ2(histogram, distribution);
			final int degreeOfFreedom = histogram.length();
			assert (degreeOfFreedom > 0);

			final double maxChi = StatisticsAssert.chi(0.999, degreeOfFreedom)*2;

			if (χ2 > maxChi) {
				System.out.println(format(
					"The histogram %s doesn't follow the distribution %s. \n" +
						"χ2 must be smaller than %f but was %f",
					histogram, distribution,
					maxChi, χ2
				));
			}

			Assert.assertTrue(
				χ2 <= maxChi,
				format(
					"The histogram %s doesn't follow the distribution %s. \n" +
						"χ2 must be smaller than %f but was %f",
					histogram, distribution,
					maxChi, χ2
				)
			);
		}
	}

}
