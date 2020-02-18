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
import static io.jenetics.internal.util.Hashes.hash;
import static io.jenetics.internal.util.SerialIO.readLong;
import static io.jenetics.internal.util.SerialIO.writeLong;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.util.Objects;

import io.jenetics.Gene;
import io.jenetics.Phenotype;
import io.jenetics.internal.util.Requires;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.1
 * @version !__version__!
 */
final class DefaultEvolutionStart<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements EvolutionStart<G, C>
{
	private static final long serialVersionUID = 2L;

	private final ISeq<Phenotype<G, C>> _population;
	private final long _generation;

	DefaultEvolutionStart(
		final Iterable<Phenotype<G, C>> population,
		final long generation
	) {
		_population = ISeq.of(population);
		_generation = Requires.positive(generation);
	}

	@Override
	public ISeq<Phenotype<G, C>> population() {
		return _population;
	}

	@Override
	public long generation() {
		return _generation;
	}

	@Override
	public int hashCode() {
		return hash(_generation, hash(_population, hash(getClass())));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof DefaultEvolutionStart &&
			_generation == ((DefaultEvolutionStart)obj)._generation &&
			Objects.equals(_population, ((DefaultEvolutionStart)obj)._population);
	}

	@Override
	public String toString() {
		return format(
			"EvolutionStart[population-size=%d, generation=%d]",
			_population.size(), _generation
		);
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
		return new DefaultEvolutionStart(
			(ISeq)in.readObject(),
			readLong(in)
		);
	}
}
