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

import org.jenetics.stat.Distribution;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.util.Factory;
import org.jenetics.util.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-02-11 $</em>
 */
public class TruncationSelectorTest
	extends SelectorTester<TruncationSelector<DoubleGene, Double>>
{


	final Factory<TruncationSelector<DoubleGene, Double>>
	_factory = new Factory<TruncationSelector<DoubleGene,Double>>()
	{
		@Override
		public TruncationSelector<DoubleGene, Double> newInstance() {
			return new TruncationSelector<>();
		}
	};
	@Override
	protected Factory<TruncationSelector<DoubleGene, Double>> getFactory() {
		return _factory;
	}

	@Override
	protected Distribution<Double> getDistribution() {
		final Range<Double> domain = new Range<>(
				(getDomain().getMax() - getDomain().getMin())/2.0,
				getDomain().getMax()
			);
		return new UniformDistribution<>(domain);
	}

	@Override
	protected boolean isCheckEnabled() {
		return false;
	}

}
