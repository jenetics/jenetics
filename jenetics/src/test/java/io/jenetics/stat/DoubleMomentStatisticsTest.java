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
package io.jenetics.stat;

import static org.apache.commons.statistics.descriptive.Statistic.KURTOSIS;
import static org.apache.commons.statistics.descriptive.Statistic.MAX;
import static org.apache.commons.statistics.descriptive.Statistic.MEAN;
import static org.apache.commons.statistics.descriptive.Statistic.MIN;
import static org.apache.commons.statistics.descriptive.Statistic.SKEWNESS;
import static org.apache.commons.statistics.descriptive.Statistic.SUM;
import static org.apache.commons.statistics.descriptive.Statistic.VARIANCE;
import static io.jenetics.stat.DoubleMomentStatistics.toDoubleMomentStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.statistics.descriptive.DoubleStatistics;
import org.apache.commons.statistics.descriptive.Statistic;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class DoubleMomentStatisticsTest {

	private List<Double> numbers(final int size) {
		final Random random = new Random(123);
		final List<Double> numbers = new ArrayList<>(size);
		for (int i = 0; i < size; ++i) {
			numbers.add(random.nextDouble());
		}

		return numbers;
	}

	@Test(dataProvider = "sampleCounts")
	public void summary(final Integer sampleCounts, final Double epsilon) {
		final List<Double> numbers = numbers(sampleCounts);

		final var expected = DoubleStatistics
			.builder(Statistic.values())
			.build(numbers.stream().mapToDouble(Double::doubleValue).toArray());

		final DoubleMomentStatistics summary = numbers.stream()
			.collect(toDoubleMomentStatistics(Double::doubleValue));

		Assert.assertEquals(summary.count(), numbers.size());
		assertEqualsDouble(summary.min(), expected.getAsDouble(MIN), 0.0);
		assertEqualsDouble(summary.max(), expected.getAsDouble(MAX), 0.0);
		assertEqualsDouble(summary.sum(), expected.getAsDouble(SUM), epsilon);
		assertEqualsDouble(summary.mean(), expected.getAsDouble(MEAN), epsilon);
		assertEqualsDouble(summary.variance(), expected.getAsDouble(VARIANCE), epsilon);
		assertEqualsDouble(summary.skewness(), expected.getAsDouble(SKEWNESS), epsilon);
		assertEqualsDouble(summary.kurtosis(), expected.getAsDouble(KURTOSIS), epsilon);
	}

	@Test(dataProvider = "parallelSampleCounts")
	public void parallelSummary(final Integer sampleCounts, final Double epsilon) {
		final List<Double> numbers = numbers(sampleCounts);

		final var expected = DoubleStatistics
			.builder(Statistic.values())
			.build(numbers.stream().mapToDouble(Double::doubleValue).toArray());

		final DoubleMomentStatistics summary = numbers.parallelStream()
			.collect(toDoubleMomentStatistics(Double::doubleValue));

		Assert.assertEquals(summary.count(), numbers.size());
		assertEqualsDouble(summary.min(), expected.getAsDouble(MIN), 0.0);
		assertEqualsDouble(summary.max(), expected.getAsDouble(MAX), 0.0);
		assertEqualsDouble(summary.sum(), expected.getAsDouble(SUM), epsilon);
		assertEqualsDouble(summary.mean(), expected.getAsDouble(MEAN), epsilon);
		assertEqualsDouble(summary.variance(), expected.getAsDouble(VARIANCE), epsilon);
		assertEqualsDouble(summary.skewness(), expected.getAsDouble(SKEWNESS), epsilon);
		assertEqualsDouble(summary.kurtosis(), expected.getAsDouble(KURTOSIS), epsilon);
	}

	private static void assertEqualsDouble(final double a, final double b, final double e) {
		if (Double.isNaN(b)) {
			Assert.assertTrue(
				Double.isNaN(a),
				String.format("Expected: Double.NaN \nActual: %s", a)
			);
		} else {
			Assert.assertEquals(a, b, e);
		}
	}

	@DataProvider(name = "sampleCounts")
	public Object[][] sampleCounts() {
		return new Object[][] {
			{0, 0.0},
			{1, 0.0},
			{2, 0.05},
			{3, 0.05},
			{100, 0.05},
			{1_000, 0.0001},
			{10_000, 0.00001},
			{100_000, 0.000001},
			{1_000_000, 0.0000001},
			{2_000_000, 0.0000005}
		};
	}

	@DataProvider(name = "parallelSampleCounts")
	public Object[][] parallelSampleCounts() {
		return new Object[][] {
			{0, 0.0},
			{1, 0.0},
			{2, 0.05},
			{3, 0.05},
			{100, 0.5},
			{1_0, 0.003},
			{10_000, 0.00001},
			{100_000, 0.000001},
			{1_000_000, 0.0000001},
			{2_000_000, 0.0000005}
		};
	}

	@Test
	public void sameState() {
		final DoubleMomentStatistics dms1 = new DoubleMomentStatistics();
		final DoubleMomentStatistics dms2 = new DoubleMomentStatistics();

		final Random random = new Random();
		for (int i = 0; i < 100; ++i) {
			final double value = random.nextDouble();
			dms1.accept(value);
			dms2.accept(value);

			Assert.assertTrue(dms1.sameState(dms2));
			Assert.assertTrue(dms2.sameState(dms1));
			Assert.assertTrue(dms1.sameState(dms1));
			Assert.assertTrue(dms2.sameState(dms2));
		}
	}

}
