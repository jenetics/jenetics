package org.jenetics.stat;

import static org.jenetics.util.ArrayUtils.map;
import static org.jenetics.util.ArrayUtils.sum;
import static org.jenetics.util.Validator.nonNull;

import java.util.Arrays;
import java.util.Comparator;

import org.jenetics.util.AdaptableAccumulator;
import org.jenetics.util.ArrayUtils;
import org.jenetics.util.Converters;
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
 *     cls[i] > value <= cls[i - 1]  when <i>i</i> &isin; [1 .. cls.length - 1], 
 *     0                             when value < cls[0] and
 *     cls.length                    when value >= cls[cls.length - 1]. 
 * </pre>
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
public class Histogram<C> extends AdaptableAccumulator<C> {
    
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
	public Histogram(final Comparator<C> comparator, final C... separators) {
		_separators = check(separators);
		_comparator = nonNull(comparator, "Comparator");
		_histogram = new long[separators.length + 1];
		
		Arrays.sort(_separators, _comparator);
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
            p0j = cdf.evaluate(_separators[0]);
        } else if (j == _histogram.length - 1) {
            p0j = Float64.ONE.minus(cdf.evaluate(_separators[_separators.length - 1]));
        } else {
            p0j = cdf.evaluate(_separators[j]).minus(cdf.evaluate(_separators[j - 1]));
        }
        
        return Math.max(Math.round(p0j.doubleValue()*_samples), 1L);
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

	
	/**
	 * Create a new Histogram with the given class separators. The classes are
	 * sorted by its natural order.
	 * 
	 * @param separators the class separators.
	 * @return a new Histogram.
	 * @throws NullPointerException if the {@code separators} are {@code null}.
	 * @throws IllegalArgumentException if {@code separators.length == 0}.
	 */
	public static <C extends Comparable<? super C>> Histogram<C> valueOf(
		final C... separators
	) {
		return new Histogram<C>(
				new Comparator<C>() {
					@Override public int compare(final C o1, final C o2) {
						return o1.compareTo(o2);
					}
				}, 
				separators
			);
	}
	
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
		return valueOf(map(
				toSeparators(min.doubleValue(), max.doubleValue(), nclasses), 
				new Float64[nclasses],
				Converters.DoubleToFloat64
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
		return valueOf(map(
				toSeparators(min.longValue(), max.longValue(), nclasses), 
				new Integer64[0],
				Converters.LongToInteger64
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










