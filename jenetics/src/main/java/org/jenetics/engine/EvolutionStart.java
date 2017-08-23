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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.internal.util.Hash;
import org.jenetics.internal.util.jaxb;
import java.util.Objects;

import org.jenetics.internal.util.require;

import org.jenetics.Gene;
import org.jenetics.Population;

/**
 * Represents a state of the GA at the start of an evolution step.
 *
 * @see EvolutionResult
 *
 * @param <G> the gene type
 * @param <C> the fitness type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.1
 * @version !__version__!
 */
@XmlJavaTypeAdapter(EvolutionStart.Model.Adapter.class)
public final class EvolutionStart<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final Population<G, C> _population;
	private final long _generation;

	private EvolutionStart(
		final Population<G, C> population,
		final long generation
	) {
		_population = requireNonNull(population);
		_generation = require.positive(generation);
	}

	/**
	 * Return the population before the evolution step.
	 *
	 * @return the start population
	 */
	public Population<G, C> getPopulation() {
		return _population;
	}

	/**
	 * Return the generation of the start population.
	 *
	 * @return the start generation
	 */
	public long getGeneration() {
		return _generation;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash += 31*_generation + 17;
		hash += 31*Objects.hashCode(_population) + 17;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof EvolutionStart<?, ?> &&
			_generation == ((EvolutionStart<?, ?>)obj)._generation &&
			Objects.equals(_population, ((EvolutionStart<?, ?>)obj)._population);
	}

	@Override
	public String toString() {
		return format(
			"EvolutionStart[population-size=%d, generation=%d]",
			_population.size(), _generation
		);
	}

	/**
	 * Create a new evolution start object with the given population and for the
	 * given generation.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 * @param population the start population.
	 * @param generation the start generation of the population
	 * @return a new evolution start object
	 * @throws java.lang.NullPointerException if the given {@code population} is
	 *         {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         smaller then one
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionStart<G, C> of(
		final Population<G, C> population,
		final long generation
	) {
		return new EvolutionStart<>(population, generation);
	}


	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "evolution-start")
	@XmlType(name = "org.jenetics.engine.EvolutionStart")
	@XmlAccessorType(XmlAccessType.FIELD)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	final static class Model {

		@XmlAttribute(name = "generation", required = true)
		public long generation;

		@XmlElement(name = "population", required = true, nillable = false)
		public Object population;

		public final static class Adapter
			extends XmlAdapter<Model, EvolutionStart>
		{
			@Override
			public Model marshal(final EvolutionStart start) throws Exception {
				final Model m = new Model();
				m.generation = start.getGeneration();
				m.population = jaxb.marshal(start.getPopulation());
				return m;
			}

			@Override
			public EvolutionStart unmarshal(final Model m) throws Exception {
				return EvolutionStart.of(
					(Population)jaxb.unmarshal(m.population),
					m.generation
				);
			}
		}
	}

}
