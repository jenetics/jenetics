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

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.util.Objects.requireNonNull;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jenetics.stat.DoubleMomentStatistics;

/**
 * This class lets you create a histogram from {@code double} sample data. The
 * following graph shows the structure (buckets) of the histogram.
 * <pre>{@code
 * -Ꝏ     min                                          max    Ꝏ
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
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record Histogram(Buckets buckets) {

	/**
	 * Represents on histogram bin. For <em>open</em> buckets, the {@link #min()}
	 * value might be {@link Double#NEGATIVE_INFINITY} and the {@link #max()}
	 * value might be {@link Double#POSITIVE_INFINITY}. Buckets have range
	 * {@code [min, max)} and a {@code count} value. The following example shows
	 * <em>closed</em>, <em>half open</em> and <em>open</em> buckets.
	 * <pre>{@code
	 *    min   max       -Ꝏ    max   min   Ꝏ        -Ꝏ     Ꝏ
	 *     +----+            -----+     +-----             ------
	 *     | 12 |              20 |     | 18                 20
	 *     +----+            -----+     +-----             ------
	 * }</pre>
	 *
	 * @param min the minimal value of the bin range, inclusively. Might be
	 *        {@link Double#NEGATIVE_INFINITY}
	 * @param max the maximal value of the bin range, exclusively. Might be
	 *        {@link Double#POSITIVE_INFINITY}
	 * @param count the bucket count
	 */
	public record Bucket(double min, double max, long count) {

		/**
		 * Create a new bucket with the given values.
		 *
		 * @param min the minimal value of the bin range, inclusively. Might be
		 *        {@link Double#NEGATIVE_INFINITY}
		 * @param max the maximal value of the bin range, exclusively. Might be
		 *        {@link Double#POSITIVE_INFINITY}
		 * @param count the bucket count
		 * @throws IllegalArgumentException if the {@code min} and {@code max}
		 *         values are {@link Double#NaN}, {@code min >= max} or
		 *         {@code count < 0}
		 */
		public Bucket {
			if (Double.isNaN(min) || Double.isNaN(max) || min >= max || count < 0) {
				throw new IllegalArgumentException(
					"Invalid Bin[min=%f, max=%f, count=%d]."
						.formatted(min, max, count)
				);
			}
		}

		/**
		 * Create a new bucket with the given {@code min} and {@code max} values.
		 * The {@link Bucket#count()} value is set to zero.
		 *
		 * @param min the minimal value of the bin range, inclusively. Might be
		 *        {@link Double#NEGATIVE_INFINITY}
		 * @param max the maximal value of the bin range, exclusively. Might be
		 *        {@link Double#POSITIVE_INFINITY}
		 * @throws IllegalArgumentException if the {@code min} and {@code max}
		 *         values are {@link Double#NaN}, {@code min >= max} or
		 *         {@code count < 0}
		 */
		public Bucket(double min, double max) {
			this(min, max, 0);
		}

		/**
		 * Create a new bucket with the given {@code count} value added.
		 *
		 * @param count the count value to be added
		 * @return a new bucket with the given {@code count} value added
		 */
		public Bucket add(final long count) {
			return new Bucket(min, max, this.count + count);
		}

		boolean overlaps(final Bucket other) {
			return
				min >= other.min && min < other.max ||
				max > other.min && max < other.max ||
				other.min >= min && other.min < max ||
				other.max > min && other.max < max;
		}

		int compareTo(final double value) {
			if (value < min) {
				return -1;
			} else if (value >= max) {
				return 1;
			} else {
				return 0;
			}
		}

	}

	/**
	 * Represents a list of buckets which forms a histogram.
	 */
	public static final class Buckets implements Iterable<Bucket> {
		private final List<Bucket> buckets;

		/**
		 * Create a new buckets object from the given, non-overlapping, buckets.
		 *
		 * @param buckets the bucket list
		 * @throws IllegalArgumentException if the given {@code buckets} contains
		 *         overlapping elements or is empty.
		 */
		public Buckets(final Collection<Bucket> buckets) {
			if (buckets.isEmpty()) {
				throw new IllegalArgumentException(
					"Buckets list must not be empty."
				);
			}

			final var list = new ArrayList<>(buckets);
			list.sort(Comparator.comparingDouble(Bucket::min));
			for (int i = 1; i < list.size(); ++i) {
				final var a = list.get(i - 1);
				final var b = list.get(i);

				if (a.overlaps(b)) {
					throw new IllegalArgumentException(
						"Found overlapping buckets: %s ∩ %s.".formatted( a, b)
					);
				}
			}

			this.buckets = List.copyOf(list);
		}

		/**
		 * reate a new buckets object from the given, non-overlapping, buckets.
		 *
		 * @param buckets the bucket list
		 * @throws IllegalArgumentException if the given {@code buckets} contains
		 *         overlapping elements or is empty.
		 */
		public Buckets(final Bucket... buckets) {
			this(List.of(buckets));
		}

		/**
		 * Return the number ob bucket elements.
		 *
		 * @return the number of buckets
		 */
		public int size() {
			return buckets.size();
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
			return buckets.get(index);
		}

		/**
		 * Return the first bucket.
		 *
		 * @return the first bucket
		 */
		public Bucket first() {
			return buckets.getFirst();
		}

		/**
		 * Return the last bucket.
		 *
		 * @return the last bucket
		 */
		public Bucket last() {
			return buckets.getLast();
		}

		@Override
		public Iterator<Bucket> iterator() {
			return buckets.iterator();
		}

		/**
		 * Return the bucket elements as stream.
		 *
		 * @return the bucket elements as stream
		 */
		public Stream<Bucket> stream() {
			return buckets.stream();
		}

		int indexOf(final double value) {
			if (Double.isNaN(value)) {
				return -1;
			}

			int low = 0;
			int high = size() - 1;

			while (low <= high) {
				if (get(low).compareTo(value) < 0 ||
					get(high).compareTo(value) > 0)
				{
					return -1;
				}

				final int mid = (low + high) >>> 1;
				final int cpm = get(mid).compareTo(value);

				if (cpm == 0) {
					return mid;
				}

				if (cpm < 0) {
					high = mid;
				} else {
					low = mid + 1;
				}
			}

			return -1;
		}

		boolean equalRanges(final Buckets other) {
			if (buckets.size() != other.buckets.size()) {
				return false;
			}
			for (int i = 0; i < buckets.size(); ++i) {
				if (buckets.get(i).min != other.buckets.get(i).min ||
					buckets.get(i).max != other.buckets.get(i).max)
				{
					return false;
				}
			}
			return true;
		}

		@Override
		public int hashCode() {
			return buckets.hashCode();
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof Buckets b &&
				buckets.equals(b.buckets);
		}

		@Override
		public String toString() {
			return buckets.toString();
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
	}

	/**
	 * Return the <em>degrees of freedom</em> of the histogram, which is
	 * {@code #buckets().size() - 1}.
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
	 * Create a new histogram from the defined buckets slice. This method allows
	 * negative indexes like <em>Python</em> arrays.
	 * <p>
	 * <b>Negative array indexes</b>
	 * <pre>{@code
	 *       0    1    2    3    4    5    6    7    8    9     Indexes
	 *     +----+----+----+----+----+----+----+----+----+----+
	 *     | 0  | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  |  Array elements
	 *     +----+----+----+----+----+----+----+----+----+----+
	 *      -10  -9   -8   -7   -6   -5   -4   -3   -2   -1    Negative indexes
	 * }</pre>
	 *
	 * @param start the start index, inclusively
	 * @param end the end index, exclusively
	 * @return the new histogram from the given buckets slice
	 * @throws IndexOutOfBoundsException if the given start and end indexes
	 *         are out of bounds
	 * @throws IllegalArgumentException if the bucket slice is empty
	 */
	public Histogram slice(final int start, final int end) {
		final var s = start < 0 ? buckets.size() + start : start;
		final var e = end < 0 ? buckets.size() + end : end;

		return new Histogram(new Buckets(buckets.buckets.subList(s, e)));
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

	public void print(PrintStream output) {
		new HistogramFormat(15).print(output, this);
	}

	/**
	 * Return a histogram collector with the given {@code min} and {@code max}
	 * values and number {@code classes}. The histogram, created by the
	 * collector will consist of {@code classes + 2} buckets. The <em>inner</em>
	 * buckets will be in the range {@code [min, max)}} and consist of the
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
		private final Buckets _buckets;
		private final long[] _frequencies;
		private final DoubleMomentStatistics _statistics = new DoubleMomentStatistics();

		/**
		 * Create a <i>histogram</i> builder with the given {@code buckets}.
		 *
		 * @throws NullPointerException if the {@code buckets} is {@code null}.
		 */
		public Builder(final Buckets buckets) {
			_buckets = requireNonNull(buckets);
			_frequencies = new long[buckets.size()];
		}

		@Override
		public void accept(double value) {
			final var index = _buckets.indexOf(value);
			if (index != -1) {
				++_frequencies[index];
				_statistics.accept(value);
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
			if (!_buckets.equalRanges(other._buckets)) {
				throw new IllegalArgumentException(
					"The histogram separators are not equals."
				);
			}

			for (int i = other._frequencies.length; --i >= 0;) {
				_frequencies[i] += other._frequencies[i] + other._buckets.get(i).count;
			}
			_statistics.combine(other._statistics);
		}

		/**
		 * Create a new <em>immutable</em> histogram object from the current
		 * values.
		 *
		 * @return a new <em>immutable</em> histogram
		 */
		public Histogram build() {
			final var buckets = IntStream.range(0, _frequencies.length)
				.mapToObj(i -> _buckets.get(i).add(_frequencies[i]))
				.toList();

			return new Histogram(new Buckets(buckets));
		}

		public Histogram build(final Consumer<? super DoubleConsumer> samples) {
			samples.accept(this);
			return build();
		}

		/**
		 * Return a histogram builder with the given {@code min} and {@code max}
		 * values and number {@code classes}. The histogram, created by the
		 * builder will consist of {@code classes + 2} buckets. The <em>inner</em>
		 * buckets will be in the range {@code [min, max)}} and consist of the
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
		 * @return a new histogram collector
		 * @throws IllegalArgumentException if {@code min >= max} or min or max are
		 *         not finite or {@code classes < 1}
		 */
		public static Builder of(
			final double min,
			final double max,
			final int classes
		) {
			if (!Double.isFinite(min) || !Double.isFinite(max) || min >= max) {
				throw new IllegalArgumentException(
					"Invalid border: [min=%f, max=%f].".formatted(min, max)
				);
			}
			if (classes < 1) {
				throw new IllegalArgumentException(
					"Number of classes must at least one: %d."
						.formatted(classes)
				);
			}

			final var stride = (max - min)/classes;
			final var buckets = new Bucket[classes + 2];
			buckets[0] = new Bucket(NEGATIVE_INFINITY, min);
			buckets[buckets.length - 1] = new Bucket(max, POSITIVE_INFINITY);

			for (int i = 1; i < buckets.length - 1; ++i) {
				buckets[i] = new Bucket(
					buckets[i - 1].max,
					buckets[i - 1].max + stride
				);
			}

			return new Builder(new Buckets(buckets));
		}

	}

}
