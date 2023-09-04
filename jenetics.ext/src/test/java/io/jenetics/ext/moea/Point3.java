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

import static java.lang.String.format;

import java.util.Comparator;

/**
 * Sample implementation of a 3D double point.
 */
public final class Point3 implements Vec<Point3> {
	private final double _x;
	private final double _y;
	private final double _z;

	private Point3(final double x, final double y, final double z) {
		_x = x;
		_y = y;
		_z = z;
	}

	public double x() {
		return _x;
	}

	public double y() {
		return _y;
	}

	public double z() {
		return _z;
	}

	@Override
	public Point3 data() {
		return this;
	}

	@Override
	public int length() {
		return 3;
	}

	@Override
	public ElementComparator<Point3> comparator() {
		return Point3::cmp;
	}

	private static int cmp(final Point3 u, final Point3 v, final int i) {
		switch (i) {
			case 0: return Double.compare(u._x, v._x);
			case 1: return Double.compare(u._y, v._y);
			case 2: return Double.compare(u._z, v._z);
			default: throw new IllegalArgumentException("Illegal index: " + i);
		}
	}

	@Override
	public ElementDistance<Point3> distance() {
		return Point3::dst;
	}

	private static double dst(final Point3 u, final Point3 v, final int i) {
		switch (i) {
			case 0: return u._x - v._x;
			case 1: return u._y - v._y;
			case 2: return u._z - v._z;
			default: throw new IllegalArgumentException("Illegal index: " + i);
		}
	}

	@Override
	public Comparator<Point3> dominance() {
		return Point3::dom;
	}

	private static int dom(final Point3 u, final Point3 v) {
		return Pareto.dominance(u, v, 3, Point3::cmp);
	}

	@Override
	public int hashCode() {
		int hash = 37;
		hash += 31*Double.hashCode(_x) + 17;
		hash += 31*Double.hashCode(_y) + 17;
		hash += 31*Double.hashCode(_z) + 17;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Point3 other &&
			Double.compare(other._x, _x) == 0 &&
			Double.compare(other._y, _y) == 0 &&
			Double.compare(other._z, _z) == 0;
	}

	@Override
	public String toString() {
		return format("[%f, %f, %f]", _x, _y, _z);
	}

	public static Point3 of(final double x, final double y, final double z) {
		return new Point3(x, y, z);
	}
}
