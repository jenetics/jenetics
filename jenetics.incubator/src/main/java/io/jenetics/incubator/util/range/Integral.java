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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.util.range;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

/**
 * <em>Type class</em>, which allows an object of type {@code T} to be treated
 * as an <em>integral</em> value and to be used in {@link Range} classes.
 *
 * @param <T> the integral type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 9.2
 * @since 9.2
 */
public interface Integral<T> extends Comparator<T> {

	/**
	 * Integral <em>type class</em> for {@link Integer} values.
	 */
	Integral<Integer> INTEGER = new Integral<>() {
		@Override
		public Integer zero() {
			return 0;
		}

		@Override
		public Integer next(Integer value, long n) {
			if (n < 0) {
				throw new IllegalArgumentException("n must be greater than zero:" + n);
			}
			if (n > Integer.MAX_VALUE) {
				throw new ArithmeticException("Overflow: " + n);
			}
			return Math.addExact(value, (int)n);
		}

		@Override
		public long distance(Integer a, Integer b) {
			return Math.abs((long)a - (long)b);
		}

		@Override
		public int compare(Integer a, Integer b) {
			return a.compareTo(b);
		}
	};

	/**
	 * Integral <em>type class</em> for {@link Long} values.
	 */
	Integral<Long> LONG = new Integral<>() {
		@Override
		public Long zero() {
			return 0L;
		}

		@Override
		public Long next(Long value, long n) {
			if (n < 0) {
				throw new IllegalArgumentException("n must be greater than zero:" + n);
			}
			return Math.addExact(value, n);
		}

		@Override
		public long distance(Long a, Long b) {
			return Math.abs(Math.subtractExact(a, b));
		}

		@Override
		public int compare(Long a, Long b) {
			return a.compareTo(b);
		}
	};

	/**
	 * Integral <em>type class</em> for {@link LocalDate} values.
	 */
	Integral<LocalDate> LOCAL_DATE = new Integral<>() {
		@Override
		public LocalDate zero() {
			return LocalDate.of(0, 1, 1);
		}

		@Override
		public LocalDate next(LocalDate value, long n) {
			return value.plusDays(n);
		}

		@Override
		public long distance(LocalDate a, LocalDate b) {
			return Math.abs(ChronoUnit.DAYS.between(a, b));
		}

		@Override
		public int compare(LocalDate a, LocalDate b) {
			return a.compareTo(b);
		}
	};

	/**
	 * Return the <em>zero</em> element of {@code T}.
	 *
	 * @return the <em>zero</em> element of {@code T}
	 */
	T zero();

	/**
	 * Return the element {@code n} positions next the element {@code a}. The
	 * {@link #distance(Object, Object)} between {@code a} and the returned
	 * element will be {@code n}.
	 * {@snippet lang=java:
	 * final Integral&lt;MyType> witness = null; // @replace substring='null' replacement="..."
	 * final MyType value = null; // @replace substring='null' replacement="..."
	 * final MyType result = witness.next(value, 100);
	 * assert witness.distance(value, result) == 100;
	 * }
	 *
	 * @param a the current element
	 * @param n the number of positions to jump
	 * @return the element {@code n} positions apart from {@code a}
	 */
	T next(T a, long n);

	/**
	 * Return the element on the next position of {@code value}.
	 *
	 * @see #next(Object, long)
	 *
	 * @param value the current value
	 * @return the next value
	 */
	default T next(T value) {
		return next(value, 1);
	}

	/**
	 * Returns the distance between {@code a} and {@code b}. The returned distance
	 * will always be positive.
	 *
	 * @param a first element
	 * @param b second element
	 * @return the distance between {@code a} and {@code b}; always positive
	 */
	long distance(T a, T b);

	/**
	 * Checks if the {@code a} is before {@code b}.
	 *
	 * @param a the first value
	 * @param b the second value
	 * @return {@code true} is {@code a} is before, {@code false} otherwise
	 */
	default boolean isBefore(T a, T b) {
		return compare(a, b) < 0;
	}

	/**
	 * Checks if the {@code a} is after {@code b}.
	 *
	 * @param a the first value
	 * @param b the second value
	 * @return {@code true} is {@code a} is after, {@code false} otherwise
	 */
	default boolean isAfter(T a, T b) {
		return compare(a, b) > 0;
	}
}
