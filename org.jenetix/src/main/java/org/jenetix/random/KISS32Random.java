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
 * @version !__version__! &mdash; <em>$Date: 2014-07-28 $</em>
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
# Testing: org.jenetix.random.KISS32Random (2014-07-27 23:06)                 #
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
stdin_input_raw|  3.16e+07  | 452665567|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.96043831|  PASSED
      diehard_operm5|   0|   1000000|     100|0.51784242|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.88605731|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.16260801|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.99253568|  PASSED
        diehard_opso|   0|   2097152|     100|0.71419986|  PASSED
        diehard_oqso|   0|   2097152|     100|0.64654732|  PASSED
         diehard_dna|   0|   2097152|     100|0.84163883|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.10919213|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.37136169|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.82963409|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.41595516|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.88353039|  PASSED
     diehard_squeeze|   0|    100000|     100|0.21795988|  PASSED
        diehard_sums|   0|       100|     100|0.00164444|   WEAK
        diehard_runs|   0|    100000|     100|0.49423942|  PASSED
        diehard_runs|   0|    100000|     100|0.96204485|  PASSED
       diehard_craps|   0|    200000|     100|0.97522194|  PASSED
       diehard_craps|   0|    200000|     100|0.08351313|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.23416408|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.80711483|  PASSED
         sts_monobit|   1|    100000|     100|0.95571119|  PASSED
            sts_runs|   2|    100000|     100|0.99012501|  PASSED
          sts_serial|   1|    100000|     100|0.37785060|  PASSED
          sts_serial|   2|    100000|     100|0.58007292|  PASSED
          sts_serial|   3|    100000|     100|0.82883459|  PASSED
          sts_serial|   3|    100000|     100|0.51623916|  PASSED
          sts_serial|   4|    100000|     100|0.28246069|  PASSED
          sts_serial|   4|    100000|     100|0.48566486|  PASSED
          sts_serial|   5|    100000|     100|0.71001530|  PASSED
          sts_serial|   5|    100000|     100|0.89034329|  PASSED
          sts_serial|   6|    100000|     100|0.94270020|  PASSED
          sts_serial|   6|    100000|     100|0.56714394|  PASSED
          sts_serial|   7|    100000|     100|0.49890270|  PASSED
          sts_serial|   7|    100000|     100|0.49777671|  PASSED
          sts_serial|   8|    100000|     100|0.69281228|  PASSED
          sts_serial|   8|    100000|     100|0.75134903|  PASSED
          sts_serial|   9|    100000|     100|0.54287261|  PASSED
          sts_serial|   9|    100000|     100|0.68578616|  PASSED
          sts_serial|  10|    100000|     100|0.44767337|  PASSED
          sts_serial|  10|    100000|     100|0.14435074|  PASSED
          sts_serial|  11|    100000|     100|0.99628020|   WEAK
          sts_serial|  11|    100000|     100|0.93667757|  PASSED
          sts_serial|  12|    100000|     100|0.68028190|  PASSED
          sts_serial|  12|    100000|     100|0.05666961|  PASSED
          sts_serial|  13|    100000|     100|0.12021290|  PASSED
          sts_serial|  13|    100000|     100|0.11392096|  PASSED
          sts_serial|  14|    100000|     100|0.26695527|  PASSED
          sts_serial|  14|    100000|     100|0.88115863|  PASSED
          sts_serial|  15|    100000|     100|0.19832213|  PASSED
          sts_serial|  15|    100000|     100|0.92433589|  PASSED
          sts_serial|  16|    100000|     100|0.86726262|  PASSED
          sts_serial|  16|    100000|     100|0.54284364|  PASSED
         rgb_bitdist|   1|    100000|     100|0.65703709|  PASSED
         rgb_bitdist|   2|    100000|     100|0.18213483|  PASSED
         rgb_bitdist|   3|    100000|     100|0.84788179|  PASSED
         rgb_bitdist|   4|    100000|     100|0.29477150|  PASSED
         rgb_bitdist|   5|    100000|     100|0.09347110|  PASSED
         rgb_bitdist|   6|    100000|     100|0.49478970|  PASSED
         rgb_bitdist|   7|    100000|     100|0.45039798|  PASSED
         rgb_bitdist|   8|    100000|     100|0.62514799|  PASSED
         rgb_bitdist|   9|    100000|     100|0.51400745|  PASSED
         rgb_bitdist|  10|    100000|     100|0.63555026|  PASSED
         rgb_bitdist|  11|    100000|     100|0.23976449|  PASSED
         rgb_bitdist|  12|    100000|     100|0.01561460|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.85923709|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.47953068|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.37484355|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.67435490|  PASSED
    rgb_permutations|   2|    100000|     100|0.22123984|  PASSED
    rgb_permutations|   3|    100000|     100|0.95067582|  PASSED
    rgb_permutations|   4|    100000|     100|0.37079340|  PASSED
    rgb_permutations|   5|    100000|     100|0.38366834|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.83691914|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.93631017|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.66799954|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.67740051|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.95239617|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.96541499|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.61959887|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.19013144|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.29049882|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.93533722|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.91055504|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.82657405|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.90210859|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.72909872|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.50898852|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.25741297|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.44159270|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.34219218|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.65956647|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.21079418|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.81012709|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.13774830|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.83374703|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.98853803|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.03661352|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.52093729|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.30046244|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.71508909|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.61898828|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.94819477|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.01569697|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.72566399|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.25123199|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.06101332|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.20761220|  PASSED
             dab_dct| 256|     50000|       1|0.27374493|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.24388789|  PASSED
        dab_filltree|  32|  15000000|       1|0.11483044|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.97677077|  PASSED
       dab_filltree2|   1|   5000000|       1|0.64704484|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.19936208|  PASSED
#=============================================================================#
# Summary: PASSED=112, WEAK=2, FAILED=0                                       #
#          235,031.117 MB of random data created with 97.978 MB/sec           #
#=============================================================================#
#=============================================================================#
# Runtime: 0:39:58                                                            #
#=============================================================================#
*/
