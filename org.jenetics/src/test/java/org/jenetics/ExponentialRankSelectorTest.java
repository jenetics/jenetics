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

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.testng.SkipException;
import org.testng.annotations.Test;

import org.jenetics.stat.Distribution;
import org.jenetics.stat.Histogram;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.util.Factory;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Range;
import org.jenetics.util.Scoped;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-08-26 $</em>
 */
public class ExponentialRankSelectorTest
	extends ProbabilitySelectorTester<ExponentialRankSelector<DoubleGene, Double>>
{

	@Override
	protected boolean isSorted() {
		return true;
	}

	@Override
	protected Factory<ExponentialRankSelector<DoubleGene, Double>> factory() {
		return ExponentialRankSelector::new;
	}

	@Override
	protected Distribution<Double> getDistribution() {
		return new UniformDistribution<>(getDomain());
	}

	@Override
	@Test
	public void selectDistribution() {
	}

	public static void main(final String[] args) {
		//writeDistributionData(Optimize.MAXIMUM);
		writeDistributionData(Optimize.MINIMUM);
	}

	private static void writeDistributionData(final Optimize opt) {
		final ThreadLocal<LCG64ShiftRandom> random = new LCG64ShiftRandom.ThreadLocal();
		try (Scoped<LCG64ShiftRandom> sr = RandomRegistry.scope(random)) {

			final int npopulation = POPULATION_COUNT;
			//final int loops = 2_500_000;
			final int loops = 100000;

			printDistributions(
				System.out,
				Arrays.asList(0.0, 0.1, 0.3, 0.5, 0.75, 0.95, 0.999),
				ExponentialRankSelector::new,
				opt,
				npopulation,
				loops
			);
		}
	}

}
