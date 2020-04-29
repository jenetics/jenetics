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
package io.jenetics.ext;

import static java.lang.String.format;

import java.util.Random;

import io.jenetics.AltererResult;
import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.MutatorResult;
import io.jenetics.Phenotype;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.Seq;

/**
 * Mutator implementation which is part of the
 * <a href="https://en.wikipedia.org/wiki/Weasel_program">Weasel program</a>
 * algorithm. The <i>Weasel program</i> is an thought experiment by Richard
 * Dawkins to illustrate the functioning of the evolution: random <i>mutation</i>
 * combined with non-random cumulative <i>selection</i>.
 * <p>
 * The mutator mutates the genes of <i>every</i> chromosome of <i>every</i>
 * genotype in the population with the given mutation probability.
 * </p>
 * {@link io.jenetics.engine.Engine} setup for the <i>Weasel program:</i>
 * <pre>{@code
 * final Engine<CharacterGene, Integer> engine = Engine.builder(problem)
 *      // Set the 'WeaselSelector'.
 *     .selector(new WeaselSelector<>())
 *      // Disable survivors selector.
 *     .offspringFraction(1)
 *      // Set the 'WeaselMutator'.
 *     .alterers(new WeaselMutator<>(0.05))
 *     .build();
 * }</pre>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Weasel_program">Weasel program</a>
 * @see WeaselSelector
 *
 * @param <G> the gene type
 * @param <C> the fitness result type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.5
 * @version 5.0
 */
public class WeaselMutator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends Mutator<G, C>
{

	/**
	 * Create a new weasel mutator with the given mutation probability.
	 *
	 * @param probability the mutation probability
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *          valid range of {@code [0, 1]}.
	 */
	public WeaselMutator(final double probability) {
		super(probability);
	}

	/**
	 * Create a new weasel mutator with the <em>default</em> mutation probability
	 * of {@code 0.05}.
	 */
	public WeaselMutator() {
		this(0.05);
	}

	@Override
	public AltererResult<G, C>
	alter(final Seq<Phenotype<G, C>> population, final long generation) {
		final var random = RandomRegistry.random();
		final var result = population
			.map(pt -> mutate(pt, generation, _probability, random));

		return AltererResult.of(
			result
				.map(MutatorResult::result)
				.asISeq(),
			result.stream()
				.mapToInt(MutatorResult::mutations)
				.sum()
		);
	}

	@Override
	protected MutatorResult<Genotype<G>> mutate(
		final Genotype<G> genotype,
		final double p,
		final Random random
	) {
		final var result = genotype.stream()
			.map(gt -> mutate(gt, p, random))
			.collect(ISeq.toISeq());

		return MutatorResult.of(
			Genotype.of(result.map(MutatorResult::result)),
			result.stream().mapToInt(MutatorResult::mutations).sum()
		);
	}

	@Override
	public String toString() {
		return format("WeaselMutator[%f]", _probability);
	}

}
