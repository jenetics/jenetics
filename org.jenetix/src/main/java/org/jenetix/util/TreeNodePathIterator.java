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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetix.util;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Path (between nodes) iterator.
 */
final class TreeNodePathIterator<T>
	implements Iterator<TreeNode<T>>
{
	private final Deque<TreeNode<T>> _stack = new LinkedList<>();

	TreeNodePathIterator(
		final TreeNode<T> ancestor,
		final TreeNode<T> descendant
	) {
		requireNonNull(ancestor);
		_stack.push(requireNonNull(descendant));

		TreeNode<T> current = descendant;
		while (current != ancestor) {
			current = current.getParent().orElseThrow(() ->
				new IllegalArgumentException(format(
					"Node %s is not an ancestor of %s.",
					ancestor, descendant
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
	public TreeNode<T> next() {
		if (_stack.isEmpty()) {
			throw new NoSuchElementException("No more elements.");
		}

		return _stack.pop();
	}

}
