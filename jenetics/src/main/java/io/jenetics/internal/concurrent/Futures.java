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

import java.util.Iterator;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Helper method for handĺing future objects.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Futures {
	private Futures() {
	}

	/**
	 * Joins the given set of futures.
	 *
	 * @param futures the future objects to join.
	 */
	static void join(final Iterable<? extends Future<?>> futures) {
		final Iterator<? extends Future<?>> tasks = futures.iterator();

		Exception exception = null;
		Future<?> future = null;
		try {
			while (tasks.hasNext()) {
				future = tasks.next();
				future.get();
			}
			future = null;
		} catch (InterruptedException |
		         ExecutionException |
		         CancellationException e)
		{
			exception = e;
		}

		// Cancel all remaining tasks, in case of an error.
		if (future != null) {
			future.cancel(true);
			tasks.forEachRemaining(t -> t.cancel(true));
		}

		// Handle exceptions, if any.
		if (exception instanceof InterruptedException ie) {
			Thread.currentThread().interrupt();
			final var ce = new CancellationException(ie.getMessage());
			ce.initCause(ie);
			throw ce;
		} else if (exception instanceof CancellationException e) {
			throw e;
		} else if (exception != null) {
			throw new CompletionException(exception);
		}
	}

}
