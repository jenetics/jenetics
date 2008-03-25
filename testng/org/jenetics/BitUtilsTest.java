/*
 * Java Genetic Algorithm Library (Jenetics-0.1.0.3).
 * Copyright (c) 2007 Franz Wilhelmstötter
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
package org.jenetics;

import java.util.Arrays;

import org.jscience.mathematics.number.LargeInteger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: BitUtilsTest.java,v 1.1 2008-03-25 18:31:57 fwilhelm Exp $
 */
public class BitUtilsTest {

	@Test
	public void convertByteArrayLargeInteger() {
		LargeInteger i = LargeInteger.valueOf(23443L);
		
		byte[] array = BitUtils.toByteArray(i);
		LargeInteger i2 = BitUtils.toLargeInteger(array);
		Assert.assertEquals(i2, i);
	}
	
	@Test
	public void shift() {
		byte[] data = new byte[10];
		for (int i = 0; i < data.length*8; ++i) {
			if (i%2 == 0) {
				BitUtils.setBit(data, i, true);
			} else {
				BitUtils.setBit(data, i, false);
			}
		}
		
		byte[] sdata = data.clone();
		BitUtils.shiftLeft(sdata, 8);
		BitUtils.shiftRight(sdata, 8);
		data[0] = (byte)0;
		Assert.assertEquals(sdata, data);
	}
	
	@Test
	public void getBit() {
		byte[] data = new byte[4];
		Arrays.fill(data, (byte)0);
		
		for (int i = 0; i < data.length*8; ++i) {
			Assert.assertEquals(BitUtils.getBit(data, i), false);
		}
		
		for (int i = 0; i < data.length*8; ++i) {
			if (i%2 == 0) {
				BitUtils.setBit(data, i, true);
			} else {
				BitUtils.setBit(data, i, false);
			}
		}
		for (int i = 0; i < data.length*8; ++i) {
			if (i%2 == 0) {
				Assert.assertEquals(BitUtils.getBit(data, i), true);
			} else {
				Assert.assertEquals(BitUtils.getBit(data, i), false);
			}
		}
	}
	
	@Test
	public void invert() {
		byte[] data = new byte[10];
		for (int i = 0; i < data.length*8; ++i) {
			if (i%2 == 0) {
				BitUtils.setBit(data, i, true);
			} else {
				BitUtils.setBit(data, i, false);
			}
		}
		
		byte[] idata = data.clone();
		BitUtils.invert(idata);
		BitUtils.invert(idata);
		Assert.assertEquals(idata, data);
	}
	
}
