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

import org.jscience.mathematics.number.Float64;

import org.jenetics.stat.Distribution;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.util.Factory;
import org.jenetics.util.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
 */
public class TruncationSelectorTest
	extends SelectorTester<TruncationSelector<Float64Gene, Float64>>
{


	final Factory<TruncationSelector<Float64Gene, Float64>>
	_factory = new Factory<TruncationSelector<Float64Gene,Float64>>()
	{
		@Override
		public TruncationSelector<Float64Gene, Float64> newInstance() {
			return new TruncationSelector<>();
		}
	};
	@Override
	protected Factory<TruncationSelector<Float64Gene, Float64>> getFactory() {
		return _factory;
	}

	@Override
	protected Distribution<Float64> getDistribution() {
		final Range<Float64> domain = new Range<>(
				getDomain().getMax().minus(getDomain().getMin()).divide(2),
				getDomain().getMax()
			);
		return new UniformDistribution<>(domain);
	}

	@Override
	protected boolean isCheckEnabled() {
		return false;
	}

}
