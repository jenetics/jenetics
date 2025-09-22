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
import java.util.function.Function;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class FitnessThresholdLimitTest {

	@Test(dataProvider = "testData")
	public void testMaximum(
		final Double threshold,
		final Integer min,
		final Integer max,
		final Optimize opt,
		final Boolean result
	) {
		final FitnessThresholdLimit<Double> limit =
			new FitnessThresholdLimit<>(threshold);

		limit.test(result(min, max, opt));

		Assert.assertEquals(
			limit.test(result(min, max, opt)),
			result.booleanValue()
		);
	}

	@DataProvider(name = "testData")
	public Object[][] testData() {
		return new Object[][] {
			{990.0, 0, 900, Optimize.MAXIMUM, true},
			{990.0, 0, 990, Optimize.MAXIMUM, true},
			{990.0, 0, 991, Optimize.MAXIMUM, false},
			{990.0, 800, 991, Optimize.MAXIMUM, false},

			{300.0, 800, 1000, Optimize.MINIMUM, true},
			{300.0, 300, 1000, Optimize.MINIMUM, true},
			{300.0, 299, 1000, Optimize.MINIMUM, false},
			{300.0, 0, 1000, Optimize.MINIMUM, false},
		};
	}

	private static EvolutionResult<DoubleGene, Double> result(
		final int min,
		final int max,
		final Optimize opt
	) {
		return EvolutionResult.of(
			opt,
			population(min, max),
			2L,
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
			.mapToObj(FitnessThresholdLimitTest::phenotype)
			.collect(ISeq.toISeq());
	}

	private static Phenotype<DoubleGene, Double> phenotype(final double value) {
		return Phenotype.of(
			Genotype.of(DoubleChromosome.of(DoubleGene.of(value, 0.0, 1000.0))),
			1,
			value
		);
	}

	@Test
	// https://github.com/jenetics/jenetics/issues/318
	public void initialFitnessConvergence() {
		final Problem<Double, DoubleGene, Double> problem = Problem.of(
			d -> 1.0,
			Codecs.ofScalar(new DoubleRange(0, 1))
		);

		final Engine<DoubleGene, Double> engine = Engine.builder(problem).build();

		final AtomicInteger count = new AtomicInteger();
		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(Limits.byFitnessThreshold(0.3))
			.peek(er -> count.incrementAndGet())
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertNotNull(result);
		Assert.assertEquals(count.get(), 1);
		Assert.assertEquals(result.totalGenerations(), 1);
		Assert.assertEquals(result.generation(), 1);
	}

	@Test
	// https://github.com/jenetics/jenetics/issues/420
	public void bestFitnessResult() {
		final Genotype<IntegerGene> genotype = Genotype.of(IntegerChromosome.of(0, 10));
		final AtomicInteger ai = new AtomicInteger();
		final Function<Genotype<IntegerGene>, Integer> ff = x -> ai.incrementAndGet();

		final int threshold = 100;
		final Integer result = Engine.builder(ff, genotype)
			.build()
			.stream()
			.limit(Limits.byFitnessThreshold(threshold))
			.collect(EvolutionResult.toBestEvolutionResult())
			.bestFitness();

		Assert.assertNotNull(result);
		Assert.assertTrue(
			result >= 100,
			format("Expected value >= %s, but got %s", threshold, result)
		);
	}

}
