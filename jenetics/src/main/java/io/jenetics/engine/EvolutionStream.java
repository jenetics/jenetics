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
package io.jenetics.engine;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.jenetics.Gene;
import io.jenetics.internal.engine.EvolutionStreamImpl;

/**
 * The {@code EvolutionStream} class extends the Java {@link Stream} and adds a
 * method for limiting the evolution by a given predicate.
 *
 * @implNote Collecting an <em>empty</em> {@code EvolutionStream} will return
 *           {@code null}.
 * {@snippet lang="java":
 * final EvolutionResult<DoubleGene, Double> result = engine.stream()
 *     .limit(0)
 *     .collect(toBestEvolutionResult());
 *
 * assert result == null;
 * }
 *
 * @see java.util.stream.Stream
 * @see Engine
 * @see EvolutionStreamable
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 6.0
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
	 * {@snippet lang="java":
	 * final Phenotype<DoubleGene, Double> result = engine.stream()
	 *      // Truncate the evolution stream after 5 "steady" generations.
	 *     .limit(bySteadyFitness(5))
	 *      // The evolution will stop after maximal 100 generations.
	 *     .limit(100)
	 *     .collect(toBestPhenotype());
	 * }
	 *
	 * <b>Note:</b>
	 * The evolution result may be {@code null}, if your <em>truncation</em>
	 * predicate returns {@code false} for the initial population.
	 * {@snippet lang="java":
	 * final EvolutionResult<DoubleGene, Double> result = engine.stream()
	 *     .limit(er -> false)
	 *     .collect(toBestEvolutionResult());
	 *
	 * assert result == null;
	 * }
	 *
	 * @see Limits
	 *
	 * @param proceed the predicate which determines whether the stream is
	 *        truncated or not. <i>If the predicate returns {@code false}, the
	 *        evolution stream is truncated.</i>
	 * @return the new stream
	 * @throws NullPointerException if the given predicate is {@code null}.
	 */
	EvolutionStream<G, C>
	limit(final Predicate<? super EvolutionResult<G, C>> proceed);

	/**
	 * Create a new {@code EvolutionStream} from the given {@code start}
	 * population and {@code evolution} function. The main purpose of this
	 * factory method is to simplify the creation of an {@code EvolutionStream}
	 * from an own evolution (GA) engine.
	 * {@snippet lang="java":
	 * final Supplier<EvolutionStart<DoubleGene, Double>> start = null; // @replace substring='null' replacement="..."
	 * final EvolutionStream<DoubleGene, Double> stream =
	 *     EvolutionStream.of(start, new MySpecialEngine());
	 * }
	 *
	 * A more complete example for would look like as:
	 * {@snippet lang="java":
	 * public final class SpecialEngine {
	 *
	 *     // The fitness function.
	 *     private static Double fitness(final Genotype<DoubleGene> gt) {
	 *         return gt.gene().allele();
	 *     }
	 *
	 *     // Create a new evolution start object.
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
	 *             .ofEvolution(() -> start(50, 0), SpecialEngine::evolve)
	 *             .limit(Limits.bySteadyFitness(10))
	 *             .limit(1000)
	 *             .collect(EvolutionResult.toBestGenotype());
	 *
	 *         System.out.println(String.format("Best Genotype: %s", best));
	 *     }
	 * }
	 * }
	 *
	 * @since 5.1
	 *
	 * @see #ofAdjustableEvolution(Supplier, Function)
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
	static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionStream<G, C> ofEvolution(
		final Supplier<EvolutionStart<G, C>> start,
		final Evolution<G, C> evolution
	) {
		return new EvolutionStreamImpl<>(start, evolution);
	}

	/**
	 * Create a new evolution stream with an <em>adjustable</em> evolution
	 * function.
	 * {@snippet lang="java":
	 * public static void main(final String[] args) {
	 *     final Problem<double[], DoubleGene, Double> problem = Problem.of(
	 *         v -> Math.sin(v[0])*Math.cos(v[1]),
	 *         Codecs.ofVector(new DoubleRange(0, 2*Math.PI), 2)
	 *     );
	 *
	 *     // Engine builder template.
	 *     final Engine.Builder<DoubleGene, Double> builder = Engine
	 *         .builder(problem)
	 *         .minimizing();
	 *
	 *     // Evolution used for low fitness variance.
	 *     final Evolution<DoubleGene, Double> lowVar = builder.copy()
	 *         .alterers(new Mutator<>(0.5))
	 *         .selector(new MonteCarloSelector<>())
	 *         .build();
	 *
	 *     // Evolution used for high fitness variance.
	 *     final Evolution<DoubleGene, Double> highVar = builder.copy()
	 *         .alterers(
	 *             new Mutator<>(0.05),
	 *             new MeanAlterer<>())
	 *         .selector(new RouletteWheelSelector<>())
	 *         .build();
	 *
	 *     final EvolutionStream<DoubleGene, Double> stream =
	 *         EvolutionStream.ofAdjustableEvolution(
	 *             EvolutionStart::empty,
	 *             er -> var(er) < 0.2 ? lowVar : highVar
	 *         );
	 *
	 *     final Genotype<DoubleGene> result = stream
	 *         .limit(Limits.bySteadyFitness(50))
	 *         .collect(EvolutionResult.toBestGenotype());
	 *
	 *     System.out.println(result + ": " +
	 *         problem.fitness().apply(problem.codec().decode(result)));
	 * }
	 *
	 * private static double var(final EvolutionStart<DoubleGene, Double> result) {
	 *     return result != null
	 *         ? result.getPopulation().stream()
	 *             .map(Phenotype::fitness)
	 *             .collect(DoubleMoments.toDoubleMoments())
	 *             .variance()
	 *         : 0.0;
	 * }
	 * }
	 *
	 * @see #ofEvolution(Supplier, Evolution)
	 *
	 * @param start the evolution start object
	 * @param evolution the adaptable evolution function
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 * @return a new {@code EvolutionStream} with the given {@code start} and
	 *         {@code evolution} function
	 * @throws java.lang.NullPointerException if one of the arguments is
	 *         {@code null}
	 */
	static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionStream<G, C> ofAdjustableEvolution(
		final Supplier<EvolutionStart<G, C>> start,
		final Function<
			? super EvolutionStart<G, C>,
			? extends Evolution<G, C>> evolution
	) {
		return EvolutionStreamImpl.of(start, evolution);
	}

}
