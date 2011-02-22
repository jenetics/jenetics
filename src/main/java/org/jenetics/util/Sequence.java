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

import java.util.List;
import java.util.ListIterator;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public interface Sequence<T> extends Iterable<T> {

	public T get(final int index);
	
	public int length();
	
	@Override
	public ListIterator<T> iterator();
	
	public int foreach(final Predicate<? super T> predicate);
	
	public boolean contains(final Object element);
	
	public int indexOf(final Object element);
	
	public int lastIndexOf(final Object element);
	
	public int indexOf(final Predicate<? super T> predicate);
	
	public List<T> asList();
	
	public Object[] toArray();
	
	public T[] toArray(final T[] array);
	
	public String toString(
			final String prefix, final String separator, final String suffix
		);
	
	
	public interface Immutable<T> extends Sequence<T>, javolution.lang.Immutable {
		
		public Immutable<T> subArray(final int start);
		
		public Immutable<T> subArray(final int start, final int end);
		
		public Mutable<T> copy();
		
		public Immutable<? super T> upcast();
		
	}
	
	public interface Mutable<T> extends Sequence<T> {
		
		public void set(final int index, final T value);
		
		public Mutable<T> subArray(final int start);
		
		public Mutable<T> subArray(final int start, final int end);
		
		public Immutable<T> seal();
		
	}
	
}
