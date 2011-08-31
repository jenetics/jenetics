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

import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;
import static org.jenetics.util.validation.nonNull;

import java.util.Iterator;

import javolution.lang.Immutable;
import javolution.lang.Realtime;
import javolution.text.Text;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.Array;
import org.jenetics.util.Converter;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.Seq;
import org.jenetics.util.Verifiable;
import org.jenetics.util.validation.Verify;

/**
 * This class is the encoded problem solution with one to many Chromosomes.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class Genotype<T extends Gene<?, T>> 
	implements 
		Factory<Genotype<T>>,
		Iterable<Chromosome<T>>, 
		Verifiable, 
		XMLSerializable, 
		Realtime, 
		Immutable
{
	private static final long serialVersionUID = 2L;
	
	private final ISeq<Chromosome<T>> _chromosomes; 
	private final int _ngenes;
	
	//Caching isValid value.
	private volatile Boolean _valid = null;
	
	private Genotype(final ISeq<Chromosome<T>> chromosomes, final int ngenes) {
		assert(chromosomes != null);
		assert(ngenes(chromosomes) == ngenes);
		
		_chromosomes = chromosomes;
		_ngenes = ngenes;
	}
	
	private static int ngenes(final Seq<? extends Chromosome<?>> chromosomes) {
		int ngenes = 0;
		for (int i = chromosomes.length(); --i >= 0;) {
			ngenes += chromosomes.get(i).length();
		}
		return ngenes;
	}
	
	/**
	 * Return the chromosome at the given index. It is guaranteed, that the 
	 * returned chromosome is not null.
	 * 
	 * @param index Chromosome index.
	 * @return The Chromosome.
	 * @throws IndexOutOfBoundsException if (index < 0 || index >= _length).
	 */
	public Chromosome<T> getChromosome(final int index) {
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
	 * 		  {@code Genotype}.
	 */
	public T getGene() {
		assert(_chromosomes != null);
		assert(_chromosomes.get(0) != null);
		
		return _chromosomes.get(0).getGene();
	}
	
	public ISeq<Chromosome<T>> toSeq() {
		return _chromosomes;
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
	public int length() {
		return _chromosomes.length();
	}
	
	/**
	 * Return the number of genes this genotype consists of. This is the sum of
	 * the number of genes of the genotype chromosomes.
	 * 
	 * @return Return the number of genes this genotype consists of.
	 */
	int getNumberOfGenes() {
		return _ngenes;
	}
	
	/**
	 * Test if this genotype is valid. A genotype is valid if all its
	 * {@link Chromosome}s are valid.
	 * 
	 * @return true if this genotype is valid, false otherwise.
	 */
	@Override
	public boolean isValid() {
		if (_valid == null) {
			_valid = _chromosomes.foreach(new Verify()) == -1;
		}
		return _valid;
	}
	
	/**
	 * Return a new, random genotype by creating new, random chromosomes (calling
	 * the {@link Chromosome#newInstance()} method) from the chromosomes of this
	 * genotype.
	 */
	@Override
	public Genotype<T> newInstance() {
		final Array<Chromosome<T>> chromosomes = new Array<Chromosome<T>>(length());		
		for (int i = 0; i < length(); ++i) {
			chromosomes.set(i, _chromosomes.get(i).newInstance());
		}
		
		return new Genotype<T>(chromosomes.toISeq(), _ngenes);
	}
	
	Genotype<T> newInstance(final ISeq<Chromosome<T>> chromosomes) {
		return new Genotype<T>(chromosomes, _ngenes);
	}
	
	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(_chromosomes).value();
	}
	
	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Genotype<?>)) {
			return false;
		}
		
		final Genotype<?> gt = (Genotype<?>)o;
		return eq(_chromosomes, gt._chromosomes);
	}

	@Override
	public Text toText() {
		return new Text(_chromosomes.toString());
	}
	
	@Override
	public String toString() {
		return _chromosomes.toString();
	}
	
	
	/* *************************************************************************
	 *  Property access methods
	 * ************************************************************************/
	
	/**
	 * Return a converter which access the chromosome array of this genotype.
	 */
	public static <T extends Gene<?, T>> 
	Converter<Genotype<T>, ISeq<Chromosome<T>>> Chromosomes()
	{
		return new Converter<Genotype<T>, ISeq<Chromosome<T>>>() {
			@Override public ISeq<Chromosome<T>> convert(final Genotype<T> value) {
				return value.toSeq();
			}
		};
	}
	
	/**
	 * Return a converter which access the chromosome with the given index of
	 * this genotype.
	 */
	public static <T extends Gene<?, T>> 
	Converter<Genotype<T>, Chromosome<T>> Chromosome(final int index)
	{
		return new Converter<Genotype<T>, Chromosome<T>>() {
			@Override public Chromosome<T> convert(final Genotype<T> value) {
				return value.getChromosome(index);
			}
		};
	}
	
	/**
	 * Return a converter which access the first chromosome of this genotype.
	 */
	public static <T extends Gene<?, T>> 
	Converter<Genotype<T>, Chromosome<T>> Chromosome()
	{
		return new Converter<Genotype<T>, Chromosome<T>>() {
			@Override public Chromosome<T> convert(final Genotype<T> value) {
				return value.getChromosome();
			}
		};
	}
	
	
	/**
	 * Create a new Genotype from a given array of <code>Chromosomes</code>.
	 * The <code>Chromosome</code> array <code>c</code> is cloned.
	 * 
	 * @param chromosomes The <code>Chromosome</code> array the <code>Genotype</code>
	 * 		 consists of.
	 * @throws NullPointerException if <code>c</code> is null or one of the
	 *			chromosome.
	 * @throws IllegalArgumentException if <code>c.length == 0</code>.
	 */
	public static <G extends Gene<?, G>> Genotype<G> valueOf(
		final ISeq<? extends Chromosome<G>> chromosomes
	) {
		nonNull(chromosomes, "Chromosomes");
		if (chromosomes.length() == 0) {
			throw new IllegalArgumentException("Chromosomes must be given.");
		}
		
		return new Genotype<G>(
				chromosomes.upcast(chromosomes), 
				ngenes(chromosomes)
			);
	}
	
	/**
	 * Create a new Genotype from a given {@link Chromosome}
	 * 
	 * @param chromosome The <code>Chromosome</code> array the <code>Genotype</code>
	 * 		 consists of.
	 * @throws NullPointerException if <code>chromosome</code> is null.
	 */
	public static <G extends Gene<?, G>> Genotype<G> valueOf(
		final Chromosome<G> chromosome
	) {
		nonNull(chromosome, "Chromosome");
		
		return new Genotype<G>(
				new Array<Chromosome<G>>(chromosome).toISeq(), 
				chromosome.length()
			);
	}
	
	public static <G extends Gene<?, G>> Genotype<G> valueOf(
		final Chromosome<G> ch1, 
		final Chromosome<G> ch2
	) {
		nonNull(ch1, "Chromosome 1");
		nonNull(ch2, "Chromosome 2");
		
		return new Genotype<G>(
				new Array<Chromosome<G>>(ch1, ch2).toISeq(), 
				ch1.length() + ch2.length()
			);
	}
	
	public static <G extends Gene<?, G>> Genotype<G> valueOf(
		final Chromosome<G> ch1, 
		final Chromosome<G> ch2, 
		final Chromosome<G> ch3
	) {
		nonNull(ch1, "Chromosome 1");
		nonNull(ch2, "Chromosome 2");
		nonNull(ch3, "Chromosome 3");
		
		return new Genotype<G>(
				new Array<Chromosome<G>>(ch1, ch2, ch3).toISeq(), 
				ch1.length() + ch2.length() + ch3.length()
			);		
	}
	
	public static <G extends Gene<?, G>> Genotype<G> valueOf(
		final Chromosome<G> ch1, 
		final Chromosome<G> ch2, 
		final Chromosome<G> ch3,
		final Chromosome<G> ch4
	) {
		nonNull(ch1, "Chromosome 1");
		nonNull(ch2, "Chromosome 2");
		nonNull(ch3, "Chromosome 3");
		nonNull(ch4, "Chromosome 4");
		
		return new Genotype<G>(
				new Array<Chromosome<G>>(ch1, ch2, ch3, ch4).toISeq(), 
				ch1.length() + ch2.length() + ch3.length() + ch4.length()
			);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes"})
	static final XMLFormat<Genotype> 
	XML = new XMLFormat<Genotype>(Genotype.class) 
	{
		private static final String LENGTH = "length";
		private static final String NGENES = "ngenes";
		
		@Override
		public Genotype newInstance(
			final Class<Genotype> cls, final InputElement xml
		) 
			throws XMLStreamException 
		{
			final int length = xml.getAttribute(LENGTH, 0);
			final int ngenes = xml.getAttribute(NGENES, 0);
			final Array<Chromosome> chromosomes = new Array<Chromosome>(length);
			for (int i = 0; i < length; ++i) {
				final Chromosome<?> c = xml.getNext();
				chromosomes.set(i, c);
			}
			
			return new Genotype(chromosomes.toISeq(), ngenes);
		}
		@Override 
		public void write(final Genotype gt, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute(LENGTH, gt.length());
			xml.setAttribute(NGENES, gt.getNumberOfGenes());
			for (int i = 0; i < gt.length(); ++i) {
				xml.add(gt._chromosomes.get(i));
			}
		}
		@Override
		public void read(final InputElement xml, final Genotype gt) {	
		}
	};
}





