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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.stat;

import static java.lang.Math.max;
import static java.lang.Math.round;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.math.arithmetic.normalize;
import static org.jenetics.internal.util.Equality.eq;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;

import org.jenetics.internal.math.statistics;
import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

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
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class Histogram<C> implements Consumer<C> {

	private final Comparator<C> _comparator;
	private final C[] _separators;

	private long _count = 0;
	private final long[] _histogram;

	/**
	 * Create a new Histogram with the given class separators. The number of
	 * classes is {@code separators.length + 1}. A valid histogram consists of
	 * at least two classes (with one separator).
	 *
	 * @see #of(Comparable...)
	 *
	 * @param comparator the comparator for the separators.
	 * @param separators the class separators.
	 * @throws NullPointerException if the classes of one of its elements
	 *         or the {@code comparator} is {@code null}.
	 * @throws IllegalArgumentException if the given separators array is empty.
	 */
	@SafeVarargs
	public Histogram(final Comparator<C> comparator, final C... separators) {
		_separators = check(separators);
		_comparator = requireNonNull(comparator, "Comparator");
		_histogram = new long[separators.length + 1];

		Arrays.sort(_separators, _comparator);
		Arrays.fill(_histogram, 0L);
	}

	@SuppressWarnings("unchecked")
	private static <C> C[] check(final C... classes) {
		Arrays.asList(classes).forEach(Objects::requireNonNull);
		if (classes.length == 0) {
			throw new IllegalArgumentException("Given classes array is empty.");
		}

		return classes;
	}

	@Override
	public void accept(final C value) {
		++_count;
		++_histogram[index(value)];
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
	public void combine(final Histogram<C> other) {
		if (!eq(_separators, other._separators)) {
			throw new IllegalArgumentException(
				"The histogram separators are not equals."
			);
		}

		_count += other._count;
		for (int i = other._histogram.length; --i >= 0;) {
			_histogram[i] += other._histogram[i];
		}
	}

	/**
	 * Do binary search for the index to use.
	 *
	 * @param value the value to search.
	 * @return the histogram index.
	 */
	final int index(final C value) {
		int low = 0;
		int high = _separators.length - 1;

		while (low <= high) {
			if (_comparator.compare(value, _separators[low]) < 0) {
				return low;
			}
			if (_comparator.compare(value, _separators[high]) >= 0) {
				return high + 1;
			}

			final int mid = (low + high) >>> 1;
			if (_comparator.compare(value, _separators[mid]) < 0) {
				high = mid;
			} else if (_comparator.compare(value, _separators[mid]) >= 0) {
				low = mid + 1;
			}
		}

		throw new AssertionError("This line will never be reached.");
	}

	/**
	 * Return the comparator used for class search.
	 *
	 * @return the comparator.
	 */
	public Comparator<C> getComparator() {
		return _comparator;
	}

	/**
	 * Return a copy of the class separators.
	 *
	 * @return the class separators.
	 */
	public C[] getSeparators() {
		return _separators.clone();
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
		if (histogram.length >= _histogram.length) {
			System.arraycopy(_histogram, 0, hist, 0, _histogram.length);
		} else {
			hist = _histogram.clone();
		}

		return hist;
	}

	/**
	 * Return a copy of the current histogram.
	 *
	 * @return a copy of the current histogram.
	 */
	public long[] getHistogram() {
		return getHistogram(new long[_histogram.length]);
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
		return _histogram.length;
	}

	/**
	 * Return the <i>histogram</i> as probability array.
	 *
	 * @return the class probabilities.
	 */
	public double[] getProbabilities() {
		final double[] probabilities = new double[_histogram.length];

		assert (statistics.sum(_histogram) == _count);
		for (int i = 0; i < probabilities.length; ++i) {
			probabilities[i] = (double)_histogram[i]/(double)_count;
		}

		return probabilities;
	}

	/**
	 * Calculate the χ2 value of the current histogram for the assumed
	 * <a href="http://en.wikipedia.org/wiki/Cumulative_distribution_function">
	 * Cumulative density function</a> {@code cdf}.
	 *
	 * @see <a href="http://en.wikipedia.org/wiki/Chi-square_test">χ2-test</a>
	 * @see <a href="http://en.wikipedia.org/wiki/Chi-square_distribution">χ2-distribution</a>
	 *
	 * @param cdf the assumed Probability density function.
	 * @param min the lower limit of the CDF domain. A {@code null} value means
	 *         an open interval.
	 * @param max the upper limit of the CDF domain. A {@code null} value means
	 *         an open interval.
	 * @return the χ2 value of the current histogram.
	 * @throws NullPointerException if {@code cdf} is {@code null}.
	 */
	public double χ2(final ToDoubleFunction<C> cdf, final C min, final C max) {
		double χ2 = 0;
		for (int j = 0; j < _histogram.length; ++j) {
			final long n0j = n0(j, cdf, min, max);
			χ2 += ((_histogram[j] - n0j)*(_histogram[j] - n0j))/(double)n0j;
		}
		return χ2;
	}

	/**
	 * Calculate the χ2 value of the current histogram for the assumed
	 * <a href="http://en.wikipedia.org/wiki/Cumulative_distribution_function">
	 * Cumulative density function</a> {@code cdf}.
	 *
	 * @see <a href="http://en.wikipedia.org/wiki/Chi-square_test">χ2-test</a>
	 * @see <a href="http://en.wikipedia.org/wiki/Chi-square_distribution">χ2-distribution</a>
	 *
	 * @param cdf the assumed Probability density function.
	 * @return the χ2 value of the current histogram.
	 * @throws NullPointerException if {@code cdf} is {@code null}.
	 */
	public double χ2(final ToDoubleFunction<C> cdf) {
		return χ2(cdf, null, null);
	}

	private long n0(final int j, final ToDoubleFunction<C> cdf, final C min, final C max) {
		double p0j = 0.0;
		if (j == 0) {
			p0j = cdf.applyAsDouble(_separators[0]);
			if (min != null) {
				p0j = p0j - cdf.applyAsDouble(min);
			}
		} else if (j == _histogram.length - 1) {
			if (max != null) {
				p0j = cdf.applyAsDouble(max) - cdf.applyAsDouble(_separators[_separators.length - 1]);
			} else {
				p0j = 1.0 - cdf.applyAsDouble(_separators[_separators.length - 1]);
			}
		} else {
			p0j = cdf.applyAsDouble(_separators[j]) - cdf.applyAsDouble(_separators[j - 1]);
		}

		return max(round(p0j*_count), 1L);
	}

	/**
	 * @see #χ2(java.util.function.ToDoubleFunction)
	 *
	 * @param cdf the cumulative density function
	 * @return the chi square value of the given function
	 */
	public double chisqr(final ToDoubleFunction<C> cdf) {
		return χ2(cdf);
	}

	/**
	 * @see #χ2(java.util.function.ToDoubleFunction, Object, Object)
	 *
	 * @param cdf the cumulative density function
	 * @param min the lower limit
	 * @param max the upper limit
	 * @return the chi square value of the given function
	 */
	public double chisqr(final ToDoubleFunction<C> cdf, final C min, final C max) {
		return χ2(cdf, min, max);
	}

	public long getCount() {
		return _count;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(super.hashCode())
			.and(_separators)
			.and(_histogram).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(histogram ->
				eq(_separators, histogram._separators) &&
					eq(_histogram, histogram._histogram) &&
					super.equals(obj)
		);
	}

	@Override
	public String toString() {
		return Arrays.toString(_separators) + "\n" +
			Arrays.toString(getHistogram()) +
			"\nSamples: " + _count;
	}

	/**
	 * Create a new Histogram with the given class separators. The classes are
	 * sorted by its natural order.
	 *
	 * @param <C> the separator types
	 * @param separators the class separators.
	 * @return a new Histogram.
	 * @throws NullPointerException if the {@code separators} are {@code null}.
	 * @throws IllegalArgumentException if {@code separators.length == 0}.
	 */
	@SuppressWarnings("unchecked")
	public static <C extends Comparable<? super C>> Histogram<C> of(
		final C... separators
	) {
		return new Histogram<C>((o1, o2) -> o1.compareTo(o2), separators);
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
	 * @param nclasses the number of classes of the returned histogram. The
	 *        number of separators will be {@code nclasses - 1}.
	 * @return a new <i>histogram</i> for {@link Double} values.
	 * @throws NullPointerException if {@code min} or {@code max} is {@code null}.
	 * @throws IllegalArgumentException if {@code min.compareTo(max) >= 0} or
	 *         {@code nclasses < 2}.
	 */
	public static Histogram<Double> ofDouble(
		final Double min,
		final Double max,
		final int nclasses
	) {
		return of(toSeparators(min, max, nclasses));
	}

	private static Double[] toSeparators(
		final Double min,
		final Double max,
		final int nclasses
	) {
		check(min, max, nclasses);

		final double stride = (max - min)/nclasses;
		final Double[] separators = new Double[nclasses - 1];
		for (int i = 0; i < separators.length; ++i) {
			separators[i] = min + stride*(i + 1);
		}

		return separators;
	}

	/**
	 * Return a <i>histogram</i> for {@link Long} values. The <i>histogram</i>
	 * array of the returned {@link Histogram} will look like this:
	 *
	 * <pre>
	 *    min                            max
	 *     +----+----+----+----+  ~  +----+
	 *     | 1  | 2  | 3  | 4  |     | nc |
	 *     +----+----+----+----+  ~  +----+
	 * </pre>
	 *
	 * The range of all classes are more or less the same. But this is not
	 * always possible due to integer rounding issues. Calling this method with
	 * {@code min = 13} and {@code max = 99} will generate the following class
	 * separators for the given number of classes:
	 * <pre>
	 *  nclasses = 2: [56]
	 *  nclasses = 3: [41, 70]
	 *  nclasses = 4: [34, 55, 77]
	 *  nclasses = 5: [30, 47, 64, 81]
	 *  nclasses = 6: [27, 41, 55, 69, 84]
	 *  nclasses = 7: [25, 37, 49, 61, 73, 86]
	 *  nclasses = 8: [23, 33, 44, 55, 66, 77, 88]
	 *  nclasses = 9: [22, 31, 40, 49, 59, 69, 79, 89]
	 * </pre>
	 *
	 * @param min the minimum range value of the returned histogram.
	 * @param max the maximum range value of the returned histogram.
	 * @param nclasses the number of classes of the returned histogram. The
	 *        number of separators will be {@code nclasses - 1}.
	 * @return a new <i>histogram</i> for {@link Long} values.
	 * @throws NullPointerException if {@code min} or {@code max} is {@code null}.
	 * @throws IllegalArgumentException if {@code min.compareTo(max) >= 0} or
	 *         {@code nclasses < 2}.
	 */
	public static Histogram<Long> ofLong(
		final Long min,
		final Long max,
		final int nclasses
	) {
		return of(toSeparators(min, max, nclasses));
	}

	public static Histogram<Integer> ofInteger(
		final Integer min,
		final Integer max,
		final int nclasses
	) {
		final Integer[] separators = Arrays
			.stream(toSeparators(min.longValue(), max.longValue(), nclasses))
			.map(Long::intValue)
			.toArray(Integer[]::new);

		return of(separators);
	}

	private static Long[] toSeparators(
		final Long min,
		final Long max,
		final int nclasses
	) {
		check(min, max, nclasses);

		final int size = (int)(max - min);
		final int pts = Math.min(size, nclasses);
		final int bulk = size/pts;
		final int rest = size%pts;
		assert ((bulk*pts + rest) == size);

		final Long[] separators = new Long[pts - 1];
		for (int i = 1, n = pts - rest; i < n; ++i) {
			separators[i - 1] = i*bulk + min;
		}
		for (int i = 0; i < rest; ++i) {
			separators[separators.length - rest + i] =
				(pts - rest)*bulk + i*(bulk + 1) + min;
		}

		return separators;
	}

	public static Collector<Histogram<Double>, ?, Histogram<Double>>
	toDoubleHistogram(final Double min, final Double max, final int classCount) {
		return Collector.of(
			() -> Histogram.ofDouble(min, max, classCount),
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


	public <A extends Appendable> A print(final A out) throws IOException {
		Object min = "...";
		Object max = null;
		for (int i = 0; i < length() - 1; ++i) {
			max = _separators[i];
			out.append("[" + min + "," + max + ")");
			out.append(" " + _histogram[i] + "\n");
			min = max;
		}
		if (length() - 1 > 0) {
			out.append("[" + min + ",...)");
			out.append(" " + _histogram[length() - 1] + "\n");
		}

		return out;
	}

}
