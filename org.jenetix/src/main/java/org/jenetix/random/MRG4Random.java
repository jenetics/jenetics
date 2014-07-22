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
			new Param(2001982722, 1412284257, 1155380217, 1668339922);

		/**
		 * LEcuyer 2 parameters: a1 = 64886; a2 = 0; a3 = 0, a4 = 64322
		 */
		public static final Param LECUYER2 = new Param(64886, 0, 0, 64322);

		/**
		 * The default PRNG parameters: LECUYER1
		 */
		public static final Param DEFAULT = LECUYER1;

		public final long a1;
		public final long a2;
		public final long a3;
		public final long a4;

		public Param(final int a1, final int a2, final int a3, final int a4) {
			this.a1 = a1;
			this.a2 = a2;
			this.a3 = a3;
			this.a4 = a4;
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
			long t = Modulus.mod(seed);
			if (t < 0) t += Modulus.VALUE;

			_r1 = (int)t;
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
		final long t = Modulus.add(
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
# Testing: org.jenetix.random.MRG4Random (2014-07-19 21:15)                   #
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
stdin_input_raw|  3.28e+07  |1359213404|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.07546412|  PASSED
      diehard_operm5|   0|   1000000|     100|0.06820243|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.39175250|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.82483281|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.48848226|  PASSED
        diehard_opso|   0|   2097152|     100|0.92733820|  PASSED
        diehard_oqso|   0|   2097152|     100|0.98434238|  PASSED
         diehard_dna|   0|   2097152|     100|0.19861239|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.92145785|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.14541778|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.24200626|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.97720493|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.49100158|  PASSED
     diehard_squeeze|   0|    100000|     100|0.99067975|  PASSED
        diehard_sums|   0|       100|     100|0.22159605|  PASSED
        diehard_runs|   0|    100000|     100|0.01649850|  PASSED
        diehard_runs|   0|    100000|     100|0.18221476|  PASSED
       diehard_craps|   0|    200000|     100|0.91552152|  PASSED
       diehard_craps|   0|    200000|     100|0.27566174|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.09666636|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.30840892|  PASSED
         sts_monobit|   1|    100000|     100|0.08510805|  PASSED
            sts_runs|   2|    100000|     100|0.94828864|  PASSED
          sts_serial|   1|    100000|     100|0.63687645|  PASSED
          sts_serial|   2|    100000|     100|0.87641284|  PASSED
          sts_serial|   3|    100000|     100|0.95233487|  PASSED
          sts_serial|   3|    100000|     100|0.43949416|  PASSED
          sts_serial|   4|    100000|     100|0.91118411|  PASSED
          sts_serial|   4|    100000|     100|0.95318893|  PASSED
          sts_serial|   5|    100000|     100|0.93044176|  PASSED
          sts_serial|   5|    100000|     100|0.84147604|  PASSED
          sts_serial|   6|    100000|     100|0.70592816|  PASSED
          sts_serial|   6|    100000|     100|0.77966302|  PASSED
          sts_serial|   7|    100000|     100|0.99683643|   WEAK
          sts_serial|   7|    100000|     100|0.98444588|  PASSED
          sts_serial|   8|    100000|     100|0.71604872|  PASSED
          sts_serial|   8|    100000|     100|0.60550932|  PASSED
          sts_serial|   9|    100000|     100|0.98859858|  PASSED
          sts_serial|   9|    100000|     100|0.58317153|  PASSED
          sts_serial|  10|    100000|     100|0.37243000|  PASSED
          sts_serial|  10|    100000|     100|0.71438974|  PASSED
          sts_serial|  11|    100000|     100|0.94599251|  PASSED
          sts_serial|  11|    100000|     100|0.29447046|  PASSED
          sts_serial|  12|    100000|     100|0.06282690|  PASSED
          sts_serial|  12|    100000|     100|0.02560769|  PASSED
          sts_serial|  13|    100000|     100|0.15249070|  PASSED
          sts_serial|  13|    100000|     100|0.88525425|  PASSED
          sts_serial|  14|    100000|     100|0.69139325|  PASSED
          sts_serial|  14|    100000|     100|0.88151069|  PASSED
          sts_serial|  15|    100000|     100|0.16459660|  PASSED
          sts_serial|  15|    100000|     100|0.94852206|  PASSED
          sts_serial|  16|    100000|     100|0.53621342|  PASSED
          sts_serial|  16|    100000|     100|0.68037263|  PASSED
         rgb_bitdist|   1|    100000|     100|0.75375484|  PASSED
         rgb_bitdist|   2|    100000|     100|0.69655712|  PASSED
         rgb_bitdist|   3|    100000|     100|0.90059817|  PASSED
         rgb_bitdist|   4|    100000|     100|0.84717201|  PASSED
         rgb_bitdist|   5|    100000|     100|0.92726319|  PASSED
         rgb_bitdist|   6|    100000|     100|0.38710213|  PASSED
         rgb_bitdist|   7|    100000|     100|0.26447548|  PASSED
         rgb_bitdist|   8|    100000|     100|0.85740846|  PASSED
         rgb_bitdist|   9|    100000|     100|0.16766835|  PASSED
         rgb_bitdist|  10|    100000|     100|0.82742751|  PASSED
         rgb_bitdist|  11|    100000|     100|0.97485477|  PASSED
         rgb_bitdist|  12|    100000|     100|0.63544219|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.20349694|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.10499298|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.50756348|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.34280625|  PASSED
    rgb_permutations|   2|    100000|     100|0.98101141|  PASSED
    rgb_permutations|   3|    100000|     100|0.96692127|  PASSED
    rgb_permutations|   4|    100000|     100|0.19379669|  PASSED
    rgb_permutations|   5|    100000|     100|0.48784470|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.79898704|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.56826050|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.21129197|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.18797647|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.28915034|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.46547745|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.43780562|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.25514504|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.74407656|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.95409220|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.78012314|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.30309654|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.04432389|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.81424401|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.39549148|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.06679113|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.55317301|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.20044147|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.01861128|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.68570240|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.50288767|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.87762297|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.89211231|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.70854609|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.86436290|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.96541839|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.89634756|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.21898032|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.48524485|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.92817815|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.99845485|   WEAK
      rgb_lagged_sum|  31|   1000000|     100|0.42001918|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.78258655|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.83449165|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.44657474|  PASSED
             dab_dct| 256|     50000|       1|0.18494017|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.06940698|  PASSED
        dab_filltree|  32|  15000000|       1|0.72416267|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.13390700|  PASSED
       dab_filltree2|   1|   5000000|       1|0.85200219|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.64945205|  PASSED
#=============================================================================#
# Runtime: 0:39:44                                                            #
#=============================================================================#
*/
