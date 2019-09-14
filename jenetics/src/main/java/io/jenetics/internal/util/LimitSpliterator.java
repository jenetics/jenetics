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
package io.jenetics.internal.util;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Extends the {@link Spliterator} interface by an additional {@code proceed}
 * predicate.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 4.1
 */
public final class LimitSpliterator<T> implements Spliterator<T> {

	private final Spliterator<T> _spliterator;
	private final Predicate<? super T> _proceed;

	private boolean _limited = false;

	private LimitSpliterator(
		final Spliterator<T> spliterator,
		final Predicate<? super T> proceed
	) {
		_spliterator = requireNonNull(spliterator);
		_proceed = requireNonNull(proceed);
	}

	@Override
	public boolean tryAdvance(final Consumer<? super T> action) {
		final boolean hasNext = _spliterator.tryAdvance(element -> {
			if (_proceed.test(element)) {
				action.accept(element);
			} else {
				_limited = true;
			}
		});

		return hasNext && !_limited;
	}

	@Override
	public Spliterator<T> trySplit() {
		final Spliterator<T> split = _spliterator.trySplit();
		return split == null ? null : new LimitSpliterator<>(split, _proceed);
	}

	@Override
	public long estimateSize() {
		return 0;
	}

	@Override
	public int characteristics() {
		return _spliterator.characteristics() & ~SIZED & ~SUBSIZED;
	}

	@Override
	public Comparator<? super T> getComparator() {
		return _spliterator.getComparator();
	}


	public static <T> LimitSpliterator<T> of(
		final Spliterator<T> spliterator,
		final Predicate<? super T> proceed
	) {
		return new LimitSpliterator<T>(spliterator, proceed);
	}

}
