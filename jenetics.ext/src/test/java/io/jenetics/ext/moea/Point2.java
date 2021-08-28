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

import static java.lang.Math.sqrt;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.random.RandomGenerator;

import io.jenetics.util.ISeq;

/**
 * Represents a 2-dimensional point.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public final class Point2 implements Vec<Point2> {
	private final double _x;
	private final double _y;

	private Point2(final double x, final double y) {
		_x = x;
		_y = y;
	}

	/**
	 * Return the <em>x</em> value of the point.
	 *
	 * @return the <em>x</em> value of the point
	 */
	public double x() {
		return _x;
	}

	/**
	 * Return the <em>y</em> value of the point.
	 *
	 * @return the <em>y</em> value of the point
	 */
	public double y() {
		return _y;
	}

	@Override
	public Point2 data() {
		return this;
	}

	@Override
	public int length() {
		return 2;
	}

	@Override
	public ElementComparator<Point2> comparator() {
		return (u, v, i) -> i == 0
			? Double.compare(u._x, v._x)
			: Double.compare(u._y, v._y);
	}

	@Override
	public ElementDistance<Point2> distance() {
		return (u, v, i) -> i == 0 ? u.x() - v.x() : u.y() - v.y();
	}

	@Override
	public Comparator<Point2> dominance() {
		return Point2::dominance;
	}

	@Override
	public int dominance(final Vec<Point2> point) {
		requireNonNull(point);
		final Point2 other = point.data();

		boolean adom = false;
		boolean bdom = false;

		int cmp = Double.compare(_x, other._x);
		if (cmp > 0) {
			adom = true;
		} else if (cmp < 0) {
			bdom = true;
		}

		cmp = Double.compare(_y, other._y);
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
		int hash = 17;
		hash += 31*Double.hashCode(_x) + 37;
		hash += 31*Double.hashCode(_y) + 37;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Point2 other &&
			Double.compare(other._x, _x) == 0 &&
			Double.compare(other._y, _y) == 0;
	}

	@Override
	public String toString() {
		return format("[%f, %f]", _x, _y);
	}

	/**
	 * Create a new 2-dimensional point from the given values.
	 *
	 * @param x the x value
	 * @param y the y value
	 * @return a new 2-dimensional point from the given values
	 */
	public static Point2 of(final double x, final double y) {
		return new Point2(x, y);
	}


	static ISeq<Point2> front(final int count, final RandomGenerator random) {
		return random.doubles(count)
			.mapToObj(x -> Point2.of(x, sqrt(1 - x*x)))
			.collect(ISeq.toISeq());
	}

	static ISeq<Point2> circle(final int count, final RandomGenerator random) {
		return random.doubles()
			.mapToObj(x -> Point2.of(x, random.nextDouble()))
			.filter(p -> p.x()*p.x() + p.y()*p.y() < 0.9)
			.limit(count)
			.collect(ISeq.toISeq());
	}
}
