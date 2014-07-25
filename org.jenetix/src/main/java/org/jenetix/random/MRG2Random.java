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
public class MRG2Random extends Random32 {

	private static final long serialVersionUID = 1L;

	/**
	 * The parameter class of this random engine.
	 */
	public static final class Param implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * LEcuyer 1 parameters: a1 = 1498809829; a2 = 1160990996
		 */
		public static final Param LECUYER1 = Param.of(1498809829, 1160990996);

		/**
		 * LEcuyer 2 parameters: a1 = 46325; a2 = 1084587
		 */
		public static final Param LECUYER2 = Param.of(46325, 1084587);

		/**
		 * The default PRNG parameters: LECUYER1
		 */
		public static final Param DEFAULT = LECUYER1;

		public final long a1;
		public final long a2;

		private Param(final int a1, final int a2) {
			this.a1 = a1;
			this.a2 = a2;
 		}

		public static Param of(final int a1, final int a2) {
			return new Param(a1, a2);
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(a1)
				.and(a2).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(param ->
				eq(a1, param.a1) &&
				eq(a2, param.a2)
			);
		}

		@Override
		public String toString() {
			return format("Param[%d, %d]", a1, a2);
		}
	}

	private static final class State implements Serializable {
		private static final long serialVersionUID = 1L;

		long _r1;
		long _r2;

		State(final long seed) {
			setSeed(seed);
		}

		void setSeed(final long seed) {
			long t = OxFFFFFFFB.mod(seed);
			if (t < 0) t += OxFFFFFFFB.VALUE;

			_r1 = t;
			_r2 = 1;
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(_r1)
				.and(_r2).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(state ->
				eq(_r1, state._r1) &&
				eq(_r2, state._r2)
			);
		}

		@Override
		public String toString() {
			return format("State[%d, %d]", _r1, _r2);
		}
	}

	private final Param _param;
	private final State _state;

	public MRG2Random(final Param param, final long seed) {
		_param = requireNonNull(param);
		_state = new State(seed);
	}

	public MRG2Random(final Param param) {
		this(param, math.random.seed());
	}

	public MRG2Random(final long seed) {
		this(Param.DEFAULT, seed);
	}

	public MRG2Random() {
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
			_param.a2*_state._r2
		);

		_state._r2 = _state._r1;
		_state._r1 = t;
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
# Testing: org.jenetix.random.MRG2Random (2014-07-25 00:16)                   #
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
stdin_input_raw|  3.65e+07  |2796247308|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.93053628|  PASSED
      diehard_operm5|   0|   1000000|     100|0.75472753|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.52429146|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.70050098|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.89975589|  PASSED
        diehard_opso|   0|   2097152|     100|0.34224263|  PASSED
        diehard_oqso|   0|   2097152|     100|0.43924767|  PASSED
         diehard_dna|   0|   2097152|     100|0.98616478|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.78341156|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.55466375|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.43824831|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.03987245|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.99935996|   WEAK
     diehard_squeeze|   0|    100000|     100|0.28637585|  PASSED
        diehard_sums|   0|       100|     100|0.02988994|  PASSED
        diehard_runs|   0|    100000|     100|0.33418482|  PASSED
        diehard_runs|   0|    100000|     100|0.37019324|  PASSED
       diehard_craps|   0|    200000|     100|0.20681018|  PASSED
       diehard_craps|   0|    200000|     100|0.05623791|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.10590543|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.80414822|  PASSED
         sts_monobit|   1|    100000|     100|0.70917178|  PASSED
            sts_runs|   2|    100000|     100|0.15669521|  PASSED
          sts_serial|   1|    100000|     100|0.50584758|  PASSED
          sts_serial|   2|    100000|     100|0.70601333|  PASSED
          sts_serial|   3|    100000|     100|0.90704934|  PASSED
          sts_serial|   3|    100000|     100|0.96909031|  PASSED
          sts_serial|   4|    100000|     100|0.64683521|  PASSED
          sts_serial|   4|    100000|     100|0.51374176|  PASSED
          sts_serial|   5|    100000|     100|0.16880656|  PASSED
          sts_serial|   5|    100000|     100|0.00843133|  PASSED
          sts_serial|   6|    100000|     100|0.09004172|  PASSED
          sts_serial|   6|    100000|     100|0.41585091|  PASSED
          sts_serial|   7|    100000|     100|0.15541004|  PASSED
          sts_serial|   7|    100000|     100|0.96313491|  PASSED
          sts_serial|   8|    100000|     100|0.06611540|  PASSED
          sts_serial|   8|    100000|     100|0.29392953|  PASSED
          sts_serial|   9|    100000|     100|0.84651425|  PASSED
          sts_serial|   9|    100000|     100|0.61979395|  PASSED
          sts_serial|  10|    100000|     100|0.05331376|  PASSED
          sts_serial|  10|    100000|     100|0.11881567|  PASSED
          sts_serial|  11|    100000|     100|0.38604971|  PASSED
          sts_serial|  11|    100000|     100|0.41773535|  PASSED
          sts_serial|  12|    100000|     100|0.90811821|  PASSED
          sts_serial|  12|    100000|     100|0.61963723|  PASSED
          sts_serial|  13|    100000|     100|0.42356869|  PASSED
          sts_serial|  13|    100000|     100|0.61562736|  PASSED
          sts_serial|  14|    100000|     100|0.25933160|  PASSED
          sts_serial|  14|    100000|     100|0.40842750|  PASSED
          sts_serial|  15|    100000|     100|0.81374959|  PASSED
          sts_serial|  15|    100000|     100|0.79624768|  PASSED
          sts_serial|  16|    100000|     100|0.89292899|  PASSED
          sts_serial|  16|    100000|     100|0.80795613|  PASSED
         rgb_bitdist|   1|    100000|     100|0.81525269|  PASSED
         rgb_bitdist|   2|    100000|     100|0.63691980|  PASSED
         rgb_bitdist|   3|    100000|     100|0.70238939|  PASSED
         rgb_bitdist|   4|    100000|     100|0.35842856|  PASSED
         rgb_bitdist|   5|    100000|     100|0.64712876|  PASSED
         rgb_bitdist|   6|    100000|     100|0.23486592|  PASSED
         rgb_bitdist|   7|    100000|     100|0.07523822|  PASSED
         rgb_bitdist|   8|    100000|     100|0.72508501|  PASSED
         rgb_bitdist|   9|    100000|     100|0.97678323|  PASSED
         rgb_bitdist|  10|    100000|     100|0.62260795|  PASSED
         rgb_bitdist|  11|    100000|     100|0.25554489|  PASSED
         rgb_bitdist|  12|    100000|     100|0.15819547|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.03985027|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.11263997|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.50283130|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.11658347|  PASSED
    rgb_permutations|   2|    100000|     100|0.24995482|  PASSED
    rgb_permutations|   3|    100000|     100|0.17567413|  PASSED
    rgb_permutations|   4|    100000|     100|0.10889481|  PASSED
    rgb_permutations|   5|    100000|     100|0.14738807|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.87199469|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.39092091|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.30529492|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.85641718|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.86306240|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.02043280|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.51831036|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.88752729|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.99410795|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.87511086|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.85682264|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.19634126|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.04261426|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.88111997|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.49182069|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.19386826|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.83750096|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.82892571|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.03069138|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.05605637|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.45685081|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.01956489|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.54692406|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.15425899|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.56338946|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.75140401|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.95914670|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.78789443|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.58047197|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.95697131|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.01286293|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.99188113|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.39410481|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.20858534|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.50220630|  PASSED
             dab_dct| 256|     50000|       1|0.85742089|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.06485959|  PASSED
        dab_filltree|  32|  15000000|       1|0.42013430|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.50222225|  PASSED
       dab_filltree2|   1|   5000000|       1|0.08306608|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.04158479|  PASSED
#=============================================================================#
# Summary: PASSED=113, WEAK=1, FAILED=0                                       #
#=============================================================================#
#=============================================================================#
# Runtime: 0:39:19                                                            #
#=============================================================================#
*/
