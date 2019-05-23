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
package io.jenetics.ext.internal;

import static java.util.Objects.requireNonNull;

import java.util.Spliterator;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class UpdatableSpliterator<T> implements Spliterator<T> {

	private final Lock _lock = new ReentrantLock();
	private final Condition _available = _lock.newCondition();

	private Spliterator<T> _spliterator;
	private boolean _updated = false;

	private final Function<? super T, ? extends T> _updater;

	public UpdatableSpliterator(
		final Spliterator<T> spliterator,
		final Function<? super T, ? extends T> updater
	) {
		_spliterator = spliterator;
		_updater = requireNonNull(updater);
	}

	public UpdatableSpliterator(final Function<? super T, ? extends T> updater) {
		this(null, updater);
	}

	@Override
	public boolean
	tryAdvance(final Consumer<? super T> action) {
		requireNonNull(action);

		final Spliterator<T> spliterator;
		final boolean updated;

		_lock.lock();
		try {
			while (_spliterator == null) {
				_available.await();
			}
			spliterator = _spliterator;
			updated = _updated;
			_updated = false;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		} finally {
			_lock.unlock();
		}

		return spliterator.tryAdvance(element -> {
			final T ele = updated ? _updater.apply(element) : element;
			action.accept(ele);
		});
	}

	@Override
	public Spliterator<T> trySplit() {
		return null;
	}

	@Override
	public long estimateSize() {
		return Long.MAX_VALUE;
	}

	@Override
	public int characteristics() {
		return Spliterator.ORDERED;
	}

	public void update(final Spliterator<T> spliterator) {
		requireNonNull(spliterator);

		_lock.lock();
		try {
			_spliterator = spliterator;
			_updated = true;
			_available.signal();
		} finally {
			_lock.unlock();
		}
	}

}
