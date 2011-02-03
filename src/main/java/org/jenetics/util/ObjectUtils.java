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
 * Some helper methods for creating hash codes and compare double values.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class ObjectUtils {

	private ObjectUtils() {
		throw new AssertionError("Don't create an 'ObjectUtils' instance.");
	}
	
	public static int hashCode(final double a) {
		long bits = Double.doubleToLongBits(a);
		return (int)(bits ^ (bits >>> 32));
	}
	
	public static int hashCode(final int superHash, final double a) {
		return (31*superHash + 17) + hashCode(a);
	}
	
	public static boolean equals(final double a, final double b) {
		return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
	}
	
}
