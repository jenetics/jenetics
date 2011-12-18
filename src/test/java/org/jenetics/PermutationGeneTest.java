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

import static org.jenetics.util.factories.Int;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: org.eclipse.jdt.ui.prefs 421 2010-03-18 22:41:17Z fwilhelm $
 */
public class PermutationGeneTest extends GeneTester<PermutationGene<Integer>> {
	
	private final Factory<PermutationGene<Integer>>
	_factory = new Factory<PermutationGene<Integer>>() {
		private ISeq<Integer> _alleles = new Array<Integer>(100).fill(Int()).toISeq();
		
		@Override
		public PermutationGene<Integer> newInstance() {
			return PermutationGene.valueOf(_alleles);
		}
		
	};
	
	@Override
	protected Factory<PermutationGene<Integer>> getFactory() {
		return _factory;
	}

	@Test
	public void valueOf() {
		final int length = 100;
		final ISeq<Integer> alleles = new Array<Integer>(length).fill(Int()).toISeq();
		
		Assert.assertEquals(alleles.length(), length);
		for (int i = 0; i < alleles.length(); ++i) {
			Assert.assertEquals(alleles.get(i), new Integer(i));
		}

		for (int i = 0; i < alleles.length(); ++i) {
			Assert.assertEquals(PermutationGene.valueOf(alleles, i).getAllele(), new Integer(i));
			Assert.assertSame(PermutationGene.valueOf(alleles, i).getValidAlleles(), alleles);
		}
	}

	@Test(expectedExceptions = IndexOutOfBoundsException.class)
	public void valueOfIndexOutOfBounds1() {
		final int length = 100;
		final ISeq<Integer> alleles = new Array<Integer>(length).fill(Int()).toISeq();
		
		PermutationGene.valueOf(alleles, length + 1);
	}
	
	@Test(expectedExceptions = IndexOutOfBoundsException.class)
	public void valueOfIndexOutOfBounds2() {
		final int length = 100;
		final ISeq<Integer> alleles = new Array<Integer>(length).fill(Int()).toISeq();
		
		PermutationGene.valueOf(alleles, -1);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void valueOfZeroLength() {
		final int length = 0;
		final ISeq<Integer> alleles = new Array<Integer>(length).fill(Int()).toISeq();
		
		PermutationGene.valueOf(alleles);
	}
	
}




