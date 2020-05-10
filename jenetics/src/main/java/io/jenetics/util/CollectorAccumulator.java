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
import java.util.stream.Collector;

/**
 * @param <T> the type of input elements to the accumulate operation
 * @param <A> the accumulator type
 * @param <R> the result type of the accumulated operation
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class CollectorAccumulator<T, A, R>
	implements Accumulator<T, CollectorAccumulator<T, A, R>, R>
{

	private final Collector<T, A, R> _collector;
	private A _collection;

	CollectorAccumulator(final Collector<T, A, R> collector) {
		_collector = requireNonNull(collector);
	}

	@Override
	public CollectorAccumulator<T, A, R>
	combine(final CollectorAccumulator<T, A, R> other) {
		if (_collection == null) {
			_collection = _collector.supplier().get();
		}

		_collector.combiner().apply(_collection, other._collection);
		return this;
	}

	@Override
	public void accept(final T value) {
		if (_collection == null) {
			_collection = _collector.supplier().get();
		}
		_collector.accumulator().accept(_collection, value);
	}

	@Override
	public R result() {
		return _collector.finisher().apply(_collection);
	}

	@Override
	public Supplier<CollectorAccumulator<T, A, R>> supplier() {
		return () -> new CollectorAccumulator<>(_collector);
	}

	@Override
	public Set<Characteristics> characteristics() {
		return _collector.characteristics();
	}

}
