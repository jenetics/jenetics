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

import org.jscience.mathematics.function.Function;
import org.jscience.mathematics.number.Float64;
import org.testng.Assert;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class StatisticsAssert {

	private StatisticsAssert() {
	}
	
	public static <C extends Comparable<? super C>> void assertDistribution(
		final Histogram<C> histogram,
		final Distribution<C> distribution
	) {
		final Function<C, Float64> cdf = distribution.cdf();
		final double χ2 = histogram.χ2(cdf);
		
		// TODO: remove magic number
		Assert.assertTrue(
				χ2 < 28, 
				String.format(
						"The histogram doesn't follow the distribution %s. " +
						"χ2 must be smaller than 28: %f", 
						distribution, χ2
					)
			); 
	}
	
}
