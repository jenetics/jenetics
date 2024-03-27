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
package io.jenetics.ext.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testng.annotations.Test;

import io.jenetics.ext.util.CsvSupport.LineReader;
import io.jenetics.ext.util.CsvSupport.LineSplitter;
import io.jenetics.ext.util.CsvSupport.Separator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CsvSupportLineSplitterTest {

	@Test
	public void split() {
		final var csv = """
			0.0,0.0000
			0.1,0.0740
			0.2,0.1120
			0.3,0.1380
			0.4,0.1760
			0.5,0.2500
			0.6,0.3840
			0.7,0.6020
			0.8,0.9280
			0.9,1.3860
			1.0,2.0000
			""";

		final var reader = new LineReader();
		final var splitter = new LineSplitter(new Separator(','));
		try (Stream<String> lines = reader.read(new StringReader(csv))) {
			final var sum = lines
				.map(splitter::split)
				.map(cols -> new double[] {
						Double.parseDouble(cols[0]),
						Double.parseDouble(cols[1])
					}
				)
				.mapToDouble(cols -> cols[0] + cols[1])
				.sum();

			assertThat(sum).isEqualTo(11.55);
		}
	}

	@Test
	public void filterAndReorderColumns() {
		final var csv = """
			Country,City,AccentCity,Region,Population,Latitude,Longitude
			ad,aixas,Aixàs,06,,42.4833333,1.4666667
			ad,aixirivali,Aixirivali,06,,42.4666667,1.5
			ad,aixirivall,Aixirivall,06,,42.4666667,1.5
			ad,aixirvall,Aixirvall,06,,42.4666667,1.5
			ad,aixovall,Aixovall,06,,42.4666667,1.4833333
			""";

		final var reader = new LineReader();

		// Only read three columns, in the specified order.
		final var columns = new CsvSupport.ColumnIndexes(
			// Read 'Region' as first column.
			3,
			// Read 'City' as second column.
			1,
			// Read 'Country' as third column.
			0
		);

		// Configure the splitter with default separator and quote character,
		// and make it return only the specified columns in the defined order.
		final var splitter = new LineSplitter(columns);

		try (Stream<String> lines = reader.read(new StringReader(csv))) {
			final var result = lines
				.map(splitter::split)
				.map(Arrays::toString)
				.collect(Collectors.joining("\n", "", "\n"));

			assertThat(result).isEqualTo("""
				[Region, City, Country]
				[06, aixas, ad]
				[06, aixirivali, ad]
				[06, aixirivall, ad]
				[06, aixirvall, ad]
				[06, aixovall, ad]
				""");
		}
	}

}
