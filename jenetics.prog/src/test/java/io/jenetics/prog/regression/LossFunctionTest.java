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
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class LossFunctionTest {

	@Test
	public void mse() {
		final Double[] expected = new Double[100];
		final Double[] calculated = new Double[100];

		for (int i = 0; i < expected.length; ++i) {
			expected[i] = (double)i;
			calculated[i] = (double)i + 1;
		}
		Assert.assertEquals(LossFunction.mse(calculated, expected), 1.0);

		for (int i = 0; i < expected.length; ++i) {
			calculated[i] = (double)i + 2;
		}
		Assert.assertEquals(LossFunction.mse(calculated, expected), 4.0);

		for (int i = 0; i < expected.length; ++i) {
			calculated[i] = (double)i + 3;
		}
		Assert.assertEquals(LossFunction.mse(calculated, expected), 9.0);
	}

	@Test
	public void mae() {
		final Double[] expected = new Double[100];
		final Double[] calculated = new Double[100];

		for (int i = 0; i < expected.length; ++i) {
			expected[i] = (double)i;
			calculated[i] = (double)i + 1;
		}
		Assert.assertEquals(LossFunction.mae(calculated, expected), 1.0);

		for (int i = 0; i < expected.length; ++i) {
			calculated[i] = (double)i + 2;
		}
		Assert.assertEquals(LossFunction.mae(calculated, expected), 2.0);

		for (int i = 0; i < expected.length; ++i) {
			calculated[i] = (double)i + 3;
		}
		Assert.assertEquals(LossFunction.mae(calculated, expected), 3.0);
	}

}
