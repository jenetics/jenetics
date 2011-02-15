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

import org.jenetics.util.Array;
import org.jenetics.util.ObjectTester;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public abstract class GeneTester<G extends Gene<?, G>> extends ObjectTester<G> {

	@Test
	public void equalsAllele() {
		final Array<G> same = newSameObjects(5);
		
		final G that = same.get(0);
		for (int i = 1; i < same.length(); ++i) {
			final G other = same.get(i);
			
			Assert.assertEquals(other.getAllele(), other.getAllele());
			Assert.assertEquals(other.getAllele(), that.getAllele());
			Assert.assertEquals(that.getAllele(), other.getAllele());
			Assert.assertFalse(other.getAllele().equals(null));
		}
	}
	
	@Test
	public void notEqualsAllele() {
		for (int i = 0; i < 10; ++i) {
			final G that = getFactory().newInstance();
			final G other = getFactory().newInstance();
			
			if (that.equals(other)) {
				Assert.assertTrue(other.getAllele().equals(that.getAllele()));
				Assert.assertEquals(that.getAllele().hashCode(), other.getAllele().hashCode());
			} else {
				Assert.assertFalse(other.getAllele().equals(that.getAllele()));
			}
		}
	}
	
	@Test
	public void copy() {
		for (int i = 0; i < 10; ++i) {
			final G gene = getFactory().newInstance();
			final Object copy = gene.copy();
			
			Assert.assertEquals(copy, gene);
			Assert.assertEquals(copy.hashCode(), gene.hashCode());
		}
	}
	
}



