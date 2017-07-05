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

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class FlattenedTreeNode<T> {

	private final T _value;
	private final int _arity;
	private final int _childOffset;

	private FlattenedTreeNode(
		final T value,
		final int arity,
		final int childOffset
	) {
		_value = value;
		_arity = arity;
		_childOffset = childOffset;
	}

	public T getValue() {
		return _value;
	}

	public int getArity() {
		return _arity;
	}

	public int getChildOffset() {
		return _childOffset;
	}

	@Override
	public String toString() {
		return Objects.toString(_value);
	}

	public static <V, T extends Tree<V, T>> ISeq<FlattenedTreeNode<V>>
	flatten(final T tree)  {
		requireNonNull(tree);

		final List<FlattenedTreeNode<V>> result = new ArrayList<>();
		final Iterator<T> it = tree.breadthFirstIterator();

		int childOffset = 1;
		while (it.hasNext()) {
			final T node  = it.next();
			result.add(new FlattenedTreeNode<>(
				node.getValue(), node.childCount(), childOffset
			));

			childOffset += node.childCount();
		}

		return ISeq.of(result);
	}

	public static <V> TreeNode<V> unflatten(final List<FlattenedTreeNode<V>> seq) {
		return unflatten(TreeNode.of(), 0, seq);
	}

	private static <V> TreeNode<V> unflatten(
		final TreeNode<V> tree,
		final int index,
		final List<FlattenedTreeNode<V>> seq
	) {
		if (index < seq.size()) {
			final FlattenedTreeNode<V> node = seq.get(index);
			tree.setValue(node.getValue());

			/*
			int childOffset = 1;
			for (int i = 0; i < index; ++i) {
				childOffset += seq.get(i).getArity();
			}
			*/

			for (int i = 0; i < node.getArity(); ++i) {
				tree.attach(
					unflatten(
						TreeNode.of(),
						node.getChildOffset() + i,
						seq
					)
				);
			}
		}

		return tree;
	}

}
