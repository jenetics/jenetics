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
import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ObjectGene<A> implements Gene<A, ObjectGene<A>> {

	private final A _allele;
	private final Supplier<? extends A> _supplier;
	private final Predicate<? super A> _validator;

	private ObjectGene(
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
	public ObjectGene<A> newInstance() {
		return new ObjectGene<>(_supplier.get(), _supplier, _validator);
	}

	@Override
	public ObjectGene<A> newInstance(final A value) {
		return new ObjectGene<>(value, _supplier, _validator);
	}

	@Override
	public boolean isValid() {
		return _validator.test(_allele);
	}

	public static <A> ObjectGene<A> of(
		final A allele,
		final Supplier<? extends A> supplier,
		final Predicate<? super A> validator
	) {
		return new ObjectGene<>(allele, supplier, validator);
	}

	public static <A> ObjectGene<A> of(
		final A allele,
		final Supplier<? extends A> supplier
	) {
		return new ObjectGene<>(allele, supplier, a -> true);
	}

	public static <A> ObjectGene<A> of(final Supplier<? extends A> supplier) {
		return new ObjectGene<>(supplier.get(), supplier, a -> true);
	}

	public static <A> ObjectGene<A> of(
		final Supplier<? extends A> supplier,
		final Predicate<? super A> validator
	) {
		return new ObjectGene<>(supplier.get(), supplier, validator);
	}

	static <A> ISeq<ObjectGene<A>> seq(
		final int length,
		final Supplier<? extends A> supplier,
		final Predicate<? super A> validator
	) {
		return MSeq.<ObjectGene<A>>ofLength(length)
			.fill(() -> of(supplier.get(), supplier, validator))
			.toISeq();
	}

}
