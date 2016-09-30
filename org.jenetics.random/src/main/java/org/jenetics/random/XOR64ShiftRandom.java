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
import static org.jenetics.random.utils.readLong;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * This generator was discovered and characterized by George Marsaglia
 * [<a href="http://www.jstatsoft.org/v08/i14/paper">Xorshift RNGs</a>]. In just
 * three XORs and three shifts (generally fast operations) it produces a full
 * period of 2<sup>64</sup> - 1 on 64 bits. (The missing value is zero, which
 * perpetuates itself and must be avoided.) High and low bits pass Diehard.
 * <p>
 * Implementation of the XOR shift PRNG. The following listing shows the actual
 * PRNG implementation.
 * <pre>{@code
 * private final int a, b, c = <param>
 * private long x = <seed>
 *
 * long nextLong() {
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
public class XOR64ShiftRandom extends Random64 {

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
		 * int shift(long x, final Param param) {
		 *     x ^= x << param.a;
		 *     x ^= x >>> param.b;
		 *     return x^x << param.c;
		 * }
		 * }</pre>
		 */
		SHIFT_1 {
			@Override
			public long shift(long x, final Param param) {
				x ^= x << param.a;
				x ^= x >>> param.b;
				return x^x << param.c;
			}
		},

		/**
		 * Shift strategy two.
		 * <pre>{@code
		 * int shift(long x, final Param param) {
		 *     x ^= x << param.c;
		 *     x ^= x >>> param.b;
		 *     return x^x << param.a;
		 * }
		 * }</pre>
		 */
		SHIFT_2 {
			@Override
			public long shift(long x, final Param param) {
				x ^= x << param.c;
				x ^= x >>> param.b;
				return x^x << param.a;
			}
		},

		/**
		 * Shift strategy three.
		 * <pre>{@code
		 * int shift(long x, final Param param) {
		 *     x ^= x >>> param.a;
		 *     x ^= x << param.b;
		 *     return x^x >>> param.c;
		 * }
		 * }</pre>
		 */
		SHIFT_3 {
			@Override
			public long shift(long x, final Param param) {
				x ^= x >>> param.a;
				x ^= x << param.b;
				return x^x >>> param.c;
			}
		},

		/**
		 * Shift strategy four.
		 * <pre>{@code
		 * int shift(long x, final Param param) {
		 *     x ^= x >>> param.c;
		 *     x ^= x << param.b;
		 *     return x^x >>> param.a;
		 * }
		 * }</pre>
		 */
		SHIFT_4 {
			@Override
			public long shift(long x, final Param param) {
				x ^= x >>> param.c;
				x ^= x << param.b;
				return x^x >>> param.a;
			}
		},

		/**
		 * Shift strategy five.
		 * <pre>{@code
		 * int shift(long x, final Param param) {
		 *     x ^= x << param.a;
		 *     x ^= x << param.c;
		 *     return x^x >>> param.b;
		 * }
		 * }</pre>
		 */
		SHIFT_5 {
			@Override
			public long shift(long x, final Param param) {
				x ^= x << param.a;
				x ^= x << param.c;
				return x^x >>> param.b;
			}
		},

		/**
		 * Shift strategy six.
		 * <pre>{@code
		 * int shift(long x, final Param param) {
		 *     x ^= x << param.c;
		 *     x ^= x << param.a;
		 *     return x^x >>> param.b;
		 * }
		 * }</pre>
		 */
		SHIFT_6 {
			@Override
			public long shift(long x, final Param param) {
				x ^= x << param.c;
				x ^= x << param.a;
				return x^x >>> param.b;
			}
		},

		/**
		 * Shift strategy seven.
		 * <pre>{@code
		 * int shift(long x, final Param param) {
		 *     x ^= x >>> param.a;
		 *     x ^= x >>> param.c;
		 *     return x^x << param.b;
		 * }
		 * }</pre>
		 */
		SHIFT_7 {
			@Override
			public long shift(long x, final Param param) {
				x ^= x >>> param.a;
				x ^= x >>> param.c;
				return x^x << param.b;
			}
		},

		/**
		 * Shift strategy eight.
		 * <pre>{@code
		 * int shift(long x, final Param param) {
		 *     x ^= x >>> param.c;
		 *     x ^= x >>> param.a;
		 *     return x^x << param.b;
		 * }
		 * }</pre>
		 */
		SHIFT_8 {
			@Override
			public long shift(long x, final Param param) {
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
		public abstract long shift(long x, final Param param);
	}

	/**
	 * Parameter class for the {@code XOR64ShiftRandom} generator.
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
			new Param(1, 19, 16),
			new Param(1, 11, 50),
			new Param(1, 19, 6),
			new Param(1, 23, 14),
			new Param(1, 51, 13),
			new Param(1, 7, 9),
			new Param(10, 27, 59),
			new Param(10, 53, 13),
			new Param(11, 13, 40),
			new Param(11, 15, 37),
			new Param(11, 23, 42),
			new Param(11, 25, 48),
			new Param(11, 27, 26),
			new Param(11, 53, 23),
			new Param(11, 9, 14),
			new Param(11, 9, 34),
			new Param(12, 11, 47),
			new Param(12, 25, 27),
			new Param(13, 17, 43),
			new Param(13, 19, 28),
			new Param(13, 19, 47),
			new Param(13, 21, 18),
			new Param(13, 21, 49),
			new Param(13, 29, 35),
			new Param(13, 47, 23),
			new Param(13, 51, 21),
			new Param(13, 9, 15),
			new Param(13, 9, 50),
			new Param(14, 13, 17),
			new Param(14, 15, 19),
			new Param(14, 23, 33),
			new Param(15, 13, 28),
			new Param(15, 13, 52),
			new Param(15, 17, 27),
			new Param(15, 19, 63),
			new Param(15, 21, 46),
			new Param(15, 45, 17),
			new Param(15, 49, 26),
			new Param(16, 11, 19),
			new Param(16, 11, 27),
			new Param(16, 13, 55),
			new Param(16, 21, 35),
			new Param(16, 25, 43),
			new Param(17, 23, 29),
			new Param(17, 23, 51),
			new Param(17, 23, 52),
			new Param(17, 27, 22),
			new Param(17, 45, 22),
			new Param(17, 47, 28),
			new Param(17, 47, 29),
			new Param(18, 25, 21),
			new Param(18, 41, 23),
			new Param(19, 13, 37),
			new Param(19, 15, 46),
			new Param(19, 21, 52),
			new Param(19, 25, 20),
			new Param(19, 41, 21),
			new Param(19, 43, 27),
			new Param(2, 13, 23),
			new Param(21, 13, 52),
			new Param(21, 15, 28),
			new Param(21, 15, 29),
			new Param(21, 17, 24),
			new Param(21, 17, 30),
			new Param(21, 41, 23),
			new Param(21, 9, 29),
			new Param(23, 13, 38),
			new Param(23, 17, 25),
			new Param(23, 41, 34),
			new Param(23, 9, 38),
			new Param(23, 9, 48),
			new Param(23, 9, 57),
			new Param(24, 11, 29),
			new Param(24, 9, 35),
			new Param(25, 11, 57),
			new Param(25, 13, 29),
			new Param(25, 21, 44),
			new Param(25, 9, 39),
			new Param(29, 27, 37),
			new Param(3, 25, 20),
			new Param(3, 43, 11),
			new Param(3, 53, 7),
			new Param(31, 25, 37),
			new Param(4, 15, 51),
			new Param(4, 15, 53),
			new Param(4, 41, 19),
			new Param(4, 43, 21),
			new Param(4, 53, 7),
			new Param(4, 9, 13),
			new Param(5, 11, 54),
			new Param(5, 15, 27),
			new Param(5, 17, 11),
			new Param(5, 41, 20),
			new Param(5, 45, 16),
			new Param(5, 47, 23),
			new Param(5, 53, 20),
			new Param(5, 9, 23),
			new Param(6, 17, 47),
			new Param(6, 23, 27),
			new Param(6, 27, 7),
			new Param(6, 43, 21),
			new Param(6, 55, 17),
			new Param(7, 11, 10),
			new Param(7, 11, 35),
			new Param(7, 13, 58),
			new Param(7, 19, 17),
			new Param(7, 19, 54),
			new Param(7, 23, 8),
			new Param(7, 25, 58),
			new Param(7, 43, 28),
			new Param(7, 51, 24),
			new Param(7, 57, 12),
			new Param(7, 9, 38),
			new Param(8, 13, 25),
			new Param(8, 13, 61),
			new Param(8, 15, 21),
			new Param(8, 25, 59),
			new Param(8, 29, 19),
			new Param(8, 51, 21),
			new Param(8, 9, 25),
			new Param(9, 19, 18),
			new Param(9, 21, 11),
			new Param(9, 21, 20),
			new Param(9, 21, 40),
			new Param(9, 23, 57),
			new Param(9, 27, 10),
			new Param(9, 29, 12)
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
	 * {@code XOR64ShiftRandom} PRNG.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since !__version__!
	 * @version !__version__!
	 */
	public static final class ThreadLocal
		extends java.lang.ThreadLocal<XOR64ShiftRandom>
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
		protected XOR64ShiftRandom initialValue() {
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

	private static final class TLRandom extends XOR64ShiftRandom {
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
	public static class ThreadSafe extends XOR64ShiftRandom {
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
		public synchronized long nextLong() {
			return super.nextLong();
		}

	}


	/* *************************************************************************
	 * Main class.
	 * ************************************************************************/

	// The largest prime smaller than 2^63.
	private static final long SAFE_SEED = 9223372036854775783L;

	/**
	 * The number of seed bytes (8) this PRNG requires.
	 */
	public static final int SEED_BYTES = 8;

	private final Shift _shift;
	private final Param _param;

	private long _x = 0;

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
	public XOR64ShiftRandom(
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
	public XOR64ShiftRandom(final Param param, final byte[] seed) {
		this(Shift.DEFAULT, param, seed);
	}

	/**
	 * Create a new PRNG instance with the given parameter and seed.
	 *
	 * @param param the parameter of the PRNG.
	 * @param seed the seed of the PRNG.
	 * @throws NullPointerException if the given {@code param} is {@code null}.
	 */
	public XOR64ShiftRandom(final Param param, final long seed) {
		this(param, PRNG.seedBytes(seed, SEED_BYTES));
	}

	/**
	 * Create a new PRNG instance with the given parameter and a safe seed
	 *
	 * @param param the PRNG parameter.
	 * @throws NullPointerException if the given {@code param} is null.
	 */
	public XOR64ShiftRandom(final Param param) {
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
	public XOR64ShiftRandom(final byte[] seed) {
		this(Param.DEFAULT, seed);
	}

	/**
	 * Create a new PRNG instance with {@link Param#DEFAULT} parameter and the
	 * given seed.
	 *
	 * @param seed the seed of the PRNG
	 */
	public XOR64ShiftRandom(final long seed) {
		this(Param.DEFAULT, PRNG.seedBytes(seed, SEED_BYTES));
	}

	/**
	 * Create a new PRNG instance with {@link Param#DEFAULT} parameter and safe
	 * seed.
	 */
	public XOR64ShiftRandom() {
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

		_x = toSafeSeed(readLong(seed, 0));
	}

	private static long toSafeSeed(final long seed) {
		return seed == 0 ? 1179196819L : seed;
	}

	@Override
	public void setSeed(final long seed) {
		_x = toSafeSeed((int)seed);
	}

	@Override
	public long nextLong() {
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
		return obj instanceof XOR64ShiftRandom &&
			((XOR64ShiftRandom)obj)._x == _x &&
			Objects.equals(((XOR64ShiftRandom)obj)._param, _param);
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
# Testing: org.jenetics.random.XOR64ShiftRandom (2016-09-30 01:27)            #
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
stdin_input_raw|  5.17e+07  |1670880866|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.24512608|  PASSED
      diehard_operm5|   0|   1000000|     100|0.59930244|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.79107137|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.34713770|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.72479612|  PASSED
        diehard_opso|   0|   2097152|     100|0.25299754|  PASSED
        diehard_oqso|   0|   2097152|     100|0.07841347|  PASSED
         diehard_dna|   0|   2097152|     100|0.40708018|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.04940621|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.09706893|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.76094232|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.35111105|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.37358899|  PASSED
     diehard_squeeze|   0|    100000|     100|0.63986836|  PASSED
        diehard_sums|   0|       100|     100|0.12475311|  PASSED
        diehard_runs|   0|    100000|     100|0.91899847|  PASSED
        diehard_runs|   0|    100000|     100|0.22021389|  PASSED
       diehard_craps|   0|    200000|     100|0.02761363|  PASSED
       diehard_craps|   0|    200000|     100|0.37815653|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.87663592|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.64987867|  PASSED
         sts_monobit|   1|    100000|     100|0.99904503|   WEAK
            sts_runs|   2|    100000|     100|0.93865886|  PASSED
          sts_serial|   1|    100000|     100|0.04410343|  PASSED
          sts_serial|   2|    100000|     100|0.51070336|  PASSED
          sts_serial|   3|    100000|     100|0.51709353|  PASSED
          sts_serial|   3|    100000|     100|0.52063912|  PASSED
          sts_serial|   4|    100000|     100|0.41029028|  PASSED
          sts_serial|   4|    100000|     100|0.12833892|  PASSED
          sts_serial|   5|    100000|     100|0.99249405|  PASSED
          sts_serial|   5|    100000|     100|0.90106397|  PASSED
          sts_serial|   6|    100000|     100|0.81208058|  PASSED
          sts_serial|   6|    100000|     100|0.99782300|   WEAK
          sts_serial|   7|    100000|     100|0.19129676|  PASSED
          sts_serial|   7|    100000|     100|0.41102044|  PASSED
          sts_serial|   8|    100000|     100|0.93486881|  PASSED
          sts_serial|   8|    100000|     100|0.02874905|  PASSED
          sts_serial|   9|    100000|     100|0.53519146|  PASSED
          sts_serial|   9|    100000|     100|0.59822655|  PASSED
          sts_serial|  10|    100000|     100|0.45532179|  PASSED
          sts_serial|  10|    100000|     100|0.34722950|  PASSED
          sts_serial|  11|    100000|     100|0.43862044|  PASSED
          sts_serial|  11|    100000|     100|0.97036030|  PASSED
          sts_serial|  12|    100000|     100|0.58827890|  PASSED
          sts_serial|  12|    100000|     100|0.99082137|  PASSED
          sts_serial|  13|    100000|     100|0.84653379|  PASSED
          sts_serial|  13|    100000|     100|0.28722864|  PASSED
          sts_serial|  14|    100000|     100|0.99213353|  PASSED
          sts_serial|  14|    100000|     100|0.96236176|  PASSED
          sts_serial|  15|    100000|     100|0.95562385|  PASSED
          sts_serial|  15|    100000|     100|0.85914951|  PASSED
          sts_serial|  16|    100000|     100|0.18474476|  PASSED
          sts_serial|  16|    100000|     100|0.08618967|  PASSED
         rgb_bitdist|   1|    100000|     100|0.94089387|  PASSED
         rgb_bitdist|   2|    100000|     100|0.34258669|  PASSED
         rgb_bitdist|   3|    100000|     100|0.43439444|  PASSED
         rgb_bitdist|   4|    100000|     100|0.30712731|  PASSED
         rgb_bitdist|   5|    100000|     100|0.54898812|  PASSED
         rgb_bitdist|   6|    100000|     100|0.62018819|  PASSED
         rgb_bitdist|   7|    100000|     100|0.99999907|  FAILED
         rgb_bitdist|   8|    100000|     100|0.05230469|  PASSED
         rgb_bitdist|   9|    100000|     100|0.84509291|  PASSED
         rgb_bitdist|  10|    100000|     100|0.16768050|  PASSED
         rgb_bitdist|  11|    100000|     100|0.49951886|  PASSED
         rgb_bitdist|  12|    100000|     100|0.44099588|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.05164550|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.46071700|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.38078337|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.19076681|  PASSED
    rgb_permutations|   2|    100000|     100|0.58714248|  PASSED
    rgb_permutations|   3|    100000|     100|0.03549587|  PASSED
    rgb_permutations|   4|    100000|     100|0.26542494|  PASSED
    rgb_permutations|   5|    100000|     100|0.74777930|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.99318332|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.66967084|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.50347199|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.77811634|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.53972780|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.04099969|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.91552429|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.55753431|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.34722473|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.73465975|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.98138578|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.83555091|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.22294443|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.39073063|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.58108873|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.95456372|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.52206463|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.99852441|   WEAK
      rgb_lagged_sum|  18|   1000000|     100|0.68016208|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.93970927|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.60384557|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.43149251|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.87471877|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.89836801|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.72145518|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.69182625|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.64873737|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.09834098|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.78659343|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.45856451|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.54442436|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.62087286|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.93924655|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.56203416|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.40690540|  PASSED
             dab_dct| 256|     50000|       1|0.11980940|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.92852507|  PASSED
        dab_filltree|  32|  15000000|       1|0.13671240|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.45484241|  PASSED
       dab_filltree2|   1|   5000000|       1|0.66068921|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|0.99855484|   WEAK
#=============================================================================#
# Summary: PASSED=109, WEAK=4, FAILED=1                                       #
#          235,031.469 MB of random data created with 122.316 MB/sec          #
#=============================================================================#
#=============================================================================#
# Runtime: 0:32:01                                                            #
#=============================================================================#
*/
