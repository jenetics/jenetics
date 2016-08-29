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
package org.jenetics.random;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.random.internal.util.Equality.eq;

import java.io.Serializable;

import org.jenetics.random.internal.util.Equality;
import org.jenetics.random.internal.util.Hash;

/**
 * Multiple recursive random generator.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public class MRG2Random extends Random32 {

	private static final long serialVersionUID = 1L;

	/**
	 * The parameter class of this random engine.
	 */
	public static final class Param implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * LEcuyer 1 parameters: a1 = 1498809829; a2 = 1160990996
		 */
		public static final Param LECUYER1 = Param.of(1498809829, 1160990996);

		/**
		 * LEcuyer 2 parameters: a1 = 46325; a2 = 1084587
		 */
		public static final Param LECUYER2 = Param.of(46325, 1084587);

		/**
		 * The default PRNG parameters: LECUYER1
		 */
		public static final Param DEFAULT = LECUYER1;

		public final long a1;
		public final long a2;

		private Param(final int a1, final int a2) {
			this.a1 = a1;
			this.a2 = a2;
 		}

		public static Param of(final int a1, final int a2) {
			return new Param(a1, a2);
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(a1)
				.and(a2).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(param ->
				eq(a1, param.a1) &&
				eq(a2, param.a2)
			);
		}

		@Override
		public String toString() {
			return format("Param[%d, %d]", a1, a2);
		}
	}

	private static final class State implements Serializable {
		private static final long serialVersionUID = 1L;

		long _r1;
		long _r2;

		State(final long seed) {
			setSeed(seed);
		}

		void setSeed(final long seed) {
			long t = OxFFFFFFFB.mod(seed);
			if (t < 0) t += OxFFFFFFFB.VALUE;

			_r1 = t;
			_r2 = 1;
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(_r1)
				.and(_r2).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(state ->
				eq(_r1, state._r1) &&
				eq(_r2, state._r2)
			);
		}

		@Override
		public String toString() {
			return format("State[%d, %d]", _r1, _r2);
		}
	}

	private final Param _param;
	private final State _state;

	public MRG2Random(final Param param, final long seed) {
		_param = requireNonNull(param);
		_state = new State(seed);
	}

	public MRG2Random(final Param param) {
		this(param, math.seed());
	}

	public MRG2Random(final long seed) {
		this(Param.DEFAULT, seed);
	}

	public MRG2Random() {
		this(Param.DEFAULT, math.seed());
	}

	@Override
	public int nextInt() {
		step();
		return (int)_state._r1;
	}

	public void step() {
		final long t = OxFFFFFFFB.add(
			_param.a1*_state._r1,
			_param.a2*_state._r2
		);

		_state._r2 = _state._r1;
		_state._r1 = t;
	}

	@Override
	public void setSeed(final long seed) {
		if (_state != null) _state.setSeed(seed);
	}

	public Param getParam() {
		return _param;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_param)
			.and(_state).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(random ->
			eq(_param, random._param) &&
			eq(_state, random._state)
		);
	}

	@Override
	public String toString() {
		return format("%s[%s, %s]", getClass().getSimpleName(), _param, _state);
	}
}

