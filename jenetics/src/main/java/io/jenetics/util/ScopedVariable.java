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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * This class serves as wrapper around the {@link ScopedValue} implementation.
 * It allows defining a <em>initial</em> value, which is returned if no value
 * is bound to the scope.
 * <p>
 * <b>Use a scoped variable with its initial value</b>
 * {@snippet lang = "java":
 * // Create a scoped context with a default value.
 * static final ScopedVariable<Random> RANDOM = ScopedVariable.of(new Random(123));
 *
 * // Variable can be used without opening a scope. Using the initial value.
 * final var value = RANDOM.get().nextDouble();
 * }
 * <p>
 * <b>Use a scoped variable with a different, scoped, value</b>
 * {@snippet lang = "java":
 * // Create a scoped context with a default value.
 * static final ScopedVariable<Random> RANDOM = ScopedVariable.of(new Random(123));
 *
 * // Creating a random value with a different random instance with seed 456.
 * final var value = ScopedVariable
 *      .with(RANDOM.value(new Random(456)))
 *      .call(() -> RANDOM.get().nextDouble());
 * }
 * <p>
 * <b>Changing the initial value</b>
 * {@snippet lang="java":
 * // Change the value of the scoped variable.
 * RANDOM.set(new Random(789));
 * }
 * <p>
 * <b>Changing the scoped variable</b>
 * {@snippet lang = "java":
 * final var value = ScopedVariable
 *      .with(RANDOM.value(new Random(456)))
 *      .call(() -> {
 *           // Using the bound random generator.
 *           var value1 = RANDOM.get().nextDouble();
 *
 *           // Changing the value within this scope.
 *           // This value is reset when the scope is closed.
 *           RANDOM.set(new Random(321));
 *           return RANDOM.get().nextDouble() + value1;
 *       });
 * }
 *
 * @see ScopedValue
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class ScopedVariable<T> {

	/**
	 * Runs code with specifically bound scoped value. Its extracts the
	 * {@link Carrier#run(Runnable)} and
	 * {@link Carrier#call(CallableOp)} method
	 * into an interface.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
	 * @version !__version__!
	 * @since !__version__!
	 */
	public static final class Runner {

		private final Carrier carrier;

		private Runner(final Carrier carrier) {
			this.carrier = requireNonNull(carrier);
		}

		/**
		 * Runs an operation with each scoped value in this mapping bound to
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
		 * Calls a value-returning operation with each scoped value in this
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
		public <R, X extends Throwable> R call(CallableOp<? extends R, X> op)
			throws X
		{
			return carrier.call(op);
		}

	}

	/**
	 * Represents a value, associated with a scope, but still not bound.
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
		 * Return the (unbound) scoped value.
		 *
		 * @return the (unbound) scoped value
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
	 * Create a new scoped variable with the given initial value.
	 *
	 * @param value the initial value of the scope, may be {@code null}
	 */
	private ScopedVariable(final T initial) {
		this.initial = initial;
		entry = new AtomicReference<>(initial);
	}

	/**
	 * Create a scoped value, which can be bound to {@code this} scope at a
	 * later time.
	 *
	 * @param value the scoped value
	 * @return a new (unbound) scoped value
	 */
	public Value<T> value(final T value) {
		return new Value<>(key, value);
	}

	private AtomicReference<T> entry() {
		return key.orElse(entry);
	}

	/**
	 * Return the initial value of {@code this} variable.
	 *
	 * @return the initially scoped value
	 */
	public T initial() {
		return initial;
	}

	/**
	 * Set the {@code value} for the <em>current</em> scope. This
	 * might be <em>global</em> or <em>scoped</em>.
	 *
	 * @param value the new <em>current</em> scope value.
	 */
	public void set(final T value) {
		entry().set(value);
	}

	/**
	 * Return either the value of the <em>current</em> scope, or the <em>scoped</em>
	 * value, if called within a {@link Runner}.
	 *
	 * @return the scoped value
	 */
	public T get() {
		return entry().get();
	}

	/**
	 * Reset the value of the <em>global</em> scope to the initial value.
	 */
	public void reset() {
		entry.set(initial);
	}

	/**
	 * Returns a new runner, which allows executing code with the given bound
	 * values.
	 * {@snippet lang = "java":
	 * static final ScopedVariable<String> USER = ScopedVariable.of("initial_user");
	 * static final ScopedVariable<String> TOKEN = ScopedVariable.of("initial_token");
	 *
	 * assert USER.get().equals("initial_user");
	 * assert TOKEN.get().equals("initial_token");
	 *
	 * ScopedVariable
	 *     .with(USER.value("otto"), TOKEN.value("3973hj2l34i92j"))
	 *     .run(() -> {
	 *          assert USER.get().equals("otto");
	 *          assert TOKEN.get().equals("3973hj2l34i92j");
	 *     });
	 *
	 * assert USER.get().equals("initial_user");
	 * assert TOKEN.get().equals("initial_token");
	 * }
	 *
	 * @param values the values to bind to the scope
	 * @return a new runner with the bound scoped-values
	 * @throws IllegalArgumentException if the {@code values} array is empty
	 */
	public static Runner with(final Value<?>... values) {
		if (values.length == 0) {
			throw new IllegalArgumentException("No values specified.");
		}
		Stream.of(values).forEach(Objects::requireNonNull);

		final Carrier carrier = Stream.of(values)
			.reduce(
				null,
				Value::fold,
				(_, _) -> { throw new IllegalStateException(); }
			);

		return new Runner(carrier);
	}

	/**
	 * Create a new scoped variable with the given initial value.
	 *
	 * @param initial the initial value of the scope, may be {@code null}
	 * @return a new scoped variable with the given default value
	 */
	public static <T> ScopedVariable<T> of(final T initial) {
		return new ScopedVariable<T>(initial);
	}

}
