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
package io.jenetics.ext.util;

import static java.util.Objects.requireNonNull;
import static java.util.Spliterators.spliteratorUnknownSize;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.jenetics.util.ISeq;

/**
 * General purpose tree structure. The interface only contains tree read methods.
 * For a mutable tree implementation have a look at the {@link TreeNode} class.
 *
 * @see TreeNode
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
public interface Tree<V, T extends Tree<V, T>> extends Iterable<T> {

	/* *************************************************************************
	 * Basic (abstract) operations. All other tree operations can be derived
	 * from this methods.
	 **************************************************************************/

	/**
	 * Return the value of the current {@code Tree} node. The value my be
	 * {@code null}.
	 *
	 * @return the value of the current {@code Tree} node
	 */
	public V getValue();

	/**
	 * Return the <em>parent</em> node of this tree node.
	 *
	 * @return the parent node, or {@code Optional.empty()} if this node is the
	 *         root of the tree
	 */
	public Optional<T> getParent();

	/**
	 * Return the child node with the given index.
	 *
	 * @param index the child index
	 * @return the child node with the given index
	 * @throws IndexOutOfBoundsException  if the {@code index} is out of
	 *         bounds ({@code [0, childCount())})
	 */
	public T getChild(final int index);

	/**
	 * Return the number of children this tree node consists of.
	 *
	 * @return the number of children this tree node consists of
	 */
	public int childCount();


	/* *************************************************************************
	 * Derived operations
	 **************************************************************************/

	/**
	 * Return an iterator of the children of this {@code Tree} node.
	 *
	 * @return an iterator of the children of this {@code Tree} node.
	 */
	public default Iterator<T> childIterator() {
		return new TreeChildIterator<V, T>(Trees.<V, T>self(this));
	}

	/**
	 * Return a forward-order stream of this node's children.
	 *
	 * @return a stream of children of {@code this} node
	 */
	public default Stream<T> childStream() {
		return StreamSupport
			.stream(spliteratorUnknownSize(childIterator(), 0), false);
	}

	/**
	 * Returns {@code true} if this node is the root of the tree.
	 *
	 * @return {@code true} if this node is the root of its tree, {@code false}
	 *         otherwise
	 */
	public default boolean isRoot() {
		return !getParent().isPresent();
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
	public default int depth() {
		final Iterator<T> it = breadthFirstIterator();

		T last = null;
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
	public default int level() {
		Optional<T> ancestor = Optional.of(Trees.<V, T>self(this));
		int levels = 0;
		while ((ancestor = ancestor.flatMap(Tree<V, T>::getParent)).isPresent()) {
			++levels;
		}

		return levels;
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
	public default int getIndex(final Tree<?, ?> child) {
		int index = -1;
		for (int i = 0, n = childCount(); i < n && index == -1; ++i) {
			if (getChild(i).identical(child)) {
				index = i;
			}
		}

		return index;
	}

	/**
	 * Return the number of nodes of {@code this} node (sub-tree).
	 *
	 * @return the number of nodes of {@code this} node (sub-tree)
	 */
	public default int size() {
		return (int)breadthFirstStream().count();
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
	public default boolean isAncestor(final Tree<?, ?> node) {
		requireNonNull(node);

		Optional<T> ancestor = Optional.of(Trees.self(this));
		boolean result;
		do {
			result = ancestor.filter(a -> a.identical(node)).isPresent();
		} while (!result &&
				(ancestor = ancestor.flatMap(Tree<V, T>::getParent)).isPresent());

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
	public default boolean isDescendant(final Tree<?, ?> node) {
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
	public default Optional<T> sharedAncestor(final Tree<?, ?> node) {
		requireNonNull(node);

		T ancestor = null;
		if (node.identical(this)) {
			ancestor = Trees.<V, T>self(this);
		} else {
			final int level1 = level();
			final int level2 = node.level();

			Tree<?, ?> node1;
			Tree<?, ?> node2;
			int diff;
			if (level2 > level1) {
				diff = level2 - level1;
				node1 = node;
				node2 = Trees.<V, T>self(this);
			} else {
				diff = level1 - level2;
				node1 = Trees.<V, T>self(this);
				node2 = node;
			}

			while (diff > 0) {
				node1 = node1.getParent().orElse(null);
				--diff;
			}

			do {
				if (node1 != null && node1.identical(node2)) {
					ancestor = Trees.<V, T>self(node1);
				}
				node1 = node1 != null
					? node1.getParent().orElse(null)
					: null;
				node2 = node2.getParent().orElse(null);
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
	public default boolean isRelated(final Tree<?, ?> node) {
		requireNonNull(node);
		return node.getRoot().identical(getRoot());
	}

	/**
	 * Returns the path from the root, to get to this node. The last element in
	 * the path is this node.
	 *
	 * @return an array of TreeNode objects giving the path, where the
	 *         first element in the path is the root and the last
	 *         element is this node.
	 */
	public default ISeq<T> getPath() {
		return Trees.pathToRoot(Trees.<V, T>self(this), 0).toISeq();
	}

	/**
	 * Returns the root of the tree that contains this node. The root is the
	 * ancestor with no parent.
	 *
	 * @return the root of the tree that contains this node
	 */
	public default T getRoot() {
		T anc = Trees.<V, T>self(this);
		T prev;

		do {
			prev = anc;
			anc = anc.getParent().orElse(null);
		} while (anc != null);

		return prev;
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
	public default boolean isChild(final Tree<?, ?> node) {
		requireNonNull(node);
		return childCount() != 0 && node.getParent().equals(Optional.of(this));
	}

	/**
	 * Return the first child of {@code this} node, or {@code Optional.empty()}
	 * if {@code this} node has no children.
	 *
	 * @return the first child of this node
	 */
	public default Optional<T> firstChild() {
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
	public default Optional<T> lastChild() {
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
	public default Optional<T> childAfter(final Tree<?, ?> child) {
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
	public default Optional<T> childBefore(final Tree<?, ?> child) {
		requireNonNull(child);

		final int index = getIndex(child);
		if (index == -1) {
			throw new IllegalArgumentException("The given node is not a child.");
		}

		return index > 0
			? Optional.of(getChild(index - 1))
			: Optional.empty();
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
	public default Optional<T> nextNode() {
		Optional<T> next = Optional.empty();

		if (childCount() == 0) {
			T node = Trees.<V, T>self(this);
			while (node != null && !(next = node.nextSibling()).isPresent()) {
				node = node.getParent().orElse(null);
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
	public default Optional<T> previousNode() {
		Optional<T> node = Optional.empty();

		if (getParent().isPresent()) {
			final Optional<T> prev = previousSibling();
			if (prev.isPresent()) {
				node = prev.get().childCount() == 0
					? prev
					: prev.map(Tree<V, T>::lastLeaf);
			} else {
				node = getParent();
			}
		}

		return node;
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
	public default boolean isSibling(final Tree<?, ?> node) {
		return identical(requireNonNull(node)) ||
			getParent().equals(node.getParent());
	}

	/**
	 * Return the number of siblings of {@code this} node. A node is its own
	 * sibling (if it has no parent or no siblings, this method returns
	 * {@code 1}).
	 *
	 * @return the number of siblings of {@code this} node
	 */
	public default int siblingCount() {
		return getParent().map(Tree<V, T>::childCount).orElse(1);
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
	public default Optional<T> nextSibling() {
		return getParent().flatMap(p -> p.childAfter(Trees.<V, T>self(this)));
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
	public default Optional<T> previousSibling() {
		return getParent().flatMap(p -> p.childBefore(Trees.<V, T>self(this)));
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
	public default boolean isLeaf() {
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
	public default T firstLeaf() {
		T leaf = Trees.<V, T>self(this);
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
	public default T lastLeaf() {
		T leaf = Trees.<V, T>self(this);
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
	public default Optional<T> nextLeaf() {
		return nextSibling()
			.map(s -> Optional.of(s.firstLeaf()))
			.orElseGet(() -> getParent().flatMap(Tree<V, T>::nextLeaf));
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
	public default Optional<T> previousLeaf() {
		return previousSibling()
			.map(s -> Optional.of(s.lastLeaf()))
			.orElseGet(() -> getParent().flatMap(Tree<V, T>::previousLeaf));
	}

	/**
	 * Returns the total number of leaves that are descendants of this node.
	 * If this node is a leaf, returns {@code 1}. This method is {@code O(n)},
	 * where n is the number of descendants of {@code this} node.
	 *
	 * @see #isLeaf()
	 * @return the number of leaves beneath this node
	 */
	public default int leafCount() {
		return (int) breadthFirstStream()
			.filter(Tree<V, T>::isLeaf)
			.count();
	}

	/* *************************************************************************
	 * Tree traversing.
	 **************************************************************************/

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
	public default Iterator<T> breadthFirstIterator() {
		return new TreeNodeBreadthFirstIterator<>(Trees.<V, T>self(this));
	}

	/**
	 * Return an iterator that traverses the subtree rooted at {@code this}.
	 * The first node returned by the iterator is {@code this} node.
	 * <p>
	 * Modifying the tree by inserting, removing, or moving a node invalidates
	 * any iterator created before the modification.
	 *
	 * @see #breadthFirstIterator
	 * @return an iterator for traversing the tree in breadth-first order
	 */
	@Override
	public default Iterator<T> iterator() {
		return breadthFirstIterator();
	}

	/**
	 * Return a stream that traverses the subtree rooted at {@code this} node in
	 * breadth-first order. The first node returned by the stream is
	 * {@code this} node.
	 *
	 * @see #depthFirstIterator
	 * @see #stream()
	 * @return a stream for traversing the tree in breadth-first order
	 */
	public default Stream<T> breadthFirstStream() {
		return StreamSupport
			.stream(spliteratorUnknownSize(breadthFirstIterator(), 0), false);
	}

	/**
	 * Return a stream that traverses the subtree rooted at {@code this} node in
	 * breadth-first order. The first node returned by the stream is
	 * {@code this} node.
	 *
	 * @see #breadthFirstStream
	 * @return a stream for traversing the tree in breadth-first order
	 */
	public default Stream<T> stream() {
		return breadthFirstStream();
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
	public default Iterator<T> preorderIterator() {
		return new TreeNodePreorderIterator<>(Trees.<V, T>self(this));
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
	public default Stream<T> preorderStream() {
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
	public default Iterator<T> postorderIterator() {
		return new TreeNodePostorderIterator<>(Trees.<V, T>self(this));
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
	public default Stream<T> postorderStream() {
		return StreamSupport
			.stream(spliteratorUnknownSize(postorderIterator(), 0), false);
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
	public default Iterator<T> depthFirstIterator() {
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
	public default Stream<T> depthFirstStream() {
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
	public default Iterator<T>
	pathFromAncestorIterator(final Tree<?, ?> ancestor) {
		return new TreeNodePathIterator<>(ancestor, Trees.<V, T>self(this));
	}

	/**
	 * Tests whether {@code this} node is the same as the {@code other} node.
	 * The default implementation returns the object identity,
	 * {@code this == other}, of the two objects, but other implementations may
	 * use different criteria for checking the <i>identity</i>.
	 *
	 * @param other the {@code other} node
	 * @return {@code true} if the {@code other} node is the same as {@code this}
	 *         node.
	 */
	public default boolean identical(final Tree<?, ?> other) {
		return this == other;
	}

	/* *************************************************************************
	 * Static helper methods.
	 **************************************************************************/

	/**
	 * Calculates the hash code of the given tree.
	 *
	 * @param tree the tree where the hash is calculated from
	 * @return the hash code of the tree
	 * @throws NullPointerException if the given {@code tree} is {@code null}
	 */
	public static int hashCode(final Tree<?, ?> tree) {
		int hash = 0;
		if (tree != null) {
			hash = 17;
			for (Tree<?, ?> node : tree) {
				hash += 31*Objects.hashCode(node.getValue()) + 37;
			}
		}

		return hash;
	}

	/**
	 * Checks if the two given trees has the same structure with the same values.
	 *
	 * @param a the first tree
	 * @param b the second tree
	 * @return {@code true} if the two given trees are structurally equals,
	 *         {@code false} otherwise
	 */
	public static boolean equals(final Tree<?, ?> a, final Tree<?, ?> b) {
		return Trees.equals(a, b);
	}

	/**
	 * Return a string representation of the given tree, like the following
	 * example.
	 *
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
	 *
	 * This method is intended to be used when override the
	 * {@link Object#toString()} method.
	 *
	 * @param tree the input tree
	 * @return the string representation of the given tree
	 */
	public static String toString(final Tree<?, ?> tree) {
		return Trees.toString(tree);
	}


	/**
	 * Return a compact string representation of the given tree. The tree
	 * <pre>
	 *  mul
	 *  ├── div
	 *  │   ├── cos
	 *  │   │   └── 1.0
	 *  │   └── cos
	 *  │       └── π
	 *  └── sin
	 *      └── mul
	 *          ├── 1.0
	 *          └── z
	 *  </pre>
	 * is printed as
	 * <pre>
	 *  mul(div(cos(1.0), cos(π)), sin(mul(1.0, z)))
	 * </pre>
	 *
	 * @param tree the input tree
	 * @return the string representation of the given tree
	 */
	public static String toCompactString(final Tree<?, ?> tree) {
		return Trees.toCompactString(tree);
	}

	/**
	 * Return a string representation of the given {@code tree} in dotty syntax.
	 *
	 * @param tree the input tree
	 * @return the string representation of the given tree
	 */
	public static String toDottyString(final Tree<?, ?> tree) {
		return Trees.toDottyString("T", tree);
	}

}
