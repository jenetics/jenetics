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

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Implementation of the XOR shift PRNG.
 *
 * @see <a href="http://www.jstatsoft.org/v08/i14/paper">
 *      Xorshift RNGs, George Marsaglia</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public class XOR32ShiftRandom extends Random32 {
	private static final long serialVersionUID = 1L;

	/**
	 * Parameter class for the {@code XOR32ShiftRandom} generator. The three
	 * integer parameters are used in the PRNG as follows:
	 *
	 * [code]
	 * int y = seed();
	 * public int nextInt() {
	 *     y ^= y &lt;&lt; a;
	 *     y ^= y &gt;&gt; b;
	 *     return y ^= y &lt;&lt; c;
	 * }
	 * [/code]
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version !__version__!
	 * @since !__version__!
	 */
	public static final class Param implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * Contains a list of the parameters with the highest <i>dieharder</i>
		 * scores.
		 */
		public static final Param[] PARAMS = {
			new Param(12, 21, 5),  // p=104, w=5, f=5
			new Param(5, 21, 12),  // p=104, w=4, f=6
			new Param(5, 19, 13),  // p=103, w=4, f=7
			new Param(10, 21, 9),  // p=103, w=2, f=9
			new Param(5, 17, 13),  // p=102, w=5, f=7
			new Param(3, 13, 7),   // p=102, w=3, f=9
			new Param(9, 17, 6),   // p=102, w=3, f=9
			new Param(17, 11, 13), // p=102, w=3, f=9
			new Param(25, 9, 5),   // p=102, w=3, f=9
			new Param(7, 13, 3),   // p=101, w=7, f=6
			new Param(25, 13, 7),  // p=101, w=4, f=9
			new Param(25, 9, 10),  // p=101, w=4, f=9
			new Param(6, 17, 9),   // p=100, w=6, f=8
			new Param(13, 17, 5),  // p=100, w=5, f=9
			new Param(7, 21, 6),   // p=100, w=5, f=9
			new Param(9, 21, 2),   // p=100, w=4, f=10
			new Param(8, 23, 7),   // p=100, w=4, f=10
			new Param(19, 9, 11),  // p=100, w=4, f=10
			new Param(6, 21, 7),   // p=100, w=2, f=12
			new Param(20, 5, 3),   // p=99, w=6, f=9
			new Param(13, 17, 11), // p=99, w=5, f=10
			new Param(16, 7, 11),  // p=99, w=5, f=10
			new Param(4, 5, 15),   // p=99, w=4, f=11
			new Param(17, 13, 3),  // p=99, w=4, f=11
			new Param(5, 6, 13),   // p=99, w=2, f=13
			new Param(2, 15, 9),   // p=98, w=7, f=9
			new Param(12, 25, 7),  // p=98, w=7, f=9
			new Param(5, 7, 9),    // p=98, w=6, f=10
			new Param(9, 5, 7),    // p=98, w=6, f=10
			new Param(7, 25, 13),  // p=98, w=5, f=11
			new Param(6, 5, 13),   // p=98, w=5, f=11
			new Param(22, 5, 3),   // p=98, w=5, f=11
			new Param(2, 9, 7),    // p=98, w=5, f=11
			new Param(9, 12, 23),  // p=98, w=4, f=12
			new Param(11, 17, 13), // p=97, w=9, f=8
			new Param(7, 23, 8),   // p=97, w=6, f=11
			new Param(23, 12, 9),  // p=97, w=6, f=11
			new Param(13, 6, 5),   // p=97, w=6, f=11
			new Param(6, 13, 21),  // p=97, w=5, f=12
			new Param(7, 25, 12),  // p=97, w=5, f=12
			new Param(21, 13, 6),  // p=97, w=4, f=13
			new Param(13, 11, 17), // p=97, w=4, f=13
			new Param(7, 9, 2),    // p=97, w=4, f=13
			new Param(13, 5, 6),   // p=97, w=4, f=13
			new Param(21, 7, 6),   // p=97, w=3, f=14
			new Param(5, 25, 3),   // p=97, w=2, f=15
			new Param(11, 19, 9),  // p=97, w=10, f=7
			new Param(2, 15, 5),   // p=97, w=1, f=16
			new Param(9, 21, 10),  // p=96, w=8, f=10
			new Param(7, 5, 9),    // p=96, w=8, f=10
			new Param(11, 6, 1),   // p=96, w=8, f=10
			new Param(2, 21, 9),   // p=96, w=6, f=12
			new Param(1, 6, 11),   // p=96, w=6, f=12
			new Param(11, 9, 19),  // p=96, w=5, f=13
			new Param(1, 16, 11),  // p=96, w=4, f=14
			new Param(15, 5, 4),   // p=96, w=4, f=14
			new Param(23, 9, 12),  // p=96, w=4, f=14
			new Param(10, 9, 25),  // p=96, w=4, f=14
			new Param(5, 9, 25),   // p=96, w=3, f=15
			new Param(2, 7, 9),    // p=95, w=9, f=10
			new Param(13, 19, 5),  // p=95, w=8, f=11
			new Param(11, 7, 16),  // p=95, w=7, f=12
			new Param(11, 21, 13), // p=95, w=7, f=12
			new Param(25, 5, 9),   // p=95, w=6, f=13
			new Param(21, 5, 3),   // p=95, w=6, f=13
			new Param(9, 15, 2),   // p=95, w=5, f=14
			new Param(16, 11, 7),  // p=95, w=5, f=14
			new Param(9, 7, 2),    // p=95, w=4, f=15
			new Param(1, 9, 5),    // p=95, w=3, f=16
			new Param(6, 17, 3),   // p=95, w=1, f=18
			new Param(15, 17, 20), // p=95, w=0, f=19
			new Param(15, 9, 2),   // p=94, w=8, f=12
			new Param(25, 7, 13),  // p=94, w=8, f=12
			new Param(8, 27, 5),   // p=94, w=7, f=13
			new Param(12, 9, 23),  // p=94, w=6, f=14
			new Param(28, 9, 5),   // p=94, w=6, f=14
			new Param(7, 11, 16),  // p=94, w=4, f=16
			new Param(9, 23, 8),   // p=94, w=4, f=16
			new Param(23, 15, 17), // p=94, w=4, f=16
			new Param(27, 8, 5),   // p=94, w=4, f=16
			new Param(19, 5, 13),  // p=94, w=4, f=16
			new Param(9, 10, 25),  // p=94, w=4, f=16
			new Param(17, 4, 3),   // p=94, w=2, f=18
			new Param(13, 25, 7),  // p=93, w=9, f=12
			new Param(3, 13, 17),  // p=93, w=7, f=14
			new Param(8, 23, 9),   // p=93, w=7, f=14
			new Param(5, 27, 8),   // p=93, w=7, f=14
			new Param(9, 21, 16),  // p=93, w=3, f=18
			new Param(16, 21, 9),  // p=93, w=3, f=18
			new Param(20, 15, 17), // p=93, w=2, f=19
			new Param(13, 5, 19),  // p=93, w=1, f=20
			new Param(7, 13, 25),  // p=92, w=8, f=14
			new Param(3, 5, 20),   // p=92, w=7, f=15
			new Param(19, 5, 1),   // p=92, w=7, f=15
			new Param(3, 17, 4),   // p=92, w=4, f=18
			new Param(5, 2, 15),   // p=92, w=4, f=18
			new Param(3, 17, 6),   // p=92, w=3, f=19
			new Param(17, 3, 6),   // p=92, w=3, f=19
			new Param(25, 5, 3),   // p=92, w=11, f=11
			new Param(5, 9, 1),    // p=91, w=6, f=17
			new Param(3, 25, 5),   // p=91, w=6, f=17
			new Param(27, 3, 13),  // p=91, w=4, f=19
			new Param(13, 7, 25),  // p=91, w=2, f=21
			new Param(27, 5, 8),   // p=91, w=11, f=12
			new Param(17, 6, 3),   // p=90, w=8, f=16
			new Param(17, 6, 9),   // p=90, w=8, f=16
			new Param(25, 7, 12),  // p=90, w=8, f=16
			new Param(25, 10, 9),  // p=90, w=8, f=16
			new Param(15, 2, 5),   // p=90, w=7, f=17
			new Param(7, 25, 20),  // p=90, w=5, f=19
			new Param(21, 2, 5),   // p=90, w=4, f=20
			new Param(23, 8, 7),   // p=90, w=10, f=14
			new Param(5, 27, 25),  // p=90, w=1, f=23
		};

		/**
		 * The default parameter used by the PRNG. It's the parameter with the
		 * best <i>dieharder</i> test result.
		 */
		public static final Param DEFAULT = PARAMS[0];

		public final int a;
		public final int b;
		public final int c;

		/**
		 * Parameter object for the parameters used by this PRNG.
		 *
		 * @param a first shift parameter
		 * @param b second shift parameter
		 * @param c third shift parameter
		 */
		public Param(final int a, final int b, final int c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(new int[]{a, b, c});
		}

		@Override
		public boolean equals(final Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof Param)) {
				return false;
			}

			final Param param = (Param)other;
			return a == param.a && b == param.b && c == param.c;
		}

		@Override
		public String toString() {
			return String.format("Param[%d, %d, %d]", a, b, c);
		}
	}


	/**
	 * This class represents a <i>thread local</i> implementation of the
	 * {@code XOR32ShiftRandom} PRNG.
	 *
	 * It's recommended to initialize the {@code RandomRegistry} the following
	 * way:
	 *
	 * [code]
	 * // Register the PRNG with the default parameters.
	 * RandomRegistry.setRandom(new XOR32ShiftRandom.ThreadLocal());
	 * [/code]
	 *
	 * Be aware, that calls of the {@code setSeed(long)} method will throw an
	 * {@code UnsupportedOperationException} for <i>thread local</i> instances.
	 * [code]
	 * RandomRegistry.setRandom(new XOR32ShiftRandom.ThreadLocal());
	 *
	 * // Will throw 'UnsupportedOperationException'.
	 * RandomRegistry.getRandom().setSeed(1234);
	 * [/code]
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.1
	 * @version 1.1
	 */
	public static class ThreadLocal
		extends java.lang.ThreadLocal<XOR32ShiftRandom>
	{
		private int _paramIndex = 0;

		/**
		 * Create a new <i>thread local</i> instance.
		 */
		public ThreadLocal() {
		}

		/**
		 * Create a new PRNG using different parametrization.
		 */
		@Override
		protected synchronized XOR32ShiftRandom initialValue() {
			return new XOR32ShiftRandom(math.seed(), nextParam());
		}

		private Param nextParam() {
			return Param.PARAMS[(_paramIndex++)%Param.PARAMS.length];
		}

	}

	/**
	 * This is a <i>thread safe</i> variation of the this PRGN&mdash;by
	 * synchronizing the random number generation.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.1
	 * @version 1.1
	 */
	public static class ThreadSafe extends XOR32ShiftRandom {
		private static final long serialVersionUID = 1L;

		/**
		 * Create a new PRNG instance with the given parameter and seed.
		 *
		 * @param seed the seed of the PRNG.
		 * @param param the parameter of the PRNG.
		 * @throws NullPointerException if the given {@code param} is null.
		 */
		public ThreadSafe(final long seed, final Param param) {
			super(seed, param);
		}

		/**
		 * Create a new PRNG instance with {@link Param#DEFAULT} parameter and
		 * the given seed.
		 *
		 * @param seed the seed of the PRNG
		 */
		public ThreadSafe(final long seed) {
			this(seed, Param.DEFAULT);
		}

		/**
		 * Create a new PRNG instance with the given parameter and a safe
		 * default seed.
		 *
		 * @param param the PRNG parameter.
		 * @throws NullPointerException if the given {@code param} is null.
		 */
		public ThreadSafe(final Param param) {
			this(math.seed(), param);
		}

		/**
		 * Create a new PRNG instance with {@link Param#DEFAULT} parameter and
		 * a safe seed.
		 */
		public ThreadSafe() {
			this(math.seed(), Param.DEFAULT);
		}

		@Override
		public synchronized void setSeed(final long seed) {
			super.setSeed(seed);
		}

		@Override
		public synchronized void reset() {
			super.reset();
		}

		@Override
		public synchronized int nextInt() {
			return super.nextInt();
		}

	}


	private final Param _param;

	private int _seed;
	private int _x = 0;

	public XOR32ShiftRandom(final long seed, final Param param) {
		_param = requireNonNull(param, "PRNG param must not be null.");
		_seed = toSafeSeed((int)seed);

		_x = _seed;
	}

	public XOR32ShiftRandom(final long seed) {
		this(seed, Param.DEFAULT);
	}

	public XOR32ShiftRandom(final Param param) {
		this(math.seed(), Param.DEFAULT);
	}

	public XOR32ShiftRandom() {
		this(math.seed(), Param.DEFAULT);
	}

	private static int toSafeSeed(final int seed) {
		return seed == 0 ? 1179196819 : seed;
	}

	/**
	 * Resets the PRNG back to the creation state.
	 */
	public void reset() {
		_x = _seed;
	}

	@Override
	public void setSeed(final long seed) {
		_seed = toSafeSeed((int)seed);
		reset();
	}

	@Override
	public int nextInt() {
		_x ^= _x << _param.a; _x ^= _x >> _param.b; return _x ^= _x << _param.c;

		// Additional shift variants.
//		_x ^= _x << _param.c; _x ^= _x >> _param.b; return _x ^= _x << _param.a;
//		_x ^= _x >> _param.a; _x ^= _x << _param.b; return _x ^= _x >> _param.c;
//		_x ^= _x >> _param.c; _x ^= _x << _param.b; return _x ^= _x >> _param.a;
//		_x ^= _x << _param.a; _x ^= _x << _param.c; return _x ^= _x >> _param.b;
//		_x ^= _x << _param.c; _x ^= _x << _param.a; return _x ^= _x >> _param.b;
//		_x ^= _x >> _param.a; _x ^= _x >> _param.c; return _x ^= _x << _param.b;
//		_x ^= _x >> _param.c; _x ^= _x >> _param.a; return _x ^= _x << _param.b;
	}

	@Override
	public String toString() {
		return String.format("XOR32ShiftRandom[%s, seed=%d, x=%d]", _param, _seed, _x);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new int[]{_x, _seed, _param.hashCode()});
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		final XOR32ShiftRandom random = (XOR32ShiftRandom)obj;
		return _x == random._x &&
			_seed == random._seed &&
			_param.equals(random._param);
	}

}

