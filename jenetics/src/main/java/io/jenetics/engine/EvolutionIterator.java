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
package io.jenetics.engine;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;

import io.jenetics.Gene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
final class EvolutionIterator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Iterator<EvolutionResult<G, C>>
{

	private final Function<EvolutionStart<G, C>, EvolutionResult<G, C>> _evolution;
	private final Supplier<EvolutionStart<G, C>> _initial;

	private EvolutionStart<G, C> _start;

	EvolutionIterator(
		final Supplier<EvolutionStart<G, C>> initial,
		final Function<EvolutionStart<G, C>, EvolutionResult<G, C>> evolution
	) {
		_evolution = requireNonNull(evolution);
		_initial = requireNonNull(initial);
	}

	@Override
	public EvolutionResult<G, C> next() {
		if (_start == null) {
			_start = _initial.get();
		}

		final EvolutionResult<G, C> result = _evolution.apply(_start);
		_start = result.next();
		return result;
	}

	@Override
	public boolean hasNext() {
		return true;
	}

}
