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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class OrderedTest {

	@Test
	public void comparing() {
		final List<Ordered<Integer>> objects = IntStream.range(0, 100)
			.mapToObj(i -> Ordered.of(i, Comparator.reverseOrder()))
			.sorted(Comparator.naturalOrder())
			.collect(Collectors.toUnmodifiableList());

		for (int i = 0; i < objects.size(); ++i) {
			final int value = objects.get(i).get();
			Assert.assertEquals(value, objects.size() - i - 1);
		}
	}

}
