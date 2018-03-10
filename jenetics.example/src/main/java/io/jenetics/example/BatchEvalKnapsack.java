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
package io.jenetics.example;

import static io.jenetics.engine.EvolutionResult.toBestPhenotype;
import static io.jenetics.engine.Limits.bySteadyFitness;

import java.io.IOException;
import java.util.Random;
import java.util.function.Function;

import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.SinglePointCrossover;
import io.jenetics.engine.BatchEval;
import io.jenetics.engine.Engine;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class BatchEvalKnapsack {

	public static void main(String[] args) throws IOException {
		final Knapsack knapsack = Knapsack.of(15, new Random(123));

		final Function<Seq<Genotype<BitGene>>, Seq<Double>> batchFunction =
			batch -> batch
				.map(knapsack.codec().decoder())
				.map(knapsack.fitness());

		final BatchEval<BitGene, Double> eval = new BatchEval<>(batchFunction);

		final Engine<BitGene, Double> engine = Engine
			.builder(eval, knapsack.codec().encoding())
			.populationSize(500)
			.alterers(
				new Mutator<>(0.115),
				new SinglePointCrossover<>(0.16))
			.evaluator(eval)
			.build();

		final Phenotype<BitGene, Double> best = engine.stream()
			.limit(bySteadyFitness(20))
			.collect(toBestPhenotype());

		System.out.println(best);
	}

}
