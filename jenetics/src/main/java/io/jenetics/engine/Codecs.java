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

import static java.util.Objects.requireNonNull;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
import io.jenetics.internal.math.comb;
import io.jenetics.internal.util.Predicates;
import io.jenetics.internal.util.require;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.LongRange;

/**
 * This class contains factory methods for creating common  problem encodings.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.2
 * @version 4.4
 */
public final class Codecs {

	private Codecs() {}

	/**
	 * Return a scalar {@code Codec} for the given range.
	 *
	 * @param domain the domain of the returned {@code Codec}
	 * @return a new scalar {@code Codec} with the given domain.
	 * @throws NullPointerException if the given {@code domain} is {@code null}
	 */
	public static Codec<Integer, IntegerGene> ofScalar(final IntRange domain) {
		requireNonNull(domain);

		return Codec.of(
			Genotype.of(IntegerChromosome.of(domain)),
			gt -> gt.getChromosome().getGene().getAllele()
		);
	}

	/**
	 * Return a scalar {@code Codec} for the given range.
	 *
	 * @param domain the domain of the returned {@code Codec}
	 * @return a new scalar {@code Codec} with the given domain.
	 * @throws NullPointerException if the given {@code domain} is {@code null}
	 */
	public static Codec<Long, LongGene> ofScalar(final LongRange domain) {
		requireNonNull(domain);

		return Codec.of(
			Genotype.of(LongChromosome.of(domain)),
			gt -> gt.getGene().getAllele()
		);
	}

	/**
	 * Return a scalar {@code Codec} for the given range.
	 *
	 * @param domain the domain of the returned {@code Codec}
	 * @return a new scalar {@code Codec} with the given domain.
	 * @throws NullPointerException if the given {@code domain} is {@code null}
	 */
	public static Codec<Double, DoubleGene> ofScalar(final DoubleRange domain) {
		requireNonNull(domain);

		return Codec.of(
			Genotype.of(DoubleChromosome.of(domain)),
			gt -> gt.getGene().getAllele()
		);
	}

	/**
	 * Return a scala {@code Codec} with the given allele {@link Supplier} and
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
			gt -> gt.getGene().getAllele()
		);
	}

	/**
	 * Return a scala {@code Codec} with the given allele {@link Supplier} and
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
			gt -> gt.getGene().getAllele()
		);
	}

	/**
	 * Return a vector {@code Codec} for the given range. All vector values
	 * are restricted by the same domain.
	 *
	 * @param domain the domain of the vector values
	 * @param length the vector length
	 * @return a new vector {@code Codec}
	 * @throws NullPointerException if the given {@code domain} is {@code null}
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public static Codec<int[], IntegerGene> ofVector(
		final IntRange domain,
		final int length
	) {
		requireNonNull(domain);
		require.positive(length);

		return Codec.of(
			Genotype.of(IntegerChromosome.of(domain, length)),
			gt -> gt.getChromosome().as(IntegerChromosome.class).toArray()
		);
	}

	/**
	 * Return a vector {@code Codec} for the given range. All vector values
	 * are restricted by the same domain.
	 *
	 * @param domain the domain of the vector values
	 * @param length the vector length
	 * @return a new vector {@code Codec}
	 * @throws NullPointerException if the given {@code domain} is {@code null}
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public static Codec<long[], LongGene> ofVector(
		final LongRange domain,
		final int length
	) {
		requireNonNull(domain);
		require.positive(length);

		return Codec.of(
			Genotype.of(LongChromosome.of(domain, length)),
			gt -> gt.getChromosome().as(LongChromosome.class).toArray()
		);
	}

	/**
	 * Return a vector {@code Codec} for the given range. All vector values
	 * are restricted by the same domain.
	 *
	 * @param domain the domain of the vector values
	 * @param length the vector length
	 * @return a new vector {@code Codec}
	 * @throws NullPointerException if the given {@code domain} is {@code null}
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public static Codec<double[], DoubleGene> ofVector(
		final DoubleRange domain,
		final int length
	) {
		requireNonNull(domain);
		require.positive(length);

		return Codec.of(
			Genotype.of(DoubleChromosome.of(domain, length)),
			gt -> gt.getChromosome().as(DoubleChromosome.class).toArray()
		);
	}

	/**
	 * Create a vector {@code Codec} for the given ranges. Each vector element
	 * might have a different domain. The vector length is equal to the number
	 * of domains.
	 *
	 * @param domains the domain ranges
	 * @return a new vector {@code Codec}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the {@code domains} array is empty
	 */
	public static Codec<int[], IntegerGene> ofVector(final IntRange... domains) {
		if (domains.length == 0) {
			throw new IllegalArgumentException("Domains must not be empty.");
		}

		final ISeq<IntegerChromosome> chromosomes = Stream.of(domains)
			.peek(Objects::requireNonNull)
			.map(IntegerGene::of)
			.map(IntegerChromosome::of)
			.collect(ISeq.toISeq());

		return Codec.of(
			Genotype.of(chromosomes),
			gt -> {
				final int[] args = new int[gt.length()];
				for (int i = gt.length(); --i >= 0;) {
					args[i] = gt.getChromosome(i).getGene().intValue();
				}
				return args;
			}
		);
	}

