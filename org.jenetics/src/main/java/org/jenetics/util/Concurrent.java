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
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import javolution.context.LocalContext;

/**
 * [code]
 * try (final Concurrent c = new Concurrent()) {
 *     c.execute(task1);
 *     c.execute(task2);
 * }
 * [/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.5
 * @version 1.5 &mdash; <em>$Date: 2014-02-15 $</em>
 */
@Deprecated
final class Concurrent implements Executor, AutoCloseable {

	/* ************************************************************************
	 * Static concurrent context.
	 * ************************************************************************/

	private static final Object NULL = new Object();

	private static final LocalContext.Reference<Object>
	FORK_JOIN_POOL = new LocalContext.Reference<Object>(new ForkJoinPool(
		Runtime.getRuntime().availableProcessors()
	));

	/**
	 * Set the thread pool to use for concurrent actions. If the given pool is
	 * {@code null}, the command, given in the {@link #execute(Runnable)} method
	 * is executed in the main thread.
	 *
	 * @param pool the thread pool to use.
	 */
	public static void setForkJoinPool(final ForkJoinPool pool) {
		FORK_JOIN_POOL.set(pool != null ? pool : NULL);
	}

	/**
	 * Return the currently use thread pool.
	 *
	 * @return the currently used thread pool.
	 */
	public static ForkJoinPool getForkJoinPool() {
		final Object pool = FORK_JOIN_POOL.get();
		return pool != NULL ? (ForkJoinPool)pool : null;
	}


	/* ************************************************************************
	 * 'Dynamic' concurrent context.
	 * ************************************************************************/

	private final int TASKS_SIZE = 15;

	private final ForkJoinPool _pool;
	private final List<ForkJoinTask<?>> _tasks = new ArrayList<>(TASKS_SIZE);
	private final boolean _parallel;

	/**
	 * Create a new {@code Concurrent} executor <i>context</i> with the given
	 * {@link ForkJoinPool}.
	 *
	 * @param pool the {@code ForkJoinPool} used for concurrent execution of the
	 *        given tasks. The {@code pool} may be {@code null} and if so, the
	 *        given tasks are executed in the main thread.
	 */
	private Concurrent(final ForkJoinPool pool) {
		_pool = pool;
		_parallel = _pool != null;
	}

	/**
	 * Create a new {@code Concurrent} executor <i>context</i> with the
	 * {@code ForkJoinPool} set with the {@link #setForkJoinPool(ForkJoinPool)},
	 * or the default pool, if no one has been set.
	 */
	public Concurrent() {
		this(getForkJoinPool());
	}

	/**
	 * Return the current <i>parallelism</i> of this {@code Concurrent} object.
	 *
	 * @return the current <i>parallelism</i> of this {@code Concurrent} object
	 */
	public int getParallelism() {
		return _pool != null ? _pool.getParallelism() : 1;
	}

	@Override
	public void execute(final Runnable command) {
		if (_parallel) {
			final ForkJoinTask<?> task = toForkJoinTask(command);
			_pool.execute(task);
			_tasks.add(task);
		} else {
			command.run();
		}
	}

	private static ForkJoinTask<?> toForkJoinTask(final Runnable r) {
		return r instanceof ForkJoinTask<?> ? (ForkJoinTask<?>)r : adapt(r);
	}

	/**
	 * Executes the given {@code runnables} in {@code n} parts.
	 *
	 * @param n the number of parts the given {@code runnables} are executed.
	 * @param runnables the runnables to be executed.
	 * @throws NullPointerException if the given runnables are {@code null}.
	 */
	public void execute(final int n, final List<? extends Runnable> runnables) {
		requireNonNull(runnables, "Runnables must not be null");
		if (runnables.size() > 0) {
			final int[] parts = arrays.partition(runnables.size(), n);

			for (int i = 0; i < parts.length - 1; ++i) {
				final int part = i;

				execute(new Runnable() { @Override public void run() {
					for (int j = parts[part]; j < parts[part + 1]; ++j) {
						runnables.get(j).run();
					}
				}});
			}
		}
	}

	/**
	 * Executes the given {@code runnables} in {@link #getParallelism()} parts.
	 *
	 * @param runnables the runnables to be executed.
	 * @throws NullPointerException if the given runnables are {@code null}.
	 */
	public void execute(final List<? extends Runnable> runnables) {
		execute(getParallelism(), runnables);
	}

	/**
	 * Executes the given {@code runnables} in {@code n} parts.
	 *
	 * @param n the number of parts the given {@code runnables} are executed.
	 * @param runnables the runnables to be executed.
	 * @throws NullPointerException if the given runnables are {@code null}.
	 */
	public void execute(final int n, final Runnable... runnables) {
		requireNonNull(runnables, "Runnables must not be null");
		if (runnables.length > 0) {
			final int[] parts = arrays.partition(runnables.length, n);

			for (int i = 0; i < parts.length - 1; ++i) {
				final int part = i;

				execute(new Runnable() { @Override public void run() {
					for (int j = parts[part]; j < parts[part + 1]; ++j) {
						runnables[j].run();
					}
				}});
			}
		}
	}

	/**
	 * Executes the given {@code runnables} in {@link #getParallelism()} parts.
	 *
	 * @param runnables the runnables to be executed.
	 * @throws NullPointerException if the given runnables are {@code null}.
	 */
	public void execute(final Runnable... runnables) {
		execute(getParallelism(), runnables);
	}

	@Override
	public void close() {
		if (_parallel) {
			for (int i = _tasks.size(); --i >= 0;) {
				_tasks.get(i).join();
			}
		}
	}

}
