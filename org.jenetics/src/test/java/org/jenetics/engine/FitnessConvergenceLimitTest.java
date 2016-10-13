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

import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.engine.FitnessConvergenceLimit.Buffer;
import org.jenetics.stat.DoubleMomentStatistics;
import org.jenetics.stat.DoubleMoments;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class FitnessConvergenceLimitTest {

	@Test
	public void bufferLength() {
		final long seed = 0xdeadbeef;
		final int capacity = 10;

		final Random random = new Random(seed);
		final Buffer buffer = new Buffer(capacity);

		for (int i = 0; i < buffer.capacity(); ++i) {
			buffer.accept(random.nextDouble());
			Assert.assertEquals(buffer.length(), i + 1);
		}

		for (int i = 0; i < buffer.capacity(); ++i) {
			buffer.accept(random.nextDouble());
			Assert.assertEquals(buffer.length(), buffer.capacity());
		}
	}

	@Test
	public void stream() {
		final long seed = 0xdeadbeef;
		final int capacity = 10;

		final Random random = new Random(seed);
		final Buffer buffer = new Buffer(capacity);

		for (int i = 0; i < buffer.capacity(); ++i) {
			final double value = random.nextDouble();
			buffer.accept(value);
		}

		random.setSeed(seed);
		buffer.stream().forEach(d -> Assert.assertEquals(d, random.nextDouble()));

		random.setSeed(seed);
		for (int i = 0; i < 5; ++i) random.nextDouble();
		buffer.stream(5).forEach(d -> Assert.assertEquals(d, random.nextDouble()));
	}

	@Test
	public void bufferDoubleMoments() {
		final long seed = 0xdeadbeef;
		final int capacity = 10;

		final Random random = new Random(seed);
		final Buffer buffer = new Buffer(capacity);

		DoubleMomentStatistics statistics = new DoubleMomentStatistics();
		for (int i = 0; i < buffer.capacity(); ++i) {
			final double value = random.nextDouble()*1000;
			buffer.accept(value);
			statistics.accept(value);

			final DoubleMoments moments = DoubleMoments.of(statistics);
			Assert.assertEquals(moments, buffer.doubleMoments(1000));
		}

		final Random sr = new Random(seed);
		for (int i = 0; i < buffer.capacity(); ++i) {
			statistics = statistics(new Random(seed), i + 1, buffer.capacity() - 1);

			final double value = random.nextDouble()*1000;
			buffer.accept(value);
			statistics.accept(value);

			final DoubleMoments moments = DoubleMoments.of(statistics);
			Assert.assertEquals(moments, buffer.doubleMoments(1000));
		}
	}

	public static DoubleMomentStatistics statistics(
		final Random random,
		final int skip,
		final int size
	) {
		final DoubleMomentStatistics statistics = new DoubleMomentStatistics();
		for (int i = 0; i < skip; ++i) random.nextDouble();
		for (int i = 0; i < size; ++i) statistics.accept(random.nextDouble()*1000);

		return statistics;
	}

	@Test
	public void limit() {
		final Predicate<EvolutionResult<?, Double>> l =
			limit.byFitnessConvergence(5, 10, 0.015);

		int g = 0;
		while (l.test(result(10 + g, 100 + g, Optimize.MAXIMUM))) {
			++g;
		}

		Assert.assertEquals(g, 69);
	}

	private static EvolutionResult<DoubleGene, Double> result(
		final int min,
		final int max,
		final Optimize opt
	) {
		return EvolutionResult.of(
			opt,
			population(min, max),
			1L,
			EvolutionDurations.ZERO,
			1,
			1,
			1
		);
	}

	private static Population<DoubleGene, Double> population(
		final int min,
		final int max
	) {
		return IntStream.rangeClosed(min, max)
			.mapToDouble(i -> (double)i)
			.mapToObj(FitnessConvergenceLimitTest::phenotype)
			.collect(Population.toPopulation());
	}

	private static Phenotype<DoubleGene, Double> phenotype(final double value) {
		return Phenotype.of(
			Genotype.of(DoubleChromosome.of(DoubleGene.of(value, 0.0, 1000.0))),
			1,
			a -> a.getGene().getAllele()
		);
	}

	@Test
	public void onesCountLimit() {
		final Problem<ISeq<BitGene>, BitGene, Integer> problem = Problem.of(
			genes -> (int)genes.stream().filter(BitGene::getBit).count(),
			Codec.of(
				Genotype.of(BitChromosome.of(20, 0.125)),
				gt -> gt.getChromosome().toSeq()
			)
		);

		final Engine<BitGene, Integer> engine = Engine.builder(problem)
			.build();

		final EvolutionResult<BitGene, Integer> result = engine.stream()
			.limit(limit.byFitnessConvergence(5, 10, 0.01))
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertTrue(
			result.getTotalGenerations() < 50,
			"Gen: " + result.getTotalGenerations()
		);
	}

}
