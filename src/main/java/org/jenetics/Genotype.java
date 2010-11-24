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

import static org.jenetics.util.Validator.nonNull;

import java.util.ListIterator;

import javolution.lang.Immutable;
import javolution.lang.Realtime;
import javolution.text.Text;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.Array;
import org.jenetics.util.Converter;
import org.jenetics.util.Factory;
import org.jenetics.util.Validator;
import org.jenetics.util.Verifiable;

/**
 * This class is the encoded problem solution with one to many Chromosomes.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Genotype<T extends Gene<?, T>> 
	implements Factory<Genotype<T>>,
				Iterable<Chromosome<T>>, 
				Verifiable, 
				XMLSerializable, 
				Realtime, 
				Immutable
{
	private static final long serialVersionUID = 2L;
	
	private final Array<Chromosome<T>> _chromosomes; 
	private final int _ngenes;
	
	//Caching isValid value.
	private volatile Boolean _valid = null;
	
	/**
	 * Create a new genotype with the given number of chromosome and the overall
	 * number of genes.
	 * 
	 * @param length the number of chromosomes of the genotype.
	 * @param ngenes the overall number of genes of the genotype.
	 */
	protected Genotype(final int length, final int ngenes) {
		_chromosomes = new Array<Chromosome<T>>(length);
		_ngenes = ngenes;
	}
	
	private Genotype(final Array<Chromosome<T>> chromosomes, final int ngenes) {
		assert(chromosomes != null);
		assert(ngenes(chromosomes) == ngenes);
		
		_chromosomes = chromosomes.seal();
		_ngenes = ngenes;
	}
	
	private static int ngenes(final Array<? extends Chromosome<?>> chromosomes) {
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
	
	public Array<Chromosome<T>> getChromosomes() {
		return _chromosomes.copy();
	}
	
	@Override
	public ListIterator<Chromosome<T>> iterator() {
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
			_valid = _chromosomes.foreach(new Validator.Verify()) == -1;
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
		final Genotype<T> genotype = new Genotype<T>(_chromosomes.length(), _ngenes);
		
		for (int i = 0; i < genotype.length(); ++i) {
			genotype._chromosomes.set(i, _chromosomes.get(i).newInstance());
		}
		return genotype;
	}
	
	 Genotype<T> newInstance(final Array<Chromosome<T>> chromosomes) {
		return new Genotype<T>(chromosomes, _ngenes);
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash += 37*_chromosomes.hashCode();
		return hash;
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
		return _chromosomes.equals(gt._chromosomes);
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
	Converter<Genotype<T>, Array<Chromosome<T>>> Chromosomes()
	{
		return new Converter<Genotype<T>, Array<Chromosome<T>>>() {
			@Override public Array<Chromosome<T>> convert(final Genotype<T> value) {
				return value.getChromosomes();
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
		final Array<? extends Chromosome<G>> chromosomes
	) {
		nonNull(chromosomes, "Chromosomes");
		if (chromosomes.length() == 0) {
			throw new IllegalArgumentException("Chromosomes must be given.");
		}
				
		final Genotype<G> genotype = new Genotype<G>(
				chromosomes.length(), 
				ngenes(chromosomes)
			);
		
		for (int i = 0; i < chromosomes.length(); ++i) {
			nonNull(chromosomes.get(i), "Chromosome[" + i + "]");
			genotype._chromosomes.set(i, chromosomes.get(i));
		}
		
		return genotype;
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
		
		final int ngenes = chromosome.length();
		
		final Genotype<G> genotype = new Genotype<G>(1, ngenes);
		genotype._chromosomes.set(0, chromosome);
		return genotype;
	}
	
	public static <G extends Gene<?, G>> Genotype<G> valueOf(
		final Chromosome<G> chrom1, 
		final Chromosome<G> chrom2
	) {
		nonNull(chrom1, "Chromosome 1");
		nonNull(chrom2, "Chromosome 2");
		
		final int ngenes = chrom1.length() + 
							chrom2.length();
		
		final Genotype<G> genotype = new Genotype<G>(2, ngenes);
		genotype._chromosomes.set(0, chrom1);
		genotype._chromosomes.set(1, chrom2);
		return genotype;
	}
	
	public static <G extends Gene<?, G>> Genotype<G> valueOf(
		final Chromosome<G> chrom1, 
		final Chromosome<G> chrom2, 
		final Chromosome<G> chrom3
	) {
		nonNull(chrom1, "Chromosome 1");
		nonNull(chrom2, "Chromosome 2");
		nonNull(chrom3, "Chromosome 3");
		
		final int ngenes = chrom1.length() + 
							chrom2.length() +
							chrom3.length();		
		
		final Genotype<G> genotype = new Genotype<G>(3, ngenes);
		genotype._chromosomes.set(0, chrom1);
		genotype._chromosomes.set(1, chrom2);
		genotype._chromosomes.set(2, chrom3);
		return genotype;
	}
	
	public static <G extends Gene<?, G>> Genotype<G> valueOf(
		final Chromosome<G> chrom1, 
		final Chromosome<G> chrom2, 
		final Chromosome<G> chrom3,
		final Chromosome<G> chrom4
	) {
		nonNull(chrom1, "Chromosome 1");
		nonNull(chrom2, "Chromosome 2");
		nonNull(chrom3, "Chromosome 3");
		nonNull(chrom4, "Chromosome 4");
		
		final int ngenes = chrom1.length() + 
							chrom2.length() +
							chrom3.length() +
							chrom4.length();		
		
		final Genotype<G> genotype = new Genotype<G>(4, ngenes);
		genotype._chromosomes.set(0, chrom1);
		genotype._chromosomes.set(1, chrom2);
		genotype._chromosomes.set(2, chrom3);
		genotype._chromosomes.set(3, chrom4);
		return genotype;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes"})
	protected static final XMLFormat<Genotype> 
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
			final Genotype genotype = new Genotype(length, ngenes);
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





