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

import org.jenetics.internal.util.require;

import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Trees {
	private Trees() {require.noInstance();}


	@SuppressWarnings("unchecked")
	static <V, T extends Tree<V, T>> T self(final Object tree) {
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
	static <V, T extends Tree<V, T>> MSeq<T> pathToRoot(
		final T node,
		final int depth
	) {
		final MSeq<T> path;
		if (node == null) {
			path = depth == 0 ? MSeq.empty() : MSeq.ofLength(depth);
		} else {
			path = pathToRoot(node.getParent().orElse(null), depth + 1);
			path.set(path.length() - depth - 1, node);
		}

		return path;
	}

}
