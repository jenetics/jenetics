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

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Preorder iterator of the tree.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
final class TreeNodePreorderIterator<V, T extends Tree<V, T>>
	implements Iterator<T>
{
	private final Deque<Iterator<T>> _deque = new LinkedList<>();

	/**
	 * Create a new preorder iterator of the given tree {@code root}.
	 *
	 * @param root the root node of the tree
	 * @throws NullPointerException if the given {@code root} node is
	 *        {@code null}
	 */
	TreeNodePreorderIterator(final T root) {
		requireNonNull(root);
		_deque.push(singletonList(root).iterator());
	}

	@Override
	public boolean hasNext() {
		final Iterator<T> peek = _deque.peek();
		return peek != null && peek.hasNext();
	}

	@Override
	public T next() {
		final Iterator<T> it = _deque.peek();
		if (it == null) {
			throw new NoSuchElementException("No next element.");
		}

		final T node = it.next();
		if (!it.hasNext()) {
			_deque.pop();
		}

		final Iterator<T> children = node.childIterator();
		if (children.hasNext()) {
			_deque.push(children);
		}

		return node;
	}
}
