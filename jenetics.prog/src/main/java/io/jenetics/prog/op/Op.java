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
package io.jenetics.prog.op;

import static java.util.Objects.requireNonNull;

import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Operation interface. An operation is a function which maps some argument type
 * with a given <em>arity</em> to a result object of the same type:
 * {@code T[] -> T}.
 *
 * <pre>{@code
 * final Op<Double> add = Op.of("add", 2, v -> v[0] + v[1]);
 * final Op<Double> add3 = Op.of("add3", 3, v -> v[0] + v[1] + v[2]);
 * final Op<Double> sub = Op.of("sub", 2, v -> v[0] - v[1]);
 * final Op<Double> sin = Op.of("sin", 1, v -> Math.sin(v[0]));
 * }</pre>
 *
 * Implementations of the {@code Op} interface are usually immutable and doesn't
 * maintain internal state. But some instances are ephemeral with changing state.
 * This classes must override the {@link #get()} method inherited from the
 * {@link Supplier} interface and return a new instance.
 *
 * @see Var
 * @see Const
 * @see EphemeralConst
 *
 * @param <T> the argument type of the operation
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
public interface Op<T> extends Function<T[], T>, Supplier<Op<T>> {

	/**
	 * Return the name of the operation.
	 *
	 * @return the name of the operation
	 */
	String name();

	/**
	 * Return the arity of the operation function. If the arity is zero, the
	 * operation is <em>terminal</em> operation.
	 *
	 * @return the arity of the operation
	 */
	int arity();

	/**
	 * Determines if the operation is a terminal operation.
	 *
	 * @return {@code true} if the operation is a terminal operation,
	 *         {@code false} otherwise
	 */
	default boolean isTerminal() {
		return arity() == 0;
	}

	/**
	 * Return {@code this} operation, or a new instance from the same type, if
	 * the operation needs to maintain internal state. This is essentially the
	 * case for ephemeral constants.
	 *
	 * @see EphemeralConst
	 *
	 * @return {@code this} operation, or a new instance
	 */
	@Override
	default Op<T> get() {
		return this;
	}

	/**
	 * Create a new operation from the given parameter.
	 *
	 * @param name the operation name
	 * @param arity the arity of the operation
	 * @param function the function executed by the operation. In order to work
	 *        properly, the given function should be stateless and must not have
	 *        side effects.
	 * @param <T> the operation type
	 * @return a new operation from the given parameter
	 * @throws NullPointerException if the given {@code name} or {@code function}
	 *         is {@code null}
	 * @throws IllegalArgumentException if the given {@code arity} is smaller
	 *         than zero
	 */
	static <T> Op<T> of(
		final String name,
		final int arity,
		final Function<T[], T> function
	) {
		return new Operation<>(name, arity, function);
	}

	/**
	 * Create a new operation with the given name and unary operation. The
	 * returned {@code Op} will have arity one.
	 *
	 * @since 4.0
	 *
	 * @param name the name of the returned operation
	 * @param function the used function of the operation
	 * @param <T> the operation type
	 * @return a new operation with the given name and unary operation
	 * @throws NullPointerException if the given {@code name} or {@code function}
	 *         is {@code null}
	 */
	static <T> Op<T> of(
		final String name,
		final UnaryOperator<T> function
	) {
		requireNonNull(function);
		return of(name, 1, v -> function.apply(v[0]));
	}

	/**
	 * Create a new operation with the given name and binary operation. The
	 * returned {@code Op} will have arity two.
	 *
	 * @since 4.0
	 *
	 * @param name the name of the returned operation
	 * @param function the used function of the operation
	 * @param <T> the operation type
	 * @return a new operation with the given name and unary operation
	 * @throws NullPointerException if the given {@code name} or {@code function}
	 *         is {@code null}
	 */
	static <T> Op<T> of(
		final String name,
		final BinaryOperator<T> function
	) {
		requireNonNull(function);
		return of(name, 2, v -> function.apply(v[0], v[1]));
	}

}
