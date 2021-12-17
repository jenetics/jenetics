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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.internal.collection;

import static java.lang.String.format;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

import io.jenetics.internal.util.Bits;
import io.jenetics.util.Copyable;

/**
 * This class represents a fixed sized array of <em>bit</em> or <em>boolean</em>
 * values, backed by a {@code byte[]} array. The order of the bit values is shown
 * if the drawing.
 * <pre> {@code
 *  Byte:       3        2        1        0
 *              |        |        |        |
 *  Array: |11110011|10011101|01000000|00101010|
 *          |                 |        |      |
 *  Bit:    23                15       7      0
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
public final class BitArray implements Copyable<BitArray> {

	private static final int[] BITS = {
		1, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80
	};

	private final byte[] _data;
	private final int _start;
	private final int _end;

	/**
	 * Create a new bit-array with the given {@code data} values and
	 * {@code begin} and {@code end} <em>bit</em> indexes.
	 *
	 * @param data the {@code byte[]} array which contains the bit data
	 * @param start the start bit index (inclusively)
	 * @param end the end bit index (exclusively)
	 * @throws NullPointerException if the given {@code data} array is
	 *         {@code null}
	 * @throws IllegalArgumentException if the {@code begin} and {@code end}
	 *         indexes are not within the valid range
	 */
	BitArray(final byte[] data, final int start, final int end) {
		if (data.length == 0) {
			throw new IllegalArgumentException("Byte array must not be empty.");
		}
		if (start < 0) {
			throw new IllegalArgumentException(
				"Begin index is smaller then zero: " + start
			);
		}
		if (end < start || end > data.length*Byte.SIZE) {
			throw new IllegalArgumentException(format(
				"End index is not within the valid range of [%d, %d]: %d",
				start, data.length*Byte.SIZE, end
			));
		}

		_data = data;
		_start = start;
		_end = end;
	}

	/**
	 * Create a new bit-array with the given {@code data} values.
	 *
	 * @param data the {@code byte[]} array which contains the bit data
	 * @throws NullPointerException if the given {@code data} array is
	 *         {@code null}
	 */
	BitArray(final byte[] data) {
		this(data, 0, data.length*Byte.SIZE);
	}

	/**
	 * Return the length of the bit-array.
	 *
	 * @return the length of the bit array
	 */
	public int length() {
		return _end - _start;
	}

	/**
	 * Return the number of set bits of this bit-array.
	 *
	 * @return the number of set bits
	 */
	public int bitCount() {
		return Bits.count(_data, _start,_end);
	}

	/**
	 * Sets the specified bit {@code value} at the given bit {@code index}.
	 *
	 * @param index the bit index
	 * @param value the bit value
	 * @throws IndexOutOfBoundsException if the index is not within the valid
	 *         range of {@code [0, length())}
	 */
	public void set(final int index, final boolean value) {
		Objects.checkIndex(index, length());
		Bits.set(_data, _start + index, value);
	}

	/**
	 * Set the bit in the given byte array at the bit position (not the index
	 * within the byte array) to {@code true}.
	 *
	 * @param index the bit index
	 * @throws IndexOutOfBoundsException if the index is not within the valid
	 *         range of {@code [0, length())}
	 */
	public void set(final int index) {
		Objects.checkIndex(index, length());
		Bits.set(_data, _start + index);
	}

	/**
	 * Set the bit in the given byte array at the bit position (not the index
	 * within the byte array) to {@code false}.
	 *
	 * @param index the bit index
	 * @throws IndexOutOfBoundsException if the index is not within the valid
	 *         range of {@code [0, length())}
	 */
	public void unset(final int index) {
		Objects.checkIndex(index, length());
		Bits.unset(_data, _start + index);
	}

	/**
	 * Return the bit value at the given bit {@code index}.
	 *
	 * @param index the bit index
	 * @return the bit value
	 * @throws IndexOutOfBoundsException if the index is not within the valid
	 *         range of {@code [0, length())}
	 */
	public boolean get(final int index) {
		Objects.checkIndex(index, length());
		return Bits.get(_data, _start + index);
	}

	/**
	 * Inverts {@code this} bit-array.
	 */
	public void invert() {
		Bits.invert(_data);
	}

	/**
	 * Return the signum of the number, represented by this bit-array (-1 for
	 * negative, 0 for zero, 1 for positive).
	 *
	 * <pre>{@code
	 * final BitArray bits = ...;
	 * final BigInteger i = bits.toBigInteger();
	 * assert bits.signum() == i.signum();
	 * }</pre>
	 *
	 * @return the signum of the number, represented by this bit-array (-1 for
	 * 	       negative, 0 for zero, 1 for positive)
	 */
	public int signum() {
		if (get(length() - 1)) {
			return -1;
		} else {
			return bitCount() == 0 ? 0 : 1;
		}
	}

	/**
	 * Return the value of this bit-array as {@link BigInteger} value. This
	 * bit-array can be recreated by the returned {@code BigInteger} value. But
	 * only with the same {@link #length()} of {@code this} bit-array.
	 *
	 * <pre>{@code
	 * final var bits = BitArray.of("1111111010100110010110110010011110110101");
	 * final var bint = bits.toBigInteger();
	 * assert BitArray.of(bint, bits.length()).equals(bits);
	 * }</pre>
	 *
	 * @see #of(BigInteger, int)
	 *
	 * @return a new {@code BigInteger} object, which represents the integer
	 *         value of this bit-array
	 */
	public BigInteger toBigInteger() {
		return new BigInteger(toTowsComplementByteArray());
	}

	/**
	 * Returns the two's complement binary representation of this
	 * big integer. The output array is in <i>big-endian</i>
	 * byte-order: the most significant byte is at the offset position.
	 *
	 * <p>Note: This representation is consistent with {@code java.lang.BigInteger}
	 * byte array representation and can be used for conversion between the two
	 * classes.</p>
	 *
	 * @return the two's complement byte array representation
	 * @throws IndexOutOfBoundsException
	 *         if {@code bytes.length < (int)Math.ceil(length()/8.0)}
	 * @throws NullPointerException it the give array is {@code null}.
	 */
	private byte[] toTowsComplementByteArray() {
		final byte[] array = toByteArray();
		if (get(length() - 1)) {
			for (int i = length(), n = array.length*Byte.SIZE; i < n; ++i) {
				Bits.set(array, i);
			}
		}
		Bits.reverse(array);
		return array;
	}

	/**
	 * Return the {@code byte[]} array, which represents the state of the state
	 * of {@code this} bit-array.
	 *
	 * <pre>{@code
	 * final BitArray bits = ...;
	 * final byte[] bytes = bits.toByteArray();
	 * assert bits.equals(BitArray.of(bytes, bits.length()));
	 * }</pre>
	 *
	 * @return the bit-array data as {@code byte[]} array
	 */
	public byte[] toByteArray() {
		return Bits.copy(_data, _start, _end);
	}

	/**
	 * Create a new copy of {@code this} bit-array.
	 *
	 * @return a new copy of {@code this} bit-array
	 */
	@Override
	public BitArray copy() {
		return new BitArray(toByteArray(), 0, length());
	}

	@Override
	public int hashCode() {
		return toBigInteger().hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof BitArray other && equals(other);
	}

	private boolean equals(final BitArray array) {
		if (array.length() != length()) {
			return false;
		}
		return Arrays.equals(toByteArray(), array.toByteArray());
	}

	@Override
	public String toString() {
		final char[] chars = new char[length()];
		for (int i = 0; i < chars.length; ++i) {
			chars[chars.length - 1 - i] = get(i) ? '1' : '0';
		}

		return new String(chars);
	}

	/**
	 * Convert a binary representation of {@code this} bit-array to a string. The
	 * string has the following format:
	 * <pre>
	 *  Byte:       3        2        1        0
	 *              |        |        |        |
	 *  Array: "11110011|10011101|01000000|00101010"
	 *          |                 |        |      |
	 *  Bit:    23                15       7      0
	 * </pre>
	 *
	 * @return the binary representation of {@code this} bit array.
	 */
	public String toByteString() {
		return Bits.toByteString(toByteArray());
	}

	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	/**
	 * Creates a new bit-array from the given {@code value} and the given
	 * {@code length}. It is guaranteed, that the created bit-array will
	 * represent the given {@link BigInteger}, as long as the {@code length}
	 * is big enough to store the whole value. If the length is shorter then
	 * required, the higher order bits will be truncated.
	 *
	 * <pre>{@code
	 * final var length = 2048;
	 * final var bint = BigInteger.probablePrime(length, new Random());
	 * final var bits = BitArray.of(bint, length + 1);
	 * assert bits3.toBigInteger().equals(bint);
	 * }</pre>
	 *
	 * @see #toBigInteger()
	 *
	 * @param value the integer value
	 * @param length the length of the created bit-array
	 * @return a newly created bit-array which represent the given {@code value}
	 * @throws NullPointerException if the given {@code value} is {@code null}
	 * @throws NegativeArraySizeException if the {@code length} is negative
	 */
	public static BitArray of(final BigInteger value, final int length) {
		final byte[] array = Bits.newArray(length);
		final byte[] data = value.toByteArray();

		Bits.reverse(data);
		if (value.signum() < 0) {
			array[array.length - 1] = (byte)-1;
		}
		System.arraycopy(data, 0, array, 0, data.length);

		return new BitArray(array, 0, length);
	}

	/**
	 * Create a new bit-array from the given {@link BigInteger} value.
	 *
	 * @see #of(BigInteger, int)
	 *
	 * @param value the integer value
	 * @return a newly created bit-array which represent the given {@code value}
	 * @throws NullPointerException if the given {@code value} is {@code null}
	 */
	public static BitArray of(final BigInteger value) {
		final byte[] data = value.toByteArray();
		Bits.reverse(data);
		return new BitArray(data, 0, data.length*Byte.SIZE);
	}

	/**
	 * Creates a new bit-array from the given string {@code value}. The given
	 * {@code length} might be bigger and smaller than the length of the given
	 * {@code value} string. The higher order bits of the created bit-array are
	 * trimmed or filled with zero if the {@code length} is smaller or bigger
	 * than the given string.
	 *
	 * @see #of(CharSequence)
	 * @see #toString()
	 *
	 * @param value the given input string, consisting only of '0's and '1's
	 * @param length the length of the created bit-array
	 * @return a new bit-array from the given input {@code value}
	 * @throws IllegalArgumentException if the given input {@code value} is
	 *         empty
	 */
	public static BitArray of(final CharSequence value, final int length) {
		final byte[] data = toByteArray(value, length);
		return new BitArray(data, 0, length);
	}

	private static byte[] toByteArray(final CharSequence chars, final int length) {
		final byte[] array = Bits.newArray(length);
		for (int i = 0, j = length - 1; i < array.length; i++, j -= Byte.SIZE) {
			for (int bits = 0; bits < BITS.length && (j - bits) >= 0; ++bits) {
				if (get(chars, j - bits, length) == '1') {
					array[i] |= BITS[bits];
				}
			}
		}
		return array;
	}

	private static char get(final CharSequence chars, final int i, final int length) {
		if (chars.length() < length) {
			final int d = length - i;
			return d <= chars.length() ? chars.charAt(chars.length() - d) : '0';
		} else if (chars.length() > length) {
			return chars.charAt(chars.length() - length  + i);
		} else {
			return chars.charAt(i);
		}
	}

	/**
	 * Creates a new bit-array from the given string {@code value}. The string,
	 * created by the {@link #toString()} method, will be equals to the given
	 * input {@code value}.
	 *
	 * <pre>{@code
	 * final var string = "11111110101001100101101100100111101101011101";
	 * final var bits = BitArray.of(string);
	 * assert bits.toString().equals(string);
	 * }</pre>
	 *
	 * @see #toString()
	 *
	 * @param value the given input string, consisting only of '0's and '1's
	 * @return a new bit-array from the given input {@code value}
	 * @throws IllegalArgumentException if the given input {@code value} is
	 *         empty
	 */
	public static BitArray of(final CharSequence value) {
		return of(value, value.length());
	}

	/**
	 * Create a new bit-array with the given {@code data} values and
	 * {@code begin} and {@code end} <em>bit</em> indexes. The given
	 * {@code data} is copied.
	 *
	 * @param data the {@code byte[]} array which contains the bit data
	 * @param begin the start bit index (inclusively)
	 * @param end the end bit index (exclusively)
	 * @return a newly created bit-array
	 * @throws NullPointerException if the given {@code data} array is
	 *         {@code null}
	 * @throws IllegalArgumentException if the {@code begin} and {@code end}
	 *         indexes are not within the valid range
	 */
	public static BitArray of(final byte[] data, final int begin, final int end) {
		final var bytes = Bits.copy(data, begin, end);
		return new BitArray(bytes, 0, end - begin);
	}

	/**
	 * Create a new bit-array with the given {@code data} values and
	 * {@code length}. The given {@code data} is copied.
	 *
	 * @param data the {@code byte[]} array which contains the bit data
	 * @param length the bit length
	 * @return a newly created bit-array
	 * @throws NullPointerException if the given {@code data} array is
	 *         {@code null}
	 * @throws IllegalArgumentException if the {@code length} is greater than
	 *         {@code data.length*Byte.SIZE}
	 */
	public static BitArray of(final byte[] data, final int length) {
		return of(data, 0, length);
	}

	/**
	 * Create a new bit-array with the given {@code data} values and
	 * {@code length}. The given {@code data} is copied.
	 *
	 * @param data the {@code byte[]} array which contains the bit data
	 * @return a newly created bit-array
	 * @throws NullPointerException if the given {@code data} array is
	 *         {@code null}
	 */
	public static BitArray of(final byte[] data) {
		return of(data, 0, data.length*Byte.SIZE);
	}

	/**
	 * Crate a new bit-array with the given length. All bits are set to '0'.
	 *
	 * @param length the length of the bit-array
	 * @return a newly created bit-array with the given {@code length}
	 * @throws IllegalArgumentException if the given {@code length} is smaller
	 *         then one
	 */
	public static BitArray ofLength(final int length) {
		return new BitArray(Bits.newArray(length), 0, length);
	}

	/**
	 * Create a new bit-array which can store at least the number
	 * of bits as defined by the given {@code length} parameter. The returned
	 * byte array is initialized with ones according to the given ones
	 * probability {@code p}.
	 *
	 * @param length the number of bits, the returned bit-array can store.
	 * @param p the ones probability of the returned byte array.
	 * @return the new byte array.s
	 * @throws IllegalArgumentException if {@code p} is not a valid probability.
	 */
	public static BitArray ofLength(final int length, final double p) {
		return new BitArray(Bits.newArray(length, p), 0, length);
	}

}
