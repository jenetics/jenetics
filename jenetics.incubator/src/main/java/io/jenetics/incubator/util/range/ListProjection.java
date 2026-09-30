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
package io.jenetics.incubator.util.range;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

/**
 * A list view of a given range, onto an underlying list.
 *
 * @param list the underlying list
 * @param range the index range of the {@code list} projection
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.2
 * @since 9.2
 */
public record ListProjection<T>(List<T> list, Range<Integer> range)
	implements List<T>
{

	public ListProjection {
		if (!range.isEmpty()) {
			Objects.checkIndex(range.start(), list.size());
			Objects.checkIndex(range.end() - 1, list.size());
		}
	}

	/**
	 * Create a new list projection of the whole {@code list} range.
	 *
	 * @param list the projected list
	 */
	public ListProjection(List<T> list) {
		this(list, Range.INTEGER.dense(0, list.size()));
	}

	@Override
	public int size() {
		return (int)range.size();
	}

	@Override
	public boolean isEmpty() {
		return range.isEmpty();
	}

	@Override
	public T get(int index) {
		return list.get(range.get(index));
	}

	@Override
	public T set(int index, T element) {
		return list.set(range.get(index), element);
	}

	@Override
	public void add(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object o) {
		return stream().anyMatch(element -> Objects.equals(element, o));
	}

	@Override
	public int indexOf(Object o) {
		int index = 0;
		for (var element : this) {
			if (Objects.equals(element, o)) {
				return index;
			}
			++index;
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		int index = size();
		for (var it = listIterator(size()); it.hasPrevious();) {
			if (Objects.equals(it.previous(), o)) {
				return --index;
			}
			--index;
		}
		return -1;
	}

	@Override
	public Iterator<T> iterator() {
		return listIterator();
	}

	@Override
	public ListIterator<T> listIterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		if (index < 0 || index > size()) {
			throw new IndexOutOfBoundsException(
				"Index: %d, size: %d".formatted(index, size())
			);
		}

		return new ListIterator<>() {
			private int cursor = index;
			private int last = -1;

			@Override
			public boolean hasNext() {
				return cursor < size();
			}

			@Override
			public T next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				last = cursor;
				return get(cursor++);
			}

			@Override
			public boolean hasPrevious() {
				return cursor > 0;
			}

			@Override
			public T previous() {
				if (!hasPrevious()) {
					throw new NoSuchElementException();
				}
				last = --cursor;
				return get(cursor);
			}

			@Override
			public int nextIndex() {
				return cursor;
			}

			@Override
			public int previousIndex() {
				return cursor - 1;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public void set(final T element) {
				if (last < 0) {
					throw new IllegalStateException();
				}
				ListProjection.this.set(last, element);
			}

			@Override
			public void add(final T element) {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public Stream<T> stream() {
		return range.stream().map(list::get);
	}

	@Override
	public ListProjection<T> subList(int fromIndex, int toIndex) {
		Objects.checkFromToIndex(fromIndex, toIndex, size());
		return new ListProjection<>(list, subRange(fromIndex, toIndex));
	}

	private Range<Integer> subRange(final int fromIndex, final int toIndex) {
		return switch (range) {
			case DenseRange<Integer> dense -> Range.INTEGER.dense(
				dense.start() + fromIndex,
				dense.start() + toIndex
			);
			case SparseRange<Integer> sparse -> {
				final var ranges = new ArrayList<Range<Integer>>();
				int offset = 0;

				for (var part : sparse.ranges()) {
					final int partSize = (int)part.size();
					final int start = Math.max(0, fromIndex - offset);
					final int end = Math.min(partSize, toIndex - offset);

					if (start < end) {
						ranges.add(Range.INTEGER.dense(
							part.start() + start,
							part.start() + end
						));
					}

					offset += partSize;
					if (offset >= toIndex) {
						break;
					}
				}

				yield Range.INTEGER.of(ranges);
			}
		};
	}

	/**
	 * Return a new list projection by intersecting the range of {@code this}
	 * list with given {@code ranges} (union).
	 *
	 * @param ranges the ranges being part of the new projection
	 * @return a new list projection
	 */
	@SuppressWarnings("unchecked")
	public ListProjection<T> subList(final Range<Integer>... ranges) {
		final var sub = Range.INTEGER.of(ranges);
		if (sub.contains(range)) {
			return this;
		} else {
			return new ListProjection<>(list, range.intersect(sub));
		}
	}

	/**
	 * Create a new list projection applying the given {@code range} and range
	 * {@code operation}.
	 *
	 * @param operation the range (set) operation
	 * @param range the other range used in by the {@code operation}
	 * @return a new list projection
	 */
	public ListProjection<T> project(
		final BinaryOperator<Range<Integer>> operation,
		final Range<Integer> range
	) {
		return new ListProjection<>(list, operation.apply(this.range, range));
	}

	@Override
	public Object[] toArray() {
		final var result = new Object[size()];
		for (int i = 0; i < result.length; ++i) {
			result[i] = get(i);
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T1> T1[] toArray(T1[] a) {
		final int size = size();
		final T1[] result = a.length >= size
			? a
			: Arrays.copyOf(a, size);

		for (int i = 0; i < size; ++i) {
			result[i] = (T1)get(i);
		}
		if (result.length > size) {
			result[size] = null;
		}

		return result;
	}

	/**
	 * Return an unmodifiable copy of {@code this} list projection.
	 *
	 * @return an unmodifiable copy of {@code this} list projection
	 */
	public List<T> toList() {
		final int size = size();
		final var result = new ArrayList<T>(size);
		for (int i = 0; i < size; ++i) {
			result.add(get(i));
		}
		return Collections.unmodifiableList(result);
	}

	/* *************************************************************************
	 * Unsupported operations.
	 * ************************************************************************/

	@Override
	public boolean add(T t) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return c.stream().allMatch(this::contains);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	/* *************************************************************************
	 * Static factories.
	 * ************************************************************************/

	/**
	 * Create a new list projection with all filtered elements.
	 *
	 * @param list the projecting list
	 * @param filter the element filter
	 * @return a new list projection with all filtered elements
	 * @param <T> the element type
	 */
	public static <T> ListProjection<T>
	of(final List<T> list, Predicate<? super T> filter) {
		requireNonNull(list);
		requireNonNull(filter);

		return new ListProjection<>(
			list,
			list.stream()
				.gather(rangeOf(filter))
				.collect(Range.INTEGER.toRange())
		);
	}

	/**
	 * Return a {@link Gatherer} which collects index ranges fulfilling the
	 * given {@code predicate}.
	 * {@snippet lang = java:
	 * // List with null-values
	 * final List<String> list = IntStream.range(0, 100)
	 *     .mapToObj(i -> i%10 == 0 ? "value" : null)
	 *     .toList();
	 *
	 * // The indexes of the null-values in the list.
	 * final Range<Integer> nulls = list.stream()
	 *    .gather(ListProjection.rangeOf(Objects::isNull))
	 *    .collect(Range.INTEGER.toRange());
	 *}
	 *
	 * @param predicate the predicate which defines the element ranges
	 * @return list index ranges of elements which fulfills the given
	 *         {@code predicate}
	 * @param <T> the element type
	 */
	public static <T> Gatherer<T, ?, Range<Integer>>
	rangeOf(Predicate<? super T> predicate) {
		requireNonNull(predicate);

		final class State {
			int count = 0;
			int start = -1;
		}

		return Gatherer.ofSequential(
			State::new,
			(state, element, downstream) -> {
				if (predicate.test(element)) {
					if (state.start == -1) {
						state.start = state.count;
					}
				} else {
					if (state.start != -1) {
						final var range = Range.INTEGER.dense(
							state.start,
							state.count
						);
						downstream.push(range);

						state.start = -1;
					}
				}

				++state.count;
				return true;
			},
			(state, downstream) -> {
				if (state.start != -1) {
					final var range = Range.INTEGER.dense(
						state.start,
						state.count
					);
					downstream.push(range);
				}
			}
		);
	}

}
