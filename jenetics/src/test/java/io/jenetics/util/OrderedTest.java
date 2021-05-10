package io.jenetics.util;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

public class OrderedTest {

	@Test
	public void comparing() {
		final List<Ordered<Integer>> objects = IntStream.range(0, 100)
			.mapToObj(i -> Ordered.of(i, Comparator.reverseOrder()))
			.sorted(Comparator.naturalOrder())
			.collect(Collectors.toUnmodifiableList());

		for (int i = 0; i < objects.size(); ++i) {
			final int value = objects.get(i).get();
			Assert.assertEquals(value, objects.size() - i - 1);
		}
	}

}
