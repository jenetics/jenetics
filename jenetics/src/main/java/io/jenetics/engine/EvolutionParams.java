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

import static java.lang.Math.round;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.require.probability;

import java.util.Objects;
import java.util.stream.Stream;

import io.jenetics.Alterer;
import io.jenetics.Gene;
import io.jenetics.Mutator;
import io.jenetics.Optimize;
import io.jenetics.Selector;
import io.jenetics.SinglePointCrossover;
import io.jenetics.TournamentSelector;

/**
 * @param <G> the gene type
 * @param <C> the fitness function result type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class EvolutionParams<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	private final Selector<G, C> _survivorsSelector;
	private final Selector<G, C> _offspringSelector;
	private final Alterer<G, C> _alterer;
	private final Optimize _optimize;
	private final int _offspringCount;
	private final int _survivorsCount;
	private final long _maximalPhenotypeAge;

	private EvolutionParams(
		final Selector<G, C> survivorsSelector,
		final Selector<G, C> offspringSelector,
		final Alterer<G, C> alterer,
		final Optimize optimize,
		final int offspringCount,
		final int survivorsCount,
		final long maximalPhenotypeAge
	) {
		_survivorsSelector = survivorsSelector;
		_offspringSelector = offspringSelector;
		_alterer = alterer;
		_optimize = optimize;
		_offspringCount = offspringCount;
		_survivorsCount = survivorsCount;
		_maximalPhenotypeAge = maximalPhenotypeAge;
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
	 * Return the used offspring {@link Selector} of the GA.
	 *
	 * @return the used offspring {@link Selector} of the GA.
	 */
	public Selector<G, C> getOffspringSelector() {
		return _offspringSelector;
	}

	/**
	 * Return the used {@link Alterer} of the GA.
	 *
	 * @return the used {@link Alterer} of the GA.
	 */
	public Alterer<G, C> getAlterer() {
		return _alterer;
	}

	/**
	 * Return the number of selected offsprings.
	 *
	 * @return the number of selected offsprings
	 */
	public int getOffspringCount() {
		return _offspringCount;
	}

	/**
	 * The number of selected survivors.
	 *
	 * @return the number of selected survivors
	 */
	public int getSurvivorsCount() {
		return _survivorsCount;
	}

	/**
	 * Return the number of individuals of a population.
	 *
	 * @return the number of individuals of a population
	 */
	public int getPopulationSize() {
		return _offspringCount + _survivorsCount;
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
	 * Return the optimization strategy.
	 *
	 * @return the optimization strategy
	 */
	public Optimize getOptimize() {
		return _optimize;
	}

	/**
	 * Create a new evolution parameter builder.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness function result type
	 * @return a new parameter builder
	 */
	public static  <G extends Gene<?, G>, C extends Comparable<? super C>>
	Builder<G, C> builder() {
		return new Builder<>();
	}

	/**
	 * Return the default evolution parameters.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness function result type
	 * @return the default evolution parameters
	 */
	public static  <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionParams<G, C> defaultParams() {
		return EvolutionParams.<G, C>builder().build();
	}

	/**
	 * Builder class for the evolution parameter.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness function result type
	 */
	public static final class Builder<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	> {

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


		private Builder() {
		}

		/**
		 * The selector used for selecting the offspring population. <i>Default
		 * values is set to {@code TournamentSelector<>(3)}.</i>
		 *
		 * @param selector used for selecting the offspring population
		 * @return {@code this} builder, for command chaining
		 */
		public Builder<G, C> offspringSelector(
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
		public Builder<G, C> survivorsSelector(
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
		public Builder<G, C> selector(final Selector<G, C> selector) {
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
		public final Builder<G, C> alterers(
			final Alterer<G, C> first,
			final Alterer<G, C>... rest
		) {
			requireNonNull(first);
			Stream.of(rest).forEach(Objects::requireNonNull);

			_alterer = rest.length == 0
				? first
				: Alterer.of(rest).compose(first);

			return this;
		}

		/**
		 * The optimization strategy used by the engine. <i>Default values is
		 * set to {@code Optimize.MAXIMUM}.</i>
		 *
		 * @param optimize the optimization strategy used by the engine
		 * @return {@code this} builder, for command chaining
		 */
		public Builder<G, C> optimize(final Optimize optimize) {
			_optimize = requireNonNull(optimize);
			return this;
		}

		/**
		 * Set to a fitness maximizing strategy.
		 *
		 * @since 3.4
		 *
		 * @return {@code this} builder, for command chaining
		 */
		public Builder<G, C> maximizing() {
			return optimize(Optimize.MAXIMUM);
		}

		/**
		 * Set to a fitness minimizing strategy.
		 *
		 * @return {@code this} builder, for command chaining
		 */
		public Builder<G, C> minimizing() {
			return optimize(Optimize.MINIMUM);
		}

		/**
		 * The offspring fraction. <i>Default values is set to {@code 0.6}.</i>
		 * This method call is equivalent to
		 * {@code survivorsFraction(1 - offspringFraction)} and will override
		 * any previously set survivors-fraction.
		 *
		 * @see #survivorsFraction(double)
		 *
		 * @param fraction the offspring fraction
		 * @return {@code this} builder, for command chaining
		 * @throws java.lang.IllegalArgumentException if the fraction is not
		 *         within the range [0, 1].
		 */
		public Builder<G, C> offspringFraction(final double fraction) {
			_offspringFraction = probability(fraction);
			return this;
		}

		/**
		 * The survivors fraction. <i>Default values is set to {@code 0.4}.</i>
		 * This method call is equivalent to
		 * {@code offspringFraction(1 - survivorsFraction)} and will override
		 * any previously set offspring-fraction.
		 *
		 * @see #offspringFraction(double)
		 *
		 * @param fraction the survivors fraction
		 * @return {@code this} builder, for command chaining
		 * @throws java.lang.IllegalArgumentException if the fraction is not
		 *         within the range [0, 1].
		 */
		public Builder<G, C> survivorsFraction(final double fraction) {
			_offspringFraction = 1.0 - probability(fraction);
			return this;
		}

		/**
		 * The number of offspring individuals.
		 *
		 * @param size the number of offspring individuals.
		 * @return {@code this} builder, for command chaining
		 * @throws java.lang.IllegalArgumentException if the size is not
		 *         within the range [0, population-size].
		 */
		public Builder<G, C> offspringSize(final int size) {
			if (size < 0) {
				throw new IllegalArgumentException(format(
					"Offspring size must be greater or equal zero, but was %s.",
					size
				));
			}

			return offspringFraction((double)size/(double)_populationSize);
		}

		/**
		 * The number of survivors.
		 *
		 * @param size the number of survivors.
		 * @return {@code this} builder, for command chaining
		 * @throws java.lang.IllegalArgumentException if the size is not
		 *         within the range [0, population-size].
		 */
		public Builder<G, C> survivorsSize(final int size) {
			if (size < 0) {
				throw new IllegalArgumentException(format(
					"Survivors must be greater or equal zero, but was %s.",
					size
				));
			}

			return survivorsFraction((double)size/(double)_populationSize);
		}

		/**
		 * The number of individuals which form the population. <i>Default
		 * values is set to {@code 50}.</i>
		 *
		 * @param size the number of individuals of a population
		 * @return {@code this} builder, for command chaining
		 * @throws java.lang.IllegalArgumentException if {@code size < 1}
		 */
		public Builder<G, C> populationSize(final int size) {
			if (size < 1) {
				throw new IllegalArgumentException(format(
					"Population size must be greater than zero, but was %s.",
					size
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
		public Builder<G, C> maximalPhenotypeAge(final long age) {
			if (age < 1) {
				throw new IllegalArgumentException(format(
					"Phenotype age must be greater than one, but was %s.", age
				));
			}
			_maximalPhenotypeAge = age;
			return this;
		}

		/**
		 * Builds an new {@code EvolutionParams} instance from the set properties.
		 *
		 * @return an new {@code EvolutionParams} instance from the set properties
		 */
		public EvolutionParams<G, C> build() {
			return new EvolutionParams<>(
				_survivorsSelector,
				_offspringSelector,
				_alterer,
				_optimize,
				offspringCount(),
				survivorsCount(),
				_maximalPhenotypeAge
			);
		}


		/**
		 * Return the used {@link Alterer} of the GA.
		 *
		 * @return the used {@link Alterer} of the GA.
		 */
		public Alterer<G, C> alterers() {
			return _alterer;
		}


		/**
		 * Return the maximal allowed phenotype age.
		 *
		 * @return the maximal allowed phenotype age
		 */
		public long maximalPhenotypeAge() {
			return _maximalPhenotypeAge;
		}

		/**
		 * Return the offspring fraction.
		 *
		 * @return the offspring fraction.
		 */
		public double offspringFraction() {
			return _offspringFraction;
		}

		/**
		 * Return the used offspring {@link Selector} of the GA.
		 *
		 * @return the used offspring {@link Selector} of the GA.
		 */
		public Selector<G, C> offspringSelector() {
			return _offspringSelector;
		}

		/**
		 * Return the used survivor {@link Selector} of the GA.
		 *
		 * @return the used survivor {@link Selector} of the GA.
		 */
		public Selector<G, C> survivorsSelector() {
			return _survivorsSelector;
		}

		/**
		 * Return the optimization strategy.
		 *
		 * @return the optimization strategy
		 */
		public Optimize optimize() {
			return _optimize;
		}

		/**
		 * Return the survivors count.
		 *
		 * @return the survivors count
		 */
		public int survivorsCount() {
			return _populationSize - offspringCount();
		}

		/**
		 * Return the offspring count.
		 *
		 * @return the offspring count
		 */
		public int offspringCount() {
			return (int)round(_offspringFraction*_populationSize);
		}

		/**
		 * Return the number of individuals of a population.
		 *
		 * @return the number of individuals of a population
		 */
		public int populationSize() {
			return _populationSize;
		}

	}

}
