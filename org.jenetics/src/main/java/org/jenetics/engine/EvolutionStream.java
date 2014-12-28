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
package org.jenetics.engine;

import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jenetics.Gene;

/**
 * The {@code EvolutionStream} class extends the Java {@link Stream} and adds a
 * method for limiting the evolution by a given predicate.
 *
 * @see java.util.stream.Stream
 * @see Engine
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-10-22 $</em>
 */
public interface EvolutionStream<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends Stream<EvolutionResult<G, C>>
{

	/**
	 * Returns a stream consisting of the elements of this stream, truncated
	 * when the given {@code proceed} predicate returns {@code false}.
	 * <p>
	 * <i>General usage example:</i>
	 * [code]
	 * final Phenotype&lt;DoubleGene, Double&gt; result = engine.stream()
	 *      // Truncate the evolution stream after 5 "steady" generations.
	 *     .limit(bySteadyFitness(5))
	 *      // The evolution will stop after maximal 100 generations.
	 *     .limit(100)
	 *     .collect(toBestPhenotype());
	 * [/code]
	 *
	 * @see limit
	 *
	 * @param proceed the predicate which determines whether the stream is
	 *        truncated or not. <i>If the predicate returns {@code false}, the
	 *        evolution stream is truncated.</i>
	 * @return the new stream
	 * @throws NullPointerException if the given predicate is {@code null}.
	 */
	public EvolutionStream<G, C>
	limit(final Predicate<? super EvolutionResult<G, C>> proceed);

}