	/**
	 * Create a vector {@code Codec} for the given ranges. Each vector element
	 * might have a different domain. The vector length is equal to the number
	 * of domains.
	 *
	 * @param domains the domain ranges
	 * @return a new vector {@code Codec}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the {@code domains} array is empty
	 */
	public static Codec<long[], LongGene> ofVector(final LongRange... domains) {
		if (domains.length == 0) {
			throw new IllegalArgumentException("Domains must not be empty.");
		}

		final ISeq<LongChromosome> chromosomes = Stream.of(domains)
			.peek(Objects::requireNonNull)
			.map(LongGene::of)
			.map(LongChromosome::of)
			.collect(ISeq.toISeq());

		return Codec.of(
			Genotype.of(chromosomes),
			gt -> {
				final long[] args = new long[gt.length()];
				for (int i = gt.length(); --i >= 0;) {
					args[i] = gt.getChromosome(i).getGene().longValue();
				}
				return args;
			}
		);
	}

	/**
	 * Create a vector {@code Codec} for the given ranges. Each vector element
	 * might have a different domain. The vector length is equal to the number
	 * of domains.
	 *
	 * @param domains the domain ranges
	 * @return a new vector {@code Codec}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the {@code domains} array is empty
	 */
	public static Codec<double[], DoubleGene> ofVector(
		final DoubleRange... domains
	) {
		if (domains.length == 0) {
			throw new IllegalArgumentException("Domains must not be empty.");
		}

		final ISeq<DoubleChromosome> chromosomes = Stream.of(domains)
			.peek(Objects::requireNonNull)
			.map(DoubleGene::of)
			.map(DoubleChromosome::of)
			.collect(ISeq.toISeq());

		return Codec.of(
			Genotype.of(chromosomes),
			gt -> {
				final double[] args = new double[gt.length()];
				for (int i = gt.length(); --i >= 0;) {
					args[i] = gt.getChromosome(i).getGene().doubleValue();
				}
				return args;
			}
		);
	}

