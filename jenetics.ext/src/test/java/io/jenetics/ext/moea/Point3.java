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

import java.util.Arrays;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Point3 {
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
	public int hashCode() {
		return Arrays.hashCode(new double[] {_x, _y, _z});
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Point3 &&
			Double.compare(((Point3) obj)._x, _x) == 0 &&
			Double.compare(((Point3) obj)._y, _y) == 0 &&
			Double.compare(((Point3) obj)._z, _z) == 0;
	}

	@Override
	public String toString() {
		return format("[%f, %f, %f]", _x, _y, _z);
	}

	public static Point3 of(final double x, final double y, final double z) {
		return new Point3(x, y, z);
	}

}
