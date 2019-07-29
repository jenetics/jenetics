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
public class SampleTest {

	@Test(dataProvider = "aritySamples")
	public void arity(final Sample<?> sample, final int arity) {
		Assert.assertEquals(sample.arity(), arity);
	}

	@DataProvider
	public Object[][] aritySamples() {
		return new Object[][] {
			{Sample.ofDouble(1, 2), 1},
			{Sample.ofDouble(1, 2, 3), 2},
			{Sample.ofDouble(1, 2, 3, 4), 3},
			{Sample.of(new Integer[]{1, 2}), 1},
			{Sample.of(new Integer[]{1, 2, 3}), 2},
			{Sample.of(new Integer[]{1, 2, 3, 4}), 3},
			{Sample.of(new Integer[]{1, 2, 3, 4, 5}), 4},
			{Sample.of(new Integer[]{1, 2, 3, 4, 5, 6}), 5}
		};
	}

	@Test(dataProvider = "resultSamples")
	public void result(final Sample<?> sample, final Object result) {
		Assert.assertEquals(sample.result(), result);
	}

	@DataProvider
	public Object[][] resultSamples() {
		return new Object[][] {
			{Sample.ofDouble(1, 2), 2.0},
			{Sample.ofDouble(1, 2, 3), 3.0},
			{Sample.ofDouble(1, 2, 3, 4), 4.0},
			{Sample.of(new Integer[]{1, 2}), 2},
			{Sample.of(new Integer[]{1, 2, 3}), 3},
			{Sample.of(new Integer[]{1, 2, 3, 4}), 4},
			{Sample.of(new Integer[]{1, 2, 3, 4, 5}), 5},
			{Sample.of(new Integer[]{1, 2, 3, 4, 5, 6}), 6}
		};
	}

	@Test(dataProvider = "argAtSamples")
	public void argAt(final Sample<?> sample, final Object[] result) {
		Assert.assertEquals(sample.arity(), result.length);
		for (int i = 0; i < sample.arity(); ++i) {
			Assert.assertEquals(sample.argAt(i), result[i]);
		}
	}

	@Test(
		dataProvider = "argAtSamples",
		expectedExceptions = ArrayIndexOutOfBoundsException.class
	)
	public void argAtIndexToSmall(final Sample<?> sample, final Object[] result) {
		sample.argAt(-1);
	}

	@Test(
		dataProvider = "argAtSamples",
		expectedExceptions = ArrayIndexOutOfBoundsException.class
	)
	public void argAtIndexToBig(final Sample<?> sample, final Object[] result) {
		sample.argAt(result.length);
	}

	@DataProvider
	public Object[][] argAtSamples() {
		return new Object[][] {
			{Sample.ofDouble(1, 2), new Double[]{1.0}},
			{Sample.ofDouble(1, 2, 3), new Double[]{1.0, 2.0}},
			{Sample.ofDouble(1, 2, 3, 4), new Double[]{1.0, 2.0, 3.0}},
			{Sample.of(new Integer[]{1, 2}), new Integer[]{1}},
			{Sample.of(new Integer[]{1, 2, 3}), new Integer[]{1, 2}},
			{Sample.of(new Integer[]{1, 2, 3, 4}), new Integer[]{1, 2, 3}},
			{Sample.of(new Integer[]{1, 2, 3, 4, 5}), new Integer[]{1, 2, 3, 4}},
			{Sample.of(new Integer[]{1, 2, 3, 4, 5, 6}), new Integer[]{1, 2, 3, 4, 5}}
		};
	}

}
