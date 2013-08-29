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
package org.jenetics.util;

import java.util.Random;

import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.StatisticsAssert;
import org.jenetics.stat.UniformDistribution;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
 */
public class mathRandomTest {

	@Test(invocationCount = 5)
	public void toFloat_int() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate((double)math.random.toFloat(random.nextInt()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 5)
	public void toFloat_long() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate((double)math.random.toFloat(random.nextLong()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 5)
	public void toDouble_long() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate(math.random.toDouble(random.nextLong()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 5)
	public void toDouble_int_int() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			final long value = random.nextLong();
			histogram.accumulate(math.random.toDouble((int)(value >>> 32), (int)value));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 5)
	public void toFloat2_int() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate((double)math.random.toFloat2(random.nextInt()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 5)
	public void toFloat2_long() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate((double)math.random.toFloat2(random.nextLong()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 5)
	public void toDouble2_long() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate(math.random.toDouble2(random.nextLong()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 5)
	public void toDouble2_int_int() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			final long value = random.nextLong();
			histogram.accumulate(math.random.toDouble2((int)(value >>> 32), (int)value));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

}









