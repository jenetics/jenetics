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

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * General purpose tree structure.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Tree<V, T extends Tree<V, T>> extends Iterable<T> {

	/* *************************************************************************
	 * Basic operations
	 **************************************************************************/

	public V getValue();

	public Optional<T> getParent();

	public T getChild(final int index);

	public int childCount();


	/* *************************************************************************
	 * Derived operations
	 **************************************************************************/

	@SuppressWarnings("unchecked")
	public default T self() {
		return (T)this;
	}

	public default Iterator<T> childIterator() {
		return new TreeChildIterator<V, T>(self());
	}

	public default Stream<T> children() {
		return IntStream.range(0, childCount()).mapToObj(this::getChild);
	}

	public default Stream<T> nodes() {
		final Stream<T> start = Stream.of(self());
		return Stream.concat(start, children());
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
		Optional<T> ancestor = Optional.of(self());
		int levels = 0;
		while ((ancestor = ancestor.flatMap(p -> p.getParent())).isPresent()) {
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
	public default int getIndex(final T child) {
		int index = -1;
		for (int i = 0, n = childCount(); i < n && index == -1; ++i) {
			if (getChild(i).equals(child)) {
				index = i;
			}
		}

		return index;
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
	public default boolean isAncestor(final Tree<V, ?> node) {
		requireNonNull(node);

		@SuppressWarnings("unchecked")
		Optional<T> ancestor = Optional.of((T)this);
		boolean result;
		do {
			result = ancestor.filter(a -> a == node).isPresent();
		} while(!result &&
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
	public default boolean isDescendant(final Tree<V, ?> node) {
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
	public default Optional<T> sharedAncestor(final T node) {
		requireNonNull(node);

		T ancestor = null;
		if (node == this) {
			ancestor = self();
		} else {
			final int level1 = level();
			final int level2 = node.level();

			T node1;
			T node2;
			int diff;
			if (level2 > level1) {
				diff = level2 - level1;
				node1 = node;
				node2 = self();
			} else {
				diff = level1 - level2;
				node1 = self();
				node2 = node;
			}

			while (diff > 0) {
				node1 = node1.getParent().orElse(null);
				--diff;
			}

			do {
				if (node1 == node2) {
					ancestor = node1;
				}
				node1 = node1.getParent().orElse(null);
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
	public default boolean isRelated(final T node) {
		return node.getRoot() == getRoot();
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
		return pathToRoot(self(), 0).toISeq();
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
	public default MSeq<T> pathToRoot(
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

	/**
	 * Returns the root of the tree that contains this node. The root is the
	 * ancestor with no parent.
	 *
	 * @return the root of the tree that contains this node
	 */
	public default T getRoot() {
		T anc = self();
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
	public default boolean isChild(final T node) {
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
	public default Optional<T> childAfter(final T child) {
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
	public default Optional<T> childBefore(final T child) {
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
			T node = self();
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
	public default boolean isSibling(final T node) {
		return requireNonNull(node) == this ||
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
		return getParent().map(p -> p.childCount()).orElse(1);
	}

	/**
	 * Return the next sibling of {@code this} node in the parent's children
	 * array, or {@code null} if {@code this} node has no parent or it is the
	 * last child of the paren. This method performs a linear search that is
	 * {@code O(n)} where n is the number of children; to traverse the entire
	 * array, use the iterator of the parent instead.
	 *
	 * @see #children()
	 * @return the sibling of {@code this} node that immediately follows
	 *         {@code this} node
	 */
	public default Optional<T> nextSibling() {
		return getParent().flatMap(p -> p.childAfter(self()));
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
		return getParent().flatMap(p -> p.childBefore(self()));
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
		T leaf = self();
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
		T leaf = self();
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
		return (int)breathFirstStream()
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
		return new TreeNodeBreadthFirstIterator<>(self());
	}

	@Override
	public default Iterator<T> iterator() {
		return new TreeNodeBreadthFirstIterator<>(self());
	}

	/**
	 * Return a stream that traverses the subtree rooted at {@code this} node in
	 * breadth-first order. The first node returned by the stream is
	 * {@code this} node.
	 *
	 * @see #depthFirstIterator
	 * @return a stream for traversing the tree in breadth-first order
	 */
	public default Stream<T> breathFirstStream() {
		return StreamSupport
			.stream(spliteratorUnknownSize(breadthFirstIterator(), 0), false);
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
		return new TreeNodePreorderIterator<>(self());
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
		return new TreeNodePostorderIterator<>(self());
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
	pathFromAncestorIterator(final T ancestor) {
		return new TreeNodePathIterator<>(ancestor, self());
	}

}
