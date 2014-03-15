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
package org.jenetics.internal.concurrent;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import org.jenetics.util.Scoped;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 2.0 &mdash; <em>$Date$</em>
 * @since 2.0
 */
public final class ScopedExecutor implements Executor, Scoped<Executor> {

	private final List<FutureTask<?>> _tasks = new LinkedList<>();
	private final Executor _executor;

	public ScopedExecutor(final Executor executor) {
		_executor = executor;
	}

	@Override
	public void execute(final Runnable command) {
		final FutureTask<?> task = new FutureTask<>(command, null);
		_tasks.add(task);
		_executor.execute(task);
	}

	@Override
	public Executor get() {
		return this;
	}

	@Override
	public void close() {
		try {
			for (final FutureTask<?> task : _tasks) {
				task.get();
			}
		} catch (InterruptedException|ExecutionException e) {
			throw new CancellationException(e.getMessage());
		}
	}
}
