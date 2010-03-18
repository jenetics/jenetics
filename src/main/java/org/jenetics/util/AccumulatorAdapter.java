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
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: AccumulatorAdapter.java 341 2010-02-19 23:35:25Z fwilhelm $
 */
public class AccumulatorAdapter<A, B> implements Accumulator<B> {
	private final Accumulator<? super A> _adoptee;
	private final Converter<? super B, ? extends A> _converter;
	
	public AccumulatorAdapter(
		final Accumulator<? super A> adoptee, 
		final Converter<? super B, ? extends A> converter
	) {
		_adoptee = Validator.nonNull(adoptee);
		_converter = Validator.nonNull(converter);
	}
	
	public Accumulator<? super A> getAccumulator() {
		return _adoptee;
	}
	
	public Converter<? super B, ? extends A> getConverter() {
		return _converter;
	}
	
	@Override
	public void accumulate(final B value) {
		_adoptee.accumulate(_converter.convert(value));
	}
	
	public static <A, B> AccumulatorAdapter<A, B> valueOf(
			final Accumulator<? super A> adoptee, 
			final Converter<? super B, ? extends A> converter
	) {
		return new AccumulatorAdapter<A, B>(adoptee, converter);
	}

}
