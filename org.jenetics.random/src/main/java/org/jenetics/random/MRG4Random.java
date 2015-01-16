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
		this(param, math.seed());
	}

	public MRG4Random(final long seed) {
		this(Param.DEFAULT, seed);
	}

	public MRG4Random() {
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
# Testing: org.jenetix.random.MRG4Random (2014-07-28 02:56)                   #
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
stdin_input_raw|  2.47e+07  |2384103511|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.90473425|  PASSED
      diehard_operm5|   0|   1000000|     100|0.84432400|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.09157925|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.09246489|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.98464030|  PASSED
        diehard_opso|   0|   2097152|     100|0.80577998|  PASSED
        diehard_oqso|   0|   2097152|     100|0.05507160|  PASSED
         diehard_dna|   0|   2097152|     100|0.32580849|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.92335092|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.70820792|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.06044361|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.81325772|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.07151486|  PASSED
     diehard_squeeze|   0|    100000|     100|0.99389073|  PASSED
        diehard_sums|   0|       100|     100|0.32108349|  PASSED
        diehard_runs|   0|    100000|     100|0.22516326|  PASSED
        diehard_runs|   0|    100000|     100|0.67785773|  PASSED
       diehard_craps|   0|    200000|     100|0.98961811|  PASSED
       diehard_craps|   0|    200000|     100|0.71239404|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.49084381|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.66572396|  PASSED
         sts_monobit|   1|    100000|     100|0.09338861|  PASSED
            sts_runs|   2|    100000|     100|0.27852826|  PASSED
          sts_serial|   1|    100000|     100|0.10920521|  PASSED
          sts_serial|   2|    100000|     100|0.42331361|  PASSED
          sts_serial|   3|    100000|     100|0.53546983|  PASSED
          sts_serial|   3|    100000|     100|0.43889443|  PASSED
          sts_serial|   4|    100000|     100|0.31434086|  PASSED
          sts_serial|   4|    100000|     100|0.92553600|  PASSED
          sts_serial|   5|    100000|     100|0.73320377|  PASSED
          sts_serial|   5|    100000|     100|0.15095118|  PASSED
          sts_serial|   6|    100000|     100|0.66976764|  PASSED
          sts_serial|   6|    100000|     100|0.57245306|  PASSED
          sts_serial|   7|    100000|     100|0.90082897|  PASSED
          sts_serial|   7|    100000|     100|0.90590218|  PASSED
          sts_serial|   8|    100000|     100|0.92937732|  PASSED
          sts_serial|   8|    100000|     100|0.83894691|  PASSED
          sts_serial|   9|    100000|     100|0.97198877|  PASSED
          sts_serial|   9|    100000|     100|0.98744538|  PASSED
          sts_serial|  10|    100000|     100|0.68759870|  PASSED
          sts_serial|  10|    100000|     100|0.76480103|  PASSED
          sts_serial|  11|    100000|     100|0.48904550|  PASSED
          sts_serial|  11|    100000|     100|0.89885258|  PASSED
          sts_serial|  12|    100000|     100|0.23796683|  PASSED
          sts_serial|  12|    100000|     100|0.70178579|  PASSED
          sts_serial|  13|    100000|     100|0.05003847|  PASSED
          sts_serial|  13|    100000|     100|0.28436765|  PASSED
          sts_serial|  14|    100000|     100|0.90804290|  PASSED
          sts_serial|  14|    100000|     100|0.45468402|  PASSED
          sts_serial|  15|    100000|     100|0.73791536|  PASSED
          sts_serial|  15|    100000|     100|0.26001480|  PASSED
          sts_serial|  16|    100000|     100|0.75866475|  PASSED
          sts_serial|  16|    100000|     100|0.28649753|  PASSED
         rgb_bitdist|   1|    100000|     100|0.77905312|  PASSED
         rgb_bitdist|   2|    100000|     100|0.10805039|  PASSED
         rgb_bitdist|   3|    100000|     100|0.74469923|  PASSED
         rgb_bitdist|   4|    100000|     100|0.01001005|  PASSED
         rgb_bitdist|   5|    100000|     100|0.50209565|  PASSED
         rgb_bitdist|   6|    100000|     100|0.20940841|  PASSED
         rgb_bitdist|   7|    100000|     100|0.64490603|  PASSED
         rgb_bitdist|   8|    100000|     100|0.14769242|  PASSED
         rgb_bitdist|   9|    100000|     100|0.06672765|  PASSED
         rgb_bitdist|  10|    100000|     100|0.18292364|  PASSED
         rgb_bitdist|  11|    100000|     100|0.15576468|  PASSED
         rgb_bitdist|  12|    100000|     100|0.74343953|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.39510322|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.63553941|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.91713058|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.61424107|  PASSED
    rgb_permutations|   2|    100000|     100|0.61668194|  PASSED
    rgb_permutations|   3|    100000|     100|0.93753095|  PASSED
    rgb_permutations|   4|    100000|     100|0.63994588|  PASSED
    rgb_permutations|   5|    100000|     100|0.83820591|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.75849740|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.96272842|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.12761018|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.82711316|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.02207546|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.83026866|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.12170389|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.92230900|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.17446872|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.48009022|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.36598776|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.37435901|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.66430499|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.16765115|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.69164175|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.58201925|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.92663539|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.48036141|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.14255711|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.34785915|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.37322884|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.88866197|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.08351948|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.73796487|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.99653693|   WEAK
      rgb_lagged_sum|  25|   1000000|     100|0.96382677|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.64912460|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.98782668|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.25146434|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.25129113|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.15996628|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.98898401|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.34708268|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.19273212|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.05907322|  PASSED
             dab_dct| 256|     50000|       1|0.33937182|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.77655820|  PASSED
        dab_filltree|  32|  15000000|       1|0.07994703|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.51004820|  PASSED
       dab_filltree2|   1|   5000000|       1|0.92079166|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.02559734|  PASSED
#=============================================================================#
# Summary: PASSED=113, WEAK=1, FAILED=0                                       #
#          235,031.164 MB of random data created with 84.367 MB/sec           #
#=============================================================================#
#=============================================================================#
# Runtime: 0:46:25                                                            #
#=============================================================================#
*/
