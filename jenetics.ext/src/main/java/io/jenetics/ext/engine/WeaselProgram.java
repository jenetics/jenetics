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
package io.jenetics.ext.engine;

import io.jenetics.Gene;
import io.jenetics.engine.Engine.Builder;
import io.jenetics.engine.EngineSetup;
import io.jenetics.internal.util.Requires;

import io.jenetics.ext.WeaselMutator;
import io.jenetics.ext.WeaselSelector;

/**
 * Configures the evolution engine to execute the
 * <a href="https://en.wikipedia.org/wiki/Weasel_program">Weasel program</a>
 * algorithm.
 *
 * <pre>{@code
 * final Engine<CharacterGene, Integer> engine = Engine.builder(problem)
 *     .setup(new WeaselProgram<>())
 *     .build();
 * }</pre>
 *
 * @see WeaselSelector
 * @see WeaselMutator
 *
 * @param <G> the gene type
 * @param <C> the fitness result type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class WeaselProgram<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements EngineSetup<G, C>
{

	private final double _mutationProbability;

	/**
	 * Create a new weasel program setup with the give mutation probability.
	 *
	 * @param mutationProbability the mutation probability
	 * @throws IllegalArgumentException if the {@code mutationProbability} is
	 *         not in the valid range of {@code [0, 1]}.
	 */
	public WeaselProgram(final double mutationProbability) {
		_mutationProbability = Requires.probability(mutationProbability);
	}

	/**
	 * Create a new weasel program setup with the <em>default</em> mutation
	 * probability of {@code 0.05}.
	 */
	public WeaselProgram() {
		this(0.05);
	}

	@Override
	public void apply(final Builder<G, C> builder) {
		builder.selector(new WeaselSelector<>());
		builder.offspringFraction(1);
		builder.alterers(new WeaselMutator<>(_mutationProbability));
	}

}
