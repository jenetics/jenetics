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
import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.util.Random32;
import org.jenetics.util.math;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__! &mdash; <em>$Date: 2014-07-22 $</em>
 */
public class MRG3Random extends Random32 {

	private static final long serialVersionUID = 1L;

	/**
	 * The parameter class of this random engine.
	 */
	public static final class Param implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * LEcuyer 1 parameters: a1 = 2021422057; a2 = 1826992351; a3 = 1977753457
		 */
		public static final Param LECUYER1 = new Param(2021422057, 1826992351, 1977753457);

		/**
		 * LEcuyer 2 parameters: a1 = 1476728729; a2 = 0; a3 = 1155643113
		 */
		public static final Param LECUYER2 = new Param(1476728729, 0, 1155643113);

		/**
		 * LEcuyer 3 parameters: a1 = 65338; a2 = 0; a3 = 64636
		 */
		public static final Param LECUYER3 = new Param(65338, 0, 64636);

		/**
		 * The default PRNG parameters: LECUYER1
		 */
		public static final Param DEFAULT = LECUYER1;

		public final long a1;
		public final long a2;
		public final long a3;

		public Param(final int a1, final int a2, final int a3) {
			this.a1 = a1;
			this.a2 = a2;
			this.a3 = a3;
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(a1)
				.and(a2)
				.and(a3).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(param ->
				eq(a1, param.a1) &&
				eq(a2, param.a2) &&
				eq(a3, param.a3)
			);
		}

