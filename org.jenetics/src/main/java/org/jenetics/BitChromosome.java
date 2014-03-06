/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics;

import static java.lang.Math.min;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.BitSet;
import java.util.Iterator;
import java.util.ListIterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javolution.text.Text;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.number.LargeInteger;
import org.jscience.mathematics.number.Number;

import org.jenetics.internal.util.HashBuilder;
import org.jenetics.internal.util.internalbit;
import org.jenetics.internal.util.model.ModelType;
import org.jenetics.internal.util.model.ValueType;

import org.jenetics.util.ISeq;
import org.jenetics.util.bit;

/**
 * Implementation of the <i>classical</i> BitChromosome.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.6 &mdash; <em>$Date: 2014-03-06 $</em>
 */
@XmlJavaTypeAdapter(BitChromosome.Model.Adapter.class)
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

	// Wraps the genes byte array into a Seq<BitGene>.
	private transient BitGeneArray _seq;

	// Private primary constructor.
	private BitChromosome(final byte[] bits, final int length, final double p) {
		_genes = bits;
		_length = length;
		_p = p;
		_seq = new BitGeneArray(_genes, 0, _length);

	}

	/**
	 * Create a new bit chromosome from the given bit (byte) array.
	 *
	 * @param bits the bit values of the new chromosome gene.
	 * @param start the initial (bit) index of the range to be copied, inclusive
	 * @param end the final (bit) index of the range to be copied, exclusive.
	 *        (This index may lie outside the array.)
	 * @throws java.lang.ArrayIndexOutOfBoundsException if start < 0 or
	 *         start > bits.length*8
	 * @throws java.lang.IllegalArgumentException if start > end
	 * @throws java.lang.NullPointerException if the {@code bits} array is
	 *         {@code null}.
	 */
	public BitChromosome(final byte[] bits, final int start, final int end) {
		this(
			internalbit.copy(bits, start, end),
			min(bits.length << 3, end) - start,
			0.0
		);
		_p = (double)bit.count(_genes)/(double)_length;
	}

	/**
	 * Create a new {@code BitChromosome} from the given {@code byte} array. 
	 * This is a shortcut for {@code new BitChromosome(bits, 0, bits.length*8)}.
	 *
	 * @param bits the {@code byte} array.
	 */
	public BitChromosome(final byte[] bits) {
		this(bits, 0, bits.length << 3);
	}

	/**
	 * Construct a new BitChromosome with the given length.
	 *
	 * @param length Length of the BitChromosome, number of bits.
	 * @param p Probability of the TRUEs in the BitChromosome.
	 * @throws NegativeArraySizeException if the {@code length} is smaller
	 *         than one.
	 * @throws IllegalArgumentException if {@code p} is not a valid probability.
	 *
	 * @deprecated Use {@link #of(int, double)} instead.
	 */
	@Deprecated
	public BitChromosome(final int length, final double p) {
		this(bit.newArray(length, p), length, p);
	}

	/**
	 * Constructing a new BitChromosome with the given _length. The TRUEs and
	 * FALSE in the {@code Chromosome} are equally distributed.
	 *
	 * @param length Length of the BitChromosome.
	 * @throws NegativeArraySizeException if the {@code _length} is smaller
	 *         than one.
	 *
	 * @deprecated Use {@link #of(int)} instead.
	 */
	@Deprecated
	public BitChromosome(final int length) {
		this(bit.newArray(length, 0.5), length, 0.5);
	}

	/**
	 * @param length Length of the BitChromosome.
	 * @param bits the bit-set which initializes the chromosome
	 * @throws NegativeArraySizeException if the {@code length} is smaller
	 *         than one.
	 * @throws NullPointerException if the {@code bitSet} is
	 *         {@code null}.
	 *
	 * @deprecated Use {@link #of(java.util.BitSet, int)} instead.
	 */
	@Deprecated
	public BitChromosome(final int length, final BitSet bits) {
		this(toByteArray(requireNonNull(bits, "BitSet"), length));
	}

	private static byte[] toByteArray(final BitSet bits, final int length) {
		final byte[] bytes = bit.newArray(length);
		for (int i = 0; i < length; ++i) {
			if (bits.get(i)) {
				bit.set(bytes, i);
			}
		}
		return bytes;
	}

	private BitChromosome(final byte[] bits, final int length) {
		this(
			bits,
			length == -1 ? bits.length*8 : length,
			(double)bit.count(bits)/
			(double)(length == -1 ? bits.length*8 : length)
		);
	}

	/**
	 * Constructing a new BitChromosome from a given BitSet.
	 * The BitSet is copied while construction. The length of the constructed
	 * BitChromosome will be {@code bitSet.length()}
	 * (@see BitSet#length).
	 *
	 * @param bits the bit-set which initializes the chromosome
	 * @throws NullPointerException if the {@code bitSet} is
	 *        {@code null}.
	 *
	 * @deprecated Use {@link #of(java.util.BitSet)} instead.
	 */
	@Deprecated
	public BitChromosome (final BitSet bits) {
		this(bits.toByteArray(), -1);
	}

	/**
	 * Create a new {@code BitChromosome} from the given large integer value.
	 *
	 * @param value the value of the created {@code BitChromosome}
	 * @throws NullPointerException if the given {@code value} is {@code null}.
	 *
	 * @deprecated Use {@link #of(java.math.BigInteger)} instead.
	 */
	@Deprecated
	public BitChromosome(final LargeInteger value) {
		this(bit.toByteArray(value), -1);
	}

	/**
	 * Create a new {@code BitChromosome} from the given character sequence
	 * containing '0' and '1'; as created with the {@link #toCanonicalString()}
	 * method.
	 *
	 * @param value the input string.
	 * @throws NullPointerException if the {@code value} is {@code null}.
	 * @throws IllegalArgumentException if the length of the character sequence
	 *         is zero or contains other characters than '0' or '1'.
	 *
	 * @deprecated Use {@link #of(CharSequence)} instead.
	 */
	@Deprecated
	public BitChromosome (final CharSequence value) {
		this(toByteArray(requireNonNull(value, "Input")), -1);
	}

	private static byte[] toByteArray(final CharSequence value) {
		final byte[] bytes = bit.newArray(value.length());
		for (int i = value.length(); --i >= 0;) {
			final char c = value.charAt(i);
			if (c == '1') {
				bit.set(bytes, i);
			} else if (c != '0') {
				throw new IllegalArgumentException(format(
					"Illegal character '%s' at position %d", c, i
				));
			}
		}

		return bytes;
	}

	private void rangeCheck(final int index) {
		assert(_length >= 0);
		if (index < 0 || index >= _length) {
			throw new IndexOutOfBoundsException(
				"Index: " + index + ", Length: " + _length
			);
		}
	}

	/**
	 * Return the one probability of this chromosome.
	 * 
	 * @return the one probability of this chromosome.
	 */
	double getOneProbability() {
		return _p;
	}
	
	@Override
	public BitGene getGene() {
		assert (_genes != null);
		assert (_genes.length > 0);
		return BitGene.of(bit.get(_genes, 0));
	}

	@Override
	public BitGene getGene(final int index) {
		rangeCheck(index);
		assert(_genes != null);
		return BitGene.of(bit.get(_genes, index));
	}

	@Override
	public ISeq<BitGene> toSeq() {
		return _seq.toISeq();
	}

	@Override
	public int length() {
		return _length;
	}

	/**
	 * Returns the number of bits set to true in this {@code BitChromosome}.
	 *
	 * @return the number of bits set to true in this {@code BitChromosome}
	 */
	public int bitCount() {
		return bit.count(_genes);
	}

	@Override
	public Iterator<BitGene> iterator() {
		return _seq.iterator();
	}

	public ListIterator<BitGene> listIterator() {
		return _seq.listIterator();
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
	 *
	 * @deprecated Use {@link #toBigInteger()} instead.
	 */
	@Deprecated
	public LargeInteger toLargeInteger() {
		return bit.toLargeInteger(_genes);
	}

	/**
	 * Return the {@code BigInteger} value this {@code BitChromosome} represents.
	 *
	 * @return {@code BigInteger} value this {@code BitChromosome} represents.
	 */
	public BigInteger toBigInteger() {
		return new BigInteger(_genes);
	}

	/**
	 * Returns the two's-complement binary representation of this
	 * large integer. The output array is in <i>big-endian</i>
	 * byte-order: the most significant byte is at the offset position.
	 *
	 * <p>Note: This representation is consistent with {@code java.lang.BigInteger
	 *          } byte array representation and can be used for conversion
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
	 *
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
		requireNonNull(genes, "Genes");

		final BitChromosome chromosome = new BitChromosome(
			bit.newArray(genes.length()), genes.length()
		);
		int ones = 0;

		if (genes instanceof BitGeneArray.BitGeneISeq) {
			final BitGeneArray.BitGeneISeq iseq = (BitGeneArray.BitGeneISeq)genes;
			iseq.copyTo(chromosome._genes);
			ones = bit.count(chromosome._genes);
		} else {
			for (int i = genes.length(); --i >= 0;) {
				if (genes.get(i).booleanValue()) {
					bit.set(chromosome._genes, i);
					++ones;
				}
			}
		}

		chromosome._p = (double)ones/(double)genes.length();
		return chromosome;
	}

	@Override
	public BitChromosome newInstance() {
		return new BitChromosome(_length, _p);
	}

	/**
	 * Return the BitChromosome as String. A TRUE is represented by a 1 and
	 * a FALSE by a 0. The returned string can be used to create a new
	 * chromosome with the {@link #BitChromosome(CharSequence)} constructor.
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

	@Deprecated
	@Override
	public boolean isLargerThan(final BitChromosome that) {
		return toLargeInteger().isLargerThan(that.toLargeInteger());
	}

	@Deprecated
	public LargeInteger sqrt() {
		return toLargeInteger().sqrt();
	}

	@Deprecated
	@Override
	public BitChromosome plus(final BitChromosome that) {
		return new BitChromosome(toLargeInteger().plus(that.toLargeInteger()));
	}

	@Deprecated
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

	@Deprecated
	@Override
	public BitChromosome times(final BitChromosome that) {
		return new BitChromosome(toLargeInteger().times(that.toLargeInteger()));
	}

	/**
	 * Construct a new BitChromosome with the given _length.
	 *
	 * @param length Length of the BitChromosome, number of bits.
	 * @param p Probability of the TRUEs in the BitChromosome.
	 * @throws NegativeArraySizeException if the {@code length} is smaller
	 *         than one.
	 * @throws IllegalArgumentException if {@code p} is not a valid probability.
	 */
	public static BitChromosome of(final int length, final double p) {
		return new BitChromosome(length, p);
	}

	/**
	 * Constructing a new BitChromosome with the given _length. The TRUEs and
	 * FALSE in the {@code Chromosome} are equally distributed.
	 *
	 * @param length Length of the BitChromosome.
	 * @throws NegativeArraySizeException if the {@code _length} is smaller
	 *         than one.
	 */
	public static BitChromosome of(final int length) {
		return new BitChromosome(length);
	}

	/**
	 * @param length length of the BitChromosome.
	 * @param bits the bit-set which initializes the chromosome
	 * @throws NegativeArraySizeException if the {@code length} is smaller
	 *         than one.
	 * @throws NullPointerException if the {@code bitSet} is
	 *         {@code null}.
	 */
	public static BitChromosome of(final BitSet bits, final int length) {
		final byte[] bytes = bit.newArray(length);
		for (int i = 0; i < length; ++i) {
			if (bits.get(i)) {
				bit.set(bytes, i);
			}
		}
		final double p = (double)bit.count(bytes)/(double)length;

		return new BitChromosome(bytes, length, p);
	}

	/**
	 * Constructing a new BitChromosome from a given BitSet.
	 * The BitSet is copied while construction. The length of the constructed
	 * BitChromosome will be {@code bitSet.length()}
	 * (@see BitSet#length).
	 *
	 * @param bits the bit-set which initializes the chromosome
	 * @throws NullPointerException if the {@code bitSet} is
	 *        {@code null}.
	 */
	public static BitChromosome of(final BitSet bits) {
		return new BitChromosome(bits.toByteArray(), -1);
	}

	/**
	 * Create a new {@code BitChromosome} from the given big integer value.
	 *
	 * @param value the value of the created {@code BitChromosome}
	 * @throws NullPointerException if the given {@code value} is {@code null}.
	 */
	public static BitChromosome of(final BigInteger value) {
		return new BitChromosome(value.toByteArray(), -1);
	}

	/**
	 * Create a new {@code BitChromosome} from the given character sequence
	 * containing '0' and '1'; as created with the {@link #toCanonicalString()}
	 * method.
	 *
	 * @param value the input string.
	 * @throws NullPointerException if the {@code value} is {@code null}.
	 * @throws IllegalArgumentException if the length of the character sequence
	 *         is zero or contains other characters than '0' or '1'.
	 */
	public static BitChromosome of(final CharSequence value) {
		return new BitChromosome(toByteArray(requireNonNull(value, "Input")), -1);
	}

	@Override
	public int hashCode() {
		return HashBuilder.of(getClass()).and(_genes).value();
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

	@Deprecated
	@Override
	public Text toText() {
		return Text.valueOf(bit.toByteString(toByteArray()));
	}

	@Deprecated
	@Override
	public BitChromosome copy() {
		final BitChromosome chromosome = new BitChromosome(_length, _p);
		System.arraycopy(_genes, 0, chromosome._genes, 0, chromosome._genes.length);
		return chromosome;
	}


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

		_seq = new BitGeneArray(_genes, 0, _length);
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
			final byte[] data = bit.fromByteString(xml.getText().toString());
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
			xml.addText(bit.toByteString(chromosome.toByteArray()));
		}
		@Override
		public void read(final InputElement element, final BitChromosome gene) {
		}
	};

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "org.jenetics.BitChromosome")
	@XmlType(name = "org.jenetics.BitChromosome")
	@XmlAccessorType(XmlAccessType.FIELD)
	final static class Model {

		@XmlAttribute
		public int length;

		@XmlAttribute
		public double probability;

		@XmlValue
		public String value;

		@ValueType(BitChromosome.class)
		@ModelType(Model.class)
		public final static class Adapter
			extends XmlAdapter<Model, BitChromosome>
		{
			@Override
			public Model marshal(final BitChromosome chromosome) {
				final Model model = new Model();
				model.length = chromosome._length;
				model.probability = chromosome._p;
				model.value = bit.toByteString(chromosome.toByteArray());
				return model;
			}

			@Override
			public BitChromosome unmarshal(final Model model) {
				final BitChromosome chromosome = new BitChromosome(
					bit.fromByteString(model.value)
				);
				chromosome._p = model.probability;
				chromosome._length = model.length;
				return chromosome;
			}
		}
	}

}
