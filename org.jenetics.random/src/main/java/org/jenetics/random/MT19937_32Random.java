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
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * This is a 32-bit version of Mersenne Twister pseudorandom number generator.
 * <p>
 * <i>
 * References: M. Matsumoto and T. Nishimura, "Mersenne Twister: a
 * 623-dimensionally equidistributed uniform pseudorandom number generator".
 * ACM Transactions on Modeling and Computer Simulation 8. (Jan. 1998) 3--30.
 * </i>
 * <p>
 * <em>
 * This is an re-implementation of the
 * <a href="https://github.com/rabauke/trng4/blob/master/src/mt19937.hpp">
 * trng::mt19937</a> PRNG class of the
 * <a href="http://numbercrunch.de/trng/">TRNG</a> library created by Heiko
 * Bauke.</em>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public class MT19937_32Random extends Random32 {

	private static final long serialVersionUID = 1L;

	private static final int N = 624;
	private static final int M = 397;

	private static final int UM = 0x80000000; // most significant bit
	private static final int LM = 0x7FFFFFFF; // least significant 31 bits

	private static final int[] MAG01 = {0, 0x9908b0df};

	/**
	 * This class represents a <i>thread local</i> implementation of the
	 * {@code MT19937_32Random} PRNG.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since !__version__!
	 * @version !__version__!
	 */
	public static class ThreadLocal
		extends java.lang.ThreadLocal<MT19937_32Random>
	{

		/**
		 * Create a new PRNG using different seed values for every thread.
		 */
		@Override
		protected MT19937_32Random initialValue() {
			return new TLRandom(PRNG.seed());
		}
	}

	private static final class TLRandom extends MT19937_32Random {
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
	public static final class ThreadSafe extends MT19937_32Random {
		private static final long serialVersionUID = 1L;

		/**
		 * Create a new thread-safe instance of the {@code MT19937_32Random}
		 * engine.
		 *
		 * <pre>{@code
		 * final byte[] seed = MT19937_32Random.seedBytes();
		 * final Random random = new MT19937_32Random.ThreadSafe(seed);
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
		 * Create a new thread-safe instance of the {@code MT19937_32Random}
		 * engine. The constructed PRNG is equivalent with
		 * <pre>{@code
		 * final long seed = ...;
		 * final Random random = new MT19937_32Random.ThreadSafe();
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
		 * Create a new thread-safe instance of the {@code MT19937_32Random}
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
	 * The internal state of this PRNG.
	 */
	private static final class State implements Serializable {
		private static final long serialVersionUID = 1L;

		int mti = 0;
		int[] mt = new int[N];

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
				mt[mti] = readInt(seed, mti);
			}
		}

		void setSeed(final long seed) {
			mt[0] = (int)seed;
			for (mti = 1; mti < N; ++mti) {
				mt[mti] = 1812433253*(mt[mti - 1]^(mt[mti - 1] >>> 30)) + mti;
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
	public static final int SEED_BYTES = N*Integer.BYTES;

	private final State _state;

	/**
	 * Create a new <em>not</em> thread-safe instance of the
	 * {@code MT19937_32Random} engine.
	 *
	 * <pre>{@code
	 * final byte[] seed = MT19937_32Random.seedBytes();
	 * final Random random = new MT19937_32Random(seed);
	 * }</pre>
	 *
	 * @see #seedBytes()
	 *
	 * @param seed the random seed value. The seed must be (at least)
	 *        {@link #SEED_BYTES} long.
	 * @throws IllegalArgumentException if the given seed is shorter than
	 *         {@link #SEED_BYTES}
	 */
	public MT19937_32Random(final byte[] seed) {
		_state = new State(seed);
	}

	/**
	 * Create a new <em>not</em> thread-safe instance of the
	 * {@code MT19937_32Random} engine. The constructed PRNG is equivalent with
	 * <pre>{@code
	 * final long seed = ...;
	 * final Random random = new MT19937_32Random();
	 * random.setSeed(seed);
	 * }</pre>
	 * which is there for compatibility reasons with the Java {@link Random}
	 * engine.
	 *
	 * @param seed the random seed value
	 */
	public MT19937_32Random(final long seed) {
		_state = new State(seed);
	}

	/**
	 * Return a new random engine with a safe seed value.
	 */
	public MT19937_32Random() {
		this(seedBytes());
	}

	@Override
	public int nextInt() {
		int x;

		// Generate N words at one time.
		if (_state.mti >= N) {
			int i = 0;
			for (i = 0; i < N - M; ++i) {
				x = (_state.mt[i] & UM) | (_state.mt[i + 1] & LM);
				_state.mt[i] = _state.mt[i + M]^(x >>> 1)^MAG01[x & 1];
			}
			for (; i < N - 1; ++i) {
				x = (_state.mt[i] & UM) | (_state.mt[i + 1] & LM);
				_state.mt[i] = _state.mt[i + (M - N)]^(x >>> 1)^MAG01[x & 1];
			}

			x = (_state.mt[N - 1] & UM)|(_state.mt[0] & LM);
			_state.mt[N - 1] = _state.mt[M - 1]^(x >>> 1)^MAG01[x & 1];
			_state.mti = 0;
		}

		x = _state.mt[_state.mti++];
		x ^= x >>> 11;
		x ^= (x << 7) & 0x9d2c5680;
		x ^= (x << 15) & 0xefc60000;
		x ^= x >>> 18;

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
		return obj instanceof MT19937_32Random &&
			Objects.equals(((MT19937_32Random)obj)._state, _state);
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
# Testing: org.jenetics.random.MT19937_32Random (2016-09-29 23:49)            #
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
stdin_input_raw|  3.96e+07  |2191086244|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.26435802|  PASSED
      diehard_operm5|   0|   1000000|     100|0.32008868|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.69416552|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.40635422|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.67998125|  PASSED
        diehard_opso|   0|   2097152|     100|0.36163156|  PASSED
        diehard_oqso|   0|   2097152|     100|0.87085244|  PASSED
         diehard_dna|   0|   2097152|     100|0.25686527|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.02800332|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.18241934|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.13090839|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.40103834|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.97924474|  PASSED
     diehard_squeeze|   0|    100000|     100|0.10328475|  PASSED
        diehard_sums|   0|       100|     100|0.00625250|  PASSED
        diehard_runs|   0|    100000|     100|0.71891042|  PASSED
        diehard_runs|   0|    100000|     100|0.84330939|  PASSED
       diehard_craps|   0|    200000|     100|0.60622951|  PASSED
       diehard_craps|   0|    200000|     100|0.74655695|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.09474356|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.52675510|  PASSED
         sts_monobit|   1|    100000|     100|0.98407610|  PASSED
            sts_runs|   2|    100000|     100|0.58560337|  PASSED
          sts_serial|   1|    100000|     100|0.69055272|  PASSED
          sts_serial|   2|    100000|     100|0.94557054|  PASSED
          sts_serial|   3|    100000|     100|0.75789479|  PASSED
          sts_serial|   3|    100000|     100|0.11554942|  PASSED
          sts_serial|   4|    100000|     100|0.81763684|  PASSED
          sts_serial|   4|    100000|     100|0.46717919|  PASSED
          sts_serial|   5|    100000|     100|0.29742270|  PASSED
          sts_serial|   5|    100000|     100|0.17098827|  PASSED
          sts_serial|   6|    100000|     100|0.14433903|  PASSED
          sts_serial|   6|    100000|     100|0.55373349|  PASSED
          sts_serial|   7|    100000|     100|0.92957710|  PASSED
          sts_serial|   7|    100000|     100|0.86083694|  PASSED
          sts_serial|   8|    100000|     100|0.15814740|  PASSED
          sts_serial|   8|    100000|     100|0.18546183|  PASSED
          sts_serial|   9|    100000|     100|0.40044749|  PASSED
          sts_serial|   9|    100000|     100|0.15915304|  PASSED
          sts_serial|  10|    100000|     100|0.40387554|  PASSED
          sts_serial|  10|    100000|     100|0.97155780|  PASSED
          sts_serial|  11|    100000|     100|0.88044597|  PASSED
          sts_serial|  11|    100000|     100|0.58096261|  PASSED
          sts_serial|  12|    100000|     100|0.63536187|  PASSED
          sts_serial|  12|    100000|     100|0.94653927|  PASSED
          sts_serial|  13|    100000|     100|0.42166376|  PASSED
          sts_serial|  13|    100000|     100|0.44877853|  PASSED
          sts_serial|  14|    100000|     100|0.38845548|  PASSED
          sts_serial|  14|    100000|     100|0.17782861|  PASSED
          sts_serial|  15|    100000|     100|0.77170226|  PASSED
          sts_serial|  15|    100000|     100|0.59464532|  PASSED
          sts_serial|  16|    100000|     100|0.49827108|  PASSED
          sts_serial|  16|    100000|     100|0.66349392|  PASSED
         rgb_bitdist|   1|    100000|     100|0.64055861|  PASSED
         rgb_bitdist|   2|    100000|     100|0.18150327|  PASSED
         rgb_bitdist|   3|    100000|     100|0.99813985|   WEAK
         rgb_bitdist|   4|    100000|     100|0.63015331|  PASSED
         rgb_bitdist|   5|    100000|     100|0.89079186|  PASSED
         rgb_bitdist|   6|    100000|     100|0.99655883|   WEAK
         rgb_bitdist|   7|    100000|     100|0.94172726|  PASSED
         rgb_bitdist|   8|    100000|     100|0.41339306|  PASSED
         rgb_bitdist|   9|    100000|     100|0.90723810|  PASSED
         rgb_bitdist|  10|    100000|     100|0.71468992|  PASSED
         rgb_bitdist|  11|    100000|     100|0.74376206|  PASSED
         rgb_bitdist|  12|    100000|     100|0.53822546|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.22493886|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.58224244|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.93249887|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.46758368|  PASSED
    rgb_permutations|   2|    100000|     100|0.56198973|  PASSED
    rgb_permutations|   3|    100000|     100|0.98157075|  PASSED
    rgb_permutations|   4|    100000|     100|0.31947138|  PASSED
    rgb_permutations|   5|    100000|     100|0.22144049|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.40708165|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.41779792|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.72665504|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.30522742|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.82186002|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.99164667|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.08105753|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.28291078|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.85163285|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.83722706|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.11239995|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.42351549|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.94917943|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.54828720|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.57077856|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.93805135|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.47212371|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.96600597|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.30571759|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.96585212|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.47486248|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.50365969|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.07541760|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.29927040|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.01195833|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.96894472|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.39527960|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.39026460|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.87820686|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.10712420|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.21772572|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.95062291|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.90094704|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.70559185|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.91073804|  PASSED
             dab_dct| 256|     50000|       1|0.90784023|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.31525970|  PASSED
        dab_filltree|  32|  15000000|       1|0.94991931|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.29393594|  PASSED
       dab_filltree2|   1|   5000000|       1|0.58033258|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.71014531|  PASSED
#=============================================================================#
# Summary: PASSED=112, WEAK=2, FAILED=0                                       #
#          235,031.414 MB of random data created with 119.568 MB/sec          #
#=============================================================================#
#=============================================================================#
# Runtime: 0:32:45                                                            #
#=============================================================================#
*/

