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
package io.jenetics.engine;

import java.util.function.Supplier;

import io.jenetics.Gene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface EvolutionStreamable<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	/**
	 * Create a new <b>infinite</b> evolution stream with a newly created
	 * population.
	 *
	 * @return a new evolution stream.
	 */
	public EvolutionStream<G, C> stream();

	/**
	 * Create a new <b>infinite</b> evolution stream with the given evolution
	 * start. If an empty {@code Population} is given, the engines genotype
	 * factory is used for creating the population. The given population might
	 * be the result of an other engine and this method allows to start the
	 * evolution with the outcome of an different engine. The fitness function
	 * and the fitness scaler are replaced by the one defined for this engine.
	 *
	 * @param start the data the evolution stream starts with
	 * @return a new <b>infinite</b> evolution iterator
	 * @throws java.lang.NullPointerException if the given evolution
	 *         {@code start} is {@code null}.
	 */
	public EvolutionStream<G, C>
	stream(final EvolutionStart<G, C> start);

	/**
	 * Create a new <b>infinite</b> evolution stream with the given evolution
	 * start. If an empty {@code Population} is given, the engines genotype
	 * factory is used for creating the population. The given population might
	 * be the result of an other engine and this method allows to start the
	 * evolution with the outcome of an different engine. The fitness function
	 * and the fitness scaler are replaced by the one defined for this engine.
	 *
	 * @param start the data the evolution stream starts with
	 * @return a new <b>infinite</b> evolution iterator
	 * @throws java.lang.NullPointerException if the given evolution
	 *         {@code start} is {@code null}.
	 */
	public EvolutionStream<G, C>
	stream(final Supplier<EvolutionStart<G, C>> start);

}
