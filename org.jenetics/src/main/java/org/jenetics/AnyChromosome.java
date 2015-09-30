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
package org.jenetics;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jenetics.util.ISeq;

/**
 * {@code Chromosome} implementation, which allows to create genes without
 * explicit implementing the {@code Chromosome} interface.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class AnyChromosome<A> extends AbstractChromosome<AnyGene<A>> {

	private final Supplier<? extends A> _supplier;
	private final Predicate<? super A> _validator;

	protected AnyChromosome(
		final ISeq<AnyGene<A>> genes,
		final Supplier<? extends A> supplier,
		final Predicate<? super A> validator
	) {
		super(genes);
		_supplier = requireNonNull(supplier);
		_validator = requireNonNull(validator);
	}

	@Override
	public Chromosome<AnyGene<A>> newInstance(
		final ISeq<AnyGene<A>> genes
	) {
		return new AnyChromosome<>(genes, _supplier, _validator);
	}

	@Override
	public Chromosome<AnyGene<A>> newInstance() {
		return of(_supplier, _validator, length());
	}


	/* *************************************************************************
	 *  Static factory methods.
	 * ************************************************************************/

	/**
	 * Create a new chromosome of type {@code A} with the given parameters.
	 *
	 * @param <A> the allele type
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @param validator the validator used for validating the created gene. This
	 *        predicate is used in the {@link #isValid()} method.
	 * @param length the length of the created chromosome
	 * @return a new chromosome of allele type {@code A}
	 * @throws NullPointerException if the {@code supplier} or {@code validator}
	 *         is {@code null}
	 * @throws IllegalArgumentException if the length of the gene array is
	 *         smaller than one.
	 */
	public static <A> AnyChromosome<A> of(
		final Supplier<? extends A> supplier,
		final Predicate<? super A> validator,
		final int length
	) {
		return new AnyChromosome<A>(
			AnyGene.seq(length, supplier, validator),
			supplier,
			validator
		);
	}

	/**
	 * Create a new chromosome of type {@code A} with the given parameters and
	 * length 1.
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
	 * @param <A> the allele type
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @param length the length of the created chromosome
	 * @return a new chromosome of allele type {@code A}
	 * @throws NullPointerException if the {@code supplier} is {@code null}
	 * @throws IllegalArgumentException if the length of the gene array is
	 *         smaller than one.
	 */
	public static <A> AnyChromosome<A> of(
		final Supplier<? extends A> supplier,
		final int length
	) {
		return of(supplier, a -> true, length);
	}

	/**
	 * Create a new chromosome of type {@code A} with the given parameters and
	 * length 1. The {@code validator} predicate of the generated gene will
	 * always return {@code true}.
	 *
	 * @param <A> the allele type
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @return a new chromosome of allele type {@code A}
	 * @throws NullPointerException if the {@code supplier} is {@code null}
	 */
	public static <A> AnyChromosome<A> of(
		final Supplier<? extends A> supplier
	) {
		return of(supplier, 1);
	}

}
