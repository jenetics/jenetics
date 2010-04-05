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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jenetics.CharacterChromosome;
import org.jenetics.CharacterGene;
import org.jenetics.CompositeAlterer;
import org.jenetics.FitnessFunction;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.SinglePointCrossover;
import org.jenetics.util.CharSet;
import org.jenetics.util.EvaluatorRegistry;
import org.jenetics.util.Factory;
import org.jscience.mathematics.number.Integer64;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class StringGenerator {

	private static class Function implements FitnessFunction<CharacterGene, Integer64> {
		private static final long serialVersionUID = 644284481730863472L;
		
		private final String value;
		
		public Function(final String value) { 
			this.value = value;
		}
		
		public Integer64 evaluate(final Genotype<CharacterGene> genotype) {
			final CharacterChromosome chromosome = (CharacterChromosome)genotype.getChromosome();
			return Integer64.valueOf(value.length() - levenshtein(value, chromosome));
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		final int maxThreads = 2;
		final ExecutorService pool = Executors.newFixedThreadPool(maxThreads);
		
		final String value = 
			"To be, or not to be: that is the question: " +
			"Whether 'tis nobler in the mind to suffer...";
		
		final CharSet chars = new CharSet(CharSet.expand("a-zA-Z.,:' "));
		final Factory<Genotype<CharacterGene>> gtf = Genotype.valueOf(
			new CharacterChromosome(chars, value.length())
		);
		final Function ff = new Function(value);
		final GeneticAlgorithm<CharacterGene, Integer64> 
		ga = new GeneticAlgorithm<CharacterGene, Integer64>(gtf, ff);
		
		ga.setPopulationSize(5000);
		ga.setOffspringFraction(0.7);
		ga.setMaximalPhenotypeAge(50);
		ga.setSelectors(new org.jenetics.RouletteWheelSelector<CharacterGene, Integer64>());
		ga.setSelectors(new org.jenetics.LinearRankSelector<CharacterGene, Integer64>());
		ga.setSelectors(new org.jenetics.BoltzmannSelector<CharacterGene, Integer64>());
		ga.setAlterer(new CompositeAlterer<CharacterGene>(
			new Mutator<CharacterGene>(0.05),
			new SinglePointCrossover<CharacterGene>(0.1)
		));
		EvaluatorRegistry.setEvaluator(new org.jenetics.util.ConcurrentEvaluator());

		GAUtils.execute(ga, 50);
		
		java.io.FileOutputStream out = new java.io.FileOutputStream("/home/franzw/population.xml");
		org.jenetics.util.IOUtils.writeXMLAndClose(out, ga.getPopulation());
		out.close();
		
		pool.shutdown();
	}
	
	
	/**
	 * Return Levenshtein distance of two character sequences.
	 */
	private static int levenshtein(final CharSequence s, final CharSequence t) {
		//Step 1:
		final int n = s.length();
		final int m = t.length();
		if (n == 0 || m == 0) {
			return Math.max(n, m);
		}
		
		//Step 2:
		int d[][] = new int[n + 1][m +1];
		for (int i = 0; i <= n; ++i) {
			d[i][0] = i;
		}
		for (int j = 0; j <= m; ++j) {
			d[0][j] = j;
		}
		
		//Step 3:
		for (int i = 1; i <= n; ++i) {
			final char si = s.charAt(i - 1);
			
			//Step 4:
			for (int j = 1; j <= m; ++j) {
				final char tj = t.charAt(j - 1);
				
				//Step 5:
				int cost = 0;
				if (si == tj) {
					cost = 0;
				} else {
					cost = 1;
				}
				
				//Step 6:
				d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
			}
		}
		
		//Step 7:
		return d[n][m];
	}

	private static int min(final int a, final int b, final int c) {
		int m = a;
		if (b < m) {
			m = b;
		}
		if (c < m) {
			m = c;
		}
		return m;
	}
	
}
