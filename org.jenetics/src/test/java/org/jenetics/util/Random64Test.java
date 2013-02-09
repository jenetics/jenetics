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
package org.jenetics.util;

import java.util.Random;

import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.StatisticsAssert;
import org.jenetics.stat.UniformDistribution;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-02-09 $</em>
 */
public class Random64Test {

	@Test(invocationCount = 20, successPercentage = 99)
	public void toFloat_int() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate((double)Random64.toFloat(random.nextInt()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 20, successPercentage = 99)
	public void toFloat_long() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate((double)Random64.toFloat(random.nextLong()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 20, successPercentage = 99)
	public void toDouble_long() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate(Random64.toDouble(random.nextLong()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 20, successPercentage = 99)
	public void toDouble_int_int() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate(Random64.toDouble(random.nextInt(), random.nextInt()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 20, successPercentage = 99)
	public void toFloat2_int() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate((double)Random64.toFloat2(random.nextInt()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 20, successPercentage = 99)
	public void toFloat2_long() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate((double)Random64.toFloat2(random.nextLong()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 20, successPercentage = 99)
	public void toDouble2_long() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate(Random64.toDouble2(random.nextLong()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 20, successPercentage = 99)
	public void toDouble2_int_int() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate(Random64.toDouble2(random.nextInt(), random.nextInt()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

}









