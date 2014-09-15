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
import java.time.Duration;
import java.util.Comparator;
import java.util.stream.Collector;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;
import org.jenetics.internal.util.Lazy;

import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.stat.MinMax;

/**
 * Represents a state of the GA after an evolution step.
 *
 * @param <G> the gene type
 * @param <C> the fitness type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-09-15 $</em>
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
	private final int _generation;

	private final Durations _durations;
	private final int _killCount;
	private final int _invalidCount;
	private final int _alterCount;

	private final Lazy<Phenotype<G, C>> _best;
	private final Lazy<Phenotype<G, C>> _worst;

	private EvolutionResult(
		final Optimize optimize,
		final Population<G, C> population,
		final int generation,
		final Durations durations,
		final int killCount,
		final int invalidCount,
		final int alterCount
	) {
		_optimize = requireNonNull(optimize);
		_population = requireNonNull(population);
		_generation = generation;
		_durations = requireNonNull(durations);
		_killCount = killCount;
		_invalidCount = invalidCount;
		_alterCount = alterCount;

		_best = Lazy.of(this::best);
		_worst = Lazy.of(this::worst);
	}

	private Phenotype<G, C> best() {
		return _population.stream().max(_optimize.ascending()).orElse(null);
	}

	private Phenotype<G, C> worst() {
		return _population.stream().max(_optimize.descending()).orElse(null);
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
		return _population;
	}

	/**
	 * The current generation.
	 *
	 * @return the current generation
	 */
	public int getGeneration() {
		return _generation;
	}

	/**
	 * Return the timing (meta) information of the evolution step.
	 *
	 * @return the timing (meta) information of the evolution step
	 */
	public Durations getDurations() {
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

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_optimize)
			.and(_durations)
			.and(_killCount)
			.and(_invalidCount)
			.and(_alterCount)
			.and(_population)
			.and(_generation).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(result ->
			eq(_optimize, result._optimize) &&
			eq(_durations, result._durations) &&
			eq(_killCount, result._killCount) &&
			eq(_invalidCount, result._invalidCount) &&
			eq(_alterCount, result._alterCount) &&
			eq(_population, result._population) &&
			eq(_generation, result._generation)
		);
	}

	/* *************************************************************************
	 *  Some static factory methods.
	 * ************************************************************************/

	private static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Comparator<EvolutionResult<G, C>> bestComparator(final Optimize opt) {
		return (a, b) -> opt.compare(a.getBestPhenotype(), b.getBestPhenotype());
	}

	private static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Comparator<EvolutionResult<G, C>> worstComparator(final Optimize opt) {
		return (a, b) -> opt.compare(a.getWorstPhenotype(), b.getWorstPhenotype());
	}

	static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<EvolutionResult<G, C>, ?, EvolutionResult<G, C>>
	best(final Optimize opt) {
		final Comparator<EvolutionResult<G, C>> comparator = (a, b) ->
			opt.compare(a.getBestPhenotype(), b.getBestPhenotype());

		return Collector.of(
			() -> MinMax.of(comparator),
			MinMax::accept,
			MinMax::combine,
			MinMax::getMax
		);
	}

	static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<EvolutionResult<G, C>, ?, Phenotype<G, C>>
	bestPhenotype(final Optimize opt) {
		final Comparator<EvolutionResult<G, C>> comparator = (a, b) ->
			opt.compare(a.getBestPhenotype(), b.getBestPhenotype());

		return Collector.of(
			() -> MinMax.of(comparator),
			MinMax::accept,
			MinMax::combine,
			mm -> mm.getMax().getBestPhenotype()
		);
	}

	static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<EvolutionResult<G, C>, ?, Genotype<G>>
	bestGenotype(final Optimize opt) {
		final Comparator<EvolutionResult<G, C>> comparator = (a, b) ->
			opt.compare(a.getBestPhenotype(), b.getBestPhenotype());

		return Collector.of(
			() -> MinMax.of(comparator),
			MinMax::accept,
			MinMax::combine,
			mm -> mm.getMax().getBestPhenotype().getGenotype()
		);
	}

	static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<EvolutionResult<G, C>, ?, EvolutionResult<G, C>>
	worst(final Optimize opt) {
		final Comparator<EvolutionResult<G, C>> comparator = (a, b) ->
			opt.compare(a.getWorstPhenotype(), b.getWorstPhenotype());

		return Collector.of(
			() -> MinMax.of(comparator),
			MinMax::accept,
			MinMax::combine,
			MinMax::getMin
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
	static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionResult<G, C> of(
		final Optimize optimize,
		final Population<G, C> population,
		final int generation,
		final Durations durations,
		final int killCount,
		final int invalidCount,
		final int alterCount
	) {
		return new EvolutionResult<>(
			optimize,
			population,
			generation,
			durations,
			killCount,
			invalidCount,
			alterCount
		);
	}

	/* *************************************************************************
	 * Inner classes
	 **************************************************************************/

	/**
	 * This class contains timing information about one evolution step.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 3.0
	 * @version 3.0 &mdash; <em>$Date: 2014-09-15 $</em>
	 */
	public static final class Durations implements Serializable {
		private static final long serialVersionUID = 1L;

		private final Duration _offspringSelectionDuration;
		private final Duration _survivorsSelectionDuration;
		private final Duration _offspringAlterDuration;
		private final Duration _offspringFilterDuration;
		private final Duration _survivorFilterDuration;
		private final Duration _evaluationDuration;
		private final Duration _evolveDuration;

		private Durations(
			final Duration offspringSelectionDuration,
			final Duration survivorsSelectionDuration,
			final Duration offspringAlterDuration,
			final Duration offspringFilterDuration,
			final Duration survivorFilterDuration,
			final Duration evaluationDuration,
			final Duration evolveDuration
		) {
			_offspringSelectionDuration = requireNonNull(offspringSelectionDuration);
			_survivorsSelectionDuration = requireNonNull(survivorsSelectionDuration);
			_offspringAlterDuration = requireNonNull(offspringAlterDuration);
			_offspringFilterDuration = requireNonNull(offspringFilterDuration);
			_survivorFilterDuration = requireNonNull(survivorFilterDuration);
			_evaluationDuration = requireNonNull(evaluationDuration);
			_evolveDuration = requireNonNull(evolveDuration);
		}

		/**
		 * Return the duration needed for selecting the offspring population.
		 *
		 * @return the duration needed for selecting the offspring population
		 */
		public Duration getOffspringSelectionDuration() {
			return _offspringSelectionDuration;
		}

		/**
		 * Return the duration needed for selecting the survivors population.
		 *
		 * @return the duration needed for selecting the survivors population
		 */
		public Duration getSurvivorsSelectionDuration() {
			return _survivorsSelectionDuration;
		}

		/**
		 * Return the duration needed for altering the offspring population.
		 *
		 * @return the duration needed for altering the offspring population
		 */
		public Duration getOffspringAlterDuration() {
			return _offspringAlterDuration;
		}

		/**
		 * Return the duration needed for removing and replacing invalid offspring
		 * individuals.
		 *
		 * @return the duration needed for removing and replacing invalid offspring
		 *         individuals
		 */
		public Duration getOffspringFilterDuration() {
			return _offspringFilterDuration;
		}

		/**
		 * Return the duration needed for removing and replacing old and invalid
		 * survivor individuals.
		 *
		 * @return the duration needed for removing and replacing old and invalid
		 *         survivor individuals
		 */
		public Duration getSurvivorFilterDuration() {
			return _survivorFilterDuration;
		}

		/**
		 * Return the duration needed for evaluating the fitness function of the new
		 * individuals.
		 *
		 * @return the duration needed for evaluating the fitness function of the new
		 *         individuals
		 */
		public Duration getEvaluationDuration() {
			return _evaluationDuration;
		}

		/**
		 * Return the duration needed for the whole evolve step.
		 *
		 * @return the duration needed for the whole evolve step
		 */
		public Duration getEvolveDuration() {
			return _evolveDuration;
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass())
				.and(_offspringSelectionDuration)
				.and(_survivorsSelectionDuration)
				.and(_offspringAlterDuration)
				.and(_offspringFilterDuration)
				.and(_survivorFilterDuration)
				.and(_evaluationDuration)
				.and(_evolveDuration).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(d ->
				eq(_offspringSelectionDuration, d._offspringSelectionDuration) &&
				eq(_survivorsSelectionDuration, d._survivorsSelectionDuration) &&
				eq(_offspringAlterDuration, d._offspringAlterDuration) &&
				eq(_offspringFilterDuration, d._offspringFilterDuration) &&
				eq(_survivorFilterDuration, d._survivorFilterDuration) &&
				eq(_evaluationDuration, d._evaluationDuration) &&
				eq(_evolveDuration, d._evolveDuration)
			);
		}

		/**
		 * Return an new {@code EvolutionDurations} object with the given values.
		 *
		 * @param offspringSelectionDuration the duration needed for selecting the
		 *        offspring population
		 * @param survivorsSelectionDuration the duration needed for selecting the
		 *        survivors population
		 * @param offspringAlterDuration the duration needed for altering the
		 *        offspring population
		 * @param offspringFilterDuration the duration needed for removing and
		 *        replacing invalid offspring individuals
		 * @param survivorFilterDuration the duration needed for removing and
		 *        replacing old and invalid survivor individuals
		 * @param evaluationDuration the duration needed for evaluating the fitness
		 *        function of the new individuals
		 * @param evolveDuration the duration needed for the whole evolve step
		 * @return an new durations object
		 * @throws NullPointerException if one of the arguments is
		 *         {@code null}
		 */
		static Durations of(
			final Duration offspringSelectionDuration,
			final Duration survivorsSelectionDuration,
			final Duration offspringAlterDuration,
			final Duration offspringFilterDuration,
			final Duration survivorFilterDuration,
			final Duration evaluationDuration,
			final Duration evolveDuration
		) {
			return new Durations(
				offspringSelectionDuration,
				survivorsSelectionDuration,
				offspringAlterDuration,
				offspringFilterDuration,
				survivorFilterDuration,
				evaluationDuration,
				evolveDuration
			);
		}

	}
}
