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

import org.jenetics.util.Random64;
import org.jenetics.util.math;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__! &mdash; <em>$Date: 2014-07-23 $</em>
 */
public class LCG64Random extends Random64 {

	private static final long serialVersionUID = 1L;


	/**
	 * The parameter class of this random engine.
	 */
	public static final class Param implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * Default parameters: a = 0xFBD19FBBC5C07FF5L; b = 0
		 */
		public static final Param DEFAULT =
			new Param(0xFBD19FBBC5C07FF5L, 0L);

		/**
		 * LEcuyer 1 parameters: a = 0x27BB2EE687B0B0FDL; b = 0
		 */
		public static final Param LECUYER1 =
			new Param(0x27BB2EE687B0B0FDL, 0L);

		/**
		 * LEcuyer 2 parameters: a = 0x2C6FE96EE78B6955L; b = 0
		 */
		public static final Param LECUYER2 =
			new Param(0x2C6FE96EE78B6955L, 0L);

		/**
		 * LEcuyer 3 parameters: a = 0x369DEA0F31A53F85L; b = 0
		 */
		public static final Param LECUYER3 =
			new Param(0x369DEA0F31A53F85L, 0L);


		public final long a;
		public final long b;

		public Param(final long a, final long b) {
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
			return format("Param[%d, %d]", a, b);
		}

	}

	private final static class State implements Serializable {
		private static final long serialVersionUID = 1L;

		long _r;

		State(final long seed) {
			setSeed(seed);
		}

		void setSeed(final long seed) {
			_r = seed;
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass()).and(_r).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(state -> state._r == _r);
		}

		@Override
		public String toString() {
			return format("State[%d]", _r);
		}
	}

	private final Param _param;
	private final State _state;

	public LCG64Random(final Param param, final long seed) {
		_param = requireNonNull(param);
		_state = new State(seed);
	}

	public LCG64Random(final Param param) {
		this(param, math.random.seed());
	}

	public LCG64Random(final long seed) {
		this(Param.DEFAULT, seed);
	}

	public LCG64Random() {
		this(Param.DEFAULT, math.random.seed());
	}

	@Override
	public long nextLong() {
		step();
		return _state._r;
	}

	private void step() {
		_state._r = _param.a*_state._r + _param.b;
	}

	@Override
	public void setSeed(final long seed) {
		if (_state != null) _state.setSeed(seed);
	}

	@Override
	public String toString() {
		return format("%s[%s, %s]", getClass().getSimpleName(), _param, _state);
	}

}
