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

import java.lang.ref.WeakReference;

import org.jenetics.internal.collection.Stack;

import org.jenetics.util.Copyable;

/**
 * @param <T>
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Array<T> {

	private volatile Store<T> _store;
	private boolean _sealed;

	private Array(final Store<T> store, final boolean sealed) {
		_store = requireNonNull(store);
		_sealed = sealed;
	}

	public Array(final Store<T> store) {
		this(store, false);
	}

	public void set(final int index, final T value) {
		assert !_sealed : "Must not be called on sealed proxies";
		_store.copySealedArrays();
		_store.set(index, value);
	}

	public T get(final int index) {
		return _store.get(index);
	}

	public int length() {
		return _store.length();
	}

	public Array<T> copy() {
		return new Array<>(_store.copy());
	}

	public Array<T> slice(final int from, final int until) {
		final Array<T> slice = new Array<>(
			_store.slice(from, until),
			_sealed
		);
		_store.add(slice);

		return slice;
	}

	public final Array<T> seal() {
		assert !_sealed : "Must not be called on sealed proxies";

		final Array<T> proxy = new Array<>(_store, true);
		_store.add(proxy);

		return proxy;
	}

	public void checkIndex(final int index) {
		if (index < 0 || index >= length()) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Index %s is out of bounds [0, %s)", index, length()
			));
		}
	}

	private void lazyArrayCopy() {
		assert _sealed : "Must only be called on sealed proxies";
		_store = _store.copy();
		_sealed = false;
	}

	/**
	 * @param <T>
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version !__version__!
	 * @since !__version__!
	 */
	public static abstract class Store<T> implements Copyable<Store<T>> {

		private final Stack<WeakReference<Array<T>>> _proxies = new Stack<>();
		private boolean _hasProxies = false;

		protected Store() {
		}

		private void add(final Array<T> array) {
			if (array._sealed) {
				_proxies.push(new WeakReference<>(array));
				_hasProxies = true;
			}
		}

		private void copySealedArrays() {
			if (_hasProxies) {
				_proxies.popAll(reference -> {
					final Array<T> proxy = reference.get();
					if (proxy != null) {
						proxy.lazyArrayCopy();
					}
				});
			}
		}

		public abstract void set(final int index, final T value);

		public abstract T get(final int index);

		public abstract Store<T> slice(final int from, final int until);

		public abstract int length();

	}

}
