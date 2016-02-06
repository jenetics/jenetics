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
package org.jenetics.tool.optimizer;

import static java.time.Duration.ofMillis;
import static org.jenetics.engine.limit.byExecutionTime;
import static org.jenetics.engine.limit.bySteadyFitness;
import static org.jenetics.tool.optimizer.AltererCodec.ofMultiPointCrossover;
import static org.jenetics.tool.optimizer.AltererCodec.ofSwapMutator;
import static org.jenetics.tool.optimizer.SelectorCodec.ofBoltzmannSelector;
import static org.jenetics.tool.optimizer.SelectorCodec.ofExponentialRankSelector;
import static org.jenetics.tool.optimizer.SelectorCodec.ofLinearRankSelector;
import static org.jenetics.tool.optimizer.SelectorCodec.ofTournamentSelector;

import org.jenetics.BitGene;
import org.jenetics.Optimize;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.StochasticUniversalSelector;
import org.jenetics.TruncationSelector;
import org.jenetics.engine.EvolutionParam;
import org.jenetics.tool.problem.Knapsack;
import org.jenetics.util.DoubleRange;
import org.jenetics.util.IntRange;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.LongRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class EvolutionParamOptimizerTest {

	public static void main(final String[] args) {
		// The problem fow which to optimize the EvolutionParams.
		final Knapsack problem = Knapsack.of(25, new LCG64ShiftRandom(10101));

		final IntRange populationSize = IntRange.of(10, 1_000);
		final DoubleRange offspringFraction = DoubleRange.of(0, 1);
		final LongRange maximalPhenotypeAge = LongRange.of(5, 10_000);

		final EvolutionParamCodec<BitGene, Double> codec =
			EvolutionParamCodec.of(
				SelectorCodec
					.of(new RouletteWheelSelector<BitGene, Double>())
					.and(new TruncationSelector<>())
					.and(new StochasticUniversalSelector<>())
					.and(ofBoltzmannSelector(DoubleRange.of(0, 3)))
					.and(ofExponentialRankSelector(DoubleRange.of(0, 1)))
					.and(ofLinearRankSelector(DoubleRange.of(0, 3)))
					.and(ofTournamentSelector(IntRange.of(2, 10))),
				AltererCodec.<BitGene, Double>ofMutator()
					.and(ofMultiPointCrossover(IntRange.of(2, 20)))
					.and(ofSwapMutator())
			);

		final EvolutionParamOptimizer<BitGene, Double> optimizer =
			new EvolutionParamOptimizer<>(codec, () -> bySteadyFitness(250));

		final EvolutionParam<BitGene, Double> params = optimizer
			.optimize(
				problem,
				Optimize.MAXIMUM,
				() -> byExecutionTime(ofMillis(150)));

		System.out.println();
		System.out.println("Best parameters:");
		System.out.println(params);
	}

}
