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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics;

import static java.lang.Math.min;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static io.jenetics.internal.util.Hashes.hash;
import static io.jenetics.internal.util.SerialIO.readInt;
import static io.jenetics.internal.util.SerialIO.writeInt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.stream.IntStream;

import io.jenetics.internal.util.Bits;
import io.jenetics.internal.util.Requires;
import io.jenetics.util.ISeq;

/**
 * Implementation of the <i>classical</i> BitChromosome.
 *
 * @see BitGene
 *
 * @implSpec
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 6.1
 */
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
	protected final int _length;

	/**
	 * The boolean array which holds the {@link BitGene}s.
	 */
	protected final byte[] _genes;

	// Wraps the genes byte array into a Seq<BitGene>.
	private final transient BitGeneISeq _seq;

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
			Bits.copy(bits, start, end),
			min(bits.length << 3, end) - start,
			0.0
		);
		_p = (double) Bits.count(_genes)/(double)_length;
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
			(double) Bits.count(bits)/
			(double)(length == -1 ? bits.length*8 : length)
		);
	}

	private static byte[] toByteArray(final CharSequence value) {
		final byte[] bytes = Bits.newArray(value.length());
		for (int i = value.length(); --i >= 0;) {
			final char c = value.charAt(i);
			if (c == '1') {
				Bits.set(bytes, i);
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
	 * @since 5.2
	 *
	 * @return the one probability of this chromosome.
	 */
	public double oneProbability() {
		return _p;
	}

	@Override
	public BitGene gene() {
		return BitGene.of(Bits.get(_genes, 0));
	}

	/**
	 * Return the value of the first gene of this chromosome.
	 *
	 * @since 4.2
	 *
	 * @return the first value of this chromosome.
	 */
	public boolean booleanValue() {
		return Bits.get(_genes, 0);
	}

	@Override
	public BitGene get(final int index) {
		rangeCheck(index);
		return BitGene.of(Bits.get(_genes, index));
	}

	@Override
	public int length() {
		return _length;
	}

	/**
	 * Return the value on the specified index.
	 *
	 * @since 4.2
	 *
	 * @param index the gene index
	 * @return the wanted gene value
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *          (index &lt; 1 || index &gt;= length()).
	 */
	public boolean booleanValue(final int index) {
		rangeCheck(index);
		return Bits.get(_genes, index);
	}

	/**
	 * Returns the number of bits set to true in this {@code BitChromosome}.
	 *
	 * @return the number of bits set to true in this {@code BitChromosome}
	 */
	public int bitCount() {
		return Bits.count(_genes);
	}

	/**
	 * Return a list iterator over the bit-genes of this chromosome.
	 *
	 * @return a list iterator over the bit-genes of this chromosome
	 */
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

	/**
	 * Return always {@code true}.
	 *
	 * @return {@code true}, always
	 */
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
			set.set(i, get(i).bit());
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
			.filter(index -> Bits.get(_genes, index));
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
			.filter(index -> !Bits.get(_genes, index));
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
			Bits.newArray(genes.length()), genes.length()
		);
		int ones = 0;

		if (genes instanceof BitGeneISeq) {
			final BitGeneISeq iseq = (BitGeneISeq)genes;
			iseq.copyTo(chromosome._genes);
			ones = Bits.count(chromosome._genes);
		} else {
			for (int i = genes.length(); --i >= 0;) {
				if (genes.get(i).booleanValue()) {
					Bits.set(chromosome._genes, i);
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
	 * Maps the gene alleles of this chromosome, given as {@link BitSet}, by
	 * applying the given mapper function {@code f}. The mapped gene values
	 * are then wrapped into a newly created chromosome.
	 *
	 * @since 6.1
	 *
	 * @param f the mapper function
	 * @return a newly created chromosome with the mapped gene values
	 * @throws NullPointerException if the mapper function is {@code null}.
	 */
	public BitChromosome map(final Function<? super BitSet, ? extends BitSet> f) {
		return of(f.apply(toBitSet()), length(), oneProbability());
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
		return stream()
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
		Bits.invert(data);
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
		return new BitChromosome(Bits.newArray(length, p), length, p);
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
		return new BitChromosome(Bits.newArray(length, 0.5), length, 0.5);
	}

	/**
	 * Create a new {@code BitChromosome} with the given parameters.
	 *
	 * @param length length of the BitChromosome.
	 * @param bits the bit-set which initializes the chromosome
	 * @return a new {@code BitChromosome} with the given parameter
	 * @throws NegativeArraySizeException if the {@code length} is smaller
	 *         than one.
	 * @throws NullPointerException if the {@code bitSet} is
	 *         {@code null}.
	 */
	public static BitChromosome of(final BitSet bits, final int length) {
		final byte[] bytes = Bits.newArray(length);
		for (int i = 0; i < length; ++i) {
			if (bits.get(i)) {
				Bits.set(bytes, i);
			}
		}
		final double p = (double) Bits.count(bytes)/(double)length;

		return new BitChromosome(bytes, length, p);
	}

	/**
	 * Create a new {@code BitChromosome} with the given parameters.
	 *
	 * @param length length of the BitChromosome.
	 * @param bits the bit-set which initializes the chromosome
	 * @param p Probability of the TRUEs in the BitChromosome.
	 * @return a new {@code BitChromosome} with the given parameter
	 * @throws NegativeArraySizeException if the {@code length} is smaller than
	 *         one.
	 * @throws NullPointerException if the {@code bitSet} is {@code null}.
	 * @throws IllegalArgumentException if {@code p} is not a valid probability.
	 */
	public static BitChromosome of(
		final BitSet bits,
		final int length,
		final double p
	) {
		final byte[] bytes = Bits.newArray(length);
		for (int i = 0; i < length; ++i) {
			if (bits.get(i)) {
				Bits.set(bytes, i);
			}
		}

		return new BitChromosome(bytes, length, Requires.probability(p));
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
	 * @deprecated This method doesn't let you control the actual length of the
	 *             created {@code BitChromosome}. Use {@link #of(BitSet, int, double)}
	 *             or {@link #of(BitSet, int)} instead.
	 */
	@Deprecated(since = "6.1", forRemoval = true)
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
	 * Create a new {@code BitChromosome} from the given big integer value and
	 * ones probability.
	 *
	 * @param value the value of the created {@code BitChromosome}
	 * @param p Probability of the TRUEs in the BitChromosome.
	 * @return a new {@code BitChromosome} with the given parameter
	 * @throws NullPointerException if the given {@code value} is {@code null}.
	 * @throws IllegalArgumentException if {@code p} is not a valid probability.
	 */
	public static BitChromosome of(final BigInteger value, final double p) {
		final byte[] bits = value.toByteArray();
		return new BitChromosome(bits, bits.length*8, Requires.probability(p));
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

	/**
	 * Create a new {@code BitChromosome} from the given character sequence
	 * containing '0' and '1'; as created with the {@link #toCanonicalString()}
	 * method.
	 *
	 * @param value the input string.
	 * @param p Probability of the TRUEs in the BitChromosome.
	 * @return a new {@code BitChromosome} with the given parameter
	 * @throws NullPointerException if the {@code value} is {@code null}.
	 * @throws IllegalArgumentException if the length of the character sequence
	 *         is zero or contains other characters than '0' or '1'.
	 * @throws IllegalArgumentException if {@code p} is not a valid probability.
	 */
	public static BitChromosome of(final CharSequence value, final double p) {
		final byte[] bits = toByteArray(requireNonNull(value, "Input"));
		return new BitChromosome(bits, bits.length*8, Requires.probability(p));
	}

	/**
	 * Create a new {@code BitChromosome} from the given character sequence
	 * containing '0' and '1'; as created with the {@link #toCanonicalString()}
	 * method.
	 *
	 * @param value the input string.
	 * @param length length of the BitChromosome
	 * @param p Probability of the TRUEs in the BitChromosome.
	 * @return a new {@code BitChromosome} with the given parameter
	 * @throws NullPointerException if the {@code value} is {@code null}.
	 * @throws IllegalArgumentException if the length of the character sequence
	 *         is zero or contains other characters than '0' or '1'.
	 * @throws IllegalArgumentException if {@code p} is not a valid probability.
	 */
	public static BitChromosome of(
		final CharSequence value,
		final int length,
		final double p
	) {
		final byte[] bits = toByteArray(requireNonNull(value, "Input"));
		return new BitChromosome(bits, length, Requires.probability(p));
	}

	@Override
	public int hashCode() {
		return hash(_genes, hash(getClass()));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj != null &&
			getClass() == obj.getClass() &&
			length() == ((BitChromosome)obj).length() &&
			Arrays.equals(_genes, ((BitChromosome)obj)._genes);
	}

	@Override
	public String toString() {
		return Bits.toByteString(_genes);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.BIT_CHROMOSOME, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		writeInt(_length, out);
		out.writeDouble(_p);
		writeInt(_genes.length, out);
		out.write(_genes);
	}

	static BitChromosome read(final DataInput in) throws IOException {
		final int length = readInt(in);
		final double p = in.readDouble();
		final byte[] genes = new byte[readInt(in)];
		in.readFully(genes);

		return new BitChromosome(genes, length, p);
	}

}
