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
 * Some static helper methods for checking preconditions.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class Validator {

	private Validator() {
		super();
	}

	private static final class NonNull implements Predicate<Object> {
		private final String _message;
		
		public NonNull(final String message) {
			_message = message;
		}
		
		@Override
		public boolean evaluate(final Object object) {
			nonNull(object, _message );
			return true;
		}
		
	}
	
	private static final class CheckRange<C extends Comparable<C>> 
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
	
	private static final class Verify<T extends Verifiable> implements Predicate<T> {
		@Override
		public boolean evaluate(T object) {
			return object.isValid();
		}
	}
	
	/**
	 * Create a predicate which allows to check all array elements for 
	 * {@code null}.
	 * 
	 * [code]
	 *     public void foo(final Array<Integer> array) {
	 *         // Will throw an NullPointerException if one of the array values is null.
	 *         array.foreach(Validator.NonNull("Value"));
	 *     }
	 * [/code]
	 * 
	 * @param message the message to print.
	 * @return the null check predicate.
	 */
	public static Predicate<Object> NonNull(final String message) {
		return new NonNull(message);
	}
	
	/**
	 * Create a predicate which allows to check all array elements for 
	 * {@code null}.
	 * 
	 * @see #NonNull(String)
	 * 
	 * @return the null check predicate.
	 */
	public static Predicate<Object> NonNull() {
		return new NonNull("Object");
	}
	
	public static <C extends Comparable<C>> Predicate<C> CheckRange(
		final C min, final C max
	) {
		return new CheckRange<C>(min, max);
	}
	
	public static <T extends Verifiable> Predicate<T> Verify() {
		return new Verify<T>();
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
	 * Check if a given value is within a given closed range.
	 * 
	 * @param value the value to check.
	 * @param min the min value (inclusively).
	 * @param max the max value (exclusively).
	 * @return {@code value} if within the range.
	 * @throws IllegalArgumentException if the given {@code value} is out of
	 *         range.
	 */
	public static <T extends Comparable<T>> T checkRange(
		final T value, final T min, final T max
	) {
		if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
			throw new IllegalArgumentException(String.format(
				"Value %s is out of range [%s, %s).", value, min, max
			));
		}
		return value;
	}
	
	/**
	 * Check if the given integer is negative.
	 * 
	 * @param length the value to check.
	 * @throws NegativeArraySizeException if the given {@code length} is smaller
	 *         than zero.
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
					"The given probability is not in the range [0, 1]: %s", p
				));
		}
		return p;
	}
	
}
