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

import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jenetics.Gene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
final class EvolutionSpliterator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Spliterator<EvolutionResult<G, C>>
{

	private final Supplier<EvolutionStart<G, C>> _initial;
	private final Function<? super EvolutionStart<G, C>, EvolutionResult<G, C>> _evolution;
	private final Predicate<? super EvolutionResult<G, C>> _proceed;
	private final int _level;
	private long _estimate;

	private final AtomicReference<EvolutionStart<G, C>> _next = new AtomicReference<>();

	EvolutionSpliterator(
		final Supplier<EvolutionStart<G, C>> initial,
		final Function<? super EvolutionStart<G, C>, EvolutionResult<G, C>> evolution,
		final Predicate<? super EvolutionResult<G, C>> proceed,
		final int level,
		final long estimate
	) {
		_evolution = requireNonNull(evolution);
		_initial = requireNonNull(initial);
		_proceed = requireNonNull(proceed);
		_level = level;
		_estimate = estimate;
	}

	EvolutionSpliterator(
		final Supplier<EvolutionStart<G, C>> initial,
		final Function<? super EvolutionStart<G, C>, EvolutionResult<G, C>> evolution,
		final Predicate<? super EvolutionResult<G, C>> proceed
	) {
		this(initial, evolution, proceed, 0, Long.MAX_VALUE);
	}

	int _count = 0;

	@Override
	public boolean
	tryAdvance(final Consumer<? super EvolutionResult<G, C>> action) {
		if (_next.get() == null) {
			_next.set(_initial.get());
		}
		System.out.println("tryAdvance: " + _level + ":" + ++_count);

		final EvolutionResult<G, C> result = _evolution.apply(_next.get());
		action.accept(result);
		_next.set(result.next());

		return _proceed.test(result);
	}

	@Override
	public Spliterator<EvolutionResult<G, C>> trySplit() {
		System.out.println("trySplit: " + _level);

		return _estimate > 0
			? new EvolutionSpliterator<>(
				this::next, _evolution, _proceed, _level + 1, _estimate >>>= 1)
			: null;
	}

	private EvolutionStart<G, C> next() {
		final EvolutionStart<G, C> next = _next.get();
		return next != null ? next : _initial.get();
	}

	@Override
	public long estimateSize() {
		return _estimate;
	}

	@Override
	public int characteristics() {
		return NONNULL | IMMUTABLE;
	}

}
