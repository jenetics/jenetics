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
final class math {

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
	static long add(final long a, final long b) {
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
	static long sub(final long a, final long b) {
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
	
}
