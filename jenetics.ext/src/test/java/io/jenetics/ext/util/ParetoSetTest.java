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
package io.jenetics.ext.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ParetoSetTest {

	@Test
	public void add() {
		final Random random = new Random(96969);

		final List<Point2> elements = new ArrayList<>();
		final ParetoSet<Point2> set = new ParetoSet<>(Point2::dominance);

		for (int i = 0; i < 500; ++i) {
			final Point2 point = Point2.of(random.nextDouble(), random.nextDouble());
			elements.add(point);
			set.add(point);

			Assert.assertEquals(
				new HashSet<>(set),
				new HashSet<>(Pareto.front(elements, Point2::dominance).asList())
			);
		}
	}

	@Test
	public void addAll() {
		final Random random = new Random(96969);
		final ParetoSet<Point2> set = new ParetoSet<>(Point2::dominance);

		final List<Point2> elements = IntStream.range(0, 500)
			.mapToObj(i -> Point2.of(random.nextDouble(), random.nextDouble()))
			.collect(Collectors.toList());

		set.addAll(elements);

		Assert.assertEquals(
			new HashSet<>(set),
			new HashSet<>(Pareto.front(elements, Point2::dominance).asList())
		);
	}

}
