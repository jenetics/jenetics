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

import static java.lang.String.format;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Point2 implements MOV<Point2> {
	private final double _x;
	private final double _y;

	private Point2(final double x, final double y) {
		_x = x;
		_y = y;
	}

	public double x() {
		return _x;
	}

	public double y() {
		return _y;
	}

	@Override
	public Point2 value() {
		return this;
	}

	@Override
	public int size() {
		return 2;
	}

	@Override
	public int domination(final Point2 other) {
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
		return obj instanceof Point2 &&
			Double.compare(((Point2)obj)._x, _x) == 0 &&
			Double.compare(((Point2)obj)._y, _y) == 0;
	}

	@Override
	public String toString() {
		return format("[%f, %f]", _x, _y);
	}

	public static Point2 of(final double x, final double y) {
		return new Point2(x, y);
	}

}
