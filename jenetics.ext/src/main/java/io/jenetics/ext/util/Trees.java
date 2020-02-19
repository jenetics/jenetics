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

import java.util.Iterator;
import java.util.Objects;

import io.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 3.9
 */
final class Trees {
	private Trees() {}

	@SuppressWarnings("unchecked")
	static <V, T extends Tree<V, T>> T self(final Tree<?, ?> tree) {
		return (T)tree;
	}

	/**
	 * Builds the parents of node up to and including the root node, where the
	 * original node is the last element in the returned array. The length of
	 * the returned array gives the node's depth in the tree.
	 *
	 * @param node the node to get the path for
	 * @param depth  an int giving the number of steps already taken towards
	 *        the root (on recursive calls), used to size the returned array
	 * @return an array of nodes giving the path from the root to the specified
	 *         node
	 */
	static <V, T extends Tree<V, T>> MSeq<T> pathElementsFromRoot(
		final T node,
		final int depth
	) {
		final MSeq<T> path;
		if (node == null) {
			path = MSeq.ofLength(depth);
		} else {
			path = pathElementsFromRoot(
				node.parent().orElse(null),
				depth + 1
			);
			path.set(path.length() - depth - 1, node);
		}

		return path;
	}

	static <V, T extends Tree<V, T>> int[] pathFromRoot(
		final T node,
		final int depth
	) {
		final int[] path;
		if (node == null) {
			path = new int[depth - 1];
		} else {
			final T parent = node.parent().orElse(null);
			path = pathFromRoot(parent, depth + 1);

			if (parent != null) {
				final int index = node.parent()
					.map(p -> p.indexOf(node))
					.orElseThrow(AssertionError::new);

				path[path.length - depth - 1] = index;
			}
		}

		return path;
	}

	/**
	 * Checks if the two given trees has the same structure with the same values.
	 *
	 * @param a the first tree
	 * @param b the second tree
	 * @return {@code true} if the two given trees are structurally equals,
	 *         {@code false} otherwise
	 */
	static boolean equals(final Tree<?, ?> a, final Tree<?, ?> b) {
		boolean equals = a == b;
		if (!equals && a != null && b != null) {
			equals = a.childCount() == b.childCount();
			if (equals) {
				equals = Objects.equals(a.value(), b.value());
				if (equals && a.childCount() > 0) {
					equals = equals(a.childIterator(), b.childIterator());
				}
			}
		}

		return equals;
	}

	private static boolean equals(
		final Iterator<? extends Tree<?, ?>> a,
		final Iterator<? extends Tree<?, ?>> b
	) {
		boolean equals = true;
		while (a.hasNext() && equals) {
			equals = equals(a.next(), b.next());
		}

		return equals;
	}

	static int countChildren(final Tree<?, ?> tree) {
		int cnt = tree.childCount();
		for (int i = 0; i < tree.childCount(); ++i) {
			cnt += countChildren(tree.childAt(i));
		}
		return cnt;
	}

}
