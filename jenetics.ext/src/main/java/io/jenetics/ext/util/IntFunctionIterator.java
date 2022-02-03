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
package io.jenetics.ext.util;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.IntFunction;

/**
 * Helper class for iterating through given int range
 *
 * @param <T> the mapping data type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 6.0
 */
final class IntFunctionIterator<T> implements Iterator<T> {
	private final IntFunction<? extends T> _mapper;
	private final int _length;

	private int _cursor = 0;

	IntFunctionIterator(final IntFunction<? extends T> mapper, final int length) {
		_mapper = requireNonNull(mapper);
		_length = length;
	}

	@Override
	public boolean hasNext() {
		return _cursor != _length;
	}

	@Override
	public T next() {
		final int i = _cursor;
		if (_cursor >= _length) {
			throw new NoSuchElementException();
		}

		_cursor = i + 1;
		return _mapper.apply(i);
	}
}
