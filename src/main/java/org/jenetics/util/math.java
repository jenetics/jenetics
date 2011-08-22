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
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
final class math {

	private math() {
		throw new AssertionError("Don't create an 'math' instance.");
	}
	
	static long add(final long a, final long b) {
		final long c = a + b;
		if (((c ^ a) & (c ^ b) >> 63) != 0) {
			throw new ArithmeticException(String.format("Overflow: %d + %d", a, b));
		}

		return c;
	}
	
	static long sub(final long a, final long b) {
		if (a >= 0 && b >= 0) {
			final long c = a - b;
			if (((a ^ b) & (a ^ c) >> 63) != 0) {
				throw new ArithmeticException(String.format("Overflow: %d + %d", a, b));
			}
	
			return c;
		} else if (a >= 0 && b < 0) {
			return add(a, b);
		} else if (a < 0 && b >= 0) {
			
		}
		return 0;
	}
	
	static boolean isMultiplicationSave(final int a, final int b) {
		final long m = (long)a*(long)b;
		return m >= Integer.MIN_VALUE && m <= Integer.MAX_VALUE;
	}
	
}
