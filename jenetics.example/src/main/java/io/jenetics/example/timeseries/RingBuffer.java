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
package io.jenetics.example.timeseries;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A circular array buffer with a copy-and-swap cursor.
 *
 * <p>This class provides an list of T objects who's size is <em>unstable</em>.
 * It's intended for capturing data where the frequency of sampling greatly
 * outweighs the frequency of inspection (for instance, monitoring).</p>
 *
 * <p>This object keeps in memory a fixed size buffer which is used for
 * capturing objects.  It copies the objects to a snapshot array which may be
 * worked with.  The size of the snapshot array will vary based on the
 * stability of the array during the copy operation.</p>
 *
 * <p>Adding buffer to the buffer is <em>O(1)</em>, and lockless.  Taking a
 * stable copy of the sample is <em>O(n)</em>.</p>
 */
public class RingBuffer <T> {


	static final class Cursor {
		private final int _max;

		int _size = 0;
		int _cursor = -1;

		Cursor(final int max) {
			_max = max;
		}

		int next() {
			_cursor = (_cursor + 1)%_max;
			if (_size < _max) ++_size;
			return _cursor;
		}

	}


	final Object[] _buffer;
	final Cursor _cursor;


	public RingBuffer (final int size) {
		if (size < 1) {
			throw new IllegalArgumentException(
				"Buffer size must be a positive value"
			);
		}

		_buffer = new Object[size];
		_cursor = new Cursor(size);
	}

	public void add (final T sample) {
		_buffer[_cursor.next()] = sample;
	}


	public Object[] snapshot () {
		if (_cursor._size < _buffer.length) {
			final Object[] result = new Object[_cursor._size];
			System.arraycopy(_buffer, 0, result, 0, _cursor._size);
			return result;
		}

		final Object[] result = new Object[_buffer.length];
		System.arraycopy(
			_buffer, _cursor._cursor + 1,
			result, 0, _buffer.length - _cursor._cursor - 1
		);
		System.arraycopy(
			_buffer, 0,
			result, _buffer.length - _cursor._cursor - 1, _cursor._cursor + 1
		);

		return result;
	}

	public int size () {
		return _buffer.length;
	}
}
