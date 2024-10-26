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

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.ext.util.CsvSupport.ColumnIndexes;
import io.jenetics.ext.util.CsvSupport.ColumnJoiner;
import io.jenetics.ext.util.CsvSupport.LineReader;
import io.jenetics.ext.util.CsvSupport.LineSplitter;
import io.jenetics.ext.util.CsvSupport.Quote;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CsvSupportTest {

	@Test
	public void splittingEscaping() {
		final String csv = """
            one,two,three
            ,,
            '',,
            ,'',
            ,,''
            '''',,
            '''','''''''',
            '''','f''d',
            'a
            b',,
               , ,  \s
             a , b , c
             a ,' b ', c
            """;

		final String[][] expected = {
			{"one", "two", "three"},
			{"", "", ""},
			{"", "", ""},
			{"", "", ""},
			{"", "", ""},
			{"'", "", ""},
			{"'", "'''", ""},
			{"'", "f'd", ""},
			{"a\nb", "", ""},
			{"   ", " ", "   "},
			{" a ", " b ", " c"},
			{" a ", " b ", " c"}
		};

		final var reader = new LineReader(new Quote('\''));
		final var splitter = new LineSplitter(new Quote('\''));

		final String[][] result = reader.read(new StringReader(csv))
			.map(splitter::split)
			.toArray(String[][]::new);

		for (int i = 0; i < expected.length; ++i) {
			assertThat(result[i]).isEqualTo(expected[i]);
		}
	}

	@Test(dataProvider = "splitIndexes")
	public void splittingIndexes(String line, ColumnIndexes indexes, String[] expected) {
		final var splitter = new LineSplitter(indexes);
		final var columns = splitter.split(line);
		assertThat(columns).isEqualTo(expected);
	}

	@DataProvider
	public Object[][] splitIndexes() {
		return new Object[][] {
			{
				"0,1,2,3,4,5,6,7,8,9",
				ColumnIndexes.ALL,
				new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"}
			},
			{
				"0,1,2,3,4,5,6,7,8,9",
				new ColumnIndexes(0, 1, 2, 3),
				new String[] {"0", "1", "2", "3"}
			},
			{
				"0,1,2,3,4,5,6,7,8,9",
				new ColumnIndexes(4, 5, 6, 7),
				new String[] {"4", "5", "6", "7"}
			},
			{
				"0,1,2,3,4,5,6,7,8,9",
				new ColumnIndexes(7, 6, 5, 4),
				new String[] {"7", "6", "5", "4"}
			},
			{
				"0,1,2,3,4,5,6,7,8,9",
				new ColumnIndexes(7, 6, 5, 4, 5, 5),
				new String[] {"7", "6", "5", "4", "5", "5"}
			},
			{
				"0,1,2,3,4,5,6,7,8,9",
				new ColumnIndexes(9, 8, 7, 6, 5, 4, 3, 2, 1, 0),
				new String[] {"9", "8", "7", "6", "5", "4", "3", "2", "1", "0"}
			},
			{
				"0,1,2,3,4,5,6,7,8,9",
				new ColumnIndexes(0, 1, 2, 2, 4, 5, 5),
				new String[] {"0", "1", "2", "2", "4", "5", "5"}
			},
			{
				"0,1,2,3,4,5,6,7,8,9",
				new ColumnIndexes(6, 5, 2, 3, 1, 1, 0, 0),
				new String[] {"6", "5", "2", "3", "1", "1", "0", "0"}
			},
			{
				"0,1,2,3,4,5,6,7,8,9",
				new ColumnIndexes(0, 1, 2, -1, 4, -2),
				new String[] {"0", "1", "2", null, "4", null}
			},
			{
				"0,1,2,3,4,5,6,7,8,9",
				new ColumnIndexes(0, 1, 2, 2, 4, 10, 25),
				new String[] {"0", "1", "2", "2", "4", null, null}
			},
			{
				"0,1,2,3,4,5,6,7,8,9",
				new ColumnIndexes(10, 1, 2, 2, 4, 10, 25),
				new String[] {null, "1", "2", "2", "4", null, null}
			},
			{
				"0,1,2,3,4,5,6,7,8,9",
				new ColumnIndexes(10, 11, 1, 2, 33, 2, 4, 10, 25),
				new String[] {null, null, "1", "2", null, "2", "4", null, null}
			},
			{
				"0,1,2,3,4,5,6,7,8,9",
				new ColumnIndexes(10, 11, 1, 2, 33, 18, 10, 2, 4, 10, 25),
				new String[] {null, null, "1", "2", null, null, null, "2", "4", null, null}
			},
			{
				"0,1,2,3,4,5,6,7,8,9",
				new ColumnIndexes(10, 11, 1, 2, 33, 18, 10, 2, 4, 10, 25, 9, 8, 8),
				new String[] {null, null, "1", "2", null, null, null, "2", "4", null, null, "9", "8", "8"}
			},
			{
				"0,1,2,3,4,5,6,7,8,9",
				new ColumnIndexes(-10, -11, -33, -18, -10, -4),
				new String[] {null, null, null, null, null, null}
			},
			{
				"0,1,2,3,4,5,6,7,8,9",
				new ColumnIndexes(-10, -11, -33, -18, -10, -4, -1),
				new String[] {null, null, null, null, null, null, null}
			},
			{
				"0,1,2",
				new ColumnIndexes(0, 1, 2, 3, 4, 5),
				new String[] {"0", "1", "2", null, null, null}
			},
			{
				"0,1,2",
				new ColumnIndexes(0, 1, 2, 5),
				new String[] {"0", "1", "2", null}
			},
			{
				"0,1,2",
				new ColumnIndexes(0, 1, 2, 3, 3, 3),
				new String[] {"0", "1", "2", null, null, null}
			},
			{
				"0,1,2",
				new ColumnIndexes(0, 1, 2, -1, -1, -1),
				new String[] {"0", "1", "2", null, null, null}
			}
		};
	}

	@Test(dataProvider = "joinEscape")
	public void joiningEscaping(String[] columns, String expected) {
		final var joiner = new ColumnJoiner(new Quote('\''));
		assertThat(joiner.join(columns)).isEqualTo(expected);
	}

	@DataProvider
	public Object[][] joinEscape() {
		return new Object[][] {
			{
				new String[] {"'", ",", "", ""},
				"'''',',',,"
			},
			{
				new String[] {"'", ",", "", "     "},
				"'''',',',,     "
			},
			{
				new String[] {"'", ",", "\n\r", ""},
				"'''',',','\n\r',"
			}
		};
	}

	@Test(dataProvider = "joinIndexes")
	public void joiningIndexes(String[] columns, ColumnIndexes indexes, String expected) {
		final var joiner = new ColumnJoiner(new Quote('\''), indexes);
		assertThat(joiner.join(columns)).isEqualTo(expected);
	}

	@DataProvider
	public Object[][] joinIndexes() {
		return new Object[][] {
			{
				new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"},
				ColumnIndexes.ALL,
				"0,1,2,3,4,5,6,7,8,9"
			},
			{
				new String[] {"0", "1", "2", "3", null, "5", "6", "7", "8", "9"},
				ColumnIndexes.ALL,
				"0,1,2,3,,5,6,7,8,9"
			},
			{
				new String[] {"0", "1", "2", "3", "", "5", "6", "7", "8", "9"},
				ColumnIndexes.ALL,
				"0,1,2,3,,5,6,7,8,9"
			},
			{
				new String[] {"0", "1", "2", "3", " ", "5", "6", "7", "8", "9"},
				ColumnIndexes.ALL,
				"0,1,2,3, ,5,6,7,8,9"
			},
			{
				new String[] {"0", "1", "2", "3", "4", "5"},
				new ColumnIndexes(0, 1, 2, 3),
				"0,1,2,3"
			},
			{
				new String[] {"0", "1", "2", "3", "4", "5"},
				new ColumnIndexes(2, 3, 4),
				",,0,1,2"
			},
			{
				new String[] {"0", "1", "2", "3", "4", "5"},
				new ColumnIndexes(0, 1, 2, 3, -1, 4),
				"0,1,2,3,5"
			},
			{
				new String[] {"0", "1", "2", "3", "4", "5"},
				new ColumnIndexes(6, 1, 2, 3, -1, 4),
				",1,2,3,5,,0"
			},
			{
				new String[] {"0", "1", "2", "3", "4", "5"},
				new ColumnIndexes(-12, -13, -14),
				""
			},
			{
				new String[] {"0", "1", "2", "3", "4", "5"},
				new ColumnIndexes(-12, -13, -14, -3, -3, -3, 3),
				",,,"
			}
		};
	}

	@Test
	public void toComponents() {
		record Foo(String string, double value, int index) {}

		final var data = List.of(
			new Foo("first", 2,3),
			new Foo("second", 3,4),
			new Foo("third", 4,35),
			new Foo("fourth", 5,33)
		);

		final var csv = data.stream()
			.map(CsvSupport::toComponents)
			.map(CsvSupport::join)
			.collect(CsvSupport.toCsv("\n"));

		assertThat(csv).isEqualTo("""
			first,2.0,3
			second,3.0,4
			third,4.0,35
			fourth,5.0,33
			""");
	}

	@Test
	public void correctness() {
		final var csv = """
			Year,Make,Model,Description,Price
			1997,Ford,E350,"ac, abs, moon",3000.00
			1999,Chevy,"Venture ""Extended Edition""\","",4900.00
			1996,Jeep,Grand Cherokee,"MUST SELL!
			air, moon roof, loaded",4799.00
			1999,Chevy,"Venture ""Extended Edition, Very Large""\",,5000.00
			,,"Venture ""Extended Edition""\","",4900.00
			""";

		final List<String[]> parsed = CsvSupport.parse(csv);
		assertThat(parsed).hasSize(6);
		parsed.forEach(row -> assertThat(row).hasSize(5));

		final String merged = parsed.stream()
			.map(CsvSupport::join)
			.collect(CsvSupport.toCsv());
		final String expected = csv.replace(",\"\",", ",,");

		assertThat(merged).isEqualToNormalizingNewlines(expected);
	}

	@Test
	public void performance() throws IOException {
		final var path = Path.of("/home/fwilhelm/Workspace/Datasets/worldcitiespop.txt");

		for (int i = 0; i < 10; ++i) {
			try (var reader = Files.newBufferedReader(path, StandardCharsets.ISO_8859_1)) {
				final var start = System.currentTimeMillis();
				final var splitter = new LineSplitter(Quote.ZERO);

				final var count = CsvSupport.lines(reader)
					.map(splitter::split)
					.count();

				final var time = System.currentTimeMillis() - start;
				System.out.println("Count: " + count);
				System.out.println("Time: " + time); // Time: 1350
				System.setProperty("output", Long.toString(count));
			}
		}
	}

}
