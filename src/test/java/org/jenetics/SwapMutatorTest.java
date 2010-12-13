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

import static org.jenetics.TestUtils.diff;
import static org.jenetics.TestUtils.newFloat64GenePopulation;

import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class SwapMutatorTest extends MutatorTestBase {

	@Override
	public Alterer<Float64Gene> newAlterer(double p) {
		return new SwapMutator<Float64Gene>(p);
	}
	
	@Override
	@Test(dataProvider = "alterCountParameters") 
	public void alterCount(
		final Integer ngenes, 
		final Integer nchromosomes, 
		final Integer npopulation
	) {
		final Population<Float64Gene, Float64> p1 = newFloat64GenePopulation(
					ngenes, nchromosomes, npopulation
				);
		final Population<Float64Gene, Float64> p2 = p1.copy();
		Assert.assertEquals(p2, p1);
		
		final Alterer<Float64Gene> mutator = newAlterer(0.01);
		
		final int alterations = mutator.alter(p1, 1);
		final int diff = diff(p1, p2);
		
		if (ngenes == 1) {
			Assert.assertEquals(alterations, 0);
		} else {
			Assert.assertTrue(alterations >= diff/2, String.format("%d >= %d", alterations, diff/2));
			Assert.assertTrue(alterations < 2*diff, String.format("%d < %d", alterations, 2*diff));
			
		}
	}
	
	@Override
	@Test(dataProvider = "alterProbabilityParameters")
	public void alterProbability(
		final Integer ngenes, 
		final Integer nchromosomes, 
		final Integer npopulation,
		final Double p
	) {		
		super.alterProbability(ngenes, nchromosomes, npopulation, p);
	}

	@Override
	@DataProvider(name = "alterProbabilityParameters")
	public Object[][] alterProbabilityParameters() {
		return new Object[][] {
				//    ngenes,       nchromosomes     npopulation
				{ new Integer(180),  new Integer(1),  new Integer(150), new Double(0.15) },
				{ new Integer(180),  new Integer(2),  new Integer(150), new Double(0.15) },
				{ new Integer(180),  new Integer(15), new Integer(150), new Double(0.15) },
				
				{ new Integer(180),  new Integer(1),  new Integer(150), new Double(0.5) },
				{ new Integer(180),  new Integer(2),  new Integer(150), new Double(0.5) },
				{ new Integer(180),  new Integer(15), new Integer(150), new Double(0.5) },				
				
				{ new Integer(180),  new Integer(1),  new Integer(150), new Double(0.85) },
				{ new Integer(180),  new Integer(2),  new Integer(150), new Double(0.85) },
				{ new Integer(180),  new Integer(15), new Integer(150), new Double(0.85) }
		};
	}
	
}
