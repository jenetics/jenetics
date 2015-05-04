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
package org.jenetics.util;

import java.util.function.Supplier;

import org.jenetics.internal.util.require;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public final class factories {
	private factories() {require.noInstance();}

	/**
	 * Return an integer factory which creates an integer sequence starting with
	 * zero an with step one.
	 *
	 * @return an integer factory.
	 */
	public static Supplier<Integer> Int() {
		return Int(1);
	}

	/**
	 * Return an integer factory which creates an integer sequence starting with
	 * zero an with the given {@code step}.
	 *
	 * @param step the gap between the generated integers.
	 * @return an integer factory.
	 */
	public static Supplier<Integer> Int(final int step) {
		return Int(0, step);
	}

	/**
	 * Return an integer factory which creates an integer sequence starting with
	 * {@code start} an with the given {@code step}.
	 *
	 * @param step the gap between the generated integers.
	 * @return an integer factory.
	 */
	public static Supplier<Integer> Int(final int start, final int step) {
		return new Supplier<Integer>() {
			private int _value = start;

			@Override
			public Integer get() {
				return next();
			}

			private int next() {
				final int next = _value;
				_value += step;
				return next;
			}
		};
	}

}
