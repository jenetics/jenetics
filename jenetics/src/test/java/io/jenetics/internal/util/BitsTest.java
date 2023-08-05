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
package io.jenetics.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.internal.math.Randoms;
import io.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class BitsTest {

	@Test(dataProvider = "byteStrData")
	public void byteStr(final byte[] data, final String result) {
		Assert.assertEquals(Bits.toByteString(data), result);
	}

	@DataProvider(name = "byteStrData")
	public Object[][] byteStrData() {
		return new Object[][] {
			{ new byte[]{(byte)0}, "00000000" },
			{ new byte[]{(byte)1}, "00000001" },
			{ new byte[]{(byte)2}, "00000010" },
			{ new byte[]{(byte)4}, "00000100" },
			{ new byte[]{(byte)0xFF}, "11111111" },

			{ new byte[]{(byte)0, (byte)0}, "00000000|00000000" },
			{ new byte[]{(byte)1, (byte)0}, "00000000|00000001" },
			{ new byte[]{(byte)0, (byte)1}, "00000001|00000000" },
			{ new byte[]{(byte)1, (byte)1}, "00000001|00000001" },

			{ Bits.toBytes(-5165661323090255963L),
				"10100101|00011111|00111011|00111111|01100101|11100010|01001111|10111000" },
			{ Bits.toBytes(-3111444787550306452L),
				"01101100|10111111|11010010|01101011|01110011|11101101|11010001|11010100" },
			{ Bits.toBytes(-3303191740454820247L),
				"01101001|10100110|00101111|11110101|10011100|10110100|00101000|11010010" },
			{ Bits.toBytes(4795980783582945410L),
				"10000010|10100000|00001111|11001011|00011100|10111111|10001110|01000010" },
			{ Bits.toBytes(5363121614382394644L),
				"00010100|01101101|01111011|01111000|10110001|10100010|01101101|01001010" },
			{ Bits.toBytes(-8185663930382162219L),
				"11010101|11100110|11010111|01011010|00000110|10101110|01100110|10001110" },
			{ Bits.toBytes(-1232621285458438758L),
				"10011010|00010101|10101010|10111001|01111000|11011001|11100100|11101110" },
			{ Bits.toBytes(-2081775369634963197L),
				"00000011|00100001|11000000|10111110|01010100|00001100|00011100|11100011" },
			{ Bits.toBytes(-2194074334834473370L),
				"01100110|11000110|01100011|01100101|00000100|00010101|10001101|11100001" },
			{ Bits.toBytes(7950010868533801327L),
				"01101111|10110001|01110011|10010011|11000011|00011100|01010100|01101110" },
			{ Bits.toBytes(6680935979511658057L),
				"01001001|00110110|11101011|01010001|11100000|01110011|10110111|01011100" },
			{ Bits.toBytes(-2670808837407163052L),
				"01010100|00111101|01011111|01001111|10000011|01100001|11101111|11011010" },
			{ Bits.toBytes(4167160717303874479L),
				"10101111|00000011|11011100|00000100|10011000|10111010|11010100|00111001" },
			{ Bits.toBytes(-4513322218647029476L),
				"00011100|01111001|10101010|11010000|01011010|01110101|01011101|11000001" },
			{ Bits.toBytes(564299592671873811L),
				"00010011|11001011|00011010|01100000|01111101|11001011|11010100|00000111" },
			{ Bits.toBytes(5256495800767342066L),
				"11110010|11100101|00010001|10101000|00010100|11010011|11110010|01001000" },
			{ Bits.toBytes(-6440333658299846476L),
				"10110100|10110000|00001011|11111110|10101000|01010110|10011111|10100110" },
			{ Bits.toBytes(8415309172805358741L),
				"10010101|01010100|00100110|01000000|00011011|00101111|11001001|01110100" },
			{ Bits.toBytes(-9216328290938433144L),
				"10001000|01010001|00111011|11100101|00111111|00000110|00011001|10000000" },
			{ Bits.toBytes(2601188737736065391L),
				"01101111|01000001|10000001|00010010|01100000|01000111|00011001|00100100" },
			{ Bits.toBytes(8401653091248721777L),
				"01110001|01001111|11110001|11111101|11111000|10101010|10011000|01110100" },
			{ Bits.toBytes(2560100111339904486L),
				"11100110|01011101|11011010|10111101|01111100|01001101|10000111|00100011" },
			{ Bits.toBytes(928916744534420654L),
				"10101110|11101100|11100010|10000111|11011011|00101100|11100100|00001100" },
			{ Bits.toBytes(-6284404822773081359L),
				"11110001|11011010|11011011|00100001|00011100|01001111|11001001|10101000" },
			{ Bits.toBytes(2811728639172766355L),
				"10010011|01101010|10101111|11010110|01001100|01000100|00000101|00100111" },
		};
	}

	@Test
	public void toStringFromString() {
		final var random = RandomRegistry.random();
		for (int i = 0; i < 1000; ++i) {
			final byte[] bytes = new byte[625];
			random.nextBytes(bytes);

			final String string = Bits.toByteString(bytes);
			final byte[] data = Bits.fromByteString(string);

			Assert.assertEquals(data, bytes);
		}
	}

	@Test
	public void longToStringFromString() {
		final var random = RandomRegistry.random();
		for (int i = 0; i < 1000; ++i) {
			final long value = random.nextLong();
			final byte[] bytes = Bits.toBytes(value);

			final String string = Bits.toByteString(bytes);
			final byte[] data = Bits.fromByteString(string);

			Assert.assertEquals(data, bytes);
			Assert.assertEquals(Bits.toLong(data), value);
		}
	}

	@Test
	public void count() {
		for (int i = Byte.MIN_VALUE; i <= Byte.MAX_VALUE; ++i) {
			final byte value = (byte)i;

			Assert.assertEquals(Bits.count(value), count(value));
		}
	}

	private static int count(final byte value) {
		final byte[] array = new byte[]{value};
		int count = 0;
		for (int i = 0; i < 8; ++i) {
			if (Bits.get(array, i)) {
				++count;
			}
		}
		return count;
	}

	@Test(dataProvider = "bitsCountRanges")
	public void countBitsOfRange(final int length, final int start, final int end) {
		final byte[] data = new byte[length];
		new Random().nextBytes(data);

		int expected = 0;
		for (int i = start; i < end; ++i) {
			if (Bits.get(data, i)) {
				++expected;
			}
		}

		Assert.assertEquals(Bits.count(data, start, end), expected);
	}

	@DataProvider
	public Object[][] bitsCountRanges() {
		final var random = new Random(1234);

		final List<Object[]> values = new ArrayList<>();
		values.add(new Object[]{1, 0, 5});
		values.add(new Object[]{1, 0, 7});
		values.add(new Object[]{1, 0, 8});
		values.add(new Object[]{1, 1, 8});
		values.add(new Object[]{2, 8, 10});

		for (int i = 0; i < 100; ++i) {
			final int length = random.nextInt(1, 100);
			final int start = random.nextInt(length*Byte.SIZE);
			final int end = random.nextInt(start, length*Byte.SIZE) + 1;

			values.add(new Object[]{length, start, end});
		}

		return values.toArray(new Object[0][]);
	}

	@Test
	public void swap() {
		final int byteLength = 1_000;
		final int bitLength = byteLength*8;

		final byte[] seq = newByteArray(byteLength, new Random());

		for (int start = 0; start < bitLength - 3; ++start) {
			final byte[] copy = seq.clone();
			final byte[] other = newByteArray(byteLength, new Random());
			final byte[] otherCopy = other.clone();

			final int end = start + 2;
			final int otherStart = 1;

			Bits.swap(seq, start, end, other, otherStart);

			for (int j = start; j < end; ++j) {
				final boolean actual = Bits.get(seq, j);
				final boolean expected = Bits.get(otherCopy, j + otherStart - start);
				Assert.assertEquals(actual, expected);
			}
			for (int j = 0; j < (end - start); ++j) {
				final boolean actual = Bits.get(other, j + otherStart);
				final boolean expected = Bits.get(copy, j + start);
				Assert.assertEquals(actual, expected);
			}
		}
	}

	private static byte[] newByteArray(final int length, final Random random) {
		final byte[] array = new byte[length];
		for (int i = 0; i < length; ++i) {
			array[i] = (byte)random.nextInt();
		}
		return array;
	}

	@Test
	public void reverse() {
		final byte[] array = new byte[1000];
		new Random().nextBytes(array);

		final byte[] reverseArray = Bits.reverse(array.clone());
		for (int i = 0; i < array.length; ++i) {
			Assert.assertEquals(reverseArray[i], array[array.length - 1 - i]);
		}
	}

	@Test
	public void flip() {
		final long seed = Randoms.seed();
		final Random random = new Random(seed);
		final byte[] data = new byte[1000];

		for (int i = 0; i < data.length; ++i) {
			data[i] = (byte)random.nextInt();
		}

		final byte[] cdata = data.clone();
		for (int i = 0; i < data.length*8; ++i) {
			Bits.flip(cdata, i);
		}

		for (int i = 0; i < data.length*8; ++i) {
			Assert.assertEquals(Bits.get(cdata, i), !Bits.get(data, i), "Index: " + i);
		}
	}


	@Test(dataProvider = "shiftBits")
	public void shiftLeft(final Integer shift, final Integer bytes) {
		final long seed = System.currentTimeMillis();
		final Random random = new Random(seed);
		final byte[] data = new byte[bytes];

		for (int i = 0; i < data.length*8; ++i) {
			Bits.set(data, i, random.nextBoolean());
		}

		Bits.shiftLeft(data, shift);

		random.setSeed(seed);
		for (int i = 0; i < shift; ++i) {
			Assert.assertEquals(Bits.get(data, i), false);
		}
		for (int i = shift, n = data.length*8; i < n; ++i) {
			Assert.assertEquals(Bits.get(data, i), random.nextBoolean(), "Index: " + i);
		}
	}

	@Test
	public void bigShiftLeft() {
		final long seed = System.currentTimeMillis();
		final Random random = new Random(seed);
		final byte[] data = new byte[10];

		for (int i = 0; i < data.length*8; ++i) {
			Bits.set(data, i, random.nextBoolean());
		}

		Bits.shiftLeft(data, 100);

		for (int i = 0; i < data.length*8; ++i) {
			Assert.assertEquals(Bits.get(data, i), false);
		}
	}

	@Test(dataProvider = "shiftBits")
	public void shiftRight(final Integer shift, final Integer bytes) {
		final long seed = System.currentTimeMillis();
		final Random random = new Random(seed);
		final byte[] data = new byte[bytes];

		for (int i = 0; i < data.length*8; ++i) {
			Bits.set(data, i, random.nextBoolean());
		}

		Bits.shiftRight(data, shift);

		random.setSeed(seed);
		for (int i = 0; i < shift; ++i) {
			random.nextBoolean();
			Assert.assertEquals(Bits.get(data, data.length*8 - 1 - i), false);
		}
		for (int i = 0, n = data.length*8 - shift; i < n; ++i) {
			Assert.assertEquals(Bits.get(data, i), random.nextBoolean(), "Index: " + i);
		}
	}

	@Test
	public void bigShiftRight() {
		final long seed = System.currentTimeMillis();
		final Random random = new Random(seed);
		final byte[] data = new byte[10];

		for (int i = 0; i < data.length*8; ++i) {
			Bits.set(data, i, random.nextBoolean());
		}

		Bits.shiftRight(data, 100);

		for (int i = 0; i < data.length*8; ++i) {
			Assert.assertEquals(Bits.get(data, i), false, "Index: " + i);
		}
	}

	@DataProvider(name = "shiftBits")
	public Object[][] shiftBits() {
		return new Object[][] {
				{0, 0},
				{0, 1},
				{1, 1},
				{1, 2},
				{0, 3},
				{1, 3},
				{3, 3},
				{7, 3},
				{8, 3},
				{9, 3},
				{24, 3},
				{17, 5},
				{345, 50},
				{0, 100},
				{1, 100},
				{80, 100},
				{799, 100}
		};
	}

	@Test
	public void setGetBit() {
		final long seed = System.currentTimeMillis();
		final Random random = new Random(seed);
		final byte[] data = new byte[10000];

		for (int i = 0; i < data.length*8; ++i) {
			Bits.set(data, i, random.nextBoolean());
		}

		random.setSeed(seed);
		for (int i = 0; i < data.length*8; ++i) {
			Assert.assertEquals(Bits.get(data, i), random.nextBoolean());
		}
	}

	@Test
	public void setGetBit1() {
		final byte[] data = new byte[3];
		Arrays.fill(data, (byte)0);

		for (int i = 0; i < data.length*8; ++i) {
			Bits.set(data, i);
			System.out.println(Bits.toByteString(data));
			Assert.assertTrue(Bits.get(data, i));
		}
	}

	@Test
	public void setGetBid2() {
		final int length = 80;
		final byte[] data = Bits.newArray(length);

		for (int i = 0; i < length; ++i) {
			assertThat(Bits.get(data, i)).isFalse();
			Bits.set(data, i);
			assertThat(Bits.get(data, i)).isTrue();
		}
	}

	@Test(
		expectedExceptions = IndexOutOfBoundsException.class,
		dataProvider = "indexoutofboundsdata"
	)
	public void setOutOfIndex(final Integer length, final Integer index) {
		final byte[] data = Bits.newArray(length);
		Bits.set(data, index, false);
	}

	@Test(
		expectedExceptions = IndexOutOfBoundsException.class,
		dataProvider = "indexoutofboundsdata"
	)
	public void getOutOfIndex(final Integer length, final Integer index) {
		final byte[] data = Bits.newArray(length);
		Bits.get(data, index);
	}

	@DataProvider(name = "indexoutofboundsdata")
	public Object[][] getIndexOutOfBoundsData() {
		return new Object[][] {
			{1, 8},
			{1, -1},
			{2, 16},
			{2, 2342},
			{10, 80},
			{100, 108}
		};
	}

	@Test
	public void invert() {
		final long seed = System.currentTimeMillis();
		final Random random = new Random(seed);
		final byte[] data = new byte[1000];

		for (int i = 0; i < data.length*8; ++i) {
			Bits.set(data, i, random.nextBoolean());
		}

		final byte[] cdata = data.clone();
		Bits.invert(cdata);

		for (int i = 0; i < data.length*8; ++i) {
			Assert.assertEquals(Bits.get(cdata, i), !Bits.get(data, i), "Index: " + i);
		}
	}

	@Test
	public void complement() {
		final Random random = new Random(Randoms.seed());
		final byte[] data = new byte[20];
		random.nextBytes(data);

		final byte[] cdata = Bits.complement(data.clone());
		Assert.assertFalse(Arrays.equals(data, cdata));
		Assert.assertTrue(Arrays.equals(data, Bits.complement(cdata)));
	}

	@Test
	public void increment() {
		final byte[] data = new byte[4];
		for (int i = 0; i < Short.MAX_VALUE; ++i) {
			final int value = toInt(data);
			Assert.assertEquals(value, i);
			Bits.increment(data);
		}
	}

	private static int toInt(final byte[] data) {
		return
			((data[3] & 255) << 24) +
			((data[2] & 255) << 16) +
			((data[1] & 255) << 8) +
			(data[0] & 255);
	}

	private static byte[] toBytes(final int value) {
		final byte[] bytes = new byte[4];
		bytes[3] = (byte)(value >>> 24);
		bytes[2] = (byte)(value >>> 16);
		bytes[1] = (byte)(value >>>  8);
		bytes[0] = (byte) value;
		return bytes;
	}

	@Test
	public void increment2() {
		final Random random = new Random();
		for (int i = 0; i < 100_000; ++i) {
			final int value = Math.abs(random.nextInt() - 1);
			final byte[] data = toBytes(value);
			Bits.increment(data);

			Assert.assertEquals(toInt(data), value + 1);
		}
	}

	@DataProvider(name = "toByteArrayData")
	public Iterator<Object[]> toByteArrayData() {
		final long seed = System.currentTimeMillis();
		final Random random = new Random(seed);
		final int length = 20;

		return new Iterator<>() {
			private int _pos = 0;

			@Override
			public boolean hasNext() {
				return _pos < length;
			}

			@Override
			public Object[] next() {
				final int size = random.nextInt(100) + 1;
				final byte[] data = new byte[size];
				random.nextBytes(data);
				_pos += 1;

				return new Object[]{new BigInteger(data)};
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@DataProvider(name = "toLargeIntegerData")
	public Iterator<Object[]> toLargeIntegerData() {
		final long seed = System.currentTimeMillis();
		final Random random = new Random(seed);
		final int length = 20;

		return new Iterator<>() {
			private int _pos = 0;

			@Override
			public boolean hasNext() {
				return _pos < length;
			}

			@Override
			public Object[] next() {
				final int size = random.nextInt(1000) + 1;
				final byte[] data = new byte[size];
				random.nextBytes(data);
				_pos += 1;

				return new Object[]{data};
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

}
