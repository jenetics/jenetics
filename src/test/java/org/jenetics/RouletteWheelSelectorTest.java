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
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.util.Accumulators;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class RouletteWheelSelectorTest	
	extends ProbabilitySelectorTester<RouletteWheelSelector<Float64Gene, Float64>> 
{

	@Override
	protected boolean isSorted() {
		return false;
	}
	
	@Override
	protected Factory<RouletteWheelSelector<Float64Gene, Float64>> getFactory() {
		return SelectorFactories.RouletteWheelSelector;
	}
	
	@Test
	public void selectDistribution() {
		final Float64 min = Float64.ZERO;
		final Float64 max = Float64.valueOf(100);
		final int npopulation = 10000;
		final Factory<Genotype<Float64Gene>> gtf = 
			Genotype.valueOf(new Float64Chromosome(min, max));
		
		final Population<Float64Gene, Float64> population = 
			new Population<Float64Gene, Float64>();
		
		for (int i = 0; i < npopulation; ++i) {
			population.add(Phenotype.valueOf(gtf.newInstance(), TestUtils.FF, 12));
		}
		
		final Selector<Float64Gene, Float64> selector = 
			new RouletteWheelSelector<Float64Gene, Float64>();
		
		final Population<Float64Gene, Float64> selection = 
			selector.select(population, npopulation/2, Optimize.MAXIMUM);
		
		// Check the distribution of the selected population. PDF must be linear
		// increasing, since the RouletteWheelSelector is a fitness proportional
		// selector.
		final Histogram<Float64> histogram = Histogram.valueOf(min, max, 15);
		Accumulators.accumulate(
				selection, 
				histogram
					.adapt(Float64Gene.Allele)
					.adapt(Float64Chromosome.Gene)
					.adapt(Genotype.<Float64Gene>Chromosome())
					.adapt(Phenotype.<Float64Gene, Float64>Genotype())
			);
		
		// TODO: Check histogram distribution.
		System.out.println(histogram);
	}
	
}
