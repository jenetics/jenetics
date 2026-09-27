package io.jenetics.incubator.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DateRangeSetOperationsTest {

	private static final List<Sample> SAMPLES = List.of(
		new Sample("empty continuous", DateRange.Continuous.EMPTY),
		new Sample("continuous", range(2, 8)),
		new Sample("overlapping continuous", range(6, 12)),
		new Sample("contained continuous", range(4, 6)),
		new Sample("disjoint continuous", range(14, 17)),
		new Sample("empty composite", DateRange.Composite.EMPTY),
		new Sample("gapped composite", composite(1, 4, 7, 10)),
		new Sample("overlapping composite", composite(3, 6, 9, 15)),
		new Sample("normalized composite", composite(2, 5, 4, 8))
	);

	@Test(dataProvider = "rangePairs")
	void contains(final Sample left, final Sample right) {
		assertThat(left.range.contains(right.range))
			.isEqualTo(dates(left.range).containsAll(dates(right.range)));
	}

	@Test(dataProvider = "rangePairs")
	void intersect(final Sample left, final Sample right) {
		final var expected = dates(left.range);
		expected.retainAll(dates(right.range));

		assertDates(left.range.intersect(right.range), expected);
	}

	@Test(dataProvider = "rangePairs")
	void union(final Sample left, final Sample right) {
		final var expected = dates(left.range);
		expected.addAll(dates(right.range));

		assertDates(left.range.union(right.range), expected);
	}

	@Test(dataProvider = "rangePairs")
	void difference(final Sample left, final Sample right) {
		final var expected = dates(left.range);
		expected.removeAll(dates(right.range));

		assertDates(left.range.difference(right.range), expected);
	}

	@Test
	void continuousCompositeIntersectionOverload() {
		final var continuous = range(2, 12);
		final var composite = composite(1, 4, 7, 15);
		final var expected = dates(continuous);
		expected.retainAll(dates(composite));

		assertDates(continuous.intersect(composite), expected);
	}

	@Test
	void differenceAcrossMultipleRanges() {
		final var minuend = composite(1, 6, 8, 13, 15, 20, 22, 28);
		final var subtrahend = composite(1, 2, 4, 9, 11, 16, 18, 24, 26, 30);

		assertThat((Object)minuend.difference(subtrahend)).isEqualTo(
			composite(2, 4, 9, 11, 16, 18, 24, 26)
		);
	}

	@DataProvider
	static Object[][] rangePairs() {
		return SAMPLES.stream()
			.flatMap(left -> SAMPLES.stream()
				.map(right -> new Object[] {left, right}))
			.toArray(Object[][]::new);
	}

	private static void assertDates(
		final DateRange actual,
		final TreeSet<LocalDate> expected
	) {
		assertThat(actual.stream().toList()).containsExactlyElementsOf(expected);
		assertThat(actual.days()).isEqualTo(expected.size());
	}

	private static TreeSet<LocalDate> dates(final DateRange range) {
		return new TreeSet<>(range.stream().toList());
	}

	private static DateRange.Continuous range(final int start, final int end) {
		return DateRange.range(date(start), date(end));
	}

	private static DateRange.Composite composite(final int... bounds) {
		final var ranges = new ArrayList<DateRange.Continuous>();
		for (int i = 0; i < bounds.length; i += 2) {
			ranges.add(range(bounds[i], bounds[i + 1]));
		}
		return new DateRange.Composite(ranges);
	}

	private static LocalDate date(final int day) {
		return LocalDate.of(2020, 1, day);
	}

	private record Sample(String name, DateRange range) {
		@Override
		public String toString() {
			return name;
		}
	}

}
