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

import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class GeneratorSpliteratorTest {

	@Test
	public void generator1() {
		final Spliterator<Integer> spliterator =
			new GeneratorSpliterator<>(
				i -> {
					final int s = i == null ? 0 : i;
					return Stream.of(s + 1, s + 2, s + 3).spliterator();
				}
			);

		final int[] array = StreamSupport.stream(spliterator, false)
			.limit(10)
			.mapToInt(Integer::intValue)
			.toArray();

		Assert.assertEquals(array, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
	}

}
