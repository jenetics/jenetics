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

import io.jenetics.DoubleGene;
import io.jenetics.MeanAlterer;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.Limits;
import io.jenetics.engine.Problem;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

import io.jenetics.ext.moea.MOEA;
import io.jenetics.ext.moea.UFTournamentSelector;
import io.jenetics.ext.moea.Point2;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class MOEATest {

	private static final Problem<double[], DoubleGene, Point2> PROBLEM = Problem.of(
		v -> Point2.of(v[0]*cos(v[1]), v[0]*sin(v[1])),
		Codecs.ofVector(
			DoubleRange.of(0, 1),
			DoubleRange.of(0, 2*PI)
		)
	);

	public static void main(final String[] args) {
		final Engine<DoubleGene, Point2> engine = Engine.builder(PROBLEM)
			.alterers(
				new Mutator<>(0.1),
				new MeanAlterer<>())
			//.selector(new TournamentSelector<>(2))
			.selector(UFTournamentSelector.of())
			.build();

		final ISeq<Phenotype<DoubleGene, Point2>> result = engine.stream()
			.limit(Limits.byFixedGeneration(50))
			.collect(MOEA.toParetoSet(IntRange.of(30, 50)));

		result.forEach(r -> System.out.println(r.getFitness().x() + ", " + r.getFitness().y()));
	}

}
