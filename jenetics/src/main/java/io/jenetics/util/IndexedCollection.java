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

}

