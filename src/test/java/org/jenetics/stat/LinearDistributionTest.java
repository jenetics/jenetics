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

import junit.framework.Assert;

import org.jenetics.stat.Distribution.Domain;
import org.jscience.mathematics.function.Function;
import org.jscience.mathematics.number.Float64;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class LinearDistributionTest {

	@Test
	public void pdf() {
		final Domain<Double> domain = new Domain<Double>(0.0, 1.0);
		final LinearDistribution<Double> dist = new LinearDistribution<Double>(domain, 0);
		final Function<Double, Float64> pdf = dist.pdf();
		
		for (int i = 0; i <= 10; ++i) {
			final double x = i/10.0;
			Assert.assertEquals(x*2, pdf.evaluate(x).doubleValue(), 0.00001);
		}
		
		Assert.assertEquals("p(x) = 2.000000·x + 0.000000", pdf.toString());
	}
	
	@Test
	public void cdf() {
		final Domain<Double> domain = new Domain<Double>(0.0, 1.0);
		final LinearDistribution<Double> dist = new LinearDistribution<Double>(domain, 0);
		final Function<Double, Float64> cdf = dist.cdf();
		
		for (int i = 0; i <= 10; ++i) {
			final double x = i/10.0;
			final double y = cdf.evaluate(x).doubleValue();
			Assert.assertEquals(x*x, y, 0.0001);
		}
		
		Assert.assertEquals("P(x) = 1.000000·x² - 0.000000·x", cdf.toString());
	}
	
}
