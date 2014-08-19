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

import static java.util.Objects.requireNonNull;

import org.jenetics.Gene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-19 $</em>
 */
public class EvolutionResult<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{
	private final EvolutionDurations _durations;
	private final EvolutionStart<G, C> _state;

	private EvolutionResult(
		final EvolutionDurations durations,
		final EvolutionStart<G, C> state
	) {
		_durations = requireNonNull(durations);
		_state = requireNonNull(state);
	}

	public EvolutionDurations getDurations() {
		return _durations;
	}

	public EvolutionStart<G, C> getState() {
		return _state;
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionResult<G, C> of(
		final EvolutionDurations durations,
		final EvolutionStart<G, C> state
	) {
		return new EvolutionResult<>(durations, state);
	}

}
