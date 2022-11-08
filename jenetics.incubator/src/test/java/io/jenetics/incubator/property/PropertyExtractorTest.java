package io.jenetics.incubator.property;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.Test;


public class PropertyExtractorTest {

	private record Data(
		String string,
		Integer integer,
		int i,
		int[] ints,
		Integer[] integers,
		List<String> list,
		Set<Integer> set,
		Map<String, Integer> map
	) {}


	@Test
	public void extract() {
		final var data = new Data(
			"stringValue",
			123,
			456,
			new int[] {1, 2, 3},
			new Integer[] {4, 5, 6},
			List.of("a", "b", "c"),
			Set.of(1, 2, 3),
			Map.of("a", 1, "b", 2)
		);

		PropertyExtractor.DEFAULT
			.properties(data)
			.forEach(System.out::println);
	}

}
