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

import io.jenetics.Gene;
import io.jenetics.Mutator;
import io.jenetics.TruncationSelector;
import io.jenetics.engine.Engine.Builder;
import io.jenetics.engine.Engine.Setup;
import io.jenetics.internal.util.Requires;

/**
 * Setup for a (μ, λ)-Evolution Strategy. Applying this setup is done in the
 * following way.
 * <pre>{@code
 * final var engine = Engine.builder(problem)
 *     .setup(new MLEvolutionStrategy<>(μ, λ, p)
 *     .build();
 * }</pre>
 *
 * And is equivalent to the following builder setup.
 * <pre>{@code
 * final var engine = Engine.builder(problem)
 *     .populationSize(λ)
 *     .survivorsSize(0)
 *     .offspringSelector(new TruncationSelector<>(μ))
 *     .alterers(new Mutator<>(p))
 *     .build();
 * }</pre>
 *
 * @param <G> the gene type
 * @param <C> the fitness result type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 6.0
 */
public final class MLEvolutionStrategy<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Setup<G, C>
{

	private final int _mu;
	private final int _lambda;
	private final double _mutationProbability;

	/**
	 * Create a new (μ, λ)-Evolution Strategy with the given parameters.
	 *
	 * @param mu the number of the fittest individuals to be selected
	 * @param lambda the population count
	 * @param mutationProbability the mutation probability
	 * @throws IllegalArgumentException if {@code mu < 2} or {@code lambda < mu}
	 *         or {@code mutationProbability not in [0, 1]}
	 */
	public MLEvolutionStrategy(
		final int mu,
		final int lambda,
		final double mutationProbability
	) {
		if (mu < 2) {
			throw new IllegalArgumentException(format(
				"mu (μ) must be greater or equal 2: %d.", mu
			));
		}
		if (lambda < mu) {
			throw new IllegalArgumentException(format(
				"lambda (λ) must be greater or equal then μ [μ=%d, λ=%d].",
				mu, lambda
			));
		}

		_mu = mu;
		_lambda = lambda;
		_mutationProbability = Requires.probability(mutationProbability);
	}

	/**
	 * Create a new (μ, λ)-Evolution Strategy with the given parameters. The
	 * mutation probability is set to {@link Mutator#DEFAULT_ALTER_PROBABILITY}.
	 *
	 * @param mu the number of the fittest individuals to be selected
	 * @param lambda the population count
	 * @throws IllegalArgumentException if {@code mu < 2} or {@code lambda < mu}
	 *         or {@code mutationProbability not in [0, 1]}
	 */
	public MLEvolutionStrategy(final int mu, final int lambda) {
		this(mu, lambda, Mutator.DEFAULT_ALTER_PROBABILITY);
	}

	@Override
	public void apply(final Builder<G, C> builder) {
		builder.populationSize(_lambda)
			.survivorsSize(0)
			.offspringSelector(new TruncationSelector<>(_mu))
			.alterers(new Mutator<>(_mutationProbability));
	}

}
