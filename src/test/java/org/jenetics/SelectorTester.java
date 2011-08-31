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

import static org.jenetics.stat.StatisticsAssert.assertDistribution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jscience.mathematics.number.Float64;
import org.testng.annotations.Test;

import org.jenetics.stat.Distribution;
import org.jenetics.stat.Distribution.Domain;
import org.jenetics.stat.Histogram;
import org.jenetics.util.Accumulators;
import org.jenetics.util.Factory;
import org.jenetics.util.ObjectTester;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public abstract class SelectorTester<S extends Selector<Float64Gene, Float64>> 
	extends ObjectTester<S> 
{

	protected final Domain<Float64> _domain = new Domain<Float64>(
			Float64.ZERO, Float64.valueOf(100)
		); 
	
	
	protected S getSelector() {
		return getFactory().newInstance();
	}
	
	protected abstract Distribution<Float64> getDistribution();
	
	@Test(invocationCount = 20, successPercentage = 95)
	public void selectDistribution() throws InterruptedException, ExecutionException {
		final Float64 min = _domain.getMin();
		final Float64 max = _domain.getMax();
		final int npopulation = 10000;
		final Factory<Genotype<Float64Gene>> 
		gtf = Genotype.valueOf(new Float64Chromosome(min, max));
		
		
		final Population<Float64Gene, Float64> population = 
			new Population<Float64Gene, Float64>(npopulation);
		
		for (int i = 0; i < npopulation; ++i) {
			population.add(Phenotype.valueOf(gtf.newInstance(), TestUtils.FF, 12));
		}
		
		final S selector = getSelector();
		
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
					.adapt(Phenotype.<Float64Gene>Genotype())
			);
		
		
		//assertDistribution(histogram, getDistribution());
	}
	
	static class HistCall<S extends Selector<Float64Gene, Float64>> 
		implements Callable<Histogram<Float64>> 
	{
		private final int _npopulation;
		private final Factory<Genotype<Float64Gene>> _gtf;
		private final S _selector;
		private final Float64 _min;
		private final Float64 _max;
		
		public HistCall(
			final int npopulation, 
			final Factory<Genotype<Float64Gene>> gtf,
			final S selector,
			final Float64 min,
			final Float64 max
		) {
			_npopulation = npopulation;
			_gtf = gtf;
			_selector = selector;
			_min = min;
			_max = max;
		}
		
		@Override
		public Histogram<Float64> call() throws Exception {
			final Population<Float64Gene, Float64> population = 
				new Population<Float64Gene, Float64>(_npopulation);
			
			for (int i = 0; i < _npopulation; ++i) {
				population.add(Phenotype.valueOf(_gtf.newInstance(), TestUtils.FF, 12));
			}
						
			final Population<Float64Gene, Float64> selection = 
				_selector.select(population, _npopulation/2, Optimize.MAXIMUM);
			
			// Check the distribution of the selected population. PDF must be linear
			// increasing, since the RouletteWheelSelector is a fitness proportional
			// selector.
			final Histogram<Float64> histogram = Histogram.valueOf(_min, _max, 15);
			Accumulators.accumulate(
					selection, 
					histogram
						.adapt(Float64Gene.Allele)
						.adapt(Float64Chromosome.Gene)
						.adapt(Genotype.<Float64Gene>Chromosome())
						.adapt(Phenotype.<Float64Gene>Genotype())
				);	
			
			return histogram;
		}
		
	}
	
}





