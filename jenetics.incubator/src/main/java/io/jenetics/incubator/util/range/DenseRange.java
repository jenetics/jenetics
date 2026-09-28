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
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A continuous, dense range of elements between [{@link #start()}, {@link #end()}).
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.2
 * @since 9.2
 */
public final class DenseRange<T> implements Range<T>, Comparable<DenseRange<T>> {

	private final Integral<T> witness;
	private final T start;
	private final T end;

	/**
	 * Create a new <em>dens</em> range object.
	 *
	 * @param witness the witness that the type {@code T} is an integral type
	 * @param start the start (inclusively)
	 * @param end the end (exclusively)
	 * @throws IllegalArgumentException if {@code end < start}
	 */
	DenseRange(Integral<T> witness, T start, T end) {
		this.witness = requireNonNull(witness);

		if (witness.isBefore(end, start)) {
			throw new IllegalArgumentException(
				"End is before start: %s < %s."
					.formatted(end, start)
			);
		}

		if (start.equals(end)) {
			this.start = witness.zero();
			this.end = witness.zero();
		} else {
			this.start = start;
			this.end = end;
		}
	}

	/**
	 * Return the start element of the range (inclusively).
	 *
	 * @return the start element of the range (inclusively)
	 */
	public T start() {
		return start;
	}

	/**
	 * Return the end of the range (exclusively).
	 *
	 * @return the end of the range (exclusively)
	 */
	public T end() {
		return end;
	}

	@Override
	public long size() {
		return witness.distance(start, end);
	}

	@Override
	public T get(long index) {
		Objects.checkIndex(index, size());
		return witness.next(start, index);
	}

	@Override
	public Stream<T> stream() {
		return Stream.iterate(start, witness::next)
			.limit(size());
	}

	/* *********************************************************************
	 * * contains
	 * ********************************************************************/

	@Override
	public boolean contains(Range<T> other) {
		return switch (other) {
			case DenseRange<T> range -> contains(range);
			case SparseRange<T> range -> range.ranges().stream().allMatch(this::contains);
		};
	}

	/**
	 * Tests whether the given {@code range} is covered by {@code this} range.
	 *
	 * @param other the range to test
	 * @return {@code true} if the {@code range} is covered by {@code this}
	 *         composed range, {@code false} otherwise
	 */
	public boolean contains(DenseRange<T> other) {
		return other.isEmpty() ||
			!isEmpty() &&
			!witness.isAfter(start, other.start) &&
			!witness.isBefore(end, other.end);
	}

	/**
	 * Tests whether the give {@code value} is covered by {@code this} range.
	 *
	 * @param value the date to test
	 * @return {@code true} if the {@code date} is covered by {@code this}
	 *         composed range, {@code false} otherwise
	 */
	public boolean contains(T value) {
		return !witness.isAfter(start, value) && witness.isAfter(end, value);
	}

	/* *********************************************************************
	 * * intersect
	 * ********************************************************************/

	@Override
	public Range<T> intersect(Range<T> other) {
		return switch (other) {
			case DenseRange<T> r -> intersect(r);
			case SparseRange<T> r -> intersect(r);
		};
	}

	public DenseRange<T> intersect(DenseRange<T> other) {
		if (isEmpty() ||
			other.isEmpty() ||
			!witness.isAfter(end, other.start) ||
			!witness.isBefore(start, other.end))
		{
			return new DenseRange<>(witness, witness.zero(), witness.zero());
		} else {
			return new DenseRange<>(
				witness,
				witness.isAfter(start, other.start) ? start : other.start,
				witness.isBefore(end, other.end) ? end : other.end
			);
		}
	}

	public Range<T> intersect(SparseRange<T> other) {
		if (isEmpty()) {
			return this;
		} else if (other.isEmpty()) {
			return other;
		} else {
			return other.intersect(this);
		}
	}

	/* *********************************************************************
	 * * union
	 * ********************************************************************/

	@Override
	public Range<T> union(Range<T> other) {
		return switch (other) {
			case DenseRange<T> r -> union(r);
			case SparseRange<T> r -> union(r);
		};
	}

	public Range<T> union(DenseRange<T> other) {
		if (isEmpty()) {
			return other;
		} else if (other.isEmpty()) {
			return this;
		} else {
			if (contains(other.start) || contains(other.end)) {
				return new DenseRange<>(
					witness,
					witness.isBefore(start, other.start) ? start : other.start,
					witness.isAfter(end, other.end) ? end : other.end
				);
			} else {
				return new SparseRange<>(witness, List.of(this, other)).simplify();
			}
		}
	}

	public Range<T> union(SparseRange<T> other) {
		if (isEmpty()) {
			return other;
		} else if (other.isEmpty()) {
			return this;
		} else {
			final var ranges = new ArrayList<>(other.ranges());
			ranges.add(this);
			return new SparseRange<>(witness, ranges).simplify();
		}
	}

	/* *********************************************************************
	 * * difference
	 * ********************************************************************/

	@Override
	public Range<T> difference(Range<T> subtrahend) {
		return switch (subtrahend) {
			case DenseRange<T> r -> difference(r);
			case SparseRange<T> r -> difference(r);
		};
	}

	public Range<T> difference(final DenseRange<T> subtrahend) {
		if (isEmpty() || subtrahend.isEmpty() ||
			!witness.isAfter(subtrahend.end, start) ||
			!witness.isBefore(subtrahend.start, end))
		{
			return this;
		} else if (!witness.isAfter(subtrahend.start, start) &&
			!witness.isBefore(subtrahend.end, end))
		{
			return new DenseRange<>(witness, witness.zero(), witness.zero());
		} else if (witness.isAfter(subtrahend.start, start) &&
			witness.isBefore(subtrahend.end, end))
		{
			return new SparseRange<>(
				witness,
				List.of(
					new DenseRange<>(witness, start,  subtrahend.start),
					new DenseRange<>(witness, subtrahend.end, end)
				)
			);
		} else if (witness.isAfter(subtrahend.start, start)) {
			return new DenseRange<>(witness, start, subtrahend.start);
		} else {
			return new DenseRange<>(witness, subtrahend.end, end);
		}
	}

	public Range<T> difference(final SparseRange<T> subtrahend) {
		if (isEmpty() || subtrahend.isEmpty()) {
			return this;
		} else {
			return new SparseRange<>(witness, List.of(this)).difference(subtrahend);
		}
	}

	@Override
	public int compareTo(final DenseRange<T> other) {
		return witness.compare(start, other.start);
	}


	@Override
	public int hashCode() {
		return Objects.hash(start, end);
	}

	@Override
	public boolean equals(final Object obj) {
		return switch (obj) {
			case DenseRange<?> r -> (isEmpty() && r.isEmpty()) ||
				start.equals(r.start) &&
				end.equals(r.end);
			case SparseRange<?> r -> (isEmpty() && r.isEmpty()) ||
				r.ranges().size() == 1 &&
				start.equals(r.ranges().getFirst().start) &&
				end.equals(r.ranges().getFirst().end);
			case null, default -> false;
		};
	}

	@Override
	public String toString() {
		return isEmpty()
			? "{}"
			: "[%s, %s)".formatted(start, end);
	}

}
