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
package io.jenetics.testfixtures.stat;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.util.Objects.requireNonNull;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.DoubleConsumer;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.stat.DoubleMoments;
import io.jenetics.util.DoubleRange;

/**
 * This class lets you create a histogram from {@code double} sample data. The
 * following graph shows the structure of the histogram.
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
 * of the frequency {@link #frequencies()}.
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
 */
public record Histogram(
	Separators separators,
	Frequencies frequencies,
	DoubleMoments moments
)
	implements Iterable<Histogram.Bucket>
{

	/**
	 * Histogram build class.
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
		 * @throws IllegalArgumentException if the {@link #bucketCount()} and the
		 *         separators of {@code this} and the given {@code histogram} are
		 *         different.
		 * @throws NullPointerException if the given {@code histogram} is {@code null}.
		 */
		public void combine(final Builder other) {
			if (!_separators.equals(other._separators)) {
				throw new IllegalArgumentException(
					"The histogram separators are not equals."
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
			return new Histogram(_separators, new Frequencies(_frequencies), DoubleMoments.of(_statistics));
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
		 * {@link #bucketCount()} of {@code nclasses + 2}.
		 *
		 * @param min the minimum range value of the returned histogram.
		 * @param max the maximum range value of the returned histogram.
		 * @param nclasses the number of classes of the returned histogram. The
		 *        number of separators will be {@code nclasses - 1}.
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


	/**
	 * This class represents the bucket separators of the histogram.
	 * <pre>{@code
	 * min                                          max
	 *  +----+----+----+----+----+----+----+----+----+
	 *  0    1    2    3    4    5    6    7    8    9
	 * }</pre>
	 */
	public static final class Separators {

		private final double[] _separators;

		/**
		 * Create a new {@code Separators} object from the given {@code separators}.
		 *
		 * @param separators the separator values
		 * @throws IllegalArgumentException if {@code separators.length < 3},
		 *         the separator values are not finite or not unique
		 */
		public Separators(final double... separators) {
			if (separators.length < 3) {
				throw new IllegalArgumentException("""
					At least three separators, which form two buckets are \
					required, but found %d.""".formatted(separators.length)
				);
			}
			for (var separator : separators) {
				if (!Double.isFinite(separator)) {
					throw new IllegalArgumentException(
						"All separator values must be finite: %s."
							.formatted(Arrays.toString(separators))
					);
				}
			}

			final var result = separators.clone();
			Arrays.sort(result);

			for (int i = 1; i < result.length; ++i) {
				if (result[i - 1] == result[i]) {
					throw new IllegalArgumentException(
						"Separators must be unique: %s."
							.formatted(Arrays.toString(result))
					);
				}
			}

			_separators = result;
		}

		/**
		 * Return the minimal separator value.
		 *
		 * @return the minimal separator value
		 */
		public double min() {
			return _separators[0];
		}

		/**
		 * Return the maximal separator value.
		 *
		 * @return the maximal separator value
		 */
		public double max() {
			return _separators[_separators.length - 1];
		}

		/**
		 * Return the minimal and maximal separator values.
		 *
		 * @return the minimal and maximal separator values
		 */
		public DoubleRange range() {
			return DoubleRange.of(min(), max());
		}

		/**
		 * Return the number of separators.
		 *
		 * @return the number of separators
		 */
		public int length() {
			return _separators.length;
		}

		/**
		 * Return the separator at the given index.
		 *
		 * @param index the separator index
		 * @return the separator at the given index.
		 */
		public double at(final int index) {
			return _separators[index];
		}

		/**
		 * Do binary search for the bucket index of the given value.
		 *
		 * @param value the value to search
		 * @return the bucket index
		 */
		public int bucketIndexOf(final double value) {
			int low = 0;
			int high = length() - 1;

			while (low <= high) {
				if (value < at(low)) {
					return low;
				}
				if (value >= at(high)) {
					return high + 1;
				}

				final int mid = (low + high) >>> 1;
				if (value < at(mid)) {
					high = mid;
				} else if (value >= at(mid)) {
					low = mid + 1;
				}
			}

			throw new AssertionError("This line will never be reached.");
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(_separators);
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof Separators sep &&
				Arrays.equals(_separators, sep._separators);
		}

		@Override
		public String toString() {
			return Arrays.toString(_separators);
		}

		/**
		 * Return a new separator object with the given <em>finite</em>
		 * {@code min} and {@code max} separator values and given number of
		 * classes.
		 * <pre>{@code
		 *        min                                           max
		 *         +----+----+----+----+----+----+----+  ~  +----+
		 *           1    2    3    4     5   6    7          nc
		 * }</pre>
		 * The length of the created {@code Separator} class will be
		 * {@code nclasses + 1} with equally spaced separators of
		 * {@code (max - min)/nclasses}.
		 *
		 * @param min the minimum separator value, inclusively
		 * @param max the maximum separator value, exclusively
		 * @param nclasses the number of classes
		 * @return a new separator object
		 * @throws IllegalArgumentException if {@code min >= max} or {@code min}
		 *         or {@code max} are not finite or {@code nclasses < 2}
		 */
		public static Separators of(
			final double min,
			final double max,
			final int nclasses
		) {
			if (!Double.isFinite(min) || !Double.isFinite(max) || min >= max) {
				throw new IllegalArgumentException(
					"Invalid border: [min=%f, max=%f].".formatted(min, max)
				);
			}
			if (nclasses < 2) {
				throw new IllegalArgumentException(
					"Number of classes must at least two: %d."
						.formatted(nclasses)
				);
			}

			final var stride = (max - min)/nclasses;
			final var separators = new double[nclasses + 1];

			separators[0] = min;
			separators[separators.length - 1] = max;
			for (int i = 1; i < nclasses; ++i) {
				separators[i] = separators[i - 1] + stride;
			}

			return new Separators(separators);
		}
	}

	/**
	 * Represents the actual frequency data of the histogram.
	 * <pre>{@code
	 * -Ꝏ     min                                          max    Ꝏ
	 *     -----+----+----+----+----+----+----+----+----+----+-----
	 *      20  | 12 | 14 | 17 | 12 | 11 | 13 | 11 | 10 | 19 | 18
	 *     -----+----+----+----+----+----+----+----+----+----+-----
	 *       0    1    2    3    4    5    6    7    8    9    10
	 * }</pre>
	 */
	public static final class Frequencies {
		private final long[] _frequencies;

		/**
		 * Create a new frequency object from the given {@code frequency} values.
		 *
		 * @param frequencies the frequency values
		 * @throws IllegalArgumentException if the given array has less than
		 *         four elements
		 */
		public Frequencies(final long... frequencies) {
			if (frequencies.length < 4) {
				throw new IllegalArgumentException(
					"Frequency array length must be at least 4, but was %d."
						.formatted(frequencies.length)
				);
			}

			_frequencies = frequencies.clone();
		}

		/**
		 * Calculates the sample counts by summing the frequency values.
		 *
		 * @return the sample count
		 */
		public long sampleCount() {
			return io.jenetics.internal.util.Arrays.sum(_frequencies);
		}

		/**
		 * Return the frequency values as {@code long[]} array.
		 * <pre>{@code
		 * -Ꝏ     min                                          max    Ꝏ
		 *     -----+----+----+----+----+----+----+----+----+----+-----
		 *      20  | 12 | 14 | 17 | 12 | 11 | 13 | 11 | 10 | 19 | 18
		 *     -----+----+----+----+----+----+----+----+----+----+-----
		 *       0    1    2    3    4    5    6    7    8    9    10
		 * }</pre>
		 *
		 * @see #histogram()
		 *
		 * @return the frequency values.
		 */
		public long[] values() {
			return _frequencies.clone();
		}

		/**
		 * Return the histogram values, which are the frequency values with the
		 * first and last element removed.
		 * <pre>{@code
		 * min                                          max
		 *  +----+----+----+----+----+----+----+----+----+
		 *  | 12 | 14 | 17 | 12 | 11 | 13 | 11 | 10 | 19 |
		 *  +----+----+----+----+----+----+----+----+----+
		 *    0    1    2    3    4    5    6    7    8
		 * }</pre>
		 *
		 * @see #values()
		 *
		 * @return the histogram values
		 */
		public long[] histogram() {
			return Arrays.copyOfRange(_frequencies, 1, length() - 2);
		}

		/**
		 * Return the length of the frequency array.
		 *
		 * @return the length of the frequency array
		 */
		public int length() {
			return _frequencies.length;
		}

		/**
		 * Returns the frequency value at the given {@code index}.
		 *
		 * @param index the frequency index
		 * @return the frequency value at the given {@code index}
		 */
		public long at(final int index) {
			return _frequencies[index];
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(_frequencies);
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof Frequencies frequencies &&
				Arrays.equals(_frequencies, frequencies._frequencies);
		}

		@Override
		public String toString() {
			return Arrays.toString(_frequencies);
		}
	}

	/**
	 * Represents on histogram bin.
	 *
	 * @param min the minimal value of the bin range, inclusively
	 * @param max the maximal value of the bin range, exclusively
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

		/**
		 * Return the expected property of the bin, defined by the given
		 * {@code cdf}.
		 *
		 * @param cdf the CDF used for calculating the expected property
		 * @return the expected property
		 */
		public double probability(final Cdf cdf) {
			return cdf.apply(max) - cdf.apply(min);
		}
	}

	public Histogram {
		if (frequencies.length() != separators.length() + 1) {
			throw new IllegalArgumentException(
				"Frequencies length must be separator length + 1: %d != %d."
					.formatted(frequencies.length(), separators.length() + 1)
			);
		}
	}

	/**
	 * Return the <em>closed</em> range of the histogram.
	 *
	 * @return the closed range of the histogram
	 */
	public DoubleRange range() {
		return separators.range();
	}

	/**
	 * Return the elements of {@code this} {@code Buckets} object.
	 *
	 * @return a new bucket stream
	 */
	public Stream<Bucket> stream() {
		return IntStream.range(0, frequencies.length())
			.mapToObj(i -> new Bucket(
				i == 0 ? NEGATIVE_INFINITY : separators.at(i - 1),
				i == frequencies.length() - 1 ? POSITIVE_INFINITY : separators.at(i),
				frequencies.at(i)
			));
	}

	@Override
	public Iterator<Bucket> iterator() {
		return stream().iterator();
	}

	/**
	 * Return the number histogram bins, which is defined at
	 * {@code frequencies().length}.
	 *
	 * @return the number histogram bins
	 */
	public int bucketCount() {
		return frequencies.length();
	}

	/**
	 * Return the <em>degrees of freedom</em> of the histogram, which is
	 * {@link #bucketCount()} - 1.
	 *
	 * @return the degrees of freedom
	 */
	public int degreesOfFreedom() {
		return bucketCount() - 1;
	}

	/**
	 * Return the number of samples, which generated the histogram.
	 *
	 * @return the number of samples
	 */
	public long sampleCount() {
		return frequencies.sampleCount();
	}

	/**
	 * Prints a graphical representation of the histogram to the given
	 * {@code output}.
	 * {@snippet lang="java":
	 * final var builder = Histogram.Builder.of(0, 10, 20);
	 * final var random = RandomGenerator.getDefault();
	 * for (int i = 0; i < 10_000; ++i) {
	 *     builder.accept(random.nextGaussian(5, 2));
	 * }
	 *
	 * final Histogram observation = builder.build();
	 * observation.print(System.out);
	 * }
	 * <p>
	 * The code snippet above will lead to the following output.
	 * <p>
	 * <pre>{@code
	 *      ┌───────────────────────────────────────────────────────────┐
	 * 1020 ┤                            ██ ██                          │
	 *  952 ┤                         ██ ██ ██ ██                       │
	 *  884 ┤                      ██ ██ ██ ██ ██                       │
	 *  816 ┤                      ██ ██ ██ ██ ██                       │
	 *  748 ┤                      ██ ██ ██ ██ ██ ██                    │
	 *  680 ┤                   ██ ██ ██ ██ ██ ██ ██ ██                 │
	 *  612 ┤                   ██ ██ ██ ██ ██ ██ ██ ██                 │
	 *  544 ┤                ██ ██ ██ ██ ██ ██ ██ ██ ██                 │
	 *  476 ┤                ██ ██ ██ ██ ██ ██ ██ ██ ██ ██              │
	 *  408 ┤             ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██              │
	 *  340 ┤             ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██           │
	 *  272 ┤          ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██        │
	 *  204 ┤       ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██     │
	 *  136 ┤       ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██  │
	 *   68 ┤ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██ ██  │
	 *      └───┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬─┘
	 *     0.0                                                         10.0
	 * }</pre>
	 *
	 * @param output the output stream
	 */
	public void print(PrintStream output) {
		new Printer(15).print(output, this);
	}

	@Override
	public String toString() {
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

	private static final class Printer {
		private static final String FULL = "██ ";
		private static final String EMPTY = "   ";

		private final int _frequencyStepCount;

		private Printer(final int frequencyStepCount) {
			_frequencyStepCount = frequencyStepCount;
		}

		void print(PrintStream out, Histogram histogram) {
			final long[] values = histogram.frequencies().histogram();
			final long max = LongStream.of(values).max().orElse(0);
			final var stepSize = round(max/(double)_frequencyStepCount);

			final var maxStringLength = (int)Math.ceil(Math.log10(stepSize*_frequencyStepCount));
			final var formatString = "%" + maxStringLength + "d ┤ ";

			final int margin = maxStringLength + 1;
			final int length = values.length*FULL.length() + 4;

			// Print histogram
			out.print(" ".repeat(maxStringLength + 1));
			out.print("┌");
			for (int i = 0; i < values.length; i++) {
				out.print("───");
			}
			out.println("──┐");

			for (int i = _frequencyStepCount - 1; i >= 0; --i) {
				out.format(formatString, (i + 1)*stepSize);

                for (long value : values) {
                    if (value - 0.5*stepSize >= i*stepSize) {
                        out.print(FULL);
                    } else {
                        out.print(EMPTY);
                    }
                }
				out.println(" │");
			}

			out.print(" ".repeat(maxStringLength + 1));
			out.print("└──");
			for (int i = 0; i < values.length; i++) {
				out.print("─┬─");
			}
			out.println("┘");

			out.print(" ".repeat(maxStringLength));
			out.print(histogram.separators().at(0));

			final var spaces =
				maxStringLength +
				EMPTY.length()*(histogram.bucketCount() - 2) -
				Double.toString(histogram.separators().min()).length() -
				Double.toString(histogram.separators().max()).length();

			out.print(" ".repeat(spaces));
			out.print(histogram.separators().at(histogram.separators().length() - 1));
			out.println();

			// Print statistics.
			table(out, margin, length, new String[][] {
				{
					" N=%d".formatted(histogram.moments.count()),
					" ∧=%.3f".formatted(histogram.moments.min()),
					" ∨=%.3f".formatted(histogram.moments.max())
				},
				{
					" μ=%.4f".formatted(histogram.moments.mean()),
					" s²=%.4f".formatted(histogram.moments.variance()),
					" S=%.4f".formatted(histogram.moments.skewness())
				}
			});
		}

		private static long round(final double value) {
			final long size = (long)Math.ceil(value/100.0);
			final long count = (long)Math.ceil(value/size);
			return size*count;
		}

	}


	private static void table(
		PrintStream out,
		int margin,
		int length,
		String[][] table
	) {
		if (table.length >= 1) {
			final int cols = table[0].length;
			top(out, margin, length, cols);

			for (int i = 0; i < table.length; ++i) {
				final String[] line = table[i];
				line(out, margin, length, line);

				if (i < table.length - 1) {
					middle(out, margin, length, cols);
				} else {
					bottom(out, margin, length, cols);
				}
			}
		}
	}

	private static void top(PrintStream out, int margin, int length, int cols) {
		final int[] cellSizes = partition(length - cols - 1, cols);

		out.print(" ".repeat(margin));
		out.print("┌");
		for (int i = 0; i < cols - 1; ++i) {
			out.print("─".repeat(cellSizes[i]));
			out.print("┬");
		}
		out.print("─".repeat(cellSizes[cols - 1]));
		out.println("┐");
	}

	private static void middle(PrintStream out, int margin, int length, int cols) {
		final int[] cellSizes = partition(length - cols - 1, cols);

		out.print(" ".repeat(margin));
		out.print("├");
		for (int i = 0; i < cols - 1; ++i) {
			out.print("─".repeat(cellSizes[i]));
			out.print("┼");
		}
		out.print("─".repeat(cellSizes[cols - 1]));
		out.println("┤");
	}

	private static void line(PrintStream out, int margin, int length, String... cols) {
		final int[] cellSizes = partition(length - cols.length - 1, cols.length);

		out.print(" ".repeat(margin));
		out.print("│");
		for (int i = 0; i < cols.length - 1; ++i) {
			out.print(String.format("%-" + cellSizes[i] + "s", cols[i]));
			out.print("│");
		}
		out.print(String.format("%-" + cellSizes[cols.length - 1] + "s", cols[cols.length - 1]));
		out.println("│");
	}

	private static void bottom(PrintStream out, int margin, int length, int cols) {
		final int[] cellSizes = partition(length - cols - 1, cols);

		out.print(" ".repeat(margin));
		out.print("└");
		for (int i = 0; i < cols - 1; ++i) {
			out.print("─".repeat(cellSizes[i]));
			out.print("┴");
		}
		out.print("─".repeat(cellSizes[cols - 1]));
		out.println("┘");
	}

	private static int[] partition(final int size, final int parts) {
		final int[] partition = new int[parts];

		final int bulk = size/parts;
		final int rest = size%parts;

        Arrays.fill(partition, bulk);
		for (int i = 0; i < rest; ++i) {
			++partition[i];
		}

		return partition;
	}

}
