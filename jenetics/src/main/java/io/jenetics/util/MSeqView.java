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
package io.jenetics.util;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class MSeqView<T> implements MSeq<T> {

	private final List<T> _list;

	MSeqView(final List<T> list) {
		_list = requireNonNull(list);
	}

	@Override
	public T get(final int index) {
		return _list.get(index);
	}

	@Override
	public void set(final int index, final T value) {
		_list.set(index, value);
	}

	@Override
	public int length() {
		return _list.size();
	}

	@Override
	public MSeq<T> sort(
		final int start,
		final int end,
		final Comparator<? super T> comparator
	) {
		_list.subList(start, end).sort(comparator);
		return this;
	}

	@Override
	public MSeq<T> subSeq(final int start, final int end) {
		return new MSeqView<>(_list.subList(start, end));
	}

	@Override
	public MSeq<T> subSeq(final int start) {
		return new MSeqView<>(_list.subList(start, _list.size()));
	}

	@Override
	public <B> MSeq<B> map(final Function<? super T, ? extends B> mapper) {
		requireNonNull(mapper);

		final MSeq<B> result = MSeq.ofLength(length());
		for (int i = 0; i < length(); ++i) {
			result.set(i, mapper.apply(get(i)));
		}

		return result;
	}

	@Override
	public MSeq<T> append(final Iterable<? extends T> values) {
		return MSeq.of(_list).append(values);
	}

	@Override
	public MSeq<T> prepend(final Iterable<? extends T> values) {
		return MSeq.of(_list).prepend(values);
	}

	@Override
	public ISeq<T> toISeq() {
		return ISeq.of(_list);
	}

	@Override
	public MSeq<T> copy() {
		return MSeq.of(_list);
	}

}
