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

import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public interface Sequence<T> extends Iterable<T> {

	/**
	 * Return the value at the given {@code index}.
	 * 
	 * @param index index of the element to return.
	 * @return the value at the given {@code index}.
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range 
	 * 		  {@code (index < 0 || index >= size())}.
	 */
	public T get(final int index);
	
	/**
	 * Return the length of this sequence. Once the array is created, the length
	 * can't be changed.
	 * 
	 * @return the length of this sequence.
	 */
	public int length();
	
	/**
	 * Return an iterator with the new type {@code B}.
	 * 
	 * @param <B> the component type of the returned type.
	 * @param converter the converter for converting from {@code T} to {@code B}.
	 * @return the iterator of the converted type.
	 * @throws NullPointerException if the given {@code converter} is {@code null}.
	 */
	public <B> Iterator<B> iterator(
		final Converter<? super T, ? extends B> converter
	);
	
	/**
	 * Iterates over this sequence as long as the given predicate returns 
	 * {@code true}. This method is more or less an  <i>alias</i> of the 
	 * {@link #indexOf(Predicate)} method. In some cases a call to a 
	 * {@code sequence.foreach()} method can express your intention much better 
	 * than a {@code array.indexOf()} call.
	 * 
	 * [code]
	 *     final Sequence<Integer> values = new Array<Integer>(Arrays.asList(1, 2, 3, 4, 5));
	 *     final AtomicInteger sum = new AtomicInteger(0);
	 *     values.foreach(new Predicate<Integer>() {
	 *         public boolean evaluate(final Integer value) {
	 *             sum.addAndGet(value);
	 *             return true;
	 *         }
	 *     });
	 *     System.out.println("Sum: " + sum);
	 * [/code]
	 * 
	 * @param predicate the predicate to apply.
	 * @return the index of the first element on which the given predicate 
	 * 		  returns {@code false}, or -1 if the predicate returns {@code true}
	 * 		  for every array element.
	 * @throws NullPointerException if the given {@code predicate} is 
	 * 		  {@code null}.
	 */
	public int foreach(final Predicate<? super T> predicate);
	
	/**
	 * Returns {@code true} if this sequence contains the specified element.
	 *
	 * @param element element whose presence in this array is to be tested. The
	 * 		 tested element can be {@code null}.
	 * @return {@code true} if this sequence contains the specified element
	 */
	public boolean contains(final Object element);
	
	/**
	 * Returns the index of the first occurrence of the specified element
	 * in this sequence, or -1 if this sequence does not contain the element.
	 * 
	 * @param element element to search for, can be {@code null}
	 * @return the index of the first occurrence of the specified element in
	 * 		   this sequence, or -1 if this sequence does not contain the element
	 */
	public int indexOf(final Object element);
	
	/**
	 * <p>
	 * Returns the index of the first element on which the given predicate 
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * sequence element.
	 * </p>
	 * [code]
	 * 	 // Finding index of first null value.
	 * 	 final int index = sequence.indexOf(new Predicates.Nil());
	 * 	 
	 * 	 // Assert of no null values.
	 * 	 assert (sequence.indexOf(new Predicates.Nil()) == -1);
	 * [/code]
	 * 
	 * @param predicate the search predicate.
	 * @return the index of the first element on which the given predicate 
	 * 		  returns {@code true}, or -1 if the predicate returns {@code false}
	 * 		  for every sequence element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 */
	public int indexOf(final Predicate<? super T> predicate);
	
	/**
	 * Returns the index of the last occurrence of the specified element
	 * in this sequence, or -1 if this sequence does not contain the element.
	 * 
	 * @param element element to search for, can be {@code null}
	 * @return the index of the last occurrence of the specified element in
	 * 		  this sequence, or -1 if this sequence does not contain the element
	 */
	public int lastIndexOf(final Object element);
	
	/**
	 * Returns the index of the last element on which the given predicate 
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * sequence element.
	 * 
	 * @param predicate the search predicate.
	 * @return the index of the last element on which the given predicate 
	 * 		  returns {@code true}, or -1 if the predicate returns false for 
	 * 		  every sequence element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 */
	public int lastIndexOf(final Predicate<? super T> predicate);
	
	/**
	 * Returns a fixed-size list backed by the specified sequence. (Changes to
	 * the returned list "write through" to the array.) The returned list is
	 * fixed size, serializable and implements {@link RandomAccess}.
	 *
	 * @return a list view of this sequence
	 */	
	public List<T> asList();
	
	/**
	 * Return an sequence containing all of the elements in this array in right 
	 * order. The returned sequence will be "safe" in that no references to it 
	 * are maintained by this sequence. (In other words, this method must allocate 
	 * a new array.) The caller is thus free to modify the returned array. 
	 * 
	 * @see java.util.Collection#toArray()
	 * 
	 * @return an array containing all of the elements in this list in right 
	 * 		   order
	 */
	public Object[] toArray();
	
	/**
	 * Return an array containing all of the elements in this sequence in right
	 * order; the runtime type of the returned array is that of the specified 
	 * array. If this sequence fits in the specified array, it is returned therein. 
	 * Otherwise, a new array is allocated with the runtime type of the specified 
	 * array and the length of this array.
	 * <p/>
	 * If this sequence fits in the specified array with room to spare (i.e., the 
	 * array has more elements than this array), the element in the array 
	 * immediately following the end of this array is set to null. (This is 
	 * useful in determining the length of the array only if the caller knows 
	 * that the list does not contain any null elements.) 
	 * 
	 * @see java.util.Collection#toArray(Object[])
	 * 
	 * @param array the array into which the elements of this array are to be 
	 * 		 stored, if it is big enough; otherwise, a new array of the same 
	 * 		 runtime type is allocated for this purpose. 
	 * @return an array containing the elements of this array
	 * @throws ArrayStoreException if the runtime type of the specified array is 
	 * 		  not a super type of the runtime type of every element in this array
	 * @throws NullPointerException if the given array is {@code null}.	
	 */
	public T[] toArray(final T[] array);
	
	/**
	 * Returns a view of the portion of this sequence between the specified 
	 * {@code start}, inclusive, and {@code end}, exclusive. (If {@code start} 
	 * and {@code end} are equal, the returned sequence has the length zero.) The 
	 * returned sequence is backed by this sequence, so non-structural changes in the 
	 * returned sequence are reflected in this array, and vice-versa.
	 * <p/>
	 * This method eliminates the need for explicit range operations (of the 
	 * sort that commonly exist for arrays). Any operation that expects an array 
	 * can be used as a range operation by passing an sub array view instead of 
	 * an whole array. E.g.:
	 * [code]
	 * 	 Array<?> copy = array.subArray(5, 7).copy();
	 * [/code]
	 * 
	 * @param start low end point (inclusive) of the sub array.
	 * @return a view of the specified range within this array.
	 * @throws ArrayIndexOutOfBoundsException for an illegal end point index value 
	 * 		  ({@code start < 0 || start > lenght()}).
	 */
	public Sequence<T> subArray(final int start);
	
	/**
	 * Returns a view of the portion of this sequence between the specified 
	 * {@code start}, inclusive, and {@code end}, exclusive. (If {@code start} 
	 * and {@code end} are equal, the returned sequence has the length zero.) The 
	 * returned sequence is backed by this sequence, so non-structural changes in the 
	 * returned sequence are reflected in this array, and vice-versa.
	 * <p/>
	 * This method eliminates the need for explicit range operations (of the 
	 * sort that commonly exist for arrays). Any operation that expects an array 
	 * can be used as a range operation by passing an sub array view instead of 
	 * an whole array. E.g.:
	 * [code]
	 * 	 Array<?> copy = array.subArray(5, 7).copy();
	 * [/code]
	 * 
	 * @param start low end point (inclusive) of the sub sequence.
	 * @param end high end point (exclusive) of the sub sequence.
	 * @return a view of the specified range within this sequence.
	 * @throws ArrayIndexOutOfBoundsException for an illegal end point index value 
	 * 		  ({@code start < 0 || end > length() || start > end}).
	 */
	public Sequence<T> subArray(final int start, final int end);
	
	/**
	 * Create a string representation of the given sequence.
	 * 
	 * @param prefix the prefix of the string representation; e.g {@code '['}.
	 * @param separator the separator of the array elements; e.g. {@code ','}.
	 * @param suffix the suffix of the string representation; e.g. {@code ']'}.
	 * @return the string representation of this sequence.
	 */
	public String toString(
			final String prefix, final String separator, final String suffix
		);
	
	
	/**
	 * The immutable part of a sequence.
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public interface Immutable<T> extends Sequence<T>, javolution.lang.Immutable {
		
		@Override
		public Immutable<T> subArray(final int start);
		
		@Override
		public Immutable<T> subArray(final int start, final int end);
		
		public Mutable<T> copy();
		
		/**
		 * <p>
		 * The {@code upcast} method returns an array of type {@code Array<? super T>} 
		 * instead of {@code Array<T>}. This allows you to assign this array to an 
		 * array where the element type is a super type of {@code T}.
		 * </p>
		 * [code]
		 *     Sequence.Immutable<Double> da = new Array<Double>(Arrays.asList(0.0, 1.0, 2.0)).seal();
		 *     Sequence.Immutable<Number> na = da.upcast();
		 *     Sequence.Immutable<Object>; oa = na.upcast();
		 *     oa = da.upcast();
		 * [/code]
		 * 
		 * This array must be {@code sealed} for an save <em>up-cast</em>, otherwise an 
		 * {@link UnsupportedOperationException} will be thrown. 
		 * 
		 * @return the up-casted array.
		 * @throws UnsupportedOperationException if this array is not {@code sealed}.
		 */
		public <A> Immutable<A> upcast(final Immutable<? extends A> seq);
		
	}
	
	/**
	 * The mutable view of a sequence.
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public interface Mutable<T> extends Sequence<T> {
		
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
		
		@Override
		public Mutable<T> subArray(final int start);
		
		@Override
		public Mutable<T> subArray(final int start, final int end);
		
		public Immutable<T> seal();
		
	}
	
}