	/**
	 * Return a scala {@code Codec} with the given allele {@link Supplier},
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
		require.positive(length);

		return Codec.of(
			Genotype.of(AnyChromosome
				.of(supplier, alleleValidator, alleleSeqValidator, length)),
			gt -> gt.getChromosome().stream()
				.map(Gene::getAllele)
				.collect(ISeq.toISeq())
		);
	}

	/**
	 * Return a scala {@code Codec} with the given allele {@link Supplier},
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
			Predicates.<ISeq<A>>True(),
			length
		);
	}

	/**
	 * Return a scala {@code Codec} with the given allele {@link Supplier} and
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
	 * Create a permutation {@code Codec} of integer in the range
	 * {@code [0, length)}.
	 *
	 * @param length the number of permutation elements
	 * @return a permutation {@code Codec} of integers
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public static Codec<int[], EnumGene<Integer>> ofPermutation(final int length) {
		require.positive(length);

		return Codec.of(
			Genotype.of(PermutationChromosome.ofInteger(length)),
			gt -> gt.getChromosome().stream()
				.mapToInt(EnumGene::getAllele)
				.toArray()
		);
	}

	/**
	 * Return a 2-dimensional matrix {@code Codec} for the given range. All
	 * matrix values are restricted by the same domain. The dimension of the
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
	public static Codec<int[][], IntegerGene> ofMatrix(
		final IntRange domain,
		final int rows,
		final int cols
	) {
		requireNonNull(domain);
		require.positive(rows);
		require.positive(cols);

		return Codec.of(
			Genotype.of(
				IntegerChromosome.of(domain, cols).instances()
					.limit(rows)
					.collect(ISeq.toISeq())
			),
			gt -> gt.stream()
				.map(ch -> ch.stream()
					.mapToInt(IntegerGene::intValue)
					.toArray())
				.toArray(int[][]::new)
		);
	}

	/**
	 * Return a 2-dimensional matrix {@code Codec} for the given range. All
	 * matrix values are restricted by the same domain. The dimension of the
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
	public static Codec<long[][], LongGene> ofMatrix(
		final LongRange domain,
		final int rows,
		final int cols
	) {
		requireNonNull(domain);
		require.positive(rows);
		require.positive(cols);

		return Codec.of(
			Genotype.of(
				LongChromosome.of(domain, cols).instances()
					.limit(rows)
					.collect(ISeq.toISeq())
			),
			gt -> gt.stream()
				.map(ch -> ch.stream()
					.mapToLong(LongGene::longValue)
					.toArray())
				.toArray(long[][]::new)
		);
	}

	/**
	 * Return a 2-dimensional matrix {@code Codec} for the given range. All
	 * matrix values are restricted by the same domain. The dimension of the
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
	public static Codec<double[][], DoubleGene> ofMatrix(
		final DoubleRange domain,
		final int rows,
		final int cols
	) {
		requireNonNull(domain);
		require.positive(rows);
		require.positive(cols);

		return Codec.of(
			Genotype.of(
				DoubleChromosome.of(domain, cols).instances()
					.limit(rows)
					.collect(ISeq.toISeq())
			),
			gt -> gt.stream()
				.map(ch -> ch.stream()
					.mapToDouble(DoubleGene::doubleValue)
					.toArray())
				.toArray(double[][]::new)
		);
	}

	/**
	 * Create a permutation {@code Codec} with the given alleles.
	 *
	 * @param alleles the alleles of the permutation
	 * @param <T> the allele type
	 * @return a new permutation {@code Codec}
	 * @throws IllegalArgumentException if the given allele array is empty
	 * @throws NullPointerException if one of the alleles is {@code null}
	 */
	public static <T> Codec<ISeq<T>, EnumGene<T>>
	ofPermutation(final ISeq<? extends T> alleles) {
		if (alleles.isEmpty()) {
			throw new IllegalArgumentException(
				"Empty allele array is not allowed."
			);
		}

		return Codec.of(
			Genotype.of(PermutationChromosome.of(alleles)),
			gt -> gt.getChromosome().stream()
				.map(EnumGene::getAllele)
				.collect(ISeq.toISeq())
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
	public static <A, B, M extends Map<A, B>> Codec<M, EnumGene<Integer>>
	ofMapping(
		final ISeq<? extends A> source,
		final ISeq<? extends B> target,
		final Supplier<M> mapSupplier
	) {
		requireNonNull(mapSupplier);
		return ofPermutation(target.size())
			.map(perm -> toMapping(perm, source, target, mapSupplier));
	}

	private static <A, B, M extends Map<A, B>> M toMapping(
		final int[] perm,
		final ISeq<? extends A> source,
		final ISeq<? extends B> target,
		final Supplier<M> mapSupplier
	) {
		return IntStream.range(0, source.size())
			.mapToObj(i -> new SimpleImmutableEntry<>(
				source.get(i), target.get(perm[i%perm.length])))
			.collect(Collectors.toMap(
				Entry::getKey,
				Entry::getValue,
				(u,v) -> {throw new IllegalStateException("Duplicate key " + u);},
				mapSupplier));
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
	public static <A, B> Codec<Map<A, B>, EnumGene<Integer>>
	ofMapping(final ISeq<? extends A> source, final ISeq<? extends B> target) {
		return ofMapping(source, target, HashMap::new);
	}

	/**
	 * The subset {@code Codec} can be used for problems where it is required to
	 * find the best <b>variable-sized</b> subset from given basic set. A typical
	 * usage example of the returned {@code Codec} is the Knapsack problem.
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
	public static <T> Codec<ISeq<T>, BitGene> ofSubSet(
		final ISeq<? extends T> basicSet
	) {
		requireNonNull(basicSet);
		require.positive(basicSet.length());

		return Codec.of(
			Genotype.of(BitChromosome.of(basicSet.length())),
			gt -> gt.getChromosome()
				.as(BitChromosome.class).ones()
				.<T>mapToObj(basicSet)
				.collect(ISeq.toISeq())
		);
	}

	/**
	 * The subset {@code Codec} can be used for problems where it is required to
	 * find the best <b>fixed-size</b> subset from given basic set.
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
	public static <T> Codec<ISeq<T>, EnumGene<T>> ofSubSet(
		final ISeq<? extends T> basicSet,
		final int size
	) {
		requireNonNull(basicSet);
		comb.checkSubSet(basicSet.size(), size);

		return Codec.of(
			Genotype.of(PermutationChromosome.of(basicSet, size)),
			gt -> gt.getChromosome().stream()
				.map(EnumGene::getAllele)
				.collect(ISeq.toISeq())
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
