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
 * @version 1.5 &mdash; <em>$Date: 2014-12-22 $</em>
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
# Testing: org.jenetics.internal.util.SeedRandom (2014-12-22 20:54)           #
#=============================================================================#
#=============================================================================#
# Linux 3.16.0-28-generic (amd64)                                             #
# java version "1.8.0_25"                                                     #
# Java(TM) SE Runtime Environment (build 1.8.0_25-b17)                        #
# Java HotSpot(TM) 64-Bit Server VM (build 25.25-b02)                         #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  1.12e+07  |3523999329|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.37572611|  PASSED
      diehard_operm5|   0|   1000000|     100|0.44692702|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.84361034|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.28697517|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.89861849|  PASSED
        diehard_opso|   0|   2097152|     100|0.20979603|  PASSED
        diehard_oqso|   0|   2097152|     100|0.37326289|  PASSED
         diehard_dna|   0|   2097152|     100|0.29476980|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.80419403|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.01917840|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.82418429|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.14134741|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.06954100|  PASSED
     diehard_squeeze|   0|    100000|     100|0.76154851|  PASSED
        diehard_sums|   0|       100|     100|0.07217704|  PASSED
        diehard_runs|   0|    100000|     100|0.73342615|  PASSED
        diehard_runs|   0|    100000|     100|0.64837830|  PASSED
       diehard_craps|   0|    200000|     100|0.72623858|  PASSED
       diehard_craps|   0|    200000|     100|0.89289445|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.40204733|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.88215688|  PASSED
         sts_monobit|   1|    100000|     100|0.05567019|  PASSED
            sts_runs|   2|    100000|     100|0.76437536|  PASSED
          sts_serial|   1|    100000|     100|0.58463231|  PASSED
          sts_serial|   2|    100000|     100|0.67874949|  PASSED
          sts_serial|   3|    100000|     100|0.48168409|  PASSED
          sts_serial|   3|    100000|     100|0.40879187|  PASSED
          sts_serial|   4|    100000|     100|0.76121466|  PASSED
          sts_serial|   4|    100000|     100|0.90042462|  PASSED
          sts_serial|   5|    100000|     100|0.85637681|  PASSED
          sts_serial|   5|    100000|     100|0.89858936|  PASSED
          sts_serial|   6|    100000|     100|0.84334290|  PASSED
          sts_serial|   6|    100000|     100|0.67496021|  PASSED
          sts_serial|   7|    100000|     100|0.71679375|  PASSED
          sts_serial|   7|    100000|     100|0.92738873|  PASSED
          sts_serial|   8|    100000|     100|0.41318597|  PASSED
          sts_serial|   8|    100000|     100|0.61616247|  PASSED
          sts_serial|   9|    100000|     100|0.86273124|  PASSED
          sts_serial|   9|    100000|     100|0.57128161|  PASSED
          sts_serial|  10|    100000|     100|0.73812450|  PASSED
          sts_serial|  10|    100000|     100|0.95814273|  PASSED
          sts_serial|  11|    100000|     100|0.32610162|  PASSED
          sts_serial|  11|    100000|     100|0.40045741|  PASSED
          sts_serial|  12|    100000|     100|0.24064793|  PASSED
          sts_serial|  12|    100000|     100|0.47747884|  PASSED
          sts_serial|  13|    100000|     100|0.99175654|  PASSED
          sts_serial|  13|    100000|     100|0.99284241|  PASSED
          sts_serial|  14|    100000|     100|0.99102869|  PASSED
          sts_serial|  14|    100000|     100|0.87694576|  PASSED
          sts_serial|  15|    100000|     100|0.71762145|  PASSED
          sts_serial|  15|    100000|     100|0.99365915|  PASSED
          sts_serial|  16|    100000|     100|0.99806508|   WEAK
          sts_serial|  16|    100000|     100|0.54462844|  PASSED
         rgb_bitdist|   1|    100000|     100|0.62742212|  PASSED
         rgb_bitdist|   2|    100000|     100|0.98888588|  PASSED
         rgb_bitdist|   3|    100000|     100|0.90484600|  PASSED
         rgb_bitdist|   4|    100000|     100|0.70901341|  PASSED
         rgb_bitdist|   5|    100000|     100|0.35041852|  PASSED
         rgb_bitdist|   6|    100000|     100|0.12340650|  PASSED
         rgb_bitdist|   7|    100000|     100|0.90632053|  PASSED
         rgb_bitdist|   8|    100000|     100|0.80790265|  PASSED
         rgb_bitdist|   9|    100000|     100|0.74276302|  PASSED
         rgb_bitdist|  10|    100000|     100|0.92260508|  PASSED
         rgb_bitdist|  11|    100000|     100|0.31113339|  PASSED
         rgb_bitdist|  12|    100000|     100|0.79893676|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.71871665|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.89548180|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.51267244|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.19465585|  PASSED
    rgb_permutations|   2|    100000|     100|0.33349781|  PASSED
    rgb_permutations|   3|    100000|     100|0.42907136|  PASSED
    rgb_permutations|   4|    100000|     100|0.32485552|  PASSED
    rgb_permutations|   5|    100000|     100|0.27699005|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.91590639|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.54822537|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.00663531|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.42592548|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.73297229|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.71759707|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.61064366|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.03316948|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.25962150|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.39094528|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.66302419|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.04705914|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.05765758|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.28710324|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.77176131|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.40808023|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.29868917|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.29232534|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.86601426|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.52434746|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.52086801|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.68004312|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.62850965|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.14632110|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.73074317|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.53192996|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.96172879|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.15440456|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.57118782|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.96073911|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.24374222|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.18815531|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.11221182|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.30379873|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.74747346|  PASSED
             dab_dct| 256|     50000|       1|0.01382200|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.63538259|  PASSED
        dab_filltree|  32|  15000000|       1|0.56348559|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.71651759|  PASSED
       dab_filltree2|   1|   5000000|       1|0.89787578|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.94798964|  PASSED
#=============================================================================#
# Summary: PASSED=113, WEAK=1, FAILED=0                                       #
#          235,031.328 MB of random data created with 34.825 MB/sec           #
#=============================================================================#
#=============================================================================#
# Runtime: 1:52:28                                                            #
#=============================================================================#
*/
