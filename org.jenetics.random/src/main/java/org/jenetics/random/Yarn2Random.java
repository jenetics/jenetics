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
		this(param, math.seed());
	}

	public Yarn2Random(final long seed) {
		this(Param.DEFAULT, seed);
	}

	public Yarn2Random() {
		this(Param.DEFAULT, math.seed());
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
# Testing: org.jenetix.random.Yarn2Random (2014-07-28 07:08)                  #
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
stdin_input_raw|  3.88e+06  |3359239719|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.98411877|  PASSED
      diehard_operm5|   0|   1000000|     100|0.59348079|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.89126805|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.82063684|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.94915981|  PASSED
        diehard_opso|   0|   2097152|     100|0.40675195|  PASSED
        diehard_oqso|   0|   2097152|     100|0.64787469|  PASSED
         diehard_dna|   0|   2097152|     100|0.38685987|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.01645734|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.95266312|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.02393331|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.45374777|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.26881250|  PASSED
     diehard_squeeze|   0|    100000|     100|0.14951580|  PASSED
        diehard_sums|   0|       100|     100|0.09287450|  PASSED
        diehard_runs|   0|    100000|     100|0.37386892|  PASSED
        diehard_runs|   0|    100000|     100|0.77302294|  PASSED
       diehard_craps|   0|    200000|     100|0.83549618|  PASSED
       diehard_craps|   0|    200000|     100|0.56168796|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.44862652|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.84852232|  PASSED
         sts_monobit|   1|    100000|     100|0.35522162|  PASSED
            sts_runs|   2|    100000|     100|0.63207751|  PASSED
          sts_serial|   1|    100000|     100|0.59514373|  PASSED
          sts_serial|   2|    100000|     100|0.72315146|  PASSED
          sts_serial|   3|    100000|     100|0.99690095|   WEAK
          sts_serial|   3|    100000|     100|0.68745080|  PASSED
          sts_serial|   4|    100000|     100|0.57108640|  PASSED
          sts_serial|   4|    100000|     100|0.31592528|  PASSED
          sts_serial|   5|    100000|     100|0.96355812|  PASSED
          sts_serial|   5|    100000|     100|0.52320121|  PASSED
          sts_serial|   6|    100000|     100|0.49886152|  PASSED
          sts_serial|   6|    100000|     100|0.64947831|  PASSED
          sts_serial|   7|    100000|     100|0.24438927|  PASSED
          sts_serial|   7|    100000|     100|0.57422216|  PASSED
          sts_serial|   8|    100000|     100|0.11477635|  PASSED
          sts_serial|   8|    100000|     100|0.01564416|  PASSED
          sts_serial|   9|    100000|     100|0.14744442|  PASSED
          sts_serial|   9|    100000|     100|0.48704009|  PASSED
          sts_serial|  10|    100000|     100|0.73870840|  PASSED
          sts_serial|  10|    100000|     100|0.23823936|  PASSED
          sts_serial|  11|    100000|     100|0.42701055|  PASSED
          sts_serial|  11|    100000|     100|0.24808021|  PASSED
          sts_serial|  12|    100000|     100|0.80989268|  PASSED
          sts_serial|  12|    100000|     100|0.85913658|  PASSED
          sts_serial|  13|    100000|     100|0.84359805|  PASSED
          sts_serial|  13|    100000|     100|0.44261183|  PASSED
          sts_serial|  14|    100000|     100|0.92406623|  PASSED
          sts_serial|  14|    100000|     100|0.75294081|  PASSED
          sts_serial|  15|    100000|     100|0.33286431|  PASSED
          sts_serial|  15|    100000|     100|0.05571658|  PASSED
          sts_serial|  16|    100000|     100|0.44767426|  PASSED
          sts_serial|  16|    100000|     100|0.94029466|  PASSED
         rgb_bitdist|   1|    100000|     100|0.45988858|  PASSED
         rgb_bitdist|   2|    100000|     100|0.41845436|  PASSED
         rgb_bitdist|   3|    100000|     100|0.57609104|  PASSED
         rgb_bitdist|   4|    100000|     100|0.93875549|  PASSED
         rgb_bitdist|   5|    100000|     100|0.80600436|  PASSED
         rgb_bitdist|   6|    100000|     100|0.02572889|  PASSED
         rgb_bitdist|   7|    100000|     100|0.78486118|  PASSED
         rgb_bitdist|   8|    100000|     100|0.15285217|  PASSED
         rgb_bitdist|   9|    100000|     100|0.54018574|  PASSED
         rgb_bitdist|  10|    100000|     100|0.58683097|  PASSED
         rgb_bitdist|  11|    100000|     100|0.90690380|  PASSED
         rgb_bitdist|  12|    100000|     100|0.46471233|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.72775981|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.61476281|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.54739476|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.81163848|  PASSED
    rgb_permutations|   2|    100000|     100|0.77034418|  PASSED
    rgb_permutations|   3|    100000|     100|0.91598693|  PASSED
    rgb_permutations|   4|    100000|     100|0.34752032|  PASSED
    rgb_permutations|   5|    100000|     100|0.40461108|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.51591689|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.50658184|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.43605661|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.30982784|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.34890392|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.51673727|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.08038668|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.49908416|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.96367857|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.48973271|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.92666000|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.98194237|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.98433052|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.99634714|   WEAK
      rgb_lagged_sum|  14|   1000000|     100|0.83808916|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.94206321|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.38656808|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.15283014|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.12726749|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.35408429|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.61765369|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.56638894|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.97916522|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.15438555|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.92709525|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.07717367|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.13108100|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.14239389|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.59920057|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.31708790|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.14660449|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.92093943|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.97972426|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.36813409|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.09979129|  PASSED
             dab_dct| 256|     50000|       1|0.90074205|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.85498777|  PASSED
        dab_filltree|  32|  15000000|       1|0.11140229|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.63880238|  PASSED
       dab_filltree2|   1|   5000000|       1|0.20978880|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.45782098|  PASSED
#=============================================================================#
# Summary: PASSED=112, WEAK=2, FAILED=0                                       #
#          235,031.480 MB of random data created with 13.592 MB/sec           #
#=============================================================================#
#=============================================================================#
# Runtime: 4:48:12                                                            #
#=============================================================================#
*/
