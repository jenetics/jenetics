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

import java.util.List;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
final class ArrayISeq<T> extends ArraySeq<T> implements ISeq<T> {
	private static final long serialVersionUID = 1L;

	
	ArrayISeq(final ArrayRef array, final int start, final int end) {
		super(array, start, end);
	}
	
	@Override
	public List<T> asList() {
		return new ArraySeqList<T>(this);
	}
	
	@Override
	public ISeq<T> subSeq(final int start, final int end) {
		checkIndex(start, end);
		return new ArrayISeq<T>(_array, start + _start, end + _start);
	}

	@Override
	public ISeq<T> subSeq(final int start) {
		return subSeq(start, length());
	}
	
	@Override
	public MSeq<T> copy() {
		return new Array<T>(new ArrayRef(toArray()), 0, length());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A> ISeq<A> upcast(final ISeq<? extends A> seq) {
		return (ISeq<A>)seq;
	}	
	
}

