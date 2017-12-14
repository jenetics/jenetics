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
package io.jenetics.ext.internal;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Function;

import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 4.1
 */
public class SeqView<T> implements Seq<T> {

	private final List<T> _list;

	private SeqView(final List<T> list) {
		_list = requireNonNull(list);
	}

	@Override
	public T get(final int index) {
		return _list.get(index);
	}

	@Override
	public int length() {
		return _list.size();
	}

	@Override
	public <B> Seq<B> map(final Function<? super T, ? extends B> mapper) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Seq<T> append(final Iterable<? extends T> values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Seq<T> prepend(final Iterable<? extends T> values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Seq<T> subSeq(final int start) {
		return new SeqView<>(_list.subList(start, length()));
	}

	@Override
	public Seq<T> subSeq(final int start, final int end) {
		return new SeqView<>(_list.subList(start, end));
	}

	public static <T> Seq<T> of(final List<T> list) {
		return new SeqView<>(list);
	}

}
