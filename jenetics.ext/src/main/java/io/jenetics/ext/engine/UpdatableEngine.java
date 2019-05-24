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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import io.jenetics.Gene;
import io.jenetics.engine.EvolutionInit;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStart;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.EvolutionStreamable;
import io.jenetics.internal.engine.EvolutionStreamImpl;

import io.jenetics.ext.internal.UpdatableSpliterator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class UpdatableEngine<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements
		EvolutionStreamable<G, C>,
		Updatable<EvolutionStreamable<G, C>>
{

	private final Lock _lock = new ReentrantLock();

	private final List<WeakReference<UpdatableSpliterator<EvolutionResult<G, C>>>>
	_updatables = new ArrayList<>();

	private EvolutionStreamable<G, C> _engine;

	public UpdatableEngine(final EvolutionStreamable<G, C> engine) {
		_engine = requireNonNull(engine);
	}

	@Override
	public EvolutionStream<G, C>
	stream(final Supplier<EvolutionStart<G, C>> start) {
		_lock.lock();
		try {
			return stream(spliterator(start));
		} finally {
			_lock.unlock();
		}
	}

	private EvolutionStream<G, C>
	stream(UpdatableSpliterator<EvolutionResult<G, C>> spliterator) {
		_updatables.add(new WeakReference<>(spliterator));
		return new EvolutionStreamImpl<>(spliterator, false);
	}

	private UpdatableSpliterator<EvolutionResult<G, C>>
	spliterator(final Supplier<EvolutionStart<G, C>> start) {
		return new UpdatableSpliterator<>(
			_engine.stream(start).spliterator(),
			this::invalidate
		);
	}

	private EvolutionResult<G, C> invalidate(final EvolutionResult<G, C> result) {
		return result;
	}

	@Override
	public EvolutionStream<G, C> stream(final EvolutionInit<G> init) {
		_lock.lock();
		try {
			return stream(spliterator(init));
		} finally {
			_lock.unlock();
		}
	}

	private UpdatableSpliterator<EvolutionResult<G, C>>
	spliterator(final EvolutionInit<G> init) {
		return new UpdatableSpliterator<>(
			_engine.stream(init).spliterator(),
			this::invalidate
		);
	}

	@Override
	public void update(final EvolutionStreamable<G, C> engine) {
		requireNonNull(engine);

		_lock.lock();
		try {
			_engine = engine;

			final Iterator<WeakReference<UpdatableSpliterator<EvolutionResult<G, C>>>>
				it = _updatables.iterator();

			while (it.hasNext()) {
				final UpdatableSpliterator<EvolutionResult<G, C>>
					spliterator = it.next().get();

				if (spliterator != null) {
					spliterator.update(_engine.stream().spliterator());
				}
			}
		} finally {
			_lock.unlock();
		}
	}

}
