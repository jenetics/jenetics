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
import static org.jenetics.random.IntMath.log2Floor;
import static org.jenetics.random.utils.readLong;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class implements a linear congruential PRNG with additional bit-shift
 * transition.
 *
 * <em>
 * This is an re-implementation of the
 * <a href="https://github.com/rabauke/trng4/blob/master/src/lcg64_shift.hpp">
 * trng::lcg64_shift</a> PRNG class of the
 * <a href="http://numbercrunch.de/trng/">TRNG</a> library created by Heiko
 * Bauke.</em>
 *
 * <p>
 * The following listing shows the actual PRNG implementation.
 * <pre>{@code
 * final long a, b = <params>
 * long r = <seed>
 *
 * long nextLong() {
 *     r = q*r + b;
 *
 *     long t = r;
 *     t ^= t >>> 17;
 *     t ^= t << 31;
 *     t ^= t >>> 8;
 *     return t;
 * }
 * }</pre>
 *
 * <p>
 * <strong>Not that the base implementation of the {@code LCG64ShiftRandom}
 * class is not thread-safe.</strong> If multiple threads requests random
 * numbers from this class, it <i>must</i> be synchronized externally.
 * Alternatively you can use the thread-safe implementations
 * {@link LCG64ShiftRandom.ThreadSafe} or {@link LCG64ShiftRandom.ThreadLocal}.
 *
 * @see <a href="http://numbercrunch.de/trng/">TRNG</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.1
 * @version !__version__!
 */
public class LCG64ShiftRandom extends Random64 {

	private static final long serialVersionUID = 1L;

	/* *************************************************************************
	 * Parameter classes.
	 * ************************************************************************/

	/**
	 * Parameter class for the {@code LCG64ShiftRandom} generator, for the
	 * parameters <i>a</i> and <i>b</i> of the LC recursion
	 * <i>r<sub>i+1</sub> = a · r<sub>i</sub> + b</i> mod <i>2<sup>64</sup></i>.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.1
	 * @version 2.0
	 */
	public static final class Param implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * The default PRNG parameters: a = 0xFBD19FBBC5C07FF5L; b = 1
		 */
		public static final Param DEFAULT = Param.of(0xFBD19FBBC5C07FF5L, 1L);

		/**
		 * LEcuyer 1 parameters: a = 0x27BB2EE687B0B0FDL; b = 1
		 */
		public static final Param LECUYER1 = Param.of(0x27BB2EE687B0B0FDL, 1L);

		/**
		 * LEcuyer 2 parameters: a = 0x2C6FE96EE78B6955L; b = 1
		 */
		public static final Param LECUYER2 = Param.of(0x2C6FE96EE78B6955L, 1L);

		/**
		 * LEcuyer 3 parameters: a = 0x369DEA0F31A53F85L; b = 1
		 */
		public static final Param LECUYER3 = Param.of(0x369DEA0F31A53F85L, 1L);


		/**
		 * The parameter <i>a</i> of the LC recursion.
		 */
		public final long a;

		/**
		 * The parameter <i>b</i> of the LC recursion.
		 */
		public final long b;

		/**
		 * Create a new parameter object.
		 *
		 * @param a the parameter <i>a</i> of the LC recursion.
		 * @param b the parameter <i>b</i> of the LC recursion.
		 */
		private Param(final long a, final long b) {
			this.a = a;
			this.b = b;
		}

		public static Param of(final long a, final long b) {
			return new Param(a, b);
		}

		@Override
		public int hashCode() {
			return 31*(int)(a^(a >>> 32)) + 31*(int)(b^(b >>> 32));
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof Param &&
				((Param)obj).a == a &&
				((Param)obj).b == b;
		}