/*
#=============================================================================#
# Testing: org.jenetix.random.MRG2Random (2014-07-28 01:35)                   #
#=============================================================================#
#=============================================================================#
# Linux 3.13.0-32-generic (amd64)                                             #
# java version "1.8.0_11"                                                     #
# Java(TM) SE Runtime Environment (build 1.8.0_11-b12)                        #
# Java HotSpot(TM) 64-Bit Server VM (build 25.11-b03)                         #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  3.06e+07  |2957012083|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.92692490|  PASSED
      diehard_operm5|   0|   1000000|     100|0.95936619|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.12881276|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.90480341|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.99987177|   WEAK
        diehard_opso|   0|   2097152|     100|0.08934896|  PASSED
        diehard_oqso|   0|   2097152|     100|0.62793017|  PASSED
         diehard_dna|   0|   2097152|     100|0.89416041|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.78378950|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.71514949|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.35869931|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.45173151|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.85500885|  PASSED
     diehard_squeeze|   0|    100000|     100|0.66333201|  PASSED
        diehard_sums|   0|       100|     100|0.43469229|  PASSED
        diehard_runs|   0|    100000|     100|0.73087273|  PASSED
        diehard_runs|   0|    100000|     100|0.00472377|   WEAK
       diehard_craps|   0|    200000|     100|0.61517168|  PASSED
       diehard_craps|   0|    200000|     100|0.31971073|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.24500057|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.81054452|  PASSED
         sts_monobit|   1|    100000|     100|0.01969350|  PASSED
            sts_runs|   2|    100000|     100|0.73615930|  PASSED
          sts_serial|   1|    100000|     100|0.79544079|  PASSED
          sts_serial|   2|    100000|     100|0.82373661|  PASSED
          sts_serial|   3|    100000|     100|0.39465813|  PASSED
          sts_serial|   3|    100000|     100|0.89739406|  PASSED
          sts_serial|   4|    100000|     100|0.88566704|  PASSED
          sts_serial|   4|    100000|     100|0.96923591|  PASSED
          sts_serial|   5|    100000|     100|0.76030644|  PASSED
          sts_serial|   5|    100000|     100|0.75460992|  PASSED
          sts_serial|   6|    100000|     100|0.04426464|  PASSED
          sts_serial|   6|    100000|     100|0.07894548|  PASSED
          sts_serial|   7|    100000|     100|0.24986118|  PASSED
          sts_serial|   7|    100000|     100|0.27852617|  PASSED
          sts_serial|   8|    100000|     100|0.57473726|  PASSED
          sts_serial|   8|    100000|     100|0.86753961|  PASSED
          sts_serial|   9|    100000|     100|0.50179968|  PASSED
          sts_serial|   9|    100000|     100|0.54592566|  PASSED
          sts_serial|  10|    100000|     100|0.45184302|  PASSED
          sts_serial|  10|    100000|     100|0.30486779|  PASSED
          sts_serial|  11|    100000|     100|0.53401817|  PASSED
          sts_serial|  11|    100000|     100|0.22234164|  PASSED
          sts_serial|  12|    100000|     100|0.19799926|  PASSED
          sts_serial|  12|    100000|     100|0.37508690|  PASSED
          sts_serial|  13|    100000|     100|0.63878723|  PASSED
          sts_serial|  13|    100000|     100|0.60665971|  PASSED
          sts_serial|  14|    100000|     100|0.75945639|  PASSED
          sts_serial|  14|    100000|     100|0.54578182|  PASSED
          sts_serial|  15|    100000|     100|0.47176884|  PASSED
          sts_serial|  15|    100000|     100|0.91669005|  PASSED
          sts_serial|  16|    100000|     100|0.90483470|  PASSED
          sts_serial|  16|    100000|     100|0.38195365|  PASSED
         rgb_bitdist|   1|    100000|     100|0.98621484|  PASSED
         rgb_bitdist|   2|    100000|     100|0.49239634|  PASSED
         rgb_bitdist|   3|    100000|     100|0.97878207|  PASSED
         rgb_bitdist|   4|    100000|     100|0.86922088|  PASSED
         rgb_bitdist|   5|    100000|     100|0.56125065|  PASSED
         rgb_bitdist|   6|    100000|     100|0.57341420|  PASSED
         rgb_bitdist|   7|    100000|     100|0.02406307|  PASSED
         rgb_bitdist|   8|    100000|     100|0.97982794|  PASSED
         rgb_bitdist|   9|    100000|     100|0.01990383|  PASSED
         rgb_bitdist|  10|    100000|     100|0.73127868|  PASSED
         rgb_bitdist|  11|    100000|     100|0.87058211|  PASSED
         rgb_bitdist|  12|    100000|     100|0.93510757|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.78385690|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.15481517|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.71367774|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.43896362|  PASSED
    rgb_permutations|   2|    100000|     100|0.91282177|  PASSED
    rgb_permutations|   3|    100000|     100|0.36172000|  PASSED
    rgb_permutations|   4|    100000|     100|0.95994327|  PASSED
    rgb_permutations|   5|    100000|     100|0.95267615|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.20083040|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.65476990|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.92970622|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.29447555|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.63781738|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.19597845|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.09586534|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.56911659|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.91343193|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.97525408|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.37923167|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.62282433|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.65081219|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.99439125|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.86802829|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.45795654|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.77377441|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.97421944|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.97153914|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.79878718|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.90937381|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.93290010|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.76630047|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.96520933|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.25007252|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.88877223|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.05554820|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.52541725|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.58728339|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.63626091|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.64826689|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.65422618|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.10883466|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.04615469|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.05085448|  PASSED
             dab_dct| 256|     50000|       1|0.12629269|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.47331397|  PASSED
        dab_filltree|  32|  15000000|       1|0.23503577|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.84629925|  PASSED
       dab_filltree2|   1|   5000000|       1|0.88963743|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.97794889|  PASSED
#=============================================================================#
# Summary: PASSED=112, WEAK=2, FAILED=0                                       #
#          235,031.266 MB of random data created with 101.377 MB/sec          #
#=============================================================================#
#=============================================================================#
# Runtime: 0:38:38                                                            #
#=============================================================================#
*/
