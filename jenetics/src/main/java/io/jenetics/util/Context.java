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

import static java.util.Objects.requireNonNull;

import java.util.concurrent.atomic.AtomicReference;

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

	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
	 * @version !__version__!
	 * @since 2.0
	 */
	private record Entry<T>(AtomicReference<T> value) {
		private Entry(T value) {
			this(new AtomicReference<>(value));
		}
		T get() {
			return value.get();
		}
		void set(T value) {
			this.value.set(value);
		}
	}

	private final T initial;
	private final Entry<T> entry;
	private final ScopedValue<Entry<T>> key = ScopedValue.newInstance();

	/**
	 * Create a new <em>context</em> object with the given default value. The
	 * given {@code value} is the initial value of the <em>global</em> context.
	 *
	 * @param value the initial value of the context, may be {@code null}
	 */
	Context(final T value) {
		initial = value;
		entry = new Entry<>(initial);
	}

	private Entry<T> entry() {
		return key.orElse(entry);
	}

	/**
	 * Set the {@code value} for the <em>global</em> scope of the context.
	 *
	 * @param value the new <em>global</em> context value.
	 */
	void set(final T value) {
		entry().set(value);
	}

	/**
	 * Return either the value of the <em>global</em> context, or the <em>scoped</em>
	 * value, if called within a {@link #call(Object, ScopedValue.CallableOp)}
	 * <em>scoped</em> function.
	 *
	 * @return the context value, either <em>global</em> or <em>scoped</em>
	 */
	T get() {
		return entry().get();
	}

	/**
	 * Reset the value of the <em>global</em> context to the default value.
	 */
	void reset() {
		set(initial);
	}

	/**
	 * Return the result of the {@code supplier}, which is executed with the
	 * given context {@code value}.
	 *
	 * @param value the context value the {@code supplier} sees
	 * @param operation the operation executed using the given context {@code value}
	 * @return the supplier value
	 * @param <S> the context value
	 * @param <R> the supplier result
	 */
	<S extends T, R> R call(
		final S value,
		final ScopedValue.CallableOp<? extends R, RuntimeException> operation
	) {
		requireNonNull(operation);
		return ScopedValue
			.where(key, new Entry<>(value))
			.call(operation);
	}

}
