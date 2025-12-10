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
import io.jenetics.util.BatchExecutor;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ExecutorEvaluatorTest {

	@Test
	public void evaluateSerial() {
		final ISeq<Phenotype<DoubleGene, Double>> phenotypes =
			Genotype.of(DoubleChromosome.of(0, 1)).instances()
				.limit(100)
				.map(gt -> Phenotype.<DoubleGene, Double>of(gt, 1))
				.collect(ISeq.toISeq());

		phenotypes.forEach(pt -> Assert.assertTrue(pt.nonEvaluated()));

		final Evaluator<DoubleGene, Double>
			evaluator =
			new FitnessEvaluator<>(
				gt -> gt.gene().doubleValue(),
				BatchExecutor.of(Runnable::run)
			);

		final ISeq<Phenotype<DoubleGene, Double>> evaluated = evaluator.eval(phenotypes);

		evaluated.forEach(pt -> Assert.assertEquals(pt.genotype().gene().allele(), pt.fitness()));
	}

}
