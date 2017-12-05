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
package io.jenetics.ext;

import static java.lang.Math.sqrt;
import static java.lang.String.format;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.util.ISeq;

import io.jenetics.ext.util.Pareto;
import io.jenetics.ext.util.Point2;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ParetoTest {

	static ISeq<Point2> frontPoints(final int count, final Random random) {
		return random.doubles(count)
			.mapToObj(x -> Point2.of(x, sqrt(1 - x*x)))
			.collect(ISeq.toISeq());
	}

	static ISeq<Point2> circlePoints(final int count, final Random random) {
		return random.doubles()
			.mapToObj(x -> Point2.of(x, random.nextDouble()))
			.filter(p -> p.x()*p.x() + p.y()*p.y() < 0.9)
			.limit(count)
			.collect(ISeq.toISeq());
	}

	@Test
	public void compareToPoint() {
		final ISeq<Point2> outline = circlePoints(1000, new Random(234));

		for (Point2 p : outline) {
			Assert.assertTrue(p.domination(p) == 0);
			Assert.assertTrue(p.domination(Point2.of(0, 0)) > 0);
			Assert.assertTrue(p.domination(Point2.of(1, 1)) < 0);

			Assert.assertTrue(Point2.of(0, 0).domination(p) < 0);
			Assert.assertTrue(Point2.of(1, 1).domination(p) > 0);

			for (Point2 p2 : outline) {
				if (p.domination(p2) == 0) {
					Assert.assertTrue(p2.domination(p) == 0);
				}
				if (p.domination(p2) < 0) {
					Assert.assertTrue(p2.domination(p) > 0);
				}
				if (p.domination(p2) > 0) {
					Assert.assertTrue(p2.domination(p) < 0);
				}
			}
		}
	}

	@Test(dataProvider = "paretoFronts")
	public void frontOf(final ISeq<Point2> elements, final ISeq<Point2> front) {
		Assert.assertEquals(
			new HashSet<>(Pareto.front(elements, Point2::domination).asList()),
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

		final ISeq<Point2> fpoints1 = frontPoints(fp1, random);
		final ISeq<Point2> cpoints = circlePoints(cp, random);
		final ISeq<Point2> fpoints2 = frontPoints(fp2, random);
		final ISeq<Point2> all = fpoints1.append(cpoints).append(fpoints2);

		return shuffle
			? new Object[]{all.copy().shuffle(random).toISeq(), fpoints1.append(fpoints2)}
			: new Object[]{all, fpoints1.append(fpoints2)};
	}


	@Test
	public void ranks() {
		final Random random = new Random(123);
		final ISeq<Point2> fpoints = frontPoints(5, random);
		final ISeq<Point2> cpoints = circlePoints(3, random);

		System.out.println(Arrays.toString(NSGA.ranks(fpoints, Point2::domination)));
		System.out.println(Arrays.toString(NSGA.ranks(fpoints.append(cpoints), Point2::domination)));
		System.out.println(Pareto.front(fpoints, Point2::domination));
	}

}
