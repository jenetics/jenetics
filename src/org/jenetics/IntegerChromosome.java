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

import javolution.context.ObjectFactory;
import javolution.text.Text;
import javolution.text.TextBuilder;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.number.Integer64;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: IntegerChromosome.java,v 1.1 2008-03-25 18:31:55 fwilhelm Exp $
 */
public class IntegerChromosome extends NumberChromosome<IntegerGene> 
	implements ChromosomeFactory<IntegerGene>, XMLSerializable
{
	private static final long serialVersionUID = 1L;

	protected IntegerChromosome() {
	}
	
	@Override
	public Class<IntegerGene> getType() {
		return IntegerGene.class;
	}
	
	@Override
	public IntegerGene[] getGenes() {
		IntegerGene[] genes = new IntegerGene[_length];
		System.arraycopy(_genes, 0, genes, 0, _length);
		return genes;
	}

	@Override
	public IntegerChromosome mutate(final int index) {
		IntegerChromosome chromosome = newInstance(_length);
		System.arraycopy(_genes, 0, chromosome._genes, 0, _length);
		
		final int value = RandomRegistry.getRandom().nextInt(_max.intValue() + 1) + _min.intValue();
		chromosome._genes[index] = IntegerGene.valueOf(value, _min.intValue(), _max.intValue());
		
		return chromosome;
	}
	
	@Override
	public IntegerChromosome newChromosome(final IntegerGene[] genes) {
		IntegerChromosome chromosome = newInstance(_length);
		System.arraycopy(genes, 0, chromosome._genes, 0, _length);
		
		chromosome._min = genes[0]._min;
		chromosome._max = genes[0]._max;
		return chromosome;
	}

	@Override
	public IntegerChromosome newChromosome() {
		IntegerChromosome chromosome = newInstance(_length);
		chromosome._min = _min;
		chromosome._max = _max;
		
		final Random random = RandomRegistry.getRandom();
		for (int i = 0; i < _length; ++i) {
			final int value = random.nextInt(_max.intValue() + 1) + _min.intValue();
			chromosome._genes[i] = IntegerGene.valueOf(value, _min.intValue(), _max.intValue());
		}
		
		return chromosome;
	}

	@Override
	public Text toText() {
		TextBuilder out = TextBuilder.newInstance();
		out.append("[");
		for (NumberGene<Integer64> gene : this) {
			out.append(gene.toText());
		}
		out.append("]");
		return out.toText();
	}

	public IntegerChromosome copy() {
		IntegerChromosome c = newInstance(_length);
		c._min = _min;
		c._max = _max;
		System.arraycopy(_genes, 0, c._genes, 0, c._length);
		return c;
	}
	
	@Override
	public IntegerChromosome clone() {
		return copy();
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
	
	static final ObjectFactory<IntegerChromosome> 
	FACTORY = new ObjectFactory<IntegerChromosome>() {
		@Override protected IntegerChromosome create() {
			return new IntegerChromosome();
		}
	};
	
	static IntegerChromosome newInstance(final int length) {
		IntegerChromosome chromosome = FACTORY.object();
		if (chromosome._genes == null || chromosome._genes.length != length) {
			chromosome._genes = new IntegerGene[length];
			chromosome._length = length;
		}
		return chromosome;
	}
	
	public static IntegerChromosome valueOf(final Integer64 min, final Integer64 max) {
		return valueOf(min, max, 1);
	}
	
	public static IntegerChromosome valueOf(final long min, final long max) {
		return valueOf(Integer64.valueOf(min), Integer64.valueOf(max));
	}
	
	public static IntegerChromosome valueOf(final int min, final int max, final int length) {
		return valueOf(Integer64.valueOf(min), Integer64.valueOf(max), length);
	}
	
	/**
	 * Create a new random IntegerChromosome.
	 * 
	 * @param min the min value of the {@link IntegerGene}s.
	 * @param max the max value of the {@link IntegerGene}s.
	 * @param length the length of the chromosome.
	 * @return a new IntegerChromosome.
	 * @throws IllegalArgumentException if min is not less max.
	 */
	public static IntegerChromosome valueOf(final Integer64 min, final Integer64 max, final int length) {
		if (!min.isLessThan(max)) {
			throw new IllegalArgumentException(
				"Minumum must be less than maximim: " + min + " not less " + max
			);
		}
		
		IntegerChromosome chromosome = newInstance(length);
		chromosome._min = min;
		chromosome._max = max;
		
		for (int i = 0; i < length; ++i) {
			chromosome._genes[i] = IntegerGene.valueOf(min, max);
		}
		
		return chromosome;
	}
	
	static final XMLFormat<IntegerChromosome> 
	XML = new XMLFormat<IntegerChromosome>(IntegerChromosome.class) {
		@Override
		public IntegerChromosome newInstance(
			final Class<IntegerChromosome> cls, final InputElement element
		) throws XMLStreamException {
			final int length = element.getAttribute("length", 0);
			final IntegerChromosome chromosome = IntegerChromosome.newInstance(length);
			for (int i = 0; i < length; ++i) {
				IntegerGene gene = element.getNext();
				chromosome._genes[i] = gene;
			}
			return chromosome;
		}
		@Override
		public void write(final IntegerChromosome chromosome, final OutputElement element) 
			throws XMLStreamException 
		{
			element.setAttribute("length", chromosome._length);
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






