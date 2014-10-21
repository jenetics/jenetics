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
 * @version 3.0 &mdash; <em>$Date: 2014-10-21 $</em>
 */
final class EvolutionStreamImpl<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends StreamProxy<EvolutionResult<G, C>>
	implements EvolutionStream<G, C>
{

	private final Function<EvolutionStart<G, C>, EvolutionResult<G, C>> _evolution;
	private final Supplier<EvolutionStart<G, C>> _initial;
	private final Predicate<? super EvolutionResult<G, C>> _proceed;

	private EvolutionStreamImpl(
		final Function<EvolutionStart<G, C>, EvolutionResult<G, C>> evolution,
		final Supplier<EvolutionStart<G, C>> initial,
		final Stream<EvolutionResult<G, C>> stream,
		final Predicate<? super EvolutionResult<G, C>> proceed
	) {
		super(stream);
		_evolution = requireNonNull(evolution);
		_initial = requireNonNull(initial);
		_proceed = requireNonNull(proceed);
	}

	EvolutionStreamImpl(
		final Function<EvolutionStart<G, C>, EvolutionResult<G, C>> evolution,
		final Supplier<EvolutionStart<G, C>> initial
	) {
		this(
			evolution,
			initial,
			StreamSupport.stream(
				new EvolutionSpliterator<>(evolution, initial, r -> true),
				false
			),
			r -> true
		);
	}

	@Override
	public EvolutionStream<G, C>
	limit(final Predicate<? super EvolutionResult<G, C>> proceed) {
		final Predicate<? super EvolutionResult<G, C>> prcd = r ->
			proceed.test(r) & _proceed.test(r);

		return new EvolutionStreamImpl<G, C>(
			_evolution,
			_initial,
			StreamSupport.stream(
				new EvolutionSpliterator<>(_evolution, _initial, prcd),
				false
			),
			prcd
		);
	}

}
