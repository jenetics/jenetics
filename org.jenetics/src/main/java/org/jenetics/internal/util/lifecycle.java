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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.jenetics.util.StaticObject;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2013-10-03 $</em>
 * @since @__version__@
 */
public final class lifecycle extends StaticObject {
	private lifecycle() {}

	public static interface Method<T> {
		public void invoke(final T object) throws Exception;
	}

	public static void close(final AutoCloseable... closeables) throws Exception {
		invokeAll(c -> c.close(), closeables);
	}

	@SafeVarargs
	public static <T> void invokeAll(final Method<T> method, final T... objects)
		throws Exception
	{
		invokeAll(method, Arrays.asList(objects));
	}

	public static <T> void invokeAll(final Method<T> method, final List<T> objects)
		throws Exception
	{
		final Throwable error = invokeAll(method, new LinkedList<>(objects), null);

		if (error != null) {
			if (error instanceof Exception) {
				throw (Exception)error;
			} else if (error instanceof Error) {
				throw (Error)error;
			}
		}
	}

	private static <T> Throwable invokeAll(
		final Method<T> method,
		final Queue<T> objects,
		final Throwable previousError
	) {
		if (objects.isEmpty()) {
			return previousError;
		} else {
			final T head = objects.remove();

			Throwable currentError = null;
			if (previousError != null) {
				try {
					method.invoke(head);
				} catch (Exception suppressed) {
					previousError.addSuppressed(suppressed);
				}
				currentError = previousError;
			} else {
				try {
					method.invoke(head);
				} catch (Throwable e) {
					currentError = e;
				}
			}

			return invokeAll(method, objects, currentError);
		}
	}
}
