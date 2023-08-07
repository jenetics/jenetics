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
package io.jenetics.internal.concurrent;

import static java.lang.Math.ceil;
import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import io.jenetics.util.BatchExecutor;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 2.0
 */
public class BatchExec implements BatchExecutor {

	public static final int CORES = Runtime.getRuntime().availableProcessors();

	final Executor _executor;

	public BatchExec(final Executor executor) {
		_executor = requireNonNull(executor);
	}

	@Override
	public void execute(final Seq<? extends Runnable> batch) {
		if (batch.nonEmpty()) {
			final int[] parts = partition(
				batch.size(),
				max(
					(CORES + 1)*2,
					(int)ceil(batch.size()/(double)maxBatchSize())
				)
			);

			final var futures = new ArrayList<Future<?>>();
			for (int i = 0; i < parts.length - 1; ++i) {
				execute(
					new BatchRunnable(batch, parts[i], parts[i + 1]),
					futures
				);
			}

			Futures.join(futures);
		}
	}

	private void execute(final Runnable command, final List<Future<?>> futures) {
		if (_executor instanceof ExecutorService service) {
			futures.add(service.submit(command));
		} else {
			final FutureTask<?> task = new FutureTask<>(command, null);
			futures.add(task);
			_executor.execute(task);
		}
	}

	/**
	 * Return an array with the indexes of the partitions of an array with the
	 * given size. The length of the returned array is {@code min(size, prts) + 1}.
	 * <p>
	 * Some examples:
	 * <pre>
	 * 	 partition(10, 3): [0, 3, 6, 10]
	 * 	 partition(15, 6): [0, 2, 4, 6, 9, 12, 15]
	 * 	 partition(5, 10): [0, 1, 2, 3, 4, 5]
	 * </pre>
	 *
	 * The following examples print the start index (inclusive) and the end
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

	static int maxBatchSize() {
		return Env.maxBatchSize;
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
