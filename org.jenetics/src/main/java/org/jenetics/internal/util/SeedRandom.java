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
 * @version 1.5
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
# Testing: org.jenetics.internal.util.SeedRandom (2015-07-11 23:48)           #
#=============================================================================#
#=============================================================================#
# Linux 3.19.0-22-generic (amd64)                                             #
# java version "1.8.0_45"                                                     #
# Java(TM) SE Runtime Environment (build 1.8.0_45-b14)                        #
# Java HotSpot(TM) 64-Bit Server VM (build 25.45-b02)                         #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  1.36e+07  |1583496496|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.63372078|  PASSED
      diehard_operm5|   0|   1000000|     100|0.42965082|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.95159380|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.70376799|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.54324038|  PASSED
        diehard_opso|   0|   2097152|     100|0.99968038|   WEAK
        diehard_oqso|   0|   2097152|     100|0.77886984|  PASSED
         diehard_dna|   0|   2097152|     100|0.21670810|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.23785004|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.90127934|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.82562887|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.81261741|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.10567932|  PASSED
     diehard_squeeze|   0|    100000|     100|0.72544491|  PASSED
        diehard_sums|   0|       100|     100|0.50288166|  PASSED
        diehard_runs|   0|    100000|     100|0.68398648|  PASSED
        diehard_runs|   0|    100000|     100|0.42063708|  PASSED
       diehard_craps|   0|    200000|     100|0.91296112|  PASSED
       diehard_craps|   0|    200000|     100|0.61438210|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.90762169|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.74504070|  PASSED
         sts_monobit|   1|    100000|     100|0.88816026|  PASSED
            sts_runs|   2|    100000|     100|0.36812645|  PASSED
          sts_serial|   1|    100000|     100|0.99490490|  PASSED
          sts_serial|   2|    100000|     100|0.27489766|  PASSED
          sts_serial|   3|    100000|     100|0.72063387|  PASSED
          sts_serial|   3|    100000|     100|0.56050600|  PASSED
          sts_serial|   4|    100000|     100|0.40803091|  PASSED
          sts_serial|   4|    100000|     100|0.14610710|  PASSED
          sts_serial|   5|    100000|     100|0.59929612|  PASSED
          sts_serial|   5|    100000|     100|0.55653003|  PASSED
          sts_serial|   6|    100000|     100|0.61621081|  PASSED
          sts_serial|   6|    100000|     100|0.75965773|  PASSED
          sts_serial|   7|    100000|     100|0.49846430|  PASSED
          sts_serial|   7|    100000|     100|0.56970663|  PASSED
          sts_serial|   8|    100000|     100|0.16572680|  PASSED
          sts_serial|   8|    100000|     100|0.08035995|  PASSED
          sts_serial|   9|    100000|     100|0.74054445|  PASSED
          sts_serial|   9|    100000|     100|0.85806790|  PASSED
          sts_serial|  10|    100000|     100|0.55641159|  PASSED
          sts_serial|  10|    100000|     100|0.42741248|  PASSED
          sts_serial|  11|    100000|     100|0.56531758|  PASSED
          sts_serial|  11|    100000|     100|0.28561556|  PASSED
          sts_serial|  12|    100000|     100|0.93289442|  PASSED
          sts_serial|  12|    100000|     100|0.10609318|  PASSED
          sts_serial|  13|    100000|     100|0.11477086|  PASSED
          sts_serial|  13|    100000|     100|0.13992014|  PASSED
          sts_serial|  14|    100000|     100|0.20914453|  PASSED
          sts_serial|  14|    100000|     100|0.95561502|  PASSED
          sts_serial|  15|    100000|     100|0.57550371|  PASSED
          sts_serial|  15|    100000|     100|0.78306964|  PASSED
          sts_serial|  16|    100000|     100|0.43285388|  PASSED
          sts_serial|  16|    100000|     100|0.16620875|  PASSED
         rgb_bitdist|   1|    100000|     100|0.71550688|  PASSED
         rgb_bitdist|   2|    100000|     100|0.88565799|  PASSED
         rgb_bitdist|   3|    100000|     100|0.91094894|  PASSED
         rgb_bitdist|   4|    100000|     100|0.72079031|  PASSED
         rgb_bitdist|   5|    100000|     100|0.61192449|  PASSED
         rgb_bitdist|   6|    100000|     100|0.43256206|  PASSED
         rgb_bitdist|   7|    100000|     100|0.03695155|  PASSED
         rgb_bitdist|   8|    100000|     100|0.26807834|  PASSED
         rgb_bitdist|   9|    100000|     100|0.71177709|  PASSED
         rgb_bitdist|  10|    100000|     100|0.76951202|  PASSED
         rgb_bitdist|  11|    100000|     100|0.99989410|   WEAK
         rgb_bitdist|  12|    100000|     100|0.14204596|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.29058628|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.21390783|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.59206742|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.56050634|  PASSED
    rgb_permutations|   2|    100000|     100|0.33349781|  PASSED
    rgb_permutations|   3|    100000|     100|0.88981296|  PASSED
    rgb_permutations|   4|    100000|     100|0.76390317|  PASSED
    rgb_permutations|   5|    100000|     100|0.48873433|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.30633683|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.04766862|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.51126881|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.94936627|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.66631999|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.15509063|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.90258883|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.66819928|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.53981443|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.77138015|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.63349854|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.75517662|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.92570878|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.94638496|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.86636475|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.12173801|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.90327802|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.87874304|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.90947355|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.30990157|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.97972501|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.09146056|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.15026244|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.81182139|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.02634058|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.85870563|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.26305053|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.88560212|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.11539175|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.34220295|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.82927978|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.24521890|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.93860839|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.07520693|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.27213709|  PASSED
             dab_dct| 256|     50000|       1|0.54023003|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.77373956|  PASSED
        dab_filltree|  32|  15000000|       1|0.04265883|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.36676414|  PASSED
       dab_filltree2|   1|   5000000|       1|0.08120551|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.76563780|  PASSED
#=============================================================================#
# Summary: PASSED=112, WEAK=2, FAILED=0                                       #
#          235,031.492 MB of random data created with 41.394 MB/sec           #
#=============================================================================#
#=============================================================================#
# Runtime: 1:34:37                                                            #
#=============================================================================#
*/
