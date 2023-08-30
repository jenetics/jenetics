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
package io.jenetics.incubator.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

public class CsvSupportTest {

	record Measurement(
		OffsetDateTime createdAt,
		int co2,
		float temperature,
		float humidity
	) {
		static Measurement of(final String[] values) {
			return new Measurement(
				OffsetDateTime.parse(values[0]),
				Integer.parseInt(values[1]),
				Float.parseFloat(values[2]),
				Float.parseFloat(values[3])
			);
		}
	}

	@Test
	public void readMeasurements() throws IOException {
		final var file = "/io/jenetics/incubator/util/Temperatures.csv";
		final var input = CsvSupportTest.class.getResourceAsStream(file);
		final var reader = new InputStreamReader(input);

		try (var lines = CsvSupport.read(reader)) {
			final var columns = new String[4];
			final var result = lines
				.map(line -> CsvSupport.split(line, columns))
				.map(Measurement::of)
				.toList();

			result.forEach(System.out::println);
		}
	}

	@Test
	public void readWrite() throws IOException {
		final var file = "/io/jenetics/incubator/util/Temperatures.csv";
		final var input = CsvSupportTest.class.getResourceAsStream(file);
		final var reader = new InputStreamReader(input);

		final List<List<String>> rows;
		try (var lines = CsvSupport.read(reader)) {
			rows = lines
				.map(CsvSupport::split)
				.toList();
		}

		final String csv = rows.stream()
			.map(CsvSupport::join)
			.collect(CsvSupport.toCsv());

		final String expected;
		try (var in = CsvSupportTest.class.getResourceAsStream(file)) {
			expected = new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}

		assertThat(csv).isEqualToIgnoringNewLines(expected);
	}

	@Test
	public void split() throws IOException {
		final var line = "1,2,3,4";
		final var column = new String[3];

		CsvSupport.split(line, column, 2, 3);
		System.out.println(Arrays.toString(column));
	}

}
