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
package org.jenetics.internal.util;

import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;
import static org.jenetics.util.arrays.partition;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 2.0 &mdash; <em>$Date: 2014-04-05 $</em>
 * @since 2.0
 */
public abstract class Concurrency implements Executor, AutoCloseable {

	public static final int CORES = Runtime.getRuntime().availableProcessors();

	public static final Concurrency SERIAL_EXECUTOR = new SerialConcurrency();

	private static final class LazyPoolHolder {
		public static final ForkJoinPool FORK_JOIN_POOL =
			new ForkJoinPool(max(CORES - 1, 1));
	}

	public static ForkJoinPool commonPool() {
		return LazyPoolHolder.FORK_JOIN_POOL;
	}

	public abstract void execute(final List<? extends Runnable> runnables);

	@Override
	public abstract void close();

	/**
	 * Return an new Concurrency object from the given executor.
	 *
	 * @param executor the underlying Executor
	 * @return a new Concurrency object
	 */
	public static Concurrency with(final Executor executor) {
		if (executor instanceof ForkJoinPool) {
			return new ForkJoinPoolConcurrency((ForkJoinPool)executor);
		} else if (executor instanceof ExecutorService) {
			return new ExecutorServiceConcurrency((ExecutorService)executor);
		} else if (executor == SERIAL_EXECUTOR) {
			return SERIAL_EXECUTOR;
		} else {
			return new ExecutorConcurrency(executor);
		}
	}

	/**
	 * Return a new Concurrency object using the common ForkJoinPool.
	 *
	 * @return a new Concurrency object using the new ForkJoinPool
	 */
	public static Concurrency withCommonPool() {
		return with(commonPool());
	}


	/**
	 * This Concurrency uses a ForkJoinPool.
	 */
	private static final class ForkJoinPoolConcurrency extends Concurrency {
		private final Stack<ForkJoinTask<?>> _tasks = new Stack<>();
		private final ForkJoinPool _pool;

		ForkJoinPoolConcurrency(final ForkJoinPool pool) {
			_pool = requireNonNull(pool);
		}

		@Override
		public void execute(final Runnable runnable) {
			_tasks.push(_pool.submit(runnable));
		}

		@Override
		public void execute(final List<? extends Runnable> runnables) {
			_tasks.push(_pool.submit(new RunnablesAction(runnables)));
		}

		@Override
		public void close() {
			for (ForkJoinTask<?> t = _tasks.pop(); t != null; t = _tasks.pop()) {
				t.join();
			}
		}
	}

	/**
	 * This Concurrency uses an ExecutorService.
	 */
	private static final class ExecutorServiceConcurrency extends Concurrency {
		private final Stack<Future<?>> _futures = new Stack<>();
		private final ExecutorService _service;

		ExecutorServiceConcurrency(final ExecutorService service) {
			_service = requireNonNull(service);
		}

		@Override
		public void execute(final Runnable command) {
			_futures.push(_service.submit(command));
		}

		@Override
		public void execute(final List<? extends Runnable> runnables) {
			final int[] parts = partition(runnables.size(), CORES + 1);
			for (int i = 0; i < parts.length - 1; ++i) {
				execute(new RunnablesRunnable(runnables, parts[i], parts[i + 1]));
			}
		}

		@Override
		public void close() {
			try {
				for (Future<?> f = _futures.pop(); f != null; f = _futures.pop()) {
					f.get();
				}
			} catch (InterruptedException|ExecutionException e) {
				throw new CancellationException(e.getMessage());
			}
		}
	}

	/**
	 * This Concurrency uses an Executor.
	 */
	private static final class ExecutorConcurrency extends Concurrency {
		private final Stack<FutureTask<?>> _tasks = new Stack<>();
		private final Executor _executor;

		ExecutorConcurrency(final Executor executor) {
			_executor = requireNonNull(executor);
		}

		@Override
		public void execute(final Runnable command) {
			final FutureTask<?> task = new FutureTask<>(command, null);
			_tasks.push(task);
			_executor.execute(task);
		}

		@Override
		public void execute(final List<? extends Runnable> runnables) {
			final int[] parts = partition(runnables.size(), CORES + 1);
			for (int i = 0; i < parts.length - 1; ++i) {
				execute(new RunnablesRunnable(runnables, parts[i], parts[i + 1]));
			}
		}

		@Override
		public void close() {
			try {
				for (FutureTask<?> t = _tasks.pop(); t != null; t = _tasks.pop()) {
					t.get();
				}
			} catch (InterruptedException|ExecutionException e) {
				throw new CancellationException(e.getMessage());
			}
		}
	}

	/**
	 * This Concurrency executes the runnables within the main thread.
	 */
	private static final class SerialConcurrency extends Concurrency {

		@Override
		public void execute(final Runnable command) {
			command.run();
		}

		@Override
		public void execute(final List<? extends Runnable> runnables) {
			for (final Runnable runnable : runnables) {
				runnable.run();
			}
		}

		@Override
		public void close() {
		}
	}

}
