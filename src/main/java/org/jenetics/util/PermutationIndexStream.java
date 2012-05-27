/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import java.util.Random;

/**
 * This class creates an index stream for one particular (pseudo) permutation.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version $Id$
 */
abstract class PermutationIndexStream extends IndexStream {

	final Random _random;
	final int _length;
	int _pos = 0;

	PermutationIndexStream(final int length, final Random random) {
		_random = random;
		_length = length;
	}

	public int getLength() {
		return _length;
	}

	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	final static class ArrayPermutation extends PermutationIndexStream {

		private final byte[] _array;

		ArrayPermutation(final int length, final Random random) {
			super(length, random);

			assert(length <= Byte.MAX_VALUE);
			_array = new byte[length];
			for (int i = 0; i < length; ++i) {
				_array[i] = (byte)i;
			}
			for (int j = _array.length - 1; j > 0; --j) {
				swap(_array, j, random.nextInt(j + 1));
			}
		}

		private static void swap(final byte[] array, final int i, final int j) {
			final byte temp = array[i];
			array[i] = array[j];
			array[j] = temp;
		}

		@Override
		public int next() {
			int next = -1;
			if (_pos < _array.length) {
				next = _array[_pos++];
			}

			return next;
		}
	}

	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	final static class StridePermutation extends PermutationIndexStream {

		private final int _stride;
		private final int _start;
		private int _calls = 0;

		StridePermutation(final int length, final Random random) {
			super(length, random);

			_stride = stride(_length, _random);
			_start = _random.nextInt(length);
			_pos = _start;

			assert(_stride < _length);
			assert(math.gcd(_stride, _length) == 1);
			assert(_start < _length);
		}

		// The stride has to be smaller than length and relative prime to length.
		// https://en.wikipedia.org/wiki/Coprime
		private static int stride(final int length, final Random random) {
			int value = length;

			// The probability that two numbers are coprime is ~ 61%
			while (math.gcd(length, value) != 1) {
				value = random.nextInt(length/2) + length/3;
			}

			return value;
		}

		@Override
		public int next() {
			int next = -1;
			if (_calls < _length) {
				next = _pos;
				_pos = (_pos + _stride)%_length;
				++_calls;
			}

			return next;
		}

		@Override
		public String toString() {
			return String.format(
				"Length: %d, start: %d, stride: %d",
				_length, _start, _stride
			);
		}

	}


	public static IndexStream valueOf(final int length) {
		return valueOf(length, new Random());
	}

	public static IndexStream valueOf(final int length, final Random random) {
		if (length < 0) {
			throw new IllegalArgumentException(
				"Length must be greater than zero: " + length
			);
		}

		PermutationIndexStream stream = null;
		if (length <= Byte.MAX_VALUE) {
			stream = new ArrayPermutation(length, random);
		} else {
			stream = new StridePermutation(length, random);
		}
		return stream;
	}


}










