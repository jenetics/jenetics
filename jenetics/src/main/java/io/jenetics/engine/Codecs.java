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
package io.jenetics.engine;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import io.jenetics.AnyChromosome;
import io.jenetics.AnyGene;
import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.EnumGene;
import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.LongChromosome;
import io.jenetics.LongGene;
import io.jenetics.PermutationChromosome;
import io.jenetics.internal.math.Combinatorics;
import io.jenetics.internal.util.Bits;
import io.jenetics.internal.util.Predicates;
import io.jenetics.internal.util.Requires;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.LongRange;

/**
 * This class contains factory methods for creating common  problem encodings.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.2
 * @version 5.2
 */
public final class Codecs {

	private Codecs() {}

	/**
	 * Return a scalar {@link InvertibleCodec} for the given range.
	 *
	 * @param domain the domain of the returned {@code Codec}
	 * @return a new scalar {@code Codec} with the given domain.
	 * @throws NullPointerException if the given {@code domain} is {@code null}
	 */
	public static InvertibleCodec<Integer, IntegerGene>
	ofScalar(final IntRange domain) {
		requireNonNull(domain);

		return InvertibleCodec.of(
			Genotype.of(IntegerChromosome.of(domain)),
			gt -> gt.chromosome().gene().allele(),
			val -> Genotype.of(IntegerChromosome.of(IntegerGene.of(val, domain)))
		);
	}

	/**
	 * Return a scalar {@link InvertibleCodec} for the given range.
	 *
	 * @param domain the domain of the returned {@code Codec}
	 * @return a new scalar {@code Codec} with the given domain.
	 * @throws NullPointerException if the given {@code domain} is {@code null}
	 */
	public static InvertibleCodec<Long, LongGene>
	ofScalar(final LongRange domain) {
		requireNonNull(domain);

		return InvertibleCodec.of(
			Genotype.of(LongChromosome.of(domain)),
			gt -> gt.gene().allele(),
			val -> Genotype.of(LongChromosome.of(LongGene.of(val, domain)))
		);
	}

	/**
	 * Return a scalar {@link InvertibleCodec} for the given range.
	 *
	 * @param domain the domain of the returned {@code Codec}
	 * @return a new scalar {@code Codec} with the given domain.
	 * @throws NullPointerException if the given {@code domain} is {@code null}
	 */
	public static InvertibleCodec<Double, DoubleGene>
	ofScalar(final DoubleRange domain) {
		requireNonNull(domain);

		return InvertibleCodec.of(
			Genotype.of(DoubleChromosome.of(domain)),
			gt -> gt.gene().allele(),
			val -> Genotype.of(DoubleChromosome.of(DoubleGene.of(val, domain)))
		);
	}

	/**
	 * Return a scala {@link Codec} with the given allele {@link Supplier} and
	 * allele {@code validator}. The {@code supplier} is responsible for
	 * creating new random alleles, and the {@code validator} can verify it.
	 * <p>
	 * The following example shows a codec which creates and verifies
	 * {@code BigInteger} objects.
	 * <pre>{@code
	 * final Codec<BigInteger, AnyGene<BigInteger>> codec = Codecs.of(
	 *     // Create new random 'BigInteger' object.
	 *     () -> {
	 *         final byte[] data = new byte[100];
	 *         RandomRegistry.getRandom().nextBytes(data);
	 *         return new BigInteger(data);
	 *     },
	 *     // Verify that bit 7 is set. (For illustration purpose.)
	 *     bi -> bi.testBit(7)
	 * );
	 * }</pre>
	 *
	 * @see AnyGene#of(Supplier, Predicate)
	 * @see AnyChromosome#of(Supplier, Predicate)
	 *
	 * @param <A> the allele type
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @param validator the validator used for validating the created gene. This
	 *        predicate is used in the {@link AnyGene#isValid()} method.
	 * @return a new {@code Codec} with the given parameters
	 * @throws NullPointerException if one of the parameters is {@code null}
	 */
	public static <A> Codec<A, AnyGene<A>> ofScalar(
		final Supplier<? extends A> supplier,
		final Predicate<? super A> validator
	) {
		return Codec.of(
			Genotype.of(AnyChromosome.of(supplier, validator)),
			gt -> gt.gene().allele()
		);
	}

