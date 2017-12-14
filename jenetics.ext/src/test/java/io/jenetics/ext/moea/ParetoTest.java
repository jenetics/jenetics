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

import static io.jenetics.ext.moea.Point2.circle;
import static io.jenetics.ext.moea.Point2.front;

import java.util.HashSet;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ParetoTest {

	@Test
	public void compareToPoint() {
		final ISeq<Point2> outline = circle(1000, new Random(234));

		for (Point2 p : outline) {
			Assert.assertTrue(p.dominance(p) == 0);
			Assert.assertTrue(p.dominance(Point2.of(0, 0)) > 0);
			Assert.assertTrue(p.dominance(Point2.of(1, 1)) < 0);

			Assert.assertTrue(Point2.of(0, 0).dominance(p) < 0);
			Assert.assertTrue(Point2.of(1, 1).dominance(p) > 0);

			for (Point2 p2 : outline) {
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

	@Test(dataProvider = "paretoFronts")
	public void frontOf(final ISeq<Point2> elements, final ISeq<Point2> front) {
		Assert.assertEquals(
			new HashSet<>(Pareto.front(elements, Point2::dominance).asList()),
			new HashSet<>(front.asList())
		);
	}

	@DataProvider(name = "paretoFronts")
	public Object[][] paretoFronts() {
		return new Object[][] {
			points(0, 10, 10, false),
			points(0, 10, 10, true),
			points(10, 10, 10, false),
			points(10, 10, 10, true),
			points(100, 50, 100, true),
			points(500, 100, 600, false),
			points(500, 100, 600, true)
		};
	}

	private static Object[] points(
		final int fp1,
		final int cp,
		final int fp2,
		final boolean shuffle
	) {
		final Random random = new Random(123);

		final ISeq<Point2> fpoints1 = front(fp1, random);
		final ISeq<Point2> cpoints = circle(cp, random);
		final ISeq<Point2> fpoints2 = front(fp2, random);
		final ISeq<Point2> all = fpoints1.append(cpoints).append(fpoints2);

		return shuffle
			? new Object[]{all.copy().shuffle(random).toISeq(), fpoints1.append(fpoints2)}
			: new Object[]{all, fpoints1.append(fpoints2)};
	}


	@Test
	public void rank() {
		final Random random = new Random(123);
		final ISeq<Point2> fpoints = front(5, random);
		final ISeq<Point2> cpoints = circle(3, random);

		Assert.assertEquals(
			Pareto.rank(fpoints, Point2::dominance),
			new int[]{0, 0, 0, 0, 0}
		);

		Assert.assertEquals(
			Pareto.rank(fpoints.append(cpoints), Point2::dominance),
			new int[]{0, 0, 0, 0, 0, 1, 2, 1}
		);
	}

	@Test
	public void crowdedDistance() {
		final Random random = new Random(123);
		final ISeq<Point2> cpoints = circle(6, random);

		final double[] dist = Pareto.crowdingDistance(
			cpoints,
			(u, v, i) -> i == 0
				? Double.compare(u.x(), v.x())
				: Double.compare(u.y(), v.y()),
			(u, v, i) -> i == 0 ? u.x() - v.x() : u.y() - v.y(),
			Point2::length
		);

		Assert.assertEquals(
			dist,
			new double[]{
				Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
				Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
				1.3951870515321534, 0.844891356584359
			}
		);
	}

}
