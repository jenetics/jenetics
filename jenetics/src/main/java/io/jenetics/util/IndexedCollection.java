package io.jenetics.util;

import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.collection.Array.checkIndex;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface IndexedCollection<T> extends Iterable<T> {

	/**
	 * Return the value at the given {@code index}.
	 *
	 * @param index index of the element to return.
	 * @return the value at the given {@code index}.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         (index &lt; 0 || index &gt;= size()).
	 */
	public T get(final int index);

	/**
	 * Return the number of elements of this collection.
	 *
	 * @return the number of elements of this collection
	 */
	public int length();

	/**
	 * Returns {@code true} if this sequence contains no elements.
	 *
	 * @return {@code true} if this sequence contains no elements
	 */
	public default boolean isEmpty() {
		return length() == 0;
	}

	/**
	 * Returns {@code true} if this sequence contains at least one element.
	 *
	 * @return {@code true} if this sequence contains at least one element
	 */
	public default boolean nonEmpty() {
		return !isEmpty();
	}

	@Override
	public default Iterator<T> iterator() {
		return listIterator();
	}

	public default ListIterator<T> listIterator() {
		return new ListIterator<T>() {
			private int cursor = 0;
			private int lastElement = -1;

			@Override
			public boolean hasNext() {
				return cursor != length();
			}

			@Override
			public T next() {
				final int i = cursor;
				if (cursor >= length()) {
					throw new NoSuchElementException();
				}

				cursor = i + 1;
				return get(lastElement = i);
			}

			@Override
			public int nextIndex() {
				return cursor;
			}

			@Override
			public boolean hasPrevious() {
				return cursor != 0;
			}

			@Override
			public T previous() {
				final int i = cursor - 1;
				if (i < 0) {
					throw new NoSuchElementException();
				}

				cursor = i;
				return get(lastElement = i);
			}

			@Override
			public int previousIndex() {
				return cursor - 1;
			}

			@Override
			public void set(final T value) {
				throw new UnsupportedOperationException(
					"Iterator is immutable."
				);
			}

			@Override
			public void add(final T value) {
				throw new UnsupportedOperationException(
					"Can't change Iterator size."
				);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException(
					"Can't change Iterator size."
				);
			}

		};
	}

	public default Stream<T> stream() {
		return IntStream.range(0, length()).mapToObj(this::get);
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
	public default boolean forAll(final Predicate<? super T> predicate) {
		boolean valid = true;

		if (this instanceof RandomAccess) {
			for (int i = 0, n = length(); i < n && valid; ++i) {
				valid = predicate.test(get(i));
			}
		} else {
			final Iterator<T> it = iterator();
			while (it.hasNext() && valid) {
				valid = predicate.test(it.next());
			}
		}

		return valid;
	}

	/**
	 * Returns {@code true} if this sequence contains the specified element.
	 *
	 * @param element element whose presence in this sequence is to be tested.
	 *        The tested element can be {@code null}.
	 * @return {@code true} if this sequence contains the specified element
	 */
	public default boolean contains(final Object element) {
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
	public default int indexOf(final Object element) {
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
	public default int indexOf(final Object element, final int start) {
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
	public default int indexOf(final Object element, final int start, final int end) {
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
	public default int indexWhere(final Predicate<? super T> predicate) {
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
	public default int indexWhere(
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
	public default int indexWhere(
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
	public default int lastIndexOf(final Object element) {
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
	public default int lastIndexOf(final Object element, final int end) {
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
	public default int lastIndexOf(
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
	public default int lastIndexWhere(final Predicate<? super T> predicate) {
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
	public default int lastIndexWhere(
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
	public default int lastIndexWhere(
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

}

