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
package org.jenetics.examples;

import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.FitnessFunction;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.GenotypeFactory;
import org.jenetics.Mutation;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.util.Probability;
import org.jscience.mathematics.number.Integer64;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: OnesCounting.java,v 1.5 2008-09-23 18:01:53 fwilhelm Exp $
 */
public class OnesCounting {

	private static class OneCounter implements FitnessFunction<BitGene, Integer64> {
		private static final long serialVersionUID = 5457381926611887312L;

		public Integer64 evaluate(Genotype<BitGene> genotype) {
			int count = 0;
			for (BitGene gene : genotype.getChromosome()) {
				if (gene.getBit()) {
					++count;
				}
			}
			return Integer64.valueOf(count);
		}
		
	}
	

	public static void main(String[] args) throws Exception {
		final GenotypeFactory<BitGene> gtf = Genotype.valueOf(
			BitChromosome.valueOf(20, Probability.valueOf(0.15))
		);
		final OneCounter ff = new OneCounter();
		final GeneticAlgorithm<BitGene, Integer64> ga = new GeneticAlgorithm<BitGene, Integer64>(gtf, ff);
		
		ga.setPopulationSize(50);
		ga.setSelectors(new RouletteWheelSelector<BitGene, Integer64>());
		ga.setAlterer(
			new Mutation<BitGene>(Probability.valueOf(0.55), 
			new SinglePointCrossover<BitGene>(Probability.valueOf(0.06)))
		);
		
		GAUtils.execute(ga, 100);
	}

}
