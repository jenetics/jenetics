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
public class MRG5Random  extends Random32 {

	private static final long serialVersionUID = 1L;

	/**
	 * The parameter class of this random engine.
	 */
	public static final class Param implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * LEcuyer 1 parameters:
		 *     a1 = 107374182
		 *     a2 = 0
		 *     a3 = 0
		 *     a4 = 0
		 *     a5 = 104480
		 */
		public static final Param LECUYER1 =
			Param.of(107374182, 0, 0, 0, 104480);


		/**
		 * The default PRNG parameters: LECUYER1
		 */
		public static final Param DEFAULT = LECUYER1;

		public final long a1;
		public final long a2;
		public final long a3;
		public final long a4;
		public final long a5;

		private Param(
			final int a1,
			final int a2,
			final int a3,
			final int a4,
			final int a5
		) {
			this.a1 = a1;
			this.a2 = a2;
			this.a3 = a3;
			this.a4 = a4;
			this.a5 = a5;
		}

		public static Param of(
			final int a1,
			final int a2,
			final int a3,
			final int a4,
			final int a5
		) {
			return new Param(a1, a2, a3, a4, a5);
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(a1)
				.and(a2)
				.and(a3)
				.and(a4)
				.and(a5).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(param ->
				eq(a1, param.a1) &&
				eq(a2, param.a2) &&
				eq(a3, param.a3) &&
				eq(a4, param.a4) &&
				eq(a5, param.a5)
			);
		}