		@Override
		public String toString() {
			return format("Param[%d, %d, %d]", a1, a2, a3);
		}
	}

	private static final class State implements Serializable {
		private static final long serialVersionUID = 1L;

		long _r1;
		long _r2;
		long _r3;

		State(final long seed) {
			setSeed(seed);
		}

		void setSeed(final long seed) {
			long t = modulus.mod(seed);
			if (t < 0) t += modulus.VALUE;

			_r1 = t;
			_r2 = 1;
			_r3 = 1;
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(_r1)
				.and(_r2)
				.and(_r3).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(state ->
				eq(_r1, state._r1) &&
				eq(_r2, state._r2) &&
				eq(_r3, state._r3)
			);
		}

		@Override
		public String toString() {
			return format("State[%d, %d, %d]", _r1, _r2, _r3);
		}
	}

	private final Param _param;
	private final State _state;

	public MRG3Random(final Param param, final long seed) {
		_param = requireNonNull(param);
		_state = new State(seed);
	}

	public MRG3Random(final Param param) {
		this(param, math.random.seed());
	}

	public MRG3Random(final long seed) {
		this(Param.DEFAULT, seed);
	}

	public MRG3Random() {
		this(Param.DEFAULT, math.random.seed());
	}

	@Override
	public int nextInt() {
		step();
		return (int)_state._r1;
	}

	public void step() {
		final long t = modulus.add(
			_param.a1*_state._r1,
			_param.a2*_state._r2,
			_param.a3*_state._r3
		);

		_state._r3 = _state._r2;
		_state._r2 = _state._r1;
		_state._r1 = t;
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
# Testing: org.jenetix.random.MRG3Random (2014-07-19 19:28)                   #
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
stdin_input_raw|  3.14e+07  | 826708739|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.53810249|  PASSED
      diehard_operm5|   0|   1000000|     100|0.26401158|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.95940495|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.10725737|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.79358463|  PASSED
        diehard_opso|   0|   2097152|     100|0.12450245|  PASSED
        diehard_oqso|   0|   2097152|     100|0.32264688|  PASSED
         diehard_dna|   0|   2097152|     100|0.42775943|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.99303098|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.15000351|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.96805407|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.88110958|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.26859313|  PASSED
     diehard_squeeze|   0|    100000|     100|0.32497386|  PASSED
        diehard_sums|   0|       100|     100|0.02060832|  PASSED
        diehard_runs|   0|    100000|     100|0.43958050|  PASSED
        diehard_runs|   0|    100000|     100|0.19517083|  PASSED
       diehard_craps|   0|    200000|     100|0.68767713|  PASSED
       diehard_craps|   0|    200000|     100|0.24362727|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.11588922|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.76810555|  PASSED
         sts_monobit|   1|    100000|     100|0.09297486|  PASSED
            sts_runs|   2|    100000|     100|0.82621437|  PASSED
          sts_serial|   1|    100000|     100|0.42565576|  PASSED
          sts_serial|   2|    100000|     100|0.02864829|  PASSED
          sts_serial|   3|    100000|     100|0.14252348|  PASSED
          sts_serial|   3|    100000|     100|0.04391931|  PASSED
          sts_serial|   4|    100000|     100|0.61209166|  PASSED
          sts_serial|   4|    100000|     100|0.96740736|  PASSED
          sts_serial|   5|    100000|     100|0.32753521|  PASSED
          sts_serial|   5|    100000|     100|0.32167928|  PASSED
          sts_serial|   6|    100000|     100|0.76623723|  PASSED
          sts_serial|   6|    100000|     100|0.51783526|  PASSED
          sts_serial|   7|    100000|     100|0.95649735|  PASSED
          sts_serial|   7|    100000|     100|0.69933778|  PASSED
          sts_serial|   8|    100000|     100|0.87036319|  PASSED
          sts_serial|   8|    100000|     100|0.86960566|  PASSED
          sts_serial|   9|    100000|     100|0.81433444|  PASSED
          sts_serial|   9|    100000|     100|0.95897486|  PASSED
          sts_serial|  10|    100000|     100|0.23810652|  PASSED
          sts_serial|  10|    100000|     100|0.32963514|  PASSED
          sts_serial|  11|    100000|     100|0.22594359|  PASSED
          sts_serial|  11|    100000|     100|0.40540517|  PASSED
          sts_serial|  12|    100000|     100|0.16845627|  PASSED
          sts_serial|  12|    100000|     100|0.18498714|  PASSED
          sts_serial|  13|    100000|     100|0.20106842|  PASSED
          sts_serial|  13|    100000|     100|0.89468598|  PASSED
          sts_serial|  14|    100000|     100|0.79489661|  PASSED
          sts_serial|  14|    100000|     100|0.09396223|  PASSED
          sts_serial|  15|    100000|     100|0.26129902|  PASSED
          sts_serial|  15|    100000|     100|0.98420872|  PASSED
          sts_serial|  16|    100000|     100|0.88684684|  PASSED
          sts_serial|  16|    100000|     100|0.82022408|  PASSED
         rgb_bitdist|   1|    100000|     100|0.39227645|  PASSED
         rgb_bitdist|   2|    100000|     100|0.87701058|  PASSED
         rgb_bitdist|   3|    100000|     100|0.31397560|  PASSED
         rgb_bitdist|   4|    100000|     100|0.98656714|  PASSED
         rgb_bitdist|   5|    100000|     100|0.11930281|  PASSED
         rgb_bitdist|   6|    100000|     100|0.20244650|  PASSED
         rgb_bitdist|   7|    100000|     100|0.75323641|  PASSED
         rgb_bitdist|   8|    100000|     100|0.28234498|  PASSED
         rgb_bitdist|   9|    100000|     100|0.65772315|  PASSED
         rgb_bitdist|  10|    100000|     100|0.99723767|   WEAK
         rgb_bitdist|  11|    100000|     100|0.14745536|  PASSED
         rgb_bitdist|  12|    100000|     100|0.95276042|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.42228624|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.90444252|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.61286231|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.06048339|  PASSED
    rgb_permutations|   2|    100000|     100|0.98272952|  PASSED
    rgb_permutations|   3|    100000|     100|0.05633461|  PASSED
    rgb_permutations|   4|    100000|     100|0.45837715|  PASSED
    rgb_permutations|   5|    100000|     100|0.27366400|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.70685528|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.40425631|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.41335579|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.71014498|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.99335968|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.93145131|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.27050021|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.82245035|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.83956558|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.50518783|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.30985361|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.82909133|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.26442637|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.84298554|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.79040843|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.99973631|   WEAK
      rgb_lagged_sum|  16|   1000000|     100|0.37790217|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.55524642|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.56565938|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.38102712|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.71546335|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.61432556|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.50141773|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.98511799|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.78852898|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.57796958|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.24396522|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.64027167|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.61199182|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.78285011|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.89354865|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.88477173|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.29900295|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.56107005|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.21018880|  PASSED
             dab_dct| 256|     50000|       1|0.36341519|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.45379962|  PASSED
        dab_filltree|  32|  15000000|       1|0.80724653|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.70298944|  PASSED
       dab_filltree2|   1|   5000000|       1|0.54643034|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.62794219|  PASSED
#=============================================================================#
# Runtime: 0:45:18                                                            #
#=============================================================================#
*/