		@Override
		public String toString() {
			return format("%s[a=%d, b=%d]", getClass().getName(), a, b);
		}
	}


	/* *************************************************************************
	 * Thread safe classes.
	 * ************************************************************************/

	/**
	 * This class represents a <i>thread local</i> implementation of the
	 * {@code LCG64ShiftRandom} PRNG.
	 *
	 * It's recommended to initialize the {@code RandomRegistry} the following
	 * way:
	 *
	 * <pre>{@code
	 * // Register the PRNG with the default parameters.
	 * RandomRegistry.setRandom(new LCG64ShiftRandom.ThreadLocal());
	 *
	 * // Register the PRNG with the {@code LECUYER3} parameters.
	 * RandomRegistry.setRandom(new LCG64ShiftRandom.ThreadLocal(
	 *     LCG64ShiftRandom.LECUYER3
	 * ));
	 * }</pre>
	 *
	 * Be aware, that calls of the {@code setSeed(long)} method will throw an
	 * {@code UnsupportedOperationException} for <i>thread local</i> instances.
	 * <pre>{@code
	 * RandomRegistry.setRandom(new LCG64ShiftRandom.ThreadLocal());
	 *
	 * // Will throw 'UnsupportedOperationException'.
	 * RandomRegistry.getRandom().setSeed(1234);
	 * }</pre>
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.1
	 * @version 3.0
	 */
	public static final class ThreadLocal
		extends java.lang.ThreadLocal<LCG64ShiftRandom>
	{
		private static final long STEP_BASE = 1L << 56;

		private int _block = 0;
		private long _seed = PRNG.seed();

		private final Param _param;

		/**
		 * Create a new <i>thread local</i> instance of the
		 * {@code LCG64ShiftRandom} PRNG with the given parameters.
		 *
		 * @param param the LC parameters.
		 * @throws NullPointerException if the given parameters are null.
		 */
		public ThreadLocal(final Param param) {
			_param = requireNonNull(param, "PRNG param must not be null.");
		}

		/**
		 * Create a new <i>thread local</i> instance of the
		 * {@code LCG64ShiftRandom} PRNG with the {@code DEFAULT} parameters.
		 */
		public ThreadLocal() {
			this(Param.DEFAULT);
		}

		/**
		 * Create a new PRNG using <i>block splitting</i> for guaranteeing well
		 * distributed PRN for every thread.
		 *
		 * <p>
		 * <strong>Tina’s Random Number Generator Library</strong>
		 * <br>
		 * <em>Chapter 2. Pseudo-random numbers for parallel Monte Carlo
		 *     simulations, Page 7</em>
		 * <br>
		 * <small>Heiko Bauke</small>
		 * <br>
		 * [<a href="http://numbercrunch.de/trng/trng.pdf">
		 *  http://numbercrunch.de/trng/trng.pdf
		 *  </a>].
		 */
		@Override
		protected synchronized LCG64ShiftRandom initialValue() {
			if (_block > 127) {
				_block = 0;
				_seed = PRNG.seed();
			}

			final LCG64ShiftRandom random = new TLRandom(_param, _seed);
			random.jump(_block++*STEP_BASE);
			return random;
		}

	}

	private static final class TLRandom extends LCG64ShiftRandom {
		private static final long serialVersionUID = 1L;

		private final Boolean _sentry = Boolean.TRUE;

		private TLRandom(final Param param, final long seed) {
			super(param, seed);
		}

		@Override
		public void setSeed(final byte[] seed) {
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
	 * @since 1.1
	 * @version 3.0
	 */
	public static final class ThreadSafe extends LCG64ShiftRandom {
		private static final long serialVersionUID = 1L;

		/**
		 * Create a new PRNG instance with the given parameter and seed.
		 *
		 * @param param the parameter of the PRNG.
		 * @param seed the seed of the PRNG.
		 * @throws NullPointerException if the given {@code param} or {@code seed}
		 *         is {@code null}.
		 * @throws IllegalArgumentException if the given seed is shorter than
		 *         {@link #SEED_BYTES}
		 */
		public ThreadSafe(final Param param, final byte[] seed) {
			super(param, seed);
		}

		/**
		 * Create a new PRNG instance with the given parameter and seed.
		 *
		 * @param seed the seed of the PRNG.
		 * @param param the parameter of the PRNG.
		 * @throws NullPointerException if the given {@code param} is
		 *         {@code null}.
		 */
		public ThreadSafe(final Param param, final long seed) {
			super(param, seed);
		}

		/**
		 * Create a new PRNG instance with the given parameter and a safe seed
		 *
		 * @param param the PRNG parameter.
		 * @throws NullPointerException if the given {@code param} is null.
		 */
		public ThreadSafe(final Param param) {
			super(param);
		}

		/**
		 * Create a new PRNG instance with the given parameter and seed.
		 *
		 * @param seed the seed of the PRNG.
		 * @throws NullPointerException if the given {@code seed} is {@code null}.
		 * @throws IllegalArgumentException if the given seed is shorter than
		 *         {@link #SEED_BYTES}
		 */
		public ThreadSafe(final byte[] seed) {
			super(seed);
		}

		/**
		 * Create a new PRNG instance with {@link Param#DEFAULT} parameter and the
		 * given seed.
		 *
		 * @param seed the seed of the PRNG
		 */
		public ThreadSafe(final long seed) {
			super(seed);
		}

		/**
		 * Create a new PRNG instance with {@link Param#DEFAULT} parameter and
		 * a safe seed.
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

		@Override
		public synchronized void split(final int p, final int s) {
			super.split(p, s);
		}

		@Override
		public synchronized void jump2(final int s) {
			super.jump2(s);
		}

		@Override
		public synchronized void jump(final long step) {
			super.jump(step);
		}

	}

	/**
	 * Represents the state of this random engine
	 */
	private final static class State implements Serializable {
		private static final long serialVersionUID = 1L;

		long _r;

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

			_r = readLong(seed, 0);
		}

		@Override
		public int hashCode() {
			return Long.hashCode(_r);
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof State && ((State)obj)._r == _r;
		}

		@Override
		public String toString() {
			return format("State[%d]", _r);
		}
	}


	/* *************************************************************************
	 * Main class.
	 * ************************************************************************/

	/**
	 * The number of seed bytes (8) this PRNG requires.
	 */
	public static final int SEED_BYTES = 8;

	private Param _param;
	private final State _state;

	/**
	 * Create a new PRNG instance with the given parameter and seed.
	 *
	 * @param param the parameter of the PRNG.
	 * @param seed the seed of the PRNG.
	 * @throws NullPointerException if the given {@code param} or {@code seed}
	 *         is {@code null}.
	 * @throws IllegalArgumentException if the given seed is shorter than
	 *         {@link #SEED_BYTES}
	 */
	public LCG64ShiftRandom(final Param param, final byte[] seed) {
		_param = requireNonNull(param, "PRNG param must not be null.");
		_state = new State(seed);
	}

	/**
	 * Create a new PRNG instance with the given parameter and seed.
	 *
	 * @param param the parameter of the PRNG.
	 * @param seed the seed of the PRNG.
	 * @throws NullPointerException if the given {@code param} is {@code null}.
	 */
	public LCG64ShiftRandom(final Param param, final long seed) {
		this(param, PRNG.seedBytes(seed, SEED_BYTES));
	}

	/**
	 * Create a new PRNG instance with the given parameter and a safe seed
	 *
	 * @param param the PRNG parameter.
	 * @throws NullPointerException if the given {@code param} is null.
	 */
	public LCG64ShiftRandom(final Param param) {
		this(param, seedBytes());
	}

	/**
	 * Create a new PRNG instance with the given parameter and seed.
	 *
	 * @param seed the seed of the PRNG.
	 * @throws NullPointerException if the given {@code seed} is {@code null}.
	 * @throws IllegalArgumentException if the given seed is shorter than
	 *         {@link #SEED_BYTES}
	 */
	public LCG64ShiftRandom(final byte[] seed) {
		this(Param.DEFAULT, seed);
	}

	/**
	 * Create a new PRNG instance with {@link Param#DEFAULT} parameter and the
	 * given seed.
	 *
	 * @param seed the seed of the PRNG
	 */
	public LCG64ShiftRandom(final long seed) {
		this(Param.DEFAULT, PRNG.seedBytes(seed, SEED_BYTES));
	}

	/**
	 * Create a new PRNG instance with {@link Param#DEFAULT} parameter and safe
	 * seed.
	 */
	public LCG64ShiftRandom() {
		this(Param.DEFAULT, PRNG.seed());
	}

	@Override
	public long nextLong() {
		step();

		long t = _state._r;
		t ^= t >>> 17;
		t ^= t << 31;
		t ^= t >>> 8;
		return t;
	}

	private void step() {
		_state._r = _param.a*_state._r + _param.b;
	}

	/**
	 * Set the seed value of the PRNG.
	 *
	 * @param seed the seed value.
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

	/**
	 * Changes the internal state of the PRNG in a way that future calls to
	 * {@link #nextLong()} will generated the s<sup>th</sup> sub-stream of
	 * p<sup>th</sup> sub-streams. <i>s</i> must be within the range of
	 * {@code [0, p-1)}. This method is mainly used for <i>parallelization</i>
	 * via <i>leap-frogging</i>.
	 *
	 * @param p the overall number of sub-streams
	 * @param s the s<sup>th</sup> sub-stream
	 * @throws IllegalArgumentException if {@code p < 1 || s >= p}.
	 */
	public void split(final int p, final int s) {
		if (p < 1) {
			throw new IllegalArgumentException(format(
				"p must be >= 1 but was %d.", p
			));
		}
		if (s >= p) {
			throw new IllegalArgumentException(format(
				"s must be < %d but was %d.", p, s
			));
		}

		if (p > 1) {
			jump(s + 1);
			final long b = _param.b*f(p, _param.a);
			final long a = IntMath.pow(_param.a, p);
			_param = Param.of(a, b);
			backward();
		}
	}

	/**
	 * Changes the internal state of the PRNG in such a way that the engine
	 * <i>jumps</i> 2<sup>s</sup> steps ahead.
	 *
	 * @param s the 2<sup>s</sup> steps to jump ahead.
	 * @throws IllegalArgumentException if {@code s < 0}.
	 */
	public void jump2(final int s) {
		if (s < 0) {
			throw new IllegalArgumentException(format(
				"s must be positive but was %d.", s
			));
		}

		if (s >= Long.SIZE) {
			throw new IllegalArgumentException(format(
				"The 'jump2' size must be smaller than %d but was %d.",
				Long.SIZE, s
			));
		}

		_state._r = _state._r*IntMath.pow(_param.a, 1L << s) +
					f(1L << s, _param.a)*_param.b;
	}

	/**
	 * Changes the internal state of the PRNG in such a way that the engine
	 * <i>jumps</i> s steps ahead.
	 *
	 * @param step the steps to jump ahead.
	 * @throws IllegalArgumentException if {@code s < 0}.
	 */
	public void jump(final long step) {
		if (step < 0) {
			throw new IllegalArgumentException(format(
				"step must be positive but was %d", step
			));
		}

		if (step < 16) {
			for (int i = 0; i < step; ++i) {
				step();
			}
		} else {
			long s = step;
			int i = 0;
			while (s > 0) {
				if (s%2 == 1) {
					jump2(i);
				}
				++i;
				s >>= 1;
			}
		}
	}

	private void backward() {
		for (int i = 0; i < Long.SIZE; ++i) {
			jump2(i);
		}
	}

	public Param getParam() {
		return _param;
	}

	@Override
	public int hashCode() {
		int hash = 31;
		hash += 17*_param.hashCode() + 37;
		hash += 17*_state.hashCode() + 37;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof LCG64ShiftRandom &&
			Objects.equals(((LCG64ShiftRandom)obj)._param, _param) &&
			Objects.equals(((LCG64ShiftRandom)obj)._state, _state);
	}

	@Override
	public String toString() {
		return format("%s[%s, %s]", getClass().getSimpleName(), _param, _state);
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

	/* *************************************************************************
	 * Some static helper methods
	 ***************************************************************************/

	/**
	 * Compute prod(1+a^(2^i), i=0..l-1).
	 */
	private static long g(final int l, final long a) {
		long p = a;
		long res = 1;
		for (int i = 0; i < l; ++i) {
			res *= 1 + p;
			p *= p;
		}

		return res;
	}

	/**
	 * Compute sum(a^i, i=0..s-1).
	 */
	private static long f(final long s, final long a) {
		long y = 0;

		if (s != 0) {
			long e = log2Floor(s);
			long p = a;

			for (int l = 0; l <= e; ++l) {
				if (((1L << l) & s) != 0) {
					y = g(l, a) + p*y;
				}
				p *= p;
			}
		}

		return y;
	}

}

/*
#=============================================================================#
# Testing: org.jenetics.random.LCG64ShiftRandom (2016-08-25 21:46)            #
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
stdin_input_raw|  5.25e+07  |4287852941|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.12435335|  PASSED
      diehard_operm5|   0|   1000000|     100|0.93486001|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.89716971|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.55853650|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.56080705|  PASSED
        diehard_opso|   0|   2097152|     100|0.52863333|  PASSED
        diehard_oqso|   0|   2097152|     100|0.77380939|  PASSED
         diehard_dna|   0|   2097152|     100|0.67905636|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.62490430|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.64044258|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.59033501|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.07802492|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.79621001|  PASSED
     diehard_squeeze|   0|    100000|     100|0.91273684|  PASSED
        diehard_sums|   0|       100|     100|0.00487926|   WEAK
        diehard_runs|   0|    100000|     100|0.49454242|  PASSED
        diehard_runs|   0|    100000|     100|0.95846873|  PASSED
       diehard_craps|   0|    200000|     100|0.17272001|  PASSED
       diehard_craps|   0|    200000|     100|0.99961218|   WEAK
 marsaglia_tsang_gcd|   0|  10000000|     100|0.46887150|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.92069321|  PASSED
         sts_monobit|   1|    100000|     100|0.62576530|  PASSED
            sts_runs|   2|    100000|     100|0.37662643|  PASSED
          sts_serial|   1|    100000|     100|0.96090945|  PASSED
          sts_serial|   2|    100000|     100|0.33757030|  PASSED
          sts_serial|   3|    100000|     100|0.84275539|  PASSED
          sts_serial|   3|    100000|     100|0.52140979|  PASSED
          sts_serial|   4|    100000|     100|0.91561872|  PASSED
          sts_serial|   4|    100000|     100|0.42643392|  PASSED
          sts_serial|   5|    100000|     100|0.86738245|  PASSED
          sts_serial|   5|    100000|     100|0.69229369|  PASSED
          sts_serial|   6|    100000|     100|0.13424137|  PASSED
          sts_serial|   6|    100000|     100|0.23784817|  PASSED
          sts_serial|   7|    100000|     100|0.05177638|  PASSED
          sts_serial|   7|    100000|     100|0.81545537|  PASSED
          sts_serial|   8|    100000|     100|0.94388767|  PASSED
          sts_serial|   8|    100000|     100|0.39185031|  PASSED
          sts_serial|   9|    100000|     100|0.16323199|  PASSED
          sts_serial|   9|    100000|     100|0.03106728|  PASSED
          sts_serial|  10|    100000|     100|0.56248789|  PASSED
          sts_serial|  10|    100000|     100|0.74606938|  PASSED
          sts_serial|  11|    100000|     100|0.38090190|  PASSED
          sts_serial|  11|    100000|     100|0.95141515|  PASSED
          sts_serial|  12|    100000|     100|0.55936157|  PASSED
          sts_serial|  12|    100000|     100|0.24375442|  PASSED
          sts_serial|  13|    100000|     100|0.95016066|  PASSED
          sts_serial|  13|    100000|     100|0.32415085|  PASSED
          sts_serial|  14|    100000|     100|0.59593336|  PASSED
          sts_serial|  14|    100000|     100|0.51966424|  PASSED
          sts_serial|  15|    100000|     100|0.98358194|  PASSED
          sts_serial|  15|    100000|     100|0.75143817|  PASSED
          sts_serial|  16|    100000|     100|0.48628730|  PASSED
          sts_serial|  16|    100000|     100|0.56537687|  PASSED
         rgb_bitdist|   1|    100000|     100|0.75777501|  PASSED
         rgb_bitdist|   2|    100000|     100|0.76019407|  PASSED
         rgb_bitdist|   3|    100000|     100|0.78504384|  PASSED
         rgb_bitdist|   4|    100000|     100|0.37944979|  PASSED
         rgb_bitdist|   5|    100000|     100|0.39135341|  PASSED
         rgb_bitdist|   6|    100000|     100|0.66127921|  PASSED
         rgb_bitdist|   7|    100000|     100|0.82558722|  PASSED
         rgb_bitdist|   8|    100000|     100|0.73395274|  PASSED
         rgb_bitdist|   9|    100000|     100|0.27469836|  PASSED
         rgb_bitdist|  10|    100000|     100|0.84045266|  PASSED
         rgb_bitdist|  11|    100000|     100|0.99144857|  PASSED
         rgb_bitdist|  12|    100000|     100|0.63018057|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.43595230|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.74899391|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.76268729|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.22356681|  PASSED
    rgb_permutations|   2|    100000|     100|0.37671828|  PASSED
    rgb_permutations|   3|    100000|     100|0.65918401|  PASSED
    rgb_permutations|   4|    100000|     100|0.66039531|  PASSED
    rgb_permutations|   5|    100000|     100|0.62917289|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.34080452|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.82260131|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.52245806|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.36171700|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.81930298|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.05874992|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.99718345|   WEAK
      rgb_lagged_sum|   7|   1000000|     100|0.60494227|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.97568712|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.10086115|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.55859809|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.43517144|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.53474776|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.06953711|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.48210788|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.80255457|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.29278779|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.01727731|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.92931341|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.82310647|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.68768148|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.17217339|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.96139343|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.93288136|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.92314017|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.82700708|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.98083872|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.29706498|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.09819577|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.91385928|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.22476523|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.74575467|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.69035600|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.34731272|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.04383427|  PASSED
             dab_dct| 256|     50000|       1|0.72682566|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.72861926|  PASSED
        dab_filltree|  32|  15000000|       1|0.61023214|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.96391477|  PASSED
       dab_filltree2|   1|   5000000|       1|0.19175966|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.97759974|  PASSED
#=============================================================================#
# Summary: PASSED=111, WEAK=3, FAILED=0                                       #
#          235,031.328 MB of random data created with 131.705 MB/sec          #
#=============================================================================#
#=============================================================================#
# Runtime: 0:29:44                                                            #
#=============================================================================#
*/
