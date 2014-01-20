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
package org.jenetics.util;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Implementation of the XOR shift PRNG.
 *
 * @see <a href="http://www.jstatsoft.org/v08/i14/paper">
 *      Xorshift RNGs, George Marsaglia</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-01-20 $</em>
 * @since @__version__@
 */
public class XOR32ShiftRandom extends Random32 {
	private static final long serialVersionUID = 1L;

	/**
	 * Parameter class for the {@code XOR32ShiftRandom} generator. The three
	 * integer parameters are used in the PRNG as follows:
	 *
	 * [code]
	 * y ^= y << a;
	 * y ^= y >> b;
	 * return y ^= y << c;
	 * [/code]
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version @__version__@ &mdash; <em>$Date: 2014-01-20 $</em>
	 * @since @__version__@
	 */
	public static final class Param implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * Contains a list of the parameters with the highest 'dieharder'
		 * scores.
		 */
		public static final Param[] PARAMS = {
			new Param(12, 21, 5),  // p=104, w=5, f=5
			new Param(5, 21, 12),  // p=104, w=4, f=6
			new Param(5, 19, 13),  // p=103, w=4, f=7
			new Param(10, 21, 9),  // p=103, w=2, f=9
			new Param(5, 17, 13),  // p=102, w=5, f=7
			new Param(3, 13, 7),   // p=102, w=3, f=9
			new Param(9, 17, 6),   // p=102, w=3, f=9
			new Param(17, 11, 13), // p=102, w=3, f=9
			new Param(25, 9, 5),   // p=102, w=3, f=9
			new Param(7, 13, 3),   // p=101, w=7, f=6
			new Param(25, 13, 7),  // p=101, w=4, f=9
			new Param(25, 9, 10),  // p=101, w=4, f=9
			new Param(6, 17, 9),   // p=100, w=6, f=8
			new Param(13, 17, 5),  // p=100, w=5, f=9
			new Param(7, 21, 6),   // p=100, w=5, f=9
			new Param(9, 21, 2),   // p=100, w=4, f=10
			new Param(8, 23, 7),   // p=100, w=4, f=10
			new Param(19, 9, 11),  // p=100, w=4, f=10
			new Param(6, 21, 7),   // p=100, w=2, f=12
			new Param(20, 5, 3),   // p=99, w=6, f=9
			new Param(13, 17, 11), // p=99, w=5, f=10
			new Param(16, 7, 11),  // p=99, w=5, f=10
			new Param(4, 5, 15),   // p=99, w=4, f=11
			new Param(17, 13, 3),  // p=99, w=4, f=11
			new Param(5, 6, 13),   // p=99, w=2, f=13
			new Param(2, 15, 9),   // p=98, w=7, f=9
			new Param(12, 25, 7),  // p=98, w=7, f=9
			new Param(5, 7, 9),    // p=98, w=6, f=10
			new Param(9, 5, 7),    // p=98, w=6, f=10
			new Param(7, 25, 13),  // p=98, w=5, f=11
			new Param(6, 5, 13),   // p=98, w=5, f=11
			new Param(22, 5, 3),   // p=98, w=5, f=11
			new Param(2, 9, 7),    // p=98, w=5, f=11
			new Param(9, 12, 23),  // p=98, w=4, f=12
			new Param(11, 17, 13), // p=97, w=9, f=8
			new Param(7, 23, 8),   // p=97, w=6, f=11
			new Param(23, 12, 9),  // p=97, w=6, f=11
			new Param(13, 6, 5),   // p=97, w=6, f=11
			new Param(6, 13, 21),  // p=97, w=5, f=12
			new Param(7, 25, 12),  // p=97, w=5, f=12
			new Param(21, 13, 6),  // p=97, w=4, f=13
			new Param(13, 11, 17), // p=97, w=4, f=13
			new Param(7, 9, 2),    // p=97, w=4, f=13
			new Param(13, 5, 6),   // p=97, w=4, f=13
			new Param(21, 7, 6),   // p=97, w=3, f=14
			new Param(5, 25, 3),   // p=97, w=2, f=15
			new Param(11, 19, 9),  // p=97, w=10, f=7
			new Param(2, 15, 5),   // p=97, w=1, f=16
			new Param(9, 21, 10),  // p=96, w=8, f=10
			new Param(7, 5, 9),    // p=96, w=8, f=10
			new Param(11, 6, 1),   // p=96, w=8, f=10
			new Param(2, 21, 9),   // p=96, w=6, f=12
			new Param(1, 6, 11),   // p=96, w=6, f=12
			new Param(11, 9, 19),  // p=96, w=5, f=13
			new Param(1, 16, 11),  // p=96, w=4, f=14
			new Param(15, 5, 4),   // p=96, w=4, f=14
			new Param(23, 9, 12),  // p=96, w=4, f=14
			new Param(10, 9, 25),  // p=96, w=4, f=14
			new Param(5, 9, 25),   // p=96, w=3, f=15
			new Param(2, 7, 9),    // p=95, w=9, f=10
			new Param(13, 19, 5),  // p=95, w=8, f=11
			new Param(11, 7, 16),  // p=95, w=7, f=12
			new Param(11, 21, 13), // p=95, w=7, f=12
			new Param(25, 5, 9),   // p=95, w=6, f=13
			new Param(21, 5, 3),   // p=95, w=6, f=13
			new Param(9, 15, 2),   // p=95, w=5, f=14
			new Param(16, 11, 7),  // p=95, w=5, f=14
			new Param(9, 7, 2),    // p=95, w=4, f=15
			new Param(1, 9, 5),    // p=95, w=3, f=16
			new Param(6, 17, 3),   // p=95, w=1, f=18
			new Param(15, 17, 20), // p=95, w=0, f=19
			new Param(15, 9, 2),   // p=94, w=8, f=12
			new Param(25, 7, 13),  // p=94, w=8, f=12
			new Param(8, 27, 5),   // p=94, w=7, f=13
			new Param(12, 9, 23),  // p=94, w=6, f=14
			new Param(28, 9, 5),   // p=94, w=6, f=14
			new Param(7, 11, 16),  // p=94, w=4, f=16
			new Param(9, 23, 8),   // p=94, w=4, f=16
			new Param(23, 15, 17), // p=94, w=4, f=16
			new Param(27, 8, 5),   // p=94, w=4, f=16
			new Param(19, 5, 13),  // p=94, w=4, f=16
			new Param(9, 10, 25),  // p=94, w=4, f=16
			new Param(17, 4, 3),   // p=94, w=2, f=18
			new Param(13, 25, 7),  // p=93, w=9, f=12
			new Param(3, 13, 17),  // p=93, w=7, f=14
			new Param(8, 23, 9),   // p=93, w=7, f=14
			new Param(5, 27, 8),   // p=93, w=7, f=14
			new Param(9, 21, 16),  // p=93, w=3, f=18
			new Param(16, 21, 9),  // p=93, w=3, f=18
			new Param(20, 15, 17), // p=93, w=2, f=19
			new Param(13, 5, 19),  // p=93, w=1, f=20
			new Param(7, 13, 25),  // p=92, w=8, f=14
			new Param(3, 5, 20),   // p=92, w=7, f=15
			new Param(19, 5, 1),   // p=92, w=7, f=15
			new Param(3, 17, 4),   // p=92, w=4, f=18
			new Param(5, 2, 15),   // p=92, w=4, f=18
			new Param(3, 17, 6),   // p=92, w=3, f=19
			new Param(17, 3, 6),   // p=92, w=3, f=19
			new Param(25, 5, 3),   // p=92, w=11, f=11
			new Param(5, 9, 1),    // p=91, w=6, f=17
			new Param(3, 25, 5),   // p=91, w=6, f=17
			new Param(27, 3, 13),  // p=91, w=4, f=19
			new Param(13, 7, 25),  // p=91, w=2, f=21
			new Param(27, 5, 8),   // p=91, w=11, f=12
			new Param(17, 6, 3),   // p=90, w=8, f=16
			new Param(17, 6, 9),   // p=90, w=8, f=16
			new Param(25, 7, 12),  // p=90, w=8, f=16
			new Param(25, 10, 9),  // p=90, w=8, f=16
			new Param(15, 2, 5),   // p=90, w=7, f=17
			new Param(7, 25, 20),  // p=90, w=5, f=19
			new Param(21, 2, 5),   // p=90, w=4, f=20
			new Param(23, 8, 7),   // p=90, w=10, f=14
			new Param(5, 27, 25),  // p=90, w=1, f=23
		};

