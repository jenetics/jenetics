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

import org.jscience.mathematics.number.Number;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public abstract class NumberGeneTester<N extends Number<N>,G extends NumberGene<N,G>> 
	extends GeneTester<G>
{
	
	@Test
	public void newInstanceFromNumber() {
		for (int i = 0; i < 100; ++i) {
			final G gene1 = getFactory().newInstance();
			final G gene2 = gene1.newInstance(gene1.getAllele());
			
			Assert.assertEquals(gene2, gene1);
		}
	}
	
	@Test
	public void minMax() {
		for (int i = 0; i < 100; ++i) {
			final G gene = getFactory().newInstance();
			
			Assert.assertTrue(gene.getNumber().compareTo(gene.getMin()) >= 0);
			Assert.assertTrue(gene.getNumber().compareTo(gene.getMax()) <= 0);
		}
	}
	
	@Test
	public void compareTo() {
		for (int i = 0; i < 100; ++i) {
			final G gene1 = getFactory().newInstance();
			final G gene2 = getFactory().newInstance();
			
			if (gene1.getNumber().compareTo(gene2.getNumber()) > 0) {
				Assert.assertTrue(gene1.compareTo(gene2) > 0);
			} else if (gene1.getNumber().compareTo(gene2.getNumber()) < 0) {
				Assert.assertTrue(gene1.compareTo(gene2) < 0);
			} else {
				Assert.assertTrue(gene1.compareTo(gene2) == 0);
			}
		}		
	}
	
	@Test
	public void isLargerThen() {
		for (int i = 0; i < 100; ++i) {
			final G gene1 = getFactory().newInstance();
			final G gene2 = getFactory().newInstance();
			
			if (gene1.getNumber().isLargerThan(gene2.getNumber())) {
				Assert.assertTrue(gene1.isLargerThan(gene2));
			} else {
				Assert.assertFalse(gene1.isLargerThan(gene2));
			}
		}
	}
	
	@Test
	public void isGreaterThen() {
		for (int i = 0; i < 100; ++i) {
			final G gene1 = getFactory().newInstance();
			final G gene2 = getFactory().newInstance();
			
			if (gene1.getNumber().isGreaterThan(gene2.getNumber())) {
				Assert.assertTrue(gene1.isGreaterThan(gene2));
			} else {
				Assert.assertFalse(gene1.isGreaterThan(gene2));
			}
		}
	}
	
	@Test
	public void opposite() {
		for (int i = 0; i < 100; ++i) {
			final G gene1 = getFactory().newInstance();
			final G gene2 = gene1.opposite();
			final G gene3 = gene2.opposite();
			
			assertMinMax(gene1, gene2);
			assertMinMax(gene2, gene3);
			Assert.assertEquals(gene3, gene1);
			Assert.assertFalse(gene1.equals(gene2));
			assertValid(gene2);
			assertValid(gene3);
		}
	}
	
	@Test
	public void plus() {
		for (int i = 0; i < 100; ++i) {
			final G gene1 = getFactory().newInstance();
			final G gene2 = getFactory().newInstance();
			final G gene3 = gene1.plus(gene2);
			
			assertMinMax(gene1, gene2);
			assertMinMax(gene2, gene3);
			assertValid(gene3);
			Assert.assertEquals(
					gene3.getNumber(), 
					gene1.getNumber().plus(gene2.getNumber())
				);
		}
	}
	
	@Test
	public void minus() {
		for (int i = 0; i < 100; ++i) {
			final G gene1 = getFactory().newInstance();
			final G gene2 = getFactory().newInstance();
			final G gene3 = gene1.minus(gene2);
			
			assertMinMax(gene1, gene2);
			assertMinMax(gene2, gene3);
			assertValid(gene3);
			Assert.assertEquals(
					gene3.getNumber(), 
					gene1.getNumber().minus(gene2.getNumber())
				);
		}
	}
	
	@Test
	public void times() {
		for (int i = 0; i < 100; ++i) {
			final G gene1 = getFactory().newInstance();
			final G gene2 = getFactory().newInstance();
			final G gene3 = gene1.times(gene2);
			
			assertMinMax(gene1, gene2);
			assertMinMax(gene2, gene3);
			assertValid(gene3);
			Assert.assertEquals(
					gene3.getNumber(), 
					gene1.getNumber().times(gene2.getNumber())
				);
		}
	}
	
	public void assertMinMax(final G gene1, final G gene2) {
		Assert.assertEquals(gene1.getMin(), gene2.getMin());
		Assert.assertEquals(gene1.getMax(), gene2.getMax());
	}
	
	public void assertValid(final G gene) {
		if (gene.isValid()) {
			Assert.assertTrue(gene.getNumber().compareTo(gene.getMin()) >= 0);
			Assert.assertTrue(gene.getNumber().compareTo(gene.getMax()) <= 0);
		} else {
			Assert.assertTrue(
					gene.getNumber().compareTo(gene.getMin()) < 0 ||
					gene.getNumber().compareTo(gene.getMax()) > 0
				);
		}
	}

}





