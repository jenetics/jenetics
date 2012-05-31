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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;
import static org.jenetics.util.object.nonNull;

import java.util.Iterator;

import org.jscience.mathematics.structure.GroupAdditive;


/**
 * Collection of some general purpose Accumulators and some static helper classes
 * for accumulating.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &ndash; <em>$Revision$</em>
 */
public final class accumulators {

	private accumulators() {
		throw new AssertionError("Don't create an 'accumulators' instance.");
	}

	public static final Accumulator<Object> NULL = new Accumulator<Object>() {
		@Override
		public void accumulate(final Object value) {
		}
	};

	/**
	 * Calculates min value.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.0
	 * @version $Id$
	 */
	public static final class Min<C extends Comparable<? super C>>
		extends MappableAccumulator<C>
	{
		private C _min;

		/**
		 * Create a new Min accumulator.
		 */
		public Min() {
		}

		/**
		 * Copy constructor.
		 *
		 * @param min the accumulator to copy.
		 * @throws NullPointerException if {@code min} is {@code null}.
		 */
		public Min(final Min<C> min) {
			nonNull(min, "Min");
			_samples = min._samples;
			_min = min._min;
		}

		/**
		 * Return the min value, accumulated so far.
		 *
		 * @return the min value, accumulated so far.
		 */
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

			++_samples;
		}

		@Override
		public int hashCode() {
			return hashCodeOf(getClass()).and(super.hashCode()).and(_min).value();
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj == null || obj.getClass() != getClass()) {
				return false;
			}

