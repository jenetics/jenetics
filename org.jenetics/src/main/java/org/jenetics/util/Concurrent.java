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
public abstract class Concurrent implements Executor {

	/* ************************************************************************
	 * Static concurrent context.
	 * ************************************************************************/

	public static final ForkJoinPool DEFAULT =
		new ForkJoinPool(Runtime.getRuntime().availableProcessors());

	private static final Context<ForkJoinPool> CONTEXT = new Context<>(DEFAULT);

	/**
	 * Set the thread pool to use for concurrent actions. If the given pool is
	 * {@code null}, the command, given in the {@link #execute(Runnable)} method
	 * is executed in the main thread.
	 *
	 * @param pool the thread pool to use.
	 */
	public static void setForkJoinPool(final ForkJoinPool pool) {
		CONTEXT.set(pool);
	}

	/**
	 * Return the currently use thread pool. {@code null} may be returned in
	 * case of serial execution.
	 *
	 * @return the currently used thread pool.
	 */
	public static ForkJoinPool getForkJoinPool() {
		return CONTEXT.get();
	}

	public static Scoped<Concurrent> scope(final ForkJoinPool pool) {
		return new ConcurrentScope(CONTEXT.scope(pool));
	}

	public static Scoped<Concurrent> scope() {
		return scope(getForkJoinPool());
	}

	/* ************************************************************************
	 * Interface methods.
	 * ************************************************************************/

	/**
	 * Return the current <i>parallelism</i> of this {@code Concurrent} object.
	 *
	 * @return the current <i>parallelism</i> of this {@code Concurrent} object
	 */
	public abstract int getParallelism();

	/**
	 * Executes the given {@code runnables} in {@code n} parts.
	 *
	 * @param n the number of parts the given {@code runnables} are executed.
	 * @param runnables the runnables to be executed.
	 * @throws NullPointerException if the given runnables are {@code null}.
	 */
	public abstract void execute(final int n, final List<? extends Runnable> runnables);

	/**
	 * Executes the given {@code runnables} in {@link #getParallelism()} parts.
	 *
	 * @param runnables the runnables to be executed.
	 * @throws NullPointerException if the given runnables are {@code null}.
	 */
	public abstract void execute(final List<? extends Runnable> runnables);

	/**
	 * Executes the given {@code runnables} in {@code n} parts.
	 *
	 * @param n the number of parts the given {@code runnables} are executed.
	 * @param runnables the runnables to be executed.
	 * @throws NullPointerException if the given runnables are {@code null}.
	 */
	public abstract void execute(final int n, final Runnable... runnables);

	/**
	 * Executes the given {@code runnables} in {@link #getParallelism()} parts.
	 *
	 * @param runnables the runnables to be executed.
	 * @throws NullPointerException if the given runnables are {@code null}.
	 */
	public abstract void execute(final Runnable... runnables);


	/* ************************************************************************
	 * Scope implementation
	 * ************************************************************************/

	private static final class ConcurrentScope
		extends Concurrent
		implements Scoped<Concurrent>
	{
		private final int TASKS_SIZE = 30;

		private final Scoped<ForkJoinPool> _pool;
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
		private ConcurrentScope(final Scoped<ForkJoinPool> pool) {
			_pool = requireNonNull(pool);
			_parallel = _pool.get() != null;
		}

		@Override
		public int getParallelism() {
			return _pool.get() != null ? _pool.get().getParallelism() : 1;
		}

		@Override
		public void execute(final Runnable command) {
			if (_parallel) {
				final ForkJoinTask<?> task = toForkJoinTask(command);
				_pool.get().execute(task);
				_tasks.add(task);
			} else {
				command.run();
			}
		}

		private static ForkJoinTask<?> toForkJoinTask(final Runnable r) {
			return r instanceof ForkJoinTask<?> ? (ForkJoinTask<?>)r : adapt(r);
		}

		@Override
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

		@Override
		public void execute(final List<? extends Runnable> runnables) {
			execute(getParallelism(), runnables);
		}

		@Override
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

		@Override
		public void execute(final Runnable... runnables) {
			execute(getParallelism(), runnables);
		}

		@Override
		public Concurrent get() {
			return this;
		}

		@Override
		public void close() {
			try {
				if (_parallel) {
					for (int i = _tasks.size(); --i >= 0;) {
						_tasks.get(i).join();
					}
				}
			} finally {
				_pool.close();
			}
		}
	}

}
