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

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.jenetics.engine.EvolutionResult.toBestPhenotype;
import static org.jenetics.engine.EvolutionStream.Limit.bySteadyFitness;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutator;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionStatistics;
import org.jenetics.stat.MinMax;

public class RealFunction {

	private static Double evaluate(final Genotype<DoubleGene> gt) {
		final double x = gt.getGene().doubleValue();
		return cos(0.5 + sin(x)) * cos(x);
	}

	public static void main(String[] args) {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(
				RealFunction::evaluate,
				DoubleChromosome.of(0.0, 2.0*PI))
			.populationSize(500)
			.optimize(Optimize.MINIMUM)
			.alterers(
				new Mutator<>(0.03),
				new MeanAlterer<>(0.6))
			.build();


		final EvolutionStatistics<Double, ?>
			statistics = EvolutionStatistics.ofComparable();

		final Phenotype<DoubleGene, Double> result = engine.stream()
			.limit(bySteadyFitness(7))
			.limit(100)
			.peek(statistics)
			.collect(toBestPhenotype());

		System.out.println(statistics);
		System.out.println(result);
	}
}