	/**
	 * Return a scala {@link Codec} with the given allele {@link Supplier} and
	 * allele {@code validator}. The {@code supplier} is responsible for
	 * creating new random alleles.
	 *
	 * @see #ofScalar(Supplier, Predicate)
	 * @see AnyGene#of(Supplier)
	 * @see AnyChromosome#of(Supplier)
	 *
	 * @param <A> the allele type
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @return a new {@code Codec} with the given parameters
	 * @throws NullPointerException if the parameter is {@code null}
	 */
	public static <A> Codec<A, AnyGene<A>> ofScalar(
		final Supplier<? extends A> supplier
	) {
		return Codec.of(
			Genotype.of(AnyChromosome.of(supplier)),
			gt -> gt.gene().allele()
		);
	}

	/**
	 * Return a vector {@link InvertibleCodec} for the given range. All vector
	 * values are restricted by the same domain.
	 *
	 * @param domain the domain of the vector values
	 * @param length the vector length
	 * @return a new vector {@code Codec}
	 * @throws NullPointerException if the given {@code domain} is {@code null}
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public static InvertibleCodec<int[], IntegerGene> ofVector(
		final IntRange domain,
		final int length
	) {
		requireNonNull(domain);
		Requires.positive(length);

		return InvertibleCodec.of(
			Genotype.of(IntegerChromosome.of(domain, length)),
			gt -> gt.chromosome().as(IntegerChromosome.class).toArray(),
			val -> Genotype.of(
				IntegerChromosome.of(
					IntStream.of(val)
						.mapToObj(i -> IntegerGene.of(i, domain))
						.collect(ISeq.toISeq())
				)
			)
		);
	}

	/**
	 * Return a vector {@link InvertibleCodec} for the given range. All vector
	 * values are restricted by the same domain.
	 *
	 * @param domain the domain of the vector values
	 * @param length the vector length
	 * @return a new vector {@code Codec}
	 * @throws NullPointerException if the given {@code domain} is {@code null}
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public static InvertibleCodec<long[], LongGene> ofVector(
		final LongRange domain,
		final int length
	) {
		requireNonNull(domain);
		Requires.positive(length);

		return InvertibleCodec.of(
			Genotype.of(LongChromosome.of(domain, length)),
			gt -> gt.chromosome().as(LongChromosome.class).toArray(),
			val -> Genotype.of(
				LongChromosome.of(
					LongStream.of(val)
						.mapToObj(l -> LongGene.of(l, domain))
						.collect(ISeq.toISeq())
				)
			)
		);
	}

	/**
	 * Return a vector {@link InvertibleCodec} for the given range. All vector
	 * values are restricted by the same domain.
	 *
	 * @param domain the domain of the vector values
	 * @param length the vector length
	 * @return a new vector {@code Codec}
	 * @throws NullPointerException if the given {@code domain} is {@code null}
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public static InvertibleCodec<double[], DoubleGene> ofVector(
		final DoubleRange domain,
		final int length
	) {
		requireNonNull(domain);
		Requires.positive(length);

		return InvertibleCodec.of(
			Genotype.of(DoubleChromosome.of(domain, length)),
			gt -> gt.chromosome().as(DoubleChromosome.class).toArray(),
			val -> Genotype.of(
				DoubleChromosome.of(
					DoubleStream.of(val)
						.mapToObj(d -> DoubleGene.of(d, domain))
						.collect(ISeq.toISeq())
				)
			)
		);
	}

	/**
	 * Create a vector {@link InvertibleCodec} for the given ranges. Each vector
	 * element might have a different domain. The vector length is equal to the
	 * number of domains.
	 *
	 * @param domains the domain ranges
	 * @return a new vector {@code Codec}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the {@code domains} array is empty
	 */
	public static InvertibleCodec<int[], IntegerGene>
	ofVector(final IntRange... domains) {
		if (domains.length == 0) {
			throw new IllegalArgumentException("Domains must not be empty.");
		}

		final ISeq<IntegerChromosome> chromosomes = Stream.of(domains)
			.peek(Objects::requireNonNull)
			.map(IntegerGene::of)
			.map(IntegerChromosome::of)
			.collect(ISeq.toISeq());

		return InvertibleCodec.of(
			Genotype.of(chromosomes),
			gt -> {
				final int[] args = new int[gt.length()];
				for (int i = gt.length(); --i >= 0;) {
					args[i] = gt.get(i).gene().intValue();
				}
				return args;
			},
			val -> Genotype.of(
				IntStream.range(0, val.length)
					.mapToObj(i ->
						IntegerChromosome.of(IntegerGene.of(val[i], domains[i])))
					.collect(ISeq.toISeq())
			)
		);
	}

