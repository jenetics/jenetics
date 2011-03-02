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
package org.jenetics;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.ArrayUtils;
import org.jenetics.util.Factory;
import org.jenetics.util.MSeq;
import org.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class PermutationChromosomeTest extends ChromosomeTester<Integer64Gene> { 
    
	private final Factory<Chromosome<Integer64Gene>> 
	_factory = new PermutationChromosome(500);
	@Override protected Factory<Chromosome<Integer64Gene>> getFactory() {
		return _factory;
	}
	
	@Test
	public void create() {
		PermutationChromosome c = new PermutationChromosome(50, true);
		final MSeq<Integer64Gene> array = c.toArray().copy();
		Assert.assertFalse(isSorted(array));
		
		ArrayUtils.sort(array);
		for (int i = 0; i < array.length(); ++i) {
			Assert.assertEquals(array.get(i).intValue(), i);
		}
	}
	
	@Test
	public void sortedNewInstance() {
		PermutationChromosome c = new PermutationChromosome(50, false);
		final MSeq<Integer64Gene> array = c.toArray().copy();
		Assert.assertTrue(isSorted(array));
		
		for (int i = 0; i < array.length(); ++i) {
			Assert.assertEquals(array.get(i).intValue(), i);
		}
	}
	
	private boolean isSorted(final Seq<Integer64Gene> array) {
		boolean sorted = true;
		for (int i = 0; i < array.length() - 1 && sorted; ++i) {
			sorted = array.get(i).compareTo(array.get(i + 1)) <= 0;
		}
		return sorted;
	}
	
}
