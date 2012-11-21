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

import java.io.IOException;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.MappedAccumulatorTester;
import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.TestDataIterator;
import org.jenetics.util.TestDataIterator.Data;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class VarianceTest extends MappedAccumulatorTester<Variance<Double>> {

	private final static String DATA = "/org/jenetics/util/statistic-moments.txt";

	private final Factory<Variance<Double>> _factory = new Factory<Variance<Double>>() {
		@Override
		public Variance<Double> newInstance() {
			final Random random = RandomRegistry.getRandom();

			final Variance<Double> variance = new Variance<>();
			for (int i = 0; i < 1000; ++i) {
				variance.accumulate(random.nextGaussian());
			}

			return variance;
		}
	};
	@Override
	protected Factory<Variance<Double>> getFactory() {
		return _factory;
	}

	@Test
	public void variance() throws IOException {
		try (TestDataIterator it = dataIt()) {
			final Variance<Double> moment = new Variance<>();
			while (it.hasNext()) {
				final Data data = it.next();
				moment.accumulate(data.number);

				Assert.assertEquals(moment.getMean(), data.mean);
				Assert.assertEquals(moment.getVariance(), data.variance, 0.000001);
			}
		}
	}

	private static TestDataIterator dataIt() throws IOException {
		return new TestDataIterator(
			MeanTest.class.getResourceAsStream(DATA), "\\s"
		);
	}

}
