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
import static org.jenetics.util.bit.shiftRight;
import static org.jenetics.util.bit.toByteLength;

import org.jenetics.util.StaticObject;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.6 &mdash; <em>$Date: 2014-03-01 $</em>
 * @since 1.6
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
	 * @throws java.lang.ArrayIndexOutOfBoundsException if start < 0 or
	 *         start > data.length*8
	 * @throws java.lang.IllegalArgumentException if start > end
	 * @throws java.lang.NullPointerException if the {@code data} array is
	 *         {@code null}.
	 */
	public static byte[] copy(final byte[] data, final int start, final int end) {
		if (start > end) {
			throw new IllegalArgumentException(String.format(
				"start > end: %d > %d", start, end
			));
		}
		if (start < 0 || start > data.length << 3) {
			throw new ArrayIndexOutOfBoundsException(String.format(
				"%d < 0 || %d > %d", start, start, data.length*8
			));
		}

		final int to = min(data.length << 3, end);
		final int byteStart = start >>> 3;
		final int bitStart = start & 7;
		final int bitLength = to - start;

		final byte[] copy = new byte[toByteLength(to - start)];

		if (copy.length > 0) {
			// Perform the byte wise right shift.
			System.arraycopy(data, byteStart, copy, 0, copy.length);

			// Do the remaining bit wise right shift.
			shiftRight(copy, bitStart);

			// Add the 'lost' bits from the next byte, if available.
			if (data.length > copy.length + byteStart) {
				copy[copy.length - 1] |= (byte)(data[byteStart + copy.length]
												<< (8 - bitStart));
			}

			// Trim (delete) the overhanging bits.
			copy[copy.length - 1] &= 0xFF >>> ((copy.length << 3) - bitLength);
		}

		return copy;
	}

}
