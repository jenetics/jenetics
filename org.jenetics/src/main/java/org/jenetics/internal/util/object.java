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
package org.jenetics.internal.util;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import org.jenetics.util.Function;
import org.jenetics.util.Seq;
import org.jenetics.util.StaticObject;
import org.jenetics.util.Verifiable;
import org.jenetics.util.arrays;

/**
 * Some helper methods for creating hash codes and comparing values.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.6 &mdash; <em>$Date: 2014-03-01 $</em>
 */
public final class object extends StaticObject {
	private object() {}


	/**
	 * A range checking predicate which can be used to check whether the elements
	 * of an array are within an given range. If not, an
	 * {@link IllegalArgumentException} is thrown. If one value is {@code null},
	 * an {@link NullPointerException} is thrown.
	 * <p/>
	 *
	 * The following code will throw an {@link IllegalArgumentException} if the
	 * integers in the array are smaller than zero and greater than 9.
	 * [code]
	 * final Array<Integer> array = ...
	 * arrays.forEach(CheckRange<(0, 10));
	 * [/code]
	 */
	public static <C extends Comparable<? super C>> Function<C, Boolean>
	CheckRange(final C min, final C max)
	{
		return new Function<C,Boolean>() {
			@Override
			public Boolean apply(final C value) {
				requireNonNull(value);
				if (value.compareTo(min) < 0 || value.compareTo(max) >= 0) {
					throw new IllegalArgumentException(format(
						"Given value %s is out of range [%s, %s)",
						value, min, max
					));
				}
				return Boolean.TRUE;
			}
		};
	}


	/**
	 * Verifies {@link org.jenetics.util.Verifiable} array elements. All elements are valid if the
	 * condition
	 * [code]
	 * arrays.forAll(Verify) == true
	 * [/code]
	 * is true.
	 */
	public static final Function<Verifiable, Boolean>
		Verify = new Function<Verifiable,Boolean>() {
		@Override
		public Boolean apply(final Verifiable object) {
			return object.isValid() ? Boolean.TRUE : Boolean.FALSE;
		}
	};

	/**
	 * A {@code null} checking predicate which can be used to check an array
	 * for null values. The following code will throw an
	 * {@link NullPointerException} if one of the array elements is {@code null}.
	 *
	 * [code]
	 * final Array<String> array = ...
	 * array.forEach(NonNull("Object"));
	 * ...
	 * final String[] array = ...
	 * arrays.forEach(array, NonNull);
	 * [/code]
	 */
	public static final Function<Object, Boolean> NonNull = NonNull("Object");

	/**
	 * A {@code null} checking predicate which can be used to check an array
	 * for null values. The following code will throw an
	 * {@link NullPointerException} if one of the array elements is {@code null}.
	 *
	 * [code]
	 * final Array<String> array = ...
	 * array.forEach(NonNull("Object"));
	 * ...
	 * final String[] array = ...
	 * arrays.forEach(array, NonNull);
	 * [/code]
	 */
	public static Function<Object, Boolean> NonNull(final String message) {
		return new Function<Object,Boolean>() {
			@Override public Boolean apply(final Object object) {
				requireNonNull(object, message );
				return Boolean.TRUE;
			}
		};
	}

	/**
	 * Check if the specified value is not negative.
	 *
	 * @param value the value to check.
	 * @param message the exception message.
	 * @return the given value.
	 * @throws IllegalArgumentException if {@code value < 0}.
	 */
	public static double nonNegative(final double value, final String message) {
		if (value < 0) {
			throw new IllegalArgumentException(format(
				"%s must not negative: %f.", message, value
			));
		}
		return value;
	}

	/**
	 * Check if the specified value is not negative.
	 *
	 * @param value the value to check.
	 * @return the given value.
	 * @throws IllegalArgumentException if {@code value < 0}.
	 */
	public static double nonNegative(final double value) {
		return nonNegative(value, "Value");
	}

