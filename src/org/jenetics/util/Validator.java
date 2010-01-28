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
 * @version $Id: Validator.java,v 1.4 2010-01-28 19:34:14 fwilhelm Exp $
 */
public final class Validator {

	private Validator() {
		super();
	}

	private static class NonNull implements Predicate<Object> {
		private final String _message;
		
		public NonNull(final String message) {
			_message = message;
		}
		
		@Override
		public boolean evaluate(final Object object) {
			nonNull(object, _message);
			return true;
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
	 * @see {@link #NonNull(String)}
	 * 
	 * @return the null check predicate.
	 */
	public static Predicate<Object> NonNull() {
		return new NonNull("Object");
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
					"The given probability is not in the range [0 .. 1]: %s", p
				));
		}
		return p;
	}
	
}
