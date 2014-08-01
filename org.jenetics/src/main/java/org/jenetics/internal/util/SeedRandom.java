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
package org.jenetics.internal.util;

import org.jenetics.internal.math.random;

import org.jenetics.util.Random64;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.5
 * @version 1.5 &mdash; <em>$Date: 2014-08-01 $</em>
 */
public final class SeedRandom extends Random64 {

	private static final long serialVersionUID = 1L;

	@Override
	public long nextLong() {
		return random.seed();
	}

}

/*
#=============================================================================#
# Testing: org.jenetics.internal.util.SeedRandom (2014-04-22 12:36)           #
#=============================================================================#
#=============================================================================#
# Linux 3.13.0-24-generic (amd64)                                             #
# java version "1.8.0_05"                                                     #
# Java(TM) SE Runtime Environment (build 1.8.0_05-b13)                        #
# Java HotSpot(TM) 64-Bit Server VM (build 25.5-b02)                          #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  1.06e+07  |4223159524|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.71041441|  PASSED
      diehard_operm5|   0|   1000000|     100|0.06607752|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.77728357|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.72179755|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.79387542|  PASSED
        diehard_opso|   0|   2097152|     100|0.73259902|  PASSED
        diehard_oqso|   0|   2097152|     100|0.86305827|  PASSED
         diehard_dna|   0|   2097152|     100|0.30108049|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.27473503|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.40934755|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.99155313|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.84821181|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.94347283|  PASSED
     diehard_squeeze|   0|    100000|     100|0.75838019|  PASSED
        diehard_sums|   0|       100|     100|0.03785750|  PASSED
        diehard_runs|   0|    100000|     100|0.80868108|  PASSED
        diehard_runs|   0|    100000|     100|0.70270540|  PASSED
       diehard_craps|   0|    200000|     100|0.20030692|  PASSED
       diehard_craps|   0|    200000|     100|0.22608499|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.01319358|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.23424175|  PASSED
         sts_monobit|   1|    100000|     100|0.42095125|  PASSED
            sts_runs|   2|    100000|     100|0.13315236|  PASSED
          sts_serial|   1|    100000|     100|0.99695793|   WEAK
          sts_serial|   2|    100000|     100|0.31566362|  PASSED
          sts_serial|   3|    100000|     100|0.80349886|  PASSED
          sts_serial|   3|    100000|     100|0.40495214|  PASSED
          sts_serial|   4|    100000|     100|0.39112221|  PASSED
          sts_serial|   4|    100000|     100|0.54533551|  PASSED
          sts_serial|   5|    100000|     100|0.27362587|  PASSED
          sts_serial|   5|    100000|     100|0.68241342|  PASSED
          sts_serial|   6|    100000|     100|0.53552566|  PASSED
          sts_serial|   6|    100000|     100|0.98758723|  PASSED
          sts_serial|   7|    100000|     100|0.26238965|  PASSED
          sts_serial|   7|    100000|     100|0.58175926|  PASSED
          sts_serial|   8|    100000|     100|0.62340062|  PASSED
          sts_serial|   8|    100000|     100|0.64366897|  PASSED
          sts_serial|   9|    100000|     100|0.75113642|  PASSED
          sts_serial|   9|    100000|     100|0.58664747|  PASSED
          sts_serial|  10|    100000|     100|0.02049776|  PASSED
          sts_serial|  10|    100000|     100|0.26469770|  PASSED
          sts_serial|  11|    100000|     100|0.07832761|  PASSED
          sts_serial|  11|    100000|     100|0.41810579|  PASSED
          sts_serial|  12|    100000|     100|0.09819133|  PASSED
          sts_serial|  12|    100000|     100|0.47223090|  PASSED
          sts_serial|  13|    100000|     100|0.05887155|  PASSED
          sts_serial|  13|    100000|     100|0.99591171|   WEAK
          sts_serial|  14|    100000|     100|0.86185846|  PASSED
          sts_serial|  14|    100000|     100|0.44251441|  PASSED
          sts_serial|  15|    100000|     100|0.65003028|  PASSED
          sts_serial|  15|    100000|     100|0.73739514|  PASSED
          sts_serial|  16|    100000|     100|0.90216148|  PASSED
          sts_serial|  16|    100000|     100|0.99987305|   WEAK
         rgb_bitdist|   1|    100000|     100|0.64588128|  PASSED
         rgb_bitdist|   2|    100000|     100|0.47972230|  PASSED
         rgb_bitdist|   3|    100000|     100|0.79526231|  PASSED
         rgb_bitdist|   4|    100000|     100|0.61466022|  PASSED
         rgb_bitdist|   5|    100000|     100|0.01053864|  PASSED
         rgb_bitdist|   6|    100000|     100|0.51896952|  PASSED
         rgb_bitdist|   7|    100000|     100|0.52953918|  PASSED
         rgb_bitdist|   8|    100000|     100|0.09633417|  PASSED
         rgb_bitdist|   9|    100000|     100|0.78091472|  PASSED
         rgb_bitdist|  10|    100000|     100|0.01464355|  PASSED
         rgb_bitdist|  11|    100000|     100|0.99414031|  PASSED
         rgb_bitdist|  12|    100000|     100|0.44780389|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.37895660|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.21325073|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.76739152|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.98586045|  PASSED
    rgb_permutations|   2|    100000|     100|0.15478968|  PASSED
    rgb_permutations|   3|    100000|     100|0.72579504|  PASSED
    rgb_permutations|   4|    100000|     100|0.90112077|  PASSED
    rgb_permutations|   5|    100000|     100|0.93415411|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.67755189|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.53350803|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.75160830|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.95320210|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.69835782|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.14251841|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.61276346|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.03326428|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.08956529|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.77418292|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.07620763|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.82431391|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.45160866|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.81942071|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.60192946|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.64025339|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.42162814|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.60384121|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.99696990|   WEAK
      rgb_lagged_sum|  19|   1000000|     100|0.84472582|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.53275591|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.55411060|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.82656208|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.11422681|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.05369973|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.38739967|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.52356648|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.62629851|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.98209346|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.97938071|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.20042502|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.70518561|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.07702179|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.85288542|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.62553279|  PASSED
             dab_dct| 256|     50000|       1|0.54131795|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.87892014|  PASSED
        dab_filltree|  32|  15000000|       1|0.83510569|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.93425443|  PASSED
       dab_filltree2|   1|   5000000|       1|0.13543131|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.88199641|  PASSED
#=============================================================================#
# Runtime: 2:04:22                                                            #
#=============================================================================#
*/
