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

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class SeparatorsTest {

	record TestData(
		double[] separators,
		double[] values,
		int[] indexes
	) {}

	@Test(dataProvider = "data")
	void bucketIndexOf(TestData data) {
		final var separators = new Separators(data.separators);

		assertThat(data.values.length).isEqualTo(data.indexes.length);

		for (int i = 0; i < data.indexes.length; ++i) {
			final var index = separators.bucketIndexOf(data.values[i]);

			assertThat(index)
				.withFailMessage(
					"Expected bucket for %s was %s, but got %s.",
					data.values[i], data.indexes[i], index
				)
				.isEqualTo(data.indexes[i]);
		}
	}

	@DataProvider
	public Object[][] data() {
		return new Object[][] {
			{
				new TestData(
					new double[0],
					new double[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
					new int[]    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
				)
			},
			{
				new TestData(
					new double[] {5},
					new double[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
					new int[]    {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1}
				)
			},
			{
				new TestData(
					new double[] {3, 7},
					new double[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
					new int[]    {0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2}
				)
			},
			{
				new TestData(
					new double[] {2, 5, 7},
					new double[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
					new int[]    {0, 0, 1, 1, 1, 2, 2, 3, 3, 3, 3}
				)
			},
			{
				new TestData(
					new double[] {2, 5, 7, 10},
					new double[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
					new int[]    {0, 0, 1, 1, 1, 2, 2, 3, 3, 3, 4}
				)
			},
			{
				new TestData(
					new double[] {2, 5, 7, 10},
					new double[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 100},
					new int[]    {0, 0, 1, 1, 1, 2, 2, 3, 3, 3, 4}
				)
			},
			{
				new TestData(
					new double[] {2, 5, 7, 10},
					new double[] {Double.NEGATIVE_INFINITY, 1, 2, 3, 4, 5, 6, 7, 8, 9, Double.POSITIVE_INFINITY},
					new int[]    {0, 0, 1, 1, 1, 2, 2, 3, 3, 3, 4}
				)
			}
		};
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void nanSeparator() {
		new Separators(1, 2, 3, Double.NaN);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void duplicateSeparator() {
		new Separators(1, 2, 3, 2);
	}

	@Test
	public void of() {
		final var sep = Separators.of(1, 2, 1);

		assertThat(sep.bucketIndexOf(0)).isEqualTo(0);
		assertThat(sep.bucketIndexOf(1)).isEqualTo(1);
		assertThat(sep.bucketIndexOf(2)).isEqualTo(2);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void ofZeroClasses() {
		Separators.of(1,2, 0);
	}

	@Test
	public void slice() {
		final var seps = new Separators(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		assertThat(seps.slice(2, -2).toArray())
			.isEqualTo(new double[] {2, 3, 4, 5, 6, 7, 8});
	}

	@Test(expectedExceptions = IndexOutOfBoundsException.class)
	public void invalidSlice2() {
		final var seps = new Separators(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		seps.slice(1, 100);
	}

	@Test(expectedExceptions = IndexOutOfBoundsException.class)
	public void invalidSlice3() {
		final var seps = new Separators(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		seps.slice(1, -100);
	}

}
