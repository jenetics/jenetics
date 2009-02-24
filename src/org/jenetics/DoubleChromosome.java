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

import java.util.Random;

import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.Array;
import org.jenetics.util.RandomRegistry;
import org.jscience.mathematics.number.Float64;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: DoubleChromosome.java,v 1.11 2009-02-24 19:33:24 fwilhelm Exp $
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
	 */
	public DoubleChromosome(final double min, final double max) {
		this(Float64.valueOf(min), Float64.valueOf(max));
	}
	
	/**
	 * Create a new chromosome
	 * 
	 * @param min the minimal value of this chromosome.
	 * @param max the maximal value of this chromosome.
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
		
		_min = genes[0]._max;
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
		final Array<DoubleGene> genes = Array.newInstance(length());
		final Random random = RandomRegistry.getRandom(); 
		
		for (int i = 0; i < length(); ++i) {
			final double value = random.nextDouble()*
				(_max.doubleValue() - _min.doubleValue()) + _min.doubleValue();
			
			genes.set(i, DoubleGene.valueOf(
				value, _min.doubleValue(), _max.doubleValue()
			));
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
		@Override
		public DoubleChromosome newInstance(
			final Class<DoubleChromosome> cls, final InputElement xml
		) throws XMLStreamException {
			final int length = xml.getAttribute("length", 0);
			final double min = xml.getAttribute("min", 0.0);
			final double max = xml.getAttribute("max", 1.0);
			
			final Array<DoubleGene> genes = Array.newInstance(length);
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
			xml.setAttribute("length", chromosome.length());
			xml.setAttribute("min", chromosome._min.doubleValue());
			xml.setAttribute("max", chromosome._max.doubleValue());
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





