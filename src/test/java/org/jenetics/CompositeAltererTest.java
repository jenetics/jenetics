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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class CompositeAltererTest {

	@Test
	public void join() {
		CompositeAlterer<Float64Gene> alterer = CompositeAlterer.join(
				new Mutator<Float64Gene>(),
				new GaussianMutator<Float64Gene>()
			);
		
		Assert.assertEquals(alterer.getAlterers().length(), 2);
		Assert.assertEquals(alterer.getAlterers().get(0), new Mutator<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(1), new GaussianMutator<Float64Gene>());
		
		alterer = CompositeAlterer.join(alterer, new MeanAlterer<Float64Gene>());
		
		Assert.assertEquals(alterer.getAlterers().length(), 3);
		Assert.assertEquals(alterer.getAlterers().get(0), new Mutator<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(1), new GaussianMutator<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(2), new MeanAlterer<Float64Gene>());
		
		alterer = new CompositeAlterer<Float64Gene>(
				new MeanAlterer<Float64Gene>(),
				new SwapMutator<Float64Gene>(),
				alterer,
				new SwapMutator<Float64Gene>()
			);
		
		Assert.assertEquals(alterer.getAlterers().length(), 6);
		Assert.assertEquals(alterer.getAlterers().get(0), new MeanAlterer<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(1), new SwapMutator<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(2), new Mutator<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(3), new GaussianMutator<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(4), new MeanAlterer<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(5), new SwapMutator<Float64Gene>());
	}
	
}
