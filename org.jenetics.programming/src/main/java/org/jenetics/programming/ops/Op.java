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
package org.jenetics.programming.ops;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

/**
 * Operation interface.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Op<T> extends Function<T[], T> {

	/**
	 * Return the name of the operation.
	 *
	 * @return the name of the operation
	 */
	public  String name();

	/**
	 * Return the arity of the operation function. If the arity is zero, the
	 * operation is <em>terminal</em> operation.
	 *
	 * @return the arity of the operation
	 */
	public int arity();

	/**
	 * Determines if the operation is a terminal operation.
	 *
	 * @return {@code true} if the operation is a terminal operation,
	 *         {@code false} otherwise
	 */
	public default boolean isTerminal() {
		return arity() == 0;
	}

	/**
	 * Create a new operation from the given parameter.
	 *
	 * @param name the operation name
	 * @param arity the arity of the operation
	 * @param function the function executed by the operation
	 * @param <T> the operation type
	 * @return a new operation from the given parameter
	 * @throws NullPointerException if the given {@code name} or {@code function}
	 *         is {@code null}
	 * @throws IllegalArgumentException if the given {@code arity} is smaller
	 *         than zero
	 */
	public static <T> Op<T> of(
		final String name,
		final int arity,
		final Function<T[], T> function
	) {
		requireNonNull(name);
		requireNonNull(function);
		if (arity < 0) {
			throw new IllegalArgumentException("Arity smaller than zero: " + arity);
		}

		return new Op<T>() {
			@Override
			public String name() {
				return name;
			}

			@Override
			public T apply(final T[] value) {
				return function.apply(value);
			}

			@Override
			public int arity() {
				return arity;
			}

			@Override
			public String toString() {
				return name();
			}
		};
	}
}
