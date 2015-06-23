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

import org.jenetics.DoubleGene;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class EngineOptimizer<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
> {

	static <G extends Gene<?, G>, C extends Comparable<? super C>> C fitness(
		final int generations,
		final Parameters<G, C> params,
		final Codec<G, C> codec,
		final Function<Genotype<G>, C> ff
	) {
		final Engine<G, C> engine = Engine.builder(ff, codec.encoding())
			.alterers(params.getAlterers())
			.offspringSelector(params.getOffspringSelector())
			.survivorsSelector(params.getSurvivorsSelector())
			.offspringFraction(params.getOffspringFraction())
			.populationSize(params.getPopulationSize())
			.maximalPhenotypeAge(params.getMaximalPhenotypeAge())
			.build();

		final Genotype<G> gt = engine.stream()
			.limit(generations)
			.collect(EvolutionResult.toBestGenotype());

		return codec.decoder().apply(gt);
	}

	static double problemFitness(final Genotype<DoubleGene> gt) {
		return Math.sin(gt.getGene().doubleValue());
	}

	public static void main(final String[] args) {
		final Codec<DoubleGene, Double> problemCodec = Codec.ofDouble(0.0, 2*Math.PI);

		final Codec<DoubleGene, Parameters<DoubleGene, Double>> parametersCodec =
			new ParametersCodec<>(
				ISeq.of(
					new GaussianMutatorProxy<DoubleGene, Double>(0.5),
					new MeanAltererProxy<DoubleGene, Double>(0.5),
					new MultiPointCrossoverProxy<DoubleGene, Double>(0.5, 2, 5),
					new MutatorProxy<DoubleGene, Double>(0.5)
				),
				ISeq.of(
					new BoltzmannSelectorProxy<DoubleGene, Double>(1),
					new ExponentialRankSelectorProxy<DoubleGene, Double>(1),
					new LinearRankSelectorProxy<DoubleGene, Double>(1),
					new RouletteWheelSelectorProxy<DoubleGene, Double>(1),
					new StochasticUniversalSelectorProxy<DoubleGene, Double>(1),
					new TournamentSelectorProxy<DoubleGene, Double>(1.0, 5),
					new TruncationSelectorProxy<DoubleGene, Double>(1)
				),
				ISeq.of(
					new BoltzmannSelectorProxy<DoubleGene, Double>(1),
					new ExponentialRankSelectorProxy<DoubleGene, Double>(1),
					new LinearRankSelectorProxy<DoubleGene, Double>(1),
					new RouletteWheelSelectorProxy<DoubleGene, Double>(1),
					new StochasticUniversalSelectorProxy<DoubleGene, Double>(1),
					new TournamentSelectorProxy<DoubleGene, Double>(1.0, 5),
					new TruncationSelectorProxy<DoubleGene, Double>(1)
				),
				50, 100,
				10, 1000
			);

		final Function<Parameters<DoubleGene, Double>, Double> pff = p -> {
			return fitness(20, p, problemCodec, EngineOptimizer::problemFitness);
		};

		final Engine<DoubleGene, Double> engine = Engine
			.builder(pff.compose(parametersCodec.decoder()), parametersCodec.encoding())
			.build();

		final Genotype<DoubleGene> gt = engine.stream()
			.limit(20)
			.collect(EvolutionResult.toBestGenotype());

		final Parameters<DoubleGene, Double> params = parametersCodec.decoder().apply(gt);
		System.out.println(params);
		System.out.println();
		System.out.println();
	}

}
