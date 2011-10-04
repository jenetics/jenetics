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

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public abstract class AbstractAccumulatorTester<A extends MappableAccumulator<Double>> 
	extends ObjectTester<A> 
{
	
	@Test
	public void accumulatedSamples() {
		final int SAMPLES = 12345;
		final Random random = new Random(123456);
		final MappableAccumulator<Double> accu = getFactory().newInstance();
		
		final long samples = accu.getSamples();
		
		for (int i = 0; i < SAMPLES; ++i) {
			accu.accumulate(random.nextDouble()*6);
		}
		
		Assert.assertEquals(accu._samples, SAMPLES + samples);
		Assert.assertEquals(accu.getSamples(), SAMPLES + samples);
	}
	
	@Test
	public void testClone() {
		MappableAccumulator<Double> accu1 = getFactory().newInstance();
		for (int i = 0; i < 1000; ++i) {
			accu1.accumulate(Double.valueOf(i));
		}
		
		Accumulator<Double> accu2 = accu1.clone();
		
		Assert.assertNotSame(accu1, accu2);
		Assert.assertEquals(accu1.hashCode(), accu2.hashCode());
		Assert.assertEquals(accu1, accu2);
		
		accu1.accumulate(4.5);
		Assert.assertFalse(accu1.equals(accu2));
		Assert.assertFalse(accu1.hashCode() == accu2.hashCode());
		
		accu2.accumulate(4.5);
		Assert.assertEquals(accu1.hashCode(), accu2.hashCode());
		Assert.assertEquals(accu1, accu2);
		
		accu1 = getFactory().newInstance();
		accu2 = accu1.clone();
		
		Assert.assertNotSame(accu1, accu2);
		Assert.assertEquals(accu1.hashCode(), accu2.hashCode());
		Assert.assertEquals(accu1, accu2);
	}
	
}
