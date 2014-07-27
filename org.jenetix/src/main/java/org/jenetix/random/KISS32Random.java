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

import static java.lang.String.format;
import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.util.Random32;
import org.jenetics.util.math;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__! &mdash; <em>$Date: 2014-07-27 $</em>
 */
public class KISS32Random extends Random32 {

	private static final long serialVersionUID = 1L;

	/**
	 * The state of this random engine.
	 */
	private static final class State implements Serializable {
		private static final long serialVersionUID = 1L;

		int _x;
		int _y;
		int _z;
		int _w;
		int _c = 0;

		State(final int x, final int y, final int z, final int w) {
			_x = x;
			_y = y;
			_z = z;
			_w = w;
		}

		State(final long a, final long b) {
			_x = (int)(a >>> Integer.SIZE);
			_y = (int)a;
			_z = (int)(b >>> Integer.SIZE);
			_w = (int)b;
		}

		State(final long seed) {
			setSeed(seed);
		}

		void setSeed(final long seed) {
			final long a = seed;
			final long b = mix(seed);

			_x = (int)(a >>> Integer.SIZE);
			_y = (int)a;
			_z = (int)(b >>> Integer.SIZE);
			_w = (int)b;
		}

		private static long mix(final long a) {
			long c = a^Long.rotateLeft(a, 7);
			c ^= c << 17;
			c ^= c >>> 31;
			c ^= c << 8;
			return c;
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

	public KISS32Random(final long seed1, final long seed2) {
		_state = new State(seed1, seed2);
	}

	public KISS32Random(final long seed) {
		this(seed, State.mix(seed));
	}

	public KISS32Random() {
		this(math.random.seed(), math.random.seed());
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
# Testing: org.jenetix.random.KISS32Random (2014-07-27 21:51)                 #
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
stdin_input_raw|  3.51e+07  | 655266785|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.99190581|  PASSED
      diehard_operm5|   0|   1000000|     100|0.90497058|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.82882280|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.72222453|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.09853477|  PASSED
        diehard_opso|   0|   2097152|     100|0.41444847|  PASSED
        diehard_oqso|   0|   2097152|     100|0.06044595|  PASSED
         diehard_dna|   0|   2097152|     100|0.92474437|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.04462265|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.19339466|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.13090839|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.84342780|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.77431246|  PASSED
     diehard_squeeze|   0|    100000|     100|0.80548031|  PASSED
        diehard_sums|   0|       100|     100|0.01112349|  PASSED
        diehard_runs|   0|    100000|     100|0.54045515|  PASSED
        diehard_runs|   0|    100000|     100|0.98848956|  PASSED
       diehard_craps|   0|    200000|     100|0.97804560|  PASSED
       diehard_craps|   0|    200000|     100|0.94016447|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.04819804|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.46602579|  PASSED
         sts_monobit|   1|    100000|     100|0.71434134|  PASSED
            sts_runs|   2|    100000|     100|0.54546462|  PASSED
          sts_serial|   1|    100000|     100|0.48588864|  PASSED
          sts_serial|   2|    100000|     100|0.82113249|  PASSED
          sts_serial|   3|    100000|     100|0.84203336|  PASSED
          sts_serial|   3|    100000|     100|0.49113300|  PASSED
          sts_serial|   4|    100000|     100|0.99342944|  PASSED
          sts_serial|   4|    100000|     100|0.87149392|  PASSED
          sts_serial|   5|    100000|     100|0.18240501|  PASSED
          sts_serial|   5|    100000|     100|0.02589280|  PASSED
          sts_serial|   6|    100000|     100|0.97794757|  PASSED
          sts_serial|   6|    100000|     100|0.77220670|  PASSED
          sts_serial|   7|    100000|     100|0.51539750|  PASSED
          sts_serial|   7|    100000|     100|0.16274400|  PASSED
          sts_serial|   8|    100000|     100|0.37155946|  PASSED
          sts_serial|   8|    100000|     100|0.24512670|  PASSED
          sts_serial|   9|    100000|     100|0.72920529|  PASSED
          sts_serial|   9|    100000|     100|0.54462654|  PASSED
          sts_serial|  10|    100000|     100|0.99803526|   WEAK
          sts_serial|  10|    100000|     100|0.95974898|  PASSED
          sts_serial|  11|    100000|     100|0.70966610|  PASSED
          sts_serial|  11|    100000|     100|0.70943517|  PASSED
          sts_serial|  12|    100000|     100|0.75382161|  PASSED
          sts_serial|  12|    100000|     100|0.25471894|  PASSED
          sts_serial|  13|    100000|     100|0.99091584|  PASSED
          sts_serial|  13|    100000|     100|0.73747903|  PASSED
          sts_serial|  14|    100000|     100|0.65642546|  PASSED
          sts_serial|  14|    100000|     100|0.03583472|  PASSED
          sts_serial|  15|    100000|     100|0.32276745|  PASSED
          sts_serial|  15|    100000|     100|0.61592578|  PASSED
          sts_serial|  16|    100000|     100|0.30500655|  PASSED
          sts_serial|  16|    100000|     100|0.90112372|  PASSED
         rgb_bitdist|   1|    100000|     100|0.78830028|  PASSED
         rgb_bitdist|   2|    100000|     100|0.08823691|  PASSED
         rgb_bitdist|   3|    100000|     100|0.95615543|  PASSED
         rgb_bitdist|   4|    100000|     100|0.48034118|  PASSED
         rgb_bitdist|   5|    100000|     100|0.17425117|  PASSED
         rgb_bitdist|   6|    100000|     100|0.55372513|  PASSED
         rgb_bitdist|   7|    100000|     100|0.57324578|  PASSED
         rgb_bitdist|   8|    100000|     100|0.88031137|  PASSED
         rgb_bitdist|   9|    100000|     100|0.12145148|  PASSED
         rgb_bitdist|  10|    100000|     100|0.23819681|  PASSED
         rgb_bitdist|  11|    100000|     100|0.17285913|  PASSED
         rgb_bitdist|  12|    100000|     100|0.97711596|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.20824435|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.20456466|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.11395985|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.56644264|  PASSED
    rgb_permutations|   2|    100000|     100|0.99570897|   WEAK
    rgb_permutations|   3|    100000|     100|0.60352388|  PASSED
    rgb_permutations|   4|    100000|     100|0.04247774|  PASSED
    rgb_permutations|   5|    100000|     100|0.63936845|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.97736534|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.68121843|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.81347425|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.76463188|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.27088262|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.71086516|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.99299260|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.59342928|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.81989142|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.54499930|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.61053777|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.39190942|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.69107794|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.52512213|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.83062993|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.31074444|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.47143807|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.49054286|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.19231235|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.84398279|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.85777044|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.56953151|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.65770137|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.46497664|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.92326330|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.98841839|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.82152581|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.80310539|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.70385487|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.96287277|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.02657992|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.32597254|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.61896930|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.81760156|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.57230330|  PASSED
             dab_dct| 256|     50000|       1|0.35477359|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.90692656|  PASSED
        dab_filltree|  32|  15000000|       1|0.86585488|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.55770474|  PASSED
       dab_filltree2|   1|   5000000|       1|0.70544806|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.77738292|  PASSED
#=============================================================================#
# Summary: PASSED=112, WEAK=2, FAILED=0                                       #
#          235,031.367 MB of random data created with 96.697 MB/sec           #
#=============================================================================#
#=============================================================================#
# Runtime: 0:40:30                                                            #
#=============================================================================#
*/
