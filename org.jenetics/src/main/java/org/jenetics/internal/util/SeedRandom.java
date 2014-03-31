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
 * @version 1.5 &mdash; <em>$Date: 2014-03-16 $</em>
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
# Testing: org.jenetics.internal.util.SeedRandom (2014-03-16 13:22)           #
#=============================================================================#
#=============================================================================#
# Linux 3.11.0-18-generic (amd64)                                             #
# java version "1.7.0_51"                                                     #
# Java(TM) SE Runtime Environment (build 1.7.0_51-b13)                        #
# Java HotSpot(TM) 64-Bit Server VM (build 24.51-b03)                         #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  1.17e+07  |2250864647|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.45086956|  PASSED
      diehard_operm5|   0|   1000000|     100|0.99245153|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.24638807|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.68441660|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.80568191|  PASSED
        diehard_opso|   0|   2097152|     100|0.55819169|  PASSED
        diehard_oqso|   0|   2097152|     100|0.66119310|  PASSED
         diehard_dna|   0|   2097152|     100|0.02292468|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.88266427|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.44032379|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.87504329|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.62434030|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.10927180|  PASSED
     diehard_squeeze|   0|    100000|     100|0.99983341|   WEAK
        diehard_sums|   0|       100|     100|0.12208181|  PASSED
        diehard_runs|   0|    100000|     100|0.73923041|  PASSED
        diehard_runs|   0|    100000|     100|0.09163173|  PASSED
       diehard_craps|   0|    200000|     100|0.06988155|  PASSED
       diehard_craps|   0|    200000|     100|0.35641180|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.58541802|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.05195259|  PASSED
         sts_monobit|   1|    100000|     100|0.62163050|  PASSED
            sts_runs|   2|    100000|     100|0.99098281|  PASSED
          sts_serial|   1|    100000|     100|0.98064522|  PASSED
          sts_serial|   2|    100000|     100|0.95415214|  PASSED
          sts_serial|   3|    100000|     100|0.46116833|  PASSED
          sts_serial|   3|    100000|     100|0.71196031|  PASSED
          sts_serial|   4|    100000|     100|0.51308998|  PASSED
          sts_serial|   4|    100000|     100|0.86870806|  PASSED
          sts_serial|   5|    100000|     100|0.58372552|  PASSED
          sts_serial|   5|    100000|     100|0.20529929|  PASSED
          sts_serial|   6|    100000|     100|0.55134443|  PASSED
          sts_serial|   6|    100000|     100|0.88995224|  PASSED
          sts_serial|   7|    100000|     100|0.72675032|  PASSED
          sts_serial|   7|    100000|     100|0.89543539|  PASSED
          sts_serial|   8|    100000|     100|0.96387578|  PASSED
          sts_serial|   8|    100000|     100|0.68490504|  PASSED
          sts_serial|   9|    100000|     100|0.89335817|  PASSED
          sts_serial|   9|    100000|     100|0.91388996|  PASSED
          sts_serial|  10|    100000|     100|0.58545854|  PASSED
          sts_serial|  10|    100000|     100|0.04055863|  PASSED
          sts_serial|  11|    100000|     100|0.99594188|   WEAK
          sts_serial|  11|    100000|     100|0.60874838|  PASSED
          sts_serial|  12|    100000|     100|0.89994348|  PASSED
          sts_serial|  12|    100000|     100|0.70140582|  PASSED
          sts_serial|  13|    100000|     100|0.88520993|  PASSED
          sts_serial|  13|    100000|     100|0.85418702|  PASSED
          sts_serial|  14|    100000|     100|0.97804353|  PASSED
          sts_serial|  14|    100000|     100|0.97314944|  PASSED
          sts_serial|  15|    100000|     100|0.94899016|  PASSED
          sts_serial|  15|    100000|     100|0.82477961|  PASSED
          sts_serial|  16|    100000|     100|0.86492674|  PASSED
          sts_serial|  16|    100000|     100|0.08978355|  PASSED
         rgb_bitdist|   1|    100000|     100|0.96392264|  PASSED
         rgb_bitdist|   2|    100000|     100|0.59662470|  PASSED
         rgb_bitdist|   3|    100000|     100|0.93328051|  PASSED
         rgb_bitdist|   4|    100000|     100|0.85806847|  PASSED
         rgb_bitdist|   5|    100000|     100|0.65706655|  PASSED
         rgb_bitdist|   6|    100000|     100|0.79357386|  PASSED
         rgb_bitdist|   7|    100000|     100|0.82679317|  PASSED
         rgb_bitdist|   8|    100000|     100|0.49662067|  PASSED
         rgb_bitdist|   9|    100000|     100|0.84537472|  PASSED
         rgb_bitdist|  10|    100000|     100|0.52372538|  PASSED
         rgb_bitdist|  11|    100000|     100|0.74695995|  PASSED
         rgb_bitdist|  12|    100000|     100|0.20998838|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.39617480|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.15867022|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.29394374|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.62414924|  PASSED
    rgb_permutations|   2|    100000|     100|0.02075613|  PASSED
    rgb_permutations|   3|    100000|     100|0.90699056|  PASSED
    rgb_permutations|   4|    100000|     100|0.32683462|  PASSED
    rgb_permutations|   5|    100000|     100|0.98430917|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.99860237|   WEAK
      rgb_lagged_sum|   1|   1000000|     100|0.93711217|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.43498697|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.99961127|   WEAK
      rgb_lagged_sum|   4|   1000000|     100|0.91099344|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.83500478|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.91573727|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.52191376|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.83614464|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.00208060|   WEAK
      rgb_lagged_sum|  10|   1000000|     100|0.57516953|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.51325642|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.21488959|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.96958649|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.74996921|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.53984511|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.69088652|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.02612046|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.69190387|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.73575574|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.98436125|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.64684643|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.23020421|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.82012699|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.98938091|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.85575982|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.94007363|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.41273506|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.66049282|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.94849700|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.67637694|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.64906730|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.69490347|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.87030622|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.02103817|  PASSED
             dab_dct| 256|     50000|       1|0.88256530|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.42020968|  PASSED
        dab_filltree|  32|  15000000|       1|0.07703027|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.52675165|  PASSED
       dab_filltree2|   1|   5000000|       1|0.97540895|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.85572571|  PASSED
#=============================================================================#
# Runtime: 1:44:11                                                            #
#=============================================================================#
*/
