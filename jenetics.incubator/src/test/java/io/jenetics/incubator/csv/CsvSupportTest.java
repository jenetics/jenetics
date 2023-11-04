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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.incubator.csv.CsvSupport.ColumnIndexes;
import io.jenetics.incubator.csv.CsvSupport.ColumnJoiner;
import io.jenetics.incubator.csv.CsvSupport.LineReader;
import io.jenetics.incubator.csv.CsvSupport.LineSplitter;
import io.jenetics.incubator.csv.CsvSupport.Quote;
import io.jenetics.incubator.csv.CsvSupport.Separator;

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
	public void lineReader() throws IOException {
		final var file = Path.of("/home/fwilhelm/Data/Storage/EurotaxSave_20230918000000/10Basic20230918000000/make.txt");

		final var splitter = new LineSplitter(new Separator('\t'));
		final var joiner = new ColumnJoiner(new Separator('\t'));

		try (var lines = CsvSupport.lines(Files.newBufferedReader(file, Charset.forName("Cp1252")))) {
			//lines.map(splitter::split)
			//	.forEach(cols -> System.out.println(Arrays.toString(cols)));
			var csv = lines.map(splitter::split)
				.map(joiner::join)
				.collect(CsvSupport.toCsv());

			Files.write(Path.of(file.getParent().toString(), "make.csv"), csv.getBytes());
		}
	}

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

//	@Test
//	public void readMeasurements() throws IOException {
//		final var file = "/io/jenetics/incubator/util/Temperatures.csv";
//		final var input = CsvSupportTest.class.getResourceAsStream(file);
//		final var reader = new InputStreamReader(input);
//
//		try (var lines = CsvSupport.read(reader)) {
//			final var columns = new String[4];
//			final var result = lines
//				.map(line -> CsvSupport.split(line, columns))
//				.map(Measurement::of)
//				.toList();
//
//			result.forEach(System.out::println);
//		}
//	}
//
//	@Test
//	public void readWrite() throws IOException {
//		final var file = "/io/jenetics/incubator/util/Temperatures.csv";
//		final var input = CsvSupportTest.class.getResourceAsStream(file);
//		final var reader = new InputStreamReader(input);
//
//		final List<List<String>> rows;
//		try (var lines = CsvSupport.read(reader)) {
//			rows = lines
//				.map(CsvSupport::split)
//				.toList();
//		}
//
//		final String csv = rows.stream()
//			.map(CsvSupport::join)
//			.collect(CsvSupport.toCsv());
//
//		final String expected;
//		try (var in = CsvSupportTest.class.getResourceAsStream(file)) {
//			expected = new String(in.readAllBytes(), StandardCharsets.UTF_8);
//		}
//
//		assertThat(csv).isEqualToIgnoringNewLines(expected);
//	}
//
//	@Test
//	public void split() throws IOException {
//		final var line = "0,1,2,3,4,5,6,7,8,9";
//		final var row = new String[7];
//
//		final int[] indexes = {9, 2, 3, 5, 5, 1, 8};
//		CsvSupport.split(line, row, indexes);
//		System.out.println(Arrays.toString(row));
//		System.out.println(CsvSupport.join(row, indexes));
//	}

}
