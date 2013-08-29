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

import static org.jenetics.stat.StatisticsAssert.assertDistribution;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.NormalDistribution;
import org.jenetics.stat.Variance;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
 */
public class RandomIndexStreamTest {

	@Test
	public void repeatable() {
		final IndexStream stream1 = IndexStream.Random(1000, 0.5, new Random(1));
		final IndexStream stream2 = IndexStream.Random(1000, 0.5, new Random(1));

		for (int i = stream1.next(); i != -1; i = stream1.next()) {
			Assert.assertEquals(i, stream2.next());
		}
		Assert.assertEquals(stream2.next(), -1);
	}

	@Test
	public void compatibility() {
		final TestData data = new TestData(
			"/org/jenetics/util/IndexStream.Random.dat"
		);

		final Random random = new LCG64ShiftRandom.ThreadSafe(0);
		for (String[] line : data) {
			final double p = Double.parseDouble(line[0]);
			final IndexStream stream = IndexStream.Random(1000, p, random);

			for (int i = 1; i < line.length; ++i) {
				final int index = Integer.parseInt(line[i]);
				Assert.assertEquals(stream.next(), index);
			}

			Assert.assertEquals(stream.next(), -1);
		}
	}

	@Test
	public void iterateP0() {
		final IndexStream it = IndexStream.Random(1000, 0, new Random());

		for (int i = it.next(); i != -1; i = it.next()) {
			Assert.assertTrue(false);
		}

		for (int i = 0; i < 100; ++i) {
			Assert.assertEquals(it.next(), -1);
		}
	}

	@Test
	public void iterateP1() {
		final IndexStream it = IndexStream.Random(1000, 1, new Random());

		int count = 0;
		for (int i = it.next(); i != -1; i = it.next()) {
			Assert.assertEquals(i, count);
			++count;
		}

		Assert.assertEquals(count, 1000);
	}

	@Test(dataProvider = "probabilities")
	public void distribution(final Integer n, final Double p) {
		final double mean = n*p;
		final double var = n*p*(1 - p);

		final Random random = new LCG64ShiftRandom();
		final Range<Long> domain = new Range<>(0L, n.longValue());

		final Histogram<Long> histogram = Histogram.valueOf(
					domain.getMin(), domain.getMax(), 10
				);
		final Variance<Long> variance = new Variance<>();
		for (int i = 0; i < 2500; ++i) {
			final long k = k(n, p, random);

			histogram.accumulate(k);
			variance.accumulate(k);
		}

		// Normal distribution as approximation for binomial distribution.
		assertDistribution(histogram, new NormalDistribution<>(domain, mean, var));
	}

	double var(final double p, final long N) {
		return N*p*(1.0 - p);
	}

	double mean(final double p, final long N) {
		return N*p;
	}

	long k(final int n, final double p, final Random random) {
		final IndexStream it = IndexStream.Random(n, p, random);

		int kt = 0;
		for (int i = it.next(); i != -1; i = it.next()) {
			++kt;
		}
		return kt;
	}

	@DataProvider(name = "probabilities")
	public Object[][] probabilities() {
		return new Object[][] {
				//    n,                p
				{ new Integer(1115),  new Double(0.015) },
				{ new Integer(1150),  new Double(0.015) },
				{ new Integer(1160),  new Double(0.015) },
				{ new Integer(1170),  new Double(0.015) },
				{ new Integer(11100), new Double(0.015) },
				{ new Integer(11200), new Double(0.015) },
				{ new Integer(11500), new Double(0.015) },

				{ new Integer(1115),  new Double(0.15) },
				{ new Integer(1150),  new Double(0.15) },
				{ new Integer(1160),  new Double(0.15) },
				{ new Integer(1170),  new Double(0.15) },
				{ new Integer(11100), new Double(0.15) },
				{ new Integer(11200), new Double(0.15) },
				{ new Integer(11500), new Double(0.15) },

				{ new Integer(515),   new Double(0.5) },
				{ new Integer(1115),  new Double(0.5) },
				{ new Integer(1150),  new Double(0.5) },
				{ new Integer(1160),  new Double(0.5) },
				{ new Integer(1170),  new Double(0.5) },
				{ new Integer(11100), new Double(0.5) },
				{ new Integer(11200), new Double(0.5) },
				{ new Integer(11500), new Double(0.5) },

				{ new Integer(515),   new Double(0.85) },
				{ new Integer(1115),  new Double(0.85) },
				{ new Integer(1150),  new Double(0.85) },
				{ new Integer(1160),  new Double(0.85) },
				{ new Integer(1170),  new Double(0.85) },
				{ new Integer(11100), new Double(0.85) },
				{ new Integer(11200), new Double(0.85) },
				{ new Integer(11500), new Double(0.85) },

				{ new Integer(515),   new Double(0.99) },
				{ new Integer(1115),  new Double(0.99) },
				{ new Integer(1150),  new Double(0.99) },
				{ new Integer(1160),  new Double(0.99) },
				{ new Integer(1170),  new Double(0.99) },
				{ new Integer(11100), new Double(0.99) },
				{ new Integer(11200), new Double(0.99) },
				{ new Integer(11500), new Double(0.99) }
		};
	}
}




