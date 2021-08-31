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

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.String.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.Alterer;
import io.jenetics.BoltzmannSelector;
import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.LongChromosome;
import io.jenetics.Mutator;
import io.jenetics.Optimize;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.Selector;
import io.jenetics.SwapMutator;
import io.jenetics.TournamentSelector;
import io.jenetics.TruncationSelector;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.IO;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class EngineTest {


	@Test(dataProvider = "minimalEvaluationLimits")
	public void minimalEvaluation(final int limit) {
		final AtomicInteger count = new AtomicInteger();
		final Evaluator<IntegerGene, Integer> evaluator = population -> {
			count.incrementAndGet();
			return population
				.map(pt -> pt.withFitness(1))
				.asISeq();
		};
		final Codec<Integer, IntegerGene> codec = Codecs.ofScalar(IntRange.of(1, 100));

		final Engine<IntegerGene, Integer> engine =
			new Engine.Builder<>(evaluator, codec.encoding())
				.alterers(new Mutator<>(1.0))
				.build();

		final EvolutionResult<IntegerGene, Integer> result = engine.stream()
			.limit(limit)
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(count.get(), limit == 0 ? 0 : limit +1);
	}

	@DataProvider
	public Object[][] minimalEvaluationLimits() {
		return new Object[][] {{0}, {1}, {2}, {3}, {5}, {11}, {20}, {50}, {100}};
	}

	@Test
	public void streamWithInitialGenotypes() {
		final Problem<Integer, IntegerGene, Integer> problem = Problem.of(
			a -> a,
			Codec.of(
				Genotype.of(IntegerChromosome.of(0, 1000)),
				g -> g.gene().allele() + 1
			)
		);

		final int genotypeCount = 10;
		final int max = 1000;
		final ISeq<Genotype<IntegerGene>> genotypes = IntRange.of(1, genotypeCount)
			.stream()
			.mapToObj(i -> IntegerChromosome.of(IntegerGene.of(max - 1, 0, max)))
			.map(Genotype::of)
			.collect(ISeq.toISeq());

		final Engine<IntegerGene, Integer> engine = Engine.builder(problem)
			.build();

		final EvolutionResult<IntegerGene, Integer> result = engine.stream(genotypes)
			.limit(1)
			.collect(EvolutionResult.toBestEvolutionResult());

		final long maxCount = result.population().stream()
			.filter(pt -> pt.fitness() == max)
			.count();

		Assert.assertTrue(maxCount >= genotypeCount, "" + maxCount + " >= " + genotypeCount);
	}

	@Test
	public void streamWithSerializedPopulation() throws IOException {
		// Problem definition.
		final Problem<Double, DoubleGene, Double> problem = Problem.of(
			x -> cos(0.5 + sin(x))*cos(x),
			Codecs.ofScalar(DoubleRange.of(0.0, 2.0*PI))
		);

		// Define the GA engine.
		final Engine<DoubleGene, Double> engine = Engine.builder(problem)
			.optimize(Optimize.MINIMUM)
			.offspringSelector(new RouletteWheelSelector<>())
			.build();

		final EvolutionResult<DoubleGene, Double> interimResult = engine.stream()
			.limit(Limits.bySteadyFitness(10))
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
			Codecs.ofScalar(DoubleRange.of(0.0, 2.0*PI))
		);

		// Define the GA engine.
		final Engine<DoubleGene, Double> engine = Engine.builder(problem)
			.optimize(Optimize.MINIMUM)
			.offspringSelector(new RouletteWheelSelector<>())
			.build();

		final EvolutionResult<DoubleGene, Double> interimResult = engine.stream()
			.limit(Limits.bySteadyFitness(10))
			.collect(EvolutionResult.toBestEvolutionResult());

		//engine.toBuilder()
		//	.alterers(new Mutator<>()).build()
		//	.stream(interimResult);
	}

	@Test(dataProvider = "generations")
	public void generationCount(final Long generations) {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(a -> a.gene().allele(), DoubleChromosome.of(0, 1))
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(generations)
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(generations.longValue(), result.totalGenerations());
	}

	@Test(dataProvider = "generations")
	public void generationLimit(final Long generations) {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(a -> a.gene().allele(), DoubleChromosome.of(0, 1))
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(Limits.byFixedGeneration(generations))
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(generations.longValue(), result.totalGenerations());
	}

	@Test(dataProvider = "generations")
	public void engineGenerationLimit1(final Long generations) {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(a -> a.gene().allele(), DoubleChromosome.of(0, 1))
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine
			.limit(() -> Limits.byFixedGeneration(generations))
			.stream()
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(generations.longValue(), result.totalGenerations());
	}

	@Test(dataProvider = "generations")
	public void engineGenerationLimit2(final Long generations) {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(a -> a.gene().allele(), DoubleChromosome.of(0, 1))
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine
			.limit(() -> Limits.byFixedGeneration(generations))
			.limit(() -> Limits.byFixedGeneration(Math.min(generations, 5)))
			.stream()
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(Math.min(generations, 5), result.totalGenerations());
	}

	@DataProvider(name = "generations")
	public Object[][] generations() {
		return LongStream.rangeClosed(1, 10)
			.mapToObj(i -> new Object[]{i})
			.toArray(Object[][]::new);
	}

	@Test
	public void constraint() {
		final int populationSize = 100;

		final Engine<DoubleGene, Double> engine = Engine
			.builder(a -> a.gene().allele(), DoubleChromosome.of(0, 1))
			.constraint(RetryConstraint.of(pt -> false))
			.populationSize(populationSize)
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(10)
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(result.invalidCount(), populationSize);
	}

	@Test
	public void toUniquePopulation() {
		final int populationSize = 100;

		final Engine<IntegerGene, Integer> engine = Engine
			.builder(a -> a.gene().allele(), IntegerChromosome.of(0, 10))
			.populationSize(populationSize)
			.interceptor(EvolutionResult.toUniquePopulation(
				Genotype.of(IntegerChromosome.of(0, Integer.MAX_VALUE))))
			.build();

		final EvolutionResult<IntegerGene, Integer> result = engine.stream()
			.limit(10)
			.peek(r -> {
				if (r.genotypes().stream().collect(Collectors.toSet()).size() !=
					populationSize)
				{
					throw new AssertionError(format(
						"Expected unique population size %d, but got %d.",
						populationSize,
						r.genotypes().stream().collect(Collectors.toSet()).size()
					));
				}
			})
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(result.population().size(), populationSize);
	}

	@Test
	public void parallelStream() {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(a -> a.gene().allele(), DoubleChromosome.of(0, 1))
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine
			.stream()
			.limit(Limits.byFixedGeneration(1000))
			.parallel()
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertTrue(
			result.totalGenerations() >= 1000,
			"Total generation must be bigger than 1000: " +
			result.totalGenerations()
		);
	}

	@Test
	public void variableDoubleSum() {
		final Problem<int[], IntegerGene, Integer> problem = Problem.of(
			array -> IntStream.of(array).sum(),
			Codec.of(
				Genotype.of(IntegerChromosome.of(0, 100, IntRange.of(10, 100))),
				gt -> gt.chromosome().as(IntegerChromosome.class).toArray()
			)
		);
		final Engine<IntegerGene, Integer> engine = Engine.builder(problem)
			.alterers(
				new Mutator<>(),
				new SwapMutator<>())
			.selector(new TournamentSelector<>())
			.minimizing()
			.build();

		final int[] result = problem.codec().decode(
			engine.stream()
				.limit(100)
				.collect(EvolutionResult.toBestGenotype())
		);

		Assert.assertTrue(result.length < 50, "result length: " + result.length);
		//System.out.println(result.length);
		//System.out.println(Arrays.toString(result));
	}

	@Test(dataProvider = "engineParams")
	public <G extends Gene<?, G>> void variableLengthChromosomes(
		final Genotype<G> gtf,
		final Alterer<G, Double> alterer,
		final Selector<G, Double> selector
	) {
		final Random random = new Random(123);
		final Engine<G, Double> engine = Engine
			.builder(gt -> random.nextDouble(), gtf)
			.alterers(alterer)
			.selector(selector)
			.build();

		final EvolutionResult<G, Double> result = engine.stream()
			.limit(50)
			.collect(EvolutionResult.toBestEvolutionResult());
	}

	@DataProvider(name = "engineParams")
	public Object[][] engineParams() {
		return new Object[][] {
			{
				Genotype.of(DoubleChromosome.of(0, 1, IntRange.of(2, 10))),
				new Mutator<>(),
				new RouletteWheelSelector<>()
			},
			{
				Genotype.of(IntegerChromosome.of(0, 1, IntRange.of(2, 10))),
				new Mutator<>(),
				new RouletteWheelSelector<>()
			},
			{
				Genotype.of(LongChromosome.of(0, 1, IntRange.of(2, 10))),
				new Mutator<>(),
				new RouletteWheelSelector<>()
			},
			{
				Genotype.of(DoubleChromosome.of(0, 1, IntRange.of(2, 10))),
				new SwapMutator<>(),
				new TruncationSelector<>()
			},
			{
				Genotype.of(IntegerChromosome.of(0, 1, IntRange.of(2, 10))),
				new Mutator<>(),
				new RouletteWheelSelector<>()
			},
			{
				Genotype.of(LongChromosome.of(0, 1, IntRange.of(2, 10))),
				new Mutator<>(),
				new BoltzmannSelector<>()
			}
		};
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
			return gt.gene().allele();
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
				.builder(gt -> gt.gene().doubleValue(), DoubleChromosome.of(0, 1))
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

	/*
	@Test
	public void populationEvaluator() {
		final int populationSize = 100;
		final AtomicInteger count = new AtomicInteger();

		final Engine<DoubleGene, Double> engine = Engine
			.builder(gt -> gt.getGene().doubleValue(), DoubleChromosome.of(0, 1))
			.populationSize(populationSize)
			.evaluator((gt, ff) -> {
				count.compareAndSet(0, gt.length());
				return gt.stream().map(ff).collect(ISeq.toISeq());
			})
			.build();

		engine.stream()
			.limit(1)
			.collect(EvolutionResult.toBestGenotype());

		Assert.assertEquals(count.get(), populationSize);
	}
	*/

	// https://github.com/jenetics/jenetics/issues/234
	@Test
	public void constantPopulationForZeroSurvivors() {
		final int populationSize = 20;

		final Engine<DoubleGene, Double> engine = Engine
			.builder(gt -> gt.gene().doubleValue(), DoubleChromosome.of(0, 1))
			.populationSize(populationSize)
			.survivorsSize(0)
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(100)
			.peek(r -> {
				if (r.population().size() != populationSize) {
					throw new AssertionError(format(
						"Expected population size %d, but got %d.",
						populationSize, r.population().size()
					));
				}
			})
			.collect(EvolutionResult.toBestEvolutionResult());
	}

	// https://github.com/jenetics/jenetics/issues/234
	@Test
	public void constantPopulationForZeroOffspring() {
		final int populationSize = 20;

		final Engine<DoubleGene, Double> engine = Engine
			.builder(gt -> gt.gene().doubleValue(), DoubleChromosome.of(0, 1))
			.populationSize(populationSize)
			.offspringSize(0)
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(100)
			.peek(r -> {
				if (r.population().size() != populationSize) {
					throw new AssertionError(format(
						"Expected population size %d, but got %d.",
						populationSize, r.population().size()
					));
				}
			})
			.collect(EvolutionResult.toBestEvolutionResult());
	}

}
