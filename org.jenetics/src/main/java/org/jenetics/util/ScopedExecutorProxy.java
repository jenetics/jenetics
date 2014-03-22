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

import java.util.concurrent.Executor;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 2.0 &mdash; <em>$Date$</em>
 * @since 2.0
 */
final class ScopedExecutorProxy implements Scoped<Concurrent> {

	private final Scoped<Executor> _scope;
	private final Scoped<Concurrent> _executor;

	public ScopedExecutorProxy(
		final Scoped<Executor> scope,
		final Scoped<Concurrent> executor
	) {
		_scope = scope;
		_executor = executor;
	}

	@Override
	public Concurrent get() {
		return _executor.get();
	}

	@Override
	public void close() {
		try {
			_executor.close();
		} finally {
			_scope.close();
		}
	}
}
