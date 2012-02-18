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
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class ArrayISeq<T> extends ArraySeq<T> implements ISeq<T> {
	private static final long serialVersionUID = 1L;

	
	ArrayISeq(final ArrayRef array, final int start, final int end) {
		super(array, start, end);
	}
	
	@Override
	public ISeq<T> subSeq(final int start, final int end) {
		checkIndex(start, end);
		return new ArrayISeq<>(_array, start + _start, end + _start);
	}

	@Override
	public ISeq<T> subSeq(final int start) {
		return subSeq(start, length());
	}
	
	@Override
	public <B> ArrayISeq<B> map(final Function<? super T, ? extends B> converter) {
		nonNull(converter, "Converter");
		
		final int length = length();
		final ArrayISeq<B> result = new ArrayISeq<>(new ArrayRef(length), 0, length);
		assert (result._array.data.length == length);
		
		for (int i = length; --i >= 0;) {
			@SuppressWarnings("unchecked")
			final T value = (T)_array.data[i + _start];
			result._array.data[i] = converter.apply(value);
		}
		return result;
	}
	
	@Override
	public MSeq<T> copy() {
		return new Array<>(new ArrayRef(toArray()), 0, length());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A> ISeq<A> upcast(final ISeq<? extends A> seq) {
		return (ISeq<A>)seq;
	}	
	
}

