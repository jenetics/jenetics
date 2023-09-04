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

import java.util.HashSet;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ParetoTest {

	@Test(invocationCount = 20)
	public void frontMax() {
		final Random random = new Random();
		final ISeq<Vec<double[]>> rank0 = frontMax(20, 50, random);
		final ISeq<Vec<double[]>> rank1 = frontMax(15, 50, random);
		final ISeq<Vec<double[]>> rank2 = frontMax(10, 50, random);
		final ISeq<Vec<double[]>> rank3 = frontMax(5, 50, random);
		final ISeq<Vec<double[]>> rank4 = frontMax(1, 25, random);

		final ISeq<Vec<double[]>> points = rank0
			.append(rank1)
			.append(rank2)
			.append(rank3)
			.append(rank4)
			.copy().shuffle().toISeq();

		final ISeq<Vec<double[]>> front = Pareto.front(points);
		Assert.assertEquals(new HashSet<>(front.asList()), new HashSet<>(rank0.asList()));
	}

	@Test
	public void frontMin() {
		final Random random = new Random(123123);
		final ISeq<Vec<double[]>> rank4 = frontMin(1, 25, random);
		final ISeq<Vec<double[]>> rank3 = frontMin(5, 25, random);
		final ISeq<Vec<double[]>> rank2 = frontMin(10, 25, random);
		final ISeq<Vec<double[]>> rank1 = frontMin(15, 25, random);
		final ISeq<Vec<double[]>> rank0 = frontMin(20, 50, random);

		final ISeq<Vec<double[]>> points = rank0
			.append(rank1)
			.append(rank2)
			.append(rank3)
			.append(rank4);


		final ISeq<Vec<double[]>> front = Pareto.front(points, (u, v) -> v.dominance(u));
		Assert.assertEquals(front, rank0);
	}

	@Test
	public void rankMax() {
		final Random random = new Random(123123);
		final ISeq<Vec<double[]>> rank0 = frontMax(20, 50, random);
		final ISeq<Vec<double[]>> rank1 = frontMax(15, 50, random);
		final ISeq<Vec<double[]>> rank2 = frontMax(10, 50, random);
		final ISeq<Vec<double[]>> rank3 = frontMax(5, 50, random);
		final ISeq<Vec<double[]>> rank4 = frontMax(1, 50, random);

		final ISeq<Vec<double[]>> points = rank0
			.append(rank1)
			.append(rank2)
			.append(rank3)
			.append(rank4);

		final int[] rank = Pareto.rank(points);
		Assert.assertEquals(
			IntStream.range(0, points.size())
				.filter(i -> rank[i] == 0)
				.mapToObj(points)
				.collect(Collectors.toSet()),
			new HashSet<>(rank0.asList())
		);
		Assert.assertEquals(
			IntStream.range(0, points.size())
				.filter(i -> rank[i] == 1)
				.mapToObj(points)
				.collect(Collectors.toSet()),
			new HashSet<>(rank1.asList())
		);
		Assert.assertEquals(
			IntStream.range(0, points.size())
				.filter(i -> rank[i] == 2)
				.mapToObj(points)
				.collect(Collectors.toSet()),
			new HashSet<>(rank2.asList())
		);
		Assert.assertEquals(
			IntStream.range(0, points.size())
				.filter(i -> rank[i] == 3)
				.mapToObj(points)
				.collect(Collectors.toSet()),
			new HashSet<>(rank3.asList())
		);
		Assert.assertEquals(
			IntStream.range(0, points.size())
				.filter(i -> rank[i] == 4)
				.mapToObj(points)
				.collect(Collectors.toSet()),
			new HashSet<>(rank4.asList())
		);
	}

	@Test
	public void rankMin() {
		final Random random = new Random(123123);
		final ISeq<Vec<double[]>> rank4 = frontMin(1, 25, random);
		final ISeq<Vec<double[]>> rank3 = frontMin(5, 25, random);
		final ISeq<Vec<double[]>> rank2 = frontMin(10, 25, random);
		final ISeq<Vec<double[]>> rank1 = frontMin(15, 25, random);
		final ISeq<Vec<double[]>> rank0 = frontMin(20, 50, random);

		final ISeq<Vec<double[]>> points = rank0
			.append(rank1)
			.append(rank2)
			.append(rank3)
			.append(rank4);

		final int[] rank = Pareto.rank(points, (u, v) -> v.dominance(u));
		Assert.assertEquals(
			IntStream.range(0, points.size())
				.filter(i -> rank[i] == 0)
				.mapToObj(points)
				.collect(Collectors.toSet()),
			new HashSet<>(rank0.asList())
		);
		Assert.assertEquals(
			IntStream.range(0, points.size())
				.filter(i -> rank[i] == 1)
				.mapToObj(points)
				.collect(Collectors.toSet()),
			new HashSet<>(rank1.asList())
		);
		Assert.assertEquals(
			IntStream.range(0, points.size())
				.filter(i -> rank[i] == 2)
				.mapToObj(points)
				.collect(Collectors.toSet()),
			new HashSet<>(rank2.asList())
		);
		Assert.assertEquals(
			IntStream.range(0, points.size())
				.filter(i -> rank[i] == 3)
				.mapToObj(points)
				.collect(Collectors.toSet()),
			new HashSet<>(rank3.asList())
		);
		Assert.assertEquals(
			IntStream.range(0, points.size())
				.filter(i -> rank[i] == 4)
				.mapToObj(points)
				.collect(Collectors.toSet()),
			new HashSet<>(rank4.asList())
		);
	}

	@Test
	public void dominance() {
		final ISeq<Vec<double[]>> outline = circle(1000, new Random(234));

		for (Vec<double[]> p : outline) {
			Assert.assertTrue(p.dominance(p) == 0);
			Assert.assertTrue(p.dominance(Vec.of(-1.0, -1.0)) > 0);
			Assert.assertTrue(p.dominance(Vec.of(1.0, 1.0)) < 0);

			Assert.assertTrue(Vec.of(-1.0, -1.0).dominance(p) < 0);
			Assert.assertTrue(Vec.of(1.0, 1.0).dominance(p) > 0);

			for (Vec<double[]> p2 : outline) {
				if (p.dominance(p2) == 0) {
					Assert.assertTrue(p2.dominance(p) == 0);
				}
				if (p.dominance(p2) < 0) {
					Assert.assertTrue(p2.dominance(p) > 0);
				}
				if (p.dominance(p2) > 0) {
					Assert.assertTrue(p2.dominance(p) < 0);
				}
			}
		}
	}

	static ISeq<Vec<double[]>> frontMax(
		final double r,
		final int count,
		final RandomGenerator random
	) {
		return random.doubles(count)
			.map(a -> a*PI*0.5)
			.mapToObj(a -> Vec.of(r*sin(a), r*cos(a)))
			.collect(ISeq.toISeq());
	}

	static ISeq<Vec<double[]>> frontMin(
		final double r,
		final int count,
		final RandomGenerator random
	) {
		return random.doubles(count)
			.map(a -> a*PI*0.5 + PI)
			.mapToObj(a -> Vec.of(r*sin(a), r*cos(a)))
			.collect(ISeq.toISeq());
	}

	static ISeq<Vec<double[]>> circle(final int count, final RandomGenerator random) {
		return random.doubles(count)
			.mapToObj(r -> {
				final double a = random.nextDouble()*2*PI;
				return Vec.of(r*sin(a), r*cos(a));
			})
			.collect(ISeq.toISeq());
	}

}
