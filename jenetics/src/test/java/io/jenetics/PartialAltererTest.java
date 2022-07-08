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
package io.jenetics;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.util.concurrent.CompletionException;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.PartialAlterer.Projection;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Problem;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class PartialAltererTest {

	@Test
	public void split() {
		final Phenotype<DoubleGene, Double> pt = Phenotype.of(
			Genotype.of(
				DoubleChromosome.of(0, 1),
				DoubleChromosome.of(1, 2),
				DoubleChromosome.of(2, 3),
				DoubleChromosome.of(3, 4),
				DoubleChromosome.of(4, 5),
				DoubleChromosome.of(5, 6),
				DoubleChromosome.of(6, 7),
				DoubleChromosome.of(7, 8),
				DoubleChromosome.of(8, 9),
				DoubleChromosome.of(9, 10)
			),
			0
		);

		final var projection = new PartialAlterer.Projection(new int[]{1, 3, 5});

		final Phenotype<DoubleGene, Double> split = projection.project(pt);
		Assert.assertEquals(split.genotype().length(), 3);
		Assert.assertEquals(split.genotype().get(0), pt.genotype().get(1));
		Assert.assertEquals(split.genotype().get(1), pt.genotype().get(3));
		Assert.assertEquals(split.genotype().get(2), pt.genotype().get(5));
	}

	@Test
	public void merge() {
		final Genotype<DoubleGene> gt = Genotype.of(
				DoubleChromosome.of(0, 1),
				DoubleChromosome.of(1, 2),
				DoubleChromosome.of(2, 3),
				DoubleChromosome.of(3, 4)
			);

		final ISeq<Phenotype<DoubleGene, Double>> population = gt.instances()
			.limit(3)
			.map(g -> Phenotype.<DoubleGene, Double>of(g, 0))
			.collect(ISeq.toISeq());

		final var projection = new PartialAlterer.Projection(new int[]{1, 3});

		final Seq<Phenotype<DoubleGene, Double>> split = projection.project(population);
		Assert.assertEquals(split.length(), population.length());
		for (int i = 0; i < population.length(); ++i) {
			Assert.assertEquals(split.get(i).genotype().length(), 2);
		}

		final Seq<Phenotype<DoubleGene, Double>> merged = projection.merge(split, population);
		Assert.assertEquals(merged, population);
	}

	@Test
	public void constAlterer() {
		final Genotype<DoubleGene> gt = Genotype.of(
			DoubleChromosome.of(0, 1),
			DoubleChromosome.of(1, 2),
			DoubleChromosome.of(2, 3),
			DoubleChromosome.of(3, 4)
		);

		final ISeq<Phenotype<DoubleGene, Double>> population = gt.instances()
			.limit(3)
			.map(g -> Phenotype.of(g, 0, PI))
			.collect(ISeq.toISeq());

		final Alterer<DoubleGene, Double> alterer = PartialAlterer.of(
			new ConstAlterer<DoubleGene, Double>(0.5),
			1, 2
		);

		final AltererResult<DoubleGene, Double> result =
			alterer.alter(population, 10);

		for (int i = 0; i < population.length(); ++i) {
			final Phenotype<DoubleGene, Double> pt1 = population.get(i);
			final Phenotype<DoubleGene, Double> pt2 = result.population().get(i);

			Assert.assertEquals(pt1.genotype().get(0), pt2.genotype().get(0));
			Assert.assertNotEquals(pt1.genotype().get(1), pt2.genotype().get(1));
			Assert.assertNotEquals(pt1.genotype().get(2), pt2.genotype().get(2));
			Assert.assertEquals(pt1.genotype().get(3), pt2.genotype().get(3));

			Assert.assertEquals(pt2.genotype().get(1).gene().doubleValue(), 0.5);
			Assert.assertEquals(pt2.genotype().get(2).gene().doubleValue(), 0.5);

			Assert.assertTrue(pt1.isEvaluated());
			Assert.assertFalse(pt2.isEvaluated());
		}
	}

	@Test
	public void noneAlterer() {
		final Genotype<DoubleGene> gt = Genotype.of(
			DoubleChromosome.of(0, 1),
			DoubleChromosome.of(1, 2),
			DoubleChromosome.of(2, 3),
			DoubleChromosome.of(3, 4)
		);

		final ISeq<Phenotype<DoubleGene, Double>> population = gt.instances()
			.limit(3)
			.map(g -> Phenotype.of(g, 0, PI))
			.collect(ISeq.toISeq());

		final Alterer<DoubleGene, Double> alterer = PartialAlterer.of(
			(pop, gen) -> new AltererResult<>(pop.asISeq()),
			1, 2
		);

		final AltererResult<DoubleGene, Double> result =
			alterer.alter(population, 10);

		for (int i = 0; i < population.length(); ++i) {
			final Phenotype<DoubleGene, Double> pt1 = population.get(i);
			final Phenotype<DoubleGene, Double> pt2 = result.population().get(i);

			Assert.assertSame(pt1, pt2);
		}
	}

	private static final class ConstAlterer<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
		implements Alterer<G, C>
	{
		private final C _const;

		ConstAlterer(final C value) {
			_const = value;
		}

		@Override
		public AltererResult<G, C> alter(
			final Seq<Phenotype<G, C>> population,
			final long generation
		) {
			final ISeq<Phenotype<G, C>> pop = population.map(this::mapPt).asISeq();
			return new AltererResult<>(pop, pop.length());
		}

		private Phenotype<G, C> mapPt(final Phenotype<G, C> phenotype) {
			return Phenotype.of(mapGt(phenotype.genotype()), phenotype.generation());
		}

		private Genotype<G> mapGt(final Genotype<G> genotype) {
			return Genotype.of(
				genotype.stream()
					.map(this::mapCh)
					.collect(ISeq.toISeq())
			);
		}

		@SuppressWarnings({"unchecked", "rawtypes"})
		private Chromosome<G> mapCh(final Chromosome chromosome) {
			return chromosome.newInstance(
				ISeq.of(chromosome).map(g -> mapGene((G)g))
			);
		}

		@SuppressWarnings("unchecked")
		private G mapGene(final G gene) {
			return (G)((Gene)gene).newInstance(_const);
		}

	}

	@Test(expectedExceptions = CompletionException.class)
	public void withEngineOutOfBounds() {
		// Problem definition.
		final Problem<Double, DoubleGene, Double> problem = Problem.of(
			x -> cos(0.5 + sin(x))*cos(x),
			Codecs.ofScalar(DoubleRange.of(0.0, 2.0*PI))
		);

		final Genotype<DoubleGene> gtf = Genotype.of(
			DoubleChromosome.of(0, 1),
			DoubleChromosome.of(1, 2),
			DoubleChromosome.of(2, 3),
			DoubleChromosome.of(3, 4)
		);

		// Define the GA engine.
		final Engine<DoubleGene, Double> engine = Engine
			.builder(gt -> gt.gene().doubleValue(), gtf)
			.selector(new RouletteWheelSelector<>())
			.alterers(
				PartialAlterer.of(new Mutator<DoubleGene, Double>(), 0, 2),
				PartialAlterer.of(new MeanAlterer<DoubleGene, Double>(), 3, 4),
				new GaussianMutator<>()
			)
			.build();

		engine.stream()
			.limit(100)
			.collect(EvolutionResult.toBestEvolutionResult());
	}

}
