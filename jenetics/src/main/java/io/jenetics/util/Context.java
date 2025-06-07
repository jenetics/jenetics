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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.util;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * This class serves as wrapper around the {@link ScopedValue} implementation of
 * the JDK. It enriches the functionality, so it is possible to change the
 * context value within the same thread, without the need of opening a new
 * {@link ScopedValue#where(ScopedValue, Object)} scope.
 *
 * @see ScopedValue
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 2.0
 */
final class Context<T> {

	private final T _default;
	private final AtomicReference<Entry<T>> _entry;
	private final ScopedValue<Entry<T>> _value = ScopedValue.newInstance();

	/**
	 * Create a new <em>context</em> object with the given default value. The
	 * given {@code value} is the initial value of the <em>global</em> context.
	 *
	 * @param defaultValue the default value of the context, may be {@code null}
	 */
	Context(final T defaultValue) {
		_default = defaultValue;
		_entry = new AtomicReference<>(new Entry<>(_default));
	}

	/**
	 * Return the default value of the context.
	 *
	 * @since !__version__!
	 *
	 * @return the default value of the context
	 */
	T devault() {
		return _default;
	}

	/**
	 * Checks whether the context value is <em>global</em>.
	 *
	 * @since !__version__!
	 *
	 * @return {@code true} if the context value is <em>global</em> or
	 *         {@code false} if it is <em>scoped</em>
	 */
	boolean isGlobal() {
		return !_value.isBound();
	}

	/**
	 * Set the {@code value} for the <em>global</em> scope of the context.
	 *
	 * @param value the new <em>global</em> context value.
	 */
	void set(final T value) {
		if (_value.isBound()) {
			_value.get().value = value;
		} else {
			_entry.set(new Entry<>(value));
		}
	}

	/**
	 * Return either the value of the <em>global</em> context, or the <em>scoped</em>
	 * value, if called within a {@link #with(Object, Supplier)} <em>scoped</em>
	 * function.
	 *
	 * @return the context value, either <em>global</em> or <em>scoped</em>
	 */
	T get() {
		final var entry = _entry.get();
		assert entry != null;

		return _value.orElse(entry).value;
	}

	/**
	 * Reste the value of the <em>global</em> context to the default value.
	 */
	void reset() {
		set(_default);
	}

	/**
	 * Return the result of the {@code supplier}, which is executed with the
	 * given context {@code value}.
	 *
	 * @param value the context value the {@code supplier} sees
	 * @param supplier the supplier executed using the given context {@code value}
	 * @return the supplier value
	 * @param <S> the context value
	 * @param <R> the supplier result
	 */
	<S extends T, R> R with(final S value, final Supplier<? extends R> supplier) {
		return ScopedValue
			.where(_value, new Entry<>(value))
			.call(supplier::get);
	}

	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
	 * @version 2.0
	 * @since 2.0
	 */
	private static final class Entry<T> {
		T value;
		Entry(final T value) {
			this.value = value;
		}
	}

}
