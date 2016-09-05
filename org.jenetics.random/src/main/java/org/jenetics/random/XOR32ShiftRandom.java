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
import static org.jenetics.random.utils.listOf;
import static org.jenetics.random.utils.readInt;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * This generator was discovered and characterized by George Marsaglia
 * [<a href="http://www.jstatsoft.org/v08/i14/paper">Xorshift RNGs</a>]. In just
 * three XORs and three shifts (generally fast operations) it produces a full
 * period of 2<sup>32</sup> - 1 on 32 bits. (The missing value is zero, which
 * perpetuates itself and must be avoided.) High and low bits pass Diehard.
 * <p>
 * Implementation of the XOR shift PRNG. The following listing shows the actual
 * PRNG implementation.
 * <pre>{@code
 * private final int a, b, c = <param>
 * private int x = <seed>
 *
 * int nextInt() {
 *     x ^= x << a;
 *     x ^= x >>> b;
 *     return x ^= x << c;
 * }
 * }</pre>
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

	/* *************************************************************************
	 * Parameter classes.
	 * ************************************************************************/

	/**
	 * Enumeration of the different <em>shift</em> strategies.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since !__version__!
	 * @version !__version__!
	 */
	public static enum Shift {

		/**
		 * Shift strategy one.
		 * <pre>{@code
		 * int shift(int x, final Param param) {
		 *     x ^= x << param.a;
		 *     x ^= x >>> param.b;
		 *     return x^x << param.c;
		 * }
		 * }</pre>
		 */
		SHIFT_1 {
			@Override
			public int shift(int x, final Param param) {
				x ^= x << param.a;
				x ^= x >>> param.b;
				return x^x << param.c;
			}
		},

		/**
		 * Shift strategy two.
		 * <pre>{@code
		 * int shift(int x, final Param param) {
		 *     x ^= x << param.c;
		 *     x ^= x >>> param.b;
		 *     return x^x << param.a;
		 * }
		 * }</pre>
		 */
		SHIFT_2 {
			@Override
			public int shift(int x, final Param param) {
				x ^= x << param.c;
				x ^= x >>> param.b;
				return x^x << param.a;
			}
		},

		/**
		 * Shift strategy three.
		 * <pre>{@code
		 * int shift(int x, final Param param) {
		 *     x ^= x >>> param.a;
		 *     x ^= x << param.b;
		 *     return x^x >>> param.c;
		 * }
		 * }</pre>
		 */
		SHIFT_3 {
			@Override
			public int shift(int x, final Param param) {
				x ^= x >>> param.a;
				x ^= x << param.b;
				return x^x >>> param.c;
			}
		},

		/**
		 * Shift strategy four.
		 * <pre>{@code
		 * int shift(int x, final Param param) {
		 *     x ^= x >>> param.c;
		 *     x ^= x << param.b;
		 *     return x^x >>> param.a;
		 * }
		 * }</pre>
		 */
		SHIFT_4 {
			@Override
			public int shift(int x, final Param param) {
				x ^= x >>> param.c;
				x ^= x << param.b;
				return x^x >>> param.a;
			}
		},

		/**
		 * Shift strategy five.
		 * <pre>{@code
		 * int shift(int x, final Param param) {
		 *     x ^= x << param.a;
		 *     x ^= x << param.c;
		 *     return x^x >>> param.b;
		 * }
		 * }</pre>
		 */
		SHIFT_5 {
			@Override
			public int shift(int x, final Param param) {
				x ^= x << param.a;
				x ^= x << param.c;
				return x^x >>> param.b;
			}
		},

		/**
		 * Shift strategy six.
		 * <pre>{@code
		 * int shift(int x, final Param param) {
		 *     x ^= x << param.c;
		 *     x ^= x << param.a;
		 *     return x^x >>> param.b;
		 * }
		 * }</pre>
		 */
		SHIFT_6 {
			@Override
			public int shift(int x, final Param param) {
				x ^= x << param.c;
				x ^= x << param.a;
				return x^x >>> param.b;
			}
		},

		/**
		 * Shift strategy seven.
		 * <pre>{@code
		 * int shift(int x, final Param param) {
		 *     x ^= x >>> param.a;
		 *     x ^= x >>> param.c;
		 *     return x^x << param.b;
		 * }
		 * }</pre>
		 */
		SHIFT_7 {
			@Override
			public int shift(int x, final Param param) {
				x ^= x >>> param.a;
				x ^= x >>> param.c;
				return x^x << param.b;
			}
		},

		/**
		 * Shift strategy eight.
		 * <pre>{@code
		 * int shift(int x, final Param param) {
		 *     x ^= x >>> param.c;
		 *     x ^= x >>> param.a;
		 *     return x^x << param.b;
		 * }
		 * }</pre>
		 */
		SHIFT_8 {
			@Override
			public int shift(int x, final Param param) {
				x ^= x >>> param.c;
				x ^= x >>> param.a;
				return x^x << param.b;
			}
		};

		/**
		 * The <em>default</em> shift strategy.
		 */
		public static final Shift DEFAULT = SHIFT_1;

		/**
		 * Performs the <em>xor</em> shift of {@code x} with the given
		 * {@code param}.
		 *
		 * @param x the value where the <em>xor</em> shift is performed
		 * @param param the shift parameters
		 * @return the <em>xor</em> shifted value
		 */
		public abstract int shift(int x, final Param param);
	}

	/**
	 * Parameter class for the {@code XOR32ShiftRandom} generator.
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
		public static final List<Param> PARAMS = listOf(
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
			new Param(6, 21, 7)    // p=100, w=2, f=12
		);

		/**
		 * The default parameter used by the PRNG. It's the parameter with the
		 * best <i>dieharder</i> test result.
		 */
		public static final Param DEFAULT = PARAMS.get(0);

		/**
		 * The parameter <em>a</em>.
		 */
		public final int a;

		/**
		 * The parameter <em>b</em>.
		 */
		public final int b;

		/**
		 * The parameter <em>c</em>.
		 */
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
			int hash = 17;
			hash += 31*a + 37;
			hash += 31*b + 37;
			hash += 31*c + 37;

			return hash;
		}

		@Override
		public boolean equals(final Object obj) {
			return  obj instanceof Param &&
				((Param)obj).a == a &&
				((Param)obj).b == b &&
				((Param)obj).c == c;
		}

		@Override
		public String toString() {
			return String.format("Param[%d, %d, %d]", a, b, c);
		}
	}

	/* *************************************************************************
	 * Thread safe classes.
	 * ************************************************************************/

	/**
	 * This class represents a <i>thread local</i> implementation of the
	 * {@code XOR32ShiftRandom} PRNG.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since !__version__!
	 * @version !__version__!
	 */
	public static final class ThreadLocal
		extends java.lang.ThreadLocal<XOR32ShiftRandom>
	{
		@Override
		protected XOR32ShiftRandom initialValue() {
			return new TLXOR32ShiftRandom();
		}
	}

	private static final class TLXOR32ShiftRandom extends XOR32ShiftRandom {
		private static final long serialVersionUID = 1L;

		private static volatile int _paramIndex = 0;

		private final Boolean _sentry = Boolean.TRUE;

		private TLXOR32ShiftRandom() {
			super(nextShift(), nextParam(), XOR32ShiftRandom.seedBytes());
		}

		private static Shift nextShift() {
			nextParamIndex();
			return Shift.values()[_paramIndex/Param.PARAMS.size()];
		}

		private static Param nextParam() {
			return Param.PARAMS.get(_paramIndex%Param.PARAMS.size());
		}

		private static int nextParamIndex() {
			return _paramIndex = _paramIndex++%
				(Param.PARAMS.size()*Shift.values().length);
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
	public static class ThreadSafe extends XOR32ShiftRandom {
		private static final long serialVersionUID = 1L;

		/**
		 * Create a new PRNG instance with the given <em>shift</em>, parameter and
		 * seed.
		 *
		 * @param shift the <em>shift</em> strategy of the PRNG
		 * @param param the parameter of the PRNG.
		 * @param seed the seed of the PRNG.
		 * @throws NullPointerException if the given {@code shift}, {@code param} or
		 *         {@code seed} is {@code null}.
		 * @throws IllegalArgumentException if the given seed is shorter than
		 *         {@link #SEED_BYTES}
		 */
		public ThreadSafe(
			final Shift shift,
			final Param param,
			final byte[] seed
		) {
			super(shift, param, seed);
		}

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
		public synchronized int nextInt() {
			return super.nextInt();
		}

	}


	/* *************************************************************************
	 * Main class.
	 * ************************************************************************/

	// The largest prime smaller than 2^31.
	private static final int SAFE_SEED = 2147483647;

	/**
	 * The number of seed bytes (4) this PRNG requires.
	 */
	public static final int SEED_BYTES = 4;

	private final Shift _shift;
	private final Param _param;

	private int _x = 0;

	/**
	 * Create a new PRNG instance with the given <em>shift</em>, parameter and
	 * seed.
	 *
	 * @param shift the <em>shift</em> strategy of the PRNG
	 * @param param the parameter of the PRNG.
	 * @param seed the seed of the PRNG.
	 * @throws NullPointerException if the given {@code shift}, {@code param} or
	 *         {@code seed} is {@code null}.
	 * @throws IllegalArgumentException if the given seed is shorter than
	 *         {@link #SEED_BYTES}
	 */
	public XOR32ShiftRandom(
		final Shift shift,
		final Param param,
		final byte[] seed
	) {
		_shift = requireNonNull(shift, "Shift strategy must not be null.");
		_param = requireNonNull(param, "PRNG param must not be null.");
		setSeed(seed);
	}

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
	public XOR32ShiftRandom(final Param param, final byte[] seed) {
		this(Shift.DEFAULT, param, seed);
	}

	/**
	 * Create a new PRNG instance with the given parameter and seed.
	 *
	 * @param param the parameter of the PRNG.
	 * @param seed the seed of the PRNG.
	 * @throws NullPointerException if the given {@code param} is {@code null}.
	 */
	public XOR32ShiftRandom(final Param param, final long seed) {
		this(param, PRNG.seedBytes(seed, SEED_BYTES));
	}

	/**
	 * Create a new PRNG instance with the given parameter and a safe seed
	 *
	 * @param param the PRNG parameter.
	 * @throws NullPointerException if the given {@code param} is null.
	 */
	public XOR32ShiftRandom(final Param param) {
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
	public XOR32ShiftRandom(final byte[] seed) {
		this(Param.DEFAULT, seed);
	}

	/**
	 * Create a new PRNG instance with {@link Param#DEFAULT} parameter and the
	 * given seed.
	 *
	 * @param seed the seed of the PRNG
	 */
	public XOR32ShiftRandom(final long seed) {
		this(Param.DEFAULT, PRNG.seedBytes(seed, SEED_BYTES));
	}

	/**
	 * Create a new PRNG instance with {@link Param#DEFAULT} parameter and safe
	 * seed.
	 */
	public XOR32ShiftRandom() {
		this(Param.DEFAULT, PRNG.seed());
	}

	/**
	 * Set the seed value of the PRNG.
	 *
	 * @param seed the seed value.
	 * @throws IllegalArgumentException if the given seed is shorter than
	 *         {@link #SEED_BYTES}
	 */
	public void setSeed(final byte[] seed) {
		if (seed.length < SEED_BYTES) {
			throw new IllegalArgumentException(format(
				"Required %d seed bytes, but got %d.",
				SEED_BYTES, seed.length
			));
		}

		_x = toSafeSeed(readInt(seed, 0));
	}

	private static int toSafeSeed(final int seed) {
		return seed == 0 ? SAFE_SEED : seed;
	}

	@Override
	public void setSeed(final long seed) {
		_x = toSafeSeed((int)seed);
	}

	@Override
	public int nextInt() {
		return _x = _shift.shift(_x, _param);
	}

	@Override
	public String toString() {
		return String.format("XOR32ShiftRandom[%s, x=%d]", _param, _x);
	}

	@Override
	public int hashCode() {
		int hash = 31;
		hash += 17*_x + 37;
		hash += 17*_param.hashCode() + 37;

		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof XOR32ShiftRandom &&
			((XOR32ShiftRandom)obj)._x == _x &&
			Objects.equals(((XOR32ShiftRandom)obj)._param, _param);
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
# Testing: org.jenetics.random.XOR32ShiftRandom (2016-09-02 22:05)            #
#=============================================================================#
#=============================================================================#
# Linux 4.4.0-36-generic (amd64)                                              #
# java version "1.8.0_102"                                                    #
# Java(TM) SE Runtime Environment (build 1.8.0_102-b14)                       #
# Java HotSpot(TM) 64-Bit Server VM (build 25.102-b14)                        #
#=============================================================================#
#=============================================================================#
#            dieharder version 3.31.1 Copyright 2003 Robert G. Brown          #
#=============================================================================#
   rng_name    |rands/second|   Seed   |
stdin_input_raw|  4.53e+07  |2048417961|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.29409251|  PASSED
      diehard_operm5|   0|   1000000|     100|0.98831377|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.00000000|  FAILED
    diehard_rank_6x8|   0|    100000|     100|0.48459256|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.26764905|  PASSED
        diehard_opso|   0|   2097152|     100|0.00277144|   WEAK
        diehard_oqso|   0|   2097152|     100|0.96207391|  PASSED
         diehard_dna|   0|   2097152|     100|0.49846574|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.00000000|  FAILED
diehard_count_1s_byt|   0|    256000|     100|0.02053481|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.66754904|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.14134839|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.87879934|  PASSED
     diehard_squeeze|   0|    100000|     100|0.69087825|  PASSED
        diehard_sums|   0|       100|     100|0.19779750|  PASSED
        diehard_runs|   0|    100000|     100|0.27279068|  PASSED
        diehard_runs|   0|    100000|     100|0.56815121|  PASSED
       diehard_craps|   0|    200000|     100|0.84946451|  PASSED
       diehard_craps|   0|    200000|     100|0.83755060|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.30097381|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.00000000|  FAILED
         sts_monobit|   1|    100000|     100|0.72316249|  PASSED
            sts_runs|   2|    100000|     100|0.54413871|  PASSED
          sts_serial|   1|    100000|     100|0.98927837|  PASSED
          sts_serial|   2|    100000|     100|0.77666470|  PASSED
          sts_serial|   3|    100000|     100|0.17264465|  PASSED
          sts_serial|   3|    100000|     100|0.45247625|  PASSED
          sts_serial|   4|    100000|     100|0.71674278|  PASSED
          sts_serial|   4|    100000|     100|0.59529838|  PASSED
          sts_serial|   5|    100000|     100|0.37149413|  PASSED
          sts_serial|   5|    100000|     100|0.94743346|  PASSED
          sts_serial|   6|    100000|     100|0.58633284|  PASSED
          sts_serial|   6|    100000|     100|0.55484222|  PASSED
          sts_serial|   7|    100000|     100|0.00755106|  PASSED
          sts_serial|   7|    100000|     100|0.23416825|  PASSED
          sts_serial|   8|    100000|     100|0.10600854|  PASSED
          sts_serial|   8|    100000|     100|0.85375629|  PASSED
          sts_serial|   9|    100000|     100|0.14810290|  PASSED
          sts_serial|   9|    100000|     100|0.46807609|  PASSED
          sts_serial|  10|    100000|     100|0.65581898|  PASSED
          sts_serial|  10|    100000|     100|0.79409212|  PASSED
          sts_serial|  11|    100000|     100|0.25978259|  PASSED
          sts_serial|  11|    100000|     100|0.18226648|  PASSED
          sts_serial|  12|    100000|     100|0.54406507|  PASSED
          sts_serial|  12|    100000|     100|0.92693460|  PASSED
          sts_serial|  13|    100000|     100|0.22984318|  PASSED
          sts_serial|  13|    100000|     100|0.06058651|  PASSED
          sts_serial|  14|    100000|     100|0.04754317|  PASSED
          sts_serial|  14|    100000|     100|0.06331447|  PASSED
          sts_serial|  15|    100000|     100|0.50411975|  PASSED
          sts_serial|  15|    100000|     100|0.82524520|  PASSED
          sts_serial|  16|    100000|     100|0.29259713|  PASSED
          sts_serial|  16|    100000|     100|0.57133942|  PASSED
         rgb_bitdist|   1|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   2|    100000|     100|0.00177431|   WEAK
         rgb_bitdist|   3|    100000|     100|0.18764796|  PASSED
         rgb_bitdist|   4|    100000|     100|0.99675420|   WEAK
         rgb_bitdist|   5|    100000|     100|0.25135529|  PASSED
         rgb_bitdist|   6|    100000|     100|0.09056019|  PASSED
         rgb_bitdist|   7|    100000|     100|0.82714423|  PASSED
         rgb_bitdist|   8|    100000|     100|0.00838282|  PASSED
         rgb_bitdist|   9|    100000|     100|0.96519821|  PASSED
         rgb_bitdist|  10|    100000|     100|0.03528085|  PASSED
         rgb_bitdist|  11|    100000|     100|0.67540435|  PASSED
         rgb_bitdist|  12|    100000|     100|0.55048365|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.00004446|   WEAK
rgb_minimum_distance|   3|     10000|    1000|0.00017627|   WEAK
rgb_minimum_distance|   4|     10000|    1000|0.10031800|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.00061779|   WEAK
    rgb_permutations|   2|    100000|     100|0.23917956|  PASSED
    rgb_permutations|   3|    100000|     100|0.43919345|  PASSED
    rgb_permutations|   4|    100000|     100|0.67514035|  PASSED
    rgb_permutations|   5|    100000|     100|0.03159793|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.01834274|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.04580168|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.13947763|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.08710710|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.52928821|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.77639819|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.21513553|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.53207079|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.93931498|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.78840186|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.82417822|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.55421415|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.10804748|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.30612123|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.96603045|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.11254273|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.25487465|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.97555897|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.88670102|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.78879065|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.45622268|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.97163014|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.37542594|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.78297357|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.61047298|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.78191150|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.51076747|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.58481575|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.67892391|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.55545850|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.73194652|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.49202129|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.82762699|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.01314168|  PASSED
     dab_bytedistrib|   0|  51200000|       1|1.00000000|  FAILED
             dab_dct| 256|     50000|       1|0.60978219|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.30367054|  PASSED
        dab_filltree|  32|  15000000|       1|0.13156686|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.47887137|  PASSED
       dab_filltree2|   1|   5000000|       1|0.44669138|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|1.00000000|  FAILED
#=============================================================================#
# Summary: PASSED=102, WEAK=6, FAILED=6                                       #
#          235,031.500 MB of random data created with 135.851 MB/sec          #
#=============================================================================#
#=============================================================================#
# Runtime: 0:28:50                                                            #
#=============================================================================#
*/
