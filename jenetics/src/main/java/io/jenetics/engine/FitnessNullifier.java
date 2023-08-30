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

import java.util.concurrent.atomic.AtomicBoolean;

import io.jenetics.Gene;
import io.jenetics.Phenotype;

/**
 * This class allows forcing a reevaluation of the fitness function. A
 * reevaluation is necessary if the fitness function changes. Changing the
 * fitness function is not the usual use case, but is necessary for some
 * problems, like symbolic regression analyses with changing input data (time
 * series).
 *
 * <pre>{@code
 * final var nullifier = new FitnessNullifier<DoubleGene, Double>();
 *
 * final Engine<DoubleGene, Double> engine = Engine.builder(problem)
 *     .interceptor(nullifier)
 *     .build();
 *
 * // Invalidate fitness value by calling the 'nullifyFitness' method,
 * // possible from a different thread. This forces the reevaluation of
 * // the fitness values at the start of the next generation.
 * nullifier.nullifyFitness();
 * }</pre>
 *
 * @implNote
 * This interceptor is thread-safe and can be used from different threads. No
 * additional synchronization is needed.
 *
 * @see Phenotype#nullifyFitness()
 *
 * @param <G> the gene type
 * @param <C> the fitness result type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 6.0
 * @version 6.0
 */
public final class FitnessNullifier<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements EvolutionInterceptor<G, C>
{

	private final AtomicBoolean _invalid = new AtomicBoolean(false);

	/**
	 * Nullifies the fitness values of the population, if requested. The
	 * <em>nullification flag</em> is reset after this call. Two consecutive
	 * calls of this method might lead to two different results.
	 *
	 * @see #nullifyFitness()
	 *
	 * @param start the evolution start object
	 * @return the evolution start object with the nullified fitness values,
	 *         if the nullification has been triggered
	 */
	@Override
	public EvolutionStart<G, C> before(final EvolutionStart<G, C> start) {
		final boolean invalid = _invalid.getAndSet(false);
		return invalid ? invalidate(start) : start;
	}

	private EvolutionStart<G, C> invalidate(final EvolutionStart<G, C> start) {
		return EvolutionStart.of(
			start.population().map(Phenotype::nullifyFitness),
			start.generation()
		);
	}

	/**
	 * Triggers the nullification of the fitness values of the population for
	 * the next generation.
	 *
	 * @see #before(EvolutionStart)
	 *
	 * @return {@code true} if the <em>nullification</em> request will trigger
	 *         a new fitness nullification. @{code false} if the fitness
	 *         <em>nullification</em> has been requested before, without
	 *         actually executing it.
	 */
	public boolean nullifyFitness() {
		return !_invalid.getAndSet(true);
	}

}
