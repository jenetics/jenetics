/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.util;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static io.jenetics.internal.collection.Array.checkIndex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * General interface for a ordered, fixed sized, object sequence.
 * <br>
 * Use the {@link #asList()} method to work together with the
 * <a href="http://download.oracle.com/javase/6/docs/technotes/guides/collections/index.html">
 * Java Collection Framework</a>.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 5.2
 */
public interface Seq<T> extends BaseSeq<T>, IntFunction<T> {

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @since 3.9
	 *
	 * @see #get(int)
	 *
	 * @param index index of the element to return.
	 * @return the value at the given {@code index}.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code index < 0 || index >= size()}.
	 */
	@Override
	default T apply(final int index) {
		return get(index);
	}

	/**
	 * @see #length()
	 * @return the size of this sequence
	 */
	default int size() {
		return length();
	}

	/**
	 * Tests whether a predicate holds for all elements of this sequence.
	 *
	 * @param predicate the predicate to use to test the elements.
	 * @return {@code true} if the given predicate p holds for all elements of
	 *         this sequence, {@code false} otherwise.
	 * @throws NullPointerException if the given {@code predicate} is
	 *         {@code null}.
	 */
	default boolean forAll(final Predicate<? super T> predicate) {
		boolean valid = true;

		for (int i = 0, n = length(); i < n && valid; ++i) {
			valid = predicate.test(get(i));
		}

		return valid;
	}

	/**
	 * Returns a possibly parallel {@code Stream} with this sequence as its
	 * source.  It is allowable for this method to return a sequential stream.
	 *
	 * @since 3.0
	 *
	 * @return a possibly parallel {@code Stream} over the elements in this
	 * collection
	 */
	default Stream<T> parallelStream() {
		return StreamSupport.stream(spliterator(), true);
	}

	/**
	 * Returns {@code true} if this sequence contains the specified element.
	 *
	 * @param element element whose presence in this sequence is to be tested.
	 *        The tested element can be {@code null}.
	 * @return {@code true} if this sequence contains the specified element
	 */
	default boolean contains(final Object element) {
		return indexOf(element) != -1;
	}

	/**
	 * Returns the index of the first occurrence of the specified element
	 * in this sequence, or -1 if this sequence does not contain the element.
	 *
	 * @param element element to search for, can be {@code null}
	 * @return the index of the first occurrence of the specified element in
	 *          this sequence, or -1 if this sequence does not contain the element
	 */
	default int indexOf(final Object element) {
		return indexOf(element, 0, length());
	}

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
	default int indexOf(final Object element, final int start) {
		return indexOf(element, start, length());
	}

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
	default int indexOf(final Object element, final int start, final int end) {
		return element != null
			? indexWhere(element::equals, start, end)
			: indexWhere(Objects::isNull, start, end);
	}

	/**
	 * <p>
	 * Returns the index of the first element on which the given predicate
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * sequence element.
	 * </p>
	 * <pre>{@code
	 * // Finding index of first null value.
	 * final int index = seq.indexOf(o -> o == null);
	 *
	 * // Assert of no null values.
	 * assert (sequence.indexOf(o -> o == null) == -1);
	 * }</pre>
	 *
	 * @param predicate the search predicate.
	 * @return the index of the first element on which the given predicate
	 *          returns {@code true}, or -1 if the predicate returns {@code false}
	 *          for every sequence element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 */
	default int indexWhere(final Predicate<? super T> predicate) {
		return indexWhere(predicate, 0, length());
	}

	/**
	 * <p>
	 * Returns the index of the first element on which the given predicate
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * sequence element.
	 * </p>
	 * <pre>{@code
	 * // Finding index of first null value.
	 * final int index = seq.indexOf(o -> o == null);
	 *
	 * // Assert of no null values.
	 * assert (sequence.indexOf(o -> o == null) == -1);
	 * }</pre>
	 *
	 * @param predicate the search predicate.
	 * @param start the search start index
	 * @return the index of the first element on which the given predicate
	 *          returns {@code true}, or -1 if the predicate returns {@code false}
	 *          for every sequence element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || start > length()}).
	 */
	default int indexWhere(
		final Predicate<? super T> predicate,
		final int start
	) {
		return indexWhere(predicate, start, length());
	}

	/**
	 * <p>
	 * Returns the index of the first element on which the given predicate
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * sequence element.
	 * </p>
	 * <pre>{@code
	 * // Finding index of first null value.
	 * final int index = seq.indexOf(o -> o == null);
	 *
	 * // Assert of no null values.
	 * assert (sequence.indexOf(o -> o == null) == -1);
	 * }</pre>
	 *
	 * @param predicate the search predicate.
	 * @param start the search start index
	 * @param end the search end index
	 * @return the index of the first element on which the given predicate
	 *          returns {@code true}, or -1 if the predicate returns {@code false}
	 *          for every sequence element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || end > length() || start > end}).
	 */
	default int indexWhere(
		final Predicate<? super T> predicate,
		final int start,
		final int end
	) {
		requireNonNull(predicate, "Predicate");
		checkIndex(start, end, length());

		int index = -1;
		for (int i = start; i < end && index == -1; ++i) {
			if (predicate.test(get(i))) {
				index = i;
			}
		}
		return index;
	}

	/**
	 * Returns the index of the last occurrence of the specified element
	 * in this sequence, or -1 if this sequence does not contain the element.
	 *
	 * @param element element to search for, can be {@code null}
	 * @return the index of the last occurrence of the specified element in
	 *         this sequence, or -1 if this sequence does not contain the element
	 */
	default int lastIndexOf(final Object element) {
		return lastIndexOf(element, 0, length());
	}

	/**
	 * Returns the index of the last occurrence of the specified element
	 * in this sequence, or -1 if this sequence does not contain the element.
	 *
	 * @param element element to search for, can be {@code null}
	 * @param end the search end index
	 * @return the index of the last occurrence of the specified element in
	 *         this sequence, or -1 if this sequence does not contain the element
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code end < 0 || end > length()}).
	 */
	default int lastIndexOf(final Object element, final int end) {
		return lastIndexOf(element, 0, end);
	}

	/**
	 * Returns the index of the last occurrence of the specified element
	 * in this sequence, or -1 if this sequence does not contain the element.
	 *
	 * @param element element to search for, can be {@code null}
	 * @param start the search start index
	 * @param end the search end index
	 * @return the index of the last occurrence of the specified element in
	 *         this sequence, or -1 if this sequence does not contain the element
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || end > length() || start > end}).
	 */
	default int lastIndexOf(
		final Object element,
		final int start,
		final int end
	) {
		return element != null
			? lastIndexWhere(element::equals, start, end)
			: lastIndexWhere(Objects::isNull, start, end);
	}

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
	default int lastIndexWhere(final Predicate<? super T> predicate) {
		return lastIndexWhere(predicate, 0, length());
	}

	/**
	 * Returns the index of the last element on which the given predicate
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * sequence element.
	 *
	 * @param predicate the search predicate.
	 * @param end the search end index
	 * @return the index of the last element on which the given predicate
	 *          returns {@code true}, or -1 if the predicate returns false for
	 *          every sequence element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code end < 0 || end > length()}).
	 */
	default int lastIndexWhere(
		final Predicate<? super T> predicate,
		final int end
	) {
		return lastIndexWhere(predicate, 0, end);
	}

	/**
	 * Returns the index of the last element on which the given predicate
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * sequence element.
	 *
	 * @param predicate the search predicate.
	 * @param start the search start index
	 * @param end the search end index
	 * @return the index of the last element on which the given predicate
	 *          returns {@code true}, or -1 if the predicate returns false for
	 *          every sequence element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || end > length() || start > end}).
	 */
	default int lastIndexWhere(
		final Predicate<? super T> predicate,
		final int start,
		final int end
	) {
		requireNonNull(predicate, "Predicate");
		checkIndex(start, end, length());

		int index = -1;
		for (int i = end; --i >= start && index == -1;) {
			if (predicate.test(get(i))) {
				index = i;
			}
		}
		return index;
	}

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
	<B> Seq<B> map(final Function<? super T, ? extends B> mapper);

	/**
	 * Return a <i>new</i> {@code Seq} with the given {@code values} appended.
	 *
	 * @since 3.4
	 *
	 * @param values the values to append
	 * @return a <i>new</i> {@code Seq} with the elements of {@code this}
	 *        sequence and the given {@code values} appended.
	 * @throws NullPointerException if the given {@code values} array is
	 *         {@code null}
	 */
	@SuppressWarnings("unchecked")
	default Seq<T> append(final T... values) {
		return append(Seq.of(values));
	}

	/**
	 * Return a <i>new</i> {@code Seq} with the given {@code values} appended.
	 *
	 * @since 3.4
	 *
	 * @param values the values to append
	 * @return a <i>new</i> {@code Seq} with the elements of {@code this}
	 *        sequence and the given {@code values} appended.
	 * @throws NullPointerException if the given {@code values} iterable is
	 *         {@code null}
	 */
	Seq<T> append(final Iterable<? extends T> values);

	/**
	 * Return a <i>new</i> {@code Seq} with the given {@code values} prepended.
	 *
	 * @since 3.4
	 *
	 * @param values the values to append
	 * @return a <i>new</i> {@code Seq} with the elements of {@code this}
	 *        sequence and the given {@code values} prepended.
	 * @throws NullPointerException if the given {@code values} array is
	 *         {@code null}
	 */
	@SuppressWarnings("unchecked")
	default Seq<T> prepend(final T... values) {
		return prepend(Seq.of(values));
	}

	/**
	 * Return a <i>new</i> {@code Seq} with the given {@code values} prepended.
	 *
	 * @since 3.4
	 *
	 * @param values the values to append
	 * @return a <i>new</i> {@code Seq} with the elements of {@code this}
	 *        sequence and the given {@code values} prepended.
	 * @throws NullPointerException if the given {@code values} array is
	 *         {@code null}
	 */
	Seq<T> prepend(final Iterable<? extends T> values);

	/**
	 * Returns a fixed-size list backed by the specified sequence. (Changes to
	 * the returned list "write through" to the array.) The returned list is
	 * fixed size, serializable and implements {@link RandomAccess}.
	 *
	 * @return a list view of this sequence
	 */
	default List<T> asList() {
		return new BaseSeqList<>(this);
	}

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
	default Object[] toArray() {
		final Object[] array = new Object[size()];
		for (int i = size(); --i >= 0;) {
			array[i] = get(i);
		}
		return array;
	}

	/**
	 * Return an array containing all of the elements in this sequence in right
	 * order; the runtime type of the returned array is that of the specified
	 * array. If this sequence fits in the specified array, it is returned
	 * therein. Otherwise, a new array is allocated with the runtime type of the
	 * specified array and the length of this array.
	 * <p>
	 * If this sequence fits in the specified array with room to spare (i.e.,
	 * the array has more elements than this array), the element in the array
	 * immediately following the end of this array is set to null. (This is
	 * useful in determining the length of the array only if the caller knows
	 * that the list does not contain any null elements.)
	 *
	 * @see java.util.Collection#toArray(Object[])
	 *
	 * @param <B> the runtime type of the array to contain the sequence
	 * @param array the array into which the elements of this array are to be
	 *         stored, if it is big enough; otherwise, a new array of the same
	 *         runtime type is allocated for this purpose.
	 * @return an array containing the elements of this array
	 * @throws ArrayStoreException if the runtime type of the specified array is
	 *         not a super type of the runtime type of every element in this
	 *         array
	 * @throws NullPointerException if the given {@code array} is {@code null}.
	 */
	@SuppressWarnings("unchecked")
	default <B> B[] toArray(final B[] array) {
		if (array.length < length()) {
			final Object[] copy = (Object[])java.lang.reflect.Array
				.newInstance(array.getClass().getComponentType(), length());

			for (int i = length(); --i >= 0;) {
				copy[i] = get(i);
			}

			return (B[])copy;
		}

		for (int i = 0, n = length(); i < n; ++i) {
			((Object[])array)[i] = get(i);
		}
		if (array.length > length()) {
			array[length()] = null;
		}

		return array;
	}

	/**
	 * Returns an array containing the elements of this sequence, using the
	 * provided generator function to allocate the returned array.
	 *
	 * @since 4.4
	 *
	 * @param generator a function which produces a new array of the desired
	 *        type and the provided length
	 * @param <B> the element type of the resulting array
	 * @return an array containing the elements in {@code this} sequence
	 * @throws ArrayStoreException if the runtime type of the specified array is
	 *         not a super type of the runtime type of every element in this
	 *         array
	 * @throws NullPointerException if the given {@code generator} is {@code null}.
	 */
	default <B> B[] toArray(final IntFunction<B[]> generator) {
		return toArray(generator.apply(length()));
	}

	/**
	 * Returns a view of the portion of this sequence between the specified
	 * {@code start}, inclusive, and {@code end}, exclusive. (If {@code start}
	 * and {@code end} are equal, the returned sequence has the length zero.)
	 * The returned sequence is backed by this sequence, so non-structural
	 * changes in the returned sequence are reflected in this sequence, and
	 * vice-versa.
	 * <p>
	 * This method eliminates the need for explicit range operations (of the
	 * populationSort that commonly exist for arrays). Any operation that
	 * expects an sequence can be used as a range operation by passing an sub
	 * sequence view instead of an whole sequence.
	 *
	 * @param start lower end point (inclusive) of the sub array.
	 * @return a view of the specified range within this array.
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || start > length()}).
	 */
	Seq<T> subSeq(final int start);

	/**
	 * Returns a view of the portion of this sequence between the specified
	 * {@code start}, inclusive, and {@code end}, exclusive. (If {@code start}
	 * and {@code end} are equal, the returned sequence has the length zero.)
	 * The returned sequence is backed by this sequence, so non-structural
	 * changes in the returned sequence are reflected in this array, and
	 * vice-versa.
	 * <p>
	 * This method eliminates the need for explicit range operations (of the
	 * populationSort that commonly exist for arrays). Any operation that
	 * expects an array can be used as a range operation by passing an sub
	 * sequence view instead of an whole sequence.
	 *
	 * @param start low end point (inclusive) of the sub sequence.
	 * @param end high end point (exclusive) of the sub sequence.
	 * @return a view of the specified range within this sequence.
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          ({@code start < 0 || end > length() || start > end}).
	 */
	Seq<T> subSeq(final int start, final int end);

	/**
	 * Test whether the given array is sorted in ascending order.
	 *
	 * @return {@code true} if the given {@code array} is sorted in ascending
	 *         order, {@code false} otherwise.
	 * @throws NullPointerException if the given array or one of it's element is
	 *         {@code null}.
	 */
	@SuppressWarnings("unchecked")
	default boolean isSorted() {
		boolean sorted = true;
		for (int i = 0, n = length() - 1; i < n && sorted; ++i) {
			sorted = ((Comparable<T>)get(i)).compareTo(get(i + 1)) <= 0;
		}

		return sorted;
	}

	/**
	 * Test whether the given array is sorted in ascending order. The order of
	 * the array elements is defined by the given comparator.
	 *
	 * @param comparator the comparator which defines the order.
	 * @return {@code true} if the given {@code array} is sorted in ascending
	 *         order, {@code false} otherwise.
	 * @throws NullPointerException if the given array or one of it's element or
	 *         the comparator is {@code null}.
	 */
	default boolean isSorted(final Comparator<? super T> comparator) {
		boolean sorted = true;
		for (int i = 0, n = length() - 1; i < n && sorted; ++i) {
			sorted = comparator.compare(get(i), get(i + 1)) <= 0;
		}

		return sorted;
	}

	/**
	 * Return this sequence as {@code MSeq} instance. If {@code this} is not a
	 * {@code MSeq} a new seq is created.
	 *
	 * @since 3.8
	 *
	 * @return a {@code MSeq} with this values
	 */
	default MSeq<T> asMSeq() {
		return this instanceof MSeq ? (MSeq<T>)this : MSeq.of(this);
	}

	/**
	 * Return this sequence as {@code ISeq} instance. If {@code this} is not a
	 * {@code ISeq} a new seq is created.
	 *
	 * @since 3.8
	 *
	 * @return a {@code ISeq} with this values
	 */
	default ISeq<T> asISeq() {
		return this instanceof ISeq ? (ISeq<T>)this : ISeq.of(this);
	}

	/**
	 * Returns the hash code value for this sequence. The hash code is defined
	 * as followed:
	 *
	 * <pre>{@code
	 * int hashCode = 1;
	 * final Iterator<E> it = seq.iterator();
	 * while (it.hasNext()) {
	 *     final E obj = it.next();
	 *     hashCode = 31*hashCode + (obj == null ? 0 : obj.hashCode());
	 * }
	 * }</pre>
	 *
	 * @see List#hashCode()
	 * @see Seq#hashCode(BaseSeq)
	 *
	 * @return the hash code value for this list
	 */
	@Override
	int hashCode();

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
	 * @see Seq#equals(BaseSeq, Object)
	 *
	 * @param object the object to be compared for equality with this sequence.
	 * @return {@code true} if the specified object is equal to this sequence,
	 *          {@code false} otherwise.
	 */
	@Override
	boolean equals(final Object object);

	/**
	 * Create a string representation of the given sequence.
	 *
	 * @param prefix the prefix of the string representation; e.g {@code '['}.
	 * @param separator the separator of the array elements; e.g. {@code ','}.
	 * @param suffix the suffix of the string representation; e.g. {@code ']'}.
	 * @return the string representation of this sequence.
	 */
	default String toString(
		final String prefix,
		final String separator,
		final String suffix
	) {
		return stream()
			.map(Objects::toString)
			.collect(joining(separator, prefix, suffix));
	}

	/**
	 * Create a string representation of the given sequence.
	 *
	 * @param separator the separator of the array elements; e.g. {@code ','}.
	 * @return the string representation of this sequence.
	 */
	default String toString(final String separator) {
		return toString("", separator, "");
	}

	/**
	 * Unified method for calculating the hash code of every {@link Seq}
	 * implementation. The hash code is defined as followed:
	 *
	 * <pre>{@code
	 * int hashCode = 1;
	 * final Iterator<E> it = seq.iterator();
	 * while (it.hasNext()) {
	 *     final E obj = it.next();
	 *     hashCode = 31*hashCode + (obj == null ? 0 : obj.hashCode());
	 * }
	 * }</pre>
	 *
	 * @see Seq#hashCode()
	 * @see List#hashCode()
	 *
	 * @param seq the sequence to calculate the hash code for.
	 * @return the hash code of the given sequence.
	 */
	static int hashCode(final BaseSeq<?> seq) {
		int hash = 1;
		for (Object element : seq) {
			hash = 31*hash + (element == null ? 0: element.hashCode());
		}
		return hash;
	}

	/**
	 * Unified method for compare to sequences for equality.
	 *
	 * @see Seq#equals(Object)
	 *
	 * @param seq the sequence to test for equality.
	 * @param obj the object to test for equality with the sequence.
	 * @return {@code true} if the given objects are sequences and contain the
	 *          same objects in the same order, {@code false} otherwise.
	 */
	static boolean equals(final BaseSeq<?> seq, final Object obj) {
		if (obj == seq) {
			return true;
		}
		if (!(obj instanceof final Seq<?> other)) {
			return false;
		}

		boolean equals = seq.length() == other.length();
		for (int i = seq.length(); equals && --i >= 0;) {
			final Object element = seq.get(i);
			equals = element != null
				? element.equals(other.get(i))
				: other.get(i) == null;
		}
		return equals;
	}

	/* *************************************************************************
	 *  Some static helper methods.
	 * ************************************************************************/

	/**
	 * Return a sequence whose elements are all the elements of the first
	 * element followed by all the elements of the sequence.
	 *
	 * @since 5.0
	 *
	 * @param a the first element
	 * @param b the appending sequence
	 * @param <T> the type of the sequence elements
	 * @return the concatenation of the two inputs
	 * @throws NullPointerException if one of the second arguments is
	 *         {@code null}
	 */
	@SuppressWarnings("unchecked")
	static <T> Seq<T> concat(
		final T a,
		final Seq<? extends T> b
	) {
		return ((Seq<T>)b).prepend(a);
	}

	/**
	 * Return a sequence whose elements are all the elements of the first
	 * sequence followed by all the elements of the vararg array.
	 *
	 * @since 5.0
	 *
	 * @param a the first sequence
	 * @param b the vararg elements
	 * @param <T> the type of the sequence elements
	 * @return the concatenation of the two inputs
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	@SuppressWarnings("unchecked")
	static <T> Seq<T> concat(
		final Seq<? extends T> a,
		final T... b
	) {
		return ((Seq<T>)a).append(b);
	}

	/**
	 * Return a sequence whose elements are all the elements of the first
	 * sequence followed by all the elements of the second sequence.
	 *
	 * @since 5.0
	 *
	 * @param a the first sequence
	 * @param b the second sequence
	 * @param <T> the type of the sequence elements
	 * @return the concatenation of the two input sequences
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	@SuppressWarnings("unchecked")
	static <T> Seq<T> concat(
		final Seq<? extends T> a,
		final Seq<? extends T> b
	) {
		return ((Seq<T>)a).append(b);
	}

	/* *************************************************************************
	 *  Some static factory methods.
	 * ************************************************************************/

	/**
	 * Single instance of an empty {@code Seq}.
	 *
	 * @since 3.3
	 */
	Seq<?> EMPTY = ISeq.EMPTY;

	/**
	 * Return an empty {@code Seq}.
	 *
	 * @since 3.3
	 *
	 * @param <T> the element type of the returned {@code Seq}.
	 * @return an empty {@code Seq}.
	 */
	static <T> Seq<T> empty() {
		return ISeq.empty();
	}

	/**
	 * Returns a {@code Collector} that accumulates the input elements into a
	 * new {@code Seq}.
	 *
	 * @param <T> the type of the input elements
	 * @return a {@code Collector} which collects all the input elements into a
	 *         {@code Seq}, in encounter order
	 */
	static <T> Collector<T, ?, Seq<T>> toSeq() {
		return Collector.of(
			(Supplier<List<T>>)ArrayList::new,
			List::add,
			(left, right) -> { left.addAll(right); return left; },
			Seq::of
		);
	}

	/**
	 * Returns a {@code Collector} that accumulates the last {@code n} input
	 * elements into a new {@code Seq}.
	 *
	 * @since 5.0
	 *
	 * @param maxSize the maximal size of the collected sequence
	 * @param <T> the type of the input elements
	 * @return a {@code Collector} which collects maximal {@code maxSize} of the
	 *         input elements into an {@code ISeq}, in encounter order
	 * @throws IllegalArgumentException if the {@code maxSize} is negative
	 */
	static <T> Collector<T, ?, Seq<T>> toSeq(final int maxSize) {
		return Seqs.toSeq(maxSize, Buffer::toSeq);
	}

	/**
	 * Create a new {@code Seq} from the given values.
	 *
	 * @param <T> the element type
	 * @param values the array values.
	 * @return a new {@code Seq} with the given values.
	 * @throws NullPointerException if the {@code values} array is {@code null}.
	 */
	@SafeVarargs
	static <T> Seq<T> of(final T... values) {
		return ISeq.of(values);
	}

	/**
	 * Create a new {@code Seq} from the given values.
	 *
	 * @param <T> the element type
	 * @param values the array values.
	 * @return a new {@code Seq} with the given values.
	 * @throws NullPointerException if the {@code values} array is {@code null}.
	 */
	static <T> Seq<T> of(final Iterable<? extends T> values) {
		return ISeq.of(values);
	}

	/**
	 * Creates a new sequence, which is filled with objects created be the given
	 * {@code supplier}.
	 *
	 * @since 3.3
	 *
	 * @param <T> the element type of the sequence
	 * @param supplier the {@code Supplier} which creates the elements, the
	 *        returned sequence is filled with
	 * @param length the length of the returned sequence
	 * @return a new sequence filled with elements given by the {@code supplier}
	 * @throws NegativeArraySizeException if the given {@code length} is
	 *         negative
	 * @throws NullPointerException if the given {@code supplier} is
	 *         {@code null}
	 */
	static <T> Seq<T> of(Supplier<? extends T> supplier, final int length) {
		return ISeq.of(supplier, length);
	}

	/**
	 * Returns a sequence backed by the specified {@code seq}. (Changes to the
	 * given sequence (if writeable) are "write through" to the returned
	 * sequence.)  This method acts as bridge between basic sequences and
	 * sequence-based APIs.
	 *
	 * @since 6.0
	 *
	 * @param seq the basic sequence containing the elements
	 * @param <T> the element type
	 * @return a sequence view of the given {@code seq}
	 * @throws NullPointerException if the given list is {@code null}
	 */
	static <T> Seq<T> viewOf(final BaseSeq<? extends T> seq) {
		return seq.isEmpty()
			? empty()
			: new SeqView<>(new BaseSeqList<>(seq));
	}

	/**
	 * Returns a sequence backed by the specified list. (Changes to the given
	 * list are "write through" to the returned sequence.)  This method acts
	 * as bridge between collection-based and sequence-based APIs.
	 *
	 * @since 4.2
	 *
	 * @param list the list containing the elements
	 * @param <T> the element type
	 * @return a sequence view of the given {@code list}
	 * @throws NullPointerException if the given list is {@code null}
	 */
	static <T> Seq<T> viewOf(final List<? extends T> list) {
		return list.isEmpty()
			? empty()
			: new SeqView<>(list);
	}

	/**
	 * Returns a fixed-size sequence backed by the specified array. (Changes to
	 * the given array are "write through" to the returned sequence.)  This
	 * method acts as bridge between array-based and sequence-based APIs.
	 *
	 * @since 4.2
	 *
	 * @param array the array containing the sequence elements
	 * @param <T> the element type
	 * @return a sequence view of the given {@code array}
	 * @throws NullPointerException if the given array is {@code null}
	 */
	static <T> Seq<T> viewOf(final T[] array) {
		return array.length == 0
			? empty()
			: new SeqView<>(Arrays.asList(array));
	}

}
