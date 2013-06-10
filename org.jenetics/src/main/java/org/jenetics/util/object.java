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
import java.util.Objects;

/**
 * Some helper methods for creating hash codes and comparing values.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.3 &mdash; <em>$Date: 2013-06-10 $</em>
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
	 * arrays.foreach(CheckRange<(0, 10));
	 * [/code]
	 */
	public static final <C extends Comparable<? super C>> Function<C, Boolean>
	CheckRange(final C min, final C max)
	{
		return new Function<C,Boolean>() {
			@Override
			public Boolean apply(final C value) {
				nonNull(value);
				if (value.compareTo(min) < 0 || value.compareTo(max) >= 0) {
					throw new IllegalArgumentException(String.format(
						"Given value %s is out of range [%s, %s)",
						value, min, max
					));
				}
				return Boolean.TRUE;
			}
		};
	}


	/**
	 * Verifies {@link Verifiable} array elements. All elements are valid if the
	 * condition
	 * [code]
	 * arrays.forall(Verify) == true
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
	 * array.foreach(NonNull("Object"));
	 * ...
	 * final String[] array = ...
	 * arrays.foreach(array, NonNull);
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
	 * array.foreach(NonNull("Object"));
	 * ...
	 * final String[] array = ...
	 * arrays.foreach(array, NonNull);
	 * [/code]
	 */
	public static final Function<Object, Boolean> NonNull(final String message) {
		return new Function<Object,Boolean>() {
			@Override public Boolean apply(final Object object) {
				nonNull(object, message );
				return Boolean.TRUE;
			}
		};
	}

	/**
	 * Checks that the specified object reference is not {@code null}.
	 *
	 * @param obj the object to check.
	 * @param message the error message.
	 * @return {@code obj} if not {@code null}.
	 * @throws NullPointerException if {@code obj} is {@code null}.
	 */
	public static <T> T nonNull(final T obj, final String message) {
		if (obj == null) {
			throw new NullPointerException(message + " must not be null.");
		}
		return obj;
	}

	/**
	 * Checks that the specified object reference is not {@code null}.
	 *
	 * @param obj the object to check.
	 * @return {@code obj} if not {@code null}.
	 * @throws NullPointerException if {@code obj} is {@code null}.
	 */
	public static <T> T nonNull(final T obj) {
		return nonNull(obj, "Object");
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
			throw new IllegalArgumentException(String.format(
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
	public static void nonNegative(final int length) {
		if (length < 0) {
			throw new NegativeArraySizeException(
				"Length must be greater than zero, but was " + length + ". "
			);
		}
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
			throw new IllegalArgumentException(String.format(
				"The given probability is not in the range [0, 1]: %f", p
			));
		}
		return p;
	}

	/**
	 * Create a HashCodeBuilder for the given type.
	 *
	 * @param type the type the HashCodebuilder is created for.
	 * @return a new HashCodeBuilder.
	 */
	public static HashCodeBuilder hashCodeOf(final Class<?> type) {
		return new DefaultHashCodeBuilder(type);
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

	/**
	 * Returns the result of calling toString for a non-null argument and "null"
	 * for a null argument.
	 *
	 * @see Objects#toString(Object)
	 *
	 * @param a the object.
	 * @return the result of calling toString for a non-null argument and "null"
	 *          for a null argument
	 *
	 * @deprecated Use {@link Objects#toString(Object)} instead.
	 */
	@Deprecated
	public static String str(final Object a) {
		return Objects.toString(a);
	}

	/**
	 * Print a binary representation of the given byte array. The printed string
	 * has the following format:
	 * <pre>
	 *  Byte:       3        2        1        0
	 *              |        |        |        |
	 *  Array: "11110011|10011101|01000000|00101010"
	 *          |                 |        |      |
	 *  Bit:    23                15       7      0
	 * </pre>
	 * <i>Only the array string is printed.</i>
	 *
	 * @param data the byte array to convert to a string.
	 * @return the binary representation of the given byte array.
	 *
	 * @deprecated Use {@link bit#toByteString(byte...)} instead.
	 */
	@Deprecated
	public static String str(final byte... data) {
		return bit.toByteString(data);
	}

}


