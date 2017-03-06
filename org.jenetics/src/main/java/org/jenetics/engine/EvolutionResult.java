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

import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;
import java.util.function.Function;
import java.util.stream.Collector;

import org.jenetics.internal.util.Hash;
import org.jenetics.internal.util.Lazy;

import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.stat.MinMax;

/**
 * Represents a state of the GA after an evolution step. It also represents the
 * final state of an evolution process and can be created with an appropriate
 * collector:
 * <pre>{@code
 * final Problem<ISeq<Point>, EnumGene<Point>, Double> tsm = ...;
 * final EvolutionResult<EnumGene<Point>, Double> result = Engine.builder(tsm)
 *     .optimize(Optimize.MINIMUM).build()
 *     .stream()
 *     .limit(100)
 *     .collect(EvolutionResult.toBestEvolutionResult());
 * }</pre>
 *
 * @see EvolutionStart
 * @see Engine
 *
 * @param <G> the gene type
 * @param <C> the fitness type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.6
 */
public final class EvolutionResult<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Comparable<EvolutionResult<G, C>>, Serializable
{
	private static final long serialVersionUID = 1L;

	private final Optimize _optimize;
	private final Population<G, C> _population;
	private final long _generation;
	private final long _totalGenerations;

	private final EvolutionDurations _durations;
	private final int _killCount;
	private final int _invalidCount;
	private final int _alterCount;

	private final Lazy<Phenotype<G, C>> _best;
	private final Lazy<Phenotype<G, C>> _worst;

	private EvolutionResult(
		final Optimize optimize,
		final Population<G, C> population,
		final long generation,
		final long totalGenerations,
		final EvolutionDurations durations,
		final int killCount,
		final int invalidCount,
		final int alterCount
	) {
		_optimize = requireNonNull(optimize);
		_population = requireNonNull(population).copy();
		_generation = generation;
		_totalGenerations = totalGenerations;
		_durations = requireNonNull(durations);
		_killCount = killCount;
		_invalidCount = invalidCount;
		_alterCount = alterCount;

		_best = Lazy.of(() -> _population.stream()
			.max(_optimize.ascending())
			.orElse(null)
		);

		_worst = Lazy.of(() -> _population.stream()
			.min(_optimize.ascending())
			.orElse(null)
		);
	}

	/**
	 * Return the optimization strategy used.
	 *
	 * @return the optimization strategy used
	 */
	public Optimize getOptimize() {
		return _optimize;
	}

	/**
	 * Return the population after the evolution step.
	 *
	 * @return the population after the evolution step
	 */
	public Population<G, C> getPopulation() {
		return _population.copy();
	}

	/**
	 * The current generation.
	 *
	 * @return the current generation
	 */
	public long getGeneration() {
		return _generation;
	}

	/**
	 * Return the generation count evaluated so far.
	 *
	 * @return the total number of generations evaluated so far
	 */
	public long getTotalGenerations() {
		return _totalGenerations;
	}

	/**
	 * Return the timing (meta) information of the evolution step.
	 *
	 * @return the timing (meta) information of the evolution step
	 */
	public EvolutionDurations getDurations() {
		return _durations;
	}

	/**
	 * Return the number of killed individuals.
	 *
	 * @return the number of killed individuals
	 */
	public int getKillCount() {
		return _killCount;
	}

	/**
	 * Return the number of invalid individuals.
	 *
	 * @return the number of invalid individuals
	 */
	public int getInvalidCount() {
		return _invalidCount;
	}

	/**
	 * The number of altered individuals.
	 *
	 * @return the number of altered individuals
	 */
	public int getAlterCount() {
		return _alterCount;
	}

	/**
	 * Return the best {@code Phenotype} of the result population.
	 *
	 * @return the best {@code Phenotype} of the result population
	 */
	public Phenotype<G, C> getBestPhenotype() {
		return _best.get();
	}

	/**
	 * Return the worst {@code Phenotype} of the result population.
	 *
	 * @return the worst {@code Phenotype} of the result population
	 */
	public Phenotype<G, C> getWorstPhenotype() {
		return _worst.get();
	}

	/**
	 * Return the best population fitness.
	 *
	 * @return The best population fitness.
	 */
	public C getBestFitness() {
		return _best.get() != null ? _best.get().getFitness() : null;
	}

	/**
	 * Return the worst population fitness.
	 *
	 * @return The worst population fitness.
	 */
	public C getWorstFitness() {
		return _worst.get() != null ? _worst.get().getFitness() : null;
	}

	/**
	 * Return the next evolution start object with the current population and
	 * the incremented generation.
	 *
	 * @return the next evolution start object
	 */
	EvolutionStart<G, C> next() {
		return EvolutionStart.of(_population, _generation + 1);
	}

