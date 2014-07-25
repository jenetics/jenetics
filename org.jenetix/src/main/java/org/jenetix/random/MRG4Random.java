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
public class MRG4Random extends Random32 {

	private static final long serialVersionUID = 1L;

	/**
	 * The parameter class of this random engine.
	 */
	public static final class Param implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * LEcuyer 1 parameters:
		 *     a1 = 2001982722
		 *     a2 = 1412284257
		 *     a3 = 1155380217
		 *     a4 = 1668339922
		 */
		public static final Param LECUYER1 =
			Param.of(2001982722, 1412284257, 1155380217, 1668339922);

		/**
		 * LEcuyer 2 parameters: a1 = 64886; a2 = 0; a3 = 0, a4 = 64322
		 */
		public static final Param LECUYER2 = Param.of(64886, 0, 0, 64322);

		/**
		 * The default PRNG parameters: LECUYER1
		 */
		public static final Param DEFAULT = LECUYER1;

		public final long a1;
		public final long a2;
		public final long a3;
		public final long a4;

		private Param(final int a1, final int a2, final int a3, final int a4) {
			this.a1 = a1;
			this.a2 = a2;
			this.a3 = a3;
			this.a4 = a4;
		}

		public static Param of(
			final int a1,
			final int a2,
			final int a3,
			final int a4
		) {
			return new Param(a1, a2, a3, a4);
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(a1)
				.and(a2)
				.and(a3)
				.and(a4).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(param ->
				eq(a1, param.a1) &&
				eq(a2, param.a2) &&
				eq(a3, param.a3) &&
				eq(a4, param.a4)
			);
		}

