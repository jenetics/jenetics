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

import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class EngineExecutorTest {

	private static double f(final Genotype<DoubleGene> gt) {
		return IntStream.range(0, 50000)
			.mapToDouble(Math::sinh)
			.sum()*gt.getGene().getAllele();
	}

	public static void main(final String[] args) {
		//final ExecutorService executor = Executors.newFixedThreadPool(2);

		final Engine<DoubleGene, Double> engine = Engine
			.builder(EngineExecutorTest::f, DoubleChromosome.of(0, 1))
			.executor(new ForkJoinPool(10))
			.build();

		for (int i = 0; i < 1000; ++i) {
			final Double result = engine.stream()
				.limit(100)
				.collect(EvolutionResult.toBestGenotype())
				.getGene().getAllele();

			System.out.println("Gen: " + i + ": " + result);
		}

		//executor.shutdown();
		System.out.println("READY");
	}


}
