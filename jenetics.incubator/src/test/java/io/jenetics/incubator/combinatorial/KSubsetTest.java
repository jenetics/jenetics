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

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class KSubsetTest {

	@Test
	public void next() {
		final var sub = new KSubset(20, 8);
		final long N = BinomialCoefficient.apply(sub.n(), sub.k());

		final int[] T = sub.first();
		int count = 1;
		for (int i = 0; i < N + 3 && KSubset.next(T, sub.n()); ++i) {
			++count;
		}

		assertThat(count).isEqualTo(N);
	}

	@Test
	public void rank() {
		final var sub = new KSubset(20, 8);
		final int[] T = sub.first();

		assertThat(sub.rank(T)).isEqualTo(0);
		int rank = 1;
		while (KSubset.next(T, sub.n())) {
			assertThat(sub.rank(T)).isEqualTo(rank);
			++rank;
		}
	}

	@Test
	public void unrank() {
		final var sub = new KSubset(20, 8);
		final int[] T = sub.first();

		final int[] U = new int[sub.k()];
		long rank = 0;
		sub.unrank(rank, U);
		assertThat(U).isEqualTo(T);

		while (KSubset.next(T, sub.n())) {
			++rank;
			sub.unrank(rank, U);

			assertThat(U).isEqualTo(T);
		}
	}

}
