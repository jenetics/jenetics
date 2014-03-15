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
package org.jenetics.internal.util;

import java.util.concurrent.atomic.AtomicReference;

import org.jenetics.util.Scoped;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 2.0 &mdash; <em>$Date: 2014-03-15 $</em>
 * @since 2.0
 */
public final class Context<T> {

	private final T _default;
	private final AtomicReference<Entry<T>> _entry;
	private final ThreadLocal<Entry<T>> _threadLocalEntry = new ThreadLocal<>();

	public Context(final T defaultValue) {
		_default = defaultValue;
		_entry = new AtomicReference<>(new Entry<>(defaultValue));
	}

	public void set(final T value) {
		final Entry<T> e = _threadLocalEntry.get();
		if (e != null) e.value = value; else _entry.set(new Entry<T>(value));
	}

	public T get() {
		final Entry<T> e = _threadLocalEntry.get();
		return (e != null ? e : _entry.get()).value;
	}

	public void reset() {
		set(_default);
	}

	public <S extends T> Scoped<S> scope(final S value) {
		final Entry<T> e = _threadLocalEntry.get();
		if (e != null) {
			_threadLocalEntry.set(e.inner(value));
		} else {
			_threadLocalEntry.set(new Entry<T>(value, Thread.currentThread()));
		}

		return new Scope<>(value, _threadLocalEntry);
	}

	public <S> Scoped<S> scope(final T value, final Supplier<? extends S> supplier) {
		final Scoped<T> scoped = scope(value);
		return new Scoped<S>() {
			@Override
			public S get() {
				return supplier.get();
			}

			@Override
			public void close() {
				scoped.close();
			}
		};
	}

	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version 2.0 &mdash; <em>$Date: 2014-03-15 $</em>
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
			assert(thread == Thread.currentThread());
			return new Entry<>(value, this, thread);
		}

	}

	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version 2.0 &mdash; <em>$Date: 2014-03-15 $</em>
	 * @since 2.0
	 */
	private static final class Scope<A, B> implements Scoped<A> {

		private final A _value;
		private final ThreadLocal<Entry<B>> _threadLocalEntry;

		Scope(final A value, final ThreadLocal<Entry<B>> threadLocalEntry) {
			_value = value;
			_threadLocalEntry = threadLocalEntry;
		}

		@Override
		public A get() {
			return _value;
		}

		@Override
		public void close() {
			final Entry<B> e = _threadLocalEntry.get();
			if (e != null) {
				if (e.thread != Thread.currentThread()) {
					throw new IllegalStateException(
						"Value context must be closed by the creating thread."
					);
				}

				_threadLocalEntry.set(e.parent);
			} else {
				throw new IllegalStateException(
					"Value context has been already close."
				);
			}
		}

	}
}
