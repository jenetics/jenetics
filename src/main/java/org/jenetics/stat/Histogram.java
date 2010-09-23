package org.jenetics.stat;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.jenetics.util.Validator.nonNull;

import java.util.Arrays;
import java.util.Comparator;

import org.jenetics.util.AdaptableAccumulator;
import org.jenetics.util.ArrayUtils;
import org.jenetics.util.Validator;
import org.jenetics.util.Validator.NonNull;
import org.jscience.mathematics.function.Function;
import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.number.Integer64;

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
public class Histogram<C> extends AdaptableAccumulator<C> {
	private final C[] _classes;
	private final Comparator<C> _comparator;
	private final long[] _histogram;
	
	/**
	 * Create a new Histogram with the given class separators.
	 * 
	 * @param comparator the comparator for the classes.
	 * @param classes the class separators.
	 * @throws NullPointerException if the classes of one of its elements
	 *         or the {@code comparator} is {@code null}.
	 * @throws IllegalArgumentException if the given classes array is empty.
	 */
	public Histogram(final Comparator<C> comparator, final C... classes) {
		_classes = check(classes);
		_comparator = nonNull(comparator, "Comparator");
		_histogram = new long[classes.length + 1];
		
		Arrays.sort(_classes, _comparator);
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
			if (_comparator.compare(value, _classes[low]) < 0) {
				return low;
			} 
			if (_comparator.compare(value, _classes[high]) >= 0) {
				return high + 1;
			} 
			
			final int mid = (low + high) >>> 1;
			if (_comparator.compare(value, _classes[mid]) < 0) {
				high = mid;
			} else if (_comparator.compare(value, _classes[mid]) >= 0) {
				low = mid + 1;
			}
		}
		
		assert (false): "This line will be never reached.";
		return -1; 
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
	 * Create a new Histogram with the given class separators. The classes are
	 * sorted by its natural order.
	 * 
	 * @param classes the class separators.
	 * @return a new Histogram.
	 * @throws NullPointerException if the given classes are {@code null}.
	 * @throws IllegalArgumentException if the classes array is empty.
	 */
	public static <C extends Comparable<? super C>> Histogram<C> valueOf(
		final C... classes
	) {
		return new Histogram<C>(
				new Comparator<C>() {
					@Override public int compare(final C o1, final C o2) {
						return o1.compareTo(o2);
					}
				}, 
				classes
			);
	}
	
	public static Histogram<Float64> valueOf(
		final Float64 min, 
		final Float64 max, 
		final int nclasses
	) {
		assert (nclasses >= 2);
		
		final double stride = max.minus(min).divide(nclasses).doubleValue(); 
		final Float64[] classes = new Float64[nclasses - 1];
		
		classes[0] = Float64.valueOf(stride + min.doubleValue());
		for (int i = 1; i < classes.length; ++i) {
			classes[i] = Float64.valueOf(stride*i + min.doubleValue());
		}
		
		System.out.println(Arrays.toString(classes));
		return valueOf(classes);
	}
	
	public static Histogram<Double> valueOf(
		final Double min, 
		final Double max, 
		final int nclasses
	) {
		return null;
	}
	
	public static Histogram<Integer> valueOf(
		final Integer min, 
		final Integer max, 
		final int nclasses
	) {
		return null;
	}
	
	public static Histogram<Integer64> valueOf(
		final Integer64 min, 
		final Integer64 max, 
		final int nclasses
	) {
		final long interval = max.longValue() - min.longValue();
		final int ncls = (int)min(interval, nclasses);
		
		final long bulk = interval/(ncls - 1);
				
		final Integer64[] classes = new Integer64[ncls];
		for (int i = 0; i < ncls - 1; ++i) {
			classes[i] = Integer64.valueOf(i*bulk + min.longValue());
		}
		classes[classes.length - 1] = max;
		
		System.out.println(Arrays.toString(classes));
		
		return valueOf(classes);
	}
	
	
	public static void main(String[] args) {
		valueOf(Float64.valueOf(0), Float64.valueOf(60), 3);
		valueOf(Integer64.valueOf(34), Integer64.valueOf(77), 2);
	}
	
	
	public static int[] partition(final int size, final int parts) {
		final int pts = Math.min(size, parts);
		final int[] partition = new int[pts + 1];
		
		final int bulk = size/pts;
		final int rest = size%pts;
		assert ((bulk*pts + rest) == size);
		
		for (int i = 0, n = pts - rest; i < n; ++i) {
			partition[i] = i*bulk;
		}
		for (int i = 0, n = rest + 1; i < n; ++i) {
			partition[pts - rest + i] = (pts - rest)*bulk + i*(bulk + 1);
		}
		
		return partition;
	}
	
}










