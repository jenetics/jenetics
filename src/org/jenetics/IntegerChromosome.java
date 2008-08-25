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
import org.jscience.mathematics.number.Integer64;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: IntegerChromosome.java,v 1.3 2008-08-25 19:35:23 fwilhelm Exp $
 */
public class IntegerChromosome extends NumberChromosome<IntegerGene> 
	implements ChromosomeFactory<IntegerGene>, XMLSerializable
{
	private static final long serialVersionUID = 1L;

	protected IntegerChromosome(final Array<IntegerGene> genes) {
		super(genes);
	}
	
	public IntegerChromosome(final long min, final long max) {
		this(Integer64.valueOf(min), Integer64.valueOf(max));
	}
	
	public IntegerChromosome(final Integer64 min, final Integer64 max) {
		this(min, max, 1);
	}
	
	public IntegerChromosome(final long min, final long max, int length) {
		this(Integer64.valueOf(min), Integer64.valueOf(max), length);
	}
	
	public IntegerChromosome(final IntegerGene... genes) {
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
	public IntegerChromosome(final Integer64 min, final Integer64 max, final int length) {
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
	public Class<IntegerGene> getType() {
		return IntegerGene.class;
	}
	
	@Override
	public IntegerChromosome mutate(final int index) {
		final IntegerChromosome chromosome = new IntegerChromosome(_genes);
		
		final IntegerGene gene = _genes.get(index);
		final Random random = RandomRegistry.getRandom(); 
		long value = ((long)random.nextDouble()*
			(_max.longValue() - _min.longValue()) + _min.longValue());
		chromosome._genes.set(index, gene.newInstance(value));
		
		return chromosome;
	}
	
	@Override
	public IntegerChromosome newChromosome(final Array<IntegerGene> genes) {
		final IntegerChromosome chromosome = new IntegerChromosome(genes);		
		chromosome._min = genes.get(0)._min;
		chromosome._max = genes.get(0)._max;
		return chromosome;
	}

	@Override
	public IntegerChromosome newChromosome() {
		final Array<IntegerGene> genes = Array.newInstance(length());
		final Random random = RandomRegistry.getRandom(); 
		
		for (int i = 0; i < length(); ++i) {
			final long value = ((long)random.nextDouble()*
				(_max.longValue() - _min.longValue()) + _min.longValue());
			
			genes.set(i, IntegerGene.valueOf(
				value, _min.longValue(), _max.longValue()
			));
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
		@Override
		public IntegerChromosome newInstance(
			final Class<IntegerChromosome> cls, final InputElement element
		) throws XMLStreamException {
			final int length = element.getAttribute("length", 0);
			final Array<IntegerGene> genes = Array.newInstance(length);
			
			for (int i = 0; i < length; ++i) {
				final IntegerGene gene = element.getNext();
				genes.set(i, gene);
			}
			return new IntegerChromosome(genes);
		}
		@Override
		public void write(final IntegerChromosome chromosome, final OutputElement element) 
			throws XMLStreamException 
		{
			element.setAttribute("length", chromosome.length());
			for (IntegerGene gene : chromosome) {
				element.add(gene);
			}
		}
		@Override
		public void read(final InputElement element, final IntegerChromosome chromosome) 
			throws XMLStreamException 
		{
		}
	};

}






