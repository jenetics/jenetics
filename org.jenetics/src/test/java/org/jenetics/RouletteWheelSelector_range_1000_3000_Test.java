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
import org.jenetics.stat.Histogram;
import org.jenetics.stat.LinearDistribution;
import org.jenetics.util.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class RouletteWheelSelector_range_1000_3000_Test extends RouletteWheelSelectorTest {

	private Range<Float64> _domain = new Range<>(Float64.valueOf(1000), Float64.valueOf(3000));

	@Override
	protected final Range<Float64> getDomain() {
		return _domain;
	}

	@Override
	protected Distribution<Float64> getDistribution() {
		double x1 = getDomain().getMin().doubleValue();
		double x2 = getDomain().getMax().doubleValue();
		double a = x1*(x2 - x1) + (x2 - x1)*(x2 - x1)/2.0;
		return new LinearDistribution<>(getDomain(), x1/a);
	}

	@Override
	protected double χ2(
		final Histogram<Float64> histogram,
		final Distribution<Float64> distribution
	) {
		return histogram.χ2(
			distribution.getCDF(),
			getDomain().getMin(), getDomain().getMax()
		);
	}

}