	/**
	 * Create a vector {@link InvertibleCodec} for the given ranges. Each vector
	 * element might have a different domain. The vector length is equal to the
	 * number of domains.
	 *
	 * @param domains the domain ranges
	 * @return a new vector {@code Codec}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the {@code domains} array is empty
	 */
	public static InvertibleCodec<long[], LongGene>
	ofVector(final LongRange... domains) {
		if (domains.length == 0) {
			throw new IllegalArgumentException("Domains must not be empty.");
		}

		final ISeq<LongChromosome> chromosomes = Stream.of(domains)
			.peek(Objects::requireNonNull)
			.map(LongGene::of)
			.map(LongChromosome::of)
			.collect(ISeq.toISeq());

		return InvertibleCodec.of(
			Genotype.of(chromosomes),
			gt -> {
				final long[] args = new long[gt.length()];
				for (int i = gt.length(); --i >= 0;) {
					args[i] = gt.get(i).gene().longValue();
				}
				return args;
			},
			val -> Genotype.of(
				IntStream.range(0, val.length)
					.mapToObj(i ->
						LongChromosome.of(LongGene.of(val[i], domains[i])))
					.collect(ISeq.toISeq())
			)
		);
	}

	/**
	 * Create a vector {@link InvertibleCodec} for the given ranges. Each vector
	 * element might have a different domain. The vector length is equal to the
	 * number of domains.
	 *
	 * @param domains the domain ranges
	 * @return a new vector {@code Codec}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the {@code domains} array is empty
	 */
	public static InvertibleCodec<double[], DoubleGene>
	ofVector(final DoubleRange... domains) {
		if (domains.length == 0) {
			throw new IllegalArgumentException("Domains must not be empty.");
		}

		final ISeq<DoubleChromosome> chromosomes = Stream.of(domains)
			.peek(Objects::requireNonNull)
			.map(DoubleGene::of)
			.map(DoubleChromosome::of)
			.collect(ISeq.toISeq());

		return InvertibleCodec.of(
			Genotype.of(chromosomes),
			gt -> {
				final double[] args = new double[gt.length()];
				for (int i = gt.length(); --i >= 0;) {
					args[i] = gt.get(i).gene().doubleValue();
				}
				return args;
			},
			val -> Genotype.of(
				IntStream.range(0, val.length)
					.mapToObj(i ->
						DoubleChromosome.of(DoubleGene.of(val[i], domains[i])))
					.collect(ISeq.toISeq())
			)
		);
	}

	/**
	 * Return a scala {@link Codec} with the given allele {@link Supplier},
	 * allele {@code validator} and {@code Chromosome} length. The
	 * {@code supplier} is responsible for creating new random alleles, and the
	 * {@code validator} can verify it.
	 * <p>
	 * The following example shows a codec which creates and verifies
	 * {@code BigInteger} object arrays.
	 * <pre>{@code
	 * final Codec<BigInteger[], AnyGene<BigInteger>> codec = Codecs.of(
	 *     // Create new random 'BigInteger' object.
	 *     () -> {
	 *         final byte[] data = new byte[100];
	 *         RandomRegistry.getRandom().nextBytes(data);
	 *         return new BigInteger(data);
	 *     },
	 *     // Verify that bit 7 is set. (For illustration purpose.)
	 *     bi -> bi.testBit(7),
	 *     // The 'Chromosome' length.
	 *     123
	 * );
	 * }</pre>
	 *
	 * @see AnyChromosome#of(Supplier, Predicate, Predicate, int)
	 *
	 * @param <A> the allele type
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @param alleleValidator the validator used for validating the created gene.
	 *        This predicate is used in the {@link AnyGene#isValid()} method.
	 * @param alleleSeqValidator the validator used for validating the created
	 *        chromosome. This predicate is used in the
	 *        {@link AnyChromosome#isValid()} method.
	 * @param length the vector length
	 * @return a new {@code Codec} with the given parameters
	 * @throws NullPointerException if one of the parameters is {@code null}
	 * @throws IllegalArgumentException if the length of the vector is smaller
	 *         than one.
	 */
	public static <A> Codec<ISeq<A>, AnyGene<A>> ofVector(
		final Supplier<? extends A> supplier,
		final Predicate<? super A> alleleValidator,
		final Predicate<? super ISeq<A>> alleleSeqValidator,
		final int length
	) {
		requireNonNull(supplier);
		requireNonNull(alleleSeqValidator);
		requireNonNull(alleleSeqValidator);
		Requires.positive(length);

		return Codec.of(
			Genotype.of(AnyChromosome
				.of(supplier, alleleValidator, alleleSeqValidator, length)),
			gt -> gt.chromosome().stream()
				.map(Gene::allele)
				.collect(ISeq.toISeq())
		);
	}

