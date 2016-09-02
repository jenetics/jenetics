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
import static org.jenetics.random.utils.readLong;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * This is a 64-bit version of Mersenne Twister pseudorandom number generator.
 * <p>
 * <i>
 * References: M. Matsumoto and T. Nishimura, "Mersenne Twister: a
 * 623-dimensionally equidistributed uniform pseudorandom number generator".
 * ACM Transactions on Modeling and Computer Simulation 8. (Jan. 1998) 3--30.
 * </i>
 * <p>
 * <em>
 * This is an re-implementation of the
 * <a href="https://github.com/rabauke/trng4/blob/master/src/mt19937_64.hpp">
 * trng::mt19937_64</a> PRNG class of the
 * <a href="http://numbercrunch.de/trng/">TRNG</a> library created by Heiko
 * Bauke.</em>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public class MT19937_64Random extends Random64 {

	private static final long serialVersionUID = 1L;

	private static final int N = 312;
	private static final int M = 156;

	private static final long UM = 0xFFFFFFFF80000000L; // most significant bit
	private static final long LM = 0x7FFFFFFFL;         // least significant 31 bits

	private static final long[] MAG01 = {0L, 0xB5026F5AA96619E9L};


	/**
	 * This class represents a <i>thread local</i> implementation of the
	 * {@code MT19937_32Random} PRNG.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since !__version__!
	 * @version !__version__!
	 */
	public static class ThreadLocal
		extends java.lang.ThreadLocal<MT19937_64Random>
	{
		@Override
		protected MT19937_64Random initialValue() {
			return new TLMT19937_64Random(math.seed());
		}
	}

	private static final class TLMT19937_64Random extends MT19937_64Random {
		private static final long serialVersionUID = 1L;

		private final Boolean _sentry = Boolean.TRUE;

		private TLMT19937_64Random(final long seed) {
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
	public static final class ThreadSafe extends MT19937_64Random {
		private static final long serialVersionUID = 1L;

		/**
		 * Create a new thread-safe instance of the {@code MT19937_64Random}
		 * engine.
		 *
		 * <pre>{@code
		 * final byte[] seed = MT19937_64Random.seedBytes();
		 * final Random random = new MT19937_64Random.ThreadSafe(seed);
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
		 * Create a new thread-safe instance of the {@code MT19937_64Random}
		 * engine. The constructed PRNG is equivalent with
		 * <pre>{@code
		 * final long seed = ...;
		 * final Random random = new MT19937_64Random.ThreadSafe();
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
		 * Create a new thread-safe instance of the {@code MT19937_64Random}
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
		public synchronized long nextLong() {
			return super.nextLong();
		}

	}


	/**
	 * The internal state of this PRNG.
	 */
	private static final class State implements Serializable {
		private static final long serialVersionUID = 1L;

		int mti;
		final long[] mt = new long[N];

		State(final byte[] seed) {
			setSeed(seed);
		}

		State(final long seed) {
			setSeed(seed);
		}

		void setSeed(final byte[] seed) {
			if (seed.length < SEED_BYTES) {
				throw new IllegalArgumentException(format(
					"Required %d seed bytes, but got %d.",
					SEED_BYTES, seed.length
				));
			}

			for (mti = 0; mti < N; ++mti) {
				mt[mti] = readLong(seed, mti);
			}
		}

		void setSeed(final long s) {
			mt[0] = s;
			for (mti = 1; mti < N; ++mti) {
				mt[mti] = 6364136223846793005L*
					(mt[mti - 1]^(mt[mti - 1] >>> 62)) +
					mti;
			}
		}

		@Override
		public int hashCode() {
			int hash = 17;
			hash += 37*mti + 31;
			hash += 37*Arrays.hashCode(mt) + 31;
			return hash;
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof State &&
				mti == ((State)obj).mti &&
				Arrays.equals(mt, ((State)obj).mt);
		}

	}


	/* *************************************************************************
	 * Main class.
	 * ************************************************************************/

	/**
	 * The number of seed bytes (2,496) this PRNG requires.
	 */
	public static final int SEED_BYTES = N*Long.BYTES;

	private final State _state;

	/**
	 * Create a new <em>not</em> thread-safe instance of the
	 * {@code MT19937_32Random} engine.
	 *
	 * <pre>{@code
	 * final byte[] seed = MT19937_64Random.seedBytes();
	 * final Random random = new MT19937_64Random(seed);
	 * }</pre>
	 *
	 * @see #seedBytes()
	 *
	 * @param seed the random seed value. The seed must be (at least)
	 *        {@link #SEED_BYTES} long.
	 * @throws IllegalArgumentException if the given seed is shorter than
	 *         {@link #SEED_BYTES}
	 */
	public MT19937_64Random(final byte[] seed) {
		_state = new State(seed);
	}

	/**
	 * Create a new <em>not</em> thread-safe instance of the
	 * {@code MT19937_64Random} engine. The constructed PRNG is equivalent with
	 * <pre>{@code
	 * final long seed = ...;
	 * final Random random = new MT19937_64Random();
	 * random.setSeed(seed);
	 * }</pre>
	 * which is there for compatibility reasons with the Java {@link Random}
	 * engine.
	 *
	 * @param seed the random seed value
	 */
	public MT19937_64Random(final long seed) {
		_state = new State(seed);
	}

	/**
	 * Return a new random engine with a safe seed value.
	 */
	public MT19937_64Random() {
		this(seedBytes());
	}

	@Override
	public long nextLong() {
		long x;

		if (_state.mti >= N) { // generate N words at one time
			int i;
			for (i = 0; i < N - M; ++i) {
				x =(_state.mt[i] & UM) | (_state.mt[i + 1] & LM);
				_state.mt[i] = _state.mt[i + M]^(x >>> 1)^MAG01[(int)(x & 1L)];
			}
			for (; i < N - 1; ++i) {
				x = (_state.mt[i] & UM) | (_state.mt[i + 1] & LM);
				_state.mt[i]= _state.mt[i + (M - N)]^(x >>> 1)^MAG01[(int)(x & 1)];
			}

			x = (_state.mt[N - 1] & UM) | (_state.mt[0] & LM);
			_state.mt[N - 1] = _state.mt[M - 1]^(x >>> 1)^MAG01[(int)(x & 1)];
			_state.mti = 0;
		}

		x = _state.mt[_state.mti++];
		x ^= (x >>> 29) & 0x5555555555555555L;
		x ^= (x << 17) & 0x71D67FFFEDA60000L;
		x ^= (x << 37) & 0xFFF7EEE000000000L;
		x ^= x >>> 43;

		return x;
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
		if (_state != null) _state.setSeed(seed);
	}

	@Override
	public int hashCode() {
		int hash = 37;
		hash += 31*_state.hashCode() + 17;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof MT19937_64Random &&
			Objects.equals(((MT19937_64Random)obj)._state, _state);
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
# Testing: org.jenetics.random.MT19937_64Random (2016-09-02 17:10)            #
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
stdin_input_raw|  4.63e+07  |1773939362|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.78555543|  PASSED
      diehard_operm5|   0|   1000000|     100|0.96519584|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.30773671|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.78071394|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.07959570|  PASSED
        diehard_opso|   0|   2097152|     100|0.19593671|  PASSED
        diehard_oqso|   0|   2097152|     100|0.69843874|  PASSED
         diehard_dna|   0|   2097152|     100|0.19129375|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.12250056|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.67610411|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.12845620|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.08919709|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.91036835|  PASSED
     diehard_squeeze|   0|    100000|     100|0.25256349|  PASSED
        diehard_sums|   0|       100|     100|0.34406241|  PASSED
        diehard_runs|   0|    100000|     100|0.10114614|  PASSED
        diehard_runs|   0|    100000|     100|0.01221612|  PASSED
       diehard_craps|   0|    200000|     100|0.19414750|  PASSED
       diehard_craps|   0|    200000|     100|0.93986074|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.51624235|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.81309203|  PASSED
         sts_monobit|   1|    100000|     100|0.82572032|  PASSED
            sts_runs|   2|    100000|     100|0.92164995|  PASSED
          sts_serial|   1|    100000|     100|0.47046992|  PASSED
          sts_serial|   2|    100000|     100|0.63271049|  PASSED
          sts_serial|   3|    100000|     100|0.24727860|  PASSED
          sts_serial|   3|    100000|     100|0.16757863|  PASSED
          sts_serial|   4|    100000|     100|0.83907090|  PASSED
          sts_serial|   4|    100000|     100|0.92461237|  PASSED
          sts_serial|   5|    100000|     100|0.74288676|  PASSED
          sts_serial|   5|    100000|     100|0.87561674|  PASSED
          sts_serial|   6|    100000|     100|0.22237635|  PASSED
          sts_serial|   6|    100000|     100|0.31409351|  PASSED
          sts_serial|   7|    100000|     100|0.54741834|  PASSED
          sts_serial|   7|    100000|     100|0.49230845|  PASSED
          sts_serial|   8|    100000|     100|0.56020903|  PASSED
          sts_serial|   8|    100000|     100|0.30417021|  PASSED
          sts_serial|   9|    100000|     100|0.78812342|  PASSED
          sts_serial|   9|    100000|     100|0.57304427|  PASSED
          sts_serial|  10|    100000|     100|0.73141017|  PASSED
          sts_serial|  10|    100000|     100|0.80667093|  PASSED
          sts_serial|  11|    100000|     100|0.62474036|  PASSED
          sts_serial|  11|    100000|     100|0.39394191|  PASSED
          sts_serial|  12|    100000|     100|0.54069834|  PASSED
          sts_serial|  12|    100000|     100|0.57046914|  PASSED
          sts_serial|  13|    100000|     100|0.12363292|  PASSED
          sts_serial|  13|    100000|     100|0.05460302|  PASSED
          sts_serial|  14|    100000|     100|0.90731872|  PASSED
          sts_serial|  14|    100000|     100|0.66076376|  PASSED
          sts_serial|  15|    100000|     100|0.47496579|  PASSED
          sts_serial|  15|    100000|     100|0.32271018|  PASSED
          sts_serial|  16|    100000|     100|0.40496072|  PASSED
          sts_serial|  16|    100000|     100|0.96740501|  PASSED
         rgb_bitdist|   1|    100000|     100|0.08330850|  PASSED
         rgb_bitdist|   2|    100000|     100|0.38958917|  PASSED
         rgb_bitdist|   3|    100000|     100|0.44857426|  PASSED
         rgb_bitdist|   4|    100000|     100|0.23533181|  PASSED
         rgb_bitdist|   5|    100000|     100|0.94687419|  PASSED
         rgb_bitdist|   6|    100000|     100|0.50553744|  PASSED
         rgb_bitdist|   7|    100000|     100|0.99292760|  PASSED
         rgb_bitdist|   8|    100000|     100|0.78335914|  PASSED
         rgb_bitdist|   9|    100000|     100|0.96968044|  PASSED
         rgb_bitdist|  10|    100000|     100|0.66862400|  PASSED
         rgb_bitdist|  11|    100000|     100|0.01086217|  PASSED
         rgb_bitdist|  12|    100000|     100|0.99974661|   WEAK
rgb_minimum_distance|   2|     10000|    1000|0.08534182|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.64165404|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.79104994|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.38417909|  PASSED
    rgb_permutations|   2|    100000|     100|0.09696535|  PASSED
    rgb_permutations|   3|    100000|     100|0.58666197|  PASSED
    rgb_permutations|   4|    100000|     100|0.83748599|  PASSED
    rgb_permutations|   5|    100000|     100|0.39434111|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.36086586|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.68277489|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.29982444|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.37884850|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.85586629|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.59149233|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.38623381|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.50496446|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.18739505|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.59441898|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.89277026|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.36654855|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.52927582|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.81602973|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.98467600|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.37226941|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.43798277|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.73910704|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.08700579|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.93022190|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.29491500|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.98372883|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.21024025|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.93676321|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.72074943|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.95846131|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.94487360|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.80316598|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.79786120|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.21851505|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.71653732|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.34276418|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.30451985|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.38541608|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.37779269|  PASSED
             dab_dct| 256|     50000|       1|0.34360271|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.37260879|  PASSED
        dab_filltree|  32|  15000000|       1|0.67902550|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.41368443|  PASSED
       dab_filltree2|   1|   5000000|       1|0.84209827|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.45717202|  PASSED
#=============================================================================#
# Summary: PASSED=113, WEAK=1, FAILED=0                                       #
#          235,031.195 MB of random data created with 130.137 MB/sec          #
#=============================================================================#
#=============================================================================#
# Runtime: 0:30:06                                                            #
#=============================================================================#
*/
