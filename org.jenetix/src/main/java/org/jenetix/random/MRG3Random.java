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
 * @version !__version__! &mdash; <em>$Date: 2014-07-25 $</em>
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
			long t = OxFFFFFFFB.mod(seed);
			if (t < 0) t += OxFFFFFFFB.VALUE;

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
		final long t = OxFFFFFFFB.add(
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
# Testing: org.jenetix.random.MRG3Random (2014-07-25 00:55)                   #
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
stdin_input_raw|  2.71e+07  |1081028587|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.95044836|  PASSED
      diehard_operm5|   0|   1000000|     100|0.81098582|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.20759797|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.14100530|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.80098953|  PASSED
        diehard_opso|   0|   2097152|     100|0.81212089|  PASSED
        diehard_oqso|   0|   2097152|     100|0.39886463|  PASSED
         diehard_dna|   0|   2097152|     100|0.42719270|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.42970780|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.20485688|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.99294000|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.75687716|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.23854676|  PASSED
     diehard_squeeze|   0|    100000|     100|0.98793639|  PASSED
        diehard_sums|   0|       100|     100|0.11408560|  PASSED
        diehard_runs|   0|    100000|     100|0.97589246|  PASSED
        diehard_runs|   0|    100000|     100|0.96734638|  PASSED
       diehard_craps|   0|    200000|     100|0.49575468|  PASSED
       diehard_craps|   0|    200000|     100|0.10853352|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.49567255|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.95358697|  PASSED
         sts_monobit|   1|    100000|     100|0.78711719|  PASSED
            sts_runs|   2|    100000|     100|0.94088068|  PASSED
          sts_serial|   1|    100000|     100|0.40868160|  PASSED
          sts_serial|   2|    100000|     100|0.99289028|  PASSED
          sts_serial|   3|    100000|     100|0.41014594|  PASSED
          sts_serial|   3|    100000|     100|0.95541196|  PASSED
          sts_serial|   4|    100000|     100|0.11907463|  PASSED
          sts_serial|   4|    100000|     100|0.24983374|  PASSED
          sts_serial|   5|    100000|     100|0.00918500|  PASSED
          sts_serial|   5|    100000|     100|0.18928055|  PASSED
          sts_serial|   6|    100000|     100|0.01709316|  PASSED
          sts_serial|   6|    100000|     100|0.35773737|  PASSED
          sts_serial|   7|    100000|     100|0.19480497|  PASSED
          sts_serial|   7|    100000|     100|0.20056060|  PASSED
          sts_serial|   8|    100000|     100|0.79753527|  PASSED
          sts_serial|   8|    100000|     100|0.40293861|  PASSED
          sts_serial|   9|    100000|     100|0.49134729|  PASSED
          sts_serial|   9|    100000|     100|0.85328296|  PASSED
          sts_serial|  10|    100000|     100|0.09747286|  PASSED
          sts_serial|  10|    100000|     100|0.01394965|  PASSED
          sts_serial|  11|    100000|     100|0.29503012|  PASSED
          sts_serial|  11|    100000|     100|0.83996943|  PASSED
          sts_serial|  12|    100000|     100|0.26073265|  PASSED
          sts_serial|  12|    100000|     100|0.94930129|  PASSED
          sts_serial|  13|    100000|     100|0.80084298|  PASSED
          sts_serial|  13|    100000|     100|0.99937604|   WEAK
          sts_serial|  14|    100000|     100|0.65946225|  PASSED
          sts_serial|  14|    100000|     100|0.49960050|  PASSED
          sts_serial|  15|    100000|     100|0.30682501|  PASSED
          sts_serial|  15|    100000|     100|0.98278810|  PASSED
          sts_serial|  16|    100000|     100|0.99888509|   WEAK
          sts_serial|  16|    100000|     100|0.73065553|  PASSED
         rgb_bitdist|   1|    100000|     100|0.96667320|  PASSED
         rgb_bitdist|   2|    100000|     100|0.39692574|  PASSED
         rgb_bitdist|   3|    100000|     100|0.28496778|  PASSED
         rgb_bitdist|   4|    100000|     100|0.76995590|  PASSED
         rgb_bitdist|   5|    100000|     100|0.54054597|  PASSED
         rgb_bitdist|   6|    100000|     100|0.43105782|  PASSED
         rgb_bitdist|   7|    100000|     100|0.10264787|  PASSED
         rgb_bitdist|   8|    100000|     100|0.33076700|  PASSED
         rgb_bitdist|   9|    100000|     100|0.20995093|  PASSED
         rgb_bitdist|  10|    100000|     100|0.83564083|  PASSED
         rgb_bitdist|  11|    100000|     100|0.41017702|  PASSED
         rgb_bitdist|  12|    100000|     100|0.91923498|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.98080967|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.78137004|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.80053394|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.97657652|  PASSED
    rgb_permutations|   2|    100000|     100|0.12902464|  PASSED
    rgb_permutations|   3|    100000|     100|0.98935584|  PASSED
    rgb_permutations|   4|    100000|     100|0.91401037|  PASSED
    rgb_permutations|   5|    100000|     100|0.64005081|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.09706664|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.22144226|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.88615264|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.88836193|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.53474814|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.77387303|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.72521110|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.46636849|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.08890290|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.85889571|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.92223977|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.99201901|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.59934748|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.62299668|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.28614055|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.41318611|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.94241062|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.07390904|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.14889477|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.28121826|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.61179949|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.20491014|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.05212574|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.85710049|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.65199418|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.68228951|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.56687332|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.96504195|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.18197990|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.76580568|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.72804734|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.28759808|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.74801591|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.16516195|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.14397427|  PASSED
             dab_dct| 256|     50000|       1|0.53623999|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.41591429|  PASSED
        dab_filltree|  32|  15000000|       1|0.84159959|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.07131760|  PASSED
       dab_filltree2|   1|   5000000|       1|0.39759220|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.45751322|  PASSED
#=============================================================================#
# Summary: PASSED=112, WEAK=2, FAILED=0                                       #
#=============================================================================#
#=============================================================================#
# Runtime: 0:43:41                                                            #
#=============================================================================#
*/
