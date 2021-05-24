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
package io.jenetics;

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
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import io.jenetics.util.Verifiable;

/**
 * The {@code Phenotype} consists of a {@link Genotype}, the current generation
 * and an optional fitness value. Once the fitness has been evaluated, a new
 * {@code Phenotype} instance, with the calculated fitness, can be created with
 * the {@link #withFitness(Comparable)}.
 *
 * @see Genotype
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @param <G> the gene type
 * @param <C> the fitness result type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 6.0
 */
public final class Phenotype<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements
		Comparable<Phenotype<G, C>>,
		Verifiable,
		Serializable
{
	private static final long serialVersionUID = 6L;

	private final Genotype<G> _genotype;
	private final long _generation;
	private final C _fitness;

	/**
	 * Create a new phenotype from the given arguments.
	 *
	 * @param genotype the genotype of this phenotype.
	 * @param generation the current generation of the generated phenotype.
	 * @param fitness the known fitness of the phenotype, maybe {@code null}
	 * @throws NullPointerException if the genotype is {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         {@code < 0}.
	 */
	private Phenotype(
		final Genotype<G> genotype,
		final long generation,
		final C fitness
	) {
		if (generation < 0) {
			throw new IllegalArgumentException(format(
				"Generation must not < 0 and was %s.", generation
			));
		}

		_genotype = requireNonNull(genotype, "Genotype");
		_generation = generation;
		_fitness = fitness;
	}

	/**
	 * Applies the given fitness function to the underlying genotype and return
	 * a new phenotype with the (newly) evaluated fitness function, if not
	 * already evaluated. If the fitness value is already set {@code this}
	 * phenotype is returned.
	 *
	 * @since 5.0
	 *
	 * @param ff the fitness function
	 * @return a evaluated phenotype or {@code this} if the fitness value is
	 *         already set
	 * @throws NullPointerException if the given fitness function is {@code null}
	 */
	public Phenotype<G, C>
	eval(final Function<? super Genotype<G>, ? extends C> ff) {
		requireNonNull(ff);
		return _fitness == null ? withFitness(ff.apply(_genotype)) : this;
	}

	/**
	 * This method returns a copy of the {@code Genotype}, to guarantee a
	 * immutable class.
	 *
	 * @return the cloned {@code Genotype} of this {@code Phenotype}.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public Genotype<G> genotype() {
		return _genotype;
	}

	/**
	 * A phenotype instance can be created with or without fitness value.
	 * Initially, the phenotype is created without fitness value. The
	 * fitness evaluation strategy is responsible for creating phenotypes with
	 * fitness value assigned.
	 *
	 * @since 4.2
	 *
	 * @see #nonEvaluated()
	 *
	 * @return {@code true} is this phenotype has an fitness value assigned,
	 *         {@code false} otherwise
	 */
	public boolean isEvaluated() {
		return _fitness != null;
	}

	/**
	 * A phenotype instance can be created with or without fitness value.
	 * Initially, the phenotype is created without fitness value. The
	 * fitness evaluation strategy is responsible for creating phenotypes with
	 * fitness value assigned.
	 *
	 * @since 5.0
	 *
	 * @see #isEvaluated()
	 *
	 * @return {@code false} is this phenotype has an fitness value assigned,
	 *         {@code true} otherwise
	 */
	public boolean nonEvaluated() {
		return _fitness == null;
	}

	/**
	 * Return the fitness value of this {@code Phenotype}.
	 *
	 * @see #fitnessOptional()
	 *
	 * @return The fitness value of this {@code Phenotype}.
	 * @throws NoSuchElementException if {@link #isEvaluated()} returns
	 *         {@code false}
	 */
	public C fitness() {
		if (_fitness == null) {
			throw new NoSuchElementException(
				"Phenotype has no assigned fitness value."
			);
		}

		return _fitness;
	}

	/**
	 * Return the fitness value of {@code this} phenotype, or
	 * {@link Optional#empty()} if not evaluated yet.
	 *
	 * @since 5.0
	 *
	 * @see #fitness()
	 *
	 * @return the fitness value
	 */
	public Optional<C> fitnessOptional() {
		return Optional.ofNullable(_fitness);
	}

	/**
	 * Return the generation this {@link Phenotype} was created.
	 *
	 * @see #age(long)
	 *
	 * @return The generation this {@link Phenotype} was created.
	 */
	public long generation() {
		return _generation;
	}

	/**
	 * Return the age of this phenotype depending on the given current generation.
	 *
	 * @see #generation()
	 *
	 * @param currentGeneration the current generation evaluated by the GA.
	 * @return the age of this phenotype:
	 *          {@code currentGeneration - this.getGeneration()}.
	 */
	public long age(final long currentGeneration) {
		return currentGeneration - _generation;
	}

	/**
	 * Return a phenotype, where the fitness is set to {@code null}. If
	 * {@code this} phenotype isn't evaluated, {@code this} instance is returned.
	 *
	 * @since 6.0
	 *
	 * @return a phenotype, where the fitness is set to {@code null}
	 */
	public Phenotype<G, C> nullifyFitness() {
		return _fitness != null ? of(_genotype, _generation) : this;
	}

	/**
	 * Test whether this phenotype is valid. The phenotype is valid if its
	 * {@link Genotype} is valid.
	 *
	 * @return true if this phenotype is valid, false otherwise.
	 */
	@Override
	public boolean isValid() {
		return _genotype.isValid();
	}

	@Override
	public int compareTo(final Phenotype<G, C> pt) {
		if (isEvaluated()) {
			return pt.isEvaluated() ? fitness().compareTo(pt.fitness()) : 1;
		} else {
			return pt.isEvaluated() ? -1 : 0;
		}
	}

	@Override
	public int hashCode() {
		return hash(_generation, hash(_fitness, hash(_genotype)));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Phenotype<?, ?> &&
			_generation == ((Phenotype<?, ?>)obj)._generation &&
			Objects.equals(_fitness, ((Phenotype<?, ?>)obj)._fitness) &&
			Objects.equals(_genotype, ((Phenotype<?, ?>)obj)._genotype);
	}

	@Override
	public String toString() {
		return _genotype + " -> " + _fitness;
	}

	/**
	 * Return a new {@code Phenotype} object with the given <em>raw</em> fitness
	 * value. The returned phenotype is automatically <em>evaluated</em>:
	 * {@code isEvaluated() == true}
	 *
	 * @since 4.2
	 *
	 * @param fitness the phenotypes fitness value
	 * @throws NullPointerException if the given {@code fitness} value is
	 *         {@code null}
	 * @return a new phenotype with the given fitness value
	 */
	public Phenotype<G, C> withFitness(final C fitness) {
		return Phenotype.of(
			_genotype,
			_generation,
			requireNonNull(fitness)
		);
	}

	/**
	 * Return a new {@code Phenotype} object with the given generation.
	 *
	 * @since 5.0
	 *
	 * @param generation the generation of the newly created phenotype
	 * @return a new phenotype with the given generation
	 */
	public Phenotype<G, C> withGeneration(final long generation) {
		return Phenotype.of(
			_genotype,
			generation,
			_fitness
		);
	}


	/* *************************************************************************
	 *  Static factory methods.
	 * ************************************************************************/

	/**
	 * Create a new phenotype from the given arguments. The phenotype is created
	 * with a non assigned fitness function and the call of {@link #isEvaluated()}
	 * will return {@code false}.
	 *
	 * @param <G> the gene type of the chromosome
	 * @param <C> the fitness value type
	 * @param genotype the genotype of this phenotype.
	 * @param generation the current generation of the generated phenotype.
	 * @return a new phenotype object
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         {@code < 0}.
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Phenotype<G, C> of(final Genotype<G> genotype, final long generation) {
		return new Phenotype<>(
			genotype,
			generation,
			null
		);
	}

	/**
	 * Create a new phenotype from the given arguments.
	 *
	 * @param <G> the gene type of the chromosome
	 * @param <C> the fitness value type
	 * @param genotype the genotype of this phenotype.
	 * @param generation the current generation of the generated phenotype.
	 * @param fitness the known fitness of the phenotype.
	 * @return a new phenotype object
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         {@code < 0}.
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Phenotype<G, C> of(
		final Genotype<G> genotype,
		final long generation,
		final C fitness
	) {
		return new Phenotype<>(
			genotype,
			generation,
			requireNonNull(fitness)
		);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.PHENOTYPE, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final ObjectOutput out) throws IOException {
		writeLong(_generation, out);
		out.writeObject(_genotype);
		out.writeObject(_fitness);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	static Object read(final ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		final var generation = readLong(in);
		final var genotype = (Genotype)in.readObject();
		final var fitness = (Comparable)in.readObject();

		return new Phenotype(
			genotype,
			generation,
			fitness
		);
	}

}
