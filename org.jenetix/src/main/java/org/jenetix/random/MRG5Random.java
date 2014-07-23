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
			long t = modulus.mod(seed);
			if (t < 0) t += modulus.VALUE;

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
		final long t = modulus.add(
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
# Testing: org.jenetix.random.MRG5Random (2014-07-23 01:45)                   #
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
stdin_input_raw|  2.47e+07  |3970114068|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.86314467|  PASSED
      diehard_operm5|   0|   1000000|     100|0.22634833|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.10141317|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.43587228|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.11994804|  PASSED
        diehard_opso|   0|   2097152|     100|0.21874097|  PASSED
        diehard_oqso|   0|   2097152|     100|0.73424677|  PASSED
         diehard_dna|   0|   2097152|     100|0.47183019|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.15156358|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.21079013|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.67544629|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.26727712|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.16287395|  PASSED
     diehard_squeeze|   0|    100000|     100|0.39300681|  PASSED
        diehard_sums|   0|       100|     100|0.21006699|  PASSED
        diehard_runs|   0|    100000|     100|0.74928244|  PASSED
        diehard_runs|   0|    100000|     100|0.92277601|  PASSED
       diehard_craps|   0|    200000|     100|0.58852188|  PASSED
       diehard_craps|   0|    200000|     100|0.66952873|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.00000000|  FAILED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.00000000|  FAILED
         sts_monobit|   1|    100000|     100|0.38583006|  PASSED
            sts_runs|   2|    100000|     100|0.86313015|  PASSED
          sts_serial|   1|    100000|     100|0.72107231|  PASSED
          sts_serial|   2|    100000|     100|0.90458281|  PASSED
          sts_serial|   3|    100000|     100|0.66157188|  PASSED
          sts_serial|   3|    100000|     100|0.47969676|  PASSED
          sts_serial|   4|    100000|     100|0.86506476|  PASSED
          sts_serial|   4|    100000|     100|0.78406929|  PASSED
          sts_serial|   5|    100000|     100|0.84518242|  PASSED
          sts_serial|   5|    100000|     100|0.63994349|  PASSED
          sts_serial|   6|    100000|     100|0.87796220|  PASSED
          sts_serial|   6|    100000|     100|0.98583872|  PASSED
          sts_serial|   7|    100000|     100|0.68989554|  PASSED
          sts_serial|   7|    100000|     100|0.89892005|  PASSED
          sts_serial|   8|    100000|     100|0.52679940|  PASSED
          sts_serial|   8|    100000|     100|0.90179157|  PASSED
          sts_serial|   9|    100000|     100|0.15112508|  PASSED
          sts_serial|   9|    100000|     100|0.99052427|  PASSED
          sts_serial|  10|    100000|     100|0.78614267|  PASSED
          sts_serial|  10|    100000|     100|0.68383645|  PASSED
          sts_serial|  11|    100000|     100|0.52832738|  PASSED
          sts_serial|  11|    100000|     100|0.43264752|  PASSED
          sts_serial|  12|    100000|     100|0.73835551|  PASSED
          sts_serial|  12|    100000|     100|0.71669756|  PASSED
          sts_serial|  13|    100000|     100|0.80677508|  PASSED
          sts_serial|  13|    100000|     100|0.91000056|  PASSED
          sts_serial|  14|    100000|     100|0.95404059|  PASSED
          sts_serial|  14|    100000|     100|0.89983777|  PASSED
          sts_serial|  15|    100000|     100|0.14503341|  PASSED
          sts_serial|  15|    100000|     100|0.32692646|  PASSED
          sts_serial|  16|    100000|     100|0.05407425|  PASSED
          sts_serial|  16|    100000|     100|0.00317404|   WEAK
         rgb_bitdist|   1|    100000|     100|0.72988344|  PASSED
         rgb_bitdist|   2|    100000|     100|0.08314618|  PASSED
         rgb_bitdist|   3|    100000|     100|0.56820465|  PASSED
         rgb_bitdist|   4|    100000|     100|0.82073939|  PASSED
         rgb_bitdist|   5|    100000|     100|0.99299094|  PASSED
         rgb_bitdist|   6|    100000|     100|0.85455611|  PASSED
         rgb_bitdist|   7|    100000|     100|0.14711604|  PASSED
         rgb_bitdist|   8|    100000|     100|0.33805173|  PASSED
         rgb_bitdist|   9|    100000|     100|0.95314983|  PASSED
         rgb_bitdist|  10|    100000|     100|0.44339895|  PASSED
         rgb_bitdist|  11|    100000|     100|0.59965544|  PASSED
         rgb_bitdist|  12|    100000|     100|0.37766445|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.04185391|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.45695955|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.94791794|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.27512774|  PASSED
    rgb_permutations|   2|    100000|     100|0.99246668|  PASSED
    rgb_permutations|   3|    100000|     100|0.71822244|  PASSED
    rgb_permutations|   4|    100000|     100|0.92083621|  PASSED
    rgb_permutations|   5|    100000|     100|0.60852649|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.59144015|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.15329583|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.95366205|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.44603859|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.46076223|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.97645623|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.06111713|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.95174487|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.55963959|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.82515406|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.86925763|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.71434412|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.93774480|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.75160091|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.42849515|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.96315020|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.96297023|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.74111238|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.94006266|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.55497258|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.99741391|   WEAK
      rgb_lagged_sum|  21|   1000000|     100|0.26940294|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.89961326|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.52871796|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.95853898|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.86500127|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.80825610|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.49792547|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.75914928|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.60676678|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.43687155|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.85340533|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.93750263|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.02030589|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.94460133|  PASSED
             dab_dct| 256|     50000|       1|0.85818364|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.91498478|  PASSED
        dab_filltree|  32|  15000000|       1|0.16180518|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.53524393|  PASSED
       dab_filltree2|   1|   5000000|       1|0.61102949|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.39480594|  PASSED
#=============================================================================#
# Runtime: 0:49:52                                                            #
#=============================================================================#
*/
