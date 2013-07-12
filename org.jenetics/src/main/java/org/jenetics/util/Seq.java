/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
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
 * General interface for a ordered, fixed sized, object sequence.
 * <br/>
 * Use the {@link #asList()} method to work together with the
 * <a href="http://download.oracle.com/javase/6/docs/technotes/guides/collections/index.html">
 * Java Collection Framework</a>.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.3 &mdash; <em>$Date: 2013-06-03 $</em>
 */
public interface Seq<T> extends Iterable<T> {

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index index of the element to return.
	 * @return the value at the given {@code index}.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *          {@code (index < 0 || index >= size())}.
	 */
	public T get(final int index);

	/**
	 * Return the length of this sequence. Once the sequence is created, the
	 * length can't be changed.
	 *
	 * @return the length of this sequence.
	 */
	public int length();

	/**
	 * Return an iterator with the new type {@code B}.
	 *
	 * @param <B> the component type of the returned type.
	 * @param mapper the converter for converting from {@code T} to {@code B}.
	 * @return the iterator of the converted type.
	 * @throws NullPointerException if the given {@code converter} is {@code null}.
	 */
	public <B> Iterator<B> iterator(
		final Function<? super T, ? extends B> mapper
	);

	/**
	 * @deprecated Align the naming with the upcomming JDK 1.8 release. Use
	 *             {@link #forEach(Function)} instead.
	 */
	@Deprecated
	public <R> void foreach(final Function<? super T, ? extends R> function);

	/**
	 * Applies a {@code function} to all elements of this sequence.
	 *
	 * @param function the function to apply to the elements.
	 * @throws NullPointerException if the given {@code function} is
	 *          {@code null}.
	 */
	public <R> void forEach(final Function<? super T, ? extends R> function);

	/**
	 * @deprecated Align the naming with the upcomming JDK 1.8 release. Use
	 *             {@link #forAll(Function)} instead.
	 */
	@Deprecated
	public boolean forall(final Function<? super T, Boolean> predicate);

	/**
	 * Tests whether a predicate holds for all elements of this sequence.
	 *
	 * @param predicate the predicate to use to test the elements.
	 * @return {@code true} if the given predicate p holds for all elements of
	 *          this sequence, {@code false} otherwise.
	 * @throws NullPointerException if the given {@code predicate} is
	 *          {@code null}.
	 */
	public boolean forAll(final Function<? super T, Boolean> predicate);

	/**
	 * Returns {@code true} if this sequence contains the specified element.
	 *
	 * @param element element whose presence in this sequence is to be tested.
	 *        The tested element can be {@code null}.
	 * @return {@code true} if this sequence contains the specified element
	 */
	public boolean contains(final Object element);

	/**
	 * Returns the index of the first occurrence of the specified element
	 * in this sequence, or -1 if this sequence does not contain the element.
	 *
	 * @param element element to search for, can be {@code null}
	 * @return the index of the first occurrence of the specified element in
	 *          this sequence, or -1 if this sequence does not contain the element
	 */
	public int indexOf(final Object element);

	/**
	 * Returns the index of the first occurrence of the specified element
	 * in this sequence, or -1 if this sequence does not contain the element.
	 *
	 * @param element element to search for, can be {@code null}
	 * @param start the start index (inclusively) for the element search.
	 * @return the index of the first occurrence of the specified element in
	 *          this sequence, or -1 if this sequence does not contain the element
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || start > length()}).
	 */
	public int indexOf(final Object element, final int start);

	/**
	 * Returns the index of the first occurrence of the specified element
	 * in this sequence, or -1 if this sequence does not contain the element.
	 *
	 * @param element element to search for, can be {@code null}
	 * @param start the start index (inclusively) for the element search.
	 * @param end the end index (exclusively) for the element search.
	 * @return the index of the first occurrence of the specified element in
	 *          this sequence, or -1 if this sequence does not contain the element
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || end > length() || start > end}).
	 */
	public int indexOf(final Object element, final int start, final int end);

	/**
	 * <p>
	 * Returns the index of the first element on which the given predicate
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * sequence element.
	 * </p>
	 * [code]
	 * // Finding index of first null value.
	 * final int index = seq.indexOf(new Predicates.Nil());
	 *
	 * // Assert of no null values.
	 * assert (sequence.indexOf(new Predicates.Nil()) == -1);
	 * [/code]
	 *
	 * @param predicate the search predicate.
	 * @return the index of the first element on which the given predicate
	 *          returns {@code true}, or -1 if the predicate returns {@code false}
	 *          for every sequence element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 */
	public int indexWhere(final Function<? super T, Boolean> predicate);

	/**
	 * <p>
	 * Returns the index of the first element on which the given predicate
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * sequence element.
	 * </p>
	 * [code]
	 * // Finding index of first null value.
	 * final int index = seq.indexOf(new Predicates.Nil());
	 *
	 * // Assert of no null values.
	 * assert (sequence.indexOf(new Predicates.Nil()) == -1);
	 * [/code]
	 *
	 * @param predicate the search predicate.
	 * @return the index of the first element on which the given predicate
	 *          returns {@code true}, or -1 if the predicate returns {@code false}
	 *          for every sequence element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || start > length()}).
	 */
	public int indexWhere(
		final Function<? super T, Boolean> predicate,
		final int start
	);

	/**
	 * <p>
	 * Returns the index of the first element on which the given predicate
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * sequence element.
	 * </p>
	 * [code]
	 * // Finding index of first null value.
	 * final int index = seq.indexOf(new Predicates.Nil());
	 *
	 * // Assert of no null values.
	 * assert (sequence.indexOf(new Predicates.Nil()) == -1);
	 * [/code]
	 *
	 * @param predicate the search predicate.
	 * @return the index of the first element on which the given predicate
	 *          returns {@code true}, or -1 if the predicate returns {@code false}
	 *          for every sequence element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || end > length() || start > end}).
	 */
	public int indexWhere(
		final Function<? super T, Boolean> predicate,
		final int start,
		final int end
	);

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
	 * Returns the index of the last occurrence of the specified element
	 * in this sequence, or -1 if this sequence does not contain the element.
	 *
	 * @param element element to search for, can be {@code null}
	 * @return the index of the last occurrence of the specified element in
	 * 		  this sequence, or -1 if this sequence does not contain the element
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code end < 0 || end > length()}).
	 */
	public int lastIndexOf(final Object element, final int end);

	/**
	 * Returns the index of the last occurrence of the specified element
	 * in this sequence, or -1 if this sequence does not contain the element.
	 *
	 * @param element element to search for, can be {@code null}
	 * @return the index of the last occurrence of the specified element in
	 * 		  this sequence, or -1 if this sequence does not contain the element
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || end > length() || start > end}).
	 */
	public int lastIndexOf(final Object element, final int start, final int end);

	/**
	 * Returns the index of the last element on which the given predicate
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * sequence element.
	 *
	 * @param predicate the search predicate.
	 * @return the index of the last element on which the given predicate
	 *          returns {@code true}, or -1 if the predicate returns false for
	 *          every sequence element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 */
	public int lastIndexWhere(final Function<? super T, Boolean> predicate);

	/**
	 * Returns the index of the last element on which the given predicate
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * sequence element.
	 *
	 * @param predicate the search predicate.
	 * @return the index of the last element on which the given predicate
	 *          returns {@code true}, or -1 if the predicate returns false for
	 *          every sequence element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code end < 0 || end > length()}).
	 */
	public int lastIndexWhere(
		final Function<? super T, Boolean> predicate,
		final int end
	);

	/**
	 * Returns the index of the last element on which the given predicate
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * sequence element.
	 *
	 * @param predicate the search predicate.
	 * @return the index of the last element on which the given predicate
	 *          returns {@code true}, or -1 if the predicate returns false for
	 *          every sequence element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || end > length() || start > end}).
	 */
	public int lastIndexWhere(
		final Function<? super T, Boolean> predicate,
		final int start,
		final int end
	);

	/**
	 * Returns a fixed-size list backed by the specified sequence. (Changes to
	 * the returned list "write through" to the array.) The returned list is
	 * fixed size, serializable and implements {@link RandomAccess}.
	 *
	 * @return a list view of this sequence
	 */
	public List<T> asList();

	/**
	 * Builds a new sequence by applying a function to all elements of this
	 * sequence.
	 *
	 * @param <B> the element type of the returned collection.
	 * @param mapper the function to apply to each element.
	 * @return a new sequence of type That resulting from applying the given
	 *         function f to each element of this sequence and collecting the
	 *         results.
	 * @throws NullPointerException if the element {@code mapper} is
	 *         {@code null}.
	 */
	public <B> Seq<B> map(final Function<? super T, ? extends B> mapper);

	/**
	 * Return an array containing all of the elements in this sequence in right
	 * order. The returned array will be "safe" in that no references to it
	 * are maintained by this sequence. (In other words, this method must allocate
	 * a new array.) The caller is thus free to modify the returned array.
	 *
	 * @see java.util.Collection#toArray()
	 *
	 * @return an array containing all of the elements in this list in right
	 *          order
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
	 *         stored, if it is big enough; otherwise, a new array of the same
	 *         runtime type is allocated for this purpose.
	 * @return an array containing the elements of this array
	 * @throws ArrayStoreException if the runtime type of the specified array is
	 *          not a super type of the runtime type of every element in this array
	 * @throws NullPointerException if the given array is {@code null}.
	 */
	public T[] toArray(final T[] array);

	/**
	 * Returns a view of the portion of this sequence between the specified
	 * {@code start}, inclusive, and {@code end}, exclusive. (If {@code start}
	 * and {@code end} are equal, the returned sequence has the length zero.) The
	 * returned sequence is backed by this sequence, so non-structural changes
	 * in the returned sequence are reflected in this sequence, and vice-versa.
	 * <p/>
	 * This method eliminates the need for explicit range operations (of the
	 * sort that commonly exist for arrays). Any operation that expects an sequence
	 * can be used as a range operation by passing an sub sequence view instead of
	 * an whole sequence.
	 *
	 * @param start low end point (inclusive) of the sub array.
	 * @return a view of the specified range within this array.
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || start > length()}).
	 */
	public Seq<T> subSeq(final int start);

	/**
	 * Returns a view of the portion of this sequence between the specified
	 * {@code start}, inclusive, and {@code end}, exclusive. (If {@code start}
	 * and {@code end} are equal, the returned sequence has the length zero.) The
	 * returned sequence is backed by this sequence, so non-structural changes in the
	 * returned sequence are reflected in this array, and vice-versa.
	 * <p/>
	 * This method eliminates the need for explicit range operations (of the
	 * sort that commonly exist for arrays). Any operation that expects an array
	 * can be used as a range operation by passing an sub sequence view instead of
	 * an whole sequence.
	 *
	 * @param start low end point (inclusive) of the sub sequence.
	 * @param end high end point (exclusive) of the sub sequence.
	 * @return a view of the specified range within this sequence.
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || end > length() || start > end}).
	 */
	public Seq<T> subSeq(final int start, final int end);

	/**
	 * Returns the hash code value for this sequence. The hash code is defined
	 * as followed:
	 *
	 * [code]
	 * int hashCode = 1;
	 * final Iterator<E> it = seq.iterator();
	 * while (it.hasNext()) {
	 *     final E obj = it.next();
	 *     hashCode = 31*hashCode + (obj == null ? 0 : obj.hashCode());
	 * }
	 * [/code]
	 *
	 * @see List#hashCode()
	 *
	 * @return the hash code value for this list
	 */
	@Override
	public int hashCode();

	/**
	 * Compares the specified object with this sequence for equality. Returns
	 * true if and only if the specified object is also a sequence, both
	 * sequence have the same size, and all corresponding pairs of elements in
	 * the two sequences are equal. (Two elements e1 and e2 are equal if
	 * (e1==null ? e2==null : e1.equals(e2)).) This definition ensures that the
	 * equals method works properly across different implementations of the Seq
	 * interface.
	 *
	 * @see List#equals(Object)
	 *
	 * @param object the object to be compared for equality with this sequence.
	 * @return {@code true} if the specified object is equal to this sequence,
	 *          {@code false} otherwise.
	 */
	@Override
	public boolean equals(final Object object);

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
	 * Create a string representation of the given sequence.
	 *
	 * @param separator the separator of the array elements; e.g. {@code ','}.
	 * @return the string representation of this sequence.
	 */
	public String toString(final String separator);

}






