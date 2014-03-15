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
package org.jenetics.internal.context;

import java.util.concurrent.atomic.AtomicReference;

import org.jenetics.internal.util.Supplier;

import org.jenetics.util.Scoped;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 2.0 &mdash; <em>$Date$</em>
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

}
