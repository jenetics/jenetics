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
package org.jenetics.stat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class SummaryTest {

	private List<Double> numbers(final int size) {
		final Random random = new Random(123);
		final List<Double> numbers = new ArrayList<>(size);
		for (int i = 0; i < size; ++i) {
			numbers.add(random.nextDouble());
		}

		return numbers;
	}

	@Test
	public void summary() {
		final List<Double> numbers = numbers(10000);

		final DescriptiveStatistics expected = new DescriptiveStatistics();
		numbers.forEach(expected::addValue);

		final Summary<Double> summary = numbers.stream().collect(Summary.collector());
		Assert.assertEquals(summary.getSampleCount(), numbers.size());
		Assert.assertEquals(summary.getMin(), expected.getMin());
		Assert.assertEquals(summary.getMax(), expected.getMax());
		Assert.assertEquals(summary.getSum(), expected.getSum(), 0.00001);
		Assert.assertEquals(summary.getMean(), expected.getMean(), 0.00001);
		Assert.assertEquals(summary.getVariance(), expected.getVariance(), 0.00001);
		Assert.assertEquals(summary.getSkewness(), expected.getSkewness(), 0.00001);
		Assert.assertEquals(summary.getKurtosis(), expected.getKurtosis(), 0.00001);

		final Summary<Double> psummary = numbers.parallelStream().collect(Summary.collector());
		Assert.assertEquals(psummary.getSampleCount(), numbers.size());
		Assert.assertEquals(psummary.getMin(), expected.getMin());
		Assert.assertEquals(psummary.getMax(), expected.getMax());
		Assert.assertEquals(psummary.getSum(), expected.getSum(), 0.00001);
		Assert.assertEquals(psummary.getMean(), expected.getMean(), 0.00001);
		Assert.assertEquals(psummary.getVariance(), expected.getVariance(), 0.00001);
		Assert.assertEquals(psummary.getSkewness(), expected.getSkewness(), 0.00001);
		Assert.assertEquals(psummary.getKurtosis(), expected.getKurtosis(), 0.00001);
	}

}
