package io.jenetics.incubator.util;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.tools.ant.filters.StringInputStream;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.incubator.util.Lifecycle.CloseableValue;

public class CSVTest {

	@Test(dataProvider = "csvs")
	public void lines(final String csv, final List<String> lines) throws IOException {
		final List<String> readLines;
		try (var input = new StringInputStream(csv)) {
			readLines = CSV.reader().readAllLines(input);
		}

		Assert.assertEquals(readLines, lines);

		final var rows = readLines.stream()
			.map(CSV::splitLine)
			.collect(Collectors.toList());

		Assert.assertEquals(
			rows,
			lines.stream()
				.map(CSV::splitLine)
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
		Assert.assertEquals(CSV.splitLine(row), result);
	}

	@DataProvider
	public Object[][] rows() {
		return new Object[][] {
			{
				"",
				List.of("")
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
		CSV.splitLine(row);
	}

	@DataProvider
	public Object[][] illegalRows() {
		return new Object[][]{
			{"\""},
			{" \"\""},
			{"\"\" "},
			{"a,\"b\nc,d"},
			{"123,2.99,AMO024,Title, \"Description, \"\"more info\", ,123987564,"},
			{"123,2.99,AMO024,Title,\"Description, \"\"more info\" , ,123987564,"}
		};
	}

	@Test(dataProvider = "columns")
	public void joinCols(final List<?> columns, final String row) {
		Assert.assertEquals(CSV.joinCols(columns), row);
		Assert.assertEquals(CSV.splitLine(CSV.joinCols(columns)), columns);
		Assert.assertEquals(columns, CSV.splitLine(row));
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

	@Test
	public void writeRead() throws IOException {
		final var random = new Random();
		final List<List<String>> values = Stream.generate(() -> nextRow(random))
			.limit(200)
			.collect(Collectors.toList());

		final String csv = values.stream()
			.collect(CSV.rowsToCSV());

		final var path = CloseableValue.of(
			Files.createTempFile("CSVTest-", null),
			Files::deleteIfExists
		);

		try (path) {
			Files.writeString(path.get(), csv);

			try (var lines = Files.lines(path.get())) {
				final var readValues = lines
					.map(CSV::splitLine)
					.collect(Collectors.toList());

				Assert.assertEquals(readValues, values);
			}
		}
	}

	private static List<String> nextRow(final Random random) {
		return List.of(
			"" + random.nextDouble(),
			"" + random.nextBoolean(),
			"" + random.nextFloat(),
			"" + random.nextInt(),
			"" + random.nextLong()
		);
	}

}
