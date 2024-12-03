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
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.util.Objects.requireNonNull;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
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
 */
public record Histogram(List<Bucket> buckets) {

	/**
	 * This class represents the bucket <em>separators</em> of a histogram. The
	 * graph below shows the separators and the associated buckets.
	 * <pre>{@code
	 * -Ꝏ     min                                          max    Ꝏ
	 *     -----+----+----+----+----+----+----+----+----+----+-----
	 *       0  | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10    Buckets
	 *     -----+----+----+----+----+----+----+----+----+----+-----
	 *          0    1    2    3    4    5    6    7    8    9       Separators
	 * }</pre>
	 * The number of buckets is the number of separators plus one.
	 */
	static final class Separators {
		private final double[] values;
		private final int start;
		private final int end;

		private Separators(final double[] values, final int start, final int end) {
			Objects.checkFromIndexSize(start, end - start, values.length);
			Objects.checkFromToIndex(start, end, values.length);

			this.values = values;
			this.start = start;
			this.end = end;
		}

		/**
		 * Create a new {@code Separators} object from the given {@code separators}.
		 * If no separator is given, this object defines only one bucket.
		 *
		 * @param values the separator values
		 * @throws IllegalArgumentException if the separator values are not
		 *         finite or not unique
		 */
		public Separators(final double... values) {
			this(check(values), 0, values.length);
		}

		private static double[] check(final double[] values) {
			for (var separator : values) {
				if (!Double.isFinite(separator)) {
					throw new IllegalArgumentException(
						"All separator values must be finite: %s."
							.formatted(Arrays.toString(values))
					);
				}
			}

			final var result = values.clone();
			Arrays.sort(result);

			for (int i = 1; i < result.length; ++i) {
				if (result[i - 1] == result[i]) {
					throw new IllegalArgumentException(
						"Separators must be unique: %s."
							.formatted(Arrays.toString(result))
					);
				}
			}

			return result;
		}

		/**
		 * Return a copy of the separator values.
		 *
		 * @return a copy of the separator values
		 */
		public double[] values() {
			return Arrays.copyOfRange(values, start, end);
		}

		/**
		 * Create a new {@link Separators} object with the values from the given
		 * {@code start} index.
		 *
		 * @param start the start index of the separator values
		 * @return a new separators object from the given {@code start} index
		 * @throws IndexOutOfBoundsException if the indexes are out of range
		 */
		public Separators slice(final int start) {
			return new Separators(values, start, end);
		}

		/**
		 * Create a new {@link Separators} object with the values from the given
		 * {@code start} and {@code end} index. Negative {@code end} indexes
		 * indicates that the separator values are trimmed from the <em>end</em>.
		 * {@snippet lang="java":
		 * final var separators = new Separators(0, 1, 2, 3, 4, 5, 6, 7, 9, 10);
		 * System.out.println(separators.slice(1, -1));
		 * }
		 * The snippet above will print
		 * <pre>
		 * [1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0]
		 * </pre>
		 *
		 * @param start the start index of the separator values (inclusively)
		 * @param end the end index of the separator values (exclusively). If
		 *        the index is negative, the values will be trimmed from the
		 *        other side of the array
		 * @return a new separators object from the given {@code start} index
		 * @throws IndexOutOfBoundsException if the indexes are out of range
		 */
		public Separators slice(final int start, final int end) {
			return new Separators(values, start, end < 0 ? this.end + end : end);
		}

		/**
		 * Return the minimal separator value. If no separator is defined,
		 * {@link Double#NEGATIVE_INFINITY} is returned.
		 *
		 * @return the minimal separator value or {@link Double#NEGATIVE_INFINITY}
		 *         if no separator value is defined
		 */
		public double min() {
			return length() > 0 ? at(0) : Double.NEGATIVE_INFINITY;
		}

		/**
		 * Return the maximal separator value. If no separator is defined,
		 * {@link Double#POSITIVE_INFINITY} is returned.
		 *
		 * @return the maximal separator value {@link Double#POSITIVE_INFINITY}
		 * 		  if no separator value is defined
		 */
		public double max() {
			return length() > 0 ? at(length() - 1) : Double.POSITIVE_INFINITY;
		}

		/**
		 * Return the number of separators.
		 *
		 * @return the number of separators
		 */
		public int length() {
			return end - start;
		}

