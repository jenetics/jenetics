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

/**
 * Contains factory (methods) for some 'primitive' types.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2013-05-25 $</em>
 */
public final class factories extends StaticObject {
	private factories() {}

	/**
	 * Return an integer factory which creates an integer sequence starting with
	 * zero an with step one.
	 *
	 * @return an integer factory.
	 */
	public static Factory<Integer> Int() {
		return Int(1);
	}

	/**
	 * Return an integer factory which creates an integer sequence starting with
	 * zero an with the given {@code step}.
	 *
	 * @param step the gap between the generated integers.
	 * @return an integer factory.
	 */
	public static Factory<Integer> Int(final int step) {
		return Int(0, step);
	}

	/**
	 * Return an integer factory which creates an integer sequence starting with
	 * {@code start} an with the given {@code step}.
	 *
	 * @param step the gap between the generated integers.
	 * @return an integer factory.
	 */
	public static Factory<Integer> Int(final int start, final int step) {
		return new Factory<Integer>() {
			private int _value = start;

			@Override
			public Integer newInstance() {
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
