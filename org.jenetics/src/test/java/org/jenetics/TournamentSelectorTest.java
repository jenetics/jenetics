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

import static java.lang.String.format;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.internal.util.Named;
import org.jenetics.internal.util.Pair;

import org.jenetics.stat.Distribution;
import org.jenetics.stat.Histogram;
import org.jenetics.stat.StatisticsAssert;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.util.Factory;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Scoped;
import org.jenetics.util.TestData;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-08-22 $</em>
 */
public class TournamentSelectorTest
	extends SelectorTester<TournamentSelector<DoubleGene, Double>>
{

	@Override
	protected Factory<TournamentSelector<DoubleGene, Double>> factory() {
		return () -> new TournamentSelector<>(3);
	}

	@Override
	protected Distribution<Double> getDistribution() {
		return new UniformDistribution<>(getDomain());
	}

	@Override
	@Test
	public void selectDistribution() {
		//throw new SkipException("TODO: implement this test.");
	}

	@Test(
		dataProvider = "expectedDistribution",
		invocationCount = 20,
		successPercentage = 95
	)
	public void selectDist(
		final Integer tournamentSize,
		final Named<double[]> expected,
		final Optimize opt
	) {
		final int npopulation = 250;
		final int loops = 500;

		final Histogram<Double> distribution = SelectorTester.distribution(
			new TournamentSelector<DoubleGene, Double>(tournamentSize),
			opt,
			npopulation,
			loops
		);

		StatisticsAssert.assertDistribution(distribution, expected.value);
	}

	@DataProvider
	public Object[][] expectedDistribution() {
		final String resource =
			"/org/jenetics/selector/distribution/TournamentSelector";

		return Arrays.stream(Optimize.values())
			.flatMap(opt -> {
				final TestData data = TestData.of(resource, opt.toString());
				final double[][] csv = data.stream()
					.map(TestData::toDouble)
					.toArray(double[][]::new);

				final int[] sizes = TestData.toInt(csv[0]);

				return IntStream.range(0, sizes.length)
					.mapToObj(i -> new Object[]{
						sizes[i],
						Named.of(
							format("distribution[%d]", sizes[i]),
							expected(csv, i)
						),
						opt
					});
			}).toArray(Object[][]::new);
	}

	private static double[] expected(final double[][] csv, final int c) {
		final double[] col = new double[csv.length - 1];
		for (int i = 0; i < col.length; ++i) {
			col[i] = Math.max(csv[i + 1][c], Double.MIN_VALUE);
		}
		return col;
	}

	public static void main(final String[] args) {
        System.out.println(Optimize.MINIMUM);
        writeDistributionData(Optimize.MINIMUM);

		System.out.println(Optimize.MAXIMUM);
		writeDistributionData(Optimize.MAXIMUM);
	}

	private static void writeDistributionData(final Optimize opt) {
		final ThreadLocal<LCG64ShiftRandom> random = new LCG64ShiftRandom.ThreadLocal();
		try (Scoped<LCG64ShiftRandom> sr = RandomRegistry.scope(random)) {
			final List<Integer> sizes = Arrays.asList(2, 3, 4, 5, 6, 7, 13, 23, 37);

            // For big testing: npopulation = 25_000, loops = 2_500_000
            // For fast testing: npopulation = 500, loops = 10_000
			final int npopulation = 25_000;
			final int loops = 2_500_000;

			final List<Pair<Integer, Histogram<Double>>> result = sizes.parallelStream()
				.map(i -> Pair.of(i, new TournamentSelector<DoubleGene, Double>(i)))
				.map(s -> Pair.of(s._1, distribution(s._2, opt, npopulation, loops)))
				.collect(Collectors.toList());

			result.sort((a, b) -> a._1 - b._1);
			final List<Histogram<Double>> histograms = result.stream()
				.map(p -> p._2)
				.collect(Collectors.toList());

			final String header = sizes.stream()
				.map(Objects::toString)
				.collect(Collectors.joining(",", "", ""));

			System.out.println(header);
			print(System.out, histograms);
		}
	}

	private static void print(
		final PrintStream writer,
		final List<Histogram<Double>> histograms
	) {
		final double[][] array = histograms.stream()
			.map(Histogram::getNormalizedHistogram)
			.toArray(double[][]::new);

		final NumberFormat format = NumberFormat.getNumberInstance(Locale.ENGLISH);
		format.setMinimumFractionDigits(15);
		format.setMaximumFractionDigits(15);

		for (int i = 0; i < array[0].length; ++i) {
			for (int j = 0; j < array.length; ++j) {
				writer.print(format.format(array[j][i]));
				if (j < array.length - 1) {
					writer.print(',');
				}
			}
			writer.println();
		}
	}

}
