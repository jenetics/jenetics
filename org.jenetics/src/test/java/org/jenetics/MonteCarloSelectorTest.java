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

import org.testng.annotations.Test;

import org.jenetics.stat.Distribution;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-03-07 $</em>
 */
public class MonteCarloSelectorTest
	extends SelectorTester<MonteCarloSelector<DoubleGene, Double>>
{

	final Factory<MonteCarloSelector<DoubleGene, Double>>
	_factory = new Factory<MonteCarloSelector<DoubleGene,Double>>()
	{
		@Override
		public MonteCarloSelector<DoubleGene, Double> newInstance() {
			return new MonteCarloSelector<>();
		}
	};

	@Override
	protected Factory<MonteCarloSelector<DoubleGene, Double>> getFactory() {
		return _factory;
	}

	@Override
	protected boolean isCheckEnabled() {
		return true;
	}

	@Override
	protected Distribution<Double> getDistribution() {
		return new UniformDistribution<>(getDomain());
	}

	@Test
	public void foo() {

	}

}
