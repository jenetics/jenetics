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

import org.jenetics.util.Random64;
import org.jenetics.util.math;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.5
 * @version 1.5 &mdash; <em>$Date$</em>
 */
public class SeedRandom extends Random64 {

	private static final long serialVersionUID = 1L;

	@Override
	public long nextLong() {
		return math.random.seed();
	}

}

/*
#=============================================================================#
# Testing: org.jenetics.internal.util.SeedRandom                              #
#=============================================================================#
#=============================================================================#
# Linux 3.11.0-13-generic (amd64)                                             #
# java version "1.7.0_45"                                                     #
# Java(TM) SE Runtime Environment (build 1.7.0_45-b18)                        #
# Java HotSpot(TM) 64-Bit Server VM (build 24.45-b08)                         #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  1.00e+07  |1146368347|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.35266157|  PASSED
      diehard_operm5|   0|   1000000|     100|0.31974804|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.86009483|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.63475455|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.45913951|  PASSED
        diehard_opso|   0|   2097152|     100|0.73997770|  PASSED
        diehard_oqso|   0|   2097152|     100|0.57789303|  PASSED
         diehard_dna|   0|   2097152|     100|0.01474714|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.20158366|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.80619180|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.14001786|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.26640663|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.09687171|  PASSED
     diehard_squeeze|   0|    100000|     100|0.42637624|  PASSED
        diehard_sums|   0|       100|     100|0.36007192|  PASSED
        diehard_runs|   0|    100000|     100|0.88344031|  PASSED
        diehard_runs|   0|    100000|     100|0.12004370|  PASSED
       diehard_craps|   0|    200000|     100|0.77770972|  PASSED
       diehard_craps|   0|    200000|     100|0.69676663|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.44639694|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.81850855|  PASSED
         sts_monobit|   1|    100000|     100|0.11232111|  PASSED
            sts_runs|   2|    100000|     100|0.96521158|  PASSED
          sts_serial|   1|    100000|     100|0.91908335|  PASSED
          sts_serial|   2|    100000|     100|0.33361526|  PASSED
          sts_serial|   3|    100000|     100|0.13300830|  PASSED
          sts_serial|   3|    100000|     100|0.54060089|  PASSED
          sts_serial|   4|    100000|     100|0.37642507|  PASSED
          sts_serial|   4|    100000|     100|0.43480777|  PASSED
          sts_serial|   5|    100000|     100|0.17686521|  PASSED
          sts_serial|   5|    100000|     100|0.21034924|  PASSED
          sts_serial|   6|    100000|     100|0.02016016|  PASSED
          sts_serial|   6|    100000|     100|0.71831816|  PASSED
          sts_serial|   7|    100000|     100|0.08441173|  PASSED
          sts_serial|   7|    100000|     100|0.76624826|  PASSED
          sts_serial|   8|    100000|     100|0.79243463|  PASSED
          sts_serial|   8|    100000|     100|0.17204372|  PASSED
          sts_serial|   9|    100000|     100|0.94707602|  PASSED
          sts_serial|   9|    100000|     100|0.86039602|  PASSED
          sts_serial|  10|    100000|     100|0.90285128|  PASSED
          sts_serial|  10|    100000|     100|0.77404769|  PASSED
          sts_serial|  11|    100000|     100|0.43717694|  PASSED
          sts_serial|  11|    100000|     100|0.13912166|  PASSED
          sts_serial|  12|    100000|     100|0.99978409|   WEAK
          sts_serial|  12|    100000|     100|0.78910361|  PASSED
          sts_serial|  13|    100000|     100|0.88277071|  PASSED
          sts_serial|  13|    100000|     100|0.85501551|  PASSED
          sts_serial|  14|    100000|     100|0.80255742|  PASSED
          sts_serial|  14|    100000|     100|0.84860546|  PASSED
          sts_serial|  15|    100000|     100|0.97135629|  PASSED
          sts_serial|  15|    100000|     100|0.34734669|  PASSED
          sts_serial|  16|    100000|     100|0.48532866|  PASSED
          sts_serial|  16|    100000|     100|0.57964986|  PASSED
         rgb_bitdist|   1|    100000|     100|0.06879518|  PASSED
         rgb_bitdist|   2|    100000|     100|0.08841772|  PASSED
         rgb_bitdist|   3|    100000|     100|0.99652728|   WEAK
         rgb_bitdist|   4|    100000|     100|0.48673246|  PASSED
         rgb_bitdist|   5|    100000|     100|0.21353364|  PASSED
         rgb_bitdist|   6|    100000|     100|0.77407608|  PASSED
         rgb_bitdist|   7|    100000|     100|0.64988815|  PASSED
         rgb_bitdist|   8|    100000|     100|0.53260791|  PASSED
         rgb_bitdist|   9|    100000|     100|0.69832144|  PASSED
         rgb_bitdist|  10|    100000|     100|0.79283989|  PASSED
         rgb_bitdist|  11|    100000|     100|0.96249398|  PASSED
         rgb_bitdist|  12|    100000|     100|0.98030356|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.82453357|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.61846060|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.07693645|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.03698884|  PASSED
    rgb_permutations|   2|    100000|     100|0.89861756|  PASSED
    rgb_permutations|   3|    100000|     100|0.43918232|  PASSED
    rgb_permutations|   4|    100000|     100|0.94755340|  PASSED
    rgb_permutations|   5|    100000|     100|0.29371063|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.41533876|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.99251851|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.49804450|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.78357060|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.38808087|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.64407466|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.94989922|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.73204119|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.92872570|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.49618685|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.62689209|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.80013203|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.12601005|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.54463808|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.00636170|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.53534238|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.69976038|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.96124373|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.30929969|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.52256008|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.98990899|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.31113794|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.09174711|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.28072614|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.80452193|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.74434094|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.22094615|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.57860880|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.26934509|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.23415366|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.41115239|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.79954082|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.00251638|   WEAK
     rgb_kstest_test|   0|     10000|    1000|0.14907691|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.67172431|  PASSED
             dab_dct| 256|     50000|       1|0.24692699|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.83413594|  PASSED
        dab_filltree|  32|  15000000|       1|0.48728619|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.16513082|  PASSED
       dab_filltree2|   1|   5000000|       1|0.60454354|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.88343168|  PASSED
#=============================================================================#
# Runtime: 2:02:37                                                            #
#=============================================================================#
*/

