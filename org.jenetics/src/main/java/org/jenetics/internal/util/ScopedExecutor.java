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

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import org.jenetics.util.Concurrency;
import org.jenetics.util.Scoped;
import org.jenetics.util.arrays;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 2.0 &mdash; <em>$Date: 2014-03-21 $</em>
 * @since 2.0
 */
public final class ScopedExecutor implements Concurrency, Scoped<Concurrency> {

	private static final int MIN_THRESHOLD = 2;
	private static final int CORES = Runtime.getRuntime().availableProcessors();

	private final Stack<FutureTask<?>> _tasks = new Stack<>();
	private final Executor _executor;

	public ScopedExecutor(final Executor executor) {
		_executor = executor;
	}

	@Override
	public void execute(final Runnable command) {
		final FutureTask<?> task = new FutureTask<>(command, null);
		_tasks.push(task);
		_executor.execute(task);
	}

	@Override
	public void execute(final List<? extends Runnable> runnables) {
		final int[] parts = arrays.partition(
			runnables.size(),
			partitions(runnables.size())
		);

		for (int i = 0; i < parts.length - 1; ++i) {
			final int part = i;

			execute(new Runnable() { @Override public void run() {
				for (int j = parts[part]; j < parts[part + 1]; ++j) {
					runnables.get(j).run();
				}
			}});
		}
	}

	private static int partitions(final int ntasks) {
		int threshold;
		if (CORES == 1) {
			threshold = max(ntasks/2, MIN_THRESHOLD);
		} else {
			threshold = max(
				(int)((double)ntasks/(CORES*2)),
				MIN_THRESHOLD
			);
		}

		return max(ntasks/threshold, 1);
	}

	@Override
	public Concurrency get() {
		return this;
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
