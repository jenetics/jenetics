/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import org.jscience.mathematics.number.Float64;

import org.jenetics.stat.Distribution;
import org.jenetics.stat.Histogram;
import org.jenetics.stat.LinearDistribution;
import org.jenetics.util.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
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