	/**
	 * Return a scala {@link Codec} with the given allele {@link Supplier},
	 * allele {@code validator} and {@code Chromosome} length. The
	 * {@code supplier} is responsible for creating new random alleles, and the
	 * {@code validator} can verify it.
	 *
	 * @param <A> the allele type
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @param validator the validator used for validating the created gene. This
	 *        predicate is used in the {@link AnyGene#isValid()} method.
	 * @param length the vector length
	 * @return a new {@code Codec} with the given parameters
	 * @throws NullPointerException if one of the parameters is {@code null}
	 * @throws IllegalArgumentException if the length of the vector is smaller
	 *         than one.
	 */
	public static <A> Codec<ISeq<A>, AnyGene<A>> ofVector(
		final Supplier<? extends A> supplier,
		final Predicate<? super A> validator,
		final int length
	) {
		return ofVector(
			supplier,
			validator,
			Predicates.True(),
			length
		);
	}

	/**
	 * Return a scala {@link Codec} with the given allele {@link Supplier} and
	 * {@code Chromosome} length. The {@code supplier} is responsible for
	 * creating new random alleles.
	 *
	 * @param <A> the allele type
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @param length the vector length
	 * @return a new {@code Codec} with the given parameters
	 * @throws NullPointerException if one of the parameters is {@code null}
	 * @throws IllegalArgumentException if the length of the vector is smaller
	 *         than one.
	 */
	public static <A> Codec<ISeq<A>, AnyGene<A>> ofVector(
		final Supplier<? extends A> supplier,
		final int length
	) {
		return ofVector(supplier, Predicates.TRUE, length);
	}

	/**
	 * Create a permutation {@link InvertibleCodec} of integer in the range
	 * {@code [0, length)}.
	 *
	 * @param length the number of permutation elements
	 * @return a permutation {@code Codec} of integers
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public static InvertibleCodec<int[], EnumGene<Integer>>
	ofPermutation(final int length) {
		Requires.positive(length);

		final PermutationChromosome<Integer> chromosome =
			PermutationChromosome.ofInteger(length);

		final Map<Integer, EnumGene<Integer>> genes = chromosome.stream()
			.collect(Collectors.toMap(EnumGene::allele, identity()));

		return InvertibleCodec.of(
			Genotype.of(chromosome),
			gt -> gt.chromosome().stream()
				.mapToInt(EnumGene::allele)
				.toArray(),
			val -> Genotype.of(
				new PermutationChromosome<>(
					IntStream.of(val)
						.mapToObj(genes::get)
						.collect(ISeq.toISeq())
				)
			)
		);
	}

	/**
	 * Create a permutation {@link InvertibleCodec} with the given alleles.
	 *
	 * @param alleles the alleles of the permutation
	 * @param <T> the allele type
	 * @return a new permutation {@code Codec}
	 * @throws IllegalArgumentException if the given allele array is empty
	 * @throws NullPointerException if one of the alleles is {@code null}
	 */
	public static <T> InvertibleCodec<ISeq<T>, EnumGene<T>>
	ofPermutation(final ISeq<? extends T> alleles) {
		if (alleles.isEmpty()) {
			throw new IllegalArgumentException(
				"Empty allele array is not allowed."
			);
		}

		final Map<T, EnumGene<T>> genes =
			IntStream.range(0, alleles.length())
				.mapToObj(i -> EnumGene.<T>of(i, alleles))
				.collect(Collectors.toMap(EnumGene::allele, identity()));

		return InvertibleCodec.of(
			Genotype.of(new PermutationChromosome<>(ISeq.of(genes.values()))),
			gt -> gt.chromosome().stream()
				.map(EnumGene::allele)
				.collect(ISeq.toISeq()),
			val -> Genotype.of(
				new PermutationChromosome<>(
					val.stream()
						.map(genes::get)
						.collect(ISeq.toISeq())
				)
			)
		);
	}

