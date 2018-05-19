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

import java.util.function.Function;

import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class MappedISeq<T, A> implements ISeq<T> {

	private final A _array;
	private final ElementGetter<A, T> _getter;


	public MappedISeq(final A array, final ElementGetter<A, T> getter) {
		_array = array;
		_getter = getter;
	}

	public A array() {
		return _array;
	}

	@Override
	public ISeq<T> subSeq(int start, int end) {
		return null;
	}

	@Override
	public ISeq<T> subSeq(int start) {
		return null;
	}

	@Override
	public T get(int index) {
		return null;
	}

	@Override
	public int length() {
		return 0;
	}

	@Override
	public <B> ISeq<B> map(Function<? super T, ? extends B> mapper) {
		return null;
	}

	@Override
	public ISeq<T> append(Iterable<? extends T> values) {
		return null;
	}

	@Override
	public ISeq<T> prepend(Iterable<? extends T> values) {
		return null;
	}

	@Override
	public MSeq<T> copy() {
		return null;
	}
}
