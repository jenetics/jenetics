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

import static java.lang.String.format;
import static org.jenetics.internal.util.Equality.eq;
import static org.jenetics.random.utils.highInt;
import static org.jenetics.random.utils.lowInt;
import static org.jenetics.random.utils.mix;

import java.io.Serializable;

import org.jenetics.internal.math.random;
import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.util.Random32;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__! &mdash; <em>$Date: 2014-12-29 $</em>
 */
public class KISS32Random extends Random32 {

	private static final long serialVersionUID = 1L;

	/**
	 * The state of this random engine.
	 */
	private static final class State implements Serializable {
		private static final long serialVersionUID = 1L;

		int _x = 123456789;
		int _y = 234567891;
		int _z = 345678912;
		int _w = 456789123;
		int _c = 0;

		State(final long seed) {
			setSeed(seed);
		}

		void setSeed(final long seed) {
			final long a = seed;
			final long b = mix(seed);

			_x = highInt(a);
			_y = lowInt(a);
			_z = highInt(b);
			_w = lowInt(b);
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(_x)
				.and(_y)
				.and(_z)
				.and(_w)
				.and(_c).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(state ->
				eq(_x, state._x) &&
				eq(_y, state._y) &&
				eq(_z, state._z) &&
				eq(_w, state._w) &&
				eq(_c, state._c)
			);
		}

		@Override
		public String toString() {
			return format("State[%d, %d, %d, %d, %d]", _x, _y, _z, _w, _c);
		}

	}

	private final State _state;

	public KISS32Random(final long seed) {
		_state = new State(seed);
	}

	public KISS32Random() {
		this(random.seed());
	}

	@Override
	public int nextInt() {
		step();
		return _state._x + _state._y + _state._w;
	}

	private void step() {
		_state._y ^= _state._y << 5;
		_state._y ^= _state._y >>> 7;
		_state._y ^= _state._y << 22;

		int t = _state._z + _state._w + _state._c;
		_state._z = _state._w;
		_state._c = t >>> 31;
		_state._w = t&2147483647;
		_state._x += 1411392427;
	}

	@Override
	public void setSeed(final long seed) {
		if (_state != null) _state.setSeed(seed);
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_state).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(random ->
			eq(_state, random._state)
		);
	}

	@Override
	public String toString() {
		return format("%s[%s]", getClass().getSimpleName(), _state);
	}

}

