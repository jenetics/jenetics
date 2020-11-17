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

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.incubator.util.Lifecycle.CloseableValue;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CSVTest {

	private static final String DEFAULT_CHARACTERS =
		"abcdefghijklmnopqrstuvwxyz !\"$%&/()=?`{[]}\\+~*#';.:,-_<>|@^'\t\n\r";

	@Test(dataProvider = "csvs")
	public void lines(final String csv, final List<String> lines) throws IOException {
		final List<String> readLines;
		try (var input = new StringReader(csv)) {
			readLines = CSV.LINE_READER.readAll(input);
		}

		Assert.assertEquals(readLines, lines);

		final var rows = readLines.stream()
			.map(CSV::split)
			.collect(Collectors.toList());

		Assert.assertEquals(
			rows,
			lines.stream()
				.map(CSV::split)
				.collect(Collectors.toList())
		);
	}

	@DataProvider
	public Object[][] csvs() {
		return new Object[][] {
			{
				"",
				List.of() // 0
			},
			{
				"\r\n",
				List.of() // 1
			},
			{
				"\r\n\r\n",
				List.of() // 2
			},
			{
				" \r\n\r\n",
				List.of(" ") // 3
			},
			{
				"r1\r\nr2\r\n",
				List.of("r1", "r2") // 4
			},
			{
				"r1\n\rr2\n\rr3",
				List.of("r1", "r2", "r3") // 5
			},
			{
				"r1\n\rr2\n\r\"r3\"",
				List.of("r1", "r2", "\"r3\"")
			},
			{
				"\"r1\r\nr2\"\r\nr3",
				List.of("\"r1\r\nr2\"", "r3")
			},
			{
				"r0\r\n\"r1\r\nr2\"\r\nr3",
				List.of("r0", "\"r1\r\nr2\"", "r3")
			},
			{
				"r0\r\n\"r1\r\nr2\"\r\n",
				List.of("r0", "\"r1\r\nr2\"")
			},
			{
				"r0\r\n\"r1\"\"\r\nr2\"\r\n",
				List.of("r0", "\"r1\"\"\r\nr2\"")
			},
			{
				"r0\r\n\"r1\"\"\r\nr2\"\"\"",
				List.of("r0", "\"r1\"\"\r\nr2\"\"\"")
			},
			{
				"r1\n" +
				"r2\r\n" +
				"\"r3.1\nr3.2\"\r\n" +
				"r4",
				List.of(
					"r1",
					"r2",
					"\"r3.1\nr3.2\"",
					"r4"
				)
			}
		};
	}

	@Test(dataProvider = "rows")
	public void splitRow(final String row, final List<String> result) {
		Assert.assertEquals(CSV.split(row), result);
	}

	@DataProvider
	public Object[][] rows() {
		return new Object[][] {
			{
				"",
				List.of("")
			},
			{
				"a,b,c,d,e,f",
				List.of("a", "b", "c", "d", "e", "f")
			},
			{
				"\"\"",
				List.of("")
			},
			{
				"\"\"\"\"",
				List.of("\"")
			},
			{
				"\"\"\"a\n\"\"\"",
				List.of("\"a\n\"")
			},
			{
				"a,b\nc,d",
				List.of("a", "b\nc", "d")
			},
			{
				"a,\"b\nc\",d",
				List.of("a", "b\nc", "d")
			},
			{
				"a,\"b\nc\",\"d,\"",
				List.of("a", "b\nc", "d,")
			},
			{
				"\"\"",
				List.of("")
			},
			{
				" ",
				List.of(" ")
			},
			{
				",",
				List.of("", "")
			},
			{
				",,",
				List.of("", "", "")
			},
			{
				",,,",
				List.of("", "", "", "")
			},
			{
				",,, ",
				List.of("", "", "", " ")
			},
			{
				",,, 4 ",
				List.of("", "", "", " 4 ")
			},
			{
				", ",
				List.of("", " ")
			},
			{
				" , ",
				List.of(" ", " ")
			},
			{
				" ,   ",
				List.of(" ", "   ")
			},
			{
				" ,   ,",
				List.of(" ", "   ", "")
			},
			{
				"\",\"",
				List.of(",")
			},
			{
				"\",\",foo",
				List.of(",", "foo")
			},
			{
				",\"\"",
				List.of("", "")
			},
			{
				",\"\",\"\"\"\"\"\"\"\"",
				List.of("", "", "\"\"\"")
			},
			{
				"123,2.99,AMO024,Title,\"Description, \"\"more info\", ,123987564,",
				List.of("123", "2.99", "AMO024", "Title", "Description, \"more info", " ", "123987564", "")
			}
		};
	}

	@Test(dataProvider = "illegalRows", expectedExceptions = IllegalArgumentException.class)
	public void illegalSplit(final String row) {
		CSV.split(row);
	}

	@DataProvider
	public Object[][] illegalRows() {
		return new Object[][]{
			{"\""},
			{"\",\"\n"},
			{" \"\""},
			{"\"\" "},
			{"a,\"b\nc,d"},
			{"123,2.99,AMO024,Title, \"Description, \"\"more info\", ,123987564,"},
			{"123,2.99,AMO024,Title,\"Description, \"\"more info\" , ,123987564,"}
		};
	}

	@Test(dataProvider = "columns")
	public void joinCols(final List<?> columns, final String row) {
		Assert.assertEquals(CSV.join(columns), row);
		Assert.assertEquals(CSV.split(CSV.join(columns)), columns);
		Assert.assertEquals(columns, CSV.split(row));
	}

	@DataProvider
	public Object[][] columns() {
		return new Object[][] {
			{List.of(""), ""},
			{List.of("a"), "a"},
			{List.of("a", "b"), "a,b"},
			{List.of("a", "b,"), "a,\"b,\""},
			{List.of("a", "\"b"), "a,\"\"\"b\""},
			{List.of("", ""), ","},
			{List.of("", "", "", ""), ",,,"},
			{List.of("", "", "", ""), ",,,"},
			{List.of("", "a", "b", ""), ",a,b,"}
		};
	}

	@Test(invocationCount = 10)
	public void randomJoinSplitTest() {
		final var row = nextRow(1000, new Random());
		final var line = CSV.join(row);
		final var cols = CSV.split(line);

		Assert.assertEquals(cols, row);
	}

	@Test(invocationCount = 10)
	public void randomWriteRead() throws IOException {
		final var random = new Random();
		final List<List<?>> values = Stream.generate(() -> nextRow(25, random))
			.limit(50)
			.collect(Collectors.toList());

		final String csv = values.stream()
			.map(CSV::join)
			.collect(CSV.toCSV());

		final var path = CloseableValue.of(
			Files.createTempFile("CSVTest-", null),
			Files::deleteIfExists
		);

		try (path) {
			Files.writeString(path.get(), csv);

			try (var lines = CSV.LINE_READER.read(path.get())) {
				final var readValues = lines
					.map(CSV::split)
					.collect(Collectors.toList());

				Assert.assertEquals(readValues, values);
			}
		}
	}

	private static List<String> nextRow(final int columns, final Random random) {
		final List<String> cols = new ArrayList<>(columns);
		for (int i = 0; i < columns; ++i) {
			cols.add(nextString(random.nextInt(30) + 30, random));
		}
		return cols;
	}

	private static String nextString(final int length, final Random random) {
		final Supplier<Character> generator = () -> DEFAULT_CHARACTERS
			.charAt(random.nextInt(DEFAULT_CHARACTERS.length()));

		return Stream.generate(generator)
			.limit(length)
			.map(String::valueOf)
			.collect(Collectors.joining());
	}



}
