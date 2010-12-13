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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 * 	 
 */
package org.jenetics.examples;

import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.CompositeAlterer;
import org.jenetics.FitnessFunction;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.NumberStatistics;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.util.Factory;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class OnesCounting {

	private static class OneCounter implements FitnessFunction<BitGene, Integer> {
		private static final long serialVersionUID = 1L;

		@Override
		public Integer evaluate(Genotype<BitGene> genotype) {
			int count = 0;
			for (BitGene gene : genotype.getChromosome()) {
				if (gene.getBit()) {
					++count;
				}
			}
			return count;
		}
		
		@Override
		public String toString() {
			return "OneCounter";
		}
		
	}
	

	public static void main(String[] args) throws Exception {
		final Factory<Genotype<BitGene>> gtf = Genotype.valueOf(
			BitChromosome.valueOf(20, 0.15)
		);
		final OneCounter ff = new OneCounter();
		final GeneticAlgorithm<BitGene, Integer> ga = new GeneticAlgorithm<BitGene, Integer>(gtf, ff);
		
		ga.setStatisticsCalculator(new NumberStatistics.Calculator<BitGene, Integer>());
		ga.setPopulationSize(50);
		ga.setSelectors(new RouletteWheelSelector<BitGene, Integer>());
		ga.setAlterer(new CompositeAlterer<BitGene>(
			new Mutator<BitGene>(0.55), 
			new SinglePointCrossover<BitGene>(0.06)
		));
		
		final int generations = 100;
		
		GAUtils.printConfig(
				"Ones counting", 
				ga, 
				generations, 
				((CompositeAlterer<?>)ga.getAlterer()).getAlterers().toArray()
			);
		
		GAUtils.execute(ga, generations, 10);
	}

}
