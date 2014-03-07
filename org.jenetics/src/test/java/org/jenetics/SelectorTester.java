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

import static org.jenetics.util.accumulators.accumulate;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.stat.Distribution;
import org.jenetics.stat.Histogram;
import org.jenetics.stat.StatisticsAssert;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.ObjectTester;
import org.jenetics.util.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-02-17 $</em>
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

	protected S getSelector() {
		return getFactory().newInstance();
	}

	protected boolean isCheckEnabled() {
		return true;
	}

	@Test(invocationCount = 20, successPercentage = 95)
	public void selectDistribution() {
		final int npopulation = 101;
		final int loops = 500;


		final Double min = getDomain().getMin();
		final Double max = getDomain().getMax();
		final Histogram<Double> histogram = Histogram.of(min, max, _histogramSize);

		final Factory<Genotype<DoubleGene>>
		gtf = Genotype.of(new DoubleChromosome(min, max));



		final Population<DoubleGene, Double>
		population = new Population<>(npopulation);

		final S selector = getSelector();

		for (int j = 0; j < loops; ++j) {
			for (int i = 0; i < npopulation; ++i) {
				population.add(Phenotype.of(gtf.newInstance(), TestUtils.FF, 12));
			}


			final Population<DoubleGene, Double> selection =
				selector.select(population, npopulation, Optimize.MAXIMUM);

			accumulate(
					selection,
					histogram
						.map(Allele)
						.map(Gene)
						.map(Genotype.<DoubleGene>Chromosome())
						.map(Phenotype.<DoubleGene>Genotype())
				);

			population.clear();
		}

		check(histogram, getDistribution());
	}

	private static final Function<DoubleGene, Double> Allele =
		new Function<DoubleGene, Double>() {
			@Override public Double apply(final DoubleGene value) {
				return value.getAllele();
			}
		};

	private static final Function<Chromosome<DoubleGene>, DoubleGene>
		Gene = AbstractChromosome.gene();

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
				System.out.println(String.format(
					"The histogram %s doesn't follow the distribution %s. \n" +
					"χ2 must be smaller than %f but was %f",
					histogram, distribution,
					maxChi, χ2
				));
			}

			Assert.assertTrue(
				χ2 <= maxChi,
				String.format(
						"The histogram %s doesn't follow the distribution %s. \n" +
						"χ2 must be smaller than %f but was %f",
						histogram, distribution,
						maxChi, χ2
					)
			);
		}
	}

}
