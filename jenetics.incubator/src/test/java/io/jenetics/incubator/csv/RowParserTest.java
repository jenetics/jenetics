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

import io.jenetics.ext.util.CsvSupport.ColumnIndexes;
import io.jenetics.ext.util.CsvSupport.LineReader;
import io.jenetics.ext.util.CsvSupport.LineSplitter;
import org.testng.annotations.Test;

import java.io.StringReader;
import java.util.function.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class RowParserTest {

	@Test
	public void parse() {
		final var csv = """
			Country,City,AccentCity,Region,Population,Latitude,Longitude
			ad,aixas,Aixàs,06,123123,42.4833333,1.4666667
			ad,aixirivali,Aixirivali,06,234234,42.4666667,1.5
			ad,aixirivall,Aixirivall,06,456,42.4666667,1.5
			ad,aixirvall,Aixirvall,06,678,42.4666667,1.5
			ad,aixovall,Aixovall,06,234234,42.4666667,1.4833333
			""";

		record Entry(int population, String city, String country) {
			static final Function<String, Entry> PARSER =
				RowParser.of(Entry.class)
					.compose(ColumnParser.DEFAULT_ROW_PARSER)
					.compose(new LineSplitter(new ColumnIndexes(4, 1, 0))::split);
		}

		try (var lines = new LineReader().read(new StringReader(csv))) {
			lines.skip(1)
				.map(Entry.PARSER)
				.forEach(System.out::println);
		}
	}

}
