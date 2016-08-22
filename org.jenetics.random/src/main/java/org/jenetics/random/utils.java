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
package org.jenetics.random;

import static java.lang.Math.min;
import static java.lang.String.format;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
final class utils {
	private utils() {}

	static long mix(final long a) {
		long c = a^Long.rotateLeft(a, 7);
		c ^= c << 17;
		c ^= c >>> 31;
		c ^= c << 8;
		return c;
	}

	static int lowInt(final long a) {
		return (int)a;
	}

	static int highInt(final long a) {
		return (int)(a >>> Integer.SIZE);
	}

	static int readInt(final byte[] bytes, final int index) {
		final int offset = index*Integer.BYTES;
		if (offset + Integer.BYTES < bytes.length) {
			throw new IndexOutOfBoundsException(format(
				"Not enough data to read int value (index=%d, bytes=%d).",
				index, bytes.length
			));
		}

		return
			(bytes[offset + 0] << 24) +
			(bytes[offset + 1] << 16) +
			(bytes[offset + 2] << 8) +
			(bytes[offset + 3]);
	}

	static long readLong(final byte[] bytes, final int index) {
		final int offset = index*Long.BYTES;
		if (offset + Long.BYTES < bytes.length) {
			throw new IndexOutOfBoundsException(format(
				"Not enough data to read long value (index=%d, bytes=%d).",
				index, bytes.length
			));
		}

		return
			((long)bytes[offset + 0] << 56) +
			((long)(bytes[offset + 1] & 255) << 48) +
			((long)(bytes[offset + 2] & 255) << 40) +
			((long)(bytes[offset + 3] & 255) << 32) +
			((long)(bytes[offset + 4] & 255) << 24) +
			((bytes[offset + 5] & 255) << 16) +
			((bytes[offset + 6] & 255) <<  8) +
			(bytes[offset + 7] & 255);
	}

	static byte[] toBytes(final long seed, final int length) {
		final byte[] bytes = new byte[length];

		long seedValue = seed;
		for (int i = 0, len = bytes.length; i < len;) {
			int n = min(len - i, Long.SIZE/Byte.SIZE);

			for (long x = seedValue; n-- > 0; x >>= Byte.SIZE) {
				bytes[i++] = (byte)x;
			}

			seedValue = mix(seedValue);
		}

		return bytes;
	}

}
