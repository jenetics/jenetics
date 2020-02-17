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

import static org.testng.Assert.assertEquals;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class FutureEvaluatorTest {

	static Future<Double> eval(final Genotype<DoubleGene> gt) {
		return CompletableFuture.completedFuture(gt.gene().doubleValue());
	}

	@Test
	public void evaluate() {
		final Genotype<DoubleGene> gtf = Genotype.of(DoubleChromosome.of(0, 1));
		final ISeq<Phenotype<DoubleGene, Double>> population = gtf.instances()
			.limit(100)
			.map(gt -> Phenotype.<DoubleGene, Double>of(gt, 1))
			.collect(ISeq.toISeq());

		population.forEach(pt -> Assert.assertTrue(pt.nonEvaluated()));

		final Evaluator<DoubleGene, Double> evaluator = Evaluators.async(FutureEvaluatorTest::eval);
		final ISeq<Phenotype<DoubleGene, Double>> evaluated = evaluator.eval(population);

		evaluated.forEach(pt -> Assert.assertTrue(pt.isEvaluated()));
		evaluated.forEach(pt ->
			assertEquals(pt.genotype().gene().doubleValue(), pt.fitness().doubleValue()));
	}

}
