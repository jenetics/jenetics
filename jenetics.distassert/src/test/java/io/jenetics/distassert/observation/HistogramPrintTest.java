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
package io.jenetics.distassert.observation;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.testng.annotations.Test;

import io.jenetics.distassert.observation.Histogram.Buckets;
import io.jenetics.distassert.observation.Histogram.Partition;

public class HistogramPrintTest {

	@Test
	void print() {
		final var histogram = new Histogram(
			new Buckets(
				new Partition(new Interval(0, 4), 1, 2, 3),
				1, 2, 3, 4
			)
		);

		assertThat(print(histogram, 24, 8)).containsExactly(
			"  4 ┐         ██  ",
			"  3 ┤      ██ ██  ",
			"  2 ┤   ██ ██ ██  ",
			"  1 ┤██ ██ ██ ██  ",
			"    └──┬──┬──┬──╴ ",
			"   0.0         4.0"
		);
	}

	@Test
	void placeBarsBetweenBucketTicks() {
		final var histogram = new Histogram(
			new Buckets(
				new Partition(new Interval(0, 4), 1, 2, 3),
				1, 2, 3, 4
			)
		);
		final var output = print(histogram, 24, 8);
		final String bars = output.get(3);
		final String axis = output.get(4);
		final int axisX = axis.indexOf('└');

		for (int i = 0; i < 4; ++i) {
			final int left = axisX + i*3;
			assertThat(bars.substring(left + 1, left + 3)).isEqualTo("██");
			assertThat(axis.charAt(left)).isEqualTo(i == 0 ? '└' : '┬');
		}
		assertThat(axis.charAt(axisX + 4*3)).isEqualTo('╴');
	}

	@Test
	void centerEndpointLabelsOnBoundaryTicks() {
		final var histogram = new Histogram(
			new Buckets(
				new Partition(new Interval(0, 4), 1, 2, 3),
				1, 2, 3, 4
			)
		);
		final var output = print(histogram, 24, 8);
		final String axis = output.get(4);
		final String labels = output.get(5);
		final int origin = axis.indexOf('└');
		final int end = axis.indexOf('╴');

		assertThat(labels.indexOf("0.0") + 1).isEqualTo(origin);
		assertThat(labels.indexOf("4.0") + 1).isEqualTo(end);
	}

	@Test
	void usePleasantAxisLabels() {
		assertThat(print(histogram(1020), 24, 18))
			.anyMatch(line -> line.contains("1100 ┐"))
			.anyMatch(line -> line.contains("1000 ┤"))
			.noneMatch(line -> line.contains("1020 ┤"));
		assertThat(print(histogram(13), 24, 18))
			.anyMatch(line -> line.contains("15 ┐"))
			.noneMatch(line -> line.contains("13 ┤"));
		assertThat(print(histogram(7891), 24, 18))
			.anyMatch(line -> line.contains("8000 ┐"))
			.noneMatch(line -> line.contains("7891 ┤"));
	}

	@Test
	void spaceYAxisLabelsEvenly() {
		final var output = print(histogram(13), 24, 18);

		assertThat(output.get(0)).contains("15 ┐");
		assertThat(output.get(5)).contains("10 ┤");
		assertThat(output.get(10)).contains("5 ┤");
	}

	@Test
	void drawFractionalBarTopsWithPartialBlocks() {
		final var histogram = new Histogram(
			new Buckets(
				new Partition(new Interval(0, 4), 1, 2, 3),
				1, 4, 8, 11
			)
		);

		final String output = String.join("\n", print(histogram, 24, 8));
		assertThat(output).contains("▂").contains("▅").contains("▆");
	}

	@Test
	void printZeroFrequencyHistogram() {
		final var histogram = new Histogram(
			new Buckets(Partition.of(0, 2, 2), 0, 0)
		);

		final var output = print(histogram, 20, 6);

		assertThat(output).hasSize(3);
		assertThat(output).noneMatch(line -> line.indexOf('█') >= 0);
		assertThat(output.getFirst()).contains("0 ┐");
	}

	@Test
	void printWithDefaultDimensions() {
		final var histogram = new Histogram(
			new Buckets(Partition.of(0, 3, 3), 1, 2, 1)
		);

		assertThat(print(histogram))
			.hasSize(4)
			.allMatch(line -> line.length() <= 70);
	}

	@Test
	void rejectDimensionsWhichAreTooSmall() {
		final var histogram = new Histogram(
			new Buckets(Partition.of(0, 4, 4), 1, 2, 3, 4)
		);

		assertThatIllegalArgumentException()
			.isThrownBy(() -> print(histogram, 9, 8));
		assertThatIllegalArgumentException()
			.isThrownBy(() -> print(histogram, 24, 3));
	}

	private static List<String> print(final Histogram histogram) {
		final var bytes = new ByteArrayOutputStream();
		try (var out = new PrintStream(bytes, false, UTF_8)) {
			histogram.print(out);
		}
		return bytes.toString(UTF_8).lines().toList();
	}

	private static Histogram histogram(final long frequency) {
		return new Histogram(
			new Buckets(Partition.of(0, 1, 1), frequency)
		);
	}

	private static List<String> print(
		final Histogram histogram,
		final int width,
		final int height
	) {
		final var bytes = new ByteArrayOutputStream();
		try (var out = new PrintStream(bytes, false, UTF_8)) {
			histogram.print(out, width, height);
		}
		return bytes.toString(UTF_8).lines().toList();
	}
}
