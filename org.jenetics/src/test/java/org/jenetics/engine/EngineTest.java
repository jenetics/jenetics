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

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import org.jenetics.IntegerChromosome;
import org.jenetics.IntegerGene;
import org.jenetics.Mutator;
import org.jenetics.Optimize;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.util.DoubleRange;
import org.jenetics.util.IO;
import org.jenetics.util.ISeq;
import org.jenetics.util.IntRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class EngineTest {

	@Test
	public void streamWithInitialGenotypes() {
		final Problem<Integer, IntegerGene, Integer> problem = Problem.of(
			a -> a,
			Codec.of(
				Genotype.of(IntegerChromosome.of(0, 1000)),
				g -> g.getGene().getAllele()
			)
		);

		final int genotypeCount = 10;
		final int max = 1000;
		final ISeq<Genotype<IntegerGene>> genotypes = IntRange.of(1, genotypeCount)
			.stream()
			.mapToObj(i -> IntegerChromosome.of(IntegerGene.of(max, 0, max)))
			.map(Genotype::of)
			.collect(ISeq.toISeq());

		final Engine<IntegerGene, Integer> engine = Engine.builder(problem)
			.build();

		final EvolutionResult<IntegerGene, Integer> result = engine.stream(genotypes)
			.limit(1)
			.collect(EvolutionResult.toBestEvolutionResult());

		final long maxCount = result.getPopulation().stream()
			.filter(pt -> pt.getFitness() == max)
			.count();

		Assert.assertTrue(maxCount >= genotypeCount);
	}

	@Test
	public void streamWithSerializedPopulation() throws IOException {
		// Problem definition.
		final Problem<Double, DoubleGene, Double> problem = Problem.of(
			x -> cos(0.5 + sin(x))*cos(x),
			codecs.ofScalar(DoubleRange.of(0.0, 2.0*PI))
		);

		// Define the GA engine.
		final Engine<DoubleGene, Double> engine = Engine.builder(problem)
			.optimize(Optimize.MINIMUM)
			.offspringSelector(new RouletteWheelSelector<>())
			.build();

		final EvolutionResult<DoubleGene, Double> interimResult = engine.stream()
			.limit(limit.bySteadyFitness(10))
			.collect(EvolutionResult.toBestEvolutionResult());

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		IO.object.write(interimResult, out);

		final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		@SuppressWarnings("unchecked")
		final EvolutionResult<DoubleGene, Double> loadedResult =
			(EvolutionResult<DoubleGene, Double>)IO.object.read(in);

		final EvolutionResult<DoubleGene, Double> result = engine
			.stream(loadedResult)
			.limit(10)
			.collect(EvolutionResult.toBestEvolutionResult());
	}

	@Test
	public void initialResult() {
		// Problem definition.
		final Problem<Double, DoubleGene, Double> problem = Problem.of(
			x -> cos(0.5 + sin(x))*cos(x),
			codecs.ofScalar(DoubleRange.of(0.0, 2.0*PI))
		);

		// Define the GA engine.
		final Engine<DoubleGene, Double> engine = Engine.builder(problem)
			.optimize(Optimize.MINIMUM)
			.offspringSelector(new RouletteWheelSelector<>())
			.build();

		final EvolutionResult<DoubleGene, Double> interimResult = engine.stream()
			.limit(limit.bySteadyFitness(10))
			.collect(EvolutionResult.toBestEvolutionResult());

		engine.builder()
			.alterers(new Mutator<>()).build()
			.stream(interimResult);
	}

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
