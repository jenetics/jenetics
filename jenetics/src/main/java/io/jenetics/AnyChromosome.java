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

import java.util.function.Predicate;
import java.util.function.Supplier;

import io.jenetics.internal.util.Predicates;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

/**
 * {@code Chromosome} implementation, which allows to create genes without
 * explicit implementing the {@code Chromosome} interface.
 *
 * <pre>{@code
 * public class LastMonday {
 *
 *     // First monday of 2015.
 *     private static final LocalDate MIN_MONDAY = LocalDate.of(2015, 1, 5);
 *
 *     // The used Codec.
 *     private static final Codec<LocalDate, AnyGene<LocalDate>> CODEC = Codec.of(
 *         Genotype.of(AnyChromosome.of(LastMonday::nextRandomMonday)),
 *         gt -> gt.gene().allele()
 *     );
 *
 *     // Supplier of random 'LocalDate' objects. The implementation is responsible
 *     // for guaranteeing the desired allele restriction. In this case we will
 *     // generate only mondays.
 *     private static LocalDate nextRandomMonday() {
 *         return MIN_MONDAY.plusWeeks(RandomRegistry.getRandom().nextInt(1000));
 *     }
 *
 *     // The fitness function: find a monday at the end of the month.
 *     private static double fitness(final LocalDate date) {
 *         return date.getDayOfMonth();
 *     }
 *
 *     public static void main(final String[] args) {
 *         final Engine<AnyGene<LocalDate>, Double> engine = Engine
 *             .builder(LastMonday::fitness, CODEC)
 *             .offspringSelector(new RouletteWheelSelector<>())
 *             .build();
 *
 *         final Phenotype<AnyGene<LocalDate>, Double> best = engine.stream()
 *             .limit(50)
 *             .collect(EvolutionResult.toBestPhenotype());
 *
 *         System.out.println(best);
 *     }
 *
 * }
 * }</pre>
 *
 * The <i>full</i> example above shows how the {@code AnyChromosome} is used
 * to use for an allele-type with no predefined gene- and chromosome type.
 *
 * @see AnyGene
 *
 * @implSpec
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.2
 * @since 3.3
 */
public class AnyChromosome<A> extends VariableChromosome<AnyGene<A>> {

	private final Supplier<? extends A> _supplier;
	private final Predicate<? super A> _alleleValidator;
	private final Predicate<? super ISeq<A>> _alleleSeqValidator;

	private Boolean _valid = null;

	/**
	 * Create a new {@code AnyChromosome} from the given {@code genes}
	 * array. An chromosome is valid if both, the {@code alleleValidator} and
	 * the {@code alleleSeqValidator} return {@code true}.
	 *
	 * @param genes the genes that form the chromosome.
	 * @param lengthRange the allowed length range of the chromosome
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @param alleleValidator the validator used for validating the created gene.
	 *        This predicate is used in the {@link AnyGene#isValid()} method.
	 * @param alleleSeqValidator the validator used for validating the created
	 *        chromosome. This predicate is used in the
	 *        {@link AnyChromosome#isValid()} method.
	 * @throws NullPointerException if the given arguments is {@code null}
	 * @throws IllegalArgumentException if the length of the gene sequence is
	 *         empty, doesn't match with the allowed length range, the minimum
	 *         or maximum of the range is smaller or equal zero or the given
	 *         range size is zero.
	 */
	protected AnyChromosome(
		final ISeq<AnyGene<A>> genes,
		final Supplier<? extends A> supplier,
		final Predicate<? super A> alleleValidator,
		final Predicate<? super ISeq<A>> alleleSeqValidator,
		final IntRange lengthRange
	) {
		super(genes, lengthRange);
		_supplier = requireNonNull(supplier);
		_alleleValidator = requireNonNull(alleleValidator);
		_alleleSeqValidator = requireNonNull(alleleSeqValidator);
	}

	@Override
	public boolean isValid() {
		Boolean valid =
			(_alleleValidator == Predicates.TRUE &&
				_alleleSeqValidator == Predicates.<ISeq<A>>True())
			? Boolean.TRUE
			: _valid;

		if (valid == null) {
			final ISeq<A> alleles = stream()
				.map(Gene::allele)
				.collect(ISeq.toISeq());

			valid = _alleleSeqValidator.test(alleles) &&
				alleles.forAll(_alleleValidator);
		}

		return _valid = valid;
	}

	@Override
	public Chromosome<AnyGene<A>> newInstance(final ISeq<AnyGene<A>> genes) {
		return new AnyChromosome<>(
			genes,
			_supplier,
			_alleleValidator,
			_alleleSeqValidator,
			lengthRange()
		);
	}

	@Override
	public Chromosome<AnyGene<A>> newInstance() {
		return of(_supplier, _alleleValidator, _alleleSeqValidator, lengthRange());
	}


	/* *************************************************************************
	 *  Static factory methods.
	 * ************************************************************************/

