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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class LinearDistributionTest {

	@Test
	public void pdf() {
		final var domain = new Interval(0.0, 1.0);
		final var dist = new LinearDistribution(domain, 0);
		final var pdf = dist.pdf();

		for (int i = 0; i <= 10; ++i) {
			final double x = i/10.0;
			Assert.assertEquals(x*2, pdf.apply(x), 0.00001);
		}
	}

	@Test
	public void cdf() {
		final var domain = new Interval(0.0, 1.0);
		final var dist = new LinearDistribution(domain, 0);
		final var cdf = dist.cdf();

		for (int i = 0; i <= 10; ++i) {
			final double x = i/10.0;
			final double y = cdf.apply(x);
			Assert.assertEquals(x*x, y, 0.0001);
		}
	}

}
