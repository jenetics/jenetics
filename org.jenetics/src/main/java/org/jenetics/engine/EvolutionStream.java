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
package org.jenetics.engine;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jenetics.Gene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public class EvolutionStream<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends StreamProxy<EvolutionResult<G, C>>
{

	private final Function<EvolutionStart<G, C>, EvolutionResult<G, C>> _evolution;
	private final Supplier<EvolutionStart<G, C>> _initial;

	EvolutionStream(
		final Function<EvolutionStart<G, C>, EvolutionResult<G, C>> evolution,
		final Supplier<EvolutionStart<G, C>> initial,
		final Stream<EvolutionResult<G, C>> stream
	) {
		super(stream);
		_evolution = requireNonNull(evolution);
		_initial = requireNonNull(initial);
	}

	EvolutionStream(
		final Function<EvolutionStart<G, C>, EvolutionResult<G, C>> evolution,
		final Supplier<EvolutionStart<G, C>> initial
	) {
		this(
			evolution,
			initial,
			StreamSupport.stream(
				new UnlimitedEvolutionSpliterator<>(
					evolution,
					initial
				),
				false
			)
		);
	}

	public Stream<EvolutionResult<G, C>>
	limit(final Predicate<EvolutionResult<G, C>> terminate) {
		return new EvolutionStream<G, C>(
			_evolution,
			_initial,
			StreamSupport.stream(
				new TerminatingEvolutionSpliterator<>(
					_evolution,
					_initial,
					terminate
				),
				false
			)
		);
	}

}
