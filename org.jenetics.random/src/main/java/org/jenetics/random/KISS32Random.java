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

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

/**
 * Implementation of an simple PRNG as proposed in
 * <a href="http://www0.cs.ucl.ac.uk/staff/d.jones/GoodPracticeRNG.pdf">
 * Good Practice in (Pseudo) Random Number Generation for Bioinformatics
 * Applications</a> (JKISS32, page 3) by <em><a href="mailto:d.jones@cs.ucl.ac.uk">
 * David Jones</a>, UCL Bioinformatics Group</em>.
 * <p>
 * The following listing shows the actual PRNG implementation.
 * <pre>{@code
 * private int x, y, z, w = <seed>
 * private int c = 0;
 *
 * int nextInt() {
 *     int t;
 *     y ^= y << 5;
 *     y ^= y >>> 7;
 *     y ^= y << 22;
 *
 *     t = z + w + c;
 *     z = w;
 *     c = t >>> 31;
 *     w = t & 2147483647;
 *     x += 1411392427;
 *
 *     return x + y + w;
 * }
 * }</pre>
 *
 * <p>
 * The period of this <i>PRNG</i> is &asymp; 2<sup>121</sup>
 * &asymp; 2.6&sdot;10<sup>36</sup>
 *
 * <p>
 * <strong>Not that the base implementation of the {@code KISS32Random}
 * class is not thread-safe.</strong> If multiple threads requests random
 * numbers from this class, it <i>must</i> be synchronized externally.
 * Alternatively you can use the thread-safe implementations
 * {@link KISS32Random.ThreadSafe} or {@link KISS32Random.ThreadLocal}.
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

		/**
		 * Create a new PRNG using different seed values for every thread.
		 */
		@Override
		protected KISS32Random initialValue() {
			return new TLRandom();
		}
	}

	private static final class TLRandom extends KISS32Random {
		private static final long serialVersionUID = 1L;

		private final Boolean _sentry = Boolean.TRUE;

		private TLRandom() {
			super(KISS32Random.seedBytes());
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
	public static final class ThreadSafe extends KISS32Random {
		private static final long serialVersionUID = 1L;

		/**
		 * Create a new thread-safe instance of the {@code KISS32Random}
		 * engine.
		 *
		 * <pre>{@code
		 * final byte[] seed = KISS32Random.seedBytes();
		 * final Random random = new KISS32Random.ThreadSafe(seed);
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
		 * Create a new thread-safe instance of the {@code KISS32Random}
		 * engine. The constructed PRNG is equivalent with
		 * <pre>{@code
		 * final long seed = ...;
		 * final Random random = new KISS32Random.ThreadSafe();
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
		 * Create a new thread-safe instance of the {@code KISS32Random}
		 * engine. The PRNG is initialized with {@link #seedBytes()}.
		 */
		public ThreadSafe() {
		}

		@Override
		public synchronized void setSeed(final byte[] seed) {
			super.setSeed(seed);
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
	 * The internal state of random engine.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since !__version__!
	 * @version !__version__!
	 */
	private static final class State implements Serializable {
		private static final long serialVersionUID = 1L;

		int _x = 123456789;
		int _y = 234567891;
		int _z = 345678912;
		int _w = 456789123;
		int _c = 0;

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

			_x = readInt(seed, 0);
			_y = readInt(seed, 1);
			_z = readInt(seed, 2);
			_w = readInt(seed, 3);
		}

		@Override
		public int hashCode() {
			int hash = 31;
			hash += 37*_x + 17;
			hash += 37*_y + 17;
			hash += 37*_z + 17;
			hash += 37*_w + 17;
			hash += 37*_c + 17;

			return hash;
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

	/* *************************************************************************
	 * Main class.
	 * ************************************************************************/

	/**
	 * The number of seed bytes (16) this PRNG requires.
	 */
	public static final int SEED_BYTES = 16;

	private final State _state;

	/**
	 * Create a new <em>not</em> thread-safe instance of the {@code KISS32Random}
	 * engine.
	 *
	 * <pre>{@code
	 * final byte[] seed = KISS32Random.seedBytes();
	 * final Random random = new KISS32Random(seed);
	 * }</pre>
	 *
	 * @see #seedBytes()
	 *
	 * @param seed the random seed value. The seed must be (at least)
	 *        {@link #SEED_BYTES} long.
	 * @throws IllegalArgumentException if the given seed is shorter than
	 *         {@link #SEED_BYTES}
	 */
	public KISS32Random(final byte[] seed) {
		_state = new State(seed);
	}

	/**
	 * Create a new <em>not</em> thread-safe instance of the {@code KISS32Random}
	 * engine. The constructed PRNG is equivalent with
	 * <pre>{@code
	 * final long seed = ...;
	 * final Random random = new KISS32Random();
	 * random.setSeed(seed);
	 * }</pre>
	 * which is there for compatibility reasons with the Java {@link Random}
	 * engine.
	 *
	 * @param seed the random seed value
	 */
	public KISS32Random(final long seed) {
		this(seedBytes(seed, SEED_BYTES));
	}

	/**
	 * Create a new <em>not</em> thread-safe instance of the {@code KISS32Random}
	 * engine. The PRNG is initialized with {@link #seedBytes()}.
	 */
	public KISS32Random() {
		this(seedBytes());
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

		final int t = _state._z + _state._w + _state._c;
		_state._z = _state._w;
		_state._c = t >>> 31;
		_state._w = t&2147483647;
		_state._x += 1411392427;
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
		return obj instanceof KISS32Random &&
			Objects.equals(((KISS32Random)obj)._state, _state);
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
# Testing: org.jenetics.random.KISS32Random (2016-08-22 19:01)                #
#=============================================================================#
#=============================================================================#
# Linux 4.4.0-34-generic (amd64)                                              #
# java version "1.8.0_102"                                                    #
# Java(TM) SE Runtime Environment (build 1.8.0_102-b14)                       #
# Java HotSpot(TM) 64-Bit Server VM (build 25.102-b14)                        #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  5.60e+07  |3596930239|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.10271522|  PASSED
      diehard_operm5|   0|   1000000|     100|0.90523014|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.99570319|   WEAK
    diehard_rank_6x8|   0|    100000|     100|0.20389426|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.92564404|  PASSED
        diehard_opso|   0|   2097152|     100|0.40664785|  PASSED
        diehard_oqso|   0|   2097152|     100|0.14674418|  PASSED
         diehard_dna|   0|   2097152|     100|0.09103884|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.95078425|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.59404857|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.72169377|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.59717810|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.96544956|  PASSED
     diehard_squeeze|   0|    100000|     100|0.60076899|  PASSED
        diehard_sums|   0|       100|     100|0.01634155|  PASSED
        diehard_runs|   0|    100000|     100|0.86485720|  PASSED
        diehard_runs|   0|    100000|     100|0.97438888|  PASSED
       diehard_craps|   0|    200000|     100|0.96396341|  PASSED
       diehard_craps|   0|    200000|     100|0.19283878|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.28651271|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.59316480|  PASSED
         sts_monobit|   1|    100000|     100|0.40787222|  PASSED
            sts_runs|   2|    100000|     100|0.06697199|  PASSED
          sts_serial|   1|    100000|     100|0.13155121|  PASSED
          sts_serial|   2|    100000|     100|0.87024921|  PASSED
          sts_serial|   3|    100000|     100|0.52116716|  PASSED
          sts_serial|   3|    100000|     100|0.64803681|  PASSED
          sts_serial|   4|    100000|     100|0.48963275|  PASSED
          sts_serial|   4|    100000|     100|0.64752128|  PASSED
          sts_serial|   5|    100000|     100|0.97381079|  PASSED
          sts_serial|   5|    100000|     100|0.89044043|  PASSED
          sts_serial|   6|    100000|     100|0.80694815|  PASSED
          sts_serial|   6|    100000|     100|0.31661444|  PASSED
          sts_serial|   7|    100000|     100|0.98193895|  PASSED
          sts_serial|   7|    100000|     100|0.66717369|  PASSED
          sts_serial|   8|    100000|     100|0.99326925|  PASSED
          sts_serial|   8|    100000|     100|0.77934442|  PASSED
          sts_serial|   9|    100000|     100|0.81158069|  PASSED
          sts_serial|   9|    100000|     100|0.70495169|  PASSED
          sts_serial|  10|    100000|     100|0.23003793|  PASSED
          sts_serial|  10|    100000|     100|0.11563741|  PASSED
          sts_serial|  11|    100000|     100|0.50645319|  PASSED
          sts_serial|  11|    100000|     100|0.08808045|  PASSED
          sts_serial|  12|    100000|     100|0.20923477|  PASSED
          sts_serial|  12|    100000|     100|0.01412164|  PASSED
          sts_serial|  13|    100000|     100|0.27413142|  PASSED
          sts_serial|  13|    100000|     100|0.15554319|  PASSED
          sts_serial|  14|    100000|     100|0.05167846|  PASSED
          sts_serial|  14|    100000|     100|0.63690425|  PASSED
          sts_serial|  15|    100000|     100|0.01233959|  PASSED
          sts_serial|  15|    100000|     100|0.01191752|  PASSED
          sts_serial|  16|    100000|     100|0.38632004|  PASSED
          sts_serial|  16|    100000|     100|0.77249878|  PASSED
         rgb_bitdist|   1|    100000|     100|0.40792167|  PASSED
         rgb_bitdist|   2|    100000|     100|0.81034990|  PASSED
         rgb_bitdist|   3|    100000|     100|0.48950873|  PASSED
         rgb_bitdist|   4|    100000|     100|0.50612605|  PASSED
         rgb_bitdist|   5|    100000|     100|0.95295878|  PASSED
         rgb_bitdist|   6|    100000|     100|0.14291821|  PASSED
         rgb_bitdist|   7|    100000|     100|0.44651428|  PASSED
         rgb_bitdist|   8|    100000|     100|0.97090514|  PASSED
         rgb_bitdist|   9|    100000|     100|0.99576880|   WEAK
         rgb_bitdist|  10|    100000|     100|0.18860863|  PASSED
         rgb_bitdist|  11|    100000|     100|0.44011922|  PASSED
         rgb_bitdist|  12|    100000|     100|0.23523165|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.65381494|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.87861604|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.91949184|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.09857976|  PASSED
    rgb_permutations|   2|    100000|     100|0.42451299|  PASSED
    rgb_permutations|   3|    100000|     100|0.94349673|  PASSED
    rgb_permutations|   4|    100000|     100|0.82438398|  PASSED
    rgb_permutations|   5|    100000|     100|0.12830440|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.99548028|   WEAK
      rgb_lagged_sum|   1|   1000000|     100|0.43708584|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.84494391|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.71452319|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.57210316|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.06915242|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.96871055|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.21830056|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.53220580|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.43201103|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.72983883|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.92353536|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.07064761|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.89344408|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.47278959|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.26306178|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.33318758|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.92351456|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.94849083|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.49119619|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.97172130|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.60477707|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.29065098|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.76460795|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.77239034|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.71668053|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.75845547|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.40019633|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.71962290|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.40083496|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.37064283|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.23932781|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.94499132|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.02553371|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.70562095|  PASSED
             dab_dct| 256|     50000|       1|0.80270650|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.55289999|  PASSED
        dab_filltree|  32|  15000000|       1|0.89677929|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.88054487|  PASSED
       dab_filltree2|   1|   5000000|       1|0.67757840|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.88881094|  PASSED
#=============================================================================#
# Summary: PASSED=111, WEAK=3, FAILED=0                                       #
#          235,031.133 MB of random data created with 122.271 MB/sec          #
#=============================================================================#
#=============================================================================#
# Runtime: 0:32:02                                                            #
#=============================================================================#
*/
