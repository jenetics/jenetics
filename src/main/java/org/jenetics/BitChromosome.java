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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 * 
 */
package org.jenetics;

import static org.jenetics.util.object.checkProbability;
import static org.jenetics.util.object.hashCodeOf;
import static org.jenetics.util.object.nonNegative;
import static org.jenetics.util.object.nonNull;
import static org.jenetics.util.object.str;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Random;

import javolution.text.Text;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.number.LargeInteger;
import org.jscience.mathematics.number.Number;

import org.jenetics.util.Array;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.bit;

/**
 * Implementation of the <i>classical</i> BitChromosome.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class BitChromosome extends Number<BitChromosome> 
	implements 
		Chromosome<BitGene>, 
		XMLSerializable 
{
	private static final long serialVersionUID = 1L;
	
	
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
	 * Construct a new BitChromosome with the given _length. 
	 * 
	 * @param length Length of the BitChromosome, number of bits.
	 * @param p Probability of the TRUEs in the BitChromosome.
	 * @throws NegativeArraySizeException if the <code>length</code> is smaller
	 *         than one.
	 * @throws IllegalArgumentException if <code>p</code> is out of range.
	 */
	public BitChromosome(final int length, final double p) {
		nonNegative(length);
		checkProbability(p);
		
		final int bytes = (length & 7) == 0 ? (length >>> 3) : (length >>> 3) + 1;
		_genes = new byte[bytes];
		_length = length;
		
		final Random random = RandomRegistry.getRandom();
		for (int i = 0, n = length(); i < n; ++i) {
			set(i, random.nextDouble() < p);
		}
	}
	
	/**
	 * Constructing a new BitChromosome with the given _length. The TRUEs and
	 * FALSE in the {@code Chromosome} are equaly distributed.
	 * 
	 * @param length Length of the BitChromosome.
	 * @throws NegativeArraySizeException if the <code>_length</code> is smaller
	 *         than one.
	 */
	public BitChromosome(final int length) {
		this(length, 0.5);
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
	public BitChromosome (final BitSet bits) {
		this(bits.length(), bits);
	}
	
	/**
	 * @param length Length of the BitChromosome.
	 * @param bits
	 * @throws NegativeArraySizeException if the <code>length</code> is smaller
	 *         than one.
	 * @throws NullPointerException if the <code>bitSet</code> is 
	 *         <code>null</code>.
	 */
	public BitChromosome(final int length, final BitSet bits) {
		nonNull(bits, "BitSet");
		
		final int bytes = (length & 7) == 0 ? (length >>> 3) : (length >>> 3) + 1;
		_genes = new byte[bytes];
		_length = length;
		
		int ones = 0;
		for (int i = 0; i < length; ++i) {
			if (bits.get(i)) {
				++ones;
				bit.set(_genes, i, true);
			} else {
				bit.set(_genes, i, false);
			}
			
		}
		_p = (double)ones/(double)length;
	}
	
	public BitChromosome(final LargeInteger value) {
		this(bit.toByteArray(value));
	}
	
	public BitChromosome(final byte[] value) {
		this(value.length*8);
		System.arraycopy(value, 0, _genes, 0, value.length);
	}
	
	/**
	 * Create a new {@code BitChromosome} from the given character sequence
	 * containing '0' and '1'.
	 * 
	 * @param value the input string.
	 * @throws NullPointerException if the {@code value} is {@code null}.
	 * @throws IllegalArgumentException if the length of the character sequence
	 *         is zero or contains other characters than '0' or '1'.
	 */
	public BitChromosome (final CharSequence value) {
		this(value.length());
		
		nonNull(value, "Input");
		if (value.length() == 0) {
			throw new IllegalArgumentException("Length must greater than zero.");
		}
		
		int ones = 0;
		for (int i = 0, n = value.length(); i < n; ++i) {
			final char c = value.charAt(i);
			if (c == '1') {
				++ones;
				bit.set(_genes, i, true);
			} else if (c == '0') {
				bit.set(_genes, i, false);
			} else {
				throw new IllegalArgumentException(String.format(
					"Illegal character '%s' at position %d", c, i
				));
			}
		}
		
		_p = (double)ones/(double)value.length();
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
		bit.set(_genes, index, value);
	}
	
	private boolean get(final int index) {
		return bit.get(_genes, index);
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
	public ISeq<BitGene> toSeq() {
		final Array<BitGene> genes = new Array<>(_length);
		for (int i = 0; i < _length; ++i) {
			genes.set(i, bit.get(_genes, i) ? BitGene.TRUE : BitGene.FALSE);
		}
		return genes.toISeq();
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
			@Override public BitGene next()	{
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
		return bit.toLargeInteger(_genes);
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
	 * @param bytes the bytes to hold the binary representation 
	 *	       (two's-complement) of this large integer.
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
	
	@Override
	public BitChromosome newInstance(final ISeq<BitGene> genes) {
		nonNull(genes, "Genes");
		
		final BitChromosome chromosome = new BitChromosome(genes.length(), _p);
		
		int ones = 0;
		for (int i = 0; i < genes.length(); ++i) {
			if (genes.get(i) == BitGene.TRUE) {
				++ones;
			}
			bit.set(chromosome._genes, i, genes.get(i) == BitGene.TRUE);
		}
		chromosome._p = (double)ones/(double)genes.length();
		return chromosome;
	}
	
	@Override
	public BitChromosome newInstance() {
		final Random random = RandomRegistry.getRandom();
		final BitChromosome chromosome = new BitChromosome(_length, _p);
		for (int i = 0; i < _length; ++i) {
			bit.set(chromosome._genes, i, random.nextDouble() < _p);
		}
		return chromosome;
	}
	
	/**
	 * Return the BitChromosome as String. A TRUE is represented by a 1 and
	 * a FALSE by a 0.
	 * 
	 * @return String representation (containing only '1' and '0') of the
	 *         BitChromosome.
	 */
	public String toCanonicalString() {
		final StringBuilder out = new StringBuilder(length());
		for (int i = 0; i < _length; ++i) {
			out.append(bit.get(_genes, i) ? '1' : '0');
		}
		return out.toString();
	}
	
	@Override
	public int compareTo(final BitChromosome that) {
		return toLargeInteger().compareTo(that.toLargeInteger());
	}

	@Override
	public boolean isLargerThan(final BitChromosome that) {
		return toLargeInteger().isLargerThan(that.toLargeInteger());
	}

	public LargeInteger sqrt() {
		return toLargeInteger().sqrt();
	}
	
	@Override
	public BitChromosome plus(final BitChromosome that) {
		return new BitChromosome(toLargeInteger().plus(that.toLargeInteger()));
	}

	@Override
	public BitChromosome opposite() {
		return new BitChromosome(toLargeInteger().opposite());
	}
	
	/**
	 * Invert the ones and zeros of this bit chromosome.
	 * 
	 * @return a new BitChromosome with inverted ones and zeros.
	 */
	public BitChromosome invert() {
		final BitChromosome copy = copy();
		bit.invert(copy._genes);
		return copy;
	}

	@Override
	public BitChromosome times(final BitChromosome that) {
		return new BitChromosome(toLargeInteger().times(that.toLargeInteger()));
	}
	
	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(_genes).value();
	}
	
	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		
		final BitChromosome c = (BitChromosome)o;
		boolean equals = length() == c.length();
		for (int i = 0, n = length(); equals && i < n; ++i) {
			equals = getGene(i) == c.getGene(i);
		}
		return equals;
	}

	@Override
	public Text toText() {
		return Text.valueOf(str(toByteArray()));
	}
	
	@Override
	public BitChromosome copy() {
		final BitChromosome chromosome = new BitChromosome(_length, _p);
		System.arraycopy(_genes, 0, chromosome._genes, 0, chromosome._genes.length);
		return chromosome;
	}
	
	
	static String toString(final byte[] data) {
		final StringBuilder out = new StringBuilder(data.length*8 + data.length);
		
		if (data.length > 0) {
			for (int j = 7; j >= 0; --j) {
				out.append((data[data.length - 1] >>> j) & 1);
			}
		}
		for (int i = data.length - 2; i >= 0 ;--i) {
			out.append('|');
			for (int j = 7; j >= 0; --j) {
				out.append((data[i] >>> j) & 1);
			}
		}

		return out.toString();
	}
	
	/**
	 * Convert a string which was created with the {@link #toString(byte...)}
	 * method back to an byte array.
	 * 
	 * @param data the string to convert.
	 * @return the byte array.
	 * @throws IllegalArgumentException if the given data string could not be
	 *         converted.
	 */
	 static byte[] toByteArray(final String data) {
		final String[] parts = data.split("\\|");
		final byte[] bytes = new byte[parts.length];
		
		for (int i = 0; i < parts.length; ++i) {
			if (parts[i].length() != 8) {
				throw new IllegalArgumentException(
					"Byte value doesn't contain 8 bit: " + parts[i]
				);
			} else {
				try {
					bytes[parts.length - 1 - i] = (byte)Integer.parseInt(parts[i], 2);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
		
		return bytes;
	}
	 
	
	/* *************************************************************************
	 *  XML object serialization
	 * ************************************************************************/	
	
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
			final byte[] data = toByteArray(xml.getText().toString());
			final BitChromosome chromosome = new BitChromosome(data);
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
			xml.addText(BitChromosome.toString(chromosome.toByteArray()));
		}
		@Override
		public void read(final InputElement element, final BitChromosome gene) {
		}
	};	
	
	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/
	
	private void writeObject(final ObjectOutputStream out)
		throws IOException 
	{
		out.defaultWriteObject();
	
		out.writeInt(_length);
		out.writeDouble(_p);
		out.writeInt(_genes.length);
		out.write(_genes);
	}
	
	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException 
	{
		in.defaultReadObject();
	
		_length = in.readInt();
		_p = in.readDouble();
		
		final int bytes = in.readInt();
		_genes = new byte[bytes];
		in.readFully(_genes);
		
	}
	
}