/*
#=============================================================================#
# Testing: org.jenetics.util.XOR32ShiftRandom (2014-01-20 20:27)              #
#=============================================================================#
#=============================================================================#
# Linux 3.11.0-15-generic (amd64)                                             #
# java version "1.7.0_51"                                                     #
# Java(TM) SE Runtime Environment (build 1.7.0_51-b13)                        #
# Java HotSpot(TM) 64-Bit Server VM (build 24.51-b03)                         #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  3.37e+07  | 164227574|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.55094332|  PASSED
      diehard_operm5|   0|   1000000|     100|0.71654695|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.00000000|  FAILED
    diehard_rank_6x8|   0|    100000|     100|0.59908299|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.43297133|  PASSED
        diehard_opso|   0|   2097152|     100|0.00007659|   WEAK
        diehard_oqso|   0|   2097152|     100|0.42770496|  PASSED
         diehard_dna|   0|   2097152|     100|0.20400884|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.00000000|  FAILED
diehard_count_1s_byt|   0|    256000|     100|0.84647704|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.43595334|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.39130458|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.19178405|  PASSED
     diehard_squeeze|   0|    100000|     100|0.70999323|  PASSED
        diehard_sums|   0|       100|     100|0.17520235|  PASSED
        diehard_runs|   0|    100000|     100|0.91631589|  PASSED
        diehard_runs|   0|    100000|     100|0.42333525|  PASSED
       diehard_craps|   0|    200000|     100|0.91774531|  PASSED
       diehard_craps|   0|    200000|     100|0.92063161|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.57979526|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.00000000|  FAILED
         sts_monobit|   1|    100000|     100|0.81856728|  PASSED
            sts_runs|   2|    100000|     100|0.99113034|  PASSED
          sts_serial|   1|    100000|     100|0.77601761|  PASSED
          sts_serial|   2|    100000|     100|0.46391374|  PASSED
          sts_serial|   3|    100000|     100|0.89742918|  PASSED
          sts_serial|   3|    100000|     100|0.76446920|  PASSED
          sts_serial|   4|    100000|     100|0.19540110|  PASSED
          sts_serial|   4|    100000|     100|0.06252507|  PASSED
          sts_serial|   5|    100000|     100|0.06944274|  PASSED
          sts_serial|   5|    100000|     100|0.14528339|  PASSED
          sts_serial|   6|    100000|     100|0.65763367|  PASSED
          sts_serial|   6|    100000|     100|0.93595250|  PASSED
          sts_serial|   7|    100000|     100|0.60611796|  PASSED
          sts_serial|   7|    100000|     100|0.77057314|  PASSED
          sts_serial|   8|    100000|     100|0.99739536|   WEAK
          sts_serial|   8|    100000|     100|0.59186910|  PASSED
          sts_serial|   9|    100000|     100|0.46964084|  PASSED
          sts_serial|   9|    100000|     100|0.08182208|  PASSED
          sts_serial|  10|    100000|     100|0.69808000|  PASSED
          sts_serial|  10|    100000|     100|0.57732253|  PASSED
          sts_serial|  11|    100000|     100|0.24905129|  PASSED
          sts_serial|  11|    100000|     100|0.25778277|  PASSED
          sts_serial|  12|    100000|     100|0.71052509|  PASSED
          sts_serial|  12|    100000|     100|0.55475453|  PASSED
          sts_serial|  13|    100000|     100|0.53333047|  PASSED
          sts_serial|  13|    100000|     100|0.68524266|  PASSED
          sts_serial|  14|    100000|     100|0.25326745|  PASSED
          sts_serial|  14|    100000|     100|0.79562926|  PASSED
          sts_serial|  15|    100000|     100|0.98899691|  PASSED
          sts_serial|  15|    100000|     100|0.57567682|  PASSED
          sts_serial|  16|    100000|     100|0.81292030|  PASSED
          sts_serial|  16|    100000|     100|0.25341010|  PASSED
         rgb_bitdist|   1|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   2|    100000|     100|0.07580896|  PASSED
         rgb_bitdist|   3|    100000|     100|0.31088062|  PASSED
         rgb_bitdist|   4|    100000|     100|0.90534729|  PASSED
         rgb_bitdist|   5|    100000|     100|0.44904548|  PASSED
         rgb_bitdist|   6|    100000|     100|0.22242015|  PASSED
         rgb_bitdist|   7|    100000|     100|0.02813920|  PASSED
         rgb_bitdist|   8|    100000|     100|0.80147144|  PASSED
         rgb_bitdist|   9|    100000|     100|0.26869158|  PASSED
         rgb_bitdist|  10|    100000|     100|0.29888677|  PASSED
         rgb_bitdist|  11|    100000|     100|0.06042877|  PASSED
         rgb_bitdist|  12|    100000|     100|0.30286370|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.00000800|   WEAK
rgb_minimum_distance|   3|     10000|    1000|0.00001483|   WEAK
rgb_minimum_distance|   4|     10000|    1000|0.40854032|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.00434230|   WEAK
    rgb_permutations|   2|    100000|     100|0.46450052|  PASSED
    rgb_permutations|   3|    100000|     100|0.94752415|  PASSED
    rgb_permutations|   4|    100000|     100|0.76609383|  PASSED
    rgb_permutations|   5|    100000|     100|0.94126049|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.09648995|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.04025477|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.92252010|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.04278490|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.99753210|   WEAK
      rgb_lagged_sum|   5|   1000000|     100|0.35423233|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.90819707|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.73585291|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.33519608|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.91698879|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.45228000|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.29878735|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.72197718|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.28566856|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.50039330|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.38175075|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.99992799|   WEAK
      rgb_lagged_sum|  17|   1000000|     100|0.87418378|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.16713326|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.58310813|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.91948677|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.99352877|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.44459069|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.74766913|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.97459776|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.55139779|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.97779081|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.92543972|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.42307289|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.48338333|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.41185845|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.65962099|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.57819932|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.03748478|  PASSED
     dab_bytedistrib|   0|  51200000|       1|1.00000000|  FAILED
             dab_dct| 256|     50000|       1|0.21874118|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.24403027|  PASSED
        dab_filltree|  32|  15000000|       1|0.27101765|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.71799385|  PASSED
       dab_filltree2|   1|   5000000|       1|0.23754901|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|1.00000000|  FAILED
#=============================================================================#
# Summary: PASSED: 101, WEAK: 7, FAILED: 6                                    #
#=============================================================================#
#=============================================================================#
# Runtime: 0:40:46                                                            #
#=============================================================================#
*/
