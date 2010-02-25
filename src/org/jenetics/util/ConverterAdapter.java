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
 * This class allows to build transitive converters.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class ConverterAdapter<A, B, C> implements Converter<A, C> {
	private final Converter<A, B> _first;
	private final Converter<B, C> _second;
	
	public ConverterAdapter(
		final Converter<A, B> first, 
		final Converter<B, C> second
	) {
		_first = Validator.nonNull(first);
		_second = Validator.nonNull(second);
	}
	
	@Override
	public C convert(final A value) {
		return _second.convert(_first.convert(value));
	}
	
	public static <A, B, C> Converter<A, C> valueOf(
		final Converter<A, B> first,
		final Converter<B, C> second
	) {
		return new ConverterAdapter<A, B, C>(first, second);
	}

	public static <A, B, C, D> Converter<A, D> valueOf(
		final Converter<A, B> first,
		final Converter<B, C> second,
		final Converter<C, D> third
	) {
		return new ConverterAdapter<A, C, D>(
				new ConverterAdapter<A, B, C>(first, second), third
			);
	}
	
}



