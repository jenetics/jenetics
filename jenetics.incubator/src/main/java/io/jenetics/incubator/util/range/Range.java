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
import static java.util.function.Predicate.not;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.2
 * @since 9.2
 */
public sealed interface Range<T> extends Iterable<T>
	permits DenseRange, SparseRange
{

	final class Factory<T> {

		private final Integral<T> witness;
		private final DenseRange<T> emptyDense;
		private final SparseRange<T> emptySparse;

		private Factory(final Integral<T> witness) {
			this.witness = requireNonNull(witness);
			this.emptyDense = Range.dense(witness, witness.min(), witness.min());
			this.emptySparse = new SparseRange<>(witness, List.of());
		}

		public DenseRange<T> dense(T start, T end) {
			return Range.dense(witness, start, end);
		}

		public DenseRange<T> of(T element) {
			return  Range.of(witness, element);
		}

		@SafeVarargs
		public final Range<T> of(Range<T>... ranges) {
			return Range.of(witness, ranges);
		}

		public Range<T> of(List<? extends Range<T>> ranges) {
			return Range.of(witness, ranges);
		}

		public DenseRange<T> emptyDense() {
			return emptyDense;
		}

		public SparseRange<T> emptySparse() {
			return emptySparse;
		}

		public Range<T> empty() {
			return emptyDense;
		}

	}

	static <T> Factory<T> factory(final Integral<T> witness) {
		return new Factory<>(witness);
	}

	/**
	 * Return the number of elements of {@code this} range.
	 *
	 * @return the number of elements of {@code this} range
	 */
	long size();

	/**
	 * Returns the element at the specified position in this range.
	 *
	 * @param index index of the element to return
	 * @return the element at the specified position in this list
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         ({@code index < 0 || index >= size()})
	 */
	T get(long index);

	/**
	 * Tests whether {@code this} range is empty.
	 *
	 * @return {@code true} if {@code this} range is empty, {@code false}
	 *         otherwise
	 */
	default boolean isEmpty() {
		return size() == 0;
	}

	@Override
	default Iterator<T> iterator() {
		return stream().iterator();
	}

	/**
	 * Return a stream of the elements {@code this} range consists of.
	 *
	 * @return the local dates of {@code this} range
	 */
	Stream<T> stream();


	/* *********************************************************************
	 * Basic set operations.
	 * ********************************************************************/

	/**
	 * Tests whether the {@code other} range is covered by {@code this} range.
	 *
	 * @param other the {@code other} range to test
	 * @return {@code true} if the {@code other} is covered by {@code this}
	 *         composed range, {@code false} otherwise
	 */
	boolean contains(Range<T> other);

	/**
	 * Return a composite range, which is an intersection between {@code this}
	 * and the given {@code range}.
	 *
	 * @param other the range to intersect with
	 * @return the intersection between {@code this} and the given {@code range}
	 */
	Range<T> intersect(Range<T> other);

	/**
	 * Return the union of {@code this} range and the {@code other}.
	 *
	 * @param other the other range
	 * @return the union of {@code this} range and the {@code other}.
	 */
	Range<T> union(Range<T> other);

	/**
	 * Subtracts the {@code subtrahend} ranges from {@code this} range.
	 *
	 * @param subtrahend the ranges to subtract
	 * @return the range difference
	 */
	Range<T> difference(Range<T> subtrahend);


	/* *********************************************************************
	 * * Range factories
	 * ********************************************************************/


	/**
	 * Creates a new dense range with the given {@code start} and {@code end}.
	 *
	 * @param witness the witness that the type {@code T} is an integral type
	 * @param start the start (inclusively)
	 * @param end the end (exclusively)
	 * @return  a new continuous range
	 */
	static <T> DenseRange<T> dense(Integral<T> witness, T start, T end) {
		return new DenseRange<>(witness, start, end);
	}

	/**
	 * Return a new range consisting of the given {@code value}.
	 *
	 * @param witness the witness that the type {@code T} is an integral type
	 * @param value the element the created date range consists of
	 * @return a new range consisting of the given {@code value}
	 */
	static <T> DenseRange<T> of(Integral<T> witness, T value) {
		return new DenseRange<>(witness, value, witness.next(value));
	}

	/**
	 * Return a new range consisting of the given {@code values}.
	 *
	 * @param witness the witness that the type {@code T} is an integral type
	 * @param values the elements the created range consists of
	 * @return a new range consisting of the given {@code values}
	 */
	@SuppressWarnings("unchecked")
	static <T> Range<T> of(Integral<T> witness, T... values) {
		return of(
			witness,
			Stream.of(values)
				.map(v -> Range.of(witness, v))
				.toArray(Range[]::new)
		);
	}

	/**
	 * Create a new range consisting of the given subranges.
	 *
	 * @param witness the witness that the type {@code T} is an integral type
	 * @param ranges the subranges of the created range
	 * @return a new range consisting of the given subranges
	 */
	static <T> Range<T> of(Integral<T> witness, List<? extends Range<T>> ranges) {
		if (ranges.isEmpty()) {
			return new DenseRange<>(witness, witness.min(), witness.min());
		} else if (ranges.size() == 1 && ranges.getFirst() instanceof DenseRange<?>) {
			return ranges.getFirst();
		}

		return new SparseRange<>(
			witness,
			ranges.stream()
				.filter(not(Range::isEmpty))
				.<DenseRange<T>>mapMulti((range, consumer) -> {
					switch (range) {
						case DenseRange<T> r -> consumer.accept(r);
						case SparseRange<T> r -> r.ranges().forEach(consumer);
					}
				})
				.toList()
		)
			.simplify();
	}

	/**
	 * Create a new range consisting of the given subranges.
	 *
	 * @param witness the witness that the type {@code T} is an integral type
	 * @param ranges the subranges of the created range
	 * @return a new range consisting of the given subranges
	 */
	@SafeVarargs
	static <T> Range<T> of(Integral<T> witness, Range<T>... ranges) {
		return of(witness, Arrays.asList(ranges));
	}

}
