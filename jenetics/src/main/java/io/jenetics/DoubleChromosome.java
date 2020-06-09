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
import java.io.Serializable;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.MSeq;

/**
 * Numeric chromosome implementation which holds 64 bit floating point numbers.
 *
 * @see DoubleGene
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.6
 * @version 6.1
 */
public class DoubleChromosome
	extends AbstractBoundedChromosome<Double, DoubleGene>
	implements
		NumericChromosome<Double, DoubleGene>,
		Serializable
{
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
	protected DoubleChromosome(
		final ISeq<DoubleGene> genes,
		final IntRange lengthRange
	) {
		super(genes, lengthRange);
	}

	@Override
	public DoubleChromosome newInstance(final ISeq<DoubleGene> genes) {
		return new DoubleChromosome(genes, lengthRange());
	}

	@Override
	public DoubleChromosome newInstance() {
		return of(_min, _max, lengthRange());
	}

	/**
	 * Maps the gene alleles of this chromosome, given as {@code double[]} array,
	 * by applying the given mapper function {@code f}. The mapped gene values
	 * are then wrapped into a newly created chromosome.
	 *
	 * <pre>{@code
	 * final DoubleChromosome chromosome = ...;
	 * final DoubleChromosome normalized = chromosome.map(Main::normalize);
	 *
	 * static double[] normalize(final double[] values) {
	 *     final double sum = sum(values);
	 *     for (int i = 0; i < values.length; ++i) {
	 *         values[i] /= sum;
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
	 *         {@code double[]} array is empty or doesn't match with the allowed
	 *         length range
	 */
	public DoubleChromosome map(final Function<? super double[], double[]> f) {
		requireNonNull(f);

		final var range = DoubleRange.of(_min, _max);
		final var genes = DoubleStream.of(f.apply(toArray()))
			.mapToObj(v -> DoubleGene.of(v, range))
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
	public DoubleStream doubleStream() {
		return IntStream.range(0, length()).mapToDouble(this::doubleValue);
	}

	/**
	 * Returns an double array containing all of the elements in this chromosome
	 * in proper sequence.  If the chromosome fits in the specified array, it is
	 * returned therein. Otherwise, a new array is allocated with the length of
	 * this chromosome.
	 *
	 * @since 3.0
	 *
	 * @param array the array into which the elements of this chromosomes are to
	 *        be stored, if it is big enough; otherwise, a new array is
	 *        allocated for this purpose.
	 * @return an array containing the elements of this chromosome
	 * @throws NullPointerException if the given {@code array} is {@code null}
	 */
	public double[] toArray(final double[] array) {
		final double[] a = array.length >= length()
			? array
			: new double[length()];

		for (int i = length(); --i >= 0;) {
			a[i] = doubleValue(i);
		}

		return a;
	}

	/**
	 * Returns an double array containing all of the elements in this chromosome
	 * in proper sequence.
	 *
	 * @since 3.0
	 *
	 * @return an array containing the elements of this chromosome
	 */
	public double[] toArray() {
		return toArray(new double[length()]);
	}


	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	/**
	 * Create a new {@code DoubleChromosome} with the given genes.
	 *
	 * @param genes the genes of the chromosome.
	 * @return a new chromosome with the given genes.
	 * @throws IllegalArgumentException if the length of the genes array is
	 *         empty or the given {@code genes} doesn't have the same range.
	 * @throws NullPointerException if the given {@code genes} array is
	 *         {@code null}
	 */
	public static DoubleChromosome of(final DoubleGene... genes) {
		checkGeneRange(Stream.of(genes).map(DoubleGene::range));
		return new DoubleChromosome(ISeq.of(genes), IntRange.of(genes.length));
	}

	/**
	 * Create a new {@code DoubleChromosome} with the given genes.
	 *
	 * @since 4.3
	 *
	 * @param genes the genes of the chromosome.
	 * @return a new chromosome with the given genes.
	 * @throws NullPointerException if the given {@code genes} are {@code null}
	 * @throws IllegalArgumentException if the of the genes iterable is empty or
	 *         the given {@code genes} doesn't have the same range.
	 */
	public static DoubleChromosome of(final Iterable<DoubleGene> genes) {
		final ISeq<DoubleGene> values = ISeq.of(genes);
		checkGeneRange(values.stream().map(DoubleGene::range));
		return new DoubleChromosome(values, IntRange.of(values.length()));
	}

	/**
	 * Create a new random chromosome.
	 *
	 * @since 4.0
	 *
	 * @param min the min value of the {@link DoubleGene}s (inclusively).
	 * @param max the max value of the {@link DoubleGene}s (exclusively).
	 * @param lengthRange the allowed length range of the chromosome.
	 * @return a new {@code DoubleChromosome} with the given parameter
	 * @throws IllegalArgumentException if the length of the gene sequence is
	 *         empty, doesn't match with the allowed length range, the minimum
	 *         or maximum of the range is smaller or equal zero or the given
	 *         range size is zero.
	 * @throws NullPointerException if the given {@code lengthRange} is
	 *         {@code null}
	 */
	public static DoubleChromosome of(
		final double min,
		final double max,
		final IntRange lengthRange
	) {
		final ISeq<DoubleGene> genes = DoubleGene.seq(min, max, lengthRange);
		return new DoubleChromosome(genes, lengthRange);
	}

	/**
	 * Create a new random {@code DoubleChromosome}.
	 *
	 * @param min the min value of the {@link DoubleGene}s (inclusively).
	 * @param max the max value of the {@link DoubleGene}s (exclusively).
	 * @param length the length of the chromosome.
	 * @return a new {@code DoubleChromosome} with the given parameter
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public static DoubleChromosome of(
		final double min,
		final double max,
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
	 * @return a new {@code DoubleChromosome} with the given parameter
	 * @throws IllegalArgumentException if the length of the gene sequence is
	 *         empty, doesn't match with the allowed length range, the minimum
	 *         or maximum of the range is smaller or equal zero or the given
	 *         range size is zero.
	 * @throws NullPointerException if the given {@code lengthRange} is
	 *         {@code null}
	 */
	public static DoubleChromosome of(
		final DoubleRange range,
		final IntRange lengthRange
	) {
		return of(range.min(), range.max(), lengthRange);
	}

	/**
	 * Create a new random {@code DoubleChromosome}.
	 *
	 * @since 3.2
	 *
	 * @param range the integer range of the chromosome.
	 * @param length the length of the chromosome.
	 * @return a new random {@code DoubleChromosome}
	 * @throws NullPointerException if the given {@code range} is {@code null}
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public static DoubleChromosome of(final DoubleRange range, final int length) {
		return of(range.min(), range.max(), length);
	}

	/**
	 * Create a new random {@code DoubleChromosome} of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (exclusively).
	 * @return a new {@code DoubleChromosome} with the given parameter
	 */
	public static DoubleChromosome of(final double min, final double max) {
		return of(min, max, 1);
	}

	/**
	 * Create a new random {@code DoubleChromosome} of length one.
	 *
	 * @since 3.2
	 *
	 * @param range the double range of the chromosome.
	 * @return a new random {@code DoubleChromosome} of length one
	 * @throws NullPointerException if the given {@code range} is {@code null}
	 */
	public static DoubleChromosome of(final DoubleRange range) {
		return of(range.min(), range.max());
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.DOUBLE_CHROMOSOME, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		writeInt(length(), out);
		writeInt(lengthRange().min(), out);
		writeInt(lengthRange().max(), out);
		out.writeDouble(_min);
		out.writeDouble(_max);

		for (int i = 0, n = length(); i < n; ++i) {
			out.writeDouble(doubleValue(i));
		}
	}

	static DoubleChromosome read(final DataInput in) throws IOException {
		final var length = readInt(in);
		final var lengthRange = IntRange.of(readInt(in), readInt(in));
		final var min = in.readDouble();
		final var max = in.readDouble();

		final MSeq<DoubleGene> values = MSeq.ofLength(length);
		for (int i = 0; i < length; ++i) {
			values.set(i, DoubleGene.of(in.readDouble(), min, max));
		}

		return new DoubleChromosome(values.toISeq(), lengthRange);
	}

}
