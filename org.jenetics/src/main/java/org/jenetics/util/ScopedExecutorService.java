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

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.jenetics.internal.util.Stack;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 2.0 &mdash; <em>$Date$</em>
 * @since 2.0
 */
final class ScopedExecutorService
	extends Concurrent
	implements Scoped<Concurrent>
{

	private final Stack<Future<?>> _futures = new Stack<>();
	private final ExecutorService _service;

	public ScopedExecutorService(final ExecutorService service) {
		_service = service;
	}

	@Override
	public void execute(final Runnable command) {
		_futures.push(_service.submit(command));
	}

	@Override
	public Concurrent get() {
		return this;
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
