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
 * @version !__version__! &mdash; <em>$Date: 2014-07-21 $</em>
 */
public class MRG5Random  extends Random32 {

	private static final long serialVersionUID = 1L;

	private static final long MODULUS = 0xFFFFFFFFL;
	private static final ModularArithmetic _modulus =
		new ModularArithmetic(MODULUS);

	/**
	 * The parameter class of this random engine.
	 */
	public static final class Param implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * LEcuyer 1 parameters:
		 *     a1 = 107374182
		 *     a2 = 0
		 *     a3 = 0
		 *     a4 = 0
		 *     a5 = 104480
		 */
		public static final Param LECUYER1 =
			new Param(107374182, 0, 0, 0, 104480);


		/**
		 * The default PRNG parameters: LECUYER1
		 */
		public static final Param DEFAULT = LECUYER1;

		public final long a1;
		public final long a2;
		public final long a3;
		public final long a4;
		public final long a5;

		public Param(
			final int a1,
			final int a2,
			final int a3,
			final int a4,
			final int a5
		) {
			this.a1 = a1;
			this.a2 = a2;
			this.a3 = a3;
			this.a4 = a4;
			this.a5 = a5;
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(a1)
				.and(a2)
				.and(a3)
				.and(a4)
				.and(a5).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(param ->
				eq(a1, param.a1) &&
				eq(a2, param.a2) &&
				eq(a3, param.a3) &&
				eq(a4, param.a4) &&
				eq(a5, param.a5)
			);
		}

		@Override
		public String toString() {
			return format("Param[%d, %d, %d, %d, %d]", a1, a2, a3, a4, a5);
		}
	}

	private static final class State implements Serializable {
		private static final long serialVersionUID = 1L;

		int _r1;
		int _r2;
		int _r3;
		int _r4;
		int _r5;

		State(final long seed) {
			setSeed(seed);
		}

		void setSeed(final long seed) {
			long t = seed%MODULUS;
			if (t < 0) t += MODULUS;

			_r1 = (int)t;
			_r2 = 1;
			_r3 = 1;
			_r4 = 1;
			_r5 = 1;
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(_r1)
				.and(_r2)
				.and(_r3)
				.and(_r4)
				.and(_r5).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(state ->
				eq(_r1, state._r1) &&
				eq(_r2, state._r2) &&
				eq(_r3, state._r3) &&
				eq(_r4, state._r4) &&
				eq(_r5, state._r5)
			);
		}

		@Override
		public String toString() {
			return format("State[%d, %d, %d, %d, %d]", _r1, _r2, _r3, _r4, _r5);
		}
	}

	private final Param _param;
	private final State _state;

	public MRG5Random(final Param param, final long seed) {
		_param = requireNonNull(param);
		_state = new State(seed);
	}

	public MRG5Random(final Param param) {
		this(param, math.random.seed());
	}

	public MRG5Random(final long seed) {
		this(Param.DEFAULT, seed);
	}

	public MRG5Random() {
		this(Param.DEFAULT, math.random.seed());
	}

	@Override
	public int nextInt() {
		step();
		return _state._r1;
	}

	public void step() {
		final long t = _modulus.add(
			_param.a1*_state._r1,
			_param.a2*_state._r2,
			_param.a3*_state._r3,
			_param.a4*_state._r4,
			_param.a5*_state._r5
		);

		_state._r5 = _state._r4;
		_state._r4 = _state._r3;
		_state._r3 = _state._r2;
		_state._r2 = _state._r1;
		_state._r1 = (int)t;
	}

	private static long add(final long a,final long b) {
		return (a%MODULUS + b%MODULUS)%MODULUS;
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

	public static void main(final String[] args) {
		MRG5Random random = new MRG5Random(124);
		for (int i = 0; i < 10; ++i)
		System.out.println(random.nextInt());
	}

}
