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

import javolution.lang.Immutable;

/**
 * Immutable sequence view. This interface allows to create a mutable copy of 
 * this immutable sequence.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public interface ISeq<T> 
	extends 
		Seq<T>,
		Copyable<MSeq<T>>,
		Immutable 
{
			
	/**
	 * <p>
	 * Helper method for up-casting an given immutable sequence. This allows you 
	 * to assign this sequence to an sequence where the element type is a super 
	 * type of {@code T}.
	 * </p>
	 * [code]
	 *     ISeq<Double> da = new Array<Double>(Arrays.asList(0.0, 1.0, 2.0)).seal();
	 *     ISeq<Number> na = da.upcast(da);
	 *     ISeq<Object>; oa = na.upcast(na);
	 *     oa = da.upcast(da);
	 * [/code]
	 * 
	 * @param seq the sequence to cast.
	 * @return the up-casted sequence.
	 */
	public <A> ISeq<A> upcast(final ISeq<? extends A> seq);
	
	/**
	 * Return a shallow copy of this array. The array elements are not cloned.
	 * The copied array is not sealed. If the array is a sub-array (created
	 * with the {@link #subSeq(int, int)} method, only the sub-array-part is
	 * copied.
	 * 
	 * @return a shallow copy of this array.
	 */
	@Override
	public MSeq<T> copy();
	
}





