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

import org.jenetics.util.Random32;
import org.jenetics.util.math;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__! &mdash; <em>$Date: 2014-07-17 $</em>
 */
public class MT19937_32Random extends Random32 {

	private static final long serialVersionUID = 1L;

	private static final int N = 624;
	private static final int M = 397;

	private static final int UM = -2147483648; // most significant bit
	private static final int LM = 2147483647; // least significant 31 bits

	/**
	 * The status class of the random engine.
	 */
	private static final class Status {
		int mti = 0;
		int[] mt = new int[N];

		Status(final long seed) {
			setSeed(seed);
		}

		Status() {
			this(math.random.seed());
		}

		void setSeed(final long seed) {
			mt[0] = (int)seed;
			for (mti = 1; mti < N; ++mti) {
				mt[mti] = (1812433253*(mt[mti - 1]^(mt[mti - 1] >>> 30)) + mti);
			}
		}
	}

	private final Status status = new Status();

	public MT19937_32Random(final long seed) {
		status.setSeed(seed);
	}

	public MT19937_32Random() {
		this(math.random.seed());
	}

	@Override
	public int nextInt() {
		int x;
		final int[] mag01 = {0, -1727483681};

		// Generate N words at one time.
		if (status.mti >= N) {
			int i = 0;
			for (i = 0; i < N - M; ++i) {
				x = (status.mt[i] & UM) | (status.mt[i + 1] & LM);
				status.mt[i] = status.mt[i + M]^(x >>> 1)^mag01[x & 1];
			}
			for (; i < N - 1; ++i) {
				x = (status.mt[i] & UM) | (status.mt[i + 1] & LM);
				status.mt[i] = status.mt[i + (M - N)]^(x >>> 1)^mag01[x & 1];
			}

			x = (status.mt[N - 1] & UM)|(status.mt[0] & LM);
			status.mt[N - 1] = status.mt[M - 1]^(x >>> 1) ^ mag01[x & 1];
			status.mti = 0;
		}

		x = status.mt[status.mti++];
		x ^= (x >>> 11);
		x ^= (x << 7) & -1658038656;
		x ^= (x << 15) & -272236544;
		x ^= (x >>> 18);

		return x;
	}

	@Override
	public void setSeed(final long seed) {
		if (status != null) {
			status.setSeed(seed);
		}
	}

	public static void main(final String[] args) {
		final MT19937_32Random random = new MT19937_32Random(0);
		//random.setSeed(0);

		for (int i = 0; i < 10; ++i) {
			System.out.println(random.nextInt());
		}
	}

}
