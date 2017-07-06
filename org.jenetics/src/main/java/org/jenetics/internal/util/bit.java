/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.internal.util;

import static java.lang.Integer.parseInt;
import static java.lang.Math.min;

import org.jenetics.internal.math.random;

import org.jenetics.util.RandomRegistry;


/**
 * Some bit utils. All operation assume <a href="http://en.wikipedia.org/wiki/Endianness">
 * <b>little-endian</b></a> byte order.
 *
 * <pre>
 *  Byte:       3        2        1        0
 *              |        |        |        |
 *  Array: |11110011|10011101|01000000|00101010|
 *          |                 |        |      |
 *  Bit:    23                15       7      0
 * </pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.0
 */
public final class bit {
	private bit() {require.noInstance();}

	/**
	 * Lookup table for counting the number of set bits in a {@code byte} value.
	 */
	private static final byte[] BIT_SET_TABLE = {
		(byte)1, (byte)2, (byte)2, (byte)3, (byte)2, (byte)3, (byte)3, (byte)4,
		(byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5,
		(byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5,
		(byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6,
		(byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5,
		(byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6,
		(byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6,
		(byte)4, (byte)5, (byte)5, (byte)6, (byte)5, (byte)6, (byte)6, (byte)7,
		(byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5,
		(byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6,
		(byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6,
		(byte)4, (byte)5, (byte)5, (byte)6, (byte)5, (byte)6, (byte)6, (byte)7,
		(byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6,
		(byte)4, (byte)5, (byte)5, (byte)6, (byte)5, (byte)6, (byte)6, (byte)7,
		(byte)4, (byte)5, (byte)5, (byte)6, (byte)5, (byte)6, (byte)6, (byte)7,
		(byte)5, (byte)6, (byte)6, (byte)7, (byte)6, (byte)7, (byte)7, (byte)8,
		(byte)0, (byte)1, (byte)1, (byte)2, (byte)1, (byte)2, (byte)2, (byte)3,
		(byte)1, (byte)2, (byte)2, (byte)3, (byte)2, (byte)3, (byte)3, (byte)4,
		(byte)1, (byte)2, (byte)2, (byte)3, (byte)2, (byte)3, (byte)3, (byte)4,
		(byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5,
		(byte)1, (byte)2, (byte)2, (byte)3, (byte)2, (byte)3, (byte)3, (byte)4,
		(byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5,
		(byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5,
		(byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6,
		(byte)1, (byte)2, (byte)2, (byte)3, (byte)2, (byte)3, (byte)3, (byte)4,
		(byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5,
		(byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5,
		(byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6,
		(byte)2, (byte)3, (byte)3, (byte)4, (byte)3, (byte)4, (byte)4, (byte)5,
		(byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6,
		(byte)3, (byte)4, (byte)4, (byte)5, (byte)4, (byte)5, (byte)5, (byte)6,
		(byte)4, (byte)5, (byte)5, (byte)6, (byte)5, (byte)6, (byte)6, (byte)7
	};
	private static final int BIT_SET_TABLE_INDEX_OFFSET = 128;

	/**
	 * Return the (boolean) value of the byte array at the given bit index.
	 *
	 * @param data the byte array.
	 * @param index the bit index.
	 * @return the value at the given bit index.
	 * @throws IndexOutOfBoundsException if the index is
	 *          {@code index >= max || index < 0}.
	 * @throws NullPointerException if the {@code data} array is {@code null}.
	 */
	public static boolean get(final byte[] data, final int index) {
		return (data[index >>> 3] & (1 << (index & 7))) != 0;
	}

	/**
	 * Set the bit in the given byte array at the bit position (not the index
	 * within the byte array) to the specified value.
	 *
	 * @param data the byte array.
	 * @param index the bit index within the byte array.
	 * @param value the value to set.
	 * @return the given data array.
	 * @throws IndexOutOfBoundsException if the index is
	 *         {@code index >= max || index < 0}.
	 * @throws NullPointerException if the {@code data} array is {@code null}.
	 */
	public static byte[] set(
		final byte[] data,
		final int index,
		final boolean value
	) {
		return value ? set(data, index) : unset(data, index);
	}

	/**
	 * Set the bit in the given byte array at the bit position (not the index
	 * within the byte array) to {@code true}.
	 *
	 * @param data the byte array.
	 * @param index the bit index within the byte array.
	 * @return the given data array.
	 * @throws IndexOutOfBoundsException if the index is
	 *          {@code index >= max || index < 0}.
	 * @throws NullPointerException if the {@code data} array is {@code null}.
	 */
	public static byte[] set(final byte[] data, final int index) {
		data[index >>> 3] |= 1 << (index & 7);
		return data;
	}

	/**
	 * Set the bit in the given byte array at the bit position (not the index
	 * within the byte array) to {@code false}.
	 *
	 * @param data the byte array.
	 * @param index the bit index within the byte array.
	 * @return the given data array.
	 * @throws IndexOutOfBoundsException if the index is
	 *          {@code index >= max || index < 0}.
	 * @throws NullPointerException if the {@code data} array is {@code null}.
	 */
	public static byte[] unset(final byte[] data, final int index) {
		data[index >>> 3] &= ~(1 << (index & 7));
		return data;
	}

	/**
	 * Swap a given range with a range of the same size with another array.
	 *
	 * <pre>
	 *                start            end
	 *                  |               |
	 * data:      +---+---+---+---+---+---+---+---+---+---+---+---+
	 *              +---------------+
	 *                              +---------------+
	 * otherData: +---+---+---+---+---+---+---+---+---+---+---+---+
	 *                              |
	 *                          otherStart
	 * </pre>
	 *
	 * @param data the first byte array which are used for swapping.
	 * @param start the start bit index of the {@code data} byte array,
	 *        inclusively.
	 * @param end the end bit index of the {@code data} byte array, exclusively.
	 * @param otherData the other byte array to swap the elements with.
	 * @param otherStart the start index of the {@code otherData} byte array.
	 * @throws IndexOutOfBoundsException if {@code start > end} or
	 *         if {@code start < 0 || end >= data.length*8 || otherStart < 0 ||
	 *         otherStart + (end - start) >= otherData.length*8}
	 */
	public static void swap(
		final byte[] data, final int start, final int end,
		final byte[] otherData, final int otherStart
	) {
		for (int i = end - start; --i >= 0;) {
			final boolean temp = get(data, i + start);
			set(data, i + start, get(otherData, otherStart + i));
			set(otherData, otherStart + i, temp);
		}
	}

	/**
	 * Returns the number of one-bits in the given {@code byte} array.
	 *
	 * @param data the {@code byte} array for which the one bits should be
	 *        counted.
	 * @return the number of one bits in the given {@code byte} array.
	 */
	public static int count(final byte[] data) {
		int count = 0;
		for (int i = data.length; --i >= 0;) {
			count += count(data[i]);
		}
		return count;
	}

	/**
	 * Returns the number of one-bits in the given {@code byte} {@code value}.
	 *
	 * @param value the value for which the one bits should be counted.
	 * @return the number of one bits in the given value
	 */
	public static int count(final byte value) {
		return BIT_SET_TABLE[value + BIT_SET_TABLE_INDEX_OFFSET];
	}

	/**
	 * Shifting all bits in the given {@code data} array the given
	 * {@code shift} to the right. The bits on the left side are filled with
	 * zeros.
	 *
	 * @param data the data bits to shift.
	 * @param shift the number of bits to shift.
	 * @return the given {@code data} array.
	 * @throws NullPointerException if the {@code data} array is {@code null}.
	 */
	public static byte[] shiftRight(final byte[] data, final int shift) {
		final int bytes = min(shift >>> 3, data.length);
		final int bits = shift & 7;

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
				nextCarry = d << (Byte.SIZE - bits);

				d >>>= bits;
				d |= carry;
				data[i] = (byte)(d & 0xFF);

				carry = nextCarry;
			}
		}

		return data;
	}

	/**
	 * Shifting all bits in the given {@code data} array the given
	 * {@code shift} to the left. The bits on the right side are filled with
	 * zeros.
	 *
	 * @param data the data bits to shift.
	 * @param shift the number of bits to shift.
	 * @return the given {@code data} array.
	 * @throws NullPointerException if the {@code data} array is {@code null}.
	 */
	public static byte[] shiftLeft(final byte[] data, final int shift) {
		final int bytes = min(shift >>> 3, data.length);
		final int bits = shift & 7;

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

			for (int i = bytes; i < data.length; ++i) {
				int d = data[i] & 0xFF;
				nextCarry = d >>> (Byte.SIZE - bits);

				d <<= bits;
				d |= carry;
				data[i] = (byte)(d & 0xFF);

				carry = nextCarry;
			}
		}

		return data;
	}

	/**
	 * Increment the given {@code data} array.
	 *
	 * @param data the given {@code data} array.
	 * @return the given {@code data} array.
	 * @throws NullPointerException if the {@code data} array is {@code null}.
	 */
	public static byte[] increment(final byte[] data) {
		boolean carry = true;
		for (int i = 0; i < data.length && carry; ++i) {
			data[i] = (byte)(data[i] + 1);
			carry = data[i] > 0xFF;
		}

		return data;
	}

	/**
	 * Invert the given {@code data} array.
	 *
	 * @param data the given {@code data} array.
	 * @return the given {@code data} array.
	 * @throws NullPointerException if the {@code data} array is {@code null}.
	 */
	public static byte[] invert(final byte[] data)	{
		for (int i = data.length; --i >= 0;) {
			data[i] = (byte)~data[i];
		}
		return data;
	}

	/**
	 * Make the two's complement of the given {@code data} array.
	 *
	 * @param data the given {@code data} array.
	 * @return the given {@code data} array.
	 * @throws NullPointerException if the {@code data} array is {@code null}.
	 */
	public static byte[] complement(final byte[] data) {
		return increment(invert(data));
	}

	/**
	 * Flip the bit at the given index.
	 *
	 * @param data the data array.
	 * @param index the index of the bit to flip.
	 * @return the input array, for command chaining
	 * @throws IndexOutOfBoundsException if the index is
	 *          {@code index >= max || index < 0}.
	 * @throws NullPointerException if the {@code data} array is {@code null}.
	 */
	public static byte[] flip(final byte[] data, final int index) {
		return get(data, index) ? unset(data, index) : set(data, index);
	}

	public static byte[] reverse(final byte[] array) {
		int i = 0;
		int j = array.length;

		while (i < j) {
			swap(array, i++, --j);
		}

		return array;
	}

	private static void swap(final byte[] array, final int i, final int j) {
		final byte temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	/**
	 * Copies the specified range of the specified array into a new array.
	 *
	 * @param data the bits from which a range is to be copied
	 * @param start the initial index of the range to be copied, inclusive
	 * @param end the final index of the range to be copied, exclusive.
	 * @return a new array containing the specified range from the original array
	 * @throws ArrayIndexOutOfBoundsException if start &lt; 0 or
	 *         start &gt; data.length*8
	 * @throws IllegalArgumentException if start &gt; end
	 * @throws NullPointerException if the {@code data} array is
	 *         {@code null}.
	 */
	public static byte[] copy(final byte[] data, final int start, final int end) {
		if (start > end) {
			throw new IllegalArgumentException(String.format(
				"start > end: %d > %d", start, end
			));
		}
		if (start < 0 || start > data.length << 3) {
			throw new ArrayIndexOutOfBoundsException(String.format(
				"%d < 0 || %d > %d", start, start, data.length*8
			));
		}

		final int to = min(data.length << 3, end);
		final int byteStart = start >>> 3;
		final int bitStart = start & 7;
		final int bitLength = to - start;

		final byte[] copy = new byte[toByteLength(to - start)];

		if (copy.length > 0) {
			// Perform the byte wise right shift.
			System.arraycopy(data, byteStart, copy, 0, copy.length);

			// Do the remaining bit wise right shift.
			shiftRight(copy, bitStart);

			// Add the 'lost' bits from the next byte, if available.
			if (data.length > copy.length + byteStart) {
				copy[copy.length - 1] |= (byte)(data[byteStart + copy.length]
					<< (Byte.SIZE - bitStart));
			}

			// Trim (delete) the overhanging bits.
			copy[copy.length - 1] &= 0xFF >>> ((copy.length << 3) - bitLength);
		}

		return copy;
	}

	public static boolean getAndSet(final byte[] array, final int index) {
		final boolean result = get(array, index);
		set(array, index);
		return result;
	}

	/**
	 * Convert a binary representation of the given byte array to a string. The
	 * string has the following format:
	 * <pre>
	 *  Byte:       3        2        1        0
	 *              |        |        |        |
	 *  Array: "11110011|10011101|01000000|00101010"
	 *          |                 |        |      |
	 *  Bit:    23                15       7      0
	 * </pre>
	 * <i>Only the array string is printed.</i>
	 *
	 * @see #fromByteString(String)
	 *
	 * @param data the byte array to convert to a string.
	 * @return the binary representation of the given byte array.
	 */
	public static String toByteString(final byte... data) {
		final StringBuilder out = new StringBuilder();

		if (data.length > 0) {
			for (int j = 7; j >= 0; --j) {
				out.append((data[data.length - 1] >>> j) & 1);
			}
		}
		for (int i = data.length - 2; i >= 0 ;--i) {
			out.append('|');
			for (int j = 7; j >= 0; --j) {
				out.append((data[i] >>> j) & 1);
			}
		}

		return out.toString();
	}

	/**
	 * Convert a string which was created with the {@link #toByteString(byte...)}
	 * method back to an byte array.
	 *
	 * @see #toByteString(byte...)
	 *
	 * @param data the string to convert.
	 * @return the byte array.
	 * @throws IllegalArgumentException if the given data string could not be
	 *          converted.
	 */
	 public static byte[] fromByteString(final String data) {
		final String[] parts = data.split("\\|");
		final byte[] bytes = new byte[parts.length];

		for (int i = 0; i < parts.length; ++i) {
			if (parts[i].length() != Byte.SIZE) {
				throw new IllegalArgumentException(
					"Byte value doesn't contain 8 bit: " + parts[i]
				);
			}

			try {
				bytes[parts.length - 1 - i] = (byte)parseInt(parts[i], 2);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(e);
			}
		}

		return bytes;
	}

	/**
	 * Create a new {@code byte[]} array which can store at least the number
	 * of bits as defined by the given {@code length} parameter.
	 *
	 * @param length the number of bits, the returned byte array can store.
	 * @return the new byte array.s
	 */
	public static byte[] newArray(final int length) {
		return new byte[toByteLength(length)];
	}

	/**
	 * Create a new {@code byte[]} array which can store at least the number
	 * of bits as defined by the given {@code length} parameter. The returned
	 * byte array is initialized with ones according to the given ones
	 * probability {@code p}.
	 *
	 * @param length the number of bits, the returned byte array can store.
	 * @param p the ones probability of the returned byte array.
	 * @return the new byte array.s
	 * @throws IllegalArgumentException if {@code p} is not a valid probability.
	 */
	public static byte[] newArray(final int length, final double p) {
		final byte[] bytes = newArray(length);

		random.indexes(RandomRegistry.getRandom(), length, p)
			.forEach(i -> bytes[i >>> 3] |= 1 << (i & 7));

		return bytes;
	}

	/**
	 * Return the minimum number of bytes to store the given number of bits.
	 *
	 * @param bitLength the number of bits
	 * @return the number of bytes needed to store the given number of bits.
	 */
	public static int toByteLength(final int bitLength) {
		return (bitLength & 7) == 0 ? (bitLength >>> 3) : (bitLength >>> 3) + 1;
	}

	public static long toLong(final byte[] data) {
		return
			((long)data[0] << 56) +
			((long)(data[1] & 255) << 48) +
			((long)(data[2] & 255) << 40) +
			((long)(data[3] & 255) << 32) +
			((long)(data[4] & 255) << 24) +
			((data[5] & 255) << 16) +
			((data[6] & 255) <<  8) +
			(data[7] & 255);
	}

	public static byte[] toBytes(final long value) {
		final byte[] bytes = new byte[8];
		bytes[0] = (byte)(value >>> 56);
		bytes[1] = (byte)(value >>> 48);
		bytes[2] = (byte)(value >>> 40);
		bytes[3] = (byte)(value >>> 32);
		bytes[4] = (byte)(value >>> 24);
		bytes[5] = (byte)(value >>> 16);
		bytes[6] = (byte)(value >>>  8);
		bytes[7] = (byte) value;
		return bytes;
	}

}
