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

import java.io.PrintStream;
import java.util.Arrays;
import java.util.function.DoubleConsumer;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

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
 * of the frequency {@link #table()}.
 * <p>
 * <b>Histogram creation from stream</b>
 * {@snippet lang="java":
 * final Histogram observation = RandomGenerator.getDefault()
 *     .doubles(10000)
 *     .collect(
 *          () -> Histogram.of(0.0, 1.0, 20),
 *          Histogram::accept,
 *          Histogram::combine
 *     );
 * }
 * <p>
 * <b>Histogram creation from array</b>
 * {@snippet lang="java":
 * final double[] data = null; // @replace substring='null' replacement="..."
 * final Histogram observation = Histogram.of(0.0, 1.0, 20);
 * for (var d : data) {
 *     observation.accept(d);
 * }
 * }
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public final class Histogram implements DoubleConsumer {

	/**
	 * Represents on histogram bin.
	 *
	 * @param min the minimal value of the bin range, inclusively
	 * @param max the maximal value of the bin range, exclusively
	 * @param count the bin count
	 */
	public record Bin(double min, double max, long count) {
		public Bin {
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
		double probability(final Cdf cdf) {
			return cdf.apply(max) - cdf.apply(min);
		}
	}

	private final double[] _separators;
	private final long[] _table;

	private long _count = 0;

	/**
	 * Create a new Histogram with the given class separators. The number of
	 * classes is {@code separators.length + 1}. A valid histogram consists of
	 * at least two classes (with one separator).
	 *
	 * @param separators the class separators.
	 * @throws NullPointerException if the classes of one of its elements
	 *         or the {@code comparator} is {@code null}.
	 * @throws IllegalArgumentException if the given separators array is smaller
	 *         than three
	 */
	public Histogram(final double... separators) {
		if (separators.length < 3) {
			throw new IllegalArgumentException("""
				At least three separators, which form two buckets are required, \
				but found %d.""".formatted(separators.length)
			);
		}

		_separators = sanatize(separators);
		_table = new long[separators.length + 1];
	}

	private static double[] sanatize(final double[] separators) {
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

		return result;
	}

	/**
	 * Return the <em>closed</em> range of the histogram.
	 *
	 * @return the closed range of the histogram
	 */
	public DoubleRange range() {
		return DoubleRange.of(_separators[0], _separators[_separators.length - 1]);
	}

	/**
	 * Return a stream of the histogram bins, inclusively the <em>open</em> bins
	 * at the beginning and the end.
	 *
	 * @return a stream of the histogram bins
	 */
	public Stream<Bin> bins() {
		return IntStream.range(0, _table.length).mapToObj(i -> {
			if (i == 0) {
				return new Bin(Double.NEGATIVE_INFINITY, _separators[i], _table[i]);
			} else if (i == _table.length - 1) {
				return new Bin(_separators[i - 1], Double.POSITIVE_INFINITY, _table[i]);
			} else {
				return new Bin(_separators[i - 1], _separators[i], _table[i]);
			}
		});
	}

	@Override
	public void accept(final double value) {
		++_count;
		++_table[index(value)];
	}

	/**
	 * Combine the given {@code other} histogram with {@code this} one.
	 *
	 * @param other the histogram to add.
	 * @throws IllegalArgumentException if the {@link #binCount()} and the
	 *         separators of {@code this} and the given {@code histogram} are
	 *         not the same.
	 * @throws NullPointerException if the given {@code histogram} is {@code null}.
	 */
	public void combine(final Histogram other) {
		if (!Arrays.equals(_separators, other._separators)) {
			throw new IllegalArgumentException(
				"The histogram separators are not equals."
			);
		}

		_count += other._count;
		for (int i = other._table.length; --i >= 0;) {
			_table[i] += other._table[i];
		}
	}

	/**
	 * Do binary search for the index to use.
	 *
	 * @param value the value to search.
	 * @return the histogram index.
	 */
	private int index(final double value) {
		int low = 0;
		int high = _separators.length - 1;

		while (low <= high) {
			if (value < _separators[low]) {
				return low;
			}
			if (value >= _separators[high]) {
				return high + 1;
			}

			final int mid = (low + high) >>> 1;
			if (value < _separators[mid]) {
				high = mid;
			} else if (value >= _separators[mid]) {
				low = mid + 1;
			}
		}

		throw new AssertionError("This line will never be reached.");
	}

	/**
	 * Return a copy of the current histogram data, inclusively the open bins
	 * at the beginning and the end.
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
	 * @return a copy of the current histogram.
	 */
	public long[] table() {
		return _table.clone();
	}

	/**
	 * Return the <em>closed</em> histogram data.
	 * <pre>{@code
	 * min                                          max
	 *  +----+----+----+----+----+----+----+----+----+
	 *  | 12 | 14 | 17 | 12 | 11 | 13 | 11 | 10 | 19 |
	 *  +----+----+----+----+----+----+----+----+----+
	 *    0    1    2    3    4    5    6    7    8
	 * }</pre>
	 *
	 * @see #table()
	 *
	 * @return the closed histogram data
	 */
	public long[] histogram() {
		return Arrays.copyOfRange(_table, 1, _table.length - 2);
	}

	/**
	 * Return the number histogram bins, which is defined at
	 * {@code table().length}.
	 *
	 * @return the number histogram bins
	 */
	public int binCount() {
		return _table.length;
	}

	/**
	 * Return the <em>degrees of freedom</em> of the histogram, which is
	 * {@link #binCount()} - 1.
	 *
	 * @return the degrees of freedom
	 */
	public int degreesOfFreedom() {
		return binCount() - 1;
	}

	/**
	 * Return the number of samples, which generated the histogram.
	 *
	 * @return the number of samples
	 */
	public long sampleCount() {
		return _count;
	}

	public void print(PrintStream out) {
		final var hist = histogram();
		long max = LongStream.of(hist).max().orElse(0);

		double factor = 80.0/max;

		out.print("+");
		out.println("-".repeat(80));
		for (var count : hist) {
			out.print("|");
			int m = (int)(count*factor);
			out.println("*".repeat(m));
		}
		out.print("+");
		out.println("-".repeat(80));
	}

	@Override
	public String toString() {
		return Arrays.toString(_separators) + "\n" +
			Arrays.toString(_table) +
			"\nSamples: " + _count;
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
	 * {@link #binCount()} of {@code nclasses + 2}.
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
	public static Histogram of(
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
				"Number of classes must at least two: %d.".formatted(nclasses)
			);
		}

		final var stride = (max - min)/nclasses;
		final var separators = new double[nclasses + 1];

		separators[0] = min;
		separators[separators.length - 1] = max;
		for (int i = 1; i < nclasses; ++i) {
			separators[i] = separators[i - 1] + stride;
		}

		return new Histogram(separators);
	}

	public static Collector<Histogram, ?, Histogram>
	toDoubleHistogram(final double min, final double max, final int nclasses) {
		return Collector.of(
			() -> Histogram.of(min, max, nclasses),
			Histogram::combine,
			(a, b) -> {a.combine(b); return a;}
		);
	}

}
