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
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CyclicSpliteratorTest {

	@Test
	public void cycle1() {
		final Supplier<Spliterator<Integer>> s1 = () -> Stream.of(1, 2, 3).spliterator();
		final Supplier<Spliterator<Integer>> s2 = () -> Stream.of(4, 5, 6).spliterator();
		final Supplier<Spliterator<Integer>> s3 = () -> Stream.of(7, 8, 9).spliterator();

		final CyclicSpliterator<Integer> s = new CyclicSpliterator<>(List.of(s1, s2, s3));
		final int[] array = StreamSupport.stream(s, false)
			.limit(10)
			.mapToInt(Integer::intValue)
			.toArray();

		Assert.assertEquals(array, new int[]{
			1, 2, 3, 4, 5, 6, 7, 8, 9,
			1
		});
	}

	@Test
	public void cycle2() {
		final Supplier<Spliterator<Integer>> s1 = () -> Stream.of(1, 2, 3).spliterator();
		final Supplier<Spliterator<Integer>> s2 = () -> Stream.of(4, 5, 6).spliterator();
		final Supplier<Spliterator<Integer>> s3 = () -> Stream.of(7, 8, 9).spliterator();

		final CyclicSpliterator<Integer> s = new CyclicSpliterator<>(List.of(s1, s2, s3));
		final int[] array = StreamSupport.stream(s, false)
			.limit(35)
			.mapToInt(Integer::intValue)
			.toArray();

		Assert.assertEquals(array, new int[]{
			1, 2, 3, 4, 5, 6, 7, 8, 9,
			1, 2, 3, 4, 5, 6, 7, 8, 9,
			1, 2, 3, 4, 5, 6, 7, 8, 9,
			1, 2, 3, 4, 5, 6, 7, 8
		});
	}

}
