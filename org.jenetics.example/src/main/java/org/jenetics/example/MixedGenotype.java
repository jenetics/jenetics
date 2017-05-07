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

import java.util.function.Function;

import org.jenetics.BitChromosome;
import org.jenetics.Chromosome;
import org.jenetics.DoubleChromosome;
import org.jenetics.Genotype;
import org.jenetics.IntegerChromosome;
import org.jenetics.Phenotype;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MixedGenotype {

	private static final Genotype ENCODING = Genotype.of(
		(Chromosome)DoubleChromosome.of(0, 4, 5),
		(Chromosome)BitChromosome.of(70),
		(Chromosome)IntegerChromosome.of(0, 10, 3)
	);

	private static double fitness(final Genotype gt) {
		final DoubleChromosome dc = (DoubleChromosome)gt.getChromosome(0);
		final BitChromosome bc = (BitChromosome)gt.getChromosome(1);
		final IntegerChromosome ic = (IntegerChromosome)gt.getChromosome(2);

		return dc.doubleValue() + bc.bitCount() + ic.doubleValue();
	}

	public static void main(final String[] args) {
		final Engine engine = Engine
			.builder((Function<Genotype, Double>)MixedGenotype::fitness, ENCODING)
			.build();

		final Phenotype best = (Phenotype)engine.stream()
			.limit(10)
			.collect(EvolutionResult.toBestPhenotype());

		System.out.println(best);
	}

}