	/**
	 * Compare {@code this} evolution result with another one, according the
	 * populations best individual.
	 *
	 * @param other the other evolution result to compare
	 * @return  a negative integer, zero, or a positive integer as this result
	 *          is less than, equal to, or greater than the specified result.
	 */
	@Override
	public int compareTo(final EvolutionResult<G, C> other) {
		return _optimize.compare(_best.get(), other._best.get());
	}

	private EvolutionResult<G, C> withTotalGenerations(final long total) {
		return of(
			_optimize,
			_population,
			_generation,
			total,
			_durations,
			_killCount,
			_invalidCount,
			_alterCount
		);
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_optimize)
			.and(_population)
			.and(_generation)
			.and(_totalGenerations)
			.and(_durations)
			.and(_killCount)
			.and(_invalidCount)
			.and(_alterCount)
			.and(getBestFitness()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof EvolutionResult<?, ?> &&
			eq(_optimize, ((EvolutionResult<?, ?>)obj)._optimize) &&
			eq(_population, ((EvolutionResult<?, ?>)obj)._population) &&
			eq(_generation, ((EvolutionResult<?, ?>)obj)._generation) &&
			eq(_totalGenerations, ((EvolutionResult<?, ?>)obj)._totalGenerations) &&
			eq(_durations, ((EvolutionResult<?, ?>)obj)._durations) &&
			eq(_killCount, ((EvolutionResult<?, ?>)obj)._killCount) &&
			eq(_invalidCount, ((EvolutionResult<?, ?>)obj)._invalidCount) &&
			eq(_alterCount, ((EvolutionResult<?, ?>)obj)._alterCount) &&
			eq(getBestFitness(), ((EvolutionResult<?, ?>)obj).getBestFitness());
	}


	/* *************************************************************************
	 *  Some static collector/factory methods.
	 * ************************************************************************/

