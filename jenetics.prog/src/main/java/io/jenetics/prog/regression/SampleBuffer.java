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
package io.jenetics.prog.regression;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class holds the actual sample values which are used for the symbolic
 * regression example. This class is <em>thread-safe</em> and can be used in a
 * <em>producer-consumer</em> setup.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class SampleBuffer<T> implements Iterable<Sample<T>> {

	private final RingBuffer _buffer;

	private Object[] _snapshot = {};

	private SampleBuffer(final int size) {
		_buffer = new RingBuffer(size);
	}

	public void add(final Sample<T> sample) {
		_buffer.add(sample);
	}

	public void addAll(final Collection<? extends Sample<T>> samples) {
		_buffer.addAll(samples);
	}

	@Override
	public Iterator<Sample<T>> iterator() {
		final Object[] array = _snapshot;

		return new Iterator<>() {
			private int cursor = 0;
			int lastElement = -1;

			@Override
			public boolean hasNext() {
				return cursor != array.length;
			}

			@Override
			public Sample<T> next() {
				final int i = cursor;
				if (cursor >= array.length) {
					throw new NoSuchElementException();
				}

				cursor = i + 1;
				@SuppressWarnings("unchecked")
				final Sample<T> element = (Sample<T>)array[lastElement = i];
				return element;
			}
		};
	}

}
