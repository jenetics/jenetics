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
package org.jenetics.diagram;

import static java.util.Objects.requireNonNull;
import static org.jenetics.diagram.CandleStickPoint.toCandleStickPoint;
import static org.jenetics.engine.EvolutionResult.toBestEvolutionResult;
import static org.jenetics.engine.limit.bySteadyFitness;

import java.util.stream.IntStream;

import org.jenetics.Gene;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz  Wilhelmstötter</a>
 */
public class SteadyFitnessTermination<G extends Gene<?, G>> {

	private final Engine<G, Double> _engine;

	public SteadyFitnessTermination(final Engine<G, Double> engine) {
		_engine = requireNonNull(engine);
	}

	CandleStickPoint eval(final int generation, final int times) {
		return IntStream.range(0, times).parallel()
			.mapToObj(i -> toResult(generation))
			.collect(toCandleStickPoint());
	}

	private IntDoublePair toResult(final int generation) {
		final EvolutionResult<G, Double> result = _engine.stream()
			.limit(bySteadyFitness(generation))
			.collect(toBestEvolutionResult());

		return IntDoublePair.of(
			(int)result.getTotalGenerations(),
			result.getBestFitness()
		);
	}

}
