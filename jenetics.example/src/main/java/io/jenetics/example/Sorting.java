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

import java.util.Random;
import java.util.stream.IntStream;

import io.jenetics.Chromosome;
import io.jenetics.EnumGene;
import io.jenetics.Genotype;
import io.jenetics.Optimize;
import io.jenetics.PartiallyMatchedCrossover;
import io.jenetics.PermutationChromosome;
import io.jenetics.SwapMutator;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Limits;
import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.util.RandomRegistry;

public class Sorting {

	private static int dist(Chromosome<EnumGene<Integer>> path, int i, int j) {
		return (path.getGene(i).getAllele() - path.getGene(j).getAllele())*
			(path.getGene(i).getAllele() - path.getGene(j).getAllele());
	}

	private static int length(final Genotype<EnumGene<Integer>> genotype) {
		return IntStream.range(1, genotype.getChromosome().length())
			.map(i -> dist(genotype.getChromosome(), i, i - 1))
			.sum();
	}

	public static void main(final String[] args) {
		RandomRegistry.setRandom(new Random());
		final Engine<EnumGene<Integer>, Integer> engine = Engine
			.builder(
				Sorting::length,
				PermutationChromosome.ofInteger(20))
			.optimize(Optimize.MINIMUM)
			.populationSize(1000)
			//.survivorsSelector(new RouletteWheelSelector<>())
			//.offspringSelector(new TruncationSelector<>())
			.offspringFraction(0.9)
			.alterers(
				new SwapMutator<>(0.01),
				new PartiallyMatchedCrossover<>(0.3))
			.build();


		final EvolutionStatistics<Integer, DoubleMomentStatistics> statistics =
			EvolutionStatistics.ofNumber();

		final EvolutionResult<EnumGene<Integer>, Integer> result = engine.stream()
			.limit(Limits.bySteadyFitness(100))
			.limit(2500)
			.peek(statistics)
			.collect(EvolutionResult.toBestEvolutionResult());

		System.out.println(statistics);
		System.out.println(result.getBestPhenotype());
	}

}
