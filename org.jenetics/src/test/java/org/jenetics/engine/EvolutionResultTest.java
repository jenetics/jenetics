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
package org.jenetics.engine;

import java.io.Serializable;
import java.time.Duration;
import java.util.Random;
import java.util.function.Function;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.util.Factory;
import org.jenetics.util.ObjectTester;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-09-20 $</em>
 */
public class EvolutionResultTest
	extends ObjectTester<EvolutionResult<DoubleGene, Double>>
{

	@Override
	protected Factory<EvolutionResult<DoubleGene, Double>> factory() {
		final Function<Genotype<DoubleGene>, Double> ff =
			(Function<Genotype<DoubleGene>, Double> & Serializable)
				a -> a.getGene().getAllele();

		return () -> {
			final Random random = RandomRegistry.getRandom();

			final Genotype<DoubleGene> gt = Genotype.of(DoubleChromosome.of(0, 1));

			return EvolutionResult.of(
				random.nextBoolean() ? Optimize.MAXIMUM : Optimize.MINIMUM,
				new Population<DoubleGene, Double>(100)
					.fill(() -> Phenotype.of(gt.newInstance(), ff, 1), 100),
				random.nextInt(1000),
				EvolutionDurations.of(
					Duration.ofMillis(random.nextInt(1_000_000)),
					Duration.ofMillis(random.nextInt(1_000_000)),
					Duration.ofMillis(random.nextInt(1_000_000)),
					Duration.ofMillis(random.nextInt(1_000_000)),
					Duration.ofMillis(random.nextInt(1_000_000)),
					Duration.ofMillis(random.nextInt(1_000_000)),
					Duration.ofMillis(random.nextInt(1_000_000))
				),
				random.nextInt(100),
				random.nextInt(100),
				random.nextInt(100)
			);
		};
	}


}
