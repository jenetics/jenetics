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

import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.SerialIO.readInt;
import static io.jenetics.internal.util.SerialIO.writeInt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.MSeq;

/**
 * Numeric chromosome implementation which holds 32-bit integer numbers.
 *
 * @see IntegerGene
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz  Wilhelmstötter</a>
 * @since 2.0
 * @version 7.0
 */
public class IntegerChromosome
	extends AbstractBoundedChromosome<Integer, IntegerGene>
	implements
		NumericChromosome<Integer, IntegerGene>,
		Serializable
{
	@Serial
	private static final long serialVersionUID = 3L;

	/**
	 * Create a new chromosome from the given {@code genes} and the allowed
	 * length range of the chromosome.
	 *
	 * @since 4.0
	 *
	 * @param genes the genes that form the chromosome.
	 * @param lengthRange the allowed length range of the chromosome
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the length of the gene sequence is
	 *         empty, doesn't match with the allowed length range, the minimum
	 *         or maximum of the range is smaller or equal zero or the given
	 *         range size is zero.
	 */
	protected IntegerChromosome(
		final ISeq<IntegerGene> genes,
		final IntRange lengthRange
	) {
		super(genes, lengthRange);
	}

	@Override
	public IntegerChromosome newInstance(final ISeq<IntegerGene> genes) {
		return new IntegerChromosome(genes, lengthRange());
	}

	@Override
	public IntegerChromosome newInstance() {
		return of(_min, _max, lengthRange());
	}

	/**
	 * Maps the gene alleles of this chromosome, given as {@code int[]} array,
	 * by applying the given mapper function {@code f}. The mapped gene values
	 * are then wrapped into a newly created chromosome.
	 *
	 * <pre>{@code
	 * final IntegerChromosome chromosome = ...;
	 * final IntegerChromosome halved = chromosome.map(Main::half);
	 *
	 * static int[] half(final int[] values) {
	 *     for (int i = 0; i < values.length; ++i) {
	 *         values[i] /= 2;
	 *     }
	 *     return values;
	 * }
	 * }</pre>
	 *
	 * @since 6.1
	 *
	 * @param f the mapper function
	 * @return a newly created chromosome with the mapped gene values
	 * @throws NullPointerException if the mapper function is {@code null}.
	 * @throws IllegalArgumentException if the length of the mapped
	 *         {@code int[]} array is empty or doesn't match with the allowed
	 *         length range
	 */
	public IntegerChromosome map(final Function<? super int[], int[]> f) {
		requireNonNull(f);

		final var range = IntRange.of(_min, _max);
		final var genes = IntStream.of(f.apply(toArray()))
			.mapToObj(v -> IntegerGene.of(v, range))
			.collect(ISeq.toISeq());

		return newInstance(genes);
	}

	/**
	 * Returns a sequential stream of the alleles with this chromosome as its
	 * source.
	 *
	 * @since 4.3
	 *
	 * @return a sequential stream of alleles
	 */
	public IntStream intStream() {
		return IntStream.range(0, length()).map(this::intValue);
	}

	/**
	 * Returns an int array containing all the elements in this chromosome in
	 * proper sequence.  If the chromosome fits in the specified array, it is
	 * returned therein. Otherwise, a new array is allocated with the length of
	 * this chromosome.
	 *
	 * @since 3.0
	 *
	 * @param array the array into which the elements of these chromosomes are
	 *        to be stored, if it is big enough; otherwise, a new array is
	 *        allocated for this purpose.
	 * @return an array containing the elements of this chromosome
	 * @throws NullPointerException if the given {@code array} is {@code null}
	 */
	public int[] toArray(final int[] array) {
		final int[] a = array.length >= length() ? array : new int[length()];
		for (int i = length(); --i >= 0;) {
			a[i] = intValue(i);
		}

		return a;
	}

	/**
	 * Returns an int array containing all the elements in this chromosome in
	 * proper sequence.
	 *
	 * @since 3.0
	 *
	 * @return an array containing the elements of this chromosome
	 */
	public int[] toArray() {
		return toArray(new int[length()]);
	}


	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	/**
	 * Create a new {@code IntegerChromosome} with the given genes.
	 *
	 * @param genes the genes of the chromosome.
	 * @return a new chromosome with the given genes.
	 * @throws IllegalArgumentException if the length of the genes array is
	 *         empty or the given {@code genes} doesn't have the same range.
	 */
	public static IntegerChromosome of(final IntegerGene... genes) {
		checkGeneRange(Stream.of(genes).map(IntegerGene::range));
		return new IntegerChromosome(ISeq.of(genes), IntRange.of(genes.length));
	}

	/**
	 * Create a new {@code IntegerChromosome} with the given genes.
	 *
	 * @since 4.3
	 *
	 * @param genes the genes of the chromosome.
	 * @return a new chromosome with the given genes.
	 * @throws NullPointerException if the given {@code genes} are {@code null}
	 * @throws IllegalArgumentException if the of the genes iterable is empty or
	 *         the given {@code genes} doesn't have the same range.
	 */
	public static IntegerChromosome of(final Iterable<IntegerGene> genes) {
		final ISeq<IntegerGene> values = ISeq.of(genes);
		checkGeneRange(values.stream().map(IntegerGene::range));
		return new IntegerChromosome(values, IntRange.of(values.length()));
	}

	/**
	 * Create a new random chromosome.
	 *
	 * @since 4.0
	 *
	 * @param min the min value of the {@link IntegerGene}s (inclusively).
	 * @param max the max value of the {@link IntegerGene}s (exclusively).
	 * @param lengthRange the allowed length range of the chromosome.
	 * @return a new {@code IntegerChromosome} with the given parameter
	 * @throws IllegalArgumentException if the length of the gene sequence is
	 *         empty, doesn't match with the allowed length range, the minimum
	 *         or maximum of the range is smaller or equal zero or the given
	 *         range size is zero.
	 * @throws IllegalArgumentException if {@code max} is greater than
	 *         or equal to {@code min}
	 * @throws NullPointerException if the given {@code lengthRange} is
	 *         {@code null}
	 */
	public static IntegerChromosome of(
		final int min,
		final int max,
		final IntRange lengthRange
	) {
		final ISeq<IntegerGene> values = IntegerGene.seq(min, max, lengthRange);
		return new IntegerChromosome(values, lengthRange);
	}

	/**
	 * Create a new random {@code IntegerChromosome}.
	 *
	 * @param min the min value of the {@link IntegerGene}s (inclusively).
	 * @param max the max value of the {@link IntegerGene}s (exclusively).
	 * @param length the length of the chromosome.
	 * @return a new random {@code IntegerChromosome}
	 * @throws IllegalArgumentException if the length is smaller than one
	 * @throws IllegalArgumentException if {@code max} is greater than
	 *         or equal to {@code min}
	 */
	public static IntegerChromosome of(
		final int min,
		final int max,
		final int length
	) {
		return of(min, max, IntRange.of(length));
	}

	/**
	 * Create a new random chromosome.
	 *
	 * @since 4.0
	 *
	 * @param range the integer range of the chromosome.
	 * @param lengthRange the allowed length range of the chromosome.
	 * @return a new {@code IntegerChromosome} with the given parameter
	 * @throws IllegalArgumentException if the length of the gene sequence is
	 *         empty, doesn't match with the allowed length range, the minimum
	 *         or maximum of the range is smaller or equal zero or the given
	 *         range size is zero.
	 * @throws NullPointerException if the given {@code lengthRange} is
	 *         {@code null}
	 * @throws IllegalArgumentException if {@code max} is greater than
	 *         or equal to {@code min}
	 */
	public static IntegerChromosome of(
		final IntRange range,
		final IntRange lengthRange
	) {
		return of(range.min(), range.max(), lengthRange);
	}

	/**
	 * Create a new random {@code IntegerChromosome}.
	 *
	 * @since 3.2
	 *
	 * @param range the integer range of the chromosome.
	 * @param length the length of the chromosome.
	 * @return a new random {@code IntegerChromosome}
	 * @throws NullPointerException if the given {@code range} is {@code null}
	 * @throws IllegalArgumentException if the length is smaller than one
	 * @throws IllegalArgumentException if {@code max} is greater than
	 *         or equal to {@code min}
	 */
	public static IntegerChromosome of(final IntRange range, final int length) {
		return of(range.min(), range.max(), length);
	}

	/**
	 * Create a new random {@code IntegerChromosome} of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (exclusively).
	 * @return a new random {@code IntegerChromosome} of length one
	 * @throws IllegalArgumentException if {@code max} is greater than
	 *         or equal to {@code min}
	 */
	public static IntegerChromosome of(final int min, final int max) {
		return of(min, max, 1);
	}

	/**
	 * Create a new random {@code IntegerChromosome} of length one.
	 *
	 * @since 3.2
	 *
	 * @param range the integer range of the chromosome.
	 * @return a new random {@code IntegerChromosome} of length one
	 * @throws NullPointerException if the given {@code range} is {@code null}
	 * @throws IllegalArgumentException if {@code max} is greater than
	 *         or equal to {@code min}
	 */
	public static IntegerChromosome of(final IntRange range) {
		return of(range.min(), range.max(), 1);
	}



	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.INTEGER_CHROMOSOME, this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		writeInt(length(), out);
		writeInt(lengthRange().min(), out);
		writeInt(lengthRange().max(), out);
		writeInt(_min, out);
		writeInt(_max, out);

		for (int i = 0, n = length(); i < n; ++i) {
			writeInt(intValue(i), out);
		}
	}

	static IntegerChromosome read(final DataInput in) throws IOException {
		final var length = readInt(in);
		final var lengthRange = IntRange.of(readInt(in), readInt(in));
		final var min = readInt(in);
		final var max = readInt(in);

		final MSeq<IntegerGene> values = MSeq.ofLength(length);
		for (int i = 0; i < length; ++i) {
			values.set(i, IntegerGene.of(readInt(in), min, max));
		}

		return new IntegerChromosome(values.toISeq(), lengthRange);
	}

}
