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
package io.jenetics.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.Comparator;
import java.util.stream.Stream;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstötter@gmail.com">Franz Wilhelmstötter</a>
 */
public class GatherersTest {

	@Test
	public void strictlyIncreasing() {
		final ISeq<Integer> values = Stream.of(3, 2, 5, 4, 7, 7, 4, 9)
			.gather(Gatherers.strictlyIncreasing())
			.collect(ISeq.toISeq());

		assertThat(values).isEqualTo(ISeq.of(3, 5, 7, 9));
	}

	@Test
	public void strictlyIncreasingWithDuplicates() {
		final ISeq<Integer> values = Stream.of(1, 1, 1, 2, 2, 1, 3)
			.gather(Gatherers.strictlyIncreasing())
			.collect(ISeq.toISeq());

		assertThat(values).isEqualTo(ISeq.of(1, 2, 3));
	}

	@Test
	public void strictlyIncreasingEmptyStream() {
		final ISeq<Integer> values = Stream.<Integer>empty()
			.gather(Gatherers.strictlyIncreasing())
			.collect(ISeq.toISeq());

		assertThat(values).isEqualTo(ISeq.empty());
	}

	@Test
	public void strictlyIncreasingSameAsStreamsFlatMap() {
		final ISeq<Integer> source = ISeq.of(3, 2, 5, 4, 7, 7, 4, 9);

		final ISeq<Integer> flatMapped = source.stream()
			.flatMap(Streams.toStrictlyIncreasing())
			.collect(ISeq.toISeq());
		final ISeq<Integer> gathered = source.stream()
			.gather(Gatherers.strictlyIncreasing())
			.collect(ISeq.toISeq());

		assertThat(gathered).isEqualTo(flatMapped);
	}

	@Test
	public void strictlyImproving() {
		final ISeq<Integer> values = Stream.of(9, 8, 9, 5, 6, 6, 2, 9)
			.gather(Gatherers.strictlyImproving(Comparator.reverseOrder()))
			.collect(ISeq.toISeq());

		assertThat(values).isEqualTo(ISeq.of(9, 8, 5, 2));
	}

	@Test
	public void strictlyImprovingNullComparator() {
		assertThatNullPointerException()
			.isThrownBy(() -> Gatherers.strictlyImproving(null));
	}

	@Test
	public void strictlyImprovingWithNullValues() {
		final ISeq<Integer> values = Stream.<Integer>of(null, 1, null, 2)
			.gather(Gatherers.strictlyIncreasing())
			.collect(ISeq.toISeq());

		assertThat(values).isEqualTo(ISeq.of(1, 2));
	}

}
