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
package io.jenetics;

import io.jenetics.incubator.stat.Histogram;
import io.jenetics.incubator.stat.ObservationTask;
import io.jenetics.stat.LongMomentStatistics;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.StableRandomExecutor;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.jenetics.TestUtils.diff;
import static io.jenetics.TestUtils.newDoubleGenePopulation;
import static io.jenetics.incubator.stat.Assurance.assertThatObservation;
import static java.lang.Math.sqrt;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class MeanAltererTest extends AltererTester {

	@Override
	public Alterer<DoubleGene, Double> newAlterer(final double p) {
		return new MeanAlterer<>(p);
	}

	@Test
	public void recombinate() {
		final int ngenes = 11;
		final int nchromosomes = 9;
		final int npopulation = 100;
		final ISeq<Phenotype<DoubleGene, Double>> p1 =
			newDoubleGenePopulation(ngenes, nchromosomes, npopulation);

		final MSeq<Phenotype<DoubleGene, Double>> p2 = p1.copy();
		final int[] selected = new int[]{3, 34};

		final MeanAlterer<DoubleGene, Double> crossover = new MeanAlterer<>(0.1);
		crossover.recombine(p2, selected, 3);

		Assert.assertEquals(diff(p1, p2), ngenes);
	}

	@Test(dataProvider = "alterProbabilityParameters")
	public void alterProbability(
		final Integer ngenes,
		final Integer nchromosomes,
		final Integer npopulation,
		final Double p
	) {
		final ISeq<Phenotype<DoubleGene, Double>> population =
			newDoubleGenePopulation(ngenes, nchromosomes, npopulation);

		// The mutator to test.
		final MeanAlterer<DoubleGene, Double> crossover = new MeanAlterer<>(p);

		final var statistics = new LongMomentStatistics();

		final var observation = new ObservationTask(
			samples -> {
				for (int i = 0; i < 100; ++i) {
					final long alterations = crossover
						.alter(population, 1)
						.alterations();
					samples.add(alterations);
					statistics.accept(alterations);
				}
			},
			Histogram.Partition.of(0, ngenes*nchromosomes*npopulation, 20)
		);
		new StableRandomExecutor(123456789).execute(observation);

		assertThatObservation(observation)
			.isNormal(npopulation*p, sqrt(statistics.variance()));
	}

	@DataProvider(name = "alterProbabilityParameters")
	public Object[][] alterProbabilityParameters() {
		return TestUtils.alterProbabilityParameters();
	}
}
