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

import javolution.context.LocalContext;

import org.jscience.mathematics.number.Float64;
import org.testng.annotations.DataProvider;

import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
 */
public class NumberStatisticsBuilderTest extends StatisticsBuilderTest {

	@Override
	public Object newBuilder() {
		return new NumberStatistics.Builder<Float64Gene, Float64>();
	}

	@Override
	@DataProvider(name = "properties")
	public Object[][] builderProperties() {
		LocalContext.enter();
		try {
			final Random random = new Random(12345678);
			RandomRegistry.setRandom(random);

			return new Object[][] {
					{"generation", Integer.TYPE, random.nextInt(1000)},
					{"invalid", Integer.TYPE, random.nextInt(1000)},
					{"killed", Integer.TYPE, random.nextInt(10000)},
					{"samples", Integer.TYPE, random.nextInt(1000)},
					{"ageMean", Double.TYPE, random.nextDouble()},
					{"ageVariance", Double.TYPE, random.nextDouble()},
					{"fitnessMean", Double.TYPE, random.nextDouble()},
					{"fitnessVariance", Double.TYPE, random.nextDouble()},
					{"standardError", Double.TYPE, random.nextDouble()},
					{"bestPhenotype", Phenotype.class, TestUtils.newFloat64Phenotype()},
					{"worstPhenotype", Phenotype.class, TestUtils.newFloat64Phenotype()},
					{"optimize", Optimize.class, Optimize.MINIMUM},
					{"optimize", Optimize.class, Optimize.MAXIMUM}
			};
		} finally {
			LocalContext.exit();
		}
	}

}
