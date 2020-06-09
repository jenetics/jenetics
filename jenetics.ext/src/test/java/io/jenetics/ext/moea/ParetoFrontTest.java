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
package io.jenetics.ext.moea;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ParetoFrontTest {

	@Test(invocationCount = 10)
	public void add() {
		final Random random = new Random();

		final List<Vec<double[]>> elements = new ArrayList<>();
		final ParetoFront<Vec<double[]>> set = new ParetoFront<>(Vec::dominance);

		for (int i = 0; i < 500; ++i) {
			final Vec<double[]> point = circle(random);
			elements.add(point);
			set.add(point);

			Assert.assertEquals(
				new HashSet<>(set),
				new HashSet<>(Pareto.front(ISeq.of(elements)).asList())
			);
		}
	}

	@Test(invocationCount = 10)
	public void addReverse() {
		final Random random = new Random();

		final List<Vec<double[]>> elements = new ArrayList<>();
		final ParetoFront<Vec<double[]>> set = new ParetoFront<>((a, b) -> b.dominance(a));

		for (int i = 0; i < 500; ++i) {
			final Vec<double[]> point = circle(random);
			elements.add(point);
			set.add(point);

			Assert.assertEquals(
				new HashSet<>(set),
				new HashSet<>(Pareto.front(ISeq.of(elements), (a, b) -> b.dominance(a)).asList())
			);
		}
	}

	@Test(invocationCount = 10)
	public void addAll() {
		final Random random = new Random();
		final ParetoFront<Vec<double[]>> set = new ParetoFront<>(Vec::dominance);

		final List<Vec<double[]>> elements = IntStream.range(0, 500)
			.mapToObj(i -> circle(random))
			.collect(Collectors.toList());

		set.addAll(elements);

		Assert.assertEquals(
			new HashSet<>(set),
			new HashSet<>(Pareto.front(ISeq.of(elements)).asList())
		);
	}

	@Test
	public void trim() {
		final Random random = new Random();
		final ParetoFront<Vec<double[]>> set = new ParetoFront<>(Vec::dominance);

		final List<Vec<double[]>> elements = IntStream.range(0, 100_000)
			.mapToObj(i -> circle(random))
			.collect(Collectors.toList());

		set.addAll(elements);

		final Set<Vec<double[]>> front = new HashSet<>(
			Pareto.front(Seq.viewOf(elements)).asList()
		);

		Assert.assertEquals(new HashSet<>(set), front);

		final int trimmedSize = set.size()/2;
		Assert.assertTrue(trimmedSize > 0);

		set.trim(trimmedSize, Vec::compare, Vec::distance, Vec::length);
		Assert.assertEquals(set.size(), trimmedSize);

		/*
		final List<Vec<double[]>> missing = set.stream()
			.filter(v -> !front.contains(v))
			.collect(Collectors.toList());

		System.out.println(missing);
		*/
	}

	private static Vec<double[]> circle(final Random random) {
		final double r = random.nextDouble();
		final double a = random.nextDouble()*2*PI;
		return Vec.of(r*sin(a), r*cos(a));
	}

	@Test
	public void withEqualsPredicate() {
		final class Entry {
			final int random = ThreadLocalRandom.current().nextInt();
			final Vec<int[]> data;
			Entry(final Vec<int[]> data) {
				this.data = data;
			}
			@Override
			public int hashCode() {
				return Objects.hash(random, data);
			}
			@Override
			public boolean equals(final Object obj) {
				return obj == this ||
					obj instanceof Entry &&
					random == ((Entry)obj).random &&
					Objects.equals(data, ((Entry)obj).data);
			}
		}

		final ParetoFront<Entry> front = new ParetoFront<>(
			(e1, e2) -> e1.data.dominance(e2.data),
			(e1, e2) -> e1.data.equals(e2.data)
		);
		front.add(new Entry(Vec.of(1, 2, 3)));
		front.add(new Entry(Vec.of(1, 2, 3)));
		front.add(new Entry(Vec.of(1, 2, 4)));
		front.add(new Entry(Vec.of(1, 2, 4)));

		Assert.assertEquals(front.size(), 1);
	}

}
