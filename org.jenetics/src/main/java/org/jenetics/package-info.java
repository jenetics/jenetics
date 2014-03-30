/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */

/**
 * <h3>Introduction</h3>
 *
 * The <em>Jenetics</em> project provides a
 * <a href="http://en.wikipedia.org/wiki/Genetic_algorithm" >Genetic Algorithm</a>
 * (GA) implementation. The project has very few dependencies to other libraries.
 * At runtime it only depends on the <em>Jenetics</em> library.
 * <p>
 * The order of the single execution steps of genetic algorithms may slightlyh
 * differ from implementation to implementation. The following pseudo-code shows
 * the <em>Jenetics</em> genetic algorithm steps.

 * <img width="556" alt="Genetic algorithm"
 *      src="doc-files/genetic-algorithm.png" >
 *
 * <p>
 * Line (1) creates the initial population and the line (2) calculates the
 * fitness value of the individuals. (This is done by the
 * {@code GeneticAlgorithm.setup()} method.) Line (4) increases the generation
 * number and line (5) and (6) selects the survivor and the offspring population.
 * The offspring/survivor fraction is determined by the {@code offspringFraction}
 * property of the GA. The selected offspring are altered in line (7). The next
 * line combines the survivor population and the altered offspring population--
 * after removing the died individuals--to the new population. The steps from
 * line (4) to (9) are repeated until a given termination criterion is fulfilled.
 *
 *
 * <h3>Data structures</h3>
 * <p><img alt="Structure Diagram" src="doc-files/StructureClassDiagram.svg" ></p>
 *
 * The diagram above shows the main data structures of the GA implementation.
 * The {@link org.jenetics.Gene} is the base of the building block. Genes are
 * aggregated in {@link org.jenetics.Chromosome}s. One to n Chromosomes are
 * aggregated in {@link org.jenetics.Genotype}s. A Genotype and a fitness
 * {@link org.jenetics.util.Function} form the {@link org.jenetics.Phenotype}.
 * Phenotypes are collected into a {@link org.jenetics.Population}.
 *
 * <h3>Getting started</h3>
 *
 * The minimum GA setup needs a genotype factory, {@code Factory<Genotype<?>>},
 * and a fitness {@code Function}. The {@code Genotype} implements the
 * {@code Factory} interface and can therefore be used as prototype for creating
 * the initial Population and for creating new random Genotypes.
 *
 * [code]
 * public static void main(final String[] args) {
 *     final Factory&lt;Genotype&lt;BitGene&gt;&gt; gtf = Genotype.of(
 *         BitChromosome.of(10, 0.5)
 *     );
 *     final Function&lt;Genotype&lt;BitGene&gt; Double&gt; ff = ...
 *     final GeneticAlgorithm&lt;BitGene, Double&gt;
 *     ga = new GeneticAlgorithm&lt;&gt;(gtf, ff, Optimize.MAXIMUM)
 *
 *     ga.setup();
 *     ga.evolve(100);
 *     System.out.println(ga.getBestPhenotype());
 * }
 * [/code]
 *
 * <p>
 * The genotype factory, {@code gtf}, in the example above will create genotypes
 * which consists of one {@code BitChromosome} with length 10. The one to zero
 * probability of the newly created genotypes is set to 0.5. The fitness function
 * is parameterized with a {@code BitGene} and a {@code Double}. That means
 * that the fitness function is calculating the fitness value as {@code Double}.
 * The return type of the fitness function must be at least a {@code Comparable}.
 * The {@code GeneticAlgorithm} object is then created with the genotype factory
 * and the fitness function. In this example the GA tries to maximize the fitness
 * function. If you want to find the minimal value you have to change the optimize
 * parameter from {@code Optimize.MAXIMUM} to {@code Optimize.MINIMUM}. The
 * {@code ga.setup()} call creates the initial population and calculates its
 * fitness value. Then the GA evolves 100 generations ({@code ga.evolve(100)})
 * an prints the best phenotype found so far onto the console.
 * </p>
 * In a more advanced setup you may want to change the default mutation and/or
 * selection strategies.
 *
 * [code]
 * public static void main(final String[] args) {
 *     ...
 *     ga.setSelectors(new RouletteWheelSelector&lt;BitGene&gt;());
 *     ga.setAlterers(
 *         new SinglePointCrossover&lt;BitGene&gt;(0.1),
 *         new Mutator&lt;BitGene&gt;(0.01)
 *     );
 *
 *     ga.setup();
 *     ga.evolve(100);
 *     System.out.println(ga.getBestPhenotype());
 * }
 * [/code]
 *
 * <p>
 * The selection strategy for offspring and survivors are set to the
 * roulette-wheel selector. It is also possible to set the selector for
 * offspring and survivors independently with the {@code setOffspringSelector}
 * and {@code setSurvivorSelector} methods. The alterers are concatenated, at
 * first the crossover (with probability 0.1) is performed and then the
 * chromosomes are mutated (with probability 0.01).
 * </p>
 *
 * <h3>Serialization</h3>
 *
 * With the serialization mechanism you can write a population to disk and load
 * it into an GA at a later time. It can also be used to transfer populations to
 * GAs, running on different hosts, over a network link. The IO class, located
 * in the {@code org.jenetics.util} package, supports native Java serialization
 * and XML serialization. For XML marshaling <em>Jenetics</em> internally uses
 * the XML support from the Javolution project.
 *
 * [code]
 * // Writing the population to disk.
 * final File file = new File("population.xml");
 * IO.jaxb.write(ga.getPopulation(), file);
 *
 * // Reading the population from disk.
 * final Population&lt;DoubleGene,Double&gt; population =
 *     (Population&lt;DoubleGene, Double&gt;)IO.jaxb.read(file);
 * ga.setPopulation(population);
 * [/code]
 *
 *
 * <h3>Examples</h3>
 *
 * <h4>Ones Counting</h4>
 * Ones counting is one of the simplest model-problem. It uses a binary
 * chromosome and forms a classic genetic algorithm. In the classic genetic
 * algorithm the problem is a maximization problem and the fitness function is
 * positive. The domain of the fitness function is a bit-chromosome. The fitness
 * of a Genotype is proportional to the number of ones.
 *
 * [code]
 * import org.jenetics.BitChromosome;
 * import org.jenetics.BitGene;
 * import org.jenetics.GeneticAlgorithm;
 * import org.jenetics.Genotype;
 * import org.jenetics.Mutator;
 * import org.jenetics.NumberStatistics;
 * import org.jenetics.Optimize;
 * import org.jenetics.RouletteWheelSelector;
 * import org.jenetics.SinglePointCrossover;
 * import org.jenetics.util.Factory;
 * import org.jenetics.util.Function;
 *
 * final class OneCounter
 *     implements Function&lt;Genotype&lt;BitGene&gt;, Integer&gt;
 * {
 *     \@Override
 *     public Integer apply(final Genotype&lt;BitGene&gt; genotype) {
 *         return ((BitChromosome)genotype.getChromosome()).bitCount();
 *     }
 * }
 *
 * public class OnesCounting {
 *     public static void main(String[] args) {
 *         final Factory&lt;Genotype&lt;BitGene&gt;&gt; gtf = Genotype.of(
 *             BitChromosome.of(20, 0.15)
 *         );
 *         final Function&lt;Genotype&lt;BitGene&gt;, Integer&gt; ff = new OneCounter();
 *         final GeneticAlgorithm&lt;BitGene, Integer&gt; ga =
 *             new GeneticAlgorithm&lt;&gt;(gtf, ff, Optimize.MAXIMUM);
 *
 *         ga.setStatisticsCalculator(
 *             new NumberStatistics.Calculator&lt;BitGene, Integer&gt;()
 *         );
 *         ga.setPopulationSize(50);
 *         ga.setSelectors(
 *             new RouletteWheelSelector&lt;BitGene, Integer&gt;()
 *         );
 *         ga.setAlterers(
 *             new Mutator&lt;BitGene&gt;(0.55),
 *             new SinglePointCrossover&lt;BitGene&gt;(0.06)
 *         );
 *
 *         ga.setup();
 *         ga.evolve(100);
 *         System.out.println(ga.getBestStatistics());
 *     }
 * }
 * [/code]
 *
 * The genotype in this example consists of one BitChromosome with a ones
 * probability of 0.15. The altering of the offspring population is performed
 * by mutation, with mutation probability of 0.55, and then by a single-point
 * crossover, with crossover probability of 0.06. After creating the initial
 * population, with the ga.setup() call, 100 generations are evolved. The
 * tournament selector is used for both, the offspring- and the survivor
 * selection---this is the default selector.
 *
 *
 * <h4>Traveling Salesman</h4>
 *
 * The Traveling Salesman problem is one of the classical problems in
 * computational mathematics and it is the most notorious NP-complete problem.
 * The goal is to find the shortest distance, or the path, with the least costs,
 * between N  different cities. Testing all possible path for N  cities would
 * lead to N!  checks to find the shortest one.
 * The following example uses a path where the cities are lying on a circle.
 * That means, the optimal path will be a polygon. This makes it easier to check
 * the quality of the found solution.
 *
 * [code]
 * import static java.lang.Math.PI;
 * import static java.lang.Math.abs;
 * import static java.lang.Math.sin;
 *
 * import org.jenetics.Chromosome;
 * import org.jenetics.EnumGene;
 * import org.jenetics.GeneticAlgorithm;
 * import org.jenetics.Genotype;
 * import org.jenetics.NumberStatistics.Calculator;
 * import org.jenetics.Optimize;
 * import org.jenetics.PartiallyMatchedCrossover;
 * import org.jenetics.PermutationChromosome;
 * import org.jenetics.SwapMutator;
 * import org.jenetics.util.Factory;
 * import org.jenetics.util.Function;
 *
 * class FF
 *     implements Function&lt;Genotype&lt;EnumGene&lt;Integer&gt;&gt;, Double&gt;
 * {
 *     private final double[][] _adjacence;
 *     public FF(final double[][] adjacence) {
 *         _adjacence = adjacence;
 *     }
 *
 *     \@Override
 *     public Double apply(final Genotype&lt;EnumGene&lt;Integer&gt;&gt; genotype) {
 *         final Chromosome&lt;EnumGene&lt;Integer&gt;&gt; path =
 *             genotype.getChromosome();
 *
 *         double length = 0.0;
 *         for (int i = 0, n = path.length(); i &lt; n; ++i) {
 *             final int from = path.getGene(i).getAllele();
 *             final int to = path.getGene((i + 1)%n).getAllele();
 *             length += _adjacence[from][to];
 *         }
 *         return length;
 *     }
 * }
 *
 * public class TravelingSalesman {
 *
 *     public static void main(String[] args) {
 *         final int stops = 20;
 *
 *         final Function&lt;Genotype&lt;EnumGene&lt;Integer&gt;&gt;, Double&gt; ff =
 *             new FF(adjacencyMatrix(stops));
 *         final Factory&lt;Genotype&lt;EnumGene&lt;Integer&gt;&gt;&gt; gt = Genotype.of(
 *             PermutationChromosome.ofInteger(stops)
 *         );
 *         final GeneticAlgorithm&lt;EnumGene&lt;Integer&gt;, Double&gt;
 *             ga = new GeneticAlgorithm&lt;&gt;(gt, ff, Optimize.MINIMUM);
 *         ga.setStatisticsCalculator(
 *             new Calculator&lt;EnumGene&lt;Integer&gt;, Double&gt;()
 *         );
 *         ga.setPopulationSize(300);
 *         ga.setAlterers(
 *             new SwapMutator&lt;EnumGene&lt;Integer&gt;&gt;(0.2),
 *             new PartiallyMatchedCrossover&lt;Integer&gt;(0.3)
 *         );
 *
 *         ga.setup();
 *         ga.evolve(700);
 *         System.out.println(ga.getBestStatistics());
 *         System.out.println(ga.getBestPhenotype());
 *     }
 *
 *     private static double[][] adjacencyMatrix(int stops) {
 *         double[][] matrix = new double[stops][stops];
 *         for (int i = 0; i &lt; stops; ++i) {
 *             for (int j = 0; j &lt; stops; ++j) {
 *                 matrix[i][j] = chord(stops, abs(i - j), RADIUS);
 *             }
 *         }
 *         return matrix;
 *     }
 *     private static double chord(int stops, int i, double r) {
 *         return 2.0*r*abs(sin((PI*i)/stops));
 *     }
 *     private static double RADIUS = 10.0;
 * }
 * [/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2014-03-30 $</em>
 */
package org.jenetics;

