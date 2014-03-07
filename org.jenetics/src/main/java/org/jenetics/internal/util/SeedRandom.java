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
 * @version 1.5 &mdash; <em>$Date: 2014-02-15 $</em>
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
# Testing: org.jenetics.internal.util.SeedRandom (2013-11-24 00:33)           #
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
stdin_input_raw|  1.17e+07  |3610683373|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.97460404|  PASSED
      diehard_operm5|   0|   1000000|     100|0.79899198|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.87854806|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.83448131|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.87489801|  PASSED
        diehard_opso|   0|   2097152|     100|0.87316034|  PASSED
        diehard_oqso|   0|   2097152|     100|0.94364126|  PASSED
         diehard_dna|   0|   2097152|     100|0.31732608|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.36195514|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.18156980|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.41119622|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.70489715|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.59735142|  PASSED
     diehard_squeeze|   0|    100000|     100|0.21022717|  PASSED
        diehard_sums|   0|       100|     100|0.01750477|  PASSED
        diehard_runs|   0|    100000|     100|0.45713475|  PASSED
        diehard_runs|   0|    100000|     100|0.57573252|  PASSED
       diehard_craps|   0|    200000|     100|0.56849057|  PASSED
       diehard_craps|   0|    200000|     100|0.91252137|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.74171873|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.96090391|  PASSED
         sts_monobit|   1|    100000|     100|0.90633955|  PASSED
            sts_runs|   2|    100000|     100|0.99961500|   WEAK
          sts_serial|   1|    100000|     100|0.31697520|  PASSED
          sts_serial|   2|    100000|     100|0.35185353|  PASSED
          sts_serial|   3|    100000|     100|0.10046809|  PASSED
          sts_serial|   3|    100000|     100|0.54610883|  PASSED
          sts_serial|   4|    100000|     100|0.27473010|  PASSED
          sts_serial|   4|    100000|     100|0.92323151|  PASSED
          sts_serial|   5|    100000|     100|0.89056590|  PASSED
          sts_serial|   5|    100000|     100|0.76852820|  PASSED
          sts_serial|   6|    100000|     100|0.89624192|  PASSED
          sts_serial|   6|    100000|     100|0.83632453|  PASSED
          sts_serial|   7|    100000|     100|0.65461440|  PASSED
          sts_serial|   7|    100000|     100|0.70319355|  PASSED
          sts_serial|   8|    100000|     100|0.71674589|  PASSED
          sts_serial|   8|    100000|     100|0.54643360|  PASSED
          sts_serial|   9|    100000|     100|0.36329177|  PASSED
          sts_serial|   9|    100000|     100|0.40788467|  PASSED
          sts_serial|  10|    100000|     100|0.99766575|   WEAK
          sts_serial|  10|    100000|     100|0.36797294|  PASSED
          sts_serial|  11|    100000|     100|0.45611190|  PASSED
          sts_serial|  11|    100000|     100|0.60708386|  PASSED
          sts_serial|  12|    100000|     100|0.53129995|  PASSED
          sts_serial|  12|    100000|     100|0.05900480|  PASSED
          sts_serial|  13|    100000|     100|0.86140618|  PASSED
          sts_serial|  13|    100000|     100|0.97718982|  PASSED
          sts_serial|  14|    100000|     100|0.54050190|  PASSED
          sts_serial|  14|    100000|     100|0.10640482|  PASSED
          sts_serial|  15|    100000|     100|0.86585155|  PASSED
          sts_serial|  15|    100000|     100|0.25186134|  PASSED
          sts_serial|  16|    100000|     100|0.15392609|  PASSED
          sts_serial|  16|    100000|     100|0.05277569|  PASSED
         rgb_bitdist|   1|    100000|     100|0.98096994|  PASSED
         rgb_bitdist|   2|    100000|     100|0.28208948|  PASSED
         rgb_bitdist|   3|    100000|     100|0.93910443|  PASSED
         rgb_bitdist|   4|    100000|     100|0.30025613|  PASSED
         rgb_bitdist|   5|    100000|     100|0.30557814|  PASSED
         rgb_bitdist|   6|    100000|     100|0.47517556|  PASSED
         rgb_bitdist|   7|    100000|     100|0.54709374|  PASSED
         rgb_bitdist|   8|    100000|     100|0.29328224|  PASSED
         rgb_bitdist|   9|    100000|     100|0.85137630|  PASSED
         rgb_bitdist|  10|    100000|     100|0.90311508|  PASSED
         rgb_bitdist|  11|    100000|     100|0.53836100|  PASSED
         rgb_bitdist|  12|    100000|     100|0.66304694|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.74142634|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.23783417|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.95246397|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.05855663|  PASSED
    rgb_permutations|   2|    100000|     100|0.46347152|  PASSED
    rgb_permutations|   3|    100000|     100|0.22094499|  PASSED
    rgb_permutations|   4|    100000|     100|0.97833148|  PASSED
    rgb_permutations|   5|    100000|     100|0.70853470|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.66818755|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.61451553|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.03098824|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.88963203|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.25591661|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.93073179|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.21282573|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.67369002|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.02921275|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.93586578|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.85102147|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.46718690|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.68587487|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.73251361|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.72268836|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.97040070|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.41269788|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.91962695|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.25445028|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.30479882|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.31372081|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.93719885|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.52612376|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.78761448|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.97630104|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.64809966|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.62541241|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.88155798|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.12199714|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.41020838|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.32321076|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.58529308|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.09307691|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.18145942|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.68674440|  PASSED
             dab_dct| 256|     50000|       1|0.01911370|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.28152481|  PASSED
        dab_filltree|  32|  15000000|       1|0.22817017|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.15518777|  PASSED
       dab_filltree2|   1|   5000000|       1|0.46939197|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.44522072|  PASSED
#=============================================================================#
# Runtime: 1:50:19                                                            #
#=============================================================================#
*/
