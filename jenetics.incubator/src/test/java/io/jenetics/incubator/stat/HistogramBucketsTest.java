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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.stat;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.incubator.stat.Histogram.Bucket;
import io.jenetics.incubator.stat.Histogram.Buckets;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class HistogramBucketsTest {

	//@Test(dataProvider = "buckets")
	public void indexOf(final Buckets buckets, final double value, final int index) {
		assertThat(buckets.indexOf(value)).isEqualTo(index);
	}

	@DataProvider
	public Object[][] buckets() {
		return new Object[][] {
			{buckets(1, 2), NaN, -1},
			{buckets(1, 2), NEGATIVE_INFINITY, -1},
			{buckets(1, 2), POSITIVE_INFINITY, -1},
			{buckets(NEGATIVE_INFINITY, 2), NEGATIVE_INFINITY, 0},
			{buckets(1, 2), 1.5, 0},
			{buckets(1, 2), 0.5, -1},
			{buckets(1, 2), 2, -1},

			{buckets(1, 2, 2, 3), NEGATIVE_INFINITY, -1},
			{buckets(1, 2, 2, 3), POSITIVE_INFINITY, -1},
			{buckets(1, 2, 2, 3), 1.5, 0},
			{buckets(1, 2, 2, 3), 2.5, 1},
			{buckets(1, 2, 2, 3), 3.5, -1},
			{buckets(1, 2, 2, 3), 0.5, -1},

			{buckets(1, 2, 3, 4), NEGATIVE_INFINITY, -1},
			{buckets(1, 2, 3, 4), POSITIVE_INFINITY, -1},
			{buckets(1, 2, 3, 4), 1.5, 0},
			{buckets(1, 2, 3, 4), 3.5, 1},
			{buckets(1, 2, 3, 4), 4.5, -1},
			{buckets(1, 2, 3, 4), 0.5, -1},
			{buckets(1, 2, 3, 4), 2.5, -1},

			{buckets(1, 2, 3, 4, 4, 5), NEGATIVE_INFINITY, -1},
			{buckets(1, 2, 3, 4, 4, 5), POSITIVE_INFINITY, -1},
			{buckets(1, 2, 3, 4, 4, 5), 1.5, 0},
			{buckets(1, 2, 3, 4, 4, 5), 3.5, 1},
			{buckets(1, 2, 3, 4, 4, 5), 4.5, 2},
			{buckets(1, 2, 3, 4, 4, 5), 5.5, -1},
			{buckets(1, 2, 3, 4, 4, 5), 0.5, -1},
			{buckets(1, 2, 3, 4, 4, 5), 2.5, -1},

			{buckets(1, 2, 3, 4, 4, 5, 7, 8), NEGATIVE_INFINITY, -1},
			{buckets(1, 2, 3, 4, 4, 5, 7, 8), POSITIVE_INFINITY, -1},
			{buckets(1, 2, 3, 4, 4, 5, 7, 8), 1.5, 0},
			{buckets(1, 2, 3, 4, 4, 5, 7, 8), 3.5, 1},
			{buckets(1, 2, 3, 4, 4, 5, 7, 8), 4.5, 2},
			{buckets(1, 2, 3, 4, 4, 5, 7, 8), 7.5, 3},
			{buckets(1, 2, 3, 4, 4, 5, 7, 8), 5.5, -1},
			{buckets(1, 2, 3, 4, 4, 5, 7, 8), 5.0, -1},
			{buckets(1, 2, 3, 4, 4, 5, 7, 8), 0.5, -1},
			{buckets(1, 2, 3, 4, 4, 5, 7, 8), 2.5, -1},
			{buckets(1, 2, 3, 4, 4, 5, 7, 8), 8.0, -1}
		};
	}

	static Buckets buckets(final double... values) {
		final var buckets = new ArrayList<Bucket>();
		for (int i = 0; i < values.length; i += 2) {
			//buckets.add(new Bucket(values[i], values[i + 1]));
		}
		return new Buckets(buckets);
	}

	@Test
	public void create() {
		/*
		new Bucket(0, 1).split(10)
			.add(new Bucket(1, 10).split(10));

		final Histogram histogram = new Histogram.Builder(new Bucket(0, 10).split(100))
			.build(samples -> {

			});

		 */
	}

}
