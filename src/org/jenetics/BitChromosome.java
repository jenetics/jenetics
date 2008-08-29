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

import static org.jenetics.util.Validator.checkChromosomeLength;
import static org.jenetics.util.Validator.notNull;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Random;

import javolution.context.ObjectFactory;
import javolution.text.Text;
import javolution.text.TextBuilder;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.Array;
import org.jenetics.util.BitUtils;
import org.jenetics.util.Probability;
import org.jscience.mathematics.number.LargeInteger;
import org.jscience.mathematics.number.Number;

/**
 * BitChromosome.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: BitChromosome.java,v 1.8 2008-08-29 21:18:16 fwilhelm Exp $
 */
public class BitChromosome extends Number<LargeInteger> 
	implements Chromosome<BitGene>, ChromosomeFactory<BitGene>, XMLSerializable 
{
	private static final long serialVersionUID = 1347736183659208046L;
	
	
	/**
	 * The one's probability of the randomly generated Chromosome.
	 */
	protected Probability _p;
	
	/**
	 * The length of the chromosomes (in bit).
	 */
	protected int _length;
	
	/**
	 * The boolean array which holds the {@link BitGene}s.
	 */
	protected boolean[] _genes;
	
	/**
	 * Protected constructor, needed for the FACTORY.
	 */
	protected BitChromosome() {
	}
	
	@Override
	public Class<BitGene> getType() {
		return BitGene.class;
	}

	private void rangeCheck(final int index) {
		assert(_length >= 0);
		if (index < 0 || index >= _length) {
			throw new IndexOutOfBoundsException(
				"Index: " + index + ", Length: " + _length
			);
		}
	}
	
	@Override
	public BitGene getGene() {
		assert (_genes != null);
		assert (_genes.length > 0);
		return _genes[0] ? BitGene.TRUE : BitGene.FALSE;
	}
	
	@Override
	public BitGene getGene(final int index) {
		rangeCheck(index);
		assert(_genes != null);
		return _genes[index] ? BitGene.TRUE : BitGene.FALSE;
	}
	
	@Override
	public Array<BitGene> getGenes() {
		final Array<BitGene> genes = Array.newInstance(_length);
		for (int i = 0; i < _length; ++i) {
			genes.set(i, _genes[i] ? BitGene.TRUE : BitGene.FALSE);
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
			public boolean hasNext() {
				return _pos < _genes.length;
			}
			public BitGene next()  {
				return _genes[_pos++] ? BitGene.TRUE : BitGene.FALSE;
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	@Override
	public BitChromosome mutate(final int index) {
		return flip(index);
	}
	
	/**
	 * Flips the bit at the given position.
	 * 
	 * @param index The position of the gene to flip.
	 * @throws IndexOutOfBoundsException if the index is out of range 
	 * 		(index < 0 || index >= _length()).
	 */
	public BitChromosome flip(final int index) {
		rangeCheck(index);
		
		BitChromosome chromosome = BitChromosome.newInstance(_length, _p);
		System.arraycopy(_genes, 0, chromosome._genes, 0, _length);
		chromosome._genes[index] = !chromosome._genes[index];
		return chromosome;
	}
	
	//TODO: This are fast but wrong conversion methods.
//	/**
//	 * Answer a binary integer constructed from a sequence of bits.
//	 *
//	 * @param first the index of the first bit
//	 * @param last the index of the last bit
//	 * @return the corresponding integer
//	 */
//	public int intValue(final int first, final int last) {
//		int result = 0 & 0x0;
//		for (int i = first; i <= last; ++i) {
//			result <<= 1;
//			if (_genes[i]) {
//				result = result | 0x0001;
//			}
//		}
//		return result;
//	}
//
//	
//	/**
//	 * Return the integer value this BitChromosome represents. 
//	 * 
//	 * @param first First index of the BitChromosome.
//	 * @param last Last index of the BitChromosome.
//	 * @return Integer value of the defined part of the BitChromosome.
//	 */
//	public long longValue(final int first, final int last) {
//		long result = 0 & 0x0;
//		for (int i = first; i <= last; ++i) {
//			result <<= 1;
//			if (_genes[i]) {
//				result = result | 0x0001;
//			}
//		}
//		return result;
//	}

	/**
	 * //TODO: this can be done faster.
	 * 
	 * Return the long value this BitChromosome represents. 
	 * 
	 * @return Long value this BitChromosome represents.
	 */	 
	@Override
	public long longValue() {
		//return longValue(0, _length - 1);
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
		final byte[] data = new byte[(int)Math.ceil(length()/8.0)];
		toByteArray(data);
		return LargeInteger.valueOf(data, 0, data.length);
	}
	
    /**
     * Returns the two's-complement binary representation of this 
     * large integer. The output array is in <i>big-endian</i>
     * byte-order: the most significant byte is at the offset position.
     * 
     * <p>Note: This representation is consitent with <code>java.lang.BigInteger
     *          </code> byte array representation and can be used for conversion 
     *          between the two classes.</p>
     * 
     * @param  bytes the bytes to hold the binary representation 
     *         (two's-complement) of this large integer.
     * @return the number of bytes written.
     * @throws IndexOutOfBoundsException 
     *         if {@code bytes.length < (int)Math.ceil(length()/8.0)}  
     */
    public int toByteArray(byte[] bytes) {
    	final int bytesLength = (int)Math.ceil(length()/8.0);
    	if (bytes.length < bytesLength) {
    		throw new IndexOutOfBoundsException(); 
    	}

    	Arrays.fill(bytes, 0, bytes.length, (byte)0);
    	for (int i = 0; i < _length; ++i) {
    		BitUtils.setBit(
    			bytes, i, getGene(i).booleanValue()
    		);
    	}
    	
    	return bytesLength;
    }
    
    /**
     * @return a byte array which represents this {@code BitChromosome}.
     * @see #toByteArray(byte[])
     */
    public byte[] toByteArray() {
    	final int bytesLength = (int)Math.ceil(length()/8.0);
    	final byte[] data = new byte[bytesLength];
    	final int length = toByteArray(data);
    	assert (length == data.length);
    	return data;
    }
	
	/**
	 * Return the corresponding BitSet of this BitChromosome. 
	 * 
	 * @return The corresponding BitSet of this BitChromosome.
	 */
	public BitSet toBitSet() {
		BitSet set = new BitSet(length());
		for (int i = 0, n = length(); i < n; ++i) {
			set.set(i, getGene(i).getBit());
		}
		return set;
	}
	
	/**
	 * Create a new BitChromosome with the same _length. The chromosome is
	 * randomized.
	 */
	@Override
	public BitChromosome newChromosome(final Array<BitGene> genes) {
		BitChromosome chromosome = BitChromosome.newInstance(genes.length(), _p);
		for (int i = 0; i < genes.length(); ++i) {
			chromosome._genes[i] = genes.get(i).booleanValue();
		}
		return chromosome;
	}
	
	@Override
	public BitChromosome newChromosome() {
		final Random random = RandomRegistry.getRandom();
		BitChromosome chromosome = BitChromosome.newInstance(_length, _p);
		for (int i = 0; i < _length; ++i) {
			chromosome._genes[i] = random.nextDouble() < _p.doubleValue();
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
		StringBuilder out = new StringBuilder(length());
		for (int i = 0; i < _length; ++i) {
			out.append(_genes[i] ? '1' : '0');
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
		TextBuilder out = TextBuilder.newInstance();
		out.append("[");
		for (int i = 1; i < length(); ++i) {
			out.append(_genes[i] ? '1' : '0');
		}
		out.append("]");
		return out.toText();
	}
	
	@Override
	public BitChromosome copy() {
		BitChromosome chromosome = newInstance(_length, _p);
		System.arraycopy(_genes, 0, chromosome._genes, 0, _length);
		return chromosome;
	}
		
	static final ObjectFactory<BitChromosome> 
	FACTORY = new ObjectFactory<BitChromosome>() {
		@Override protected BitChromosome create() {
			return new BitChromosome();
		}
	};
	
	static BitChromosome newInstance(final int length, final Probability p) {
		BitChromosome chromosome = FACTORY.object();
		if (chromosome._genes == null || chromosome._genes.length != length) {
			chromosome._genes = new boolean[length];
			chromosome._length = length;
			chromosome._p = p;
		}
		return chromosome;
	}
	
	/**
	 * Construct a new BitChromosome with the given _length. 
	 * 
	 * @param length Length of the BitChromosome.
	 * @param p Probability of the TRUEs in the BitChromosome.
	 * @throws NegativeArraySizeException if the <code>length</code> is smaller
	 *         than one.
	 * @throws NullPointerException if <code>p</code> is <code>null</code>.
	 */
	public static BitChromosome valueOf(final int length, final Probability p) {
		checkChromosomeLength(length);
		notNull(p, "Probability");
		
		final Random random = RandomRegistry.getRandom();
		BitChromosome chromosome = newInstance(length, p);
		for (int i = 0, n = chromosome._length; i < n; ++i) {
			chromosome._genes[i] = random.nextDouble() < p.doubleValue();
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
		return valueOf(length, Probability.valueOf(0.5));
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
		notNull(bits, "BitSet");
		
		BitChromosome chromosome = newInstance(length, null);
		int ones = 0;
		for (int i = 0; i < length; ++i) {
			chromosome._genes[i] = bits.get(i);
			if (chromosome._genes[i]) {
				++ones;
			}
		}
		chromosome._p = Probability.valueOf((double)ones/(double)length);
		
		return chromosome;
	}
	
	public static BitChromosome valueOf(final LargeInteger value) {
		return valueOf(BitUtils.toByteArray(value));
	}
	
	public static BitChromosome valueOf(final byte[] value) {
		final int bitLength = value.length*8;
		BitChromosome chromosome = BitChromosome.valueOf(bitLength);
    	for (int i = 0; i < bitLength; ++i) {
    		chromosome._genes[i] = BitUtils.getBit(value, i);
    	}
		return chromosome;
	}
	
	
	
	static final XMLFormat<BitChromosome> 
	XML = new XMLFormat<BitChromosome>(BitChromosome.class) {
		@Override
		public BitChromosome newInstance(final Class<BitChromosome> cls, final InputElement xml) 
			throws XMLStreamException 
		{
			final double probability = xml.getAttribute("probability", 0.5);
			final byte[] data = BitUtils.toByteArray(xml.getText().toString());
			final BitChromosome chromosome = BitChromosome.valueOf(data);
			chromosome._p = Probability.valueOf(probability);
			return chromosome;
		} 
		@Override
		public void write(final BitChromosome chromosome, final OutputElement xml) 
			throws XMLStreamException 
		{
			xml.setAttribute("probability", chromosome._p.doubleValue());
			xml.addText(BitUtils.toString(chromosome.toByteArray()));
		}
		@Override
		public void read(final InputElement element, final BitChromosome gene) {
		}
	};	
}

