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
package io.jenetics.ext.engine;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import io.jenetics.Alterer;
import io.jenetics.Gene;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStreamable;
import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.util.DoubleRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
final class FitnessVarianceAdaptiveEngine<
	G extends Gene<?, G>,
	N extends Number & Comparable<? super N>
>
	implements Function<EvolutionResult<G, N>, EvolutionStreamable<G, N>>
{

	private final DoubleRange _variance;
	private final Engine.Builder<G, N> _builder;
	private final Alterer<G, N> _narrow;
	private final Alterer<G, N> _enlarge;

	private Engine<G, N> _engine;
	private boolean _narrowing;

	FitnessVarianceAdaptiveEngine(
		final DoubleRange variance,
		final Engine.Builder<G, N> builder,
		final Alterer<G, N> narrow,
		final Alterer<G, N> enlarge
	) {
		_variance = requireNonNull(variance);
		_builder = requireNonNull(builder).copy();
		_narrow = requireNonNull(narrow);
		_enlarge = requireNonNull(enlarge);
	}

	@Override
	public EvolutionStreamable<G, N> apply(final EvolutionResult<G, N> result) {
		if (result == null || _engine == null) {
			_engine = _builder
				.alterers(_enlarge)
				.build();
			_narrowing = false;
		} else {
			final DoubleMomentStatistics stat = new DoubleMomentStatistics();

			result.getPopulation()
				.forEach(pt -> stat.accept(pt.getFitness().doubleValue()));

			if (stat.getVariance() < _variance.getMin() && _narrowing) {
				_engine = _builder
					.alterers(_enlarge)
					.build();
				_narrowing = false;
			} else if (stat.getVariance() > _variance.getMax() && !_narrowing) {
				_engine = _builder
					.alterers(_narrow)
					.build();
				_narrowing = true;
			}
		}

		assert _engine != null;
		return _engine;
	}

}
