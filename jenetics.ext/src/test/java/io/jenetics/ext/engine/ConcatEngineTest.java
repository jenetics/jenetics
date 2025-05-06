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
package io.jenetics.ext.engine;

import static io.jenetics.ext.engine.EvolutionStreamables.streamable;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.Chromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.MeanAlterer;
import io.jenetics.MonteCarloSelector;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionInit;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.Limits;
import io.jenetics.engine.Problem;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;

import io.jenetics.ext.EvolutionStreams;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ConcatEngineTest {

	@Test
	public void concat0() {
		final EvolutionStream<IntegerGene, Integer> stream =
			ConcatEngine.<IntegerGene, Integer>of().stream();

		final int[] array = stream
			.mapToInt(r -> r.genotypes().get(0).gene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{});
	}

	@Test
	public void concat1() {
		final EvolutionStream<IntegerGene, Integer> stream =
			ConcatEngine.of(streamable(1))
				.stream();

		final int[] array = stream
			.mapToInt(r -> r.genotypes().get(0).gene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{1});
	}

	@Test
	public void concat1a() {
		final EvolutionStream<IntegerGene, Integer> stream =
			ConcatEngine.of(streamable(1))
				.stream(() -> EvolutionStreams.result(5).toEvolutionStart());

		final int[] array = stream
			.mapToInt(r -> r.genotypes().get(0).gene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{6});
	}

	@Test
	public void concat1b() {
		EvolutionInit<IntegerGene> init = EvolutionInit.of(
			EvolutionStreams.result(5)
				.toEvolutionStart()
				.population().stream()
				.map(Phenotype::genotype)
				.collect(ISeq.toISeq()),
			1
		);

		final EvolutionStream<IntegerGene, Integer> stream =
			ConcatEngine.of(streamable(1))
				.stream(init);

		final int[] array = stream
			.mapToInt(r -> r.genotypes().get(0).gene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{6});
	}

	@Test
	public void concat2() {
		final EvolutionStream<IntegerGene, Integer> stream =
			ConcatEngine.of(streamable(5))
				.stream();

		final int[] array = stream
			.mapToInt(r -> r.genotypes().get(0).gene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{1, 2, 3, 4, 5});
	}

	@Test
	public void concat3() {
		final EvolutionStream<IntegerGene, Integer> stream =
			ConcatEngine.of(streamable(5))
				.stream()
				.limit(Limits.byFixedGeneration(3));

		final int[] array = stream
			.mapToInt(r -> r.genotypes().get(0).gene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{1, 2, 3});
	}

	@Test
	public void concat4() {
		final EvolutionStream<IntegerGene, Integer> stream =
			ConcatEngine.of(
				streamable(3),
				streamable(4),
				streamable(5)
			)
			.stream();

		final int[] array = stream
			.mapToInt(r -> r.genotypes().get(0).gene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
	}

	@Test
	public void concat5() {
		final EvolutionStream<IntegerGene, Integer> stream =
			ConcatEngine.of(
				streamable(3),
				streamable(4),
				streamable(5),
				streamable(15),
				streamable(15)
			)
			.stream()
			.limit(Limits.byFixedGeneration(15));

		final int[] array = stream
			.mapToInt(r -> r.genotypes().get(0).gene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15});
	}

	@Test
	public void concatInit() {
		final Chromosome<IntegerGene> ch = IntegerChromosome.of(IntegerGene.of(5, 0, 1000));
		final Genotype<IntegerGene> gt = Genotype.of(ch);
		final EvolutionInit<IntegerGene> init = EvolutionInit.of(
			ISeq.<Genotype<IntegerGene>>of(gt),
			1L
		);

		final EvolutionStream<IntegerGene, Integer> stream =
			ConcatEngine.of(
				streamable(2),
				streamable(2),
				streamable(2),
				streamable(32)
			)
			.stream(init);

		final int[] array = stream
			.limit(12)
			.mapToInt(r -> r.genotypes().get(0).gene().intValue())
			.toArray();

		Assert.assertEquals(array, new int[]{6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17});
	}


	public static void main(final String[] args) {
		final Problem<double[], DoubleGene, Double> problem = Problem.of(
			v -> Math.sin(v[0])*Math.cos(v[1]),
			Codecs.ofVector(new DoubleRange(0, 2*Math.PI), 2)
		);

		final Engine<DoubleGene, Double> engine1 = Engine.builder(problem)
			.minimizing()
			.alterers(new Mutator<>(0.2))
			.selector(new MonteCarloSelector<>())
			.build();

		final Engine<DoubleGene, Double> engine2 = Engine.builder(problem)
			.minimizing()
			.alterers(
				new Mutator<>(0.1),
				new MeanAlterer<>())
			.selector(new RouletteWheelSelector<>())
			.build();

		final Genotype<DoubleGene> result =
			ConcatEngine.of(
				engine1.limit(50),
				engine2.limit(() -> Limits.bySteadyFitness(30)))
			.stream()
				.collect(EvolutionResult.toBestGenotype());

		System.out.println(result + ": " +
				problem.fitness().apply(problem.codec().decode(result)));
	}

}
