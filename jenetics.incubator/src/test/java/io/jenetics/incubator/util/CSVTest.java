package io.jenetics.incubator.util;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CSVTest {

	@Test(dataProvider = "rows")
	public void split(final String row, final List<String> result) {
		CSV.split(row).forEach(System.out::println);

		Assert.assertEquals(CSV.split(row), result);
	}

	@DataProvider
	public Object[][] rows() {
		return new Object[][] {
			{
				"123,2.99,AMO024,Title,\"Description, \"\"more info\",,123987564",
				List.of("123", "2.99", "AMO024", "Title", "Description, more info", "", "123987564")
			}
		};
	}

}
