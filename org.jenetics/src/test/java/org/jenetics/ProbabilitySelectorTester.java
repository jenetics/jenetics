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
package org.jenetics;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.util.RandomRegistry;
import org.jenetics.util.lists;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public abstract class ProbabilitySelectorTester<
	S extends ProbabilitySelector<DoubleGene, Double>
>
	extends SelectorTester<S>
{

	protected abstract boolean isSorted();

	@Test
	public void indexOf() {
		final Random random = RandomRegistry.getRandom();

		final double[] props = new double[10];
		double divisor = props.length*(props.length + 1)/2.0;
		for (int i = 0; i < props.length; ++i) {
			props[i] = (i + 1)/divisor;
		}
		randomize(props, random);

		final double[] incremental = ProbabilitySelector.incremental(props.clone());

		double samples = 1000000;
		double[] indices = new double[props.length];
		Arrays.fill(indices, 0);

		for (int i = 0; i < samples; ++i) {
			indices[ProbabilitySelector.indexOf(incremental, random.nextDouble())] += 1;
		}

		for (int i = 0; i < props.length; ++i) {
			indices[i] /= samples;
		}

		Reporter.log(toString(props) + String.format(": %6f", sum(props)));
		Reporter.log(toString(indices) + String.format(": %6f", sum(indices)));

		for (int i = 0; i < props.length; ++i) {
			Assert.assertEquals(indices[i], props[i], 0.005);
		}
	}

	@Test(dataProvider = "probabilitySizes")
	public void indexOf(final Integer size) {
		final Random random = RandomRegistry.getRandom();

		final double[] props = new double[size];
		double divisor = props.length*(props.length + 1)/2.0;
		for (int i = 0; i < props.length; ++i) {
			props[i] = (i + 1)/divisor;
		}
		randomize(props, random);

		final double[] incremental = ProbabilitySelector.incremental(props.clone());

		final int samples = 100000;
		for (int i = 0; i < samples; ++i) {
			final double value = random.nextDouble();
			final int index1 = ProbabilitySelector.indexOf(incremental, value);
			final int index2 = indexOf(props, value);

			Assert.assertEquals(index1, index2);
		}
	}

	private static int indexOf(final double[] array, final double value) {
		int j = 0;
		double sum = 0;
		for (int i = 0; sum < value && i < array.length; ++i) {
			sum += array[i];
			j = i;
		}
		return j;
	}

	@DataProvider(name = "probabilitySizes")
	public Object[][] probabilitySizes() {
		return new Object[][] {
			{1}, {2}, {3}, {5}, {9}, {15}, {30}, {99}, {150}
		};
	}

	@Test
	public void probabilities() {
		final Population<DoubleGene, Double> population = TestUtils.newDoublePopulation(100);
		lists.shuffle(population, new Random(System.currentTimeMillis()));

		final S selector = factory().newInstance();
		final double[] props = selector.probabilities(population, 23);
		Assert.assertEquals(props.length, population.size());

//		if (isSorted()) {
//			assertSortedDescending(population);
//			assertSortedDescending(props);
//		}
		Assert.assertEquals(sum(props), 1.0, 0.000001);
		assertPositive(props);
	}

	private static String toString(final double[] array) {
		StringBuilder out = new StringBuilder();

		out.append("[");
		if (array.length > 0) {
			out.append(String.format("%6f", array[0]));
		}
		for (int i = 1; i < array.length; ++i) {
			out.append(", ");
			out.append(String.format("%6f", array[i]));
		}
		out.append("]");

		return out.toString();
	}

	protected static double sum(final double[] array) {
		double sum = 0;
		for (int i = 0; i < array.length; ++i) {
			sum += array[i];
		}
		return sum;
	}

	protected static void assertPositive(final double[] array) {
		for (int i = 0; i < array.length; ++i) {
			Assert.assertTrue(array[i] >= 0.0, "All values must be positive: " + array[i]);
		}
	}

	private static void randomize(final double[] array, final Random random) {
		requireNonNull(array, "Array");
		for (int j = array.length - 1; j > 0; --j) {
			swap(array, j, random.nextInt(j + 1));
		}
	}

	private static void swap(final double[] array, int i, int j) {
		double temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	protected static void assertSortedDescending(final double[] values) {
		for (int i = 1; i < values.length; ++i) {
			Assert.assertTrue(values[i - 1] >= values[i]);
		}
	}

	protected static <T extends Comparable<T>>
	void assertSortedDescending(final List<? extends T> values) {
		for (int i = 1; i < values.size(); ++i) {
			Assert.assertTrue(values.get(i - 1).compareTo(values.get(i)) >= 0);
		}
	}

}
