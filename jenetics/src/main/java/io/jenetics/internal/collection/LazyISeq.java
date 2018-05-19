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
package io.jenetics.internal.collection;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import java.util.function.IntFunction;

import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class LazyISeq<T> implements ISeq<T> {

	private final IntFunction<? extends T> _values;
	private final int _start;
	private final int _length;

	private LazyISeq(
		final IntFunction<? extends T> values,
		final int from,
		final int until
	) {
		_values = requireNonNull(values);
		_start = from;
		_length = until - from;
	}

	@Override
	public T get(final int index) {
		checkIndex(index);
		return _values.apply(index + _start);
	}

	private void checkIndex(final int index) {
		if (index < 0 || index >= length()) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Index %s is out of bounds [0, %s)", index, length()
			));
		}
	}

	@Override
	public int length() {
		return _length;
	}

	@Override
	public ISeq<T> subSeq(final int start) {
		return subSeq(start, length());
	}

	@Override
	public ISeq<T> subSeq(final int start, final int end) {
		checkIndex(start, end);
		return new LazyISeq<>(_values, start + _start, end + _start);
	}

	private void checkIndex(final int from, final int until) {
		Array.checkIndex(from, until, length());
	}

	@Override
	public <B> ISeq<B> map(final Function<? super T, ? extends B> mapper) {
		requireNonNull(mapper);
		return new LazyISeq<>(
			index -> mapper.apply(_values.apply(index)),
			_start,
			_start + _length
		);
	}

	@Override
	public ISeq<T> append(final Iterable<? extends T> values) {
		return copy().append(values).toISeq();
	}

	@Override
	public ISeq<T> prepend(final Iterable<? extends T> values) {
		return copy().prepend(values).toISeq();
	}

	@Override
	public MSeq<T> copy() {
		final MSeq<T> mseq = MSeq.ofLength(length());
		for (int i = 0; i < mseq.length(); ++i) {
			mseq.set(i, get(i));
		}
		return mseq;
	}

	static <T> ISeq<T> of(final IntFunction<? extends T> values, final int length) {
		return length == 0
			? ISeq.empty()
			: new LazyISeq<>(values, 0, length);
	}

}
