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

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

import org.jenetics.util.Seq;

/**
 * Helper object for calculating object equality.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
public final class Equality {
	private Equality() {require.noInstance();}

	public static final Predicate<Object> TRUE = a -> true;

	/**
	 * Create a new {@code Equality} object for testing object equality.
	 *
	 * @param self the {@code this} object to test; must not be {@code null}
	 * @param other the {@code other} object to test; maybe {@code null}
	 * @param <T> the object type
	 * @return the {@code Predicate} object for equality testing
	 * @throws java.lang.NullPointerException if the {@code self} parameter is
	 *         {@code null}
	 */
	@SuppressWarnings("unchecked")
	public static <T> Predicate<Predicate<T>>
	of(final T self, final Object other) {
		Objects.requireNonNull(self);
		return self == other ?
			p -> true :
			other == null || self.getClass() != other.getClass() ?
				p -> false : p -> p.test((T)other);
	}

	/**
	 * Check if the given arguments are from the same type.
	 *
	 * @param self the {@code this} object to test; must not be {@code null}
	 * @param other the {@code other} object to test; maybe {@code null}
	 * @return {@code true} if the two objects are from the same type
	 * @throws java.lang.NullPointerException if the {@code self} parameter is
	 *         {@code null}
	 */
	public static boolean ofType(final Object self, final Object other) {
		Objects.requireNonNull(self);
		return self == other ||
			other != null && self.getClass() == other.getClass();
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
		return a != null ? a.equals(b) : b == null;
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
		return Seq.equals(a, b);
	}
}
