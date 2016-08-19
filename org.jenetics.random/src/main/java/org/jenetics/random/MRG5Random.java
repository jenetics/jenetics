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
		this(param, math.seed());
	}

	public MRG5Random(final long seed) {
		this(Param.DEFAULT, seed);
	}

	public MRG5Random() {
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
# Testing: org.jenetix.random.MRG5Random (2014-07-28 03:42)                   #
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
stdin_input_raw|  2.49e+07  |4160187180|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.92069498|  PASSED
      diehard_operm5|   0|   1000000|     100|0.54415817|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.01270098|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.33873061|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.29051586|  PASSED
        diehard_opso|   0|   2097152|     100|0.43264755|  PASSED
        diehard_oqso|   0|   2097152|     100|0.19162707|  PASSED
         diehard_dna|   0|   2097152|     100|0.66271617|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.62724374|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.45117090|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.04820412|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.21215126|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.94494890|  PASSED
     diehard_squeeze|   0|    100000|     100|0.40997175|  PASSED
        diehard_sums|   0|       100|     100|0.50077986|  PASSED
        diehard_runs|   0|    100000|     100|0.53015491|  PASSED
        diehard_runs|   0|    100000|     100|0.32937070|  PASSED
       diehard_craps|   0|    200000|     100|0.73999979|  PASSED
       diehard_craps|   0|    200000|     100|0.20790756|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.27958266|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.61822870|  PASSED
         sts_monobit|   1|    100000|     100|0.56318382|  PASSED
            sts_runs|   2|    100000|     100|0.64916752|  PASSED
          sts_serial|   1|    100000|     100|0.65850330|  PASSED
          sts_serial|   2|    100000|     100|0.42858821|  PASSED
          sts_serial|   3|    100000|     100|0.05607234|  PASSED
          sts_serial|   3|    100000|     100|0.36260746|  PASSED
          sts_serial|   4|    100000|     100|0.86305768|  PASSED
          sts_serial|   4|    100000|     100|0.35269316|  PASSED
          sts_serial|   5|    100000|     100|0.71315230|  PASSED
          sts_serial|   5|    100000|     100|0.43581049|  PASSED
          sts_serial|   6|    100000|     100|0.91887157|  PASSED
          sts_serial|   6|    100000|     100|0.85965965|  PASSED
          sts_serial|   7|    100000|     100|0.57345388|  PASSED
          sts_serial|   7|    100000|     100|0.51094444|  PASSED
          sts_serial|   8|    100000|     100|0.53813889|  PASSED
          sts_serial|   8|    100000|     100|0.17128930|  PASSED
          sts_serial|   9|    100000|     100|0.62055400|  PASSED
          sts_serial|   9|    100000|     100|0.87918316|  PASSED
          sts_serial|  10|    100000|     100|0.17792141|  PASSED
          sts_serial|  10|    100000|     100|0.66776913|  PASSED
          sts_serial|  11|    100000|     100|0.04003519|  PASSED
          sts_serial|  11|    100000|     100|0.55904818|  PASSED
          sts_serial|  12|    100000|     100|0.88035130|  PASSED
          sts_serial|  12|    100000|     100|0.33700951|  PASSED
          sts_serial|  13|    100000|     100|0.92557347|  PASSED
          sts_serial|  13|    100000|     100|0.95515705|  PASSED
          sts_serial|  14|    100000|     100|0.61051660|  PASSED
          sts_serial|  14|    100000|     100|0.48713630|  PASSED
          sts_serial|  15|    100000|     100|0.18496374|  PASSED
          sts_serial|  15|    100000|     100|0.07274052|  PASSED
          sts_serial|  16|    100000|     100|0.04039699|  PASSED
          sts_serial|  16|    100000|     100|0.61126347|  PASSED
         rgb_bitdist|   1|    100000|     100|0.94828848|  PASSED
         rgb_bitdist|   2|    100000|     100|0.73953872|  PASSED
         rgb_bitdist|   3|    100000|     100|0.48207762|  PASSED
         rgb_bitdist|   4|    100000|     100|0.68964473|  PASSED
         rgb_bitdist|   5|    100000|     100|0.98589427|  PASSED
         rgb_bitdist|   6|    100000|     100|0.85607161|  PASSED
         rgb_bitdist|   7|    100000|     100|0.97380185|  PASSED
         rgb_bitdist|   8|    100000|     100|0.19322204|  PASSED
         rgb_bitdist|   9|    100000|     100|0.64128485|  PASSED
         rgb_bitdist|  10|    100000|     100|0.06720830|  PASSED
         rgb_bitdist|  11|    100000|     100|0.63951737|  PASSED
         rgb_bitdist|  12|    100000|     100|0.03388670|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.45390359|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.81036053|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.04070774|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.29225968|  PASSED
    rgb_permutations|   2|    100000|     100|0.43639691|  PASSED
    rgb_permutations|   3|    100000|     100|0.97889721|  PASSED
    rgb_permutations|   4|    100000|     100|0.91366514|  PASSED
    rgb_permutations|   5|    100000|     100|0.91216236|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.41184038|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.42114809|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.49087594|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.63429418|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.46835606|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.81889004|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.96208629|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.96069308|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.99157468|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.46399883|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.67670398|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.97132131|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.18902774|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.59245861|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.66418480|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.64512686|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.68422311|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.41229356|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.99200116|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.39337702|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.19229924|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.19997498|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.90460328|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.81428644|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.83183740|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.92864092|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.73156835|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.11214511|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.65834968|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.01216053|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.77207029|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.57925735|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.33263196|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.68981195|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.99928931|   WEAK
             dab_dct| 256|     50000|       1|0.58230667|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.86561106|  PASSED
        dab_filltree|  32|  15000000|       1|0.46835024|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.92535512|  PASSED
       dab_filltree2|   1|   5000000|       1|0.02796523|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.93748309|  PASSED
#=============================================================================#
# Summary: PASSED=113, WEAK=1, FAILED=0                                       #
#          235,031.211 MB of random data created with 78.962 MB/sec           #
#=============================================================================#
#=============================================================================#
# Runtime: 0:49:36                                                            #
#=============================================================================#
*/
