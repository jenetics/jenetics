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

import java.util.Arrays;
import java.util.stream.Stream;

import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.util.IntRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.2
 * @since 4.2
 */
public class Matrix {

	public static void main(final String[] args) {
		final Problem<int[][], IntegerGene, Integer> problem = Problem.of(
			Matrix::fitness,
			Codec.of(
				Genotype.of(IntegerChromosome.of(new IntRange(0, 10), 3), 3),
				gt -> gt.stream()
					.map(ch -> ch.stream()
						.mapToInt(IntegerGene::intValue).toArray())
					.toArray(int[][]::new)
			)
		);

		final Engine<IntegerGene, Integer> engine = Engine.builder(problem).build();

		final Genotype<IntegerGene> gt = engine.stream()
			.limit(Limits.byFixedGeneration(20))
			.collect(EvolutionResult.toBestGenotype());

		final int[][] best = problem.codec().decode(gt);
		print(best);
	}

	private static int fitness(final int[][] m) {
		int sum = 0;
		for (int[] v : m) sum += fitness(v);
		return sum;
	}

	private static int fitness(final int[] v) {
		int sum = 0;
		int tens = 0;
		for (int value : v) {
			sum += value;
			if (value == 10) ++tens;
		}

		return tens <= 1 ? sum : 0;
	}

	private static void print(final int[][] m) {
		System.out.println(
			Arrays.toString(
				Stream.of(m)
					.map(Arrays::toString)
					.toArray(String[]::new)
			)
		);
	}
}
