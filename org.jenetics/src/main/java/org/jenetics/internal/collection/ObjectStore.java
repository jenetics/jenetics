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

import org.jenetics.internal.collection.Array.Store;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ObjectStore<T> implements Store<T> {

	private final Object[] _array;

	private ObjectStore(final Object[] array) {
		_array = requireNonNull(array);
	}

	@Override
	public void set(final int index, T value) {
		_array[index] = value;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(final int index) {
		return (T)_array[index];
	}

	@Override
	public ObjectStore<T> copy(final int from, final int until) {
		final Object[] array = new Object[until - from];
		System.arraycopy(_array, from, array, 0, until - from);

		return new ObjectStore<>(array);
	}

	@Override
	public int length() {
		return _array.length;
	}

	public static <T> ObjectStore<T> of(final Object[] array) {
		return new ObjectStore<>(array);
	}

	public static <T> ObjectStore<T> ofLength(final int length) {
		return new ObjectStore<>(new Object[length]);
	}

}
