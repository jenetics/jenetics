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
package io.jenetics.incubator.util;

import static java.util.function.Predicate.not;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.2
 * @since 9.2
 */
public sealed interface DateRange extends Iterable<LocalDate> {

	/**
	 * Return the number of days of {@code this} date range.
	 *
	 * @return the number of days of {@code this} date range
	 */
	long days();

	/**
	 * Tests whether {@code this} date range is empty.
	 *
	 * @return {@code true} if {@code this} date range is empty, {@code false}
	 *         otherwise
	 */
	default boolean isEmpty() {
		return days() == 0;
	}

	@Override
	default Iterator<LocalDate> iterator() {
		return stream().iterator();
	}

	/**
	 * Return a stream of the dates {@code this} date range consists of.
	 *
	 * @return the local dates of {@code this} date range
	 */
	Stream<LocalDate> stream();

	/* *********************************************************************
	 * Basic set operations.
	 * ********************************************************************/

	/**
	 * Tests whether the {@code other} date range is covered by {@code this},
	 * date range.
	 *
	 * @param other the {@code other} range to test
	 * @return {@code true} if the {@code other} is covered by {@code this}
	 *         composed range, {@code false} otherwise
	 */
	boolean contains(DateRange other);

	/**
	 * Return a composite date range, which is an intersection between {@code this}
	 * and the given continuous date {@code range}.
	 *
	 * @param other the continuous date range to intersect with
	 * @return the intersection between {@code this} and the given {@code range}
	 */
	DateRange intersect(DateRange other);

	/**
	 * Return the union of {@code this} date range and the {@code other}.
	 *
	 * @param other the other date range
	 * @return the union of {@code this} date range and the {@code other}.
	 */
	DateRange union(DateRange other);

	/**
	 * Subtracts the {@code subtrahend} date ranges from {@code this} date range.
	 *
	 * @param subtrahend the date ranges to subtract
	 * @return the date range difference
	 */
	DateRange difference(DateRange subtrahend);


	/**
	 * Creates a new continuous date range with the given {@code start} and
	 * {@code end} date.
	 *
	 * @param start the start date (inclusively)
	 * @param end the end date (exclusively)
	 * @return  a new continuous date range
	 */
	static Continuous range(LocalDate start, LocalDate end) {
		return new Continuous(start, end);
	}

	/**
	 * Return a new date range consisting of the given {@code date}.
	 *
	 * @param date the element the created date range consists of
	 * @return a new date range consisting of the given {@code date}
	 */
	static Continuous of(LocalDate date) {
		return new Continuous(date, date.plusDays(1));
	}

	/**
	 * Return a new date range consisting of the given {@code dates}.
	 *
	 * @param dates the elements the created date range consists of
	 * @return a new date range consisting of the given {@code dates}
	 */
	static DateRange of(LocalDate... dates) {
		return of(
			Stream.of(dates)
				.map(DateRange::of)
				.toArray(DateRange[]::new)
		);
	}

	/**
	 * Create a new date range consisting of the given subranges.
	 *
	 * @param ranges the subranges of the created date range
	 * @return a new date range consisting of the given subranges
	 */
	static DateRange of(DateRange... ranges) {
		if (ranges.length == 0) {
			return Composite.EMPTY;
		} else if (ranges.length == 1 && ranges[0] instanceof Continuous) {
			return ranges[0];
		}

		return new Composite(
			Stream.of(ranges)
				.filter(not(DateRange::isEmpty))
				.<Continuous>mapMulti((range, consumer) -> {
					switch (range) {
						case Continuous r -> consumer.accept(r);
						case Composite r -> r.ranges().forEach(consumer);
					}
				})
				.toList()
		).simplify();
	}


