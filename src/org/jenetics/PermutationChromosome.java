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

import java.util.Arrays;
import java.util.Random;

import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.Array;
import org.jenetics.util.BitUtils;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Validator;
import org.jscience.mathematics.number.Integer64;

/**
 * The mutable methods of the {@link AbstractChromosome} has been overridden so 
 * that no invalid permutation will be created.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: PermutationChromosome.java,v 1.6 2009-01-16 23:16:35 fwilhelm Exp $
 */
public class PermutationChromosome extends AbstractChromosome<IntegerGene> 
	implements ChromosomeFactory<IntegerGene>, XMLSerializable
{
	private static final long serialVersionUID = 3504723054127043564L;

	protected PermutationChromosome(final Array<IntegerGene> genes) {
		super(genes);
	}
	
	/**
	 * Create a new PermutationChromosome from the given {@code values}.
	 * 
	 * @param values the values of the newly created PermutationChromosome.
	 * @throws NullPointerException it the given {@code values} are {@code null}.
	 * @throws IllegalArgumentException if
	 *          <ul>
	 *             <li>the {@code values} array contains duplicate values or</li>
	 *             <li>one of the array value is smaller than zero or</li>
	 *             <li>one of the array value is greater than {@code values.length - 1} or</li>
	 *             <li>the array length is smaller than 1</li>
	 *          </ul>
	 */
	public PermutationChromosome(final int[] values) {
		super(values.length);
		
		//Check the input.
		Validator.notNull(values, "Values");
		if (values.length < 1) {
			throw new IllegalArgumentException("Array must contain at least one value.");
		}
		
		byte[] check = new byte[values.length/8 + 1];
		Arrays.fill(check, (byte)0);
		for (int i = 0; i < values.length; ++i) {
			if (values[i] < 0) {
				throw new IllegalArgumentException(
					"Value at position " + i + " is smaller than zero: " + values[i]
				);
			}
			if (values[i] > values.length - 1) {
				throw new IllegalArgumentException(
					"Value at position " + i + " is greater than " + 
					(values.length - 1) + ": "  + values[i]
				);
			}
			
			if (BitUtils.getBit(check, values[i])) {
				throw new IllegalArgumentException("Value " + values[i] + " is duplicate.");
			} else {
				BitUtils.setBit(check, values[i], true);
			}
		}
		
		for (int i = 0; i < values.length; ++i) {
			_genes.set(i, IntegerGene.valueOf(values[i], 0, values.length - 1));
		}
	}
	
	/**
	 * Create a new randomly created permutation with the length {@code length}.
	 * 
	 * @param length the length of the chromosome.
	 * @param randomize if true, the chromosome is randomized, otherwise the
	 *        values of the chromosome are in ascending order from 0 to 
	 *        {@code length - 1}
	 * @throws IllegalArgumentException if the given {@code length} is smaller than 1.
	 */
	public PermutationChromosome(final int length, final boolean randomize) {
		super(length);
		if (length < 1) {
			throw new IllegalArgumentException("Length must be greater than 1, but was " + length);
		}
		
		final Random random = RandomRegistry.getRandom();
		if (randomize) {
			//Permutation algorithm from D. Knuth TAOCP, Seminumerical Algorithms, 
			//Third edition, page 145, Algorith P (Shuffling).
			for (int j = 0; j < length; ++j) {
				final int i = random.nextInt(j + 1);
				_genes.set(j, _genes.get(i));
				_genes.set(i, IntegerGene.valueOf(j, 0, length - 1));
			}
		} else {
			for (int i = 0; i < length; ++i) {
				_genes.set(i, IntegerGene.valueOf(i, 0, length - 1));
			}
		}
	}
	
	/**
	 * Create a new randomly created permutation with the length {@code length}.
	 * 
	 * @param length the length of the chromosome.
	 * @throws IllegalArgumentException if the given {@code length} is smaller than 1.
	 */
	public PermutationChromosome(final int length) {
		this(length, false);
	}
	
	@Override
	public Class<IntegerGene> getType() {
		return IntegerGene.class;
	}

	/**
	 * Mutates the given gene. To keep this chromosome in a valid state the mutation is
	 * performed by a swap with a second, randomly choosen, gene of this chromosome. This
	 * mutation method is equivalent to:
	 * 
	 * [code]
	 *     final Random random = ...;
	 *     this.swap(index, random.nextInt(this.length()));
	 * [/code]
	 */
	@Override
	public PermutationChromosome mutate(final int index) {
		final PermutationChromosome chromosome = new PermutationChromosome(_genes);
		final Random random = RandomRegistry.getRandom();
		final int otherIndex = random.nextInt(length());
		final IntegerGene temp = chromosome._genes.get(index);
		chromosome._genes.set(index, chromosome._genes.get(otherIndex));
		chromosome._genes.set(otherIndex, temp);
		
		return chromosome;
	}
	
	/**
	 * Check if this chromosome represents still a valid permutation. 
	 */
	@Override
	public boolean isValid() {
		byte[] check = new byte[length()/8 + 1];
		Arrays.fill(check, (byte)0);
		
		boolean valid = true;
		for (int i = 0; i < length() && valid; ++i) {
			final int value = _genes.get(i).intValue();
			if (value >= 0 && value < length()) {
				if (BitUtils.getBit(check, value)) {
					valid = false;
				} else {
					BitUtils.setBit(check, value, true);
				}
			} else {
				valid = false;
			}
		}
		return valid;
	}

	@Override
	public PermutationChromosome newChromosome() {
		return new PermutationChromosome(length(), true);
	}
	
	@Override
	public PermutationChromosome newChromosome(final Array<IntegerGene> genes) {
		return new PermutationChromosome(genes);
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
		if (!(obj instanceof PermutationChromosome)) {
			return false;
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append(_genes.get(0).getAllele().intValue());
		for (int i = 1; i < length(); ++i) {
			out.append("|").append(_genes.get(i).getAllele().intValue());
		}
		return out.toString();
	}
	
	static final XMLFormat<PermutationChromosome> 
	XML = new XMLFormat<PermutationChromosome>(PermutationChromosome.class) {
		@Override
		public PermutationChromosome newInstance(
			final Class<PermutationChromosome> cls, final InputElement xml
		) throws XMLStreamException 
		{
			final int length = xml.getAttribute("length", 0);
			final int min = xml.getAttribute("min", 0);
			final int max = xml.getAttribute("max", length);
			final Array<IntegerGene> genes = Array.newInstance(length);
			
			for (int i = 0; i < length; ++i) {
				final Integer64 value = xml.getNext();
				genes.set(i, IntegerGene.valueOf(value.longValue(), min, max));
			}
			return new PermutationChromosome(genes);
		}
		@Override
		public void write(final PermutationChromosome chromosome, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute("length", chromosome.length());
			xml.setAttribute("min", 0);
			xml.setAttribute("max", chromosome.length() - 1);
			for (IntegerGene gene : chromosome) {
				xml.add(gene.getAllele());
			}
		}
		@Override
		public void read(
			final InputElement element, final PermutationChromosome chromosome
		) 
			throws XMLStreamException 
		{
		}
	};
	
}




