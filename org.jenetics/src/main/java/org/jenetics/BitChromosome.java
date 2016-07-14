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
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.BitSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.stream.IntStream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;
import org.jenetics.internal.util.bit;

import org.jenetics.util.ISeq;

/**
 * Implementation of the <i>classical</i> BitChromosome.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.4
 */
@XmlJavaTypeAdapter(BitChromosome.Model.Adapter.class)
public class BitChromosome extends Number
	implements
		Chromosome<BitGene>,
		Comparable<BitChromosome>,
		Serializable
{
	private static final long serialVersionUID = 2L;


	/**
	 * The ones probability of the randomly generated Chromosome.
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
	private transient BitGeneISeq _seq;

	// Private primary constructor.
	private BitChromosome(final byte[] bits, final int length, final double p) {
		_genes = bits;
		_length = length;
		_p = p;
		_seq = BitGeneMSeq.of(_genes, length).toISeq();
	}

	/**
	 * Create a new bit chromosome from the given bit (byte) array.
	 *
	 * @param bits the bit values of the new chromosome gene.
	 * @param start the initial (bit) index of the range to be copied, inclusive
	 * @param end the final (bit) index of the range to be copied, exclusive.
	 *        (This index may lie outside the array.)
	 * @throws java.lang.ArrayIndexOutOfBoundsException if {@code start < 0} or
	 *         {@code start > bits.length*8}
	 * @throws java.lang.IllegalArgumentException if {@code start > end}
	 * @throws java.lang.NullPointerException if the {@code bits} array is
	 *         {@code null}.
	 */
	public BitChromosome(final byte[] bits, final int start, final int end) {
		this(
			bit.copy(bits, start, end),
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

	private BitChromosome(final byte[] bits, final int length) {
		this(
			bits,
			length == -1 ? bits.length*8 : length,
			(double)bit.count(bits)/
			(double)(length == -1 ? bits.length*8 : length)
		);
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
		assert _genes != null;
		assert _genes.length > 0;
		return BitGene.of(bit.get(_genes, 0));
	}

	/**
	 * Return the value of the first gene of this chromosome.
	 *
	 * @since 2.0
	 * @return the first value of this chromosome.
	 */
	public boolean get() {
		return bit.get(_genes, 0);
	}

	@Override
	public BitGene getGene(final int index) {
		rangeCheck(index);
		assert _genes != null;
		return BitGene.of(bit.get(_genes, index));
	}

	/**
	 * Return the value on the specified index.
	 *
	 * @since 2.0
	 * @param index the gene index
	 * @return the wanted gene value
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *          (index &lt; 1 || index &gt;= length()).
	 */
	public boolean get(final int index) {
		rangeCheck(index);
		return bit.get(_genes, index);
	}

	@Override
	public ISeq<BitGene> toSeq() {
		return _seq;
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
	 * @return long value this BitChromosome represents.
	 */
	@Override
	public int intValue() {
		return (int)longValue();
	}

	/**
	 * Return the long value this BitChromosome represents.
	 *
	 * @return long value this BitChromosome represents.
	 */
	@Override
	public long longValue() {
		return toBigInteger().longValue();
	}

	/**
	 * Return the float value this BitChromosome represents.
	 *
	 * @return float value this BitChromosome represents.
	 */
	@Override
	public float floatValue() {
		return (float)longValue();
	}

	/**
	 * Return the double value this BitChromosome represents.
	 *
	 * @return double value this BitChromosome represents.
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

	/**
	 * Return the indexes of the <i>ones</i> of this bit-chromosome as stream.
	 *
	 * @since 3.0
	 *
	 * @return the indexes of the <i>ones</i> of this bit-chromosome
	 */
	public IntStream ones() {
		return IntStream.range(0, length())
			.filter(index -> bit.get(_genes, index));
	}

	/**
	 * Return the indexes of the <i>zeros</i> of this bit-chromosome as stream.
	 *
	 * @since 3.0
	 *
	 * @return the indexes of the <i>zeros</i> of this bit-chromosome
	 */
	public IntStream zeros() {
		return IntStream.range(0, length())
			.filter(index -> !bit.get(_genes, index));
	}

	@Override
	public BitChromosome newInstance(final ISeq<BitGene> genes) {
		requireNonNull(genes, "Genes");
		if (genes.isEmpty()) {
			throw new IllegalArgumentException(
				"The genes sequence must contain at least one gene."
			);
		}

		final BitChromosome chromosome = new BitChromosome(
			bit.newArray(genes.length()), genes.length()
		);
		int ones = 0;

		if (genes instanceof BitGeneISeq) {
			final BitGeneISeq iseq = (BitGeneISeq)genes;
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
		return of(_length, _p);
	}

	/**
	 * Return the BitChromosome as String. A TRUE is represented by a 1 and
	 * a FALSE by a 0. The returned string can be used to create a new
	 * chromosome with the {@link #of(CharSequence)} constructor.
	 *
	 * @return String representation (containing only '1' and '0') of the
	 *         BitChromosome.
	 */
	public String toCanonicalString() {
		return toSeq().stream()
			.map(g -> g.booleanValue() ? "1" : "0")
			.collect(joining());
	}

	@Override
	public int compareTo(final BitChromosome that) {
		return toBigInteger().compareTo(that.toBigInteger());
	}

	/**
	 * Invert the ones and zeros of this bit chromosome.
	 *
	 * @return a new BitChromosome with inverted ones and zeros.
	 */
	public BitChromosome invert() {
		final byte[] data = _genes.clone();
		bit.invert(data);
		return new BitChromosome(data, _length, 1.0 - _p);
	}

	/**
	 * Construct a new BitChromosome with the given _length.
	 *
	 * @param length Length of the BitChromosome, number of bits.
	 * @param p Probability of the TRUEs in the BitChromosome.
	 * @return a new {@code BitChromosome} with the given parameter
	 * @throws NegativeArraySizeException if the {@code length} is smaller
	 *         than one.
	 * @throws IllegalArgumentException if {@code p} is not a valid probability.
	 */
	public static BitChromosome of(final int length, final double p) {
		return new BitChromosome(bit.newArray(length, p), length, p);
	}

	/**
	 * Constructing a new BitChromosome with the given _length. The TRUEs and
	 * FALSE in the {@code Chromosome} are equally distributed.
	 *
	 * @param length Length of the BitChromosome.
	 * @return a new {@code BitChromosome} with the given parameter
	 * @throws NegativeArraySizeException if the {@code _length} is smaller
	 *         than one.
	 */
	public static BitChromosome of(final int length) {
		return new BitChromosome(bit.newArray(length, 0.5), length, 0.5);
	}

	/**
	 * @param length length of the BitChromosome.
	 * @param bits the bit-set which initializes the chromosome
	 * @return a new {@code BitChromosome} with the given parameter
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
	 * BitChromosome will be {@code bitSet.length()} ({@link BitSet#length}).
	 *
	 * @param bits the bit-set which initializes the chromosome
	 * @return a new {@code BitChromosome} with the given parameter
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
	 * @return a new {@code BitChromosome} with the given parameter
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
	 * @return a new {@code BitChromosome} with the given parameter
	 * @throws NullPointerException if the {@code value} is {@code null}.
	 * @throws IllegalArgumentException if the length of the character sequence
	 *         is zero or contains other characters than '0' or '1'.
	 */
	public static BitChromosome of(final CharSequence value) {
		return new BitChromosome(toByteArray(requireNonNull(value, "Input")), -1);
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(_genes).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(c -> {
			boolean equals = length() == c.length();
			for (int i = 0, n = length(); equals && i < n; ++i) {
				equals = getGene(i) == c.getGene(i);
			}
			return equals;
		});
	}

	@Override
	public String toString() {
		return bit.toByteString(_genes);
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

		_seq = BitGeneISeq.of(_genes, _length);
	}

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "bit-chromosome")
	@XmlType(name = "org.jenetics.BitChromosome")
	@XmlAccessorType(XmlAccessType.FIELD)
	final static class Model {

		@XmlAttribute(name = "length", required = true)
		public int length;

		@XmlAttribute(name = "ones-probability", required = true)
		public double probability;

		@XmlValue
		public String value;

		public final static class Adapter
			extends XmlAdapter<Model, BitChromosome>
		{
			@Override
			public Model marshal(final BitChromosome chromosome) {
				final Model model = new Model();
				model.length = chromosome._length;
				model.probability = chromosome._p;
				model.value = chromosome.toCanonicalString();
				return model;
			}

			@Override
			public BitChromosome unmarshal(final Model model) {
				return new BitChromosome(
					toByteArray(model.value),
					model.length,
					model.probability
				);
			}
		}
	}

}