	/**
	 * Check if the given integer is negative.
	 *
	 * @param length the value to check.
	 * @throws NegativeArraySizeException if the given {@code length} is smaller
	 * 		  than zero.
	 */
	public static int nonNegative(final int length) {
		if (length < 0) {
			throw new NegativeArraySizeException(
				"Length must be greater than zero, but was " + length + ". "
			);
		}
		return length;
	}

	/**
	 * Check if the given double value is within the closed range {@code [0, 1]}.
	 *
	 * @param p the probability to check.
	 * @return p if it is a valid probability.
	 * @throws IllegalArgumentException if {@code p} is not a valid probability.
	 */
	public static double checkProbability(final double p) {
		if (p < 0.0 || p > 1.0) {
			throw new IllegalArgumentException(format(
				"The given probability is not in the range [0, 1]: %f", p
			));
		}
		return p;
	}

	/**
	 * Compares the two given {@code boolean} values.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final boolean a, final boolean b) {
		return a == b;
	}

	/**
	 * Compares the two given {@code boolean} arrays.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final boolean[] a, final boolean[] b) {
		return Arrays.equals(a, b);
	}

	/**
	 * Compares the two given {@code byte} values.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final byte a, final byte b) {
		return a == b;
	}

	/**
	 * Compares the two given {@code byte} arrays.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final byte[] a, final byte[] b) {
		return Arrays.equals(a, b);
	}

	/**
	 * Compares the two given {@code char} values.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final char a, final char b) {
		return a == b;
	}

	/**
	 * Compares the two given {@code char} arrays.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final char[] a, final char[] b) {
		return Arrays.equals(a, b);
	}

	/**
	 * Compares the two given {@code short} values.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final short a, final short b) {
		return a == b;
	}

	/**
	 * Compares the two given {@code short} arrays.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final short[] a, final short[] b) {
		return Arrays.equals(a, b);
	}

	/**
	 * Compares the two given {@code int} values.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final int a, final int b) {
		return a == b;
	}

	/**
	 * Compares the two given {@code int} arrays.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final int[] a, final int[] b) {
		return Arrays.equals(a, b);
	}

	/**
	 * Compares the two given {@code long} values.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final long a, final long b) {
		return a == b;
	}

	/**
	 * Compares the two given {@code long} arrays.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final long[] a, final long[] b) {
		return Arrays.equals(a, b);
	}

	/**
	 * Compares the two given {@code float} values.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final float a, final float b) {
		return Float.floatToIntBits(a) == Float.floatToIntBits(b);
	}

	/**
	 * Compares the two given {@code float} arrays.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final float[] a, final float[] b) {
		return Arrays.equals(a, b);
	}

	/**
	 * Compares the two given {@code double} values.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final double a, final double b) {
		return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
	}

	/**
	 * Compares the two given {@code double} arrays.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final double[] a, final double[] b) {
		return Arrays.equals(a, b);
	}

	/**
	 * Compares the two given {@code Enum} values.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final Enum<?> a, final Enum<?> b) {
		return a == b;
	}

	/**
	 * Compares the two given {@code Object} values.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final Object a, final Object b) {
		return (a != null ? a.equals(b) : b == null);
	}

	public static boolean eq(final Measurable<Duration> a, final Measurable<Duration> b) {
		if (a == null && b == null) {
			return true;
		}
		return a != null && b != null &&
			a.longValue(SI.NANO(SI.SECOND)) == b.longValue(SI.NANO(SI.SECOND));
	}

	/**
	 * Compares the two given {@code Object} arrays.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final Object[] a, final Object[] b) {
		return Arrays.equals(a, b);
	}

	/**
	 * Compares the two given {@code Seq} values.
	 *
	 * @param a first value to compare.
	 * @param b second value to compare.
	 * @return {@code true} if the given values are equal, {@code false}
	 *          otherwise.
	 */
	public static boolean eq(final Seq<?> a, final Seq<?> b) {
		return arrays.equals(a, b);
	}

}
