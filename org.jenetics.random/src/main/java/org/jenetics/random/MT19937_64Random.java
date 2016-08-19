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
package org.jenetics.random;

import static org.jenetics.random.internal.util.Equality.eq;

import java.io.Serializable;

import org.jenetics.random.internal.util.Equality;
import org.jenetics.random.internal.util.Hash;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public class MT19937_64Random extends Random64 {

	private static final long serialVersionUID = 1L;

	private static final int N = 312;
	private static final int M = 156;

	private static final long UM = 0xFFFFFFFF80000000L; // most significant bit
	private static final long LM = 0x7FFFFFFFL;         // least significant 31 bits

	private static final class State implements Serializable {
		private static final long serialVersionUID = 1L;

		int mti;
		final long[] mt = new long[N];

		State(final long seed) {
			setSeed(seed);
		}

		void setSeed(final long s) {
			mt[0] = s;
			for (mti = 1; mti < N; ++mti) {
				mt[mti] = 6364136223846793005L*
					(mt[mti - 1]^(mt[mti - 1] >>> 62)) +
					mti;
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

	private final State _state;

	/**
	 * Create a new random engine with the given seed.
	 *
	 * @param seed the seed of the random engine
	 */
	public MT19937_64Random(final long seed) {
		_state = new State(seed);
	}

	/**
	 * Return a new random engine with a safe seed value.
	 */
	public MT19937_64Random() {
		this(math.seed());
	}

	@Override
	public long nextLong() {
		long x;
		final long[] mag01 = {0L, 0xB5026F5AA96619E9L};

		if (_state.mti >= N) { // generate N words at one time
			int i;
			for (i = 0; i < N - M; ++i) {
				x =(_state.mt[i] & UM) | (_state.mt[i + 1] & LM);
				_state.mt[i] = _state.mt[i + M]^(x >>> 1)^mag01[(int)(x & 1L)];
			}
			for (; i < N - 1; ++i) {
				x = (_state.mt[i] & UM) | (_state.mt[i + 1] & LM);
				_state.mt[i]= _state.mt[i + (M - N)]^(x >>> 1)^mag01[(int)(x & 1)];
			}

			x = (_state.mt[N - 1] & UM) | (_state.mt[0] & LM);
			_state.mt[N - 1] = _state.mt[M - 1]^(x >>> 1)^mag01[(int)(x & 1)];
			_state.mti = 0;
		}

		x = _state.mt[_state.mti++];
		x ^= (x >>> 29) & 0x5555555555555555L;
		x ^= (x << 17) & 0x71D67FFFEDA60000L;
		x ^= (x << 37) & 0xFFF7EEE000000000L;
		x ^= x >>> 43;

		return x;
	}

	@Override
	public void setSeed(final long seed) {
		if (_state != null) _state.setSeed(seed);
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(_state).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(random -> eq(_state, random._state));
	}

}

/*
#=============================================================================#
# Testing: org.jenetix.random.MT19937_64Random (2014-07-28 05:11)             #
#=============================================================================#
#=============================================================================#
# Linux 3.13.0-32-generic (amd64)                                             #
# java version "1.8.0_11"                                                     #
# Java(TM) SE Runtime Environment (build 1.8.0_11-b12)                        #
# Java HotSpot(TM) 64-Bit Server VM (build 25.11-b03)                         #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  3.74e+07  |2874551973|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.87085878|  PASSED
      diehard_operm5|   0|   1000000|     100|0.22533754|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.03116562|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.62780367|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.88787293|  PASSED
        diehard_opso|   0|   2097152|     100|0.96086604|  PASSED
        diehard_oqso|   0|   2097152|     100|0.97213312|  PASSED
         diehard_dna|   0|   2097152|     100|0.19993110|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.96004960|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.78368440|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.92463763|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.91819498|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.99758519|   WEAK
     diehard_squeeze|   0|    100000|     100|0.95701102|  PASSED
        diehard_sums|   0|       100|     100|0.55370944|  PASSED
        diehard_runs|   0|    100000|     100|0.96372857|  PASSED
        diehard_runs|   0|    100000|     100|0.06207775|  PASSED
       diehard_craps|   0|    200000|     100|0.98521200|  PASSED
       diehard_craps|   0|    200000|     100|0.13126186|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.50477740|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.73751106|  PASSED
         sts_monobit|   1|    100000|     100|0.11088090|  PASSED
            sts_runs|   2|    100000|     100|0.98786721|  PASSED
          sts_serial|   1|    100000|     100|0.26554696|  PASSED
          sts_serial|   2|    100000|     100|0.17428962|  PASSED
          sts_serial|   3|    100000|     100|0.24182847|  PASSED
          sts_serial|   3|    100000|     100|0.44952064|  PASSED
          sts_serial|   4|    100000|     100|0.16674742|  PASSED
          sts_serial|   4|    100000|     100|0.53939037|  PASSED
          sts_serial|   5|    100000|     100|0.17776906|  PASSED
          sts_serial|   5|    100000|     100|0.66525775|  PASSED
          sts_serial|   6|    100000|     100|0.30853091|  PASSED
          sts_serial|   6|    100000|     100|0.85798275|  PASSED
          sts_serial|   7|    100000|     100|0.66889183|  PASSED
          sts_serial|   7|    100000|     100|0.44599439|  PASSED
          sts_serial|   8|    100000|     100|0.67099544|  PASSED
          sts_serial|   8|    100000|     100|0.76554000|  PASSED
          sts_serial|   9|    100000|     100|0.08884788|  PASSED
          sts_serial|   9|    100000|     100|0.37702274|  PASSED
          sts_serial|  10|    100000|     100|0.18436686|  PASSED
          sts_serial|  10|    100000|     100|0.68478879|  PASSED
          sts_serial|  11|    100000|     100|0.05915646|  PASSED
          sts_serial|  11|    100000|     100|0.47862978|  PASSED
          sts_serial|  12|    100000|     100|0.29696937|  PASSED
          sts_serial|  12|    100000|     100|0.86825509|  PASSED
          sts_serial|  13|    100000|     100|0.14201133|  PASSED
          sts_serial|  13|    100000|     100|0.08672329|  PASSED
          sts_serial|  14|    100000|     100|0.30190865|  PASSED
          sts_serial|  14|    100000|     100|0.85855948|  PASSED
          sts_serial|  15|    100000|     100|0.25938415|  PASSED
          sts_serial|  15|    100000|     100|0.65676588|  PASSED
          sts_serial|  16|    100000|     100|0.87425179|  PASSED
          sts_serial|  16|    100000|     100|0.86107469|  PASSED
         rgb_bitdist|   1|    100000|     100|0.16904664|  PASSED
         rgb_bitdist|   2|    100000|     100|0.87335475|  PASSED
         rgb_bitdist|   3|    100000|     100|0.78426092|  PASSED
         rgb_bitdist|   4|    100000|     100|0.05709253|  PASSED
         rgb_bitdist|   5|    100000|     100|0.52526716|  PASSED
         rgb_bitdist|   6|    100000|     100|0.15017838|  PASSED
         rgb_bitdist|   7|    100000|     100|0.38085691|  PASSED
         rgb_bitdist|   8|    100000|     100|0.60881232|  PASSED
         rgb_bitdist|   9|    100000|     100|0.29129503|  PASSED
         rgb_bitdist|  10|    100000|     100|0.01177788|  PASSED
         rgb_bitdist|  11|    100000|     100|0.13961581|  PASSED
         rgb_bitdist|  12|    100000|     100|0.98625191|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.86830236|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.13136545|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.31324029|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.21104797|  PASSED
    rgb_permutations|   2|    100000|     100|0.46933297|  PASSED
    rgb_permutations|   3|    100000|     100|0.98947183|  PASSED
    rgb_permutations|   4|    100000|     100|0.86417315|  PASSED
    rgb_permutations|   5|    100000|     100|0.15551016|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.59984722|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.42922978|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.22062436|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.59937130|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.90373509|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.43866997|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.66324708|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.93231932|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.23504253|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.61005830|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.58202778|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.99516156|   WEAK
      rgb_lagged_sum|  12|   1000000|     100|0.69536350|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.06679205|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.50724111|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.81476201|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.80407194|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.75766885|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.96110158|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.27042099|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.06250758|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.99212082|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.96728620|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.18105822|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.82189981|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.97581441|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.87389774|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.63407565|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.50021342|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.95361939|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.42225262|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.03279465|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.77412154|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.23983462|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.43149805|  PASSED
             dab_dct| 256|     50000|       1|0.57205638|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.95647544|  PASSED
        dab_filltree|  32|  15000000|       1|0.54407484|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.20911503|  PASSED
       dab_filltree2|   1|   5000000|       1|0.01773879|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.98227774|  PASSED
#=============================================================================#
# Summary: PASSED=112, WEAK=2, FAILED=0                                       #
#          235,030.906 MB of random data created with 103.825 MB/sec          #
#=============================================================================#
#=============================================================================#
# Runtime: 0:37:43                                                            #
#=============================================================================#
*/
