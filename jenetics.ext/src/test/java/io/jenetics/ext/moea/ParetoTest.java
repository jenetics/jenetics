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

import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.util.HashSet;
import java.util.Random;
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

	@Test
	public void crowdedDistance() {
		final Random random = new Random(5345);
		ISeq<Vec<double[]>> points = IntStream.range(0, 50)
			.mapToObj(i -> {
				final double k = random.nextInt(10_000);
				final double l = random.nextInt(10_000);
				return Vec.of(k, l);
			})
			.collect(ISeq.toISeq());

		final double[] distance = Pareto.crowdingDistance(points);
		Assert.assertEquals(
			distance,
			new double[]{
				0.07870822748764089, POSITIVE_INFINITY, 0.04223935440208857,
				0.0734723931549105, 0.08262933375836108, 0.07080599996446849,
				0.1280772521691561, 0.07500782380601785, 0.10346165319126902,
				0.08739273743069301, 0.09371414325750799, 0.09677297469253897,
				0.06860068967311245, 0.1556309291729891, 0.044762588039068914,
				0.05301158371826927, 0.05807157942534296, 0.12285056425606283,
				0.05444377867925762, 0.07798119564333192, 0.08084558556530863,
				0.05994420808040598, 0.13953549396293413, 0.08340532220133795,
				POSITIVE_INFINITY, 0.030426551909450487, 0.09843817081818175,
				0.12123391133159023, 0.04392629523611162, 0.02599473437004262,
				0.091243421437676, 0.11746678529383461, 0.08712213776362061,
				0.033935252836441665, 0.04480182979654032, 0.06176829353429881,
				0.10228229213988838, 0.07835188917954063, 0.040138923055904996,
				0.14922803236443535, 0.062381971784731975, 0.03538877910831255,
				POSITIVE_INFINITY, 0.15121414896756094, 0.11176206518424872,
				0.10671055446081582, 0.10338412049057095, 0.06070200703348302,
				0.10065784667258369, POSITIVE_INFINITY}
		);
	}


	static ISeq<Vec<double[]>> frontMax(
		final double r,
		final int count,
		final Random random
	) {
		return random.doubles(count)
			.map(a -> a*PI*0.5)
			.mapToObj(a -> Vec.of(r*sin(a), r*cos(a)))
			.collect(ISeq.toISeq());
	}

	static ISeq<Vec<double[]>> frontMin(
		final double r,
		final int count,
		final Random random
	) {
		return random.doubles(count)
			.map(a -> a*PI*0.5 + PI)
			.mapToObj(a -> Vec.of(r*sin(a), r*cos(a)))
			.collect(ISeq.toISeq());
	}

	static ISeq<Vec<double[]>> circle(final int count, final Random random) {
		return random.doubles(count)
			.mapToObj(r -> {
				final double a = random.nextDouble()*2*PI;
				return Vec.of(r*sin(a), r*cos(a));
			})
			.collect(ISeq.toISeq());
	}

}
