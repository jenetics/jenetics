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
package org.jenetics.engine;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jenetics.Gene;

/**
 * The {@code EvolutionStream} class extends the Java {@link Stream} and adds a
 * method for limiting the evolution by a given predicate.
 *
 * @see java.util.stream.Stream
 * @see Engine
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.1
 */
public interface EvolutionStream<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends Stream<EvolutionResult<G, C>>
{

	/**
	 * Returns a stream consisting of the elements of this stream, truncated
	 * when the given {@code proceed} predicate returns {@code false}.
	 * <p>
	 * <i>General usage example:</i>
	 * <pre>{@code
	 * final Phenotype<DoubleGene, Double> result = engine.stream()
	 *      // Truncate the evolution stream after 5 "steady" generations.
	 *     .limit(bySteadyFitness(5))
	 *      // The evolution will stop after maximal 100 generations.
	 *     .limit(100)
	 *     .collect(toBestPhenotype());
	 * }</pre>
	 *
	 * @see limit
	 *
	 * @param proceed the predicate which determines whether the stream is
	 *        truncated or not. <i>If the predicate returns {@code false}, the
	 *        evolution stream is truncated.</i>
	 * @return the new stream
	 * @throws NullPointerException if the given predicate is {@code null}.
	 */
	public EvolutionStream<G, C>
	limit(final Predicate<? super EvolutionResult<G, C>> proceed);

	/**
	 * Create a new {@code EvolutionStream} from the given {@code start}
	 * population and {@code evolution} function. The main purpose of this
	 * factory method is to simplify the creation of an {@code EvolutionStream}
	 * from an own evolution (GA) engine.
	 *
	 * <pre>{@code
	 * final Supplier<EvolutionStart<DoubleGene, Double>> start = ...
	 * final EvolutionStream<DoubleGene, Double> stream =
	 *     EvolutionStream.of(start, new MySpecialEngine());
	 * }</pre>
	 *
	 * A more complete example for would look like as:
	 *
	 * <pre>{@code
	 * public final class SpecialEngine {
	 *
	 *     // The fitness function.
	 *     private static Double fitness(final Genotype<DoubleGene> gt) {
	 *         return gt.getGene().getAllele();
	 *     }
	 *
	 *     // Create new evolution start object.
	 *     private static EvolutionStart<DoubleGene, Double>
	 *     start(final int populationSize, final long generation) {
	 *         final Population<DoubleGene, Double> population =
	 *             Genotype.of(DoubleChromosome.of(0, 1)).instances()
	 *                 .map(gt -> Phenotype.of(gt, generation, SpecialEngine::fitness))
	 *                 .limit(populationSize)
	 *                 .collect(Population.toPopulation());
	 *
	 *         return EvolutionStart.of(population, generation);
	 *     }
	 *
	 *     // The special evolution function.
	 *     private static EvolutionResult<DoubleGene, Double>
	 *     evolve(final EvolutionStart<DoubleGene, Double> start) {
	 *         // Your special evolution implementation comes here!
	 *         return null;
	 *     }
	 *
	 *     public static void main(final String[] args) {
	 *         final Genotype<DoubleGene> best = EvolutionStream
	 *             .of(() -> start(50, 0), SpecialEngine::evolve)
	 *             .limit(limit.bySteadyFitness(10))
	 *             .limit(1000)
	 *             .collect(EvolutionResult.toBestGenotype());
	 *
	 *         System.out.println(String.format("Best Genotype: %s", best));
	 *     }
	 * }
	 * }</pre>
	 *
	 *
	 * @since 3.1
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 * @param start the evolution start
	 * @param evolution the evolution function
	 * @return a new {@code EvolutionStream} with the given {@code start} and
	 *         {@code evolution} function
	 * @throws java.lang.NullPointerException if one of the arguments is
	 *         {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionStream<G, C> of(
		final Supplier<EvolutionStart<G, C>> start,
		final Function<? super EvolutionStart<G, C>, EvolutionResult<G, C>> evolution
	) {
		return new EvolutionStreamImpl<>(start, evolution);
	}

}
