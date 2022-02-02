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

import io.jenetics.util.ISeq;

/**
 * Tree specification, where the nodes of the whole tree are stored in an array.
 * The tree
 * <pre>
 * 0
 * ├── 1
 * │   ├── 4
 * │   └── 5
 * ├── 2
 * │   └── 6
 * └── 3
 *     ├── 7
 *     │   ├── 10
 *     │   └── 11
 *     ├── 8
 *     └── 9
 * </pre>
 * will be stored in breadth-first order and will look like this:
 * <pre>
 * ┌─┬─┬─┐       ┌──────┬──┐
 * 0 1 2 3 4 5 6 7 8 9 10 11
 *   └─│─│─┴─┘ │ │ │ │
 *     └─│─────┘ │ │ │
 *       └───────┴─┴─┘
 * </pre>
 * The child nodes are always stored on the right side of the parent flattened
 * Nodes. So you have to read the tree from left to right. All children of a
 * parent node are stored continuously after the {@code childOffset} and are
 * defined by the sub-array {@code [childOffset, childOffset + childCount)}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
public interface FlatTree<V, T extends FlatTree<V, T>> extends Tree<V, T> {

	/**
	 * Return the index of the first child node in the underlying node array.
	 * {@code -1} is returned if {@code this} node is a leaf.
	 *
	 * @return Return the index of the first child node in the underlying node
	 *         array, or {@code -1} if {@code this} node is a leaf
	 */
	int childOffset();

	/**
	 * Return the whole flattened tree values in breadth-first order. This is
	 * equivalent to
	 * <pre>{@code
	 * final ISeq<T> seq = getRoot().breadthFirstStream()
	 *     .collect(ISeq.toISeq());
	 * }</pre>
	 *
	 * @return the flattened tree values in breadth-first order
	 */
	default ISeq<T> flattenedNodes() {
		return root().breadthFirstStream().collect(ISeq.toISeq());
	}

}
