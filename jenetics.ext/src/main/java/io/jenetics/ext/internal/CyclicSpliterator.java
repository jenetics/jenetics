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
import static io.jenetics.internal.util.LimitSpliterator.TRUE;
import static io.jenetics.internal.util.LimitSpliterator.and;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.jenetics.internal.util.LimitSpliterator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class CyclicSpliterator<T> implements LimitSpliterator<T> {

	private final Predicate<? super T> _proceed;
	private final List<Supplier<Spliterator<T>>> _spliterators;

	private ConcatSpliterator<T> _concat = null;

	public CyclicSpliterator(
		final Predicate<? super T> proceed,
		final List<Supplier<Spliterator<T>>> spliterators
	) {
		_proceed = requireNonNull(proceed);
		spliterators.forEach(Objects::requireNonNull);
		_spliterators = new ArrayList<>(spliterators);
	}

	public CyclicSpliterator(final List<Supplier<Spliterator<T>>> spliterators) {
		this(TRUE(), spliterators);
	}

	@Override
	public LimitSpliterator<T> limit(final Predicate<? super T> proceed) {
		return new CyclicSpliterator<>(and(_proceed, proceed), _spliterators);
	}

	@Override
	public boolean tryAdvance(final Consumer<? super T> action) {
		requireNonNull(action);

		if (_spliterators.isEmpty()) {
			return false;
		}

		final AtomicBoolean proceed = new AtomicBoolean(true);
		final boolean advance = spliterator().tryAdvance(t -> {
			proceed.set(_proceed.test(t));
			action.accept(t);
		});

		if (!advance) {
			_concat = null;
		}

		return proceed.get();
	}

	@Override
	public Spliterator<T> trySplit() {
		return new CyclicSpliterator<>(_proceed, _spliterators);
	}

	@Override
	public long estimateSize() {
		return Long.MAX_VALUE;
	}

	@Override
	public int characteristics() {
		return Spliterator.ORDERED;
	}

	private ConcatSpliterator<T> spliterator() {
		if (_concat == null) {
			_concat = new ConcatSpliterator<T>(
				_spliterators.stream()
					.map(Supplier::get)
					.collect(Collectors.toList())
			);
		}

		return _concat;
	}

}
