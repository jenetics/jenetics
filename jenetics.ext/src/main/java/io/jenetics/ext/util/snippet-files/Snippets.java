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

import static java.lang.Double.parseDouble;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.ext.util.CsvSupport;
import io.jenetics.ext.util.CsvSupport.ColumnIndexes;
import io.jenetics.ext.util.CsvSupport.ColumnJoiner;
import io.jenetics.ext.util.CsvSupport.LineReader;
import io.jenetics.ext.util.CsvSupport.LineSplitter;
import io.jenetics.ext.util.CsvSupport.Separator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
final class Snippets {

	static class CsvSupportSnippets {
		void read() throws IOException {
			// @start region="readRows"
			final List<String[]> rows;
			try (Stream<String> lines = CsvSupport.lines(new FileReader("data.csv"))) {
				rows = lines
					.map(CsvSupport::split)
					.toList();
			}
			// @end
		}

		void parseCsv() {
			// @start region="parseCsv"
			final List<String[]> rows = CsvSupport.parse("""
				# Country,City,AccentCity,Region,Population,Latitude,Longitude
				ad,aixas,Aixàs,06,,42.4833333,1.4666667
				ad,aixirivali,Aixirivali,06,,42.4666667,1.5
				"""
			);
			// @end
		}

		void collect() {
			// @start region="CsvSupportSnippets.collect"
			final String[][] data = null; // @replace substring='null' replacement="..."
			final String csv = Arrays.stream(data)
				.map(CsvSupport::join)
				.collect(CsvSupport.toCsv());
			// @end
		}

		void projection() {
			// @start region="readEntries"
			// The data structure.
			record City(String name, String country, double lat, double lon) {
				City(final String[] row) {
					this(row[0], row[1], parseDouble(row[2]), parseDouble(row[3]));
				}
				Object[] toComponents() {
					return new Object[] {name, country, lat, lon};
				}
			}

			// The CSV data.
			final var csv = """
				# Country,City,AccentCity,Region,Population,Latitude,Longitude
				ad,aixas,Aixàs,06,,42.4833333,1.4666667
				ad,aixirivali,Aixirivali,06,,42.4666667,1.5
				ad,aixirivall,Aixirivall,06,,42.4666667,1.5
				ad,aixirvall,Aixirvall,06,,42.4666667,1.5
				ad,aixovall,Aixovall,06,,42.4666667,1.4833333
				""";

			// The splitter + the projected columns.
			final var projection = new CsvSupport.ColumnIndexes(2, 0, 5, 6);
			final var splitter = new CsvSupport.LineSplitter(projection);

			// Read and convert the data.
			final List<City> entries;
			try (var lines = CsvSupport.lines(new StringReader(csv))) {
				entries = lines
					.filter(line -> !line.startsWith("#"))
					.map(splitter::split)
					.map(City::new)
					.peek(System.out::println)
					.toList();
			}
			// @end

			// @start region="writeEntries"
			// The joiner + the embedding column indexes
			final var embedding = new CsvSupport.ColumnIndexes(2, 0, 5, 6);
			final var joiner = new CsvSupport.ColumnJoiner(embedding);

			// Create CSV string from records.
			final String csv2 = entries.stream()
				.map(City::toComponents)
				.map(joiner::join)
				.collect(CsvSupport.toCsv());

			System.out.println(csv2);
			// @end
		}
	}

	static class LineSplitterSnippets {

		void simpleSplit() {
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

			// @start region="LineSplitterSnippets.simpleSplit"
			final var reader = new LineReader();
			final var splitter = new LineSplitter(new Separator(',')); // @highlight
			final List<String[]> rows;
			try (Stream<String> lines = reader.read(new StringReader(csv))) {
				rows = lines
					.map(splitter::split) // @highlight
					.toList();
			}
			// @end
		}

		void projectingSplit() {
			// @start region="LineSplitterSnippets.projectingSplit"
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
			final var projection = new ColumnIndexes(
				// Read 'Region' as first column.
				3,
				// Read 'City' as second column.
				1,
				// Read 'Country' as third column.
				0
			);

			// Configure the splitter with default separator and quote character,
			// and make it return only the specified columns in the defined order.
			final var splitter = new LineSplitter(projection); // @highlight

			try (Stream<String> lines = reader.read(new StringReader(csv))) {
				final var result = lines
					.map(splitter::split) // @highlight
					.map(Arrays::toString)
					.collect(Collectors.joining("\n", "", "\n"));

				assert result.equals("""
					[Region, City, Country]
					[06, aixas, ad]
					[06, aixirivali, ad]
					[06, aixirivall, ad]
					[06, aixirvall, ad]
					[06, aixovall, ad]
					"""
				);
			}
			// @end
		}

	}

	static class ColumnJoinerSnippets {

		void simpleJoin() {
			final var data = new String[][] {
				{"Region", "City", "Country"},
				{"06", "aixas", "ad"},
				{"06", "aixirivali", "ad"},
				{"06", "aixirivall", "ad"},
				{"06", "aixirvall", "ad"},
				{"06", "aixovall", "ad"}
			};

			// @start region="ColumnJoinerSnippets.simpleJoin"
			final var joiner = ColumnJoiner.DEFAULT;
			final var csv = Arrays.stream(data)
				.map(joiner::join)
				.collect(CsvSupport.toCsv());
			// @end
		}

		void embedToCsv() {
			// @start region="ColumnJoinerSnippets.embedToCsv"
			final var data = new String[][] {
				{"Region", "City", "Country"},
				{"06", "aixas", "ad"},
				{"06", "aixirivali", "ad"},
				{"06", "aixirivall", "ad"},
				{"06", "aixirvall", "ad"},
				{"06", "aixovall", "ad"}
			};

			final var embedding = new ColumnIndexes(
				// Writes 'Region' as fourth column.
				3,
				// Write 'City' as second column.
				1,
				// Write 'Country' as third column.
				0,

				// Since the data rows have only three elements, the
				// missing column data are set to an empty string.
				// The last written column index will be 6, which
				// results to 7 written columns
				6
			);

			final var joiner = new ColumnJoiner(embedding);

			final var csv = Arrays.stream(data)
				.map(joiner::join)
				.collect(CsvSupport.toCsv("\n"));

			assert csv.equals("""
				Country,City,,Region,,,
				ad,aixas,,06,,,
				ad,aixirivali,,06,,,
				ad,aixirivall,,06,,,
				ad,aixirvall,,06,,,
				ad,aixovall,,06,,,
				"""
			);
			// @end
		}

	}

}
