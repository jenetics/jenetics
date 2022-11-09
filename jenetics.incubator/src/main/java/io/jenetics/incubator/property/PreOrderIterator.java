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
package io.jenetics.incubator.property;

import static java.util.Objects.requireNonNull;
import static java.util.Spliterators.spliteratorUnknownSize;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Preorder property iterator.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class PreOrderIterator<S, T> implements Iterator<T> {

	private final Extractor<? super S, ? extends T> reader;
	private final Function<? super T, ? extends S> mapper;
	private final Deque<Iterator<? extends T>> deque = new ArrayDeque<>();

	PreOrderIterator(
		final S object,
		final Extractor<? super S, ? extends T> reader,
		final Function<? super T, ? extends S> mapper
	) {
		this.reader = requireNonNull(reader);
		this.mapper = requireNonNull(mapper);
		deque.push(reader.extract(object).iterator());
	}

	@Override
	public boolean hasNext() {
		final var peek = deque.peek();
		return peek != null && peek.hasNext();
	}

	@Override
	public T next() {
		final var it = deque.peek();
		if (it == null) {
			throw new NoSuchElementException("No next element.");
		}

		final T node = it.next();
		if (!it.hasNext()) {
			deque.pop();
		}

		final var children = reader
			.extract(mapper.apply(node))
			.iterator();
		if (children.hasNext()) {
			deque.push(children);
		}

		return node;
	}

	Stream<T> stream() {
		return StreamSupport.stream(
			spliteratorUnknownSize(this, Spliterator.SIZED),
			false
		);
	}

}
