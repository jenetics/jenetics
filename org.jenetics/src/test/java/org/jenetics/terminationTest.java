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
package org.jenetics;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-09-19 $</em>
 */
public class terminationTest {

//	@Test
//	public void generation() {
//		final GeneticAlgorithm<DoubleGene, Double> ga = TestUtils.GA();
//		ga.setup();
//		ga.evolve(termination.Generation(10));
//		Assert.assertEquals(ga.getGeneration(), 10);
//
//		ga.evolve(5);
//		ga.evolve(termination.Generation(10));
//		Assert.assertEquals(ga.getGeneration(), 15);
//
//		ga.evolve(6);
//		ga.evolve(termination.Generation(50));
//		Assert.assertEquals(ga.getGeneration(), 50);
//	}
//
//	static final Function<Genotype<DoubleGene>, Double> FF =
//		new Function<Genotype<DoubleGene>, Double>()
//	{
//		@Override
//		public Double apply(final Genotype<DoubleGene> genotype) {
//			final double value = genotype.getChromosome().getGene().getAllele();
//			return Math.sin(value);
//		}
//	};
//
//	static GeneticAlgorithm<DoubleGene, Double> GA() {
//		return new GeneticAlgorithm<>(
//				Genotype.of(DoubleChromosome.of(0, 10)), FF
//			);
//	}
//
//	@Test
//	public void steadyState() {
//		final int steadyGenerations = 11;
//		final LinkedList<Double> values = new LinkedList<>();
//		values.addFirst(-100.0);
//
//		final GeneticAlgorithm<DoubleGene, Double> ga = GA();
//		ga.setPopulationSize(20);
//		ga.setAlterers(
//			ga.getAlterer(),
//			new Mutator<DoubleGene, Double>(0.999)
//		);
//		ga.setup();
//		values.addFirst(ga.getBestPhenotype().getFitness());
//
//		final Predicate<Statistics<?, Double>> until =
//			termination.SteadyFitness(steadyGenerations);
//
//		while (until.test(ga.getStatistics())) {
//			ga.evolve();
//			values.addFirst(ga.getBestPhenotype().getFitness());
//
//			if (values.size() > steadyGenerations) {
//				values.removeLast();
//			}
//		}
//
//		Assert.assertEquals(values.size(), steadyGenerations);
//		Assert.assertTrue(ga.getGeneration() > steadyGenerations);
//
//		Collections.sort(values);
//		Double value = values.removeFirst();
//		for (Double f : values) {
//			Assert.assertEquals(f, value);
//		}
//
//	}

}
