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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Slice<T> {

	private final Storage<T> _storage;
	private final int _start;
	private final int _length;

	private Slice(final Storage<T> storage, final int from, final int until) {
		_storage = storage;
		_start = from;
		_length = until - from;
	}

	public void set(final int index, final T value) {
		_storage.set(index + _start, value);
	}

	public T get(final int index) {
		return _storage.get(index + _start);
	}

	public int length() {
		return _length;
	}

	public Storage<T> copy() {
		return _storage.copy(_start, _start + _length);
	}

	public Slice<T> slice(final int from, final int until) {
		return of(_storage, from + _start, _start + until - from);
	}

	public static <T> Slice<T> of(
		final Storage<T> storage,
		final int from,
		final int until
	) {
		return new Slice<>(storage, from, until);
	}

}
