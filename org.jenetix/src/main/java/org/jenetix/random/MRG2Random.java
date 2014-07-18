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
package org.jenetix.random;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;
import java.util.Optional;
import java.util.Random;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.util.Random32;
import org.jenetics.util.math;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__! &mdash; <em>$Date: 2014-07-18 $</em>
 */
public class MRG2Random extends Random32 {

	private static final long serialVersionUID = 1L;

	public static final class Param implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * LEcuyer 1 parameters: a = 1498809829; b = 1160990996
		 */
		public static final Param LECUYER1 = new Param(1498809829, 1160990996);

		/**
		 * LEcuyer 2 parameters: a = 46325; b = 1084587
		 */
		public static final Param LECUYER2 = new Param(46325, 1084587);

		/**
		 * The default PRNG parameters: LECUYER1
		 */
		public static final Param DEFAULT = LECUYER1;

		public final int a;
		public final int b;

		public Param(final int a, final int b) {
			this.a = a;
			this.b = b;
 		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(a)
				.and(b).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(param ->
				eq(a, param.a) &&
				eq(b, param.b)
			);
		}

		@Override
		public String toString() {
			return format("Param[%d, %d", a, b);
		}
	}

	private static final class State implements Serializable {
		private static final long serialVersionUID = 1L;

		int r1;
		int r2;

		State(final long seed) {
			setSeed(seed);
		}

		State() {
			this(math.random.seed());
		}

		void setSeed(final long s) {
			long t = s%Integer.MAX_VALUE;
			if (t < 0) {
				t += Integer.MAX_VALUE;
			}

			r1 = (int)t;
			r2 = 1;
		}
	}

	private final Param _param;
	private final State _state;

	public MRG2Random(final Param param, final long seed) {
		_param = requireNonNull(param);
		_state = new State(seed);
	}

	public MRG2Random(final Param param) {
		this(param, math.random.seed());
	}

	public MRG2Random(final long seed) {
		this(Param.DEFAULT, seed);
	}

	public MRG2Random() {
		this(Param.DEFAULT, math.random.seed());
	}

	@Override
	public int nextInt() {
		step();
		return _state.r1;
	}

	public void step() {
		final long t = (long)_param.a*(long)_state.r1 +
						(long)_param.b*(long)_state.r2;

		_state.r2 = _state.r1;
		_state.r1 = (int)(t%Integer.MAX_VALUE); //int_math::modulo<modulus, 2>(t);
	}

	@Override
	public void setSeed(final long seed) {
		Optional.ofNullable(_state).ifPresent(s -> s.setSeed(seed));
	}

	public static void main(final String[] args) {
		final Random random = new MRG2Random(32344);
		for (int i = 0; i < 20; ++i) {
			System.out.println(random.nextInt());
		}
	}
}
