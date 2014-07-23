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
 * @version !__version__! &mdash; <em>$Date: 2014-07-23 $</em>
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
		public static final Param LECUYER1 = Param.of(2021422057, 1826992351, 1977753457);

		/**
		 * LEcuyer 2 parameters: a1 = 1476728729; a2 = 0; a3 = 1155643113
		 */
		public static final Param LECUYER2 = Param.of(1476728729, 0, 1155643113);

		/**
		 * LEcuyer 3 parameters: a1 = 65338; a2 = 0; a3 = 64636
		 */
		public static final Param LECUYER3 = Param.of(65338, 0, 64636);

		/**
		 * The default PRNG parameters: LECUYER1
		 */
		public static final Param DEFAULT = LECUYER1;

		public final long a1;
		public final long a2;
		public final long a3;

		private Param(final int a1, final int a2, final int a3) {
			this.a1 = a1;
			this.a2 = a2;
			this.a3 = a3;
		}

		public static Param of(final int a1, final int a2, final int a3) {
			return new Param(a1, a2, a3);
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
# Testing: org.jenetix.random.MRG3Random (2014-07-23 00:20)                   #
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
stdin_input_raw|  3.69e+07  |  91464276|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.42608743|  PASSED
      diehard_operm5|   0|   1000000|     100|0.36988836|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.24879695|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.71948299|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.55660686|  PASSED
        diehard_opso|   0|   2097152|     100|0.00281160|   WEAK
        diehard_oqso|   0|   2097152|     100|0.60469270|  PASSED
         diehard_dna|   0|   2097152|     100|0.98171798|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.79650506|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.49324851|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.22401317|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.75044611|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.96922016|  PASSED
     diehard_squeeze|   0|    100000|     100|0.24933972|  PASSED
        diehard_sums|   0|       100|     100|0.16430540|  PASSED
        diehard_runs|   0|    100000|     100|0.27053082|  PASSED
        diehard_runs|   0|    100000|     100|0.18720218|  PASSED
       diehard_craps|   0|    200000|     100|0.98238234|  PASSED
       diehard_craps|   0|    200000|     100|0.09092033|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.00000000|  FAILED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.00000000|  FAILED
         sts_monobit|   1|    100000|     100|0.44238157|  PASSED
            sts_runs|   2|    100000|     100|0.47445718|  PASSED
          sts_serial|   1|    100000|     100|0.49827042|  PASSED
          sts_serial|   2|    100000|     100|0.27808439|  PASSED
          sts_serial|   3|    100000|     100|0.09343061|  PASSED
          sts_serial|   3|    100000|     100|0.79631043|  PASSED
          sts_serial|   4|    100000|     100|0.09620740|  PASSED
          sts_serial|   4|    100000|     100|0.49830740|  PASSED
          sts_serial|   5|    100000|     100|0.06812970|  PASSED
          sts_serial|   5|    100000|     100|0.98059953|  PASSED
          sts_serial|   6|    100000|     100|0.03699996|  PASSED
          sts_serial|   6|    100000|     100|0.30763281|  PASSED
          sts_serial|   7|    100000|     100|0.00612305|  PASSED
          sts_serial|   7|    100000|     100|0.06866756|  PASSED
          sts_serial|   8|    100000|     100|0.04677013|  PASSED
          sts_serial|   8|    100000|     100|0.57507066|  PASSED
          sts_serial|   9|    100000|     100|0.56680314|  PASSED
          sts_serial|   9|    100000|     100|0.52421594|  PASSED
          sts_serial|  10|    100000|     100|0.81356503|  PASSED
          sts_serial|  10|    100000|     100|0.94857782|  PASSED
          sts_serial|  11|    100000|     100|0.27531695|  PASSED
          sts_serial|  11|    100000|     100|0.07730244|  PASSED
          sts_serial|  12|    100000|     100|0.60879037|  PASSED
          sts_serial|  12|    100000|     100|0.80868063|  PASSED
          sts_serial|  13|    100000|     100|0.87680801|  PASSED
          sts_serial|  13|    100000|     100|0.39599495|  PASSED
          sts_serial|  14|    100000|     100|0.32593410|  PASSED
          sts_serial|  14|    100000|     100|0.79889182|  PASSED
          sts_serial|  15|    100000|     100|0.51240481|  PASSED
          sts_serial|  15|    100000|     100|0.60478312|  PASSED
          sts_serial|  16|    100000|     100|0.97997350|  PASSED
          sts_serial|  16|    100000|     100|0.91696200|  PASSED
         rgb_bitdist|   1|    100000|     100|0.51622199|  PASSED
         rgb_bitdist|   2|    100000|     100|0.81763764|  PASSED
         rgb_bitdist|   3|    100000|     100|0.35530101|  PASSED
         rgb_bitdist|   4|    100000|     100|0.86027475|  PASSED
         rgb_bitdist|   5|    100000|     100|0.15810664|  PASSED
         rgb_bitdist|   6|    100000|     100|0.20760400|  PASSED
         rgb_bitdist|   7|    100000|     100|0.21375180|  PASSED
         rgb_bitdist|   8|    100000|     100|0.92354869|  PASSED
         rgb_bitdist|   9|    100000|     100|0.60234163|  PASSED
         rgb_bitdist|  10|    100000|     100|0.26366120|  PASSED
         rgb_bitdist|  11|    100000|     100|0.79253486|  PASSED
         rgb_bitdist|  12|    100000|     100|0.71614959|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.79566280|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.51761810|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.82144694|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.20345535|  PASSED
    rgb_permutations|   2|    100000|     100|0.63435797|  PASSED
    rgb_permutations|   3|    100000|     100|0.38209562|  PASSED
    rgb_permutations|   4|    100000|     100|0.20567667|  PASSED
    rgb_permutations|   5|    100000|     100|0.90473930|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.84877797|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.64026436|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.77609701|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.81063160|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.75621276|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.82962090|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.66704740|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.94983868|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.77228427|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.77536554|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.41079742|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.80229259|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.50792292|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.86235504|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.92917103|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.84424628|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.06710764|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.73950577|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.68767173|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.38734553|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.43261818|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.08887891|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.63742810|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.58999073|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.92083245|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.38911551|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.00456322|   WEAK
      rgb_lagged_sum|  27|   1000000|     100|0.41747705|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.93695684|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.74661340|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.06918858|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.32214146|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.20390910|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.64551491|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.08349958|  PASSED
             dab_dct| 256|     50000|       1|0.94156761|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.90252204|  PASSED
        dab_filltree|  32|  15000000|       1|0.73110377|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.03568108|  PASSED
       dab_filltree2|   1|   5000000|       1|0.88557510|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.92752236|  PASSED
#=============================================================================#
# Runtime: 0:43:02                                                            #
#=============================================================================#
*/
