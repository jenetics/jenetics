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
package io.jenetics;

import static io.jenetics.distassert.assertion.Assertions.assertThat;

import java.util.stream.DoubleStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.distassert.observation.Histogram;
import io.jenetics.distassert.observation.Observer;
import io.jenetics.distassert.observation.Sample;
import io.jenetics.distassert.observation.Sampler;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.StableRandomExecutor;

import io.jenetics.ext.util.CsvSupport;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class GaussianMutatorShapeTest {

	@Test(dataProvider = "parameters")
	public void next(final double shift, final double sigma) {
		final var shape = new GaussianMutator.Shape(shift, sigma);
		final var range = new DoubleRange(0.0, 10.0);
		final var stddev = shape.stddev(range);
		final var mean = shape.mean(range);

		final var observation = Observer
			.using(new StableRandomExecutor(123))
			.observe(
				Sample.repeat(
					100_000,
					sample -> sample.accept(
						shape.sample(RandomRegistry.random(), range)
					)
				),
				Histogram.Partition.of(range.min(), range.max(), 21)
			);

		assertThat(observation)
			.withinRange(range.min(), range.max())
			.isNormal(mean, stddev);
	}

	@DataProvider
	public static Object[][] parameters() {
		return new Object[][] {
			{0.0, 1.0},
			{0.0, 1.5},
			{0.0, 2.0},
			{0.0, 2.5},
			{0.0, 3.0},

			{0.5, 1.0},
			{0.5, 1.5},
			{0.5, 2.0},
			{0.5, 2.5},
			{0.5, 3.0},

			{1.0, 1.0},
			{1.0, 1.5},
			{1.0, 2.0},
			{1.0, 2.5},
			{1.0, 3.0},

			{1.5, 1.0},
			{1.5, 1.5},
			{1.5, 2.0},
			{1.5, 2.5},
			{1.5, 3.0},
		};
	}

	public static void main(String[] args) {
		//sigmas();
		shift();
	}

	private static void sigmas() {
		final var sigmas = new double[] {0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0};

		var data = DoubleStream.of(sigmas)
			.mapToObj(sigma -> frequencies(0, sigma))
			.toArray(double[][]::new);

		data = transpose(data);

		var joiner = new CsvSupport.ColumnJoiner(new CsvSupport.Separator(' '));

		var csv = Stream.concat(Stream.of(sigmas), Stream.of(data))
			.map(line -> DoubleStream.of(line)
				.mapToObj("%.5f"::formatted)
				.toArray(String[]::new))
			.map(joiner::join)
			.collect(CsvSupport.toCsv());

		System.out.println(csv);
	}

	private static void shift() {
		final var shifts = new double[] {
			0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0
		};

		var data = DoubleStream.of(shifts)
			.map(i -> i*1)
			.mapToObj(shift -> frequencies(shift, 2))
			.toArray(double[][]::new);

		data = transpose(data);

		var joiner = new CsvSupport.ColumnJoiner(new CsvSupport.Separator(' '));

		var csv = Stream.concat(Stream.of(shifts), Stream.of(data))
			.map(line -> DoubleStream.of(line)
				.mapToObj("%.5f"::formatted)
				.toArray(String[]::new))
			.map(joiner::join)
			.collect(CsvSupport.toCsv());

		System.out.println(csv);
	}

	private static double[] frequencies(final double shift, final double sigma) {
		final var shape = new GaussianMutator.Shape(shift, sigma);
		final var random = RandomRegistry.random();
		final var range = new DoubleRange(0.0, 20.0);

		final var observation = Sampler
			.observe(
				Sample.repeat(
					1_000_000,
					sample -> {
						final var s = shape.sample(random, range);
						if (!Double.isNaN(s)) {
							sample.accept(s);
						}
					}
				),
				Histogram.Partition.of(range.min(), range.max(), 21)
			);
		System.out.println("SAMPLES: " + observation.histogram().samples()
		+ ", MEAN: " + shape.mean(range)
		);

		return LongStream.of(observation.histogram().buckets().frequencies())
			.mapToDouble(v -> (double)v/(double)observation.histogram().samples())
			.toArray();
	}

	private static double[][] transpose(final double[][] matrix) {
		final var result = new double[matrix[0].length][matrix.length];

		for (int i = 0; i < matrix[0].length; ++i) {
			for (int j = 0; j < matrix.length; ++j) {
				result[i][j] = matrix[j][i];
			}
		}

		return result;
	}

}
