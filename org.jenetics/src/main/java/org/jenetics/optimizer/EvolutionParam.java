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
package org.jenetics.optimizer;

import static java.lang.Math.round;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.require.probability;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jenetics.Alterer;
import org.jenetics.Gene;
import org.jenetics.Mutator;
import org.jenetics.Optimize;
import org.jenetics.Selector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.TournamentSelector;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class EvolutionParam<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{
	private Function<? super C, ? extends C> _fitnessScaler = a -> a;
	private Selector<G, C> _survivorsSelector = new TournamentSelector<>(3);
	private Selector<G, C> _offspringSelector = new TournamentSelector<>(3);
	private Alterer<G, C> _alterer = Alterer.of(
		new SinglePointCrossover<G, C>(0.2),
		new Mutator<>(0.15)
	);
	private Optimize _optimize = Optimize.MAXIMUM;
	private double _offspringFraction = 0.6;
	private int _populationSize = 50;
	private long _maximalPhenotypeAge = 70;


	/**
	 * Set the fitness scaler of the evolution {@code Engine}. <i>Default
	 * value is set to the identity function.</i>
	 *
	 * @param scaler the fitness scale to use in the GA {@code Engine}
	 * @return {@code this} builder, for command chaining
	 */
	public EvolutionParam<G, C> fitnessScaler(
		final Function<? super C, ? extends C> scaler
	) {
		_fitnessScaler = requireNonNull(scaler);
		return this;
	}

	/**
	 * The selector used for selecting the offspring population. <i>Default
	 * values is set to {@code TournamentSelector<>(3)}.</i>
	 *
	 * @param selector used for selecting the offspring population
	 * @return {@code this} builder, for command chaining
	 */
	public EvolutionParam<G, C> offspringSelector(
		final Selector<G, C> selector
	) {
		_offspringSelector = requireNonNull(selector);
		return this;
	}

	/**
	 * The selector used for selecting the survivors population. <i>Default
	 * values is set to {@code TournamentSelector<>(3)}.</i>
	 *
	 * @param selector used for selecting survivors population
	 * @return {@code this} builder, for command chaining
	 */
	public EvolutionParam<G, C> survivorsSelector(
		final Selector<G, C> selector
	) {
		_survivorsSelector = requireNonNull(selector);
		return this;
	}

	/**
	 * The selector used for selecting the survivors and offspring
	 * population. <i>Default values is set to
	 * {@code TournamentSelector<>(3)}.</i>
	 *
	 * @param selector used for selecting survivors and offspring population
	 * @return {@code this} builder, for command chaining
	 */
	public EvolutionParam<G, C> selector(final Selector<G, C> selector) {
		_offspringSelector = requireNonNull(selector);
		_survivorsSelector = requireNonNull(selector);
		return this;
	}

	/**
	 * The alterers used for alter the offspring population. <i>Default
	 * values is set to {@code new SinglePointCrossover<>(0.2)} followed by
	 * {@code new Mutator<>(0.15)}.</i>
	 *
	 * @param first the first alterer used for alter the offspring
	 *        population
	 * @param rest the rest of the alterers used for alter the offspring
	 *        population
	 * @return {@code this} builder, for command chaining
	 * @throws java.lang.NullPointerException if one of the alterers is
	 *         {@code null}.
	 */
	@SafeVarargs
	public final EvolutionParam<G, C> alterers(
		final Alterer<G, C> first,
		final Alterer<G, C>... rest
	) {
		requireNonNull(first);
		Stream.of(rest).forEach(Objects::requireNonNull);

		_alterer = rest.length == 0 ?
			first :
			Alterer.of(rest).compose(first);

		return this;
	}

	/**
	 * The optimization strategy used by the engine. <i>Default values is
	 * set to {@code Optimize.MAXIMUM}.</i>
	 *
	 * @param optimize the optimization strategy used by the engine
	 * @return {@code this} builder, for command chaining
	 */
	public EvolutionParam<G, C> optimize(final Optimize optimize) {
		_optimize = requireNonNull(optimize);
		return this;
	}

	/**
	 * The offspring fraction. <i>Default values is set to {@code 0.6}.</i>
	 *
	 * @param fraction the offspring fraction
	 * @return {@code this} builder, for command chaining
	 * @throws java.lang.IllegalArgumentException if the fraction is not
	 *         within the range [0, 1].
	 */
	public EvolutionParam<G, C> offspringFraction(final double fraction) {
		_offspringFraction = probability(fraction);
		return this;
	}

	/**
	 * The number of individuals which form the population. <i>Default
	 * values is set to {@code 50}.</i>
	 *
	 * @param size the number of individuals of a population
	 * @return {@code this} builder, for command chaining
	 * @throws java.lang.IllegalArgumentException if {@code size < 1}
	 */
	public EvolutionParam<G, C> populationSize(final int size) {
		if (size < 1) {
			throw new IllegalArgumentException(format(
				"Population size must be greater than zero, but was %s.", size
			));
		}
		_populationSize = size;
		return this;
	}

	/**
	 * The maximal allowed age of a phenotype. <i>Default values is set to
	 * {@code 70}.</i>
	 *
	 * @param age the maximal phenotype age
	 * @return {@code this} builder, for command chaining
	 * @throws java.lang.IllegalArgumentException if {@code age < 1}
	 */
	public EvolutionParam<G, C> maximalPhenotypeAge(final long age) {
		if (age < 1) {
			throw new IllegalArgumentException(format(
				"Phenotype age must be greater than one, but was %s.", age
			));
		}
		_maximalPhenotypeAge = age;
		return this;
	}





	private int getSurvivorsCount() {
		return _populationSize - getOffspringCount();
	}

	private int getOffspringCount() {
		return (int)round(_offspringFraction*_populationSize);
	}

	/**
	 * Return the used {@link Alterer} of the GA.
	 *
	 * @return the used {@link Alterer} of the GA.
	 */
	public Alterer<G, C> getAlterers() {
		return _alterer;
	}

	/**
	 * Return the fitness scaler of the GA engine.
	 *
	 * @return the fitness scaler
	 */
	public Function<? super C, ? extends C> getFitnessScaler() {
		return _fitnessScaler;
	}

	/**
	 * Return the maximal allowed phenotype age.
	 *
	 * @return the maximal allowed phenotype age
	 */
	public long getMaximalPhenotypeAge() {
		return _maximalPhenotypeAge;
	}

	/**
	 * Return the offspring fraction.
	 *
	 * @return the offspring fraction.
	 */
	public double getOffspringFraction() {
		return _offspringFraction;
	}

	/**
	 * Return the used offspring {@link Selector} of the GA.
	 *
	 * @since 3.1
	 *
	 * @return the used offspring {@link Selector} of the GA.
	 */
	public Selector<G, C> getOffspringSelector() {
		return _offspringSelector;
	}

	/**
	 * Return the used survivor {@link Selector} of the GA.
	 *
	 * @return the used survivor {@link Selector} of the GA.
	 */
	public Selector<G, C> getSurvivorsSelector() {
		return _survivorsSelector;
	}

	/**
	 * Return the optimization strategy.
	 *
	 * @return the optimization strategy
	 */
	public Optimize getOptimize() {
		return _optimize;
	}

	/**
	 * Return the number of individuals of a population.
	 *
	 * @return the number of individuals of a population
	 */
	public int getPopulationSize() {
		return _populationSize;
	}
}
