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
		final int n = 20;
		final int k = 8;
		final long N = BinomialCoefficient.apply(n, k);

		final int[] T = KSubset.first(k);
		int count = 1;
		for (int i = 0; i < N + 3 && KSubset.next(T, n); ++i) {
			++count;
		}

		assertThat(count).isEqualTo(N);
	}

	@Test
	public void rank() {
		final int n = 20;
		final int k = 8;
		final int[] T = KSubset.first(k);

		assertThat(KSubset.rank(T, n)).isEqualTo(0);
		int rank = 1;
		while (KSubset.next(T, n)) {
			assertThat(KSubset.rank(T, n)).isEqualTo(rank);
			++rank;
		}
	}

	@Test
	public void unrank() {
		final int n = 20;
		final int k = 8;
		final int[] T = KSubset.first(k);

		final int[] U = new int[k];
		long rank = 0;
		KSubset.unrank(rank, n, U);
		assertThat(U).isEqualTo(T);

		while (KSubset.next(T, n)) {
			++rank;
			KSubset.unrank(rank, n, U);

			assertThat(U).isEqualTo(T);
		}
	}

}
