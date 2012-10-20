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
package org.jenetics;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.ISeq;
import org.jenetics.util.ObjectTester;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public abstract class ChromosomeTester<G extends Gene<?, G>>
	extends ObjectTester<Chromosome<G>>
{

	@Test
	public void getGene() {
		final Chromosome<G> c = getFactory().newInstance();
		final ISeq<G> genes = c.toSeq();
		
		Assert.assertEquals(c.getGene(), genes.get(0));
		for (int i = 0; i < genes.length(); ++i) {
			Assert.assertSame(c.getGene(i), genes.get(i));
		}
	}
	
	@Test
	public void newInstanceFromArray() {
		for (int i = 0; i < 100; ++i) {
			final Chromosome<G> c1 = getFactory().newInstance();
			final ISeq<G> genes = c1.toSeq();
			final Chromosome<G> c2 = c1.newInstance(genes);
			
			Assert.assertEquals(c2, c1);
		}
	}
	
	@Test(expectedExceptions = NullPointerException.class)
	public void newInstanceFromNullArray() {
		final Chromosome<G> c = getFactory().newInstance();
		c.newInstance(null);
	}
	
	@Test
	public void newInstanceFromRandom() {
		for (int i = 0; i < 100; ++i) {
			final Chromosome<G> c1 = getFactory().newInstance();
			final Chromosome<G> c2 = c1.newInstance();
			
			Assert.assertEquals(c2.length(), c1.length());
			if (c1.equals(c2)) {
				Assert.assertEquals(c2.toSeq(), c1.toSeq());
			}
		}
	}
	
	@Test
	public void iterator(){
		final Chromosome<G> c = getFactory().newInstance();
		final ISeq<G> a = c.toSeq();
		
		int index = 0;
		for (G gene : c) {
			Assert.assertEquals(gene, a.get(index));
			Assert.assertEquals(gene, c.getGene(index));
			
			++index;
		}
	}
	
	@Test
	public void length() {
		final Chromosome<G> c = getFactory().newInstance();
		final ISeq<G> a = c.toSeq();
		
		Assert.assertEquals(c.length(), a.length());
	}
	
}


