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
package io.jenetics.prog.regression;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class DoubleSampleTest {

	@Test(dataProvider = "argsSamples")
	public void args(final DoubleSample sample, final Double[] result) {
		final Double[] args = sample.args();
		Assert.assertEquals(args.length, result.length);

		for (int i = 0; i < args.length; ++i) {
			Assert.assertEquals(args[i], result[i]);
		}
	}

	@DataProvider
	public Object[][] argsSamples() {
		return new Object[][] {
			{Sample.ofDouble(1, 2), new Double[]{1.0}},
			{Sample.ofDouble(1, 2, 3), new Double[]{1.0, 2.0}},
			{Sample.ofDouble(1, 2, 3, 4), new Double[]{1.0, 2.0, 3.0}}
		};
	}

}
