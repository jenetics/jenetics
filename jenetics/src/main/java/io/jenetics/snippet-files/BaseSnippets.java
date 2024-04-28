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


import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.GaussianMutator;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.MeanAlterer;
import io.jenetics.Mutator;
import io.jenetics.PartialAlterer;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.engine.Engine;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
final class BaseSnippets {

	static final class PartialAltererSnippets {

		void usage() {
			// @start region="PartialAltererSnippets.usage"
			// The genotype prototype, consisting of 4 chromosomes
			final Genotype<DoubleGene> gtf = Genotype.of(
				DoubleChromosome.of(0, 1),
				DoubleChromosome.of(1, 2),
				DoubleChromosome.of(2, 3),
				DoubleChromosome.of(3, 4)
			);

			var genotype = Genotype.of(
				IntegerChromosome.of(1, 10, 10), // First chromosome
				IntegerChromosome.of(1, 3, 10), // Second chromosome
				IntegerChromosome.of(1, 10, 10), // Third chromosome
				IntegerChromosome.of(1, 5, 10) // Fourth chromosome
			);

			// Define the GA engine.
			final Engine<DoubleGene, Double> engine = Engine
				.builder(gt -> gt.gene().doubleValue(), gtf)
				.selector(new RouletteWheelSelector<>())
				.alterers(
					// The `Mutator` is used on chromosome with index 0 and 2.
					PartialAlterer.of(new Mutator<DoubleGene, Double>(), 0, 2),
					// The `MeanAlterer` is used on chromosome 3.
					PartialAlterer.of(new MeanAlterer<DoubleGene, Double>(), 3),
					// The `GaussianMutator` is used on all chromosomes.
					new GaussianMutator<>()
				)
				.build();
			// @end
		}

	}

}
