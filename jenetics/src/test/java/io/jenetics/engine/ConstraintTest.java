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

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ConstraintTest {

	@Test
	public void unconstrained() {
		final Constraint<DoubleGene, Double> constraint = RetryConstraint.of(
			pt -> pt.genotype().gene().doubleValue() < 0.5,
			100
		);
		final Factory<Genotype<DoubleGene>> gtf = Genotype.of(DoubleChromosome.of(0, 1));

		int validCount = 0;
		for (int i = 0; i < 100; ++i) {
			final Genotype<DoubleGene> gt = gtf.newInstance();
			final Phenotype<DoubleGene, Double> pt = Phenotype.of(gt, 1);

			if (constraint.test(pt)) {
				++validCount;
			} else {
				final var repaired = constraint.repair(pt, 1);
				Assert.assertTrue(constraint.test(repaired), "" + repaired + ":" + i);
			}
		}

		Assert.assertTrue(validCount > 10, "validCount < 100: " + validCount);
	}

	@Test
	public void constrain() {
		final Constraint<DoubleGene, Double> constraint = RetryConstraint.of(
			pt -> pt.genotype().gene().doubleValue() < 0.5,
			100
		);
		final Factory<Genotype<DoubleGene>> gtf = constraint
			.constrain(Genotype.of(DoubleChromosome.of(0, 1)));

		for (int i = 0; i < 100; ++i) {
			final Genotype<DoubleGene> gt = gtf.newInstance();
			final Phenotype<DoubleGene, Double> pt = Phenotype.of(gt, 1);
			Assert.assertTrue(constraint.test(pt), "" + pt);
		}
	}

}
