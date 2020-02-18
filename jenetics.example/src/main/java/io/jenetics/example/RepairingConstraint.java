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
package io.jenetics.example;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.DoubleStream;

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Constraint;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.1
 * @since 5.1
 */
public class RepairingConstraint implements Constraint<DoubleGene, Double> {
	@Override
	public boolean test(final Phenotype<DoubleGene, Double> pt) {
		return isValid(
			pt.genotype().chromosome()
				.as(DoubleChromosome.class)
				.toArray()
		);
	}

	static boolean isValid(final double[] x) {
		return x[0] + x[1] <= 1 && x[1]*x[2] <= 0.5;
	}

	@Override
	public Phenotype<DoubleGene, Double> repair(
		final Phenotype<DoubleGene, Double> pt,
		final long generation
	) {
		final double[] x = pt.genotype().chromosome()
			.as(DoubleChromosome.class)
			.toArray();

		return pt(repair(x), generation);
	}

	static double[] repair(final double[] x) {
		if (x[0] + x[1] > 1) x[0] = 1 - x[1];
		if (x[1]*x[2] > 0.5) x[2] = 0.5/x[1];
		return x;
	}

	static Phenotype<DoubleGene, Double> pt(double[] r, long gen) {
		final Genotype<DoubleGene> gt = Genotype.of(
			DoubleChromosome.of(
				DoubleStream.of(r).boxed()
					.map(v -> DoubleGene.of(v, DoubleRange.of(0, 1)))
					.collect(ISeq.toISeq())
			)
		);
		return Phenotype.of(gt, gen);
	}

	public static void main(final String[] args) {
		final Random random = new Random();

		for (int i = 0; i < 100; ++i) {
			final double[] x = random.doubles(3).toArray();
			if (!isValid(x)) {
				System.out.print(Arrays.toString(x));
				repair(x);
				System.out.println("-->" + Arrays.toString(x));
				if (!isValid(x)) {
					throw new RuntimeException(Arrays.toString(x));
				}
			}
		}
	}

}
