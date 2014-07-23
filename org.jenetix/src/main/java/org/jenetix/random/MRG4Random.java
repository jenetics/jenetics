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
			long t = modulus.mod(seed);
			if (t < 0) t += modulus.VALUE;

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
		final long t = modulus.add(
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
# Testing: org.jenetix.random.MRG4Random (2014-07-23 01:03)                   #
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
stdin_input_raw|  3.88e+07  |2492322226|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.05671219|  PASSED
      diehard_operm5|   0|   1000000|     100|0.59030520|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.94551333|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.20407588|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.08126672|  PASSED
        diehard_opso|   0|   2097152|     100|0.07790366|  PASSED
        diehard_oqso|   0|   2097152|     100|0.58567431|  PASSED
         diehard_dna|   0|   2097152|     100|0.58738367|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.70185483|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.97660585|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.59033501|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.28567910|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.82168472|  PASSED
     diehard_squeeze|   0|    100000|     100|0.74234362|  PASSED
        diehard_sums|   0|       100|     100|0.11443084|  PASSED
        diehard_runs|   0|    100000|     100|0.94462716|  PASSED
        diehard_runs|   0|    100000|     100|0.99843909|   WEAK
       diehard_craps|   0|    200000|     100|0.07775314|  PASSED
       diehard_craps|   0|    200000|     100|0.98534946|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.00000000|  FAILED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.00000000|  FAILED
         sts_monobit|   1|    100000|     100|0.89207109|  PASSED
            sts_runs|   2|    100000|     100|0.90572530|  PASSED
          sts_serial|   1|    100000|     100|0.32215546|  PASSED
          sts_serial|   2|    100000|     100|0.72946533|  PASSED
          sts_serial|   3|    100000|     100|0.99576127|   WEAK
          sts_serial|   3|    100000|     100|0.85486278|  PASSED
          sts_serial|   4|    100000|     100|0.47001510|  PASSED
          sts_serial|   4|    100000|     100|0.40598704|  PASSED
          sts_serial|   5|    100000|     100|0.95694664|  PASSED
          sts_serial|   5|    100000|     100|0.76878234|  PASSED
          sts_serial|   6|    100000|     100|0.42557240|  PASSED
          sts_serial|   6|    100000|     100|0.61820866|  PASSED
          sts_serial|   7|    100000|     100|0.38887055|  PASSED
          sts_serial|   7|    100000|     100|0.94264381|  PASSED
          sts_serial|   8|    100000|     100|0.48479614|  PASSED
          sts_serial|   8|    100000|     100|0.77365173|  PASSED
          sts_serial|   9|    100000|     100|0.30172739|  PASSED
          sts_serial|   9|    100000|     100|0.94271613|  PASSED
          sts_serial|  10|    100000|     100|0.15743333|  PASSED
          sts_serial|  10|    100000|     100|0.73560194|  PASSED
          sts_serial|  11|    100000|     100|0.53385847|  PASSED
          sts_serial|  11|    100000|     100|0.42905193|  PASSED
          sts_serial|  12|    100000|     100|0.64918718|  PASSED
          sts_serial|  12|    100000|     100|0.62237490|  PASSED
          sts_serial|  13|    100000|     100|0.45317751|  PASSED
          sts_serial|  13|    100000|     100|0.95734695|  PASSED
          sts_serial|  14|    100000|     100|0.82182696|  PASSED
          sts_serial|  14|    100000|     100|0.09794986|  PASSED
          sts_serial|  15|    100000|     100|0.50437843|  PASSED
          sts_serial|  15|    100000|     100|0.87989771|  PASSED
          sts_serial|  16|    100000|     100|0.88942454|  PASSED
          sts_serial|  16|    100000|     100|0.45442938|  PASSED
         rgb_bitdist|   1|    100000|     100|0.33516489|  PASSED
         rgb_bitdist|   2|    100000|     100|0.99382029|  PASSED
         rgb_bitdist|   3|    100000|     100|0.73138739|  PASSED
         rgb_bitdist|   4|    100000|     100|0.30946715|  PASSED
         rgb_bitdist|   5|    100000|     100|0.45548930|  PASSED
         rgb_bitdist|   6|    100000|     100|0.18026393|  PASSED
         rgb_bitdist|   7|    100000|     100|0.86142855|  PASSED
         rgb_bitdist|   8|    100000|     100|0.85430492|  PASSED
         rgb_bitdist|   9|    100000|     100|0.81889480|  PASSED
         rgb_bitdist|  10|    100000|     100|0.50081687|  PASSED
         rgb_bitdist|  11|    100000|     100|0.08008181|  PASSED
         rgb_bitdist|  12|    100000|     100|0.51505132|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.40617256|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.96891769|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.00193405|   WEAK
rgb_minimum_distance|   5|     10000|    1000|0.38701444|  PASSED
    rgb_permutations|   2|    100000|     100|0.52390611|  PASSED
    rgb_permutations|   3|    100000|     100|0.25925662|  PASSED
    rgb_permutations|   4|    100000|     100|0.26052116|  PASSED
    rgb_permutations|   5|    100000|     100|0.13382702|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.97963455|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.81558661|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.81254803|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.57271933|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.99847196|   WEAK
      rgb_lagged_sum|   5|   1000000|     100|0.20377481|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.82380255|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.17752002|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.51543660|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.24217764|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.89230797|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.56355936|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.73244099|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.76096033|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.86869557|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.39001348|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.36658195|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.85995324|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.75964997|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.06195360|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.07603266|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.88847396|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.88278937|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.80998119|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.29837778|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.73483996|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.24370350|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.43787962|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.99844411|   WEAK
      rgb_lagged_sum|  29|   1000000|     100|0.04900577|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.01532960|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.81742905|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.29188295|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.85734453|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.60641267|  PASSED
             dab_dct| 256|     50000|       1|0.82552444|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.35498826|  PASSED
        dab_filltree|  32|  15000000|       1|0.82425438|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.89892066|  PASSED
       dab_filltree2|   1|   5000000|       1|0.12835675|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|1.00000000|  FAILED
#=============================================================================#
# Runtime: 0:41:31                                                            #
#=============================================================================#
*/
