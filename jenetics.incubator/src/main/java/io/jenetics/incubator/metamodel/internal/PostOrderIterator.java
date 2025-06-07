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

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

/**
 * Postorder iterator which <em>recursively</em> traverses the object graph. It
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
 * final var it = new PostOrderIterator<>(
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
public final class PostOrderIterator<S, T> extends TraverseIterator<S, T> {

	private final Iterator<? extends T> children;

	private T root;
	private Iterator<? extends T> subtree;

	// Set for holding the already visited objects.
	private final Set<Object> visited;

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
	public PostOrderIterator(
		final S object,
		final Dtor<? super S, ? extends T> dtor,
		final Function<? super T, ? extends S> mapper,
		final Function<? super S, ?> unwrapper
	) {
		this(
			object, null, dtor, mapper, unwrapper,
			Collections.newSetFromMap(new IdentityHashMap<>())
		);
	}

	private PostOrderIterator(
		final S object,
		final T root,
		final Dtor<? super S, ? extends T> dtor,
		final Function<? super T, ? extends S> mapper,
		final Function<? super S, ?> unwrapper,
		final Set<Object> visited
	) {
		super(dtor, mapper, unwrapper);
		this.visited = requireNonNull(visited);

		this.root = root;

		final var id = unwrapper.apply(object);
		final var exists = !visited.add(id);
		children = exists
			? Collections.emptyIterator()
			: dtor.destruct(object).iterator();

		subtree = Collections.emptyIterator();
	}

	@Override
	public boolean hasNext() {
		return subtree.hasNext() || children.hasNext() || root != null;
	}

	@Override
	public T next() {
		final T result;
		if (subtree.hasNext()) {
			result = subtree.next();
		} else if (children.hasNext()) {
			final T next = children.next();

			subtree = new PostOrderIterator<>(
				mapper.apply(next), next, dtor, mapper, unwrapper, visited
			);

			result = subtree.next();
		} else {
			result = root;
			root = null;
		}

		return result;
	}

	/**
	 * Create a new (<em>property</em>) post-order iterator from the given
	 * arguments.
	 *
	 * @param object the root object of the model
	 * @param dtor the extractor function which extracts the direct
	 *        extractable properties
	 * @param unwrapper objects returned by this function are used for identifying
	 *        already visited source objects, for preventing infinite loops. This
	 *        method <em>unwraps</em> the object if {@code A} is a
	 *        <em>box</em>-type.
	 * @return a new post-order iterator for the given arguments
	 */
	public static <A> PostOrderIterator<A, A> of(
		final A object,
		final Dtor<? super A, ? extends A> dtor,
		final Function<? super A, ?> unwrapper
	) {
		return new PostOrderIterator<>(object, dtor, Function.identity(), unwrapper);
	}

}
