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
package io.jenetics.incubator.metamodel.internal;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;

/**
 * Preorder iterator which <em>recursively</em> traverses the object graph. It
 * also tracks already visited nodes to prevent infinite loops in the traversal.
 * The following code example shows how to recursively traverse the properties of
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
public final class PreOrderIterator<S, T> extends TraverseIterator<S, T> {

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
	 * @param unwrapper objects returned by this function are used for identifying
	 *        already visited source objects, for preventing infinite loops. This
	 *        method <em>unwraps</em> the object if {@code A} is a
	 *        <em>box</em>-type.
	 */
	public PreOrderIterator(
		final S object,
		final Dtor<? super S, ? extends T> dtor,
		final Function<? super T, ? extends S> mapper,
		final Function<? super S, ?> unwrapper
	) {
		super(dtor, mapper, unwrapper);

		deque.push(dtor.destruct(object).iterator());
		visited.add(unwrapper.apply(object));
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
		final var exists = !visited.add(unwrapper.apply(source));
		final Iterator<? extends T> children = exists
			? Collections.emptyIterator()
			: dtor.destruct(source).iterator();

		if (children.hasNext()) {
			deque.push(children);
		}

		return node;
	}

	/**
	 * Create a new (<em>property</em>) pre-order iterator from the given
	 * arguments.
	 *
	 * @param object the root object of the model
	 * @param dtor the extractor function which extracts the direct
	 *        extractable properties
	 * @param unwarpper objects returned by this function are used for identifying
	 *        already visited source objects, for preventing infinite loops. This
	 *        method <em>unwraps</em> the object if {@code A} is a
	 *        <em>box</em>-type.
	 * @return a new pre-order iterator for the given arguments
	 */
	public static <A> PreOrderIterator<A, A> of(
		final A object,
		final Dtor<? super A, ? extends A> dtor,
		final Function<? super A, ?> unwarpper
	) {
		return new PreOrderIterator<A, A>(object, dtor, Function.identity(), unwarpper);
	}

}
