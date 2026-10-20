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

import static io.jenetics.stat.IntSummary.sum;

import java.util.Arrays;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class IndexCursorTest {

	//@Test(dataProvider = "indexSizes")
	public void next(Sizes sizes) {
		final var index = new IndexCursor(sizes.values);
		assertThat(index.pos()).isEqualTo(-1);
		assertThat(index.prev()).isFalse();

		index.pos(-123);
		assertThat(index.pos()).isEqualTo(-1);

		int count = 0;
		while (index.next()) {
			assertThat(index.pos()).isEqualTo(count);

			final var index1 = index.index1();
			final var index2 = index.index2();
			index.pos(count);
			assertThat(index.pos()).isEqualTo(count);
			assertThat(index.index1()).isEqualTo(index1);
			assertThat(index.index2()).isEqualTo(index2);

			++count;
		}
		assertThat(count).isEqualTo(sum(sizes.values));
		assertThat(index.size()).isEqualTo(count);
		if (index.size() == 0) {
			assertThat(index.pos()).isEqualTo(-1);
		} else {
			assertThat(index.pos()).isEqualTo(count);
		}

		while (index.prev()) {
			--count;
			assertThat(index.pos()).isEqualTo(count - 1);

			final var index1 = index.index1();
			final var index2 = index.index2();
			index.pos(count - 1);
			assertThat(index.pos()).isEqualTo(count - 1);
			assertThat(index.index1()).isEqualTo(index1);
			assertThat(index.index2()).isEqualTo(index2);
		}
		if (index.size() == 0) {
			assertThat(count).isEqualTo(0);
		} else {
			assertThat(count).isEqualTo(1);
			assertThat(index.pos()).isEqualTo(0);
		}
	}

	public record Sizes(int... values) {
		@Override
		public String toString() {
			return Arrays.toString(values);
		}
	}

	@DataProvider
	public Object[][] indexSizes() {
		return new Object[][] {
			{new Sizes(0, 0)},
			{new Sizes(0, 1)},
			{new Sizes(1, 0)},
			{new Sizes(1, 1)},
			{new Sizes(1, 10)},
			{new Sizes(10, 1)},
			{new Sizes(7, 8)},
			{new Sizes(23, 17)},
			{new Sizes(23, 0, 17)},
			{new Sizes(23, 0, 17, 234, 0, 3, 1, 4, 6)}
		};
	}

}