		public static final Param DEFAULT = PARAMS[0];

		public final int a;
		public final int b;
		public final int c;

		/**
		 *
		 * @param a first shift parameter
		 * @param b second shift parameter
		 * @param c third shift parameter
		 */
		public Param(final int a, final int b, final int c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(new int[]{a, b, c});
		}

		@Override
		public boolean equals(final Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof Param)) {
				return false;
			}

			final Param param = (Param)other;
			return a == param.a && b == param.b && c == param.c;
		}

		@Override
		public String toString() {
			return String.format("Param[%d, %d, %d]", a, b, c);
		}
	}


	private final Param _param;
	private final long _seed;

	private int _x = 0;

	public XOR32ShiftRandom(final long seed, final Param param) {
		_param = requireNonNull(param, "PRNG param must not be null.");
		_seed = seed;

		_x = (int)_seed;
	}

	public XOR32ShiftRandom(final long seed) {
		this(seed, Param.DEFAULT);
	}

	public XOR32ShiftRandom(final Param param) {
		this(math.random.seed(), Param.DEFAULT);
	}

	public XOR32ShiftRandom() {
		this(math.random.seed(), Param.DEFAULT);
	}

	@Override
	public int nextInt() {
		_x ^= _x << _param.a; _x ^= _x >> _param.b; return _x ^= _x << _param.c;

		// Additional shift variants.
//		_x ^= _x << _param.c; _x ^= _x >> _param.b; return _x ^= _x << _param.a;
//		_x ^= _x >> _param.a; _x ^= _x << _param.b; return _x ^= _x >> _param.c;
//		_x ^= _x >> _param.c; _x ^= _x << _param.b; return _x ^= _x >> _param.a;
//		_x ^= _x << _param.a; _x ^= _x << _param.c; return _x ^= _x >> _param.b;
//		_x ^= _x << _param.c; _x ^= _x << _param.a; return _x ^= _x >> _param.b;
//		_x ^= _x >> _param.a; _x ^= _x >> _param.c; return _x ^= _x << _param.b;
//		_x ^= _x >> _param.c; _x ^= _x >> _param.a; return _x ^= _x << _param.b;
	}

	// https://bugs.webkit.org/attachment.cgi?id=191670&action=prettypatch
	/*
	int nextInt_1() {
		return (int)((_x += (_x*_x | 5)) >> 32);
	}
	*/

	@Override
	public String toString() {
		return String.format("XOR32ShiftRandom[%s]", _param);
	}

}

