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
 * Mutable sequence view.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public interface MSeq<T> extends Seq<T> {
	
	/**
	 * Set the {@code value} at the given {@code index}.
	 * 
	 * @param index the index of the new value.
	 * @param value the new value.
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range 
	 * 		  {@code (index < 0 || index >= size())}.
	 * @throws UnsupportedOperationException if this sequence is sealed 
	 * 		  ({@code isSealed() == true}).
	 */
	public void set(final int index, final T value);
	
	/**
	 * Return whether this sequence is sealed (immutable) or not.
	 * 
	 * @return {@code false} if this sequence can be changed, {@code true} 
	 *         otherwise.
	 */
	public boolean isSealed();
	
	/**
	 * Seal this mutable sequence and return an immutable view of this
	 * sequence.
	 * 
	 * @return an immutable view of this sequence.
	 */
	public ISeq<T> seal();
	
}