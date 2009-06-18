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

import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jscience.mathematics.number.Integer64;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: IntegerChromosome.java,v 1.14 2009-06-18 20:45:18 fwilhelm Exp $
 */
public class IntegerChromosome extends NumberChromosome<IntegerGene> 
	implements ChromosomeFactory<IntegerGene>, XMLSerializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new chromosome from the given genes array.
	 * 
	 * @param genes the genes of the new chromosome.
	 * @throws IllegalArgumentException if the {@code genes.length()} is smaller than 
	 *         one.
	 */
	protected IntegerChromosome(final Array<IntegerGene> genes) {
		super(genes);
	}
	
	/**
	 * Create a new random IntegerChromosome.
	 * 
	 * @param min the min value of the {@link DoubleGene}s.
	 * @param max the max value of the {@link DoubleGene}s.
	 * @throws IllegalArgumentException if min is not less max.
	 */
	public IntegerChromosome(final long min, final long max) {
		this(Integer64.valueOf(min), Integer64.valueOf(max));
	}
	
	/**
	 * Create a new random IntegerChromosome with length one.
	 * 
	 * @param min the min value of the {@link DoubleGene}s.
	 * @param max the max value of the {@link DoubleGene}s.
	 * @throws NullPointerException if {@code min} or {@code max} is {@code null}.
	 * @throws IllegalArgumentException if min is not less max.
	 */
	public IntegerChromosome(final Integer64 min, final Integer64 max) {
		this(min, max, 1);
	}
	
	/**
	 * Create a new random IntegerChromosome.
	 * 
	 * @param min the min value of the {@link DoubleGene}s.
	 * @param max the max value of the {@link DoubleGene}s.
	 * @param length the length of the chromosome.
	 * @throws IllegalArgumentException if min is not less max.
	 */
	public IntegerChromosome(final long min, final long max, int length) {
		this(Integer64.valueOf(min), Integer64.valueOf(max), length);
	}
	
	/**
	 * Create a new chromosome from the given genes array.
	 * 
	 * @param genes the genes of the new chromosome.
	 * @throws NullPointerException if the given genes array is {@code null}.
	 * @throws IllegalArgumentException if the {@code genes.length} is smaller than 
	 *         one.
	 */
	public IntegerChromosome(final IntegerGene... genes) {
		super(genes.length);
		
		_min = genes[0]._min;
		_max = genes[0]._max;
		for (int i = 0; i < genes.length; ++i) {
			_genes.set(i, genes[i]);
		}
	}
	
	/**
	 * Create a new random IntegerChromosome.
	 * 
	 * @param min the min value of the {@link DoubleGene}s.
	 * @param max the max value of the {@link DoubleGene}s.
	 * @param length the length of the chromosome.
	 * @throws NullPointerException if {@code min} or {@code max} is {@code null}.
	 * @throws IllegalArgumentException if min is not less max.
	 */
	public IntegerChromosome(
		final Integer64 min, final Integer64 max, final int length
	) {
		super(length);
		
		if (!min.isLessThan(max)) {
			throw new IllegalArgumentException(
				"Minumum must be less than maximim: " + min + " not less " + max
			);
		}
		_min = min;
		_max = max;
		
		for (int i = 0; i < length; ++i) {
			_genes.set(i, IntegerGene.valueOf(min, max));
		}
	}
	
	@Override
	public IntegerChromosome newInstance(final Array<IntegerGene> genes) {
		final IntegerChromosome chromosome = new IntegerChromosome(genes);		
		chromosome._min = genes.get(0)._min;
		chromosome._max = genes.get(0)._max;
		return chromosome;
	}

	@Override
	public IntegerChromosome newInstance() {
		final Array<IntegerGene> genes = new Array<IntegerGene>(length());
		final Factory<IntegerGene> factory = _genes.get(0);
		
		for (int i = 0; i < length(); ++i) {
			genes.set(i, factory.newInstance());
		}
		
		final IntegerChromosome chromosome = new IntegerChromosome(genes);
		chromosome._min = _min;
		chromosome._max = _max;
		return chromosome;
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash += super.hashCode()*37;
		return hash;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof IntegerChromosome)) {
			return false;
		}
		return super.equals(obj);
	}
	
	static final XMLFormat<IntegerChromosome> 
	XML = new XMLFormat<IntegerChromosome>(IntegerChromosome.class) {
		private static final String LENGTH = "length";
		private static final String MIN = "min";
		private static final String MAX = "max";
		
		@Override
		public IntegerChromosome newInstance(
			final Class<IntegerChromosome> cls, final InputElement xml
		) throws XMLStreamException 
		{
			final int length = xml.getAttribute(LENGTH, 0);
			final int min = xml.getAttribute(MIN, 0);
			final int max = xml.getAttribute(MAX, 10);
			final Array<IntegerGene> genes = new Array<IntegerGene>(length);
			
			for (int i = 0; i < length; ++i) {
				final Integer64 value = xml.getNext();
				genes.set(i, IntegerGene.valueOf(value.longValue(), min, max));
			}
			
			final IntegerChromosome chromosome = new IntegerChromosome(genes);
			chromosome._min = Integer64.valueOf(min);
			chromosome._max = Integer64.valueOf(max);
			
			return chromosome;
		}
		@Override
		public void write(final IntegerChromosome chromosome, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute(LENGTH, chromosome.length());
			xml.setAttribute(MIN, chromosome._min.intValue());
			xml.setAttribute(MAX, chromosome._max.intValue());
			for (IntegerGene gene : chromosome) {
				xml.add(gene.getAllele());
			}
		}
		@Override
		public void read(final InputElement element, final IntegerChromosome chromosome) 
			throws XMLStreamException 
		{
		}
	};

}






