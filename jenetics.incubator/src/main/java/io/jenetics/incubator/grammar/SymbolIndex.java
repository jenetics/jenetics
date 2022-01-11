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

import io.jenetics.incubator.grammar.Cfg.Rule;

/**
 * Interface for selecting a symbol index.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
@FunctionalInterface
public interface SymbolIndex {

	/**
	 * Selects an index with the given upper {@code bound}, exclusively.
	 *
	 * @param rule the rule which requested the index
	 * @param bound the upper bound of the symbol index, exclusively
	 * @return the next symbol index
	 * @throws IllegalArgumentException if the given {@code bound} is smaller
	 *         than one
	 */
	int next(final Rule rule, final int bound);

	/**
	 * Create a new symbol-index object from the given random generator. This
	 * can be used for generating random sentences of derivation-trees.
	 *
	 * @param random the random generator used for generating the sentences
	 * @return a new symbol-index object from the given random generator
	 */
	static SymbolIndex of(final RandomGenerator random) {
		return (rule, bound) -> random.nextInt(bound);
	}

}
