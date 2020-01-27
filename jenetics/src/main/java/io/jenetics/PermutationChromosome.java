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

import static java.lang.String.format;
import static io.jenetics.internal.util.Bits.getAndSet;
import static io.jenetics.internal.util.SerialIO.readInt;
import static io.jenetics.internal.util.SerialIO.writeInt;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.jenetics.internal.math.Combinatorics;
import io.jenetics.internal.util.Arrays;
import io.jenetics.internal.util.Bits;
import io.jenetics.internal.util.Requires;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.MSeq;

/**
 * This chromosome can be used to model permutations of a given (sub) set of
 * alleles.
 *
 * <pre>{@code
 * final ISeq<String> alleles = ISeq.of("one", "two", "three", "four", "five");
 *
 * // Create a new randomly permuted chromosome from the given alleles.
 * final PermutationChromosome<String> ch1 = PermutationChromosome.of(alleles);
 * System.out.println(ch1);
 * System.out.println(ch1.newInstance());
 *
 * // Create a new randomly permuted chromosome from a subset of the given alleles.
 * final PermutationChromosome<String> ch2 = PermutationChromosome.of(alleles, 3);
 * System.out.println(ch2);
 * System.out.println(ch2.newInstance());
 *
 * // Console output:
 * // > three|two|one|five|four
 * // > two|one|four|five|three
 * // > three|one|five
 * // > five|three|one
 * }</pre>
 *
 * Usable {@link Alterer} for this chromosome:
 * <ul>
 *     <li>{@link PartiallyMatchedCrossover}</li>
 *     <li>{@link SwapMutator}</li>
 * </ul>
 * <p>
 * <em><b>Implementation note 1:</b>
 * The factory methods of the {@link AbstractChromosome} has been overridden so
 * that no invalid permutation will be created.
 * </em>
 *
 * <p>
 * <em><b>Implementation note 2:</b>
 * This class uses an algorithm for choosing subsets which is based on a
 * FORTRAN77 version, originally implemented by Albert Nijenhuis, Herbert Wilf.
 * The actual Java implementation is based on the  C++ version by John Burkardt.
 * </em>
 * <br>
 * <em><a href="https://people.sc.fsu.edu/~burkardt/c_src/subset/subset.html">
 *  Reference:</a></em>
 *   Albert Nijenhuis, Herbert Wilf,
 *   Combinatorial Algorithms for Computers and Calculators,
 *   Second Edition,
 *   Academic Press, 1978,
 *   ISBN: 0-12-519260-6,
 *   LC: QA164.N54.
 * </p>
 *
 *
 * @see EnumGene
 * @see PartiallyMatchedCrossover
 * @see SwapMutator
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 6.0
 */
