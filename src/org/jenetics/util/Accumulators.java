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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Accumulators {

	private Accumulators() {
	}
	
	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static class FirstMoment<N extends Number> implements Accumulator<N> {
		
		/**
		 * Number of values accumulated so far.
		 */
		protected long _samples = 0;
		
		/**
		 * Mean value of the values that have been added.
		 */
		protected double _moment1 = Double.NaN;
		
		/**
		 * Deviation of last added value from previous mean. This is used by
		 * higher order moments.
		 */
		protected double _deviation = Double.NaN;
		
		protected double _ndeviation = Double.NaN;
		
		public FirstMoment() {
		}
		
		public double getFirstMoment() {
			return _moment1;
		}
		
		public long getSamples() {
			return _samples;
		}
		
		public void accumulate(final N value) {
			if (_samples == 0) {
				_moment1 = 0;
			}
			++_samples;
			
			_deviation = value.doubleValue() - _moment1;
			_ndeviation = _deviation/(double)_samples;
			_moment1 += _ndeviation;
		}
	}

	public static <N extends Number> FirstMoment<N> FirstMoment() {
		return new FirstMoment<N>();
	}
	
	public static class SecondMoment<N extends Number> extends FirstMoment<N> {
		protected double _moment2 = Double.NaN;
		
		public SecondMoment() {
		}
		
		public double getSecondMoment() {
			return _moment2;
		}
		
		@Override
		public void accumulate(final N value) {
			if (_samples < 1) {
				_moment1 = 0;
				_moment2 = 0;
			}
			super.accumulate(value);
			_moment2 += ((double)_samples - 1.0)*_deviation*_ndeviation;
		}
	}
	
	public static <N extends Number> SecondMoment<N> SecondMoment() {
		return new SecondMoment<N>();
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
	
	public static <C extends Comparable<C>> MinMax<C> MinMax() {
		return new MinMax<C>();
	}

	
	public static <T> void accumulate(
		final Iterable<? extends T> values, 
		final Array<Accumulator<T>> accumulators
	) {
		final AP<T> ap = new AP<T>();
		
		for (final T value : values) {
			ap._value = value;
			accumulators.foreach(ap);
		}
	}
	
	private static class AP<T> implements Predicate<Accumulator<T>> {
		T _value;
		
		@Override
		public boolean evaluate(final Accumulator<T> accumulator) {
			accumulator.accumulate(_value);
			return true;
		}
		
	}
	
}






