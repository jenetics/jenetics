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
import org.jscience.mathematics.number.Float64;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: DoubleChromosome.java,v 1.16 2009-06-18 20:45:17 fwilhelm Exp $
 */
public class DoubleChromosome extends NumberChromosome<DoubleGene> 
	implements ChromosomeFactory<DoubleGene>, XMLSerializable
{	
	private static final long serialVersionUID = 6018295796115102264L;
	
	
	protected DoubleChromosome(final Array<DoubleGene> genes) {
		super(genes);
	}
	
	/**
	 * Create a new chromosome
	 * 
	 * @param min the minimal value of this chromosome.
	 * @param max the maximal value of this chromosome.
	 * @throws IllegalArgumentException if min is not less max.
	 */
	public DoubleChromosome(final double min, final double max) {
		this(Float64.valueOf(min), Float64.valueOf(max));
	}
	
	/**
	 * Create a new chromosome
	 * 
	 * @param min the minimal value of this chromosome.
	 * @param max the maximal value of this chromosome.
	 * @throws NullPointerException if {@code min} or {@code max} is {@code null}.
	 * @throws IllegalArgumentException if min is not less max.
	 */
	public DoubleChromosome(final Float64 min, final Float64 max) {
		this(min, max, 1);
	}
	
	/**
	 * Create a new chromosome
	 * 
	 * @param min the minimal value of this chromosome.
	 * @param max the maximal value of this chromosome.
	 * @param length the {@code length} of the new chromosome.
	 * @throws IllegalArgumentException if the {@code length} is smaller than 
	 *         one.
	 */
	public DoubleChromosome(final double min, final double max, final int length) {
		this(Float64.valueOf(min), Float64.valueOf(max), length);
	}
	
	/**
	 * Create a new chromosome.
	 * 
	 * @param genes the genes this chromosome consists.
	 * @throws IllegalArgumentException if the number of genes is smaller than 
	 *        one.
	 * @throws NullPointerException if the {@code genes} are {@code null}.
	 */
	public DoubleChromosome(final DoubleGene... genes) {
		super(genes.length);
		
		_min = genes[0]._min;
		_max = genes[0]._max;
		for (int i = 0; i < genes.length; ++i) {
			_genes.set(i, genes[i]);
		}
	}
	
	/**
	 * Create a new random DoubleChromosome.
	 * 
	 * @param min the min value of the {@link DoubleGene}s.
	 * @param max the max value of the {@link DoubleGene}s.
	 * @param length the length of the chromosome.
	 * @throws IllegalArgumentException if min is not less max.
	 */
	public DoubleChromosome(final Float64 min, final Float64 max, final int length) {
		super(length);
		
		if (!min.isLessThan(max)) {
			throw new IllegalArgumentException(
				"Minumum must be less than maximim: " + min + " not less " + max
			);
		}
		_min = min;
		_max = max;
		
		for (int i = 0; i < length; ++i) {
			_genes.set(i, DoubleGene.valueOf(min, max));
		}
	}
	
	@Override
	public DoubleChromosome newInstance(final Array<DoubleGene> genes) {
		final DoubleChromosome chromosome = new DoubleChromosome(genes);		
		chromosome._min = genes.get(0)._min;
		chromosome._max = genes.get(0)._max;
		return chromosome;
	}

	@Override
	public DoubleChromosome newInstance() {
		final Array<DoubleGene> genes = new Array<DoubleGene>(length());
		final Factory<DoubleGene> factory = _genes.get(0);
		
		for (int i = 0; i < length(); ++i) {
			genes.set(i, factory.newInstance());
		}
		
		final DoubleChromosome chromosome = new DoubleChromosome(genes);
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
		if (!(obj instanceof DoubleChromosome)) {
			return false;
		}
		return super.equals(obj);
	}
	
	

	
	static final XMLFormat<DoubleChromosome> 
	XML = new XMLFormat<DoubleChromosome>(DoubleChromosome.class) 
	{
		private static final String LENGTH = "length";
		private static final String MIN = "min";
		private static final String MAX = "max";
		
		@Override
		public DoubleChromosome newInstance(
			final Class<DoubleChromosome> cls, final InputElement xml
		) throws XMLStreamException {
			final int length = xml.getAttribute(LENGTH, 0);
			final double min = xml.getAttribute(MIN, 0.0);
			final double max = xml.getAttribute(MAX, 1.0);
			
			final Array<DoubleGene> genes = new Array<DoubleGene>(length);
			for (int i = 0; i < length; ++i) {
				final Float64 value = xml.getNext();
				genes.set(i, DoubleGene.valueOf(value.doubleValue(), min, max));
			}
			
			final DoubleChromosome chromosome = new DoubleChromosome(genes);
			chromosome._min = Float64.valueOf(min);
			chromosome._max = Float64.valueOf(max);
			
			return chromosome;
		}
		@Override
		public void write(final DoubleChromosome chromosome, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute(LENGTH, chromosome.length());
			xml.setAttribute(MIN, chromosome._min.doubleValue());
			xml.setAttribute(MAX, chromosome._max.doubleValue());
			for (DoubleGene gene : chromosome) {
				xml.add(gene.getAllele());
			}
		}
		@Override
		public void read(final InputElement element, final DoubleChromosome chromosome) 
			throws XMLStreamException 
		{
		}
	};


}





