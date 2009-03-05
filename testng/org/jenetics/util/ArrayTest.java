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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ArrayTest.java,v 1.6 2009-03-05 19:39:01 fwilhelm Exp $
 */
public class ArrayTest {

	@Test
	public void copy() {
		final Array<Integer> array = new Array<Integer>(10);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		
		final Array<Integer> copy = array.subArray(3, 8).copy();
		Assert.assertEquals(copy.length(), 5);
		for (int i = 0; i < 5; ++i) {
			Assert.assertEquals(copy.get(i), new Integer(i + 3));
		}
	}
	
	@Test
	public void subArray() {
		final Array<Integer> array = new Array<Integer>(10);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		
		final Array<Integer> sub = array.subArray(3, 8);
		Assert.assertEquals(sub.length(), 5);
		for (int i = 0; i < 5; ++i) {
			Assert.assertEquals(sub.get(i), new Integer(i + 3));
			sub.set(i, i + 100);
		}
		
		for (int i = 3; i < 8; ++i) {
			Assert.assertEquals(array.get(i), new Integer(i + 97));
		}
		
		final Array<Integer> copy = sub.copy();
		Assert.assertEquals(copy.length(), 5);
		for (int i = 0; i < 5; ++i) {
			Assert.assertEquals(sub.get(i), new Integer(i + 100));
		}
		
		int count = 0;
		for (Integer i : sub) {
			Assert.assertEquals(i, new Integer(count + 100));
			++count;
		}
		Assert.assertEquals(count, 5);
		
//		System.out.println(sub);
//		System.out.println(array);
	}
	
}
