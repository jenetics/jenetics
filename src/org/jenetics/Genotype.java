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

import java.util.Iterator;

import org.jenetics.util.Array;

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
 * @version $Id: Genotype.java,v 1.9 2008-09-23 19:18:57 fwilhelm Exp $
 */
public class Genotype<T extends Gene<?>> 
	implements GenotypeFactory<T>, Iterable<Chromosome<T>>, Verifiable, 
			XMLSerializable, Realtime, Immutable
{
	private static final long serialVersionUID = 868536407305322003L;
	
	private final Array<Chromosome<T>> _chromosomes; 
	
	//Caching isValid value.
	private Boolean _valid = null;
	
	protected Genotype(final int length) {
		_chromosomes = Array.newInstance(length);
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
		assert(_chromosomes.get(index) != null);
		
		return _chromosomes.get(index);
	}
	
	/**
	 * Return the first chromosome.
	 * 
	 * @return The first chromosome.
	 */
	public Chromosome<T> getChromosome() {
		assert(_chromosomes != null);
		assert(_chromosomes.get(0) != null);
		
		return _chromosomes.get(0);
	}
	
	/**
	 * Return the first {@link Gene} of the first {@link Chromosome} of this
	 * {@code Genotype}.
	 * 
	 * @return the first {@link Gene} of the first {@link Chromosome} of this
	 *         {@code Genotype}.
	 */
	public T getGene() {
		assert(_chromosomes != null);
		assert(_chromosomes.get(0) != null);
		return _chromosomes.get(0).getGene();
	}
	
	public Array<Chromosome<T>> getChromosomes() {
		return _chromosomes.copy();
	}
	
	@Override
	public Iterator<Chromosome<T>> iterator() {
		return _chromosomes.iterator();
	}
	
	/**
	 * Getting the number of _chromosomes of this genotype.
	 * 
	 * @return number of _chromosomes.
	 */
	public int chromosomes() {
		return _chromosomes.length();
	}
	
	/**
	 * Getting the number of _chromosomes of this genotype.
	 * 
	 * @return number of _chromosomes.
	 */ 
	public int length() {
		return _chromosomes.length();
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
			for (int i = 0; i < _chromosomes.length() && valid; ++i) {
				valid = ((Verifiable)_chromosomes.get(i)).isValid();
			}
			_valid = valid ? Boolean.TRUE : Boolean.FALSE;
		} else {
			valid = _valid.booleanValue();
		}
		return valid;
	}
	
	@Override
	public Genotype<T> newGenotype() {
		final Genotype<T> genotype = new Genotype<T>(_chromosomes.length());
		for (int i = 0; i < _chromosomes.length(); ++i) {
			genotype._chromosomes.set(i, _chromosomes.get(i).newChromosome());
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
		if (index < 0 || index >= length()) {
			throw new IndexOutOfBoundsException(
				"Invalid index: " + index + ", _length: " + length()
			);
		}
	}

	@Override
	public Text toText() {
		final TextBuilder out = TextBuilder.newInstance();
		out.append("[");
		for (Object c : _chromosomes) {
			out.append(c);
		}
		out.append("]");
		return out.toText();
	}
	
	@Override
	public String toString() {
		return toText().toString();
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
	public static <G extends Gene<?>> Genotype<G> valueOf(final Array<Chromosome<G>> chromosomes) {
		notNull(chromosomes, "Chromosomes");
		if (chromosomes.length() == 0) {
			throw new IllegalArgumentException("Chromosomes must be given.");
		}
		
		final Genotype<G> genotype = new Genotype<G>(chromosomes.length());
		for (int i = 0; i < chromosomes.length(); ++i) {
			notNull(chromosomes.get(i), "Chromosome[" + i + "]");
			genotype._chromosomes.set(i, chromosomes.get(i));
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
		notNull(chromosome, "Chromosome");
		
		final Genotype<G> genotype = new Genotype<G>(1);
		genotype._chromosomes.set(0, chromosome);
		return genotype;
	}
	
	public static <G extends Gene<?>> Genotype<G> valueOf(
		final Chromosome<G> chrom1, 
		final Chromosome<G> chrom2
	) {
		notNull(chrom1, "Chromosome 1");
		notNull(chrom2, "Chromosome 2");
		
		final Genotype<G> genotype = new Genotype<G>(2);
		genotype._chromosomes.set(0, chrom1);
		genotype._chromosomes.set(1, chrom2);
		return genotype;
	}
	
	public static <G extends Gene<?>> Genotype<G> valueOf(
			final Chromosome<G> chrom1, 
			final Chromosome<G> chrom2, 
			final Chromosome<G> chrom3
	) {
		notNull(chrom1, "Chromosome 1");
		notNull(chrom2, "Chromosome 2");
		notNull(chrom3, "Chromosome 3");
		
		final Genotype<G> genotype = new Genotype<G>(3);
		genotype._chromosomes.set(0, chrom1);
		genotype._chromosomes.set(1, chrom2);
		genotype._chromosomes.set(2, chrom3);
		return genotype;
	}
	
	public static <G extends Gene<?>> Genotype<G> valueOf(final Genotype<G> genotype) {
		notNull(genotype, "Genotype");
		
		final Genotype<G> gtype = new Genotype<G>(genotype.length());
		for (int i = 0; i < genotype.length(); ++i) {
			gtype._chromosomes.set(i, genotype._chromosomes.get(i));
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
			final Genotype genotype = new Genotype(length);
			for (int i = 0; i < length; ++i) {
				final Chromosome<?> c = xml.getNext();
				genotype._chromosomes.set(i, c);
			}
			return genotype;
		}
		@Override 
		public void write(final Genotype gt, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute("length", gt.length());
			for (int i = 0; i < gt.length(); ++i) {
				xml.add(gt._chromosomes.get(i));
			}
		}
		@Override
		public void read(final InputElement xml, final Genotype gt) {	
		}
	};
}





