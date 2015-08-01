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
public final class ArrayProxy<T> {

	private volatile Array<T> _array;
	private boolean _sealed;

	private ArrayProxy(final Array<T> array, final boolean sealed) {
		_array = array;
		_sealed = sealed;
	}

	public ArrayProxy(final Array<T> array) {
		this(array, false);
	}

	public void set(final int index, final T value) {
		assert !_sealed : "Must not be called on sealed proxies";
		_array.copySealedProxyArrays();
		_array.set(index, value);
	}

	public T get(final int index) {
		return _array.get(index);
	}

	public ArrayProxy<T> copy() {
		return new ArrayProxy<>(_array.copy());
	}

	public ArrayProxy<T> slice(final int from, final int until) {
		final ArrayProxy<T> slice = new ArrayProxy<>(
			_array.slice(from, until),
			_sealed
		);
		_array.add(slice);

		return slice;
	}

	public final ArrayProxy<T> seal() {
		assert !_sealed : "Must not be called on sealed proxies";

		final ArrayProxy<T> proxy = new ArrayProxy<>(_array, true);
		_array.add(proxy);

		return proxy;
	}

	boolean isSealed() {
		return _sealed;
	}

	void lazyArrayCopy() {
		assert _sealed : "Must only be called on sealed proxies";
		_array = _array.copy();
		_sealed = false;
	}

}
