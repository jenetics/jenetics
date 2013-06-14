/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import static java.util.Objects.requireNonNull;
import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;

import javolution.context.ObjectFactory;
import javolution.lang.Immutable;
import javolution.lang.Realtime;
import javolution.text.Text;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.Function;
import org.jenetics.util.Verifiable;
import org.jenetics.util.functions;


/**
 * The <code>Phenotype</code> consists of a {@link Genotype} plus a
 * fitness {@link Function}, where the fitness {@link Function} represents the
 * environment where the {@link Genotype} lives.
 * This class implements the {@link Comparable} interface, to define a natural
 * order between two <code>Phenotype</code>s. The natural order of the
 * <code>Phenotypes</code> is defined by its fitness value (given by the
 * fitness {@link Function}.
 * The <code>Phenotype</code> is immutable and therefore can't be changed after
 * creation.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-06-11 $</em>
 */
public final class Phenotype<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements
		Comparable<Phenotype<G, C>>,
		Immutable,
		Verifiable,
		XMLSerializable,
		Realtime,
		Runnable
{
	private static final long serialVersionUID = 1L;

	private Genotype<G> _genotype;
	private Function<Genotype<G>, C> _fitnessFunction;
	private Function<C, C> _fitnessScaler;

	private int _generation = 0;

	//Storing the fitness value for lazy evaluation.
	private C _rawFitness = null;
	private C _fitness = null;

	private Phenotype() {
	}

	/**
	 * This method returns a copy of the <code>Genotype</code>, to guarantee a
	 * immutable class.
	 *
	 * @return the cloned <code>Genotype</code> of this <code>Phenotype</code>.
	 * @throws NullPointerException if one of the arguments is <code>null</code>.
	 */
	public Genotype<G> getGenotype() {
		return _genotype;
	}

	/**
	 * Evaluates the (raw) fitness values and caches it so the fitness calculation
	 * is performed only once.
	 */
	public void evaluate() {
		if (_rawFitness == null) {
			_rawFitness = _fitnessFunction.apply(_genotype);
			_fitness = _fitnessScaler.apply(_rawFitness);
		}
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
	public Function<Genotype<G>, C> getFitnessFunction() {
		return _fitnessFunction;
	}

	/**
	 * Return the fitness scaler used by this phenotype to scale the <i>raw</i>
	 * fitness.
	 *
	 * @return the fitness scaler.
	 */
	public Function<C, C> getFitnessScaler() {
		return _fitnessScaler;
	}

	/**
	 * Return the fitness value of this <code>Phenotype</code>.
	 *
	 * @return The fitness value of this <code>Phenotype</code>.
	 */
	public C getFitness() {
		evaluate();
		return _fitness;
	}

	/**
	 * Return the raw fitness (before scaling) of the phenotype.
	 *
	 * @return The raw fitness (before scaling) of the phenotype.
	 */
	public C getRawFitness() {
		evaluate();
		return _rawFitness;
	}

	/**
	 * Return the generation this {@link Phenotype} was created.
	 *
	 * @return The generation this {@link Phenotype} was created.
	 */
	public int getGeneration() {
		return _generation;
	}

	/**
	 * Return the age of this phenotype depending on the given current generation.
	 *
	 * @param currentGeneration the current generation evaluated by the GA.
	 * @return the age of this phenotype:
	 *          {@code currentGeneration - this.getGeneration()}.
	 */
	public int getAge(final int currentGeneration) {
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
		return hashCodeOf(getClass()).
				and(_generation).
				and(getFitness()).
				and(getRawFitness()).
				and(_genotype).value();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Phenotype<?, ?>)) {
			return false;
		}

		final Phenotype<?, ?> pt = (Phenotype<?, ?>)obj;
		return eq(getFitness(), pt.getFitness()) &&
				eq(getRawFitness(), pt.getRawFitness()) &&
				eq(_genotype, pt._genotype) &&
				eq(_generation, pt._generation);
	}

	@Override
	public Text toText() {
		return _genotype.toText();
	}

	@Override
	public String toString() {
		return toText().toString() + " --> " + getFitness();
	}

	@SuppressWarnings("rawtypes")
	private static final ObjectFactory FACTORY = new ObjectFactory() {
		@Override protected Object create() {
			return new Phenotype();
		}
	};

	/**
	 * Factory method for creating a new {@link Phenotype} with the same
	 * {@link FitnessFunction} and age as this {@link Phenotype}.
	 *
	 * @param genotype the new genotype of the new phenotype.
	 * @param generation date of birth (generation) of the new phenotype.
	 * @return New {@link Phenotype} with the same fitness {@link Function}.
	 * @throws NullPointerException if the {@code genotype} is {@code null}.
	 */
	Phenotype<G, C> newInstance(final Genotype<G> genotype, final int generation) {
		requireNonNull(genotype, "Genotype");
		return Phenotype.valueOf(
			genotype, _fitnessFunction, _fitnessScaler, generation
		);
	}

	/**
	 * Return a new phenotype with the the genotype of this and with new
	 * fitness function, fitness scaler and generation.
	 *
	 * @param function the (new) fitness scaler of the created phenotype.
	 * @param scaler the (new) fitness scaler of the created phenotype
	 * @param generation the generation of the new phenotype.
	 * @return a new phenotype with the given values.
	 * @throws NullPointerException if one of the values is {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is < 0.
	 */
	public Phenotype<G, C> newInstance(
		final Function<Genotype<G>, C> function,
		final Function<C, C> scaler,
		final int generation
	) {
		return valueOf(_genotype, function, scaler, generation);
	}

	/**
	 * Return a new phenotype with the the genotype of this and with new
	 * fitness function and generation.
	 *
	 * @param function the (new) fitness scaler of the created phenotype.
	 * @param generation the generation of the new phenotype.
	 * @return a new phenotype with the given values.
	 * @throws NullPointerException if one of the values is {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is < 0.
	 */
	public Phenotype<G, C> newInstance(
		final Function<Genotype<G>, C> function,
		final int generation
	) {
		return valueOf(_genotype, function, functions.<C>Identity(), generation);
	}


	/* *************************************************************************
	 *  Property access methods
	 * ************************************************************************/

	/**
	 * Create a {@link Function} which return the phenotype age when calling
	 * {@code converter.convert(phenotype)}.
	 *
	 * @param currentGeneration the current generation.
	 * @return an age {@link Function}.
	 */
	public static Function<Phenotype<?, ?>, Integer>
	Age(final int currentGeneration)
	{
		return new Function<Phenotype<?, ?>, Integer>() {
			@Override public Integer apply(final Phenotype<?, ?> value) {
				return value.getAge(currentGeneration);
			}
		};
	}

	/**
	 * Create a {@link Function} which return the phenotype generation when
	 * calling {@code converter.convert(phenotype)}.
	 *
	 * @return a generation {@link Function}.
	 */
	public static Function<Phenotype<?, ?>, Integer> Generation() {
		return new Function<Phenotype<?, ?>, Integer>() {
			@Override public Integer apply(final Phenotype<?, ?> value) {
				return value.getGeneration();
			}
		};
	}

	/**
	 * Create a {@link Function} which return the phenotype fitness when
	 * calling {@code converter.convert(phenotype)}.
	 *
	 * @param <C> the fitness value type.
	 * @return a fitness {@link Function}.
	 */
	public static <C extends Comparable<? super C>>
	Function<Phenotype<?, C>, C> Fitness()
	{
		return new Function<Phenotype<?, C>, C>() {
			@Override public C apply(final Phenotype<?, C> value) {
				return value.getFitness();
			}
		};
	}

	/**
	 * Create a {@link Function} which return the phenotype raw fitness when
	 * calling {@code converter.convert(phenotype)}.
	 *
	 * @param <C> the fitness value type.
	 * @return a raw fitness {@link Function}.
	 */
	public static <C extends Comparable<? super C>>
	Function<Phenotype<?, C>, C> RawFitnees()
	{
		return new Function<Phenotype<?, C>, C>() {
			@Override public C apply(final Phenotype<?, C> value) {
				return value.getRawFitness();
			}
		};
	}

	/**
	 * Create a {@link Function} which return the phenotype genotype when
	 * calling {@code converter.convert(phenotype)}.
	 *
	 * @param <G> the gene type.
	 * @return a genotype {@link Function}.
	 */
	public static <G extends Gene<?, G>>
	Function<Phenotype<G, ?>, Genotype<G>> Genotype()
	{
		return new Function<Phenotype<G, ?>, Genotype<G>>() {
			@Override public Genotype<G> apply(final Phenotype<G, ?> value) {
				return value.getGenotype();
			}
		};
	}

	/**
	 * The <code>Genotype</code> is copied to guarantee an immutable class. Only
	 * the age of the <code>Phenotype</code> can be incremented.
	 *
	 * @param genotype the genotype of this phenotype.
	 * @param fitnessFunction the fitness function of this phenotype.
	 * @param generation the current generation of the generated phenotype.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is < 0.
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Phenotype<G, C> valueOf(
		final Genotype<G> genotype,
		final Function<Genotype<G>, C> fitnessFunction,
		final int generation
	) {
		return valueOf(genotype, fitnessFunction, functions.<C>Identity(), generation);
	}

	/**
	 * The <code>Genotype</code> is copied to guarantee an immutable class. Only
	 * the age of the <code>Phenotype</code> can be incremented.
	 *
	 * @param genotype the genotype of this phenotype.
	 * @param fitnessFunction the fitness function of this phenotype.
	 * @param fitnessScaler the fitness scaler.
	 * @param generation the current generation of the generated phenotype.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is < 0.
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Phenotype<G, C> valueOf(
		final Genotype<G> genotype,
		final Function<Genotype<G>, C> fitnessFunction,
		final Function<C, C> fitnessScaler,
		final int generation
	) {
		requireNonNull(genotype, "Genotype");
		requireNonNull(fitnessFunction, "Fitness function");
		requireNonNull(fitnessScaler, "Fitness scaler");
		if (generation < 0) {
			throw new IllegalArgumentException(
				"Generation must not < 0: " + generation
			);
		}

		@SuppressWarnings("unchecked")
		final Phenotype<G, C> pt = (Phenotype<G, C>)FACTORY.object();
		pt._genotype = genotype;
		pt._fitnessFunction = fitnessFunction;
		pt._fitnessScaler = fitnessScaler;
		pt._generation = generation;

		pt._rawFitness = null;
		pt._fitness = null;
		return pt;
	}

	/* *************************************************************************
	 *  XML object serialization
	 * ************************************************************************/

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static final XMLFormat<Phenotype>
	XML = new XMLFormat<Phenotype>(Phenotype.class)
	{
		private static final String GENERATION = "generation";
		private static final String FITNESS = "fitness";
		private static final String RAW_FITNESS = "raw-fitness";

		@Override
		public Phenotype newInstance(
			final Class<Phenotype> cls, final InputElement xml
		)
			throws XMLStreamException
		{
			final Phenotype pt = (Phenotype)FACTORY.object();
			pt._generation = xml.getAttribute(GENERATION, 0);
			pt._genotype = xml.getNext();
			pt._fitness = xml.get(FITNESS);
			pt._rawFitness = xml.get(RAW_FITNESS);
			return pt;
		}
		@Override
		public void write(final Phenotype pt, final OutputElement xml)
			throws XMLStreamException
		{
			xml.setAttribute(GENERATION, pt._generation);
			xml.add(pt._genotype);
			xml.add(pt.getFitness(), FITNESS);
			xml.add(pt.getRawFitness(), RAW_FITNESS);
		}
		@Override
		public void read(final InputElement xml, final Phenotype gt) {
		}
	};

}




