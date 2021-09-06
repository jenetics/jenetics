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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 4.1
 */
public class CyclicSpliterator<T> implements Spliterator<T> {

	private final List<Supplier<Spliterator<T>>> _spliterators;

	private ConcatSpliterator<T> _concat = null;

	public CyclicSpliterator(final List<Supplier<Spliterator<T>>> spliterators) {
		spliterators.forEach(Objects::requireNonNull);
		_spliterators = new ArrayList<>(spliterators);
	}

	@Override
	public boolean tryAdvance(final Consumer<? super T> action) {
		boolean advance = true;
		if (_spliterators.isEmpty()) {
			advance = false;
		} else {
			if (!spliterator().tryAdvance(action)) {
				_concat = null;
			}
		}

		return advance;
	}

	@Override
	public Spliterator<T> trySplit() {
		return new CyclicSpliterator<>(_spliterators);
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
			_concat = new ConcatSpliterator<>(
				_spliterators.stream()
					.map(Supplier::get)
					.toList()
			);
		}

		return _concat;
	}

}
