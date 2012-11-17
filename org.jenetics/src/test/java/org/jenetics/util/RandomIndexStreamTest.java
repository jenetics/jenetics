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

		final Random random = new XORShiftRandom();
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
//			final ProbabilityIndexIterator itt =
//				new ProbabilityIndexIterator(n, p, random);
//
//			for (int j = itt.next(); j != -1; j = itt.next()) {
//				final ProbabilityIndexIterator ittt =
//					new ProbabilityIndexIterator(n, p, random);
//
//				for (int k = ittt.next(); k != -1; k = ittt.next()) {
//					++kt;
//				}
//			}
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




