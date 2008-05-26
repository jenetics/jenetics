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

import static org.jenetics.Checker.checkNull;

import java.util.Iterator;

import javolution.context.ObjectFactory;
import javolution.lang.Immutable;
import javolution.lang.Realtime;
import javolution.text.Text;
import javolution.text.TextBuilder;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

/**
 * This class is the encoded problem solution with one to many Chromosomes.
 * 
 * @see GenotypeFactory
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Genotype.java,v 1.4 2008-05-26 20:46:48 fwilhelm Exp $
 */
public class Genotype<T extends Gene<?>> 
	implements GenotypeFactory<T>, Iterable<Chromosome<T>>, Verifiable, 
			XMLSerializable, Realtime, Immutable
{
	private static final long serialVersionUID = 868536407305322003L;
	
	@SuppressWarnings("unchecked")
	private Chromosome[] _chromosomes; 
	private int _length;
	
	//Caching isValid value.
	private Boolean _valid = null;
	
	protected Genotype() {
	}
	
	/**
	 * Return the chromosome at the given index. It is garantued, that the 
	 * returned chromosome is not null.
	 * 
	 * @param index Chromosome index.
	 * @return The Chromosome.
	 * @throws IndexOutOfBoundsException if (index < 0 || index >= _length).
	 */
	public Chromosome<T> getChromosome(final int index) {
		checkIndex(index);
		assert(_chromosomes != null);
		assert(_chromosomes[index] != null);
		
		@SuppressWarnings("unchecked")
		Chromosome<T> chromosome = _chromosomes[index];
		return chromosome;
	}
	
	/**
	 * Return the first chromosome.
	 * 
	 * @return The first chromosome.
	 */
	public Chromosome<T> getChromosome() {
		assert(_chromosomes != null);
		assert(_chromosomes[0] != null);
		
		@SuppressWarnings("unchecked")
		Chromosome<T> chromosome = _chromosomes[0];
		return chromosome;
	}
	
	/**
	 * Return the first {@link Gene} of the first {@link Chromosome} of this
	 * {@code Genotype}.
	 * 
	 * @return the first {@link Gene} of the first {@link Chromosome} of this
	 *         {@code Genotype}.
	 */
	@SuppressWarnings("unchecked")
	public T getGene() {
		assert(_chromosomes != null);
		assert(_chromosomes[0] != null);
		return (T)_chromosomes[0].getGene();
	}
	
	@SuppressWarnings("unchecked")
	public Chromosome<T>[] getChromosomes() {
		Chromosome<T>[] chromosomes = new Chromosome[_length];
		for (int i = 0; i < _length; ++i) {
			chromosomes[i] = _chromosomes[i];
		}
		return chromosomes;
	}
	
	@Override
	public Iterator<Chromosome<T>> iterator() {
		@SuppressWarnings("unchecked") 
		final ArrayIterator<Chromosome<T>> 
		it = new ArrayIterator<Chromosome<T>>(_chromosomes);
		return it;
	}
	
	/**
	 * Getting the number of _chromosomes of this genotype.
	 * 
	 * @return number of _chromosomes.
	 */
	public int chromosomes() {
		return _length;
	}
	
	/**
	 * Getting the number of _chromosomes of this genotype.
	 * 
	 * @return number of _chromosomes.
	 */ 
	public int length() {
		return _length;
	}
	
	/**
	 * Test if this genotype is valid. A genotype is valid if all its
	 * {@link Chromosome}s are valid.
	 * 
	 * @return true if this genotype is valid, false otherwise.
	 */
	@Override
	public boolean isValid() {
		boolean valid = true;
		if (_valid == null) {
			for (int i = 0; i < _length && valid; ++i) {
				valid = ((Verifiable)_chromosomes[i]).isValid();
			}
			_valid = valid ? Boolean.TRUE : Boolean.FALSE;
		} else {
			valid = _valid.booleanValue();
		}
		return valid;
	}
	
	@Override
	public Genotype<T> newGenotype() {
		Genotype<T> genotype = newInstance(_length);
		for (int i = 0; i < _length; ++i) {
			genotype._chromosomes[i] = _chromosomes[i].newChromosome();
		}
		return genotype;
	}
	@Override
	public int hashCode() {
		int hash = 17;
		for (int i = 0, n = chromosomes(); i < n; ++i) {
			hash += getChromosome(i).hashCode()*37;
		}		 
		return hash;
	}
	
	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Genotype)) {
			return false;
		}
		
		final Genotype<?> gt = (Genotype<?>)o;
		boolean equals = chromosomes() == gt.chromosomes();
		for (int i = 0, n = chromosomes(); i < n && equals; ++i) {
			equals = getChromosome(i).equals(gt.getChromosome(i));
		}
		return equals;
	}
	
	private void checkIndex(final int index) {
		if (index < 0 || index >= _length) {
			throw new IndexOutOfBoundsException(
				"Invalid index: " + index + ", _length: " + _length
			);
		}
	}

	@Override
	public Text toText() {
		TextBuilder out = TextBuilder.newInstance();
		out.append("[");
		for (Object c : _chromosomes) {
			out.append(c.toString());
		}
		out.append("]");
		return out.toText();
	}
	
	private static class GenotypeFactory<A extends Gene<?>> extends ObjectFactory<Genotype<A>> {
		@Override protected Genotype<A> create() {
			return new Genotype<A>();
		}	
	}
	private static final GenotypeFactory<? extends Gene<?>> 
	FACTORY = new GenotypeFactory<Gene<?>>();
	
	static <G extends Gene<?>> Genotype<G> newInstance(final int length) {
		@SuppressWarnings("unchecked")
		Genotype<G> genotype = (Genotype<G>)FACTORY.object();
		
		if (genotype._chromosomes == null || genotype._length != length) {
			genotype._chromosomes = new Chromosome[length];
			genotype._length = length;
		}
		return genotype;
	}
	
	/**
	 * Create a new Genotype from a given array of <code>Chromosomes</code>.
	 * The <code>Chromosome</code> array <code>c</code> is cloned.
	 * 
	 * @param chromosomes The <code>Chromosome</code> array the <code>Genotype</code>
	 *        consists of.
	 * @throws NullPointerException if <code>c</code> is null or one of the
	 * 		   chromosome.
	 * @throws IllegalArgumentException if <code>c.length == 0</code>.
	 */
	public static <G extends Gene<?>> Genotype<G> valueOf(final Chromosome<G>[] chromosomes) {
		checkNull(chromosomes, "Chromosomes");
		if (chromosomes.length == 0) {
			throw new IllegalArgumentException("Chromosomes must be given.");
		}
		
		Genotype<G> genotype = newInstance(chromosomes.length);
		for (int i = 0; i < chromosomes.length; ++i) {
			checkNull(chromosomes[i], "Chromosome[" + i + "]");
			genotype._chromosomes[i] = chromosomes[i];
		}
		return genotype;
	}
	
	/**
	 * Create a new Genotype from a given {@link Chromosome}
	 * 
	 * @param chromosome The <code>Chromosome</code> array the <code>Genotype</code>
	 *        consists of.
	 * @throws NullPointerException if <code>chromosome</code> is null.
	 */
	public static <G extends Gene<?>> Genotype<G> valueOf(final Chromosome<G> chromosome) {
		checkNull(chromosome, "Chromosome");
		
		Genotype<G> genotype = newInstance(1);
		genotype._chromosomes[0] = chromosome;
		return genotype;
	}
	
	public static <G extends Gene<?>> Genotype<G> valueOf(
		final Chromosome<G> chrom1, 
		final Chromosome<G> chrom2
	) {
		checkNull(chrom1, "Chromosome 1");
		checkNull(chrom2, "Chromosome 2");
		
		Genotype<G> genotype = newInstance(2);
		genotype._chromosomes[0] = chrom1;
		genotype._chromosomes[1] = chrom2;
		return genotype;
	}
	
	public static <G extends Gene<?>> Genotype<G> valueOf(
			final Chromosome<G> chrom1, 
			final Chromosome<G> chrom2, 
			final Chromosome<G> chrom3
	) {
		checkNull(chrom1, "Chromosome 1");
		checkNull(chrom2, "Chromosome 2");
		checkNull(chrom3, "Chromosome 3");
		
		Genotype<G> genotype = newInstance(3);
		genotype._chromosomes[0] = chrom1;
		genotype._chromosomes[1] = chrom2;
		genotype._chromosomes[2] = chrom3;
		return genotype;
	}
	
	public static <G extends Gene<?>> Genotype<G> valueOf(final Genotype<G> genotype) {
		checkNull(genotype, "Genotype");
		
		Genotype<G> gtype = newInstance(genotype._length);
		for (int i = 0; i < genotype._chromosomes.length; ++i) {
			gtype._chromosomes[i] = genotype._chromosomes[i];
		}
		return gtype;
	}
	
	@SuppressWarnings("unchecked")
	static final XMLFormat<Genotype> 
	XML = new XMLFormat<Genotype>(Genotype.class) {
		@Override
		public Genotype newInstance(final Class<Genotype> cls, final InputElement xml) 
			throws XMLStreamException 
		{
			final int length = xml.getAttribute("length", 0);
			final Genotype genotype = Genotype.newInstance(length);
			for (int i = 0; i < length; ++i) {
				final Chromosome<?> c = xml.getNext();
				genotype._chromosomes[i] = c;
			}
			return genotype;
		}
		@Override 
		public void write(final Genotype gt, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute("length", gt._length);
			for (int i = 0; i < gt._length; ++i) {
				xml.add(gt._chromosomes[i]);
			}
		}
		@Override
		public void read(final InputElement xml, final Genotype gt) {	
		}
	};
}





