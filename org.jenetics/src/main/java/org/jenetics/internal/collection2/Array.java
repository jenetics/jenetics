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
package org.jenetics.internal.collection2;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import org.jenetics.util.Copyable;

/**
 * @param <T>
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Array<T> {

	private final StorageRef<T> _store;
	private final int _start;
	private final int _length;

	private Array(final StorageRef<T> store, final int from, final int until) {
		_store = requireNonNull(store);
		_start = from;
		_length = until - from;
	}

	public Array(final Storage<T> store) {
		this(StorageRef.of(store), 0, store.length());
	}

	public final Array<T> seal() {
		return new Array<>(_store.seal(), _start, _length + _start);
	}

	public void set(final int index, final T value) {
		_store.set(index + _start, value);
	}

	public T get(final int index) {
		return _store.get(index + _start);
	}

	public int length() {
		return _length;
	}

	public Array<T> copy() {
		return new Array<>(_store.copy(_start, _length + _start));
	}

	public Array<T> slice(final int from, final int until) {
		return new Array<>(_store, from + _start, until + _start);
	}

	public void checkIndex(final int index) {
		if (index < 0 || index >= length()) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Index %s is out of bounds [0, %s)", index, length()
			));
		}
	}

	/**
	 * @param <T>
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version !__version__!
	 * @since !__version__!
	 */
	public static abstract class Store<T> implements Copyable<Store<T>> {

		public abstract void set(final int index, final T value);

		public abstract T get(final int index);

		public abstract Store<T> slice(final int from, final int until);

		public abstract int length();

	}

}
