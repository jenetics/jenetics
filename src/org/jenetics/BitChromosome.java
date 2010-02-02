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

import static org.jenetics.util.Validator.nonNegative;
import static org.jenetics.util.Validator.nonNull;
import static org.jenetics.util.Validator.checkProbability;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Random;

import javolution.context.ObjectFactory;
import javolution.text.Text;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.Array;
import org.jenetics.util.BitUtils;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Validator;
import org.jscience.mathematics.number.LargeInteger;
import org.jscience.mathematics.number.Number;

/**
 * BitChromosome.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: BitChromosome.java,v 1.24 2010-02-02 20:12:39 fwilhelm Exp $
 */
public class BitChromosome extends Number<LargeInteger> 
	implements Chromosome<BitGene>, ChromosomeFactory<BitGene>, XMLSerializable 
{
	private static final long serialVersionUID = 1347736183659208046L;
	
	
	/**
	 * The one's probability of the randomly generated Chromosome.
	 */
	protected double _p;
	
	/**
	 * The length of the chromosomes (number of bits).
	 */
	protected int _length;
	
	/**
	 * The boolean array which holds the {@link BitGene}s.
	 */
	protected byte[] _genes;
	
	/**
	 * Protected constructor, needed for the FACTORY.
	 */
	protected BitChromosome() {
	}

	private void rangeCheck(final int index) {
		assert(_length >= 0);
		if (index < 0 || index >= _length) {
			throw new IndexOutOfBoundsException(
				"Index: " + index + ", Length: " + _length
			);
		}
	}
	
	private void set(final int index, final boolean value) {
		BitUtils.setBit(_genes, index, value);
	}
	
	private boolean get(final int index) {
		return BitUtils.getBit(_genes, index);
	}
	
	@Override
	public BitGene getGene() {
		assert (_genes != null);
		assert (_genes.length > 0);
		return get(0) ? BitGene.TRUE : BitGene.FALSE;
	}
	
	@Override
	public BitGene getGene(final int index) {
		rangeCheck(index);
		assert(_genes != null);
		return get(index) ? BitGene.TRUE : BitGene.FALSE;
	}
	
	@Override
	public Array<BitGene> toArray() {
		final Array<BitGene> genes = new Array<BitGene>(_length);
		for (int i = 0; i < _length; ++i) {
			genes.set(i, BitUtils.getBit(_genes, i) ? BitGene.TRUE : BitGene.FALSE);
		}
		return genes;
	}

	@Override
	public int length() {
		return _length;
	}

	@Override
	public Iterator<BitGene> iterator() {
		return new Iterator<BitGene>() {
			private int _pos = 0;
			@Override public boolean hasNext() {
				return _pos < _length;
			}
			@Override public BitGene next()  {
				return get(_pos++) ? BitGene.TRUE : BitGene.FALSE;
			}
			@Override public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Return the long value this BitChromosome represents. 
	 * 
	 * @return Long value this BitChromosome represents.
	 */	 
	@Override
	public long longValue() {
		return toLargeInteger().longValue();
	}


	/**
	 * Return the double value this BitChromosome represents. 
	 * 
	 * @return Double value this BitChromosome represents.
	 */
	@Override
	public double doubleValue() {
		return longValue();
	}
	
	@Override
	public boolean isValid() {
		return true;
	}
	
	/**
	 * Return the LargeInteger value this BitChromosome represents. 
	 * 
	 * @return LargeInteger value this BitChromosome represents.
	 */
	public LargeInteger toLargeInteger() {
		return LargeInteger.valueOf(_genes, 0, _genes.length);
	}
	
    /**
     * Returns the two's-complement binary representation of this 
     * large integer. The output array is in <i>big-endian</i>
     * byte-order: the most significant byte is at the offset position.
     * 
     * <p>Note: This representation is consistent with <code>java.lang.BigInteger
     *          </code> byte array representation and can be used for conversion 
     *          between the two classes.</p>
     * 
     * @param  bytes the bytes to hold the binary representation 
     *         (two's-complement) of this large integer.
     * @return the number of bytes written.
     * @throws IndexOutOfBoundsException 
     *         if {@code bytes.length < (int)Math.ceil(length()/8.0)}  
     * @throws NullPointerException it the give array is {@code null}.
     */
    public int toByteArray(final byte[] bytes) {
    	if (bytes.length < _genes.length) {
    		throw new IndexOutOfBoundsException(); 
    	}
    	
    	System.arraycopy(_genes, 0, bytes, 0, _genes.length);
    	
    	return _genes.length;
    }
    
    /**
     * @return a byte array which represents this {@code BitChromosome}. The 
     *         length of the array is {@code (int)Math.ceil(length()/8.0)}.
     * @see #toByteArray(byte[])
     */
    public byte[] toByteArray() {
    	final byte[] data = new byte[_genes.length];
    	toByteArray(data);
    	return data;
    }
	
	/**
	 * Return the corresponding BitSet of this BitChromosome. 
	 * 
	 * @return The corresponding BitSet of this BitChromosome.
	 */
	public BitSet toBitSet() {
		final BitSet set = new BitSet(length());
		for (int i = 0, n = length(); i < n; ++i) {
			set.set(i, getGene(i).getBit());
		}
		return set;
	}
	
	/**
	 * Create a new BitChromosome with the same _length. The chromosome is
	 * randomized.
	 * 
	 * @throws NullPointerException if the given {@code genes} array is 
	 *         {@code null}.
	 */
	@Override
	public BitChromosome newInstance(final Array<BitGene> genes) {
		Validator.nonNull(genes, "Genes");
		
		final BitChromosome chromosome = BitChromosome.newInstance(genes.length(), _p);
		
		int ones = 0;
		for (int i = 0; i < genes.length(); ++i) {
			if (genes.get(i) == BitGene.TRUE) {
				++ones;
			}
			BitUtils.setBit(chromosome._genes, i, genes.get(i) == BitGene.TRUE);
		}
		chromosome._p = (double)ones/(double)genes.length();
		return chromosome;
	}
	
	/**
	 * Create a new, <em>random</em> chromosome.
	 */
	@Override
	public BitChromosome newInstance() {
		final Random random = RandomRegistry.getRandom();
		final BitChromosome chromosome = BitChromosome.newInstance(_length, _p);
		for (int i = 0; i < _length; ++i) {
			BitUtils.setBit(chromosome._genes, i, random.nextDouble() < _p);
		}
		return chromosome;
	}
	
	/**
	 * Return the BitChromosome as String. A TRUE is represented by a 1 and
	 * a FALSE by a 0.
	 * 
	 * @return String representation (containing only '1' and '0') of the
	 * 		BitChromosome.
	 */
	public String toCanonicalString() {
		final StringBuilder out = new StringBuilder(length());
		for (int i = 0; i < _length; ++i) {
			out.append(BitUtils.getBit(_genes, i) ? '1' : '0');
		}
		return out.toString();
	}
	
	@Override
	public int compareTo(final LargeInteger that) {
		return toLargeInteger().compareTo(that);
	}

	@Override
	public boolean isLargerThan(final LargeInteger that) {
		return toLargeInteger().isLargerThan(that);
	}

	public LargeInteger sqrt() {
		return toLargeInteger().sqrt();
	}
	
	@Override
	public LargeInteger plus(final LargeInteger that) {
		return toLargeInteger().plus(that);
	}

	@Override
	public LargeInteger opposite() {
		return toLargeInteger().opposite();
	}

	@Override
	public LargeInteger times(final LargeInteger that) {
		return toLargeInteger().times(that);
	}
	
	@Override
	public int hashCode() {
		return intValue();
	}
	
	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof BitChromosome)) {
			return false;
		}
		
		final BitChromosome c = (BitChromosome)o;
		boolean equals = length() == c.length();
		for (int i = 0, n = length(); equals && i < n; ++i) {
			equals = getGene(i).getBit() == c.getGene(i).getBit();
		}
		return equals;
	}

	@Override
	public Text toText() {
		return Text.valueOf(BitUtils.toString(toByteArray()));
	}
	
	@Override
	public BitChromosome copy() {
		BitChromosome chromosome = newInstance(_length, _p);
		System.arraycopy(_genes, 0, chromosome._genes, 0, chromosome._genes.length);
		return chromosome;
	}
		
	static final ObjectFactory<BitChromosome> 
	FACTORY = new ObjectFactory<BitChromosome>() {
		@Override protected BitChromosome create() {
			return new BitChromosome();
		}
	};
	
	static BitChromosome newInstance(final int length, final double p) {
		final BitChromosome chromosome = FACTORY.object();
		final int size = (int)Math.ceil(length/8.0);
		
		if (chromosome._genes == null || chromosome._genes.length != size) {
			chromosome._genes = new byte[size];
			chromosome._length = length;
			chromosome._p = p;
		}
		Arrays.fill(chromosome._genes, (byte)0);
		return chromosome;
	}
	
	/**
	 * Construct a new BitChromosome with the given _length. 
	 * 
	 * @param length Length of the BitChromosome, number of bits.
	 * @param p Probability of the TRUEs in the BitChromosome.
	 * @throws NegativeArraySizeException if the <code>length</code> is smaller
	 *         than one.
	 * @throws IllegalArgumentException if <code>p</code> is out of range.
	 */
	public static BitChromosome valueOf(final int length, final double p) {
		nonNegative(length);
		checkProbability(p);
		
		final Random random = RandomRegistry.getRandom();
		final BitChromosome chromosome = newInstance(length, p);
		for (int i = 0, n = chromosome.length(); i < n; ++i) {
			chromosome.set(i, random.nextDouble() < p);
		}
		return chromosome;
	}
	
	/**
	 * Constructing a new BitChromosome with the given _length. The TRUEs and
	 * FALSE in the {@code Chromosome} are equaly distributed.
	 * 
	 * @param length Length of the BitChromosome.
	 * @throws NegativeArraySizeException if the <code>_length</code> is smaller
	 *         than one.
	 */
	public static BitChromosome valueOf(final int length) {
		return valueOf(length, 0.5);
	}
	
	/**
	 * Constructing a new BitChromosome from a given BitSet.
	 * The BitSet is copied while construction. The length of the constructed
	 * BitChromosome will be <code>bitSet.length()</code> 
	 * (@see BitSet#length).
	 * 
	 * @param bits 
	 * @throws NullPointerException if the <code>bitSet</code> is 
	 *         <code>null</code>.
	 */
	public static BitChromosome valueOf(final BitSet bits) {
		return valueOf(bits.length(), bits);
	}
	
	/**
	 * @param length Length of the BitChromosome.
	 * @param bits
	 * @throws NegativeArraySizeException if the <code>length</code> is smaller
	 *         than one.
	 * @throws NullPointerException if the <code>bitSet</code> is 
	 *         <code>null</code>.
	 */
	public static BitChromosome valueOf(final int length, final BitSet bits) {
		nonNull(bits, "BitSet");
		
		BitChromosome chromosome = newInstance(length, 1);
		int ones = 0;
		for (int i = 0; i < length; ++i) {
			if (bits.get(i)) {
				++ones;
				BitUtils.setBit(chromosome._genes, i, true);
			} else {
				BitUtils.setBit(chromosome._genes, i, false);
			}
			
		}
		chromosome._p = (double)ones/(double)length;
		return chromosome;
	}
	
	public static BitChromosome valueOf(final LargeInteger value) {
		return valueOf(BitUtils.toByteArray(value));
	}
	
	public static BitChromosome valueOf(final byte[] value) {
		final BitChromosome chromosome = BitChromosome.valueOf(value.length*8);
		System.arraycopy(value, 0, chromosome._genes, 0, value.length);
		return chromosome;
	}
	
	/**
	 * Create a new {@code BitChromosome} from the given character sequence
	 * containing '0' and '1'.
	 * 
	 * @param value the input string.
	 * @return the new created {@code BitChromosome}.
	 * @throws NullPointerException if the {@code value} is {@code null}.
	 * @throws IllegalArgumentException if the length of the character sequence
	 *         is zero or contains other characters than '0' or '1'.
	 */
	public static BitChromosome valueOf(final CharSequence value) {
		Validator.nonNull(value, "Input");
		if (value.length() == 0) {
			throw new IllegalArgumentException("Length must greater than zero.");
		}
		
		final BitChromosome chromosome = BitChromosome.valueOf(value.length());
		
		int ones = 0;
		for (int i = 0, n = value.length(); i < n; ++i) {
			final char c = value.charAt(i);
			if (c == '1') {
				++ones;
				BitUtils.setBit(chromosome._genes, i, true);
			} else if (c == '0') {
				BitUtils.setBit(chromosome._genes, i, false);
			} else {
				throw new IllegalArgumentException(String.format(
					"Illegal character '%s' at position %d", c, i
				));
			}
		}
		
		chromosome._p = (double)ones/(double)value.length();
		return chromosome;
	}
	
	
	
	static final XMLFormat<BitChromosome> 
	XML = new XMLFormat<BitChromosome>(BitChromosome.class) 
	{
		private static final String LENGTH = "length";
		private static final String PROBABILITY = "probability";
		
		@Override
		public BitChromosome newInstance(
			final Class<BitChromosome> cls, final InputElement xml
		) 
			throws XMLStreamException 
		{
			final int length = xml.getAttribute(LENGTH, 1);
			final double probability = xml.getAttribute(PROBABILITY, 0.5);
			final byte[] data = BitUtils.toByteArray(xml.getText().toString());
			final BitChromosome chromosome = BitChromosome.valueOf(data);
			chromosome._p = probability;
			chromosome._length = length;
			return chromosome;
		} 
		@Override
		public void write(final BitChromosome chromosome, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute(LENGTH, chromosome._length);
			xml.setAttribute(PROBABILITY, chromosome._p);
			xml.addText(BitUtils.toString(chromosome.toByteArray()));
		}
		@Override
		public void read(final InputElement element, final BitChromosome gene) {
		}
	};	
}

