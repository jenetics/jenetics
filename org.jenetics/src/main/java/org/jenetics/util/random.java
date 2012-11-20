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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version 1.1 &mdash; <em>$Date: 2012-11-20 $</em>
 */
class random {

	private random() {
		throw new AssertionError("Don't create an 'random' instance.");
	}

	public static void main(final String[] args) {
		final Random random = new org.jenetics.util.XORShiftRandom(12345);
		for (int i = 0; i < 5000; ++i) {
			System.out.println(random.nextLong());
		}
	}

	static final class NullLock implements Lock {
		@Override public void lock() {}
		@Override public void lockInterruptibly() {}
		@Override public boolean tryLock() {
			return false;
		}
		@Override public boolean tryLock(long time, TimeUnit unit) {
			return false;
		}
		@Override public void unlock() {}
		@Override public Condition newCondition() {
			throw new UnsupportedOperationException();
		}
	}

	public static final Lock NULL = new NullLock();


	static class XORShiftRandom extends Random {
		private static final long serialVersionUID = 1L;

		private final Lock _lock;

		private long _x;

		public XORShiftRandom(final Lock lock) {
			this(lock, System.nanoTime());
		}

		public XORShiftRandom(final Lock lock, final long seed) {
			_lock = lock;
			_x = seed == 0 ? 0xdeadbeef : seed;
		}

		@Override
		public long nextLong() {
			if (_lock != NULL) _lock.lock();
			try {
				_x ^= (_x << 21);
				_x ^= (_x >>> 35);
				_x ^= (_x << 4);
				return _x;
			} finally {
				if (_lock != NULL) _lock.unlock();
			}
		}

		@Override
		protected int next(final int bits) {
			return (int)(nextLong() >>> (64 - bits));
		}
	}

	static class AXORShiftRandom extends Random {
		private static final long serialVersionUID = 1L;


		private final AtomicLong _x = new AtomicLong();

		public AXORShiftRandom() {
			this(System.nanoTime());
		}

		public AXORShiftRandom(final long seed) {
			_x.set(seed == 0 ? 0xdeadbeef : seed);
		}

		@Override
		public long nextLong() {
			long oldseed;
			long nextseed;
			do {
				oldseed = _x.get();
				nextseed = oldseed;
				nextseed ^= (nextseed << 21);
				nextseed ^= (nextseed >>> 35);
				nextseed ^= (nextseed << 4);
			} while (!_x.compareAndSet(oldseed, nextseed));
			return nextseed;
		}

		@Override
		protected int next(final int bits) {
			return (int)(nextLong() >>> (64 - bits));
		}
	}


}
