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
package org.jenetics.stat;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.AbstractAccumulator;
import org.jenetics.util.AbstractAccumulatorCommonTests;
import org.jenetics.util.TestDataIterator;
import org.jenetics.util.TestDataIterator.Data;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class MeanTest extends AbstractAccumulatorCommonTests {
	private final String DATA = "/org/jenetics/util/statistic-moments.txt";
	
	@Override
	public AbstractAccumulator<Double> newAccumulator() {
		return new Mean<Double>();
	}
	
	@Test
	public void mean() throws IOException {
		final TestDataIterator it = new TestDataIterator(
				getClass().getResourceAsStream(DATA), "\\s"
			);
		
		try {
			final Mean<Double> moment = new Mean<Double>();
			while (it.hasNext()) {
				final Data data = it.next();
				moment.accumulate(data.number);
				
				Assert.assertEquals(moment.getMean(), data.mean);
			}
		} finally {
			it.close();
		}
	}

}





