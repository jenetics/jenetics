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

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jenetics.internal.util.Equality;

import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * {@code Gene} implementation, which allows to create genes without explicit
 * implementing the {@code Gene} interface.
 *
 * <pre>{@code
 * class Main {
 *     // First monday of 2015.
 *     private static final LocalDate MIN_MONDAY = LocalDate.of(2015, 1, 5);
 *
 *     // Supplier of random 'LocalDate' objects. The implementation is responsible
 *     // for guaranteeing the desired allele restriction. In this case we will
 *     // generate only mondays.
 *     static LocalDate nextRandomMonday() {
 *         return MIN_MONDAY.plusWeeks(RandomRegistry.getRandom().nextInt(1000));
 *     }
 *
 *     // Create a new 'LocalDate' gene. All other genes, created with
 *     // gene.newInstance(), are calling the 'newRandomMonday' method.
 *     final AnyGene<LocalDate> gene = AnyGene.of(Main::nextRandomMonday);
 * }
 * }</pre>
 * The example above shows how to create {@code LocalDate} genes from a random
 * {@code LocalDate} supplier. It also shows how to implement a restriction on
 * the created dates. The usage of the {@code AnyGene} class is useful for
 * supporting custom allele types without explicit implementation of the
 * {@code Gene} interface. But the {@code AnyGene} can only be used for a subset
 * of the existing alterers.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.3
 * @since 3.3
 */
public final class AnyGene<A> implements Gene<A, AnyGene<A>> {

	private final A _allele;
	private final Supplier<? extends A> _supplier;
	private final Predicate<? super A> _validator;

	private AnyGene(
		final A allele,
		final Supplier<? extends A> supplier,
		final Predicate<? super A> validator
	) {
		_allele = allele;
		_supplier = requireNonNull(supplier);
		_validator = requireNonNull(validator);
	}

	@Override
	public A getAllele() {
		return _allele;
	}

	@Override
	public AnyGene<A> newInstance() {
		return new AnyGene<>(_supplier.get(), _supplier, _validator);
	}

	@Override
	public AnyGene<A> newInstance(final A value) {
		return new AnyGene<>(value, _supplier, _validator);
	}

	@Override
	public boolean isValid() {
		return _validator.test(_allele);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(_allele);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof AnyGene<?> &&
			Equality.eq(((AnyGene<?>)obj)._allele, _allele);
	}

	@Override
	public String toString() {
		return Objects.toString(_allele);
	}


	/* *************************************************************************
	 *  Static factory methods.
	 * ************************************************************************/

	/**
	 * Create a new {@code AnyGene} instance with the given parameters. New
	 * (random) genes are created with the given allele {@code supplier}.
	 *
	 * @param <A> the allele type
	 * @param allele the actual allele instance the created gene represents.
	 *        {@code null} values are allowed.
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @param validator the validator used for validating the created gene. This
	 *        predicate is used in the {@link #isValid()} method.
	 * @return a new {@code AnyGene} with the given parameters
	 * @throws NullPointerException if the {@code supplier} or {@code validator}
	 *         is {@code null}
	 */
	public static <A> AnyGene<A> of(
		final A allele,
		final Supplier<? extends A> supplier,
		final Predicate<? super A> validator
	) {
		return new AnyGene<>(allele, supplier, validator);
	}

	/**
	 * Create a new {@code AnyGene} instance with the given parameters. New
	 * (random) genes are created with the given allele {@code supplier}. The
	 * {@code validator} predicate of the generated gene will always return
	 * {@code true}.
	 *
	 * @param <A> the allele type
	 * @param allele the actual allele instance the created gene represents.
	 *        {@code null} values are allowed.
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @return a new {@code AnyGene} with the given parameters
	 * @throws NullPointerException if the {@code suppler} is {@code null}
	 */
	public static <A> AnyGene<A> of(
		final A allele,
		final Supplier<? extends A> supplier
	) {
		return new AnyGene<>(allele, supplier, a -> true);
	}

	/**
	 * Create a new {@code AnyGene} instance with the given allele
	 * {@code supplier}. The {@code validator} predicate of the generated gene
	 * will always return {@code true}.
	 *
	 * @param <A> the allele type
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @return a new {@code AnyGene} with the given parameters
	 * @throws NullPointerException if one of the parameters is {@code null}
	 */
	public static <A> AnyGene<A> of(final Supplier<? extends A> supplier) {
		return new AnyGene<>(supplier.get(), supplier, a -> true);
	}

	/**
	 * Create a new {@code AnyGene} instance with the given parameters. New
	 * (random) genes are created with the given allele {@code supplier}.
	 *
	 * @param <A> the allele type
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random alleles
	 * @param validator the validator used for validating the created gene. This
	 *        predicate is used in the {@link #isValid()} method.
	 * @return a new {@code AnyGene} with the given parameters
	 * @throws NullPointerException if one of the parameters is {@code null}
	 */
	public static <A> AnyGene<A> of(
		final Supplier<? extends A> supplier,
		final Predicate<? super A> validator
	) {
		return new AnyGene<>(supplier.get(), supplier, validator);
	}

	// Create gene sequence.
	static <A> ISeq<AnyGene<A>> seq(
		final int length,
		final Supplier<? extends A> supplier,
		final Predicate<? super A> validator
	) {
		return MSeq.<AnyGene<A>>ofLength(length)
			.fill(() -> of(supplier.get(), supplier, validator))
			.toISeq();
	}

}
