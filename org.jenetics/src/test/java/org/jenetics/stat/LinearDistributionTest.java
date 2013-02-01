/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
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
package org.jenetics.stat;

import java.util.Random;
import java.util.function.Function;

import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.Factory;
import org.jenetics.util.ObjectTester;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-02-01 $</em>
 */
public class LinearDistributionTest extends ObjectTester<LinearDistribution<Double>> {

	private final Factory<LinearDistribution<Double>>
	_factory = new Factory<LinearDistribution<Double>>() {
		@Override
		public LinearDistribution<Double> newInstance() {
			final Random random = RandomRegistry.getRandom();

			final double min = random.nextInt(100) + 100;
			final double max = random.nextInt(100) + 100 + min;
			final double y2 = random.nextDouble();
			final LinearDistribution<Double> dist =
				new LinearDistribution<>(new Range<>(min, max), y2);

			return dist;
		}
	};
	@Override
	protected Factory<LinearDistribution<Double>> getFactory() {
		return _factory;
	}

	@Test
	public void pdf() {
		final Range<Double> domain = new Range<>(0.0, 1.0);
		final LinearDistribution<Double> dist = new LinearDistribution<>(domain, 0);
		final Function<Double, Float64> pdf = dist.getPDF();

		for (int i = 0; i <= 10; ++i) {
			final double x = i/10.0;
			Assert.assertEquals(x*2, pdf.apply(x).doubleValue(), 0.00001);
		}

		Assert.assertEquals("p(x) = 2.000000·x + 0.000000", pdf.toString());
	}

	@Test
	public void cdf() {
		final Range<Double> domain = new Range<>(0.0, 1.0);
		final LinearDistribution<Double> dist = new LinearDistribution<>(domain, 0);
		final Function<Double, Float64> cdf = dist.getCDF();

		for (int i = 0; i <= 10; ++i) {
			final double x = i/10.0;
			final double y = cdf.apply(x).doubleValue();
			Assert.assertEquals(x*x, y, 0.0001);
		}

		Assert.assertEquals("P(x) = 1.000000·x² - 0.000000·x", cdf.toString());
	}

}
