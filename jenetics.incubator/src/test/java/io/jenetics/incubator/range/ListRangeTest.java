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
package io.jenetics.incubator.range;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import io.jenetics.incubator.util.range.ListRange;
import io.jenetics.incubator.util.range.Range;

public class ListRangeTest {

	@Test
	void basicListView() {
		final var backing = new ArrayList<>(List.of(0, 1, 2, 3, 4, 5));
		final var range = new ListRange<>(backing, Range.INTEGER.dense(1, 5));

		assertThat(range.size()).isEqualTo(4);
		assertThat(range.isEmpty()).isFalse();
		assertThat(range.get(0)).isEqualTo(1);
		assertThat(range.get(3)).isEqualTo(4);
		assertThat(range.toList()).containsExactly(1, 2, 3, 4);

		range.set(1, 20);
		assertThat(backing).containsExactly(0, 1, 20, 3, 4, 5);
	}

	@Test
	void containsAndIndices() {
		final var range = new ListRange<>(
			new ArrayList<>(java.util.Arrays.asList("a", "b", "a", null))
		);

		assertThat(range.contains("a")).isTrue();
		assertThat(range.contains(null)).isTrue();
		assertThat(range.contains("x")).isFalse();
		assertThat(range.containsAll(java.util.Arrays.asList("a", null))).isTrue();
		assertThat(range.indexOf("a")).isEqualTo(0);
		assertThat(range.lastIndexOf("a")).isEqualTo(2);
		assertThat(range.indexOf("x")).isEqualTo(-1);
		assertThat(range.lastIndexOf("x")).isEqualTo(-1);
	}

	@Test
	void listIteratorSupportsNavigationAndSet() {
		final var backing = new ArrayList<>(List.of(0, 1, 2, 3));
		final var range = new ListRange<>(backing, Range.INTEGER.dense(1, 4));
		final var iterator = range.listIterator(1);

		assertThat(iterator.nextIndex()).isEqualTo(1);
		assertThat(iterator.previousIndex()).isEqualTo(0);
		assertThat(iterator.next()).isEqualTo(2);
		iterator.set(20);
		assertThat(iterator.previous()).isEqualTo(20);
		assertThat(iterator.previous()).isEqualTo(1);
		assertThat(iterator.hasPrevious()).isFalse();
		assertThat(backing).containsExactly(0, 1, 20, 3);

		assertThatThrownBy(iterator::remove)
			.isInstanceOf(UnsupportedOperationException.class);
		assertThatThrownBy(() -> iterator.add(4))
			.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void denseSubListUsesLogicalIndices() {
		final var backing = new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6));
		final var range = new ListRange<>(backing, Range.INTEGER.dense(2, 7));

		assertThat(range.subList(1, 4).toList()).containsExactly(3, 4, 5);
		assertThat(range.subList(0, 0).isEmpty()).isTrue();
		assertThat(range.subList(1, 4).subList(1, 2).toList())
			.containsExactly(4);
	}

	@Test
	void sparseSubListUsesLogicalIndices() {
		final var range = ListRange.of(
			List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
			value -> value%2 == 0
		);

		assertThat(range.toList()).containsExactly(0, 2, 4, 6, 8);
		assertThat(range.subList(1, 4).toList()).containsExactly(2, 4, 6);
	}

	@Test
	void intersectUsesTheBackingRange() {
		final var range = new ListRange<>(
			List.of(0, 1, 2, 3, 4, 5, 6, 7),
			Range.INTEGER.dense(2, 7)
		);

		assertThat(range.intersect(Range.INTEGER.dense(4, 6)).toList())
			.containsExactly(4, 5);
	}

	@Test
	void arraysAndEmptyRanges() {
		final var empty = new ListRange<>(
			List.of(),
			Range.INTEGER.dense(0, 0)
		);
		final var range = new ListRange<>(List.of(1, 2, 3));

		assertThat(empty.isEmpty()).isTrue();
		assertThat(empty.toArray()).isEmpty();
		assertThat(range.toArray(new Integer[0])).containsExactly(1, 2, 3);
	}

	@Test
	void rejectInvalidRangesAndSubLists() {
		assertThatThrownBy
			(() -> new ListRange<>(List.of(1), Range.INTEGER.dense(0, 2)))
			.isInstanceOf(IndexOutOfBoundsException.class);

		final var range = new ListRange<>(List.of(1, 2, 3));
		assertThatThrownBy(() -> range.subList(-1, 1))
			.isInstanceOf(IndexOutOfBoundsException.class);
		assertThatThrownBy(() -> range.subList(2, 1))
			.isInstanceOf(IndexOutOfBoundsException.class);
		assertThatThrownBy(() -> range.listIterator(4))
			.isInstanceOf(IndexOutOfBoundsException.class);
	}
}