		/**
		 * Return the separator at the given index.
		 *
		 * @param index the separator index
		 * @return the separator at the given index.
		 */
		public double at(final int index) {
			Objects.checkIndex(index, length());
			return values[start + index];
		}

		/**
		 * Return the bucket index for the given value. A binary search is
		 * performed for finding the bucket index. If no separator is defined,
		 * this method will return zero, since there is only one big bucket.
		 *
		 * @param value the value to search
		 * @return the bucket index
		 */
		public int bucketIndexOf(final double value) {
			if (Double.isNaN(value)) {
				throw new IllegalArgumentException("NaN");
			}
			if (length() == 0) {
				return 0;
			}
			if (value == Double.NEGATIVE_INFINITY) {
				return length() - 1;
			}
			if (value == Double.POSITIVE_INFINITY) {
				return 0;
			}

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
			int result = 1;
			for (int i = start; i < end; ++i) {
				final var element = values[i];
				result = 31 * result + Double.hashCode(element);
			}
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof Separators sep &&
				Arrays.equals(values, start, end, sep.values, sep.start, sep.end);
		}

		@Override
		public String toString() {
			int max = length() - 1;
			if (max == -1) {
				return "[]";
			}

			final var b = new StringBuilder();
			b.append('[');
			for (int i = 0; ; ++i) {
				b.append(at(i));
				if (i == max) {
					return b.append(']').toString();
				}
				b.append(", ");
			}
		}

		/**
		 * Return a new separator object with the given <em>finite</em>
		 * {@code min} and {@code max} separator values and given number of
		 * classes between minimal and maximal values.
		 * <pre>{@code
		 * -Ꝏ     min                                          max    Ꝏ
		 *     -----+----+----+----+----+----+----+----+----+----+-----
		 *       0  | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10    Buckets
		 *     -----+----+----+----+----+----+----+----+----+----+-----
		 *          0    1    2    3    4    5    6    7    8    9       Separators
		 * }</pre>
		 * The length of the created {@code Separator} class will be
		 * {@code nclasses + 1} with equally spaced separators of
		 * {@code (max - min)/nclasses}. {@code nclasses} will also define
		 * {@code nclasses + 1} buckets.
		 *
		 * @param min the minimum separator value, inclusively
		 * @param max the maximum separator value, exclusively
		 * @param nclasses the number of classes between the {@code min} and
		 *        {@code max} values
		 * @return a new separator object
		 * @throws IllegalArgumentException if {@code min >= max} or {@code min}
		 *         or {@code max} are not finite or {@code nclasses < 1}
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
			if (nclasses < 1) {
				throw new IllegalArgumentException(
					"Number of classes must at least one: %d."
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
	 * Represents the actual frequency data for each bucket of the histogram.
	 */
	static final class Frequencies {
		private final long[] values;
		private final int start;
		private final int end;

		private Frequencies(final long[] values, final int start, final int end) {
			Objects.checkFromIndexSize(start, end - start, values.length);
			Objects.checkFromToIndex(start, end, values.length);

			this.values = values;
			this.start = start;
			this.end = end;
		}

		/**
		 * Create a new frequency object from the given {@code frequency} values.
		 *
		 * @param values the frequency values
		 * @throws IllegalArgumentException if the given array has less than
		 *         one element
		 */
		public Frequencies(final long... values) {
			this(values.clone(), 0, values.length);

			if (values.length < 1) {
				throw new IllegalArgumentException(
					"Frequency array length must be at least one, but was %d."
						.formatted(values.length)
				);
			}
		}

		/**
		 * Calculates the sample counts by summing the frequency values.
		 *
		 * @return the sample count
		 */
		public long sampleCount() {
			long sum = 0;
			for (var value : values) {
				sum += value;
			}
			return sum;
		}

		public long[] values() {
			return Arrays.copyOfRange(values, start, end);
		}

		/**
		 * Return the length of the frequency array.
		 *
		 * @return the length of the frequency array
		 */
		public int length() {
			return end - start;
		}

		/**
		 * Returns the frequency value at the given {@code index}.
		 *
		 * @param index the frequency index
		 * @return the frequency value at the given {@code index}
		 */
		public long at(final int index) {
			return values[start + index];
		}

		/**
		 * Create a new {@link Frequencies} object with the values from the given
		 * {@code start} index.
		 *
		 * @param start the start index of the frequency values
		 * @return a new frequency object from the given {@code start} index
		 * @throws IndexOutOfBoundsException if the indexes are out of range
		 */
		public Frequencies slice(final int start) {
			return new Frequencies(values, start, end);
		}

