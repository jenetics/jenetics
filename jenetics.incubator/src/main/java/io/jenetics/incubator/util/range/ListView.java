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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A list view of a given range, onto an underlying list.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.2
 * @since 9.2
 */
public final class ListView<T> extends AbstractList<T> {

	private final List<T> list;
	private final Range<Integer> range;

	public ListView(List<T> list, Range<Integer> range) {
		if (!range.isEmpty()) {
			Objects.checkIndex(range.start(), list.size());
			Objects.checkIndex(range.end() - 1, list.size());
		}
		this.list = list;
		this.range = range;
	}

	/**
	 * Create a new list projection of the whole {@code list} range.
	 *
	 * @param list the projected list
	 */
	public ListView(List<T> list) {
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
	public boolean contains(Object o) {
		return stream().anyMatch(element -> Objects.equals(element, o));
	}

	@Override
	public Stream<T> stream() {
		return range.stream().map(list::get);
	}

	@Override
	public ListView<T> subList(int fromIndex, int toIndex) {
		Objects.checkFromToIndex(fromIndex, toIndex, size());
		return new ListView<>(list, subRange(fromIndex, toIndex));
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
	public ListView<T> subList(final Range<Integer>... ranges) {
		final var sub = Range.INTEGER.of(ranges);
		if (sub.contains(range)) {
			return this;
		} else {
			return new ListView<>(list, range.intersect(sub));
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
	public ListView<T> project(
		final BinaryOperator<Range<Integer>> operation,
		final Range<Integer> range
	) {
		return new ListView<>(list, operation.apply(this.range, range));
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
	 * Static factories.
	 * ************************************************************************/

	/**
	 * Create a new list projection with all filtered elements.
	 *
	 * @param list the projecting list
	 * @param range the element range of the given {@code list}
	 * @return a new list projection with all filtered elements
	 * @param <T> the element type
	 * @throws IndexOutOfBoundsException if the {@code range} is invalid for the
	 *         given {@code list}
	 */
	public static <T> ListView<T>
	of(final List<T> list, final Range<Integer> range) {
		return new ListView<>(list, range);
	}

	/**
	 * Create a new list projection with all filtered elements.
	 *
	 * @param list the projecting list
	 * @param filter the element filter
	 * @return a new list projection with all filtered elements
	 * @param <T> the element type
	 */
	public static <T> ListView<T>
	of(final List<T> list, Predicate<? super T> filter) {
		requireNonNull(list);
		requireNonNull(filter);

		return new ListView<>(
			list,
			list.stream()
				.gather(Ranges.rangeOf(filter))
				.collect(Range.INTEGER.toRange())
		);
	}

	/**
	 * Return a flattened list projection of the given list of lists.
	 *
	 * @param lists the list of lists
	 * @return a flattened list projection
	 * @param <T> the element type
	 */
	public static <T> List<T> concat(final List<? extends List<T>> lists) {
		return new CompositeList<>(lists);
	}

	/**
	 * Return a flattened list projection of the given array of lists.
	 *
	 * @param lists the array of lists
	 * @return a flattened list projection
	 * @param <T> the element type
	 */
	@SafeVarargs
	public static <T> List<T> concat(final List<T>... lists) {
		return concat(Arrays.asList(lists));
	}

}
