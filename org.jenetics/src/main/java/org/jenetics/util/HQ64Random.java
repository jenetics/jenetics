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


/**
 * This is the implementation of the <i>highest quality recommended generator,</i>
 * suggested in
 *
 * <p align="left">
 * <strong>Numerical Recipes 3rd Edition: The Art of Scientific Computing</strong>
 * <br/>
 * <em>Chapter 7. Random Numbers, Section 7.1, Page 342</em>
 * <br/>
 * <small>Cambridge University Press New York, NY, USA ©2007</small>
 * <br/>
 * ISBN:0521880688 9780521880688
 * <br/>
 * [<a href="http://www.nr.com/">http://www.nr.com/</a>].
 * <p/>
 *
 * The period of the generator is &asymp;3.138&sdot;10<sup>57</sup>.
 *
 * <p><b>
 * The <i>main</i> class of this PRNG is not thread safe. To create an thread
 * safe instances of this PRNG, use the {@link HQ64Random.ThreadSafe} class.
 * </b></p>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 1.1 &mdash; <em>$Date$</em>
 */
class HQ64Random extends Random64 {

	private static final long serialVersionUID = 1L;

	/**
	 * This field can be used to initial the {@link RandomRegistry} with a fast
	 * and thread safe random engine of this type; each thread gets a <i>local</i>
	 * copy of the {@code HQ64Random} engine.
	 *
	 * [code]
	 * RandomRegistry.setRandom(HQ64Random.INSTANCE);
	 * [/code]
	 *
	 * Calling the {@link HQ64Random#setSeed(long)} method on the returned
	 * instance will throw an {@link UnsupportedOperationException}.
	 */
	public static final ThreadLocal<HQ64Random>
	INSTANCE = new ThreadLocal<HQ64Random>() {
		@Override
		protected HQ64Random initialValue() {
			return new HQ64Random() {
				private static final long serialVersionUID = 1L;
				@Override
				public void setSeed(final long seed) {
					throw new UnsupportedOperationException();
				}
			};
		}
	};

	private long _u = 0L;
	private long _v = 0L;
	private long _w = 0L;


	public HQ64Random() {
		this(System.nanoTime());
	}

	public HQ64Random(final long seed) {
		init(seed);
	}

	void init(final long seed) {
		_v = 4101842887655102017L;
		_w = 1;
		_u = seed^_v;
		_w  = 1L;
		nextLong();
		_v = _u;
		nextLong();
		_w = _v;
		nextLong();
	}

	@Override
	public long nextLong() {
		_u = _u*2862933555777941757L + 7046029254386353087L;
		_v ^= _v >> 17;
		_v ^= _v << 31;
		_v ^= _v >> 8;
		_w = 0xFFFFDA61*(_w & 0xFFFFFFFF) + (_w >> 32);

		long x = _u^(_u << 21);
		x ^= x >> 35;
		x ^= x << 4;

		return (x + _v) ^ _w;
	}

	@Override
	public void setSeed(final long seed) {
		init(seed);
	}

	/**
	 * This class is a <i>thread safe</i> version of the {@code HQ64Random}
	 * engine. Instances of <i>this</i> class and instances of the non-thread
	 * safe variants, with the same seed, will generate the same sequence of
	 * random numbers.
	 * [code]
	 * final HQ64Random a = new HQ64Random(123);
	 * final HQ64Random b = HQ64Random.ThreadSafe(123);
	 * for (int i = 0; i < 1000;  ++i) {
	 *     assert (a.nextLong() == b.nextLong());
	 *     assert (a.nextDouble() == b.nextDouble());
	 * }
	 * [/code]
	 */
	public static final class ThreadSafe extends HQ64Random {
		private static final long serialVersionUID = 1L;

		/**
		 * Create a new <i>thread safe</i> instance of the HQ64Random PRNG, with
		 * an seed of {@link System#nanoTime()}.
		 */
		public ThreadSafe() {
		}

		/**
		 * Create a new <i>thread safe</i> instance of the HQ64Random PRNG.
		 *
		 * @param seed the seed of the PRNG.
		 */
		public ThreadSafe(final long seed) {
			super.init(seed);
		}

		@Override
		synchronized void init(final long seed) {
			super.init(seed);
		}

		@Override
		public synchronized long nextLong() {
			return super.nextLong();
		}

	}

}


