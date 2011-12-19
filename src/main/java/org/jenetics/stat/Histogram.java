/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *     
 */
package org.jenetics.stat;

import static java.lang.Math.max;
import static java.lang.Math.round;
import static org.jenetics.util.arrays.foreach;
import static org.jenetics.util.functions.DoubleToFloat64;
import static org.jenetics.util.functions.LongToInteger64;
import static org.jenetics.util.math.sum;
import static org.jenetics.util.object.NonNull;
import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;
import static org.jenetics.util.object.nonNull;

import java.util.Arrays;
import java.util.Comparator;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.number.Integer64;

import org.jenetics.util.Function;
import org.jenetics.util.MappableAccumulator;
import org.jenetics.util.arrays;

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
 * />
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
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Histogram<C> extends MappableAccumulator<C> {
    
	private final C[] _separators;
	private final Comparator<C> _comparator;
	private final long[] _histogram;
	
	/**
	 * Create a new Histogram with the given class separators. The number of
	 * classes is {@code separators.length + 1}. A valid histogram consists of
	 * at least two classes (with one separator).
	 * 
	 * @see #valueOf(Comparable...)
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
		_comparator = nonNull(comparator, "Comparator");
		_histogram = new long[separators.length + 1];
		
		Arrays.sort(_separators, _comparator);
		Arrays.fill(_histogram, 0L);
	}
	
	@SafeVarargs
	private Histogram(
		final long[] histogram, 
		final Comparator<C> comparator, 
		final C... separators
	) {
		_histogram = histogram;
		_comparator = comparator;
		_separators = separators;
	}
	
	@SuppressWarnings("unchecked")
	private C[] check(final C... classes) {
		foreach(classes, NonNull);
		if (classes.length == 0) {
			throw new IllegalArgumentException("Given classes array is empty.");
		}
		
		return classes;
	}
	
	@Override
	public void accumulate(final C value) {
		++_histogram[index(value)];
		++_samples;
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
		
		assert (false): "This line will never be reached.";
		return -1; 
	}
	
	/**
	 * Add the given {@code histogram} to this in a newly created one.
	 * 
	 * @param histogram the histogram to add.
	 * @return a new histogram with the added values of this and the given one.
	 * @throws IllegalArgumentException if the {@link #length()} and the 
	 *         separators of {@code this} and the given {@code histogram} are
	 *         not the same.
	 * @throws NullPointerException if the given {@code histogram} is {@code null}. 
	 */
	public Histogram<C> plus(final Histogram<C> histogram) {
		if (!_comparator.equals(histogram._comparator)) {
			throw new IllegalArgumentException(
					"The histogram comparators are not equals."
				);
		}
		if (!Arrays.equals(_separators, histogram._separators)) {
			throw new IllegalArgumentException(
					"The histogram separators are not equals."
				);
		}
		
		final long[] data = new long[_histogram.length];
		for (int i = 0; i < data.length; ++i) {
			data[i] = _histogram[i] + histogram._histogram[i];
		}
		
		return new Histogram<>(data, _comparator, _separators);
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
		nonNull(histogram);
		
		long[] hist = histogram;
		if (histogram != null && histogram.length >= _histogram.length) {
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
		
		assert (sum(_histogram) == _samples);
		for (int i = 0; i < probabilities.length; ++i) {
			probabilities[i] = (double)_histogram[i]/(double)_samples;
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
	 * @param cdf the assumed Probability density function-
	 * @return the χ2 value of the current histogram.
	 * @throws NullPointerException if {@code cdf} is {@code null}.
	 */
	public double χ2(final Function<C, Float64> cdf) {
		double χ2 = 0;
		for (int j = 0; j < _histogram.length; ++j) {
			final long n0j = n0(j, cdf);
			χ2 += ((_histogram[j] - n0j)*(_histogram[j] - n0j))/(double)n0j;
		}
		return χ2; 
	}

	private long n0(final int j, final Function<C, Float64> cdf) {
		Float64 p0j = Float64.ZERO;
		if (j == 0) {
			p0j = cdf.apply(_separators[0]);
		} else if (j == _histogram.length - 1) {
			p0j = Float64.ONE.minus(cdf.apply(_separators[_separators.length - 1]));
		} else {
			p0j = cdf.apply(_separators[j]).minus(cdf.apply(_separators[j - 1]));
		}
		
		return max(round(p0j.doubleValue()*_samples), 1L);
	}
	
//	long[] expection(final Function<C, Float64> cdf) {
//		final long[] e = new long[_histogram.length];
//		
//		for (int j = 0; j < _histogram.length; ++j) {
//			e[j] = n0(j, cdf);
//		}
//		return e;
//	}
    
	/**
	 * @see #χ2(Function)
	 */
	public double chisqr(final Function<C, Float64> cdf) {
		return χ2(cdf);
	}
	
	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).
				and(super.hashCode()).
				and(_separators).
				and(_histogram).value();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		final Histogram<?> histogram = (Histogram<?>)obj;
		return 	eq(_separators, histogram._separators) &&
				eq(_histogram, histogram._histogram) &&
				super.equals(obj);
	}
	
	@Override
	public String toString() {
		return Arrays.toString(getHistogram());
	}
	
	@Override
	public Histogram<C> clone() {
		return (Histogram<C>)super.clone();
	}

	
	/**
	 * Create a new Histogram with the given class separators. The classes are
	 * sorted by its natural order.
	 * 
	 * @param separators the class separators.
	 * @return a new Histogram.
	 * @throws NullPointerException if the {@code separators} are {@code null}.
	 * @throws IllegalArgumentException if {@code separators.length == 0}.
	 */
	@SuppressWarnings("unchecked")
	public static <C extends Comparable<? super C>> Histogram<C> valueOf(
		final C... separators
	) {
		return new Histogram<>(COMPARATOR, separators);
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static final Comparator COMPARATOR = new Comparator() {
		@Override
		public int compare(final Object o1, final Object o2) {
			return ((Comparable)o1).compareTo((Comparable)o2);
		}
	};
	
	/**
	 * Return a <i>histogram</i> for {@link Float64} values. The <i>histogram</i> 
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
	 * @return a new <i>histogram</i> for {@link Float64} values.
	 * @throws NullPointerException if {@code min} or {@code max} is {@code null}.
	 * @throws IllegalArgumentException if {@code min.compareTo(max) >= 0} or
	 *         {@code nclasses < 2}.
	 */
	public static Histogram<Float64> valueOf(
		final Float64 min, 
		final Float64 max, 
		final int nclasses
	) {
		return valueOf(arrays.map(
				toSeparators(min.doubleValue(), max.doubleValue(), nclasses), 
				new Float64[nclasses - 1],
				DoubleToFloat64
			));
	}
	
	/**
	 * @see #valueOf(Float64, Float64, int)
	 */
	public static Histogram<Double> valueOf(
		final Double min, 
		final Double max, 
		final int nclasses
	) {
		return valueOf(toSeparators(min, max, nclasses));
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
	 * Return a <i>histogram</i> for {@link Integer64} values. The <i>histogram</i> 
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
	 * @return a new <i>histogram</i> for {@link Integer64} values.
	 * @throws NullPointerException if {@code min} or {@code max} is {@code null}.
	 * @throws IllegalArgumentException if {@code min.compareTo(max) >= 0} or
	 *         {@code nclasses < 2}.
	 */
	public static Histogram<Integer64> valueOf(
		final Integer64 min, 
		final Integer64 max, 
		final int nclasses
	) {
		return valueOf(arrays.map(
				toSeparators(min.longValue(), max.longValue(), nclasses), 
				new Integer64[0],
				LongToInteger64
			));
	}
	
	/**
	 * @see #valueOf(Integer64, Integer64, int)
	 */
	public static Histogram<Long> valueOf(
		final Long min, 
		final Long max, 
		final int nclasses
	) {
		return valueOf(toSeparators(min, max, nclasses));
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
		for (int i = 0, n = rest; i < n; ++i) {
			separators[separators.length - rest + i] = 
					(pts - rest)*bulk + i*(bulk + 1) + min;
		}
		
		return separators;
	}
	
	/*
	 * Check the input values of the valueOf methods.
	 */
	private static <C extends Comparable<? super C>> void 
	check(final C min, final C max, final int nclasses) 
	{
	    nonNull(min, "Minimum");
	    nonNull(max, "Maximum");
	    if (min.compareTo(max) >= 0) {
	    	throw new IllegalArgumentException(String.format(
	    			"Min must be smaller than max: %s < %s failed.", min, max
	    		));
	    }
	    if (nclasses < 2) {
	        throw new IllegalArgumentException(String.format(
	                "nclasses should be < 2, but was %s.", nclasses
	            ));
	    }
	}

	
}