	/**
	 * Return a 2-dimensional matrix {@link InvertibleCodec} for the given range.
	 * All matrix values are restricted by the same domain. The dimension of the
	 * returned matrix is {@code int[rows][cols]}.
	 *
	 * @since 4.4
	 *
	 * @param domain the domain of the matrix values
	 * @param rows the number of rows of the matrix
	 * @param cols the number of columns of the matrix
	 * @return a new matrix {@code Codec}
	 * @throws NullPointerException if the given {@code domain} is {@code null}
	 * @throws IllegalArgumentException if the {@code rows} or {@code cols} are
	 *         smaller than one.
	 */
	public static InvertibleCodec<int[][], IntegerGene> ofMatrix(
		final IntRange domain,
		final int rows,
		final int cols
	) {
		requireNonNull(domain);
		Requires.positive(rows);
		Requires.positive(cols);

		return InvertibleCodec.of(
			Genotype.of(
				IntegerChromosome.of(domain, cols).instances()
					.limit(rows)
					.collect(ISeq.toISeq())
			),
			gt -> gt.stream()
				.map(ch -> ch.as(IntegerChromosome.class).toArray())
				.toArray(int[][]::new),
			matrix -> Genotype.of(
				Stream.of(matrix)
					.map(row ->
						IntegerChromosome.of(
							IntStream.of(row)
								.mapToObj(v -> IntegerGene.of(v, domain))
								.collect(ISeq.toISeq())
						))
					.collect(ISeq.toISeq())
			)
		);
	}

	/**
	 * Return a 2-dimensional matrix {@link InvertibleCodec} for the given range.
	 * All matrix values are restricted by the same domain. The dimension of the
	 * returned matrix is {@code long[rows][cols]}.
	 *
	 * @since 4.4
	 *
	 * @param domain the domain of the matrix values
	 * @param rows the number of rows of the matrix
	 * @param cols the number of columns of the matrix
	 * @return a new matrix {@code Codec}
	 * @throws NullPointerException if the given {@code domain} is {@code null}
	 * @throws IllegalArgumentException if the {@code rows} or {@code cols} are
	 *         smaller than one.
	 */
	public static InvertibleCodec<long[][], LongGene> ofMatrix(
		final LongRange domain,
		final int rows,
		final int cols
	) {
		requireNonNull(domain);
		Requires.positive(rows);
		Requires.positive(cols);

		return InvertibleCodec.of(
			Genotype.of(
				LongChromosome.of(domain, cols).instances()
					.limit(rows)
					.collect(ISeq.toISeq())
			),
			gt -> gt.stream()
				.map(ch -> ch.as(LongChromosome.class).toArray())
				.toArray(long[][]::new),
			matrix -> Genotype.of(
				Stream.of(matrix)
					.map(row ->
						LongChromosome.of(
							LongStream.of(row)
								.mapToObj(v -> LongGene.of(v, domain))
								.collect(ISeq.toISeq())
						))
					.collect(ISeq.toISeq())
			)
		);
	}

	/**
	 * Return a 2-dimensional matrix {@link InvertibleCodec} for the given range.
	 * All matrix values are restricted by the same domain. The dimension of the
	 * returned matrix is {@code double[rows][cols]}.
	 *
	 * @since 4.4
	 *
	 * @param domain the domain of the matrix values
	 * @param rows the number of rows of the matrix
	 * @param cols the number of columns of the matrix
	 * @return a new matrix {@code Codec}
	 * @throws NullPointerException if the given {@code domain} is {@code null}
	 * @throws IllegalArgumentException if the {@code rows} or {@code cols} are
	 *         smaller than one.
	 */
	public static InvertibleCodec<double[][], DoubleGene> ofMatrix(
		final DoubleRange domain,
		final int rows,
		final int cols
	) {
		requireNonNull(domain);
		Requires.positive(rows);
		Requires.positive(cols);

		return InvertibleCodec.of(
			Genotype.of(
				DoubleChromosome.of(domain, cols).instances()
					.limit(rows)
					.collect(ISeq.toISeq())
			),
			gt -> gt.stream()
				.map(ch -> ch.as(DoubleChromosome.class).toArray())
				.toArray(double[][]::new),
			matrix -> Genotype.of(
				Stream.of(matrix)
					.map(row ->
						DoubleChromosome.of(
							DoubleStream.of(row)
								.mapToObj(v -> DoubleGene.of(v, domain))
								.collect(ISeq.toISeq())
						))
					.collect(ISeq.toISeq())
			)
		);
	}

