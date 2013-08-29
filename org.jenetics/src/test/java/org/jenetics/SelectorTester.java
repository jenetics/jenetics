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

import org.jscience.mathematics.number.Float64;

import org.jenetics.stat.ChiSquare;
import org.jenetics.stat.Distribution;
import org.jenetics.stat.Histogram;
import org.jenetics.util.Factory;
import org.jenetics.util.ObjectTester;
import org.jenetics.util.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public abstract class SelectorTester<S extends Selector<Float64Gene, Float64>>
	extends ObjectTester<S>
{

	private final Range<Float64> _domain = new Range<>(Float64.ZERO, Float64.valueOf(100));
	protected Range<Float64> getDomain() {
		return _domain;
	}

	protected abstract Distribution<Float64> getDistribution();

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


		final Float64 min = getDomain().getMin();
		final Float64 max = getDomain().getMax();
		final Histogram<Float64> histogram = Histogram.valueOf(min, max, _histogramSize);

		final Factory<Genotype<Float64Gene>>
		gtf = Genotype.valueOf(new Float64Chromosome(min, max));



		final Population<Float64Gene, Float64>
		population = new Population<>(npopulation);

		final S selector = getSelector();

		for (int j = 0; j < loops; ++j) {
			for (int i = 0; i < npopulation; ++i) {
				population.add(Phenotype.valueOf(gtf.newInstance(), TestUtils.FF, 12));
			}


			final Population<Float64Gene, Float64> selection =
				selector.select(population, npopulation, Optimize.MAXIMUM);

			accumulate(
					selection,
					histogram
						.map(Float64Gene.Allele)
						.map(Float64Chromosome.Gene)
						.map(Genotype.<Float64Gene>Chromosome())
						.map(Phenotype.<Float64Gene>Genotype())
				);

			population.clear();
		}

		check(histogram, getDistribution());
	}

	protected double χ2(
		final Histogram<Float64> histogram,
		final Distribution<Float64> distribution
	) {
		return histogram.χ2(distribution.getCDF());
	}

	protected void check(
		final Histogram<Float64> histogram,
		final Distribution<Float64> distribution
	) {
		if (isCheckEnabled()) {
			final double χ2 =  χ2(histogram, distribution);
			final int degreeOfFreedom = histogram.length();
			assert (degreeOfFreedom > 0);

			final double maxChi = ChiSquare.chi_999(degreeOfFreedom)*2;

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





