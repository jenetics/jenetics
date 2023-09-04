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
package io.jenetics.ext.util;

import static java.util.Objects.requireNonNull;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Breadth-first search (BFS) traversing of the tree. It starts at the tree root
 * and explores the neighbor nodes first, before moving to the next level
 * neighbors.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
final class TreeNodeBreadthFirstIterator<V, T extends Tree<V, T>>
	implements Iterator<T>
{
	private final Queue<Iterator<T>> _queue = new ArrayDeque<>();

	/**
	 * Create a new breath-first iterator from the given {@code root} element.
	 *
	 * @param root the root element of the tree
	 * @throws NullPointerException if the given {@code root} node is
	 *         {@code null}
	 */
	TreeNodeBreadthFirstIterator(final T root) {
		requireNonNull(root);
		_queue.add(List.of(root).iterator());
	}

	@Override
	public boolean hasNext() {
		final Iterator<T> peek = _queue.peek();
		return peek != null && peek.hasNext();
	}

	@Override
	public T next() {
		final Iterator<T> it = _queue.peek();
		if (it == null) {
			throw new NoSuchElementException("No next element.");
		}

		final T node = it.next();
		if (!it.hasNext()) {
			_queue.poll();
		}

		final Iterator<T> children = node.childIterator();
		if (children.hasNext()) {
			_queue.add(children);
		}

		return node;
	}

}
