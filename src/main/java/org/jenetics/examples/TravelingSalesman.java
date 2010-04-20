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
import jsr166y.ForkJoinPool;

import org.jenetics.Chromosome;
import org.jenetics.CompositeAlterer;
import org.jenetics.FitnessFunction;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.Integer64Gene;
import org.jenetics.Optimize;
import org.jenetics.PartiallyMatchedCrossover;
import org.jenetics.PermutationChromosome;
import org.jenetics.SwapMutator;
import org.jenetics.util.EvaluatorRegistry;
import org.jenetics.util.Factory;
import org.jenetics.util.ForkJoinEvaluator;

/**
 * The classical <a href="http://en.wikipedia.org/wiki/Travelling_salesman_problem">TSP</a>.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class TravelingSalesman {
	
	private static class Function implements FitnessFunction<Integer64Gene, Double> {
		private static final long serialVersionUID = 8402072476064049463L;
		
		private final double[][] _adjacence;
		
		public Function(final double[][] adjacence) {
			_adjacence = adjacence;
		}
		
		@Override
		public Double evaluate(final Genotype<Integer64Gene> genotype) {
			final Chromosome<Integer64Gene> path = genotype.getChromosome();
			
			double length = 0.0;
			for (int i = 0, n = path.length(); i < n; ++i) {
				final int from = path.getGene(i).intValue();
				final int to = path.getGene((i + 1)%n).intValue();
				length += _adjacence[from][to];
			}
			return length;
		}
	}
	
	public static void main(String[] args) {
		final int stops = 20;
		
		final FitnessFunction<Integer64Gene, Double> ff = new Function(adjacencyMatrix(stops));
		final Factory<Genotype<Integer64Gene>> gtf = Genotype.valueOf(
			new PermutationChromosome(stops)
		);
		final GeneticAlgorithm<Integer64Gene, Double> ga = GeneticAlgorithm.valueOf(gtf, ff, Optimize.MINIMUM);
		ga.setPopulationSize(200);
        ga.setAlterer(new CompositeAlterer<Integer64Gene>(
            new SwapMutator<Integer64Gene>(0.2), 
            new PartiallyMatchedCrossover<Integer64Gene>(0.3)
        ));
        //ga.setSelectors(new org.jenetics.MonteCarloSelector<IntegerGene, Double>());
        //ga.setAlterer(new org.jenetics.NullAlterer<IntegerGene>());
        
//        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//        EvaluatorRegistry.setEvaluator(new ThreadedEvaluator(pool));
        
        ForkJoinPool pool = new ForkJoinPool();
        EvaluatorRegistry.setEvaluator(new ForkJoinEvaluator(pool));
        try {
        	GAUtils.execute(ga, 100);
        } finally {
        	pool.shutdown();
        }
        
//		try {
//			XMLSerializer.write(ga.getPopulation(), new FileOutputStream("/home/franzw/population.xml"));
//			ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream("/home/franzw/population.obj"));
//			oout.writeObject(ga.getPopulation());
//			oout.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
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





