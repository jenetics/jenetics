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
package io.jenetics.incubator.grammar;

import java.util.random.RandomGenerator;

import io.jenetics.Gene;
import io.jenetics.Genotype;

/**
 * Interface for selecting a symbol index.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
@FunctionalInterface
public interface SymbolIndex {

	@FunctionalInterface
	interface Factor<G extends Gene<?, G>> {
		SymbolIndex create(final Genotype<G> genotype);
	}

	/**
	 * Selects an index with the given upper {@code bound}, exclusively.
	 *
	 * @param bound the upper bound of the symbol index, exclusively
	 * @return the next symbol index
	 * @throws IllegalArgumentException if the given {@code bound} is smaller
	 *         than one
	 */
	int next(final int bound);

	static SymbolIndex of(final RandomGenerator random) {
		return random::nextInt;
	}

}
