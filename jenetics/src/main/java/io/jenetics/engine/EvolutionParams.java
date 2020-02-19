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
import static io.jenetics.internal.util.SerialIO.readInt;
import static io.jenetics.internal.util.SerialIO.readLong;
import static io.jenetics.internal.util.SerialIO.writeInt;
import static io.jenetics.internal.util.SerialIO.writeLong;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;

import io.jenetics.Alterer;
import io.jenetics.Gene;
import io.jenetics.Mutator;
import io.jenetics.Selector;
import io.jenetics.SinglePointCrossover;
import io.jenetics.TournamentSelector;
import io.jenetics.internal.util.Requires;

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
 * @version 5.2
 * @since 5.2
 */
public final class EvolutionParams<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final Selector<G, C> _survivorsSelector;
	private final Selector<G, C> _offspringSelector;
	private final Alterer<G, C> _alterer;
	private final int _populationSize;
	private final double _offspringFraction;
	private final long _maximalPhenotypeAge;

	private EvolutionParams(
		final Selector<G, C> survivorsSelector,
		final Selector<G, C> offspringSelector,
		final Alterer<G, C> alterer,
		final int populationSize,
		final double offspringFraction,
		final long maximalPhenotypeAge
	) {
		_survivorsSelector = requireNonNull(survivorsSelector);
		_offspringSelector = requireNonNull(offspringSelector);
		_alterer = requireNonNull(alterer);
		_populationSize = Requires.positive(populationSize);
		_offspringFraction = Requires.probability(offspringFraction);
		_maximalPhenotypeAge = Requires.positive(maximalPhenotypeAge);
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
	 * Return the population size.
	 *
	 * @return the population size
	 */
	public int populationSize() {
		return _populationSize;
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
	 * Return the maximal allowed phenotype age.
	 *
	 * @return the maximal allowed phenotype age
	 */
	public long maximalPhenotypeAge() {
		return _maximalPhenotypeAge;
	}


	/* *************************************************************************
	 * Derived properties.
	 **************************************************************************/

	/**
	 * Return the number of offspring. <em>This is a derived property.</em>
	 *
	 * @return the offspring count.
	 */
	public int offspringSize() {
		return (int)Math.rint(_populationSize*_offspringFraction);
	}

	/**
	 * Return the number of survivors. <em>This is a derived property.</em>
	 *
	 * @return the number of survivors
	 */
	public int survivorsSize() {
		return _populationSize - offspringSize();
	}

	/**
	 * Return a new builder object, initialized with {@code this} parameters.
	 *
	 * @return a new pre-filled builder object
	 */
	public EvolutionParams.Builder<G, C> toBuilder() {
		return EvolutionParams.<G, C>builder()
			.survivorsSelector(survivorsSelector())
			.offspringSelector(offspringSelector())
			.alterers(alterer())
			.populationSize(populationSize())
			.offspringFraction(offspringFraction())
			.maximalPhenotypeAge(maximalPhenotypeAge());
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
		private int _populationSize = 50;
		private double _offspringFraction = 0.6;
		private long _maximalPhenotypeAge = 70;


		private Builder() {
		}

		public Builder<G, C> evolutionParams(final EvolutionParams<G, C> params) {
			survivorsSelector(params.survivorsSelector());
			offspringSelector(params.offspringSelector());
			alterers(params.alterer());
			populationSize(params.populationSize());
			offspringFraction(params.offspringFraction());
			maximalPhenotypeAge(params.maximalPhenotypeAge());
			return this;
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

			_populationSize = size;
			return this;
		}


		/**
		 * The offspring fraction.
		 *
		 * @param fraction the offspring fraction
		 * @return {@code this} builder, for command chaining
		 * @throws IllegalArgumentException if the fraction is not within the
		 *         range [0, 1].
		 */
		public Builder<G, C> offspringFraction(final double fraction) {
			_offspringFraction = probability(fraction);
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
				_populationSize,
				_offspringFraction,
				_maximalPhenotypeAge
			);
		}


		/* *********************************************************************
		 * Current properties
		 ***********************************************************************/

		/**
		 * Return the used {@link Alterer} of the GA.
		 *
		 * @return the used {@link Alterer} of the GA.
		 */
		public Alterer<G, C> alterer() {
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
		 * Return the number of individuals of a population.
		 *
		 * @return the number of individuals of a population
		 */
		public int populationSize() {
			return _populationSize;
		}

		/**
		 * Return the offspring fraction.
		 *
		 * @return the offspring fraction.
		 */
		public double offspringFraction() {
			return _offspringFraction;
		}

		/* *************************************************************************
		 * Derived properties.
		 **************************************************************************/

		/**
		 * Return the number of offspring. <em>This is a derived property.</em>
		 *
		 * @return the offspring count.
		 */
		public int offspringSize() {
			return (int)Math.round(_populationSize*_offspringFraction);
		}

		/**
		 * Return the number of survivors. <em>This is a derived property.</em>
		 *
		 * @return the number of survivors
		 */
		public int survivorsSize() {
			return _populationSize - offspringSize();
		}

	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.EVOLUTION_PARAMS, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final ObjectOutput out) throws IOException {
		out.writeObject(survivorsSelector());
		out.writeObject(offspringSelector());
		out.writeObject(alterer());
		writeInt(populationSize(), out);
		out.writeDouble(offspringFraction());
		writeLong(maximalPhenotypeAge(), out);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	static EvolutionParams read(final ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		return new EvolutionParams(
			(Selector)in.readObject(),
			(Selector)in.readObject(),
			(Alterer)in.readObject(),
			readInt(in),
			in.readDouble(),
			readLong(in)
		);
	}

}
