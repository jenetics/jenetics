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
 * Applications</a> (page 3) by <em><a href="mailto:d.jones@cs.ucl.ac.uk">
 * David Jones</a>, UCL Bioinformatics Group</em>.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__! &mdash; <em>$Date: 2015-01-09 $</em>
 */
public class KISS32Random extends Random32 {

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
	 * @version !__version__! &mdash; <em>$Date: 2015-01-09 $</em>
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
			return Equality.of(this, obj).test(state ->
				eq(_x, state._x) &&
				eq(_y, state._y) &&
				eq(_z, state._z) &&
				eq(_w, state._w) &&
				eq(_c, state._c)
			);
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
# Testing: org.jenetics.random.KISS32Random (2015-01-09 21:30)                #
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
stdin_input_raw|  3.26e+07  |3757304324|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.69959358|  PASSED
      diehard_operm5|   0|   1000000|     100|0.42261930|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.51146349|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.83950086|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.38006721|  PASSED
        diehard_opso|   0|   2097152|     100|0.34345654|  PASSED
        diehard_oqso|   0|   2097152|     100|0.91640741|  PASSED
         diehard_dna|   0|   2097152|     100|0.00690152|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.43900187|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.80150596|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.99758746|   WEAK
    diehard_2dsphere|   2|      8000|     100|0.90875027|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.20854940|  PASSED
     diehard_squeeze|   0|    100000|     100|0.73566420|  PASSED
        diehard_sums|   0|       100|     100|0.10543127|  PASSED
        diehard_runs|   0|    100000|     100|0.37561571|  PASSED
        diehard_runs|   0|    100000|     100|0.58048853|  PASSED
       diehard_craps|   0|    200000|     100|0.93106605|  PASSED
       diehard_craps|   0|    200000|     100|0.94878378|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.31696329|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.52325802|  PASSED
         sts_monobit|   1|    100000|     100|0.58961233|  PASSED
            sts_runs|   2|    100000|     100|0.22066231|  PASSED
          sts_serial|   1|    100000|     100|0.51519204|  PASSED
          sts_serial|   2|    100000|     100|0.26716839|  PASSED
          sts_serial|   3|    100000|     100|0.12616487|  PASSED
          sts_serial|   3|    100000|     100|0.20024817|  PASSED
          sts_serial|   4|    100000|     100|0.25067860|  PASSED
          sts_serial|   4|    100000|     100|0.50233570|  PASSED
          sts_serial|   5|    100000|     100|0.69789581|  PASSED
          sts_serial|   5|    100000|     100|0.47407660|  PASSED
          sts_serial|   6|    100000|     100|0.94372044|  PASSED
          sts_serial|   6|    100000|     100|0.82539865|  PASSED
          sts_serial|   7|    100000|     100|0.84163295|  PASSED
          sts_serial|   7|    100000|     100|0.25870489|  PASSED
          sts_serial|   8|    100000|     100|0.46044151|  PASSED
          sts_serial|   8|    100000|     100|0.40570280|  PASSED
          sts_serial|   9|    100000|     100|0.56119348|  PASSED
          sts_serial|   9|    100000|     100|0.95271166|  PASSED
          sts_serial|  10|    100000|     100|0.84294584|  PASSED
          sts_serial|  10|    100000|     100|0.76657804|  PASSED
          sts_serial|  11|    100000|     100|0.80317944|  PASSED
          sts_serial|  11|    100000|     100|0.78026909|  PASSED
          sts_serial|  12|    100000|     100|0.53595970|  PASSED
          sts_serial|  12|    100000|     100|0.76819286|  PASSED
          sts_serial|  13|    100000|     100|0.49813553|  PASSED
          sts_serial|  13|    100000|     100|0.04662170|  PASSED
          sts_serial|  14|    100000|     100|0.30526399|  PASSED
          sts_serial|  14|    100000|     100|0.95943504|  PASSED
          sts_serial|  15|    100000|     100|0.56689922|  PASSED
          sts_serial|  15|    100000|     100|0.89104521|  PASSED
          sts_serial|  16|    100000|     100|0.12433490|  PASSED
          sts_serial|  16|    100000|     100|0.62557937|  PASSED
         rgb_bitdist|   1|    100000|     100|0.68854207|  PASSED
         rgb_bitdist|   2|    100000|     100|0.31036007|  PASSED
         rgb_bitdist|   3|    100000|     100|0.53754093|  PASSED
         rgb_bitdist|   4|    100000|     100|0.46881843|  PASSED
         rgb_bitdist|   5|    100000|     100|0.79620212|  PASSED
         rgb_bitdist|   6|    100000|     100|0.46600306|  PASSED
         rgb_bitdist|   7|    100000|     100|0.13342856|  PASSED
         rgb_bitdist|   8|    100000|     100|0.97882095|  PASSED
         rgb_bitdist|   9|    100000|     100|0.10588070|  PASSED
         rgb_bitdist|  10|    100000|     100|0.55366964|  PASSED
         rgb_bitdist|  11|    100000|     100|0.99724273|   WEAK
         rgb_bitdist|  12|    100000|     100|0.60663329|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.84780910|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.50388135|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.25211595|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.26792566|  PASSED
    rgb_permutations|   2|    100000|     100|0.18400685|  PASSED
    rgb_permutations|   3|    100000|     100|0.07887513|  PASSED
    rgb_permutations|   4|    100000|     100|0.44434125|  PASSED
    rgb_permutations|   5|    100000|     100|0.47104588|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.28770223|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.82903396|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.95536620|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.17046354|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.58865110|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.68980141|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.93488073|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.35331104|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.93328944|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.05140788|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.96448377|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.46083461|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.69240181|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.94519160|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.99729505|   WEAK
      rgb_lagged_sum|  15|   1000000|     100|0.95097149|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.55457159|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.87433523|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.61285787|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.86634896|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.37777915|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.32948049|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.54974627|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.97083621|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.99435357|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.45926723|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.24744443|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.79917964|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.62264376|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.79425656|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.87532289|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.75199907|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.62488887|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.30005931|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.51093575|  PASSED
             dab_dct| 256|     50000|       1|0.90521126|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.90871507|  PASSED
        dab_filltree|  32|  15000000|       1|0.97134913|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.13766733|  PASSED
       dab_filltree2|   1|   5000000|       1|0.12433319|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.38842899|  PASSED
#=============================================================================#
# Summary: PASSED=111, WEAK=3, FAILED=0                                       #
#          235,031.398 MB of random data created with 94.636 MB/sec           #
#=============================================================================#
#=============================================================================#
# Runtime: 0:41:23                                                            #
#=============================================================================#
*/
