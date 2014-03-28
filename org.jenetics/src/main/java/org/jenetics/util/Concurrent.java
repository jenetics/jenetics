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

import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import org.jenetics.internal.util.Context;

/**
 * [code]
 * try (Scoped&lt;Concurrent&gt; concurrent = Concurrent.scope()) {
 *     concurrent.get().execute(task1);
 *     concurrent.get().execute(task2);
 * }
 * [/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 2.0
 * @version 2.0 &mdash; <em>$Date$</em>
 */
public abstract class Concurrent implements Executor {

	private static final int CORES = Runtime.getRuntime().availableProcessors();

	private static final Concurrent SERIAL_EXECUTOR = new Concurrent() {
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

	private static final ForkJoinPool DEFAULT =
		new ForkJoinPool(max(CORES - 1, 1));

	private static final Context<Executor> CONTEXT =
		new Context<Executor>(DEFAULT);

	Concurrent() {
	}

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
	public static Scoped<Concurrent> scope() {
		return newScope(CONTEXT.get());
	}

	/**
	 * Open a new executor scope, where all tasks are executed in the main
	 * thread.
	 *
	 * @return a new <i>serial</i> executor scope.
	 */
	public static Scoped<Concurrent> serial() {
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
	public static Scoped<Concurrent> scope(final Executor executor) {
		final Scoped<Executor> scope = CONTEXT.scope(requireNonNull(executor));
		final Scoped<Concurrent> exec = newScope(executor);

		return new ScopedExecutorProxy(scope, exec);
	}

	/**
	 * Set the used executor to the default value.
	 */
	public static void reset() {
		CONTEXT.reset();
	}

	private static Scoped<Concurrent> newScope(final Executor executor) {
		if (executor instanceof ForkJoinPool) {
			return new ScopedForkJoinPool((ForkJoinPool)executor);
		} else if (executor instanceof ExecutorService) {
			return new ScopedExecutorService((ExecutorService)executor);
		} else {
			return new ScopedExecutor(executor);
		}
	}

	/* *************************************************************************
	 *  Instance methods
	 * ************************************************************************/

	public void execute(final List<? extends Runnable> runnables) {
		final int[] parts = arrays.partition(
			runnables.size(),
			CORES == 1 ? 1 : CORES + 1
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

}
