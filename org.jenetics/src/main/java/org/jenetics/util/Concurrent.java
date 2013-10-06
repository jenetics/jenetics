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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import javolution.context.LocalContext;

/**
 * [code]
 * try (Concurrent c = new Concurrent()) {
 *     c.execute(task1);
 *     c.execute(task2);
 * }
 * [/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__version__@
 * @version @__version__@ &mdash; <em>$Date: 2013-10-06 $</em>
 */
public class Concurrent implements Executor, AutoCloseable {

	private final int TASKS_SIZE = 15;

	private static LocalContext.Reference<ForkJoinPool> _POOL = new LocalContext.Reference<>(
			new ForkJoinPool(
					Math.max(Runtime.getRuntime().availableProcessors() - 1, 1)
				)
			);

	public static void setForkJoinPool(final ForkJoinPool pool) {
		_POOL.set(pool);
	}

	public static ForkJoinPool getForkJoinPool() {
		return _POOL.get();
	}

	private final List<ForkJoinTask<?>> _tasks = new ArrayList<>(TASKS_SIZE);

	@Override
	public void execute(final Runnable command) {
		if (_POOL.get().getParallelism() > 1) {
			final ForkJoinTask<?> task = ForkJoinTask.adapt(command);
			_POOL.get().execute(task);
			_tasks.add(task);
		} else {
			command.run();
		}
	}

	@Override
	public void close() {
		for (final ForkJoinTask<?> task : _tasks) {
			task.join();
		}
	}

}