/*
#=============================================================================#
# Testing: org.jenetics.util.XOR32ShiftRandom (2014-01-17 21:00)              #
#=============================================================================#
#=============================================================================#
# Linux 3.2.0-58-generic (amd64)                                              #
# java version "1.7.0_51"                                                     #
# Java(TM) SE Runtime Environment (build 1.7.0_51-b13)                        #
# Java HotSpot(TM) 64-Bit Server VM (build 24.51-b03)                         #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  2.20e+07  |1242060020|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.30950683|  PASSED
      diehard_operm5|   0|   1000000|     100|0.79705199|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.00000000|  FAILED
    diehard_rank_6x8|   0|    100000|     100|0.30563238|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.32923512|  PASSED
        diehard_opso|   0|   2097152|     100|0.38780253|  PASSED
        diehard_oqso|   0|   2097152|     100|0.09761295|  PASSED
         diehard_dna|   0|   2097152|     100|0.57737170|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.00001572|   WEAK
diehard_count_1s_byt|   0|    256000|     100|0.03392922|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.99225529|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.02828560|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.72864595|  PASSED
     diehard_squeeze|   0|    100000|     100|0.60202279|  PASSED
        diehard_sums|   0|       100|     100|0.51188987|  PASSED
        diehard_runs|   0|    100000|     100|0.19276195|  PASSED
        diehard_runs|   0|    100000|     100|0.94354967|  PASSED
       diehard_craps|   0|    200000|     100|0.92127940|  PASSED
       diehard_craps|   0|    200000|     100|0.37151236|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.88506715|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.00000000|  FAILED
         sts_monobit|   1|    100000|     100|0.63228419|  PASSED
            sts_runs|   2|    100000|     100|0.18125543|  PASSED
          sts_serial|   1|    100000|     100|0.97894134|  PASSED
          sts_serial|   2|    100000|     100|0.25322744|  PASSED
          sts_serial|   3|    100000|     100|0.45359344|  PASSED
          sts_serial|   3|    100000|     100|0.52816287|  PASSED
          sts_serial|   4|    100000|     100|0.22600369|  PASSED
          sts_serial|   4|    100000|     100|0.74113823|  PASSED
          sts_serial|   5|    100000|     100|0.04026766|  PASSED
          sts_serial|   5|    100000|     100|0.62353264|  PASSED
          sts_serial|   6|    100000|     100|0.02053354|  PASSED
          sts_serial|   6|    100000|     100|0.12426509|  PASSED
          sts_serial|   7|    100000|     100|0.69392232|  PASSED
          sts_serial|   7|    100000|     100|0.35916047|  PASSED
          sts_serial|   8|    100000|     100|0.11621992|  PASSED
          sts_serial|   8|    100000|     100|0.20126736|  PASSED
          sts_serial|   9|    100000|     100|0.20213606|  PASSED
          sts_serial|   9|    100000|     100|0.73425338|  PASSED
          sts_serial|  10|    100000|     100|0.75765904|  PASSED
          sts_serial|  10|    100000|     100|0.90859946|  PASSED
          sts_serial|  11|    100000|     100|0.85104939|  PASSED
          sts_serial|  11|    100000|     100|0.48702250|  PASSED
          sts_serial|  12|    100000|     100|0.47717792|  PASSED
          sts_serial|  12|    100000|     100|0.86740091|  PASSED
          sts_serial|  13|    100000|     100|0.91940781|  PASSED
          sts_serial|  13|    100000|     100|0.89791450|  PASSED
          sts_serial|  14|    100000|     100|0.56057314|  PASSED
          sts_serial|  14|    100000|     100|0.73918359|  PASSED
          sts_serial|  15|    100000|     100|0.60626596|  PASSED
          sts_serial|  15|    100000|     100|0.71033522|  PASSED
          sts_serial|  16|    100000|     100|0.98138308|  PASSED
          sts_serial|  16|    100000|     100|0.23925839|  PASSED
         rgb_bitdist|   1|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   2|    100000|     100|0.00482217|   WEAK
         rgb_bitdist|   3|    100000|     100|0.84384531|  PASSED
         rgb_bitdist|   4|    100000|     100|0.62489373|  PASSED
         rgb_bitdist|   5|    100000|     100|0.85461154|  PASSED
         rgb_bitdist|   6|    100000|     100|0.05177046|  PASSED
         rgb_bitdist|   7|    100000|     100|0.64569426|  PASSED
         rgb_bitdist|   8|    100000|     100|0.10481618|  PASSED
         rgb_bitdist|   9|    100000|     100|0.36540944|  PASSED
         rgb_bitdist|  10|    100000|     100|0.72964713|  PASSED
         rgb_bitdist|  11|    100000|     100|0.87412574|  PASSED
         rgb_bitdist|  12|    100000|     100|0.70164888|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.00035168|   WEAK
rgb_minimum_distance|   3|     10000|    1000|0.00001032|   WEAK
rgb_minimum_distance|   4|     10000|    1000|0.62001972|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.00427931|   WEAK
    rgb_permutations|   2|    100000|     100|0.33994082|  PASSED
    rgb_permutations|   3|    100000|     100|0.14155115|  PASSED
    rgb_permutations|   4|    100000|     100|0.93932048|  PASSED
    rgb_permutations|   5|    100000|     100|0.90801947|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.85518768|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.16689947|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.68840664|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.48044407|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.95919751|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.78025435|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.40750439|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.16807295|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.38280743|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.90768880|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.74588623|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.82053683|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.30518643|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.78977228|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.72833668|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.83574957|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.21718338|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.71817208|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.89149142|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.48001144|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.95771682|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.96208735|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.11337933|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.94304419|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.90389989|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.76444950|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.60745970|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.75786125|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.97964140|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.76863755|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.57756005|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.92521604|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.12525451|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.28965414|  PASSED
     dab_bytedistrib|   0|  51200000|       1|1.00000000|  FAILED
             dab_dct| 256|     50000|       1|0.03027039|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.07937117|  PASSED
        dab_filltree|  32|  15000000|       1|0.29084245|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.94530616|  PASSED
       dab_filltree2|   1|   5000000|       1|0.10930471|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|1.00000000|  FAILED
#=============================================================================#
# Summary: PASSED: 104, WEAK: 5, FAILED: 5                                    #
#=============================================================================#
#=============================================================================#
# Runtime: 1:01:12                                                            #
#=============================================================================#
 */
