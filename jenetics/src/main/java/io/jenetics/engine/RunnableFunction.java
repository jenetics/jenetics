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
package io.jenetics.engine;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

/**
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.0
 * @since 8.0
 */
final class RunnableFunction<T, R> implements Runnable {
	private final T _input;
	private final Function<? super T, ? extends R> _function;

	private R _result;

	public RunnableFunction(
		final T argument,
		final Function<? super T, ? extends R> function
	) {
		_input = requireNonNull(argument);
		_function = requireNonNull(function);
	}

	public T input() {
		return _input;
	}

	public R result() {
		return _result;
	}

	@Override
	public void run() {
		_result = _function.apply(_input);
	}

}
