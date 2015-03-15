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
import org.jenetics.Optimize;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Optimizer<ARG_TYPE> {

	/**
	 * Worker class used for hiding the gene type.
	 *
	 * @param <G> the gene type
	 */
	private class Worker<G extends Gene<?, G>> {

		private class Exec<R extends Comparable<? super R>> {

			EvolutionParam<G, R> _param;

			ARG_TYPE optimize(final Function<ARG_TYPE, R> function, final Optimize optimize) {
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
		}

		private final Codec<G, ARG_TYPE> _codec;

		private Worker(final Codec<G, ARG_TYPE> codec) {
			_codec = requireNonNull(codec);
		}

		<R extends Comparable<? super R>> EvolutionParam<G, R> param() {
			return new EvolutionParam<>();
		}

		private <R extends Comparable<? super R>> ARG_TYPE
		optimize(final Function<ARG_TYPE, R> function, final Optimize optimize) {
			final EvolutionParam<G, R> param = param();

			final Engine<G, R> engine = Engine
				.builder(function.compose(_codec.decoder()), _codec.encoding())
				.fitnessScaler(param.getFitnessScaler())
				.survivorsSelector(param.getSurvivorsSelector())
				.offspringSelector(param.getOffspringSelector())
				.alterers(param.getAlterers())
				.optimize(optimize)
				.offspringFraction(param.getOffspringFraction())
				.populationSize(param.getPopulationSize())
				.maximalPhenotypeAge(param.getMaximalPhenotypeAge())
				.build();

			final Genotype<G> bgt = engine.stream()
				.limit(limit.bySteadyFitness(30))
				.collect(EvolutionResult.toBestGenotype());

			return _codec.decoder().apply(bgt);
		}

		<R extends Comparable<? super R>> ARG_TYPE
		argmin(final Function<ARG_TYPE, R> function) {
			return optimize(function, Optimize.MINIMUM);
		}

		<R extends Comparable<? super R>> ARG_TYPE
		argmax(final Function<ARG_TYPE, R> function) {
			return optimize(function, Optimize.MAXIMUM);
		}

	}

	private Worker<?> _worker;

	private Optimizer() {
	}

	public <R extends Comparable<? super R>> ARG_TYPE
	argmin(final Function<ARG_TYPE, R> function) {
		return _worker.argmin(function);
	}

	public <R extends Comparable<? super R>> ARG_TYPE
	argmax(final Function<ARG_TYPE, R> function) {
		return _worker.argmax(function);
	}

	/**
	 *
	 * @param codec
	 * @param <G>
	 * @param <S>
	 * @return
	 */
	public static <G extends Gene<?, G>, S> Optimizer<S>
	of(final Codec<G, S> codec) {
		final Optimizer<S> optimizer = new Optimizer<>();
		optimizer._worker = optimizer.new Worker<>(codec);

		return optimizer;
	}

	public static Optimizer<Double> ofDouble(
		final double min,
		final double max
	) {
		final Codec<DoubleGene, Double> codec = Codec.ofDouble(min, max);
		final Optimizer<Double> optimizer = new Optimizer<>();
		optimizer._worker = optimizer.new Worker<>(codec);

		return of(Codec.ofDouble(min, max));
	}

	public static void main(final String[] args) {
		final double result = Optimizer.ofDouble(0, 100).argmin(i -> i);

		System.out.println(result);
	}
}
