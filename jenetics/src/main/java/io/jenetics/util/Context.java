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

import java.lang.ScopedValue.CallableOp;
import java.lang.ScopedValue.Carrier;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

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
	 * Represents a value, associated with a context, but still not bound.
	 *
	 * @param <T> the value type
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
	 * @version !__version__!
	 * @since !__version__!
	 */
	public static final class Value<T> {
		private final ScopedValue<AtomicReference<T>> key;
		private final T value;

		private Value(
			final ScopedValue<AtomicReference<T>> key,
			final T value
		) {
			this.key = requireNonNull(key);
			this.value = value;
		}

		/**
		 * Return the context value.
		 *
		 * @return the context value
		 */
		public T get() {
			return value;
		}

		private static <T> Carrier fold(Carrier carrier, Value<T> value) {
			requireNonNull(value);

			return carrier == null
				? ScopedValue.where(value.key, new AtomicReference<>(value.value))
				: carrier.where(value.key, new AtomicReference<>(value.value));
		}
	}

	/**
	 * Runs code with specifically bound context values. Is essentially a wrapper
	 * around a {@link Carrier}.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
	 * @version !__version__!
	 * @since !__version__!
	 */
	public static final class Runner {
		private final Carrier carrier;

		private Runner(Carrier carrier) {
			this.carrier = requireNonNull(carrier);
		}

		/**
		 * Runs an operation with each context value in this mapping bound to
		 * its value in the current thread.
		 *
		 * @see Carrier#run(Runnable)
		 *
		 * @param op the operation to run
		 */
		public void run(Runnable op) {
			carrier.run(op);
		}

		/**
		 * Calls a value-returning operation with each context value in this
		 * mapping bound to its value in the current thread.
		 *
		 * @see Carrier#call(CallableOp)
		 *
		 * @param op the operation to run
		 * @param <R> the type of the result of the operation
		 * @param <X> type of the exception thrown by the operation
		 * @return the result
		 * @throws X if {@code op} completes with an exception
		 */
		public <R, X extends Throwable> R call(CallableOp<? extends R, X> op) throws X {
			return carrier.call(op);
		}
	}

	private final T initial;
	private final AtomicReference<T> entry;
	private final ScopedValue<AtomicReference<T>> key = ScopedValue.newInstance();

	/**
	 * Create a new <em>context</em> object with the given default value. The
	 * given {@code value} is the initial value of the <em>global</em> context.
	 *
	 * @param value the initial value of the context, may be {@code null}
	 */
	public Context(final T value) {
		initial = value;
		entry = new AtomicReference<>(initial);
	}

	/**
	 * Create a new context object with a {@code null} default value.
	 */
	public Context() {
		this(null);
	}

	/**
	 * Create a context value, which can be bound to {@code this} context at a
	 * later time.
	 *
	 * @param value the actual context value
	 * @return a new (unbound) context value
	 */
	public Value<T> value(final T value) {
		return new Value<>(key, value);
	}

	private AtomicReference<T> entry() {
		return key.orElse(entry);
	}

	/**
	 * Set the {@code value} for the <em>global</em> scope of the context.
	 *
	 * @param value the new <em>global</em> context value.
	 */
	public void set(final T value) {
		entry().set(value);
	}

	/**
	 * Return either the value of the <em>global</em> context, or the <em>scoped</em>
	 * value, if called within a {@link Runner}.
	 *
	 * @return the context value, either <em>global</em> or <em>scoped</em>
	 */
	public T get() {
		return entry().get();
	}

	/**
	 * Reset the value of the <em>global</em> context to the default value.
	 */
	public void reset() {
		set(initial);
		entry.set(initial);
	}

	/**
	 * Returns a new runner, which allows executing code with the given bound
	 * values.
	 *
	 * @param values the values to bind to the contexts
	 * @return a new runner with the bound context values
	 */
	public static Runner with(final Value<?>... values) {
		final Carrier carrier = Stream.of(values)
			.reduce(
				null,
				Value::fold,
				(_, _) -> { throw new IllegalStateException(); }
			);

		return new Runner(carrier);
	}

}
