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

import io.jenetics.BitChromosome;
import io.jenetics.Chromosome;
import io.jenetics.DoubleChromosome;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;

/**
 * This example shows how to create a heterogeneous {@link Genotype} with
 * different types of chromosomes. It is not possible to create such genotypes
 * in a typesafe manner. The only possibility is to use rawtypes and suppress
 * the warnings. You therefore have to cast to the correct chromosome type when
 * using the raw genotype in the fitness function.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.6
 * @since 3.6
 */
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
			.builder(MixedGenotype::fitness, ENCODING)
			.build();

		final Phenotype best = (Phenotype)engine.stream()
			.limit(10)
			.collect(EvolutionResult.toBestPhenotype());

		System.out.println(best);
	}

}
