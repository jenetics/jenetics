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

	static long add(long a, long b) {
		long c = a + b;
		if (((c ^ a) & (c ^ b) >> 63) != 0) {
			throw new ArithmeticException();
		}

		return c;
	}
	
	@Test
	public void add() {
		add(Long.MAX_VALUE, 0);
	}
	
	@Test
	public void convertByteArrayLargeInteger() {
		LargeInteger i = LargeInteger.valueOf(23443L);
		
		byte[] array = bit.toByteArray(i);
		LargeInteger i2 = bit.toLargeInteger(array);
		Assert.assertEquals(i2, i);
	}
	
	@Test(dataProvider = "shiftBits")
	public void shiftLeft(final Integer shift, final Integer bytes) {
		final long seed = 9420987471198734L;
		final Random random = new Random(seed);
		final byte[] data = new byte[bytes];
		
		for (int i = 0; i < data.length*8; ++i) {
			bit.setBit(data, i, random.nextBoolean());
		}
		
		bit.shiftLeft(data, shift);
		
		random.setSeed(seed);
		for (int i = 0; i < shift; ++i) {
			Assert.assertEquals(bit.getBit(data, i), false);
		}
		for (int i = shift, n = data.length*8; i < n; ++i) {
			Assert.assertEquals(bit.getBit(data, i), random.nextBoolean());
		}
	}
	
	@Test
	public void bigShiftLeft() {
		final long seed = 94209874198734L;
		final Random random = new Random(seed);
		final byte[] data = new byte[10];
		
		for (int i = 0; i < data.length*8; ++i) {
			bit.setBit(data, i, random.nextBoolean());
		}
		
		bit.shiftLeft(data, 100);
		
		for (int i = 0; i < data.length*8; ++i) {
			Assert.assertEquals(bit.getBit(data, i), false);
		}
	}
	
	@Test(dataProvider = "shiftBits")
	public void shiftRight(final Integer shift, final Integer bytes) {
		final long seed = 9420987671198734L;
		final Random random = new Random(seed);
		final byte[] data = new byte[bytes];
		
		for (int i = 0; i < data.length*8; ++i) {
			bit.setBit(data, i, random.nextBoolean());
		}
		
		bit.shiftRight(data, shift);
		
		random.setSeed(seed);
		for (int i = 0; i < shift; ++i) {
			random.nextBoolean();
			Assert.assertEquals(bit.getBit(data, data.length*8 - 1 - i), false);
		}
		for (int i = 0, n = data.length*8 - shift; i < n; ++i) {
			Assert.assertEquals(bit.getBit(data, i), random.nextBoolean(), "Index: " + i);
		}
	}
	
	@Test
	public void bigShiftRight() {
		final long seed = 94209874198734L;
		final Random random = new Random(seed);
		final byte[] data = new byte[10];
		
		for (int i = 0; i < data.length*8; ++i) {
			bit.setBit(data, i, random.nextBoolean());
		}
		
		bit.shiftRight(data, 100);
		
		for (int i = 0; i < data.length*8; ++i) {
			Assert.assertEquals(bit.getBit(data, i), false);
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
		final long seed = 94209874710998734L;
		final Random random = new Random(seed);
		final byte[] data = new byte[10000];
				
		for (int i = 0; i < data.length*8; ++i) {
			bit.setBit(data, i, random.nextBoolean());
		}
		
		random.setSeed(seed);
		for (int i = 0; i < data.length*8; ++i) {
			Assert.assertEquals(bit.getBit(data, i), random.nextBoolean());
		}
	}
	
	@Test
	public void invert() {
		byte[] data = new byte[10];
		for (int i = 0; i < data.length*8; ++i) {
			if (i%2 == 0) {
				bit.setBit(data, i, true);
			} else {
				bit.setBit(data, i, false);
			}
		}
		
		byte[] idata = data.clone();
		bit.invert(idata);
		bit.invert(idata);
		Assert.assertEquals(idata, data);
	}
	
}




