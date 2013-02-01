/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
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

import static org.jenetics.util.object.nonNull;
import static org.jenetics.util.object.str;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.number.Integer64;

/**
 * This class contains some short general purpose functions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-02-01 $</em>
 */
public final class functions extends StaticObject {
	private functions() {}

	/**
	 * Convert an object to a string by calling the objects {@link Object#toString()}
	 * method.
	 */
	public static final Function<Object, String>
	ObjectToString = o -> Objects.toString(o);

	/**
	 * Convert a string value to its length.
	 */
	public static final Function<String, Integer>
	StringLength = s -> s.length();

	/**
	 * Convert a string to an integer. If the string can't be converted, an
	 * {@link NumberFormatException} is throws by the {@link Function#apply(Object)}
	 * method.
	 */
	public static final Function<String, Integer>
	StringToInteger = s -> Integer.parseInt(s);

	/**
	 * Convert a string to a long. If the string can't be converted, an
	 * {@link NumberFormatException} is throws by the {@link Function#apply(Object)}
	 * method.
	 */
	public static final Function<String, Long>
	StringToLong = s -> Long.parseLong(s);

	/**
	 * Convert a string to an Integer64. If the string can't be converted, an
	 * {@link NumberFormatException} is throws by the {@link Function#apply(Object)}
	 * method.
	 */
	public static final Function<String, Integer64>
	StringToInteger64 = s -> Integer64.valueOf(s);

	/**
	 * Convert a string to a float. If the string can't be converted, an
	 * {@link NumberFormatException} is throws by the {@link Function#apply(Object)}
	 * method.
	 */
	public static final Function<String, Float>
	StringToFloat = s -> Float.parseFloat(s);

	/**
	 * Convert a string to a double. If the string can't be converted, an
	 * {@link NumberFormatException} is throws by the {@link Function#apply(Object)}
	 * method.
	 */
	public static final Function<String, Double>
	StringToDouble = s -> Double.parseDouble(s);

	/**
	 * Convert a string to a Float64. If the string can't be converted, an
	 * {@link NumberFormatException} is throws by the {@link Function#apply(Object)}
	 * method.
	 */
	public static final Function<String, Float64>
	StringToFloat64 = s -> Float64.valueOf(s);

	/**
	 * Convert a {@link Float64} value to a {@link Double} value.
	 */
	public static final Function<Float64, Double>
	Float64ToDouble = f -> f.doubleValue();

	/**
	 * Convert a {@link Double} value to a {@link Float64} value.
	 */
	public static final Function<Double, Float64>
	DoubleToFloat64 = d -> Float64.valueOf(d);

	/**
	 * Convert a {@link Integer64} value to a {@link Long} value.
	 */
	public static final Function<Integer64, Long>
	Integer64ToLong = i -> i.longValue();

	/**
	 * Convert a {link Long} value to a {@link Integer64} value.
	 */
	public static final Function<Long, Integer64>
	LongToInteger64 = l -> Integer64.valueOf(l);

	/**
	 * A predicate which return {@code true} if an given value is {@code null}.
	 */
	public static final Function<Object, Boolean>
	Null = new Function<Object, Boolean>() {
		@Override public Boolean apply(final Object object) {
			return object == null ? Boolean.TRUE : Boolean.FALSE;
		}
		@Override public String toString() {
			return String.format("%s", getClass().getSimpleName());
		}
	};

	/**
	 * Return a predicate which negates the return value of the given predicate.
	 *
	 * @param <T> the value type to check.
	 * @param a the predicate to negate.
	 * @return a predicate which negates the return value of the given predicate.
	 * @throws NullPointerException if the given predicate is {@code null}.
	 */
	public static <T> Function<T, Boolean> not(final Function<? super T, Boolean> a) {
		nonNull(a);
		return new Function<T, Boolean>() {
			@Override public Boolean apply(final T object) {
				return a.apply(object) ? Boolean.FALSE : Boolean.TRUE;
			}
			@Override public String toString() {
				return String.format("%s[%s]", getClass().getSimpleName(), a);
			}
		};
	}

	/**
	 * Return a {@code and} combination of the given predicates.
	 *
	 * @param <T> the value type to check.
	 * @param a the first predicate
	 * @param b the second predicate
	 * @return a {@code and} combination of the given predicates.
	 * @throws NullPointerException if one of the given predicates is
	 *         {@code null}.
	 */
	public static <T> Function<T, Boolean> and(
		final Function<? super T, Boolean> a,
		final Function<? super T, Boolean> b
	) {
		nonNull(a);
		nonNull(b);
		return new Function<T, Boolean>() {
			@Override public Boolean apply(final T object) {
				return a.apply(object) && b.apply(object);
			}
			@Override public String toString() {
				return String.format("%s[%s, %s]", getClass().getSimpleName(), a, b);
			}
		};
	}

	/**
	 * Return a {@code or} combination of the given predicates.
	 *
	 * @param <T> the value type to check.
	 * @param a the first predicate
	 * @param b the second predicate
	 * @return a {@code and} combination of the given predicates.
	 * @throws NullPointerException if one of the given predicates is
	 *          {@code null}.
	 */
	public static <T> Function<T, Boolean> or(
		final Function<? super T, Boolean> a,
		final Function<? super T, Boolean> b
	) {
		nonNull(a);
		nonNull(b);
		return new Function<T, Boolean>() {
			@Override public Boolean apply(final T object) {
				return a.apply(object) || b.apply(object);
			}
			@Override public String toString() {
				return String.format(
						"%s[%s, %s]",
						getClass().getSimpleName(), a, b
					);
			}
		};
	}

	/**
	 * Return the identity function for the given type.
	 *
	 * @return the identity function for the given type.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Function<T, T> Identity() {
		return o -> o;
	}


	public static <A, B, C> Function<A, C> compose(
		final Function<A, B> f1,
		final Function<B, C> f2
	) {
		nonNull(f1, "Function 1");
		nonNull(f2, "Function 2");

		return new Function<A, C>() {
			@Override public C apply(A value) {
				return f2.apply(f1.apply(value));
			}
		};
	}

	public static <A, B, C, D> Function<A, D> compose(
		final Function<A, B> f1,
		final Function<B, C> f2,
		final Function<C, D> f3
	) {
		return compose(compose(f1, f2), f3);
	}

	public static <A, B, C, D, E> Function<A, E> compose(
		final Function<A, B> f1,
		final Function<B, C> f2,
		final Function<C, D> f3,
		final Function<D, E> f4
	) {
		return compose(compose(compose(f1, f2), f3), f4);
	}

	public static <A, B, C, D, E, F> Function<A, F> compose(
		final Function<A, B> f1,
		final Function<B, C> f2,
		final Function<C, D> f3,
		final Function<D, E> f4,
		final Function<E, F> f5
	) {
		return compose(compose(compose(compose(f1, f2), f3), f4), f5);
	}

}






