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
package io.jenetics.ext.moea;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.String.format;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.Selector;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Problem;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class NSGA2SelectorTest {

	private static final Problem<double[], DoubleGene, Vec<double[]>>
		PROBLEM = Problem.of(
			v -> Vec.of(new double[]{v[0]*cos(v[1]), v[0]*sin(v[1])}),
			Codecs.ofVector(
				DoubleRange.of(0, 1),
				DoubleRange.of(0, 2*PI)
			)
		);

	private static final int OBJECTIVES = 2;

	@Test
	public void selectMax() {
		final Selector<DoubleGene, Vec<double[]>> selector =
			NSGA2Selector.ofVec(OBJECTIVES);

		final ISeq<Phenotype<DoubleGene, Vec<double[]>>> population =
			Stream.generate(this::phenotype)
				.limit(2000)
				.collect(ISeq.toISeq());

		final ISeq<Phenotype<DoubleGene, Vec<double[]>>> selected =
			selector.select(population, 100, Optimize.MAXIMUM);

		/*
		selected.stream().limit(100)
			.map(Phenotype::getFitness)
			.forEach(f -> System.out.println(f.data()[0] + " " + f.data()[1]));
		*/

		final double mean = selected.stream()
			.map(Phenotype::fitness)
			.mapToDouble(NSGA2SelectorTest::dist)
			.sum()/selected.size();

		Assert.assertTrue(mean > 0.8, format("Expect mean > 0.8: %s", mean));

		Assert.assertEquals(
			selected.stream()
				.map(Phenotype::fitness)
				.collect(Collectors.toSet())
				.size(),
			selected.size()
		);
	}

	@Test
	public void selectMin() {
		final Selector<DoubleGene, Vec<double[]>> selector =
			NSGA2Selector.ofVec(OBJECTIVES);

		final ISeq<Phenotype<DoubleGene, Vec<double[]>>> population =
			Stream.generate(this::phenotype)
				.limit(2000)
				.collect(ISeq.toISeq());

		final ISeq<Phenotype<DoubleGene, Vec<double[]>>> selected =
			selector.select(population, 100, Optimize.MINIMUM);

		final double mean = selected.stream()
			.map(Phenotype::fitness)
			.mapToDouble(NSGA2SelectorTest::dist)
			.sum()/selected.size();

		Assert.assertTrue(mean < -0.8, format("Expect mean < -0.8: %s", mean));

		Assert.assertEquals(
			selected.stream()
				.map(Phenotype::fitness)
				.collect(Collectors.toSet())
				.size(),
			selected.size()
		);
	}

	static double dist(final Vec<double[]> vec) {
		double dist = 0;
		for (int i = 0; i < vec.length(); ++i) {
			dist += vec.data()[i];
		}
		return dist;
	}

	private Phenotype<DoubleGene, Vec<double[]>> phenotype() {
		final Genotype<DoubleGene> gt = PROBLEM.codec().encoding().newInstance();
		return Phenotype.of(
			gt,
			1L,
			PROBLEM.fitness().apply(PROBLEM.codec().decode(gt))
		);
	}

}
