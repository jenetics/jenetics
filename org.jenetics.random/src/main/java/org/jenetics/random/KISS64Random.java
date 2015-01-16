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
import static org.jenetics.random.internal.util.Equality.eq;
import static org.jenetics.random.utils.mix;

import java.io.Serializable;

import org.jenetics.random.internal.util.Equality;
import org.jenetics.random.internal.util.Hash;

/**
 * Implementation of an simple PRNG as proposed in
 * <a href="http://www0.cs.ucl.ac.uk/staff/d.jones/GoodPracticeRNG.pdf">
 * Good Practice in (Pseudo) Random Number Generation for Bioinformatics
 * Applications</a> (JKISS64, page 10) by <em><a href="mailto:d.jones@cs.ucl.ac.uk">
 * David Jones</a>, UCL Bioinformatics Group</em>.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__! &mdash; <em>$Date: 2015-01-09 $</em>
 */
public class KISS64Random extends Random64 {

	private static final long serialVersionUID = 1L;

	/**
	 * This class represents a <i>thread local</i> implementation of the
	 * {@code KISS32Random} PRNG.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since !__version__!
	 * @version !__version__! &mdash; <em>$Date: 2015-01-09 $</em>
	 */
	public static final class ThreadLocal
		extends java.lang.ThreadLocal<KISS64Random>
	{
		@Override
		protected KISS64Random initialValue() {
			return new TLKISS64Random(math.seed());
		}
	}

	private static final class TLKISS64Random extends KISS64Random {
		private static final long serialVersionUID = 1L;

		private final Boolean _sentry = Boolean.TRUE;

		private TLKISS64Random(final long seed) {
			super(seed);
		}

		@Override
		public void setSeed(final long seed) {
			if (_sentry != null) {
				throw new UnsupportedOperationException(
					"The 'setSeed(long)' method is not supported " +
						"for thread local instances."
				);
			}
		}
	}


	/**
	 * This is a <i>thread safe</i> variation of the this PRNG&mdash;by
	 * synchronizing the random number generation.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since !__version__!
	 * @version !__version__! &mdash; <em>$Date: 2015-01-09 $</em>
	 */
	public static final class ThreadSafe extends KISS64Random {
		private static final long serialVersionUID = 1L;

		/**
		 * Create a new PRNG instance with the given seed.
		 *
		 * @param seed the seed of the PRNG.
		 */
		public ThreadSafe(final long seed) {
			super(seed);
		}

		/**
		 * Create a new PRNG instance with a safe seed.
		 */
		public ThreadSafe() {
			this(math.seed());
		}

		@Override
		public synchronized void setSeed(final long seed) {
			super.setSeed(seed);
		}

		@Override
		public synchronized int nextInt() {
			return super.nextInt();
		}
	}

	/**
	 * The internal state of this PRNG.
	 */
	private static final class State implements Serializable {
		private static final long serialVersionUID = 1L;

		long _x = 123456789123L;
		long _y = 987654321987L;
		int _z1 = 43219876;
		int _c1 = 6543217;
		int _z2 = 21987643;
		int _c2 = 1732654;

		State(final long seed) {
			setSeed(seed);
		}

		void setSeed(final long seed) {
			_x ^= seed;
			_y ^= mix(seed);
			if (_y == 0L) _y = 0xdeadbeef;
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(_x)
				.and(_y)
				.and(_z1)
				.and(_c1)
				.and(_z2)
				.and(_c2).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(state ->
				eq(_x, state._x) &&
				eq(_y, state._y) &&
				eq(_z1, state._z1) &&
				eq(_c1, state._c1) &&
				eq(_z2, state._z2) &&
				eq(_z2, state._z2)
			);
		}

		@Override
		public String toString() {
			return format(
				"State[%d, %d, %d, %d, %d, %d]",
				_x, _y, _z1, _c1, _z2, _c2
			);
		}
	}

	private final State _state;

	public KISS64Random(final long seed) {
		_state = new State(seed);
	}

	public KISS64Random() {
		this(math.seed());
	}

	@Override
	public long nextLong() {
		step();
		return _state._x + _state._y + _state._z1 + ((long)_state._z2 << 32);
	}

	private void step() {
		_state._x = 0x14ADA13ED78492ADL*_state._x + 123456789;

		_state._y ^= _state._y << 21;
		_state._y ^= _state._y >>> 17;
		_state._y ^= _state._y << 30;

		long t = 4294584393L*_state._z1 + _state._c1;
		_state._c1 = (int)(t >> 32);
		_state._z1 = (int)t;

		t = 4246477509L*_state._z2 + _state._c2;
		_state._c2 = (int)(t >> 32);
		_state._z2 = (int)t;
	}

	@Override
	public void setSeed(final long seed) {
		if (_state != null) _state.setSeed(seed);
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_state).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(random ->
			eq(_state, random._state)
		);
	}

	@Override
	public String toString() {
		return format("%s[%s]", getClass().getSimpleName(), _state);
	}

}

