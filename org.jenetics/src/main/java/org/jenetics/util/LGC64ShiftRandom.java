/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

/**
 * https://github.com/rabauke/trng4/blob/master/src/lcg64_shift.hpp
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 1.1 &mdash; <em>$Date$</em>
 */
public class LGC64ShiftRandom extends Random64 {

	private static final long serialVersionUID = 1L;

	public static final class Parameter {
		public final long a;
		public final long b;
		public Parameter(final long a, final long b) {
			this.a = a;
			this.b = b;
		}
	}

	public static final Parameter DEFAULT = new Parameter(0xFBD19FBBC5C07FF5L, 1L);

	private long _a = DEFAULT.a;
	private long _b = DEFAULT.b;
	private long _r = 0;

	@Override
	public long nextLong() {
		step();

		long t = _r;
		t ^= t >>> 17;
		t ^= t << 31;
		t ^= t >>> 8;
		return t;
	}

	void step() {
		_r = _a*_r + _b;
	}

	@Override
	public void setSeed(final long seed) {
		_r = seed;
	}

}
