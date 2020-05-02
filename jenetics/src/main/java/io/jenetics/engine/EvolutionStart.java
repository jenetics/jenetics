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
import java.io.Serializable;
import java.util.Objects;

import io.jenetics.Gene;
import io.jenetics.Phenotype;
import io.jenetics.internal.util.Requires;
import io.jenetics.util.ISeq;

/**
 * Represents a state of the GA at the start of an evolution step.
 *
 * @see EvolutionResult
 * @see EvolutionInit
 * @see EvolutionStreamable#stream(EvolutionStart)
 *
 * @param <G> the gene type
 * @param <C> the fitness type
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.1
 * @version 6.0
 */
public final class EvolutionStart<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Serializable
{

	private static final long serialVersionUID = 2L;

	private final ISeq<Phenotype<G, C>> _population;
	private final long _generation;

	private final boolean _dirty;

	EvolutionStart(
		final ISeq<Phenotype<G, C>> population,
		final long generation,
		final boolean dirty
	) {
		_population = requireNonNull(population);
		_generation = Requires.positive(generation);
		_dirty = dirty;
	}

	/**
	 * Return the population before the evolution step.
	 *
	 * @return the start population
	 */
	public ISeq<Phenotype<G, C>> population() {
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

	/**
	 * Indicates whether the population is guaranteed to be evaluated. If this
	 * flag is {@code true}, the population possibly contains unevaluated
	 * individuals.
	 *
	 * @return {@code false}, if it is guaranteed that all individuals has
	 *         already been evaluated, {@code true} otherwise
	 */
	boolean isDirty() {
		return _dirty;
	}

	@Override
	public int hashCode() {
		return hash(_generation, hash(_population, hash(getClass())));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof EvolutionStart &&
			_generation == ((EvolutionStart)obj)._generation &&
			Objects.equals(_population, ((EvolutionStart)obj)._population);
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
		final ISeq<Phenotype<G, C>> population,
		final long generation
	) {
		return new EvolutionStart<>(population, generation, true);
	}

	/**
	 * An empty evolution start object, which can be used as initial evolution
	 * value. The evolution {@link Engine} is then responsible for creating the
	 * proper initial population,
	 *
	 * @since 5.1
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 * @return an empty evolution start object
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionStart<G, C> empty() {
		return new EvolutionStart<>(ISeq.empty(), 1, false);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.EVOLUTION_START, this);
	}

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
		return new EvolutionStart(
			(ISeq)in.readObject(),
			readLong(in),
			true
		);
	}

}
