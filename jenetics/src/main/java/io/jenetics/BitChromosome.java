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
import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.Requires.probability;
import static io.jenetics.internal.util.SerialIO.readBytes;
import static io.jenetics.internal.util.SerialIO.readInt;
import static io.jenetics.internal.util.SerialIO.writeBytes;
import static io.jenetics.internal.util.SerialIO.writeInt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.BitSet;
import java.util.function.Function;
import java.util.stream.IntStream;

import io.jenetics.internal.collection.BitArray;
import io.jenetics.util.ISeq;

/**
 * Implementation of the <i>classical</i> BitChromosome.
 *
 * @see BitGene
 *
 * @implNote
 * This class is immutable and thread-safe. The bits of the bit chromosome are
 * backed by a {@code byte[]} array with the following layout:
 * <pre> {@code
 *  Byte:       3        2        1        0
 *              |        |        |        |
 *  Array: |11110011|10011101|01000000|00101010|
 *          |                 |        |      |
 *  Bit:    23                15       7      0
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 7.0
 */
public final class BitChromosome extends Number
	implements
		Chromosome<BitGene>,
		Comparable<BitChromosome>,
		Serializable
{
	@Serial
	private static final long serialVersionUID = 2L;


	private static final double DEFAULT_PROBABILITY = 0.5;

	/**
	 * The boolean array which holds the {@link BitGene}s.
	 */
	private final BitArray _genes;

	/**
	 * The ones probability of the randomly generated Chromosome.
	 */
	private final double _p;

	// Private primary constructor.
	private BitChromosome(final BitArray genes, final double p) {
		_genes = requireNonNull(genes);
		_p = probability(p);
	}

	/**
	 * Create a new bit chromosome from the given bit (byte) array.
	 *
	 * @since 7.0
	 *
	 * @param bits the bit values of the new chromosome gene.
	 * @param start the initial (bit) index of the range to be copied, inclusive
	 * @param end the final (bit) index of the range to be copied, exclusive.
	 *        (This index may lie outside the array.)
	 * @param p the ones probability
	 * @throws java.lang.ArrayIndexOutOfBoundsException if {@code start < 0} or
	 *         {@code start > bits.length*8}
	 * @throws java.lang.IllegalArgumentException if {@code start > end}
	 * @throws java.lang.NullPointerException if the {@code bits} array is
	 *         {@code null}.
	 */
	public BitChromosome(
		final byte[] bits,
		final int start,
		final int end,
		final double p
	) {
		this(BitArray.of(bits, start, min(end, bits.length*Byte.SIZE)), p);
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
		this(bits, start, end, DEFAULT_PROBABILITY);
	}

	/**
	 * Create a new {@code BitChromosome} from the given {@code byte} array.
	 * This is a shortcut for {@code new BitChromosome(bits, 0, bits.length*8)}.
	 *
	 * @param bits the {@code byte} array.
	 */
	public BitChromosome(final byte[] bits) {
		this(bits, 0, bits.length*Byte.SIZE);
	}

	/**
	 * Return the one <em>nominal</em> probability of this chromosome. It's not
	 * the actual one-probability of {@code this} chromosome.
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
		return BitGene.of(_genes.get(0));
	}

	/**
	 * Return the value of the first gene of this chromosome.
	 *
	 * @since 4.2
	 *
	 * @return the first value of this chromosome.
	 */
	public boolean booleanValue() {
		return _genes.get(0);
	}

	@Override
	public BitGene get(final int index) {
		return BitGene.of(_genes.get(index));
	}

	@Override
	public int length() {
		return _genes.length();
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
		return _genes.get(index);
	}

	/**
	 * Returns the number of bits set to true in this {@code BitChromosome}.
	 *
	 * @return the number of bits set to true in this {@code BitChromosome}
	 */
	public int bitCount() {
		return _genes.bitCount();
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
		return _genes.toBigInteger();
	}

	/**
	 * Returns the byte array, which represents the bit values of {@code this}
	 * chromosome.
	 *
	 * @return a byte array which represents this {@code BitChromosome}. The
	 *         length of the array is {@code (int)Math.ceil(length()/8.0)}.
	 */
	public byte[] toByteArray() {
		return _genes.toByteArray();
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
			.filter(_genes::get);
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
			.filter(index -> !_genes.get(index));
	}

	@Override
	public BitChromosome newInstance(final ISeq<BitGene> genes) {
		if (genes.isEmpty()) {
			throw new IllegalArgumentException(
				"The genes sequence must contain at least one gene."
			);
		}

		final var array = BitArray.ofLength(genes.length());
		for (int i = 0; i < genes.length(); ++i) {
			array.set(i, genes.get(i).booleanValue());
		}

		return new BitChromosome(array, _p);
	}

	@Override
	public BitChromosome newInstance() {
		return of(length(), _p);
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
		return _genes.toString();
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
		final var array = _genes.copy();
		array.invert();
		return new BitChromosome(array, 1.0 - _p);
	}

	@Override
	public int hashCode() {
		return _genes.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof BitChromosome other &&
			_genes.equals(other._genes);
	}

	@Override
	public String toString() {
		return _genes.toByteString();
	}


	/* *************************************************************************
	 * Static factory methods.
	 **************************************************************************/

	/**
	 * Constructing a new BitChromosome with the given {@code length} and
	 * randomly set bits. The TRUEs and FALSE in the {@code Chromosome} are
	 * equally distributed with one-probability of {@code p}.
	 *
	 * @param length Length of the BitChromosome, number of bits.
	 * @param p Probability of the TRUEs in the BitChromosome.
	 * @return a new {@code BitChromosome} with the given parameter
	 * @throws NegativeArraySizeException if the {@code length} is smaller
	 *         than one.
	 * @throws IllegalArgumentException if {@code p} is not a valid probability.
	 */
	public static BitChromosome of(final int length, final double p) {
		return new BitChromosome(BitArray.ofLength(length, p), p);
	}

	/**
	 * Constructing a new BitChromosome with the given {@code length} and
	 * randomly set bits. The TRUEs and FALSE in the {@code Chromosome} are
	 * equally distributed with one-probability of 0.5.
	 *
	 * @param length Length of the BitChromosome.
	 * @return a new {@code BitChromosome} with the given parameter
	 * @throws NegativeArraySizeException if the {@code length} is smaller
	 *         than one.
	 */
	public static BitChromosome of(final int length) {
		return of(length, DEFAULT_PROBABILITY);
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
		final var array = BitArray.ofLength(length);
		for (int i = 0; i < length; ++i) {
			if (bits.get(i)) {
				array.set(i, true);
			}
		}

		return new BitChromosome(array, probability(p));
	}

	/**
	 * Create a new {@code BitChromosome} with the given parameters. The
	 * {@link #oneProbability()} of the chromosome is set to {@code 0.5}.
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
		return of(bits, length, DEFAULT_PROBABILITY);
	}

	/**
	 * Constructing a new BitChromosome from a given BitSet. The length of the
	 * constructed {@code BitChromosome} will be ({@link BitSet#length}).
	 *
	 * @see #of(BitSet, int, double)
	 * @see #of(BitSet, int)
	 *
	 * @param bits the bit-set which initializes the chromosome
	 * @return a new {@code BitChromosome} with the given parameter
	 * @throws NullPointerException if the {@code bitSet} is
	 *        {@code null}.
	 */
	public static BitChromosome of(final BitSet bits) {
		return of(bits, bits.length());
	}

	/**
	 * Create a new {@code BitChromosome} from the given big integer value and
	 * ones probability.
	 *
	 * @param value the value of the created {@code BitChromosome}
	 * @param length length of the BitChromosome
	 * @param p Probability of the TRUEs in the BitChromosome.
	 * @return a new {@code BitChromosome} with the given parameter
	 * @throws NullPointerException if the given {@code value} is {@code null}.
	 * @throws IllegalArgumentException if {@code p} is not a valid probability.
	 */
	public static BitChromosome of(
		final BigInteger value,
		final int length,
		final double p
	) {
		final var array = BitArray.of(value, length);
		return new BitChromosome(array, probability(p));
	}

	/**
	 * Create a new {@code BitChromosome} from the given big integer value and
	 * ones probability. The {@link #oneProbability()} of the chromosome is set
	 * to {@code 0.5}.
	 *
	 * @since 7.0
	 *
	 * @param value the value of the created {@code BitChromosome}
	 * @param length length of the BitChromosome
	 * @return a new {@code BitChromosome} with the given parameter
	 * @throws NullPointerException if the given {@code value} is {@code null}.
	 * @throws IllegalArgumentException if {@code p} is not a valid probability.
	 */
	public static BitChromosome of(
		final BigInteger value,
		final int length
	) {
		return of(value, length, DEFAULT_PROBABILITY);
	}


	/**
	 * Create a new {@code BitChromosome} from the given big integer value. The
	 * {@link #oneProbability()} of the chromosome is set to {@code 0.5}.
	 *
	 * @param value the value of the created {@code BitChromosome}
	 * @return a new {@code BitChromosome} with the given parameter
	 * @throws NullPointerException if the given {@code value} is {@code null}.
	 */
	public static BitChromosome of(final BigInteger value) {
		final var array = BitArray.of(value);
		return new BitChromosome(array, DEFAULT_PROBABILITY);
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
		final var array = BitArray.of(value, length);
		return new BitChromosome(array, probability(p));
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
		return of(value, value.length(), p);
	}

	/**
	 * Create a new {@code BitChromosome} from the given character sequence
	 * containing '0' and '1'; as created with the {@link #toCanonicalString()}
	 * method. The {@link #oneProbability()} of the chromosome is set to
	 * {@code 0.5}.
	 *
	 * @param value the input string.
	 * @return a new {@code BitChromosome} with the given parameter
	 * @throws NullPointerException if the {@code value} is {@code null}.
	 * @throws IllegalArgumentException if the length of the character sequence
	 *         is zero or contains other characters than '0' or '1'.
	 */
	public static BitChromosome of(final CharSequence value) {
		return of(value, value.length(), DEFAULT_PROBABILITY);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.BIT_CHROMOSOME, this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		writeBytes(toByteArray(), out);
		writeInt(length(), out);
		out.writeDouble(oneProbability());
	}

	static BitChromosome read(final DataInput in) throws IOException {
		final var bytes = readBytes(in);
		final var length  = readInt(in);
		final var p = in.readDouble();
		final var genes = BitArray.of(bytes,0, length);

		return new BitChromosome(genes, p);
	}

}