/*
#=============================================================================#
# Testing: org.jenetix.random.KISS64Random (2014-07-28 18:18)                 #
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
stdin_input_raw|  3.25e+07  |4234037151|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.99673037|   WEAK
      diehard_operm5|   0|   1000000|     100|0.23431178|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.64996699|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.25879478|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.17993733|  PASSED
        diehard_opso|   0|   2097152|     100|0.96738398|  PASSED
        diehard_oqso|   0|   2097152|     100|0.66512924|  PASSED
         diehard_dna|   0|   2097152|     100|0.93265432|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.90364682|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.81988197|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.66609484|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.24437877|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.42227784|  PASSED
     diehard_squeeze|   0|    100000|     100|0.27214432|  PASSED
        diehard_sums|   0|       100|     100|0.95643483|  PASSED
        diehard_runs|   0|    100000|     100|0.72265499|  PASSED
        diehard_runs|   0|    100000|     100|0.73256363|  PASSED
       diehard_craps|   0|    200000|     100|0.12990202|  PASSED
       diehard_craps|   0|    200000|     100|0.59285450|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.95299455|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.85701382|  PASSED
         sts_monobit|   1|    100000|     100|0.77937132|  PASSED
            sts_runs|   2|    100000|     100|0.99180756|  PASSED
          sts_serial|   1|    100000|     100|0.64824589|  PASSED
          sts_serial|   2|    100000|     100|0.56058168|  PASSED
          sts_serial|   3|    100000|     100|0.67143560|  PASSED
          sts_serial|   3|    100000|     100|0.51253209|  PASSED
          sts_serial|   4|    100000|     100|0.08041274|  PASSED
          sts_serial|   4|    100000|     100|0.26997580|  PASSED
          sts_serial|   5|    100000|     100|0.55158684|  PASSED
          sts_serial|   5|    100000|     100|0.91837408|  PASSED
          sts_serial|   6|    100000|     100|0.22246567|  PASSED
          sts_serial|   6|    100000|     100|0.86172234|  PASSED
          sts_serial|   7|    100000|     100|0.16056523|  PASSED
          sts_serial|   7|    100000|     100|0.96317213|  PASSED
          sts_serial|   8|    100000|     100|0.79468134|  PASSED
          sts_serial|   8|    100000|     100|0.91627005|  PASSED
          sts_serial|   9|    100000|     100|0.60418126|  PASSED
          sts_serial|   9|    100000|     100|0.27736241|  PASSED
          sts_serial|  10|    100000|     100|0.79073375|  PASSED
          sts_serial|  10|    100000|     100|0.27255288|  PASSED
          sts_serial|  11|    100000|     100|0.62676385|  PASSED
          sts_serial|  11|    100000|     100|0.25239319|  PASSED
          sts_serial|  12|    100000|     100|0.90208951|  PASSED
          sts_serial|  12|    100000|     100|0.55882459|  PASSED
          sts_serial|  13|    100000|     100|0.66853598|  PASSED
          sts_serial|  13|    100000|     100|0.30884007|  PASSED
          sts_serial|  14|    100000|     100|0.35889819|  PASSED
          sts_serial|  14|    100000|     100|0.29054529|  PASSED
          sts_serial|  15|    100000|     100|0.54720585|  PASSED
          sts_serial|  15|    100000|     100|0.83821341|  PASSED
          sts_serial|  16|    100000|     100|0.73319726|  PASSED
          sts_serial|  16|    100000|     100|0.83050871|  PASSED
         rgb_bitdist|   1|    100000|     100|0.18506621|  PASSED
         rgb_bitdist|   2|    100000|     100|0.93021250|  PASSED
         rgb_bitdist|   3|    100000|     100|0.78401060|  PASSED
         rgb_bitdist|   4|    100000|     100|0.19348848|  PASSED
         rgb_bitdist|   5|    100000|     100|0.94907438|  PASSED
         rgb_bitdist|   6|    100000|     100|0.83804534|  PASSED
         rgb_bitdist|   7|    100000|     100|0.91140507|  PASSED
         rgb_bitdist|   8|    100000|     100|0.00366955|   WEAK
         rgb_bitdist|   9|    100000|     100|0.64980441|  PASSED
         rgb_bitdist|  10|    100000|     100|0.85477700|  PASSED
         rgb_bitdist|  11|    100000|     100|0.50672227|  PASSED
         rgb_bitdist|  12|    100000|     100|0.50470065|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.81156675|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.88298798|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.40103217|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.15768295|  PASSED
    rgb_permutations|   2|    100000|     100|0.42054038|  PASSED
    rgb_permutations|   3|    100000|     100|0.95601816|  PASSED
    rgb_permutations|   4|    100000|     100|0.22143978|  PASSED
    rgb_permutations|   5|    100000|     100|0.87247835|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.37303724|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.69373166|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.27612973|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.40003088|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.26797464|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.86927746|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.67001707|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.04704593|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.95644566|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.30928791|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.17055162|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.43930958|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.46533395|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.94208245|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.51397168|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.74756399|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.53578582|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.60111007|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.81314357|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.26437464|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.54189889|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.74817328|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.37754312|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.04502074|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.43165428|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.70559209|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.37700078|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.81917413|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.95978532|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.52089820|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.21264345|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.45477591|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.52421335|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.88342483|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.65440155|  PASSED
             dab_dct| 256|     50000|       1|0.80888845|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.22007092|  PASSED
        dab_filltree|  32|  15000000|       1|0.28469929|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.60450695|  PASSED
       dab_filltree2|   1|   5000000|       1|0.02085500|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.62135891|  PASSED
#=============================================================================#
# Summary: PASSED=112, WEAK=2, FAILED=0                                       #
#          235,031.188 MB of random data created with 99.400 MB/sec           #
#=============================================================================#
#=============================================================================#
# Runtime: 0:39:24                                                            #
#=============================================================================#
*/
