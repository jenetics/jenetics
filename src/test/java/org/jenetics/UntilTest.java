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

import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class UntilTest {

	@Test
	public void generation() {
		final GeneticAlgorithm<Float64Gene, Float64> ga = TestUtils.GA();
		ga.setup();
		ga.evolve(Until.Generation(10));
		Assert.assertEquals(ga.getGeneration(), 10);
		
		ga.evolve(5);
		ga.evolve(Until.Generation(10));
		Assert.assertEquals(ga.getGeneration(), 15);
		
		ga.evolve(6);
		ga.evolve(Until.Generation(50));
		Assert.assertEquals(ga.getGeneration(), 50);
	}
	
}
