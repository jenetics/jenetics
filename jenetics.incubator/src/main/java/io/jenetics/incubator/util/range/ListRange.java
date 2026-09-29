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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Gatherer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.2
 * @since 9.2
 */
public record ListRange<T>(List<T> list, Range<Integer> range) implements List<T> {

	public ListRange {
		Objects.checkIndex(range.start(), list.size());
		Objects.checkIndex(range.end() - 1, list.size());
	}

	public ListRange(List<T> list) {
		this(list, Range.INTEGER.dense(0, list.size()));
	}

	public int size() {
		return (int)range.size();
	}

	@Override
	public boolean isEmpty() {
		return range.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

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
	public int indexOf(Object o) {
		return 0;
	}

	@Override
	public int lastIndexOf(Object o) {
		return 0;
	}

	@Override
	public ListIterator<T> listIterator() {
		return null;
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return null;
	}

	@Override
	public ListRange<T> subList(int fromIndex, int toIndex) {
		final var sub = Range.INTEGER.dense(fromIndex, toIndex);
		return new ListRange<>(list, range.intersect(sub));
	}

	public ListRange<T> subList(final Range<Integer>... ranges) {
		final var sub = Range.INTEGER.of(ranges);
		if (sub.contains(range)) {
			return this;
		} else {
			return new ListRange<>(list, range.intersect(sub));
		}
	}

	@Override
	public Iterator<T> iterator() {
		return stream().iterator();
	}

	@Override
	public Object[] toArray() {
		return stream().toArray();
	}

	@Override
	public <T1> T1[] toArray(T1[] a) {
		return toList().toArray(a);
	}

	public List<T> toList() {
		return stream().toList();
	}

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
		return false;
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

	@Override
	public Stream<T> stream() {
		return range.stream().map(list::get);
	}

	public ListRange<T> intersect(final Range<Integer> range) {
		return new ListRange<>(list, range.intersect(range));
	}

	public static <T> ListRange<T> of(final List<T> list, Predicate<? super T> predicate) {
		requireNonNull(predicate);

		final Range<Integer> range = Range.INTEGER.of(
			list.stream()
				.gather(rangeOf(predicate))
				.toList()
		);

		return new ListRange<>(list, range);
	}

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

	static void main() {
		final var list = IntStream.range(0, 100)
			.mapToObj(i -> i%10 == 0 ? "value" : null)
			.toList();

		var sparse = ListRange.of(list, Objects::isNull);
		for (final var element : sparse) {
			if (element != null) {
				System.out.println("ERROR: " +element);
			}
		}

		sparse = ListRange.of(list, Predicate.not(Objects::isNull));
		for (final var element : sparse) {
			if (element == null) {
				System.out.println("ERROR: " + element);
			}
		}
	}

}
