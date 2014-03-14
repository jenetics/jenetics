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

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.jenetics.internal.context.Context;

/**
 * [code]
 * try (final Scoped<Concurrent> c = Concurrent.scope()) {
 *     c.get().execute(task1);
 *     c.get().execute(task2);
 * }
 * [/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 2.0
 * @version 2.0 &mdash; <em>$Date$</em>
 */
public abstract class Concurrent {

	private static final ForkJoinPool DEFAULT =
		new ForkJoinPool(Runtime.getRuntime().availableProcessors());

	private static Executor SERIAL_EXECUTOR = new Executor() {
		@Override
		public void execute(final Runnable command) {
			command.run();
		}
	};

	private static final Context<Executor> CONTEXT =
		new Context<Executor>(DEFAULT);


	public static ForkJoinPool commonPool() {
		return DEFAULT;
	}

	/**
	 * Set the executor object to use for concurrent actions.
	 *
	 * @param executor the executor object to use.
	 * @throws java.lang.NullPointerException if the given {@code executor} is
	 *         {@code null}.
	 */
	public static void setExecutor(final Executor executor) {
		CONTEXT.set(requireNonNull(executor, "Executor must not be null."));
	}

	/**
	 * Return the currently used executor object.
	 *
	 * @return the currently used thread pool.
	 */
	public static Executor getExecutor() {
		return CONTEXT.get();
	}

	public static void reset() {
		setExecutor(commonPool());
	}

	public static Scoped<Executor> scope(final Executor executor) {
		return new ScopedConcurrentExecutor(
			CONTEXT.scope(requireNonNull(executor))
		);
	}

	public static Scoped<Executor> scope() {
		return scope(getExecutor());
	}

	public static Scoped<Executor> serial() {
		return CONTEXT.scope(SERIAL_EXECUTOR);
	}

}

final class ScopedConcurrentExecutor implements Executor, Scoped<Executor> {
	private final int TASKS_SIZE = 30;

	private final Scoped<?> _scoped;
	private final Executor _executor;
	private final List<Future<?>> _tasks = new ArrayList<>(TASKS_SIZE);

	ScopedConcurrentExecutor(final Scoped<Executor> pool) {
		_executor = pool.get();
		_scoped = pool;
	}

	@Override
	public void execute(final Runnable command) {
		final FutureTask task = toFutureTask(command);
		_tasks.add(task);
		_executor.execute(task);
	}

	private static FutureTask<?> toFutureTask(final Runnable runnable) {
		return new FutureTask<>(runnable, null);
	}

	@Override
	public Executor get() {
		return this;
	}

	@Override
	public void close() {
		try {
			for (int i = _tasks.size(); --i >= 0;) {
				_tasks.get(i).get();
			}
		} catch (InterruptedException|ExecutionException e) {
			throw new CancellationException(e.getMessage());
		} finally {
			_scoped.close();
		}
	}
}
