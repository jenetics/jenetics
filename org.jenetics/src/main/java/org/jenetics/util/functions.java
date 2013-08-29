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

import static org.jenetics.util.object.nonNull;

import java.io.Serializable;
import java.util.Objects;

import org.jscience.mathematics.number.Float64;
import org.jscience.mathematics.number.Integer64;

/**
 * This class contains some short general purpose functions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public final class functions extends StaticObject {
	private functions() {}

	/**
	 * Convert an object to a string by calling the objects {@link Object#toString()}
	 * method.
	 */
	public static final Function<Object, String>
	ObjectToString = new Function<Object, String>() {
		@Override public String apply(final Object value) {
			return Objects.toString(value);
		}
	};

	/**
	 * Convert a string value to its length.
	 */
	public static final Function<String, Integer>
	StringLength = new Function<String, Integer>() {
		@Override public Integer apply(final String value) {
			return value.length();
		}
	};

	/**
	 * Convert a string to an integer. If the string can't be converted, an
	 * {@link NumberFormatException} is throws by the {@link Function#apply(Object)}
	 * method.
	 */
	public static final Function<String, Integer>
	StringToInteger = new Function<String, Integer>() {
		@Override public Integer apply(final String value) {
			return Integer.parseInt(value);
		}
	};

	/**
	 * Convert a string to a long. If the string can't be converted, an
	 * {@link NumberFormatException} is throws by the {@link Function#apply(Object)}
	 * method.
	 */
	public static final Function<String, Long>
	StringToLong = new Function<String, Long>() {
		@Override public Long apply(final String value) {
			return Long.parseLong(value);
		}
	};

	/**
	 * Convert a string to an Integer64. If the string can't be converted, an
	 * {@link NumberFormatException} is throws by the {@link Function#apply(Object)}
	 * method.
	 */
	public static final Function<String, Integer64>
	StringToInteger64 = new Function<String, Integer64>() {
		@Override public Integer64 apply(final String value) {
			return Integer64.valueOf(value);
		}
	};

	/**
	 * Convert a string to a float. If the string can't be converted, an
	 * {@link NumberFormatException} is throws by the {@link Function#apply(Object)}
	 * method.
	 */
	public static final Function<String, Float>
	StringToFloat = new Function<String, Float>() {
		@Override public Float apply(final String value) {
			return Float.parseFloat(value);
		}
	};

	/**
	 * Convert a string to a double. If the string can't be converted, an
	 * {@link NumberFormatException} is throws by the {@link Function#apply(Object)}
	 * method.
	 */
	public static final Function<String, Double>
	StringToDouble = new Function<String, Double>() {
		@Override public Double apply(final String value) {
			return Double.parseDouble(value);
		}
	};

	/**
	 * Convert a string to a Float64. If the string can't be converted, an
	 * {@link NumberFormatException} is throws by the {@link Function#apply(Object)}
	 * method.
	 */
	public static final Function<String, Float64>
	StringToFloat64 = new Function<String, Float64>() {
		@Override public Float64 apply(final String value) {
			return Float64.valueOf(value);
		}
	};

	/**
	 * Convert a {@link Float64} value to a {@link Double} value.
	 */
	public static final Function<Float64, Double>
	Float64ToDouble = new Function<Float64, Double>() {
		@Override public Double apply(final Float64 value) {
			return value.doubleValue();
		}
	};

	/**
	 * Convert a {@link Double} value to a {@link Float64} value.
	 */
	public static final Function<Double, Float64>
	DoubleToFloat64 = new Function<Double, Float64>() {
		@Override public Float64 apply(final Double value) {
			return Float64.valueOf(value);
		}
	};

	/**
	 * Convert a {@link Integer64} value to a {@link Long} value.
	 */
	public static final Function<Integer64, Long>
	Integer64ToLong = new Function<Integer64, Long>() {
		@Override public Long apply(final Integer64 value) {
			return value.longValue();
		}
	};

	/**
	 * Convert a {link Long} value to a {@link Integer64} value.
	 */
	public static final Function<Long, Integer64>
	LongToInteger64 = new Function<Long, Integer64>() {
		@Override public Integer64 apply(final Long value) {
			return Integer64.valueOf(value);
		}
	};

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


	private static final class Identity
		implements Function<Object, Object>, Serializable
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Object apply(final Object value) {
			return value;
		}
	}

	private static Function<Object, Object> IDENTITY = new Identity();

	/**
	 * Return the identity function for the given type.
	 *
	 * @return the identity function for the given type.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Function<T, T> Identity() {
		return (Function<T, T>)IDENTITY;
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






