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

import org.jenetics.util.Random32;
import org.jenetics.util.math.random;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__! &mdash; <em>$Date: 2014-07-14 $</em>
 * @since !__version__!
 */
public class KISSRandom extends Random32 {

	private static final class State {
		int x;
		int y;
		int z;
		int c;

		State(final long seed1, final long seed2) {
			x = (int)(seed1 >>> Integer.SIZE);
			y = (int)seed1;
			z = (int)(seed2 >>> Integer.SIZE);
			c = (int)seed2;
		}

		State(final long seed) {
			this(seed, mix(seed));
		}

		private static long mix(final long a) {
			long c = a^Long.rotateLeft(a, 7);
			c ^= c << 17;
			c ^= c >>> 31;
			c ^= c << 8;
			return c;
		}

		int next() {
			x = 69069*x + 12345;
			y ^= y << 13;
			y ^= y >> 17;
			y ^= y << 5;

			long t, a = 698769069;
			t = a*z + c;
			c = (int)(t >> 32);
			z = (int)t;

			return x + y + z;
		}

	}

	private final State state = new State(random.seed(), random.seed());

	@Override
	public int nextInt() {
		return state.next();
	}
}

/*
#=============================================================================#
# Testing: org.jenetix.random.KISSRandom (2014-07-12 22:38)                   #
#=============================================================================#
#=============================================================================#
# Linux 3.13.0-30-generic (amd64)                                             #
# java version "1.8.0_05"                                                     #
# Java(TM) SE Runtime Environment (build 1.8.0_05-b13)                        #
# Java HotSpot(TM) 64-Bit Server VM (build 25.5-b02)                          #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  3.25e+07  |1597832263|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.37397998|  PASSED
      diehard_operm5|   0|   1000000|     100|0.62965616|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.57132083|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.31680773|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.40756872|  PASSED
        diehard_opso|   0|   2097152|     100|0.94296990|  PASSED
        diehard_oqso|   0|   2097152|     100|0.98683682|  PASSED
         diehard_dna|   0|   2097152|     100|0.80096688|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.87522706|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.86446615|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.45040839|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.99835430|   WEAK
    diehard_3dsphere|   3|      4000|     100|0.53955777|  PASSED
     diehard_squeeze|   0|    100000|     100|0.36060764|  PASSED
        diehard_sums|   0|       100|     100|0.14449701|  PASSED
        diehard_runs|   0|    100000|     100|0.22247398|  PASSED
        diehard_runs|   0|    100000|     100|0.22552927|  PASSED
       diehard_craps|   0|    200000|     100|0.28357001|  PASSED
       diehard_craps|   0|    200000|     100|0.71054193|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.91232369|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.56624446|  PASSED
         sts_monobit|   1|    100000|     100|0.32030720|  PASSED
            sts_runs|   2|    100000|     100|0.93501093|  PASSED
          sts_serial|   1|    100000|     100|0.34105609|  PASSED
          sts_serial|   2|    100000|     100|0.25100277|  PASSED
          sts_serial|   3|    100000|     100|0.53414048|  PASSED
          sts_serial|   3|    100000|     100|0.53426101|  PASSED
          sts_serial|   4|    100000|     100|0.80499754|  PASSED
          sts_serial|   4|    100000|     100|0.82786339|  PASSED
          sts_serial|   5|    100000|     100|0.74399765|  PASSED
          sts_serial|   5|    100000|     100|0.61590740|  PASSED
          sts_serial|   6|    100000|     100|0.11666316|  PASSED
          sts_serial|   6|    100000|     100|0.19725919|  PASSED
          sts_serial|   7|    100000|     100|0.22953756|  PASSED
          sts_serial|   7|    100000|     100|0.19323036|  PASSED
          sts_serial|   8|    100000|     100|0.22149063|  PASSED
          sts_serial|   8|    100000|     100|0.33520622|  PASSED
          sts_serial|   9|    100000|     100|0.40015496|  PASSED
          sts_serial|   9|    100000|     100|0.61978633|  PASSED
          sts_serial|  10|    100000|     100|0.82265500|  PASSED
          sts_serial|  10|    100000|     100|0.95172538|  PASSED
          sts_serial|  11|    100000|     100|0.91468281|  PASSED
          sts_serial|  11|    100000|     100|0.95503794|  PASSED
          sts_serial|  12|    100000|     100|0.93847010|  PASSED
          sts_serial|  12|    100000|     100|0.90392772|  PASSED
          sts_serial|  13|    100000|     100|0.33602781|  PASSED
          sts_serial|  13|    100000|     100|0.30816007|  PASSED
          sts_serial|  14|    100000|     100|0.18697488|  PASSED
          sts_serial|  14|    100000|     100|0.58887897|  PASSED
          sts_serial|  15|    100000|     100|0.23389568|  PASSED
          sts_serial|  15|    100000|     100|0.20567543|  PASSED
          sts_serial|  16|    100000|     100|0.11831113|  PASSED
          sts_serial|  16|    100000|     100|0.75531410|  PASSED
         rgb_bitdist|   1|    100000|     100|0.01058030|  PASSED
         rgb_bitdist|   2|    100000|     100|0.50328899|  PASSED
         rgb_bitdist|   3|    100000|     100|0.54307428|  PASSED
         rgb_bitdist|   4|    100000|     100|0.67555551|  PASSED
         rgb_bitdist|   5|    100000|     100|0.98132976|  PASSED
         rgb_bitdist|   6|    100000|     100|0.15135325|  PASSED
         rgb_bitdist|   7|    100000|     100|0.95085545|  PASSED
         rgb_bitdist|   8|    100000|     100|0.00821616|  PASSED
         rgb_bitdist|   9|    100000|     100|0.37004904|  PASSED
         rgb_bitdist|  10|    100000|     100|0.71769839|  PASSED
         rgb_bitdist|  11|    100000|     100|0.67281224|  PASSED
         rgb_bitdist|  12|    100000|     100|0.94079708|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.68839070|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.68294062|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.29800742|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.42919320|  PASSED
    rgb_permutations|   2|    100000|     100|0.99833719|   WEAK
    rgb_permutations|   3|    100000|     100|0.73965333|  PASSED
    rgb_permutations|   4|    100000|     100|0.14160971|  PASSED
    rgb_permutations|   5|    100000|     100|0.12205361|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.27720974|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.99438714|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.02108443|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.68176672|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.66451197|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.12634814|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.53246891|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.37792191|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.88118153|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.86303530|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.92565937|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.59319393|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.69532968|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.89467789|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.06503774|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.85517083|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.68518180|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.03210387|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.91855448|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.79640530|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.24387797|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.45764210|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.81439257|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.19837129|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.93304843|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.60859312|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.02409187|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.06211794|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.99286624|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.29864683|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.45915874|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.83199104|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.36140438|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.86294397|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.00919131|  PASSED
             dab_dct| 256|     50000|       1|0.58410106|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.84065158|  PASSED
        dab_filltree|  32|  15000000|       1|0.37383297|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.30063762|  PASSED
       dab_filltree2|   1|   5000000|       1|0.98196836|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.22197673|  PASSED
#=============================================================================#
# Runtime: 0:41:08                                                            #
#=============================================================================#
*/
