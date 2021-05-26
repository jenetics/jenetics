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

import static java.time.Clock.systemUTC;
import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.Duration;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * This class contains factory methods for (flat) mapping stream elements. The
 * functions of this class can be used in the following way.
 *
 * <pre>{@code
 * final ISeq<Integer> values = new Random().ints(0, 100).boxed()
 *     .limit(100)
 *     .flatMap(Streams.toIntervalMax(13))
 *     .collect(ISeq.toISeq());
 * }</pre>
 * The example above will create the following output
 * <pre>{@code
 *          +----3---+----3---+
 *          |        |        |
 *     +----9--8--3--3--5--4--2--9----|
 *        toIntervalMax(3)
 *     +----------9--------5----------|
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 6.0
 * @version !__version__!
 */
public final class Streams {
	private Streams() {}


	/**
	 * Return a new flat-mapper function, which guarantees a strictly increasing
	 * stream, from an arbitrarily ordered source stream. Note that this
	 * function doesn't sort the stream. It <em>just</em> skips the <em>out of
	 * order</em> elements.
	 *
	 * <pre>{@code
	 *     +----3--2--5--4--7--7--4--9----|
	 *        toStrictlyIncreasing()
	 *     +----3-----5-----7--------9----|
	 * }</pre>
	 *
	 * <pre>{@code
	 * final ISeq<Integer> values = new Random().ints(0, 100)
	 *     .boxed()
	 *     .limit(100)
	 *     .flatMap(Streams.toStrictlyIncreasing())
	 *     .collect(ISeq.toISeq());
	 *
	 * System.out.println(values);
	 * // [6,47,65,78,96,96,99]
	 * }</pre>
	 *
	 *
	 * @param <C> the comparable type
	 * @return a new flat-mapper function
	 */
	public static <C extends Comparable<? super C>>
	Function<C, Stream<C>> toStrictlyIncreasing() {
		return strictlyImproving(Streams::max, r -> false);
	}

	/**
	 * Return a new flat-mapper function, which guarantees a strictly increasing
	 * stream, from an arbitrarily ordered source stream. Note that this
	 * function doesn't sort the stream. It <em>just</em> skips the <em>out of
	 * order</em> elements.
	 *
	 * @since !__version__!
	 *
	 * @see #toStrictlyIncreasing()
	 *
	 * @param <C> the comparable type
	 * @param reset a predicate which decides when the best element found so
	 *        far is reset. The improving element streams starts from fresh.
	 * @return a new flat-mapper function
	 */
	public static <C extends Comparable<? super C>>
	Function<C, Stream<C>> toStrictlyIncreasing(final Predicate<? super C> reset) {
		return strictlyImproving(Streams::max, reset);
	}

	/**
	 * Return a new flat-mapper function, which guarantees a strictly decreasing
	 * stream, from an arbitrarily ordered source stream. Note that this
	 * function doesn't sort the stream. It <em>just</em> skips the <em>out of
	 * order</em> elements.
	 *
	 * <pre>{@code
	 *     +----9--8--9--5--6--6--2--9----|
	 *        toStrictlyDecreasing()
	 *     +----9--8-----5--------2-------|
	 * }</pre>
	 *
	 * <pre>{@code
	 * final ISeq<Integer> values = new Random().ints(0, 100)
	 *     .boxed()
	 *     .limit(100)
	 *     .flatMap(Streams.toStrictlyDecreasing())
	 *     .collect(ISeq.toISeq());
	 *
	 * System.out.println(values);
	 * // [45,32,15,12,3,1]
	 * }</pre>
	 *
	 * @param <C> the comparable type
	 * @return a new flat-mapper function
	 */
	public static <C extends Comparable<? super C>>
	Function<C, Stream<C>> toStrictlyDecreasing() {
		return strictlyImproving(Streams::min, r -> false);
	}

	/**
	 * Return a new flat-mapper function, which guarantees a strictly decreasing
	 * stream, from an arbitrarily ordered source stream. Note that this
	 * function doesn't sort the stream. It <em>just</em> skips the <em>out of
	 * order</em> elements.
	 *
	 * @since !__version__!
	 *
	 * @see #toStrictlyDecreasing()
	 *
	 * @param <C> the comparable type
	 * @param reset a predicate which decides when the best element found so
	 *        far is reset. The improving element streams starts from fresh.
	 * @return a new flat-mapper function
	 */
	public static <C extends Comparable<? super C>>
	Function<C, Stream<C>> toStrictlyDecreasing(final Predicate<? super C> reset) {
		return strictlyImproving(Streams::min, reset);
	}

	/**
	 * Return a new flat-mapper function, which guarantees a strictly improving
	 * stream, from an arbitrarily ordered source stream. Note that this
	 * function doesn't sort the stream. It <em>just</em> skips the <em>out of
	 * order</em> elements.
	 *
	 * <pre>{@code
	 * final ISeq<Integer> values = new Random().ints(0, 100)
	 *     .boxed()
	 *     .limit(100)
	 *     .flatMap(Streams.toStrictlyImproving(Comparator.naturalOrder()))
	 *     .collect(ISeq.toISeq());
	 *
	 * System.out.println(values);
	 * // [6,47,65,78,96,96,99]
	 * }</pre>
	 *
	 * @see #toStrictlyIncreasing()
	 * @see #toStrictlyDecreasing()
	 *
	 * @param <T> the element type
	 * @param comparator the comparator used for testing the elements
	 * @return a new flat-mapper function
	 */
	public static <T> Function<T, Stream<T>>
	toStrictlyImproving(final Comparator<? super T> comparator) {
		return strictlyImproving((a, b) -> best(comparator, a, b), r -> false);
	}

	/**
	 * Return a new flat-mapper function, which guarantees a strictly improving
	 * stream, from an arbitrarily ordered source stream. Note that this
	 * function doesn't sort the stream. It <em>just</em> skips the <em>out of
	 * order</em> elements.
	 *
	 * @since !__version__!
	 *
	 * @see #toStrictlyImproving(Comparator)
	 *
	 * @param <T> the element type
	 * @param comparator the comparator used for testing the elements
	 * @param reset a predicate which decides when the best element found so
	 *        far is reset. The improving element streams starts from fresh.
	 * @return a new flat-mapper function
	 */
	public static <T> Function<T, Stream<T>> toStrictlyImproving(
		final Comparator<? super T> comparator,
		final Predicate<? super T> reset
	) {
		return strictlyImproving((a, b) -> best(comparator, a, b), reset);
	}

	private static <C> Function<C, Stream<C>>
	strictlyImproving(
		final BinaryOperator<C> comparator,
		final Predicate<? super C> reset
	) {
		requireNonNull(comparator);

		return new Function<>() {
			private C _best;

			@Override
			public Stream<C> apply(final C result) {
				if (reset.test(result)) {
					_best = null;
				}

				final C best = comparator.apply(_best, result);

				final Stream<C> stream = best == _best
					? Stream.empty()
					: Stream.of(best);

				_best = best;

				return stream;
			}
		};
	}

	private static <T extends Comparable<? super T>> T max(final T a, final T b) {
		return best(Comparator.naturalOrder(), a, b);
	}

	private static <T extends Comparable<? super T>> T min(final T a, final T b) {
		return best(Comparator.reverseOrder(), a, b);
	}

	private static <T>
	T best(final Comparator<? super T> comparator, final T a, final T b) {
		if (a == null && b == null) return null;
		if (a == null) return b;
		if (b == null) return a;
		return comparator.compare(a, b) >= 0 ? a : b;
	}

	/**
	 * Return a new flat-mapper function which returns (emits) the maximal value
	 * of the last <em>n</em> elements.
	 *
	 * <pre>{@code
	 *          +----3---+----3---+
	 *          |        |        |
	 *     +----9--8--3--3--5--4--2--9----|
	 *        toIntervalMax(3)
	 *     +----------9--------5----------|
	 * }</pre>
	 *
	 * @param size the size of the slice
	 * @param <C> the element type
	 * @return a new flat-mapper function
	 * @throws IllegalArgumentException if the given size is smaller than one
	 */
	public static <C extends Comparable<? super C>>
	Function<C, Stream<C>> toIntervalMax(final int size) {
		return sliceBest(Streams::max, size);
	}

	/**
	 * Return a new flat-mapper function which returns (emits) the minimal value
	 * of the last <em>n</em> elements.
	 *
	 * <pre>{@code
	 *          +----3---+----3---+
	 *          |        |        |
	 *     +----9--8--3--3--1--4--2--9----|
	 *        toIntervalMin(3)
	 *     +----------3--------1----------|
	 * }</pre>
	 *
	 * @param size the size of the slice
	 * @param <C> the element type
	 * @return a new flat-mapper function
	 * @throws IllegalArgumentException if the given size is smaller than one
	 */
	public static <C extends Comparable<? super C>>
	Function<C, Stream<C>> toIntervalMin(final int size) {
		return sliceBest(Streams::min, size);
	}

	/**
	 * Return a new flat-mapper function which returns (emits) the minimal value
	 * of the last <em>n</em> elements.
	 *
	 * @see #toIntervalMax(int)
	 * @see #toIntervalMin(int)
	 *
	 * @param <C> the element type
	 * @param size the size of the slice
	 * @param comparator the comparator used for testing the elements
	 * @return a new flat-mapper function
	 * @throws IllegalArgumentException if the given size is smaller than one
	 * @throws NullPointerException if the given {@code comparator} is
	 *         {@code null}
	 */
	public static <C> Function<C, Stream<C>>
	toIntervalBest(final Comparator<? super C> comparator, final int size) {
		requireNonNull(comparator);
		return sliceBest((a, b) -> best(comparator, a, b), size);
	}

	private static <C> Function<C, Stream<C>> sliceBest(
		final BinaryOperator<C> comparator,
		final int rangeSize
	) {
		requireNonNull(comparator);
		if (rangeSize < 1) {
			throw new IllegalArgumentException(
				"Range size must be at least one: " + rangeSize
			);
		}

		final AtomicInteger count = new AtomicInteger();
		final Predicate<C> finished = t -> {
			if (count.incrementAndGet() < rangeSize) {
				return true;
			} {
				count.set(0);
				return false;
			}
		};
		return sliceBest(comparator, finished);
	}

	/**
	 * Return a new flat-mapper function which returns (emits) the maximal value
	 * of the elements emitted within the given {@code timespan}.
	 *
	 * <pre>{@code
	 *          +---3s---+---3s---+
	 *          |        |        |
	 *     +----9--8--3--3--5--4--2--9----|
	 *        toIntervalMax(3s)
	 *     +----------9--------5----------|
	 * }</pre>
	 *
	 * @see #toIntervalMax(Duration, Clock)
	 *
	 * @param <C> the element type
	 * @param timespan the timespan the elements are collected for the
	 *        calculation slice
	 * @return a new flat-mapper function
	 * @throws IllegalArgumentException if the given size is smaller than one
	 * @throws NullPointerException if the given {@code timespan} is {@code null}
	 */
	public static <C extends Comparable<? super C>>
	Function<C, Stream<C>> toIntervalMax(final Duration timespan) {
		return sliceBest(Streams::max, timespan, systemUTC());
	}

	/**
	 * Return a new flat-mapper function which returns (emits) the maximal value
	 * of the elements emitted within the given {@code timespan}.
	 *
	 * <pre>{@code
	 *          +---3s---+---3s---+
	 *          |        |        |
	 *     +----9--8--3--3--5--4--2--9----|
	 *        toIntervalMax(3s)
	 *     +----------9--------5----------|
	 * }</pre>
	 *
	 * @see #toIntervalMax(Duration)
	 *
	 * @param <C> the element type
	 * @param timespan the timespan the elements are collected for the
	 *        calculation slice
	 * @param clock the {@code clock} used for measuring the {@code timespan}
	 * @return a new flat-mapper function
	 * @throws IllegalArgumentException if the given size is smaller than one
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <C extends Comparable<? super C>>
	Function<C, Stream<C>> toIntervalMax(final Duration timespan, final Clock clock) {
		return sliceBest(Streams::max, timespan, clock);
	}

	/**
	 * Return a new flat-mapper function which returns (emits) the minimal value
	 * of the elements emitted within the given {@code timespan}.
	 *
	 * <pre>{@code
	 *          +---3s---+---3s---+
	 *          |        |        |
	 *     +----9--8--3--3--1--4--2--9----|
	 *        toIntervalMin(3s)
	 *     +----------3--------1----------|
	 * }</pre>
	 *
	 * @see #toIntervalMin(Duration, Clock)
	 *
	 * @param <C> the element type
	 * @param timespan the timespan the elements are collected for the
	 *        calculation slice
	 * @return a new flat-mapper function
	 * @throws IllegalArgumentException if the given size is smaller than one
	 * @throws NullPointerException if the given {@code timespan} is {@code null}
	 */
	public static <C extends Comparable<? super C>>
	Function<C, Stream<C>> toIntervalMin(final Duration timespan) {
		return sliceBest(Streams::min, timespan, systemUTC());
	}

	/**
	 * Return a new flat-mapper function which returns (emits) the minimal value
	 * of the elements emitted within the given {@code timespan}.
	 *
	 * <pre>{@code
	 *          +---3s---+---3s---+
	 *          |        |        |
	 *     +----9--8--3--3--1--4--2--9----|
	 *        toIntervalMin(3s)
	 *     +----------3--------1----------|
	 * }</pre>
	 *
	 * @see #toIntervalMin(Duration)
	 *
	 * @param <C> the element type
	 * @param timespan the timespan the elements are collected for the
	 *        calculation slice
	 * @param clock the {@code clock} used for measuring the {@code timespan}
	 * @return a new flat-mapper function
	 * @throws IllegalArgumentException if the given size is smaller than one
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <C extends Comparable<? super C>>
	Function<C, Stream<C>> toIntervalMin(final Duration timespan, final Clock clock) {
		return sliceBest(Streams::min, timespan, clock);
	}

	/**
	 * Return a new flat-mapper function which returns (emits) the minimal value
	 * of the elements emitted within the given {@code timespan}.
	 *
	 * @see #toIntervalMin(Duration)
	 * @see #toIntervalMax(Duration)
	 *
	 * @param <C> the element type
	 * @param comparator the comparator used for testing the elements
	 * @param timespan the timespan the elements are collected for the
	 *        calculation slice
	 * @return a new flat-mapper function
	 * @throws IllegalArgumentException if the given size is smaller than one
	 @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <C> Function<C, Stream<C>>
	toIntervalBest(final Comparator<? super C> comparator, final Duration timespan) {
		requireNonNull(comparator);
		return sliceBest((a, b) -> best(comparator, a, b), timespan, systemUTC());
	}

	/**
	 * Return a new flat-mapper function which returns (emits) the best value
	 * of the elements emitted within the given {@code timespan}.
	 *
	 * @param <C> the element type
	 * @param comparator the comparator used for testing the elements
	 * @param timespan the timespan the elements are collected for the
	 *        calculation slice
	 * @param clock the {@code clock} used for measuring the {@code timespan}
	 * @return a new flat-mapper function
	 * @throws IllegalArgumentException if the given size is smaller than one
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <C> Function<C, Stream<C>>
	toIntervalBest(
		final Comparator<? super C> comparator,
		final Duration timespan,
		final Clock clock
	) {
		requireNonNull(comparator);
		return sliceBest((a, b) -> best(comparator, a, b), timespan, clock);
	}

	private static <C> Function<C, Stream<C>> sliceBest(
		final BinaryOperator<C> comparator,
		final Duration timespan,
		final Clock clock
	) {
		requireNonNull(comparator);
		requireNonNull(timespan);

		return new Function<>() {
			private final long _timespan  = timespan.toMillis();

			private long _start = 0;
			private long _end = 0;
			private C _best;

			@Override
			public Stream<C> apply(final C value) {
				if (_start == 0) {
					_start = clock.millis();
				}

				_best = comparator.apply(_best, value);
				_end = clock.millis();

				final Stream<C> result;
				if (_end - _start >= _timespan) {
					result = Stream.of(_best);
					_start = 0;
					_best = null;
				} else {
					result = Stream.empty();
				}

				return result;
			}
		};
	}

	/**
	 * Return a new flat-mapper function which returns (emits) the maximal value
	 * of the last <em>n</em> elements.
	 *
	 * <pre>{@code
	 *          f  t  t  f  t  t  f
	 *          +--------+--------+
	 *          |        |        |
	 *     +----9--8--3--3--1--4--2--9----|
	 *      toIntervalMax(predicate)
	 *     +----------9--------4----------|
	 * }</pre>
	 *
	 * @since !__version__!
	 *
	 * @param intervalFinished the predicate which defines an interval. If it
	 *        returns {@code true} the same <em>best</em> interval is used. If
	 *        the predicate returns {@code false}, a new interval is started.
	 * @param <C> the element type
	 * @return a new flat-mapper function
	 * @throws IllegalArgumentException if the given size is smaller than one
	 */
	public static <C extends Comparable<? super C>>
	Function<C, Stream<C>>
	toIntervalMax(final Predicate<? super C> intervalFinished) {
		return sliceBest(Streams::max, intervalFinished);
	}

	/**
	 * Return a new flat-mapper function which returns (emits) the maximal value
	 * of the last <em>n</em> elements.
	 *
	 * <pre>{@code
	 *          f  t  t  f  t  t  f
	 *          +--------+--------+
	 *          |        |        |
	 *     +----9--8--3--3--1--4--2--9----|
	 *      toIntervalMin(predicate)
	 *     +----------3--------1----------|
	 * }</pre>
	 *
	 * @since !__version__!
	 *
	 * @param intervalFinished the predicate which defines an interval. If it
	 *        returns {@code true} the same <em>best</em> interval is used. If
	 *        the predicate returns {@code false}, a new interval is started.
	 * @param <C> the element type
	 * @return a new flat-mapper function
	 * @throws IllegalArgumentException if the given size is smaller than one
	 */
	public static <C extends Comparable<? super C>>
	Function<C, Stream<C>>
	toIntervalMin(final Predicate<? super C> intervalFinished) {
		return sliceBest(Streams::max, intervalFinished);
	}

	/**
	 * Return a new flat-mapper function which returns (emits) the best value
	 * of the elements emitted as long the given {@code intervalFinished}
	 * predicate returns {@code true}. A new interval is started when the
	 * predicate returns {@code false}.
	 *
	 * <pre>{@code
	 *          f  t  t  f  t  t  f
	 *          +--------+--------+
	 *          |        |        |
	 *     +----9--8--3--3--1--4--2--9----|
	 *      toIntervalBest(min, predicate)
	 *     +----------3--------1----------|
	 * }</pre>
	 *
	 * @since !__version__!
	 *
	 * @param <C> the element type
	 * @param comparator the comparator used for testing the elements
	 * @param sameInterval the predicate which defines an interval. If it
	 *        returns {@code true} the same <em>best</em> interval is used. If
	 *        the predicate returns {@code false}, a new interval is started.
	 * @return a new flat-mapper function
	 * @throws IllegalArgumentException if the given size is smaller than one
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <C> Function<C, Stream<C>>
	toIntervalBest(
		final Comparator<? super C> comparator,
		final Predicate<? super C> sameInterval
	) {
		return null;
	}

	private static <C> Function<C, Stream<C>> sliceBest(
		final BinaryOperator<C> comparator,
		final Predicate<? super C> intervalFinished
	) {
		requireNonNull(comparator);
		requireNonNull(intervalFinished);

		return new Function<>() {
			private C _best;

			@Override
			public Stream<C> apply(final C value) {
				_best = comparator.apply(_best, value);

				final Stream<C> result;
				if (intervalFinished.test(value)) {
					result = Stream.empty();
				} else {
					result = Stream.of(_best);
					_best = null;
				}

				return result;
			}
		};
	}

}
