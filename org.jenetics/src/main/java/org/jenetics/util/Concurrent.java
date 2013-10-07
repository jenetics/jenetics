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
 * @version @__version__@ &mdash; <em>$Date$</em>
 */
public class Concurrent implements Executor, AutoCloseable {

	private final int TASKS_SIZE = 15;

	private static final Object NULL = new Object();
	
	private static final LocalContext.Reference<Object> 
	FORK_JOIN_POOL = new LocalContext.Reference<Object>(new ForkJoinPool(
		Runtime.getRuntime().availableProcessors()
	));

	/**
	 * Set the thread pool to use for concurrent actions. If the given pool is
	 * {@code null}, the command, given in the {@link #execute(Runnable)} mehtod
	 * is executed in the main thread.
	 * 
	 * @param pool the thread pool to use.
	 */
	public static void setForkJoinPool(final ForkJoinPool pool) {
		System.out.println("SET POOL:" + pool);
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

	private final ForkJoinPool _pool;
	private final List<ForkJoinTask<?>> _tasks = new ArrayList<>(TASKS_SIZE);
	private final boolean _parallel;

	private Concurrent(final ForkJoinPool pool) {
		_pool = pool;
		_parallel = _pool != null;
	}
	
	public Concurrent() {
		this(getForkJoinPool());
	}
	
	public int getParallelism() {
		return _pool != null ? _pool.getParallelism() : 1;
	}
	
	@Override
	public void execute(final Runnable command) {
		if (_parallel) {
			final ForkJoinTask<?> task = ForkJoinTask.adapt(command);
			_pool.execute(task);
			_tasks.add(task);
		} else {
			command.run();
		}
	}

	public void execute(final Runnable... runnables) {

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


