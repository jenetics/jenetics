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
package io.jenetics.util;

import static java.util.Objects.requireNonNull;

import java.util.Set;
import java.util.function.Supplier;

/**
 * @param <T> the type of input elements to the accumulate operation
 * @param <A> the accumulator type
 * @param <R> the result type of the accumulated operation
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class SynchronizedAccumulator<T, A extends Accumulator<T, A, R>, R>
	implements Accumulator<T, SynchronizedAccumulator<T, A, R>, R>
{

	private final A _accumulator;
	private final Object _lock;

	SynchronizedAccumulator(final A accumulator, final Object lock) {
		_accumulator = requireNonNull(accumulator);
		_lock = requireNonNull(lock);
	}

	@Override
	public SynchronizedAccumulator<T, A, R>
	combine(final SynchronizedAccumulator<T, A, R> other) {
		synchronized (_lock) {
			_accumulator.combine(other._accumulator);
		}
		return this;
	}

	@Override
	public void accept(final T value) {
		synchronized (_lock) {
			_accumulator.accept(value);
		}
	}

	@Override
	public R result() {
		synchronized (_lock) {
			return _accumulator.result();
		}
	}

	@Override
	public Supplier<SynchronizedAccumulator<T, A, R>> supplier() {
		return () -> new SynchronizedAccumulator<>(
			_accumulator.supplier().get(),
			_lock
		);
	}

	@Override
	public Set<Characteristics> characteristics() {
		return _accumulator.characteristics();
	}

}
