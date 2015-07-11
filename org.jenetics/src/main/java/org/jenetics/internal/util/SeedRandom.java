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
# Testing: org.jenetics.internal.util.SeedRandom (2015-07-11 13:27)           #
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
stdin_input_raw|  8.25e+06  |3473602665|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.79886287|  PASSED
      diehard_operm5|   0|   1000000|     100|0.95901961|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.51815484|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.17472719|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.71399389|  PASSED
        diehard_opso|   0|   2097152|     100|0.91911142|  PASSED
        diehard_oqso|   0|   2097152|     100|0.57982185|  PASSED
         diehard_dna|   0|   2097152|     100|0.25919178|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.41708690|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.99307521|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.01311425|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.94115379|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.54142532|  PASSED
     diehard_squeeze|   0|    100000|     100|0.71614150|  PASSED
        diehard_sums|   0|       100|     100|0.43346748|  PASSED
        diehard_runs|   0|    100000|     100|0.38811059|  PASSED
        diehard_runs|   0|    100000|     100|0.99409423|  PASSED
       diehard_craps|   0|    200000|     100|0.49188848|  PASSED
       diehard_craps|   0|    200000|     100|0.63571425|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.42325043|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.64998496|  PASSED
         sts_monobit|   1|    100000|     100|0.39724147|  PASSED
            sts_runs|   2|    100000|     100|0.29094979|  PASSED
          sts_serial|   1|    100000|     100|0.35867708|  PASSED
          sts_serial|   2|    100000|     100|0.56125023|  PASSED
          sts_serial|   3|    100000|     100|0.72540455|  PASSED
          sts_serial|   3|    100000|     100|0.57756151|  PASSED
          sts_serial|   4|    100000|     100|0.12837858|  PASSED
          sts_serial|   4|    100000|     100|0.35175037|  PASSED
          sts_serial|   5|    100000|     100|0.22010860|  PASSED
          sts_serial|   5|    100000|     100|0.96049368|  PASSED
          sts_serial|   6|    100000|     100|0.43989933|  PASSED
          sts_serial|   6|    100000|     100|0.23730634|  PASSED
          sts_serial|   7|    100000|     100|0.94891117|  PASSED
          sts_serial|   7|    100000|     100|0.23084505|  PASSED
          sts_serial|   8|    100000|     100|0.81759513|  PASSED
          sts_serial|   8|    100000|     100|0.29595443|  PASSED
          sts_serial|   9|    100000|     100|0.60587612|  PASSED
          sts_serial|   9|    100000|     100|0.07583079|  PASSED
          sts_serial|  10|    100000|     100|0.12528718|  PASSED
          sts_serial|  10|    100000|     100|0.12888471|  PASSED
          sts_serial|  11|    100000|     100|0.57152121|  PASSED
          sts_serial|  11|    100000|     100|0.96440555|  PASSED
          sts_serial|  12|    100000|     100|0.34826058|  PASSED
          sts_serial|  12|    100000|     100|0.54488901|  PASSED
          sts_serial|  13|    100000|     100|0.16114551|  PASSED
          sts_serial|  13|    100000|     100|0.21790324|  PASSED
          sts_serial|  14|    100000|     100|0.63063882|  PASSED
          sts_serial|  14|    100000|     100|0.02817021|  PASSED
          sts_serial|  15|    100000|     100|0.69907265|  PASSED
          sts_serial|  15|    100000|     100|0.70739544|  PASSED
          sts_serial|  16|    100000|     100|0.44737114|  PASSED
          sts_serial|  16|    100000|     100|0.80507427|  PASSED
         rgb_bitdist|   1|    100000|     100|0.96748957|  PASSED
         rgb_bitdist|   2|    100000|     100|0.18566335|  PASSED
         rgb_bitdist|   3|    100000|     100|0.56815112|  PASSED
         rgb_bitdist|   4|    100000|     100|0.58966413|  PASSED
         rgb_bitdist|   5|    100000|     100|0.53376780|  PASSED
         rgb_bitdist|   6|    100000|     100|0.27031770|  PASSED
         rgb_bitdist|   7|    100000|     100|0.48570783|  PASSED
         rgb_bitdist|   8|    100000|     100|0.42814902|  PASSED
         rgb_bitdist|   9|    100000|     100|0.85462353|  PASSED
         rgb_bitdist|  10|    100000|     100|0.89421265|  PASSED
         rgb_bitdist|  11|    100000|     100|0.77422217|  PASSED
         rgb_bitdist|  12|    100000|     100|0.07565098|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.11698204|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.35898048|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.74026788|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.90718349|  PASSED
    rgb_permutations|   2|    100000|     100|0.90971770|  PASSED
    rgb_permutations|   3|    100000|     100|0.46751285|  PASSED
    rgb_permutations|   4|    100000|     100|0.01349001|  PASSED
    rgb_permutations|   5|    100000|     100|0.63604012|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.71799430|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.70658104|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.65899107|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.16372566|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.11197291|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.72471862|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.96574522|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.75481773|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.98876546|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.49655596|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.11982059|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.65600430|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.45074004|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.65462118|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.42560087|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.64295716|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.94959946|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.21589072|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.26756085|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.24767435|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.74858120|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.99462976|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.19328591|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.12417314|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.39592148|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.47416655|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.32474663|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.78059020|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.70921006|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.15074749|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.85898128|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.74352597|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.71490925|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.36454079|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.24484496|  PASSED
             dab_dct| 256|     50000|       1|0.46997931|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.85986128|  PASSED
        dab_filltree|  32|  15000000|       1|0.01396230|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.00989703|  PASSED
       dab_filltree2|   1|   5000000|       1|0.37341438|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.28806694|  PASSED
#=============================================================================#
# Summary: PASSED=114, WEAK=0, FAILED=0                                       #
#          235,031.273 MB of random data created with 23.062 MB/sec           #
#=============================================================================#
#=============================================================================#
# Runtime: 2:49:51                                                            #
#=============================================================================#
*/
