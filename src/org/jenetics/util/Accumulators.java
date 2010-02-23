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
package org.jenetics.util;

import javolution.context.ConcurrentContext;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Accumulators {

	private Accumulators() {
	}
	
	/**
	 * <p>Calculate the Arithmetic mean:</p>
	 * <p><img src="doc-files/arithmetic-mean.gif" alt="Arithmentic Mean" /></p>
	 * 
	 * @see <a href="http://mathworld.wolfram.com/ArithmeticMean.html">Wolfram MathWorld: Artithmetic Mean</a>
	 * @see <a href="http://en.wikipedia.org/wiki/Arithmetic_mean">Wikipedia: Arithmetic Mean</a>
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static class Mean<N extends Number> implements Accumulator<N> {
		
		private long _samples = 0;
		private double _mean = Double.NaN;
		
		public Mean() {
		}
		
		/**
		 * Return the number of samples accumulated so far.
		 * 
		 * @return the number of samples accumulated so far.
		 */
		public long getSamples() {
			return _samples;
		}
		
		/**
		 * Return the mean value of the accumulated values.
		 * 
		 * @return the mean value of the accumulated values, or {@link java.lang.Double#NaN}
		 *         if {@code getSamples() == 0}.
		 */
		public double getMean() {
			return _mean;
		}
		
		public double getStandardError() {
			double sem = Double.NaN;

			if (_samples < 0) {
				sem = _mean/Math.sqrt(_samples);
			}
			
			return sem;
		}
		
		/**
		 * @throws NullPointerException if the given {@code value} is {@code null}.
		 */
		@Override
		public void accumulate(final N value) {
			if (_samples == 0) {
				_mean = 0;
			}
			++_samples;
			
			_mean += (value.doubleValue() - _mean)/(double)_samples;
		}
	}
	
	/**
	 * <p>Calculate the variance from a finite sample of <i>n</i> observations:</p>
	 * <p><img src="doc-files/variance.gif" alt="Variance" /></p>
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance" >
	 *         Wikipedia: Algorithms for calculating variance</a>
	 * @see <a href="http://mathworld.wolfram.com/Variance.html">
	 *         Wolfram MathWorld: Variance</a>
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static class Variance<N extends Number> implements Accumulator<N> {
		
		private long _samples = 0;
		private double _mean = Double.NaN;
		private double _m2 = Double.NaN;
		
		public Variance() {
		}
		
		/**
		 * Return the number of samples accumulated so far.
		 * 
		 * @return the number of samples accumulated so far.
		 */
		public long getSamples() {
			return _samples;
		}
		
		/**
		 * Return the mean value of the accumulated values.
		 * 
		 * @return the mean value of the accumulated values, or {@link java.lang.Double#NaN}
		 *         if {@code getSamples() == 0}.
		 */
		public double getMean() {
			return _mean;
		}
		
		/**
		 * Return the variance of the accumulated values.
		 * <p><img src="doc-files/variance.gif" alt="Variance" /></p>
		 * 
		 * @return the variance of the accumulated values, or {@link java.lang.Double#NaN}
		 *         if {@code getSamples() == 0}.
		 */
		public double getVariance() {
			double variance = Double.NaN;
			
			if (_samples == 1) {
				variance = _m2;
			} else if (_samples > 1) {
				variance = _m2/(double)(_samples - 1);
			}
			
			return variance;
		}
		
		public double getStandardError() {
			double sem = Double.NaN;

			if (_samples < 0) {
				sem = Math.sqrt(getVariance()/_samples);
			}
			
			return sem;
		}
		
		/**
		 * @throws NullPointerException if the given {@code value} is {@code null}.
		 */
		@Override
		public void accumulate(final N value) {
			if (_samples == 0) {
				_mean = 0;
				_m2 = 0;
			}
			++_samples;
			
			final double data = value.doubleValue();
			final double delta = data - _mean;
			_mean += delta/(double)_samples;
			_m2 += delta*(data - _mean);
		}
	}
	
	/**
	 * Calculates min and max values.
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static class MinMax<C extends Comparable<C>> implements Accumulator<C> {
		private C _min;
		private C _max;
		
		public MinMax() {
		}
		
		public C getMin() {
			return _min;
		}
		
		public C getMax() {
			return _max;
		}
		
		/**
		 * @throws NullPointerException if the given {@code value} is {@code null}.
		 */
		@Override
		public void accumulate(final C value) {
			if (value != null) {
				if (_min == null) {
					_min = value;
					_max = value;
				} else {
					if (value.compareTo(_min) < 0) {
						_min = value;
					} else if (value.compareTo(_max) > 0) {
						_max = value;
					}
				}
			}
		}
	}


	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of all given
	 * {@code accumulators} with each value of the given {@code values}. The 
	 * accumulation is done in parallel.
	 * 
	 * @param <T> the value type.
	 * @param values the values to accumulate.
	 * @param accumulators the accumulators to apply.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Iterable<? extends T> values, 
		final Array<Accumulator<? super T>> accumulators
	) {
		switch (accumulators.length()) {
		case 1:
			accumulate(
					values, 
					accumulators.get(0)
				); break;
		case 2:
			accumulate(
					values, 
					accumulators.get(0), 
					accumulators.get(1)
				); break;
		case 3:
			accumulate(
					values, 
					accumulators.get(0), 
					accumulators.get(1),
					accumulators.get(2)
				); break;
		case 4:
			accumulate(
					values, 
					accumulators.get(0), 
					accumulators.get(1),
					accumulators.get(2),
					accumulators.get(3)
				); break;
		case 5:
			accumulate(
					values, 
					accumulators.get(0), 
					accumulators.get(1),
					accumulators.get(2),
					accumulators.get(3),
					accumulators.get(4)
				); break;
		default:
			ConcurrentContext.enter();
			try {
				for (final Accumulator<? super T> accumulator : accumulators) {
					ConcurrentContext.execute(new Acc<T>(values, accumulator));
				}
			} finally {
				ConcurrentContext.exit();
			}
		}
	}
	
	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of the given
	 * {@code accumulator} with each value of the given {@code values}. 
	 * 
	 * @param <T> the value type.
	 * @param values the values to accumulate.
	 * @param accumulator the accumulator.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Iterable<? extends T> values,
		final Accumulator<? super T> accumulator
	) {
		for (final T value : values) {
			accumulator.accumulate(value);
		}
	}
	
	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of all given
	 * {@code accumulators} with each value of the given {@code values}. The 
	 * accumulation is done in parallel.
	 * 
	 * @param <T> the value type.
	 * @param values the values to accumulate.
	 * @param accumulator1 the first accumulator.
	 * @param accumulator2 the second accumulator.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Iterable<? extends T> values,
		final Accumulator<? super T> accumulator1,
		final Accumulator<? super T> accumulator2
	) {
		ConcurrentContext.enter();
		try {
			ConcurrentContext.execute(new Acc<T>(values, accumulator1));
			ConcurrentContext.execute(new Acc<T>(values, accumulator2));;			
		} finally {
			ConcurrentContext.exit();
		}
	}
	
	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of all given
	 * {@code accumulators} with each value of the given {@code values}. The 
	 * accumulation is done in parallel.
	 * 
	 * @param <T> the value type.
	 * @param values the values to accumulate.
	 * @param accumulator1 the first accumulator.
	 * @param accumulator2 the second accumulator.
	 * @param accumulator3 the third accumulator
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Iterable<? extends T> values,
		final Accumulator<? super T> accumulator1,
		final Accumulator<? super T> accumulator2,
		final Accumulator<? super T> accumulator3
	) {
		ConcurrentContext.enter();
		try {
			ConcurrentContext.execute(new Acc<T>(values, accumulator1));
			ConcurrentContext.execute(new Acc<T>(values, accumulator2));
			ConcurrentContext.execute(new Acc<T>(values, accumulator3));			
		} finally {
			ConcurrentContext.exit();
		}
	}
	
	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of all given
	 * {@code accumulators} with each value of the given {@code values}. The 
	 * accumulation is done in parallel.
	 * 
	 * @param <T> the value type.
	 * @param values the values to accumulate.
	 * @param accumulator1 the first accumulator.
	 * @param accumulator2 the second accumulator.
	 * @param accumulator3 the third accumulator.
	 * @param accumulator4 the fourth accumulator.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Iterable<? extends T> values,
		final Accumulator<? super T> accumulator1,
		final Accumulator<? super T> accumulator2,
		final Accumulator<? super T> accumulator3,
		final Accumulator<? super T> accumulator4
	) {
		ConcurrentContext.enter();
		try {
			ConcurrentContext.execute(new Acc<T>(values, accumulator1));
			ConcurrentContext.execute(new Acc<T>(values, accumulator2));
			ConcurrentContext.execute(new Acc<T>(values, accumulator3));
			ConcurrentContext.execute(new Acc<T>(values, accumulator4));	
		} finally {
			ConcurrentContext.exit();
		}
	}
	
	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of all given
	 * {@code accumulators} with each value of the given {@code values}. The 
	 * accumulation is done in parallel.
	 * 
	 * @param <T> the value type.
	 * @param values the values to accumulate.
	 * @param accumulator1 the first accumulator.
	 * @param accumulator2 the second accumulator.
	 * @param accumulator3 the third accumulator.
	 * @param accumulator4 the fourth accumulator.
	 * @param accumulator5 the fifth accumulator.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Iterable<? extends T> values,
		final Accumulator<? super T> accumulator1,
		final Accumulator<? super T> accumulator2,
		final Accumulator<? super T> accumulator3,
		final Accumulator<? super T> accumulator4,
		final Accumulator<? super T> accumulator5
	) {
		ConcurrentContext.enter();
		try {	
			ConcurrentContext.execute(new Acc<T>(values, accumulator1));
			ConcurrentContext.execute(new Acc<T>(values, accumulator2));
			ConcurrentContext.execute(new Acc<T>(values, accumulator3));
			ConcurrentContext.execute(new Acc<T>(values, accumulator4));
			ConcurrentContext.execute(new Acc<T>(values, accumulator5));
		} finally {
			ConcurrentContext.exit();
		}
	}	
	
	private static class Acc<T> implements Runnable {
		private final Iterable<? extends T> _values;
		private final Accumulator<? super T> _accumulator;
		
		public Acc(
			final Iterable<? extends T> values,
			final Accumulator<? super T> accumulator
		) {
			_values = values;
			_accumulator = accumulator;
		}
		
		@Override
		public void run() {
			for (final T value : _values) {
				_accumulator.accumulate(value);
			}
		}
	}
	
}






