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
# Testing: org.jenetics.random.LCG64Random (2016-08-24 22:46)                 #
#=============================================================================#
#=============================================================================#
# Linux 4.4.0-34-generic (amd64)                                              #
# java version "1.8.0_102"                                                    #
# Java(TM) SE Runtime Environment (build 1.8.0_102-b14)                       #
# Java HotSpot(TM) 64-Bit Server VM (build 25.102-b14)                        #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  5.17e+07  |3897611629|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.88548747|  PASSED
      diehard_operm5|   0|   1000000|     100|0.80251870|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.96717131|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.00000000|  FAILED
   diehard_bitstream|   0|   2097152|     100|0.00000000|  FAILED
        diehard_opso|   0|   2097152|     100|0.00000000|  FAILED
        diehard_oqso|   0|   2097152|     100|0.00000000|  FAILED
         diehard_dna|   0|   2097152|     100|0.00000000|  FAILED
diehard_count_1s_str|   0|    256000|     100|0.00021959|   WEAK
diehard_count_1s_byt|   0|    256000|     100|0.00000000|  FAILED
 diehard_parking_lot|   0|     12000|     100|0.90162649|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.94593300|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.33394223|  PASSED
     diehard_squeeze|   0|    100000|     100|0.11685873|  PASSED
        diehard_sums|   0|       100|     100|0.06138188|  PASSED
        diehard_runs|   0|    100000|     100|0.93298949|  PASSED
        diehard_runs|   0|    100000|     100|0.97805794|  PASSED
       diehard_craps|   0|    200000|     100|0.13613602|  PASSED
       diehard_craps|   0|    200000|     100|0.46414575|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.00000000|  FAILED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.00000000|  FAILED
         sts_monobit|   1|    100000|     100|0.72925241|  PASSED
            sts_runs|   2|    100000|     100|0.00000000|  FAILED
          sts_serial|   1|    100000|     100|0.14066289|  PASSED
          sts_serial|   2|    100000|     100|0.00000000|  FAILED
          sts_serial|   3|    100000|     100|0.00000000|  FAILED
          sts_serial|   3|    100000|     100|0.00060825|   WEAK
          sts_serial|   4|    100000|     100|0.00000000|  FAILED
          sts_serial|   4|    100000|     100|0.09376754|  PASSED
          sts_serial|   5|    100000|     100|0.00000000|  FAILED
          sts_serial|   5|    100000|     100|0.00004061|   WEAK
          sts_serial|   6|    100000|     100|0.00000000|  FAILED
          sts_serial|   6|    100000|     100|0.00007356|   WEAK
          sts_serial|   7|    100000|     100|0.00000000|  FAILED
          sts_serial|   7|    100000|     100|0.00000007|  FAILED
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
rgb_minimum_distance|   2|     10000|    1000|0.03130406|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.13100745|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.79192070|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.81133248|  PASSED
    rgb_permutations|   2|    100000|     100|0.81187132|  PASSED
    rgb_permutations|   3|    100000|     100|0.42313121|  PASSED
    rgb_permutations|   4|    100000|     100|0.37485218|  PASSED
    rgb_permutations|   5|    100000|     100|0.43963140|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.96341563|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.49660313|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.81598205|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.11201423|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.40602034|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.94323163|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.01944067|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.77611889|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.23530547|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.21079805|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.92717588|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.97233628|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.77613408|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.34360744|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.27390641|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.77563262|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.99836877|   WEAK
      rgb_lagged_sum|  17|   1000000|     100|0.59679446|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.19147595|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.66943435|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.19557555|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.44431371|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.19581062|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.97162269|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.93090280|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.27926471|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.78440485|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.87518816|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.69397699|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.07037714|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.54412512|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.48015536|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.58021928|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.14585797|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.00000000|  FAILED
             dab_dct| 256|     50000|       1|0.00000000|  FAILED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.63571361|  PASSED
        dab_filltree|  32|  15000000|       1|0.63402635|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.00000000|  FAILED
       dab_filltree2|   1|   5000000|       1|0.00000000|  FAILED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|1.00000000|  FAILED
#=============================================================================#
# Summary: PASSED=58, WEAK=5, FAILED=51                                       #
#          235,031.516 MB of random data created with 130.904 MB/sec          #
#=============================================================================#
#=============================================================================#
# Runtime: 0:29:55                                                            #
#=============================================================================#
*/
