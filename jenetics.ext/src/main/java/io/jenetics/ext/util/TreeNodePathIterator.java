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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Path (between nodes) iterator.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.1
 * @since 3.9
 */
final class TreeNodePathIterator<V, T extends Tree<V, T>>
	implements Iterator<T>
{
	private final Deque<T> _stack = new ArrayDeque<>();

	/**
	 * Create an iterator between two tree nodes.
	 *
	 * @param ancestor the ancestor tree node
	 * @param descendant the descendant tree node
	 * @throws NullPointerException if one of the nodes is {@code null}
	 */
	TreeNodePathIterator(
		final Tree<?, ?> ancestor,
		final T descendant
	) {
		requireNonNull(ancestor);
		_stack.push(requireNonNull(descendant));

		T current = descendant;
		while (!current.identical(ancestor)) {
			current = current.getParent().orElseThrow(() ->
				new IllegalArgumentException(format(
					"Node %s is not an ancestor of %s.",
					ancestor.getValue(), descendant.getValue()
				))
			);

			_stack.push(current);
		}
	}

	@Override
	public boolean hasNext() {
		return !_stack.isEmpty();
	}

	@Override
	public T next() {
		return _stack.pop();
	}

}
