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
import static org.jenetics.random.utils.highInt;
import static org.jenetics.random.utils.lowInt;
import static org.jenetics.random.utils.mix;

import java.io.Serializable;

import org.jenetics.random.internal.util.Equality;
import org.jenetics.random.internal.util.Hash;

/**
 * Implementation of an simple PRNG as proposed in
 * <a href="http://www0.cs.ucl.ac.uk/staff/d.jones/GoodPracticeRNG.pdf">
 * Good Practice in (Pseudo) Random Number Generation for Bioinformatics
 * Applications</a> (JKISS32, page 3) by <em><a href="mailto:d.jones@cs.ucl.ac.uk">
 * David Jones</a>, UCL Bioinformatics Group</em>.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public class KISS32Random extends Random32 {

	private static final long serialVersionUID = 1L;

	/**
	 * This class represents a <i>thread local</i> implementation of the
	 * {@code KISS32Random} PRNG.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since !__version__!
	 * @version !__version__!
	 */
	public static final class ThreadLocal
		extends java.lang.ThreadLocal<KISS32Random>
	{
		@Override
		protected KISS32Random initialValue() {
			return new TLKISS32Random(math.seed());
		}
	}

	private static final class TLKISS32Random extends KISS32Random {
		private static final long serialVersionUID = 1L;

		private final Boolean _sentry = Boolean.TRUE;

		private TLKISS32Random(final long seed) {
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
	 * @version !__version__!
	 */
	public static final class ThreadSafe extends KISS32Random {
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
	 * The state of this random engine.
	 */
	private static final class State implements Serializable {
		private static final long serialVersionUID = 1L;

		int _x = 123456789;
		int _y = 234567891;
		int _z = 345678912;
		int _w = 456789123;
		int _c = 0;

		State(final long seed) {
			setSeed(seed);
		}

		void setSeed(final long seed) {
			final long a = seed;
			final long b = mix(seed);

			_x = highInt(a);
			_y = lowInt(a);
			_z = highInt(b);
			_w = lowInt(b);
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(_x)
				.and(_y)
				.and(_z)
				.and(_w)
				.and(_c).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof State &&
				_x == ((State)obj)._x &&
				_y == ((State)obj)._y &&
				_z == ((State)obj)._z &&
				_w == ((State)obj)._w &&
				_c == ((State)obj)._c;
		}

		@Override
		public String toString() {
			return format("State[%d, %d, %d, %d, %d]", _x, _y, _z, _w, _c);
		}

	}

	private final State _state;

	public KISS32Random(final long seed) {
		_state = new State(seed);
	}

	public KISS32Random() {
		this(math.seed());
	}

	@Override
	public int nextInt() {
		step();
		return _state._x + _state._y + _state._w;
	}

	private void step() {
		_state._y ^= _state._y << 5;
		_state._y ^= _state._y >>> 7;
		_state._y ^= _state._y << 22;

		int t = _state._z + _state._w + _state._c;
		_state._z = _state._w;
		_state._c = t >>> 31;
		_state._w = t&2147483647;
		_state._x += 1411392427;
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
# Testing: org.jenetics.random.KISS32Random (2015-01-10 10:51)                #
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
stdin_input_raw|  3.16e+07  |2107679938|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.16810754|  PASSED
      diehard_operm5|   0|   1000000|     100|0.46260760|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.19824223|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.63272061|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.93299413|  PASSED
        diehard_opso|   0|   2097152|     100|0.92418367|  PASSED
        diehard_oqso|   0|   2097152|     100|0.57371153|  PASSED
         diehard_dna|   0|   2097152|     100|0.13240121|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.33279457|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.87334427|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.96257187|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.99999222|   WEAK
    diehard_3dsphere|   3|      4000|     100|0.28601250|  PASSED
     diehard_squeeze|   0|    100000|     100|0.96952368|  PASSED
        diehard_sums|   0|       100|     100|0.75917617|  PASSED
        diehard_runs|   0|    100000|     100|0.98223494|  PASSED
        diehard_runs|   0|    100000|     100|0.31469259|  PASSED
       diehard_craps|   0|    200000|     100|0.74836369|  PASSED
       diehard_craps|   0|    200000|     100|0.99705258|   WEAK
 marsaglia_tsang_gcd|   0|  10000000|     100|0.57681799|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.46428247|  PASSED
         sts_monobit|   1|    100000|     100|0.75366327|  PASSED
            sts_runs|   2|    100000|     100|0.77856241|  PASSED
          sts_serial|   1|    100000|     100|0.74730010|  PASSED
          sts_serial|   2|    100000|     100|0.92790090|  PASSED
          sts_serial|   3|    100000|     100|0.84924615|  PASSED
          sts_serial|   3|    100000|     100|0.39800639|  PASSED
          sts_serial|   4|    100000|     100|0.92623474|  PASSED
          sts_serial|   4|    100000|     100|0.36482767|  PASSED
          sts_serial|   5|    100000|     100|0.97985494|  PASSED
          sts_serial|   5|    100000|     100|0.69642852|  PASSED
          sts_serial|   6|    100000|     100|0.83794199|  PASSED
          sts_serial|   6|    100000|     100|0.55467528|  PASSED
          sts_serial|   7|    100000|     100|0.44298088|  PASSED
          sts_serial|   7|    100000|     100|0.13185662|  PASSED
          sts_serial|   8|    100000|     100|0.04281752|  PASSED
          sts_serial|   8|    100000|     100|0.15010165|  PASSED
          sts_serial|   9|    100000|     100|0.57196595|  PASSED
          sts_serial|   9|    100000|     100|0.08096077|  PASSED
          sts_serial|  10|    100000|     100|0.89409042|  PASSED
          sts_serial|  10|    100000|     100|0.13689023|  PASSED
          sts_serial|  11|    100000|     100|0.77882201|  PASSED
          sts_serial|  11|    100000|     100|0.89682473|  PASSED
          sts_serial|  12|    100000|     100|0.77879922|  PASSED
          sts_serial|  12|    100000|     100|0.97485465|  PASSED
          sts_serial|  13|    100000|     100|0.97360863|  PASSED
          sts_serial|  13|    100000|     100|0.68627145|  PASSED
          sts_serial|  14|    100000|     100|0.98299295|  PASSED
          sts_serial|  14|    100000|     100|0.99500576|   WEAK
          sts_serial|  15|    100000|     100|0.82816877|  PASSED
          sts_serial|  15|    100000|     100|0.71103735|  PASSED
          sts_serial|  16|    100000|     100|0.06792861|  PASSED
          sts_serial|  16|    100000|     100|0.78015256|  PASSED
         rgb_bitdist|   1|    100000|     100|0.94429702|  PASSED
         rgb_bitdist|   2|    100000|     100|0.10313007|  PASSED
         rgb_bitdist|   3|    100000|     100|0.90223816|  PASSED
         rgb_bitdist|   4|    100000|     100|0.68963135|  PASSED
         rgb_bitdist|   5|    100000|     100|0.65342213|  PASSED
         rgb_bitdist|   6|    100000|     100|0.95137662|  PASSED
         rgb_bitdist|   7|    100000|     100|0.73613516|  PASSED
         rgb_bitdist|   8|    100000|     100|0.81898532|  PASSED
         rgb_bitdist|   9|    100000|     100|0.94512007|  PASSED
         rgb_bitdist|  10|    100000|     100|0.38069283|  PASSED
         rgb_bitdist|  11|    100000|     100|0.91761669|  PASSED
         rgb_bitdist|  12|    100000|     100|0.27099379|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.31981029|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.32152379|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.58468148|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.50747038|  PASSED
    rgb_permutations|   2|    100000|     100|0.55010395|  PASSED
    rgb_permutations|   3|    100000|     100|0.27304509|  PASSED
    rgb_permutations|   4|    100000|     100|0.74116498|  PASSED
    rgb_permutations|   5|    100000|     100|0.91590539|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.09606700|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.79137886|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.53361428|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.97424816|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.30780408|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.18675541|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.58156092|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.22296032|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.17602100|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.63164274|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.37063729|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.35450935|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.82426435|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.06128432|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.79921156|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.75075338|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.80886576|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.13530000|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.58378418|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.94365590|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.38613024|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.99875371|   WEAK
      rgb_lagged_sum|  22|   1000000|     100|0.86508274|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.87870751|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.44441377|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.26767767|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.79706592|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.70329930|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.31836202|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.06345306|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.30841832|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.64303033|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.31288890|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.32333432|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.52495417|  PASSED
             dab_dct| 256|     50000|       1|0.93094461|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.19540574|  PASSED
        dab_filltree|  32|  15000000|       1|0.88076682|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.41445039|  PASSED
       dab_filltree2|   1|   5000000|       1|0.91782687|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.53180785|  PASSED
#=============================================================================#
# Summary: PASSED=110, WEAK=4, FAILED=0                                       #
#          235,031.250 MB of random data created with 98.288 MB/sec           #
#=============================================================================#
#=============================================================================#
# Runtime: 0:39:51                                                            #
#=============================================================================#
*/
