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
import static org.jenetics.util.bit.get;
import static org.jenetics.util.bit.set;
import static org.jenetics.util.bit.toByteLength;

import org.jenetics.util.StaticObject;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-02-19 $</em>
 * @since @__version__@
 */
public final class internalbit extends StaticObject {
	private internalbit() {}

	/**
	 * Copies the specified range of the specified array into a new array.
	 *
	 * @param data the bits from which a range is to be copied
	 * @param start the initial index of the range to be copied, inclusive
	 * @param end the final index of the range to be copied, exclusive.
	 * @return a new array containing the specified range from the original array
	 * @throws java.lang.ArrayIndexOutOfBoundsException if from < 0 or
	 *         from > data.length*8
	 * @throws java.lang.IllegalArgumentException if from > to
	 */
	public static byte[] copy(final byte[] data, final int start, final int end) {
		if (start > end) {
			throw new IllegalArgumentException(String.format(
				"start > end: %d > %d", start, end
			));
		}
		if (start < 0 || start > data.length*8) {
			throw new ArrayIndexOutOfBoundsException(String.format(
				"%d < 0 || %d > %d", start, start, data.length*8
			));
		}

		final int toIndex = min(data.length*8, end);
		final int newLength = toByteLength(toIndex - start);
		final byte[] copy = new byte[newLength];

		for (int i = 0, n = toIndex - start; i < n; ++i) {
			set(copy, i, get(data, i + start));
		}

		return copy;
	}

}
