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

import org.jscience.mathematics.number.Float64;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: DoubleChromosome.java,v 1.1 2008-03-25 18:31:57 fwilhelm Exp $
 */
public class DoubleChromosome extends NumberChromosome<DoubleGene> 
	implements ChromosomeFactory<DoubleGene>, XMLSerializable
{	
	private static final long serialVersionUID = 6018295796115102264L;
	
	protected DoubleChromosome() {
	}

	@Override
	public Class<DoubleGene> getType() {
		return DoubleGene.class;
	}
	
	@Override
	public DoubleGene[] getGenes() {
		DoubleGene[] genes = new DoubleGene[_length];
		System.arraycopy(_genes, 0, genes, 0, _length);
		return genes;
	}
	
	@Override
	public DoubleChromosome mutate(final int index) {
		final DoubleChromosome chromosome = newInstance(_length);
		System.arraycopy(_genes, 0, chromosome._genes, 0, _length);
		
		final DoubleGene gene = _genes[index];
		final Random random = RandomRegistry.getRandom(); 
		double value = random.nextDouble()*
			(_max.doubleValue() - _min.doubleValue()) + _min.doubleValue();
		chromosome._genes[index] = gene.newInstance(value);
		
		return chromosome;
	}
	
	@Override
	public DoubleChromosome newChromosome(final DoubleGene[] genes) {
		DoubleChromosome chromosome = newInstance(_length);
		System.arraycopy(genes, 0, chromosome._genes, 0, _length);
		
		chromosome._min = genes[0]._min;
		chromosome._max = genes[0]._max;
		return chromosome;
	}

	@Override
	public DoubleChromosome newChromosome() {
		final DoubleChromosome chromosome = newInstance(_length);
		chromosome._min = _min;
		chromosome._max = _max;
		
		final Random random = RandomRegistry.getRandom(); 
		
		for (int i = 0; i < _length; ++i) {
			double value = random.nextDouble()*
				(_max.doubleValue() - _min.doubleValue()) + _min.doubleValue();
			
			chromosome._genes[i] = DoubleGene.valueOf(
				value, _min.doubleValue(), _max.doubleValue()
			);
		}
		
		return chromosome;
	}
	
	@Override
	public Text toText() {
		TextBuilder out = TextBuilder.newInstance();
		out.append("[");
		for (NumberGene<Float64> gene : this) {
			out.append(gene.toText());
		}
		out.append("]");
		return out.toText();
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
	
	private static final ObjectFactory<DoubleChromosome> 
	FACTORY = new ObjectFactory<DoubleChromosome>() {
		@Override protected DoubleChromosome create() {
			return new DoubleChromosome();
		}
	};
	
	static DoubleChromosome newInstance(final int length) {
		DoubleChromosome chromosome = FACTORY.object();
		if (chromosome._genes == null || chromosome._genes.length != length) {
			chromosome._genes = new DoubleGene[length];
			chromosome._length = length;
		}
		return chromosome;
	}
	
	public static DoubleChromosome valueOf(final double min, final double max) {
		return valueOf(Float64.valueOf(min), Float64.valueOf(max));
	}
	
	public static DoubleChromosome valueOf(final Float64 min, final Float64 max) {
		return valueOf(min, max, 1);
	}
	
	public static DoubleChromosome valueOf(double min, double max, int length) {
		return valueOf(Float64.valueOf(min), Float64.valueOf(max), length);
	}
	
	public static DoubleChromosome valueOf(final DoubleGene... genes) {
		DoubleChromosome chromosome = newInstance(genes.length);
		
		chromosome._min = genes[0]._max;
		chromosome._max = genes[0]._max;
		
		for (int i = 0; i < genes.length; ++i) {
			chromosome._genes[i] = genes[i];
		}
		
		 return chromosome;
	}
	
	/**
	 * Create a new random DoubleChromosome.
	 * 
	 * @param min the min value of the {@link DoubleGene}s.
	 * @param max the max value of the {@link DoubleGene}s.
	 * @param length the length of the chromosome.
	 * @return a new DoubleChromosome.
	 * @throws IllegalArgumentException if min is not less max.
	 */
	public static DoubleChromosome valueOf(final Float64 min, final Float64 max, final int length) {
		if (!min.isLessThan(max)) {
			throw new IllegalArgumentException(
				"Minumum must be less than maximim: " + min + " not less " + max
			);
		}
		DoubleChromosome chromosome = newInstance(length);
		chromosome._min = min;
		chromosome._max = max;
		
		for (int i = 0; i < length; ++i) {
			chromosome._genes[i] = DoubleGene.valueOf(min, max);
		}
		return chromosome;
	}
	
	static final XMLFormat<DoubleChromosome> 
	XML = new XMLFormat<DoubleChromosome>(DoubleChromosome.class) 
	{
		@Override
		public DoubleChromosome newInstance(
			final Class<DoubleChromosome> cls, final InputElement xml
		) throws XMLStreamException {
			final int length = xml.getAttribute("length", 0);
			final DoubleChromosome chromosome = DoubleChromosome.newInstance(length);
			for (int i = 0; i < length; ++i) {
				final DoubleGene gene = xml.getNext();
				chromosome._genes[i] = gene;
			}
			return chromosome;
		}
		@Override
		public void write(final DoubleChromosome chromosome, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute("length", chromosome._length);
			for (DoubleGene gene : chromosome) {
				xml.add(gene);
			}
		}
		@Override
		public void read(final InputElement element, final DoubleChromosome chromosome) 
			throws XMLStreamException 
		{
		}
	};
}





