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
import static org.jenetics.random.utils.readInt;
import static org.jenetics.random.utils.readLong;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

/**
 * Implementation of an simple PRNG as proposed in
 * <a href="http://www0.cs.ucl.ac.uk/staff/d.jones/GoodPracticeRNG.pdf">
 * Good Practice in (Pseudo) Random Number Generation for Bioinformatics
 * Applications</a> (JKISS64, page 10) by <em><a href="mailto:d.jones@cs.ucl.ac.uk">
 * David Jones</a>, UCL Bioinformatics Group</em>.
 *
 * <p>
 * The following listing shows the actual PRNG implementation.
 * <pre>{@code
 * private long x, y = <seed>
 * private int z1, c1, z2, c2 = <seed>
 *
 * long nextLong() {
 *     x = 0x14ADA13ED78492ADL*x + 123456789;
 *
 *     y ^= y << 21;
 *     y ^= y >>> 17;
 *     y ^= y << 30;
 *
 *     long t = 4294584393L*_z1 + _c1;
 *     c1 = (int)(t >> 32);
 *     z1 = (int)t;
 *
 *     t = 4246477509L*z2 + c2;
 *     c2 = (int)(t >> 32);
 *     z2 = (int)t;
 *
 *     return x + y + z1 + ((long)z2 << 32);
 * }
 * }</pre>
 *
 * <p>
 * The period of this <i>PRNG</i> is &asymp; 2<sup>250</sup>
 * &asymp; 1.8&sdot;10<sup>75</sup>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public class KISS64Random extends Random64 {

	private static final long serialVersionUID = 1L;

	/**
	 * This class represents a <i>thread local</i> implementation of the
	 * {@code KISS64Random} PRNG.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since !__version__!
	 * @version !__version__!
	 */
	public static final class ThreadLocal
		extends java.lang.ThreadLocal<KISS64Random>
	{
		@Override
		protected KISS64Random initialValue() {
			return new TLKISS64Random(seedBytes());
		}
	}

	private static final class TLKISS64Random extends KISS64Random {
		private static final long serialVersionUID = 1L;

		private final Boolean _sentry = Boolean.TRUE;

		private TLKISS64Random(final byte[] seed) {
			super(seed);
		}

		@Override
		public void setSeed(final byte[] seed) {
			if (_sentry != null) {
				throw new UnsupportedOperationException(
					"The 'setSeed' method is not supported " +
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
	public static final class ThreadSafe extends KISS64Random {
		private static final long serialVersionUID = 1L;

		/**
		 * Create a new thread-safe instance of the {@code KISS64Random} engine.
		 *
		 * <pre>{@code
		 * final byte[] seed = KISS64Random.seedBytes();
		 * final Random random = new KISS64Random.ThreadSafe(seed);
		 * }</pre>
		 *
		 * @see #seedBytes()
		 *
		 * @param seed the random seed value. The seed must be (at least)
		 *        {@link #SEED_BYTES} long.
		 * @throws IllegalArgumentException if the given seed is shorter than
		 *         {@link #SEED_BYTES}
		 */
		public ThreadSafe(final byte[] seed) {
			super(seed);
		}

		/**
		 * Create a new thread-safe instance of the {@code KISS64Random} engine.
		 * The constructed PRNG is equivalent with
		 * <pre>{@code
		 * final long seed = ...;
		 * final Random random = new KISS64Random.ThreadSafe();
		 * random.setSeed(seed);
		 * }</pre>
		 * which is there for compatibility reasons with the Java {@link Random}
		 * engine.
		 *
		 * @param seed the random seed value
		 */
		public ThreadSafe(final long seed) {
			super(seed);
		}

		/**
		 * Create a new thread-safe instance of the {@code KISS64Random} engine.
		 * The PRNG is initialized with {@link #seedBytes()}.
		 */
		public ThreadSafe() {
		}

		@Override
		public synchronized void setSeed(final long seed) {
			super.setSeed(seed);
		}

		@Override
		public synchronized long nextLong() {
			return super.nextLong();
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

		State(final byte[] seed) {
			setSeed(seed);
		}

		void setSeed(final byte[] seed) {
			if (seed.length < SEED_BYTES) {
				throw new IllegalArgumentException(format(
					"Required %d seed bytes, but got %d.",
					SEED_BYTES, seed.length
				));
			}

			_x = readLong(seed, 0);
			_y = readLong(seed, 1);
			_z1 = readInt(seed, 4);
			_c1 = readInt(seed, 5);
			_z2 = readInt(seed, 6);
			_c2 = readInt(seed, 7);
		}

		@Override
		public int hashCode() {
			int hash = 31;
			hash += 37*_x + 17;
			hash += 37*_y + 17;
			hash += 37*_z1 + 17;
			hash += 37*_c1 + 17;
			hash += 37*_z2 + 17;
			hash += 37*_c2 + 17;

			return hash;
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof State &&
				_x == ((State)obj)._x &&
				_y == ((State)obj)._y &&
				_z1 == ((State)obj)._z1 &&
				_c1 == ((State)obj)._c1 &&
				_z2 == ((State)obj)._z2 &&
				_c2 == ((State)obj)._c2;
		}

		@Override
		public String toString() {
			return format(
				"State[%d, %d, %d, %d, %d, %d]",
				_x, _y, _z1, _c1, _z2, _c2
			);
		}
	}


	/* *************************************************************************
	 * Main class.
	 * ************************************************************************/

	/**
	 * The number of seed bytes (32) this PRNG requires.
	 */
	public static final int SEED_BYTES = 32;

	private final State _state;

	/**
	 * Create a new <em>not</em> thread-safe instance of the {@code KISS64Random}
	 * engine.
	 *
	 * <pre>{@code
	 * final byte[] seed = KISS64Random.seedBytes();
	 * final Random random = new KISS64Random(seed);
	 * }</pre>
	 *
	 * @see #seedBytes()
	 *
	 * @param seed the random seed value. The seed must be (at least)
	 *        {@link #SEED_BYTES} long.
	 * @throws IllegalArgumentException if the given seed is shorter than
	 *         {@link #SEED_BYTES}
	 */
	public KISS64Random(final byte[] seed) {
		_state = new State(seed);
	}

	/**
	 * Create a new <em>not</em> thread-safe instance of the {@code KISS64Random}
	 * engine. The constructed PRNG is equivalent with
	 * <pre>{@code
	 * final long seed = ...;
	 * final Random random = new KISS64Random();
	 * random.setSeed(seed);
	 * }</pre>
	 * which is there for compatibility reasons with the Java {@link Random}
	 * engine.
	 *
	 * @param seed the random seed value
	 */
	public KISS64Random(final long seed) {
		this(PRNG.seedBytes(seed, SEED_BYTES));
	}

	/**
	 * Create a new <em>not</em> thread-safe instance of the {@code KISS64Random}
	 * engine. The PRNG is initialized with {@link #seedBytes()}.
	 */
	public KISS64Random() {
		this(seedBytes());
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

	/**
	 * Initializes the PRNg with the given seed.
	 *
	 * @see #seedBytes()
	 *
	 * @param seed the random seed value. The seed must be (at least)
	 *        {@link #SEED_BYTES} big.
	 * @throws IllegalArgumentException if the given seed is shorter than
	 *         {@link #SEED_BYTES}
	 */
	public void setSeed(final byte[] seed) {
		if (_state != null) _state.setSeed(seed);
	}

	@Override
	public void setSeed(final long seed) {
		setSeed(PRNG.seedBytes(seed, SEED_BYTES));
	}

	@Override
	public int hashCode() {
		int hash = 37;
		hash += 31*_state.hashCode() + 17;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof KISS64Random &&
			Objects.equals(((KISS64Random)obj)._state, _state);
	}

	@Override
	public String toString() {
		return format("%s[%s]", getClass().getSimpleName(), _state);
	}

	/**
	 * Create a new <em>seed</em> byte array suitable for this PRNG. The
	 * returned seed array is {@link #SEED_BYTES} long.
	 *
	 * @see PRNG#seedBytes(int)
	 *
	 * @return a new <em>seed</em> byte array of length {@link #SEED_BYTES}
	 */
	public static byte[] seedBytes() {
		return PRNG.seedBytes(SEED_BYTES);
	}

}

/*
#=============================================================================#
# Testing: org.jenetics.random.KISS64Random (2015-01-17 00:35)                #
#=============================================================================#
#=============================================================================#
# Linux 3.16.0-29-generic (amd64)                                             #
# java version "1.8.0_25"                                                     #
# Java(TM) SE Runtime Environment (build 1.8.0_25-b17)                        #
# Java HotSpot(TM) 64-Bit Server VM (build 25.25-b02)                         #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  3.44e+07  |3307330384|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.55287259|  PASSED
      diehard_operm5|   0|   1000000|     100|0.92365483|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.52708103|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.56393705|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.63360501|  PASSED
        diehard_opso|   0|   2097152|     100|0.99447693|  PASSED
        diehard_oqso|   0|   2097152|     100|0.07109955|  PASSED
         diehard_dna|   0|   2097152|     100|0.54965784|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.69657581|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.58691933|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.51526020|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.72650522|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.45061608|  PASSED
     diehard_squeeze|   0|    100000|     100|0.68709426|  PASSED
        diehard_sums|   0|       100|     100|0.01608992|  PASSED
        diehard_runs|   0|    100000|     100|0.05046328|  PASSED
        diehard_runs|   0|    100000|     100|0.66878534|  PASSED
       diehard_craps|   0|    200000|     100|0.91610485|  PASSED
       diehard_craps|   0|    200000|     100|0.13202614|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.16945300|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.41093390|  PASSED
         sts_monobit|   1|    100000|     100|0.93079438|  PASSED
            sts_runs|   2|    100000|     100|0.22155964|  PASSED
          sts_serial|   1|    100000|     100|0.03921031|  PASSED
          sts_serial|   2|    100000|     100|0.28979934|  PASSED
          sts_serial|   3|    100000|     100|0.22516776|  PASSED
          sts_serial|   3|    100000|     100|0.40722402|  PASSED
          sts_serial|   4|    100000|     100|0.33952751|  PASSED
          sts_serial|   4|    100000|     100|0.92495857|  PASSED
          sts_serial|   5|    100000|     100|0.68458775|  PASSED
          sts_serial|   5|    100000|     100|0.99837919|   WEAK
          sts_serial|   6|    100000|     100|0.90111776|  PASSED
          sts_serial|   6|    100000|     100|0.46063937|  PASSED
          sts_serial|   7|    100000|     100|0.96780932|  PASSED
          sts_serial|   7|    100000|     100|0.63842973|  PASSED
          sts_serial|   8|    100000|     100|0.30404664|  PASSED
          sts_serial|   8|    100000|     100|0.87309380|  PASSED
          sts_serial|   9|    100000|     100|0.09952701|  PASSED
          sts_serial|   9|    100000|     100|0.36628674|  PASSED
          sts_serial|  10|    100000|     100|0.50782043|  PASSED
          sts_serial|  10|    100000|     100|0.33580786|  PASSED
          sts_serial|  11|    100000|     100|0.73257995|  PASSED
          sts_serial|  11|    100000|     100|0.26759534|  PASSED
          sts_serial|  12|    100000|     100|0.86831954|  PASSED
          sts_serial|  12|    100000|     100|0.61364870|  PASSED
          sts_serial|  13|    100000|     100|0.69255568|  PASSED
          sts_serial|  13|    100000|     100|0.32069418|  PASSED
          sts_serial|  14|    100000|     100|0.76121712|  PASSED
          sts_serial|  14|    100000|     100|0.21844078|  PASSED
          sts_serial|  15|    100000|     100|0.50266718|  PASSED
          sts_serial|  15|    100000|     100|0.06756362|  PASSED
          sts_serial|  16|    100000|     100|0.36930817|  PASSED
          sts_serial|  16|    100000|     100|0.70590375|  PASSED
         rgb_bitdist|   1|    100000|     100|0.84571784|  PASSED
         rgb_bitdist|   2|    100000|     100|0.43347439|  PASSED
         rgb_bitdist|   3|    100000|     100|0.85764768|  PASSED
         rgb_bitdist|   4|    100000|     100|0.11952186|  PASSED
         rgb_bitdist|   5|    100000|     100|0.30194398|  PASSED
         rgb_bitdist|   6|    100000|     100|0.23054152|  PASSED
         rgb_bitdist|   7|    100000|     100|0.95094301|  PASSED
         rgb_bitdist|   8|    100000|     100|0.80778440|  PASSED
         rgb_bitdist|   9|    100000|     100|0.78380176|  PASSED
         rgb_bitdist|  10|    100000|     100|0.66925006|  PASSED
         rgb_bitdist|  11|    100000|     100|0.56829120|  PASSED
         rgb_bitdist|  12|    100000|     100|0.08250980|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.10401739|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.30154584|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.30857282|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.95752630|  PASSED
    rgb_permutations|   2|    100000|     100|0.01682582|  PASSED
    rgb_permutations|   3|    100000|     100|0.14749399|  PASSED
    rgb_permutations|   4|    100000|     100|0.70746164|  PASSED
    rgb_permutations|   5|    100000|     100|0.97472092|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.81861304|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.07676359|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.95210438|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.14770274|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.64584725|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.94581535|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.67100796|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.91895855|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.31208572|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.10658091|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.31452722|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.51254439|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.99389072|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.99115757|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.76357499|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.37237307|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.31348435|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.62504686|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.31066635|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.42463409|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.75989697|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.18362396|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.00239427|   WEAK
      rgb_lagged_sum|  23|   1000000|     100|0.60091260|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.27042591|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.62861837|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.04262765|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.84944964|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.79777048|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.75513511|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.45293703|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.74755927|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.96727408|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.93909523|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.01712324|  PASSED
             dab_dct| 256|     50000|       1|0.83018411|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.75879412|  PASSED
        dab_filltree|  32|  15000000|       1|0.20900992|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.39626571|  PASSED
       dab_filltree2|   1|   5000000|       1|0.75551838|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.07178559|  PASSED
#=============================================================================#
# Summary: PASSED=112, WEAK=2, FAILED=0                                       #
#          235,031.266 MB of random data created with 98.535 MB/sec           #
#=============================================================================#
#=============================================================================#
# Runtime: 0:39:45                                                            #
#=============================================================================#
*/
