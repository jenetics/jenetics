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

import static java.lang.String.format;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.Chromosome;
import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.prngine.Random32;
import io.jenetics.stat.DoubleSummary;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class GeneConvergenceLimitTest {

	@Test(dataProvider = "limits")
	public void limit(
		final ISeq<Phenotype<DoubleGene, Double>> pop,
		final double geneConvergence,
		final double convergenceRage,
		final boolean proceed
	) {
		//final ISeq<DoubleMoments> stat = GeneConvergenceLimit.statistics(pop);
		//stat.forEach(System.out::println);

		final Predicate<EvolutionResult<DoubleGene, ?>> l =
			Limits.byGeneConvergence(geneConvergence, convergenceRage);

		//System.out.println(l.test(result(pop)));
		Assert.assertEquals(l.test(result(pop)), proceed);
	}

	@DataProvider(name = "limits")
	public Object[][] limits() {
		return new Object[][] {
			{population(0, 10), 0.6, 0.5, true},
			{population(0, 10), 0.5, 0.6, false},
			{population(0, 10), 0.5, 0.5, false},
			{population(5, 11), 0.9, 0.7, true}
		};
	}

	private static EvolutionResult<DoubleGene, Double> result(
		final ISeq<Phenotype<DoubleGene, Double>> pop
	) {
		return EvolutionResult.of(
			Optimize.MAXIMUM,
			pop,
			3L,
			EvolutionDurations.ZERO,
			1,
			1,
			1
		);
	}

	private static ISeq<Phenotype<DoubleGene, Double>> population(
		final int min,
		final int max
	) {
		return IntStream.rangeClosed(min, max)
			.mapToDouble(i -> (double)i)
			.mapToObj(GeneConvergenceLimitTest::phenotype)
			.collect(ISeq.toISeq());
	}

	private static Phenotype<DoubleGene, Double> phenotype(final double value) {
		return Phenotype.of(
			Genotype.of(DoubleChromosome.of(
				DoubleGene.of(value, 0.0, 1000.0),
				DoubleGene.of(value, 0.0, 1000.0)
			)),
			1
		);
	}

	@Test
	public void engineLimit() {
		final Problem<double[], DoubleGene, Double> problem = Problem.of(
			DoubleSummary::sum,
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 10, 100)),
				gt -> gt.stream()
					.flatMap(Chromosome::stream)
					.mapToDouble(DoubleGene::doubleValue)
					.toArray()
			)
		);

		final Engine<DoubleGene, Double> engine = Engine.builder(problem).build();
		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(Limits.byGeneConvergence(0.7, 0.7))
			.limit(10_000)
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertTrue(
			result.totalGenerations() < 10_000,
			format(
				"Total generations bigger than 10,000: %s",
				result.totalGenerations()
			)
		);
	}

	@Test
	// https://github.com/jenetics/jenetics/issues/318
	public void initialGeneConvergence() {
		RandomRegistry.using(Random32.of(() -> 234), random -> {
			final Problem<Double, DoubleGene, Double> problem = Problem.of(
				d -> 1.0,
				Codecs.ofScalar(new DoubleRange(0, 1))
			);

			final Engine<DoubleGene, Double> engine = Engine
				.builder(problem)
				.executor(Runnable::run)
				.build();

			final AtomicInteger count = new AtomicInteger();
			final EvolutionResult<DoubleGene, Double> result = engine.stream()
				.limit(Limits.byGeneConvergence(0.03, 0.03))
				.peek(er -> count.incrementAndGet())
				.collect(EvolutionResult.toBestEvolutionResult());

			Assert.assertNotNull(result);
			Assert.assertEquals(count.get(), 1);
			Assert.assertEquals(result.totalGenerations(), 1);
			Assert.assertEquals(result.generation(), 1);
		});
	}

}
