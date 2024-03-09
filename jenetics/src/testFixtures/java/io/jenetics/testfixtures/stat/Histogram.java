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

import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.math.Basics.normalize;

import java.util.Arrays;
import java.util.function.DoubleConsumer;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jenetics.util.DoubleRange;

/**
 * To create an <i>Histogram Accumulator</i> you have to define the <i>class
 * border</i> which define the histogram classes. A value is part of the
 * <i>i</i><sup>th</sup> histogram array element:
 * <p>
 * <img
 *     src="doc-files/histogram-class.gif"
 *     alt="i=\left\{\begin{matrix}  0 & when & v < c_0 \\
 *         len(c) & when & v \geq c_{len(c)-1} \\
 *         j & when & c_j< v \leq c_{j-1}  \\  \end{matrix}\right."
 * >
 * </p>
 *
 * Example:
 * <pre>
 * Separators:             0    1    2    3    4    5    6    7    8    9
 *                  -------+----+----+----+----+----+----+----+----+----+------
 * Frequencies:        20  | 12 | 14 | 17 | 12 | 11 | 13 | 11 | 10 | 19 |  18
 *                  -------+----+----+----+----+----+----+----+----+----+------
 * Histogram index:     0     1    2    3    4    5    6    7    8    9    10
 * </pre>
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access this object concurrently, and at least one of the
 * threads modifies it, it must be synchronized externally.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public final class Histogram implements DoubleConsumer {

	public record Bin(double min, double max, long count) {
		public Bin {
			if (min >= max || count < 0) {
				throw new IllegalArgumentException(
					"Invalid Bin[min=%f, max=%f, count=%d]."
						.formatted(min, max, count)
				);
			}
		}

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
	 * @throws IllegalArgumentException if the given separators array is empty.
	 */
	public Histogram(final double... separators) {
		if (separators.length == 0) {
			throw new IllegalArgumentException("Separator array is empty.");
		}

		_separators = separators.clone();
		Arrays.sort(_separators);
		_table = new long[separators.length + 1];
	}

	public DoubleRange range() {
		return DoubleRange.of(_separators[0], _separators[_separators.length - 1]);
	}

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
	 * Return a copy of the current histogram.
	 *
	 * @return a copy of the current histogram.
	 */
	public long[] table() {
		return _table.clone();
	}

	public long[] hist() {
		final var hist = new long[_table.length - 2];
		System.arraycopy(_table, 1, hist, 0, hist.length);
		return hist;
	}

	public double[] normalizedTable() {
		return normalize(table());
	}

	/**
	 * Return the number of classes of this histogram.
	 *
	 * @return the number of classes of this histogram.
	 */
	public int binCount() {
		return _table.length;
	}

	public int degreesOfFreedom() {
		return binCount() - 1;
	}

	public double chi2(final Cdf cdf) {
		requireNonNull(cdf);

		final var chi2 = bins()
			.map(bin -> new double[] {bin.count*bin.count, bin.probability(cdf)*_count})
			.filter(values -> values[0] != 0.0)
			.mapToDouble(values -> values[0]/values[1])
			.sum();

		return chi2 - _count;
	}

	public long sampleCount() {
		return _count;
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
	 *
	 * <pre>
	 *    min                            max
	 *     +----+----+----+----+  ~  +----+
	 *     | 1  | 2  | 3  | 4  |     | nc |
	 *     +----+----+----+----+  ~  +----+
	 * </pre>
	 *
	 * The range of all classes will be equal: {@code (max - min)/nclasses}.
	 *
	 * @param min the minimum range value of the returned histogram.
	 * @param max the maximum range value of the returned histogram.
	 * @param classes the number of classes of the returned histogram. The
	 *        number of separators will be {@code nclasses - 1}.
	 * @return a new <i>histogram</i> for {@link Double} values.
	 * @throws NullPointerException if {@code min} or {@code max} is {@code null}.
	 * @throws IllegalArgumentException if {@code min.compareTo(max) >= 0} or
	 *         {@code nclasses < 2}.
	 */
	public static Histogram of(
		final double min,
		final double max,
		final int classes
	) {
		if (!Double.isFinite(min) || !Double.isFinite(max) || min >= max) {
			throw new IllegalArgumentException();
		}
		if (classes < 1) {
			throw new IllegalArgumentException();
		}

		final double stride = (max - min)/classes;
		final double[] bins = new double[classes + 1];

		bins[0] = min;
		bins[bins.length - 1] = max;
		for (int i = 1; i < classes; ++i) {
			bins[i] = bins[i - 1] + stride;
		}

		return new Histogram(bins);
	}

	public static Collector<Histogram, ?, Histogram>
	toDoubleHistogram(final double min, final double max, final int classCount) {
		return Collector.of(
			() -> Histogram.of(min, max, classCount),
			Histogram::combine,
			(a, b) -> {a.combine(b); return a;}
		);
	}

}
