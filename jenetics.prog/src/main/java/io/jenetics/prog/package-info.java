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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */

/**
 * <h2>Example</h2>
 *
 * The following example shows how to solve a GP problem with <em>Jenetics</em>.
 * We are trying to find a polynomial (or an arbitrary mathematical function)
 * which approximates a given data set.
 *
 * <table>
 *     <caption>Sample points</caption>
 *     <tr><th>x</th><th>y</th></tr>
 *     <tr><td>0.00</td><td>0.0000</td></tr>
 *     <tr><td>0.10</td><td>0.0740</td></tr>
 *     <tr><td>0.20</td><td>0.1120</td></tr>
 *     <tr><td>0.30</td><td>0.1380</td></tr>
 *     <tr><td>...</td><td>...</td></tr>
 * </table>
 *
 * The sample points has been created with the function
 * <em>f(x) = 4*x^3 - 3*x^2 + x</em>. The knowledge of the creating function
 * makes it easier to compare the quality of the evolved function. For the
 * example we created 21 data points.
 *
 * <p>
 *     <b>NOTE</b>: <em>The function which created the sample points is not
 *     needed in the error function we have to define for the GP. It just let
 *     us verify the final, evolved result.</em>
 * </p>
 *
 * As first step, we have to define the set of allowed <em>non-terminal</em>
 * and the <em>terminal</em> operations the GP is working with. Selecting the
 * right set of operation has a big influence on the performance of the GP. If
 * the operation set is bigger than necessary, we will expand the potential
 * search space, and the execution time for finding a solution. For our
 * <em>polynomial</em> example we will chose the following <em>operations</em>
 * and <em>terminals</em>.
 *
 * {@snippet lang="java":
 * static final ISeq<Op<Double>> OPERATIONS = ISeq.of(
 *     MathOp.ADD,
 *     MathOp.SUB,
 *     MathOp.MUL
 * );
 *
 * static final ISeq<Op<Double>> TERMINALS = ISeq.of(
 *     Var.of("x", 0),
 *     EphemeralConst.of(() -> (double)RandomRegistry.getRandom().nextInt(10))
 * );
 * }
 *
 * The chosen non-terminal operation set is sufficient to create any polynomial.
 * For the terminal operations, we added a variable "x", with index zero, and
 * an ephemeral integer constant. The purpose of the ephemeral constant is to
 * create constant values, which will differ for every tree, but stay constant
 * within a tree.
 * <p>
 * In the next step define the fitness function for the GP, which will be an
 * error function we will minimize.
 *
 * {@snippet lang="java":
 * // The lookup table where the data points are stored.
 * static final double[][] SAMPLES = new double[][] {
 *     {-1.0, -8.0000},
 *     {-0.9, -6.2460},
 *     ...
 * };
 *
 * static double error(final ProgramGene<Double> program) {
 *     return Arrays.stream(SAMPLES).mapToDouble(sample -> {
 *         final double x = sample[0];
 *         final double y = sample[1];
 *         final double result = program.eval(x);
 *         return abs(y - result) + program.size()*0.00005;
 *     })
 *     .sum();
 * }
 * }
 *
 * The error function calculates the sum of the (absolute) difference between
 * the sample value and the value calculated the by the evolved <em>program</em>
 * ({@code ProgramGene}). Since we prefer compact programs over complex one, we
 * will add a penalty for the program size (the number of nodes of the program
 * tree).
 *
 * <p>
 *     <b>CAUTION</b>: <em>The penalty for the tree size must be small enough to
 *     not dominate the error function. We still want to find an approximating
 *     function and not the smallest possible one</em>
 * </p>
 *
 * After we have defined the error function, we need to define the proper
 * {@code Codec}.
 *
 * {@snippet lang="java":
 * static final Codec<ProgramGene<Double>, ProgramGene<Double>> CODEC =
 *     Codec.of(
 *         Genotype.of(ProgramChromosome.of(
 *             // Program tree depth.
 *             5,
 *             // Chromosome validator.
 *             ch -> ch.root().size() <= 50,
 *             OPERATIONS,
 *             TERMINALS
 *         )),
 *         Genotype::gene
 *     );
 * }
 *
 *
 * There are two particularities in the definition of the
 * {@code ProgramChromosome}:
 *
 * <ol>
 *     <li>Since we want to narrow the search space, we are limiting the depth
 *     of newly created program trees to 5.</li>
 *     <li>Because of crossover operations performed during evolution, the
 *     resulting programs can grow quite big. To prevent an unlimited growth of
 *     the program trees, we mark programs with more than _50_ nodes as
 *     invalid.</li>
 * </ol>
 *
 * Now we are ready to put everything together:
 *
 * {@snippet lang="java":
 * public static void main(final String[] args) {
 *     final Engine<ProgramGene<Double>, Double> engine = Engine
 *         .builder(Polynomial::error, CODEC)
 *         .minimizing()
 *         .alterers(
 *             new SingleNodeCrossover<>(),
 *             new Mutator<>())
 *         .build();
 *
 *     final ProgramGene<Double> program = engine.stream()
 *         .limit(500)
 *         .collect(EvolutionResult.toBestGenotype())
 *         .gene();
 *
 *     System.out.println(Tree.toString(program));
 * }
 * }
 *
 * The GP is capable of finding the polynomial which created the sample data.
 * After a few tries, we got the following (correct) output program:
 *
 * <pre>
 * add
 * ├── mul
 * │   ├── x
 * │   └── sub
 * │       ├── 0.0
 * │       └── mul
 * │           ├── x
 * │           └── sub
 * │               ├── sub
 * │               │   ├── sub
 * │               │   │   ├── sub
 * │               │   │   │   ├── 3.0
 * │               │   │   │   └── x
 * │               │   │   └── x
 * │               │   └── x
 * │               └── x
 * └── x
 * </pre>
 *
 * This program can be reduced to <em>4*x^3 - 3*x^2 + x</em>, which is exactly
 * the polynomial, which created the sample data.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
package io.jenetics.prog;
