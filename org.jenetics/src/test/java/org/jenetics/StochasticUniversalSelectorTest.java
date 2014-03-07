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
 * @version <em>$Date: 2014-02-14 $</em>
 */
public class StochasticUniversalSelectorTest
	extends ProbabilitySelectorTester<StochasticUniversalSelector<DoubleGene,Double>>
{

	@Override
	protected boolean isSorted() {
		return true;
	}

	final Factory<StochasticUniversalSelector<DoubleGene,Double>>
	_factory = new Factory<StochasticUniversalSelector<DoubleGene,Double>>() {
		@Override
		public StochasticUniversalSelector<DoubleGene, Double> newInstance() {
			return new StochasticUniversalSelector<>();
		}
	};
	@Override
	protected Factory<StochasticUniversalSelector<DoubleGene, Double>> getFactory() {
		return _factory;
	}

	@Override
	protected Distribution<Double> getDistribution() {
		return new UniformDistribution<>(getDomain());
	}

	// TODO: implement select-distribution test.
	@Override
	@Test
	public void selectDistribution() {
		//super.selectDistribution();
	}

}
