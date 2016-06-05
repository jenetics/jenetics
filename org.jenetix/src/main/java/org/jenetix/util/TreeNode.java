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
import static java.util.Spliterators.spliteratorUnknownSize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jenetics.util.Copyable;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * A general purpose node in a tree data-structure.
 *
 * @param <T> the value type of the tree node
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class TreeNode<T> implements Copyable<TreeNode<T>>, Serializable  {
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
	public T getValue() {
		return _value;
	}

	/**
	 * Returns this node's parent if available.
	 *
	 * @return the tree-node, or an empty value if this node has no parent
	 */
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
	public void setParent(final TreeNode<T> parent) {
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
	public TreeNode<T> getChild(final int index) {
		return _children.get(index);
	}

	/**
	 * Returns the number of children of {@code this} node.
	 *
	 * @return the number of children of {@code this} node
	 */
	public int childCount() {
		return _children.size();
	}

	/**
	 * Return the number of nodes of {@code this} node (sub-tree).
	 *
	 * @return the number of nodes of {@code this} node (sub-tree)
	 */
	public int size() {
		return (int)breathFirstStream().count();
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
		_children.remove(index).setParent(null);
		return this;
	}

	/**
	 * Returns the index of the specified child in this node's child array, or
	 * {@code -1} if {@code this} node doesn't contain the given {@code child}.
	 * This method performs a linear search and is O(n) where {@code n} is the
	 * number of children.
	 *
	 * @param child  the TreeNode to search for among this node's children
	 * @throws NullPointerException if the given {@code child} is {@code null}
	 * @return the index of the node in this node's child array, or {@code -1}
	 *         if the node could not be found
	 */
	public int getIndex(final TreeNode<T> child) {
		return _children.indexOf(requireNonNull(child));
	}

	/**
	 * Return a forward-order iterator of this node's children.
	 *
	 * @return an iterator of children of {@code this} node
	 */
	public Iterator<TreeNode<T>> childIterator() {
		return _children.iterator();
	}

	/**
	 * Return a forward-order stream of this node's children.
	 *
	 * @return a stream of children of {@code this} node
	 */
	public Stream<TreeNode<T>> childStream() {
		return _children.stream();
	}


	/* *************************************************************************
	 * Derived operations
	 **************************************************************************/

	/**
	 * Removes the subtree rooted at {@code this} node from the tree, giving
	 * {@code this} node a {@code null} parent. Does nothing if {@code this}
	 * node is the root of its tree.
	 */
	public void removeFromParent() {
		if (_parent != null) {
			_parent.remove(this);
		}
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
		for (int i = childCount() - 1; i >= 0; i--) {
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
	public TreeNode<T> add(final TreeNode<T> child) {
		requireNonNull(child);

		if (child._parent == this) {
			insert(childCount() - 1, child);
		} else {
			insert(childCount(), child);
		}

		return this;
	}

	/* *************************************************************************
	 * Query operations
	 **************************************************************************/

	/**
	 * Return {@code true} if the given {@code node} is an ancestor of
	 * {@code this} node. This operation is at worst {@code O(h)} where {@code h}
	 * is the distance from the root to {@code this} node.
	 *
	 * @param node the node to test
	 * @return {@code true} if the given {@code node} is an ancestor of
	 *         {@code this} node, {@code false} otherwise
	 * @throws NullPointerException if the given {@code node} is {@code null}
	 */
	public boolean isAncestor(final TreeNode<T> node) {
		requireNonNull(node);

		boolean result;
		TreeNode<T> ancestor = this;
		do {
			result = ancestor == node;
		} while(!result && (ancestor = ancestor._parent) != null);

		return result;
	}

	/**
	 * Return {@code true} if the given {@code node} is a descendant of
	 * {@code this} node. If the given {@code node} is {@code null},
	 * {@code false} is returned. This operation is at worst {@code O(h)} where
	 * {@code h} is the distance from the root to {@code this} node.
	 *
	 * @param node the node to test as descendant of this node
	 * @return {@code true} if this node is an ancestor of the given {@code node}
	 * @throws NullPointerException if the given {@code node} is {@code null}
	 */
	public boolean isDescendant(final TreeNode<T> node) {
		return requireNonNull(node).isAncestor(this);
	}

	/**
	 * Returns the nearest common ancestor to this node and the given {@code node}.
	 * A node is considered an ancestor of itself.
	 *
	 * @param node {@code node} to find common ancestor with
	 * @return nearest ancestor common to this node and the given {@code node},
	 *         or {@link Optional#empty()} if no common ancestor exists.
	 * @throws NullPointerException if the given {@code node} is {@code null}
	 */
	public Optional<TreeNode<T>> sharedAncestor(final TreeNode<T> node) {
		requireNonNull(node);

		TreeNode<T> ancestor = null;
		if (node == this) {
			ancestor = this;
		} else {
			final int level1 = level();
			final int level2 = node.level();

			TreeNode<T> node1;
			TreeNode<T> node2;
			int diff;
			if (level2 > level1) {
				diff = level2 - level1;
				node1 = node;
				node2 = this;
			} else {
				diff = level1 - level2;
				node1 = this;
				node2 = node;
			}

			while (diff > 0) {
				node1 = node1._parent;
				--diff;
			}

			do {
				if (node1 == node2) {
					ancestor = node1;
				}
				node1 = node1._parent;
				node2 = node2._parent;
			} while (node1 != null && ancestor == null);
		}

		return Optional.ofNullable(ancestor);
	}

	/**
	 * Returns true if and only if the given {@code node} is in the same tree as
	 * {@code this} node.
	 *
	 * @param node the other node to check
	 * @return true if the given {@code node} is in the same tree as {@code this}
	 *         node, {@code false} otherwise.
	 * @throws NullPointerException if the given {@code node} is {@code null}
	 */
	public boolean isRelated(final TreeNode<T> node) {
		return node.getRoot() == getRoot();
	}

	/**
	 * Returns the depth of the tree rooted at this node. The <i>depth</i> of a
	 * tree is the longest distance from {@code this} node to a leaf. If
	 * {@code this} node has no children, 0 is returned. This operation is much
	 * more expensive than {@link #level()} because it must effectively traverse
	 * the entire tree rooted at {@code this} node.
	 *
	 * @return the depth of the tree whose root is this node
	 */
	public int depth() {
		final Iterator<TreeNode<T>> it = breadthFirstIterator();

		TreeNode<T> last = null;
		while (it.hasNext()) {
			last = it.next();
		}

		assert last != null;
		return last.level() - level();
	}

	/**
	 * Returns the number of levels above this node. The <i>level</i> of a tree
	 * is the distance from the root to {@code this} node. If {@code this} node
	 * is the root, returns 0.
	 *
	 * @return the number of levels above this node
	 */
	public int level() {
		TreeNode<T> ancestor = this;
		int levels = 0;
		while ((ancestor = ancestor._parent) != null) {
			++levels;
		}

		return levels;
	}

	/**
	 * Returns the path from the root, to get to this node. The last element in
	 * the path is this node.
	 *
	 * @return an array of TreeNode objects giving the path, where the
	 *         first element in the path is the root and the last
	 *         element is this node.
	 */
	public ISeq<TreeNode<T>> getPath() {
		return pathToRoot(this, 0).toISeq();
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
	private MSeq<TreeNode<T>> pathToRoot(
		final TreeNode<T> node,
		final int depth
	) {
		final MSeq<TreeNode<T>> path;
		if (node == null) {
			path = depth == 0 ? MSeq.empty() : MSeq.ofLength(depth);
		} else {
			path = pathToRoot(node._parent, depth + 1);
			path.set(path.length() - depth - 1, node);
		}

		return path;
	}

	/**
	 * Returns the root of the tree that contains this node. The root is the
	 * ancestor with no parent.
	 *
	 * @return the root of the tree that contains this node
	 */
	public TreeNode<T> getRoot() {
		TreeNode<T> anc = this;
		TreeNode<T> prev;

		do {
			prev = anc;
			anc = anc._parent;
		} while (anc != null);

		return prev;
	}

	/**
	 * Returns {@code true} if this node is the root of the tree.
	 *
	 * @return {@code true} if this node is the root of its tree, {@code false}
	 *         otherwise
	 */
	public boolean isRoot() {
		return _parent == null;
	}

	/**
	 * Return the node that follows {@code this} node in a pre-order traversal
	 * of {@code this} tree node. Return {@code Optional.empty()} if this node
	 * is the last node of the traversal. This is an inefficient way to traverse
	 * the entire tree use an iterator instead.
	 *
	 * @see #preorderIterator
	 * @return the node that follows this node in a pre-order traversal, or
	 *        {@code Optional.empty()} if this node is last
	 */
	public Optional<TreeNode<T>> nextNode() {
		Optional<TreeNode<T>> next = Optional.empty();

		if (childCount() == 0) {
			TreeNode<T> node = this;
			while (node != null && !(next = node.nextSibling()).isPresent()) {
				node = node._parent;
			}
		} else {
			next = Optional.of(getChild(0));
		}

		return next;
	}

	/**
	 * Return the node that precedes this node in a pre-order traversal of
	 * {@code this} tree node. Returns {@code Optional.empty()} if this node is
	 * the first node of the traversal, the root of the tree. This is an
	 * inefficient way to traverse the entire tree; use an iterator instead.
	 *
	 * @see #preorderIterator
	 * @return the node that precedes this node in a pre-order traversal, or
	 *         {@code Optional.empty()} if this node is the first
	 */
	public Optional<TreeNode<T>> previousNode() {
		Optional<TreeNode<T>> node = Optional.empty();

		if (_parent != null) {
			final Optional<TreeNode<T>> prev = previousSibling();
			if (prev.isPresent()) {
				node = prev.get().childCount() == 0
					? prev
					: prev.map(TreeNode::lastLeaf);
			} else {
				node = getParent();
			}
		}

		return node;
	}

	/**
	 * Return an iterator that traverses the subtree rooted at {@code this} node
	 * in pre-order. The first node returned by the iterator is {@code this}
	 * node.
	 * <p>
	 * Modifying the tree by inserting, removing, or moving a node invalidates
	 * any iterator created before the modification.
	 *
	 * @see #postorderIterator
	 * @return an iterator for traversing the tree in pre-order
	 */
	public Iterator<TreeNode<T>> preorderIterator() {
		return new TreeNodePreorderIterator<>(this);
	}

	/**
	 * Return a stream that traverses the subtree rooted at {@code this} node
	 * in pre-order. The first node returned by the stream is {@code this} node.
	 * <p>
	 * Modifying the tree by inserting, removing, or moving a node invalidates
	 * any iterator created before the modification.
	 *
	 * @see #preorderIterator
	 * @return a stream for traversing the tree in pre-order
	 */
	public Stream<TreeNode<T>> preorderStream() {
		return StreamSupport
			.stream(spliteratorUnknownSize(preorderIterator(), 0), false);
	}

	/**
	 * Return an iterator that traverses the subtree rooted at {@code this}
	 * node in post-order. The first node returned by the iterator is the
	 * leftmost leaf.  This is the same as a depth-first traversal.
	 *
	 * @see #depthFirstIterator
	 * @see #preorderIterator
	 * @return an iterator for traversing the tree in post-order
	 */
	public Iterator<TreeNode<T>> postorderIterator() {
		return new TreeNodePostorderIterator<>(this);
	}

	/**
	 * Return a stream that traverses the subtree rooted at {@code this} node in
	 * post-order. The first node returned by the iterator is the leftmost leaf.
	 * This is the same as a depth-first traversal.
	 *
	 * @see #depthFirstIterator
	 * @see #preorderIterator
	 * @return a stream for traversing the tree in post-order
	 */
	public Stream<TreeNode<T>> postorderStream() {
		return StreamSupport
			.stream(spliteratorUnknownSize(postorderIterator(), 0), false);
	}

	/**
	 * Return an iterator that traverses the subtree rooted at {@code this}
	 * node in breadth-first order. The first node returned by the iterator is
	 * {@code this} node.
	 * <p>
	 * Modifying the tree by inserting, removing, or moving a node invalidates
	 * any iterator created before the modification.
	 *
	 * @see #depthFirstIterator
	 * @return an iterator for traversing the tree in breadth-first order
	 */
	public Iterator<TreeNode<T>> breadthFirstIterator() {
		return new TreeNodeBreadthFirstIterator<>(this);
	}

	/**
	 * Return a stream that traverses the subtree rooted at {@code this} node in
	 * breadth-first order. The first node returned by the stream is
	 * {@code this} node.
	 *
	 * @see #depthFirstIterator
	 * @return a stream for traversing the tree in breadth-first order
	 */
	public Stream<TreeNode<T>> breathFirstStream() {
		return StreamSupport
			.stream(spliteratorUnknownSize(breadthFirstIterator(), 0), false);
	}

	/**
	 * Return an iterator that traverses the subtree rooted at {@code this} node
	 * in depth-first order. The first node returned by the iterator is the
	 * leftmost leaf. This is the same as a postorder traversal.
	 * <p>
	 * Modifying the tree by inserting, removing, or moving a node invalidates
	 * any iterator created before the modification.
	 *
	 * @see #breadthFirstIterator
	 * @see #postorderIterator
	 * @return an iterator for traversing the tree in depth-first order
	 */
	public Iterator<TreeNode<T>> depthFirstIterator() {
		return postorderIterator();
	}

	/**
	 * Return a stream that traverses the subtree rooted at {@code this} node in
	 * depth-first. The first node returned by the iterator is the leftmost leaf.
	 * This is the same as a post-order traversal.
	 *
	 * @see #depthFirstIterator
	 * @see #preorderIterator
	 * @return a stream for traversing the tree in post-order
	 */
	public Stream<TreeNode<T>> depthFirstStream() {
		return postorderStream();
	}

	/**
	 * Return an iterator that follows the path from {@code ancestor} to
	 * {@code this} node. The iterator return {@code ancestor} as first element,
	 * The creation of the iterator is O(m), where m is the number of nodes
	 * between {@code this} node and the {@code ancestor}, inclusive.
	 * <p>
	 * Modifying the tree by inserting, removing, or moving a node invalidates
	 * any iterator created before the modification.
	 *
	 * @see #isAncestor
	 * @see #isDescendant
	 * @param ancestor the ancestor node
	 * @return an iterator for following the path from an ancestor of {@code this}
	 *         node to this one
	 * @throws IllegalArgumentException if the {@code ancestor} is not an
	 *         ancestor of this node
	 * @throws NullPointerException if the given {@code ancestor} is {@code null}
	 */
	public Iterator<TreeNode<T>> pathFromAncestorIterator(
		final TreeNode<T> ancestor
	) {
		return new TreeNodePathIterator<>(ancestor, this);
	}


	/* *************************************************************************
	 * Child query operations
	 **************************************************************************/

	/**
	 * Return {@code true} if the given {@code node} is a child of {@code this}
	 * node.
	 *
	 * @param node the other node to check
	 * @return  {@code true} if {@code node}is a child, {@code false} otherwise
	 * @throws NullPointerException if the given {@code node} is {@code null}
	 */
	public boolean isChild(final TreeNode<T> node) {
		requireNonNull(node);
		return childCount() != 0 && node._parent == this;
	}

	/**
	 * Return the first child of {@code this} node, or {@code Optional.empty()}
	 * if {@code this} node has no children.
	 *
	 * @return the first child of this node
	 */
	public Optional<TreeNode<T>> firstChild() {
		return childCount() > 0
			? Optional.of(getChild(0))
			: Optional.empty();
	}

	/**
	 * Return the last child of {@code this} node, or {@code Optional.empty()}
	 * if {@code this} node has no children.
	 *
	 * @return the last child of this node
	 */
	public Optional<TreeNode<T>> lastChild() {
		return childCount() > 0
			? Optional.of(getChild(childCount() - 1))
			: Optional.empty();
	}

	/**
	 * Return the child which comes immediately after {@code this} node. This
	 * method performs a linear search of this node's children for {@code child}
	 * and is {@code O(n)} where n is the number of children.
	 *
	 * @param child the child node
	 * @return  the child of this node that immediately follows the {@code child},
	 *          or {@code Optional.empty()} if the given {@code node} is the
	 *          first node.
	 * @throws NullPointerException if the given {@code child} is {@code null}
	 */
	public Optional<TreeNode<T>> childAfter(final TreeNode<T> child) {
		requireNonNull(child);

		final int index = getIndex(child);
		if (index == -1) {
			throw new IllegalArgumentException("The given node is not a child.");
		}

		return index < childCount() - 1
			? Optional.of(getChild(index + 1))
			: Optional.empty();
	}

	/**
	 * Return the child which comes immediately before {@code this} node. This
	 * method performs a linear search of this node's children for {@code child}
	 * and is {@code O(n)} where n is the number of children.
	 *
	 * @param child the child node
	 * @return  the child of this node that immediately precedes the {@code child},
	 *          or {@code null} if the given {@code node} is the first node.
	 * @throws NullPointerException if the given {@code child} is {@code null}
	 */
	public Optional<TreeNode<T>> childBefore(final TreeNode<T> child) {
		requireNonNull(child);

		final int index = getIndex(child);
		if (index == -1) {
			throw new IllegalArgumentException("The given node is not a child.");
		}

		return index > 0
			? Optional.of(getChild(index - 1))
			: Optional.empty();
	}

	/* *************************************************************************
	 * Sibling query operations
	 **************************************************************************/

	/**
	 * Test if the given {@code node} is a sibling of {@code this} node.
	 *
	 * @param node node to test as sibling of this node
	 * @return {@code true} if the {@code node} is a sibling of {@code this}
	 *         node
	 * @throws NullPointerException if the given {@code node} is {@code null}
	 */
	public boolean isSibling(final TreeNode<T> node) {
		return requireNonNull(node) == this ||
			_parent != null && _parent == node._parent;
	}

	/**
	 * Return the number of siblings of {@code this} node. A node is its own
	 * sibling (if it has no parent or no siblings, this method returns
	 * {@code 1}).
	 *
	 * @return the number of siblings of {@code this} node
	 */
	public int siblingCount() {
		final TreeNode<T> parent = _parent;
		return parent != null ? parent.childCount() : 1;
	}

	/**
	 * Return the next sibling of {@code this} node in the parent's children
	 * array, or {@code null} if {@code this} node has no parent or it is the
	 * last child of the paren. This method performs a linear search that is
	 * {@code O(n)} where n is the number of children; to traverse the entire
	 * array, use the iterator of the parent instead.
	 *
	 * @see #childStream()
	 * @return the sibling of {@code this} node that immediately follows
	 *         {@code this} node
	 */
	public Optional<TreeNode<T>> nextSibling() {
		return getParent().flatMap(p -> p.childAfter(this));
	}

	/**
	 * Return the previous sibling of {@code this} node in the parent's children
	 * list, or {@code Optional.empty()} if this node has no parent or is the
	 * parent's first child. This method performs a linear search that is O(n)
	 * where n is the number of children.
	 *
	 * @return the sibling of {@code this} node that immediately precedes this
	 *         node
	 */
	public Optional<TreeNode<T>> previousSibling() {
		return getParent().flatMap(p -> p.childBefore(this));
	}

	/* *************************************************************************
	 * Leaf query operations
	 **************************************************************************/

	/**
	 * Return {@code true} if {@code this} node has no children.
	 *
	 * @return {@code true} if {@code this} node has no children, {@code false}
	 *         otherwise
	 */
	public boolean isLeaf() {
		return childCount() == 0;
	}

	/**
	 * Return the first leaf that is a descendant of {@code this} node; either
	 * this node or its first child's first leaf. {@code this} node is returned
	 * if it is a leaf.
	 *
	 * @see #isLeaf
	 * @see  #isDescendant
	 * @return the first leaf in the subtree rooted at this node
	 */
	public TreeNode<T> firstLeaf() {
		TreeNode<T> leaf = this;
		while (!leaf.isLeaf()) {
			leaf = leaf.firstChild().orElseThrow(AssertionError::new);
		}

		return leaf;
	}

	/**
	 * Return the last leaf that is a descendant of this node; either
	 * {@code this} node or its last child's last leaf. Returns {@code this}
	 * node if it is a leaf.
	 *
	 * @see #isLeaf
	 * @see #isDescendant
	 * @return the last leaf in this subtree
	 */
	public TreeNode<T> lastLeaf() {
		TreeNode<T> leaf = this;
		while (!leaf.isLeaf()) {
			leaf = leaf.lastChild().orElseThrow(AssertionError::new);
		}

		return leaf;
	}

	/**
	 * Returns the leaf after {@code this} node or {@code Optional.empty()} if
	 * this node is the last leaf in the tree.
	 * <p>
	 * In order to determine the next node, this method first performs a linear
	 * search in the parent's child-list in order to find the current node.
	 * <p>
	 * That implementation makes the operation suitable for short traversals
	 * from a known position. But to traverse all of the leaves in the tree, you
	 * should use {@link #depthFirstIterator()} to iterator the nodes in the
	 * tree and use {@link #isLeaf()} on each node to determine which are leaves.
	 *
	 * @see #depthFirstIterator
	 * @see #isLeaf
	 * @return return the next leaf past this node
	 */
	public Optional<TreeNode<T>> nextLeaf() {
		return nextSibling()
			.map(s -> Optional.of(s.firstLeaf()))
			.orElseGet(() -> getParent().flatMap(TreeNode::nextLeaf));
	}

	/**
	 * Return the leaf before {@code this} node or {@code null} if {@code this}
	 * node is the first leaf in the tree.
	 * <p>
	 * In order to determine the previous node, this method first performs a
	 * linear search in the parent's child-list in order to find the current
	 * node.
	 * <p>
	 * That implementation makes the operation suitable for short traversals
	 * from a known position. But to traverse all of the leaves in the tree, you
	 * should use {@link #depthFirstIterator()} to iterate the nodes in the tree
	 * and use {@link #isLeaf()} on each node to determine which are leaves.
	 *
	 * @see #depthFirstIterator
	 * @see #isLeaf
	 * @return returns the leaf before {@code this} node
	 */
	public Optional<TreeNode<T>> previousLeaf() {
		return previousSibling()
			.map(s -> Optional.of(s.lastLeaf()))
			.orElseGet(() -> getParent().flatMap(TreeNode::previousLeaf));
	}

	/**
	 * Returns the total number of leaves that are descendants of this node.
	 * If this node is a leaf, returns {@code 1}. This method is {@code O(n)},
	 * where n is the number of descendants of {@code this} node.
	 *
	 * @see #isLeaf()
	 * @return the number of leaves beneath this node
	 */
	public int leafCount() {
		return (int)breathFirstStream()
			.filter(TreeNode::isLeaf)
			.count();
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
			target.add(targetChild);
			fill(child, targetChild);
		});
	}

	@Override
	public int hashCode() {
		return hash(this);
	}

	private static int hash(final TreeNode<?> node) {
		int hash = 31*Objects.hashCode(node.getValue()) + 17;
		for (TreeNode<?> child : node._children) {
			hash += 31*hash(child) + 17;
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

		out.append("+- ").append(node.getValue()).append("\n");
		IntStream.range(0, node.childCount())
			.forEach(i -> toString(node.getChild(i), out, level + 1));

		return out;
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

}
