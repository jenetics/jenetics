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
package io.jenetics.incubator.combinatorial;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.random.RandomGenerator;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class KSubsetTest {

	record Ints(int[] values) {
		@Override
		public int hashCode() {
			return Arrays.hashCode(values);
		}
		@Override
		public boolean equals(final Object obj) {
			return obj instanceof Ints ints &&
				Arrays.equals(values, ints.values);
		}
		@Override
		public String toString() {
			return Arrays.toString(values);
		}
	}

	@Test
	public void cursor() {
		final var sub = new KSubset(18, 7);
		final var elements = new HashSet<Ints>();

		final var cursor = sub.cursor();
		final var T = new int[sub.k()];
		var count = 0;
		while (cursor.next(T)) {
			elements.add(new Ints(T));
			++count;
		}

		assertThat(count).isEqualTo(sub.size());
		assertThat(elements).hasSize(count);
	}

	@Test
	public void cursor2() {
		final var sub = new KSubset(18, 7);
		final var elements = new HashSet<Ints>();

		final var cursor = sub.cursor();
		int[] T;
		var count = 0;
		while ((T = cursor.next()) != null) {
			elements.add(new Ints(T));
			++count;
		}

		assertThat(count).isEqualTo(sub.size());
		assertThat(elements).hasSize(count);
	}

	@Test
	public void iterator() {
		final var sub = new KSubset(18, 7);
		final var elements = new HashSet<Ints>();

		final var it = sub.iterator();
		var count = 0;
		while (it.hasNext()) {
			elements.add(new Ints(it.next()));
			++count;
		}

		assertThat(count).isEqualTo(sub.size());
		assertThat(elements).hasSize(count);
	}

	@Test
	public void stream() {
		final var sub = new KSubset(18, 7);
		final var elements = new HashSet<Ints>();

		final var count = sub.stream()
			.peek(e -> elements.add(new Ints(e)))
			.count();

		assertThat(count).isEqualTo(sub.size());
		assertThat(elements).hasSize((int)count);
	}

	@Test
	public void randomCursor() {
		final var sub = new KSubset(18, 7);

		new KSubset(18, 7)
			.cursor(RandomGenerator.getDefault())
			.next();
	}

	@Test
	public void rank() {
		final var sub = new KSubset(20, 8);

		assertThat(sub.rank(sub.start())).isEqualTo(0);
		assertThat(sub.rank(sub.end())).isEqualTo(sub.size() - 1);

		int rank = 0;
		for (var element : sub) {
			assertThat(sub.rank(element)).isEqualTo(rank);
			++rank;
		}
	}

	@Test
	public void unrank() {
		final var sub = new KSubset(20, 8);
		final var cursor = sub.cursor();;
		final var T = new int[sub.k()];

		for (long rank = 0; rank < sub.size(); ++rank) {
			assertThat(cursor.next(T)).isTrue();
			assertThat(sub.unrank(rank)).isEqualTo(T);
		}
		assertThat(cursor.next(T)).isFalse();
	}

	@Test
	public void compare() {
		final var ksubset = new KSubset(5, 3);
		for (long rank = 1; rank < ksubset.size(); ++rank) {
			final int[] a = ksubset.unrank(rank - 1);
			final int[] b = ksubset.unrank(rank);

			assertThat(ksubset.compare(a, b)).isLessThan(0);
			assertThat(ksubset.compare(b, a)).isGreaterThan(0);
			assertThat(ksubset.compare(a, a)).isEqualTo(0);
			assertThat(ksubset.compare(b, b)).isEqualTo(0);
		}
	}

}