	/**
	 * Return a collector which collects the best result of an evolution stream.
	 *
	 * <pre>{@code
	 * final Problem<ISeq<Point>, EnumGene<Point>, Double> tsm = ...;
	 * final EvolutionResult<EnumGene<Point>, Double> result = Engine.builder(tsm)
	 *     .optimize(Optimize.MINIMUM).build()
	 *     .stream()
	 *     .limit(100)
	 *     .collect(EvolutionResult.toBestEvolutionResult());
	 * }</pre>
	 *
	 * If the collected {@link EvolutionStream} is empty, the collector returns
	 * <b>{@code null}</b>.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 * @return a collector which collects the best result of an evolution stream
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<EvolutionResult<G, C>, ?, EvolutionResult<G, C>>
	toBestEvolutionResult() {
		return Collector.of(
			MinMax::<EvolutionResult<G, C>>of,
			MinMax::accept,
			MinMax::combine,
			mm -> mm.getMax() != null
				? mm.getMax().withTotalGenerations(mm.getCount())
				: null
		);
	}

	/**
	 * Return a collector which collects the best phenotype of an evolution
	 * stream.
	 *
	 * <pre>{@code
	 * final Problem<ISeq<Point>, EnumGene<Point>, Double> tsm = ...;
	 * final Phenotype<EnumGene<Point>, Double> result = Engine.builder(tsm)
	 *     .optimize(Optimize.MINIMUM).build()
	 *     .stream()
	 *     .limit(100)
	 *     .collect(EvolutionResult.toBestPhenotype());
	 * }</pre>
	 *
	 * If the collected {@link EvolutionStream} is empty, the collector returns
	 * <b>{@code null}</b>.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 * @return a collector which collects the best phenotype of an evolution
	 *         stream
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<EvolutionResult<G, C>, ?, Phenotype<G, C>>
	toBestPhenotype() {
		return Collector.of(
			MinMax::<EvolutionResult<G, C>>of,
			MinMax::accept,
			MinMax::combine,
			mm -> mm.getMax() != null
				? mm.getMax().getBestPhenotype()
				: null
		);
	}

	/**
	 * Return a collector which collects the best genotype of an evolution
	 * stream.
	 *
	 * <pre>{@code
	 * final Problem<ISeq<Point>, EnumGene<Point>, Double> tsm = ...;
	 * final Genotype<EnumGene<Point>> result = Engine.builder(tsm)
	 *     .optimize(Optimize.MINIMUM).build()
	 *     .stream()
	 *     .limit(100)
	 *     .collect(EvolutionResult.toBestGenotype());
	 * }</pre>
	 *
	 * If the collected {@link EvolutionStream} is empty, the collector returns
	 * <b>{@code null}</b>.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 * @return a collector which collects the best genotype of an evolution
	 *         stream
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<EvolutionResult<G, C>, ?, Genotype<G>>
	toBestGenotype() {
		return Collector.of(
			MinMax::<EvolutionResult<G, C>>of,
			MinMax::accept,
			MinMax::combine,
			mm -> mm.getMax() != null
				? mm.getMax().getBestPhenotype() != null
					? mm.getMax().getBestPhenotype().getGenotype()
					: null
				: null
		);
	}

	/**
	 * Return a collector which collects the best <em>result</em> (in the native
	 * problem space).
	 *
	 * <pre>{@code
	 * final Problem<ISeq<Point>, EnumGene<Point>, Double> tsm = ...;
	 * final ISeq<Point> route = Engine.builder(tsm)
	 *     .optimize(Optimize.MINIMUM).build()
	 *     .stream()
	 *     .limit(100)
	 *     .collect(EvolutionResult.toBestResult(tsm.codec().decoder()));
	 * }</pre>
	 *
	 * If the collected {@link EvolutionStream} is empty, the collector returns
	 * <b>{@code null}</b>.
	 *
	 * @since 3.6
	 *
	 * @param decoder the decoder which converts the {@code Genotype} into the
	 *        result of the problem space.
	 * @param <T> the <em>native</em> problem result type
	 * @param <G> the gene type
	 * @param <C> the fitness result type
	 * @return a collector which collects the best result of an evolution stream
	 * @throws NullPointerException if the given {@code decoder} is {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>, T>
	Collector<EvolutionResult<G, C>, ?, T>
	toBestResult(final Function<Genotype<G>, T> decoder) {
		requireNonNull(decoder);

		return Collector.of(
			MinMax::<EvolutionResult<G, C>>of,
			MinMax::accept,
			MinMax::combine,
			mm -> mm.getMax() != null
				? mm.getMax().getBestPhenotype() != null
					? decoder.apply(mm.getMax().getBestPhenotype().getGenotype())
					: null
				: null
		);
	}

	/**
	 * Return a collector which collects the best <em>result</em> (in the native
	 * problem space).
	 *
	 * <pre>{@code
	 * final Problem<ISeq<Point>, EnumGene<Point>, Double> tsm = ...;
	 * final ISeq<Point> route = Engine.builder(tsm)
	 *     .optimize(Optimize.MINIMUM).build()
	 *     .stream()
	 *     .limit(100)
	 *     .collect(EvolutionResult.toBestResult(tsm.codec()));
	 * }</pre>
	 *
	 * If the collected {@link EvolutionStream} is empty, the collector returns
	 * <b>{@code null}</b>.
	 *
	 * @since 3.6
	 *
	 * @param codec the problem decoder
	 * @param <T> the <em>native</em> problem result type
	 * @param <G> the gene type
	 * @param <C> the fitness result type
	 * @return a collector which collects the best result of an evolution stream
	 * @throws NullPointerException if the given {@code codec} is {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>, T>
	Collector<EvolutionResult<G, C>, ?, T>
	toBestResult(final Codec<T, G> codec) {
		return toBestResult(codec.decoder());
	}

	/**
	 * Return an new {@code EvolutionResult} object with the given values.
	 *
	 * @param optimize the optimization strategy used
	 * @param population the population after the evolution step
	 * @param generation the current generation
	 * @param totalGenerations the overall number of generations
	 * @param durations the timing (meta) information
	 * @param killCount the number of individuals which has been killed
	 * @param invalidCount the number of individuals which has been removed as
	 *        invalid
	 * @param alterCount the number of individuals which has been altered
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 * @return an new evolution result object
	 * @throws java.lang.NullPointerException if one of the parameters is
	 *         {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionResult<G, C> of(
		final Optimize optimize,
		final Population<G, C> population,
		final long generation,
		final long totalGenerations,
		final EvolutionDurations durations,
		final int killCount,
		final int invalidCount,
		final int alterCount
	) {
		return new EvolutionResult<>(
			optimize,
			population,
			generation,
			totalGenerations,
			durations,
			killCount,
			invalidCount,
			alterCount
		);
	}

	/**
	 * Return an new {@code EvolutionResult} object with the given values.
	 *
	 * @param optimize the optimization strategy used
	 * @param population the population after the evolution step
	 * @param generation the current generation
	 * @param durations the timing (meta) information
	 * @param killCount the number of individuals which has been killed
	 * @param invalidCount the number of individuals which has been removed as
	 *        invalid
	 * @param alterCount the number of individuals which has been altered
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 * @return an new evolution result object
	 * @throws java.lang.NullPointerException if one of the parameters is
	 *         {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionResult<G, C> of(
		final Optimize optimize,
		final Population<G, C> population,
		final long generation,
		final EvolutionDurations durations,
		final int killCount,
		final int invalidCount,
		final int alterCount
	) {
		return new EvolutionResult<>(
			optimize,
			population,
			generation,
			generation,
			durations,
			killCount,
			invalidCount,
			alterCount
		);
	}

}
