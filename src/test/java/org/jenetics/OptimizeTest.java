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

import java.io.Serializable;
import java.util.Comparator;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class OptimizeTest {

	private static class FF
		implements Function<Genotype<Float64Gene>, Double>,
					Serializable
	{
		private static final long serialVersionUID = 6215331666272441749L;

		@Override
		public Double apply(Genotype<Float64Gene> genotype) {
			return genotype.getGene().doubleValue();
		}
	}
	
	private static final FF _ff = new FF();
	
	private static Phenotype<Float64Gene, Double> pt(double value) {
		return Phenotype.valueOf(Genotype.valueOf(new Float64Chromosome(Float64Gene.valueOf(value, 0, 10))), _ff, 0);
	}
	
	@Test
	public void comparator() {
		Comparator<Phenotype<Float64Gene, Double>> comp =
			Optimize.MAXIMUM.<Phenotype<Float64Gene, Double>>descending();
		Assert.assertTrue(comp.compare(pt(2), pt(3)) > 0);
		Assert.assertTrue(comp.compare(pt(2), pt(2)) == 0);
		Assert.assertTrue(comp.compare(pt(5), pt(3)) < 0);
		
		comp = Optimize.MINIMUM.<Phenotype<Float64Gene, Double>>descending();
		Assert.assertTrue(comp.compare(pt(4), pt(3)) > 0);
		Assert.assertTrue(comp.compare(pt(2), pt(2)) == 0);
		Assert.assertTrue(comp.compare(pt(2), pt(3)) < 0);
	}
	
	@Test
	public void compare() {
		final FF ff = new FF();
		final Phenotype<Float64Gene, Double> pt1 = Phenotype.valueOf(Genotype.valueOf(
				new Float64Chromosome(Float64Gene.valueOf(5, 0, 10))), ff, 0);
		final Phenotype<Float64Gene, Double> pt2 = Phenotype.valueOf(Genotype.valueOf(
				new Float64Chromosome(Float64Gene.valueOf(7, 0, 10))), ff, 0);
		final Phenotype<Float64Gene, Double> pt3 = Phenotype.valueOf(Genotype.valueOf(
				new Float64Chromosome(Float64Gene.valueOf(7, 0, 10))), ff, 0);		
		
		Assert.assertTrue(Optimize.MINIMUM.compare(pt1, pt2) > 0);
		Assert.assertTrue(Optimize.MAXIMUM.compare(pt1, pt2) < 0);
		Assert.assertTrue(Optimize.MINIMUM.compare(pt3, pt2) == 0);
		Assert.assertTrue(Optimize.MAXIMUM.compare(pt3, pt2) == 0);
	}
	
	@Test
	public void best() {
		final FF ff = new FF();
		final Phenotype<Float64Gene, Double> pt1 = Phenotype.valueOf(Genotype.valueOf(
				new Float64Chromosome(Float64Gene.valueOf(5, 0, 10))), ff, 0);
		final Phenotype<Float64Gene, Double> pt2 = Phenotype.valueOf(Genotype.valueOf(
				new Float64Chromosome(Float64Gene.valueOf(7, 0, 10))), ff, 0);
		final Phenotype<Float64Gene, Double> pt3 = Phenotype.valueOf(Genotype.valueOf(
				new Float64Chromosome(Float64Gene.valueOf(7, 0, 10))), ff, 0);	
		
		Assert.assertSame(Optimize.MINIMUM.best(pt1, pt2), pt1);
		Assert.assertSame(Optimize.MAXIMUM.best(pt1, pt2), pt2);
		Assert.assertSame(Optimize.MINIMUM.best(pt2, pt3), pt2);
	}
	
	@Test
	public void worst() {
		final FF ff = new FF();
		final Phenotype<Float64Gene, Double> pt1 = Phenotype.valueOf(Genotype.valueOf(
				new Float64Chromosome(Float64Gene.valueOf(5, 0, 10))), ff, 0);
		final Phenotype<Float64Gene, Double> pt2 = Phenotype.valueOf(Genotype.valueOf(
				new Float64Chromosome(Float64Gene.valueOf(7, 0, 10))), ff, 0);
		final Phenotype<Float64Gene, Double> pt3 = Phenotype.valueOf(Genotype.valueOf(
				new Float64Chromosome(Float64Gene.valueOf(7, 0, 10))), ff, 0);	
		
		Assert.assertSame(Optimize.MINIMUM.worst(pt1, pt2), pt2);
		Assert.assertSame(Optimize.MAXIMUM.worst(pt1, pt2), pt1);
		Assert.assertSame(Optimize.MINIMUM.worst(pt2, pt3), pt2);
	}
	
}





