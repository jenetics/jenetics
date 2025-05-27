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
package io.jenetics.distassert.observation;

import static java.lang.Double.doubleToLongBits;
import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOfRange;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.DoubleConsumer;
import java.util.function.IntFunction;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.jenetics.distassert.Interval;

/**
 * This class lets you create a histogram from {@code double} sample data. The
 * following graph shows the structure (buckets) of the histogram.
 * <pre>{@code
 *    -Ꝏ    min                                          max   Ꝏ
 *       -----+----+----+----+----+----+----+----+----+----+-----
 *        20  | 12 | 14 | 17 | 12 | 11 | 13 | 11 | 10 | 19 | 18
 *       -----+----+----+----+----+----+----+----+----+----+-----
 *       left   0    1    2    3    4    5    6    7    8    right
 *   residual                                                residual
 * }</pre>
 * <p>
 * The defined buckets must all be finite. A {@code [-Ꝏ, min)} and a
 * {@code [max, Ꝏ)} residual bin is automatically added and available via the
 * {@link #residual()} component.
 * <p>
 * <b>Histogram creation from double stream</b>
 * {@snippet class="ObservationSnippets" region="Histogram.builder"}
 *
 * @param buckets the {@link Bucket} list, the histogram consists of
 * @param residual the <em>residual</em> buckets, which complete the whole
 *                 {@code double} range (-Ꝏ, Ꝏ)
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record Histogram(Buckets buckets, Residual residual) {

	/* *************************************************************************
	 * Histogram support classes.
	 * ************************************************************************/

	/**
	 * A partition divides an <em>interval</em> into sub-intervals.
	 *
	 * @param interval the overall partition interval
	 * @param separators the interval separators doesn't include the endpoints
	 *        of the {@code interval}
	 */
	public record Partition(Interval interval, double... separators)
		implements Iterable<Interval>
	{
		/**
		 * Create a new partition with the given parameter
		 *
		 * @param interval the overall partition interval
		 * @param separators the interval separators
		 * @throws IllegalArgumentException if the {@code separators} are not
		 *         strictly monotone increasing or not part of the {@code interval}
		 */
		public Partition {
			requireNonNull(interval);
			requireNonNull(separators);

			double value = interval.min();
			for (var separator : separators) {
				if (Double.isNaN(separator)) {
					throw new IllegalArgumentException(
						"Separators contains NaN: %s.".formatted(this)
					);
				}
				if (value >= separator) {
					throw new IllegalArgumentException(
						"Separators are not strictly monotone increasing: %s."
							.formatted(this)
					);
				}

				value = separator;
			}
			if (value >= interval.max()) {
				throw new IllegalArgumentException(
					"Separators greater than interval: %s.".formatted(this)
				);
			}

			separators = separators.clone();
		}

		@Override
		public double[] separators() {
			return separators.clone();
		}

		/**
		 * Return the number of subintervals of {@code this} partition.
		 *
		 * @return the number of subintervals
		 */
		public int size() {
			return separators.length + 1;
		}

		public Interval get(final int index) {
			Objects.checkIndex(index, separators.length + 1);

			if (separators.length == 0) {
				return interval;
			} else {
				return new Interval(
					index == 0 ? interval.min() : separators[index - 1],
					index == separators.length ? interval.max() : separators[index]
				);
			}
		}

		@Override
		public ListIterator<Interval> iterator() {
			return new ReadOnlyListIterator<>(size(), this::get);
		}

		/**
		 * Return the intervals of {@code this} stream as stream.
		 *
		 * @return the interval stream of {@code this} partition
		 */
		public Stream<Interval> stream() {
			return StreamSupport.stream(spliterator(), false);
		}

		/**
		 * Return the index of the interval the given {@code value} belongs to.
		 * If the value is smaller than the partition interval, -1 is returned.
		 * If it is greater than the partition interval, {@link #size()} is
		 * returned.
		 *
		 * @param value the value to test
		 * @return the interval index
		 * @throws IllegalArgumentException if the value is {@link Double#NaN}
		 */
		public int indexOf(final double value) {
			if (Double.isNaN(value)) {
				throw new IllegalArgumentException("Value is NaN.");
			}
			if (value < interval.min()) {
				return -1;
			}
			if (value >= interval.max() && value != Double.POSITIVE_INFINITY) {
				return size();
			}

			if (separators.length == 0) {
				return interval.compareTo(value);
			}

			int low = 0;
			int high = separators.length - 1;

			while (low <= high) {
				if (value < separators[low]) {
					return low;
				}
				if (value >= separators[high]) {
					return high + 1;
				}

				final int mid = (low + high) >>> 1;
				if (value < separators[mid]) {
					high = mid;
				} else if (value >= separators[mid]) {
					low = mid + 1;
				}
			}

			throw new AssertionError("This line will never be reached.");
		}

		@Override
		public int hashCode() {
			return Objects.hash(interval, Arrays.hashCode(separators));
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof Partition(var i, var s) &&
				interval.equals(i) &&
				Arrays.equals(separators, s);
		}

		@Override
		public String toString() {
			return "Partition[interval=%s, separators=%s]"
				.formatted(interval, Arrays.toString(separators));
		}

		/**
		 * Create a partition from the given {@code interval} by splitting it
		 * into the given number of {@code parts}.
		 *
		 * @param interval the interval to partition
		 * @param parts the number of subintervals
		 * @return a new partition
		 */
		public static Partition of(final Interval interval, final int parts) {
			if (!Double.isFinite(interval.min()) || !Double.isFinite(interval.max())) {
				throw new IllegalArgumentException(
					"Open ranges can't be split: %s.".formatted(interval)
				);
			}
			if (parts < 1) {
				throw new IllegalArgumentException(
					"Number of parts must at least one: %d."
						.formatted(parts)
				);
			}
			final long size = elements(interval);
			if (size < parts) {
				throw new IllegalArgumentException("""
					%s can hold only %d distinct double values. \
					"Can't split it into %d parts.
					""".formatted(interval, size, parts)
				);
			}

			if (parts == 1) {
				return new Partition(interval);
			}

			final var stride = (interval.max() - interval.min())/parts;
			assert stride > 0.0;

			final var separators = new double[parts - 1];
			separators[0] = interval.min() + stride;
			for (int i = 1; i < separators.length; ++i) {
				separators[i] = separators[i - 1] + stride;
			}

			return new Partition(interval, separators);
		}

		public static Partition of(final double min, final double max, final int parts) {
			return of(new Interval(min, max), parts);
		}
	}

	/**
	 * Represents a bucket of a histogram.
	 *
	 * @param interval the interval of the bucket
	 * @param count the sample count of the bucket
	 */
	public record Bucket(Interval interval, long count) {

		/**
		 * Create a new bucket with the given values.
		 *
		 * @param interval the bucket range
		 * @param count the bucket count
		 * @throws IllegalArgumentException if {@code count < 0}
		 */
		public Bucket {
			requireNonNull(interval);
			if (count < 0) {
				throw new IllegalArgumentException(
					"Sample count must not be negative: %s.".formatted(count)
				);
			}
		}

	}

	/**
	 * Represents a list of buckets which are part of a histogram. The buckets
	 * of this object are non-overlapping and have no gaps.
	 * <pre>{@code
	 *    min                                     max
	 *     +----+----+----+----+----+----+----+----+
	 *     | 1  | 2  | 3  | 4  |  5 | 6  | 7  |  8 |
	 *     +----+----+----+----+----+----+----+----+
	 * }</pre>
	 */
	public record Buckets(Partition partition, long... frequencies)
		implements Iterable<Bucket>
	{

		public Buckets {
			requireNonNull(partition);
			requireNonNull(frequencies);

			if (partition.size() != frequencies.length) {
				throw new IllegalArgumentException(
					"Partition size does not match frequencies: %s != %s"
						.formatted(partition.separators, frequencies.length)
				);
			}
			for (var frequency : frequencies) {
				if (frequency < 0) {
					throw new IllegalArgumentException(
						"Frequencies must not be negative: %s."
							.formatted(Arrays.toString(frequencies))
					);
				}
			}

			frequencies = frequencies.clone();
		}

		public Buckets(final Partition partition) {
			this(partition, new long[partition.size()]);
		}

		@Override
		public long[] frequencies() {
			return frequencies.clone();
		}

		/**
		 * Return the number ob bucket elements.
		 *
		 * @return the number of buckets
		 */
		public int size() {
			return partition.size();
		}

		/**
		 * Returns the bucket at the specified position.
		 *
		 * @param index index of the element to return
		 * @return the bucket at the specified position
		 * @throws IndexOutOfBoundsException if the index is out of range
		 *         ({@code index < 0 || index >= size()})
		 */
		public Bucket get(final int index) {
			Objects.checkIndex(index, partition.size());
			return new Bucket(partition.get(index), frequencies[index]);
		}

		@Override
		public ListIterator<Bucket> iterator() {
			return new ReadOnlyListIterator<>(size(), this::get);
		}

		/**
		 * Return the bucket elements as stream.
		 *
		 * @return the bucket elements as stream
		 */
		public Stream<Bucket> stream() {
			return StreamSupport.stream(spliterator(), false);
		}

		@Override
		public int hashCode() {
			return Objects.hash(partition, Arrays.hashCode(frequencies));
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof Buckets(var p, var f) &&
				partition.equals(p) &&
				Arrays.equals(frequencies, f);
		}

		@Override
		public String toString() {
			return "Buckets[partition=%s, frequencies=%s]"
				.formatted(partition, Arrays.toString(frequencies));
		}

	}

	/**
	 * Contains the <em>left</em> and <em>right</em> residual counts for
	 * histograms with <em>finite</em> interval.
	 *
	 * <pre>{@code
	 *    -Ꝏ    min                                          max   Ꝏ
	 *       -----+----+----+----+----+----+----+----+----+----+-----
	 *        20  | 12 | 14 | 17 | 12 | 11 | 13 | 11 | 10 | 19 | 18
	 *       -----+----+----+----+----+----+----+----+----+----+-----
	 *       left   0    1    2    3    4    5    6    7    8    right
	 *   residual                                                residual
	 * }</pre>
	 *
	 * @param left the left (lower) residual count. This value is increased if
	 *        a sample value is smaller than the histogram interval.
	 * @param right the right (upper) residual count. This value is increased if
	 *        a sample value is greater or equal than the histogram interval.
	 */
	public record Residual(long left, long right) {
		public static final Residual EMPTY = new Residual(0, 0);

		/**
		 * Create a new residual count object.
		 *
		 * @param left the left (lower) residual count. This value is increased if
		 *        a sample value is smaller than the histogram interval.
		 * @param right the right (upper) residual count. This value is increased if
		 *        a sample value is greater or equal than the histogram interval.
		 * @throws IllegalArgumentException if one of the arguments is negative
		 */
		public Residual {
			if (left < 0 || right < 0) {
				throw new IllegalArgumentException(
					"Residual values must not be negative: %s.".formatted(this)
				);
			}
		}
	}

	/**
	 * Histogram builder.
	 */
	public static final class Builder implements SampleConsumer {
		private final Partition partition;
		private final long[] frequencies;

		private DoubleConsumer observer = value -> {};

		/**
		 * Create a <i>histogram</i> builder with the given {@code buckets} and
		 * {@code residual}.
		 *
		 * @param buckets the buckets, which defines the histogram partition
		 *        and initial samples, which allows to continue the
		 *        <em>sampling</em>
		 * @param residual the initial residual of the histogram builder
		 * @throws NullPointerException if one of the arguments is {@code null}.
		 */
		public Builder(final Buckets buckets, final Residual residual) {
			requireNonNull(buckets);
			requireNonNull(residual);

			this.partition = buckets.partition();
			this.frequencies = new long[buckets.size() + 2];
			this.frequencies[0] = residual.left;
			this.frequencies[this.frequencies.length - 1] = residual.right;
			arraycopy(
				buckets.frequencies, 0,
				this.frequencies, 1, buckets.size()
			);
		}

		/**
		 * Create a <i>histogram</i> builder with the given {@code buckets}.
		 *
		 * @param buckets the buckets, which defines the histogram partition
		 *        and initial samples, which allows to continue the
		 *        <em>sampling</em>
		 * @throws NullPointerException if the {@code buckets} is {@code null}.
		 */
		public Builder(final Buckets buckets) {
			this(buckets, Residual.EMPTY);
		}

		/**
		 * Create a <i>histogram</i> builder with the given {@code partition}.
		 *
		 * @param partition the histogram partition
		 * @throws NullPointerException if the {@code partition} is {@code null}.
		 */
		public Builder(final Partition partition) {
			this(new Buckets(partition));
		}

		/**
		 * Set an additional value observer.
		 *
		 * @param observer the value observer
		 * @throws NullPointerException if the given {@code observer} is
		 *         {@code null}.
		 * @return {@code this} builder
		 */
		public Builder observer(final DoubleConsumer observer) {
			this.observer = requireNonNull(observer);
			return this;
		}

		@Override
		public Builder accept(final double value) {
			++frequencies[partition.indexOf(value) + 1];
			observer.accept(value);
			return this;
		}

		@Override
		public Builder accept(Number sample) {
			SampleConsumer.super.accept(sample);
			return this;
		}

		@Override
		public Builder acceptAll(int... samples) {
			SampleConsumer.super.acceptAll(samples);
			return this;
		}

		@Override
		public Builder acceptAll(long... samples) {
			SampleConsumer.super.acceptAll(samples);
			return this;
		}

		@Override
		public Builder acceptAll(double... samples) {
			SampleConsumer.super.acceptAll(samples);
			return this;
		}

		@Override
		public Builder acceptAll(Iterable<? extends Number> samples) {
			SampleConsumer.super.acceptAll(samples);
			return this;
		}

		@Override
		public Builder acceptAll(DoubleStream samples) {
			SampleConsumer.super.acceptAll(samples);
			return this;
		}

		@Override
		public Builder acceptAll(Stream<? extends Number> samples) {
			SampleConsumer.super.acceptAll(samples);
			return this;
		}

		/**
		 * Add the given {@code sampling} to this histogram.
		 * {@snippet class="ObservationSnippets" region="Histogram.builder"}
		 *
		 * @param sampling the samples consumer
		 * @return {@code this} samples object for method chaining
		 */
		public Builder accept(final Sampling sampling) {
			sampling.writeTo(this);
			return this;
		}

		/**
		 * Combine the given {@code other} histogram with {@code this} one.
		 *
		 * @param other the histogram to add.
		 * @throws IllegalArgumentException if the {@code #bucketCount()} and the
		 *         separators of {@code this} and the given {@code histogram} are
		 *         different.
		 * @throws NullPointerException if the given {@code histogram} is
		 *         {@code null}.
		 */
		public void combine(final Builder other) {
			if (!partition.equals(other.partition)) {
				throw new IllegalArgumentException(
					"Can't combine multiple buckets with different partitions: %s != %s."
						.formatted(partition, other.partition)
				);
			}

			for (int i = other.frequencies.length; --i >= 0;) {
				frequencies[i] += other.frequencies[i];
			}
		}

		/**
		 * Create a new <em>immutable</em> histogram object from the current
		 * values.
		 *
		 * @return a new <em>immutable</em> histogram object
		 */
		public Histogram build() {
			final var buckets = new Buckets(
				partition,
				copyOfRange(frequencies, 1, frequencies.length - 1)
			);
			final var residuals = new Residual(
				frequencies[0],
				frequencies[frequencies.length - 1]
			);

			return new Histogram(buckets, residuals);
		}

		/**
		 * Return a histogram builder with the given {@code min} and {@code max}
		 * values and number {@code classes}.
		 * <pre>{@code
		 *    -Ꝏ    min                                          max   Ꝏ
		 *       -----+----+----+----+----+----+----+----+----+----+-----
		 *        20  | 12 | 14 | 17 | 12 | 11 | 13 | 11 | 10 | 19 | 18
		 *       -----+----+----+----+----+----+----+----+----+----+-----
		 *       left   0    1    2    3    4    5    6    7    8    right
		 *   residual                                                residual
		 * }</pre>
		 *
		 * @param interval the range of the observed values
		 * @param classes the number of classes between {@code [min, max)}
		 * @return a new histogram builder
		 * @throws IllegalArgumentException if {@code classes < 1}
		 */
		public static Builder of(final Interval interval, final int classes) {
			return new Builder(Partition.of(interval, classes));
		}

		/**
		 * Return a histogram builder with the given {@code min} and {@code max}
		 * values and number {@code classes}.
		 *
		 * @see #of(Interval, int)
		 *
		 * @param min the minimal value of the inner buckets
		 * @param max the maximal value of the inner buckets
		 * @param classes the number of classes between {@code [min, max)}
		 * @return a new histogram builder
		 * @throws IllegalArgumentException if {@code min >= max} or min or max are
		 *         not finite or {@code classes < 1}
		 */
		public static Builder of(final double min, final double max, final int classes) {
			return new Builder(Partition.of(new Interval(min, max), classes));
		}

	}

	/* *************************************************************************
	 * Histogram implementation.
	 * ************************************************************************/

	/**
	 * Create a new histogram consisting of the given buckets.
	 *
	 * @param buckets the histogram buckets
	 * @param residual the histogram's residual counts
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public Histogram {
		requireNonNull(buckets);
		requireNonNull(residual);
	}

	/**
	 * Create a new histogram consisting of the given buckets.
	 *
	 * @param buckets the histogram buckets
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public Histogram(final Buckets buckets) {
		this(buckets, Residual.EMPTY);
	}

	/**
	 * Return the <em>degrees of freedom</em> of the histogram, which is
	 * {@code buckets().size() - 1}.
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/Degrees_of_freedom_(statistics)">
	 *     Degrees of freedom</a>
	 *
	 * @return the degrees of freedom
	 */
	public int degreesOfFreedom() {
		return buckets.size() - 1;
	}

	/**
	 * Return the number of samples, which generated the histogram, without the
	 * residuals.
	 *
	 * @return the number of samples, without residuals
	 */
	public long samples() {
		long result = 0;
		for (long sample : buckets.frequencies) {
			result += sample;
		}
		return result;
	}

	/**
	 * Return the partition of {@code this} histogram.
	 *
	 * @return the partition of {@code this} histogram
	 */
	public Partition partition() {
		return buckets.partition();
	}

	/**
	 * Return the interval of {@code this} histogram.
	 *
	 * @return the interval of {@code this} histogram
	 */
	public Interval interval() {
		return partition().interval();
	}

	/**
	 * Return a new builder with the buckets (inclusively bucket counts).
	 *
	 * @return a new histogram builder with the buckets of {@code this}
	 *         histogram
	 */
	public Builder toBuilder() {
		return new Builder(buckets, residual);
	}

	/**
	 * Return a histogram collector with the given {@code min} and {@code max}
	 * values and number {@code classes}. The histogram, created by the
	 * collector will consist of {@code classes + 2} buckets. The <em>inner</em>
	 * buckets will be in the range {@code [min, max)} and consist of the
	 * defined {@code classes}.
	 * <pre>{@code
	 *  -Ꝏ   min                                           max   Ꝏ
	 *     ----+----+----+----+----+----+----+----+  ~  +----+----
	 *         | 1  | 2  | 3  | 4  |  5 | 6  | 7  |     |  c |
	 *     ----+----+----+----+----+----+----+----+  ~  +----+----
	 * }</pre>
	 *
	 * @param partition the histogram partition
	 * @param fn the function converting the elements to double values
	 * @return a new histogram collector
	 * @param <T> the stream element type
	 * @throws IllegalArgumentException if {@code min >= max} or min or max are
	 *         not finite or {@code classes < 1}
	 * @throws NullPointerException if {@code fn} is {@code null}
	 */
	public static <T> Collector<T, ?, Histogram> toHistogram(
		final Partition partition,
		final ToDoubleFunction<? super T> fn
	) {
		requireNonNull(fn);

		return Collector.of(
			() -> new Histogram.Builder(partition),
			(hist, val) -> hist.accept(fn.applyAsDouble(val)),
			(a, b) -> { a.combine(b); return a; },
			Histogram.Builder::build
		);
	}

	/**
	 * Return a histogram collector with the given {@code min} and {@code max}
	 * values and number {@code classes}.
	 *
	 * @param partition the histogram partition
	 * @return a new histogram collector
	 * @param <T> the stream element type
	 * @throws IllegalArgumentException if {@code min >= max} or min or max are
	 *         not finite or {@code classes < 1}
	 */
	public static <T extends Number> Collector<T, ?, Histogram>
	toHistogram(final Partition partition) {
		return toHistogram(partition, Number::doubleValue);
	}


	/* *************************************************************************
	 * Some private helper classes
	 * ************************************************************************/

	private static final class ReadOnlyListIterator<T> implements ListIterator<T> {
		private final int size;
		private final IntFunction<T> getter;

		private int cursor = 0;
		private int lastElement = -1;

		private ReadOnlyListIterator(final int size, final IntFunction<T> getter) {
			this.size = size;
			this.getter = getter;
		}

		@Override
		public boolean hasNext() {
			return cursor != size;
		}

		@Override
		public T next() {
			final int i = cursor;
			if (cursor >= size) {
				throw new NoSuchElementException();
			}

			cursor = i + 1;
			return getter.apply(lastElement = i);
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
			return getter.apply(lastElement = i);
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

	}

	/* *************************************************************************
	 * Some static helper methods.
	 * ************************************************************************/

	/**
	 * Return the number of <em>distinct</em> {@code double} values {@code this}
	 * interval can <em>hold</em>.
	 *
	 * @return the number of distinct double values of {@code this} interval
	 */
	static long elements(Interval interval) {
		if (Double.isInfinite(interval.min()) || Double.isInfinite(interval.max())) {
			return Long.MAX_VALUE;
		}

		long left = interval.min() < 0
			? Long.MIN_VALUE - doubleToLongBits(interval.min())
			: doubleToLongBits(interval.min());

		long right = interval.max() < 0
			? Long.MIN_VALUE - doubleToLongBits(interval.max())
			: doubleToLongBits(interval.max());

		// Overflow safe subtraction.
		final long result = right - left;
		return ((right^left) & (right^result)) < 0 ? Long.MAX_VALUE : result;
	}

}
