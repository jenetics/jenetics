/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
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
import java.util.Iterator;
import java.util.Random;

import org.jscience.mathematics.number.LargeInteger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class bitTest {

	@Test(dataProvider = "byteStrData")
	public void byteStr(final byte[] data, final String result) {
		Assert.assertEquals(bit.toString(data), result);
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
			
			{ bit.toBytes(-5165661323090255963L), "10100101|00011111|00111011|00111111|01100101|11100010|01001111|10111000" },
			{ bit.toBytes(-3111444787550306452L), "01101100|10111111|11010010|01101011|01110011|11101101|11010001|11010100" },
			{ bit.toBytes(-3303191740454820247L), "01101001|10100110|00101111|11110101|10011100|10110100|00101000|11010010" },
			{ bit.toBytes(4795980783582945410L), "10000010|10100000|00001111|11001011|00011100|10111111|10001110|01000010" },
			{ bit.toBytes(5363121614382394644L), "00010100|01101101|01111011|01111000|10110001|10100010|01101101|01001010" },
			{ bit.toBytes(-8185663930382162219L), "11010101|11100110|11010111|01011010|00000110|10101110|01100110|10001110" },
			{ bit.toBytes(-1232621285458438758L), "10011010|00010101|10101010|10111001|01111000|11011001|11100100|11101110" },
			{ bit.toBytes(-2081775369634963197L), "00000011|00100001|11000000|10111110|01010100|00001100|00011100|11100011" },
			{ bit.toBytes(-2194074334834473370L), "01100110|11000110|01100011|01100101|00000100|00010101|10001101|11100001" },
			{ bit.toBytes(7950010868533801327L), "01101111|10110001|01110011|10010011|11000011|00011100|01010100|01101110" },
			{ bit.toBytes(6680935979511658057L), "01001001|00110110|11101011|01010001|11100000|01110011|10110111|01011100" },
			{ bit.toBytes(-2670808837407163052L), "01010100|00111101|01011111|01001111|10000011|01100001|11101111|11011010" },
			{ bit.toBytes(4167160717303874479L), "10101111|00000011|11011100|00000100|10011000|10111010|11010100|00111001" },
			{ bit.toBytes(-4513322218647029476L), "00011100|01111001|10101010|11010000|01011010|01110101|01011101|11000001" },
			{ bit.toBytes(564299592671873811L), "00010011|11001011|00011010|01100000|01111101|11001011|11010100|00000111" },
			{ bit.toBytes(5256495800767342066L), "11110010|11100101|00010001|10101000|00010100|11010011|11110010|01001000" },
			{ bit.toBytes(-6440333658299846476L), "10110100|10110000|00001011|11111110|10101000|01010110|10011111|10100110" },
			{ bit.toBytes(8415309172805358741L), "10010101|01010100|00100110|01000000|00011011|00101111|11001001|01110100" },
			{ bit.toBytes(-9216328290938433144L), "10001000|01010001|00111011|11100101|00111111|00000110|00011001|10000000" },
			{ bit.toBytes(2601188737736065391L), "01101111|01000001|10000001|00010010|01100000|01000111|00011001|00100100" },
			{ bit.toBytes(8401653091248721777L), "01110001|01001111|11110001|11111101|11111000|10101010|10011000|01110100" },
			{ bit.toBytes(2560100111339904486L), "11100110|01011101|11011010|10111101|01111100|01001101|10000111|00100011" },
			{ bit.toBytes(928916744534420654L), "10101110|11101100|11100010|10000111|11011011|00101100|11100100|00001100" },
			{ bit.toBytes(-6284404822773081359L), "11110001|11011010|11011011|00100001|00011100|01001111|11001001|10101000" },
			{ bit.toBytes(2811728639172766355L), "10010011|01101010|10101111|11010110|01001100|01000100|00000101|00100111" },
		};
	}
	
	/*
	@Test
	public void foo() {
		for (int j = 0; j < 25; ++j) {
			long value = RandomRegistry.getRandom().nextLong();
			byte[] bytes = bit.toBytes(value);
			String string = bit.toString(bytes);
			
			System.out.println(String.format(
				"{ bit.toBytes(%dL), \"%s\" },",
				value, string
			));
		}
	}
	*/
	
	@Test
	public void flip() {
		final long seed = System.currentTimeMillis();
		final Random random = new Random(seed);
		final byte[] data = new byte[4];

		for (int i = 0; i < data.length*8; ++i) {
			bit.set(data, i, random.nextBoolean());
		}

		final byte[] cdata = data.clone();
		for (int i = 0; i < data.length*8; ++i) {
			bit.flip(cdata, i);
		}

		for (int i = 0; i < data.length*8; ++i) {
			Assert.assertEquals(bit.get(cdata, i), !bit.get(data, i), "Index: " + i);
		}
	}


	@Test(dataProvider = "shiftBits")
	public void shiftLeft(final Integer shift, final Integer bytes) {
		final long seed = System.currentTimeMillis();
		final Random random = new Random(seed);
		final byte[] data = new byte[bytes];

		for (int i = 0; i < data.length*8; ++i) {
			bit.set(data, i, random.nextBoolean());
		}

		bit.shiftLeft(data, shift);

		random.setSeed(seed);
		for (int i = 0; i < shift; ++i) {
			Assert.assertEquals(bit.get(data, i), false);
		}
		for (int i = shift, n = data.length*8; i < n; ++i) {
			Assert.assertEquals(bit.get(data, i), random.nextBoolean(), "Index: " + i);
		}
	}

	@Test
	public void bigShiftLeft() {
		final long seed = System.currentTimeMillis();
		final Random random = new Random(seed);
		final byte[] data = new byte[10];

		for (int i = 0; i < data.length*8; ++i) {
			bit.set(data, i, random.nextBoolean());
		}

		bit.shiftLeft(data, 100);

		for (int i = 0; i < data.length*8; ++i) {
			Assert.assertEquals(bit.get(data, i), false);
		}
	}

	@Test(dataProvider = "shiftBits")
	public void shiftRight(final Integer shift, final Integer bytes) {
		final long seed = System.currentTimeMillis();
		final Random random = new Random(seed);
		final byte[] data = new byte[bytes];

		for (int i = 0; i < data.length*8; ++i) {
			bit.set(data, i, random.nextBoolean());
		}

		bit.shiftRight(data, shift);

		random.setSeed(seed);
		for (int i = 0; i < shift; ++i) {
			random.nextBoolean();
			Assert.assertEquals(bit.get(data, data.length*8 - 1 - i), false);
		}
		for (int i = 0, n = data.length*8 - shift; i < n; ++i) {
			Assert.assertEquals(bit.get(data, i), random.nextBoolean(), "Index: " + i);
		}
	}

	@Test
	public void bigShiftRight() {
		final long seed = System.currentTimeMillis();
		final Random random = new Random(seed);
		final byte[] data = new byte[10];

		for (int i = 0; i < data.length*8; ++i) {
			bit.set(data, i, random.nextBoolean());
		}

		bit.shiftRight(data, 100);

		for (int i = 0; i < data.length*8; ++i) {
			Assert.assertEquals(bit.get(data, i), false, "Index: " + i);
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
			bit.set(data, i, random.nextBoolean());
		}

		random.setSeed(seed);
		for (int i = 0; i < data.length*8; ++i) {
			Assert.assertEquals(bit.get(data, i), random.nextBoolean());
		}
	}

	@Test(expectedExceptions = IndexOutOfBoundsException.class)
	public void setOutOfIndex() {
		final byte[] data = new byte[3];
		bit.set(data, 100, false);
	}

	@Test(expectedExceptions = IndexOutOfBoundsException.class)
	public void getOutOfIndex() {
		final byte[] data = new byte[3];
		bit.get(data, 100);
	}

	@Test
	public void increment() {
		final int min = -128;
		final int max = 127;

		for (int i = min; i < -2; ++i) {
			final LargeInteger li1 = LargeInteger.valueOf(i);
			final byte[] data = bit.toByteArray(li1);

			bit.increment(data);

			final LargeInteger li2 = bit.toLargeInteger(data);
			Assert.assertEquals(li2, li1.plus(1));
		}
		for (int i = 0; i < max; ++i) {
			final LargeInteger li1 = LargeInteger.valueOf(i);
			final byte[] data = bit.toByteArray(li1);

			bit.increment(data);

			final LargeInteger li2 = bit.toLargeInteger(data);
			Assert.assertEquals(li2, li1.plus(1));
		}
	}

	@Test
	public void invert() {
		final long seed = System.currentTimeMillis();
		final Random random = new Random(seed);
		final byte[] data = new byte[1000];

		for (int i = 0; i < data.length*8; ++i) {
			bit.set(data, i, random.nextBoolean());
		}

		final byte[] cdata = data.clone();
		bit.invert(cdata);

		for (int i = 0; i < data.length*8; ++i) {
			Assert.assertEquals(bit.get(cdata, i), !bit.get(data, i), "Index: " + i);
		}
	}
	
	@Test
	public void complement() {
		final Random random = new Random(math.random.seed());
		final byte[] data = new byte[20];
		random.nextBytes(data);
		
		final byte[] cdata = bit.complement(data.clone());
		Assert.assertFalse(Arrays.equals(data, cdata));
		Assert.assertTrue(Arrays.equals(data, bit.complement(cdata)));
	}

	@Test(dataProvider = "toByteArrayData")
	public void toByteArray(final LargeInteger value) {
		final byte[] data = bit.toByteArray(value);
		final LargeInteger i = bit.toLargeInteger(data);
		final byte[] idata = bit.toByteArray(i);

		Assert.assertEquals(idata, data);
		Assert.assertEquals(i, value);
	}

	@DataProvider(name = "toByteArrayData")
	public Iterator<Object[]> toByteArrayData() {
		final long seed = System.currentTimeMillis();
		final Random random = new Random(seed);
		final int length = 20;

		return new Iterator<Object[]>() {
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

				return new Object[]{ LargeInteger.valueOf(data, 0, data.length) };
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Test(dataProvider = "toLargeIntegerData")
	public void toLargeInteger(final byte[] data) {
		final LargeInteger i = bit.toLargeInteger(data);

		final byte[] idata = bit.toByteArray(i);
		final LargeInteger j = bit.toLargeInteger(idata);

		for (int k = 0, n = Math.min(idata.length, data.length); k < n; ++k) {
			if (idata[idata.length - k - 1] != data[data.length - k - 1]) {
				Assert.assertEquals(idata[k], data[k]);
			}

		}
		Assert.assertEquals(j, i);
	}

	@DataProvider(name = "toLargeIntegerData")
	public Iterator<Object[]> toLargeIntegerData() {
		final long seed = System.currentTimeMillis();
		final Random random = new Random(seed);
		final int length = 20;

		return new Iterator<Object[]>() {
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

				return new Object[]{ data };
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

}







