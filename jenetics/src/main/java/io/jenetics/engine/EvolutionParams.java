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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.Requires.probability;

import java.util.Objects;
import java.util.stream.Stream;

import io.jenetics.Alterer;
import io.jenetics.Gene;
import io.jenetics.Mutator;
import io.jenetics.Selector;
import io.jenetics.SinglePointCrossover;
import io.jenetics.TournamentSelector;

/**
 * This class collects the parameters which control the behaviour of the
 * evolution process. This doesn't include the parameters for the
 * <em>technical</em> execution like the used execution service.
 *
 * @see Engine
 * @see Engine.Builder
 *
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
	private final int _offspringCount;
	private final int _survivorsCount;
	private final long _maximalPhenotypeAge;

	private EvolutionParams(
		final Selector<G, C> survivorsSelector,
		final Selector<G, C> offspringSelector,
		final Alterer<G, C> alterer,
		final int offspringCount,
		final int survivorsCount,
		final long maximalPhenotypeAge
	) {
		_survivorsSelector = survivorsSelector;
		_offspringSelector = offspringSelector;
		_alterer = alterer;
		_offspringCount = offspringCount;
		_survivorsCount = survivorsCount;
		_maximalPhenotypeAge = maximalPhenotypeAge;
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
	 * Return the used offspring {@link Selector} of the GA.
	 *
	 * @return the used offspring {@link Selector} of the GA.
	 */
	public Selector<G, C> offspringSelector() {
		return _offspringSelector;
	}

	/**
	 * Return the used {@link Alterer} of the GA.
	 *
	 * @return the used {@link Alterer} of the GA.
	 */
	public Alterer<G, C> alterer() {
		return _alterer;
	}


	/**
	 * Return the number of offspring.
	 *
	 * @return the offspring count.
	 */
	public double offspringCount() {
		return _offspringCount;
	}

	/**
	 * Return the number of survivors.
	 *
	 * @return the number of survivors
	 */
	public int survivorsCount() {
		return _survivorsCount;
	}

	/**
	 * Return the population count. The value is derived from the offspring and
	 * survivors count.
	 *
	 * @return the population count
	 */
	public int populationSize() {
		return _survivorsCount + _survivorsCount;
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


	/* *************************************************************************
	 * Params builder
	 **************************************************************************/

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
		private int _offspringCount = 30;
		private int _survivorsCount = 20;
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
		 * The number of offspring individuals.
		 *
		 * @param count the number of offspring individuals.
		 * @return {@code this} builder, for command chaining
		 * @throws IllegalArgumentException if the count is smaller then zero
		 */
		public Builder<G, C> offspringCount(final int count) {
			if (count < 0) {
				throw new IllegalArgumentException(format(
					"Offspring count must be greater or equal zero, but was %d.",
					count
				));
			}
			_offspringCount = count;
			return this;
		}

		/**
		 * The number of survivors.
		 *
		 * @param count the number of survivors.
		 * @return {@code this} builder, for command chaining
		 @throws IllegalArgumentException if the count is smaller then zero
		 */
		public Builder<G, C> survivorsCount(final int count) {
			if (count < 0) {
				throw new IllegalArgumentException(format(
					"Survivors count must be greater or equal zero, but was %d.",
					count
				));
			}
			_survivorsCount = count;
			return this;
		}

		/**
		 * The offspring fraction.
		 *
		 * @see #survivorsFraction(double)
		 *
		 * @param fraction the offspring fraction
		 * @return {@code this} builder, for command chaining
		 * @throws IllegalArgumentException if the fraction is not within the
		 *         range [0, 1].
		 */
		public Builder<G, C> offspringFraction(final double fraction) {
			probability(fraction);
			final int populationCount = _offspringCount + _survivorsCount;
			_offspringCount = (int)Math.round(populationCount*fraction);
			_survivorsCount = populationCount - _offspringCount;
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
		 * @throws IllegalArgumentException if the fraction is not within the
		 *         range [0, 1].
		 */
		public Builder<G, C> survivorsFraction(final double fraction) {
			probability(fraction);
			final int populationCount = _offspringCount + _survivorsCount;
			_survivorsCount = (int)Math.round(populationCount*fraction);
			_offspringCount = populationCount - _survivorsCount;
			return this;
		}

		/**
		 * The number of individuals which form the population. <i>Default
		 * values is set to {@code 50}.</i>
		 *
		 * @param size the number of individuals of a population
		 * @return {@code this} builder, for command chaining
		 * @throws IllegalArgumentException if {@code size < 1}
		 */
		public Builder<G, C> populationSize(final int size) {
			if (size < 1) {
				throw new IllegalArgumentException(format(
					"Population size must be greater than zero, but was %s.",
					size
				));
			}

			final double offspringFraction =
				_offspringCount/(double)(_offspringCount + _survivorsCount);
			_offspringCount = (int)Math.round(size*offspringFraction);
			_survivorsCount = size - _offspringCount;
			return this;
		}

		/**
		 * The maximal allowed age of a phenotype. <i>Default values is set to
		 * {@code 70}.</i>
		 *
		 * @param age the maximal phenotype age
		 * @return {@code this} builder, for command chaining
		 * @throws IllegalArgumentException if {@code age < 1}
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
				_offspringCount,
				_survivorsCount,
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
		 * Return the survivors count.
		 *
		 * @return the survivors count
		 */
		public int survivorsCount() {
			return _survivorsCount;
		}

		public double survivorsFraction() {
			return _survivorsCount/(double)populationSize();
		}

		/**
		 * Return the offspring count.
		 *
		 * @return the offspring count
		 */
		public int offspringCount() {
			return _offspringCount;
		}

		public double offspringFraction() {
			return _offspringCount/(double)populationSize();
		}

		/**
		 * Return the number of individuals of a population.
		 *
		 * @return the number of individuals of a population
		 */
		public int populationSize() {
			return _offspringCount + _survivorsCount;
		}

	}

}
