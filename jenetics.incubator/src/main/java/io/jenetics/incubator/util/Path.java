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
package io.jenetics.incubator.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @param <T> the path element type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 6.0
 */
public interface Path<T> extends Iterable<Path<T>>, Comparable<Path<T>> {

	/**
	 * Return the path element of {@code this path}.
	 *
	 * @return the path element, or {@code null} if {@code this} path is empty
	 */
	T element();

	/**
	 * Returns the number of elements in the path.
	 *
	 * @return the number of elements in the path, or 0 if this path only
	 * represents a root component
	 */
	int count();

	/**
	 * Returns an element of this path. The index parameter is the index of
	 * the path element to return. The element that is closest to the root
	 * in the property hierarchy has index 0. The element that is farthest
	 * from the root has index count - 1.
	 *
	 * @param index the path index
	 * @return the path element
	 */
	Path<T> get(final int index);

	/**
	 * Return the path element of {@code this path}.
	 *
	 * @return the path element, or {@code null} if {@code this} path is empty
	 */
	default boolean isEmpty() {
		return count() == 0;
	}

	/**
	 * Returns the <em>parent path</em>, or {@code null} if this
	 * path has no parent.
	 *
	 * @return a path representing the path's parent, or {@code null} if
	 *         {@code this} path has no parent
	 */
	default Path<T> parent() {
		return count() > 1 ? subPath(0, count() - 1) : null;
	}

	/**
	 * Returns a relative {@code Path} that is a subsequence the element names
	 * of this path.
	 *
	 * @param fromIndex low endpoint (inclusive) of the subPath
	 * @param toIndex   high endpoint (exclusive) of the subPath
	 * @return a new {@code Path} object that is a subsequence of the
	 * elements in this {@code Path}
	 * @throws IndexOutOfBoundsException for an illegal endpoint index value
	 *        ({@code fromIndex < 0 || toIndex > size || fromIndex > toIndex})
	 */
	Path<T> subPath(final int fromIndex, final int toIndex);

	/**
	 * Appends the given {@code elements} to {@code this} and returns a new
	 * path object.
	 *
	 * @param elements the paths to append to {@code this} path
	 * @return a new path object
	 */
	@SuppressWarnings("unchecked")
	Path<T> append(final T... elements);

	@Override
	default Iterator<Path<T>> iterator() {
		return new Iterator<>() {
			private int cursor = 0;

			@Override
			public boolean hasNext() {
				return cursor != count();
			}

			@Override
			public Path<T> next() {
				final int i = cursor;
				if (cursor >= count()) {
					throw new NoSuchElementException();
				}

				cursor = i + 1;
				return get(i);
			}
		};
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	default int compareTo(final Path<T> other) {
		for (int i = 0, n = Math.min(count(), other.count()); i < n; ++i) {
			final var e1 = (Comparable)get(i).element();
			final var e2 = (Comparable)other.get(i).element();

			int cmp = e1.compareTo(e2);
			if (cmp != 0) {
				return cmp;
			}
		}

		return Integer.compare(count(), other.count());
	}

}
