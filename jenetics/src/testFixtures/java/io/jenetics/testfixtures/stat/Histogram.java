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

import static java.lang.Math.max;
import static java.lang.Math.round;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.math.Basics.normalize;
import static io.jenetics.internal.util.Hashes.hash;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;

import io.jenetics.stat.LongSummary;

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
public class Histogram implements DoubleConsumer {

	private final double[] _bins;
	private final long[] _table;

	private long _count = 0;

	/**
	 * Create a new Histogram with the given class separators. The number of
	 * classes is {@code separators.length + 1}. A valid histogram consists of
	 * at least two classes (with one separator).
	 *
	 * @param bins the class separators.
	 * @throws NullPointerException if the classes of one of its elements
	 *         or the {@code comparator} is {@code null}.
	 * @throws IllegalArgumentException if the given separators array is empty.
	 */
	@SafeVarargs
	public Histogram(final double... bins) {
		_bins = bins;
		_table = new long[bins.length + 1];
	}

	@SuppressWarnings("unchecked")
	private static <C> C[] check(final C... classes) {
		List.of(classes).forEach(Objects::requireNonNull);
		if (classes.length == 0) {
			throw new IllegalArgumentException("Given classes array is empty.");
		}

		return classes;
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
	 * @throws IllegalArgumentException if the {@link #length()} and the
	 *         separators of {@code this} and the given {@code histogram} are
	 *         not the same.
	 * @throws NullPointerException if the given {@code histogram} is {@code null}.
	 */
	public void combine(final Histogram other) {
		if (!Arrays.equals(_bins, other._bins)) {
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
	public int index(final double value) {
		int low = 0;
		int high = _bins.length - 1;

		while (low <= high) {
			if (value < _bins[low]) {
				return low;
			}
			if (value >= _bins[high]) {
				return high + 1;
			}

			final int mid = (low + high) >>> 1;
			if (value < _bins[mid]) {
				high = mid;
			} else if (value >= _bins[mid]) {
				low = mid + 1;
			}
		}

		throw new AssertionError("This line will never be reached.");
	}

	/**
	 * Copy the histogram into the given array. If the array is big enough
	 * the same array is returned, otherwise a new array is created and
	 * returned. The length of the histogram array is the number of separators
	 * plus one ({@code getSeparators().length + 1}).
	 *
	 * @param histogram array to copy the histogram.
	 * @return the histogram array.
	 * @throws NullPointerException if the given array is {@code null}.
	 */
	public long[] getHistogram(final long[] histogram) {
		requireNonNull(histogram);

		long[] hist = histogram;
		if (histogram.length >= _table.length) {
			System.arraycopy(_table, 0, hist, 0, _table.length);
		} else {
			hist = _table.clone();
		}

		return hist;
	}

	/**
	 * Return a copy of the current histogram.
	 *
	 * @return a copy of the current histogram.
	 */
	public long[] getHistogram() {
		return getHistogram(new long[_table.length]);
	}

	public double[] getNormalizedHistogram() {
		return normalize(getHistogram());
	}

	/**
	 * Return the number of classes of this histogram.
	 *
	 * @return the number of classes of this histogram.
	 */
	public int length() {
		return _table.length;
	}

	public int classes() {
		return _bins.length - 1;
	}

	/**
	 * Return the <i>histogram</i> as probability array.
	 *
	 * @return the class probabilities.
	 */
	public double[] getProbabilities() {
		final double[] probabilities = new double[_table.length];

		assert (LongSummary.sum(_table) == _count);
		for (int i = 0; i < probabilities.length; ++i) {
			probabilities[i] = (double)_table[i]/(double)_count;
		}

		return probabilities;
	}

	public double chi2(final Cdf cdf) {
		requireNonNull(cdf);

		double chi2 = 0;
		for (int i = 0; i < classes(); ++i) {
			final var e = p_i(i, cdf)*_count;
			final var o2 = _table[i + 1]*_table[i + 1];
			chi2 += o2/e;
		}

		return chi2 - _count;
	}

	private double p_i(final int i, final Cdf cdf) {
		return cdf.apply(_bins[i + 1]) - cdf.apply(_bins[i]);
	}

	public long getCount() {
		return _count;
	}

	@Override
	public String toString() {
		return Arrays.toString(_bins) + "\n" +
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

	/*
	 * Check the input values of the valueOf methods.
	 */
	private static <C extends Comparable<? super C>> void
	check(final C min, final C max, final int nclasses)
	{
		requireNonNull(min, "Minimum");
		requireNonNull(max, "Maximum");
		if (min.compareTo(max) >= 0) {
			throw new IllegalArgumentException(format(
				"Min must be smaller than max: %s < %s failed.", min, max
			));
		}
		if (nclasses < 2) {
			throw new IllegalArgumentException(format(
				"nclasses should be < 2, but was %s.", nclasses
			));
		}
	}

}
