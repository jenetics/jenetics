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
 * <q align="justified" cite="http://www.nr.com/"><em>
 * This generator was discovered and characterized by George Marsaglia
 * [<a href="http://www.jstatsoft.org/v08/i14/paper">Xorshift RNGs</a>]. In just
 * three XORs and three shifts (generally fast operations) it produces a full
 * period of 2<sup>64</sup> - 1 on 64 bits. (The missing value is zero, which
 * perpetuates itself and must be avoided.) High and low bits pass Diehard.
 * </em></q>
 * <p align="left">
 * <strong>Numerical Recipes 3rd Edition: The Art of Scientific Computing</strong>
 * <br/>
 * <em>Chapter 7. Random Numbers; Page 345</em>
 * <br/>
 * <small>Cambridge University Press New York, NY, USA ©2007</small>
 * <br/>
 * ISBN:0521880688 9780521880688
 * <br/>
 * [<a href="http://www.nr.com/">http://www.nr.com/</a>].
 * <p/>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 1.1 &mdash; <em>$Date$</em>
 */
public class XORShiftRandom extends Random {
	private static final long serialVersionUID = 1L;

	public final static ThreadLocal<XORShiftRandom>
	INSTANCE = new ThreadLocal<XORShiftRandom>() {
		@Override protected XORShiftRandom initialValue() {
			return new XORShiftRandom();
		}
	};

	private long _x;

	private final Rand _rand = new Rand();

	public XORShiftRandom() {
		this(System.nanoTime());
	}

	public XORShiftRandom(final long seed) {
		_x = seed == 0 ? 0xdeadbeef : seed;
	}

	public static ThreadLocal<XORShiftRandom> newThreadLocal(final long seed) {
		return new ThreadLocal<XORShiftRandom>() {
			@Override protected XORShiftRandom initialValue() {
				return new XORShiftRandom(seed);
			}
		};
	}

	@Override
	public long nextLong() {
//		The other suggested shift values are:
//			21, 35, 4
//			20, 41, 5
//			17, 31, 8
//			11, 29, 14
//			14, 29, 11
//			30, 35, 13
//			21, 37, 4
//			21, 43, 4
//			23, 41, 18

		_x ^= (_x << 21);
		_x ^= (_x >>> 35);
		_x ^= (_x << 4);
		return _x;
		//return _rand.nextLong();
	}

	@Override
	protected int next(final int bits) {
		return (int)(nextLong() >>> (64 - bits));
		//return _rand.next(bits);
	}

	private static final class Rand {
		private long _x;

		public Rand() {
			init(System.nanoTime());
		}

		void init(final long seed) {
			_x = seed == 0 ? 0xdeadbeef : seed;
		}

		long nextLong() {
//			The other suggested shift values are:
//				21, 35, 4
//				20, 41, 5
//				17, 31, 8
//				11, 29, 14
//				14, 29, 11
//				30, 35, 13
//				21, 37, 4
//				21, 43, 4
//				23, 41, 18

			_x ^= (_x << 21);
			_x ^= (_x >>> 35);
			_x ^= (_x << 4);
			return _x;
		}

		int next(final int bits) {
			return (int)(nextLong() >>> (64 - bits));
		}

	}

}







