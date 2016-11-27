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
package org.jenetics.util;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 2.0
 * @since 3.0
 */
final class Context<T> {

	private final T _default;
	private final AtomicReference<Entry<T>> _entry;
	private final ThreadLocal<Entry<T>> _threadLocalEntry = new ThreadLocal<>();

	Context(final T defaultValue) {
		_default = defaultValue;
		_entry = new AtomicReference<>(new Entry<>(defaultValue));
	}

	void set(final T value) {
		final Entry<T> e = _threadLocalEntry.get();

		if (e != null) e.value = value;
		else _entry.set(new Entry<>(value));
	}

	T get() {
		final Entry<T> e = _threadLocalEntry.get();
		return (e != null ? e : _entry.get()).value;
	}

	void reset() {
		set(_default);
	}

	<S extends T, R> R with(final S value, final Function<S, R> f) {
		final Entry<T> e = _threadLocalEntry.get();
		if (e != null) {
			_threadLocalEntry.set(e.inner(value));
		} else {
			_threadLocalEntry.set(new Entry<>(value, Thread.currentThread()));
		}

		try {
			return f.apply(value);
		} finally {
			_threadLocalEntry.set(_threadLocalEntry.get().parent);
		}
	}

	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version 2.0
	 * @since 2.0
	 */
	private static final class Entry<T> {
		final Thread thread;
		final Entry<T> parent;
		T value;

		Entry(final T value, final Entry<T> parent, final Thread thread) {
			this.value = value;
			this.parent = parent;
			this.thread = thread;
		}

		Entry(final T value, final Thread thread) {
			this(value, null, thread);
		}

		Entry(final T value) {
			this(value, null, null);
		}

		Entry<T> inner(final T value) {
			assert thread == Thread.currentThread();
			return new Entry<>(value, this, thread);
		}

	}

}
