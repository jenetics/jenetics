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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import static org.jenetics.util.object.nonNull;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This class contains some short general purpose functions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-02-13 $</em>
 */
public final class functions extends StaticObject {
	private functions() {}

	public static <A, B, C> Function<A, C> compose(
		final Function<A, B> f1,
		final Function<B, C> f2
	) {
		nonNull(f1, "Function 1");
		nonNull(f2, "Function 2");

		return new Function<A, C>() {
			@Override public C apply(A value) {
				return f2.apply(f1.apply(value));
			}
		};
	}

	public static <A, B, C, D> Function<A, D> compose(
		final Function<A, B> f1,
		final Function<B, C> f2,
		final Function<C, D> f3
	) {
		return compose(compose(f1, f2), f3);
	}

	public static <A, B, C, D, E> Function<A, E> compose(
		final Function<A, B> f1,
		final Function<B, C> f2,
		final Function<C, D> f3,
		final Function<D, E> f4
	) {
		return compose(compose(compose(f1, f2), f3), f4);
	}

	public static <A, B, C, D, E, F> Function<A, F> compose(
		final Function<A, B> f1,
		final Function<B, C> f2,
		final Function<C, D> f3,
		final Function<D, E> f4,
		final Function<E, F> f5
	) {
		return compose(compose(compose(compose(f1, f2), f3), f4), f5);
	}

}






