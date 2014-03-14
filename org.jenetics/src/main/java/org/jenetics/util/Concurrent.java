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
import static java.util.concurrent.ForkJoinTask.adapt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
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
 * @version 2.0 &mdash; <em>$Date: 2014-03-14 $</em>
 */
public abstract class Concurrent {

	private static final ForkJoinPool DEFAULT = new ForkJoinPool(
		Math.max(Runtime.getRuntime().availableProcessors() - 1, 1)
	);

	private static final Executor SERIAL_EXECUTOR = new Executor() {
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
		final Scoped<Executor> scoped = CONTEXT.scope(requireNonNull(executor));

		if (executor instanceof ForkJoinPool) {
			return new ScopedForkJoinPool(scoped);
		} else {
			return new ScopedExecutorService(scoped);
		}
	}

	public static Scoped<Executor> scope() {
		return scope(getExecutor());
	}

	public static Scoped<Executor> serial() {
		return CONTEXT.scope(SERIAL_EXECUTOR);
	}

}

abstract class ScopedExecutor<F extends Future<?>>
	implements Executor, Scoped<Executor>
{
	private static final int TASKS_SIZE = 30;

	protected final Scoped<?> _outer;
	protected final Executor _executor;
	protected final List<F> _futures = new ArrayList<>(TASKS_SIZE);

	ScopedExecutor(final Scoped<?> outer, final Executor executor) {
		_outer = requireNonNull(outer);
		_executor = requireNonNull(executor);
	}

	@Override
	public Executor get() {
		return this;
	}

	@Override
	public void close() {
		try {
			join();
		} finally {
			_outer.close();
		}
	}

	protected abstract void join();
}

final class ScopedExecutorService extends ScopedExecutor<FutureTask<?>> {

	ScopedExecutorService(final Scoped<Executor> pool) {
		super(pool, pool.get());
	}

	@Override
	public void execute(final Runnable command) {
		final FutureTask<?> task = toFutureTask(command);
		_futures.add(task);
		_executor.execute(task);
	}

	private static FutureTask<?> toFutureTask(final Runnable runnable) {
		return new FutureTask<>(runnable, null);
	}

	@Override
	protected void join() {
		try {
			for (int i = _futures.size(); --i >= 0;) {
				_futures.get(i).get();
			}
		} catch (InterruptedException|ExecutionException e) {
			throw new CancellationException(e.getMessage());
		}
	}
}

final class ScopedForkJoinPool extends ScopedExecutor<ForkJoinTask<?>> {
	ScopedForkJoinPool(final Scoped<Executor> pool) {
		super(pool, pool.get());
	}

	@Override
	public void execute(final Runnable command) {
		final ForkJoinTask<?> task = toForkJoinTask(command);
		_futures.add(task);
		((ForkJoinPool)_executor).execute(task);
	}

	private static ForkJoinTask<?> toForkJoinTask(final Runnable r) {
		return r instanceof ForkJoinTask<?> ? (ForkJoinTask<?>)r : adapt(r);
	}

	@Override
	protected void join() {
		for (int i = _futures.size(); --i >= 0;) {
			_futures.get(i).join();
		}
	}
}
