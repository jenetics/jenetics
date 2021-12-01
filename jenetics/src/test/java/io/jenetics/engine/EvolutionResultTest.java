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
package io.jenetics.engine;

import static io.jenetics.engine.EvolutionResult.toBestEvolutionResult;

import java.io.Serializable;
import java.time.Duration;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.stat.MinMax;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.ObjectTester;
import io.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class EvolutionResultTest
	extends ObjectTester<EvolutionResult<DoubleGene, Double>>
{

	@Override
	protected Factory<EvolutionResult<DoubleGene, Double>> factory() {
		final Function<Genotype<DoubleGene>, Double> ff =
			(Function<Genotype<DoubleGene>, Double> & Serializable)
				a -> a.gene().allele();

		return () -> {
			final var random = RandomRegistry.random();
			final Genotype<DoubleGene> gtf = Genotype.of(DoubleChromosome.of(0, 1));

			return EvolutionResult.of(
				random.nextBoolean() ? Optimize.MAXIMUM : Optimize.MINIMUM,
				IntStream.range(0, 100)
					.mapToObj(i -> {
						final Genotype<DoubleGene> gt = gtf.newInstance();
						return Phenotype.of(gt, 1, gt.gene().doubleValue());
					})
					.collect(ISeq.toISeq()),
				random.nextInt(1000),
				random.nextInt(1000),
				new EvolutionDurations(
					Duration.ofMillis(random.nextInt(1_000_000)),
					Duration.ofMillis(random.nextInt(1_000_000)),
					Duration.ofMillis(random.nextInt(1_000_000)),
					Duration.ofMillis(random.nextInt(1_000_000)),
					Duration.ofMillis(random.nextInt(1_000_000)),
					Duration.ofMillis(random.nextInt(1_000_000)),
					Duration.ofMillis(random.nextInt(1_000_000))
				),
				random.nextInt(100),
				random.nextInt(100),
				random.nextInt(100)
			);
		};
	}

	// https://github.com/jenetics/jenetics/issues/146
	@Test
	public void emptyStreamCollectEvolutionResult() {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(a -> a.gene().allele(), DoubleChromosome.of(0, 1))
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(0)
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertNull(result);
	}

	// https://github.com/jenetics/jenetics/issues/146
	@Test
	public void emptyStreamCollectPhenotype() {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(a -> a.gene().allele(), DoubleChromosome.of(0, 1))
			.build();

		final Phenotype<DoubleGene, Double> result = engine.stream()
			.limit(0)
			.collect(EvolutionResult.toBestPhenotype());

		Assert.assertNull(result);
	}

	// https://github.com/jenetics/jenetics/issues/146
	@Test
	public void emptyStreamCollectGenotype() {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(a -> a.gene().allele(), DoubleChromosome.of(0, 1))
			.build();

		final Genotype<DoubleGene> result = engine.stream()
			.limit(0)
			.collect(EvolutionResult.toBestGenotype());

		Assert.assertNull(result);
	}

	@Test
	public void bestWorstPhenotype() {
		final int length = 100;

		final MSeq<Phenotype<IntegerGene, Integer>> population = MSeq.ofLength(length);
		for (int i = 0; i < length; ++i) {
			final Genotype<IntegerGene> gt = Genotype.of(IntegerChromosome.of(
				IntegerGene.of(i, 0, length)
			));
			population.set(i, Phenotype.of(gt, 1, i));
		}
		population.shuffle(RandomRegistry.random());

		final EvolutionResult<IntegerGene, Integer> maxResult = EvolutionResult.of(
			Optimize.MAXIMUM, population.toISeq(),
			0, 0, EvolutionDurations.ZERO, 0, 0, 0
		);

		Assert.assertEquals(maxResult.bestFitness().intValue(), length - 1);
		Assert.assertEquals(maxResult.worstFitness().intValue(), 0);

		final EvolutionResult<IntegerGene, Integer> minResult = EvolutionResult.of(
				Optimize.MINIMUM, population.toISeq(),
				0, 0, EvolutionDurations.ZERO, 0, 0, 0
			);

		Assert.assertEquals(minResult.bestFitness().intValue(), 0);
		Assert.assertEquals(minResult.worstFitness().intValue(), length - 1);
	}

	@Test
	public void compareTo() {
		final int length = 100;
		final Function<Genotype<IntegerGene>, Integer> ff = gt -> gt.gene().allele();

		final MSeq<Phenotype<IntegerGene, Integer>> small = MSeq.ofLength(length);
		for (int i = 0; i < length; ++i) {
			final Genotype<IntegerGene> gt = Genotype.of(IntegerChromosome.of(
				IntegerGene.of(i, 0, length)
			));
			small.set(i, Phenotype.of(gt, 1, gt.gene().intValue()));
		}
		small.shuffle(RandomRegistry.random());

		final MSeq<Phenotype<IntegerGene, Integer>> big = MSeq.ofLength(length);
		for (int i = 0; i < length; ++i) {
			final Genotype<IntegerGene> gt = Genotype.of(IntegerChromosome.of(
				IntegerGene.of(i + length, 0, length)
			));
			big.set(i, Phenotype.of(gt, 1, gt.gene().intValue()));
		}
		big.shuffle(RandomRegistry.random());


		final EvolutionResult<IntegerGene, Integer> smallMaxResult = EvolutionResult.of(
			Optimize.MAXIMUM, small.toISeq(),
			0, 0, EvolutionDurations.ZERO, 0, 0, 0
		);
		final EvolutionResult<IntegerGene, Integer> bigMaxResult = EvolutionResult.of(
			Optimize.MAXIMUM, big.toISeq(),
			0, 0, EvolutionDurations.ZERO, 0, 0, 0
		);

		Assert.assertTrue(smallMaxResult.compareTo(bigMaxResult) < 0);
		Assert.assertTrue(bigMaxResult.compareTo(smallMaxResult) > 0);
		Assert.assertTrue(smallMaxResult.compareTo(smallMaxResult) == 0);
		Assert.assertTrue(bigMaxResult.compareTo(bigMaxResult) == 0);


		final EvolutionResult<IntegerGene, Integer> smallMinResult = EvolutionResult.of(
			Optimize.MINIMUM, small.toISeq(),
			0, 0, EvolutionDurations.ZERO, 0, 0, 0
		);
		final EvolutionResult<IntegerGene, Integer> bigMinResult = EvolutionResult.of(
			Optimize.MINIMUM, big.toISeq(),
			0, 0, EvolutionDurations.ZERO, 0, 0, 0
		);

		Assert.assertTrue(smallMinResult.compareTo(bigMinResult) > 0);
		Assert.assertTrue(bigMinResult.compareTo(smallMinResult) < 0);
		Assert.assertTrue(smallMinResult.compareTo(smallMinResult) == 0);
		Assert.assertTrue(bigMinResult.compareTo(bigMinResult) == 0);
	}

	@Test
	public void bestCollector() {
		final int bestMaxValue = IntStream.range(0, 100)
			.mapToObj(value -> newResult(Optimize.MAXIMUM, value))
			.collect(toBestEvolutionResult())
			.bestFitness();

		Assert.assertEquals(bestMaxValue, 99);

		final int bestMinValue = IntStream.range(0, 100)
			.mapToObj(value -> newResult(Optimize.MINIMUM, value))
			.collect(EvolutionResult.toBestGenotype())
			.gene().allele();

		Assert.assertEquals(bestMinValue, 0);
	}

	private static EvolutionResult<IntegerGene, Integer> newResult(
		final Optimize opt,
		final int value
	) {
		final int length = 1000;
		final Function<Genotype<IntegerGene>, Integer> ff = gt -> gt.gene().allele();

		final MSeq<Phenotype<IntegerGene, Integer>> pop = MSeq.ofLength(length);
		for (int i = 0; i < length; ++i) {
			final Genotype<IntegerGene> gt = Genotype.of(IntegerChromosome.of(
				IntegerGene.of(value, 0, length)
			));
			pop.set(i, Phenotype.of(gt, 1, value));
		}
		pop.shuffle(RandomRegistry.random());


		return EvolutionResult
			.of(opt, pop.toISeq(), 0, 0, EvolutionDurations.ZERO, 0, 0, 0);
	}

	@Test
	public void toUniquePopulation() {
		final ISeq<Genotype<IntegerGene>> genotypes =
			Genotype.of(IntegerChromosome.of(0, Integer.MAX_VALUE)).instances()
				.limit(10)
				.collect(ISeq.toISeq());

		final EvolutionResult<IntegerGene, Integer> result = result(genotypes);
		Assert.assertSame(
			EvolutionResult.<IntegerGene, Integer>toUniquePopulation().after(result),
			result
		);
	}

	private EvolutionResult<IntegerGene, Integer>
	result(final ISeq<Genotype<IntegerGene>> genotypes) {
		return EvolutionResult.of(
			Optimize.MAXIMUM,
			genotypes.map(gt -> Phenotype.of(gt, 1)),
			8,
			2,
			EvolutionDurations.ZERO,
			3,
			4,
			5
		);
	}

	@Test
	public void toUniquePopulation2() {
		final ISeq<Genotype<IntegerGene>> genotypes =
			Genotype.of(IntegerChromosome.of(0, 10)).instances()
				.limit(100)
				.collect(ISeq.toISeq());

		final var unifier = EvolutionResult.<IntegerGene, Integer>toUniquePopulation(
				Genotype.of(IntegerChromosome.of(0, Integer.MAX_VALUE))
		);

		final EvolutionResult<IntegerGene, Integer> result = result(genotypes);
		final EvolutionResult<IntegerGene, Integer> unified = unifier.after(result);

		Assert.assertNotEquals(unified, result);
		Assert.assertEquals(
			unified.genotypes().stream().collect(Collectors.toSet()).size(),
			unified.population().size()
		);
		Assert.assertEquals(
			result.population().size(),
			unified.population().size()
		);
	}

	@Test
	public void toUniquePopulation3() {
		final ISeq<Genotype<IntegerGene>> genotypes =
			Genotype.of(IntegerChromosome.of(0, 10)).instances()
				.limit(100)
				.collect(ISeq.toISeq());

		final EvolutionInterceptor<IntegerGene, Integer> unifier =
			EvolutionResult.toUniquePopulation(
				Genotype.of(IntegerChromosome.of(0, 10))
			);

		final EvolutionResult<IntegerGene, Integer> result = result(genotypes);
		final EvolutionResult<IntegerGene, Integer> unified = unifier.after(result);

		Assert.assertNotEquals(unified, result);
		Assert.assertTrue(
			unified.genotypes().stream().collect(Collectors.toSet()).size() <
			unified.population().size()
		);
		Assert.assertEquals(
			result.population().size(),
			unified.population().size()
		);
	}

	@Test
	public void toStrictlyImprovingResults() {
		final ISeq<Integer> results = new Random()
			.ints(100, 0, 100)
			.mapToObj(value -> newResult(Optimize.MAXIMUM, value))
			.flatMap(MinMax.toStrictlyIncreasing())
			.map(EvolutionResult::bestFitness)
			.collect(ISeq.toISeq());

		Assert.assertTrue(results.isSorted());
	}

}
