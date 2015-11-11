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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.internal.collection;

import static java.util.Objects.requireNonNull;

import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.4
 */
public final class ArraySpliterator<T> implements Spliterator<T> {

	private final Array<T> _array;
	private final int _fence;
	private int _index;

	public ArraySpliterator(
		final Array<T> array,
		final int origin,
		final int fence
	) {
		_array = requireNonNull(array);
		_index = origin;
		_fence = fence;
	}

	public ArraySpliterator(final Array<T> array) {
		this(array, 0, array.length());
	}

	@Override
	public void forEachRemaining(final Consumer<? super T> action) {
		requireNonNull(action);

		Array<T> array;
		int i;
		int hi;

		if ((array = _array).length() >= (hi = _fence) &&
			(i = _index) >= 0 && i < (_index = hi))
		{
			do {
				action.accept(array.get(i));
			} while (++i < hi);
		}
	}

	@Override
	public boolean tryAdvance(final Consumer<? super T> action) {
		if (_index >= 0 && _index < _fence) {
			action.accept(_array.get(_index++));
			return true;
		}
		return false;
	}

	@Override
	public Spliterator<T> trySplit() {
		final int lo = _index;
		final int mid = (lo + _fence) >>> 1;

		return (lo >= mid)
			? null
			: new ArraySpliterator<>(_array, lo, _index = mid);
	}

	@Override
	public long estimateSize() {
		return _fence - _index;
	}

	@Override
	public int characteristics() {
		return Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED;
	}

}
