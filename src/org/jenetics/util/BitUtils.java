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

import org.jscience.mathematics.number.LargeInteger;


/**
 * Some bit utils.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: BitUtils.java,v 1.2 2008-08-29 21:18:15 fwilhelm Exp $
 */
public final class BitUtils {

	private BitUtils() {
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
	 * Shifting all bits in the given <code>data</code> array the given <code>bits</code>
	 * to the right. The bits on the left side are filled with zeros.
	 * 
	 * @param data the data bits to shift.
	 * @param bits the number of bits to shift.
	 * @return the given <code>data</code> array.
	 */
	public static byte[] shiftRight(final byte[] data, final int bits) {
		if (bits <= 0) {
			return data;
		}
		
		int d = 0;
		if (data.length == 1) {
			if (bits <= 8) {
				d = data[0] & 0xFF;
				d >>>= bits;
				data[0] = (byte)d;
			} else {
				data[0] = 0;
			}
		} else if (data.length > 1) {
			int carry = 0;
			
			if (bits < 8) {
				for (int i = data.length - 1; i > 0; --i) {
					carry = data[i - 1] & (1 << (bits - 1));
					carry = carry << (8 - bits);
					
					d = data[i] & 0xFF;
					d >>>= bits;
					d |= carry;
	
					data[i] = (byte)d;
				}
				
				d = data[0] & 0xFF;
				d >>>= bits;
	
				data[0] = (byte)d ;
			} else {
				for (int i = data.length - 1; i > 0; --i) {
					data[i] = data[i - 1];
				}
				data[0] = 0;
				shiftRight(data, bits - 8);
			}
		}
		
		return data;
	}
	
	/**
	 * Shifting all bits in the given <code>data</code> array the given <code>bits</code>
	 * to the left. The bits on the right side are filled with zeros.
	 * 
	 * @param data the data bits to shift.
	 * @param bits the number of bits to shift.
	 * @return the given <code>data</code> array.
	 */
	public static byte[] shiftLeft(final byte[] data, final int bits) {
		if (bits <= 0) {
			return data;
		}
		
		int d = 0;
		if (data.length == 1) {
			if (bits <= 8) {
				d = data[0] & 0xFF;
				d <<= bits;
				data[0] = (byte)d;
			} else {
				data[0] = 0;
			}
		} else if (data.length > 1) {
			int carry = 0;
			
			if (bits < 8) {
				for (int i = 0; i < data.length - 1; ++i) {
					carry = data[i + 1] & (1 >>> (8 - bits));
					
					d = data[i] & 0xFF;
					d <<= bits;
					d |= carry;
	
					data[i] = (byte)d;
				}
				
				d = data[data.length - 1] & 0xFF;
				d <<= bits;
	
				data[data.length - 1] = (byte)d ;
			} else {
				for (int i = 0; i < data.length - 1; ++i) {
					data[i] = data[i + 1];
				}
				data[data.length - 1] = 0;
				shiftLeft(data, bits - 8);
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
	public static byte[] invert(final byte[] data)  {
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
		
		final int MAX = data.length*8;
		if (index >= MAX || index < 0) {
			throw new IndexOutOfBoundsException("Index out of bounds: " + index);
		}
		
		final int pos = data.length - index/8 - 1;
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
		
		final int MAX = data.length*8;
		if (index >= MAX || index < 0) {
			throw new IndexOutOfBoundsException("Index out of bounds: " + index);
		}
		
		final int pos = data.length - index/8 - 1;
		final int bitPos = index%8;
		final int d = data[pos] & 0xFF;
		return (d & (1 << bitPos)) != 0;
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
	 *    double a = 0.0;
	 *    for (int i = 0; i < 10; ++i) {
	 *        a = Math.nextAfter(a, Double.POSITIVE_INFINITY);
	 *    }
	 *
	 *    for (int i = 0; i < 19; ++i) {
	 *        a = Math.nextAfter(a, Double.NEGATIVE_INFINITY);
	 *        System.out.println(
	 *            a + "\t" + ulpPosition(a) + "\t" + ulpDistance(0.0, a)
	 *        );
	 *     }
	 * [/code]
	 * 
	 * The code fragment above will create the following output:
	 * <pre>
	 *     4.4E-323    9    9
	 *     4.0E-323    8    8
	 *     3.5E-323    7    7
	 *     3.0E-323    6    6
	 *     2.5E-323    5    5
	 *     2.0E-323    4    4
	 *     1.5E-323    3    3
	 *     1.0E-323    2    2
	 *     4.9E-324    1    1
	 *     0.0         0    0
	 *    -4.9E-324   -1    1
	 *    -1.0E-323   -2    2
	 *    -1.5E-323   -3    3
	 *    -2.0E-323   -4    4
	 *    -2.5E-323   -5    5
	 *    -3.0E-323   -6    6
	 *    -3.5E-323   -7    7
	 *    -4.0E-323   -8    8
	 *    -4.4E-323   -9    9
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
	
	public static String toString(final long n) {
		final StringBuilder out = new StringBuilder();
		for (int i = 63; i >= 0; --i) {
			out.append((n >>> i) & 1);
		}
		return out.toString();
	}
	
	public static String toString(final byte... data) {
		final StringBuilder out = new StringBuilder();
		
		if (data.length > 0) {
			for (int j = 7; j >= 0; --j) {
				out.append((data[0] >>> j) & 1);
			}
		}
		for (int i = 1; i < data.length; ++i) {
			out.append('|');
			for (int j = 7; j >= 0; --j) {
				out.append((data[i] >>> j) & 1);
			}
		}

		return out.toString();
	}
	
	/**
	 * Convert a string which was created with the {@link #toString(byte...)}
	 * method back to an byte array.
	 * 
	 * @param data the string to convert.
	 * @return the byte array.
	 * @throws IllegalArgumentException if the given data string could not be
	 *         converted.
	 */
	public static byte[] toByteArray(final String data) {
		final String[] parts = data.split("\\|");
		final byte[] bytes = new byte[parts.length];
		
		for (int i = 0; i < parts.length; ++i) {
			if (parts[i].length() != 8) {
				throw new IllegalArgumentException(
					"Byte value doesn't contain 8 bit: " + parts[i]
				);
			} else {
				try {
					bytes[i] = (byte)Integer.parseInt(parts[i], 2);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
		
		return bytes;
	}
	
	
	
	/*
	 * The following methods are copied from the Apache.Commons.Math library.
	 * The copied methods was 
	 * - org.apache.commons.math.util.MathUtil.addAndCheck(long, long, String) and
	 * - org.apache.commons.math.util.MathUtil.subAndCheck(long, long)
	 * *************************************************************************
	 * 
	 * Licensed to the Apache Software Foundation (ASF) under one or more
	 * contributor license agreements.  See the NOTICE file distributed with
	 * this work for additional information regarding copyright ownership.
	 * The ASF licenses this file to You under the Apache License, Version 2.0
	 * (the "License"); you may not use this file except in compliance with
	 * the License.  You may obtain a copy of the License at
	 *
	 *      http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS,
	 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 * See the License for the specific language governing permissions and
	 * limitations under the License.
	 */	
	private static long add(final long a, final long b) {
		long sum = 0;
		if (a > b) {
			// use symmetry to reduce boundry cases
			sum = add(b, a);
		} else {
			assert a <= b;
			
			if (a < 0) {
				if (b < 0) {
					// check for negative overflow
					if (Long.MIN_VALUE - b <= a) {
						sum = a + b;
					} else {
						throw new ArithmeticException();
					}
				} else {
					// opposite sign addition is always safe
					sum = a + b;
				}
			} else {
				assert a >= 0;
				assert b >= 0;

				// check for positive overflow
				if (a <= Long.MAX_VALUE - b) {
					sum = a + b;
				} else {
					throw new ArithmeticException();
				}
			}
		}
		return sum;
	}	
	
	private static long sub(final long a, final long b) {
		long ret = 0;
		if (b == Long.MIN_VALUE) {
			if (a < 0) {
				ret = a - b;
			} else {
				throw new ArithmeticException();
			}
		} else {
			// use additive inverse
			ret = add(a, -b);
		}
		return ret;
	}    
	
}




