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

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.sin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jenetics.Chromosome;
import org.jenetics.ConcurrentStatisticCalculator;
import org.jenetics.FitnessFunction;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.GenotypeFactory;
import org.jenetics.IntegerGene;
import org.jenetics.Mutation;
import org.jenetics.PartiallyMatchedCrossover;
import org.jenetics.PermutationChromosome;
import org.jenetics.Phenotype;
import org.jenetics.Probability;

/**
 * The classical <a href="http://en.wikipedia.org/wiki/Travelling_salesman_problem">TSP</a>.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: TravelingSalesman.java,v 1.5 2008-07-08 17:03:36 fwilhelm Exp $
 */
public class TravelingSalesman {
	
	private static class Function implements FitnessFunction<IntegerGene> {
		private static final long serialVersionUID = 8402072476064049463L;
		
		private final double[][] adjacence;
		
		public Function(final double[][] adjacence) {
			this.adjacence = adjacence;
		}
		
		@Override
		public double evaluate(final Genotype<IntegerGene> genotype) {
			final Chromosome<IntegerGene> path = genotype.getChromosome();
			
			double length = 0.0;
			for (int i = 0, n = path.length(); i < n; ++i) {
				final int from = path.getGene(i).intValue();
				final int to = path.getGene((i + 1)%n).intValue();
				length -= adjacence[from][to];
			}
			return length;
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		final int stops = 10;
		
		final FitnessFunction<IntegerGene> ff = new Function(adjacencyMatrix(stops));
		final GenotypeFactory<IntegerGene> gtf = Genotype.valueOf(
			new PermutationChromosome(stops)
		);
		final GeneticAlgorithm<IntegerGene> ga = new GeneticAlgorithm<IntegerGene>(gtf, ff);
		ga.setPopulationSize(10000);
		
		final int threads = 4;
		final ExecutorService pool = Executors.newFixedThreadPool(threads);
		ga.setStatisticCalculator(
			new ConcurrentStatisticCalculator(threads, pool)
		);
		
        ga.setAlterer(
            new Mutation<IntegerGene>(Probability.valueOf(0.1), 
            new PartiallyMatchedCrossover<IntegerGene>(Probability.valueOf(0.3)))
        );
        
        long start = System.currentTimeMillis();
        ga.setup();
        for (int i = 0; i < 30; ++i) {
        	ga.evolve();
        	Phenotype<IntegerGene> bpt = ga.getStatistic().getBestPhenotype();
        	System.out.println(
        		bpt + " --> " + bpt.getFitness() +  " : " + 
        			ga.getStatistic().getFitnessVariance()
        	);
        }
        pool.awaitTermination(1, TimeUnit.SECONDS);
        pool.shutdown();
        
        System.out.println("Best path found:");
        System.out.println(ga.getBestPhenotype() + " --> " + ga.getBestPhenotype().getFitness());
        System.out.println("Minimal tour length: " + (chord(stops, 1, RADIUS)*stops));
        long end = System.currentTimeMillis();
        System.out.println("Time: " + ((end -start)/1000.0) + "s");
	}
	
	/**
	 * All points in the created adjacency matrix lie on a circle. So it is easy 
	 * to check the quality of the solution found by the GA.
	 */
	private static double[][] adjacencyMatrix(int stops) {
		double[][] matrix = new double[stops][stops];
		for (int i = 0; i < stops; ++i) {
			for (int j = 0; j < stops; ++j) {
				matrix[i][j] = chord(stops, abs(i - j), RADIUS);
			}
		}
		return matrix;
	}
	private static double chord(int stops, int i, double r) {
		return 2.0*r*abs(sin((PI*i)/stops));
	}
	private static double RADIUS = 10.0;
}





