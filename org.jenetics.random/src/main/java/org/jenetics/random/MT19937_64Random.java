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

		/**
		 * Create a new PRNG using different seed values for every thread.
		 */
		@Override
		protected MT19937_64Random initialValue() {
			return new TLRandom(PRNG.seed());
		}
	}

	private static final class TLRandom extends MT19937_64Random {
		private static final long serialVersionUID = 1L;

		private final Boolean _sentry = Boolean.TRUE;

		private TLRandom(final long seed) {
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
# Testing: org.jenetics.random.MT19937_64Random (2016-09-30 00:22)            #
#=============================================================================#
#=============================================================================#
# Linux 4.4.0-38-generic (amd64)                                              #
# java version "1.8.0_102"                                                    #
# Java(TM) SE Runtime Environment (build 1.8.0_102-b14)                       #
# Java HotSpot(TM) 64-Bit Server VM (build 25.102-b14)                        #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  4.41e+07  |3115363637|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.48902643|  PASSED
      diehard_operm5|   0|   1000000|     100|0.47934623|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.75656765|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.52484816|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.27388816|  PASSED
        diehard_opso|   0|   2097152|     100|0.21124687|  PASSED
        diehard_oqso|   0|   2097152|     100|0.85237849|  PASSED
         diehard_dna|   0|   2097152|     100|0.19408478|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.28299324|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.70766684|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.68281092|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.21459295|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.66470114|  PASSED
     diehard_squeeze|   0|    100000|     100|0.68898665|  PASSED
        diehard_sums|   0|       100|     100|0.11834279|  PASSED
        diehard_runs|   0|    100000|     100|0.39841465|  PASSED
        diehard_runs|   0|    100000|     100|0.72409053|  PASSED
       diehard_craps|   0|    200000|     100|0.99702930|   WEAK
       diehard_craps|   0|    200000|     100|0.97455900|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.29830728|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.60377397|  PASSED
         sts_monobit|   1|    100000|     100|0.65527396|  PASSED
            sts_runs|   2|    100000|     100|0.88410142|  PASSED
          sts_serial|   1|    100000|     100|0.96671860|  PASSED
          sts_serial|   2|    100000|     100|0.68300399|  PASSED
          sts_serial|   3|    100000|     100|0.95760653|  PASSED
          sts_serial|   3|    100000|     100|0.17539681|  PASSED
          sts_serial|   4|    100000|     100|0.82135268|  PASSED
          sts_serial|   4|    100000|     100|0.41055046|  PASSED
          sts_serial|   5|    100000|     100|0.05479026|  PASSED
          sts_serial|   5|    100000|     100|0.10666014|  PASSED
          sts_serial|   6|    100000|     100|0.87375563|  PASSED
          sts_serial|   6|    100000|     100|0.51895989|  PASSED
          sts_serial|   7|    100000|     100|0.95769518|  PASSED
          sts_serial|   7|    100000|     100|0.66876259|  PASSED
          sts_serial|   8|    100000|     100|0.19910222|  PASSED
          sts_serial|   8|    100000|     100|0.44721030|  PASSED
          sts_serial|   9|    100000|     100|0.88106974|  PASSED
          sts_serial|   9|    100000|     100|0.82618360|  PASSED
          sts_serial|  10|    100000|     100|0.83595932|  PASSED
          sts_serial|  10|    100000|     100|0.66415963|  PASSED
          sts_serial|  11|    100000|     100|0.62596793|  PASSED
          sts_serial|  11|    100000|     100|0.20357866|  PASSED
          sts_serial|  12|    100000|     100|0.98311772|  PASSED
          sts_serial|  12|    100000|     100|0.11548949|  PASSED
          sts_serial|  13|    100000|     100|0.92543549|  PASSED
          sts_serial|  13|    100000|     100|0.77452541|  PASSED
          sts_serial|  14|    100000|     100|0.42907628|  PASSED
          sts_serial|  14|    100000|     100|0.96860228|  PASSED
          sts_serial|  15|    100000|     100|0.81733969|  PASSED
          sts_serial|  15|    100000|     100|0.93521439|  PASSED
          sts_serial|  16|    100000|     100|0.35459646|  PASSED
          sts_serial|  16|    100000|     100|0.26113235|  PASSED
         rgb_bitdist|   1|    100000|     100|0.89994691|  PASSED
         rgb_bitdist|   2|    100000|     100|0.85588316|  PASSED
         rgb_bitdist|   3|    100000|     100|0.91485358|  PASSED
         rgb_bitdist|   4|    100000|     100|0.96781908|  PASSED
         rgb_bitdist|   5|    100000|     100|0.51230292|  PASSED
         rgb_bitdist|   6|    100000|     100|0.94280129|  PASSED
         rgb_bitdist|   7|    100000|     100|0.85747438|  PASSED
         rgb_bitdist|   8|    100000|     100|0.76017332|  PASSED
         rgb_bitdist|   9|    100000|     100|0.91585805|  PASSED
         rgb_bitdist|  10|    100000|     100|0.01347017|  PASSED
         rgb_bitdist|  11|    100000|     100|0.80216463|  PASSED
         rgb_bitdist|  12|    100000|     100|0.61043901|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.81581048|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.77530336|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.66547077|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.26582446|  PASSED
    rgb_permutations|   2|    100000|     100|0.46036257|  PASSED
    rgb_permutations|   3|    100000|     100|0.84130370|  PASSED
    rgb_permutations|   4|    100000|     100|0.61964445|  PASSED
    rgb_permutations|   5|    100000|     100|0.43553733|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.97393337|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.55835304|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.72070146|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.27604783|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.03662585|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.26650022|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.98403567|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.48147414|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.45514274|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.94831081|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.93431842|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.16926894|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.67282534|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.15564727|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.06413711|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.30036546|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.27327794|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.94981484|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.00104813|   WEAK
      rgb_lagged_sum|  19|   1000000|     100|0.52905670|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.86052428|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.55919634|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.72840539|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.52285398|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.65488288|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.71052796|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.95579436|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.81059773|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.99999786|   WEAK
      rgb_lagged_sum|  29|   1000000|     100|0.62090978|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.23146436|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.88060796|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.03990954|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.79380049|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.03844555|  PASSED
             dab_dct| 256|     50000|       1|0.60871880|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.54602349|  PASSED
        dab_filltree|  32|  15000000|       1|0.98841853|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.59612880|  PASSED
       dab_filltree2|   1|   5000000|       1|0.21095812|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.09304886|  PASSED
#=============================================================================#
# Summary: PASSED=111, WEAK=3, FAILED=0                                       #
#          235,031.406 MB of random data created with 119.613 MB/sec          #
#=============================================================================#
#=============================================================================#
# Runtime: 0:32:44                                                            #
#=============================================================================#
*/
