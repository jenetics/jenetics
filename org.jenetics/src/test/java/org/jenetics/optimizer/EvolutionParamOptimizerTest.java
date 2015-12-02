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

import static java.time.Duration.ofMillis;
import static org.jenetics.engine.limit.byExecutionTime;
import static org.jenetics.engine.limit.bySteadyFitness;

import org.jentics.problem.Knapsack;

import org.jenetics.BitGene;
import org.jenetics.Optimize;
import org.jenetics.engine.EvolutionParam;
import org.jenetics.util.IntRange;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class EvolutionParamOptimizerTest {

	public static void main(final String[] args) {
		RandomRegistry.setRandom(new LCG64ShiftRandom.ThreadLocal());

		final Knapsack problem =
			RandomRegistry.with(new LCG64ShiftRandom(1234), r -> {
				return Knapsack.of(200, r);
			});

		final EvolutionParamCodec<BitGene, Double> codec =
			EvolutionParamCodec.<BitGene, Double>of(
				SelectorCodec.numeric(),
				AltererCodec.<BitGene, Double>ofMutator()
					.append(AltererCodec.ofMultiPointCrossover(IntRange.of(2, 20)))
					.append(AltererCodec.ofSwapMutator())
			);

		final EvolutionParamOptimizer<BitGene, Double> optimizer =
			EvolutionParamOptimizer.of(codec, () -> bySteadyFitness(250));

		final EvolutionParam<BitGene, Double> params = optimizer
			.optimize(
				problem,
				Optimize.MAXIMUM,
				() -> byExecutionTime(ofMillis(150)));

		/*
		final RealFunction problem = new RealFunction();

		final EvolutionParamCodec<DoubleGene, Double> codec =
			EvolutionParamCodec.<DoubleGene, Double>of(
				SelectorCodec.numeric(),
				AltererCodec.numericMean()
			);

		final EvolutionParamOptimizer<DoubleGene, Double> optimizer =
			new EvolutionParamOptimizer<>(codec, () -> bySteadyFitness(250));

		final EvolutionParam<DoubleGene, Double> params = optimizer
			.optimize(problem, () -> byExecutionTime(ofMillis(150)));
		*/

		System.out.println();
		System.out.println("Best parameters:");
		System.out.println(params);
	}

}
