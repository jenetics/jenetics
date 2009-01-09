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

import org.jenetics.util.Array;

import javolution.text.CharArray;
import javolution.text.Text;
import javolution.text.TextBuilder;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: CharacterChromosome.java,v 1.8 2009-01-09 21:29:40 fwilhelm Exp $
 */
public class CharacterChromosome extends AbstractChromosome<CharacterGene>
	implements ChromosomeFactory<CharacterGene>, CharSequence, XMLSerializable
{	
	private static final long serialVersionUID = 8213347401351340289L;

	/**
	 * Create a new chromosome
	 * 
	 * @param length the {@code length} of the new chromosome.
	 * @throws IllegalArgumentException if the {@code length} is smaller than 
	 *         one.
	 */
	public CharacterChromosome(final int length) {
		super(length);
		for (int i = 0; i < length(); ++i) {
			_genes.set(i, CharacterGene.valueOf());
		}
	}
	
	/**
	 * Create a new chromosome from the given {@code genes} array. The genes 
	 * array is copied, so changes to the given genes array doesn't effect the 
	 * genes of this chromosome.
	 * 
	 * @param genes the genes that form the chromosome.
	 * @throws NullPointerException if the given gene array is {@code null}.
	 * @throws new IllegalArgumentException if the length of the gene array is
	 *         smaller than one.
	 */
	public CharacterChromosome(final Array<CharacterGene> genes) {
		super(genes);
	}
	
	@Override
	public Class<CharacterGene> getType() {
		return CharacterGene.class;
	}

	@Override
	public CharacterChromosome mutate(final int index) {
		final CharacterChromosome chromosome = new CharacterChromosome(_genes);
		chromosome._genes.set(index, CharacterGene.valueOf());
		return chromosome;
	}

	@Override
	public CharacterChromosome newChromosome(final Array<CharacterGene> genes) {
		return new CharacterChromosome(genes);
	}
	
	@Override
	public CharacterChromosome newChromosome() {
		final CharacterChromosome chromosome = new CharacterChromosome(length());
		for (int i = 0; i < length(); ++i) {
			chromosome._genes.set(i, CharacterGene.valueOf());
		}
		return chromosome;
	}
	
	@Override
	public Text toText() {
		TextBuilder out = TextBuilder.newInstance();
		out.append("[");
		for (CharacterGene gene : this) {
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
		if (!(obj instanceof CharacterChromosome)) {
			return false;
		}
		
		return super.equals(obj); 
	}
	
	
	static final XMLFormat<CharacterChromosome> 
	XML = new XMLFormat<CharacterChromosome>(CharacterChromosome.class) {
		@Override
		public CharacterChromosome newInstance(final Class<CharacterChromosome> cls, final InputElement xml) 
			throws XMLStreamException 
		{
			final int length = xml.getAttribute("length", 0);
			final CharacterChromosome chromosome = new CharacterChromosome(length);
			final CharArray values = xml.getText();
			for (int i = 0; i < length; ++i) {
				chromosome._genes.set(i, CharacterGene.valueOf(values.charAt(i)));
			}
			return chromosome;
		}
		@Override
		public void write(final CharacterChromosome chromosome, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute("length", chromosome.length());
			final StringBuilder out = new StringBuilder(chromosome.length());
			for (CharacterGene gene : chromosome) {
				out.append(gene.getAllele().charValue());
			}
			xml.addText(out.toString());
		}
		@Override
		public void read(final InputElement element, final CharacterChromosome chromosome) {
		}
		
	};

	@Override
	public char charAt(int index) {
		return getGene(index).getAllele();
	}

	@Override
	public CharacterChromosome subSequence(final int start, final int end) {
		return new CharacterChromosome(_genes.subArray(start, end).copy());
	}

}



