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
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
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
		_allele = requireNonNull(allele);
		_supplier = requireNonNull(supplier);
		_validator = requireNonNull(validator);
	}

	@Override
	public A getAllele() {
		return _allele;
	}

	public Supplier<? extends A> getSupplier() {
		return _supplier;
	}

	public Predicate<? super A> getValidator() {
		return _validator;
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
	 * @param allele the actual allele instance the created gene represents
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random genes
	 * @param validator the validator used for validating the created gene. This
	 *        predicate is used in the {@link #isValid()} method.
	 * @return a new {@code AnyGene} with the given parameters
	 * @throws NullPointerException if one of the parameters is {@code null}
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
	 * @param allele the actual allele instance the created gene represents
	 * @param supplier the allele-supplier which is used for creating new,
	 *        random genes
	 * @return a new {@code AnyGene} with the given parameters
	 * @throws NullPointerException if one of the parameters is {@code null}
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
	 *        random genes
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
	 *        random genes
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
