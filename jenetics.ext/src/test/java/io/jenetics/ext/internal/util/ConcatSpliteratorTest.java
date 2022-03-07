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
package io.jenetics.ext.internal.util;

import java.util.List;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ConcatSpliteratorTest {

	@Test
	public void concat1() {
		final Spliterator<Integer> s1 = Stream.of(1, 2, 3).spliterator();

		final int[] array = StreamSupport
			.stream(new ConcatSpliterator<>(List.of(s1)), false)
			.mapToInt(Integer::intValue)
			.toArray();

		Assert.assertEquals(array, new int[]{1, 2, 3});
	}

	@Test
	public void concat2() {
		final Spliterator<Integer> s1 = Stream.of(1, 2, 3).spliterator();
		final Spliterator<Integer> s2 = Stream.of(4, 5, 6).spliterator();

		final int[] array = StreamSupport
			.stream(new ConcatSpliterator<>(List.of(s1, s2)), false)
			.mapToInt(Integer::intValue)
			.toArray();

		Assert.assertEquals(array, new int[]{1, 2, 3, 4, 5, 6});
	}

	@Test
	public void concat3() {
		final Spliterator<Integer> s1 = Stream.of(1, 2, 3).spliterator();
		final Spliterator<Integer> s2 = Stream.of(4, 5, 6).spliterator();
		final Spliterator<Integer> s3 = Stream.of(7, 8, 9).spliterator();

		final int[] array = StreamSupport
			.stream(new ConcatSpliterator<>(List.of(s1, s2, s3)), false)
			.mapToInt(Integer::intValue)
			.toArray();

		Assert.assertEquals(array, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
	}

	@Test
	public void concat4() {
		final Spliterator<Integer> s1 = Stream.of(1, 2, 3).spliterator();
		final Spliterator<Integer> s2 = Stream.of(4, 5, 6).spliterator();
		final Spliterator<Integer> s3 = Stream.of(7, 8, 9).spliterator();

		final int[] array = StreamSupport
			.stream(new ConcatSpliterator<>(List.of(s1, s2, s3)), false)
			.mapToInt(Integer::intValue)
			.limit(2)
			.toArray();

		Assert.assertEquals(array, new int[]{1, 2});
	}

	@Test
	public void concat5() {
		final Spliterator<Integer> s1 = Stream.of(1, 2, 3).spliterator();
		final Spliterator<Integer> s2 = Stream.of(4, 5, 6).spliterator();
		final Spliterator<Integer> s3 = Stream.of(7, 8, 9).spliterator();

		final int[] array = StreamSupport
			.stream(new ConcatSpliterator<>(List.of(s1, s2, s3)), false)
			.mapToInt(Integer::intValue)
			.limit(5)
			.toArray();

		Assert.assertEquals(array, new int[]{1, 2, 3, 4, 5});
	}

	@Test
	public void concat6() {
		final Spliterator<Integer> s1 = Stream.of(1, 2, 3).limit(1).spliterator();
		final Spliterator<Integer> s2 = Stream.of(4, 5, 6).spliterator();
		final Spliterator<Integer> s3 = Stream.of(7, 8, 9).spliterator();

		final int[] array = StreamSupport
			.stream(new ConcatSpliterator<>(List.of(s1, s2, s3)), false)
			.mapToInt(Integer::intValue)
			.limit(5)
			.toArray();

		Assert.assertEquals(array, new int[]{1, 4, 5, 6, 7});
	}

}
