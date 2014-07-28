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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetix.random;

import static org.jenetix.random.ints.mix;

import java.io.Serializable;

import org.jenetics.util.Random64;
import org.jenetics.util.math;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__! &mdash; <em>$Date: 2014-07-28 $</em>
 */
public class KISS64Random extends Random64 {

	private static final long serialVersionUID = 1L;

	private static final class State implements Serializable {
		private static final long serialVersionUID = 1L;

		long _x;
		long _y;
		int _z1;
		int _c1;
		int _z2;
		int _c2;

		State(
			final long x,
			final long y,
			final int z1,
			final int c1,
			final int z2,
			final int c2
		) {
			_x = x;
			_y = y == 0 ? 0xdeadbeef : y;
			_z1 = z1;
			_c1 = c1;
			_z2 = z2;
			_c2 = c2;
		}

		State(final long a, final long b, final long c, final long d) {
			this(
				a,
				b,
				(int)(c >>> Integer.SIZE),
				(int)c,
				(int)(d >>> Integer.SIZE),
				(int)d
			);
		}

		State(final long seed) {
			setSeed(seed);
		}

		void setSeed(final long seed) {
			final long a = seed;
			final long b = mix(seed);
			final long c = mix(b);
			final long d = mix(c);

			_x = a;
			_y = b == 0 ? 0xdeadbeef : b;
			_z1 = (int)(c >>> Integer.SIZE);
			_c1 = (int)c;
			_z2 = (int)(d >>> Integer.SIZE);
			_c2 = (int)d;
		}

		void step() {
			_x = 0x14ADA13ED78492ADL*_x + 123456789;

			_y ^= _y << 21;
			_y ^= _y >>> 17;
			_y ^= _y << 30;

			long t = 4294584393L*_z1 + _c1;
			_c1 = (int)(t >> 32);
			_z1 = (int)t;

			t = 4246477509L*_z2 + _c2;
			_c2 = (int)(t >> 32);
			_z2 = (int)t;
		}
	}

	private final State _state;

	public KISS64Random(
		final long seed1,
		final long seed2,
		final long seed3,
		final long seed4
	) {
		_state = new State(seed1, seed2, seed3, seed4);
	}

	public KISS64Random(final long seed) {
		_state = new State(seed);
	}

	public KISS64Random() {
		this(
			math.random.seed(),
			math.random.seed(),
			math.random.seed(),
			math.random.seed()
		);
	}

	@Override
	public long nextLong() {
		_state.step();
		return _state._x + _state._y + _state._z1 + ((long)_state._z2 << 32);
	}

	@Override
	public void setSeed(final long seed) {
		if (_state != null) _state.setSeed(seed);
	}

}
