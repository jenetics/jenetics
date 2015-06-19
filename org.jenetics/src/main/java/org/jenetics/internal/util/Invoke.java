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

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.2
 * @since 3.2
 */
public final class Invoke {
	private Invoke() { require.noInstance(); }

	public interface MethodHandle<T, E extends Throwable> {
		public void apply(final T argument) throws E;
	}

	@SafeVarargs
	public static <T extends Closeable> void closeAll(final T... objects)
		throws IOException
	{
		all(Closeable::close, objects);
	}

	@SafeVarargs
	public static <T, E extends Throwable> void all(
		final MethodHandle<T, E> method,
		final T... objects
	)
		throws E
	{
		all(method, Arrays.asList(objects));
	}

	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> void all(
		final MethodHandle<T, E> method,
		final Iterable<T> objects
	)
		throws E
	{
		final Throwable e = invoke(method, objects);
		if (e != null) {
			throw (E)e;
		}
	}

	private static <T, E extends Throwable> Throwable invoke(
		final MethodHandle<T, E> method,
		final Iterable<T> objects
	) {
		Throwable error = null;

		for (final T object : objects) {
			try {
				method.apply(object);
			} catch (Throwable e) {
				if (e instanceof Error) {
					throw (Error)e;
				} else if (error != null) {
					error.addSuppressed(e);
				} else {
					error = e;
				}
			}
		}

		return error;
	}

}
