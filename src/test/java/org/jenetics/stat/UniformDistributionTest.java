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
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class UniformDistributionTest {

	@Test 
	public void pdf() {
		final UniformDistribution<Double> dist = new UniformDistribution<Double>(0.0, 10.0);
		final Function<Double, Float64> pdf = dist.pdf();
		
		Assert.assertEquals(pdf.evaluate(0.00), Float64.valueOf(0.1));
		Assert.assertEquals(pdf.evaluate(1.15), Float64.valueOf(0.1));
		Assert.assertEquals(pdf.evaluate(2.24), Float64.valueOf(0.1));
		Assert.assertEquals(pdf.evaluate(3.43), Float64.valueOf(0.1));
		Assert.assertEquals(pdf.evaluate(4.42), Float64.valueOf(0.1));
		Assert.assertEquals(pdf.evaluate(5.59), Float64.valueOf(0.1));
		Assert.assertEquals(pdf.evaluate(10.0), Float64.valueOf(0.1));
		
		Assert.assertEquals(pdf.evaluate(-0.01), Float64.valueOf(0.0));
		Assert.assertEquals(pdf.evaluate(10.01), Float64.valueOf(0.0));
	}
	
	@Test
	public void pdfToString() {
		final UniformDistribution<Double> dist = new UniformDistribution<Double>(0.0, 10.0);
		final Function<Double, Float64> pdf = dist.pdf();
		
		Assert.assertEquals(pdf.toString(), "p(x) = 0.1");
	}
	
	@Test
	public void cdf() {
		final UniformDistribution<Double> dist = new UniformDistribution<Double>(0.0, 10.0);
		final Function<Double, Float64> cdf = dist.cdf();
		
		Assert.assertEquals(cdf.evaluate(-9.0), Float64.valueOf(0.0));
		Assert.assertEquals(cdf.evaluate(0.0), Float64.valueOf(0.0));
		Assert.assertEquals(cdf.evaluate(1.0), Float64.valueOf(0.1));
		Assert.assertEquals(cdf.evaluate(2.0), Float64.valueOf(0.2));
		Assert.assertEquals(cdf.evaluate(3.0), Float64.valueOf(0.3));
		Assert.assertEquals(cdf.evaluate(4.0), Float64.valueOf(0.4));
		Assert.assertEquals(cdf.evaluate(5.0), Float64.valueOf(0.5));
		Assert.assertEquals(cdf.evaluate(6.0), Float64.valueOf(0.6));
		Assert.assertEquals(cdf.evaluate(7.0), Float64.valueOf(0.7));
		Assert.assertEquals(cdf.evaluate(8.0), Float64.valueOf(0.8));
		Assert.assertEquals(cdf.evaluate(9.0), Float64.valueOf(0.9));
		Assert.assertEquals(cdf.evaluate(10.0), Float64.valueOf(1.0));
		Assert.assertEquals(cdf.evaluate(19.0), Float64.valueOf(1.0));
	}
	
	@Test
	public void cdfToString() {
		final UniformDistribution<Double> dist = new UniformDistribution<Double>(1.0, 10.0);
		final Function<Double, Float64> cdf = dist.cdf();
		
		Assert.assertEquals(cdf.toString(), "P(x) = (x - 1.0)/(10.0 - 1.0)");
	}
	
}
