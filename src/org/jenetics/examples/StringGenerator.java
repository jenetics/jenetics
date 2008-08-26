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

import org.jenetics.CharacterChromosome;
import org.jenetics.CharacterGene;
import org.jenetics.Chromosome;
import org.jenetics.SinglePointCrossover;
import org.jenetics.FitnessFunction;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.GenotypeFactory;
import org.jenetics.Mutation;
import org.jenetics.Population;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.Statistic;
import org.jenetics.util.Probability;
import org.jscience.mathematics.number.Integer64;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: StringGenerator.java,v 1.5 2008-08-26 22:29:36 fwilhelm Exp $
 */
public class StringGenerator {

	private static class Function implements FitnessFunction<CharacterGene, Integer64> {
		private static final long serialVersionUID = 644284481730863472L;
		
		private final String value;
		
		public Function(final String value) {
			this.value = value;
		}
		
		public Integer64 evaluate(Genotype<CharacterGene> genotype) {
			int matches = 0;
			Chromosome<CharacterGene> chromosome = genotype.getChromosome();
			
			for (int i = 0; i < value.length(); ++i) {
				if (chromosome.getGene(i).getCharacter().equals(value.charAt(i))) {
					++matches;
				}
			}
			
			return Integer64.valueOf(matches);
		}
		
	}
	
	
	
	public static void main(String[] args) {
		final String value = "A test string";
		
		final GenotypeFactory<CharacterGene> gtf = Genotype.valueOf(
			new CharacterChromosome(value.length())
		);
		final Function ff = new Function(value);
		final GeneticAlgorithm<CharacterGene, Integer64> 
		ga = new GeneticAlgorithm<CharacterGene, Integer64>(gtf, ff);
		
		ga.setPopulationSize(100);
		ga.setSurvivorFraction(Probability.valueOf(0.3));
		ga.setOffspringFraction(Probability.valueOf(0.7));
		ga.setMaximalPhenotypeAge(30);
		ga.setSelectors(new RouletteWheelSelector<CharacterGene, Integer64>());
		ga.setAlterer(
			new Mutation<CharacterGene>(Probability.valueOf(0.3)).append(
			new SinglePointCrossover<CharacterGene>(Probability.valueOf(0.1))
		));
		ga.setup();
		
		Population<CharacterGene, Integer64> p = ga.getPopulation();
		Statistic<CharacterGene, Integer64> stat = ga.getStatistic(); 		
		
		for (int i = 0; i < 200; ++i) {
			ga.evolve();
			p = ga.getPopulation();
			System.out.print("" + p.size() + ": ");
			System.out.println(ga.getStatistic().getBestPhenotype().toText());
			
			stat = ga.getStatistic();
		}
		
		System.out.println(stat); 
	}
	
}
