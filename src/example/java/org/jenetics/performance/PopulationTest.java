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
package org.jenetics.performance;

import java.util.Iterator;

import org.jscience.mathematics.number.Float64;

import org.jenetics.FitnessFunction;
import org.jenetics.Float64Chromosome;
import org.jenetics.Float64Gene;
import org.jenetics.Genotype;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.util.Array;
import org.jenetics.util.ArrayUtils;
import org.jenetics.util.Timer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class PopulationTest extends PerfTest {

	private int N = 1000000;
	private final int LOOPS = 1000;
	
	private final Population<Float64Gene, Float64> _population = newFloat64GenePopulation(
			1, 1, N
		);
	
	public PopulationTest() {
		super("Population");
	}

	@Override
	protected int calls() {
		return N;
	}

	private void iterator() {
		final Timer timer = newTimer("iterator");
		
		for (int i = LOOPS; --i >= 0;) {
			timer.start();
			for (Iterator<?> it = _population.iterator(); it.hasNext();) {
				it.next();
			}
			timer.stop();
		}
	}
	
	private void iterator2() {
		final Timer timer = newTimer("iterator2");
		
		for (int i = LOOPS; --i >= 0;) {
			timer.start();
			for (@SuppressWarnings("unused") Object value : _population) {
			}
			timer.stop();
		}
	}
	
	private void sort() {
		final Timer timer = newTimer("sort");
		
		for (int i = 50; --i >= 0;) {
			timer.start();
			_population.sort();
			timer.stop();
			
			ArrayUtils.shuffle(_population);
		}	
	}
	
	@Override
	protected PerfTest measure() {
		iterator();
		iterator2();
		sort();
		return this;
	}
	
	
	private static final class Continous 
		implements FitnessFunction<Float64Gene, Float64> 
	{
		private static final long serialVersionUID = 1L;
		
		@Override
		public Float64 evaluate(Genotype<Float64Gene> genotype) {
			return genotype.getChromosome().getGene().getAllele(); 
		}
	}

	private static final FitnessFunction<Float64Gene, Float64> FF = new Continous();
	
	private static final Population<Float64Gene, Float64> newFloat64GenePopulation(
		final int ngenes, 
		final int nchromosomes, 
		final int npopulation
	) {
		final Array<Float64Chromosome> chromosomes = 
			new Array<Float64Chromosome>(nchromosomes);
		
		for (int i = 0; i < nchromosomes; ++i) {
			chromosomes.set(i, new Float64Chromosome(0, 10, ngenes));
		}	
		
		final Genotype<Float64Gene> genotype = Genotype.valueOf(chromosomes.toISeq());
		final Population<Float64Gene, Float64> population = 
			new Population<Float64Gene, Float64>(npopulation);
		
		for (int i = 0; i < npopulation; ++i) {
			population.add(Phenotype.valueOf(genotype.newInstance(), FF, 0));
		}	
		
		return population;
	}

}
