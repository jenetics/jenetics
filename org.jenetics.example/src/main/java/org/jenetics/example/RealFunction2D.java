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
package org.jenetics.example;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.GaussianMutator;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.TournamentSelector;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;

final class Real2D implements Function<Genotype<DoubleGene>, Double> {
	@Override
	public Double apply(final Genotype<DoubleGene> gt) {
		final Double x1 = gt.getChromosome(0).getGene().getAllele();
		final Double x2 = gt.getChromosome(1).getGene().getAllele();

		return f(x1, x2);
	}

	private static double f(final double x1, final double x2) {
		return x1+x2;
	}
}

public class RealFunction2D {

	private static class FF implements Function<Genotype<DoubleGene>, Double> {
		@Override
		public Double apply(final Genotype<DoubleGene> gt) {
			final Double x1 = gt.getChromosome(0).getGene().getAllele();
			final Double x2 = gt.getChromosome(1).getGene().getAllele();

			return f(x1, x2);
		}

		// Function to be optimized.
		private static double f(final double x1, final double x2) {
			return x1+x2;
		}
	}

	public static void main(final String[] args) {
		final Factory<Genotype<DoubleGene>> gtf = Genotype.of(
			new DoubleChromosome(0.0, 10.0), // x1 in [0, 10]
			new DoubleChromosome(5.0, 33.0)  // x2 in [5, 33]
		);

		final Function<Genotype<DoubleGene>, Double> ff = new FF();
		final GeneticAlgorithm<DoubleGene, Double> ga = new GeneticAlgorithm<>(gtf, ff);

		ga.setSelectors(new TournamentSelector<DoubleGene, Double>(3));
		// This are the alterers you can use for this example, in any combination.
		ga.setAlterers(
			new MeanAlterer<DoubleGene>(),
			new GaussianMutator<DoubleGene>()
		);

		ga.setup();
		ga.evolve(100);

		System.out.println(ga.getBestPhenotype());
	}

}
