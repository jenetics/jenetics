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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.DoubleGene;
import io.jenetics.MeanAlterer;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.Problem;
import io.jenetics.util.Accumulator;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class MOEATest {

	@Test(invocationCount = 5, successPercentage = 80)
	public void collect() {
		final Problem<double[], DoubleGene, Vec<double[]>> problem = Problem.of(
			v -> Vec.of(v[0]*cos(v[1]), v[0]*sin(v[1])),
			Codecs.ofVector(
				DoubleRange.of(0, 1),
				DoubleRange.of(0, 2*PI)
			)
		);

		final Engine<DoubleGene, Vec<double[]>> engine = Engine.builder(problem)
			.alterers(
				new Mutator<>(0.1),
				new MeanAlterer<>())
			.offspringSelector(new TournamentSelector<>(2))
			.survivorsSelector(UFTournamentSelector.ofVec())
			.minimizing()
			.build();

		final List<Vec<double[]>> pop = new ArrayList<>();

		final var accu = Accumulator.of(MOEA.<DoubleGene, double[], Vec<double[]>>toParetoSet(IntRange.of(20, 25)));

		final ISeq<Vec<double[]>> result = engine.stream()
			.limit(100)
			.peek(accu)
			.peek(er -> pop.addAll(er.population()
				.map(Phenotype::fitness).asList()))
			.collect(MOEA.toParetoSet(IntRange.of(20, 25)))
			.map(Phenotype::fitness);

		Assert.assertTrue(result.size() >= 10, "Expected >= 10, got " + result.size());
		Assert.assertTrue(result.size() <= 50, "Expected <= 50, got " + result.size());
		Assert.assertEquals(accu.result().size(), result.size());
		Assert.assertEquals(accu.result().map(Phenotype::fitness), result);

		final Set<Vec<double[]>> front = new HashSet<>(
			Pareto
				.front(ISeq.of(pop), Comparator.reverseOrder())
				.asList()
		);

		final Set<Vec<double[]>> missing = result.stream()
			.filter(r -> !front.contains(r))
			.collect(Collectors.toSet());

		Assert.assertTrue(missing.size() < 10, "Expected < 10, got " + missing.size());
	}

}