		@Override
		public String toString() {
			return format("Param[%d, %d, %d, %d]", a1, a2, a3, a4);
		}
	}

	private static final class State implements Serializable {
		private static final long serialVersionUID = 1L;

		long _r1;
		long _r2;
		long _r3;
		long _r4;

		State(final long seed) {
			setSeed(seed);
		}

		void setSeed(final long seed) {
			long t = OxFFFFFFFB.mod(seed);
			if (t < 0) t += OxFFFFFFFB.VALUE;

			_r1 = t;
			_r2 = 1;
			_r3 = 1;
			_r4 = 1;
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(_r1)
				.and(_r2)
				.and(_r3)
				.and(_r4).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(state ->
				eq(_r1, state._r1) &&
				eq(_r2, state._r2) &&
				eq(_r3, state._r3) &&
				eq(_r4, state._r4)
			);
		}

		@Override
		public String toString() {
			return format("State[%d, %d, %d, %d]", _r1, _r2, _r3, _r4);
		}
	}

	private final Param _param;
	private final State _state;

	public MRG4Random(final Param param, final long seed) {
		_param = requireNonNull(param);
		_state = new State(seed);
	}

	public MRG4Random(final Param param) {
		this(param, math.random.seed());
	}

	public MRG4Random(final long seed) {
		this(Param.DEFAULT, seed);
	}

	public MRG4Random() {
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
			_param.a3*_state._r3,
			_param.a4*_state._r4
		);

		_state._r4 = _state._r3;
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
# Testing: org.jenetix.random.MRG4Random (2014-07-25 01:39)                   #
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
stdin_input_raw|  3.01e+07  |1083165157|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.63532538|  PASSED
      diehard_operm5|   0|   1000000|     100|0.49346197|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.85572764|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.29339005|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.24060096|  PASSED
        diehard_opso|   0|   2097152|     100|0.32702730|  PASSED
        diehard_oqso|   0|   2097152|     100|0.20386533|  PASSED
         diehard_dna|   0|   2097152|     100|0.20834439|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.91063099|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.55261114|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.92481402|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.26142937|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.44914701|  PASSED
     diehard_squeeze|   0|    100000|     100|0.96168680|  PASSED
        diehard_sums|   0|       100|     100|0.85995823|  PASSED
        diehard_runs|   0|    100000|     100|0.87809081|  PASSED
        diehard_runs|   0|    100000|     100|0.43103749|  PASSED
       diehard_craps|   0|    200000|     100|0.46790291|  PASSED
       diehard_craps|   0|    200000|     100|0.72203477|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.66541391|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.34252168|  PASSED
         sts_monobit|   1|    100000|     100|0.55257033|  PASSED
            sts_runs|   2|    100000|     100|0.79430613|  PASSED
          sts_serial|   1|    100000|     100|0.12964519|  PASSED
          sts_serial|   2|    100000|     100|0.37330851|  PASSED
          sts_serial|   3|    100000|     100|0.02001696|  PASSED
          sts_serial|   3|    100000|     100|0.08374931|  PASSED
          sts_serial|   4|    100000|     100|0.14526826|  PASSED
          sts_serial|   4|    100000|     100|0.98496670|  PASSED
          sts_serial|   5|    100000|     100|0.15135754|  PASSED
          sts_serial|   5|    100000|     100|0.15350230|  PASSED
          sts_serial|   6|    100000|     100|0.99783287|   WEAK
          sts_serial|   6|    100000|     100|0.10718048|  PASSED
          sts_serial|   7|    100000|     100|0.23323124|  PASSED
          sts_serial|   7|    100000|     100|0.20429074|  PASSED
          sts_serial|   8|    100000|     100|0.46146368|  PASSED
          sts_serial|   8|    100000|     100|0.15489702|  PASSED
          sts_serial|   9|    100000|     100|0.18765281|  PASSED
          sts_serial|   9|    100000|     100|0.26236255|  PASSED
          sts_serial|  10|    100000|     100|0.17301985|  PASSED
          sts_serial|  10|    100000|     100|0.33174244|  PASSED
          sts_serial|  11|    100000|     100|0.10310267|  PASSED
          sts_serial|  11|    100000|     100|0.33614202|  PASSED
          sts_serial|  12|    100000|     100|0.24293056|  PASSED
          sts_serial|  12|    100000|     100|0.96997505|  PASSED
          sts_serial|  13|    100000|     100|0.38060764|  PASSED
          sts_serial|  13|    100000|     100|0.51062499|  PASSED
          sts_serial|  14|    100000|     100|0.11227898|  PASSED
          sts_serial|  14|    100000|     100|0.92778354|  PASSED
          sts_serial|  15|    100000|     100|0.04569646|  PASSED
          sts_serial|  15|    100000|     100|0.01824952|  PASSED
          sts_serial|  16|    100000|     100|0.66158763|  PASSED
          sts_serial|  16|    100000|     100|0.41618499|  PASSED
         rgb_bitdist|   1|    100000|     100|0.77947241|  PASSED
         rgb_bitdist|   2|    100000|     100|0.30296735|  PASSED
         rgb_bitdist|   3|    100000|     100|0.82521855|  PASSED
         rgb_bitdist|   4|    100000|     100|0.70191310|  PASSED
         rgb_bitdist|   5|    100000|     100|0.78034617|  PASSED
         rgb_bitdist|   6|    100000|     100|0.83994199|  PASSED
         rgb_bitdist|   7|    100000|     100|0.56191382|  PASSED
         rgb_bitdist|   8|    100000|     100|0.85556063|  PASSED
         rgb_bitdist|   9|    100000|     100|0.64667666|  PASSED
         rgb_bitdist|  10|    100000|     100|0.44184965|  PASSED
         rgb_bitdist|  11|    100000|     100|0.99506489|   WEAK
         rgb_bitdist|  12|    100000|     100|0.44763634|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.14106254|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.16521743|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.06111575|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.03654209|  PASSED
    rgb_permutations|   2|    100000|     100|0.82422212|  PASSED
    rgb_permutations|   3|    100000|     100|0.91890207|  PASSED
    rgb_permutations|   4|    100000|     100|0.90249402|  PASSED
    rgb_permutations|   5|    100000|     100|0.76353386|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.99986899|   WEAK
      rgb_lagged_sum|   1|   1000000|     100|0.99990987|   WEAK
      rgb_lagged_sum|   2|   1000000|     100|0.16294170|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.67305904|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.85929264|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.79348054|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.90467787|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.26180587|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.95452809|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.99831549|   WEAK
      rgb_lagged_sum|  10|   1000000|     100|0.81746095|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.53275971|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.14466406|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.83318520|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.62014376|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.40863382|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.32266008|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.54433650|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.13743291|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.23733532|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.19128952|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.69022327|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.77747028|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.46459045|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.06103392|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.76580504|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.72096724|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.87825210|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.00814636|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.41227896|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.90832582|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.67437910|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.63601832|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.44171809|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.57384531|  PASSED
             dab_dct| 256|     50000|       1|0.33339065|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.57091559|  PASSED
        dab_filltree|  32|  15000000|       1|0.96290450|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.29030526|  PASSED
       dab_filltree2|   1|   5000000|       1|0.25917609|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.16582875|  PASSED
#=============================================================================#
# Summary: PASSED=109, WEAK=5, FAILED=0                                       #
#=============================================================================#
#=============================================================================#
# Runtime: 0:46:27                                                            #
#=============================================================================#
*/
