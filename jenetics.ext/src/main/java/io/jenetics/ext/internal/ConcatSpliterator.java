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

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This {@code Spliterator} takes a list of other spliterators which are
 * concatenated and a limiting predicate.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.3
 * @since 4.1
 */
public class ConcatSpliterator<T> implements Spliterator<T> {

	private final Deque<Spliterator<T>> _spliterators;
	private final int _characteristics;
	private final long _size;

	/**
	 * Create a new concatenating spliterator with the given arguments.
	 *
	 * @param spliterators the spliterators which are concatenated
	 * @throws NullPointerException if one of the arguments are {@code null}
	 */
	public ConcatSpliterator(final Collection<Spliterator<T>> spliterators) {
		_spliterators = new LinkedList<>(spliterators);


		int characteristics = (ORDERED | SIZED | SUBSIZED);
		long size = 0;
		for (var spliterator : spliterators) {
			characteristics &= spliterator.characteristics();
			size += spliterator.estimateSize();
		}
		if (size < 0) {
			size = Long.MAX_VALUE;
			characteristics &= (~SIZED) & (~SUBSIZED);
		}

		_characteristics = characteristics;
		_size = size;
	}

	@Override
	public boolean tryAdvance(final Consumer<? super T> action) {
		boolean advance = true;
		if (!_spliterators.isEmpty()) {
			final Spliterator<T> spliterator = _spliterators.peek();
			assert spliterator != null;

			if (!spliterator.tryAdvance(action)) {
				_spliterators.removeFirst();
				advance = !_spliterators.isEmpty();
			}
		} else {
			advance = false;
		}

		return advance;
	}

	@Override
	public Spliterator<T> trySplit() {
		final List<Spliterator<T>> split = _spliterators.stream()
			.map(Spliterator::trySplit)
			.collect(Collectors.toList());

		return split.stream().noneMatch(Objects::isNull)
			? new ConcatSpliterator<>(split)
			: null;
	}

	@Override
	public long estimateSize() {
		return _size;
	}

	@Override
	public int characteristics() {
		return _characteristics;
	}

}
