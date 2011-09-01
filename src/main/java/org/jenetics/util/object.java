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

/**
 * Some helper methods for creating hash codes and compare values.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class object {

	private object() {
		throw new AssertionError("Don't create an 'object' instance.");
	}
	
	/**
	 * A {@code null} checking predicate which can be used to check an array
	 * for null values. The following code will throw an 
	 * {@link NullPointerException} if one of the array elements is {@code null}.
	 * 
	 * [code]
	 * 	 final Array<String> array = ...
	 * 	 array.foreach(new NonNull());
	 * 	 ...
	 * 	 final String[] array = ...
	 * 	 ArrayUtils.foreach(array, new NonNull());
	 * [/code]
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static final class NonNull implements Predicate<Object> {
		private final String _message;
		
		public NonNull() {
			this("Object");
		}
		
		public NonNull(final String message) {
			_message = message;
		}
		
		@Override
		public boolean evaluate(final Object object) {
			nonNull(object, _message );
			return true;
		}
		
	}
	
	
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
	 * 	 final Array<Integer> array = ...
	 * 	 array.foreach(new CheckRange<Integer>(0, 10));
	 * [/code]
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static final class CheckRange<C extends Comparable<? super C>> 
		implements Predicate<C> 
	{
		private final C _min;
		private final C _max;
		
		public CheckRange(final C min, final C max) {
			_min = nonNull(min);
			_max = nonNull(max);
		}
		
		@Override
		public boolean evaluate(final C value) {
			nonNull(value);
			if (value.compareTo(_min) < 0 || value.compareTo(_max) >= 0) {
				throw new IllegalArgumentException(String.format(
						"Given value %s is out of range [%s, %s)", 
						value, _min, _max
					));
			}
			return true;
		}	
	}
	
	
	/**
	 * Verifies {@link Verifiable} array elements. All elements are valid if the
	 * condition
	 * [code]
	 * 	 array.foreach(new Verify()) == -1
	 * [/code]
	 * is true.
	 * 
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	public static final class Verify implements Predicate<Verifiable> {
		@Override
		public boolean evaluate(final Verifiable object) {
			return object.isValid();
		}
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
	
	public static final class HashCodeBuilder {
		private static final int P1 = 47;
		private static final int P2 = 103;
		private static final int P3 = 1231;
		private static final int P4 = 1237;
		
		private int _hash = 0;
		
		private HashCodeBuilder(final int hash) {
			_hash = hash;
		}
		
		public HashCodeBuilder and(final boolean value) {
			_hash += value ? P3 : P4; return this;
		}
		
		public HashCodeBuilder and(final boolean[] values) {
			_hash += Arrays.hashCode(values); return this;
		}
		
		public HashCodeBuilder and(final byte value) {
			_hash += P1*value + P2; return this;
		}
		
		public HashCodeBuilder and(final byte[] values) {
			_hash += Arrays.hashCode(values); return this;
		}
		
		public HashCodeBuilder and(final char value) {
			_hash += P1*value + P2; return this;
		}
		
		public HashCodeBuilder and(final char[] values) {
			_hash += Arrays.hashCode(values); return this;
		}		
		
		public HashCodeBuilder and(final short value) {
			_hash += P1*value + P2; return this;
		}
		
		public HashCodeBuilder and(final short[] values) {
			_hash += Arrays.hashCode(values); return this;
		}
		
		public HashCodeBuilder and(final int value) {
			_hash += P1*value + P2; return this;
		}
		
		public HashCodeBuilder and(final int[] values) {
			_hash += Arrays.hashCode(values); return this;
		}
		
		public HashCodeBuilder and(final long value) {
			_hash += P1*(int)(value^(value >>> 32)); return this;
		}
		
		public HashCodeBuilder and(final long[] values) {
			_hash += Arrays.hashCode(values); return this;
		}
		
		public HashCodeBuilder and(final float value) {
			_hash += P1*Float.floatToIntBits(value); return this;
		}
		
		public HashCodeBuilder and(final float[] values) {
			_hash += Arrays.hashCode(values); return this;
		}
		
		public HashCodeBuilder and(final double value) {
			long bits = Double.doubleToLongBits(value);
			_hash += (int)(bits^(bits >>> 32));
			return this;
		}
		
		public HashCodeBuilder and(final double[] values) {
			_hash += Arrays.hashCode(values); return this;
		}
		
		public HashCodeBuilder and(final Object value) {
			_hash += P1*(value == null ? 0 : value.hashCode()) + P2; return this;
		}
		
		public HashCodeBuilder and(final Object[] values) {
			_hash += Arrays.hashCode(values); return this;
		}
		
		public int value() {
			return _hash;
		}
	}
	
	public static HashCodeBuilder hashCodeOf(final Class<?> type) {
		return new HashCodeBuilder(type.hashCode());
	}
	
	public static boolean eq(final boolean a, final boolean b) {
		return a == b;
	}
	
	public static boolean eq(final boolean[] a, final boolean[] b) {
		return Arrays.equals(a, b);
	}
	
	public static boolean eq(final byte a, final byte b) {
		return a == b;
	}
	
	public static boolean eq(final byte[] a, final byte[] b) {
		return Arrays.equals(a, b);
	}
	
	public static boolean eq(final char a, final char b) {
		return a == b;
	}
	
	public static boolean eq(final char[] a, final char[] b) {
		return Arrays.equals(a, b);
	}
	
	public static boolean eq(final short a, final short b) {
		return a == b;
	}
	
	public static boolean eq(final short[] a, final short[] b) {
		return Arrays.equals(a, b);
	}
	
	public static boolean eq(final int a, final int b) {
		return a == b;
	}
	
	public static boolean eq(final int[] a, final int[] b) {
		return Arrays.equals(a, b);
	}
	
	public static boolean eq(final long a, final long b) {
		return a == b;
	}
	
	public static boolean eq(final long[] a, final long[] b) {
		return Arrays.equals(a, b);
	}
	
	public static boolean eq(final float a, final float b) {
		return Float.floatToIntBits(a) == Float.floatToIntBits(b);
	}
	
	public static boolean eq(final float[] a, final float[] b) {
		return Arrays.equals(a, b);
	}
	
	public static boolean eq(final double a, final double b) {
		return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
	}
	
	public static boolean eq(final double[] a, final double[] b) {
		return Arrays.equals(a, b);
	}
	
	public static boolean eq(final Enum<?> a, final Enum<?> b) {
		return a == b;
	}
	
	public static boolean eq(final Object a, final Object b) {
		return (a != null ? a.equals(b) : b == null);
	}
	
	public static boolean eq(final Object[] a, final Object[] b) {
		return Arrays.equals(a, b);
	}
	
	public static String str(final Object a) {
		return a != null ? a.toString() : "null";
	}
	
}


