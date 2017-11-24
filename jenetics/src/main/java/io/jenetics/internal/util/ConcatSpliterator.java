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
import static io.jenetics.internal.util.LimitSpliterator.TRUE;
import static io.jenetics.internal.util.LimitSpliterator.and;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This {@code Spliterator} takes a list of other spliterators which are
 * concatenated and a limiting predicate.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class ConcatSpliterator<T> implements LimitSpliterator<T> {

	private final Predicate<? super T> _proceed;
	private final Deque<Spliterator<T>> _spliterators;

	/**
	 * Create a new concatenating spliterator with the given arguments.
	 *
	 * @param proceed the limiting predicate
	 * @param spliterators the spliterators which are concatenated
	 * @throws NullPointerException if one of the arguments are {@code null}
	 */
	public ConcatSpliterator(
		final Predicate<? super T> proceed,
		final Collection<Spliterator<T>> spliterators
	) {
		_proceed = requireNonNull(proceed);
		spliterators.forEach(Objects::requireNonNull);
		_spliterators = new LinkedList<>(spliterators);
	}

	public ConcatSpliterator(final Collection<Spliterator<T>> spliterators) {
		this(TRUE(), spliterators);
	}

	@Override
	public boolean tryAdvance(final Consumer<? super T> action) {
		requireNonNull(action);

		if (!_spliterators.isEmpty()) {
			final Spliterator<T> spliterator = _spliterators.peek();
			final AtomicBoolean proceed = new AtomicBoolean();

			final boolean advance = spliterator.tryAdvance(t -> {
				action.accept(t);
				proceed.set(_proceed.test(t));
			}) && proceed.get();

			if (!advance) {
				_spliterators.removeFirst();
				return !_spliterators.isEmpty();
			}

			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Spliterator<T> trySplit() {
		final List<Spliterator<T>> split = _spliterators.stream()
			.map(Spliterator::trySplit)
			.collect(Collectors.toList());

		return split.stream().noneMatch(Objects::isNull)
			? new ConcatSpliterator<>(_proceed, split)
			: null;
	}

	@Override
	public ConcatSpliterator<T> limit(final Predicate<? super T> proceed) {
		return new ConcatSpliterator<>(and(_proceed, proceed), _spliterators);
	}

	@Override
	public long estimateSize() {
		final boolean maxValueSized = _spliterators.stream()
			.mapToLong(Spliterator::estimateSize)
			.anyMatch(l -> l == Long.MAX_VALUE);

		return maxValueSized
			? Long.MAX_VALUE
			: _spliterators.stream()
				.mapToLong(Spliterator::estimateSize)
				.min()
				.orElse(1L)*_spliterators.size();
	}

	@Override
	public int characteristics() {
		return _spliterators.stream()
			.mapToInt(Spliterator::characteristics)
			.reduce(0xFFFFFFFF, (i1, i2) -> i1 & i2)
			& ~Spliterator.SORTED;
	}

}
