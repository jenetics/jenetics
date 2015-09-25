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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class ObjectChromosome<A> extends AbstractChromosome<ObjectGene<A>> {

	private final Supplier<A> _supplier;

	protected ObjectChromosome(final ISeq<ObjectGene<A>> genes, final Supplier<A> supplier) {
		super(genes);
		_supplier = requireNonNull(supplier);
	}

	@Override
	public Chromosome<ObjectGene<A>> newInstance(final ISeq<ObjectGene<A>> genes) {
		return new ObjectChromosome<>(genes, _supplier);
	}

	@Override
	public Chromosome<ObjectGene<A>> newInstance() {
		return of(length(), _supplier);
	}

	public static <A> ObjectChromosome<A> of(final int length, final Supplier<A> supplier) {
		return new ObjectChromosome<A>(ObjectGene.seq(length, supplier), supplier);
	}

}
