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


/**
 * Object with mathematical functions.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class math {

	private math() {
		throw new AssertionError("Don't create an 'math' instance.");
	}
	
	/**
	 * Add to long values and throws an ArithmeticException in the case of an
	 * overflow.
	 * 
	 * @param a the first summand.
	 * @param b the second summand. 
	 * @return the sum of the given values.
	 * @throws ArithmeticException if the summation would lead to an overflow.
	 */
	public static long add(final long a, final long b) {
		final long z = a + b;
		if (a > 0) {
			if (b > 0 && z < 0) {
				throw new ArithmeticException(String.format("Overflow: %d + %d", a, b));
			}
		} else if (b < 0 && z > 0) {
			throw new ArithmeticException(String.format("Overflow: %d + %d", a, b));
		}
		
		return z;
	}
	
	/**
	 * Subtracts to long values and throws an ArithmeticException in the case of an
	 * overflow.
	 * 
	 * @param a the minuend.
	 * @param b the subtrahend. 
	 * @return the difference of the given values.
	 * @throws ArithmeticException if the subtraction would lead to an overflow.
	 */
	public static long sub(final long a, final long b) {
		final long z = a - b;
		if (a > 0) {
			if (b < 0 && z < 0) {
				throw new ArithmeticException(String.format("Overflow: %d - %d", a, b));
			}
		} else if (b > 0 && z > 0) {
			throw new ArithmeticException(String.format("Overflow: %d - %d", a, b));
		}
		
		return z;
	}
	
	static double plus(final double a, final long ulpDistance) {
		long t = Double.doubleToLongBits(a) + ulpDistance;
		if (t < 0) {
			t = Long.MIN_VALUE - t;
		}
		return Double.longBitsToDouble(t);
	}
	
	static boolean isMultiplicationSave(final int a, final int b) {
		final long m = (long)a*(long)b;
		return m >= Integer.MIN_VALUE && m <= Integer.MAX_VALUE;
	}
	
	/**
	 * Return the <a href="http://en.wikipedia.org/wiki/Unit_in_the_last_place">ULP</a>
	 * distance of the given two double values.
	 * 
	 * @param a first double.
	 * @param b second double.
	 * @return the ULP distance.
	 * @throws ArithmeticException if the distance doesn't fit in a long value.
	 */
	public static long ulpDistance(final double a, final double b) {
		return sub(ulpPosition(a), ulpPosition(b));
	}
	
	/**
	 * Calculating the <a href="http://en.wikipedia.org/wiki/Unit_in_the_last_place">ULP</a> 
	 * position of a double number.
	 * 
	 * [code]
	 * 	double a = 0.0;
	 * 	for (int i = 0; i < 10; ++i) {
	 * 		 a = Math.nextAfter(a, Double.POSITIVE_INFINITY);
	 * 	}
	 *
	 * 	for (int i = 0; i < 19; ++i) {
	 * 		 a = Math.nextAfter(a, Double.NEGATIVE_INFINITY);
	 * 		 System.out.println(
	 * 			  a + "\t" + ulpPosition(a) + "\t" + ulpDistance(0.0, a)
	 * 		 );
	 * 	 }
	 * [/code]
	 * 
	 * The code fragment above will create the following output:
	 * <pre>
	 * 	 4.4E-323	 9 	9
	 * 	 4.0E-323	 8 	8
	 * 	 3.5E-323	 7 	7
	 * 	 3.0E-323	 6 	6
	 * 	 2.5E-323	 5 	5
	 * 	 2.0E-323	 4 	4
	 * 	 1.5E-323	 3 	3
	 * 	 1.0E-323	 2 	2
	 * 	 4.9E-324	 1 	1
	 * 	 0.0         0  0
	 * 	-4.9E-324	-1 	1
	 * 	-1.0E-323	-2 	2
	 * 	-1.5E-323	-3 	3
	 * 	-2.0E-323	-4 	4
	 * 	-2.5E-323	-5 	5
	 * 	-3.0E-323	-6 	6
	 * 	-3.5E-323	-7 	7
	 * 	-4.0E-323	-8 	8
	 * 	-4.4E-323	-9 	9
	 * </pre>
	 * 
	 * @param a the double number.
	 * @return the ULP position.
	 */
	public static long ulpPosition(final double a) {
		long t = Double.doubleToLongBits(a);
		if (t < 0) {
			t = Long.MIN_VALUE - t;
		}
		return t;
	}
	
}
