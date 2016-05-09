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

import static java.lang.Math.pow;
import static org.jenetics.internal.math.random.indexes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.jenetics.internal.util.IntRef;

import org.jenetics.AbstractAlterer;
import org.jenetics.Chromosome;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

public class DynamicGenotype {

	// Explicit Genotype factory instead of Genotype templates.
	private static final Factory<Genotype<DoubleGene>> ENCODING = () -> {
		final Random random = RandomRegistry.getRandom();
		return Genotype.of(
			// Vary the chromosome count between 10 and 20.
			IntStream.range(0, random.nextInt(10) + 10)
				// Vary the chromosome length between 10 and 20.
				.mapToObj(i -> DoubleChromosome.of(0, 10, random.nextInt(10) + 10))
				.collect(ISeq.toISeq())
		);
	};

	private static double fitness(final Genotype<DoubleGene> gt) {
		// Calculate fitness from "dynamic" Genotype.
		System.out.println("Gene count: " + gt.getNumberOfGenes());
		return 0;
	}

	// The special mutator also variates the chromosome/genotype length.
	private static final class DynamicMutator<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
		extends AbstractAlterer<G, C>
	{
		public DynamicMutator(double probability) {
			super(probability);
		}

		@Override
		public int alter(
			final Population<G, C> population,
			final long generation
		) {
			final double p = pow(_probability, 1.0/3.0);
			final IntRef alterations = new IntRef(0);

			indexes(RandomRegistry.getRandom(), population.size(), p).forEach(i -> {
				final Phenotype<G, C> pt = population.get(i);

				final Genotype<G> gt = pt.getGenotype();
				final Genotype<G> mgt = mutate(gt, p, alterations);

				final Phenotype<G, C> mpt = pt.newInstance(mgt, generation);
				population.set(i, mpt);
			});

			return alterations.value;
		}

		private Genotype<G> mutate(
			final Genotype<G> genotype,
			final double p,
			final IntRef alterations
		) {
			final List<Chromosome<G>> chromosomes =
				new ArrayList<>(genotype.toSeq().asList());

			// Add/remove Chromosome to Genotype.
			final Random random = RandomRegistry.getRandom();
			final double rd = random.nextDouble();
			if (rd < 1/3.0) {
				chromosomes.remove(0);
			} else if (rd < 2/3.0) {
				chromosomes.add(chromosomes.get(0).newInstance());
			}

			alterations.value +=
				indexes(RandomRegistry.getRandom(), chromosomes.size(), p)
					.map(i -> mutate(chromosomes, i, p))
					.sum();

			return Genotype.of(chromosomes);
		}

		private int mutate(final List<Chromosome<G>> c, final int i, final double p) {
			final Chromosome<G> chromosome = c.get(i);
			final List<G> genes = new ArrayList<>(chromosome.toSeq().asList());

			final int mutations = mutate(genes, p);
			if (mutations > 0) {
				c.set(i, chromosome.newInstance(ISeq.of(genes)));
			}
			return mutations;
		}

		private int mutate(final List<G> genes, final double p) {
			final Random random = RandomRegistry.getRandom();

			// Add/remove Gene from chromosome.
			final double rd = random.nextDouble();
			if (rd < 1/3.0) {
				genes.remove(0);
			} else if (rd < 2/3.0) {
				genes.add(genes.get(0).newInstance());
			}

			return (int)indexes(random, genes.size(), p)
				.peek(i -> genes.set(i, genes.get(i).newInstance()))
				.count();
		}

	}

	public static void main(final String[] args) {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(DynamicGenotype::fitness, ENCODING)
			.alterers(new DynamicMutator<>(0.25))
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(20)
			.collect(EvolutionResult.toBestEvolutionResult());

		System.out.println(result.getBestFitness());
	}

}
