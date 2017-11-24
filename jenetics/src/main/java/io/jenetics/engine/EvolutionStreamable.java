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

import java.util.function.Supplier;

import io.jenetics.Gene;
import io.jenetics.Phenotype;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface EvolutionStreamable<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	/**
	 * Create a new <b>infinite</b> evolution stream with a newly created
	 * population.
	 *
	 * @return a new evolution stream.
	 */
	public EvolutionStream<G, C> stream();

	/**
	 * Create a new <b>infinite</b> evolution stream with the given evolution
	 * start. If an empty {@code Population} is given, the engines genotype
	 * factory is used for creating the population. The given population might
	 * be the result of an other engine and this method allows to start the
	 * evolution with the outcome of an different engine. The fitness function
	 * and the fitness scaler are replaced by the one defined for this engine.
	 *
	 * @param start the data the evolution stream starts with
	 * @return a new <b>infinite</b> evolution iterator
	 * @throws java.lang.NullPointerException if the given evolution
	 *         {@code start} is {@code null}.
	 */
	public EvolutionStream<G, C>
	stream(final Supplier<EvolutionStart<G, C>> start);


	/* *************************************************************************
	 * Default interface methods.
	 * ************************************************************************/

	/**
	 * Create a new <b>infinite</b> evolution stream with the given evolution
	 * start. If an empty {@code Population} is given, the engines genotype
	 * factory is used for creating the population. The given population might
	 * be the result of an other engine and this method allows to start the
	 * evolution with the outcome of an different engine. The fitness function
	 * and the fitness scaler are replaced by the one defined for this engine.
	 *
	 * @param start the data the evolution stream starts with
	 * @return a new <b>infinite</b> evolution iterator
	 * @throws java.lang.NullPointerException if the given evolution
	 *         {@code start} is {@code null}.
	 */
	public default EvolutionStream<G, C>
	stream(final EvolutionStart<G, C> start) {
		return stream(() -> start);
	}

	/**
	 * Create a new {@code EvolutionStream} starting with a previously evolved
	 * {@link EvolutionResult}. The stream is initialized with the population
	 * of the given {@code result} and its total generation
	 * {@link EvolutionResult#getTotalGenerations()}.
	 *
	 * <pre>{@code
	 * private static final Problem<Double, DoubleGene, Double>
	 * PROBLEM = Problem.of(
	 *     x -> cos(0.5 + sin(x))*cos(x),
	 *     Codecs.ofScalar(DoubleRange.of(0.0, 2.0*PI))
	 * );
	 *
	 * private static final Engine<DoubleGene, Double>
	 * ENGINE = Engine.builder(PROBLEM)
	 *     .optimize(Optimize.MINIMUM)
	 *     .offspringSelector(new RouletteWheelSelector<>())
	 *     .build();
	 *
	 * public static void main(final String[] args) throws IOException {
	 *     // Result of the first evolution run.
	 *     final EvolutionResult<DoubleGene, Double> rescue = ENGINE.stream()
	 *         .limit(Limits.bySteadyFitness(10))
	 *         .collect(EvolutionResult.toBestEvolutionResult());
	 *
	 *     // Save the result of the first run into a file.
	 *     final Path path = Paths.get("result.bin");
	 *     IO.object.write(rescue, path);
	 *
	 *     // Load the previous result and continue evolution.
	 *     \@SuppressWarnings("unchecked")
	 *     final EvolutionResult<DoubleGene, Double> result = ENGINE
	 *         .stream((EvolutionResult<DoubleGene, Double>)IO.object.read(path))
	 *         .limit(Limits.bySteadyFitness(20))
	 *         .collect(EvolutionResult.toBestEvolutionResult());
	 *
	 *     System.out.println(result.getBestPhenotype());
	 * }
	 * }</pre>
	 *
	 * The example above shows how to save an {@link EvolutionResult} from a
	 * first run, save it to disk and continue the evolution.
	 *
	 * @param result the previously evolved {@code EvolutionResult}
	 * @return a new evolution stream, which continues a previous one
	 * @throws NullPointerException if the given evolution {@code result} is
	 *         {@code null}
	 */
	public default EvolutionStream<G, C>
	stream(final EvolutionResult<G, C> result) {
		return stream(EvolutionStart.of(
			result.getPopulation(),
			result.getGeneration()
		));
	}

	/**
	 * Create a new <b>infinite</b> evolution stream with the given initial
	 * population. If an empty {@code Population} is given, the engines genotype
	 * factory is used for creating the population. The given population might
	 * be the result of an other engine and this method allows to start the
	 * evolution with the outcome of an different engine. The fitness function
	 * and the fitness scaler are replaced by the one defined for this engine.
	 *
	 * @param population the initial individuals used for the evolution stream.
	 *        Missing individuals are created and individuals not needed are
	 *        skipped.
	 * @param generation the generation the stream starts from; must be greater
	 *        than zero.
	 * @return a new evolution stream.
	 * @throws java.lang.NullPointerException if the given {@code population} is
	 *         {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         smaller then one
	 */
	public default EvolutionStream<G, C> stream(
		final ISeq<Phenotype<G, C>> population,
		final long generation
	) {
		return stream(EvolutionStart.of(population, generation));
	}

	/**
	 * Create a new <b>infinite</b> evolution stream with the given initial
	 * population. If an empty {@code Population} is given, the engines genotype
	 * factory is used for creating the population. The given population might
	 * be the result of an other engine and this method allows to start the
	 * evolution with the outcome of an different engine. The fitness function
	 * and the fitness scaler are replaced by the one defined for this engine.
	 *
	 * @param population the initial individuals used for the evolution stream.
	 *        Missing individuals are created and individuals not needed are
	 *        skipped.
	 * @return a new evolution stream.
	 * @throws java.lang.NullPointerException if the given {@code population} is
	 *         {@code null}.
	 */
	public default EvolutionStream<G, C>
	stream(final ISeq<Phenotype<G, C>> population) {
		return stream(EvolutionStart.of(population, 1));
	}

}
