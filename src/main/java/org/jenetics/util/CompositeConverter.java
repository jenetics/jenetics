/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
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

/**
 * This class allows to build transitive converters.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class CompositeConverter<A, B, C> implements Converter<A, C> {
	private final Converter<A, B> _first;
	private final Converter<B, C> _second;
	
	/**
	 * Create a new transitive converter with the given converters.
	 * 
	 * @param first first converter
	 * @param second second converter
	 * @throws NullPointerException if one of the converters is {@code null}.
	 */
	public CompositeConverter(
		final Converter<A, B> first,
		final Converter<B, C> second
	) {
		_first = nonNull(first);
		_second = nonNull(second);
	}
	
	@Override
	public C convert(final A value) {
		return _second.convert(_first.convert(value));
	}
	
	@Override
	public String toString() {
		return String.format(
				"%s[%s, %s]", 
				getClass().getSimpleName(), _first, _second
			);
	}
	
	public static <A, B, C> Converter<A, C> valueOf(
		final Converter<A, B> c1,
		final Converter<B, C> c2
	) {
		return new CompositeConverter<>(c1, c2);
	}

	public static <A, B, C, D> Converter<A, D> valueOf(
		final Converter<A, B> c1,
		final Converter<B, C> c2,
		final Converter<C, D> c3
	) {
		return valueOf(valueOf(c1, c2), c3);
	}
	
	public static <A, B, C, D, E> Converter<A, E> valueOf(
		final Converter<A, B> c1,
		final Converter<B, C> c2,
		final Converter<C, D> c3,
		final Converter<D, E> c4
	) {
		return valueOf(valueOf(valueOf(c1, c2), c3), c4);
	}
	
	public static <A, B, C, D, E, F> Converter<A, F> valueOf(
		final Converter<A, B> c1,
		final Converter<B, C> c2,
		final Converter<C, D> c3,
		final Converter<D, E> c4,
		final Converter<E, F> c5
	) {
		return valueOf(valueOf(valueOf(valueOf(c1, c2), c3), c4), c5);
	}
	
}



