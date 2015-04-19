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
 * This is the base package of the Jenetics library and contains all domain
 * classes, like Gene, Chromosome or Genotype. Most of this types are immutable
 * data classes and doesn't implement any behavior. It also contains the Selector
 * and Alterer interfaces and its implementations. The classes in this package
 * are (almost) sufficient to implement an own GA.
 *
 * <h3>Introduction</h3>
 * <p><strong>Jenetics</strong> is an <strong>Genetic Algorithm</strong>,
 * respectively an <strong>Evolutionary Algorithm</strong>, library written in
 * Java. It is designed with a clear separation of the several concepts of the
 * algorithm, e.&nbsp;g. Gene, Chromosome, Genotype, Phenotype, Population and
 * fitness Function. <strong>Jenetics</strong> allows you to minimize and
 * maximize the given fitness function without tweaking it. In contrast to other
 * GA implementations, the library uses the concept of an evolution stream
 * (EvolutionStream) for executing the evolution steps. Since the
 * EvolutionStream implements the Java Stream interface, it works smoothly with
 * the rest of the Java streaming API.</p>
 *
 * <h3>Getting Started</h3>
 * <p>The minimum evolution Engine setup needs a genotype factory,
 * Factory&lt;Genotype&lt;?&gt;&gt;, and a fitness Function. The Genotype
 * implements the Factory interface and can therefore be used as prototype for
 * creating the initial Population and for creating new random Genotypes.</p>
 *
 * <pre>{@code
 * import org.jenetics.BitChromosome;
 * import org.jenetics.BitGene;
 * import org.jenetics.Genotype;
 * import org.jenetics.engine.Engine;
 * import org.jenetics.engine.EvolutionResult;
 * import org.jenetics.util.Factory;
 *
 * public class HelloWorld {
 *      // 2.) Definition of the fitness function.
 *     private static Integer eval(Genotype<BitGene> gt) {
 *         return ((BitChromosome)gt.getChromosome()).bitCount();
 *     }
 *
 *     public static void main(String[] args) {
 *         // 1.) Define the genotype (factory) suitable
 *         //     for the problem.
 *         Factory<Genotype<BitGene>> gtf =
 *         Genotype.of(BitChromosome.of(10, 0.5));
 *
 *         // 3.) Create the execution environment.
 *         Engine<BitGene, Integer> engine = Engine
 *            .builder(HelloWorld::eval, gtf)
 *            .build();
 *
 *         // 4.) Start the execution (evolution) and
 *         //     collect the result.
 *         Genotype<BitGene> result = engine.stream()
 *             .limit(100)
 *             .collect(EvolutionResult.toBestGenotype());
 *
 *         System.out.println("Hello World:\n" + result);
 *     }
 * }
 * }</pre>
 *
 * <p>In contrast to other GA implementations, the library uses the concept of
 * an evolution stream (EvolutionStream) for executing the evolution steps.
 * Since the EvolutionStream implements the Java Stream interface, it works
 * smoothly with the rest of the Java streaming API. Now let's have a closer
 * look at listing above and discuss this simple program step by step:</p>
 * <ol>
 * <li>The probably most challenging part, when setting up a new evolution
 * Engine, is to transform the problem domain into a appropriate Genotype
 * (factory) representation. In our example we want to count the number of ones
 * of a BitChromosome. Since we are counting only the ones of one chromosome,
 * we are adding only one BitChromosome to our Genotype. In general, the
 * Genotype can be created with 1 to n chromosomes.</li>
 * <li>Once this is done, the fitness function which should be maximized, can be
 * defined. Utilizing the new language features introduced in Java 8, we simply
 * write a private static method, which takes the genotype we defined and
 * calculate it's fitness value. If we want to use the optimized bit-counting
 * method, bitCount(), we have to cast the Chromosome&lt;BitGene&gt; class to
 * the actual used BitChromosome class. Since we know for sure that we created
 * the Genotype with a BitChromosome, this can be done safely. A reference to
 * the eval method is then used as fitness function and passed to the
 * Engine.build method.</li>
 * <li>In the third step we are creating the evolution Engine, which is
 * responsible for changing, respectively evolving, a given population. The
 * Engine is highly configurable and takes parameters for controlling the
 * evolutionary and the computational environment. For changing the evolutionary
 * behavior, you can set different alterers and selectors. By changing the used
 * Executor service, you control the number of threads, the Engine is allowed to
 * use. An new Engine instance can only be created via its builder, which is
 * created by calling the Engine.builder method.</li>
 * <li>In the last step, we can create a new EvolutionStream from our Engine.
 * The EvolutionStream is the model or view of the evolutionary process. It
 * serves as a »process handle« and also allows you, among other things, to
 * control the termination of the evolution. In our example, we simply truncate
 * the stream after 100 generations. If you don't limit the stream, the
 * EvolutionStream will not terminate and run forever. Since the EvolutionStream
 * extends the java.util.stream.Stream interface, it integrates smoothly with
 * the rest of the Java streaming API. The final result, the best Genotype in
 * our example, is then collected with one of the predefined collectors of the
 * EvolutionResult class.</li>
 * </ol>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.1
 */
package org.jenetics;

