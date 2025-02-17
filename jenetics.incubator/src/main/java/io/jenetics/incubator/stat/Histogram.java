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
package io.jenetics.incubator.stat;

import static java.lang.Double.doubleToLongBits;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntFunction;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.jenetics.stat.DoubleMomentStatistics;

/**
 * This class lets you create a histogram from {@code double} sample data. The
 * following graph shows the structure (buckets) of the histogram.
 * <pre>{@code
 *  -Ꝏ    min                                          max   Ꝏ
 *     -----+----+----+----+----+----+----+----+----+----+-----
 *      20  | 12 | 14 | 17 | 12 | 11 | 13 | 11 | 10 | 19 | 18
 *     -----+----+----+----+----+----+----+----+----+----+-----
 *       0    1    2    3    4    5    6    7    8    9    10
 * }</pre>
 * <p>
 * The defined separators must all be finite. A {@code [-Ꝏ, min)} and a
 * {@code [max, Ꝏ)} bin is automatically added at the beginning and the end
 * of the frequency.
 * <p>
 * <b>Histogram creation from double stream</b>
 * {@snippet lang="java":
 * final Histogram observation = RandomGenerator.getDefault()
 *     .doubles(10_000)
 *     .collect(
 *         () -> Histogram.Builder.of(0, 1, 20),
 *         Histogram.Builder::accept,
 *         Histogram.Builder::combine
 *     )
 *     .build();
 * }
 * <b>Histogram creation from object stream</b>
 * {@snippet lang="java":
 * final ISeq<DoubleGene> genes = DoubleGene.of(0, 10)
 *     .instances().limit(1000)
 *     .collect(ISeq.toISeq());
 *
 * final Histogram observations = genes.stream()
 *     .collect(Histogram.toHistogram(0, 10, 20, DoubleGene::doubleValue));
 * }
 * <p>
 * <b>Histogram creation from array</b>
 * {@snippet lang="java":
 * final double[] data = null; // @replace substring='null' replacement="..."
 * final var builder = Histogram.Builder.of(0.0, 1.0, 20);
 * for (var d : data) {
 *     builder.accept(d);
 * }
 * final Histogram observations = builder.build();
 * }
 *
 * @param buckets the {@link Bucket} list, the histogram consists of
 * @param residual the <em>residual</em> buckets, which complete the whole
 *                 {@code double} range (-Ꝏ, Ꝏ)
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record Histogram(Buckets buckets, Buckets residual) {

	/**
	 * Defines a double range.
	 *
	 * @param min the minimal range value (inclusively)
	 * @param max the maximal range value (exclusively)
	 */
	public record Range(double min, double max) {

		/**
		 * Create a new bucket with the given values.
		 *
		 * @param min the minimal value of the bin range, inclusively. Might be
		 *        {@link Double#NEGATIVE_INFINITY}
		 * @param max the maximal value of the bin range, exclusively. Might be
		 *        {@link Double#POSITIVE_INFINITY}
		 * @throws IllegalArgumentException if the {@code min} and {@code max}
		 *         values are {@link Double#NaN} or {@code min >= max}
		 */
		public Range {
			if (Double.isNaN(min) || Double.isNaN(max) || min >= max) {
				throw new IllegalArgumentException(
					"Invalid range: %s.".formatted(this)
				);
			}
		}

		/**
		 * Test whether the given {@code value} lies within, below or above
		 * {@code this} range.
		 *
		 * @param value the value to test
		 * @return {@code -1}, {@code 0} or {@code 1} if the given {@code value}
		 *          lies below, within or above {@code this} range
		 */
		public int compareTo(final double value) {
			if (value < min) {
				return -1;
			} else if (value >= max) {
				return 1;
			} else {
				return 0;
			}
		}

		/**
		 * Return the number of <em>distinct</em> {@code double} values
		 * {@code this} range can <em>hold</em>.
		 *
		 * @return the number of distinct double values of {@code this} range
		 */
		long size() {
			return doubleToLongBits(max) - doubleToLongBits(min) - 1;
		}

	}

	/**
	 * A partition divides an <em>interval</em> into sub-intervals.
	 *
	 * @param interval the overall partition interval
	 * @param separators the interval separators
	 */
	public record Partition(Range interval, double... separators)
		implements Iterable<Range>
	{
		public Partition {
			requireNonNull(interval);

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

		public Range get(final int index) {
			Objects.checkIndex(index, separators.length + 1);

			if (separators.length == 0) {
				return interval;
			} else {
				return new Range(
					index == 0 ? interval.min() : separators[index - 1],
					index == separators.length - 1 ? interval.max() : separators[index]
				);
			}
		}

		public int size() {
			return separators.length + 1;
		}

		@Override
		public ListIterator<Range> iterator() {
			return new ReadOnlyListIterator<>(size(), this::get);
		}

		public Stream<Range> stream() {
			return StreamSupport.stream(spliterator(), false);
		}

		public int indexOf(final double value) {
			if (Double.isNaN(value)) {
				return -1;
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
				.formatted(interval, separators);
		}

		/**
		 * Create a partition from the given {@code interval} by splitting it
		 * into the given number of {@code parts}.
		 *
		 * @param internal the interval to partition
		 * @param parts the number of sub-intervals
		 * @return a new partition
		 */
		public static Partition of(final Range internal, final int parts) {
			if (!Double.isFinite(internal.min) || !Double.isFinite(internal.max)) {
				throw new IllegalArgumentException(
					"Open ranges can't be split: %s.".formatted(internal)
				);
			}
			if (parts < 1) {
				throw new IllegalArgumentException(
					"Number of parts must at least one: %d."
						.formatted(parts)
				);
			}
			final long size = internal.size();
			if (size < parts) {
				throw new IllegalArgumentException("""
					%s can hold only %d distinct double values. \
					"Can't split it into %d parts.
					""".formatted(internal, size, parts)
				);
			}

			if (parts == 1) {
				return new Partition(internal);
			}

			final var stride = (internal.max - internal.min)/parts;
			assert stride > 0.0;

			final var separators = new double[parts - 1];
			separators[0] = internal.min + stride;
			for (int i = 1; i < separators.length; ++i) {
				separators[i] = separators[i - 1] + stride;
			}

			return new Partition(internal, separators);
		}
	}

	/**
	 * Represents on histogram bin. For <em>open</em> buckets. Buckets have range
	 * {@code [min, max)} and a {@code count} value. The following example shows
	 * <em>closed</em>, <em>half open</em> and <em>open</em> buckets.
	 * <pre>{@code
	 *    min  max        -Ꝏ    max   min   Ꝏ         -Ꝏ    Ꝏ
	 *     +----+            -----+     +-----             ------
	 *     | 12 |              20 |     | 18                 20
	 *     +----+            -----+     +-----             ------
	 * }</pre>
	 *
	 * @param range the range of the bucket
	 * @param count the bucket count
	 */
	public record Bucket(Range range, long count) {

		/**
		 * Create a new bucket with the given values.
		 *
		 * @param range the bucket range
		 * @param count the bucket count
		 * @throws IllegalArgumentException if {@code count < 0}
		 */
		public Bucket {
			if (count < 0) {
				throw new IllegalArgumentException(
					"Bucket count must not be negative: %s.".formatted(count)
				);
			}
		}

		/**
		 * Create a new bucket with the given {@code range}. The
		 * {@link Bucket#count()} value is set to zero.
		 *
		 * @param range the bucket range
		 */
		public Bucket(final Range range) {
			this(range, 0);
		}

		/**
		 * Create a new bucket with the given {@code count} value added.
		 *
		 * @param count the count value to be added
		 * @return a new bucket with the given {@code count} value added
		 */
		public Bucket add(final long count) {
			return new Bucket(range, this.count + count);
		}

	}

	/**
	 * Represents a list of buckets which are part of a histogram. The buckets
	 * of this object must be non-overlapping and will usually have no gaps,
	 * although they are allowed, like shown in the following example.
	 * <pre>{@code
	 *    min                                     max
	 *     +----+----+----+----+----+----+----+----+
	 *     | 1  | 2  | 3  | 4  |  5 | 6  | 7  |  8 |
	 *     +----+----+----+----+----+----+----+----+
	 * }</pre>
	 * This example shows non overlapping buckets with gaps.
	 * <pre>{@code
	 *    min                                      max
	 *     +----+----+----+   +----+----+ +----+----+
	 *     | 1  | 2  | 3  |   | 4  | 5  | | 6  |  7 |
	 *     +----+----+----+   +----+----+ +----+----+
	 * }</pre>
	 */
	public record Buckets(Partition partition, long... frequencies)
		implements Iterable<Bucket>
	{

		public Buckets {
			requireNonNull(partition);
			if (partition.size() != frequencies.length) {
				throw new IllegalArgumentException(
					"Partition size does not match frequencies: %s != %s"
						.formatted(partition.separators, frequencies.length)
				);
			}

			frequencies = frequencies.clone();
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
				.formatted(partition, frequencies);
		}

	}

	/**
	 * Create a new histogram consisting of the given buckets.
	 *
	 * @param buckets the histogram buckets
	 * @throws NullPointerException if the given {@code buckets} is {@code null}
	 */
	public Histogram {
		requireNonNull(buckets);
		requireNonNull(residual);
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
	 * Return the number of samples, which generated the histogram.
	 *
	 * @return the number of samples
	 */
	public long sampleCount() {
		return buckets.stream()
			.mapToLong(Bucket::count)
			.sum();
	}

	/**
	 * Return a new builder with the buckets (inclusively bucket counts).
	 *
	 * @return a new histogram builder with the buckets of {@code this}
	 *         histogram
	 */
	public Builder toBuilder() {
		return new Builder(buckets);
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
	 * @see Histogram.Builder#of(double, double, int)
	 *
	 * @param min the minimal value of the inner buckets
	 * @param max the maximal value of the inner buckets
	 * @param classes the number of classes between {@code [min, max)}
	 * @param fn the function converting the elements to double values
	 * @return a new histogram collector
	 * @param <T> the stream element type
	 * @throws IllegalArgumentException if {@code min >= max} or min or max are
	 *         not finite or {@code classes < 1}
	 * @throws NullPointerException if {@code fn} is {@code null}
	 */
	public static <T> Collector<T, ?, Histogram> toHistogram(
		final double min,
		final double max,
		final int classes,
		final ToDoubleFunction<? super T> fn
	) {
		requireNonNull(fn);

		return Collector.of(
			() -> Histogram.Builder.of(min, max, classes),
			(hist, val) -> hist.accept(fn.applyAsDouble(val)),
			(a, b) -> { a.combine(b); return a; },
			Histogram.Builder::build
		);
	}

	/**
	 * Return a histogram collector with the given {@code min} and {@code max}
	 * values and number {@code classes}.
	 *
	 * @see #toHistogram(double, double, int, ToDoubleFunction)
	 *
	 * @param min the minimal value of the inner buckets
	 * @param max the maximal value of the inner buckets
	 * @param classes the number of classes between {@code [min, max)}
	 * @return a new histogram collector
	 * @param <T> the stream element type
	 * @throws IllegalArgumentException if {@code min >= max} or min or max are
	 *         not finite or {@code classes < 1}
	 */
	public static <T extends Number> Collector<T, ?, Histogram> toHistogram(
		final double min,
		final double max,
		final int classes
	) {
		return toHistogram(min, max, classes, Number::doubleValue);
	}


	/**
	 * Histogram builder class.
	 */
	public static final class Builder implements DoubleConsumer {
		private final Partition partition;
		private final long[] frequencies;
		private final DoubleMomentStatistics statistics;

		/**
		 * Create a <i>histogram</i> builder with the given {@code buckets}.
		 *
		 * @throws NullPointerException if the {@code buckets} is {@code null}.
		 */
		public Builder(final Buckets buckets) {
			this.partition = buckets.partition();
			this.frequencies = buckets.frequencies();
			this.statistics = new DoubleMomentStatistics();
		}

		@Override
		public void accept(final double value) {
			final var index = partition.indexOf(value);
			if (index != -1) {
				++frequencies[index];
				statistics.accept(value);
			}
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
			statistics.combine(other.statistics);
		}

		/**
		 * Create a new <em>immutable</em> histogram object from the current
		 * values.
		 *
		 * @return a new <em>immutable</em> histogram
		 */
		public Histogram build() {
			return new Histogram(new Buckets(partition, frequencies), null);
		}

		/**
		 * Create a new <em>immutable</em> histogram from the given {@code sample}
		 * block.
		 * {@snippet lang="java":
		 * final double[] values = RandomGenerator.getDefault()
		 *     .doubles(10000, -5, 5)
		 *     .toArray();
		 *
		 * final var histogram = Histogram.Builder.of(-5, 5, 10)
		 *     .build(samples -> {
		 * 	        for (double value : values) {
		 * 	            samples.accept(value);
		 * 	        }
		 * 	    });
		 * }
		 *
		 * @param samples the samples consumer
		 * @return a new histogram
		 */
		public Histogram build(final Consumer<? super DoubleConsumer> samples) {
			samples.accept(this);
			return build();
		}

		/**
		 * Return a histogram builder with the given {@code min} and {@code max}
		 * values and number {@code classes}. The histogram, created by the
		 * builder will consist of {@code classes + 2} buckets. The <em>inner</em>
		 * buckets will be in the range {@code [min, max)} and consist of the
		 * defined {@code classes}.
		 * <pre>{@code
		 *  -Ꝏ   min                                           max   Ꝏ
		 *     ----+----+----+----+----+----+----+----+  ~  +----+----
		 *         | 1  | 2  | 3  | 4  |  5 | 6  | 7  |     |  c |
		 *     ----+----+----+----+----+----+----+----+  ~  +----+----
		 * }</pre>
		 *
		 * @param min the minimal value of the inner buckets
		 * @param max the maximal value of the inner buckets
		 * @param classes the number of classes between {@code [min, max)}
		 * @return a new histogram builder
		 * @throws IllegalArgumentException if {@code min >= max} or min or max are
		 *         not finite or {@code classes < 1}
		 */
		public static Builder of(final double min, final double max, final int classes) {
			return null; //new Builder(Buckets.of(min, max, classes));
		}

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

}