public final class PermutationChromosome<T>
	extends AbstractChromosome<EnumGene<T>>
	implements Serializable
{
	private static final long serialVersionUID = 2L;

	private final ISeq<T> _validAlleles;

	// Private primary constructor.
	private PermutationChromosome(
		final ISeq<EnumGene<T>> genes,
		final Boolean valid
	) {
		super(genes);

		assert !genes.isEmpty();
		_validAlleles = genes.get(0).validAlleles();
		_valid = valid;
	}

	/**
	 * Create a new {@code PermutationChromosome} from the given {@code genes}.
	 * If the given {@code genes} sequence contains duplicate entries, the
	 * created {@code PermutationChromosome} will be invalid
	 * ({@code ch.isValid() == false}).
	 *
	 * @param genes the enum genes the new chromosome consists of
	 * @throws NullPointerException if the given {@code genes} are null
	 * @throws IllegalArgumentException if the given {@code genes} sequence is
	 *         empty
	 */
	public PermutationChromosome(final ISeq<EnumGene<T>> genes) {
		this(genes, null);
	}

	/**
	 * Return the sequence of valid alleles of this chromosome.
	 *
	 * @return the sequence of valid alleles of this chromosome
	 */
	public ISeq<T> validAlleles() {
		return _validAlleles;
	}

	/**
	 * Check if this chromosome represents still a valid permutation (or subset)
	 * of the given valid alleles.
	 */
	@Override
	public boolean isValid() {
		if (_valid == null) {
			final byte[] check = Bits.newArray(_validAlleles.length());
			_valid = _genes.forAll(g -> !getAndSet(check, g.alleleIndex()));
		}

		return _valid;
	}

	/**
	 * Create a new, <em>random</em> chromosome.
	 */
	@Override
	public PermutationChromosome<T> newInstance() {
		return of(_validAlleles, length());
	}

	@Override
	public PermutationChromosome<T> newInstance(final ISeq<EnumGene<T>> genes) {
		return new PermutationChromosome<>(genes);
	}

	@Override
	public String toString() {
		return _genes.stream()
			.map(g -> g.allele().toString())
			.collect(Collectors.joining("|"));
	}

	/**
	 * Create a new, random chromosome with the given valid alleles and the
	 * desired length.
	 * <p>
	 * The following example shows how to create a {@code PermutationChromosome}
	 * for encoding a sub-set problem (of a fixed {@code length}).
	 * <pre>{@code
	 * final ISeq<String> basicSet = ISeq.of("a", "b", "c", "d", "e", "f");
	 *
	 * // The chromosome has a length of 3 and will only contain values from the
	 * // given basic-set, with no duplicates.
	 * final PermutationChromosome<String> ch = PermutationChromosome.of(basicSet, 3);
	 * }</pre>
	 *
	 * @since 3.4
	 *
	 * @param <T> the allele type
	 * @param alleles the base-set of the valid alleles
	 * @param length the length of the created chromosomes
	 * @return a new chromosome with the given valid alleles and the desired
	 *         length
	 * @throws IllegalArgumentException if {@code alleles.size() < length},
	 *         {@code length <= 0} or {@code alleles.size()*length} will
	 *         cause an integer overflow.
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T> PermutationChromosome<T> of(
		final ISeq<? extends T> alleles,
		final int length
	) {
		Requires.positive(length);
		if (length > alleles.size()) {
			throw new IllegalArgumentException(format(
				"The sub-set size must be be greater then the base-set: %d > %d",
				length, alleles.size()
			));
		}

		final int[] subset = Arrays.shuffle(Combinatorics.subset(alleles.size(), length));
		final ISeq<EnumGene<T>> genes = IntStream.of(subset)
			.mapToObj(i -> EnumGene.<T>of(i, alleles))
			.collect(ISeq.toISeq());

		return new PermutationChromosome<>(genes, true);
	}

	/**
	 * Create a new, random chromosome with the given valid alleles.
	 *
	 * @param <T> the gene type of the chromosome
	 * @param alleles the valid alleles used for this permutation arrays.
	 * @return a new chromosome with the given alleles
	 * @throws IllegalArgumentException if the given allele sequence is empty.
	 */
	public static <T> PermutationChromosome<T>
	of(final ISeq<? extends T> alleles) {
		return of(alleles, alleles.size());
	}

	/**
	 * Create a new, random chromosome with the given valid alleles.
	 *
	 * @since 2.0
	 *
	 * @param <T> the gene type of the chromosome
	 * @param alleles the valid alleles used for this permutation arrays.
	 * @return a new chromosome with the given alleles
	 * @throws IllegalArgumentException if the given allele array is empty.
	 * @throws NullPointerException if one of the alleles is {@code null}
	 */
	@SafeVarargs
	public static <T> PermutationChromosome<T> of(final T... alleles) {
		return of(ISeq.of(alleles));
	}

	/**
	 * Create a integer permutation chromosome with the given length.
	 *
	 * @param length the chromosome length.
	 * @return a integer permutation chromosome with the given length.
	 * @throws IllegalArgumentException if {@code length <= 0}.
	 */
	public static PermutationChromosome<Integer> ofInteger(final int length) {
		return ofInteger(0, Requires.positive(length));
	}

	/**
	 * Create an integer permutation chromosome with the given range.
	 *
	 * @since 2.0
	 *
	 * @param start the start of the integer range (inclusively) of the returned
	 *        chromosome.
	 * @param end the end of the integer range (exclusively) of the returned
	 *        chromosome.
	 * @return a integer permutation chromosome with the given integer range
	 *         values.
	 * @throws IllegalArgumentException if {@code start >= end} or
	 *         {@code start <= 0}
	 */
	public static PermutationChromosome<Integer>
	ofInteger(final int start, final int end) {
		if (end <= start) {
			throw new IllegalArgumentException(format(
				"end <= start: %d <= %d", end, start
			));
		}

		return ofInteger(IntRange.of(start, end), end - start);
	}

	/**
	 * Create an integer permutation chromosome with the given range and length
	 *
	 * @since 3.4
	 *
	 * @param range the value range
	 * @param length the chromosome length
	 * @return a new integer permutation chromosome
	 * @throws NullPointerException if the given {@code range} is {@code null}
	 * @throws IllegalArgumentException if
	 *         {@code range.getMax() - range.getMin() < length},
	 *         {@code length <= 0} or
	 *         {@code (range.getMax() - range.getMin())*length} will cause an
	 *         integer overflow.
	 */
	public static PermutationChromosome<Integer>
	ofInteger(final IntRange range, final int length) {
		return of(
			range.stream().boxed().collect(ISeq.toISeq()),
			length
		);
	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.PERMUTATION_CHROMOSOME, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final ObjectOutput out) throws IOException {
		out.writeObject(_validAlleles);
		for (EnumGene<?> gene : _genes) {
			writeInt(gene.alleleIndex(), out);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	static PermutationChromosome read(final ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		final ISeq validAlleles = (ISeq)in.readObject();
		final MSeq genes = MSeq.ofLength(validAlleles.length());
		for (int i = 0, n = validAlleles.length(); i < n; ++i) {
			genes.set(i, new EnumGene(readInt(in), validAlleles));
		}

		return new PermutationChromosome(genes.toISeq());
	}

}
