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

import static org.jenetics.internal.math.random.indexes;

import java.util.PrimitiveIterator.OfInt;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.internal.math.probability;
import org.jenetics.internal.util.IntRef;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.LongMomentStatistics;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class RandomIndexStreamTest {

	@Test
	public void compatibility() {
		final TestData data = TestData.of("/org/jenetics/util/IndexStream.Random");

		for (String[] line : data) {
			final Random random = new LCG64ShiftRandom.ThreadSafe(0);
			final double p = Double.parseDouble(line[0]);

			final OfInt it = indexes(random, 500, p).iterator();
			for (int i = 1; i < line.length; ++i) {
				final int index = Integer.parseInt(line[i]);
				Assert.assertEquals(it.nextInt(), index);
			}

			Assert.assertFalse(it.hasNext());
		}
	}

	@Test
	public void reference() {
		final int size = 5000;
		final double p = 0.5;

		final Random random1 = new LCG64ShiftRandom(0);
		final Random random2 = new LCG64ShiftRandom(0);

		for (int j = 0; j < 1; ++j) {
			final OfInt it = indexes(random1, size, p).iterator();
			final IndexStream stream2 = ReferenceRandomStream(
				size, p, random2
			);

			while (it.hasNext()) {
				Assert.assertEquals(it.nextInt(), stream2.next());
			}

			Assert.assertFalse(it.hasNext());
			Assert.assertEquals(stream2.next(), -1);
		}
	}

	@Test(dataProvider = "probabilities")
	public void distribution(final Integer n, final Double p) {
		final double mean = n*p;
		final double var = n*p*(1 - p);

		final Random random = new LCG64ShiftRandom();
		final Range<Long> domain = new Range<>(0L, n.longValue());

		final Histogram<Long> histogram = Histogram.ofLong(domain.getMin(), domain.getMax(), 10);
		final LongMomentStatistics variance = new LongMomentStatistics();
		for (int i = 0; i < 2500; ++i) {
			final long k = k(n, p, random);

			histogram.accept(k);
			variance.accept(k);
		}

		// Normal distribution as approximation for binomial distribution.
		// TODO: Implement test
		//assertDistribution(histogram, new NormalDistribution<>(domain, mean, var));
	}

	double var(final double p, final long N) {
		return N*p*(1.0 - p);
	}

	double mean(final double p, final long N) {
		return N*p;
	}

	long k(final int n, final double p, final Random random) {
		final IntRef kt = new IntRef(0);
		indexes(random, n, p).forEach(i -> {
			++kt.value;
		});

		return kt.value;
	}

	@DataProvider(name = "probabilities")
	public Object[][] probabilities() {
		return new Object[][] {
			// n,      p
			{1115,  0.015},
			{1150,  0.015},
			{1160,  0.015},
			{1170,  0.015},
			{11100, 0.015},
			{11200, 0.015},
			{11500, 0.015},

			{1115,  0.15},
			{1150,  0.15},
			{1160,  0.15},
			{1170,  0.15},
			{11100, 0.15},
			{11200, 0.15},
			{11500, 0.15},

			{515,   0.5},
			{1115,  0.5},
			{1150,  0.5},
			{1160,  0.5},
			{1170,  0.5},
			{11100, 0.5},
			{11200, 0.5},
			{11500, 0.5},

			{515,   0.85},
			{1115,  0.85},
			{1150,  0.85},
			{1160,  0.85},
			{1170,  0.85},
			{11100, 0.85},
			{11200, 0.85},
			{11500, 0.85},

			{515,   0.99},
			{1115,  0.99},
			{1150,  0.99},
			{1160,  0.99},
			{1170,  0.99},
			{11100, 0.99},
			{11200, 0.99},
			{11500, 0.99}
		};
	}


	public static IndexStream ReferenceRandomStream(
		final int n,
		final double p,
		final Random random
	) {
		return new IndexStream() {
			private final int P = probability.toInt(p);
			private int _pos = -1;
			@Override public int next() {
				while (_pos < n && random.nextInt() >= P) {
					++_pos;
				}
				return (_pos < n - 1) ? ++_pos : -1;
			}

		};
	}

	interface IndexStream {
		public int next();
	}


//	public static void main(final String[] args) {
//		final int delta = 500;
//
//		for (int i = 0; i <= delta; ++i) {
//			final double p = (double)(i)/(double)delta;
//			final Random random = new LCG64ShiftRandom.ThreadSafe(0);
//			final IndexStream stream = ReferenceRandomStream(delta, p, random);
//
//			System.out.print(Double.toString(p));
//			System.out.print(",");
//			for (int j = stream.next(); j != -1; j = stream.next()) {
//				System.out.print(j);
//				System.out.print(",");
//			}
//			System.out.println();
//		}
//	}

}
