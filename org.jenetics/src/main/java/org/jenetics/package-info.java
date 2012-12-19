/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
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
 * <h3>Introduction</h3>
 *
 * The <em>Jenetics</em> project provides a
 * <a href="http://en.wikipedia.org/wiki/Genetic_algorithm" >Genetic Algorithm</a>
 * (GA) implementation. The project has very few dependencies to other libraries.
 * At runtime it only depends on the <em>Jenetics</em> library.
 * <p/>
 * The order of the single execution steps of genetic algorithms may slightly
 * differ from implementation to implementation. The following pseudo-code shows
 * the <em>Jenetics</em> genetic algorithm steps.
 *
 * <img width="556" heigth="78" align="BOTTOM" border="0"
 *      src="doc-files/genetic-algorithm.png" />
 *
 * <p/>
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
 * <p><img alt="Structure Diagram" src="doc-files/StructureClassDiagram.svg" ></img><p/>
 *
 * The diagram above shows the main data structures of the GA implementation.
 * The {@link org.jenetics.Gene} is the base of the building block. Genes are
 * aggregated in {@link org.jenetics.Chromosome}s. One to n Chromosomes are
 * aggregated in {@link org.jenetics.Genotype}s. A Genptype and a fitness
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
 *     Factory<Genotype<BitGene>> gtf = Genotype.valueOf(
 *         BitChromosome.valueOf(10, 0.5)
 *     );
 *     Function<Genotype<BitGene> Float64> ff = ...
 *     GeneticAlgorithm<BitGene, Float64>
 *     ga = new GeneticAlgorithm<>(gtf, ff, Optimize.MAXIMUM)
 *
 *     ga.setup();
 *     ga.evolve(100);
 *     System.out.println(ga.getBestPhenotype());
 * }
 * [/code]
 *
 * The genotype factory, {@code gtf}, in the example above will create genotypes
 * which consists of one {@code BitChromosome} with length 10. The one to zero
 * probability of the newly created genotypes is set to 0.5. The fitness function
 * is parameterized with a {@code BitGene} and a {@code Float64}. That means
 * that the fitness function is calculating the fitness value as {@code Float64}.
 * The return type of the fitness function must be at least a {@code Comparable}.
 * The {@code GeneticAlgorithm} object is then created with the genotype factory
 * and the fitness function. In this example the GA tries to maximize the fitness
 * function. If you want to find the minimal value you have to change the optimize
 * parameter from {@code Optimize.MAXIMUM} to {@code Optimize.MINIMUM}. The
 * {@code ga.setup()} call creates the initial population and calculates its
 * fitness value. Then the GA evolves 100 generations ({@code ga.evolve(100)})
 * an prints the best phenotype found so far onto the console.
 * <p/>
 * In a more advanced setup you may want to change the default mutation and/or
 * selection strategies.
 *
 * [code]
 * public static void main(final String[] args) {
 *     ...
 *     ga.setSelectors(new RouletteWheelSelector<BitGene>());
 *     ga.setAlterers(
 *         new SinglePointCrossover<BitGene>(0.1),
 *         new Mutator<BitGene>(0.01)
 *     );
 *
 *     ga.setup();
 *     ga.evolve(100);
 *     System.out.println(ga.getBestPhenotype());
 * }
 * [/code]
 *
 * The selection strategy for offspring and survivors are set to the
 * roulette-wheel selector. It is also possible to set the selector for
 * offspring and survivors independently with the {@code setOffspringSelector}
 * and {@code setSurvivorSelector} methods. The alterers are concatenated, at
 * first the crossover (with probability 0.1) is performed and then the
 * chromosomes are mutated (with probability 0.01).
 * <p/>
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
 * IO.xml.write(ga.getPopulation(), file);
 *
 * // Reading the population from disk.
 * Population<Float64Gene,Float64> population =
 *     (Population<Float64Gene, Float64)IO.xml.read(file);
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
 *     implements Function<Genotype<BitGene>, Integer>
 * {
 *     public Integer apply(Genotype<BitGene> genotype) {
 *         int count = 0;
 *         for (BitGene gene : genotype.getChromosome()) {
 *             if (gene.getBit()) {
 *                 ++count;
 *             }
 *         }
 *         return count;
 *     }
 * }
 *
 * public class OnesCounting {
 *     public static void main(String[] args) {
 *         Factory<Genotype<BitGene>> gtf = Genotype.valueOf(
 *             new BitChromosome(20, 0.15)
 *         );
 *         Function<Genotype<BitGene>, Integer> ff = new OneCounter();
 *         GeneticAlgorithm<BitGene, Integer> ga =
 *             new GeneticAlgorithm<>(gtf, ff, Optimize.MAXIMUM);
 *
 *         ga.setStatisticsCalculator(
 *             new NumberStatistics.Calculator<BitGene, Integer>()
 *         );
 *         ga.setPopulationSize(50);
 *         ga.setSelectors(
 *             new RouletteWheelSelector<BitGene, Integer>()
 *         );
 *         ga.setAlterers(
 *             new Mutator<BitGene>(0.55),
 *             new SinglePointCrossover<BitGene>(0.06)
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
 *     implements Function<Genotype<EnumGene<Integer>>, Float64>
 * {
 *     private final double[][] _adjacence;
 *     public FF(final double[][] adjacence) {
 *         _adjacence = adjacence;
 *     }
 *     public Float64 apply(Genotype<EnumGene<Integer>> genotype) {
 *         Chromosome<EnumGene<Integer>> path =
 *             genotype.getChromosome();
 *
 *         double length = 0.0;
 *         for (int i = 0, n = path.length(); i < n; ++i) {
 *             final int from = path.getGene(i).getAllele();
 *             final int to = path.getGene((i + 1)%n).getAllele();
 *             length += _adjacence[from][to];
 *         }
 *         return Float64.valueOf(length);
 *     }
 * }
 *
 * public class TravelingSalesman {
 *
 *     public static void main(String[] args) {
 *         final int stops = 20;
 *
 *         Function<Genotype<EnumGene<Integer>>, Float64> ff =
 *             new FF(adjacencyMatrix(stops));
 *         Factory<Genotype<EnumGene<Integer>>> gt = Genotype.valueOf(
 *             PermutationChromosome.ofInteger(stops)
 *         );
 *         final GeneticAlgorithm<EnumGene<Integer>, Float64>
 *             ga = new GeneticAlgorithm<>(gt, ff, Optimize.MINIMUM);
 *         ga.setStatisticsCalculator(
 *             new Calculator<EnumGene<Integer>, Float64>()
 *         );
 *         ga.setPopulationSize(300);
 *         ga.setAlterers(
 *             new SwapMutator<EnumGene<Integer>>(0.2),
 *             new PartiallyMatchedCrossover<Integer>(0.3)
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
 *         for (int i = 0; i < stops; ++i) {
 *             for (int j = 0; j < stops; ++j) {
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
 * @version 1.0 &mdash; <em>$Date: 2012-11-06 $</em>
 */
package org.jenetics;



