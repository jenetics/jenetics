package org.jenetics.stat;

import java.util.Arrays;

import org.jenetics.util.AdaptableAccumulator;
import org.jenetics.util.ArrayUtils;
import org.jenetics.util.Validator;
import org.jenetics.util.Validator.NonNull;
import org.jscience.mathematics.function.Function;
import org.jscience.mathematics.number.Float64;

/**
 * To create an <i>Histogram Accumulator</i> you have to define the <i>class
 * border</i> which define the histogram classes. A value is part of the 
 * <i>i</i><sup>th</sup> histogram array element if 
 * <pre>
 *     classes[i] > value <= classes[i - 1]  when <i>i</i> &isin; [1 .. classes.length - 1], 
 *     0                                     when value < classes[0] and
 *     classes.length                        when value >= classes[classes.length - 1]. 
 * </pre>
 * 
 * Example:
 * <pre>
 * Class border values:    0    1    2    3    4    5    6    7    8    9
 *                  -------+----+----+----+----+----+----+----+----+----+------
 * Frequencies:        20  | 12 | 14 | 17 | 12 | 11 | 13 | 11 | 10 | 19 |  18 
 *                  -------+----+----+----+----+----+----+----+----+----+------
 * Histogram index:     0     1    2    3    4    5    6    7    8    9    10
 * </pre>
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Histogram<C extends Comparable<? super C>> 
	extends AdaptableAccumulator<C> 
{
	private final C[] _classes;
	private final long[] _histogram;
	
	/**
	 * Create a new Histogram with the given class separators.
	 * 
	 * @param classes the class separators.
	 * @throws NullPointerException if the classes of one of its elements
	 *         is {@code null}.
	 * @throws IllegalArgumentException if the given classes array is empty.
	 */
	public Histogram(final C... classes) {
		_classes = check(classes);
		_histogram = new long[classes.length + 1];
		
		Arrays.sort(_classes);
		Arrays.fill(_histogram, 0L);
	}
	
	private C[] check(final C... classes) {
		ArrayUtils.foreach(classes, new NonNull());
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
	int index(final C value) { 
		int low = 0;
		int high = _classes.length - 1;
		
		while (low <= high) {
			if (value.compareTo(_classes[low]) < 0) {
				return low;
			} 
			if (value.compareTo(_classes[high]) >= 0) {
				return high + 1;
			} 
			
			final int mid = (low + high) >>> 1;
			if (value.compareTo(_classes[mid]) < 0) {
				high = mid;
			} else if (value.compareTo(_classes[mid]) >= 0) {
				low = mid + 1;
			}
		}
		
		assert (false): "This line will be never reached.";
		return -1; 
	}
	
	/**
	 * Return a copy of the class borders.
	 * 
	 * @return the class borders.
	 */
	public C[] getClasses() {
		return _classes.clone();
	}
	
	/**
	 * Copy the histogram into the given array. If the array is big enough
	 * the same array is returned, otherwise a new array is created and 
	 * returned.
	 * 
	 * @param histogram array to copy the histogram.
	 * @return the histogram.
	 * @throws NullPointerException if the given array is {@code null}.
	 */
	public long[] getHistogram(final long[] histogram) {
		Validator.nonNull(histogram);
		
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
	 * Return the class probabilities.
	 * 
	 * @return the class probabilities.
	 */
	public double[] getProbabilities() {
		final double[] probabilities = new double[_histogram.length];
		
		assert (ArrayUtils.sum(_histogram) == _samples);
		for (int i = 0; i < probabilities.length; ++i) {
			probabilities[i] = (double)_histogram[i]/(double)_samples;
		}
		
		return probabilities;
	}
	
	/**
	 * @see #χ2(Function)
	 */
	public double chiSquare(final Function<C, Float64> cdf) {
		return χ2(cdf);
	}
	
	/**
	 * Calculate the Chi-Square value of the current histogram for the
	 * assumed <a href="http://en.wikipedia.org/wiki/Cumulative_distribution_function">
	 * Culmulative density function</a> {@code pdf}.
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/Chi-square_test">χ2-test</a>
	 * @see <a href="http://en.wikipedia.org/wiki/Chi-square_distribution">χ2-distribution</a>
	 * 
	 * @param cdf the assumed Probability density function-
	 * @return the Chi-Square value of the current histogram.
	 * @throws NullPointerException if {@code cdf} is {@code null}.
	 */
	public double χ2(final Function<C, Float64> cdf) {
		double χ2 = 0;
		for (int j = 0; j < _histogram.length; ++j) {
			final long n0j = n0(j, cdf);
			χ2 += (double)((_histogram[j] - n0j)*(_histogram[j] - n0j))/(double)n0j;
		}
		return χ2; 
	}
	
	public long[] expection(final Function<C, Float64> cdf) {
		final long[] e = new long[_histogram.length];
		
		for (int j = 0; j < _histogram.length; ++j) {
			e[j] = n0(j, cdf);
		}
		return e;
	}
	
	private long n0(final int j, final Function<C, Float64> cdf) {
		Float64 p0j = Float64.ZERO;
		if (j == 0) {
			p0j = cdf.evaluate(_classes[0]);
		} else if (j == _histogram.length - 1) {
			p0j = Float64.ONE.minus(cdf.evaluate(_classes[_classes.length - 1]));
		} else {
			p0j = cdf.evaluate(_classes[j]).minus(cdf.evaluate(_classes[j - 1]));
		}
		
		return Math.max(Math.round(p0j.doubleValue()*_samples), 1L);
	}
	
	/**
	 * Create a new Histogram with the given class separators.
	 * 
	 * @param classes the class separators.
	 * @return a new Histogram.
	 * @throws NullPointerException if the given classes are {@code null}.
	 * @throws IllegalArgumentException if the classes array is empty.
	 */
	public static <C extends Comparable<? super C>> 
	Histogram<C> valueOf(final C... classes) {
		return new Histogram<C>(classes);
	}
	
	public static 
	Histogram<Float64> valueOf(final double min, final double max, final int size) {
		final double stride = (max - min)/size;
		final Float64[] classes = ArrayUtils.toFloat64Array(min + stride, stride, size - 1);
		return new Histogram<Float64>(classes);
	}
	
}