	/**
	 * A <em>continous</em> date range.
	 *
	 * @param start the start date (inclusively)
	 * @param end the end date (exclusively)
	 */
	record Continuous(LocalDate start, LocalDate end)
		implements DateRange, Comparable<Continuous>
	{

		public static final Continuous
			EMPTY =
			new Continuous(LocalDate.of(0, 1, 1), LocalDate.of(0, 1, 1));

		/**
		 * Create a new continuous date range object.
		 *
		 * @param start the start date (inclusively)
		 * @param end the end date (exclusively)
		 * @throws IllegalArgumentException if {@code end < start}
		 */
		public Continuous {
			if (end.isBefore(start)) {
				throw new IllegalArgumentException(
					"End date is before start date: %s < %s."
						.formatted(end, start)
				);
			}

			if (start.equals(end)) {
				start = LocalDate.of(0, 1, 1);
				end = LocalDate.of(0, 1, 1);
			}
		}

		@Override
		public long days() {
			return ChronoUnit.DAYS.between(start, end);
		}

		@Override
		public boolean isEmpty() {
			return start.equals(end);
		}

		/* *********************************************************************
		 * * contains
		 * ********************************************************************/

		@Override
		public boolean contains(DateRange other) {
			return switch (other) {
				case Continuous range -> contains(range);
				case Composite range -> range.ranges.stream().allMatch(this::contains);
			};
		}

		/**
		 * Tests whether the given date {@code range} is covered by {@code this},
		 * date range.
		 *
		 * @param other the range to test
		 * @return {@code true} if the {@code range} is covered by {@code this}
		 *         composed range, {@code false} otherwise
		 */
		public boolean contains(Continuous other) {
			return other.isEmpty() ||
				!isEmpty() &&
				!start.isAfter(other.start) &&
				!end.isBefore(other.end);
		}

		/**
		 * Tests whether the give {@code date} is covered by {@code this} date range.
		 *
		 * @param date the date to test
		 * @return {@code true} if the {@code date} is covered by {@code this}
		 *         composed range, {@code false} otherwise
		 */
		public boolean contains(LocalDate date) {
			return !start.isAfter(date) && end.isAfter(date);
		}

		/* *********************************************************************
		 * * intersect
		 * ********************************************************************/

		@Override
		public DateRange intersect(DateRange other) {
			return switch (other) {
				case Continuous r -> intersect(r);
				case Composite r -> r.intersect(this);
			};
		}

		public Continuous intersect(Continuous other) {
			if (isEmpty() ||
				other.isEmpty() ||
				!end.isAfter(other.start) ||
				!start.isBefore(other.end))
			{
				return EMPTY;
			} else {
				return DateRange.range(
					start.isAfter(other.start) ? start : other.start,
					end.isBefore(other.end) ? end : other.end
				);
			}
		}

		public DateRange intersect(Composite other) {
			return other.intersect(this);
		}

		/* *********************************************************************
		 * * union
		 * ********************************************************************/

		@Override
		public DateRange union(DateRange other) {
			return switch (other) {
				case Continuous r -> union(r);
				case Composite r -> union(r);
			};
		}

		public DateRange union(Continuous other) {
			if (isEmpty()) {
				return other;
			} else if (other.isEmpty()) {
				return this;
			} else {
				if (contains(other.start) || contains(other.end)) {
					return DateRange.range(
						start.isBefore(other.start) ? start : other.start,
						end.isAfter(other.end) ? end : other.end
					);
				} else {
					return new Composite(List.of(this, other)).simplify();
				}
			}
		}

		public DateRange union(Composite other) {
			if (isEmpty()) {
				return other;
			} else if (other.isEmpty()) {
				return this;
			} else {
				final var ranges = new ArrayList<>(other.ranges);
				ranges.add(this);
				return new Composite(ranges).simplify();
			}
		}

		/* *********************************************************************
		 * * difference
		 * ********************************************************************/

		@Override
		public DateRange difference(DateRange other) {
			return switch (other) {
				case Continuous r -> difference(r);
				case Composite r -> difference(r);
			};
		}

		public DateRange difference(final Continuous subtrahend) {
			if (isEmpty() || subtrahend.isEmpty() ||
				!subtrahend.end.isAfter(start) ||
				!subtrahend.start.isBefore(end))
			{
				return this;
			} else if (!subtrahend.start.isAfter(start) &&
				!subtrahend.end.isBefore(end))
			{
				return EMPTY;
			} else if (subtrahend.start.isAfter(start) &&
				subtrahend.end.isBefore(end))
			{
				return DateRange.of(
					DateRange.range(start,  subtrahend.start),
					DateRange.range(subtrahend.end, end)
				);
			} else if (subtrahend.start.isAfter(start)) {
				return DateRange.range(start, subtrahend.start);
			} else {
				return DateRange.range(subtrahend.end, end);
			}
		}

		public DateRange difference(final Composite subtrahend) {
			if (isEmpty() || subtrahend.isEmpty()) {
				return this;
			} else {
				return new Composite(List.of(this)).difference(subtrahend);
			}
		}

		@Override
		public int compareTo(final Continuous other) {
			return start.compareTo(other.start());
		}

		@Override
		public Stream<LocalDate> stream() {
			return LongStream.range(0, days())
				.mapToObj(start::plusDays);
		}


		@Override
		public int hashCode() {
			return Objects.hash(start, end);
		}

		@Override
		public boolean equals(final Object obj) {
			return switch (obj) {
				case Continuous r -> (isEmpty() && r.isEmpty()) ||
					start.equals(r.start) &&
					end.equals(r.end);
				case Composite r -> (isEmpty() && r.isEmpty()) ||
					r.ranges.size() == 1 &&
					start.equals(r.ranges.getFirst().start) &&
					end.equals(r.ranges.getFirst().end);
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

	/**
	 * A date-range, which is the composition of a list of <em>continuous</em>
	 * {@link DateRange} objects.
	 *
	 * @param ranges the individual date-ranges this composed date-range consist of.
	 *        The date-range parts may be overlapping. The ranges will be sorted in
	 *        ascending order according its start date. The ranges will be normalized.
	 */
	record Composite(List<Continuous> ranges) implements DateRange {

		public static final Composite EMPTY = new Composite(List.of());

		public Composite {
			ranges = normalize(ranges);
		}

		private static List<Continuous> normalize(final List<Continuous> ranges) {
			if (ranges.isEmpty()) {
				return List.of();
			}
			if (isNormalized(ranges)) {
				return List.copyOf(ranges);
			}

			final var sorted = ranges.stream()
				.filter(not(DateRange::isEmpty))
				.sorted()
				.distinct()
				.toList();
			if (sorted.isEmpty()) {
				return List.of();
			}

			final var normalized = new ArrayList<Continuous>();

			var start = sorted.getFirst().start();
			var end = sorted.getFirst().end();

			for (final var range : sorted.subList(1, sorted.size())) {
				if (!range.start().isAfter(end)) {
					if (range.end().isAfter(end)) {
						end = range.end();
					}
				} else {
					normalized.add(DateRange.range(start, end));
					start = range.start();
					end = range.end();
				}
			}
			normalized.add(DateRange.range(start, end));

			return List.copyOf(normalized);
		}

		private static boolean isNormalized(final List<Continuous> ranges) {
			Continuous previous = null;
			for (var range : ranges) {
				if (range.isEmpty() ||
					previous != null && !range.start().isAfter(previous.end()))
				{
					return false;
				}
				previous = range;
			}

			return true;
		}

		@Override
		public long days() {
			return ranges.stream().mapToLong(DateRange::days).sum();
		}

		@Override
		public boolean isEmpty() {
			return ranges.isEmpty();
		}

		/* *********************************************************************
		 * * contains
		 * ********************************************************************/

		@Override
		public boolean contains(DateRange other) {
			return switch (other) {
				case Continuous r -> contains(r);
				case Composite r -> contains(r);
			};
		}

		public boolean contains(Continuous range) {
			if (range.isEmpty()) {
				return true;
			} else if (isEmpty()) {
				return false;
			} else {
				final LocalDate coveredUntil = ranges.stream()
					.reduce(
						range.start(),
						(end, subrange) ->
							!subrange.start().isAfter(end) && end.isBefore(subrange.end())
								? subrange.end()
								: end,
						(_, _) -> {
							throw new UnsupportedOperationException(
								"No parallel streams allowed."
							);
						}
					);

				return !coveredUntil.isBefore(range.end());
			}
		}

		public boolean contains(Composite range) {
			return range.ranges.stream().allMatch(this::contains);
		}

		/* *********************************************************************
		 * * intersect
		 * ********************************************************************/

		@Override
		public DateRange intersect(DateRange other) {
			return switch (other) {
				case Continuous r -> intersect(r);
				case Composite r -> intersect(r);
			};
		}

		public DateRange intersect(Continuous other) {
			if (isEmpty() || other.isEmpty()) {
				return EMPTY;
			} else {
				final var intersects = ranges.stream()
					.map(range -> range.intersect(other))
					.filter(not(DateRange::isEmpty))
					.toList();

				return new Composite(intersects).simplify();
			}

		}

		private DateRange simplify() {
			return ranges.size() == 1 ? ranges.getFirst() : this;
		}

		public DateRange intersect(Composite other) {
			if (isEmpty() || other.isEmpty()) {
				return EMPTY;
			} else {
				final List<Continuous> intersects = other.ranges.stream()
					.<Continuous>mapMulti((or, consumer) ->
						ranges.stream()
							.map(range -> range.intersect(or))
							.filter(not(DateRange::isEmpty))
							.forEach(consumer)
					)
					.toList();

				return new Composite(intersects).simplify();
			}
		}

		/* *********************************************************************
		 * * union
		 * ********************************************************************/

		@Override
		public DateRange union(DateRange other) {
			return switch (other) {
				case Continuous r -> union(r);
				case Composite r -> union(r);
			};
		}

		public DateRange union(Continuous other) {
			if (isEmpty()) {
				return other;
			} else if (other.isEmpty()) {
				return this;
			} else {
				return other.union(this);
			}
		}

		public DateRange union(Composite other) {
			if (isEmpty()) {
				return other;
			} else if (other.isEmpty()) {
				return this;
			} else {
				final var ranges = new ArrayList<>(other.ranges);
				ranges.addAll(this.ranges);
				return new Composite(ranges).simplify();
			}
		}

		/* *********************************************************************
		 * * difference
		 * ********************************************************************/

		@Override
		public DateRange difference(DateRange other) {
			return switch (other) {
				case Continuous r -> difference(r);
				case Composite r -> difference(r);
			};
		}

		public DateRange difference(final Continuous subtrahend) {
			if (isEmpty() || subtrahend.isEmpty()) {
				return this;
			}

			return difference(new Composite(List.of(subtrahend)));
		}

		public DateRange difference(final Composite subtrahend) {
			if (isEmpty() || subtrahend.isEmpty()) {
				return this;
			}

			final var result = new ArrayList<Continuous>();
			var subtrahendIndex = 0;

			// Both lists are normalized, so their cursors only move forward.
			for (var range : ranges) {
				var start = range.start();

				while (subtrahendIndex < subtrahend.ranges.size() &&
					!subtrahend.ranges.get(subtrahendIndex).end().isAfter(start))
				{
					++subtrahendIndex;
				}

				while (subtrahendIndex < subtrahend.ranges.size() &&
					subtrahend.ranges.get(subtrahendIndex).start().isBefore(range.end()))
				{
					final var sub = subtrahend.ranges.get(subtrahendIndex);
					if (sub.start().isAfter(start)) {
						result.add(DateRange.range(start, sub.start()));
					}

					if (!sub.end().isBefore(range.end())) {
						start = range.end();
						break;
					}

					if (sub.end().isAfter(start)) {
						start = sub.end();
					}
					++subtrahendIndex;
				}

				if (start.isBefore(range.end())) {
					result.add(DateRange.range(start, range.end()));
				}
			}

			return new Composite(result).simplify();
		}


		@Override
		public Stream<LocalDate> stream() {
			return ranges.stream()
				.flatMap(Continuous::stream);
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
				case Composite r -> (isEmpty() && r.isEmpty()) ||
					ranges.equals(r.ranges);
				case Continuous r -> (isEmpty() && r.isEmpty()) ||
					ranges.size() == 1 &&
					ranges.getFirst().start.equals(r.start) &&
					ranges.getFirst().end.equals(r.end);
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
					.map(Continuous::toString)
					.collect(Collectors.joining(", ", "{", "}"));
			}
		}

	}

}
