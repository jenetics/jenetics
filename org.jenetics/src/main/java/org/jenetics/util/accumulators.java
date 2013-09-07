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
package org.jenetics.util;

import static java.util.Objects.requireNonNull;
import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;

import java.util.Iterator;

import org.jscience.mathematics.structure.GroupAdditive;


/**
 * Collection of some general purpose Accumulators and some static helper classes
 * for accumulating.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-09-08 $</em>
 */
public final class accumulators extends StaticObject {
	private accumulators() {}

	public static final Accumulator<Object> NULL = new Accumulator<Object>() {
		@Override
		public void accumulate(final Object value) {
		}
	};

	/**
	 * Calculates the sum of the accumulated values.
	 *
	 * <p/>
	 * <strong>Note that this implementation is not synchronized.</strong> If
	 * multiple threads access this object concurrently, and at least one of the
	 * threads modifies it, it must be synchronized externally.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.0
	 * @version 1.0 &ndash; <em>$Revision: 4b803cb54726 $</em>
	 */
	public static class Sum<G extends GroupAdditive<G>>
		extends AbstractAccumulator<G>
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

		public G getSum() {
			return _sum;
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
	 * Calls the {@link Accumulator#accumulate(Object)} method of all given
	 * {@code accumulators} with each value of the given {@code values}. The
	 * accumulation is done in parallel.
	 *
	 * @param <T> the value type.
	 * @param values the values to accumulate.
	 * @param accus the accumulators to apply.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	@SafeVarargs
	public static <T> void accumulate(
		final Iterable<? extends T> values,
		final Accumulator<? super T>... accus
	) {
		accumulate(values, Array.valueOf(accus));
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


