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

import static org.jenetics.util.ObjectUtils.eq;
import static org.jenetics.util.ObjectUtils.hashCodeOf;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javolution.text.CharArray;
import javolution.text.Text;
import javolution.text.TextBuilder;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.Array;
import org.jenetics.util.CharSet;
import org.jenetics.util.Converter;
import org.jenetics.util.ISeq;

/**
 * CharacterChromosome which represents character sequences.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class CharacterChromosome 
	extends 
		AbstractChromosome<CharacterGene>
	implements 
		CharSequence, 
		XMLSerializable
{	
	private static final long serialVersionUID = 1L;

	private transient CharSet _validCharacters;
	
	/**
	 * Create a new chromosome with the {@link CharacterGene#DEFAULT_CHARACTERS}
	 * char set as valid characters.
	 * 
	 * @param length the {@code length} of the new chromosome.
	 * @throws IllegalArgumentException if the {@code length} is smaller than 
	 * 		  one.
	 */
	public CharacterChromosome(final int length) {
		super(
			new Array<CharacterGene>(length).fill(
					CharacterGene.valueOf(CharacterGene.DEFAULT_CHARACTERS).asFactory()
				).seal()
		);
		_validCharacters = CharacterGene.DEFAULT_CHARACTERS;
	}
	
	/**
	 * Create a new chromosome with the {@code validCharacters} char set as 
	 * valid characters.
	 * 
	 * @param validCharacters the valid characters for this chromosome.
	 * @param length the length of the new chromosome.
	 * @throws NullPointerException if the {@code validCharacters} is 
	 * 		  {@code null}.
	 * @throws IllegalArgumentException if the {@code length} is smaller than 
	 * 		  one.
	 */
	public CharacterChromosome(final CharSet validCharacters, final int length) {
		super(
			new Array<CharacterGene>(length).fill(
					CharacterGene.valueOf(validCharacters).asFactory()
				).seal()
		);
		_validCharacters = validCharacters;
	}
	
	/**
	 * Create a new chromosome from the given {@code genes} array. The genes 
	 * array is copied, so changes to the given genes array doesn't effect the 
	 * genes of this chromosome.
	 * 
	 * @param genes the genes that form the chromosome.
	 * @throws NullPointerException if the given gene array is {@code null}.
	 * @throws IllegalArgumentException if the length of the gene array is
	 * 		  smaller than one.
	 */
	public CharacterChromosome(final ISeq<CharacterGene> genes) {
		super(genes);
		_validCharacters = genes.get(0).getValidCharacters();
	}

	@Override
	public char charAt(final int index) {
		return getGene(index).getAllele();
	}

	@Override
	public CharacterChromosome subSequence(final int start, final int end) {
		return new CharacterChromosome(
					(ISeq<CharacterGene>)_genes.subSeq(start, end)
				);
	}
	
	/**
	 * @throws NullPointerException if the given gene array is {@code null}.
	 */
	@Override
	public CharacterChromosome newInstance(final ISeq<CharacterGene> genes) {
		return new CharacterChromosome(genes);
	}
	
	/**
	 * Create a new, <em>random</em> chromosome.
	 */
	@Override
	public CharacterChromosome newInstance() {
		return new CharacterChromosome(_validCharacters, length());
	}
	
	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).
				and(super.hashCode()).
				and(_validCharacters).value();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		final CharacterChromosome cc = (CharacterChromosome)obj;
		return super.equals(obj) && eq(_validCharacters, cc._validCharacters);	
	}
	
	@Override
	public String toString() {
		final StringBuilder out = new StringBuilder();
		for (CharacterGene gene : this) {
			out.append(gene.toString());
		}
		return out.toString();
	}
	
	@Override
	public Text toText() {
		final TextBuilder out = TextBuilder.newInstance();
		for (CharacterGene gene : this) {
			out.append(gene.toText());
		}
		return out.toText();
	}
	
	/* *************************************************************************
	 *  Property access methods
	 * ************************************************************************/
	
	/**
	 * Return a {@link Converter} which returns the gene array from this
	 * {@link Chromosome}.
	 */
	public static final Converter<AbstractChromosome<CharacterGene>, ISeq<CharacterGene>> 
		Genes = AbstractChromosome.genes();
	
	/**
	 * Return a {@link Converter} which returns the first {@link Gene} from this
	 * {@link Chromosome}.
	 */
	public static final Converter<Chromosome<CharacterGene>, CharacterGene> 
		Gene = AbstractChromosome.gene();
	
	/**
	 * Return a {@link Converter} which returns the {@link Gene} with the given
	 * {@code index} from this {@link Chromosome}.
	 */
	public static final Converter<Chromosome<CharacterGene>, CharacterGene> 
	Gene(final int index) 
	{
		return AbstractChromosome.gene(index);
	}
	
	static final XMLFormat<CharacterChromosome> 
	XML = new XMLFormat<CharacterChromosome>(CharacterChromosome.class) 
	{
		private static final String LENGTH = "length";
		private static final String VALID_CHARS = "valid-characters";
		
		@Override
		public CharacterChromosome newInstance(
			final Class<CharacterChromosome> cls, final InputElement xml
		) 
			throws XMLStreamException 
		{
			final int length = xml.getAttribute(LENGTH, 0);
			final CharSet validCharacters = new CharSet(xml.getAttribute(
					VALID_CHARS, CharacterGene.DEFAULT_CHARACTERS.toString()
				));
			
			final Array<CharacterGene> array = new Array<CharacterGene>(length);
			final CharArray values = xml.getText();
			for (int i = 0; i < length; ++i) {
				array.set(i, CharacterGene.valueOf(values.charAt(i), validCharacters));
			}
			return new CharacterChromosome(array.seal());
		}
		@Override
		public void write(final CharacterChromosome chromosome, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute(LENGTH, chromosome.length());
			xml.setAttribute(VALID_CHARS, chromosome._validCharacters.toString());
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

	private void writeObject(final ObjectOutputStream out)
		throws IOException 
	{
		out.defaultWriteObject();
	
		out.writeInt(length());
		out.writeObject(_validCharacters);
		
		for (CharacterGene gene : _genes) {
			out.writeChar(gene.getAllele().charValue());
		}
	}
	
	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException 
	{
		in.defaultReadObject();
	
		final int length = in.readInt();
		_validCharacters = (CharSet)in.readObject();
		
		final Array<CharacterGene> genes = new Array<CharacterGene>(length);
		for (int i = 0; i < length; ++i) {
			genes.set(i, CharacterGene.valueOf(Character.valueOf(in.readChar()), _validCharacters));
		}
		
		_genes = genes.seal();
	}
	
}



