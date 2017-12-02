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
import io.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ParetoFrontTest {

	static final class Point implements Comparable<Point> {
		final double x;
		final double y;

		private Point(final double x, final double y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int compareTo(final Point point) {
			boolean adom = false;
			boolean bdom = false;

			int cmp = Double.compare(x, point.x);
			if (cmp > 0) {
				adom = true;
				if (bdom) {
					return 0;
				}
			} else if (cmp < 0) {
				bdom = true;
				if (adom) {
					return 0;
				}
			}

			cmp = Double.compare(y, point.y);
			if (cmp > 0) {
				adom = true;
				if (bdom) {
					return 0;
				}
			} else if (cmp < 0) {
				bdom = true;
				if (adom) {
					return 0;
				}
			}

			if (adom == bdom) {
				return 0;
			} else if (adom) {
				return 1;
			} else {
				return -1;
			}
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(new double[]{x, y});
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof Point &&
				Double.compare(((Point)obj).x, x) == 0 &&
				Double.compare(((Point)obj).y, y) == 0;
		}

		@Override
		public String toString() {
			return format("[%f, %f: %f]", x, y, Math.sqrt(x*x + y*y));
		}

		static Point of(final double x, final double y) {
			return new Point(x, y);
		}
	}

	static ISeq<Point> frontPoints(final int count, final Random random) {
		return random.doubles(count)
			.mapToObj(x -> Point.of(x, sqrt(1 - x*x)))
			.collect(ISeq.toISeq());
	}

	static ISeq<Point> circlePoints(final int count, final Random random) {
		return random.doubles()
			.mapToObj(x -> Point.of(x, random.nextDouble()))
			.filter(p -> p.x*p.x + p.y*p.y < 0.9)
			.limit(count)
			.collect(ISeq.toISeq());
	}

	@Test
	public void compareToPoint() {
		final ISeq<Point> outline = circlePoints(1000, new Random(234));

		for (Point p : outline) {
			Assert.assertTrue(p.compareTo(p) == 0);
			Assert.assertTrue(p.compareTo(Point.of(0, 0)) > 0);
			Assert.assertTrue(p.compareTo(Point.of(1, 1)) < 0);

			Assert.assertTrue(Point.of(0, 0).compareTo(p) < 0);
			Assert.assertTrue(Point.of(1, 1).compareTo(p) > 0);

			for (Point p2 : outline) {
				if (p.compareTo(p2) == 0) {
					Assert.assertTrue(p2.compareTo(p) == 0);
				}
				if (p.compareTo(p2) < 0) {
					Assert.assertTrue(p2.compareTo(p) > 0);
				}
				if (p.compareTo(p2) > 0) {
					Assert.assertTrue(p2.compareTo(p) < 0);
				}
			}
		}
	}

	@Test(dataProvider = "paretoFronts")
	public void frontOf(final ISeq<Point> elements, final ISeq<Point> front) {
		Assert.assertEquals(
			new HashSet<>(ParetoFront.frontOf(elements).asList()),
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

		final ISeq<Point> fpoints1 = frontPoints(fp1, random);
		final ISeq<Point> cpoints = circlePoints(cp, random);
		final ISeq<Point> fpoints2 = frontPoints(fp2, random);
		final ISeq<Point> all = fpoints1.append(cpoints).append(fpoints2);

		return shuffle
			? new Object[]{all.copy().shuffle(random).toISeq(), fpoints1.append(fpoints2)}
			: new Object[]{all, fpoints1.append(fpoints2)};
	}


}
