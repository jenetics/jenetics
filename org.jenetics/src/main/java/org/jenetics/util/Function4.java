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

import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a function that accepts 4 arguments and produces a result. This is
 * the 4-arity specialization of {@link Function}.
 *
 * @param <T1> the type of the first argument to the function
 * @param <T2> the type of the second argument to the function
 * @param <T3> the type of the third argument to the function
 * @param <T4> the type of the fourth argument to the function
 * @param <R> the type of the result of the function
 *
 * @see Function
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface Function4<T1, T2, T3, T4, R> {

	/**
	 * Applies this function to the given arguments.
	 *
	 * @param t1 the first function argument
	 * @param t2 the second function argument
	 * @param t3 the third function argument
	 * @param t4 the fourth function argument
	 * @return the function result
	 */
	public R apply(final T1 t1, final T2 t2, final T3 t3, final T4 t4);

	/**
	 * Returns a composed function that first applies this function to its input,
	 * and then applies the {@code after} function to the result.
	 *
	 * @param <V> the type of output of the {@code after} function, and of the
	 *           composed function
	 * @param after the function to apply after this function is applied
	 * @return a composed function that first applies this function and then
	 *         applies the {@code after} function
	 * @throws NullPointerException if after is {@code null}
	 */
	public default <V> Function4<T1, T2, T3, T4, V>
	andThen(final Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (T1 t1, T2 t2, T3 t3, T4 t4) -> after.apply(apply(t1, t2, t3, t4));
	}

}
