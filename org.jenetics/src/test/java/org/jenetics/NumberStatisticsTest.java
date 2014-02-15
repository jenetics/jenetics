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

import org.jenetics.util.Factory;
import org.jenetics.util.ObjectTester;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-02-15 $</em>
 */
public class NumberStatisticsTest
	extends ObjectTester<NumberStatistics<DoubleGene, Double>>
{

	final Factory<NumberStatistics<DoubleGene, Double>>
	_factory = new Factory<NumberStatistics<DoubleGene, Double>>() {
		@Override
		public NumberStatistics<DoubleGene, Double> newInstance() {
			final Random random = RandomRegistry.getRandom();

			final NumberStatistics.Builder<DoubleGene, Double>
			builder = new NumberStatistics.Builder<>();
			builder.ageMean(random.nextDouble());
			builder.ageVariance(random.nextDouble());
			builder.invalid(random.nextInt(1000));
			builder.killed(random.nextInt(1000));
			builder.samples(random.nextInt(100000));
			builder.generation(random.nextInt(1000));
			builder.fitnessMean(random.nextDouble());
			builder.fitnessVariance(random.nextDouble());
			builder.standardError(random.nextDouble());
			builder.bestPhenotype(TestUtils.newDoublePhenotype());
			builder.worstPhenotype(TestUtils.newDoublePhenotype());

			return builder.build();
		}
	};
	@Override
	protected Factory<NumberStatistics<DoubleGene, Double>> getFactory() {
		return _factory;
	}

}
