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
package io.jenetics.internal.util;

import static java.lang.Math.ceil;
import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.8
 * @since 2.0
 */
public abstract class Concurrency implements Executor, AutoCloseable {

	public static final int CORES = Runtime.getRuntime().availableProcessors();

	public static final Concurrency SERIAL_EXECUTOR = new SerialConcurrency();

	public abstract void execute(final Seq<? extends Runnable> runnables);

	@Override
	public abstract void close();

	/**
	 * Return the underlying {@code Executor}, which is used for performing the
	 * actual task execution.
	 *
	 * @return the underlying {@code Executor} object
	 */
	public Executor getInnerExecutor() {
		return this;
	}

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
		return with(ForkJoinPool.commonPool());
	}


	/**
	 * This Concurrency uses a ForkJoinPool.
	 */
	private static final class ForkJoinPoolConcurrency extends Concurrency {
		private final List<ForkJoinTask<?>> _tasks = new ArrayList<>();
		private final ForkJoinPool _pool;

		ForkJoinPoolConcurrency(final ForkJoinPool pool) {
			_pool = requireNonNull(pool);
		}

		@Override
		public void execute(final Runnable runnable) {
			_tasks.add(_pool.submit(runnable));
		}

		@Override
		public void execute(final Seq<? extends Runnable> runnables) {
			if (runnables.nonEmpty()) {
				_tasks.add(_pool.submit(new RunnablesAction(runnables)));
			}
		}

		@Override
		public Executor getInnerExecutor() {
			return _pool;
		}

		@Override
		public void close() {
			Concurrency.join(_tasks);
		}
	}

	/**
	 * This Concurrency uses an ExecutorService.
	 */
	private static final class ExecutorServiceConcurrency extends Concurrency {
		private final List<Future<?>> _futures = new ArrayList<>();
		private final ExecutorService _service;

		ExecutorServiceConcurrency(final ExecutorService service) {
			_service = requireNonNull(service);
		}

		@Override
		public void execute(final Runnable command) {
			_futures.add(_service.submit(command));
		}

		@Override
		public void execute(final Seq<? extends Runnable> runnables) {
			if (runnables.nonEmpty()) {
				final int[] parts = partition(
					runnables.size(),
					max(
						(CORES + 1)*2,
						(int)ceil(runnables.size()/(double)Env.maxBatchSize)
					)
				);

				for (int i = 0; i < parts.length - 1; ++i) {
					execute(new RunnablesRunnable(runnables, parts[i], parts[i + 1]));
				}
			}
		}

		@Override
		public Executor getInnerExecutor() {
			return _service;
		}

		@Override
		public void close() {
			Concurrency.join(_futures);
		}

	}

	private static void join(final Iterable<? extends Future<?>> jobs) {
		Future<?> task = null;
		Iterator<? extends Future<?>> tasks = null;
		try {
			tasks = jobs.iterator();
			while (tasks.hasNext()) {
				task = tasks.next();
				task.get();
			}
		} catch (ExecutionException e) {
			Concurrency.cancel(task, tasks);
			final String msg = e.getCause() != null
				? e.getCause().getMessage()
				: null;
			throw (CancellationException)new CancellationException(msg)
				.initCause(e.getCause());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			Concurrency.cancel(task, tasks);
			final String msg = e.getMessage();
			throw (CancellationException)new CancellationException(msg)
				.initCause(e);
		}
	}

	private static void cancel(
		final Future<?> task,
		final Iterator<? extends Future<?>> tasks
	) {
		if (task != null) {
			task.cancel(true);
		}
		if (tasks != null) {
			tasks.forEachRemaining(t -> t.cancel(true));
		}
	}

	/**
	 * This Concurrency uses an Executor.
	 */
	private static final class ExecutorConcurrency extends Concurrency {
		private final List<FutureTask<?>> _tasks = new ArrayList<>();
		private final Executor _executor;

		ExecutorConcurrency(final Executor executor) {
			_executor = requireNonNull(executor);
		}

		@Override
		public void execute(final Runnable command) {
			final FutureTask<?> task = new FutureTask<>(command, null);
			_tasks.add(task);
			_executor.execute(task);
		}

		@Override
		public void execute(final Seq<? extends Runnable> runnables) {
			if (runnables.nonEmpty()) {
				final int[] parts = partition(
					runnables.size(),
					max(
						(CORES + 1)*2,
						(int)ceil(runnables.size()/(double)Env.maxBatchSize)
					)
				);

				for (int i = 0; i < parts.length - 1; ++i) {
					execute(new RunnablesRunnable(runnables, parts[i], parts[i + 1]));
				}
			}
		}

		@Override
		public void close() {
			Concurrency.join(_tasks);
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
		public void execute(final Seq<? extends Runnable> runnables) {
			runnables.forEach(Runnable::run);
		}

		@Override
		public void close() {
		}
	}


	/**
	 * Return a array with the indexes of the partitions of an array with the
	 * given size. The length of the returned array is {@code min(size, prts) + 1}.
	 * <p>
	 * Some examples:
	 * <pre>
	 * 	 partition(10, 3): [0, 3, 6, 10]
	 * 	 partition(15, 6): [0, 2, 4, 6, 9, 12, 15]
	 * 	 partition(5, 10): [0, 1, 2, 3, 4, 5]
	 * </pre>
	 *
	 * The following examples prints the start index (inclusive) and the end
	 * index (exclusive) of the {@code partition(15, 6)}.
	 * <pre>{@code
	 * int[] parts = partition(15, 6);
	 * for (int i = 0; i < parts.length - 1; ++i) {
	 *     System.out.println(i + ": " + parts[i] + "\t" + parts[i + 1]);
	 * }
	 * }</pre>
	 * <pre>
	 * 	 0: 0 	2
	 * 	 1: 2 	4
	 * 	 2: 4 	6
	 * 	 3: 6 	9
	 * 	 4: 9 	12
	 * 	 5: 12	15
	 * </pre>
	 *
	 * This example shows how this can be used in an concurrent environment:
	 * <pre>{@code
	 * try (final Concurrency c = Concurrency.start()) {
	 *     final int[] parts = arrays.partition(population.size(), _maxThreads);
	 *
	 *     for (int i = 0; i < parts.length - 1; ++i) {
	 *         final int part = i;
	 *         c.execute(new Runnable() { @Override public void run() {
	 *             for (int j = parts[part + 1]; --j <= parts[part];) {
	 *                 population.get(j).evaluate();
	 *             }
	 *         }});
	 *     }
	 * }
	 * }</pre>
	 *
	 * @param size the size of the array to partition.
	 * @param parts the number of parts the (virtual) array should be partitioned.
	 * @return the partition array with the length of {@code min(size, parts) + 1}.
	 * @throws IllegalArgumentException if {@code size} or {@code p} is less than one.
	 */
	private static int[] partition(final int size, final int parts) {
		if (size < 1) {
			throw new IllegalArgumentException(
				"Size must greater than zero: " + size
			);
		}
		if (parts < 1) {
			throw new IllegalArgumentException(
				"Number of partitions must greater than zero: " + parts
			);
		}

		final int pts = Math.min(size, parts);
		final int[] partition = new int[pts + 1];

		final int bulk = size/pts;
		final int rest = size%pts;
		assert (bulk*pts + rest) == size;

		for (int i = 0, n = pts - rest; i < n; ++i) {
			partition[i] = i*bulk;
		}
		for (int i = 0, n = rest + 1; i < n; ++i) {
			partition[pts - rest + i] = (pts - rest)*bulk + i*(bulk + 1);
		}

		return partition;
	}

	@SuppressWarnings("removal")
	private static final class Env {
		private static final int maxBatchSize = max(
			java.security.AccessController.doPrivileged(
				(java.security.PrivilegedAction<Integer>)() -> Integer.getInteger(
					"io.jenetics.concurrency.maxBatchSize",
					Integer.MAX_VALUE
				)),
			1
		);
	}

}