		@Override
		public String toString() {
			return format("Param[%d, %d, %d, %d, %d]", a1, a2, a3, a4, a5);
		}
	}

	private static final class State implements Serializable {
		private static final long serialVersionUID = 1L;

		long _r1;
		long _r2;
		long _r3;
		long _r4;
		long _r5;

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
			_r5 = 1;
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(_r1)
				.and(_r2)
				.and(_r3)
				.and(_r4)
				.and(_r5).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(state ->
				eq(_r1, state._r1) &&
				eq(_r2, state._r2) &&
				eq(_r3, state._r3) &&
				eq(_r4, state._r4) &&
				eq(_r5, state._r5)
			);
		}

		@Override
		public String toString() {
			return format("State[%d, %d, %d, %d, %d]", _r1, _r2, _r3, _r4, _r5);
		}
	}

	private final Param _param;
	private final State _state;

	public MRG5Random(final Param param, final long seed) {
		_param = requireNonNull(param);
		_state = new State(seed);
	}

	public MRG5Random(final Param param) {
		this(param, math.random.seed());
	}

	public MRG5Random(final long seed) {
		this(Param.DEFAULT, seed);
	}

	public MRG5Random() {
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
			_param.a4*_state._r4,
			_param.a5*_state._r5
		);

		_state._r5 = _state._r4;
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
# Testing: org.jenetix.random.MRG5Random (2014-07-25 02:26)                   #
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
stdin_input_raw|  2.44e+07  |1140586441|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.74984334|  PASSED
      diehard_operm5|   0|   1000000|     100|0.34516579|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.99952649|   WEAK
    diehard_rank_6x8|   0|    100000|     100|0.43869224|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.51790631|  PASSED
        diehard_opso|   0|   2097152|     100|0.10981483|  PASSED
        diehard_oqso|   0|   2097152|     100|0.60631341|  PASSED
         diehard_dna|   0|   2097152|     100|0.76294163|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.58593309|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.54537685|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.43926798|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.30528834|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.93238235|  PASSED
     diehard_squeeze|   0|    100000|     100|0.71566064|  PASSED
        diehard_sums|   0|       100|     100|0.04693740|  PASSED
        diehard_runs|   0|    100000|     100|0.06281566|  PASSED
        diehard_runs|   0|    100000|     100|0.57733333|  PASSED
       diehard_craps|   0|    200000|     100|0.13714803|  PASSED
       diehard_craps|   0|    200000|     100|0.98265932|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.18618355|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.44703731|  PASSED
         sts_monobit|   1|    100000|     100|0.14891195|  PASSED
            sts_runs|   2|    100000|     100|0.43324312|  PASSED
          sts_serial|   1|    100000|     100|0.47435200|  PASSED
          sts_serial|   2|    100000|     100|0.99188276|  PASSED
          sts_serial|   3|    100000|     100|0.48730965|  PASSED
          sts_serial|   3|    100000|     100|0.62393550|  PASSED
          sts_serial|   4|    100000|     100|0.67800370|  PASSED
          sts_serial|   4|    100000|     100|0.48515525|  PASSED
          sts_serial|   5|    100000|     100|0.36222573|  PASSED
          sts_serial|   5|    100000|     100|0.40031788|  PASSED
          sts_serial|   6|    100000|     100|0.49251350|  PASSED
          sts_serial|   6|    100000|     100|0.82981091|  PASSED
          sts_serial|   7|    100000|     100|0.22998622|  PASSED
          sts_serial|   7|    100000|     100|0.68754139|  PASSED
          sts_serial|   8|    100000|     100|0.76609441|  PASSED
          sts_serial|   8|    100000|     100|0.83708414|  PASSED
          sts_serial|   9|    100000|     100|0.17202093|  PASSED
          sts_serial|   9|    100000|     100|0.76781919|  PASSED
          sts_serial|  10|    100000|     100|0.29347505|  PASSED
          sts_serial|  10|    100000|     100|0.73141999|  PASSED
          sts_serial|  11|    100000|     100|0.20687591|  PASSED
          sts_serial|  11|    100000|     100|0.28714887|  PASSED
          sts_serial|  12|    100000|     100|0.42870266|  PASSED
          sts_serial|  12|    100000|     100|0.87198591|  PASSED
          sts_serial|  13|    100000|     100|0.67952205|  PASSED
          sts_serial|  13|    100000|     100|0.90040381|  PASSED
          sts_serial|  14|    100000|     100|0.47783157|  PASSED
          sts_serial|  14|    100000|     100|0.29673357|  PASSED
          sts_serial|  15|    100000|     100|0.58683433|  PASSED
          sts_serial|  15|    100000|     100|0.56957404|  PASSED
          sts_serial|  16|    100000|     100|0.55065642|  PASSED
          sts_serial|  16|    100000|     100|0.44711364|  PASSED
         rgb_bitdist|   1|    100000|     100|0.99558359|   WEAK
         rgb_bitdist|   2|    100000|     100|0.84541057|  PASSED
         rgb_bitdist|   3|    100000|     100|0.72294422|  PASSED
         rgb_bitdist|   4|    100000|     100|0.19307817|  PASSED
         rgb_bitdist|   5|    100000|     100|0.39787190|  PASSED
         rgb_bitdist|   6|    100000|     100|0.09763419|  PASSED
         rgb_bitdist|   7|    100000|     100|0.99377017|  PASSED
         rgb_bitdist|   8|    100000|     100|0.78680213|  PASSED
         rgb_bitdist|   9|    100000|     100|0.35158765|  PASSED
         rgb_bitdist|  10|    100000|     100|0.12186141|  PASSED
         rgb_bitdist|  11|    100000|     100|0.25908486|  PASSED
         rgb_bitdist|  12|    100000|     100|0.34952818|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.20091929|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.29008385|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.99956253|   WEAK
rgb_minimum_distance|   5|     10000|    1000|0.47168514|  PASSED
    rgb_permutations|   2|    100000|     100|0.78230631|  PASSED
    rgb_permutations|   3|    100000|     100|0.31709327|  PASSED
    rgb_permutations|   4|    100000|     100|0.81980625|  PASSED
    rgb_permutations|   5|    100000|     100|0.16409584|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.56644746|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.19790893|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.63250292|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.50796283|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.89662248|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.22400269|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.82429517|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.27470582|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.85468958|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.18013910|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.66583049|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.20998725|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.02384474|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.47196235|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.27003776|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.73447867|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.97499985|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.89396085|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.97853045|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.83487947|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.89747024|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.68108824|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.51776330|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.29760628|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.43098994|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.58639982|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.74308865|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.98318701|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.96035026|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.31232924|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.66197983|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.07279157|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.14370340|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.51698782|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.94698069|  PASSED
             dab_dct| 256|     50000|       1|0.22821499|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.68392956|  PASSED
        dab_filltree|  32|  15000000|       1|0.13069417|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.05468647|  PASSED
       dab_filltree2|   1|   5000000|       1|0.20799419|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.96763162|  PASSED
#=============================================================================#
# Summary: PASSED=111, WEAK=3, FAILED=0                                       #
#=============================================================================#
#=============================================================================#
# Runtime: 0:50:05                                                            #
#=============================================================================#
*/
