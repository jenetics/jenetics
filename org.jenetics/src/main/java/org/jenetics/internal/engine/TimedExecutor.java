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
package org.jenetics.internal.engine;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public final class TimedExecutor {
	private final Executor _executor;

	public TimedExecutor(final Executor executor) {
		_executor = requireNonNull(executor);
	}

	public <T> CompletableFuture<TimedResult<T>> async(
		final Supplier<T> supplier
	) {
		return supplyAsync(TimedResult.of(supplier), _executor);
	}

	public <U, T> CompletableFuture<TimedResult<T>> thenApply(
		final CompletableFuture<U> result,
		final Function<U, T> function
	) {
		return result.thenApplyAsync(TimedResult.of(function), _executor);
	}


	public Executor get() {
		return _executor;
	}
}
