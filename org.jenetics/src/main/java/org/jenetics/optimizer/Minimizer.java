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
package org.jenetics.optimizer;

import java.util.function.Function;
import java.util.function.Predicate;

import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.Optimize;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;

/**
 * @param <T> the argument type of function to optimize
 * @param <R> the result type of the function to optimize
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface Minimizer<T, R extends Comparable<? super R>> {

	public T argmin(final Function<T, R> function);


	public static <
		T,
		R extends Comparable<? super R>,
		G extends Gene<?, G>
	>
	Minimizer<T, R> of(
		final Codec<T, G> codec,
		final Parameters<G, R> parameters,
		final Predicate<? super EvolutionResult<G, R>> limit
	) {
		return function -> {
			final Engine<G, R> engine = Engine
				.builder(function, codec)
				.optimize(Optimize.MINIMUM)
				.alterers(parameters.getAlterers().get(0))
				.offspringSelector(parameters.getOffspringSelector())
				.survivorsSelector(parameters.getSurvivorsSelector())
				.offspringFraction(parameters.getOffspringFraction())
				.populationSize(parameters.getPopulationSize())
				.maximalPhenotypeAge(parameters.getMaximalPhenotypeAge())
				.build();

			final Genotype<G> gt = engine.stream()
				.limit(limit)
				.collect(EvolutionResult.toBestGenotype());

			return codec.decoder().apply(gt);
		};
	}

}
