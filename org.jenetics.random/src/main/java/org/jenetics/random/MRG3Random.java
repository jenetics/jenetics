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
		this(param, math.seed());
	}

	public MRG3Random(final long seed) {
		this(Param.DEFAULT, seed);
	}

	public MRG3Random() {
		this(Param.DEFAULT, math.seed());
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
# Testing: org.jenetix.random.MRG3Random (2014-07-28 02:14)                   #
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
stdin_input_raw|  3.47e+07  |3455648752|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.93928677|  PASSED
      diehard_operm5|   0|   1000000|     100|0.27252400|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.53001387|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.19390424|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.61235667|  PASSED
        diehard_opso|   0|   2097152|     100|0.10992411|  PASSED
        diehard_oqso|   0|   2097152|     100|0.08957049|  PASSED
         diehard_dna|   0|   2097152|     100|0.93169797|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.18509675|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.73924080|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.61281762|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.15502336|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.54341369|  PASSED
     diehard_squeeze|   0|    100000|     100|0.35978424|  PASSED
        diehard_sums|   0|       100|     100|0.60769049|  PASSED
        diehard_runs|   0|    100000|     100|0.63421493|  PASSED
        diehard_runs|   0|    100000|     100|0.42421579|  PASSED
       diehard_craps|   0|    200000|     100|0.87593189|  PASSED
       diehard_craps|   0|    200000|     100|0.41653357|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.95074178|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.15421256|  PASSED
         sts_monobit|   1|    100000|     100|0.32265171|  PASSED
            sts_runs|   2|    100000|     100|0.06086971|  PASSED
          sts_serial|   1|    100000|     100|0.81856728|  PASSED
          sts_serial|   2|    100000|     100|0.12234569|  PASSED
          sts_serial|   3|    100000|     100|0.65902171|  PASSED
          sts_serial|   3|    100000|     100|0.19493218|  PASSED
          sts_serial|   4|    100000|     100|0.58990190|  PASSED
          sts_serial|   4|    100000|     100|0.65324225|  PASSED
          sts_serial|   5|    100000|     100|0.90052053|  PASSED
          sts_serial|   5|    100000|     100|0.51235547|  PASSED
          sts_serial|   6|    100000|     100|0.26668102|  PASSED
          sts_serial|   6|    100000|     100|0.15664282|  PASSED
          sts_serial|   7|    100000|     100|0.69255806|  PASSED
          sts_serial|   7|    100000|     100|0.99108233|  PASSED
          sts_serial|   8|    100000|     100|0.42986646|  PASSED
          sts_serial|   8|    100000|     100|0.89238712|  PASSED
          sts_serial|   9|    100000|     100|0.85382170|  PASSED
          sts_serial|   9|    100000|     100|0.63933189|  PASSED
          sts_serial|  10|    100000|     100|0.75267936|  PASSED
          sts_serial|  10|    100000|     100|0.60032482|  PASSED
          sts_serial|  11|    100000|     100|0.66837257|  PASSED
          sts_serial|  11|    100000|     100|0.23840907|  PASSED
          sts_serial|  12|    100000|     100|0.05916436|  PASSED
          sts_serial|  12|    100000|     100|0.53476730|  PASSED
          sts_serial|  13|    100000|     100|0.52111954|  PASSED
          sts_serial|  13|    100000|     100|0.77808403|  PASSED
          sts_serial|  14|    100000|     100|0.70232611|  PASSED
          sts_serial|  14|    100000|     100|0.68814130|  PASSED
          sts_serial|  15|    100000|     100|0.86458961|  PASSED
          sts_serial|  15|    100000|     100|0.80259888|  PASSED
          sts_serial|  16|    100000|     100|0.30766374|  PASSED
          sts_serial|  16|    100000|     100|0.32122410|  PASSED
         rgb_bitdist|   1|    100000|     100|0.81843382|  PASSED
         rgb_bitdist|   2|    100000|     100|0.30874090|  PASSED
         rgb_bitdist|   3|    100000|     100|0.97170483|  PASSED
         rgb_bitdist|   4|    100000|     100|0.81347597|  PASSED
         rgb_bitdist|   5|    100000|     100|0.16390710|  PASSED
         rgb_bitdist|   6|    100000|     100|0.78567077|  PASSED
         rgb_bitdist|   7|    100000|     100|0.67318565|  PASSED
         rgb_bitdist|   8|    100000|     100|0.70747019|  PASSED
         rgb_bitdist|   9|    100000|     100|0.11644271|  PASSED
         rgb_bitdist|  10|    100000|     100|0.66907188|  PASSED
         rgb_bitdist|  11|    100000|     100|0.68733695|  PASSED
         rgb_bitdist|  12|    100000|     100|0.23612650|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.79828393|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.93707859|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.96361070|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.13862563|  PASSED
    rgb_permutations|   2|    100000|     100|0.05828365|  PASSED
    rgb_permutations|   3|    100000|     100|0.66732200|  PASSED
    rgb_permutations|   4|    100000|     100|0.34432974|  PASSED
    rgb_permutations|   5|    100000|     100|0.45814356|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.14588639|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.55710548|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.79009212|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.76060284|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.52084392|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.50235370|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.42283007|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.65135341|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.35517628|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.46069915|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.74343248|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.90235915|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.04996931|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.75291152|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.45327949|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.27125971|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.90472387|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.07344984|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.75509478|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.99957123|   WEAK
      rgb_lagged_sum|  20|   1000000|     100|0.99074028|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.10575339|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.24514398|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.04757696|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.27031353|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.25135229|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.99075174|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.86024404|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.21977800|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.84513515|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.92696418|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.65253402|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.73935607|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.18197738|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.83787305|  PASSED
             dab_dct| 256|     50000|       1|0.37890239|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.46684472|  PASSED
        dab_filltree|  32|  15000000|       1|0.20550176|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.83559724|  PASSED
       dab_filltree2|   1|   5000000|       1|0.99596823|   WEAK
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.74710255|  PASSED
#=============================================================================#
# Summary: PASSED=112, WEAK=2, FAILED=0                                       #
#          235,031.211 MB of random data created with 93.675 MB/sec           #
#=============================================================================#
#=============================================================================#
# Runtime: 0:41:48                                                            #
#=============================================================================#
*/
