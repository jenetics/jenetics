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

import static java.lang.Math.min;

import org.jscience.mathematics.number.LargeInteger;


/**
 * Some bit utils.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class bit {

	private bit() {
		throw new AssertionError("Don't create an 'bit' instance.");
	}
	
	public static byte[] toByteArray(final LargeInteger value) {
		final int byteLength = value.bitLength()/8 + 1;
		byte[] array = new byte[byteLength];
		value.toByteArray(array, 0);
		return array;
	}
	
	public static LargeInteger toLargeInteger(final byte[] array) {
		return LargeInteger.valueOf(array, 0, array.length);
	}

	/**
	 * Shifting all bits in the given <code>data</code> array the given 
	 * {@code shift} to the right. The bits on the left side are filled with 
	 * zeros. It is assumed the following array layout:
	 * <pre>
	 *  Byte:       3        2        1        0     
	 *              |        |        |        |  
	 *  Array: |11110011|10011101|01000000|00101010|
	 *          |                 |        |      |
	 *  Bit:    23                15       7      0
	 * </pre>
	 * 
	 * @param data the data bits to shift.
	 * @param shift the number of bits to shift.
	 * @return the given <code>data</code> array.
	 */
	public static byte[] shiftRight(final byte[] data, final int shift) {
		final int bytes = min(shift/8, data.length);
		final int bits = shift%8;
		
		if (bytes > 0) {
			for (int i = 0, n = data.length - bytes; i < n; ++i) {
				data[i] = data[i + bytes];
			}
			for (int i = data.length, n = data.length - bytes; --i >= n;) {
				data[i] = (byte)0;
			}
		}
		if (bits > 0 && bytes < data.length) {
			int carry = 0;
			int nextCarry = 0;
			
			for (int i = data.length; --i >= 0;) {
				int d = data[i] & 0xFF;
				nextCarry = (d << (8 - bits));
				
				d >>>= bits;
				d |= carry;
				data[i] = (byte)(d & 0xFF);
							
				carry = nextCarry;
			}
		}
		
		return data;
	}
	
	/**
	 * Shifting all bits in the given <code>data</code> array the given 
	 * {@code shift} to the left. The bits on the right side are filled with 
	 * zeros. It is assumed the following array layout:
	 * <pre>
	 *  Byte:       3        2        1        0     
	 *              |        |        |        |  
	 *  Array: |11110011|10011101|01000000|00101010|
	 *          |                 |        |      |
	 *  Bit:    23                15       7      0
	 * </pre>
	 * 
	 * @param data the data bits to shift.
	 * @param shift the number of bits to shift.
	 * @return the given <code>data</code> array.
	 */
	public static byte[] shiftLeft(final byte[] data, final int shift) {
		final int bytes = min(shift/8, data.length);
		final int bits = shift%8;
		
		if (bytes > 0) {
			for (int i = 0, n = data.length - bytes; i < n; ++i) {
				data[data.length - 1 - i] = data[data.length - 1 - i - bytes];
			}
			for (int i = 0; i < bytes; ++i) {
				data[i] = (byte)0;
			}
		}
		if (bits > 0 && bytes < data.length) {
			int carry = 0;
			int nextCarry = 0;
			
			for (int i = 0; i < data.length; ++i) {
				int d = data[i] & 0xFF;
				nextCarry = (d >>> (8 - bits));
				
				d <<= bits;
				d |= carry;
				data[i] = (byte)(d & 0xFF);
							
				carry = nextCarry;
			}
		}
		
		return data;
	}
	
	/**
	 * Increment the given <code>data</code> array.
	 * 
	 * @param data the given <code>data</code> array.
	 * @return the given <code>data</code> array.
	 */
	public static byte[] increment(final byte[] data) {
		if (data.length == 0) {
			return data;
		}
		
		int d = 0;
		int pos = data.length - 1;
		do {
			d = data[pos] & 0xFF;
			++d;
			data[pos] = (byte)d;
			--pos;
		} while (pos >= 0 && data[pos + 1] == 0);
		
		return data;
	}
	
	/**
	 * Invert the given <code>data</code> array.
	 * 
	 * @param data the given <code>data</code> array.
	 * @return the given <code>data</code> array.
	 */
	public static byte[] invert(final byte[] data)	{
		int d = 0;
		for (int i = 0; i < data.length; ++i) {
			d = data[i] & 0xFF;
			d = ~d;
			data[i] = (byte)d;
		}
		
		return data;
	}
	
	/**
	 * Make the two's complement of the given <code>data</code> array.
	 * 
	 * @param data the given <code>data</code> array.
	 * @return the given <code>data</code> array.
	 */
	public static byte[] complement(final byte[] data) {
		return increment(invert(data));
	}
	
	/**
	 * Set the bit in the given byte array at the bit position (not the index
	 * within the byte array) to the specified value.
	 * 
	 * @param data the byte array.
	 * @param index the bit index within the byte array.
	 * @param value the value to set.
	 * @return the given data array.
	 */
	public static byte[] setBit(final byte[] data, final int index, final boolean value) {
		if (data.length == 0) {
			return data;
		}
		
		final int max = data.length*8;
		if (index >= max || index < 0) {
			throw new IndexOutOfBoundsException("Index out of bounds: " + index);
		}
		
		final int pos = index/8;
		final int bitPos = index%8;
		
		int d = data[pos] & 0xFF;
		if (value) {
			d = d | (1 << bitPos);
		} else {
			d = d & ~(1 << bitPos);
		}
		data[pos] = (byte)d;
		
		return data;
	}
	
	/**
	 * Return the (boolean) value of the byte array at the given bit index.
	 * 
	 * @param data the byte array.
	 * @param index the bit index.
	 * @return the value at the given bit index.
	 */
	public static boolean getBit(final byte[] data, final int index) {
		if (data.length == 0) {
			return false;
		}
		
		final int max = data.length*8;
		if (index >= max || index < 0) {
			throw new IndexOutOfBoundsException("Index out of bounds: " + index);
		}
		
		final int pos = index/8;
		final int bitPos = index%8;
		final int d = data[pos] & 0xFF;
		return (d & (1 << bitPos)) != 0;
	}
	
	/**
	 * Flip the bit at the given index.
	 * 
	 * @param data the data array.
	 * @param index the index of the bit to flip.
	 */
	@Deprecated
	public static void flip(final byte[] data, final int index) {
		if (data.length == 0) {
			return;
		}
		
		final int max = data.length*8;
		if (index >= max || index < 0) {
			throw new IndexOutOfBoundsException("Index out of bounds: " + index);
		}
		
		//Reading the value.
		final int pos = data.length - index/8 - 1;
		final int bitPos = index%8;
		int d = data[pos] & 0xFF;
		final boolean value = (d & (1 << bitPos)) != 0;
		
		//Setting the value.
		if (value) {
			d = d | (1 << bitPos);
		} else {
			d = d & ~(1 << bitPos);
		}
		data[pos] = (byte)d;
	}
	
}




