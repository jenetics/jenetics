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
import static java.util.Objects.requireNonNull;
import static org.jenetics.random.internal.util.Equality.eq;

import java.io.Serializable;

import org.jenetics.random.internal.util.Equality;
import org.jenetics.random.internal.util.Hash;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public class LCG64Random extends Random64 {

	private static final long serialVersionUID = 1L;


	/**
	 * The parameter class of this random engine.
	 */
	public static final class Param implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * Default parameters: a = 0xFBD19FBBC5C07FF5L; b = 0
		 */
		public static final Param DEFAULT =
			Param.of(0xFBD19FBBC5C07FF5L, 0L);

		/**
		 * LEcuyer 1 parameters: a = 0x27BB2EE687B0B0FDL; b = 0
		 */
		public static final Param LECUYER1 =
			Param.of(0x27BB2EE687B0B0FDL, 0L);

		/**
		 * LEcuyer 2 parameters: a = 0x2C6FE96EE78B6955L; b = 0
		 */
		public static final Param LECUYER2 =
			Param.of(0x2C6FE96EE78B6955L, 0L);

		/**
		 * LEcuyer 3 parameters: a = 0x369DEA0F31A53F85L; b = 0
		 */
		public static final Param LECUYER3 =
			Param.of(0x369DEA0F31A53F85L, 0L);


		public final long a;
		public final long b;

		private Param(final long a, final long b) {
			this.a = a;
			this.b = b;
		}

		public static Param of(final long a, final long b) {
			return new Param(a, b);
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(a)
				.and(b).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(param ->
				eq(a, param.a) &&
				eq(b, param.b)
			);
		}

		@Override
		public String toString() {
			return format("Param[%d, %d]", a, b);
		}

	}

	private final static class State implements Serializable {
		private static final long serialVersionUID = 1L;

		long _r;

		State(final long seed) {
			setSeed(seed);
		}

		void setSeed(final long seed) {
			_r = seed;
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass()).and(_r).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(state -> state._r == _r);
		}

		@Override
		public String toString() {
			return format("State[%d]", _r);
		}
	}

	private final Param _param;
	private final State _state;

	public LCG64Random(final Param param, final long seed) {
		_param = requireNonNull(param);
		_state = new State(seed);
	}

	public LCG64Random(final Param param) {
		this(param, math.seed());
	}

	public LCG64Random(final long seed) {
		this(Param.DEFAULT, seed);
	}

	public LCG64Random() {
		this(Param.DEFAULT, math.seed());
	}

	@Override
	public long nextLong() {
		step();
		return _state._r;
	}

	private void step() {
		_state._r = _param.a*_state._r + _param.b;
	}

	@Override
	public void setSeed(final long seed) {
		if (_state != null) _state.setSeed(seed);
	}

	public Param getParam() {
		return _param;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_param)
			.and(_state).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(random ->
			eq(_param, random._param) &&
			eq(_state, random._state)
		);
	}

	@Override
	public String toString() {
		return format("%s[%s, %s]", getClass().getSimpleName(), _param, _state);
	}

}

