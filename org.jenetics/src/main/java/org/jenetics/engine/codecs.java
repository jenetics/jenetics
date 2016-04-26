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
package org.jenetics.engine;

import static java.lang.reflect.Array.newInstance;
import static java.util.Objects.requireNonNull;

import java.awt.geom.AffineTransform;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jenetics.internal.math.base;
import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.require;

import org.jenetics.AnyChromosome;
import org.jenetics.AnyGene;
import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.EnumGene;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.IntegerChromosome;
import org.jenetics.IntegerGene;
import org.jenetics.LongChromosome;
import org.jenetics.LongGene;
import org.jenetics.PermutationChromosome;
import org.jenetics.util.DoubleRange;
import org.jenetics.util.ISeq;
import org.jenetics.util.IntRange;
import org.jenetics.util.LongRange;

/**
 * This class contains factory methods for creating common  problem encodings.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.2
 * @version 3.4
 */
public final class codecs {

	private codecs() {require.noInstance();}

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
	public static Codec<Double, DoubleGene> ofScalar(final DoubleRange domain) {
		requireNonNull(domain);

		return Codec.of(
			Genotype.of(DoubleChromosome.of(domain)),
			gt -> gt.getChromosome().getGene().getAllele()
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
	 * final Codec<BigInteger, AnyGene<BigInteger>> codec = codecs.of(
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
			gt -> ((IntegerChromosome)gt.getChromosome()).toArray()
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
			gt -> ((LongChromosome)gt.getChromosome()).toArray()
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
			gt -> ((DoubleChromosome)gt.getChromosome()).toArray()
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
			.map(Objects::requireNonNull)
			.map(IntegerGene::of)
			.map(IntegerChromosome::of)
			.collect(ISeq.toISeq());

		return Codec.of(
			Genotype.of(chromosomes),
			gt -> {
				final int[] args = new int[chromosomes.length()];
				for (int i = chromosomes.length(); --i >= 0;) {
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
			.map(Objects::requireNonNull)
			.map(LongGene::of)
			.map(LongChromosome::of)
			.collect(ISeq.toISeq());

		return Codec.of(
			Genotype.of(chromosomes),
			gt -> {
				final long[] args = new long[chromosomes.length()];
				for (int i = chromosomes.length(); --i >= 0;) {
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
			.map(Objects::requireNonNull)
			.map(DoubleGene::of)
			.map(DoubleChromosome::of)
			.collect(ISeq.toISeq());

		return Codec.of(
			Genotype.of(chromosomes),
			gt -> {
				final double[] args = new double[chromosomes.length()];
				for (int i = chromosomes.length(); --i >= 0;) {
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
	 * final Codec<BigInteger[], AnyGene<BigInteger>> codec = codecs.of(
	 *     // Create new random 'BigInteger' object.
	 *     () -> {
	 *         final byte[] data = new byte[100];
	 *         RandomRegistry.getRandom().nextBytes(data);
	 *         return new BigInteger(data);
	 *     },
	 *     // The array generator.
	 *     BigInteger[]::new,
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
	 * @param generator the array generator used for generating arrays of type
	 *        {@code A}
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
	 *
	 * @deprecated Use {@link #ofVector(Supplier, Predicate, Predicate, int)}
	 *             instead
	 */
	@Deprecated
	public static <A> Codec<A[], AnyGene<A>> ofVector(
		final Supplier<? extends A> supplier,
		final IntFunction<A[]> generator,
		final Predicate<? super A> alleleValidator,
		final Predicate<? super ISeq<? super A>> alleleSeqValidator,
		final int length
	) {
		requireNonNull(supplier);
		requireNonNull(generator);
		requireNonNull(alleleSeqValidator);
		requireNonNull(alleleSeqValidator);
		require.positive(length);

		return Codec.of(
			Genotype.of(AnyChromosome
				.of(supplier, alleleValidator, alleleSeqValidator, length)),
			gt -> gt.getChromosome().toSeq().stream()
				.map(Gene::getAllele)
				.toArray(generator)
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
	 * final Codec<BigInteger[], AnyGene<BigInteger>> codec = codecs.of(
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
		final Predicate<? super ISeq<? super A>> alleleSeqValidator,
		final int length
	) {
		requireNonNull(supplier);
		requireNonNull(alleleSeqValidator);
		requireNonNull(alleleSeqValidator);
		require.positive(length);

		return Codec.of(
			Genotype.of(AnyChromosome
				.of(supplier, alleleValidator, alleleSeqValidator, length)),
			gt -> gt.getChromosome().toSeq().map(Gene::getAllele)
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
	 * @param generator the array generator used for generating arrays of type
	 *        {@code A}
	 * @param validator the validator used for validating the created gene. This
	 *        predicate is used in the {@link AnyGene#isValid()} method.
	 * @param length the vector length
	 * @return a new {@code Codec} with the given parameters
	 * @throws NullPointerException if one of the parameters is {@code null}
	 * @throws IllegalArgumentException if the length of the vector is smaller
	 *         than one.
	 *
	 * @deprecated Use {@link #ofVector(Supplier, Predicate, int)} instead
	 */
	@Deprecated
	public static <A> Codec<A[], AnyGene<A>> ofVector(
		final Supplier<? extends A> supplier,
		final IntFunction<A[]> generator,
		final Predicate<? super A> validator,
		final int length
	) {
		return ofVector(supplier, generator, validator, Equality.TRUE, length);
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
		return ofVector(supplier, validator, Equality.TRUE, length);
	}

	/**
	 * Return a scala {@code Codec} with the given allele {@link Supplier} and
	 * {@code Chromosome} length. The {@code supplier} is responsible for
	 * creating new random alleles.
	 *
	 * @param <A> the allele type
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @param generator the array generator used for generating arrays of type
	 *        {@code A}
	 * @param length the vector length
	 * @return a new {@code Codec} with the given parameters
	 * @throws NullPointerException if one of the parameters is {@code null}
	 * @throws IllegalArgumentException if the length of the vector is smaller
	 *         than one.
	 *
	 * @deprecated Use {@link #ofVector(Supplier, int)} instead
	 */
	@Deprecated
	public static <A> Codec<A[], AnyGene<A>> ofVector(
		final Supplier<? extends A> supplier,
		final IntFunction<A[]> generator,
		final int length
	) {
		return ofVector(supplier, generator, Equality.TRUE, length);
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
		return ofVector(supplier, Equality.TRUE, length);
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
			gt -> gt.getChromosome().toSeq().stream()
				.mapToInt(EnumGene<Integer>::getAllele)
				.toArray()
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
	 *
	 * @deprecated Use {@link #ofPermutation(ISeq)} instead
	 */
	@Deprecated
	@SafeVarargs
	public static <T> Codec<T[], EnumGene<T>> ofPermutation(final T... alleles) {
		if (alleles.length == 0) {
			throw new IllegalArgumentException(
				"Empty allele array is not allowed."
			);
		}

		return Codec.of(
			Genotype.of(PermutationChromosome.of(alleles)),
			gt -> gt.getChromosome().toSeq().stream()
				.map(EnumGene::getAllele)
				.toArray(length -> newArray(alleles[0].getClass(), length))
		);
	}

	@SuppressWarnings("unchecked")
	private static <T> T[] newArray(final Class<?> type, final int length) {
		return (T[])newInstance(type, length);
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
	ofPermutation(final ISeq<T> alleles) {
		if (alleles.isEmpty()) {
			throw new IllegalArgumentException(
				"Empty allele array is not allowed."
			);
		}

		return Codec.of(
			Genotype.of(PermutationChromosome.of(alleles)),
			gt -> gt.getChromosome().toSeq().map(EnumGene::getAllele)
		);
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
			gt -> ((BitChromosome)gt.getChromosome()).ones()
				.<T>mapToObj(basicSet::get)
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
		base.checkSubSet(basicSet.size(), size);

		return Codec.of(
			Genotype.of(PermutationChromosome.of(basicSet, size)),
			gt -> gt.getChromosome().stream()
				.map(EnumGene::getAllele)
				.collect(ISeq.toISeq())
		);
	}

	// https://trac.osgeo.org/postgis/wiki/DevWikiAffineParameters

	/**
	 * Creates a codec for a 2-dimensional affine transformation. The composed
	 * order of the transformation is: <i>R&bull;Sc&bull;Sh&bull;T</i>
	 *
	 * @param sx the scale factor range in x direction
	 * @param sy the scale factor range in y direction
	 * @param tx the translation range in x direction
	 * @param ty the translation range in y direction
	 * @param th the rotation range (in radians)
	 * @param kx the shear range in x direction
	 * @param ky the shear range in x direction
	 * @return the affine transformation codec
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	static Codec<AffineTransform, DoubleGene> ofAffineTransform(
		final DoubleRange sx, final DoubleRange sy,
		final DoubleRange tx, final DoubleRange ty,
		final DoubleRange th,
		final DoubleRange kx, final DoubleRange ky
	) {
		return Codec.of(
			Genotype.of(
				// Scale
				DoubleChromosome.of(sx), DoubleChromosome.of(sy),
				// Translation
				DoubleChromosome.of(tx), DoubleChromosome.of(ty),
				// Rotation
				DoubleChromosome.of(th),
				// Shear
				DoubleChromosome.of(kx), DoubleChromosome.of(ky)
			),
			gt -> {
				final AffineTransform at = new AffineTransform();

				at.translate(
					gt.getChromosome(2).getGene().doubleValue(),
					gt.getChromosome(3).getGene().doubleValue()
				);
				at.shear(
					gt.getChromosome(5).getGene().doubleValue(),
					gt.getChromosome(6).getGene().doubleValue()
				);
				at.scale(
					gt.getChromosome(0).getGene().doubleValue(),
					gt.getChromosome(1).getGene().doubleValue()
				);
				at.rotate(gt.getChromosome(4).getGene().doubleValue());

				return at;
			}
		);
	}

	/**
	 * Creates a codec for a 2-dimensional affine transformation. The composed
	 * order of the transformation is: <i>R&bull;Sc&bull;Sh&bull;T</i>
	 *
	 * @param s the scale factor range in x and y direction
	 * @param t the translation range in x and y direction
	 * @param th the rotation angle range
	 * @param k the shear range in x and y direction
	 * @return the affine transformation codec
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	static Codec<AffineTransform, DoubleGene> ofAffineTransform(
		final DoubleRange s,
		final DoubleRange t,
		final DoubleRange th,
		final DoubleRange k
	) {
		return ofAffineTransform(s, s, t, t, th, k, k);
	}

}
