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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

import io.jenetics.engine.GenotypeEvaluator;
import io.jenetics.internal.util.Lazy;
import io.jenetics.internal.util.reflect;
import io.jenetics.util.Verifiable;

/**
 * The {@code Phenotype} consists of a {@link Genotype} plus a fitness
 * {@link Function}, where the fitness {@link Function} represents the
 * environment where the {@link Genotype} lives.
 * This class implements the {@link Comparable} interface, to define a natural
 * order between two {@code Phenotype}s. The natural order of the
 * {@code Phenotypes} is defined by its fitness value (given by the
 * fitness {@link Function}. The {@code Phenotype} is immutable and therefore
 * can't be changed after creation.
 * <p>
 * The evaluation of the fitness function is performed lazily. Either by calling
 * one of the fitness accessors ({@link #getFitness()} or {@link #getRawFitness()})
 * of through the <i>evaluation</i> methods {@link #run()} or {@link #evaluate()}.
 * Since the {@code Phenotype} implements the {@link Runnable} interface, it is
 * easily possible to perform the fitness function evaluation concurrently, by
 * putting it into an {@link java.util.concurrent.ExecutorService}.
 *
 * @see Genotype
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 4.0
 */
public final class Phenotype<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements
		Comparable<Phenotype<G, C>>,
		Verifiable,
		Serializable,
		Runnable
{
	private static final long serialVersionUID = 5L;

	private final transient Function<? super Genotype<G>, ? extends C> _function;
	private final transient Function<? super C, ? extends C> _scaler;

	private final Genotype<G> _genotype;
	private final long _generation;

	private final Lazy<C> _rawFitness;
	private final Lazy<C> _fitness;

	/**
	 * Create a new phenotype from the given arguments.
	 *
	 * @param genotype the genotype of this phenotype.
	 * @param generation the current generation of the generated phenotype.
	 * @param function the fitness function of this phenotype.
	 * @param scaler the fitness scaler.
	 * @param rawFitness the known raw-fitness of the phenotype, maybe {@code null}
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         {@code < 0}.
	 */
	private Phenotype(
		final Genotype<G> genotype,
		final long generation,
		final Function<? super Genotype<G>, ? extends C> function,
		final Function<? super C, ? extends C> scaler,
		final C rawFitness
	) {
		_genotype = requireNonNull(genotype, "Genotype");
		_function = requireNonNull(function, "Fitness function");
		_scaler = requireNonNull(scaler, "Fitness scaler");
		if (generation < 0) {
			throw new IllegalArgumentException(format(
				"Generation must not < 0 and was %s.", generation
			));
		}
		_generation = generation;

		if (rawFitness != null) {
			_rawFitness = Lazy.ofValue(rawFitness);
			_fitness = Lazy.ofValue(scaler.apply(rawFitness));
		} else {
			_rawFitness = Lazy.of(() -> _function.apply(_genotype));
			_fitness = Lazy.of(() -> _scaler.apply(_rawFitness.get()));
		}
	}

	/**
	 * This method returns a copy of the {@code Genotype}, to guarantee a
	 * immutable class.
	 *
	 * @return the cloned {@code Genotype} of this {@code Phenotype}.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public Genotype<G> getGenotype() {
		return _genotype;
	}

	/**
	 * Evaluates the (raw) fitness values and caches it so the fitness calculation
	 * is performed only once.
	 *
	 * @return this phenotype, for method chaining.
	 */
	public Phenotype<G, C> evaluate() {
		getFitness();
		return this;
	}

	/**
	 * The fitness value of the <em>phenotype</em> is evaluated lazily. This
	 * method allows to check whether the fitness value has already been
	 * calculated or not.
	 *
	 * @return {@code true} is this phenotype has been evaluated, {@code false}
	 *         otherwise
	 */
	public boolean isEvaluated() {
		return _fitness.isEvaluated();
	}

	/**
	 * This method simply calls the {@link #evaluate()} method. The purpose of
	 * this method is to have a simple way for concurrent fitness calculation
	 * for expensive fitness values.
	 */
	@Override
	public void run() {
		evaluate();
	}

	/**
	 * Return the fitness function used by this phenotype to calculate the
	 * (raw) fitness value.
	 *
	 * @return the fitness function.
	 */
	public Function<? super Genotype<G>, ? extends C> getFitnessFunction() {
		return _function;
	}

	/**
	 * Return the fitness scaler used by this phenotype to scale the <i>raw</i>
	 * fitness.
	 *
	 * @return the fitness scaler.
	 */
	public Function<? super C, ? extends C> getFitnessScaler() {
		return _scaler;
	}

	/**
	 * Return the fitness value of this {@code Phenotype}.
	 *
	 * @return The fitness value of this {@code Phenotype}.
	 */
	public C getFitness() {
		return _fitness.get();
	}

	/**
	 * Return the raw fitness (before scaling) of the phenotype.
	 *
	 * @return The raw fitness (before scaling) of the phenotype.
	 */
	public C getRawFitness() {
		return _rawFitness.get();
	}

	/**
	 * Return the generation this {@link Phenotype} was created.
	 *
	 * @return The generation this {@link Phenotype} was created.
	 */
	public long getGeneration() {
		return _generation;
	}

	/**
	 * Return the age of this phenotype depending on the given current generation.
	 *
	 * @param currentGeneration the current generation evaluated by the GA.
	 * @return the age of this phenotype:
	 *          {@code currentGeneration - this.getGeneration()}.
	 */
	public long getAge(final long currentGeneration) {
		return currentGeneration - _generation;
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
		return getFitness().compareTo(pt.getFitness());
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash += 31*_generation + 37;
		hash += 31*Objects.hashCode(getFitness()) + 37;
		hash += 31*Objects.hashCode(getRawFitness()) + 37;
		hash += 31*_genotype.hashCode() + 37;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Phenotype<?, ?> &&
			Objects.equals(getFitness(), ((Phenotype<?, ?>) obj).getFitness()) &&
			Objects.equals(getRawFitness(), ((Phenotype<?, ?>)obj).getRawFitness()) &&
			Objects.equals(_genotype, ((Phenotype<?, ?>)obj)._genotype) &&
			_generation == ((Phenotype<?, ?>)obj)._generation;
	}

	@Override
	public String toString() {
		return _genotype + " --> " + getFitness();
	}

	/**
	 * Return a new {@code Phenotype} object with the given <em>raw</em> fitness
	 * value. The returned phenotype is automatically <em>evaluated</em>:
	 * {@code isEvaluated() == true}
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
			_function,
			_scaler,
			fitness
		);
	}

	/**
	 * Create a new {@code Phenotype} with a different {@code Genotype} but the
	 * same {@code generation}, fitness {@code function} and fitness
	 * {@code scaler}.
	 *
	 * @since 3.1
	 *
	 * @param genotype the new genotype
	 * @return a new {@code phenotype} with replaced {@code genotype}
	 * @throws NullPointerException if the given {@code genotype} is {@code null}.
	 */
	public Phenotype<G, C> newInstance(final Genotype<G> genotype) {
		return of(genotype, _generation, _function, _scaler);
	}

	/**
	 * Factory method for creating a new {@link Phenotype} with the same
	 * {@link Function} and age as this {@link Phenotype}.
	 *
	 * @since 3.5
	 *
	 * @param genotype the new genotype of the new phenotype.
	 * @param generation date of birth (generation) of the new phenotype.
	 * @return New {@link Phenotype} with the same fitness {@link Function}.
	 * @throws NullPointerException if the {@code genotype} is {@code null}.
	 */
	public Phenotype<G, C> newInstance(
		final Genotype<G> genotype,
		final long generation
	) {
		return of(genotype, generation, _function, _scaler);
	}

	/**
	 * Return a new phenotype with the the genotype of this and with new
	 * fitness function, fitness scaler and generation.
	 *
	 * @param generation the generation of the new phenotype.
	 * @param function the (new) fitness scaler of the created phenotype.
	 * @param scaler the (new) fitness scaler of the created phenotype
	 * @return a new phenotype with the given values.
	 * @throws NullPointerException if one of the values is {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         {@code < 0}.
	 */
	public Phenotype<G, C> newInstance(
		final long generation,
		final Function<? super Genotype<G>, ? extends C> function,
		final Function<? super C, ? extends C> scaler
	) {
		return of(_genotype, generation, function, scaler);
	}

	/**
	 * Return a new phenotype with the the genotype of this and with new
	 * fitness function and generation.
	 *
	 * @param generation the generation of the new phenotype.
	 * @param function the (new) fitness scaler of the created phenotype.
	 * @return a new phenotype with the given values.
	 * @throws NullPointerException if one of the values is {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         {@code < 0}.
	 */
	public Phenotype<G, C> newInstance(
		final long generation,
		final Function<? super Genotype<G>, ? extends C> function
	) {
		return of(_genotype, generation, function, a -> a);
	}

	/**
	 * The {@code Genotype} is copied to guarantee an immutable class. Only
	 * the age of the {@code Phenotype} can be incremented.
	 *
	 * @param <G> the gene type of the chromosome
	 * @param <C> the fitness value type
	 * @param genotype the genotype of this phenotype.
	 * @param generation the current generation of the generated phenotype.
	 * @param function the fitness function of this phenotype.
	 * @return a new phenotype from the given parameters
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         {@code < 0}.
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Phenotype<G, C> of(
		final Genotype<G> genotype,
		final long generation,
		final Function<? super Genotype<G>, C> function
	) {
		return Phenotype.<G, C>of(
			genotype,
			generation,
			function,
			function instanceof Serializable
				? (Function<? super C, ? extends C> & Serializable)a -> a
				: a -> a
		);
	}

	/**
	 * Create a new phenotype from the given arguments.
	 *
	 * @param <G> the gene type of the chromosome
	 * @param <C> the fitness value type
	 * @param genotype the genotype of this phenotype.
	 * @param generation the current generation of the generated phenotype.
	 * @param function the fitness function of this phenotype.
	 * @param scaler the fitness scaler.
	 * @return a new phenotype object
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         {@code < 0}.
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Phenotype<G, C> of(
		final Genotype<G> genotype,
		final long generation,
		final Function<? super Genotype<G>, ? extends C> function,
		final Function<? super C, ? extends C> scaler
	) {
		return new Phenotype<>(
			genotype,
			generation,
			function,
			scaler,
			null
		);
	}

	/**
	 * Create a new phenotype from the given arguments. This factory method is
	 * used when the fitness value of the phenotype has been calculated by a
	 * different {@link GenotypeEvaluator} strategy then the default
	 * one.
	 *
	 * @param <G> the gene type of the chromosome
	 * @param <C> the fitness value type
	 * @param genotype the genotype of this phenotype.
	 * @param generation the current generation of the generated phenotype.
	 * @param function the fitness function of this phenotype.
	 * @param scaler the fitness scaler.
	 * @param rawFitness the known raw-fitness of the phenotype.
	 * @return a new phenotype object
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         {@code < 0}.
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Phenotype<G, C> of(
		final Genotype<G> genotype,
		final long generation,
		final Function<? super Genotype<G>, ? extends C> function,
		final Function<? super C, ? extends C> scaler,
		final C rawFitness
	) {
		return new Phenotype<>(
			genotype,
			generation,
			function,
			scaler,
			requireNonNull(rawFitness)
		);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private void writeObject(final ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();
		out.writeLong(getGeneration());
		out.writeObject(getGenotype());
		out.writeObject(getFitness());
		out.writeObject(getRawFitness());
	}

	@SuppressWarnings("unchecked")
	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		reflect.setField(this, "_generation", in.readLong());
		reflect.setField(this, "_genotype", in.readObject());
		reflect.setField(this, "_fitness", Lazy.ofValue(in.readObject()));
		reflect.setField(this, "_rawFitness", Lazy.ofValue(in.readObject()));

		reflect.setField(this, "_function", Function.identity());
		reflect.setField(this, "_scaler", Function.identity());
	}

}
