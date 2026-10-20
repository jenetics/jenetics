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
package io.jenetics.incubator.util.range;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.incubator.util.range.DenseRange;
import io.jenetics.incubator.util.range.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class RangeTest {

	private static final List<Sample> SAMPLES = List.of(
		new Sample("empty continuous", Range.LOCAL_DATE.empty()),
		new Sample("continuous", dense(2, 8)),
		new Sample("overlapping continuous", dense(6, 12)),
		new Sample("contained continuous", dense(4, 6)),
		new Sample("disjoint continuous", dense(14, 17)),
		new Sample("empty composite", Range.empty()),
		new Sample("gapped composite", sparse(1, 4, 7, 10)),
		new Sample("overlapping composite", sparse(3, 6, 9, 15)),
		new Sample("normalized composite", sparse(2, 5, 4, 8))
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

		assertDates(left.range.minus(right.range), expected);
	}

	@Test
	void continuousCompositeIntersectionOverload() {
		final var continuous = dense(2, 12);
		final var composite = sparse(1, 4, 7, 15);
		final var expected = dates(continuous);
		expected.retainAll(dates(composite));

		assertDates(continuous.intersect(composite), expected);
	}

	@Test
	void differenceAcrossMultipleRanges() {
		final var minuend = sparse(1, 6, 8, 13, 15, 20, 22, 28);
		final var subtrahend = sparse(1, 2, 4, 9, 11, 16, 18, 24, 26, 30);

		assertThat((Object)minuend.minus(subtrahend)).isEqualTo(
			sparse(2, 4, 9, 11, 16, 18, 24, 26)
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
		final Range<LocalDate> actual,
		final TreeSet<LocalDate> expected
	) {
		assertThat(actual.stream().toList()).containsExactlyElementsOf(expected);
		assertThat(actual.size()).isEqualTo(expected.size());
	}

	private static TreeSet<LocalDate> dates(final Range<LocalDate> range) {
		return new TreeSet<>(range.stream().toList());
	}

	private static DenseRange<LocalDate> dense(final int start, final int end) {
		return Range.LOCAL_DATE.dense(date(start), date(end));
	}

	private static Range<LocalDate> sparse(final int... bounds) {
		final var ranges = new ArrayList<Range<LocalDate>>();
		for (int i = 0; i < bounds.length; i += 2) {
			ranges.add(dense(bounds[i], bounds[i + 1]));
		}
		return Range.LOCAL_DATE.of(ranges);
	}

	private static LocalDate date(final int day) {
		return LocalDate.of(2020, 1, day);
	}

	private record Sample(String name, Range<LocalDate> range) {
		@Override
		public String toString() {
			return name;
		}
	}

}
