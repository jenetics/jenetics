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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * @param <T> the path element type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 6.0
 */
public interface Path<T> extends Iterable<Path<T>> {

	T value();

	int length();

	default boolean isEmpty() {
		return length() == 0;
	}

	default Optional<Path<T>> parent() {
		return isEmpty() ? Optional.empty() : Optional.of(path(length() - 1));
	}

	default Optional<Path<T>> root() {
		return isEmpty() ? Optional.empty() : Optional.of(path(0));
	}

	default Path<T> path(final int index) {
		return subPath(0, index + 1);
	}

	List<T> elements();

	Path<T> subPath(final int beginIndex, final int endIndex);

	default Path<T> subPath(final int beginIndex) {
		return subPath(beginIndex, length());
	}

	Path<T> append(final Path<T> path);

	default Path<T> prepend(final Path<T> path) {
		return path.append(this);
	}


	@SafeVarargs
	static <T> Path<T> of(final T... elements) {

		record PathElements<T>(List<T> values) implements Path<T> {

			@Override
			public T value() {
				if (!isEmpty()) {
					return values.get(values.size() - 1);
				} else {
					throw new NoSuchElementException("Empty path.");
				}
			}

			@Override
			public int length() {
				return values.size();
			}

			@Override
			public List<T> elements() {
				return Collections.unmodifiableList(values);
			}

			@Override
			public Path<T> subPath(final int beginIndex, final int endIndex) {
				return new PathElements<>(values.subList(beginIndex, endIndex));
			}

			@Override
			public Path<T> append(final Path<T> path) {
				final var elems = new ArrayList<T>(values.size() + path.length());
				elems.addAll(values);
				elems.addAll(path.elements());

				return new PathElements<>(elems);
			}

			@Override
			public Iterator<Path<T>> iterator() {
				return new Iterator<>() {
					private int _cursor = 0;

					@Override
					public boolean hasNext() {
						return _cursor != length();
					}

					@Override
					public Path<T> next() {
						final int i = _cursor;
						if (_cursor >= length()) {
							throw new NoSuchElementException();
						}

						_cursor = i + 1;
						return path(i);
					}
				};
			}

			@Override
			public String toString() {
				return String.join(
					"/",
					elements().stream().map(Objects::toString).toList()
				);
			}

		}

		return new PathElements<T>(List.of(elements));
	}

}
