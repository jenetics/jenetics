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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Node<T> {

	private final T _value;
	private final int _arity;
	private final int _childOffset;

	private Node(final T value, final int arity, final int childOffset) {
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
		return format("%s[%d]", _value, _arity);
	}

	public static <T> Node<T> of(final T value, final int arity, final int childOffset) {
		return new Node<>(value, arity, childOffset);
	}


	public static <V, T extends Tree<V, T>> List<Node<V>> serialize(final T tree)  {
		final List<Node<V>> result = new ArrayList<>();
		final Iterator<T> it = tree.breadthFirstIterator();
		int childOffset = 1;

		while (it.hasNext()) {
			final T t  = it.next();
			final Node<V> node = Node.of(t.getValue(), t.childCount(), childOffset);
			result.add(node);
			childOffset += t.childCount();
		}

		return result;
	}

	public static <V> TreeNode<V> tree(final List<Node<V>> seq) {
		return fill(TreeNode.of(), 0, seq);
	}

	private static <V> TreeNode<V> fill(final TreeNode<V> tree, final int index, final List<Node<V>> seq) {
		if (index < seq.size()) {
			final Node<V> node = seq.get(index);
			tree.setValue(node.getValue());

			/*
			int firstChildOffset = 1;
			for (int i = 0; i < index; ++i) {
				firstChildOffset += seq.get(i).getArity();
			}
			*/

			for (int i = 0; i < node.getArity(); ++i) {
				final int childOffset = node.getChildOffset() + i;
				tree.attach(fill(TreeNode.of(), childOffset, seq));
			}
		}

		return tree;
	}

}
