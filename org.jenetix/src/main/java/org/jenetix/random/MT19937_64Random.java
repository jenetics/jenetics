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

import static org.jenetics.internal.util.Equality.eq;

import java.util.Optional;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.util.Random64;
import org.jenetics.util.math;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__! &mdash; <em>$Date: 2014-07-18 $</em>
 */
public class MT19937_64Random extends Random64 {

	private static final long serialVersionUID = 1L;

	private static final int N = 312;
	private static final int M = 156;

	private static final long UM = 0xFFFFFFFF80000000L; // most significant bit
	private static final long LM = 0x7FFFFFFFL;         // least significant 31 bits

	private static final class State {
		int mti;
		final long[] mt = new long[N];

		State(final long seed) {
			setSeed(seed);
		}

		State() {
			this(math.random.seed());
		}

		void setSeed(final long s) {
			mt[0] = s;
			for (mti = 1; mti < N; ++mti) {
				mt[mti] = (6364136223846793005L*(mt[mti - 1]^
							(mt[mti - 1] >>> 62)) + mti);
			}
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(mti)
				.and(mt).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(status ->
				eq(mti, status.mti) &&
				eq(mt, status.mt)
			);
		}

	}

	private final State state = new State();

	/**
	 * Create a new random engine with the given seed.
	 *
	 * @param seed the seed of the random engine
	 */
	public MT19937_64Random(final long seed) {
		state.setSeed(seed);
	}

	/**
	 * Return a new random engine with a safe seed value.
	 */
	public MT19937_64Random() {
		this(math.random.seed());
	}

	@Override
	public long nextLong() {
		long x;
		final long[] mag01 = {0L, 0xB5026F5AA96619E9L};

		if (state.mti >= N) { // generate N words at one time
			int i;
			for (i = 0; i < N - M; ++i) {
				x =(state.mt[i] & UM) | (state.mt[i + 1] & LM);
				state.mt[i] = state.mt[i + M]^(x >>> 1)^mag01[(int)(x & 1L)];
			}
			for (; i < N - 1; ++i) {
				x = (state.mt[i] & UM) | (state.mt[i + 1] & LM);
				state.mt[i]= state.mt[i + (M - N)]^(x >>> 1)^mag01[(int)(x & 1)];
			}

			x = (state.mt[N - 1] & UM) | (state.mt[0] & LM);
			state.mt[N - 1] = state.mt[M - 1]^(x >>> 1)^mag01[(int)(x & 1)];
			state.mti = 0;
		}

		x = state.mt[state.mti++];
		x ^= (x >>> 29) & 0x5555555555555555L;
		x ^= (x << 17) & 0x71D67FFFEDA60000L;
		x ^= (x << 37) & 0xFFF7EEE000000000L;
		x ^= (x >>> 43);

		return x;
	}

	@Override
	public void setSeed(final long seed) {
		Optional.ofNullable(state).ifPresent(s -> s.setSeed(seed));
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(state).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(random -> eq(state, random.state));
	}

}
