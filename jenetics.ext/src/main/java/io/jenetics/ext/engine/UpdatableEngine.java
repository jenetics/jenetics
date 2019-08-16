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

import io.jenetics.Gene;
import io.jenetics.engine.Evolution;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStart;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class UpdatableEngine<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Evolution<G, C>
{

	private final Object _lock = new Object();

	private Evolution<G, C> _evolution;
	private boolean _reset;

	public UpdatableEngine(final Evolution<G, C> evolution) {
		_evolution = requireNonNull(evolution);
		_reset = false;
	}

	@Override
	public EvolutionResult<G, C> evolve(final EvolutionStart<G, C> start) {
		final Evolution<G, C> evolution;
		final boolean reset;
		synchronized (_lock) {
			evolution = _evolution;
			reset = _reset;
			_reset = false;
		}

		return evolution.evolve(reset ? start.withoutFitness() : start);
	}

	public void update(final Evolution<G, C> evolution) {
		synchronized (_lock) {
			_evolution = requireNonNull(evolution);
			_reset = true;
		}
	}

}
