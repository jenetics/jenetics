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
import static io.jenetics.internal.util.SerialIO.readLong;
import static io.jenetics.internal.util.SerialIO.writeInt;
import static io.jenetics.internal.util.SerialIO.writeLong;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.LongRange;
import io.jenetics.util.MSeq;

/**
 * Numeric chromosome implementation which holds 64-bit integer numbers.
 *
 * @see LongGene
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.6
 * @version 6.1
 */
public class LongChromosome
	extends AbstractBoundedChromosome<Long, LongGene>
	implements
		NumericChromosome<Long, LongGene>,
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
	 *         or maximum of the range is smaller or equal zero, or the given
	 *         range size is zero.
	 */
	protected LongChromosome(
		final ISeq<LongGene> genes,
		final IntRange lengthRange
	) {
		super(genes, lengthRange);
	}

	@Override
	public LongChromosome newInstance(final ISeq<LongGene> genes) {
		return new LongChromosome(genes, lengthRange());
	}

	@Override
	public LongChromosome newInstance() {
		return of(_min, _max, lengthRange());
	}

	/**
	 * Maps the gene alleles of this chromosome, given as {@code long[]} array,
	 * by applying the given mapper function {@code f}. The mapped gene values
	 * are then wrapped into a newly created chromosome.
	 *
	 * {@snippet lang="java":
	 * final LongChromosome chromosome = null; // @replace substring='null' replacement="..."
	 * final LongChromosome halved = chromosome.map(Main::half);
	 *
	 * static long[] half(final long[] values) {
	 *     for (int i = 0; i < values.length; ++i) {
	 *         values[i] /= 2;
	 *     }
	 *     return values;
	 * }
	 * }
	 *
	 * @since 6.1
	 *
	 * @param f the mapper function
	 * @return a newly created chromosome with the mapped gene values
	 * @throws NullPointerException if the mapper function is {@code null}.
	 * @throws IllegalArgumentException if the length of the mapped
	 *         {@code long[]} array is empty or doesn't match with the allowed
	 *         length range
	 */
	public LongChromosome map(final Function<? super long[], long[]> f) {
		requireNonNull(f);

		final var range = LongRange.of(_min, _max);
		final var genes = LongStream.of(f.apply(toArray()))
			.mapToObj(v -> LongGene.of(v, range))
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
	public LongStream longStream() {
		return IntStream.range(0, length()).mapToLong(this::longValue);
	}

	/**
	 * Returns a long array containing all the elements in this chromosome
	 * in a proper sequence.  If the chromosome fits in the specified array, it is
	 * returned therein. Otherwise, a new array is allocated with the length of
	 * this chromosome.
	 *
	 * @since 3.0
	 *
	 * @param array the array into which the elements of these chromosomes are to
	 *        be stored, if it is big enough; otherwise, a new array is
	 *        allocated for this purpose.
	 * @return an array containing the elements of this chromosome
	 * @throws NullPointerException if the given {@code array} is {@code null}
	 */
	public long[] toArray(final long[] array) {
		final long[] a = array.length >= length() ? array : new long[length()];
		for (int i = length(); --i >= 0;) {
			a[i] = longValue(i);
		}

		return a;
	}

	/**
	 * Returns a long array containing all the elements in this chromosome
	 * in a proper sequence.
	 *
	 * @since 3.0
	 *
	 * @return an array containing the elements of this chromosome
	 */
	public long[] toArray() {
		return toArray(new long[length()]);
	}


	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	/**
	 * Create a new {@code LongChromosome} with the given genes.
	 *
	 * @param genes the genes of the chromosome.
	 * @return a new chromosome with the given genes.
	 * @throws NullPointerException if the given {@code genes} are {@code null}
	 * @throws IllegalArgumentException if the length of the genes array is
	 *         empty or the given {@code genes} doesn't have the same range.
	 */
	public static LongChromosome of(final LongGene... genes) {
		checkGeneRange(Stream.of(genes).map(LongGene::range));
		return new LongChromosome(ISeq.of(genes), IntRange.of(genes.length));
	}

	/**
	 * Create a new {@code LongChromosome} with the given genes.
	 *
	 * @since 4.3
	 *
	 * @param genes the genes of the chromosome.
	 * @return a new chromosome with the given genes.
	 * @throws NullPointerException if the given {@code genes} are {@code null}
	 * @throws IllegalArgumentException if the of the genes iterable is empty or
	 *         the given {@code genes} doesn't have the same range.
	 */
	public static LongChromosome of(final Iterable<LongGene> genes) {
		final ISeq<LongGene> values = ISeq.of(genes);
		checkGeneRange(values.stream().map(LongGene::range));
		return new LongChromosome(values, IntRange.of(values.length()));
	}

	/**
	 * Create a new random chromosome.
	 *
	 * @since 4.0
	 *
	 * @param min the min value of the {@link LongGene}s (inclusively).
	 * @param max the max value of the {@link LongGene}s (exclusively).
	 * @param lengthRange the allowed length range of the chromosome.
	 * @return a new {@code IntegerChromosome} with the given parameter
	 * @throws IllegalArgumentException if the length of the gene sequence is
	 *         empty, doesn't match with the allowed length range, the minimum
	 *         or maximum of the range is smaller or equal zero, or the given
	 *         range size is zero.
	 * @throws IllegalArgumentException if {@code max} is greater than
	 *         or equal to {@code min}
	 * @throws NullPointerException if the given {@code lengthRange} is
	 *         {@code null}
	 */
	public static LongChromosome of(
		final long min,
		final long max,
		final IntRange lengthRange
	) {
		final ISeq<LongGene> values = LongGene.seq(min, max, lengthRange);
		return new LongChromosome(values, lengthRange);
	}

	/**
	 * Create a new random {@code LongChromosome}.
	 *
	 * @param min the min value of the {@link LongGene}s (inclusively).
	 * @param max the max value of the {@link LongGene}s (exclusively).
	 * @param length the length of the chromosome.
	 * @return a new {@code LongChromosome} with the given gene parameters.
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 * @throws IllegalArgumentException if {@code max} is greater than
	 *         or equal to {@code min}
	 */
	public static LongChromosome of(
		final long min,
		final long max,
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
	 * @return a new {@code LongChromosome} with the given parameter
	 * @throws IllegalArgumentException if the length of the gene sequence is
	 *         empty, doesn't match with the allowed length range, the minimum
	 *         or maximum of the range is smaller or equal zero, or the given
	 *         range size is zero.
	 * @throws IllegalArgumentException if {@code max} is greater than
	 *         or equal to {@code min}
	 * @throws NullPointerException if the given {@code lengthRange} is
	 *         {@code null}
	 */
	public static LongChromosome of(
		final LongRange range,
		final IntRange lengthRange
	) {
		return of(range.min(), range.max(), lengthRange);
	}

	/**
	 * Create a new random {@code LongChromosome}.
	 *
	 * @since 3.2
	 *
	 * @param range the long range of the chromosome.
	 * @param length the length of the chromosome.
	 * @return a new random {@code LongChromosome}
	 * @throws NullPointerException if the given {@code range} is {@code null}
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 * @throws IllegalArgumentException if {@code max} is greater than
	 *         or equal to {@code min}
	 */
	public static LongChromosome of(final LongRange range, final int length) {
		return of(range.min(), range.max(), length);
	}

	/**
	 * Create a new random {@code LongChromosome} of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (exclusively).
	 * @return a new {@code LongChromosome} with the given gene parameters.
	 * @throws IllegalArgumentException if {@code max} is greater than
	 *         or equal to {@code min}
	 */
	public static LongChromosome of(final long min, final long max) {
		return of(min, max, 1);
	}

	/**
	 * Create a new random {@code LongChromosome} of length one.
	 *
	 * @since 3.2
	 *
	 * @param range the long range of the chromosome.
	 * @return a new random {@code LongChromosome} of length one
	 * @throws NullPointerException if the given {@code range} is {@code null}
	 * @throws IllegalArgumentException if {@code max} is greater than
	 *         or equal to {@code min}
	 */
	public static LongChromosome of(final LongRange range) {
		return of(range.min(), range.max());
	}



	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.LONG_CHROMOSOME, this);
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
		writeLong(_min, out);
		writeLong(_max, out);

		for (int i = 0, n = length(); i < n; ++i) {
			writeLong(longValue(i), out);
		}
	}

	static LongChromosome read(final DataInput in) throws IOException {
		final var length = readInt(in);
		final var lengthRange = IntRange.of(readInt(in), readInt(in));
		final var min = readLong(in);
		final var max = readLong(in);

		final MSeq<LongGene> values = MSeq.ofLength(length);
		for (int i = 0; i < length; ++i) {
			values.set(i, LongGene.of(readLong(in), min, max));
		}

		return new LongChromosome(values.toISeq(), lengthRange);
	}

}
