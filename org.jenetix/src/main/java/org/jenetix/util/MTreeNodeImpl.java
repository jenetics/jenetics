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
import java.util.List;

import javax.swing.tree.MutableTreeNode;

import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class MTreeNodeImpl<T> implements MTreeNode<T> {

	MutableTreeNode n;

	private T _value;
	private MTreeNode<? super T> _parent;
	private final List<MTreeNode<? extends T>> _children = new ArrayList<>();

	MTreeNodeImpl(final T value) {
		_value = value;
	}

	@Override
	public MTreeNode<? super T> getParent() {
		return _parent;
	}

	@Override
	public MTreeNode<T> setParent(final MTreeNode<? super T> parent) {
		_parent = parent;
		return this;
	}

	@Override
	public T getValue() {
		return _value;
	}

	@Override
	public MTreeNode<T> setValue(final T value) {
		_value = value;
		return this;
	}

	@Override
	public ISeq<MTreeNode<? extends T>> getChildren() {
		return ISeq.of(_children);
	}

	@Override
	public MTreeNode<? extends T> getChild(final int index) {
		return _children.get(index);
	}

	@Override
	public int getChildCount() {
		return _children.size();
	}

	@Override
	public MTreeNode<T> add(final MTreeNode<? extends T> node) {
		node.setParent(this);
		_children.add(node);
		return this;
	}

	@Override
	public MTreeNode<T> add(int index, MTreeNode<? extends T> node) {
		requireNonNull(node);
		if (isNodeAncestor(node)) {
			throw new IllegalArgumentException("New child is an ancestor.");
		}

		if (getParent() != null) {
			getParent().remove(node);
		}
		node.setParent(this);
		_children.add(index, node);

		return this;
	}

	/**
	 * Returns {@code true} if {@code anotherNode} is an ancestor of this node
	 * -- if it is this node, this node's parent, or an ancestor of this node's
	 * parent. (Note that a node is considered an ancestor of itself.) If
	 * {@code anotherNode} is {@code null}, this method returns {@code false}.
	 * This operation is at worst O(h) where h is the distance from the root to
	 * this node.
	 *
	 * //@see             #isNodeDescendant
	 * //@see             #getSharedAncestor
	 * @param   anotherNode     node to test as an ancestor of this node
	 * @return  true if this node is a descendant of <code>anotherNode</code>
	 */
	public boolean isNodeAncestor(final MTreeNode<? extends T> anotherNode) {
		if (anotherNode == null) {
			return false;
		}

		MTreeNode<?> ancestor = this;
		do {
			if (ancestor == anotherNode) {
				return true;
			}
		} while((ancestor = ancestor.getParent()) != null);

		return false;
	}

	@Override
	public MTreeNode<T> remove(final MTreeNode<? extends T> node) {
		requireNonNull(node);

		if (!isNodeChild(node)) {
			throw new IllegalArgumentException("argument is not a child");
		}
		remove(getIndex(node));
		return this;
	}

	/**
	 * Removes the child at the specified {@code index} from this node's
	 * children and sets that node's parent to {@code null}.
	 *
	 * @param index the index in this node's child array of the child to remove
	 * @throws ArrayIndexOutOfBoundsException if {@code index} is out of bounds
	 */
	public MTreeNode<T> remove(final int index) {
		_children.remove(index).setParent(null);
		return this;
	}

	/**
	 * Returns {@code true} if {@code node} is a child of {@code this} node. If
	 * {@code node} is {@code node}, this method returns {@code false}.
	 *
	 * @return {@code true} if {@code node} is a child of {@code this} node;
	 *         {@code false} if {@code node} is {@code null}
	 */
	public boolean isNodeChild(final TreeNode<? extends T> node) {
		return node != null &&
			getChildCount() != 0 &&
			node.getParent() == this;
	}

	/**
	 * Returns the index of the specified child in this node's child array.
	 * If the specified node is not a child of this node, returns {@code -1}.
	 * This method performs a linear search and is O(n) where n is the number of
	 * children.
	 *
	 * @param node the {@code MTreeNode} to search for among this node's children
	 * @return an int giving the index of the node in this node's child array,
	 *         or {@code -1} if the specified node is a not a child of this node
	 * @throws NullPointerException if the given {@code node} is {@code null}
	 */
	public int getIndex(final MTreeNode<? extends T> node) {
		requireNonNull(node);

		return isNodeChild(node)
			? _children.indexOf(node)
			: -1;
	}

	@Override
	public MTreeNode<T> removeFromParent() {
		_parent = null;
		return this;
	}

	@Override
	public String toString() {
		return toString(this, new StringBuilder(), 0).toString();
	}

	private StringBuilder toString(final TreeNode<?> node, final StringBuilder out, final int level) {
		for (int i = 0; i < level; ++i) {
			out.append("  ");
		}

		out.append("+- ").append(node.getValue()).append("\n");
		node.getChildren().forEach(c -> toString(c, out, level + 1));
		return out;
	}

	public static void main(final String[] args) {
		final MTreeNode<Integer> tree = MTreeNode.of(0)
			.add(MTreeNode.of(1))
			.add(MTreeNode.of(2)
				.add(MTreeNode.of(3))
				.add(MTreeNode.of(4)))
			.add(MTreeNode.of(5));

		System.out.println(tree);
	}

}
