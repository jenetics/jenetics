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
public final class StorageRef<T> implements Storage<T> {

	private Storage<T> _value;
	private boolean _sealed;

	private StorageRef(final Storage<T> value, final boolean sealed) {
		_value = value;
		_sealed = sealed;
	}

	public StorageRef<T> seal() {
		assert !_sealed : "Must not be called on sealed proxies";
		_sealed = true;
		return new StorageRef<>(_value, true);
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
	public Storage<T> copy(final int from, final int until) {
		return _value.copy(from, until);
	}

	public static <T> StorageRef<T> of(final Storage<T> value) {
		return new StorageRef<>(value, false);
	}
}
