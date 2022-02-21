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

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;
import java.util.function.Supplier;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.util.ISeq;

/**
 * This interface defines the capability of creating {@link EvolutionStream}s
 * from a given {@link EvolutionStart} object. It also decouples the engine's
 * capability from the capability to create evolution streams. The purpose of
 * this interface is similar to the {@link Iterable} interface.
 *
 * @see EvolutionStream
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 4.1
 */
public interface EvolutionStreamable<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	/**
	 * Create a new, possibly <em>infinite</em>, evolution stream with the given
	 * evolution start. If an empty {@code Population} is given, the engine's
	 * genotype factory is used for creating the population. The given
	 * population might be the result of another engine and this method allows
	 * to start the evolution with the outcome of a different engine.
	 * The fitness function is replaced by the one defined for this engine.
	 *
	 * @param start the data the evolution stream starts with
	 * @return a new <b>infinite</b> evolution stream
	 * @throws java.lang.NullPointerException if the given evolution
	 *         {@code start} is {@code null}.
	 */
	EvolutionStream<G, C>
	stream(final Supplier<EvolutionStart<G, C>> start);

	/**
	 * Create a new, possibly <em>infinite</em>, evolution stream with the given
	 * initial value. If an empty {@code Population} is given, the engines genotype
	 * factory is used for creating the population. The given population might
	 * be the result of another engine and this method allows to start the
	 * evolution with the outcome of a different engine. The fitness function
	 * is replaced by the one defined for this engine.
	 *
	 * @param init the data the evolution stream is initialized with
	 * @return a new <b>infinite</b> evolution stream
	 * @throws java.lang.NullPointerException if the given evolution
	 *         {@code start} is {@code null}.
	 */
	EvolutionStream<G, C> stream(final EvolutionInit<G> init);


	/* *************************************************************************
	 * Default interface methods.
	 * ************************************************************************/

	/**
	 * Create a new, possibly <em>infinite</em>, evolution stream with a newly
	 * created population. This method is a shortcut for
	 * <pre>{@code
	 * final EvolutionStream<G, C> stream = streamable
	 *     .stream(() -> EvolutionStart.of(ISeq.empty(), 1));
	 * }</pre>
	 *
	 * @return a new evolution stream.
	 */
	default EvolutionStream<G, C> stream() {
		return stream((Supplier<EvolutionStart<G, C>>)EvolutionStart::empty);
	}

	/**
	 * Create a new, possibly <em>infinite</em>, evolution stream with the given
	 * evolution start. If an empty {@code Population} is given, the engine's genotype
	 * factory is used for creating the population. The given population might
	 * be the result of another engine and this method allows to start the
	 * evolution with the outcome of a different engine. The fitness function
	 * is replaced by the one defined for this engine.
	 *
	 * @param start the data the evolution stream starts with
	 * @return a new <b>infinite</b> evolution iterator
	 * @throws java.lang.NullPointerException if the given evolution
	 *         {@code start} is {@code null}.
	 */
	default EvolutionStream<G, C>
	stream(final EvolutionStart<G, C> start) {
		return stream(() -> start);
	}

	/**
	 * Create a new {@code EvolutionStream} starting with a previously evolved
	 * {@link EvolutionResult}. The stream is initialized with the population
	 * of the given {@code result} and its total generation
	 * {@link EvolutionResult#totalGenerations()}.
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
	 *     System.out.println(result.bestPhenotype());
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
	default EvolutionStream<G, C>
	stream(final EvolutionResult<G, C> result) {
		return stream(EvolutionStart.of(
			result.population(),
			result.generation()
		));
	}

	/**
	 * Create a new, possibly <em>infinite</em>, evolution stream with the given
	 * initial population. If an empty {@code Population} is given, the engine's
	 * genotype factory is used for creating the population. The given population
	 * might be the result of a other engine and this method allows to start the
	 * evolution with the outcome of a different engine. The fitness function
	 * is replaced by the one defined for this engine.
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
	default EvolutionStream<G, C> stream(
		final ISeq<Phenotype<G, C>> population,
		final long generation
	) {
		return stream(EvolutionStart.of(population, generation));
	}

	/**
	 * Create a new, possibly <em>infinite</em>, evolution stream with the given
	 * initial population. If an empty {@code Population} is given, the engine's
	 * genotype factory is used for creating the population. The given population
	 * might be the result of a other engine and this method allows to start the
	 * evolution with the outcome of a different engine. The fitness function
	 * is replaced by the one defined for this engine.
	 *
	 * @param population the initial individuals used for the evolution stream.
	 *        Missing individuals are created and individuals not needed are
	 *        skipped.
	 * @return a new evolution stream.
	 * @throws java.lang.NullPointerException if the given {@code population} is
	 *         {@code null}.
	 */
	default EvolutionStream<G, C> stream(final ISeq<Phenotype<G, C>> population) {
		return stream(EvolutionStart.of(population, 1));
	}

	/**
	 * Create a new, possibly <em>infinite</em>, evolution stream with the given
	 * initial individuals. If an empty {@code Iterable} is given, the engine's
	 * genotype factory is used for creating the population.
	 *
	 * @param genotypes the initial individuals used for the evolution stream.
	 *        Missing individuals are created and individuals not needed are
	 *        skipped.
	 * @param generation the generation the stream starts from; must be greater
	 *        than zero.
	 * @return a new evolution stream.
	 * @throws java.lang.NullPointerException if the given {@code genotypes} is
	 *         {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         smaller then one
	 */
	default EvolutionStream<G, C> stream(
		final Iterable<Genotype<G>> genotypes,
		final long generation
	) {
		return stream(EvolutionInit.of(ISeq.of(genotypes), generation));
	}

	/**
	 * Create a new, possibly <em>infinite</em>, evolution stream with the given
	 * initial individuals. If an empty {@code Iterable} is given, the engine's
	 * genotype factory is used for creating the population.
	 *
	 * @param genotypes the initial individuals used for the evolution stream.
	 *        Missing individuals are created and individuals not needed are
	 *        skipped.
	 * @return a new evolution stream.
	 * @throws java.lang.NullPointerException if the given {@code genotypes} is
	 *         {@code null}.
	 */
	default EvolutionStream<G, C>
	stream(final Iterable<Genotype<G>> genotypes) {
		return stream(genotypes, 1);
	}

	/**
	 * Return a new {@code EvolutionStreamable} instance where all created
	 * {@code EvolutionStream}s are limited by the given predicate. Since some
	 * predicates has to maintain internal state, a predicate {@code Supplier}
	 * must be given instead a plain limiting predicate.
	 *
	 * @param proceed the limiting predicate supplier.
	 * @return a new evolution streamable object
	 * @throws NullPointerException if the give {@code predicate} is {@code null}
	 */
	default EvolutionStreamable<G, C>
	limit(final Supplier<? extends Predicate<? super EvolutionResult<G, C>>> proceed) {
		requireNonNull(proceed);

		return new EvolutionStreamable<>() {
			@Override
			public EvolutionStream<G, C>
			stream(final Supplier<EvolutionStart<G, C>> start) {
				return EvolutionStreamable.this.stream(start).limit(proceed.get());
			}

			@Override
			public EvolutionStream<G, C> stream(final EvolutionInit<G> init) {
				return EvolutionStreamable.this.stream(init).limit(proceed.get());
			}
		};
	}

	/**
	 * Return a new {@code EvolutionStreamable} instance where all created
	 * {@code EvolutionStream}s are limited to the given number of generations.
	 *
	 * @param generations the number of generations after the created evolution
	 *        streams are truncated
	 * @return a new evolution streamable object
	 * @throws IllegalArgumentException if the given {@code generations} is
	 *         smaller than zero.
	 */
	default EvolutionStreamable<G, C> limit(final long generations) {
		return limit(() -> Limits.byFixedGeneration(generations));
	}

}