	/**
	 * Create a codec, which creates a a mapping from the elements given in the
	 * {@code source} sequence to the elements given in the {@code target}
	 * sequence. The returned mapping can be seen as a function which maps every
	 * element of the {@code target} set to an element of the {@code source} set.
	 *
	 * <pre>{@code
	 * final ISeq<Integer> numbers = ISeq.of(1, 2, 3, 4, 5);
	 * final ISeq<String> strings = ISeq.of("1", "2", "3");
	 *
	 * final Codec<Map<Integer, String>, EnumGene<Integer>> codec =
	 *     Codecs.ofMapping(numbers, strings, HashMap::new);
	 * }</pre>
	 *
	 * If {@code source.size() > target.size()}, the created mapping is
	 * <a href="https://en.wikipedia.org/wiki/Surjective_function">surjective</a>,
	 * if {@code source.size() < target.size()}, the mapping is
	 * <a href="https://en.wikipedia.org/wiki/Injective_function">injective</a>
	 * and if both sets have the same size, the returned mapping is
	 * <a href="https://en.wikipedia.org/wiki/Bijection">bijective</a>.
	 *
	 * @since 4.3
	 *
	 * @param source the source elements. Will be the <em>keys</em> of the
	 *        encoded {@code Map}.
	 * @param target the target elements. Will be the <em>values</em> of the
	 * 	      encoded {@code Map}.
	 * @param mapSupplier a function which returns a new, empty Map into which
	 *        the mapping will be inserted
	 * @param <A> the type of the source elements
	 * @param <B> the type of the target elements
	 * @param <M> the type of the encoded Map
	 * @return a new mapping codec
	 * @throws IllegalArgumentException if the {@code target} sequences are empty
	 * @throws NullPointerException if one of the argument is {@code null}
	 */
	public static <A, B, M extends Map<A, B>> InvertibleCodec<M, EnumGene<Integer>>
	ofMapping(
		final ISeq<? extends A> source,
		final ISeq<? extends B> target,
		final Supplier<M> mapSupplier
	) {
		requireNonNull(source);
		requireNonNull(target);
		requireNonNull(mapSupplier);

		final Map<A, Integer> smap = IntStream.range(0, source.length())
			.mapToObj(i -> entry(source.get(i), i))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		final Map<B, Integer> tmap = IntStream.range(0, target.length())
			.mapToObj(i -> entry(target.get(i), i))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		final PermutationChromosome<Integer> chromosome =
			PermutationChromosome.ofInteger(target.size());

		final Map<Integer, EnumGene<Integer>> genes = chromosome.stream()
			.collect(Collectors.toMap(EnumGene::allele, identity()));

		final Codec<int[], EnumGene<Integer>> codec = Codec.of(
			Genotype.of(chromosome),
			gt -> gt.chromosome().stream()
				.mapToInt(EnumGene::allele)
				.toArray()
		);

		return codec
			.map(permutation -> toMapping(permutation, source, target, mapSupplier))
			.toInvertibleCodec(mapping -> toEncoding(mapping, smap,tmap, genes));
	}

	private static <A, B> Map.Entry<A, B> entry(final A a, final B b) {
		return new SimpleImmutableEntry<>(a, b);
	}

	private static <A, B, M extends Map<A, B>> M toMapping(
		final int[] perm,
		final ISeq<? extends A> source,
		final ISeq<? extends B> target,
		final Supplier<M> mapSupplier
	) {
		return IntStream.range(0, source.size())
			.mapToObj(i -> entry(source.get(i), target.get(perm[i%perm.length])))
			.collect(Collectors.toMap(
				Entry::getKey, Entry::getValue,
				(u,v) -> {throw new IllegalStateException("Duplicate key " + u);},
				mapSupplier));
	}

	private static <A, B> Genotype<EnumGene<Integer>> toEncoding(
		final Map<A, B> mapping,
		final Map<A, Integer> source,
		final Map<B, Integer> target,
		final Map<Integer, EnumGene<Integer>> genes
	) {
		final int[] perm = new int[target.size()];
		source.forEach((key, value) -> {
			final int i = value;
			final int j = target.get(mapping.get(key));
			perm[i%perm.length] = j;
		});

		// Fill the rest of the 'perm' array, without duplicates.
		// TODO: can be done more efficiently
		if (target.size() > source.size()) {
			final Set<Integer> indexes = new HashSet<>();
			for (int i = 0; i < target.size(); ++i) {
				indexes.add(i);
			}

			for (int i = 0; i < source.size(); ++i) {
				indexes.remove(perm[i]);
			}

			final Iterator<Integer> it = indexes.iterator();
			for (int i = source.size(); i < target.size(); ++i) {
				perm[i] = it.next();
				it.remove();
			}
		}

		return  Genotype.of(
			new PermutationChromosome<>(
				IntStream.of(perm)
					.mapToObj(genes::get)
					.collect(ISeq.toISeq())
			)
		);
	}

