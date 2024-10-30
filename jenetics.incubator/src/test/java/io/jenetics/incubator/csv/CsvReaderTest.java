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
package io.jenetics.incubator.csv;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.testng.annotations.Test;

import io.jenetics.ext.util.CsvSupport;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CsvReaderTest {

	record FullEntry(
		String country,
		String city,
		String accentCity,
		String region,
		Double population,
		Double latitude,
		Double longitude
	) {}

	record PartialEntry(
		@ColumnIndex(4) Integer population,
		@ColumnIndex(1) String city,
		@ColumnIndex(0) String country
	) {}

	@Test
	public void parse() {
		final var csv = """
			Country,City,AccentCity,Region,Population,Latitude,Longitude
			ad,aixas,Aixàs,06,123123,42.4833333,1.4666667
			ad,aixirivali,Aixirivali,06,234234,42.4666667,1.5
			ad,aixirivall,Aixirivall,06,456,42.4666667
			ad,aixirvall,Aixirvall,06,678,42.4666667,1.5
			ad,aixovall,Aixovall,06,234234,42.4666667,1.4833333
			""";

		final CsvReader<PartialEntry> reader = CsvReader.builder()
			.headers(1)
			.build(PartialEntry.class);

		final List<PartialEntry> entries = reader.parse(csv);
		entries.forEach(System.out::println);
		assertThat(entries).hasSize(5);
		//assertThat(entries.getFirst().population).isEqualTo(123123);
	}

	@Test
	public void performance() throws IOException {
		final var path = Path.of("/home/fwilhelm/Workspace/Datasets/worldcitiespop.txt");

		for (int i = 0; i < 10; ++i) {
			try (var reader = Files.newBufferedReader(path, ISO_8859_1)) {
				final var start = System.currentTimeMillis();

				final var rdr = CsvReader.builder()
					.headers(1)
					.quote(CsvSupport.Quote.ZERO)
					//.build(FullEntry.class);
					.build();

				final var count = rdr
					.read(reader)
					.count();

				final var time = System.currentTimeMillis() - start;

				System.out.println("Count: " + count);
				System.out.println("Time: " + time);
				System.setProperty("output", Long.toString(count));
			}
		}
	}

}
