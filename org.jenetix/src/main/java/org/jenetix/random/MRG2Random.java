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
			long t = modulus.mod(seed);
			if (t < 0) t += modulus.VALUE;

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
		final long t = modulus.add(
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
# Testing: org.jenetix.random.MRG2Random (2014-07-22 23:40)                   #
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
stdin_input_raw|  3.54e+07  | 545353993|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.81878290|  PASSED
      diehard_operm5|   0|   1000000|     100|0.02817865|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.13901665|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.04572383|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.69396722|  PASSED
        diehard_opso|   0|   2097152|     100|0.00000000|  FAILED
        diehard_oqso|   0|   2097152|     100|0.08162370|  PASSED
         diehard_dna|   0|   2097152|     100|0.48541193|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.59747221|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.12066643|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.95056725|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.60230377|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.95598035|  PASSED
     diehard_squeeze|   0|    100000|     100|0.02342079|  PASSED
        diehard_sums|   0|       100|     100|0.36339450|  PASSED
        diehard_runs|   0|    100000|     100|0.51242514|  PASSED
        diehard_runs|   0|    100000|     100|0.75457760|  PASSED
       diehard_craps|   0|    200000|     100|0.90278922|  PASSED
       diehard_craps|   0|    200000|     100|0.94766536|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.00000000|  FAILED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.00000000|  FAILED
         sts_monobit|   1|    100000|     100|0.25168393|  PASSED
            sts_runs|   2|    100000|     100|0.46409195|  PASSED
          sts_serial|   1|    100000|     100|0.30344132|  PASSED
          sts_serial|   2|    100000|     100|0.47423045|  PASSED
          sts_serial|   3|    100000|     100|0.38741569|  PASSED
          sts_serial|   3|    100000|     100|0.75673207|  PASSED
          sts_serial|   4|    100000|     100|0.63442387|  PASSED
          sts_serial|   4|    100000|     100|0.63610946|  PASSED
          sts_serial|   5|    100000|     100|0.38546904|  PASSED
          sts_serial|   5|    100000|     100|0.16748464|  PASSED
          sts_serial|   6|    100000|     100|0.91538654|  PASSED
          sts_serial|   6|    100000|     100|0.86015013|  PASSED
          sts_serial|   7|    100000|     100|0.93281738|  PASSED
          sts_serial|   7|    100000|     100|0.41377588|  PASSED
          sts_serial|   8|    100000|     100|0.99255219|  PASSED
          sts_serial|   8|    100000|     100|0.23440009|  PASSED
          sts_serial|   9|    100000|     100|0.62211374|  PASSED
          sts_serial|   9|    100000|     100|0.51214905|  PASSED
          sts_serial|  10|    100000|     100|0.93398188|  PASSED
          sts_serial|  10|    100000|     100|0.43688434|  PASSED
          sts_serial|  11|    100000|     100|0.62750440|  PASSED
          sts_serial|  11|    100000|     100|0.76223295|  PASSED
          sts_serial|  12|    100000|     100|0.77371642|  PASSED
          sts_serial|  12|    100000|     100|0.99167014|  PASSED
          sts_serial|  13|    100000|     100|0.29719835|  PASSED
          sts_serial|  13|    100000|     100|0.00192112|   WEAK
          sts_serial|  14|    100000|     100|0.75551246|  PASSED
          sts_serial|  14|    100000|     100|0.40260618|  PASSED
          sts_serial|  15|    100000|     100|0.28721341|  PASSED
          sts_serial|  15|    100000|     100|0.59119120|  PASSED
          sts_serial|  16|    100000|     100|0.62962876|  PASSED
          sts_serial|  16|    100000|     100|0.15676213|  PASSED
         rgb_bitdist|   1|    100000|     100|0.37152832|  PASSED
         rgb_bitdist|   2|    100000|     100|0.71785639|  PASSED
         rgb_bitdist|   3|    100000|     100|0.91691309|  PASSED
         rgb_bitdist|   4|    100000|     100|0.10003416|  PASSED
         rgb_bitdist|   5|    100000|     100|0.74566165|  PASSED
         rgb_bitdist|   6|    100000|     100|0.75424749|  PASSED
         rgb_bitdist|   7|    100000|     100|0.01042311|  PASSED
         rgb_bitdist|   8|    100000|     100|0.92292939|  PASSED
         rgb_bitdist|   9|    100000|     100|0.14358911|  PASSED
         rgb_bitdist|  10|    100000|     100|0.94115246|  PASSED
         rgb_bitdist|  11|    100000|     100|0.45062619|  PASSED
         rgb_bitdist|  12|    100000|     100|0.05819843|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.06515625|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.17641492|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.02618963|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.16280145|  PASSED
    rgb_permutations|   2|    100000|     100|0.45477343|  PASSED
    rgb_permutations|   3|    100000|     100|0.70814335|  PASSED
    rgb_permutations|   4|    100000|     100|0.70413278|  PASSED
    rgb_permutations|   5|    100000|     100|0.97374180|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.01838689|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.01748816|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.49830211|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.00005479|   WEAK
      rgb_lagged_sum|   4|   1000000|     100|0.00432846|   WEAK
      rgb_lagged_sum|   5|   1000000|     100|0.10485600|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.33392293|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.00000058|  FAILED
      rgb_lagged_sum|   8|   1000000|     100|0.04604881|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.00739432|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.79392600|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.00106449|   WEAK
      rgb_lagged_sum|  12|   1000000|     100|0.78398738|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.13961725|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.00000995|   WEAK
      rgb_lagged_sum|  15|   1000000|     100|0.19537371|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.41728879|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.26525450|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.99780122|   WEAK
      rgb_lagged_sum|  19|   1000000|     100|0.10398277|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.54344440|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.50603943|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.86155111|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.02301995|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.00307627|   WEAK
      rgb_lagged_sum|  25|   1000000|     100|0.93273568|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.11711893|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.66239973|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.77929186|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.00000000|  FAILED
      rgb_lagged_sum|  30|   1000000|     100|0.69703551|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.00000003|  FAILED
      rgb_lagged_sum|  32|   1000000|     100|0.11867058|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.08570221|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.00000000|  FAILED
             dab_dct| 256|     50000|       1|0.21695244|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.83500726|  PASSED
        dab_filltree|  32|  15000000|       1|0.48914149|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.00259963|   WEAK
       dab_filltree2|   1|   5000000|       1|0.90247277|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|1.00000000|  FAILED
#=============================================================================#
# Runtime: 0:40:31                                                            #
#=============================================================================#
*/
