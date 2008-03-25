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

import javolution.context.ObjectFactory;
import javolution.xml.XMLSerializable;

/**
 * The mutable methods of the {@link AbstractChromosome} has been overridden so 
 * that no invalid permutation will be created.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: PermutationChromosome.java,v 1.1 2008-03-25 18:31:55 fwilhelm Exp $
 */
public class PermutationChromosome extends AbstractChromosome<IntegerGene> 
	implements ChromosomeFactory<IntegerGene>, XMLSerializable
{
	private static final long serialVersionUID = 3504723054127043564L;

	protected PermutationChromosome() {
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
		PermutationChromosome chromosome = newInstance(_length);
		System.arraycopy(_genes, 0, chromosome._genes, 0, _length);
		
		final Random random = RandomRegistry.getRandom();
		final int otherIndex = random.nextInt(_length);
		IntegerGene temp = chromosome._genes[index];
		chromosome._genes[index] = chromosome._genes[otherIndex];
		chromosome._genes[otherIndex] = temp;
		
		return chromosome;
	}
	
	/**
	 * Check if this chromosome represents still a valid permutation. 
	 */
	@Override
	public boolean isValid() {
		byte[] check = new byte[_length/8 + 1];
		Arrays.fill(check, (byte)0);
		
		boolean valid = true;
		for (int i = 0; i < _length && valid; ++i) {
			final int value = _genes[i].intValue();
			if (value >= 0 && value < _length) {
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
		return valueOf(_length, true);
	}
	
	@Override
	public PermutationChromosome newChromosome(final IntegerGene[] genes) {
		PermutationChromosome chromosome = newInstance(_length);
		System.arraycopy(genes, 0, chromosome._genes, 0, _length);
		return chromosome;
	}
	
	/**
	 * Create an exact copy of this chromosome.
	 * 
	 * @return the copied chromosome.
	 */
	public PermutationChromosome copy() {
		PermutationChromosome c = newInstance(_length);
		System.arraycopy(_genes, 0, c._genes, 0, c._length);
		return c;
	}
	
	@Override
	public PermutationChromosome clone() {
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
		if (!(obj instanceof PermutationChromosome)) {
			return false;
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append(_genes[0].getAllele().intValue());
		for (int i = 1; i < _length; ++i) {
			out.append("|").append(_genes[i].getAllele().intValue());
		}
		return out.toString();
	}
	
	static final ObjectFactory<PermutationChromosome> 
	FACTORY = new ObjectFactory<PermutationChromosome>() {
		@Override protected PermutationChromosome create() {
			return new PermutationChromosome();
		}
	};
	
	static PermutationChromosome newInstance(final int length) {
		PermutationChromosome chromosome = FACTORY.object();
		if (chromosome._genes == null || chromosome._genes.length != length) {
			chromosome._genes = new IntegerGene[length];
			chromosome._length = length;
		}
		return chromosome;
	}
	
	/**
	 * Create a new PermutationChromosome from the given {@code values}.
	 * 
	 * @param values the values of the newly created PermutationChromosome.
	 * @return the newly create PermutationChromosome.
	 * @throws NullPointerException it the given {@code values} are {@code null}.
	 * @throws IllegalArgumentException if
	 *          <ul>
	 *             <li>the {@code values} array contains duplicate values or</li>
	 *             <li>one of the array value is smaller than zero or</li>
	 *             <li>one of the array value is greater than {@code values.length - 1} or</li>
	 *             <li>the array length is smaller than 1</li>
	 *          </ul>
	 */
	public static PermutationChromosome valueOf(final int[] values) {
		//Check the input.
		Checker.checkNull(values, "Values");
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
		
		PermutationChromosome chromosome = newInstance(values.length);
		for (int i = 0; i < values.length; ++i) {
			chromosome._genes[i] = IntegerGene.valueOf(values[i], 0, values.length - 1);
		}
		return chromosome;
	}
	
	/**
	 * Create a new randomly created permutation with the length {@code length}.
	 * 
	 * @param length the length of the chromosome.
	 * @param randomize if true, the chromosome is randomized, otherwise the
	 *        values of the chromosome are in ascending order from 0 to 
	 *        {@code length - 1}
	 * @return the newly created chromosome.
	 * @throws IllegalArgumentException if the given {@code length} is smaller than 1.
	 */
	public static PermutationChromosome valueOf(final int length, final boolean randomize) {
		if (length < 1) {
			throw new IllegalArgumentException("Length must be greater than 1, but was " + length);
		}
		
		PermutationChromosome chromosome = newInstance(length);
		
		final Random random = RandomRegistry.getRandom();
		if (randomize) {
			//Permutation algorithm from D. Knuth TAOCP, Seminumerical Algorithms, 
			//Third edition, page 145, Algorith P (Shuffling).
			for (int j = 0; j < length; ++j) {
				final int i = random.nextInt(j + 1);
				chromosome._genes[j] = chromosome._genes[i];
				chromosome._genes[i] = IntegerGene.valueOf(j, 0, length - 1);
			}
		} else {
			for (int i = 0; i < length; ++i) {
				chromosome._genes[i] = IntegerGene.valueOf(i, 0, length - 1);
			}
		}
		
		return chromosome;
	}
	
	/**
	 * Create a new randomly created permutation with the length {@code length}.
	 * 
	 * @param length the length of the chromosome.
	 * @return the newly created chromosome.
	 * @throws IllegalArgumentException if the given {@code length} is smaller than 1.
	 */
	public static PermutationChromosome valueOf(final int length) {
		return valueOf(length, false);
	}
	
}




