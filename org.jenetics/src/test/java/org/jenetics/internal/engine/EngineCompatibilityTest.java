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
package org.jenetics.internal.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import org.jenetics.internal.util.Concurrency;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Scoped;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-09-13 $</em>
 */
public class EngineCompatibilityTest {


	@Test
	public void test() {
		final List<Double> values = new ArrayList<>();
		try (Scoped<?> r = RandomRegistry.scope(new LCG64ShiftRandom(1))) {
			final GeneticAlgorithm<DoubleGene, Double> ga = new GeneticAlgorithm<>(
				Genotype.of(DoubleChromosome.of(0.0, 1_000.0)),
				gt -> gt.getGene().getAllele(),
				Concurrency.SERIAL_EXECUTOR
			);

			ga.setup();
			//values.add(ga.getStatistics().getBestPhenotype().getGenotype().getGene().getAllele());
			for (int i = 0; i < 15; ++i) {
				ga.evolve();
				values.add(ga.getStatistics().getBestPhenotype().getGenotype().getGene().getAllele());
			}
		}

		List<Double> newValues = new ArrayList<>();
		try (Scoped<?> r = RandomRegistry.scope(new LCG64ShiftRandom(1))) {
			final Engine<DoubleGene, Double> engine = Engine.newBuilder(
				gt -> gt.getGene().getAllele(),
				Genotype.of(DoubleChromosome.of(0.0, 1_000.0)))
			.executor(Concurrency.SERIAL_EXECUTOR)
			.survivorsSelector(new RouletteWheelSelector<>())
			.offspringSelector(new RouletteWheelSelector<>())
			.build();

			engine.stream().limit(1025)
				.map(re -> re.getBestPhenotype().getGenotype().getGene().getAllele())
				.forEach(System.out::println);
		}

		System.out.println(values);
		//System.out.println(newValues);
	}

}
