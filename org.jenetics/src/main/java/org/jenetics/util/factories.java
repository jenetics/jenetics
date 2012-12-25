/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

/**
 * Contains factory (methods) for some 'primitive' types.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2012-12-14 $</em>
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
