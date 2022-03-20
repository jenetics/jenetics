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
package io.jenetics.ext.grammar;

/**
 * Generator interface for generating <em>sentences</em>/<em>derivation trees</em>
 * from a given grammar.
 *
 * @param <T> the terminal token type of the grammar
 * @param <R> the result type of the generator
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
@FunctionalInterface
public interface Generator<T, R> {

	/**
	 * Generates a new sentence from the given grammar. If the generation of the
	 * sentence fails, an empty list is returned.
	 *
	 * @param cfg the generating grammar
	 * @return a newly created result
	 */
	R generate(final Cfg<? extends T> cfg);

}
