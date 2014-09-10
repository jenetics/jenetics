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
package org.jenetics.internal.engine;

import java.util.Comparator;

import org.jenetics.Alterer;
import org.jenetics.DoubleGene;
import org.jenetics.Gene;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutator;
import org.jenetics.Optimize;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-09-10 $</em>
 */
public class EvolutionStream {

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Comparator<EvolutionResult<G, C>> best(final Optimize opt) {
		return null;
	}

	public static void main(final String[] args) {
		final Engine<DoubleGene, Double> engine = Engine
			.newBuilder(a -> a, 0.0, 1.0)
			.alterer(new Mutator<>(0.3))
			.build();

//		double best = engine.stream(100)
//			.flatMap(r -> r.getPopulation().stream().map(Phenotype::getFitness))
//			.reduce(BinaryOperator.maxBy(engine.getOptimize().ascending()))
//			.orElse(0.0);

//		final double best = engine.stream(100)
//			.mapToDouble(EvolutionResult::getBestFitness)
//			.max().orElse(0.0);

//		final double best = engine.stream(10)
//			.collect(engine.best())
//			.getBestFitness();


		final double best = engine.stream().limit(300)
			.peek(r -> System.out.println(r.getBestPhenotype().getGenotype()))
			.collect(engine.BestGenotype)
			.getGene().doubleValue();

//		final double best = engine.stream(105)
//			.max(EvolutionResult::compareTo)
//			.map(EvolutionResult::getBestPhenotype)
//			.map(r -> r.getGenotype().getGene().getAllele())
//			.orElse(0.0);

//		final double best = engine.stream(100)
//			.max(best(engine.getOptimize()))
//			.map(v -> v.getPopulation().stream().max)

		System.out.println("BEST: " + best);
	}

}
