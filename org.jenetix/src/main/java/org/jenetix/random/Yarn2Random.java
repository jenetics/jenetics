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

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.util.Random32;
import org.jenetics.util.math;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__! &mdash; <em>$Date: 2014-07-23 $</em>
 */
public class Yarn2Random extends Random32 {
	private static final long serialVersionUID = 1L;

	private static final long GEN = 123567893;

	/**
	 * The parameter class of this random engine.
	 */
	public static final class Param implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * LEcuyer 1 parameters: a1 = 1498809829; a2 = 1160990996
		 */
		public static final Param LECUYER1 = new Param(1498809829, 1160990996);

		/**
		 * LEcuyer 2 parameters: a1 = 46325; a2 = 1084587
		 */
		public static final Param LECUYER2 = new Param(46325, 1084587);

		/**
		 * The default PRNG parameters: LECUYER1
		 */
		public static final Param DEFAULT = LECUYER1;

		public final long a1;
		public final long a2;

		public Param(final int a1, final int a2) {
			this.a1 = a1;
			this.a2 = a2;
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(a1)
				.and(a2).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(param ->
					eq(a1, param.a1) &&
						eq(a2, param.a2)
			);
		}

		@Override
		public String toString() {
			return format("Param[%d, %d]", a1, a2);
		}
	}

	private static final class State implements Serializable {
		private static final long serialVersionUID = 1L;

		long _r1;
		long _r2;

		State(final long seed) {
			setSeed(seed);
		}

		void setSeed(final long seed) {
			long t = modulus.mod(seed);
			if (t < 0) t += modulus.VALUE;

			_r1 = t;
			_r2 = 1;
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(_r1)
				.and(_r2).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(state ->
					eq(_r1, state._r1) &&
						eq(_r2, state._r2)
			);
		}

		@Override
		public String toString() {
			return format("State[%d, %d]", _r1, _r2);
		}
	}

	private final Param _param;
	private final State _state;

	public Yarn2Random(final Param param, final long seed) {
		_param = requireNonNull(param);
		_state = new State(seed);
	}

	public Yarn2Random(final Param param) {
		this(param, math.random.seed());
	}

	public Yarn2Random(final long seed) {
		this(Param.DEFAULT, seed);
	}

	public Yarn2Random() {
		this(Param.DEFAULT, math.random.seed());
	}

	@Override
	public int nextInt() {
		step();

		if (_state._r1 == 0) {
			return 0;
		}

		long n = _state._r1;
		long p = 1;
		long t = GEN;
		while ( n > 0) {
			if ((n&0x1) == 0x1) {
				p = modulus.mod(p*t);
			}
			t = modulus.mod(t*t);
			n /= 2;
		}
		return (int)p;
	}

	public void step() {
		final long t = modulus.add(
			_param.a1*_state._r1,
			_param.a2*_state._r2
		);

		_state._r2 = _state._r1;
		_state._r1 = t;
	}

	@Override
	public void setSeed(final long seed) {
		if (_state != null) _state.setSeed(seed);
	}

	public Param getParam() {
		return _param;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_param)
			.and(_state).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(random ->
				eq(_param, random._param) &&
					eq(_state, random._state)
		);
	}

	@Override
	public String toString() {
		return format("%s[%s, %s]", getClass().getSimpleName(), _param, _state);
	}
}