/*
#=============================================================================#
# Testing: org.jenetix.random.KISS32Random (2014-07-28 00:21)                 #
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
stdin_input_raw|  3.06e+07  | 102492718|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.99939659|   WEAK
      diehard_operm5|   0|   1000000|     100|0.36357211|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.20066861|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.25654616|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.48134087|  PASSED
        diehard_opso|   0|   2097152|     100|0.99315743|  PASSED
        diehard_oqso|   0|   2097152|     100|0.76886381|  PASSED
         diehard_dna|   0|   2097152|     100|0.19390323|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.45890487|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.97880650|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.35982598|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.78684317|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.94798517|  PASSED
     diehard_squeeze|   0|    100000|     100|0.80196034|  PASSED
        diehard_sums|   0|       100|     100|0.01426363|  PASSED
        diehard_runs|   0|    100000|     100|0.80304014|  PASSED
        diehard_runs|   0|    100000|     100|0.44129395|  PASSED
       diehard_craps|   0|    200000|     100|0.99936524|   WEAK
       diehard_craps|   0|    200000|     100|0.93781551|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.26919300|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.91695620|  PASSED
         sts_monobit|   1|    100000|     100|0.84827454|  PASSED
            sts_runs|   2|    100000|     100|0.00055785|   WEAK
          sts_serial|   1|    100000|     100|0.72906645|  PASSED
          sts_serial|   2|    100000|     100|0.57795959|  PASSED
          sts_serial|   3|    100000|     100|0.51231517|  PASSED
          sts_serial|   3|    100000|     100|0.83389127|  PASSED
          sts_serial|   4|    100000|     100|0.05131641|  PASSED
          sts_serial|   4|    100000|     100|0.70252125|  PASSED
          sts_serial|   5|    100000|     100|0.89198673|  PASSED
          sts_serial|   5|    100000|     100|0.81467276|  PASSED
          sts_serial|   6|    100000|     100|0.90533039|  PASSED
          sts_serial|   6|    100000|     100|0.61801445|  PASSED
          sts_serial|   7|    100000|     100|0.96522905|  PASSED
          sts_serial|   7|    100000|     100|0.41467449|  PASSED
          sts_serial|   8|    100000|     100|0.53814522|  PASSED
          sts_serial|   8|    100000|     100|0.60403200|  PASSED
          sts_serial|   9|    100000|     100|0.47515278|  PASSED
          sts_serial|   9|    100000|     100|0.53617419|  PASSED
          sts_serial|  10|    100000|     100|0.70364611|  PASSED
          sts_serial|  10|    100000|     100|0.51838526|  PASSED
          sts_serial|  11|    100000|     100|0.99701740|   WEAK
          sts_serial|  11|    100000|     100|0.99556411|   WEAK
          sts_serial|  12|    100000|     100|0.15335905|  PASSED
          sts_serial|  12|    100000|     100|0.83859162|  PASSED
          sts_serial|  13|    100000|     100|0.98927299|  PASSED
          sts_serial|  13|    100000|     100|0.38362108|  PASSED
          sts_serial|  14|    100000|     100|0.66153977|  PASSED
          sts_serial|  14|    100000|     100|0.43146402|  PASSED
          sts_serial|  15|    100000|     100|0.03242195|  PASSED
          sts_serial|  15|    100000|     100|0.66165514|  PASSED
          sts_serial|  16|    100000|     100|0.21237687|  PASSED
          sts_serial|  16|    100000|     100|0.67182818|  PASSED
         rgb_bitdist|   1|    100000|     100|0.65025159|  PASSED
         rgb_bitdist|   2|    100000|     100|0.29738727|  PASSED
         rgb_bitdist|   3|    100000|     100|0.94270837|  PASSED
         rgb_bitdist|   4|    100000|     100|0.40396724|  PASSED
         rgb_bitdist|   5|    100000|     100|0.84206688|  PASSED
         rgb_bitdist|   6|    100000|     100|0.86095816|  PASSED
         rgb_bitdist|   7|    100000|     100|0.91056625|  PASSED
         rgb_bitdist|   8|    100000|     100|0.97368059|  PASSED
         rgb_bitdist|   9|    100000|     100|0.37741682|  PASSED
         rgb_bitdist|  10|    100000|     100|0.06222656|  PASSED
         rgb_bitdist|  11|    100000|     100|0.06778765|  PASSED
         rgb_bitdist|  12|    100000|     100|0.41314116|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.51281853|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.47456344|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.33807891|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.96554079|  PASSED
    rgb_permutations|   2|    100000|     100|0.23780281|  PASSED
    rgb_permutations|   3|    100000|     100|0.96853873|  PASSED
    rgb_permutations|   4|    100000|     100|0.00232189|   WEAK
    rgb_permutations|   5|    100000|     100|0.87983199|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.19355547|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.65722250|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.80831658|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.96746080|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.63793927|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.68408851|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.86620206|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.26090034|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.26335846|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.31617848|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.16269591|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.90001789|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.37123494|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.80973948|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.42748513|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.88466801|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.06321064|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.90030757|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.81364974|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.77328185|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.13453011|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.77038698|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.25739589|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.96789254|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.43708199|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.08056126|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.28402956|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.81532213|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.60231810|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.78743744|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.16191833|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.34749903|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.52671825|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.08441727|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.68867026|  PASSED
             dab_dct| 256|     50000|       1|0.46207672|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.33090136|  PASSED
        dab_filltree|  32|  15000000|       1|0.92610459|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.45459949|  PASSED
       dab_filltree2|   1|   5000000|       1|0.41466327|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.55913704|  PASSED
#=============================================================================#
# Summary: PASSED=108, WEAK=6, FAILED=0                                       #
#          235,031.078 MB of random data created with 103.398 MB/sec          #
#=============================================================================#
#=============================================================================#
# Runtime: 0:37:53                                                            #
#=============================================================================#
*/
