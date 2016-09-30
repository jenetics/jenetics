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
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
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
			new Param(2, 21, 9),
			new Param(3, 13, 7),
			new Param(5, 13, 6),
			new Param(5, 15, 17),
			new Param(5, 17, 13),
			new Param(5, 21, 12),
			new Param(5, 9, 7),
			new Param(6, 21, 7)
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
			return String.format("Param(%d, %d, %d)", a, b, c);
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
		private final ParamSelector _selector;

		/**
		 * Create a new PRNG thread-local instance with the given <em>shift</em>
		 * and parameter.
		 *
		 * @param shift the <em>shift</em> strategy of the PRNG
		 * @param param the parameter of the PRNG.
		 * @throws NullPointerException if the given {@code shift} or
		 *        {@code param} is {@code null}.
		 */
		public ThreadLocal(final Shift shift, final Param param) {
			_selector = new ParamSelector(
				singletonList(shift),
				singletonList(param)
			);
		}

		/**
		 * Create a new PRNG thread-local instance with the given <em>shift</em>
		 * parameter.
		 *
		 * @param shift the <em>shift</em> strategy of the PRNG
		 * @throws NullPointerException if the given {@code shift} is
		 *         {@code null}.
		 */
		public ThreadLocal(final Shift shift) {
			_selector = new ParamSelector(singletonList(shift), Param.PARAMS);
		}

		/**
		 * Create a new PRNG thread-local instance with the given parameter.
		 *
		 * @param param the parameter of the PRNG.
		 * @throws NullPointerException if the given {@code param} is
		 *         {@code null}.
		 */
		public ThreadLocal(final Param param) {
			_selector = new ParamSelector(
				asList(Shift.values()),
				singletonList(param)
			);
		}

		/**
		 * Create a new PRNG thread-local instance.
		 */
		public ThreadLocal() {
			_selector = new ParamSelector(asList(Shift.values()), Param.PARAMS);
		}

		/**
		 * Create a new PRNG using different parameter values for every thread.
		 */
		@Override
		protected XOR32ShiftRandom initialValue() {
			final TLRandom random = new TLRandom(
				_selector.shift(),
				_selector.param(),
				_selector.seed()
			);
			_selector.next();

			return random;
		}
	}

	/**
	 * Helper class for periodical change or the PRNG parameters <em>shift</em>,
	 * <em>param</em> and <em>seed</em>.
	 */
	static final class ParamSelector {
		private final List<Shift> _shifts;
		private final List<Param> _params;
		private final int _paramCount;

		private int _index = 0;
		private byte[] _seed = XOR32ShiftRandom.seedBytes();

		ParamSelector(final List<Shift> shifts, final List<Param> params) {
			_shifts = requireNonNull(shifts);
			_params = requireNonNull(params);
			_paramCount = shifts.size()*params.size();
		}

		void next() {
			if (++_index >= _paramCount) {
				_index = 0;
				_seed = XOR32ShiftRandom.seedBytes();
			}
		}

		Shift shift() {
			return _shifts.get(_index/_params.size());
		}

		Param param() {
			return _params.get(_index%_params.size());
		}

		byte[] seed() {
			return _seed;
		}
	}

	private static final class TLRandom extends XOR32ShiftRandom {
		private static final long serialVersionUID = 1L;

		private final Boolean _sentry = Boolean.TRUE;

		private TLRandom(
			final Shift shift,
			final Param param,
			final byte[] seed
		) {
			super(shift, param, seed);
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
# Testing: org.jenetics.random.XOR32ShiftRandom (2016-09-30 00:54)            #
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
stdin_input_raw|  5.62e+07  |3812524575|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.82635350|  PASSED
      diehard_operm5|   0|   1000000|     100|0.72501787|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.00000000|  FAILED
    diehard_rank_6x8|   0|    100000|     100|0.45705074|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.98725322|  PASSED
        diehard_opso|   0|   2097152|     100|0.45317763|  PASSED
        diehard_oqso|   0|   2097152|     100|0.59192110|  PASSED
         diehard_dna|   0|   2097152|     100|0.41710707|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.00000000|  FAILED
diehard_count_1s_byt|   0|    256000|     100|0.26631897|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.00001001|   WEAK
    diehard_2dsphere|   2|      8000|     100|0.00000000|  FAILED
    diehard_3dsphere|   3|      4000|     100|0.85053350|  PASSED
     diehard_squeeze|   0|    100000|     100|0.19968749|  PASSED
        diehard_sums|   0|       100|     100|0.01788686|  PASSED
        diehard_runs|   0|    100000|     100|0.05912000|  PASSED
        diehard_runs|   0|    100000|     100|0.91022937|  PASSED
       diehard_craps|   0|    200000|     100|0.20642500|  PASSED
       diehard_craps|   0|    200000|     100|0.98811011|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.00000000|  FAILED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.34061957|  PASSED
         sts_monobit|   1|    100000|     100|0.45707587|  PASSED
            sts_runs|   2|    100000|     100|0.08446277|  PASSED
          sts_serial|   1|    100000|     100|0.42429471|  PASSED
          sts_serial|   2|    100000|     100|0.03498629|  PASSED
          sts_serial|   3|    100000|     100|0.95393742|  PASSED
          sts_serial|   3|    100000|     100|0.28742611|  PASSED
          sts_serial|   4|    100000|     100|0.18348364|  PASSED
          sts_serial|   4|    100000|     100|0.08648288|  PASSED
          sts_serial|   5|    100000|     100|0.90786930|  PASSED
          sts_serial|   5|    100000|     100|0.27474584|  PASSED
          sts_serial|   6|    100000|     100|0.80114592|  PASSED
          sts_serial|   6|    100000|     100|0.58978717|  PASSED
          sts_serial|   7|    100000|     100|0.67394566|  PASSED
          sts_serial|   7|    100000|     100|0.76916998|  PASSED
          sts_serial|   8|    100000|     100|0.96175209|  PASSED
          sts_serial|   8|    100000|     100|0.50441036|  PASSED
          sts_serial|   9|    100000|     100|0.11204788|  PASSED
          sts_serial|   9|    100000|     100|0.06086074|  PASSED
          sts_serial|  10|    100000|     100|0.14390280|  PASSED
          sts_serial|  10|    100000|     100|0.53094865|  PASSED
          sts_serial|  11|    100000|     100|0.42282318|  PASSED
          sts_serial|  11|    100000|     100|0.13445355|  PASSED
          sts_serial|  12|    100000|     100|0.11531712|  PASSED
          sts_serial|  12|    100000|     100|0.83951766|  PASSED
          sts_serial|  13|    100000|     100|0.98550696|  PASSED
          sts_serial|  13|    100000|     100|0.38212444|  PASSED
          sts_serial|  14|    100000|     100|0.82550425|  PASSED
          sts_serial|  14|    100000|     100|0.64534262|  PASSED
          sts_serial|  15|    100000|     100|0.86455657|  PASSED
          sts_serial|  15|    100000|     100|0.31726770|  PASSED
          sts_serial|  16|    100000|     100|0.64866619|  PASSED
          sts_serial|  16|    100000|     100|0.89267999|  PASSED
         rgb_bitdist|   1|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   2|    100000|     100|0.78639192|  PASSED
         rgb_bitdist|   3|    100000|     100|0.00000000|  FAILED
         rgb_bitdist|   4|    100000|     100|0.03614400|  PASSED
         rgb_bitdist|   5|    100000|     100|0.40970851|  PASSED
         rgb_bitdist|   6|    100000|     100|0.75811958|  PASSED
         rgb_bitdist|   7|    100000|     100|0.22513124|  PASSED
         rgb_bitdist|   8|    100000|     100|0.85396454|  PASSED
         rgb_bitdist|   9|    100000|     100|0.95354164|  PASSED
         rgb_bitdist|  10|    100000|     100|0.34187746|  PASSED
         rgb_bitdist|  11|    100000|     100|0.96035223|  PASSED
         rgb_bitdist|  12|    100000|     100|0.62267956|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.00000000|  FAILED
rgb_minimum_distance|   3|     10000|    1000|0.08500645|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.28507774|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.05768206|  PASSED
    rgb_permutations|   2|    100000|     100|0.35385972|  PASSED
    rgb_permutations|   3|    100000|     100|0.35135954|  PASSED
    rgb_permutations|   4|    100000|     100|0.87261641|  PASSED
    rgb_permutations|   5|    100000|     100|0.57880938|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.69775919|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.76112225|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.00220492|   WEAK
      rgb_lagged_sum|   3|   1000000|     100|0.91743186|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.26005188|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.82045227|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.01031718|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.88955633|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.13233756|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.79492885|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.81067816|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.04394395|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.32266394|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.16484927|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.43316254|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.84712372|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.67280961|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.80903853|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.30183593|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.77218021|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.25640449|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.49049354|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.76749350|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.44117247|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.93483847|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.03195207|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.27762707|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.86796566|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.84171023|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.79473100|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.55614112|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.18537074|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.97269194|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.26026773|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.94352391|  PASSED
             dab_dct| 256|     50000|       1|0.14905198|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.40854845|  PASSED
        dab_filltree|  32|  15000000|       1|0.50121407|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.49732688|  PASSED
       dab_filltree2|   1|   5000000|       1|0.03971529|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|1.00000000|  FAILED
#=============================================================================#
# Summary: PASSED=104, WEAK=2, FAILED=8                                       #
#          235,031.250 MB of random data created with 120.698 MB/sec          #
#=============================================================================#
#=============================================================================#
# Runtime: 0:32:27                                                            #
#=============================================================================#
*/
