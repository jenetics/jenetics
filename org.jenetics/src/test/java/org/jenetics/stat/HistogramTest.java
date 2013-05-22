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
package org.jenetics.stat;

import java.util.Arrays;
import java.util.Random;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.number.Integer64;
import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.MappedAccumulatorTester;
import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
 */
public class HistogramTest
	extends MappedAccumulatorTester<Histogram<Double>>
{

	private final Factory<Histogram<Double>>
	_factory = new Factory<Histogram<Double>>() {
		@Override
		public Histogram<Double> newInstance() {
			final Random random = RandomRegistry.getRandom();
			final double min = random.nextInt(100) + 100;
			final double max = random.nextInt(100) + 100 + min;

			final Histogram<Double> histogram = Histogram.valueOf(
						min, max, random.nextInt(10) + 5
					);
			for (int i = 0; i < 1000; ++i) {
				histogram.accumulate(random.nextGaussian()*(max - min) + min);
			}

			return histogram;
		}
	};
	@Override
	protected Factory<Histogram<Double>> getFactory() {
		return _factory;
	}

	@Test
	public void createDouble() {
		final double begin = 12;
		final double end = 123;
		final int elements = 10;

		Histogram<Double> histogram = Histogram.valueOf(begin, end, elements);
		Assert.assertEquals(histogram.length(), elements);
		Assert.assertEquals(histogram.getHistogram(), new long[elements]);
	}

	@Test
	public void createFloat64() {
		final Float64 begin = Float64.valueOf(12);
		final Float64 end = Float64.valueOf(123);
		final int elements = 10;

		Histogram<Float64> histogram = Histogram.valueOf(begin, end, elements);
		Assert.assertEquals(histogram.length(), elements);
		Assert.assertEquals(histogram.getHistogram(), new long[elements]);
	}

	@Test
	public void createLong() {
		final long begin = 0;
		final long end = 1000;
		final int elements = 9;

		Histogram<Long> histogram = Histogram.valueOf(begin, end, elements);
		Assert.assertEquals(histogram.length(), elements);
		Assert.assertEquals(histogram.getHistogram(), new long[elements]);
	}

	@Test
	public void createInteger64() {
		final Integer64 begin = Integer64.ZERO;
		final Integer64 end = Integer64.valueOf(1000);
		final int elements = 9;

		Histogram<Integer64> histogram = Histogram.valueOf(begin, end, elements);
		Assert.assertEquals(histogram.length(), elements);
		Assert.assertEquals(histogram.getHistogram(), new long[elements]);
	}

	@Test
	public void accumulate() {
		final long begin = 0;
		final long end = 10;
		final int elements = 9;

		Histogram<Long> histogram = Histogram.valueOf(begin, end, elements);
		for (int i = 0; i < elements*1000; ++i) {
			histogram.accumulate(Long.valueOf(i%elements));
		}

		final long[] expected = new long[9];
		Arrays.fill(expected, 1000);
		Assert.assertEquals(histogram.getHistogram(), expected);
	}

	@Test
	public void histogramIndex() {
		final Random random = RandomRegistry.getRandom();
		Double[] parts = new Double[10000];
		for (int i = 0; i < parts.length; ++i) {
			parts[i] = Double.valueOf(i);
		}

		Histogram<Double> histogram = Histogram.valueOf(parts);
		Double[] classes = histogram.getSeparators();
		for (int i = 0; i < 1000; ++i) {
			final Double value = random.nextDouble()*(parts.length + 1);
			Assert.assertEquals(histogram.index(value), linearindex(classes, value));
		}

		parts = new Double[]{1.0};
		histogram = Histogram.valueOf(parts);
		classes = histogram.getSeparators();
		for (int i = 0; i < 10; ++i) {
			final Double value = random.nextDouble()*(parts.length + 1);
			Assert.assertEquals(histogram.index(value), linearindex(classes, value));
		}

		parts = new Double[]{1.0, 2.0};
		histogram = Histogram.valueOf(parts);
		classes = histogram.getSeparators();
		for (int i = 0; i < 10; ++i) {
			final Double value = random.nextDouble()*(parts.length + 1);
			Assert.assertEquals(histogram.index(value), linearindex(classes, value));
		}

		parts = new Double[]{1.0, 2.0, 3.0};
		histogram = Histogram.valueOf(parts);
		classes = histogram.getSeparators();
		for (int i = 0; i < 10; ++i) {
			final Double value = random.nextDouble()*(parts.length + 1);
			Assert.assertEquals(histogram.index(value), linearindex(classes, value));
		}
	}

	// The 'brute force' variante to test the binsearch one.
	private static <C extends Comparable<C>> int linearindex(final C[] classes, final C value) {
		int index = classes.length;
		for (int i = 0; i < classes.length && index == classes.length; ++i) {
			if (value.compareTo(classes[i]) < 0) {
				index = i;
			}
		}
		return index;
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void histogramEmptyClasses() {
		Histogram.valueOf(new Double[0]);
	}

	@Test
	public void histogram() {
		final Random random = RandomRegistry.getRandom();
		final Histogram<Double> histogram = Histogram.valueOf(new Double[]{1d, 2d, 3d, 4d, 5d});

		for (int i = 0; i < 600000; ++i) {
			histogram.accumulate(random.nextDouble()*6);
		}
		Assert.assertEquals(histogram.getSamples(), 600000);

		final long[] hist = histogram.getHistogram();
		for (int i = 0; i < hist.length; ++i) {
			Assert.assertEquals(hist[i], 100000.0, 1000.0);
		}
	}

}



