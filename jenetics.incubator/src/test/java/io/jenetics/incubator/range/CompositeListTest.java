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
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import io.jenetics.incubator.util.range.CompositeList;

public class CompositeListTest {

	@Test
	void flattensListsAndSkipsEmptyComponents() {
		final var composite = new CompositeList<>(List.of(
			List.of(1, 2),
			List.of(),
			List.of(3, 4, 5)
		));

		assertThat(composite.size()).isEqualTo(5);
		assertThat(composite.isEmpty()).isFalse();
		assertThat(composite.stream().toList()).containsExactly(1, 2, 3, 4, 5);
		assertThat(composite.get(0)).isEqualTo(1);
		assertThat(composite.get(2)).isEqualTo(3);
		assertThat(composite.get(4)).isEqualTo(5);
	}

	@Test
	void containsAndIndicesCrossComponentBoundaries() {
		final var composite = new CompositeList<>(List.of(
			List.of("a", "b"),
			List.of("c", "b", "d")
		));

		assertThat(composite.contains("c")).isTrue();
		assertThat(composite.containsAll(Arrays.asList("a", "d"))).isTrue();
		assertThat(composite.indexOf("b")).isEqualTo(1);
		assertThat(composite.lastIndexOf("b")).isEqualTo(3);
		assertThat(composite.indexOf("x")).isEqualTo(-1);
	}

	@Test
	void setAndIteratorSetUpdateOwningLists() {
		final var first = new ArrayList<>(List.of(1, 2));
		final var second = new ArrayList<>(List.of(3, 4));
		final var composite = new CompositeList<>(List.of(first, second));

		assertThat(composite.set(2, 30)).isEqualTo(3);
		final var iterator = composite.listIterator(3);
		assertThat(iterator.previous()).isEqualTo(30);
		iterator.set(300);

		assertThat(first).containsExactly(1, 2);
		assertThat(second).containsExactly(300, 4);
	}

	@Test
	void iteratorsAndArraysFollowListSemantics() {
		final var composite = new CompositeList<>(List.of(
			List.of(1, 2),
			List.of(3)
		));
		final var iterator = composite.listIterator(1);

		assertThat(iterator.previousIndex()).isEqualTo(0);
		assertThat(iterator.next()).isEqualTo(2);
		assertThat(iterator.next()).isEqualTo(3);
		assertThat(iterator.hasNext()).isFalse();
		assertThat(composite.toArray()).containsExactly(1, 2, 3);
		assertThat(composite.toArray(new Integer[4]))
			.containsExactly(1, 2, 3, null);
	}

	@Test
	void subListIsACompositeBackedView() {
		final var first = new ArrayList<>(List.of(0, 1));
		final var second = new ArrayList<>(List.of(2, 3, 4));
		final var composite = new CompositeList<>(List.of(first, second));
		final var subList = composite.subList(1, 4);

		assertThat(subList).containsExactly(1, 2, 3);
		subList.set(1, 20);
		assertThat(second).containsExactly(20, 3, 4);
	}

	@Test
	void structuralChangesAreUnsupported() {
		final var composite = new CompositeList<>(List.of(List.of(1, 2)));

		assertThatThrownBy(() -> composite.add(3))
			.isInstanceOf(UnsupportedOperationException.class);
		assertThatThrownBy(() -> composite.remove(0))
			.isInstanceOf(UnsupportedOperationException.class);
		assertThatThrownBy(composite::clear)
			.isInstanceOf(UnsupportedOperationException.class);
		assertThatThrownBy(() -> composite.listIterator().remove())
			.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void emptyAndInvalidBounds() {
		final var composite = new CompositeList<Integer>(List.of(List.of(), List.of()));

		assertThat(composite.isEmpty()).isTrue();
		assertThat(composite.toArray()).isEmpty();
		assertThatThrownBy(() -> composite.get(0))
			.isInstanceOf(IndexOutOfBoundsException.class);
		assertThatThrownBy(() -> composite.subList(1, 0))
			.isInstanceOf(IndexOutOfBoundsException.class);
		assertThatThrownBy(() -> new CompositeList<Integer>(List.of(List.of(1), null)))
			.isInstanceOf(NullPointerException.class);
	}
}
