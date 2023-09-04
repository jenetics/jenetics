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
import static io.jenetics.internal.util.Hashes.hash;
import static io.jenetics.internal.util.SerialIO.readLong;
import static io.jenetics.internal.util.SerialIO.writeLong;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.internal.util.Requires;
import io.jenetics.util.ISeq;

/**
 * Represents the initialization value of an evolution stream/iterator.
 *
 * @see EvolutionStart
 * @see EvolutionStreamable#stream(EvolutionInit)
 *
 * @param <G> the gene type
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 4.1
 */
public final class EvolutionInit<G extends Gene<?, G>>
	implements Serializable
{

	@Serial
	private static final long serialVersionUID = 1L;

	private final ISeq<Genotype<G>> _population;
	private final long _generation;

	private EvolutionInit(
		final ISeq<Genotype<G>> population,
		final long generation
	) {
		_population = requireNonNull(population);
		_generation = Requires.positive(generation);
	}

	/**
	 * Return the initial population.
	 *
	 * @return the initial population
	 */
	public ISeq<Genotype<G>> population() {
		return _population;
	}

	/**
	 * Return the generation of the start population.
	 *
	 * @return the start generation
	 */
	public long generation() {
		return _generation;
	}

	@Override
	public int hashCode() {
		return hash(_generation, hash(_population));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof EvolutionInit<?> other &&
			_generation == other._generation &&
			Objects.equals(_population, other._population);
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
	 * @param population the start population.
	 * @param generation the start generation of the population
	 * @return a new evolution start object
	 * @throws java.lang.NullPointerException if the given {@code population} is
	 *         {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         smaller then one
	 */
	public static <G extends Gene<?, G>>
	EvolutionInit<G> of(
		final ISeq<Genotype<G>> population,
		final long generation
	) {
		return new EvolutionInit<>(population, generation);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.EVOLUTION_INIT, this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final ObjectOutput out) throws IOException {
		out.writeObject(_population);
		writeLong(_generation, out);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	static Object read(final ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		return new EvolutionInit(
			(ISeq)in.readObject(),
			readLong(in)
		);
	}

}
