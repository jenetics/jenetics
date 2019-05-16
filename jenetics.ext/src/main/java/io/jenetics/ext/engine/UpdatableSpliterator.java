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

import java.util.Spliterator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import io.jenetics.Gene;
import io.jenetics.engine.EvolutionResult;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class UpdatableSpliterator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements
		Spliterator<EvolutionResult<G, C>>,
		Updatable<Spliterator<EvolutionResult<G, C>>>
{

	private final ReadWriteLock _lock = new ReentrantReadWriteLock();

	private final UpdatableEngine<G, C> _engine;

	private Spliterator<EvolutionResult<G, C>> _current;

	private EvolutionResult<G, C> _result;

	UpdatableSpliterator(
		final UpdatableEngine<G, C> engine,
		final Spliterator<EvolutionResult<G, C>> spliterator
	) {
		_engine = requireNonNull(engine);
		_current = requireNonNull(spliterator);
	}

	@Override
	public boolean
	tryAdvance(final Consumer<? super EvolutionResult<G, C>> action) {
		requireNonNull(action);

		final boolean advance = spliterator().tryAdvance(element -> {
			action.accept(element);
			_result = element;
		});

		return advance;
	}

	@Override
	public Spliterator<EvolutionResult<G, C>> trySplit() {
		return new UpdatableSpliterator<>(_engine, _current);
	}

	@Override
	public long estimateSize() {
		return Long.MAX_VALUE;
	}

	@Override
	public int characteristics() {
		return Spliterator.ORDERED;
	}

	private Spliterator<EvolutionResult<G, C>> spliterator() {
		_lock.readLock().lock();
		try {
			final Spliterator<EvolutionResult<G, C>> result = _current;
			return result;
		} finally {
			_lock.readLock().unlock();
		}
	}

	@Override
	public void update(final Spliterator<EvolutionResult<G, C>> spliterator) {
		requireNonNull(spliterator);

		_lock.writeLock().lock();
		try {
			_current = spliterator;
		} finally {
			_lock.writeLock().unlock();
		}
	}

}
