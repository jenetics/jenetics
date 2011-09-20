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

/**
 * Provides a <a href="http://en.wikipedia.org/wiki/Genetic_algorithm" >
 * Genetic Algorithm</a> (GA) implementation. Genetic Algorithms was first 
 * proposed and analyzed by <a href="http://en.wikipedia.org/wiki/John_Henry_Holland" >
 * John Holland</a> in 1975. The GA consists of the following steps:
 * [code]
 *     Initialize population;
 *     Measure fitness of populaton;
 *     while (!stopCondition) {
 *         select offsprings;select survivors;
 *         increase survirvor age;
 *         alter offsprings;
 *         add survivors and offsprings to new population;
 *         measure fitness of new population;
 *     } 
 * [/code]
 * 
 * <h3>Data structures</h3>
 * <p><img alt="Structure Diagram" src="doc-files/structure.png" ></img><p/>
 * 
 * The diagram above shows the main data structures of the GA implementation. 
 * The {@link org.jenetics.Gene} is the base of the building block. Genes are 
 * aggregated in {@link org.jenetics.Chromosome}s. One to n Chromosomes are 
 * aggregated in {@link org.jenetics.Genotype}s. A Genptype and a 
 * {@link org.jenetics.FitnessFunction} form the {@link org.jenetics.Phenotype}.
 * Phenotypes are collected into a {@link org.jenetics.Population}.
 * 
 * 
 * <h3>Settting up the {@link org.jenetics.GeneticAlgorithm}</h3> 
 * 
 * The minimum GA setup needs a genotype {@link org.jenetics.util.Factory} and 
 * an {@link org.jenetics.FitnessFunction}. The genotype 
 * {@link org.jenetics.Genotype} implements the {@link org.jenetics.util.Factory} 
 * interface and can be used as prototype.
 * [code]
 *     //Defining a Genotype prototype.
 *     Genotype<BitGene> gt = Genotype.create(
 *             BitChromosome.valueOf(10, 0.5);
 *         );
 *     FitnessFunction<BitGene, Float64> ff = ...//FitnessFunction implementation
 *     GeneticAlgorithm<BitGene, Float64> ga = new GeneticAlgorithm.valueOf(gt, ff);
 * [/code]
 * 
 * All other needed GA parameters are initialized with default values. Therefore
 * the GA is ready for use now.
 * 
 * [code]
 *     ga.setup();
 *     ga.evolve(100);
 *     System.out.println(ga);
 * [/code]
 * 
 * <p>
 * The {@link org.jenetics.GeneticAlgorithm#setup} call creates the initial 
 * population and calculates its fitness. Then the GA evolves 100 generations 
 * and prints the (last) populations statistic.
 * <p/>
 * 
 * In a more advanced setup you want to define mutation and/or selection 
 * strategies.
 * 
 * [code]
 *     ...
 *     Selector<BitGene> selector = new StochasticUniversalSelector();
 *     ga.setOffspringSelector(selector);
 *     ga.setSurvivorSelector(selector);
 *     ga.setAlterer(new SinglePointCrossover<BitGene>(0.1));
 *     ga.addAlterer(new Mutation<BitGene>(0.001));
 * [/code]
 * 
 * You can define selection strategies for survivors and offsprings 
 * independently. The Alterers are concatenated, first the crossover (with 
 * crossover probability 0.1) is performed and than the chromosomes are mutated 
 * (with a probability 0.001).
 *
 *
 * <h3>Examles</h3>
 *
 * <h4>0/1 knapsack problem</h4>
 *
 * In the knapsack problem a set of items, together with their size and value, 
 * is given. The task is to select a disjoint subset so that the total size does 
 * not exeed the knapsacks size. 
 * (<a href="http://en.wikipedia.org/wiki/Knapsack_problem">Wikipedia: 
 * Knapsack problem</a>)<p/>
 * 
 * For the 0/1 knapsack problem we define a {@link org.jenetics.BitChromosome}, 
 * one bit for each item. If the i<sup>th</sup> {@link org.jenetics.BitGene} is 
 * set to one the i<sup>th</sup> item is selected.
 * 
 * [code]
 *     class Item implements Serializable {
 *         private static final long serialVersionUID = 1L;
 *         public double size;
 *         public double value;
 *     }
 *     
 *     class KnappsackFunction implements FitnessFunction<BitGene, Float64> {
 *         private static final long serialVersionUID = -1L;
 *         
 *         private final Item[] _items;
 *         private final double _knapsackSize;
 *         
 *         public KnappsackFunction(final Item[] items, double knapsackSize) {
 *             _items = items;
 *             _knapsackSize = knapsackSize;
 *         }
 *         
 *         public Item[] getItems() {
 *             return _items;
 *         }
 *         
 *         public Float64 evaluate(final Genotype<BitGene> genotype) {
 *             final Chromosome<BitGene> ch = genotype.getChromosome();
 *              
 *             double size = 0;
 *             double value = 0;
 *             for (int i = 0, n = ch.length(); i < n; ++i) {
 *                 if (ch.getGene(i).getBit()) {
 *                     size += _items[i].size;
 *                     value += _items[i].value;
 *                 }
 *             }
 *             
 *             if (size > _knapsackSize) {
 *                 return Float64.ZERO;
 *             } else {
 *                 return Float64.valueOf(value);
 *             }
 *         }
 *     }
 *     
 *     public class Knapsack {
 *         private static KnappsackFunction newFitnessFuntion(int n, double knapsackSize) {
 *             Item[] items = new Item[n];
 *             for (int i = 0; i < items.length; ++i) {
 *                 items[i] = new Item();
 *                 items[i].size = (Math.random() + 1)*10;
 *                 items[i].value = (Math.random() + 1)*15;
 *             }
 *             
 *             return new KnappsackFunction(items, knapsackSize);
 *         }
 *     
 *         public static void main(String[] argv) throws Exception {
 *             //Defining the fitness function and the genotype.
 *             final KnappsackFunction ff = newFitnessFuntion(15, 100);
 *             final Factory<Genotype<BitGene>> genotype = Genotype.valueOf(
 *                     BitChromosome.valueOf(15, 0.5)
 *                 );
 *             
 *             final GeneticAlgorithm<BitGene, Float64> ga = GeneticAlgorithm.valueOf(genotype, ff);
 *             ga.setMaximalPhenotypeAge(10);
 *             ga.setPopulationSize(1000);
 *             ga.setSelectors(new RouletteWheelSelector<BitGene, Float64>());
 *             ga.setAlterer(new Mutation<BitGene>(0.115));
 *             ga.addAlterer(new SinglePointCrossover<BitGene>(0.06));
 *             
 *             ga.setup();
 *             ga.evolve(100);
 *             System.out.println(ga);
 *         }
 *     }
 * [/code]
 * 
 * 
 * <h4>Ones counting</h4>
 * 
 * Ones counting is one of the simplest model-problem and consists of a binary
 * chromosome. The fitness of a {@link org.jenetics.Genotype} is proportional to 
 * the number of ones. The {@link org.jenetics.FitnessFunction} looks like this:
 * 
 * [code]
 *     class OneCounter implements FitnessFunction<BitGene, Float64> {
 *         public Float64 eval(final Genotype<BitGene, Float64> genotype) {
 *             int count = 0;
 *             for (BitGene gene : genotype.getChromosome()) {
 *                 if (gene.getBit()) {
 *                     ++count;
 *                 }
 *             }
 *            return Float64.valueOf(count);
 *        }
 *    }
 * [/code]
 * 
 * 
 * <h4>Real function</h4>
 * The GA also works with NumberGenes. In the given example the FitnessFunction 
 * tries to find the value where the sinus becomes a maximum.
 * 
 * [code]
 *     class RealFunction implements FitnessFunction<Float64Gene, Float64> {
 *         public Float64 evaluate(final Genotype<Float64Gene> genotype) {
 *             return Float64.valueOf(Math.sin(genotype.getChromosome().getGene(0).doubleValue()));
 *         }
 *     }
 * [/code]
 * 
 * To narrow the search space a number range for the 
 * {@link org.jenetics.Float64Gene} must be definde. In our example we restrict 
 * the values to the closed interval [0,2PI].
 * 
 * [code]
 *     //The Genotype implements the genotype Factory interface.
 *     Factory<Genotype<Float64Gene>> gtf = Genotype.newGenotype(
 *             DoubleChromosome.valueOf(0, 2*Math.PI)
 *         );
 *     
 *     GeneticAlgorithm<Float64Gene, Float64> ga = new GeneticAlgorithm.valueOf(gtf, ff);
 *     ga.setFitnessScaler(new PowerScaler(2.0));
 *     ga.setPopulationSize(20);
 *     ga.setAlterer(new Mutation<Float64Gene>(0.05));
 *     ga.addAlterer(new MeanAlterer<Float64Gene>(0.5));
 *     
 *     ga.setup();
 *     for (int i = 0; i < 50; ++i) {
 *         ga.evolve();
 *         System.out.println(ga);
 *     }
 *     
 *     System.out.println(ga.getBestStatistic());
 * [/code]
 * 
 * Here a sample of the generated output:
 * 
 * <pre>
 *     1: (best): [[230.824557520235]] --> 0.23980732495842583
 *     2: (best): [[230.824557520235]] --> 0.23980732495842583
 *     3: (best): [[230.824557520235]] --> 0.23980732495842583
 *     4: (best): [[230.824557520235]] --> 0.23980732495842583
 *     5: (best): [[232.9335430984407]] --> 0.23131219448799875
 *     6: (best): [[232.9335430984407]] --> 0.23131219448799875
 *     7: (best): [[232.9335430984407]] --> 0.23131219448799875
 *     8: (best): [[232.9335430984407]] --> 0.23131219448799875
 *     9: (best): [[232.9335430984407]] --> 0.23131219448799875
 *    10: (best): [[232.9335430984407]] --> 0.23131219448799875
 *    11: (best): [[232.9335430984407]] --> 0.23131219448799875
 *    
 *    Execution time: 45 ms
 *    Samples:         10
 *    Best Phenotype:  [[230.824557520235]] --> 0.23980732495842583
 *    Worst Phenotype: [[177.3496273696343]] --> 0.0021336795733455406
 * </pre>
 * 
 * 
 * <h4>TSP</h4>
 * 
 * Implemenentaion of the classical TSP problem
 * 
 * [code]
 *     public class TravelingSalesman {
 *     
 *         private static class Function implements FitnessFunction<IntegerGene, Integer> {
 *              private static final long serialVersionUID = 1L;
 *             
 *              private final double[][] _adjacence;
 *              
 *              public Function(final double[][] adjacence) {
 *                  _adjacence = adjacence;
 *              }
 *              
 *              public Integer evaluate(final Genotype<IntegerGene> genotype) {
 *                  final Chromosome<IntegerGene> path = genotype.getChromosome();
 *                  
 *                  double length = 0.0;
 *                  for (int i = 0, n = path.length(); i < n; ++i) {
 *                      final int from = path.getGene(i).intValue();
 *                      final int to = path.getGene((i + 1)%n).intValue();
 *                      length -= adjacence[from][to];
 *                  }
 *                  
 *                  return (int)length*100;
 *              }
 *          }
 *          
 *          public static void main(String[] args) {
 *              final int stops = 10;
 *              
 *              final FitnessFunction<IntegerGene, Integer> ff = new Function(adjacencyMatrix(stops));
 *              final Factory<Genotype<IntegerGene>> gtf = Genotype.valueOf(
 *                      new PermutationChromosome(stops)
 *                  );
 *                  
 *              final GeneticAlgorithm<IntegerGene, Integer> ga = GeneticAlgorithm.valueOf(gtf, ff);
 *              ga.setPopulationSize(100);
 *              ga.setAlterer(new Mutation<IntegerGene>(0.5));
 *              ga.addAlterer(new PartiallyMatchedCrossover<IntegerGene>(0.3));
 *                  
 *              ga.setup();
 *              ga.evolve(10);
 *          }
 *          
 *          // All points in the created adjacency matrix lie on a circle. So it is easy 
 *          // to check the quality of the solution found by the GA.
 *          private static double[][] adjacencyMatrix(int stops) {
 *              double[][] matrix = new double[stops][stops];
 *              for (int i = 0; i < stops; ++i) {
 *                  for (int j = 0; j < stops; ++j) {
 *                      matrix[i][j] = chord(stops, abs(i - j), RADIUS);
 *                  }
 *              }
 *              
 *              return matrix;
 *          }
 *          
 *          private static double chord(int stops, int i, double r) {
 *              return 2.0*r*abs(sin((PI*i)/stops));
 *          }
 *          private static double RADIUS = 10.0;
 *     } 
 * [/code]
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
package org.jenetics;


