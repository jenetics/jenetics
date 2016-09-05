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

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
final class utils {
	private utils() {}

	@SafeVarargs
	public static <T> List<T> listOf(final T... elements) {
		return Collections.unmodifiableList(Arrays.asList(elements));
	}

	static int readInt(final byte[] bytes, final int index) {
		final int offset = index*Integer.BYTES;
		if (offset + Integer.BYTES > bytes.length) {
			throw new IndexOutOfBoundsException(format(
				"Not enough data to read int value (index=%d, bytes=%d).",
				index, bytes.length
			));
		}

		return
			((bytes[offset + 0] & 255) << 24) +
			((bytes[offset + 1] & 255) << 16) +
			((bytes[offset + 2] & 255) << 8) +
			((bytes[offset + 3] & 255));
	}

	public static byte[] toBytes(final int value) {
		final byte[] bytes = new byte[4];
		bytes[0] = (byte)(value >>> 24);
		bytes[1] = (byte)(value >>> 16);
		bytes[2] = (byte)(value >>>  8);
		bytes[3] = (byte) value;
		return bytes;
	}

	static long readLong(final byte[] bytes, final int index) {
		final int offset = index*Long.BYTES;
		if (offset + Long.BYTES > bytes.length) {
			throw new IndexOutOfBoundsException(format(
				"Not enough data to read long value (index=%d, bytes=%d).",
				index, bytes.length
			));
		}

		return
			((long)(bytes[offset + 0] & 255) << 56) +
			((long)(bytes[offset + 1] & 255) << 48) +
			((long)(bytes[offset + 2] & 255) << 40) +
			((long)(bytes[offset + 3] & 255) << 32) +
			((long)(bytes[offset + 4] & 255) << 24) +
			((bytes[offset + 5] & 255) << 16) +
			((bytes[offset + 6] & 255) <<  8) +
			(bytes[offset + 7] & 255);
	}

	public static byte[] toBytes(final long value) {
		final byte[] bytes = new byte[8];
		bytes[0] = (byte)(value >>> 56);
		bytes[1] = (byte)(value >>> 48);
		bytes[2] = (byte)(value >>> 40);
		bytes[3] = (byte)(value >>> 32);
		bytes[4] = (byte)(value >>> 24);
		bytes[5] = (byte)(value >>> 16);
		bytes[6] = (byte)(value >>>  8);
		bytes[7] = (byte) value;
		return bytes;
	}

}
