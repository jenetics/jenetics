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
 * This class serves as wrapper around the {@link ScopedValue} implementation.
 * It allows defining a <em>default</em> value, which is returned if no value
 * is bound to the context.
 * <p>
 * <b>Use a context value with its default</b>
 * {@snippet lang = "java":
 * // Create a scoped context with a default value.
 * static final ScopedVariable<Random>
 *     RANDOM = new ScopedVariable<>(new Random(123));
 *
 *  // Context can be used without opening a scope. Using the default
 *  // value of the context.
 *  final var value = RANDOM.get().nextDouble();
 * }
 * <p>
 * <b>Use a context value with a different, scoped value</b>
 * {@snippet lang = "java":
 * // Create a scoped context with a default value.
 * static final ScopedVariable<Random>
 *     RANDOM = new ScopedVariable<>(new Random(123));
 *
 *  // Creating a random value with a different random instance with seed 456.
 *  final var value = ScopedVariable
 *      .with(RANDOM.value(new Random(456)))
 *      .call(() -> RANDOM.get().nextDouble());
 * }
 * <p>
 * <b>Changing the default value</b>
 * {@snippet lang="java":
 * // Create a scoped context with a default value.
 * RANDOM.set(new Random(789));
 * }
 * <p>
 * <b>Changing the scoped value</b>
 * {@snippet lang = "java":
 *  final var value = ScopedVariable
 *      .with(RANDOM.value(new Random(456)))
 *      .call(() -> {
 *           // Using the bound random generator.
 *           var value1 = RANDOM.get().nextDouble();
 *
 *           // Changing the value within this scope.
 *           // This value is reset when the scope is closed.
 *           RANDOM.set(new Random(321));
 * 		     return RANDOM.get().nextDouble() + value1;
 *       });
 * }
 *
 * @see ScopedValue
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ScopedVariable<T> {

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
		 * Return the (unbound) context value.
		 *
		 * @return the (unbound) context value
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

	private final T initial;
	private final AtomicReference<T> entry;
	private final ScopedValue<AtomicReference<T>> key = ScopedValue.newInstance();

	/**
	 * Create a new <em>context</em> object with the given default value. The
	 * given {@code value} is the initial value of the <em>global</em> context.
	 *
	 * @param value the initial value of the context, may be {@code null}
	 */
	public ScopedVariable(final T value) {
		initial = value;
		entry = new AtomicReference<>(initial);
	}

	/**
	 * Create a new context object with a {@code null} default value.
	 */
	public ScopedVariable() {
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
	 * Return the initial value of {@code this} context.
	 *
	 * @return the initial context value
	 */
	public T initialValue() {
		return initial;
	}

	/**
	 * Set the {@code value} for the <em>current</em> scope of the context. This
	 * might be <em>global</em> or <em>scoped</em>.
	 *
	 * @param value the new <em>current</em> scope value.
	 */
	public void set(final T value) {
		entry().set(value);
	}

	/**
	 * Return either the value of the <em>current</em> context, or the <em>scoped</em>
	 * value, if called within a {@link ScopedRunner}.
	 *
	 * @return the context value, either <em>global</em> or <em>scoped</em>
	 */
	public T get() {
		return entry().get();
	}

	/**
	 * Reset the value of the <em>global</em> context to the initial value.
	 */
	public void reset() {
		entry.set(initial);
	}

	/**
	 * Returns a new runner, which allows executing code with the given bound
	 * values.
	 *
	 * @param values the values to bind to the contexts
	 * @return a new runner with the bound context values
	 */
	public static ScopedRunner with(final Value<?>... values) {
		final Carrier carrier = Stream.of(values)
			.reduce(
				null,
				Value::fold,
				(_, _) -> { throw new IllegalStateException(); }
			);

		return new ScopedRunner() {
			@Override
			public void run(Runnable op) {
				carrier.run(op);
			}
			@Override
			public <R, X extends Throwable> R call(CallableOp<? extends R, X> op) throws X {
				return carrier.call(op);
			}
		};
	}

}
