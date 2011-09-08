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

import java.util.Iterator;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jscience.mathematics.number.LargeInteger;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class bitTest {
	
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
	
//	@Test
//	public void increment() {
//		final byte[] data = new byte[3];
//		//bit.set(data, 0);
//		
//		for (int i = 0; i < 257; ++i) {
//			System.out.println(i + ": " + object.str(data));
//			bit.increment(data);
//		}
//		System.out.println(object.str(data));
//		
//		bit.invert(data);
//		System.out.println(object.str(data));
//		
//		bit.increment(data);
//		System.out.println(object.str(data));
//		
////		System.out.println(LargeInteger.valueOf(1).toByteArray(data, 0));
////		
////		System.out.println("lib: " + object.str(data));
////		System.out.println(bit.toLargeInteger(data));
////		bit.increment(data);
////		System.out.println(object.str(data));
////		System.out.println(bit.toLargeInteger(data));
////		
////		bit.increment(data);
////		System.out.println(object.str(data));
////		System.out.println(bit.toLargeInteger(data));
//	}
	
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
		final int length = 25;
		
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
		final int length = 25;
		
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







