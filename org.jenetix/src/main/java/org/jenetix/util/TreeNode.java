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
import static org.jenetics.internal.math.random.nextInt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;

import org.jenetics.util.Copyable;
import org.jenetics.util.IntRange;

/**
 * A general purpose node in a tree data-structure.
 *
 * @param <T> the value type of the tree node
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class TreeNode<T>
	implements
		Tree<T, TreeNode<T>>,
		Iterable<TreeNode<T>>,
		Copyable<TreeNode<T>>,
		Serializable
{
	private static final long serialVersionUID = -1L;

	private T _value;
	private TreeNode<T> _parent;
	private final List<TreeNode<T>> _children = new ArrayList<>();

	/**
	 * Create a new tree node with no parent and children, but with the given
	 * user {@code value}.
	 *
	 * @param value the user value of the new tree node
	 */
	private TreeNode(final T value) {
		_value = value;
	}


	/* *************************************************************************
	 * Basic operations
	 **************************************************************************/

	/**
	 * Sets the user object for this node.
	 *
	 * @param value the node {@code value}
	 */
	public void setValue(final T value) {
		_value = value;
	}

	/**
	 * Return the node value
	 *
	 * @return the node value
	 */
	@Override
	public T getValue() {
		return _value;
	}

	/**
	 * Returns this node's parent if available.
	 *
	 * @return the tree-node, or an empty value if this node has no parent
	 */
	@Override
	public Optional<TreeNode<T>> getParent() {
		return Optional.ofNullable(_parent);
	}

	/**
	 * Sets this node's parent, but does not change the parent's child array.
	 * This method is called from {@code insert()} and {@code remove()} to
	 * reassign a child's parent, and it should not be messaged from anywhere
	 * else.
	 *
	 * @param parent this node's new parent
	 */
	void setParent(final TreeNode<T> parent) {
		_parent = parent;
	}

	/**
	 * Returns the child at the specified index in this node's child array.
	 *
	 * @param index   an index into this node's child array
	 * @return the tree-node in this node's child array at the specified index
	 * @throws ArrayIndexOutOfBoundsException  if the {@code index} is out of
	 *         bounds
	 */
	@Override
	public TreeNode<T> getChild(final int index) {
		return _children.get(index);
	}

	/**
	 * Returns the number of children of {@code this} node.
	 *
	 * @return the number of children of {@code this} node
	 */
	@Override
	public int childCount() {
		return _children.size();
	}

	/**
	 * Removes the {@code child} from its present parent (if it has one), sets
	 * the child's parent to this node, and then adds the child to this node's
	 * child array at index {@code index}. The new {@code child} must not be
	 * {@code null} and must not be an ancestor of {@code this} node.
	 *
	 * @param index the index in the child array where this node is to be
	 *        inserted
	 * @param child the sub-node to be inserted
	 * @return {@code this} tree-node, for method chaining
	 * @throws ArrayIndexOutOfBoundsException if {@code index} is out of bounds
	 * @throws IllegalArgumentException if {@code child} is an ancestor of
	 *         {@code this} node
	 * @throws NullPointerException if the given {@code child} is {@code null}
	 */
	public TreeNode<T> insert(final int index, final TreeNode<T> child) {
		requireNonNull(child);
		if (isAncestor(child)) {
			throw new IllegalArgumentException("The new child is an ancestor.");
		}

		if (child._parent != null) {
			child._parent.remove(child);
		}

		child.setParent(this);
		_children.add(index, child);

		TreeNode<T> parent = this;
		while (parent != null) {
			parent = parent._parent;
		}

		return this;
	}

	/**
	 * Removes the child at the specified index from this node's children and
	 * sets that node's parent to {@code null}. The child node to remove must be
	 * a {@code MutableTreeNode}.
	 *
	 * @param index the index in this node's child array of the child to remove
	 * @return {@code this} tree-node, for method chaining
	 * @throws ArrayIndexOutOfBoundsException  if the {@code index} is out of
	 *         bounds
	 */
	public TreeNode<T> remove(final int index) {
		final TreeNode<T> child = _children.remove(index);
		assert child._parent == this;

		TreeNode<T> parent = this;
		while (parent != null) {
			parent = parent._parent;
		}

		child.setParent(null);

		return this;
	}

	/* *************************************************************************
	 * Derived operations
	 **************************************************************************/

	/**
	 * Detaches the subtree rooted at {@code this} node from the tree, giving
	 * {@code this} node a {@code null} parent. Does nothing if {@code this}
	 * node is the root of its tree.
	 *
	 * @return {@code this}
	 */
	public TreeNode<T> detach() {
		if (_parent != null) {
			_parent.remove(this);
		}

		return this;
	}

	/**
	 * Remove the {@code child} from {@code this} node's child array, giving it
	 * a {@code null} parent.
	 *
	 * @param child the child of this node to remove
	 * @throws NullPointerException if the given {@code child} is {@code null}
	 * @throws IllegalArgumentException if the given {@code child} is not a
	 *         child of this node
	 */
	public void remove(final TreeNode<T> child) {
		requireNonNull(child);

		if (!isChild(child)) {
			throw new IllegalArgumentException("The given child is not a child.");
		}
		remove(getIndex(child));
	}

	/**
	 * Removes all children fo {@code this} node and setting their parents to
	 * {@code null}. If {@code this} node has no children, this method does
	 * nothing.
	 */
	public void removeAllChildren() {
		for (int i = 0; i < childCount(); ++i) {
			remove(i);
		}
	}

	/**
	 * Remove the given {@code child} from its parent and makes it a child of
	 * this node by adding it to the end of this node's child array.
	 *
	 * @param child the new child added to this node
	 * @return {@code this} tree-node, for method chaining
	 * @throws NullPointerException if the given {@code child} is {@code null}
	 */
	public TreeNode<T> attach(final TreeNode<T> child) {
		requireNonNull(child);

		if (child._parent == this) {
			insert(childCount() - 1, child);
		} else {
			insert(childCount(), child);
		}

		return this;
	}

	@Override
	public TreeNode<T> copy() {
		final TreeNode<T> target = TreeNode.of(getValue());
		fill(this, target);
		return target;
	}

	private static <T> void fill(final TreeNode<T> source, final TreeNode<T> target) {
		source.childStream().forEachOrdered(child -> {
			final TreeNode<T> targetChild = TreeNode.of(child.getValue());
			target.attach(targetChild);
			fill(child, targetChild);
		});
	}
	@Override
	public int hashCode() {
		int hash = 17;
		final Iterator<?> it = depthFirstIterator();
		while (it.hasNext()) {
			hash += 31*Objects.hashCode(it.next()) + 17;
		}
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof TreeNode<?> && equals(this, (TreeNode<?>)obj);
	}

	private static boolean equals(
		final TreeNode<?> that,
		final TreeNode<?> other
	) {
		boolean equals = that.childCount() == other.childCount();
		if (equals) {
			equals = Objects.equals(that.getValue(), other.getValue());
			if (equals && !that._children.isEmpty()) {
				equals = equals(that._children, other._children);
			}
		}

		return equals;
	}

	private static boolean equals(
		final Collection<? extends TreeNode<?>> that,
		final Collection<? extends TreeNode<?>> other
	) {
		boolean equals = true;
		final Iterator<? extends TreeNode<?>> it1 = that.iterator();
		final Iterator<? extends TreeNode<?>> it2 = other.iterator();
		while (it1.hasNext() && equals) {
			equals = equals(it1.next(), it2.next());
		}

		return equals;
	}

	@Override
	public String toString() {
		return toString(this, new StringBuilder(), 0).toString();
	}

	private StringBuilder toString(
		final TreeNode<?> node,
		final StringBuilder out,
		final int level
	) {
		for (int i = 0; i < level; ++i) {
			out.append("  ");
		}

		out.append("+- ").append(node.getValue() + ": " + node.s() + " : " + node.size()).append("\n");
		IntStream.range(0, node.childCount())
			.forEach(i -> toString(node.getChild(i), out, level + 1));

		return out;
	}

	private int s() {
		return (int)breathFirstStream().count();
	}

	/* *************************************************************************
	 * Static factory methods.
	 **************************************************************************/

	/**
	 * Return a new {@code TreeNode} with the given node {@code value}.
	 *
	 * @param value the node value
	 * @param <T> the tree-node type
	 * @return a new tree-node
	 */
	public static <T> TreeNode<T> of(final T value) {
		return new TreeNode<>(value);
	}

	/**
	 * Return a new {@code TreeNode} with a {@code null} tree value.
	 *
	 * @param <T> the tree-node type
	 * @return a new tree-node
	 */
	public static <T> TreeNode<T> of() {
		return new TreeNode<>(null);
	}

	public static <T> TreeNode<T> ofShape(
		final IntRange childCount,
		final IntRange leafLevel,
		final Random random
	) {
		return ofShape(() -> nextInt(random, childCount), leafLevel, random);
	}

	public static <T> TreeNode<T> ofShape(
		final IntSupplier childCount,
		final IntRange leafLevel,
		final Random random
	) {
		final TreeNode<T> root = TreeNode.of();
		addChildren(root, 0, childCount, leafLevel, random);
		return root;
	}

	private static <T> void addChildren(
		final TreeNode<T> node,
		final int nodeLevel,
		final IntSupplier childCount,
		final IntRange leafLevel,
		final Random random
	) {
		assert node.level() == nodeLevel;

		for (int i = 0, n = childCount.getAsInt(); i < n; ++i) {
			final TreeNode<T> child = TreeNode.of();
			node.attach(child);

			final int d = nextInt(random, leafLevel);

			if (nodeLevel < d) {
				addChildren(child, nodeLevel + 1, childCount, leafLevel, random);
			}
		}
	}

}
