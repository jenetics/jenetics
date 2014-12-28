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
package org.jenetics.example;

import java.util.stream.IntStream;

import org.jenetics.Chromosome;
import org.jenetics.EnumGene;
import org.jenetics.Genotype;
import org.jenetics.Optimize;
import org.jenetics.PartiallyMatchedCrossover;
import org.jenetics.PermutationChromosome;
import org.jenetics.SwapMutator;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.EvolutionStatistics;
import org.jenetics.engine.limit;
import org.jenetics.stat.DoubleMomentStatistics;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;

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
		RandomRegistry.setRandom(new LCG64ShiftRandom.ThreadLocal());
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
			.limit(limit.bySteadyFitness(100))
			.limit(2500)
			.peek(statistics)
			.collect(EvolutionResult.toBestEvolutionResult());

		System.out.println(statistics);
		System.out.println(result.getBestPhenotype());
	}

}
