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

import static java.lang.Math.min;

import org.jenetics.util.Concurrent;
import org.jenetics.util.Factory;
import org.jenetics.util.StaticObject;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__version__@
 * @version @__version__@ &mdash; <em>$Date$</em>
 */
public final class arrays extends StaticObject {
	private arrays() {}

	private static int MIN_BULK_SIZE = 11;

	public static Object[] fill(final Object[] array, final Factory<?> factory) {
		if (array.length > 0) {
			try (Concurrent c = new Concurrent()) {
				final int threads = c.getParallelism();
				final int[] parts = org.jenetics.util.arrays.partition(
					array.length, min(threads, array.length/MIN_BULK_SIZE)
				);

				for (int i = 0; i < parts.length - 1; ++i) {
					final int part = i;

					c.execute(new Runnable() { @Override public void run() {
						for (int j = parts[part]; j < parts[part + 1]; ++j) {
							array[j] = factory.newInstance();
						}
					}});
				}
			}
		}

		return array;
	}

}


