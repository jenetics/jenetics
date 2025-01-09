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
import static java.lang.Double.POSITIVE_INFINITY;
import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.incubator.stat.Histogram.Bucket;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class HistogramBucketTest {

	@Test(dataProvider = "buckets")
	public void isOverlapping(final Bucket b1, final Bucket b2, final boolean result) {
		assertThat(b1.isOverlapping(b2)).isEqualTo(result);
		assertThat(b2.isOverlapping(b1)).isEqualTo(result);

		assertThat(b1.isOverlapping(b1)).isEqualTo(true);
		assertThat(b2.isOverlapping(b2)).isEqualTo(true);
	}

	@DataProvider
	public Object[][] buckets() {
		return new Object[][] {
			{new Bucket(1, 2), new Bucket(3, 4), false},
			{new Bucket(1, 2), new Bucket(2, 3), false},
			{new Bucket(1, 2), new Bucket(1.9, 3), true},
			{new Bucket(1, 2.1), new Bucket(2, 3), true},
			{new Bucket(1, 20), new Bucket(2, 3), true},
			{new Bucket(NEGATIVE_INFINITY, 20), new Bucket(2, 3), true},
			{new Bucket(1, POSITIVE_INFINITY), new Bucket(2, 3), true},
			{new Bucket(NEGATIVE_INFINITY, 20), new Bucket(NEGATIVE_INFINITY, 3), true},
			{new Bucket(NEGATIVE_INFINITY, POSITIVE_INFINITY), new Bucket(NEGATIVE_INFINITY, POSITIVE_INFINITY), true}
		};
	}

	@Test
	public void next() {
		final var bucket = new Bucket(1, 2);
		assertThat(bucket.next(10)).isEqualTo(new Bucket(2, 12));
		assertThat(bucket.next(POSITIVE_INFINITY)).isEqualTo(new Bucket(2, POSITIVE_INFINITY));
	}

	@Test
	public void previous() {
		final var bucket = new Bucket(5, 6);
		assertThat(bucket.previous(3)).isEqualTo(new Bucket(2, 5));
		assertThat(bucket.previous(POSITIVE_INFINITY)).isEqualTo(new Bucket(NEGATIVE_INFINITY, 5));
	}

}
