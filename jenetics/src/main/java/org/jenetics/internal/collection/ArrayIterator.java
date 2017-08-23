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

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 3.4
 */
public class ArrayIterator<T> implements ListIterator<T> {

	public final Array<T> array;

	protected int cursor = 0;
	protected int lastElement = -1;

	public ArrayIterator(final Array<T> array) {
		this.array = requireNonNull(array, "Array must not be null.");
	}

	@Override
	public boolean hasNext() {
		return cursor != array.length();
	}

	@Override
	public T next() {
		final int i = cursor;
		if (cursor >= array.length()) {
			throw new NoSuchElementException();
		}

		cursor = i + 1;
		return array.get(lastElement = i);
	}

	@Override
	public int nextIndex() {
		return cursor;
	}

	@Override
	public boolean hasPrevious() {
		return cursor != 0;
	}

	@Override
	public T previous() {
		final int i = cursor - 1;
		if (i < 0) {
			throw new NoSuchElementException();
		}

		cursor = i;
		return array.get(lastElement = i);
	}

	@Override
	public int previousIndex() {
		return cursor - 1;
	}

	@Override
	public void set(final T value) {
		throw new UnsupportedOperationException(
			"Iterator is immutable."
		);
	}

	@Override
	public void add(final T value) {
		throw new UnsupportedOperationException(
			"Can't change Iterator size."
		);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
			"Can't change Iterator size."
		);
	}

}