	/**
	 * Create a codec, which creates a a mapping from the elements given in the
	 * {@code source} sequence to the elements given in the {@code target}
	 * sequence. The returned mapping can be seen as a function which maps every
	 * element of the {@code target} set to an element of the {@code source} set.
	 *
	 * <pre>{@code
	 * final ISeq<Integer> numbers = ISeq.of(1, 2, 3, 4, 5);
	 * final ISeq<String> strings = ISeq.of("1", "2", "3");
	 *
	 * final Codec<Map<Integer, String>, EnumGene<Integer>> codec =
	 *     Codecs.ofMapping(numbers, strings);
	 * }</pre>
	 *
	 * If {@code source.size() > target.size()}, the created mapping is
	 * <a href="https://en.wikipedia.org/wiki/Surjective_function">surjective</a>,
	 * if {@code source.size() < target.size()}, the mapping is
	 * <a href="https://en.wikipedia.org/wiki/Injective_function">injective</a>
	 * and if both sets have the same size, the returned mapping is
	 * <a href="https://en.wikipedia.org/wiki/Bijection">bijective</a>.
	 *
	 * @since 4.3
	 *
	 * @param source the source elements. Will be the <em>keys</em> of the
	 *        encoded {@code Map}.
	 * @param target the target elements. Will be the <em>values</em> of the
	 * 	      encoded {@code Map}.
	 * @param <A> the type of the source elements
	 * @param <B> the type of the target elements
	 * @return a new mapping codec
	 * @throws IllegalArgumentException if the {@code target} sequences are empty
	 * @throws NullPointerException if one of the argument is {@code null}
	 */
	public static <A, B> InvertibleCodec<Map<A, B>, EnumGene<Integer>>
	ofMapping(final ISeq<? extends A> source, final ISeq<? extends B> target) {
		return ofMapping(source, target, HashMap::new);
	}

	/**
	 * The subset {@link InvertibleCodec} can be used for problems where it is
	 * required to find the best <b>variable-sized</b> subset from given basic
	 * set. A typical usage example of the returned {@code Codec} is the
	 * Knapsack problem.
	 * <p>
	 * The following code snippet shows a simplified variation of the Knapsack
	 * problem.
	 * <pre>{@code
	 * public final class Main {
	 *     // The basic set from where to choose an 'optimal' subset.
	 *     private final static ISeq<Integer> SET =
	 *         ISeq.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	 *
	 *     // Fitness function directly takes an 'int' value.
	 *     private static int fitness(final ISeq<Integer> subset) {
	 *         assert(subset.size() <= SET.size());
	 *         final int size = subset.stream()
	 *             .collect(Collectors.summingInt(Integer::intValue));
	 *         return size <= 20 ? size : 0;
	 *     }
	 *
	 *     public static void main(final String[] args) {
	 *         final Engine<BitGene, Double> engine = Engine
	 *             .builder(Main::fitness, codec.ofSubSet(SET))
	 *             .build();
	 *         ...
	 *     }
	 * }
	 * }</pre>
	 *
	 * @param <T> the element type of the basic set
	 * @param basicSet the basic set, from where to choose the <i>optimal</i>
	 *        subset.
	 * @return a new codec which can be used for modelling <i>subset</i>
	 *         problems.
	 * @throws NullPointerException if the given {@code basicSet} is
	 *         {@code null}; {@code null} elements are allowed.
	 * @throws IllegalArgumentException if the {@code basicSet} size is smaller
	 *         than one.
	 */
	public static <T> InvertibleCodec<ISeq<T>, BitGene>
	ofSubSet(final ISeq<? extends T> basicSet) {
		requireNonNull(basicSet);
		Requires.positive(basicSet.length());

		return InvertibleCodec.of(
			Genotype.of(BitChromosome.of(basicSet.length())),
			gt -> gt.chromosome()
				.as(BitChromosome.class).ones()
				.<T>mapToObj(basicSet)
				.collect(ISeq.toISeq()),
			values -> {
				final byte[] bits = Bits.newArray(basicSet.size());

				int i = 0;
				for (T v : values) {
					while (i < basicSet.size() && !Objects.equals(basicSet.get(i), v)) {
						++i;
					}
					Bits.set(bits, i);
				}

				return Genotype.of(new BitChromosome(bits, 0, basicSet.size()));
			}
		);
	}

