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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class IArray<T> extends ArrayBase<T> implements ISeq<T> {
	private static final long serialVersionUID = 1L;

	private final Array<T> _adoptee;
	
	IArray(final Array<T> array) {
		super(array._array, array._start, array._end, true);
		_adoptee = array;
	}
	
	void defense() {
		final Object[] array = new Object[length()];
		System.arraycopy(_array, _start, array, 0, length());
		_array = array;
	}
	
	@Override
	public ISeq<T> subSeq(final int start, final int end) {
		if (start < 0 || end > length() || start > end) {
			throw new ArrayIndexOutOfBoundsException(String.format(
				"Invalid index range: [%d, %s)", start, end
			));
		}
		
		return new IArray<T>(_adoptee.subSeq(start, end));
	}

	@Override
	public ISeq<T> subSeq(final int start) {
		return subSeq(start, length());
	}
	
	@Override
	public MSeq<T> copy() {
		return _adoptee.copy();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A> ISeq<A> upcast(final ISeq<? extends A> seq) {
		return (ISeq<A>)seq;
	}	
	
}