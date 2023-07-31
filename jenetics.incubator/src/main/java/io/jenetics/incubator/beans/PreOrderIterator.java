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
package io.jenetics.incubator.beans;

import static java.util.Objects.requireNonNull;
import static java.util.Spliterators.spliteratorUnknownSize;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Preorder iterator which <em>recursively</em> traverses the object graph. It
 * also tracks already visited nodes to prevent infinite loops in the traversal.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class PreOrderIterator<S, T> implements Iterator<T> {

	private final Extractor<? super S, ? extends T> extractor;
	private final Function<? super T, ? extends S> mapper;
	private final Function<? super S, ?> identity;
	private final Deque<Iterator<? extends T>> deque = new ArrayDeque<>();

	// Set for holding the already visited objects.
	private final Set<Object> visited =
		Collections.newSetFromMap(new IdentityHashMap<>());

	public PreOrderIterator(
		final S object,
		final Extractor<? super S, ? extends T> extractor,
		final Function<? super T, ? extends S> mapper,
		final Function<? super S, ?> identity
	) {
		this.extractor = requireNonNull(extractor);
		this.mapper = requireNonNull(mapper);
		this.identity = requireNonNull(identity);

		deque.push(extractor.extract(object).iterator());
		visited.add(identity.apply(object));
	}

	@Override
	public boolean hasNext() {
		final var peek = deque.peek();
		return peek != null && peek.hasNext();
	}

	@Override
	public T next() {
		final Iterator<? extends T> it = deque.peek();
		if (it == null) {
			throw new NoSuchElementException("No next element.");
		}

		final T node = it.next();
		if (!it.hasNext()) {
			deque.pop();
		}

		final S source = mapper.apply(node);
		final Iterator<? extends T> children =
			!visited.add(identity.apply(source))
				? Collections.emptyIterator()
				: extractor.extract(source).iterator();

		if (children.hasNext()) {
			deque.push(children);
		}

		return node;
	}

	public Stream<T> stream() {
		return StreamSupport.stream(
			spliteratorUnknownSize(this, Spliterator.SIZED),
			false
		);
	}

	public static <S, T> Extractor<S, T> extractor(
		final Extractor<? super S, ? extends T> extractor,
		final Function<? super T, ? extends S> mapper,
		final Function<? super S, ?> identity
	) {
		return source -> new PreOrderIterator<S, T>(
			source, extractor, mapper, identity
		).stream();
	}

}
