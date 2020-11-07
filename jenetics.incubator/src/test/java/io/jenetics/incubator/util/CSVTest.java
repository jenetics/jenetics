package io.jenetics.incubator.util;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CSVTest {

	@Test(dataProvider = "rows")
	public void split(final String row, final List<String> result) {
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
		CSV.split(row);
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
	public void join(final List<?> columns, final String row) {
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

}
