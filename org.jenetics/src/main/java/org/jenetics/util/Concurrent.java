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

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import org.jenetics.internal.util.Context;
import org.jenetics.internal.util.ScopedExecutor;
import org.jenetics.internal.util.ScopedExecutorProxy;
import org.jenetics.internal.util.ScopedExecutorService;
import org.jenetics.internal.util.ScopedForkJoinPool;

/**
 * [code]
 * try (Scoped<Executor> executor = Concurrent.scope()) {
 *     executor.get().execute(task1);
 *     executor.get().execute(task2);
 * }
 * [/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 2.0
 * @version 2.0 &mdash; <em>$Date: 2014-03-21 $</em>
 */
public final class Concurrent extends StaticObject {
	private Concurrent() {}

	private static final ForkJoinPool DEFAULT = new ForkJoinPool(
		Math.max(Runtime.getRuntime().availableProcessors() - 1, 1)
	);

	private static final Concurrency SERIAL_EXECUTOR = new Concurrency() {
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
	};

	private static final Context<Executor> CONTEXT =
		new Context<Executor>(DEFAULT);

	/**
	 * Set the executor object to use for concurrent actions.
	 *
	 * @param executor the executor object to use.
	 * @throws java.lang.NullPointerException if the given {@code executor} is
	 *         {@code null}.
	 */
	public static void setExecutor(final Executor executor) {
		CONTEXT.set(requireNonNull(executor, "Executor must not be null."));
	}

	/**
	 * Return the currently used executor object.
	 *
	 * @return the currently used thread pool.
	 */
	public static Executor getExecutor() {
		return CONTEXT.get();
	}

	/**
	 * Open a new executor scope with the currently set
	 * {@link java.util.concurrent.Executor}.
	 *
	 * @return a new executor with the currently se {@code Executor}.
	 */
	public static Scoped<Concurrency> scope() {
		return newScope(CONTEXT.get());
	}

	/**
	 * Open a new executor scope, where all tasks are executed in the main
	 * thread.
	 *
	 * @return a new <i>serial</i> executor scope.
	 */
	public static Scoped<Concurrency> serial() {
		return CONTEXT.scope(SERIAL_EXECUTOR);
	}

	/**
	 * Open a new executor scope with the given {@code executor}. When this
	 * scope is <i>closed</i>, the original executor is restored.
	 *
	 * @param executor the executor used for the new scope.
	 * @return a new executor scope
	 * @throws java.lang.NullPointerException if the given {@code executor} is
	 *         {@code null}.
	 */
	public static Scoped<Concurrency> scope(final Executor executor) {
		final Scoped<Executor> scope = CONTEXT.scope(requireNonNull(executor));
		final Scoped<Concurrency> exec = newScope(executor);

		return new ScopedExecutorProxy(scope, exec);
	}

	/**
	 * Set the used executor to the default value.
	 */
	public static void reset() {
		CONTEXT.reset();
	}

	private static Scoped<Concurrency> newScope(final Executor executor) {
		if (executor instanceof ForkJoinPool) {
			return new ScopedForkJoinPool((ForkJoinPool)executor);
		} else if (executor instanceof ExecutorService) {
			return new ScopedExecutorService((ExecutorService)executor);
		} else {
			return new ScopedExecutor(executor);
		}
	}

}
