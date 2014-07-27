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
 * @version !__version__! &mdash; <em>$Date$</em>
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
		int _c;

		State(final long seed) {
			setSeed(seed);
		}

		void setSeed(final long seed) {
			final long a = seed;
			final long b = mix(seed);

			_x = (int)(a >>> Integer.SIZE);
			_y = a == 0L ? 0xdeadbeef : (int)a;
			_z = (int)(b >>> Integer.SIZE);
			_c = (int)b;
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
				.and(_c).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(state ->
				eq(_x, state._x) &&
				eq(_y, state._y) &&
				eq(_z, state._z) &&
				eq(_c, state._c)
			);
		}

		@Override
		public String toString() {
			return format("State[%d, %d, %d, %d]", _x, _y, _z, _c);
		}

	}

	private final State _state;

	public KISS32Random(final long seed) {
		_state = new State(seed);
	}

	public KISS32Random() {
		this(math.random.seed());
	}

	@Override
	public int nextInt() {
		step();
		return _state._x + _state._y + _state._z;
	}

	private void step() {
		_state._x = 69069*_state._x + 12345;
		_state._y ^= _state._y << 13;
		_state._y ^= _state._y >> 17;
		_state._y ^= _state._y << 5;

		final long t = 698769069*_state._z + _state._c;
		_state._c = (int)(t >> 32);
		_state._z = (int)t;
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
# Testing: org.jenetix.random.KISSRandom (2014-07-27 20:55)                   #
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
stdin_input_raw|  3.06e+07  |2507331893|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.59470085|  PASSED
      diehard_operm5|   0|   1000000|     100|0.04275867|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.80551839|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.55525290|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.39285682|  PASSED
        diehard_opso|   0|   2097152|     100|0.31658204|  PASSED
        diehard_oqso|   0|   2097152|     100|0.52163831|  PASSED
         diehard_dna|   0|   2097152|     100|0.83570913|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.39659129|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.84253630|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.36104913|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.55263257|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.89426465|  PASSED
     diehard_squeeze|   0|    100000|     100|0.35065935|  PASSED
        diehard_sums|   0|       100|     100|0.10962642|  PASSED
        diehard_runs|   0|    100000|     100|0.23355541|  PASSED
        diehard_runs|   0|    100000|     100|0.93007810|  PASSED
       diehard_craps|   0|    200000|     100|0.28886629|  PASSED
       diehard_craps|   0|    200000|     100|0.88109780|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.31615919|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.68361322|  PASSED
         sts_monobit|   1|    100000|     100|0.98852942|  PASSED
            sts_runs|   2|    100000|     100|0.95988582|  PASSED
          sts_serial|   1|    100000|     100|0.08905048|  PASSED
          sts_serial|   2|    100000|     100|0.43684047|  PASSED
          sts_serial|   3|    100000|     100|0.16510658|  PASSED
          sts_serial|   3|    100000|     100|0.97490716|  PASSED
          sts_serial|   4|    100000|     100|0.51093104|  PASSED
          sts_serial|   4|    100000|     100|0.31991857|  PASSED
          sts_serial|   5|    100000|     100|0.94895477|  PASSED
          sts_serial|   5|    100000|     100|0.99357212|  PASSED
          sts_serial|   6|    100000|     100|0.09521367|  PASSED
          sts_serial|   6|    100000|     100|0.18502544|  PASSED
          sts_serial|   7|    100000|     100|0.55318437|  PASSED
          sts_serial|   7|    100000|     100|0.63042632|  PASSED
          sts_serial|   8|    100000|     100|0.98275146|  PASSED
          sts_serial|   8|    100000|     100|0.13396245|  PASSED
          sts_serial|   9|    100000|     100|0.64428360|  PASSED
          sts_serial|   9|    100000|     100|0.25910182|  PASSED
          sts_serial|  10|    100000|     100|0.29041717|  PASSED
          sts_serial|  10|    100000|     100|0.91013386|  PASSED
          sts_serial|  11|    100000|     100|0.90811198|  PASSED
          sts_serial|  11|    100000|     100|0.64708269|  PASSED
          sts_serial|  12|    100000|     100|0.73419049|  PASSED
          sts_serial|  12|    100000|     100|0.54536660|  PASSED
          sts_serial|  13|    100000|     100|0.82541929|  PASSED
          sts_serial|  13|    100000|     100|0.74672322|  PASSED
          sts_serial|  14|    100000|     100|0.44493722|  PASSED
          sts_serial|  14|    100000|     100|0.43425710|  PASSED
          sts_serial|  15|    100000|     100|0.26960697|  PASSED
          sts_serial|  15|    100000|     100|0.56764983|  PASSED
          sts_serial|  16|    100000|     100|0.90772151|  PASSED
          sts_serial|  16|    100000|     100|0.48647991|  PASSED
         rgb_bitdist|   1|    100000|     100|0.13697068|  PASSED
         rgb_bitdist|   2|    100000|     100|0.30661955|  PASSED
         rgb_bitdist|   3|    100000|     100|0.94316892|  PASSED
         rgb_bitdist|   4|    100000|     100|0.65280985|  PASSED
         rgb_bitdist|   5|    100000|     100|0.45471595|  PASSED
         rgb_bitdist|   6|    100000|     100|0.92761711|  PASSED
         rgb_bitdist|   7|    100000|     100|0.94760597|  PASSED
         rgb_bitdist|   8|    100000|     100|0.95037448|  PASSED
         rgb_bitdist|   9|    100000|     100|0.24388874|  PASSED
         rgb_bitdist|  10|    100000|     100|0.16456758|  PASSED
         rgb_bitdist|  11|    100000|     100|0.91272062|  PASSED
         rgb_bitdist|  12|    100000|     100|0.14377146|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.36315858|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.50182760|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.02582378|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.34278919|  PASSED
    rgb_permutations|   2|    100000|     100|0.79590639|  PASSED
    rgb_permutations|   3|    100000|     100|0.24782248|  PASSED
    rgb_permutations|   4|    100000|     100|0.68870589|  PASSED
    rgb_permutations|   5|    100000|     100|0.09501457|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.96317935|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.41269554|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.50016399|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.63852667|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.87904816|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.49801713|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.99579842|   WEAK
      rgb_lagged_sum|   7|   1000000|     100|0.64732844|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.22760495|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.98118923|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.01640776|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.22392713|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.55459119|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.56421360|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.98129559|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.47609502|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.21275997|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.31932622|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.79625163|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.56481973|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.01763372|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.58241986|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.78468727|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.14595154|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.74713386|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.36212303|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.92104117|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.82831086|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.16419449|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.28234268|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.12836582|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.38206387|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.42900192|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.45315369|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.71964622|  PASSED
             dab_dct| 256|     50000|       1|0.26822894|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.21718267|  PASSED
        dab_filltree|  32|  15000000|       1|0.98304433|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.47118474|  PASSED
       dab_filltree2|   1|   5000000|       1|0.44089172|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.45993010|  PASSED
#=============================================================================#
# Summary: PASSED=113, WEAK=1, FAILED=0                                       #
#          235,031.320 MB of random data created with 98.521 MB/sec           #
#=============================================================================#
#=============================================================================#
# Runtime: 0:39:45                                                            #
#=============================================================================#
*/
