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

import static java.lang.Double.MAX_VALUE;
import static java.util.Objects.requireNonNull;

import java.io.PrintStream;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;
import java.util.stream.IntStream;

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
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record Histogram(List<Bucket> buckets) {

	/**
	 * Represents on histogram bin. For <em>open</em> buckets, the {@link #min()}
	 * value might be {@link Double#NEGATIVE_INFINITY} and the {@link #max()}
	 * value might be {@link Double#POSITIVE_INFINITY}.
	 *
	 * @param min the minimal value of the bin range, inclusively. Might be
	 *        {@link Double#NEGATIVE_INFINITY}
	 * @param max the maximal value of the bin range, exclusively. Might be
	 *        {@link Double#POSITIVE_INFINITY}
	 * @param count the bin count
	 */
	public record Bucket(double min, double max, long count) {
		public Bucket {
			if (min >= max || count < 0) {
				throw new IllegalArgumentException(
					"Invalid Bin[min=%f, max=%f, count=%d]."
						.formatted(min, max, count)
				);
			}
		}
	}

	/**
	 * Create a new histogram with the given {@code buckets}. The bucket list
	 * must be not empty.
	 *
	 * @param buckets the histogram buckets
	 * @throws IllegalArgumentException if the bucket list is empty
	 */
	public Histogram {
		if (buckets.isEmpty()) {
			throw new IllegalArgumentException("Buckets list must not be empty.");
		}
		buckets = List.copyOf(buckets);
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

		return new Histogram(buckets.subList(s, e));
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

	@Override
	public String toString() {
		/*
		return """
			Histogram[
			    separators=%s,
			    table=%s,
			    %s
			]
			""".formatted(
					separators,
					frequencies,
					moments
				);
		 */
		return buckets.toString();
	}

	public static <T> Collector<T, ?, Histogram> toHistogram(
		final double min,
		final double max,
		final int nclasses,
		final ToDoubleFunction<? super T> fn
	) {
		return Collector.of(
			() -> Histogram.Builder.of(min, max, nclasses),
			(hist, val) -> hist.accept(fn.applyAsDouble(val)),
			(a, b) -> { a.combine(b); return a; },
			Histogram.Builder::build
		);
	}

	public static <T extends Number> Collector<T, ?, Histogram> toHistogram(
		final double min,
		final double max,
		final int nclasses
	) {
		return toHistogram(min, max, nclasses, Number::doubleValue);
	}

	/**
	 * Histogram builder class.
	 */
	public static final class Builder implements DoubleConsumer {
		private final Separators _separators;
		private final long[] _frequencies;
		private final DoubleMomentStatistics _statistics = new DoubleMomentStatistics();

		/**
		 * Create a <i>histogram</i> builder with the given {@code separators}.
		 * The created <i>histogram</i> will have the following structure:
		 * <pre>{@code
		 * -Ꝏ     min                                          max    Ꝏ
		 *     -----+----+----+----+----+----+----+----+----+----+-----
		 *      20  | 12 | 14 | 17 | 12 | 11 | 13 | 11 | 10 | 19 | 18
		 *     -----+----+----+----+----+----+----+----+----+----+-----
		 *       0    1    2    3    4    5    6    7    8    9    10
		 * }</pre>
		 *
		 * @throws NullPointerException if {@code separators} is {@code null}.
		 */
		public Builder(Separators separators) {
			_separators = requireNonNull(separators);
			_frequencies = new long[separators.length() + 1];
		}

		@Override
		public void accept(double value) {
			++_frequencies[_separators.bucketIndexOf(value)];
			_statistics.accept(value);
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
			if (!_separators.equals(other._separators)) {
				throw new IllegalArgumentException(
					"The histogram separators are not equals: %s != %s."
						.formatted(_separators, other._separators)
				);
			}

			for (int i = other._frequencies.length; --i >= 0;) {
				_frequencies[i] += other._frequencies[i];
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
				.mapToObj(i -> new Bucket(
					i == 0 ? -MAX_VALUE : _separators.at(i - 1),
					i == _frequencies.length - 1
						? MAX_VALUE
						: _separators.at(i),
					_frequencies[i]
				))
				.toList();

			return new Histogram(buckets);
		}

		public Histogram build(final Consumer<? super DoubleConsumer> samples) {
			samples.accept(this);
			return build();
		}

		/**
		 * Return a <i>histogram</i> for {@link Double} values. The <i>histogram</i>
		 * array of the returned {@link Histogram} will look like this:
		 * <pre>{@code
		 *  -Ꝏ   min                                           max   Ꝏ
		 *     ----+----+----+----+----+----+----+----+  ~  +----+----
		 *         | 1  | 2  | 3  | 4  |  5 | 6  | 7  |     | nc |
		 *     ----+----+----+----+----+----+----+----+  ~  +----+----
		 * }</pre>
		 * The range of all classes will be equal {@code (max - min)/nclasses} and
		 * an open bin at the beginning and end is added. This leads to a
		 * {@code #bucketCount()} of {@code nclasses + 2}.
		 *
		 * @see Separators#of(double, double, int)
		 *
		 * @param min the minimum range value of the returned histogram.
		 * @param max the maximum range value of the returned histogram.
		 * @param nclasses the number of histogram classes, where the number of
		 *        separators will be {@code nclasses - 1}.
		 * @return a new <i>histogram</i> for {@link Double} values.
		 * @throws NullPointerException if {@code min} or {@code max} is {@code null}.
		 * @throws IllegalArgumentException if {@code min >= max} or min or max are
		 *         not finite or {@code nclasses < 2}
		 */
		public static Builder of(
			final double min,
			final double max,
			final int nclasses
		) {
			return new Builder(Separators.of(min, max, nclasses));
		}
	}

}
