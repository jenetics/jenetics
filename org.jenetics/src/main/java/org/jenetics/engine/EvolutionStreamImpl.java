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
 * @version 3.0
 */
final class EvolutionStreamImpl<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends StreamProxy<EvolutionResult<G, C>>
	implements EvolutionStream<G, C>
{

	private final Supplier<EvolutionStart<G, C>> _start;
	private final Function<? super EvolutionStart<G, C>, EvolutionResult<G, C>> _evolution;
	private final Predicate<? super EvolutionResult<G, C>> _proceed;

	private EvolutionStreamImpl(
		final Supplier<EvolutionStart<G, C>> start,
		final Function<? super EvolutionStart<G, C>, EvolutionResult<G, C>> evolution,
		final Stream<EvolutionResult<G, C>> stream,
		final Predicate<? super EvolutionResult<G, C>> proceed
	) {
		super(stream);
		_evolution = requireNonNull(evolution);
		_start = requireNonNull(start);
		_proceed = requireNonNull(proceed);
	}

	EvolutionStreamImpl(
		final Supplier<EvolutionStart<G, C>> start,
		final Function<? super EvolutionStart<G, C>, EvolutionResult<G, C>> evolution
	) {
		this(
			start, evolution,
			StreamSupport.stream(
				new EvolutionSpliterator<>(start, evolution, TRUE()),
				false
			),
			TRUE()
		);
	}

	@Override
	public EvolutionStream<G, C>
	limit(final Predicate<? super EvolutionResult<G, C>> proceed) {
		final Predicate<? super EvolutionResult<G, C>> prcd =
			_proceed == TRUE ? proceed : r -> proceed.test(r) & _proceed.test(r);

		return new EvolutionStreamImpl<>(
			_start,
			_evolution,
			StreamSupport.stream(
				new EvolutionSpliterator<>(_start, _evolution, prcd),
				false
			),
			prcd
		);
	}

	private static final Predicate<?> TRUE = a -> true;

	@SuppressWarnings("unchecked")
	private static <T> Predicate<T> TRUE() {
		return (Predicate<T>)TRUE;
	}

}
