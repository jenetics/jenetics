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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * @param <T>
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Array<T> {

	private final Store.Ref<T> _store;
	private final int _start;
	private final int _length;

	private Array(final Store.Ref<T> store, final int from, final int until) {
		_store = requireNonNull(store);
		_start = from;
		_length = until - from;
	}

	private Array(final Store<T> store) {
		this(Store.Ref.of(store), 0, store.length());
	}

	public final Array<T> seal() {
		return new Array<>(_store.seal(), _start, _length + _start);
	}

	public boolean isSealed() {
		return _store.isSealed();
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
	 * Check the given {@code from} and {@code until} indices.
	 *
	 * @param start the start index, inclusively.
	 * @param end the end index, exclusively.
	 * @throws java.lang.ArrayIndexOutOfBoundsException if the given index is
	 *         not in the valid range.
	 */
	public final void checkIndex(final int start, final int end) {
		if (start > end) {
			throw new ArrayIndexOutOfBoundsException(format(
				"fromIndex(%d) > toIndex(%d)", start, end
			));
		}
		if (start < 0 || end > length()) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Invalid index range: [%d, %s)", start, end
			));
		}
	}

	public static <T> Array<T> of(final Store<T> store) {
		return new Array<>(store);
	}

	public static <T> Array<T> ofLength(final int length) {
		return new Array<T>(ObjectStore.ofLength(length));
	}


	/**
	 * @param <T>
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version !__version__!
	 * @since !__version__!
	 */
	public interface Store<T> {

		public void set(final int index, final T value);

		public T get(final int index);

		public Store<T> copy(final int from, final int until);

		public int length();

		public default Store<T> copy(final int from) {
			return copy(from, length());
		}

		public default Store<T> copy() {
			return copy(0, length());
		}


		/**
		 * @param <T>
		 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
		 * @version !__version__!
		 * @since !__version__!
		 */
		public static final class Ref<T> implements Store<T> {

			private Store<T> _value;
			private boolean _sealed;

			private Ref(final Store<T> value, final boolean sealed) {
				_value = value;
				_sealed = sealed;
			}

			public Ref<T> seal() {
				_sealed = true;
				return new Ref<>(_value, true);
			}

			public boolean isSealed() {
				return _sealed;
			}

			@Override
			public void set(final int index, final T value) {
				if (_sealed) {
					_value = copy();
					_sealed = false;
				}
				_value.set(index, value);
			}

			@Override
			public T get(final int index) {
				return _value.get(index);
			}

			@Override
			public int length() {
				return _value.length();
			}

			@Override
			public Store<T> copy(final int from, final int until) {
				return _value.copy(from, until);
			}

			public static <T> Ref<T> of(final Store<T> value) {
				return new Ref<>(value, false);
			}
		}

	}

}
