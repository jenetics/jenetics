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

import static org.jenetics.util.validation.nonNull;

/**
 * Adapts an accumulator from type {@code A} to type {@code B}.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class AccumulatorAdapter<A, B> extends AdaptableAccumulator<B> {
	private final Accumulator<? super A> _adoptee;
	private final Converter<? super B, ? extends A> _converter;
	
	/**
	 * Create an new AccumulatorAdapter. 
	 * 
	 * @param adoptee the original, adapted, Accumulator.
	 * @param converter the converter needed to convert from type {@code A} to 
	 *        type {@code B}.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public AccumulatorAdapter(
		final Accumulator<? super A> adoptee, 
		final Converter<? super B, ? extends A> converter
	) {
		_adoptee = nonNull(adoptee);
		_converter = nonNull(converter);
	}
	
	/**
	 * Return the adapted Accumulator.
	 * 
	 * @return the adapted Accumulator.
	 */
	public Accumulator<? super A> getAccumulator() {
		return _adoptee;
	}
	
	/**
	 * Return the needed converter from type {@code A} to  type {@code B}.
	 * 
	 * @return the needed converter from type {@code A} to  type {@code B}.
	 */
	public Converter<? super B, ? extends A> getConverter() {
		return _converter;
	}
	
	@Override
	public void accumulate(final B value) {
		_adoptee.accumulate(_converter.convert(value));
		++_samples;
	}
	
	/**
	 * Create an new AccumulatorAdapter. 
	 * 
	 * @param adoptee the original, adapted, Accumulator.
	 * @param converter the converter needed to convert from type {@code A} to 
	 *        type {@code B}.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public static <A, B> AccumulatorAdapter<A, B> valueOf(
			final Accumulator<? super A> adoptee, 
			final Converter<? super B, ? extends A> converter
	) {
		return new AccumulatorAdapter<A, B>(adoptee, converter);
	}
	
	@Override
	public String toString() {
		return String.format(
				"%s[a=%s, c=%s]", 
				getClass().getSimpleName(), _adoptee, _converter
			);
	}

}