/*
#=============================================================================#
# Testing: org.jenetix.random.LCG64Random (2014-07-28 00:59)                  #
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
stdin_input_raw|  3.42e+07  |2371007499|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.82466108|  PASSED
      diehard_operm5|   0|   1000000|     100|0.95763520|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.40214484|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.00000000|  FAILED
   diehard_bitstream|   0|   2097152|     100|0.00000000|  FAILED
        diehard_opso|   0|   2097152|     100|0.00000000|  FAILED
        diehard_oqso|   0|   2097152|     100|0.00000000|  FAILED
         diehard_dna|   0|   2097152|     100|0.00000000|  FAILED
diehard_count_1s_str|   0|    256000|     100|0.00000000|  FAILED
diehard_count_1s_byt|   0|    256000|     100|0.00000000|  FAILED
 diehard_parking_lot|   0|     12000|     100|0.78081639|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.32913398|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.08836764|  PASSED
     diehard_squeeze|   0|    100000|     100|0.59983470|  PASSED
        diehard_sums|   0|       100|     100|0.06252561|  PASSED
        diehard_runs|   0|    100000|     100|0.53649364|  PASSED
        diehard_runs|   0|    100000|     100|0.33217163|  PASSED
       diehard_craps|   0|    200000|     100|0.97358023|  PASSED
       diehard_craps|   0|    200000|     100|0.39165928|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.00000000|  FAILED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.00000000|  FAILED
         sts_monobit|   1|    100000|     100|0.00000000|  FAILED
            sts_runs|   2|    100000|     100|0.00000000|  FAILED
          sts_serial|   1|    100000|     100|0.00000000|  FAILED
          sts_serial|   2|    100000|     100|0.00000000|  FAILED
          sts_serial|   3|    100000|     100|0.00000000|  FAILED
          sts_serial|   3|    100000|     100|0.00011361|   WEAK
          sts_serial|   4|    100000|     100|0.00000000|  FAILED
          sts_serial|   4|    100000|     100|0.00104460|   WEAK
          sts_serial|   5|    100000|     100|0.00000000|  FAILED
          sts_serial|   5|    100000|     100|0.00003412|   WEAK
          sts_serial|   6|    100000|     100|0.00000000|  FAILED
          sts_serial|   6|    100000|     100|0.00000021|  FAILED
          sts_serial|   7|    100000|     100|0.00000000|  FAILED
          sts_serial|   7|    100000|     100|0.00000000|  FAILED
          sts_serial|   8|    100000|     100|0.00000000|  FAILED
          sts_serial|   8|    100000|     100|0.00000000|  FAILED
          sts_serial|   9|    100000|     100|0.00000000|  FAILED
          sts_serial|   9|    100000|     100|0.00000000|  FAILED
          sts_serial|  10|    100000|     100|0.00000000|  FAILED
          sts_serial|  10|    100000|     100|0.00000000|  FAILED
          sts_serial|  11|    100000|     100|0.00000000|  FAILED
          sts_serial|  11|    100000|     100|0.00000000|  FAILED
          sts_serial|  12|    100000|     100|0.00000000|  FAILED
          sts_serial|  12|    100000|     100|0.00000000|  FAILED
          sts_serial|  13|    100000|     100|0.00000000|  FAILED
          sts_serial|  13|    100000|     100|0.00000000|  FAILED
          sts_serial|  14|    100000|     100|0.00000000|  FAILED
          sts_serial|  14|    100000|     100|0.00000000|  FAILED
          sts_serial|  15|    100000|     100|0.00000000|  FAILED
          sts_serial|  15|    100000|     100|0.00000000|  FAILED
          sts_serial|  16|    100000|     100|0.00000000|  FAILED
          sts_serial|  16|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   1|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   2|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   3|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   4|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   5|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   6|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   7|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   8|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   9|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|  10|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|  11|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|  12|    100000|     100|0.00000000|  FAILED
rgb_minimum_distance|   2|     10000|    1000|0.98580432|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.90977784|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.00627783|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.28913108|  PASSED
    rgb_permutations|   2|    100000|     100|0.10562224|  PASSED
    rgb_permutations|   3|    100000|     100|0.87743607|  PASSED
    rgb_permutations|   4|    100000|     100|0.04668239|  PASSED
    rgb_permutations|   5|    100000|     100|0.84393896|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.11077701|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.44172219|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.26723882|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.33094639|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.63246696|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.81201755|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.20924725|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.92064582|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.77448795|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.03548431|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.86828598|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.07440879|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.40177430|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.96972827|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.34972760|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.45390983|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.21306972|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.50424583|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.81923525|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.30782754|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.04603715|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.66603915|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.77131248|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.70238477|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.60261022|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.65693763|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.81598618|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.92388135|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.77961805|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.97446282|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.58831115|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.16734066|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.99998866|   WEAK
     rgb_kstest_test|   0|     10000|    1000|0.34554818|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.00000000|  FAILED
             dab_dct| 256|     50000|       1|0.00000000|  FAILED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.77525096|  PASSED
        dab_filltree|  32|  15000000|       1|0.58626905|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.00000000|  FAILED
       dab_filltree2|   1|   5000000|       1|0.00000000|  FAILED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|1.00000000|  FAILED
#=============================================================================#
# Summary: PASSED=55, WEAK=4, FAILED=55                                       #
#          235,031.219 MB of random data created with 106.730 MB/sec          #
#=============================================================================#
#=============================================================================#
# Runtime: 0:36:42                                                            #
#=============================================================================#
*/
