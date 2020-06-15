package io.jenetics.internal.util;

import java.math.BigInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class BitArray {

	private static final int[] BITS = {1, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80};

	final byte[] _data;
	private final int _start;
	private final int _end;

	BitArray(final byte[] data, final int start, final int end) {
		_data = data;
		_start = start;
		_end = end;
	}

	BitArray(final byte[] data) {
		this(data, 0, data.length*8);
	}

	public int length() {
		return _end - _start;
	}

	public void set(final int index, final boolean value) {
		Bits.set(_data, _start + index, value);
	}

	public boolean get(final int index) {
		return Bits.get(_data, _start + index);
	}

	public boolean isNegative() {
		return get(length() - 1);
	}

	public BigInteger toBigInteger() {
		final int byteLength = Bits.toByteLength(length());
		final byte[] array = Bits.newArray(length());

		for (int i = 0; i < length(); ++i) {
			Bits.set(array, i, get(i));
		}
		if (isNegative()) {
			for (int i = 0; i < array.length*8 - length(); ++i) {
				Bits.set(array, length() + i);
			}
		}
		Bits.reverse(array);
		return new BigInteger(array);
	}

	public static BitArray of(final BigInteger value, final int length) {
		final byte[] array = Bits.newArray(length);
		final byte[] data = value.toByteArray();

		Bits.reverse(data);
		if (value.signum() < 0) {
			java.util.Arrays.fill(array, (byte)-1);
		}
		for (int i = 0; i < Math.min(length, data.length*8); ++i) {
			Bits.set(array, i, Bits.get(data, i));
		}

//		if (byteLength > data.length) {
//			final byte[] temp = new byte[byteLength];
//			for (int i = 0; i < data.length; ++i) {
//				temp[temp.length - 1 - i] = data[data.length - 1 - i];
//			}
//			data = temp;
//		}
//
//		if (value.signum() < 0) {
//			final int bitLength = data.length*8;
//			if (length > bitLength) {
//				for (int i = 0; i < length - bitLength; ++i) {
//					Bits.set(data, bitLength + i);
//				}
//			} else {
//				for (int i = 0; i < bitLength - length; ++i) {
//					Bits.set(data, length + i);
//				}
//			}
//		}
//		Bits.reverse(data);

		return new BitArray(array, 0, length);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof BitArray &&
			obj.toString().equals(toString());
	}

	@Override
	public String toString() {
		return IntStream.range(0, length())
			.mapToObj(i -> get(length() - 1 - i) ? "1" : "0")
			.collect(Collectors.joining());
	}



	public static BitArray of(final CharSequence value) {
		final byte[] data = fromString(value);
		return new BitArray(data, 0, value.length());
	}

	private static byte[] fromString(final CharSequence ascii) {
		final byte[] array = Bits.newArray(ascii.length());

		for (int ii = 0, jj = ascii.length() - 1; ii < array.length; ii++, jj -= 8) {
			for (int bits = 0; bits < BITS.length && (jj - bits) >= 0; ++bits) {
				if (ascii.charAt(jj - bits) == '1') {
					array[ii] |= BITS[bits];
				}
			}
		}
		return array;
	}

	public static BitArray ofLength(final int length) {
		return new BitArray(Bits.newArray(length), 0, length);
	}

}