	/**
	 * The subset {@link InvertibleCodec} can be used for problems where it is
	 * required to find the best <b>fixed-size</b> subset from given basic set.
	 *
	 * @since 3.4
	 *
	 * @see PermutationChromosome
	 * @see PermutationChromosome#of(ISeq, int)
	 *
	 * @param <T> the element type of the basic set
	 * @param basicSet the basic set, from where to choose the <i>optimal</i>
	 *        subset.
	 * @param size the length of the desired subsets
	 * @return a new codec which can be used for modelling <i>subset</i>
	 *         problems.
	 * @throws NullPointerException if the given {@code basicSet} is
	 *         {@code null}; {@code null} elements are allowed.
	 * @throws IllegalArgumentException if {@code basicSet.size() < size},
	 *         {@code size <= 0} or {@code basicSet.size()*size} will cause an
	 *         integer overflow.
	 */
	public static <T> InvertibleCodec<ISeq<T>, EnumGene<T>> ofSubSet(
		final ISeq<? extends T> basicSet,
		final int size
	) {
		requireNonNull(basicSet);
		Combinatorics.checkSubSet(basicSet.size(), size);

		final Map<T, EnumGene<T>> genes =
			IntStream.range(0, basicSet.length())
				.mapToObj(i -> EnumGene.<T>of(i, basicSet))
				.collect(Collectors.toMap(EnumGene::allele, identity()));

		return InvertibleCodec.of(
			Genotype.of(PermutationChromosome.of(basicSet, size)),
			gt -> gt.chromosome().stream()
				.map(EnumGene::allele)
				.collect(ISeq.toISeq()),
			values -> {
				if (values.size() != size) {
					throw new IllegalArgumentException(format(
						"Expected sub-set size of %d, but got %d,",
						size, values.size()
					));
				}

				return Genotype.of(
					new PermutationChromosome<>(
						values.stream()
							.map(genes::get)
							.collect(ISeq.toISeq())
					)
				);
			}
		);
	}

//	/**
//	 * Creates a codec for a 2-dimensional affine transformation. The composed
//	 * order of the transformation is: <i>R&bull;Sc&bull;Sh&bull;T</i>
//	 *
//	 * @param sx the scale factor range in x direction
//	 * @param sy the scale factor range in y direction
//	 * @param tx the translation range in x direction
//	 * @param ty the translation range in y direction
//	 * @param th the rotation range (in radians)
//	 * @param kx the shear range in x direction
//	 * @param ky the shear range in x direction
//	 * @return the affine transformation codec
//	 * @throws NullPointerException if one of the arguments is {@code null}
//	 */
//	static Codec<AffineTransform, DoubleGene> ofAffineTransform(
//		final DoubleRange sx, final DoubleRange sy,
//		final DoubleRange tx, final DoubleRange ty,
//		final DoubleRange th,
//		final DoubleRange kx, final DoubleRange ky
//	) {
//		return Codec.of(
//			Genotype.of(
//				// Scale
//				DoubleChromosome.of(sx), DoubleChromosome.of(sy),
//				// Translation
//				DoubleChromosome.of(tx), DoubleChromosome.of(ty),
//				// Rotation
//				DoubleChromosome.of(th),
//				// Shear
//				DoubleChromosome.of(kx), DoubleChromosome.of(ky)
//			),
//			gt -> {
//				final AffineTransform at = new AffineTransform();
//
//				at.translate(
//					gt.getChromosome(2).getGene().doubleValue(),
//					gt.getChromosome(3).getGene().doubleValue()
//				);
//				at.shear(
//					gt.getChromosome(5).getGene().doubleValue(),
//					gt.getChromosome(6).getGene().doubleValue()
//				);
//				at.scale(
//					gt.getChromosome(0).getGene().doubleValue(),
//					gt.getChromosome(1).getGene().doubleValue()
//				);
//				at.rotate(gt.getChromosome(4).getGene().doubleValue());
//
//				return at;
//			}
//		);
//	}
//
//	/**
//	 * Creates a codec for a 2-dimensional affine transformation. The composed
//	 * order of the transformation is: <i>R&bull;Sc&bull;Sh&bull;T</i>
//	 *
//	 * @param s the scale factor range in x and y direction
//	 * @param t the translation range in x and y direction
//	 * @param th the rotation angle range
//	 * @param k the shear range in x and y direction
//	 * @return the affine transformation codec
//	 * @throws NullPointerException if one of the arguments is {@code null}
//	 */
//	static Codec<AffineTransform, DoubleGene> ofAffineTransform(
//		final DoubleRange s,
//		final DoubleRange t,
//		final DoubleRange th,
//		final DoubleRange k
//	) {
//		return ofAffineTransform(s, s, t, t, th, k, k);
//	}

}