		/**
		 * Create a new {@link Frequencies} object with the values from the given
		 * {@code start} and {@code end} index. Negative {@code end} indexes
		 * indicates that the frequency values are trimmed from the <em>end</em>.
		 * {@snippet lang="java":
		 * final var frequencies = new Frequencies(0, 1, 2, 3, 4, 5, 6, 7, 9, 10);
		 * System.out.println(frequencies.slice(1, -1));
		 * }
		 * The snippet above will print
		 * <pre>
		 * [1, 2, 3, 4, 5, 6, 7, 8, 9]
		 * </pre>
		 *
		 * @param start the start index of the separator values (inclusively)
		 * @param end the end index of the separator values (exclusively). If
		 *        the index is negative, the values will be trimmed from the
		 *        other side of the array
		 * @return a new separators object from the given {@code start} index
		 * @throws IndexOutOfBoundsException if the indexes are out of range
		 */
		public Frequencies slice(final int start, final int end) {
			return new Frequencies(values, start, end < 0 ? this.end + end : end);
		}

		@Override
		public int hashCode() {
			int result = 1;
			for (int i = start; i < end; ++i) {
				final var element = values[i];
				result = 31 * result + Long.hashCode(element);
			}
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof Frequencies f &&
				Arrays.equals(values, start, end, f.values, f.start, f.end);
		}

		@Override
		public String toString() {
			int max = length() - 1;
			if (max == -1) {
				return "[]";
			}

			final var b = new StringBuilder();
			b.append('[');
			for (int i = 0; ; ++i) {
				b.append(at(i));
				if (i == max) {
					return b.append(']').toString();
				}
				b.append(", ");
			}
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
//		if (frequencies.length() != separators.length() + 1) {
//			throw new IllegalArgumentException(
//				"Frequencies length must be separator length + 1: %d != %d."
//					.formatted(frequencies.length(), separators.length() + 1)
//			);
//		}

		if (buckets.isEmpty()) {
			throw new IllegalArgumentException("Buckets list must not be empty.");
		}
		buckets = List.copyOf(buckets);
	}

	/**
	 * Return the <em>closed</em> range of the histogram.
	 *
	 * @return the closed range of the histogram
	 */
	public DoubleRange range() {
		return DoubleRange.of(buckets.getFirst().min(), buckets.getLast().max());
	}

//	/**
//	 * Return the elements of {@code this} {@code Buckets} object.
//	 *
//	 * @return a new bucket stream
//	 */
//	public Stream<Bucket> stream() {
//		return IntStream.range(0, frequencies.length())
//			.mapToObj(i -> new Bucket(
//				i == 0 ? NEGATIVE_INFINITY : separators.at(i - 1),
//				i == frequencies.length() - 1 ? POSITIVE_INFINITY : separators.at(i),
//				frequencies.at(i)
//			));
//	}

//	/**
//	 * Return the number histogram bins, which is defined at
//	 * {@code frequencies().length}.
//	 *
//	 * @return the number histogram bins
//	 */
//	public int bucketCount() {
//		return frequencies.length();
//	}

	/**
	 * Return the <em>degrees of freedom</em> of the histogram, which is
	 * {@code #buckets().size() - 1}.
	 *
	 * @return the degrees of freedom
	 */
	public int degreesOfFreedom() {
		return buckets.size() - 1;
	}

	public Histogram slice(final int start, final int end) {
		return new Histogram(buckets.subList(start, end < 0 ? buckets.size() + end : end));
	}

	public long[] frequencies() {
		return buckets.stream()
			.mapToLong(Bucket::count)
			.toArray();
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

	private static final class Printer {
		private static final String FULL = "██ ";
		private static final String EMPTY = "   ";

		private final int _frequencyStepCount;

		private Printer(final int frequencyStepCount) {
			_frequencyStepCount = frequencyStepCount;
		}

		void print(PrintStream out, Histogram histogram) {
			/*
			final long[] values = histogram.frequencies().slice(1, -1).values();
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
			 */
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
			out.printf("%-" + cellSizes[i] + "s", cols[i]);
			out.print("│");
		}
		out.printf("%-" + cellSizes[cols.length - 1] + "s", cols[cols.length - 1]);
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
