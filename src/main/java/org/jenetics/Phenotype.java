/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *     
 */
package org.jenetics;

import static org.jenetics.util.Validator.nonNull;

import org.jenetics.util.Converter;
import org.jenetics.util.Verifiable;

import javolution.context.ObjectFactory;
import javolution.lang.Immutable;
import javolution.lang.Realtime;
import javolution.text.Text;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;


/**
 * The <code>Phenotype</code> consists of a {@link Genotype} plus a 
 * {@link FitnessFunction}, where the  {@link FitnessFunction} represents the
 * environment where the {@link Genotype} lives. 
 * This class implements the {@link Comparable} interface, to define a natural 
 * order between two <code>Phenotype</code>s. The natural order of the 
 * <code>Phenotypes</code> is defined by its fitness value (given by the 
 * {@link FitnessFunction}.
 * The <code>Phenotype</code> is immutable and therefore can't be changed after 
 * creation.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Phenotype.java 392 2010-03-04 08:11:54Z fwilhelm $
 */
public class Phenotype<G extends Gene<?, G>, C extends Comparable<C>> 
	implements Comparable<Phenotype<G, C>>, Immutable, Verifiable, 
				XMLSerializable, Realtime, Runnable
{
	private static final long serialVersionUID = 1614815678599076552L;
	
	private Genotype<G> _genotype;
	private FitnessFunction<G, C> _fitnessFunction;
	private FitnessScaler<C> _fitnessScaler;
	
	private int _generation = 0;
	
	//Storing the fitness value and a fitnessCalculated flag
	//for lazy evaluation.
	private C _rawFitness = null;
	private C _fitness = null;
	
	protected Phenotype() {
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
			_rawFitness = _fitnessFunction.evaluate(_genotype);
			_fitness = _fitnessScaler.scale(_rawFitness);
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
	 *         {@code currentGeneration - this.getGeneration()}.
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
		nonNull(pt, "Phenotype");
		return getFitness().compareTo(pt.getFitness());
	}	
	
	@Override
	public int hashCode() {
		int hash = 31;
		hash += (_fitness != null ? _fitness.hashCode() : 0)*17 + 31;
		hash += (_rawFitness != null ? _rawFitness.hashCode() : 0)*17 + 31;
		hash += (_genotype != null ? _genotype.hashCode() : 0)*17 + 31;
		hash += _generation*17 + 31;
		return hash;
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
		return
			(_fitness != null ? _fitness.equals(pt._fitness) : pt._fitness == null) &&
			(_rawFitness != null ? _rawFitness.equals(pt._rawFitness) : pt._rawFitness == null) &&
			(_genotype != null ? _genotype.equals(pt._genotype) : pt._genotype == null) &&
			_generation == pt._generation;
	}

	@Override
	public Text toText() {
		return _genotype.toText();
	}

	@Override
	public String toString() {
		return toText().toString() + " --> " + _fitness;
	}
	
	@SuppressWarnings("unchecked")
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
	 * @return New {@link Phenotype} with the same {@link FitnessFunction}.
	 * @throws NullPointerException if the {@code genotype} is {@code null}.
	 */
	Phenotype<G, C> newInstance(final Genotype<G> genotype, final int generation) {
		nonNull(genotype, "Genotype");
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
			final FitnessFunction<G, C> function, 
			final FitnessScaler<C> scaler, 
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
			final FitnessFunction<G, C> function, 
			final int generation
	) {
		return valueOf(_genotype, function, IdentityScaler.<C>valueOf(), generation);
	}
	
	/**
	 * Create a {@link Converter} which return the phenotype age when calling
	 * {@code converter.convert(phenotype)}.
	 * 
	 * @param <SG> the gene type.
	 * @param <SC> the fitness value type.
	 * @param currentGeneration the current generation.
	 * @return an age {@link Converter}.
	 */
	public static <SG extends Gene<?, SG>, SC extends Comparable<SC>>
	Converter<Phenotype<SG, SC>, Integer> age(final int currentGeneration) 
	{
		return new Converter<Phenotype<SG, SC>, Integer>() {
			@Override public Integer convert(final Phenotype<SG, SC> value) {
				return value.getAge(currentGeneration);
			}
		};
	}
	
	/**
	 * Create a {@link Converter} which return the phenotype generation when 
	 * calling {@code converter.convert(phenotype)}.
	 * 
	 * @param <SG> the gene type.
	 * @param <SC> the fitness value type.
	 * @return a generation {@link Converter}.
	 */
	public static <SG extends Gene<?, SG>, SC extends Comparable<SC>>
	Converter<Phenotype<SG, SC>, Integer> generation() 
	{
		return new Converter<Phenotype<SG, SC>, Integer>() {
			@Override public Integer convert(final Phenotype<SG, SC> value) {
				return value.getGeneration();
			}
		};
	}
	
	/**
	 * Create a {@link Converter} which return the phenotype fitness when 
	 * calling {@code converter.convert(phenotype)}.
	 * 
	 * @param <SG> the gene type.
	 * @param <SC> the fitness value type.
	 * @return a fitness {@link Converter}.
	 */
	public static <SG extends Gene<?, SG>, SC extends Comparable<SC>>
	Converter<Phenotype<SG, SC>, SC> fitness() 
	{
		return new Converter<Phenotype<SG, SC>, SC>() {
			@Override public SC convert(final Phenotype<SG, SC> value) {
				return value.getFitness();
			}
		};
	}
	
	/**
	 * Create a {@link Converter} which return the phenotype raw fitness when 
	 * calling {@code converter.convert(phenotype)}.
	 * 
	 * @param <SG> the gene type.
	 * @param <SC> the fitness value type.
	 * @return a raw fitness {@link Converter}.
	 */
	public static <SG extends Gene<?, SG>, SC extends Comparable<SC>>
	Converter<Phenotype<SG, SC>, SC> rawFitnees() 
	{
		return new Converter<Phenotype<SG, SC>, SC>() {
			@Override public SC convert(final Phenotype<SG, SC> value) {
				return value.getRawFitness();
			}
		};
	}
	
	/**
	 * Create a {@link Converter} which return the phenotype genotype when 
	 * calling {@code converter.convert(phenotype)}.
	 * 
	 * @param <SG> the gene type.
	 * @param <SC> the fitness value type.
	 * @return a genotype {@link Converter}.
	 */
	public static <SG extends Gene<?, SG>, SC extends Comparable<SC>>
	Converter<Phenotype<SG, SC>, Genotype<SG>> genotype() 
	{
		return new Converter<Phenotype<SG, SC>, Genotype<SG>>() {
			@Override public Genotype<SG> convert(final Phenotype<SG, SC> value) {
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
	public static <SG extends Gene<?, SG>, SC extends Comparable<SC>> 
	Phenotype<SG, SC> valueOf(
		final Genotype<SG> genotype, 
		final FitnessFunction<SG, SC> fitnessFunction,
		final int generation
	) {
		return valueOf(genotype, fitnessFunction, new IdentityScaler<SC>(), generation);
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
	public static <SG extends Gene<?, SG>, SC extends Comparable<SC>> 
	Phenotype<SG, SC> valueOf(
		final Genotype<SG> genotype, 
		final FitnessFunction<SG, SC> fitnessFunction, 
		final FitnessScaler<SC> fitnessScaler,
		final int generation
	) {
		nonNull(genotype, "Genotype");
		nonNull(fitnessFunction, "Fitness function");
		nonNull(fitnessScaler, "Fitness scaler");
		if (generation < 0) {
			throw new IllegalArgumentException("Generation must not < 0: " + generation);
		}
		
		@SuppressWarnings("unchecked")
		Phenotype<SG, SC> pt = (Phenotype<SG, SC>)FACTORY.object();
		pt._genotype = Genotype.valueOf(genotype);
		pt._fitnessFunction = fitnessFunction;
		pt._fitnessScaler = fitnessScaler;
		pt._generation = generation;
		
		pt._rawFitness = null;
		pt._fitness = null;
		return pt;
	}

	@SuppressWarnings({ "unchecked" })
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
	
	static String toString(final Object value) {
		return value != null ? value.toString() : "null";
	}
	
}




