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

import static org.jenetics.util.Validator.notNull;
import javolution.context.ObjectFactory;
import javolution.lang.Immutable;
import javolution.lang.Realtime;
import javolution.text.Text;
import javolution.xml.XMLSerializable;


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
 * @version $Id: Phenotype.java,v 1.3 2008-08-26 22:29:33 fwilhelm Exp $
 */
public class Phenotype<G extends Gene<?>, C extends Comparable<C>> 
	implements Comparable<Phenotype<G, C>>, Immutable, Verifiable, 
				XMLSerializable, Realtime
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
	
	private void calculateFitness() {
		if (_rawFitness == null) {
			_rawFitness = _fitnessFunction.evaluate(_genotype);
			_fitness = _fitnessScaler.scale(_rawFitness);
		}
	}
	
	/**
	 * Return the fitness value of this <code>Phenotype</code>.
	 * 
	 * @return The fitness value of this <code>Phenotyp</code>.
	 */
	public C getFitness() {
		calculateFitness();
		return _fitness;
	}
	
	/**
	 * Return the raw fitness (befor scaling) of the phenotype.
	 * 
	 * @return The raw fitness (befor scaling) of the phenotype.
	 */
	public C getRawFitness() {
		calculateFitness();
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
		notNull(pt, "Phenotype");
		return getFitness().compareTo(pt.getFitness());
	}	
	
	@Override
	public int hashCode() {
		int hash = 17;
		
		hash = 37*_genotype.hashCode() + 17;
		hash = 37*_fitnessFunction.hashCode() + 17;
		hash = 37*_fitnessScaler.hashCode() + 17;
		
		return hash;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Phenotype)) {
			return false;
		}
		
		final Phenotype<?, ?> pt = (Phenotype<?, ?>)obj;
		return pt._fitnessFunction.equals(_fitnessFunction) &&
				pt._fitnessScaler.equals(_fitnessScaler) &&
				pt._genotype.equals(_genotype);
	}

	@Override
	public Text toText() {
		return _genotype.toText();
	}
	
	@Override
	public String toString() {
		return toText().toString();
	}
	
	@SuppressWarnings("unchecked")
	private static final ObjectFactory FACTORY = new ObjectFactory() {
		@Override protected Object create() {
			return new Phenotype();
		}
	};
	
	/**
	 * Factorymethod for creating a new {@link Phenotype} whith the same 
	 * {@link FitnessFunction} and age as this {@link Phenotype}.
	 * 
	 * @return New {@link Phenotype} whith the same {@link FitnessFunction}.
	 * @throws NullPointerException if the {@code genotype} is {@code null}.
	 */
	Phenotype<G, C> newInstance(final Genotype<G> genotype) {
		notNull(genotype, "Genotype");
		return Phenotype.valueOf(
			genotype, _fitnessFunction, _fitnessScaler, _generation
		);
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
	public static<SG extends Gene<?>, SC extends Comparable<SC>> 
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
	public static <SG extends Gene<?>, SC extends Comparable<SC>> 
	Phenotype<SG, SC> valueOf(
		final Genotype<SG> genotype, 
		final FitnessFunction<SG, SC> fitnessFunction, 
		final FitnessScaler<SC> fitnessScaler,
		final int generation
	) {
		notNull(genotype, "Genotype");
		notNull(fitnessFunction, "Fitness function");
		notNull(fitnessScaler, "Fitness scaler");
		if (generation < 0) {
			throw new IllegalArgumentException("Generation must not < 0: " + generation);
		}
		
		@SuppressWarnings("unchecked")
		Phenotype<SG, SC> p = (Phenotype<SG, SC>)FACTORY.object();
		p._genotype = Genotype.valueOf(genotype);
		p._fitnessFunction = fitnessFunction;
		p._fitnessScaler = fitnessScaler;
		p._generation = generation;
		
		p._rawFitness = null;
		p._fitness = null;
		return p;
	}
	
//	@SuppressWarnings("unchecked")
//	static final XMLFormat<Phenotype> 
//	XML = new XMLFormat<Phenotype>(Phenotype.class) {
//		@Override
//		public Phenotype<Gene<?>, ? extends Comparable<?>> newInstance(
//			final Class<Phenotype> cls, final InputElement xml
//		) throws XMLStreamException {
//			final Genotype<Gene<?>> gt = xml.getNext();
//			final FitnessFunction<Gene<?>> ff = xml.getNext();
//			final FitnessScaler fs = xml.getNext();
//			final int generation = xml.getAttribute("generation", 0);
//			final Phenotype<Gene<?>, ? extends Comparable<?>> pt = 
//				Phenotype.valueOf(gt, ff, fs, generation);
//			
//			pt._fitness = xml.getAttribute("fitness", 0);
//			pt._rawFitness = xml.getAttribute("raw-fitness", 0);
//			return pt;
//		}
//		@Override 
//		public void write(final Phenotype pt, final OutputElement xml) 
//			throws XMLStreamException 
//		{
//			xml.setAttribute("generation", pt._generation);
//			xml.setAttribute("fitness", pt.getFitness());
//			xml.setAttribute("raw-fitness", pt.getRawFitness());
//			xml.add(pt._genotype);
//			xml.add(pt._fitnessFunction);
//			xml.add(pt._fitnessScaler);
//		}
//		@Override
//		public void read(final InputElement xml, final Phenotype gt) {	
//		}
//	};

}




