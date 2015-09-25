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
	private final Supplier<A> _supplier;

	private ObjectGene(final A allele, final Supplier<A> supplier) {
		_allele = requireNonNull(allele);
		_supplier = requireNonNull(supplier);
	}

	@Override
	public A getAllele() {
		return _allele;
	}

	@Override
	public ObjectGene<A> newInstance() {
		return new ObjectGene<>(_supplier.get(), _supplier);
	}

	@Override
	public ObjectGene<A> newInstance(final A value) {
		return new ObjectGene<>(value, _supplier);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	public static <A> ObjectGene<A> of(final A allele, final Supplier<A> supplier) {
		return new ObjectGene<>(allele, supplier);
	}

	static <A> ISeq<ObjectGene<A>> seq(
		final int length,
		final Supplier<A> supplier
	) {
		return MSeq.<ObjectGene<A>>ofLength(length)
			.fill(() -> of(supplier.get(), supplier))
			.toISeq();
	}

}
