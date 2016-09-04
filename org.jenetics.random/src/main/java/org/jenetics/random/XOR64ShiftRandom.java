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
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.jenetics.random.utils.readLong;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.jenetics.random.internal.DieHarder;
import org.jenetics.random.internal.DieHarder.Assessment;
import org.jenetics.random.internal.DieHarder.Result;

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
 *     x ^= x >> b;
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
		public static final Param[] PARAMS = {
			new Param(21, 35, 4),
			new Param(20, 41, 5),
			new Param(17, 31, 8),
			new Param(11, 29, 14),
			new Param(14, 29, 11),
			new Param(30, 35, 13),
			new Param(21, 37, 4),
			new Param(21, 43, 4),
			new Param(23, 41, 18)
		};

		/**
		 * The default parameter used by the PRNG. It's the parameter with the
		 * best <i>dieharder</i> test result.
		 */
		public static final Param DEFAULT = PARAMS[0];

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
		@Override
		protected XOR64ShiftRandom initialValue() {
			return new TLXOR64ShiftRandom();
		}
	}

	private static final class TLXOR64ShiftRandom extends XOR64ShiftRandom {
		private static final long serialVersionUID = 1L;

		private static volatile int _paramIndex = 0;

		private final Boolean _sentry = Boolean.TRUE;

		private TLXOR64ShiftRandom() {
			super(nextParam(), XOR64ShiftRandom.seedBytes());
		}

		private static Param nextParam() {
			return Param.PARAMS[(_paramIndex++)%Param.PARAMS.length];
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

	/**
	 * The number of seed bytes (4) this PRNG requires.
	 */
	public static final int SEED_BYTES = 8;

	private final Param _param;

	private long _x = 0;

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
		_param = requireNonNull(param, "PRNG param must not be null.");
		setSeed(seed);
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
		_x ^= _x << _param.a;
		_x ^= _x >> _param.b;
		return _x ^= _x << _param.c;
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

	private static final Param[] ALL_PARAMS = new Param[] {
		new Param( 1, 1,54),
		new Param( 1, 1,55),
		new Param( 1, 3,45),
		new Param( 1, 7, 9),
		new Param( 1, 7,44),
		new Param( 1, 7,46),
		new Param( 1, 9,50),
		new Param( 1,11,35),
		new Param( 1,11,50),
		new Param( 1,13,45),
		new Param( 1,15, 4),
		new Param( 1,15,63),
		new Param( 1,19, 6),
		new Param( 1,19,16),
		new Param( 1,23,14),
		new Param( 1,23,29),
		new Param( 1,29,34),
		new Param( 1,35, 5),
		new Param( 1,35,11),
		new Param( 1,35,34),
		new Param( 1,45,37),
		new Param( 1,51,13),
		new Param( 1,53, 3),
		new Param( 1,59,14),
		new Param( 2,13,23),
		new Param( 2,31,51),
		new Param( 2,31,53),
		new Param( 2,43,27),
		new Param( 2,47,49),
		new Param( 3, 1,11),
		new Param( 3, 5,21),
		new Param( 3,13,59),
		new Param( 3,21,31),
		new Param( 3,25,20),
		new Param( 3,25,31),
		new Param( 3,25,56),
		new Param( 3,29,40),
		new Param( 3,29,47),
		new Param( 3,29,49),
		new Param( 3,35,14),
		new Param( 3,37,17),
		new Param( 3,43, 4),
		new Param( 3,43, 6),
		new Param( 3,43,11),
		new Param( 3,51,16),
		new Param( 3,53, 7),
		new Param( 3,61,17),
		new Param( 3,61,26),
		new Param( 4, 7,19),
		new Param( 4, 9,13),
		new Param( 4,15,51),
		new Param( 4,15,53),
		new Param( 4,29,45),
		new Param( 4,29,49),
		new Param( 4,31,33),
		new Param( 4,35,15),
		new Param( 4,35,21),
		new Param( 4,37,11),
		new Param( 4,37,21),
		new Param( 4,41,19),
		new Param( 4,41,45),
		new Param( 4,43,21),
		new Param( 4,43,31),
		new Param( 4,53, 7),
		new Param( 5, 9,23),
		new Param( 5,11,54),
		new Param( 5,15,27),
		new Param( 5,17,11),
		new Param( 5,23,36),
		new Param( 5,33,29),
		new Param( 5,41,20),
		new Param( 5,45,16),
		new Param( 5,47,23),
		new Param( 5,53,20),
		new Param( 5,59,33),
		new Param( 5,59,35),
		new Param( 5,59,63),
		new Param( 6, 1,17),
		new Param( 6, 3,49),
		new Param( 6,17,47),
		new Param( 6,23,27),
		new Param( 6,27, 7),
		new Param( 6,43,21),
		new Param( 6,49,29),
		new Param( 6,55,17),
		new Param( 7, 5,41),
		new Param( 7, 5,47),
		new Param( 7, 5,55),
		new Param( 7, 7,20),
		new Param( 7, 9,38),
		new Param( 7,11,10),
		new Param( 7,11,35),
		new Param( 7,13,58),
		new Param( 7,19,17),
		new Param( 7,19,54),
		new Param( 7,23, 8),
		new Param( 7,25,58),
		new Param( 7,27,59),
		new Param( 7,33, 8),
		new Param( 7,41,40),
		new Param( 7,43,28),
		new Param( 7,51,24),
		new Param( 7,57,12),
		new Param( 8, 5,59),
		new Param( 8, 9,25),
		new Param( 8,13,25),
		new Param( 8,13,61),
		new Param( 8,15,21),
		new Param( 8,25,59),
		new Param( 8,29,19),
		new Param( 8,31,17),
		new Param( 8,37,21),
		new Param( 8,51,21),
		new Param( 9, 1,27),
		new Param( 9, 5,36),
		new Param( 9, 5,43),
		new Param( 9, 7,18),
		new Param( 9,19,18),
		new Param( 9,21,11),
		new Param( 9,21,20),
		new Param( 9,21,40),
		new Param( 9,23,57),
		new Param( 9,27,10),
		new Param( 9,29,12),
		new Param( 9,29,37),
		new Param( 9,37,31),
		new Param( 9,41,45),
		new Param(10, 7,33),
		new Param(10,27,59),
		new Param(10,53,13),
		new Param(11, 5,32),
		new Param(11, 5,34),
		new Param(11, 5,43),
		new Param(11, 5,45),
		new Param(11, 9,14),
		new Param(11, 9,34),
		new Param(11,13,40),
		new Param(11,15,37),
		new Param(11,23,42),
		new Param(11,23,56),
		new Param(11,25,48),
		new Param(11,27,26),
		new Param(11,29,14),
		new Param(11,31,18),
		new Param(11,53,23),
		new Param(12, 1,31),
		new Param(12, 3,13),
		new Param(12, 3,49),
		new Param(12, 7,13),
		new Param(12,11,47),
		new Param(12,25,27),
		new Param(12,39,49),
		new Param(12,43,19),
		new Param(13, 3,40),
		new Param(13, 3,53),
		new Param(13, 7,17),
		new Param(13, 9,15),
		new Param(13, 9,50),
		new Param(13,13,19),
		new Param(13,17,43),
		new Param(13,19,28),
		new Param(13,19,47),
		new Param(13,21,18),
		new Param(13,21,49),
		new Param(13,29,35),
		new Param(13,35,30),
		new Param(13,35,38),
		new Param(13,47,23),
		new Param(13,51,21),
		new Param(14,13,17),
		new Param(14,15,19),
		new Param(14,23,33),
		new Param(14,31,45),
		new Param(14,47,15),
		new Param(15, 1,19),
		new Param(15, 5,37),
		new Param(15,13,28),
		new Param(15,13,52),
		new Param(15,17,27),
		new Param(15,19,63),
		new Param(15,21,46),
		new Param(15,23,23),
		new Param(15,45,17),
		new Param(15,47,16),
		new Param(15,49,26),
		new Param(16, 5,17),
		new Param(16, 7,39),
		new Param(16,11,19),
		new Param(16,11,27),
		new Param(16,13,55),
		new Param(16,21,35),
		new Param(16,25,43),
		new Param(16,27,53),
		new Param(16,47,17),
		new Param(17,15,58),
		new Param(17,23,29),
		new Param(17,23,51),
		new Param(17,23,52),
		new Param(17,27,22),
		new Param(17,45,22),
		new Param(17,47,28),
		new Param(17,47,29),
		new Param(17,47,54),
		new Param(18, 1,25),
		new Param(18, 3,43),
		new Param(18,19,19),
		new Param(18,25,21),
		new Param(18,41,23),
		new Param(19, 7,36),
		new Param(19, 7,55),
		new Param(19,13,37),
		new Param(19,15,46),
		new Param(19,21,52),
		new Param(19,25,20),
		new Param(19,41,21),
		new Param(19,43,27),
		new Param(20, 1,31),
		new Param(20, 5,29),
		new Param(21, 1,27),
		new Param(21, 9,29),
		new Param(21,13,52),
		new Param(21,15,28),
		new Param(21,15,29),
		new Param(21,17,24),
		new Param(21,17,30),
		new Param(21,17,48),
		new Param(21,21,32),
		new Param(21,21,34),
		new Param(21,21,37),
		new Param(21,21,38),
		new Param(21,21,40),
		new Param(21,21,41),
		new Param(21,21,43),
		new Param(21,41,23),
		new Param(22, 3,39),
		new Param(23, 9,38),
		new Param(23, 9,48),
		new Param(23, 9,57),
		new Param(23,13,38),
		new Param(23,13,58),
		new Param(23,13,61),
		new Param(23,17,25),
		new Param(23,17,54),
		new Param(23,17,56),
		new Param(23,17,62),
		new Param(23,41,34),
		new Param(23,41,51),
		new Param(24, 9,35),
		new Param(24,11,29),
		new Param(24,25,25),
		new Param(24,31,35),
		new Param(25, 7,46),
		new Param(25, 7,49),
		new Param(25, 9,39),
		new Param(25,11,57),
		new Param(25,13,29),
		new Param(25,13,39),
		new Param(25,13,62),
		new Param(25,15,47),
		new Param(25,21,44),
		new Param(25,27,27),
		new Param(25,27,53),
		new Param(25,33,36),
		new Param(25,39,54),
		new Param(28, 9,55),
		new Param(28,11,53),
		new Param(29,27,37),
		new Param(31, 1,51),
		new Param(31,25,37),
		new Param(31,27,35),
		new Param(33,31,43),
		new Param(33,31,55),
		new Param(43,21,46),
		new Param(49,15,61),
		new Param(55, 9,56)
	};

	// ./jrun org.jenetics.random.XOR64ShiftRandom 2>> XOR64ShiftRandom.results
	public static void main(final String[] args) throws Exception {
		final int start = Stream.of(args).findFirst()
			.map(Integer::new)
			.orElse(0);

		Arrays.asList(ALL_PARAMS).subList(start, ALL_PARAMS.length)
			.forEach(XOR64ShiftRandom::test);
	}

	private static void test(final XOR64ShiftRandom.Param param) {
		try {
			final XOR64ShiftRandom random = new XOR64ShiftRandom(param);
			final List<Result> results = DieHarder.test(random, Arrays.asList("-a"), System.out);

			final Map<Assessment, Long> grouped = results.stream()
				.collect(groupingBy(r -> r.assessment, counting()));

			final long passed = grouped.getOrDefault(Assessment.PASSED, 0L);
			final long weak = grouped.getOrDefault(Assessment.WEAK, 0L);
			final long failed = grouped.getOrDefault(Assessment.FAILED, 0L);

			synchronized (System.err) {
				System.err.println(format(
					"%d; %d; %d; %d; %s",
					(passed - failed), passed, weak, failed, param
				));
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

/*
#=============================================================================#
# Testing: org.jenetics.random.XOR64ShiftRandom (2016-09-02 22:34)            #
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
stdin_input_raw|  5.83e+07  |  93356502|
#=============================================================================#
        test_name   |ntup| tsamples |psamples|  p-value |Assessment
#=============================================================================#
   diehard_birthdays|   0|       100|     100|0.59648031|  PASSED
      diehard_operm5|   0|   1000000|     100|0.60415626|  PASSED
  diehard_rank_32x32|   0|     40000|     100|0.11809049|  PASSED
    diehard_rank_6x8|   0|    100000|     100|0.82848793|  PASSED
   diehard_bitstream|   0|   2097152|     100|0.99541355|   WEAK
        diehard_opso|   0|   2097152|     100|0.95681272|  PASSED
        diehard_oqso|   0|   2097152|     100|0.52534558|  PASSED
         diehard_dna|   0|   2097152|     100|0.06367019|  PASSED
diehard_count_1s_str|   0|    256000|     100|0.50387144|  PASSED
diehard_count_1s_byt|   0|    256000|     100|0.25307073|  PASSED
 diehard_parking_lot|   0|     12000|     100|0.22929020|  PASSED
    diehard_2dsphere|   2|      8000|     100|0.80086246|  PASSED
    diehard_3dsphere|   3|      4000|     100|0.87298393|  PASSED
     diehard_squeeze|   0|    100000|     100|0.49434606|  PASSED
        diehard_sums|   0|       100|     100|0.44404513|  PASSED
        diehard_runs|   0|    100000|     100|0.83721928|  PASSED
        diehard_runs|   0|    100000|     100|0.95434173|  PASSED
       diehard_craps|   0|    200000|     100|0.99431510|  PASSED
       diehard_craps|   0|    200000|     100|0.96406287|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.46049856|  PASSED
 marsaglia_tsang_gcd|   0|  10000000|     100|0.89546903|  PASSED
         sts_monobit|   1|    100000|     100|0.99372407|  PASSED
            sts_runs|   2|    100000|     100|0.70930203|  PASSED
          sts_serial|   1|    100000|     100|0.82347879|  PASSED
          sts_serial|   2|    100000|     100|0.07915965|  PASSED
          sts_serial|   3|    100000|     100|0.08487786|  PASSED
          sts_serial|   3|    100000|     100|0.93033731|  PASSED
          sts_serial|   4|    100000|     100|0.01867163|  PASSED
          sts_serial|   4|    100000|     100|0.36762397|  PASSED
          sts_serial|   5|    100000|     100|0.60241673|  PASSED
          sts_serial|   5|    100000|     100|0.12621894|  PASSED
          sts_serial|   6|    100000|     100|0.51828296|  PASSED
          sts_serial|   6|    100000|     100|0.93708473|  PASSED
          sts_serial|   7|    100000|     100|0.94728336|  PASSED
          sts_serial|   7|    100000|     100|0.61129404|  PASSED
          sts_serial|   8|    100000|     100|0.61394163|  PASSED
          sts_serial|   8|    100000|     100|0.69832313|  PASSED
          sts_serial|   9|    100000|     100|0.54239380|  PASSED
          sts_serial|   9|    100000|     100|0.97427986|  PASSED
          sts_serial|  10|    100000|     100|0.07712694|  PASSED
          sts_serial|  10|    100000|     100|0.99295505|  PASSED
          sts_serial|  11|    100000|     100|0.30261744|  PASSED
          sts_serial|  11|    100000|     100|0.01082089|  PASSED
          sts_serial|  12|    100000|     100|0.85180117|  PASSED
          sts_serial|  12|    100000|     100|0.65902928|  PASSED
          sts_serial|  13|    100000|     100|0.53429728|  PASSED
          sts_serial|  13|    100000|     100|0.00641005|  PASSED
          sts_serial|  14|    100000|     100|0.16616756|  PASSED
          sts_serial|  14|    100000|     100|0.24021940|  PASSED
          sts_serial|  15|    100000|     100|0.16042510|  PASSED
          sts_serial|  15|    100000|     100|0.49919589|  PASSED
          sts_serial|  16|    100000|     100|0.43152908|  PASSED
          sts_serial|  16|    100000|     100|0.36728928|  PASSED
         rgb_bitdist|   1|    100000|     100|0.02239519|  PASSED
         rgb_bitdist|   2|    100000|     100|0.58361825|  PASSED
         rgb_bitdist|   3|    100000|     100|0.08807530|  PASSED
         rgb_bitdist|   4|    100000|     100|0.73279568|  PASSED
         rgb_bitdist|   5|    100000|     100|0.99996183|   WEAK
         rgb_bitdist|   6|    100000|     100|0.96210643|  PASSED
         rgb_bitdist|   7|    100000|     100|0.32543609|  PASSED
         rgb_bitdist|   8|    100000|     100|0.39579455|  PASSED
         rgb_bitdist|   9|    100000|     100|0.24461591|  PASSED
         rgb_bitdist|  10|    100000|     100|0.40187454|  PASSED
         rgb_bitdist|  11|    100000|     100|0.53673153|  PASSED
         rgb_bitdist|  12|    100000|     100|0.95133455|  PASSED
rgb_minimum_distance|   2|     10000|    1000|0.98478922|  PASSED
rgb_minimum_distance|   3|     10000|    1000|0.87908124|  PASSED
rgb_minimum_distance|   4|     10000|    1000|0.37267409|  PASSED
rgb_minimum_distance|   5|     10000|    1000|0.78067922|  PASSED
    rgb_permutations|   2|    100000|     100|0.49881658|  PASSED
    rgb_permutations|   3|    100000|     100|0.88797443|  PASSED
    rgb_permutations|   4|    100000|     100|0.43456508|  PASSED
    rgb_permutations|   5|    100000|     100|0.16153464|  PASSED
      rgb_lagged_sum|   0|   1000000|     100|0.79295854|  PASSED
      rgb_lagged_sum|   1|   1000000|     100|0.28926952|  PASSED
      rgb_lagged_sum|   2|   1000000|     100|0.92503340|  PASSED
      rgb_lagged_sum|   3|   1000000|     100|0.12883165|  PASSED
      rgb_lagged_sum|   4|   1000000|     100|0.70274059|  PASSED
      rgb_lagged_sum|   5|   1000000|     100|0.08022014|  PASSED
      rgb_lagged_sum|   6|   1000000|     100|0.35855876|  PASSED
      rgb_lagged_sum|   7|   1000000|     100|0.84242861|  PASSED
      rgb_lagged_sum|   8|   1000000|     100|0.50130893|  PASSED
      rgb_lagged_sum|   9|   1000000|     100|0.99251362|  PASSED
      rgb_lagged_sum|  10|   1000000|     100|0.74762338|  PASSED
      rgb_lagged_sum|  11|   1000000|     100|0.85399491|  PASSED
      rgb_lagged_sum|  12|   1000000|     100|0.89342413|  PASSED
      rgb_lagged_sum|  13|   1000000|     100|0.64443694|  PASSED
      rgb_lagged_sum|  14|   1000000|     100|0.62904855|  PASSED
      rgb_lagged_sum|  15|   1000000|     100|0.71769662|  PASSED
      rgb_lagged_sum|  16|   1000000|     100|0.95911295|  PASSED
      rgb_lagged_sum|  17|   1000000|     100|0.44532357|  PASSED
      rgb_lagged_sum|  18|   1000000|     100|0.13809688|  PASSED
      rgb_lagged_sum|  19|   1000000|     100|0.26334968|  PASSED
      rgb_lagged_sum|  20|   1000000|     100|0.90119276|  PASSED
      rgb_lagged_sum|  21|   1000000|     100|0.30152629|  PASSED
      rgb_lagged_sum|  22|   1000000|     100|0.44729972|  PASSED
      rgb_lagged_sum|  23|   1000000|     100|0.53614264|  PASSED
      rgb_lagged_sum|  24|   1000000|     100|0.33034569|  PASSED
      rgb_lagged_sum|  25|   1000000|     100|0.23482943|  PASSED
      rgb_lagged_sum|  26|   1000000|     100|0.33074980|  PASSED
      rgb_lagged_sum|  27|   1000000|     100|0.96766489|  PASSED
      rgb_lagged_sum|  28|   1000000|     100|0.67306281|  PASSED
      rgb_lagged_sum|  29|   1000000|     100|0.12594446|  PASSED
      rgb_lagged_sum|  30|   1000000|     100|0.30183239|  PASSED
      rgb_lagged_sum|  31|   1000000|     100|0.75811690|  PASSED
      rgb_lagged_sum|  32|   1000000|     100|0.63057877|  PASSED
     rgb_kstest_test|   0|     10000|    1000|0.58090425|  PASSED
     dab_bytedistrib|   0|  51200000|       1|0.19454820|  PASSED
             dab_dct| 256|     50000|       1|0.80771330|  PASSED
Preparing to run test 207.  ntuple = 0
        dab_filltree|  32|  15000000|       1|0.96552752|  PASSED
        dab_filltree|  32|  15000000|       1|0.46809622|  PASSED
Preparing to run test 208.  ntuple = 0
       dab_filltree2|   0|   5000000|       1|0.39588287|  PASSED
       dab_filltree2|   1|   5000000|       1|0.28902966|  PASSED
Preparing to run test 209.  ntuple = 0
        dab_monobit2|  12|  65000000|       1|1.00000000|  FAILED
#=============================================================================#
# Summary: PASSED=111, WEAK=2, FAILED=1                                       #
#          235,031.281 MB of random data created with 131.180 MB/sec          #
#=============================================================================#
#=============================================================================#
# Runtime: 0:29:51                                                            #
#=============================================================================#
*/
