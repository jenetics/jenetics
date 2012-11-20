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
import java.util.concurrent.atomic.AtomicLong;


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
 * <em>Chapter 7. Random Numbers, Section 7.1.2, Page 345</em>
 * <br/>
 * <small>Cambridge University Press New York, NY, USA ©2007</small>
 * <br/>
 * ISBN:0521880688 9780521880688
 * <br/>
 * [<a href="http://www.nr.com/">http://www.nr.com/</a>].
 * <p/>
 *
 * <p><b>
 * The <i>main</i> class of this PRNG is not thread safe. To create an thread 
 * safe instances of this PRNG, use the {@link XORShiftRandom.ThreadSafe} class.
 * </b></p>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 1.1 &mdash; <em>$Date: 2012-11-20 $</em>
 */
public class XORShiftRandom extends Random {
	private static final long serialVersionUID = 1L;


	/**
	 * This field can be used to initial the {@link RandomRegistry} with a fast
	 * and thread safe random engine of this type; each thread gets a <i>local</i>
	 * copy of the {@code XORShiftRandom} engine.
	 *
	 * [code]
	 * RandomRegistry.setRandom(XORShiftRandom.INSTANCE);
	 * [/code]
	 *
	 * Calling the {@link XORShiftRandom#setSeed(long)} method on the returned
	 * instance will throw an {@link UnsupportedOperationException}.
	 */
	public static final ThreadLocal<XORShiftRandom>
	INSTANCE = new ThreadLocal<XORShiftRandom>() {
		@Override protected XORShiftRandom initialValue() {
			return new XORShiftRandom() {
				private static final long serialVersionUID = 1L;
				@Override
				public void setSeed(final long seed) {
					throw new UnsupportedOperationException();
				}
			};
		}
	};

	private long _x;

	/**
	 * Create a new <i>non-thread safe</i> instance of the XOR-Shift PRNG, with
	 * an seed of {@link System#nanoTime()}.
	 */
	public XORShiftRandom() {
		this(seed());
	}

	/**
	 * Create a new <i>non-thread safe</i> instance of the XOR-Shift PRNG.
	 *
	 * @param seed the seed of the PRNG.
	 */
	public XORShiftRandom(final long seed) {
		_x = init(seed);
	}

	private static long init(final long seed) {
		return seed == 0 ? 0xdeadbeef : seed;
	}
	
	private static long seed() {
		return System.nanoTime();
	}

	@Override
	public long nextLong() {
//		The list of the suggested shift values:
//		21, 35, 4
//		20, 41, 5
//		17, 31, 8
//		11, 29, 14
//		14, 29, 11
//		30, 35, 13
//		21, 37, 4
//		21, 43, 4
//		23, 41, 18

		_x ^= (_x << 21);
		_x ^= (_x >>> 35);
		_x ^= (_x << 4);
		return _x;
	}

	@Override
	protected int next(final int bits) {
		return (int)(nextLong() >>> (64 - bits));
	}

	@Override
	public void setSeed(final long seed) {
		_x = init(seed);
	}

	/**
	 * Return the current seed value.
	 *
	 * @return the current seed value.
	 */
	public long getSeed() {
		return _x;
	}

	@Override
	public String toString() {
		return String.format("%s[%d]", getClass().getName(), _x);
	}

	/**
	 * This class is a <i>thread safe</i> version of the {@code XORShiftRandom}
	 * engine. Instances of <i>this</i> class and instances of the non-thread
	 * safe variants, with the same seed, will generate the same sequence of
	 * random numbers.
	 * [code]
	 * final XORShiftRandom a = new XORShiftRandom(123);
	 * final XORShiftRandom b = XORShiftRandom.ThreadSafe(123);
	 * for (int i = 0; i < 1000;  ++i) {
	 *     assert(a.nextLong() == b.nextLong());
	 *     assert(a.nextDouble() == b.nextDouble());
	 * }
	 * [/code]
	 */
	public static final class ThreadSafe extends XORShiftRandom {
		private static final long serialVersionUID = 1L;

		private final AtomicLong _x = new AtomicLong();

		/**
		 * Create a new <i>thread safe</i> instance of the XOR-Shift PRNG, with
		 * an seed of {@link System#nanoTime()}.
		 */
		public ThreadSafe() {
			this(seed());
		}

		/**
		 * Create a new <i>thread safe</i> instance of the XOR-Shift PRNG.
		 *
		 * @param seed the seed of the PRNG.
		 */
		public ThreadSafe(final long seed) {
			_x.set(init(seed));
		}

		@Override
		public final long nextLong() {
			long oldseed;
			long x;

			do {
				oldseed = _x.get();
				x = oldseed;

				x ^= (x << 21);
				x ^= (x >>> 35);
				x ^= (x << 4);
			} while (!_x.compareAndSet(oldseed, x));

			return x;
		}

		@Override
		public long getSeed() {
			return _x.get();
		}

	};

}

