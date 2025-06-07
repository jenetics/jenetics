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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class PopulationConvergenceLimitTest {

	@Test
	public void limit() {
		final Predicate<EvolutionResult<?, Double>> l =
			Limits.byPopulationConvergence(0.015);

		int g = 0;
		while (l.test(result(10 + g, 100 + g, g + 1, Optimize.MAXIMUM))) {
			++g;
		}

		Assert.assertEquals(g, 2901);
	}

	private static EvolutionResult<DoubleGene, Double> result(
		final int min,
		final int max,
		final int generation,
		final Optimize opt
	) {
		return EvolutionResult.of(
			opt,
			population(min, max),
			generation,
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
			.mapToObj(PopulationConvergenceLimitTest::phenotype)
			.collect(ISeq.toISeq());
	}

	private static Phenotype<DoubleGene, Double> phenotype(final double value) {
		return Phenotype.of(
			Genotype.of(DoubleChromosome.of(DoubleGene.of(value, 0.0, 1000.0))),
			1,
			value
		);
	}

	@Test(invocationCount = 5)
	public void onesCountLimit() {
		final Problem<ISeq<BitGene>, BitGene, Integer> problem = Problem.of(
			genes -> (int)genes.stream().filter(BitGene::bit).count(),
			Codec.of(
				Genotype.of(BitChromosome.of(20, 0.125)),
				gt -> ISeq.of(gt.chromosome())
			)
		);

		final Engine<BitGene, Integer> engine = Engine.builder(problem)
			.build();

		final EvolutionResult<BitGene, Integer> result = engine.stream()
			.limit(Limits.byPopulationConvergence(0.015))
			.collect(toBestEvolutionResult());

		Assert.assertTrue(
			result.totalGenerations() < 2901,
			"Gen: " + result.totalGenerations()
		);
	}


	@Test
	// https://github.com/jenetics/jenetics/issues/318
	public void initialPopulationConvergence() {
		final Problem<Double, DoubleGene, Double> problem = Problem.of(
			d -> 1.0,
			Codecs.ofScalar(new DoubleRange(0, 1))
		);

		final Engine<DoubleGene, Double> engine = Engine.builder(problem).build();

		final AtomicInteger count = new AtomicInteger();
		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(Limits.byPopulationConvergence(0.03))
			.peek(er -> count.incrementAndGet())
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertNotNull(result);
		Assert.assertEquals(count.get(), 1);
		Assert.assertEquals(result.totalGenerations(), 1);
		Assert.assertEquals(result.generation(), 1);
	}

}
