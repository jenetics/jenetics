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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.2
 * @since 9.2
 */
public final class SparseRange<T> implements Range<T> {

	private final Integral<T> witness;
	private final List<DenseRange<T>> ranges;


	SparseRange(final Integral<T> witness, final List<DenseRange<T>> ranges) {
		this.witness = requireNonNull(witness);
		this.ranges = normalize(ranges);
	}


	private List<DenseRange<T>> normalize(final List<DenseRange<T>> ranges) {
		if (ranges.isEmpty()) {
			return List.of();
		}
		if (isNormalized(ranges)) {
			return List.copyOf(ranges);
		}

		final var sorted = ranges.stream()
			.filter(not(Range::isEmpty))
			.sorted()
			.distinct()
			.toList();
		if (sorted.isEmpty()) {
			return List.of();
		}

		final var normalized = new ArrayList<DenseRange<T>>();

		var start = sorted.getFirst().start();
		var end = sorted.getFirst().end();

		for (final var range : sorted.subList(1, sorted.size())) {
			if (!witness.isAfter(range.start(), end)) {
				if (witness.isAfter(range.end(), end)) {
					end = range.end();
				}
			} else {
				normalized.add(new DenseRange<>(witness, start, end));
				start = range.start();
				end = range.end();
			}
		}
		normalized.add(new DenseRange<>(witness, start, end));

		return List.copyOf(normalized);
	}

	private boolean isNormalized(final List<DenseRange<T>> ranges) {
		DenseRange<T> previous = null;
		for (var range : ranges) {
			if (range.isEmpty() ||
				previous != null &&
				!witness.isAfter(range.start(), previous.end()))
			{
				return false;
			}
			previous = range;
		}

		return true;
	}

	public List<DenseRange<T>> ranges() {
		return ranges;
	}

	@Override
	public long size() {
		return ranges.stream().mapToLong(Range::size).sum();
	}

	@Override
	public T get(long index) {
		Objects.checkIndex(index, size());

		long sum = 0;
		long pos = index;
		for (final DenseRange<T> range : ranges) {
			sum += range.size();

			if (index < sum) {
				return range.get(pos);
			}
			pos -= range.size();
		}

		throw new AssertionError("Unreachable");
	}

	@Override
	public Stream<T> stream() {
		return ranges.stream()
			.flatMap(DenseRange::stream);
	}

	/* *********************************************************************
	 * * contains
	 * ********************************************************************/

	@Override
	public boolean contains(Range<T> other) {
		return switch (other) {
			case DenseRange<T> r -> contains(r);
			case SparseRange<T> r -> contains(r);
		};
	}

	public boolean contains(DenseRange<T> range) {
		return range.isEmpty() || ranges.stream()
			.takeWhile(candidate -> !witness.isAfter(candidate.start(), range.start()))
			.anyMatch(candidate -> candidate.contains(range));
	}

	public boolean contains(SparseRange<T> range) {
		return range.ranges.stream().allMatch(this::contains);
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

	public Range<T> intersect(DenseRange<T> other) {
		if (isEmpty() || other.isEmpty()) {
			return this;
		} else {
			final var intersects = ranges.stream()
				.map(range -> range.intersect(other))
				.filter(not(Range::isEmpty))
				.toList();

			return new SparseRange<>(witness, intersects).simplify();
		}

	}

	Range<T> simplify() {
		return ranges.size() == 1 ? ranges.getFirst() : this;
	}

	public Range<T> intersect(SparseRange<T> other) {
		if (isEmpty() || other.isEmpty()) {
			return this;
		} else {
			final List<DenseRange<T>> intersects = other.ranges.stream()
				.<DenseRange<T>>mapMulti((or, consumer) ->
					ranges.stream()
						.map(range -> range.intersect(or))
						.filter(not(Range::isEmpty))
						.forEach(consumer)
				)
				.toList();

			return new SparseRange<>(witness, intersects).simplify();
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
			return other.union(this);
		}
	}

	public Range<T> union(SparseRange<T> other) {
		if (isEmpty()) {
			return other;
		} else if (other.isEmpty()) {
			return this;
		} else {
			final var ranges = new ArrayList<>(other.ranges());
			ranges.addAll(this.ranges());
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
		if (isEmpty() || subtrahend.isEmpty()) {
			return this;
		}

		return difference(new SparseRange<>(witness, List.of(subtrahend)));
	}

	public Range<T> difference(final SparseRange<T> subtrahend) {
		if (isEmpty() || subtrahend.isEmpty()) {
			return this;
		}

		final var result = new ArrayList<DenseRange<T>>();
		var subtrahendIndex = 0;

		// Both lists are normalized, so their cursors only move forward.
		for (var range : ranges) {
			var start = range.start();

			while (subtrahendIndex < subtrahend.ranges.size() &&
				!witness.isAfter(subtrahend.ranges.get(subtrahendIndex).end(), start))
			{
				++subtrahendIndex;
			}

			while (subtrahendIndex < subtrahend.ranges.size() &&
				witness.isBefore(subtrahend.ranges.get(subtrahendIndex).start(), range.end()))
			{
				final var sub = subtrahend.ranges.get(subtrahendIndex);
				if (witness.isAfter(sub.start(), start)) {
					result.add(new DenseRange<>(witness, start, sub.start()));
				}

				if (!witness.isBefore(sub.end(), range.end())) {
					start = range.end();
					break;
				}

				if (witness.isAfter(sub.end(), start)) {
					start = sub.end();
				}
				++subtrahendIndex;
			}

			if (witness.isBefore(start, range.end())) {
				result.add(new DenseRange<>(witness, start , range.end()));
			}
		}

		return new SparseRange<>(witness, result).simplify();
	}


	@Override
	public int hashCode() {
		return ranges.size() == 1
			? ranges.getFirst().hashCode()
			: ranges.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return switch (obj) {
			case SparseRange<?> r -> (isEmpty() && r.isEmpty()) ||
				ranges.equals(r.ranges);
			case DenseRange<?> r -> (isEmpty() && r.isEmpty()) ||
				ranges.size() == 1 &&
				ranges.getFirst().start().equals(r.start()) &&
				ranges.getFirst().end().equals(r.end());
			case null, default -> false;
		};
	}

	@Override
	public String toString() {
		if (isEmpty()) {
			return "{}";
		} else if (ranges.size() == 1) {
			return ranges.getFirst().toString();
		} else {
			return ranges.stream()
				.map(DenseRange::toString)
				.collect(Collectors.joining(", ", "{", "}"));
		}
	}

}
