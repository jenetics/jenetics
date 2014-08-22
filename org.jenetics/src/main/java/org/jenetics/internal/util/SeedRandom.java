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
 * @version 1.5 &mdash; <em>$Date: 2014-08-17 $</em>
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
# Testing: org.jenetics.internal.util.SeedRandom (2014-08-16 23:30)           #
#=============================================================================#
#=============================================================================#
# Linux 3.13.0-34-generic (amd64)                                             #
# java version "1.8.0_11"                                                     #
# Java(TM) SE Runtime Environment (build 1.8.0_11-b12)                        #
# Java HotSpot(TM) 64-Bit Server VM (build 25.11-b03)                         #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  1.22e+07  |3327261796|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.58418934|  PASSED
      diehard_operm5|   0|   1000000|     100|0.19119279|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.99841012|   WEAK
    diehard_rank_6x8|   0|    100000|     100|0.04740294|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.02257433|  PASSED
        diehard_opso|   0|   2097152|     100|0.65380730|  PASSED
        diehard_oqso|   0|   2097152|     100|0.29519356|  PASSED
         diehard_dna|   0|   2097152|     100|0.81740805|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.93004215|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.18639265|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.68180941|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.93072502|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.96599497|  PASSED
     diehard_squeeze|   0|    100000|     100|0.18260670|  PASSED
        diehard_sums|   0|       100|     100|0.00103311|   WEAK
        diehard_runs|   0|    100000|     100|0.66670151|  PASSED
        diehard_runs|   0|    100000|     100|0.97882699|  PASSED
       diehard_craps|   0|    200000|     100|0.98854354|  PASSED
       diehard_craps|   0|    200000|     100|0.85336710|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.21097961|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.16178138|  PASSED
         sts_monobit|   1|    100000|     100|0.64239187|  PASSED
            sts_runs|   2|    100000|     100|0.97117401|  PASSED
          sts_serial|   1|    100000|     100|0.57846782|  PASSED
          sts_serial|   2|    100000|     100|0.40613591|  PASSED
          sts_serial|   3|    100000|     100|0.69722899|  PASSED
          sts_serial|   3|    100000|     100|0.79299368|  PASSED
          sts_serial|   4|    100000|     100|0.13612396|  PASSED
          sts_serial|   4|    100000|     100|0.07491604|  PASSED
          sts_serial|   5|    100000|     100|0.80622342|  PASSED
          sts_serial|   5|    100000|     100|0.10730131|  PASSED
          sts_serial|   6|    100000|     100|0.58671733|  PASSED
          sts_serial|   6|    100000|     100|0.36417856|  PASSED
          sts_serial|   7|    100000|     100|0.99285359|  PASSED
          sts_serial|   7|    100000|     100|0.63703260|  PASSED
          sts_serial|   8|    100000|     100|0.57964588|  PASSED
          sts_serial|   8|    100000|     100|0.46772500|  PASSED
          sts_serial|   9|    100000|     100|0.37725126|  PASSED
          sts_serial|   9|    100000|     100|0.75987267|  PASSED
          sts_serial|  10|    100000|     100|0.10661367|  PASSED
          sts_serial|  10|    100000|     100|0.03771470|  PASSED
          sts_serial|  11|    100000|     100|0.77684920|  PASSED
          sts_serial|  11|    100000|     100|0.29692236|  PASSED
          sts_serial|  12|    100000|     100|0.65352897|  PASSED
          sts_serial|  12|    100000|     100|0.81857590|  PASSED
          sts_serial|  13|    100000|     100|0.83054192|  PASSED
          sts_serial|  13|    100000|     100|0.85424926|  PASSED
          sts_serial|  14|    100000|     100|0.11565630|  PASSED
          sts_serial|  14|    100000|     100|0.02062729|  PASSED
          sts_serial|  15|    100000|     100|0.17977391|  PASSED
          sts_serial|  15|    100000|     100|0.41794060|  PASSED
          sts_serial|  16|    100000|     100|0.69162528|  PASSED
          sts_serial|  16|    100000|     100|0.62546951|  PASSED
         rgb_bitdist|   1|    100000|     100|0.47518492|  PASSED
         rgb_bitdist|   2|    100000|     100|0.42955478|  PASSED
         rgb_bitdist|   3|    100000|     100|0.52270309|  PASSED
         rgb_bitdist|   4|    100000|     100|0.81808754|  PASSED
         rgb_bitdist|   5|    100000|     100|0.60202292|  PASSED
         rgb_bitdist|   6|    100000|     100|0.96443039|  PASSED
         rgb_bitdist|   7|    100000|     100|0.52403745|  PASSED
         rgb_bitdist|   8|    100000|     100|0.23050275|  PASSED
         rgb_bitdist|   9|    100000|     100|0.88396852|  PASSED
         rgb_bitdist|  10|    100000|     100|0.59752284|  PASSED
         rgb_bitdist|  11|    100000|     100|0.82675567|  PASSED
         rgb_bitdist|  12|    100000|     100|0.99999996|  FAILED
rgb_minimum_distance|   2|     10000|    1000|0.51510421|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.23319653|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.45413186|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.68692531|  PASSED
    rgb_permutations|   2|    100000|     100|0.92743241|  PASSED
    rgb_permutations|   3|    100000|     100|0.92989407|  PASSED
    rgb_permutations|   4|    100000|     100|0.14334757|  PASSED
    rgb_permutations|   5|    100000|     100|0.65704256|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.16924365|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.76549777|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.10947002|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.96265055|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.90250623|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.98403146|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.22355471|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.55735484|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.76675917|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.62658606|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.74350336|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.42191340|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.76329350|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.04718898|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.50437626|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.15461436|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.21226720|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.05237748|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.49980411|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.96992333|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.67270289|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.32350071|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.94471196|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.53156186|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.74194428|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.21865967|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.91649155|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.53956374|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.99648304|   WEAK
      rgb_lagged_sum|  29|   1000000|     100|0.24070846|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.93269442|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.56545652|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.87712178|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.22166661|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.76779385|  PASSED
             dab_dct| 256|     50000|       1|0.09236455|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.54319289|  PASSED
        dab_filltree|  32|  15000000|       1|0.05042158|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.25407755|  PASSED
       dab_filltree2|   1|   5000000|       1|0.35553390|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.73274687|  PASSED
#=============================================================================#
# Summary: PASSED=110, WEAK=3, FAILED=1                                       #
#          235,031.398 MB of random data created with 37.429 MB/sec           #
#=============================================================================#
#=============================================================================#
# Runtime: 1:44:39                                                            #
#=============================================================================#
*/
