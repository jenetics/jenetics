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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class ProbabilityIndexIteratorTest {

	@Test
	public void iterateP0() {
		final ProbabilityIndexIterator it = 
			new ProbabilityIndexIterator(1000, 0, new Random());
		
		for (int i = it.next(); i != -1; i = it.next()) {
			Assert.assertTrue(false);
		}
	}
	
	@Test
	public void iterateP1() {
		final ProbabilityIndexIterator it = 
			new ProbabilityIndexIterator(1000, 1, new Random());
		
		int count = 0;
		for (int i = it.next(); i != -1; i = it.next()) {
			Assert.assertEquals(i, count);
			++count;
		}
		
		Assert.assertEquals(count, 1000);
	}
	
	@Test
	public void iterator() {
		final List<Integer> list = new ArrayList<Integer>(1000);
		for (int i = 0; i< 1000; ++i) {
			list.add(i);
		}
		
		for (Iterator<Integer> it = ProbabilityIndexIterator.iterator(list, 0.01, new Random()); it.hasNext();) {
			System.out.println(it.next());
		}
	}
}