			final Min<?> min = (Min<?>)obj;
			return super.equals(obj) && eq(_min, min._min);
		}

		@Override
		public String toString() {
			return String.format(
					"%s[samples=%d, min=%s]",
					getClass().getSimpleName(), getSamples(), getMin()
				);
		}

		@Override
		public Min<C> clone() {
			return (Min<C>)super.clone();
		}
	}


	/**
	 * Calculates max value.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.0
	 * @version $Id$
	 */
	public static final class Max<C extends Comparable<? super C>>
		extends MappableAccumulator<C>
	{
		private C _max;

		/**
		 * Create a new Max accumulator.
		 */
		public Max() {
		}

		/**
		 * Copy constructor.
		 *
		 * @param max the accumulator to copy.
		 * @throws NullPointerException if {@code max} is {@code null}.
		 */
		public Max(final Max<C> max) {
			nonNull(max, "Max");
			_samples = max._samples;
			_max = max._max;
		}

		/**
		 * Return the max value, accumulated so far.
		 *
		 * @return the max value, accumulated so far.
		 */
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

			++_samples;
		}

		@Override
		public int hashCode() {
			return hashCodeOf(getClass()).and(super.hashCode()).and(_max).value();
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj == null || obj.getClass() != getClass()) {
				return false;
			}

			final Max<?> max = (Max<?>)obj;
			return super.equals(obj) && eq(_max, max._max);
		}

		@Override
		public String toString() {
			return String.format(
					"%s[samples=%d, max=%s]",
					getClass().getSimpleName(), getSamples(), getMax()
				);
		}

		@Override
		public Max<C> clone() {
			return (Max<C>)super.clone();
		}
	}


	/**
	 * Calculates min and max values.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.0
	 * @version $Id$
	 */
	public static final class MinMax<C extends Comparable<? super C>>
		extends MappableAccumulator<C>
	{
		private C _min;
		private C _max;

		/**
		 * Create a new min-max accumulator.
		 */
		public MinMax() {
		}

		/**
		 * Copy constructor.
		 *
		 * @param mm the accumulator to copy.
		 * @throws NullPointerException if {@code mm} is {@code null}.
		 */
		public MinMax(final MinMax<C> mm) {
			nonNull(mm, "MinMax");
			_samples = mm._samples;
			_min = mm._min;
			_max = mm._max;
		}

		/**
		 * Return the min value, accumulated so far.
		 *
		 * @return the min value, accumulated so far.
		 */
		public C getMin() {
			return _min;
		}

		/**
		 * Return the max value, accumulated so far.
		 *
		 * @return the max value, accumulated so far.
		 */
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

			++_samples;
		}

		@Override
		public int hashCode() {
			return hashCodeOf(getClass()).
					and(super.hashCode()).
					and(_min).
					and(_max).value();
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj == null || obj.getClass() != getClass()) {
				return false;
			}

			final MinMax<?> mm = (MinMax<?>)obj;
			return super.equals(obj) && eq(_min, mm._min) && eq(_max, mm._max);
		}

		@Override
		public String toString() {
			return String.format(
					"%s[samples=%d, min=%s, max=%s]",
					getClass().getSimpleName(), getSamples(), getMin(), getMax()
				);
		}

		@Override
		public MinMax<C> clone() {
			return (MinMax<C>)super.clone();
		}
	}

	public static class Sum<G extends GroupAdditive<G>>
		extends MappableAccumulator<G>
	{

		private G _sum = null;

		public Sum() {
		}

		public Sum(final G start) {
			_sum = start;
		}

		@Override
		public void accumulate(final G value) {
			if (_sum == null) {
				_sum = value;
			} else {
				_sum = _sum.plus(value);
			}

			++_samples;
		}

	}

	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of all given
	 * {@code accumulators} with each value of the given {@code values}. The
	 * accumulation is done in parallel.
	 *
	 * @param <T> the value type.
	 * @param values the values to accumulate.
	 * @param accus the accumulators to apply.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Iterable<? extends T> values,
		final Seq<? extends Accumulator<? super T>> accus
	) {
		switch (accus.length()) {
		case 1:
			accumulators.<T>accumulate(
				values,
				accus.get(0)
			);
			break;
		case 2:
			accumulators.<T>accumulate(
				values,
				accus.get(0),
				accus.get(1)
			);
			break;
		case 3:
			accumulators.<T>accumulate(
				values,
				accus.get(0),
				accus.get(1),
				accus.get(2)
			);
			break;
		case 4:
			accumulators.<T>accumulate(
				values,
				accus.get(0),
				accus.get(1),
				accus.get(2),
				accus.get(3)
			);
			break;
		case 5:
			accumulators.<T>accumulate(
				values,
				accus.get(0),
				accus.get(1),
				accus.get(2),
				accus.get(3),
				accus.get(4)
			);
			break;
		default:
			try (Concurrency c = Concurrency.start()) {
				for (final Accumulator<? super T> accumulator : accus) {
					c.execute(new Acc<>(values, accumulator));
				}
			}
		}
	}

	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of the given
	 * {@code accumulator} with each value of the given {@code values}.
	 *
	 * @param <T> the value type.
	 * @param values the values to accumulate.
	 * @param a the accumulator.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Iterator<? extends T> values,
		final Accumulator<? super T> a
	) {
		while (values.hasNext()) {
			a.accumulate(values.next());
		}
	}

	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of the given
	 * {@code accumulator} with each value of the given {@code values}.
	 *
	 * @param <T> the value type.
	 * @param values the values to accumulate.
	 * @param a the accumulator.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Iterable<? extends T> values,
		final Accumulator<? super T> a
	) {
		for (final T value : values) {
			a.accumulate(value);
		}
	}

	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of all given
	 * {@code accumulators} with each value of the given {@code values}. The
	 * accumulation is done in parallel.
	 *
	 * @param <T> the value type.
	 * @param values the values to accumulate.
	 * @param a1 the first accumulator.
	 * @param a2 the second accumulator.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Iterable<? extends T> values,
		final Accumulator<? super T> a1,
		final Accumulator<? super T> a2
	) {
		try (Concurrency c = Concurrency.start()) {
			c.execute(new Acc<>(values, a1));
			c.execute(new Acc<>(values, a2));;
		}
	}

	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of all given
	 * {@code accumulators} with each value of the given {@code values}. The
	 * accumulation is done in parallel.
	 *
	 * @param <T> the value type.
	 * @param values the values to accumulate.
	 * @param a1 the first accumulator.
	 * @param a2 the second accumulator.
	 * @param a3 the third accumulator
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Iterable<? extends T> values,
		final Accumulator<? super T> a1,
		final Accumulator<? super T> a2,
		final Accumulator<? super T> a3
	) {
		try (Concurrency c = Concurrency.start()) {
			c.execute(new Acc<>(values, a1));
			c.execute(new Acc<>(values, a2));
			c.execute(new Acc<>(values, a3));
		}
	}

	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of all given
	 * {@code accumulators} with each value of the given {@code values}. The
	 * accumulation is done in parallel.
	 *
	 * @param <T> the value type.
	 * @param values the values to accumulate.
	 * @param a1 the first accumulator.
	 * @param a2 the second accumulator.
	 * @param a3 the third accumulator.
	 * @param a4 the fourth accumulator.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Iterable<? extends T> values,
		final Accumulator<? super T> a1,
		final Accumulator<? super T> a2,
		final Accumulator<? super T> a3,
		final Accumulator<? super T> a4
	) {
		try (Concurrency c = Concurrency.start()) {
			c.execute(new Acc<>(values, a1));
			c.execute(new Acc<>(values, a2));
			c.execute(new Acc<>(values, a3));
			c.execute(new Acc<>(values, a4));
		}
	}

	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of all given
	 * {@code accumulators} with each value of the given {@code values}. The
	 * accumulation is done in parallel.
	 *
	 * @param <T> the value type.
	 * @param values the values to accumulate.
	 * @param a1 the first accumulator.
	 * @param a2 the second accumulator.
	 * @param a3 the third accumulator.
	 * @param a4 the fourth accumulator.
	 * @param a5 the fifth accumulator.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Iterable<? extends T> values,
		final Accumulator<? super T> a1,
		final Accumulator<? super T> a2,
		final Accumulator<? super T> a3,
		final Accumulator<? super T> a4,
		final Accumulator<? super T> a5
	) {
		try (Concurrency c = Concurrency.start()) {
			c.execute(new Acc<>(values, a1));
			c.execute(new Acc<>(values, a2));
			c.execute(new Acc<>(values, a3));
			c.execute(new Acc<>(values, a4));
			c.execute(new Acc<>(values, a5));
		}
	}

	private static final class Acc<T> implements Runnable {
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


