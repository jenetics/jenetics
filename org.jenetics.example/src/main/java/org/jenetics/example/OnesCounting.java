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

import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.Phenotype;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.engine.Engine;

public class OnesCounting {

	private static Integer count(final Genotype<BitGene> genotype) {
		return ((BitChromosome)genotype.getChromosome()).bitCount();
	}

	public static void main(String[] args) {
		final Engine<BitGene, Integer> engine = Engine
			.newBuilder(OnesCounting::count, BitChromosome.of(20, 0.15))
			.populationSize(500)
			.selector(new RouletteWheelSelector<>())
			.alterers(
				new Mutator<>(0.55),
				new SinglePointCrossover<>(0.06))
			.build();

		final Phenotype<BitGene, Integer> result = engine.stream().limit(100)
			.collect(engine.BestPhenotype);

		System.out.println(result);
	}
}
