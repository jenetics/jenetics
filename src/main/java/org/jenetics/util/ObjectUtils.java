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
		private int _hash = 0;
		
		private HashCodeBuilder(final int hash) {
			_hash = hash;
		}
		
		public HashCodeBuilder and(final boolean value) {
			_hash += ObjectUtils.hashCode(value); return this;
		}
		
		public HashCodeBuilder and(final boolean[] values) {
			_hash += Arrays.hashCode(values); return this;
		}
		
		public HashCodeBuilder and(final byte value) {
			_hash += ObjectUtils.hashCode(value); return this;
		}
		
		public HashCodeBuilder and(final byte[] values) {
			_hash += Arrays.hashCode(values); return this;
		}
		
		public HashCodeBuilder and(final char value) {
			_hash += ObjectUtils.hashCode(value); return this;
		}
		
		public HashCodeBuilder and(final char[] values) {
			_hash += Arrays.hashCode(values); return this;
		}		
		
		public HashCodeBuilder and(final short value) {
			_hash += ObjectUtils.hashCode(value); return this;
		}
		
		public HashCodeBuilder and(final short[] values) {
			_hash += Arrays.hashCode(values); return this;
		}
		
		public HashCodeBuilder and(final int value) {
			_hash += ObjectUtils.hashCode(value); return this;
		}
		
		public HashCodeBuilder and(final int[] values) {
			_hash += Arrays.hashCode(values); return this;
		}
		
		public HashCodeBuilder and(final long value) {
			_hash += ObjectUtils.hashCode(value); return this;
		}
		
		public HashCodeBuilder and(final long[] values) {
			_hash += Arrays.hashCode(values); return this;
		}
		
		public HashCodeBuilder and(final float value) {
			_hash += ObjectUtils.hashCode(value); return this;
		}
		
		public HashCodeBuilder and(final float[] values) {
			_hash += Arrays.hashCode(values); return this;
		}
		
		public HashCodeBuilder and(final double value) {
			_hash += ObjectUtils.hashCode(value); return this;
		}
		
		public HashCodeBuilder and(final double[] values) {
			_hash += Arrays.hashCode(values); return this;
		}
		
		public HashCodeBuilder and(final Object value) {
			_hash += ObjectUtils.hashCode(value); return this;
		}
		
		public HashCodeBuilder and(final Object[] values) {
			_hash += Arrays.hashCode(values); return this;
		}
		
		public int value() {
			return _hash;
		}
	}
	
	public static HashCodeBuilder hashCodeOf(final boolean value) {
		return new HashCodeBuilder(hashCode(value));
	}
	
	public static HashCodeBuilder hashCodeOf(final boolean[] values) {
		return new HashCodeBuilder(Arrays.hashCode(values));
	}
	
	public static HashCodeBuilder hashCodeOf(final byte value) {
		return new HashCodeBuilder(hashCode(value));
	}
	
	public static HashCodeBuilder hashCodeOf(final byte[] values) {
		return new HashCodeBuilder(Arrays.hashCode(values));
	}
	
	public static HashCodeBuilder hashCodeOf(final short value) {
		return new HashCodeBuilder(hashCode(value));
	}
	
	public static HashCodeBuilder hashCodeOf(final short[] values) {
		return new HashCodeBuilder(Arrays.hashCode(values));
	}
	
	public static HashCodeBuilder hashCodeOf(final int value) {
		return new HashCodeBuilder(hashCode(value));
	}
	
	public static HashCodeBuilder hashCodeOf(final int[] values) {
		return new HashCodeBuilder(Arrays.hashCode(values));
	}
	
	public static HashCodeBuilder hashCodeOf(final long value) {
		return new HashCodeBuilder(hashCode(value));
	}
	
	public static HashCodeBuilder hashCodeOf(final long[] values) {
		return new HashCodeBuilder(Arrays.hashCode(values));
	}
	
	public static HashCodeBuilder hashCodeOf(final float value) {
		return new HashCodeBuilder(hashCode(value));
	}
	
	public static HashCodeBuilder hashCodeOf(final float[] values) {
		return new HashCodeBuilder(Arrays.hashCode(values));
	}
	
	public static HashCodeBuilder hashCodeOf(final double value) {
		return new HashCodeBuilder(hashCode(value));
	}
	
	public static HashCodeBuilder hashCodeOf(final double[] values) {
		return new HashCodeBuilder(Arrays.hashCode(values));
	}
	
	public static HashCodeBuilder hashCodeOf(final Object object) {
		return new HashCodeBuilder(hashCode(object));
	}
	
	public static HashCodeBuilder hashCodeOf(final Object[] values) {
		return new HashCodeBuilder(Arrays.hashCode(values));
	}
	
	private static int hashCode(final boolean value) {
		return value ? 1231 : 1237;
	}
	
	private static int hashCode(final byte value) {
		return 31*value + 17;
	}
	
	private static int hashCode(final char value) {
		return 31*value + 17;
	}
	
	private static int hashCode(final short value) {
		return 31*value + 17;
	}
	
	private static int hashCode(final int value) {
		return 31*value + 17;
	}
	
	private static int hashCode(final long value) {
        return 31*(int)(value^(value >>> 32));
	}

	private static int hashCode(final float value) {
		return 31*Float.floatToIntBits(value);
	}
	
	private static int hashCode(final double value) {
		long bits = Double.doubleToLongBits(value);
		return (int)(bits^(bits >>> 32));
	}
	
	private static int hashCode(final Object value) {
		return 31*(value == null ? 0 : value.hashCode()) + 17;
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
	
	public static String asString(final Object a) {
		return a != null ? a.toString() : "null";
	}
	
}


