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
 * @version !__version__! &mdash; <em>$Date: 2014-07-24 $</em>
 */
public class Yarn2Random extends Random32 {
	private static final long serialVersionUID = 1L;

	private static final long GEN = 123567893;

	/**
	 * The parameter class of this random engine.
	 */
	public static final class Param implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * LEcuyer 1 parameters: a1 = 1498809829; a2 = 1160990996
		 */
		public static final Param LECUYER1 = new Param(1498809829, 1160990996);

		/**
		 * LEcuyer 2 parameters: a1 = 46325; a2 = 1084587
		 */
		public static final Param LECUYER2 = new Param(46325, 1084587);

		/**
		 * The default PRNG parameters: LECUYER1
		 */
		public static final Param DEFAULT = LECUYER1;

		public final long a1;
		public final long a2;

		public Param(final int a1, final int a2) {
			this.a1 = a1;
			this.a2 = a2;
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

	public Yarn2Random(final Param param, final long seed) {
		_param = requireNonNull(param);
		_state = new State(seed);
	}

	public Yarn2Random(final Param param) {
		this(param, math.random.seed());
	}

	public Yarn2Random(final long seed) {
		this(Param.DEFAULT, seed);
	}

	public Yarn2Random() {
		this(Param.DEFAULT, math.random.seed());
	}

	@Override
	public int nextInt() {
		step();

		if (_state._r1 == 0) {
			return 0;
		}

		long n = _state._r1;
		long p = 1;
		long t = GEN;
		while (n > 0) {
			if ((n&0x1) == 0x1) {
				p = OxFFFFFFFB.mod(p*t);
			}
			t = OxFFFFFFFB.mod(t*t);
			n /= 2;
		}
		return (int)p;
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
# Testing: org.jenetix.random.Yarn2Random (2014-07-23 19:59)                  #
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
stdin_input_raw|  3.69e+06  |2214698195|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.00000000|  FAILED
      diehard_operm5|   0|   1000000|     100|0.63003309|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.00000000|  FAILED
    diehard_rank_6x8|   0|    100000|     100|0.55802144|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.00000000|  FAILED
        diehard_opso|   0|   2097152|     100|0.04024087|  PASSED
        diehard_oqso|   0|   2097152|     100|0.00260944|   WEAK
         diehard_dna|   0|   2097152|     100|0.93342316|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.00000000|  FAILED
diehard_count_1s_byt|   0|    256000|     100|0.13194428|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.10495578|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.72139192|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.08969290|  PASSED
     diehard_squeeze|   0|    100000|     100|0.00000000|  FAILED
        diehard_sums|   0|       100|     100|0.49524934|  PASSED
        diehard_runs|   0|    100000|     100|0.12031970|  PASSED
        diehard_runs|   0|    100000|     100|0.33059705|  PASSED
       diehard_craps|   0|    200000|     100|0.01100030|  PASSED
       diehard_craps|   0|    200000|     100|0.87770292|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.00000000|  FAILED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.00000000|  FAILED
         sts_monobit|   1|    100000|     100|0.00240058|   WEAK
            sts_runs|   2|    100000|     100|0.00069711|   WEAK
          sts_serial|   1|    100000|     100|0.08385536|  PASSED
          sts_serial|   2|    100000|     100|0.43876400|  PASSED
          sts_serial|   3|    100000|     100|0.81264528|  PASSED
          sts_serial|   3|    100000|     100|0.17053826|  PASSED
          sts_serial|   4|    100000|     100|0.99709115|   WEAK
          sts_serial|   4|    100000|     100|0.92071766|  PASSED
          sts_serial|   5|    100000|     100|0.87536114|  PASSED
          sts_serial|   5|    100000|     100|0.99793635|   WEAK
          sts_serial|   6|    100000|     100|0.17439019|  PASSED
          sts_serial|   6|    100000|     100|0.06893621|  PASSED
          sts_serial|   7|    100000|     100|0.14077218|  PASSED
          sts_serial|   7|    100000|     100|0.49290010|  PASSED
          sts_serial|   8|    100000|     100|0.65122516|  PASSED
          sts_serial|   8|    100000|     100|0.94844756|  PASSED
          sts_serial|   9|    100000|     100|0.74066654|  PASSED
          sts_serial|   9|    100000|     100|0.82126067|  PASSED
          sts_serial|  10|    100000|     100|0.96373657|  PASSED
          sts_serial|  10|    100000|     100|0.98964617|  PASSED
          sts_serial|  11|    100000|     100|0.53875118|  PASSED
          sts_serial|  11|    100000|     100|0.09179178|  PASSED
          sts_serial|  12|    100000|     100|0.00581609|  PASSED
          sts_serial|  12|    100000|     100|0.00865037|  PASSED
          sts_serial|  13|    100000|     100|0.34627600|  PASSED
          sts_serial|  13|    100000|     100|0.58380033|  PASSED
          sts_serial|  14|    100000|     100|0.19389154|  PASSED
          sts_serial|  14|    100000|     100|0.24982966|  PASSED
          sts_serial|  15|    100000|     100|0.07578095|  PASSED
          sts_serial|  15|    100000|     100|0.89113681|  PASSED
          sts_serial|  16|    100000|     100|0.01784807|  PASSED
          sts_serial|  16|    100000|     100|0.34073283|  PASSED
         rgb_bitdist|   1|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   2|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   3|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   4|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   5|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   6|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   7|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   8|    100000|     100|0.24920583|  PASSED
         rgb_bitdist|   9|    100000|     100|0.02780907|  PASSED
         rgb_bitdist|  10|    100000|     100|0.99176714|  PASSED
         rgb_bitdist|  11|    100000|     100|0.96455628|  PASSED
         rgb_bitdist|  12|    100000|     100|0.99537867|   WEAK
rgb_minimum_distance|   2|     10000|    1000|0.06337781|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.48483526|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.41113177|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.82775405|  PASSED
    rgb_permutations|   2|    100000|     100|0.06732006|  PASSED
    rgb_permutations|   3|    100000|     100|0.66192388|  PASSED
    rgb_permutations|   4|    100000|     100|0.47617447|  PASSED
    rgb_permutations|   5|    100000|     100|0.53050558|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.27739350|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.14369108|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.72162331|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.00832059|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.15546711|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.12454960|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.88254897|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.00000157|   WEAK
      rgb_lagged_sum|   8|   1000000|     100|0.10888169|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.00000671|   WEAK
      rgb_lagged_sum|  10|   1000000|     100|0.97546839|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.00003544|   WEAK
      rgb_lagged_sum|  12|   1000000|     100|0.99007994|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.61380138|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.00033035|   WEAK
      rgb_lagged_sum|  15|   1000000|     100|0.00000000|  FAILED
      rgb_lagged_sum|  16|   1000000|     100|0.17183344|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.47448220|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.43631456|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.00000000|  FAILED
      rgb_lagged_sum|  20|   1000000|     100|0.04959737|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.25618459|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.48480042|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.00000000|  FAILED
      rgb_lagged_sum|  24|   1000000|     100|0.18300159|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.14592008|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.48785172|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.00227328|   WEAK
      rgb_lagged_sum|  28|   1000000|     100|0.90475073|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.00000000|  FAILED
      rgb_lagged_sum|  30|   1000000|     100|0.50749343|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.00000019|  FAILED
^@      rgb_lagged_sum|  32|   1000000|     100|0.52273345|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.75475347|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.00000000|  FAILED
             dab_dct| 256|     50000|       1|0.87897238|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.36604513|  PASSED
        dab_filltree|  32|  15000000|       1|0.12055030|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.00000000|  FAILED
       dab_filltree2|   1|   5000000|       1|0.00000000|  FAILED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|1.00000000|  FAILED
#=============================================================================#
# Runtime: 4:45:00                                                            #
#=============================================================================#
*/
