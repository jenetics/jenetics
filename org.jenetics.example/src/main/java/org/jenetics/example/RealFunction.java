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

import org.jscience.mathematics.number.Float64;

import org.jenetics.Float64Chromosome;
import org.jenetics.Float64Gene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutator;
import org.jenetics.NumberStatistics;
import org.jenetics.Optimize;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;

final class Real
	implements Function<Genotype<Float64Gene>, Float64>
{
	@Override
	public Float64 apply(Genotype<Float64Gene> genotype) {
		final double x = genotype.getGene().doubleValue();
		return Float64.valueOf(cos(0.5 + sin(x)) * cos(x));
	}
}

public class RealFunction {
	public static void main(String[] args) {
		Factory<Genotype<Float64Gene>> gtf = Genotype.valueOf(
			new Float64Chromosome(0.0, 2.0 * PI)
		);
		Function<Genotype<Float64Gene>, Float64> ff = new Real();
		GeneticAlgorithm<Float64Gene, Float64> ga =
		new GeneticAlgorithm<>(
			gtf, ff, Optimize.MINIMUM
		);

		ga.setStatisticsCalculator(
			new NumberStatistics.Calculator<Float64Gene, Float64>()
		);
		ga.setPopulationSize(500);
		ga.setAlterers(
			new Mutator<Float64Gene>(0.03),
			new MeanAlterer<Float64Gene>(0.6)
		);

		ga.setup();
		ga.evolve(100);
		System.out.println(ga.getBestStatistics());
		System.out.println(ga.getBestPhenotype());
	}
}
