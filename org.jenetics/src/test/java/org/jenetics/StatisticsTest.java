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

import org.jscience.mathematics.number.Float64;

import org.jenetics.util.Factory;
import org.jenetics.util.ObjectTester;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class StatisticsTest extends ObjectTester<Statistics<Float64Gene, Float64>> {

	final Factory<Statistics<Float64Gene, Float64>>
	_factory = new Factory<Statistics<Float64Gene,Float64>>() {
		private final Phenotype<Float64Gene, Float64> _best = TestUtils.newFloat64Phenotype();
		private final Phenotype<Float64Gene, Float64> _worst = _best;

		@Override
		public Statistics<Float64Gene, Float64> newInstance() {
			final Random random = RandomRegistry.getRandom();
			final int generation = random.nextInt(1000);

			final int samples = random.nextInt(1000);
			final double ageMean = random.nextDouble();
			final double ageVariance = random.nextDouble();
			final int killed = random.nextInt(1000);
			final int invalid = random.nextInt(10000);

			return new Statistics<>(
					Optimize.MAXIMUM, generation, _best, _worst,
					samples, ageMean, ageVariance, killed, invalid
				);
		}
	};
	@Override
	protected Factory<Statistics<Float64Gene, Float64>> getFactory() {
		return _factory;
	}

}





