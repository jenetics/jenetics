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

import static java.lang.Math.round;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.Alterer;
import org.jenetics.Gene;
import org.jenetics.Selector;
import org.jenetics.internal.util.Hash;
import org.jenetics.internal.util.jaxb;
import org.jenetics.internal.util.require;
import org.jenetics.util.Seq;

/**
 * Collects the evolution {@code Engine} properties, which determines the
 * evolution <i>performance</i> of the GA.
 *
 * @param <G> the gene type of the problem encoding
 * @param <C> the fitness function return type of the problem encoding
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(EvolutionParam.Model.Adapter.class)
public final class EvolutionParam<
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

	/**
	 * Create a new evolution parameter object with the given parameter.
	 *
	 * @param survivorsSelector the used survivor {@link Selector} of the GA
	 * @param offspringSelector the used offspring {@link Selector} of the GA
	 * @param alterer the used {@link Alterer} of the GA
	 * @param populationSize the number of individuals of a population
	 * @param offspringFraction the offspring fraction
	 * @param maximalPhenotypeAge the maximal allowed phenotype age
	 * @throws NullPointerException if one of the reference types is {@code null}
	 * @throws IllegalArgumentException if the population size or the maximal
	 *         phenotype age is smaller than one.
	 * @throws IllegalArgumentException if the offspring fraction is not within
	 *         the range [0..1].
	 */
	private EvolutionParam(
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
		_populationSize = require.positive(populationSize);
		_offspringFraction = require.probability(offspringFraction);
		_maximalPhenotypeAge = require.positive(maximalPhenotypeAge);
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
	 * Return the used survivor {@link Selector} of the GA.
	 *
	 * @return the used survivor {@link Selector} of the GA.
	 */
	public Selector<G, C> getSurvivorsSelector() {
		return _survivorsSelector;
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
	 * Return the number of individuals of a population.
	 *
	 * @return the number of individuals of a population
	 */
	public int getPopulationSize() {
		return _populationSize;
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
	 * Return the number of selected offsprings.
	 *
	 * @return the number of selected offsprings
	 */
	public int getOffspringCount() {
		return (int)round(_offspringFraction*_populationSize);
	}

	/**
	 * The number of selected survivors.
	 *
	 * @return the number of selected survivors
	 */
	public int getSurvivorsCount() {
		return _populationSize - getOffspringCount();
	}

	/**
	 * Return the maximal allowed phenotype age.
	 *
	 * @return the maximal allowed phenotype age
	 */
	public long getMaximalPhenotypeAge() {
		return _maximalPhenotypeAge;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
				.and(getSurvivorsSelector())
				.and(getOffspringSelector())
				.and(getAlterer())
				.and(getPopulationSize())
				.and(getOffspringFraction())
				.and(getMaximalPhenotypeAge()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof EvolutionParam<?, ?> &&
				Objects.equals(
					_survivorsSelector,
					((EvolutionParam<?, ?>)obj)._survivorsSelector
				) &&
				Objects.equals(
					_offspringSelector,
					((EvolutionParam<?, ?>)obj)._offspringSelector
				) &&
				Objects.equals(
					_alterer,
					((EvolutionParam<?, ?>)obj)._alterer
				) &&
				_populationSize ==
					((EvolutionParam<?, ?>)obj)._populationSize &&
				Double.compare(
					_offspringFraction,
					((EvolutionParam<?, ?>)obj)._offspringFraction) == 0 &&
				_maximalPhenotypeAge ==
					((EvolutionParam<?, ?>)obj)._maximalPhenotypeAge;
	}

	@Override
	public String toString() {
		return
		"Alterer:            " + _alterer + "\n" +
		"Selector:           " + Seq.of(_survivorsSelector, _offspringSelector) + "\n" +
		"Population size:    " + _populationSize + "\n" +
		"Offspring fraction: " + _offspringFraction;
	}

	/**
	 * Return a new evolution parameter object with the given parameter.
	 *
	 * @param survivorsSelector the used survivor {@link Selector} of the GA
	 * @param offspringSelector the used offspring {@link Selector} of the GA
	 * @param alterer the used {@link Alterer} of the GA
	 * @param populationSize the number of individuals of a population
	 * @param offspringFraction the offspring fraction
	 * @param maximalPhenotypeAge the maximal allowed phenotype age
	 * @param <G> the gene type of the problem encoding
	 * @param <C> the fitness function return type of the problem encoding
	 * @return a new evolution parameter object
	 * @throws NullPointerException if one of the reference types is {@code null}
	 * @throws IllegalArgumentException if the population size or the maximal
	 *         phenotype age is smaller than one.
	 * @throws IllegalArgumentException if the offspring fraction is not within
	 *         the range [0..1].
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionParam<G, C> of(
		final Selector<G, C> survivorsSelector,
		final Selector<G, C> offspringSelector,
		final Alterer<G, C> alterer,
		final int populationSize,
		final double offspringFraction,
		final long maximalPhenotypeAge
	) {
		return new EvolutionParam<>(
			survivorsSelector,
			offspringSelector,
			alterer,
			populationSize,
			offspringFraction,
			maximalPhenotypeAge
		);
	}



	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "evolution-param")
	@XmlType(name = "org.jenetics.engine.EvolutionParam")
	@XmlAccessorType(XmlAccessType.FIELD)
	@SuppressWarnings({"unchecked", "rawtypes"})
	static final class Model {

		@XmlElement(name = "survivor-selector", required = true, nillable = false)
		public Object survivorSelector;

		@XmlElement(name = "offspring-selector", required = true, nillable = false)
		public Object offspringSelector;

		@XmlElement(name = "alterer", required = true, nillable = false)
		public Object alterer;

		@XmlElement(name = "population-size", required = true, nillable = false)
		public int populationSize;

		@XmlElement(name = "offspring-fraction", required = true, nillable = false)
		public double offspringFraction;

		@XmlElement(name = "maximal-phenotype-age", required = true, nillable = false)
		public long maximalPhenotypeAge;

		public static final class Adapter
				extends XmlAdapter<Model, EvolutionParam>
		{
			@Override
			public Model marshal(final EvolutionParam ep) throws Exception {
				final Model model = new Model();
				model.survivorSelector = jaxb
					.Marshaller(ep.getSurvivorsSelector())
					.apply(ep.getSurvivorsSelector());
				model.offspringSelector = jaxb
					.Marshaller(ep.getOffspringSelector())
					.apply(ep.getOffspringSelector());
				model.alterer = jaxb
					.Marshaller(ep.getAlterer())
					.apply(ep.getAlterer());
				model.populationSize = ep.getPopulationSize();
				model.offspringFraction = ep.getOffspringFraction();
				model.maximalPhenotypeAge = ep.getMaximalPhenotypeAge();

				return model;
			}

			@Override
			public EvolutionParam unmarshal(final Model model) throws Exception {
				return new EvolutionParam(
					(Selector)jaxb.Unmarshaller(model.survivorSelector)
						.apply(model.survivorSelector),
					(Selector)jaxb.Unmarshaller(model.offspringSelector)
						.apply(model.offspringSelector),
					(Alterer)jaxb.Unmarshaller(model.alterer)
						.apply(model.alterer),
					model.populationSize,
					model.offspringFraction,
					model.maximalPhenotypeAge
				);
			}
		}
	}

}
