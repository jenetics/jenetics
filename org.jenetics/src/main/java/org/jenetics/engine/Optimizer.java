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

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import org.jenetics.DoubleGene;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutator;
import org.jenetics.Optimize;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Optimizer<T, R extends Comparable<? super R>> {

	/**
	 * Worker class used for hiding the gene type.
	 *
	 * @param <G> the gene type
	 */
	private class Worker<G extends Gene<?, G>> {
		private final Codec<G, T> _codec;
		private final EvolutionParam<G, R> _param;

		Worker(final Codec<G, T> codec, final EvolutionParam<G, R> param) {
			_codec = requireNonNull(codec);
			_param = requireNonNull(param);
		}

		private T optimize(final Function<T, R> function, final Optimize optimize) {
			final Engine<G, R> engine = Engine
				.builder(function.compose(_codec.decoder()), _codec.encoding())
				.fitnessScaler(_param.getFitnessScaler())
				.survivorsSelector(_param.getSurvivorsSelector())
				.offspringSelector(_param.getOffspringSelector())
				.alterers(_param.getAlterers())
				.optimize(optimize)
				.offspringFraction(_param.getOffspringFraction())
				.populationSize(_param.getPopulationSize())
				.maximalPhenotypeAge(_param.getMaximalPhenotypeAge())
				.build();

			final Genotype<G> bgt = engine.stream()
				.limit(limit.bySteadyFitness(30))
				.collect(EvolutionResult.toBestGenotype());

			return _codec.decoder().apply(bgt);
		}

		T argmin(final Function<T, R> function) {
			return optimize(function, Optimize.MINIMUM);
		}

		T argmax(final Function<T, R> function) {
			return optimize(function, Optimize.MAXIMUM);
		}

	}

	private Worker<?> _worker;

	private Optimizer() {
	}

	public T argmin(final Function<T, R> function) {
		return _worker.argmin(function);
	}

	public static <R extends Comparable<? super R>> Double
	sargmin(final double min, final double max, final Function<Double, R> f) {
		return null;
	}

	public T argmax(final Function<T, R> function) {
		return _worker.argmax(function);
	}

	private static <
		G extends Gene<?, G>,
		S,
		R extends Comparable<? super R>
	>
	Optimizer<S, R> of(final Codec<G, S> codec, final EvolutionParam<G, R> param) {
		final Optimizer<S, R> optimizer = new Optimizer<>();
		optimizer._worker = optimizer.new Worker<>(codec, param);

		return optimizer;
	}

	private static <
		G extends Gene<?, G>,
		S,
		R extends Comparable<? super R>
	>
	Optimizer<S, R> of(final Codec<G, S> codec) {
		return of(codec, new EvolutionParam<G, R>());
	}

	public static <R extends Comparable<? super R>> Optimizer<Double, R> ofDouble(
		final double min,
		final double max
	) {
		final Codec<DoubleGene, Double> codec = Codec.ofDouble(min, max);
		final EvolutionParam<DoubleGene, R> param =
			new EvolutionParam<DoubleGene, R>()
				.alterers(
					new Mutator<>(0.15),
					new MeanAlterer<>()
				);

		return of(codec, param);
	}

	public static void main(final String[] args) {
		final Double result = Optimizer.<Double>ofDouble(0, Math.PI)
			.argmin(Math::sin);

		System.out.println(result);
	}
}
