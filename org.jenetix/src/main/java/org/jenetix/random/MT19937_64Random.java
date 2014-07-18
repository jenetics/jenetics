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

	private final State _state = new State();

	/**
	 * Create a new random engine with the given seed.
	 *
	 * @param seed the seed of the random engine
	 */
	public MT19937_64Random(final long seed) {
		_state.setSeed(seed);
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
		x ^= (x >>> 43);

		return x;
	}

	@Override
	public void setSeed(final long seed) {
		Optional.ofNullable(_state).ifPresent(s -> s.setSeed(seed));
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
# Testing: org.jenetix.random.MT19937_64Random (2014-07-18 20:56)             #
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
stdin_input_raw|  3.13e+07  |4049656981|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.29207700|  PASSED
      diehard_operm5|   0|   1000000|     100|0.01967610|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.87065793|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.65028466|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.30431967|  PASSED
        diehard_opso|   0|   2097152|     100|0.82169385|  PASSED
        diehard_oqso|   0|   2097152|     100|0.06141533|  PASSED
         diehard_dna|   0|   2097152|     100|0.01324068|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.96648848|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.93894143|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.95757392|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.59263603|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.52850949|  PASSED
     diehard_squeeze|   0|    100000|     100|0.93680472|  PASSED
        diehard_sums|   0|       100|     100|0.40337667|  PASSED
        diehard_runs|   0|    100000|     100|0.71175874|  PASSED
        diehard_runs|   0|    100000|     100|0.88450232|  PASSED
       diehard_craps|   0|    200000|     100|0.67334333|  PASSED
       diehard_craps|   0|    200000|     100|0.59673019|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.68265061|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.31309961|  PASSED
         sts_monobit|   1|    100000|     100|0.47710402|  PASSED
            sts_runs|   2|    100000|     100|0.65430425|  PASSED
          sts_serial|   1|    100000|     100|0.41144888|  PASSED
          sts_serial|   2|    100000|     100|0.55799378|  PASSED
          sts_serial|   3|    100000|     100|0.46211662|  PASSED
          sts_serial|   3|    100000|     100|0.66923448|  PASSED
          sts_serial|   4|    100000|     100|0.24725084|  PASSED
          sts_serial|   4|    100000|     100|0.12046109|  PASSED
          sts_serial|   5|    100000|     100|0.74397256|  PASSED
          sts_serial|   5|    100000|     100|0.37814438|  PASSED
          sts_serial|   6|    100000|     100|0.40554678|  PASSED
          sts_serial|   6|    100000|     100|0.31293186|  PASSED
          sts_serial|   7|    100000|     100|0.11316009|  PASSED
          sts_serial|   7|    100000|     100|0.09952518|  PASSED
          sts_serial|   8|    100000|     100|0.91404077|  PASSED
          sts_serial|   8|    100000|     100|0.96982657|  PASSED
          sts_serial|   9|    100000|     100|0.68191490|  PASSED
          sts_serial|   9|    100000|     100|0.81693793|  PASSED
          sts_serial|  10|    100000|     100|0.46699695|  PASSED
          sts_serial|  10|    100000|     100|0.43510717|  PASSED
          sts_serial|  11|    100000|     100|0.37493613|  PASSED
          sts_serial|  11|    100000|     100|0.46121446|  PASSED
          sts_serial|  12|    100000|     100|0.89156498|  PASSED
          sts_serial|  12|    100000|     100|0.82360819|  PASSED
          sts_serial|  13|    100000|     100|0.86598199|  PASSED
          sts_serial|  13|    100000|     100|0.39449828|  PASSED
          sts_serial|  14|    100000|     100|0.32993948|  PASSED
          sts_serial|  14|    100000|     100|0.85896854|  PASSED
          sts_serial|  15|    100000|     100|0.98018322|  PASSED
          sts_serial|  15|    100000|     100|0.84382395|  PASSED
          sts_serial|  16|    100000|     100|0.22292745|  PASSED
          sts_serial|  16|    100000|     100|0.39703022|  PASSED
         rgb_bitdist|   1|    100000|     100|0.12406761|  PASSED
         rgb_bitdist|   2|    100000|     100|0.46812139|  PASSED
         rgb_bitdist|   3|    100000|     100|0.06638831|  PASSED
         rgb_bitdist|   4|    100000|     100|0.69525011|  PASSED
         rgb_bitdist|   5|    100000|     100|0.34681625|  PASSED
         rgb_bitdist|   6|    100000|     100|0.00542212|  PASSED
         rgb_bitdist|   7|    100000|     100|0.93230763|  PASSED
         rgb_bitdist|   8|    100000|     100|0.38710663|  PASSED
         rgb_bitdist|   9|    100000|     100|0.94584886|  PASSED
         rgb_bitdist|  10|    100000|     100|0.77454323|  PASSED
         rgb_bitdist|  11|    100000|     100|0.01512572|  PASSED
         rgb_bitdist|  12|    100000|     100|0.95406086|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.43570991|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.50477708|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.51323462|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.16440934|  PASSED
    rgb_permutations|   2|    100000|     100|0.37674653|  PASSED
    rgb_permutations|   3|    100000|     100|0.15179989|  PASSED
    rgb_permutations|   4|    100000|     100|0.98475614|  PASSED
    rgb_permutations|   5|    100000|     100|0.99731176|   WEAK
      rgb_lagged_sum|   0|   1000000|     100|0.96870160|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.50978428|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.78675165|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.62513871|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.39605302|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.61767239|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.66224416|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.65393303|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.95839581|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.45540316|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.61471652|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.64208244|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.99688452|   WEAK
      rgb_lagged_sum|  13|   1000000|     100|0.38489759|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.86283090|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.05624942|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.05295860|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.79360553|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.75628031|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.90202390|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.04035960|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.59157363|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.72116878|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.51710419|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.27372007|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.91747456|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.84830620|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.70534472|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.49578946|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.32271148|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.75571563|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.31068520|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.99181317|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.74010231|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.67468357|  PASSED
             dab_dct| 256|     50000|       1|0.94289679|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.15206642|  PASSED
        dab_filltree|  32|  15000000|       1|0.78987473|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.01118046|  PASSED
       dab_filltree2|   1|   5000000|       1|0.65643782|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.12912322|  PASSED
#=============================================================================#
# Runtime: 0:42:56                                                            #
#=============================================================================#
*/
