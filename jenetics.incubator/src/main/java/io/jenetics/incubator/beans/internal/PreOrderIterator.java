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
package io.jenetics.incubator.beans.internal;

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

import io.jenetics.incubator.beans.Dtor;

/**
 * Preorder iterator which <em>recursively</em> traverses the object graph. It
 * also tracks already visited nodes to prevent infinite loops in the traversal.
 *
 * The following code example shows how to recursively travers the properties of
 * a simple domain model:
 * {@snippet lang="java":
 * record Author(String forename, String surname) { }
 * record Book(String title, int pages, List<Author> authors) { }
 *
 * final var book = new Book(
 *     "Oliver Twist",
 *     366,
 *     List.of(new Author("Charles", "Dickens"))
 * );
 *
 * final var it = new PreOrderIterator<>(
 *     PathValue.of(book),
 *     Properties::extract,
 *     property -> PathValue.of(property.path(), property.value().value()),
 *     PathValue::value
 * );
 *
 * it.forEachRemaining(System.out::println);
 * }
 *
 * @param <S> the source object type
 * @param <T> the type of the extracted objects
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public final class PreOrderIterator<S, T> implements Iterator<T> {

	private final Dtor<? super S, ? extends T> dtor;
	private final Function<? super T, ? extends S> mapper;
	private final Function<? super S, ?> identity;

	private final Deque<Iterator<? extends T>> deque = new ArrayDeque<>();

	// Set for holding the already visited objects.
	private final Set<Object> visited =
		Collections.newSetFromMap(new IdentityHashMap<>());

	/**
	 * Create a new (<em>property</em>) pre-order iterator from the given
	 * arguments.
	 *
	 * @param object the root object of the model
	 * @param dtor the extractor function which extracts the direct
	 *        extractable properties
	 * @param mapper mapper function for creating the source object for the
	 *        next level from the extracted objects of type {@code T}
	 * @param identity objects, returned by this function are used for identifying
	 *        already visited source objects, for preventing infinite loops
	 */
	public PreOrderIterator(
		final S object,
		final Dtor<? super S, ? extends T> dtor,
		final Function<? super T, ? extends S> mapper,
		final Function<? super S, ?> identity
	) {
		this.dtor = requireNonNull(dtor);
		this.mapper = requireNonNull(mapper);
		this.identity = requireNonNull(identity);

		deque.push(dtor.unapply(object).iterator());
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
		final var exists = !visited.add(identity.apply(source));
		final Iterator<? extends T> children = exists
			? Collections.emptyIterator()
			: dtor.unapply(source).iterator();

		if (children.hasNext()) {
			deque.push(children);
		}

		return node;
	}

	/**
	 * Creates a {@code Stream} from {@code this} iterator.
	 *
	 * @return a {@code Stream} from {@code this} iterator
	 */
	public Stream<T> stream() {
		return StreamSupport.stream(
			spliteratorUnknownSize(this, Spliterator.SIZED),
			false
		);
	}

	/**
	 * Create an <em>recursive</em> extractor function from the given arguments.
	 *
	 * @param dtor the extractor function which extracts the direct
	 *        extractable properties
	 * @param mapper mapper function for creating the source object for the
	 *        next level from the extracted objects of type {@code T}
	 * @param identity objects, returned by this function are used for identifying
	 *        already visited source objects, for preventing infinite loops
	 * @return an <em>recursive</em> extractor function
	 * @param <S> the source object type
	 * @param <T> the type of the extracted objects
	 */
	public static <S, T> Dtor<S, T> dtor(
		final Dtor<? super S, ? extends T> dtor,
		final Function<? super T, ? extends S> mapper,
		final Function<? super S, ?> identity
	) {
		return source -> new PreOrderIterator<S, T>(
			source, dtor, mapper, identity
		).stream();
	}

}
