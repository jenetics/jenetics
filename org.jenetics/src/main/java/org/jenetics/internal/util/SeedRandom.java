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
# Testing: org.jenetics.internal.util.SeedRandom (2015-04-03 22:13)           #
#=============================================================================#
#=============================================================================#
# Linux 3.16.0-33-generic (amd64)                                             #
# java version "1.8.0_40"                                                     #
# Java(TM) SE Runtime Environment (build 1.8.0_40-b25)                        #
# Java HotSpot(TM) 64-Bit Server VM (build 25.40-b25)                         #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  1.30e+07  |3427824940|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.09459532|  PASSED
      diehard_operm5|   0|   1000000|     100|0.79150264|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.15184406|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.86442722|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.25389670|  PASSED
        diehard_opso|   0|   2097152|     100|0.74001369|  PASSED
        diehard_oqso|   0|   2097152|     100|0.89737506|  PASSED
         diehard_dna|   0|   2097152|     100|0.19354661|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.45150235|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.65783405|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.45602493|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.43857885|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.71359202|  PASSED
     diehard_squeeze|   0|    100000|     100|0.77336031|  PASSED
        diehard_sums|   0|       100|     100|0.04410528|  PASSED
        diehard_runs|   0|    100000|     100|0.44005516|  PASSED
        diehard_runs|   0|    100000|     100|0.27185291|  PASSED
       diehard_craps|   0|    200000|     100|0.43474774|  PASSED
       diehard_craps|   0|    200000|     100|0.83190679|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.97903070|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.72002656|  PASSED
         sts_monobit|   1|    100000|     100|0.10058890|  PASSED
            sts_runs|   2|    100000|     100|0.70546949|  PASSED
          sts_serial|   1|    100000|     100|0.98416961|  PASSED
          sts_serial|   2|    100000|     100|0.52767621|  PASSED
          sts_serial|   3|    100000|     100|0.35158332|  PASSED
          sts_serial|   3|    100000|     100|0.80837442|  PASSED
          sts_serial|   4|    100000|     100|0.15928133|  PASSED
          sts_serial|   4|    100000|     100|0.90284302|  PASSED
          sts_serial|   5|    100000|     100|0.31860027|  PASSED
          sts_serial|   5|    100000|     100|0.18142246|  PASSED
          sts_serial|   6|    100000|     100|0.97738179|  PASSED
          sts_serial|   6|    100000|     100|0.97496669|  PASSED
          sts_serial|   7|    100000|     100|0.92748759|  PASSED
          sts_serial|   7|    100000|     100|0.83084508|  PASSED
          sts_serial|   8|    100000|     100|0.68178172|  PASSED
          sts_serial|   8|    100000|     100|0.09366749|  PASSED
          sts_serial|   9|    100000|     100|0.20551741|  PASSED
          sts_serial|   9|    100000|     100|0.53330454|  PASSED
          sts_serial|  10|    100000|     100|0.88757259|  PASSED
          sts_serial|  10|    100000|     100|0.90322573|  PASSED
          sts_serial|  11|    100000|     100|0.04876313|  PASSED
          sts_serial|  11|    100000|     100|0.18316820|  PASSED
          sts_serial|  12|    100000|     100|0.11320707|  PASSED
          sts_serial|  12|    100000|     100|0.21835241|  PASSED
          sts_serial|  13|    100000|     100|0.29598278|  PASSED
          sts_serial|  13|    100000|     100|0.89760810|  PASSED
          sts_serial|  14|    100000|     100|0.45146769|  PASSED
          sts_serial|  14|    100000|     100|0.51941172|  PASSED
          sts_serial|  15|    100000|     100|0.74594863|  PASSED
          sts_serial|  15|    100000|     100|0.77534047|  PASSED
          sts_serial|  16|    100000|     100|0.74126245|  PASSED
          sts_serial|  16|    100000|     100|0.67686835|  PASSED
         rgb_bitdist|   1|    100000|     100|0.76435970|  PASSED
         rgb_bitdist|   2|    100000|     100|0.90967091|  PASSED
         rgb_bitdist|   3|    100000|     100|0.07618079|  PASSED
         rgb_bitdist|   4|    100000|     100|0.30759990|  PASSED
         rgb_bitdist|   5|    100000|     100|0.66290792|  PASSED
         rgb_bitdist|   6|    100000|     100|0.51297791|  PASSED
         rgb_bitdist|   7|    100000|     100|0.35343688|  PASSED
         rgb_bitdist|   8|    100000|     100|0.31101959|  PASSED
         rgb_bitdist|   9|    100000|     100|0.29595606|  PASSED
         rgb_bitdist|  10|    100000|     100|0.92014506|  PASSED
         rgb_bitdist|  11|    100000|     100|0.89494126|  PASSED
         rgb_bitdist|  12|    100000|     100|0.82239856|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.85539424|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.83133936|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.13161623|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.01869072|  PASSED
    rgb_permutations|   2|    100000|     100|0.84982592|  PASSED
    rgb_permutations|   3|    100000|     100|0.75374580|  PASSED
    rgb_permutations|   4|    100000|     100|0.96534248|  PASSED
    rgb_permutations|   5|    100000|     100|0.97373300|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.79801185|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.70792383|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.09861910|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.61532407|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.83106563|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.40942252|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.89262444|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.07894359|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.96957783|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.89206173|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.82621796|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.61620064|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.94442363|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.08025008|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.75445260|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.72690733|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.32445642|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.46642198|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.51509287|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.11472303|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.77063900|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.56863299|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.82762211|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.90399976|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.58848020|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.86878889|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.10398682|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.47811179|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.93761389|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.22281449|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.31092836|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.34047610|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.11346238|  PASSED  
     rgb_kstest_test|   0|     10000|    1000|0.89265175|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.96306035|  PASSED
             dab_dct| 256|     50000|       1|0.42000023|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.45687683|  PASSED
        dab_filltree|  32|  15000000|       1|0.84708053|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.16098859|  PASSED
       dab_filltree2|   1|   5000000|       1|0.12510290|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.68814379|  PASSED
#=============================================================================#
# Summary: PASSED=114, WEAK=0, FAILED=0                                       #
#          235,031.344 MB of random data created with 38.063 MB/sec           #
#=============================================================================#
#=============================================================================#
# Runtime: 1:42:54                                                            #
#=============================================================================#
*/
