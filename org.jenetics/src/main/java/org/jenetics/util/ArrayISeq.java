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
package org.jenetics.util;

import static org.jenetics.util.object.nonNull;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
class ArrayISeq<T> extends ArraySeq<T> implements ISeq<T> {
	private static final long serialVersionUID = 1L;


	ArrayISeq(final ArrayRef array, final int start, final int end) {
		super(array, start, end);
	}

	@Override
	public ISeq<T> subSeq(final int start, final int end) {
		checkIndex(start, end);
		return new ArrayISeq<>(_array, start + _start, end + _start);
	}

	@Override
	public ISeq<T> subSeq(final int start) {
		return subSeq(start, length());
	}

	@Override
	public <B> ISeq<B> map(final Function<? super T, ? extends B> converter) {
		nonNull(converter, "Converter");

		final int length = length();
		final ArrayISeq<B> result = new ArrayISeq<>(new ArrayRef(length), 0, length);
		assert (result._array.data.length == length);

		for (int i = length; --i >= 0;) {
			@SuppressWarnings("unchecked")
			final T value = (T)_array.data[i + _start];
			result._array.data[i] = converter.apply(value);
		}
		return result;
	}

	@Override
	public MSeq<T> copy() {
		return new Array<>(new ArrayRef(toArray()), 0, length());
	}

}

