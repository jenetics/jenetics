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
package org.jenetics.engine;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.LongStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class EngineTest {

	@Test(dataProvider = "generations")
	public void generationCount(final Long generations) {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(a -> a.getGene().getAllele(), DoubleChromosome.of(0, 1))
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(generations)
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(generations.longValue(), result.getTotalGenerations());
	}

	@Test(dataProvider = "generations")
	public void generationLimit(final Long generations) {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(a -> a.getGene().getAllele(), DoubleChromosome.of(0, 1))
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(limit.byFixedGeneration(generations))
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(generations.longValue(), result.getTotalGenerations());
	}

	@DataProvider(name = "generations")
	public Object[][] generations() {
		return LongStream.rangeClosed(1, 10)
			.mapToObj(i -> new Object[]{i})
			.toArray(Object[][]::new);
	}

	@Test
	public void phenotypeValidator() {
		final int populationSize = 100;

		final Engine<DoubleGene, Double> engine = Engine
			.builder(a -> a.getGene().getAllele(), DoubleChromosome.of(0, 1))
			.phenotypeValidator(pt -> false)
			.populationSize(populationSize)
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(10)
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(result.getInvalidCount(), populationSize);
	}

	@Test
	public void genotypeValidator() {
		final int populationSize = 100;

		final Engine<DoubleGene, Double> engine = Engine
			.builder(a -> a.getGene().getAllele(), DoubleChromosome.of(0, 1))
			.genotypeValidator(pt -> false)
			.populationSize(populationSize)
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(10)
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(result.getInvalidCount(), populationSize);
	}

	// https://github.com/jenetics/jenetics/issues/47
	@Test(timeOut = 15_000L)
	public void deadLock() {
		final Function<Genotype<DoubleGene>, Double> ff = gt -> {
			try {
				Thread.sleep( 50 );
			} catch (InterruptedException ignore) {
				Thread.currentThread().interrupt();
			}
			return gt.getGene().getAllele();
		};

		final Engine<DoubleGene, Double> engine = Engine
			.builder(ff, DoubleChromosome.of(0, 1))
			//.executor(Executors.newFixedThreadPool(10))
			.populationSize(10)
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(3)
			.collect(EvolutionResult.toBestEvolutionResult());

		//Assert.assertEquals(25L, result.getTotalGenerations());
	}

	// https://github.com/jenetics/jenetics/issues/111
	@Test(dataProvider = "executors", timeOut = 2_000L)
	public void executorDeadLock(final Executor executor) {
		try {
			final Engine<DoubleGene, Double> engine = Engine
				.builder(gt -> gt.getGene().doubleValue(), DoubleChromosome.of(0, 1))
				.executor(executor)
				.populationSize(10)
				.build();

			engine.stream()
				.limit(100)
				.collect(EvolutionResult.toBestEvolutionResult());
		} finally {
			if (executor instanceof ExecutorService) {
				((ExecutorService)executor).shutdown();
			}
		}
	}

	@DataProvider(name = "executors")
	public Object[][] executors() {
		return new Object[][] {
			{(Executor)Runnable::run},
			{Executors.newFixedThreadPool(1)},
			{Executors.newSingleThreadExecutor()},
			{Executors.newFixedThreadPool(10)},
			{new ForkJoinPool(1)},
			{new ForkJoinPool(10)}
		};
	}

}
