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
package io.jenetics.ext;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.util.Comparator;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collector;

import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.MeanAlterer;
import io.jenetics.Mutator;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Problem;
import io.jenetics.util.DoubleRange;

import io.jenetics.ext.util.ParetoSet;
import io.jenetics.ext.util.Point2;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class MOEATest {

	static final Random random = new Random();
	static Point2 f(final double[] x) {
		return Point2.of(sin(x[0])*x[1], cos(x[0])*x[1]);
	}

	static final Problem<double[], DoubleGene, Point2> problem = Problem.of(
		MOEATest::f,
		Codecs.ofVector(DoubleRange.of(0, 1), 2)
	);

	public static void main(final String[] args) {


		final Engine<DoubleGene, Point2> engine = Engine.builder(problem)
			.alterers(
				new Mutator<>(0.1),
				new MeanAlterer<>())
			.selector(new TournamentSelector<>(2))
			.build();

		/*
		final ParetoSet<Point2> result = engine.stream()
			.limit(1000)
			.collect(ParetoSet.toParetoSet(
				EvolutionResult::getBestFitness, Point2::dominance));
		*/

		/*
		final ParetoSet<double[]> gt = engine.stream()
			.limit(1000)
			.collect(ParetoSet.toParetoSet(
				MOEATest::ff,
				(double[] a, double[] b) -> f(a).dominance(f(b))));
		*/
		//result.forEach(r -> System.out.println(r.x() + "\t" + r.y()));
	}

	static double[] ff(final EvolutionResult<DoubleGene, Point2> er) {
		return problem.codec().decode(er.getBestPhenotype().getGenotype());
	}

	/*
	public static
	Collector<EvolutionResult<DoubleGene, Point2>, ?, ParetoSet<Genotype<DoubleGene>>>
	toParetoSet() {
		return Collector.of(
			() -> new ParetoSet<>(Comparator.comparing(EvolutionResult::getBestPhenotype)),
			(set, result) -> set.add(result),
			ParetoSet::merge,
			set -> set.
		);
	}
	*/

}
