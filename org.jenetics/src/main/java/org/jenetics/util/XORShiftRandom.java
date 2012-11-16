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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import java.util.Random;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 1.1 &mdash; <em>$Date$</em>
 */
class XORShiftRandom extends Random {
	private static final long serialVersionUID = 1L;
	
	private static final ThreadLocal<XORShiftRandom> _random = new ThreadLocal<XORShiftRandom>() {
		@Override
		protected XORShiftRandom initialValue() {
			return new XORShiftRandom();
		}
	};
	
	private long _seed = System.nanoTime();
	
	XORShiftRandom() {
	}
	
	public static XORShiftRandom current() {
		return _random.get();
	}
	
	@Override
	public int nextInt() {
		final long x = nextLong();	
		return (int)(x >>> 32)^(int)(x << 32);
	}
	
	@Override
	public long nextLong() {
		_seed ^= (_seed << 21);
		_seed ^= (_seed >>> 35);
		_seed ^= (_seed << 4);
		return _seed;
	}
	
	@Override
	protected int next(final int bits) {
		return (int)(nextLong() >>> (64 - bits));
	}
}







