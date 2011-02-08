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
 * Some helper methods for creating hash codes and compare double values.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class ObjectUtils {

	private ObjectUtils() {
		throw new AssertionError("Don't create an 'ObjectUtils' instance.");
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


