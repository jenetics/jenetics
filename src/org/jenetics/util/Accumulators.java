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

import java.util.Arrays;

import org.jscience.mathematics.structure.GroupAdditive;

import javolution.context.ConcurrentContext;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class Accumulators {

	private Accumulators() {
	}
	
	/**
	 * <p>Calculate  the Arithmetic mean:</p>
	 * <p><img src="doc-files/arithmetic-mean.gif" alt="Arithmentic Mean" /></p>
	 * 
	 * @see <a href="http://mathworld.wolfram.com/ArithmeticMean.html">Wolfram MathWorld: Artithmetic Mean</a>
	 * @see <a href="http://en.wikipedia.org/wiki/Arithmetic_mean">Wikipedia: Arithmetic Mean</a>
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static class Mean<N extends Number> implements Accumulator<N> {
		
		protected long _samples = 0;
		protected double _mean = Double.NaN;
		
		public Mean() {
		}
		
		/**
		 * Return the number of samples accumulated so far.
		 * 
		 * @return the number of samples accumulated so far.
		 */
		public long getSamples() {
			return _samples ;
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
	public static class Variance<N extends Number> extends Mean<N> {
		
		private double _m2 = Double.NaN;
		
		public Variance() {
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
	 * Implementation of the quantile estimation algorithm published by
	 * <p/>
	 * <strong>Raj JAIN and Imrich CHLAMTAC</strong>:
	 * <em>
	 *     The P<sup>2</sup> Algorithm for Dynamic Calculation of Quantiles and 
	 *     Histograms Without Storing Observations
	 * </em>
	 * <br/>
	 * [<a href="http://www.cse.wustl.edu/~jain/papers/ftp/psqr.pdf">Communications 
	 * of the ACM; October 1985, Volume 28, Number 10</a>]
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/Quantile">Wikipedia: Quantile</a>
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static class Quantile<N extends Number> implements Accumulator<N> {
		
		// The desired quantile.
		private double _quantile;
		
		// Marker heights.
		private final double[] _q = {0, 0, 0, 0, 0};
		
		// Marker positions.
		private final double[] _n = {0, 0, 0, 0, 0};
		
		// Desired marker positions.
		private final double[] _nn = {0, 0, 0};
		
		// Desired marker position increments.
		private final double[] _dn = {0, 0, 0};
		
		private boolean _initialized;
		
		/**
		 * Create a new quantile accumulator with the given value.
		 * 
		 * @param quantile the wished quantile value.
		 * @throws IllegalArgumentException if the {@code quantile} is not in the
		 *         range {@code [0, 1]}.
		 */
		public Quantile(double quantile) {
			if (quantile < 0.0 || quantile > 1) {
				throw new IllegalArgumentException(String.format(
						"Quantile (%s) not in the valid range of [0, 1]", quantile
					));
			}
			_quantile = quantile;
			_n[0] = -1.0;
			_q[2] = 0.0;
			_initialized = _quantile == 0.0 || _quantile == 1.0;
		}

		public double getQuantile() {
			return _q[2];
		}

		@Override
		public void accumulate(final N value) {
			if (!_initialized) {
				initialize(value.doubleValue());
			} else {
				update(value.doubleValue());
			}
		}


		private void initialize(double value) {
			if (_n[0] < 0.0) {
				_n[0] = 0.0;
				_q[0] = value;
			} else if (_n[1] == 0.0) {
				_n[1] = 1.0;
				_q[1] = value;
			} else if (_n[2] == 0.0) {
				_n[2] = 2.0;
				_q[2] = value;
			} else if (_n[3] == 0.0) {
				_n[3] = 3.0;
				_q[3] = value;
			} else if (_n[4] == 0.0) {
				_n[4] = 4.0;
				_q[4] = value;
			}
			
			if (_n[4] != 0.0) {
				Arrays.sort(_q);

				_nn[0] = 2.0*_quantile;
				_nn[1] = 4.0*_quantile;
				_nn[2] = 2.0*_quantile + 2.0;
		
				_dn[0] = _quantile/2.0;
				_dn[1] = _quantile;
				_dn[2] = (1.0 + _quantile)/2.0;
		
				_initialized = true;
			}
		}

		private void update(double value) {
			assert (_initialized);

			// If min or max, handle as special case; otherwise, ...
			if (_quantile == 0.0) {
				if (value < _q[2]) {
					_q[2] = value;
				}
			} else if (_quantile == 1.0) {
				if (value > _q[2]) {
					_q[2] = value;
				}
			} else {
				// Increment marker locations and update min and max.
				if (value < _q[0]) {
					++_n[1]; ++_n[2]; ++_n[3]; ++_n[4]; _q[0] = value;
				} else if (value < _q[1]) {
					++_n[1]; ++_n[2]; ++_n[3]; ++_n[4];
				} else if (value < _q[2]) {
					++_n[2]; ++_n[3]; ++_n[4];
				} else if (value < _q[3]) {
					++_n[3]; ++_n[4];
				} else if (value < _q[4]) {
					++_n[4];
				} else {
					++_n[4]; _q[4] = value;
				}

				// Increment positions of markers k + 1
				_nn[0] += _dn[0];
				_nn[1] += _dn[1];
				_nn[2] += _dn[2];

				// Adjust heights of markers 0 to 2 if necessary
				double mm = _n[1] - 1.0;
				double mp = _n[1] + 1.0;
				if (_nn[0] >= mp && _n[2] > mp) {
					_q[1] = qPlus(mp, _n[0], _n[1], _n[2], _q[0], _q[1], _q[2]);
					_n[1] = mp;
				} else if (_nn[0] <= mm && _n[0] < mm) {
					_q[1] = qMinus(mm, _n[0], _n[1], _n[2], _q[0], _q[1], _q[2]);
					_n[1] = mm;
				}
				
				mm = _n[2] - 1.0;
				mp = _n[2] + 1.0;
				if (_nn[1] >= mp && _n[3] > mp) {
					_q[2] = qPlus(mp, _n[1], _n[2], _n[3], _q[1], _q[2], _q[3]);
					_n[2] = mp;
				} else if (_nn[1] <= mm && _n[1] < mm) {
					_q[2] = qMinus(mm, _n[1], _n[2], _n[3], _q[1], _q[2], _q[3]);
					_n[2] = mm;
				}
				
				mm = _n[3] - 1.0;
				mp = _n[3] + 1.0;
				if (_nn[2] >= mp && _n[4] > mp) {
					_q[3] = qPlus(mp, _n[2], _n[3], _n[4], _q[2], _q[3], _q[4]);
					_n[3] = mp;
				} else if (_nn[2] <= mm && _n[2] < mm) {
					_q[3] = qMinus(mm, _n[2], _n[3], _n[4], _q[2], _q[3], _q[4]);
					_n[3] = mm;
				}
			}
		}

		private static double qPlus(
			final double mp, 
			final double m0, 
			final double m1, 
			final double m2,
			final double q0, 
			final double q1, 
			final double q2
		) {
			double result = q1 + 
						((mp - m0)*(q2 - q1)/(m2 - m1) + 
						(m2 - mp)*(q1 - q0)/(m1 - m0))/(m2 - m0);
			
			if (result > q2) {
				result = q1 + (q2 - q1)/(m2 - m1);
			}
			
			return result;
		}

		private static double qMinus(
			final double mm, 
			final double m0, 
			final double m1, 
			final double m2,
			final double q0, 
			final double q1, 
			final double q2
		) {
			double result = q1 - 
						((mm - m0)*(q2 - q1)/(m2 - m1) + 
						(m2 - mm)*(q1 - q0)/(m1 - m0))/(m2 - m0);
			
			if (q0 > result) {
				result = q1 + (q0 - q1)/(m0 - m1);
			}
			
			return result;
		}
	}
	
	/**
	 * Calculates min value.
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static class Min<C extends Comparable<C>> implements Accumulator<C> {
		private C _min;
		
		public Min() {
		}
		
		public C getMin() {
			return _min;
		}
		
		/**
		 * @throws NullPointerException if the given {@code value} is {@code null}.
		 */
		@Override
		public void accumulate(final C value) {
			if (_min == null) {
				_min = value;
			} else {
				if (value.compareTo(_min) < 0) {
					_min = value;
				}
			}
		}
	}
	
	
	/**
	 * Calculates max value.
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static class Max<C extends Comparable<C>> implements Accumulator<C> {
		private C _max;
		
		public Max() {
		}
		
		public C getMax() {
			return _max;
		}
		
		/**
		 * @throws NullPointerException if the given {@code value} is {@code null}.
		 */
		@Override
		public void accumulate(final C value) {
			if (_max == null) {
				_max = value;
			} else {
				if (value.compareTo(_max) > 0) {
					_max = value;
				}
			}
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
	
	
	/**
	 * Calculates the sum of the accumulated values.
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static class Sum<N extends GroupAdditive<N>> implements Accumulator<N> {
		public N _sum;
		
		public Sum() {
		}
		
		public N getSum() {
			return _sum;
		}
		
		@Override
		public void accumulate(final N value) {
			if (_sum == null) {
				_sum = value;
			} else {
				_sum = _sum.plus(value);
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
			Accumulators.<T>accumulate(
					values, 
					accumulators.get(0)
				); 
			break;
		case 2:
			Accumulators.<T>accumulate(
					values, 
					accumulators.get(0), 
					accumulators.get(1)
				); 
			break;
		case 3:
			Accumulators.<T>accumulate(
					values, 
					accumulators.get(0), 
					accumulators.get(1),
					accumulators.get(2)
				); 
			break;
		case 4:
			Accumulators.<T>accumulate(
					values, 
					accumulators.get(0), 
					accumulators.get(1),
					accumulators.get(2),
					accumulators.get(3)
				);
			break;
		case 5:
			Accumulators.<T>accumulate(
					values, 
					accumulators.get(0), 
					accumulators.get(1),
					accumulators.get(2),
					accumulators.get(3),
					accumulators.get(4)
				); 
			break;
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






