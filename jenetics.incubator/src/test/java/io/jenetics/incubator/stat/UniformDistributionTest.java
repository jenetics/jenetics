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
public class UniformDistributionTest {

	@Test
	public void pdf() {
		final var dist = new UniformDistribution(new Interval(0.0, 10.0));
		final var pdf = dist.pdf();

		Assert.assertEquals(pdf.apply(0.00), 0.1);
		Assert.assertEquals(pdf.apply(1.15), 0.1);
		Assert.assertEquals(pdf.apply(2.24), 0.1);
		Assert.assertEquals(pdf.apply(3.43), 0.1);
		Assert.assertEquals(pdf.apply(4.42), 0.1);
		Assert.assertEquals(pdf.apply(5.59), 0.1);
		Assert.assertEquals(pdf.apply(10.0), 0.1);

		Assert.assertEquals(pdf.apply(-0.01), 0.0);
		Assert.assertEquals(pdf.apply(10.01), 0.0);
	}

	@Test
	public void cdf() {
		final var dist = new UniformDistribution(new Interval(0.0, 10.0));
		final var cdf = dist.cdf();

		Assert.assertEquals(cdf.apply(-9.0), 0.0);
		Assert.assertEquals(cdf.apply(0.0), 0.0);
		Assert.assertEquals(cdf.apply(1.0), 0.1);
		Assert.assertEquals(cdf.apply(2.0), 0.2);
		Assert.assertEquals(cdf.apply(3.0), 0.3);
		Assert.assertEquals(cdf.apply(4.0), 0.4);
		Assert.assertEquals(cdf.apply(5.0), 0.5);
		Assert.assertEquals(cdf.apply(6.0), 0.6);
		Assert.assertEquals(cdf.apply(7.0), 0.7);
		Assert.assertEquals(cdf.apply(8.0), 0.8);
		Assert.assertEquals(cdf.apply(9.0), 0.9);
		Assert.assertEquals(cdf.apply(10.0), 1.0);
		Assert.assertEquals(cdf.apply(19.0), 1.0);
	}

}
