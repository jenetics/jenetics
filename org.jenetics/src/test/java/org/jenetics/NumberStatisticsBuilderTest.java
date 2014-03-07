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

import org.testng.annotations.DataProvider;

import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Scoped;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-02-15 $</em>
 */
public class NumberStatisticsBuilderTest extends StatisticsBuilderTest {

	@Override
	public Object newBuilder() {
		return new NumberStatistics.Builder<DoubleGene, Double>();
	}

	@Override
	@DataProvider(name = "properties")
	public Object[][] builderProperties() {
		try (Scoped<Random> s= RandomRegistry.scope(new Random(12345678))) {
			return new Object[][] {
				{"generation", Integer.TYPE, s.get().nextInt(1000)},
				{"invalid", Integer.TYPE, s.get().nextInt(1000)},
				{"killed", Integer.TYPE, s.get().nextInt(10000)},
				{"samples", Integer.TYPE, s.get().nextInt(1000)},
				{"ageMean", Double.TYPE, s.get().nextDouble()},
				{"ageVariance", Double.TYPE, s.get().nextDouble()},
				{"fitnessMean", Double.TYPE, s.get().nextDouble()},
				{"fitnessVariance", Double.TYPE, s.get().nextDouble()},
				{"standardError", Double.TYPE, s.get().nextDouble()},
				{"bestPhenotype", Phenotype.class, TestUtils.newDoublePhenotype()},
				{"worstPhenotype", Phenotype.class, TestUtils.newDoublePhenotype()},
				{"optimize", Optimize.class, Optimize.MINIMUM},
				{"optimize", Optimize.class, Optimize.MAXIMUM}
			};
		}
	}

}
