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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.testng.annotations.Test;

import io.jenetics.ext.util.CsvSupport;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CsvWriterTest {

	@Test
	public void write() {
		final var csv = """
			Country,City,AccentCity,Region,Population,Latitude,Longitude
			ad,aixas,Aixàs,06,123123,42.4833333,1.4666667
			ad,aixirivali,Aixirivali,06,234234,42.4666667,1.5
			ad,aixirivall,Aixirivall,06,456,42.4666667
			ad,aixirvall,Aixirvall,06,678,42.4666667,1.5
			ad,aixovall,Aixovall,06,234234,42.4666667,1.4833333
			""";

		final CsvReader<CsvReaderTest.PartialEntry> reader = CsvReader.builder()
			.headers(1)
			.build(CsvReaderTest.PartialEntry.class);

		final List<CsvReaderTest.PartialEntry> records = reader.parse(csv);
		records.forEach(System.out::println);

		final CsvWriter<CsvReaderTest.PartialEntry> writer = CsvWriter.builder()
			.header("Country", "City", "AccentCity", "Region", "Population",
				"Latitude", "Longitude")
			.embedding(4, 1, 0, 6)
			.build(CsvReaderTest.PartialEntry.class);

		writer.write(records, System.out);
	}

	//@Test
	public void processing() throws IOException {
		final var sourcePath = Path.of("/home/fwilhelm/Workspace/Datasets/worldcitiespop.txt");
		final var targetPath = Path.of("/home/fwilhelm/Temp/worldcitiespop_target.txt");
		Files.deleteIfExists(targetPath);

		final CsvReader<CsvReaderTest.PartialEntry> reader = CsvReader.builder()
			.headers(1)
			.quote(CsvSupport.Quote.ZERO)
			.build(CsvReaderTest.PartialEntry.class);

		final CsvWriter<CsvReaderTest.PartialEntry> writer = CsvWriter.builder()
			.header("Country", "City", "AccentCity", "Region", "Population",
				"Latitude", "Longitude")
			.embedding(4, 1, 0, 6)
			.build(CsvReaderTest.PartialEntry.class);

		try (var in = Files.newBufferedReader(sourcePath, ISO_8859_1);
			var out = Files.newBufferedWriter(targetPath, ISO_8859_1))
		{
			writer.write(reader.read(in), out);
		}

	}

}
