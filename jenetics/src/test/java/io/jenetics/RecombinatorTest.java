package io.jenetics;

import static java.lang.String.format;

import java.util.Arrays;
import java.util.Random;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class RecombinatorTest {

	private final Random _random = new Random();

	@Test(dataProvider = "individualParams")
	public void individuals(final int size, final int order) {
		for (int i = 0; i < size; ++i) {
			final int[] ind = Recombinator.individuals(i, size, order, _random);
			checkDistinctness(ind);
		}
	}

	private static void checkDistinctness(final int[] array) {
		for (int i = 1; i < array.length; ++i) {
			if (array[i - 1] == array[i]) {
				throw new AssertionError(format(
					"Array not distinct: %s", Arrays.toString(array)
				));
			}
		}
	}

	@DataProvider
	public Object[][] individualParams() {
		return new Object[][] {
			{2, 2},
			{3, 3},
			{3, 2},
			{4, 4},
			{10, 5},
			{10, 1},
			{10, 10},
		};
	}

}
