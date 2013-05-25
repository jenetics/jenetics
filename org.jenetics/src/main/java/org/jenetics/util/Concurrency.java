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

import java.util.Properties;
import java.util.concurrent.ForkJoinPool;

import javolution.context.ConcurrentContext;
import javolution.context.Context;
import javolution.context.LogContext;
import javolution.lang.Configurable;

/**
 * Simplify the usage of the {@link ConcurrentContext} usage by using the the
 * Java 'try' for resources capability.
 * <p/>
 * Normally you will write
 * [code]
 * ConcurrentContext.enter();
 * try {
 *     ConcurrentContext.execute(task1);
 *     ConcurrentContext.execute(task2); }
 * } finally {
 *     ConcurrentContext.exit();
 * }
 * [/code]
 * to execute two tasks. By using this class you can shorten the code to be
 * written to:
 * [code]
 * try (Concurrency c = Concurrency.start()) {
 *     c.execute(task1);
 *     c.execute(task2);
 * }
 * [/code]
 *
 * The configuration is performed as followed, before executing any concurrent
 * code.
 * [code]
 * public class Main {
 *     public static void main(final String[] args) {
 *         // Using 10 threads for evolving.
 *         Concurrency.setConcurrency(9);
 *
 *         // Forces the application only to use one thread.
 *         Concurrency.setConcurrency(0);
 *     }
 * }
 * [/code]
 *
 * The {@code ConcurrentContext} from the <i>JScience</i> project uses it's
 * own--optimized--thread-pool implementation. If you need to have a single
 * executor service, for the GA and your own classes, you can initialize the
 * {@code Concurrency} class with the {@link ForkJoinPool} from the JDK.
 *
 * [code]
 * public class Main {
 *     public static void main(final String[] args) {
 *         final int nthreads = 10;
 *         final ForkJoinPool pool = new ForkJoinPool(nthreads);
 *         Concurrency.setForkJoinPool(pool);
 *         ...
 *     }
 * }
 * [/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2013-05-25 $</em>
 */
public final class Concurrency implements AutoCloseable {

	private static final String KEY_CONTEXT =
		"javolution.context.ConcurrentContext#DEFAULT";

	private static final String KEY_CONCURRENTCY =
		"javolution.context.ConcurrentContext#MAXIMUM_CONCURRENCY";

	private static final Concurrency INSTANCE = new Concurrency();

	private Concurrency() {
	}

	/**
	 * Set the number of threads to use by the {@link ConcurrentContext}.
	 *
	 * @param concurrency the number of threads to use for the default concurrent
	 *        context.
	 */
	public static void setConcurrency(final int concurrency) {
		if (concurrency > ConcurrentContext.getConcurrency()) {
			final Properties properties = new Properties();
			properties.put(KEY_CONCURRENTCY, concurrency);
			setProperties(properties);
		}

		ConcurrentContext.setConcurrency(concurrency);
	}

	/**
	 * Set the concurrent-context to be used by the concurrency.
	 *
	 * @param type  the concurrent-context type.
	 * @throws NullPointerException if the given {@code type} is {@code null}.
	 */
	public static void setContext(final Class<? extends ConcurrentContext> type) {
		final Properties properties = new Properties();
		properties.put(KEY_CONTEXT, type);
		setProperties(properties);
	}

	/**
	 * Convenience method for setting the {@link ForkJoinPool} and the concurrent
	 * context to {@link ForkJoinContext}.
	 *
	 * @param pool the {@link ForkJoinPool} to use for concurrency.
	 */
	public static void setForkJoinPool(final ForkJoinPool pool) {
		ForkJoinContext.setForkJoinPool(pool);
		setContext(ForkJoinContext.class);
	}

	private static void setProperties(final Properties properties) {
		LogContext.enter(LogContext.NULL);
		try {
			Configurable.read(properties);
		} finally {
			LogContext.exit();
		}
	}

	/**
	 * Reset to the default default context.
	 */
	public static void setDefaultContext() {
		setContext(ConcurrentContext.DEFAULT.get());
	}

	@SuppressWarnings("unchecked")
	public static Class<ConcurrentContext> getContext() {
		final Context context = ConcurrentContext.getCurrent();
		return (Class<ConcurrentContext>)
				ConcurrentContext.class.cast(context).getClass();
	}

	public static Concurrency start() {
		ConcurrentContext.enter();
		return INSTANCE;
	}

	@SuppressWarnings("static-method")
	public void execute(final Runnable task) {
		ConcurrentContext.execute(task);
	}

	@Override
	public void close() {
		ConcurrentContext.exit();
	}

}
