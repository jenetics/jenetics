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
import org.jenetics.Genotype;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;

public class MultiDimExample {

	/*
	 * The following Genotype factory defines a 4 dimensional search space with
	 * different extension for each dimension.
	 */

	final Factory<Genotype<DoubleGene>> gtf_1 = Genotype.of(
		DoubleChromosome.of(0.0, 10.0),  // x1 in [0, 10]
		DoubleChromosome.of(-5.0, 0.0),  // x2 in [-5, 0]
		DoubleChromosome.of(1.0, 10.0),  // x3 in [1, 10]
		DoubleChromosome.of(0.0, 100.0)  // x4 in [0, 100]
	);

	final Function<Genotype<DoubleGene>, Double> ff_1 = new Function<Genotype<DoubleGene>, Double>() {
		@Override
		public Double apply(final Genotype<DoubleGene> gt) {
			final double x1 = gt.getChromosome(0).getGene().getAllele();
			final double x2 = gt.getChromosome(1).getGene().getAllele();
			final double x3 = gt.getChromosome(2).getGene().getAllele();
			final double x4 = gt.getChromosome(3).getGene().getAllele();

			// This assertions hold because of the defined structure of the
			// Genotype factory.
			assert(x1 >= 0 && x1 <= 10);
			assert(x2 >= -5 && x2 <= 0);
			assert(x3 >= 1 && x3 <= 10);
			assert(x4 >= 0 && x4 <= 100);


			return f(x1, x2, x3, x4);
		}

		// Your fitness function implementation.
		private double f(double x1, double x2, double x3, double x4) {
			return x1+x2+x3+x4;
		}
	};

	/*
	 * This Genotype schema has the same extension for each dimension.
	 */

	final Factory<Genotype<DoubleGene>> gtf_2 = Genotype.of(
		new DoubleChromosome(0.0, 10.0, 4)  // x1, x2, x3, x4 in [0, 10]
	);

	final Function<Genotype<DoubleGene>, Double> ff_2 = new Function<Genotype<DoubleGene>, Double>() {
		@Override
		public Double apply(final Genotype<DoubleGene> gt) {
			final double x1 = gt.getChromosome().getGene(0).getAllele();
			final double x2 = gt.getChromosome().getGene(1).getAllele();
			final double x3 = gt.getChromosome().getGene(2).getAllele();
			final double x4 = gt.getChromosome().getGene(3).getAllele();

			// This assertions hold because of the defined structure of the
			// Genotype factory.
			assert(x1 >= 0 && x1 <= 10);
			assert(x1 >= 0 && x1 <= 10);
			assert(x1 >= 0 && x1 <= 10);
			assert(x1 >= 0 && x1 <= 10);


			return f(x1, x2, x3, x4);
		}

		// Your fitness function implementation.
		private double f(double x1, double x2, double x3, double x4) {
			return x1+x2+x3+x4;
		}
	};
}