	/**
	 * Create a new chromosome of type {@code A} with the given parameters.
	 *
	 * @since 4.0
	 *
	 * @param <A> the allele type
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @param alleleValidator the validator used for validating the created gene.
	 *        This predicate is used in the {@link AnyGene#isValid()} method.
	 * @param alleleSeqValidator the validator used for validating the created
	 *        chromosome. This predicate is used in the
	 *        {@link AnyChromosome#isValid()} method.
	 * @param lengthRange the allowed length range of the chromosome
	 * @return a new chromosome of allele type {@code A}
	 * @throws NullPointerException if the given arguments is {@code null}
	 * @throws IllegalArgumentException if chromosome length is smaller than one.
	 */
	public static <A> AnyChromosome<A> of(
		final Supplier<? extends A> supplier,
		final Predicate<? super A> alleleValidator,
		final Predicate<? super ISeq<A>> alleleSeqValidator,
		final IntRange lengthRange
	) {
		return new AnyChromosome<>(
			AnyGene.seq(lengthRange, supplier, alleleValidator),
			supplier,
			alleleValidator,
			alleleSeqValidator,
			lengthRange
		);
	}

	/**
	 * Create a new chromosome of type {@code A} with the given parameters.
	 *
	 * @param <A> the allele type
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @param alleleValidator the validator used for validating the created gene.
	 *        This predicate is used in the {@link AnyGene#isValid()} method.
	 * @param alleleSeqValidator the validator used for validating the created
	 *        chromosome. This predicate is used in the
	 *        {@link AnyChromosome#isValid()} method.
	 * @param length the length of the chromosome
	 * @return a new chromosome of allele type {@code A}
	 * @throws NullPointerException if the given arguments is {@code null}
	 * @throws IllegalArgumentException if chromosome length is smaller than one.
	 */
	public static <A> AnyChromosome<A> of(
		final Supplier<? extends A> supplier,
		final Predicate<? super A> alleleValidator,
		final Predicate<? super ISeq<A>> alleleSeqValidator,
		final int length
	) {
		return of(supplier, alleleValidator, alleleSeqValidator, IntRange.of(length));
	}

	/**
	 * Create a new chromosome of type {@code A} with the given parameters.
	 *
	 * @since 4.0
	 *
	 * @param <A> the allele type
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @param validator the validator used for validating the created gene. This
	 *        predicate is used in the {@link AnyGene#isValid()} method.
	 * @param lengthRange the allowed length range of the chromosome
	 * @return a new chromosome of allele type {@code A}
	 * @throws NullPointerException if the {@code supplier} or {@code validator}
	 *         is {@code null}
	 * @throws IllegalArgumentException if chromosome length is smaller than one.
	 */
	public static <A> AnyChromosome<A> of(
		final Supplier<? extends A> supplier,
		final Predicate<? super A> validator,
		final IntRange lengthRange
	) {
		return of(supplier, validator, Predicates.True(), lengthRange);
	}

	/**
	 * Create a new chromosome of type {@code A} with the given parameters.
	 *
	 * @param <A> the allele type
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @param validator the validator used for validating the created gene. This
	 *        predicate is used in the {@link AnyGene#isValid()} method.
	 * @param length the length of the chromosome
	 * @return a new chromosome of allele type {@code A}
	 * @throws NullPointerException if the {@code supplier} or {@code validator}
	 *         is {@code null}
	 * @throws IllegalArgumentException if chromosome length is smaller than one.
	 */
	public static <A> AnyChromosome<A> of(
		final Supplier<? extends A> supplier,
		final Predicate<? super A> validator,
		final int length
	) {
		return of(supplier, validator, Predicates.True(), length);
	}

	/**
	 * Create a new chromosome of type {@code A} with the given parameters and
	 * length one.
	 *
	 * @param <A> the allele type
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @param validator the validator used for validating the created gene. This
	 *        predicate is used in the {@link #isValid()} method.
	 * @return a new chromosome of allele type {@code A}
	 * @throws NullPointerException if the {@code supplier} or {@code validator}
	 *         is {@code null}
	 */
	public static <A> AnyChromosome<A> of(
		final Supplier<? extends A> supplier,
		final Predicate<? super A> validator
	) {
		return of(supplier, validator, 1);
	}

	/**
	 * Create a new chromosome of type {@code A} with the given parameters. The
	 * {@code validator} predicate of the generated gene will always return
	 * {@code true}.
	 *
	 * @since 4.0
	 *
	 * @param <A> the allele type
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @param lengthRange the allowed length range of the chromosome
	 * @return a new chromosome of allele type {@code A}
	 * @throws NullPointerException if the {@code supplier} is {@code null}
	 * @throws IllegalArgumentException if chromosome length is smaller than one.
	 */
	public static <A> AnyChromosome<A> of(
		final Supplier<? extends A> supplier,
		final IntRange lengthRange
	) {
		return of(supplier, Predicates.TRUE, lengthRange);
	}

	/**
	 * Create a new chromosome of type {@code A} with the given parameters. The
	 * {@code validator} predicate of the generated gene will always return
	 * {@code true}.
	 *
	 * @param <A> the allele type
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @param length the length of the created chromosome
	 * @return a new chromosome of allele type {@code A}
	 * @throws NullPointerException if the {@code supplier} is {@code null}
	 * @throws IllegalArgumentException if chromosome length is smaller than one.
	 */
	public static <A> AnyChromosome<A> of(
		final Supplier<? extends A> supplier,
		final int length
	) {
		return of(supplier, Predicates.TRUE, length);
	}

	/**
	 * Create a new chromosome of type {@code A} with the given parameters and
	 * length one. The {@code validator} predicate of the generated gene will
	 * always return {@code true}.
	 *
	 * @param <A> the allele type
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @return a new chromosome of allele type {@code A}
	 * @throws NullPointerException if the {@code supplier} is {@code null}
	 */
	public static <A> AnyChromosome<A> of(final Supplier<? extends A> supplier) {
		return of(supplier, 1);
	}

}
