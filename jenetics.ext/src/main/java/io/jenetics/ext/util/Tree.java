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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.Spliterators.spliteratorUnknownSize;
import static io.jenetics.internal.util.SerialIO.readIntArray;
import static io.jenetics.internal.util.SerialIO.writeIntArray;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.jenetics.util.ISeq;
import io.jenetics.util.Self;

/**
 * General purpose tree structure. The interface only contains tree read methods.
 * For a mutable tree implementation have a look at the {@link TreeNode} class.
 *
 * @see TreeNode
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.0
 * @since 3.9
 */
public interface Tree<V, T extends Tree<V, T>> extends Self<T>, Iterable<T> {

	/* *************************************************************************
	 * Basic (abstract) operations. All other tree operations can be derived
	 * from these methods.
	 **************************************************************************/

	/**
	 * Return the value of the current {@code Tree} node. The value may be
	 * {@code null}.
	 *
	 * @return the value of the current {@code Tree} node
	 */
	V value();

	/**
	 * Return the <em>parent</em> node of this tree node.
	 *
	 * @return the parent node, or {@code Optional.empty()} if this node is the
	 *         root of the tree
	 */
	Optional<T> parent();

	/**
	 * Return the child node with the given index.
	 *
	 * @param index the child index
	 * @return the child node with the given index
	 * @throws IndexOutOfBoundsException  if the {@code index} is out of
	 *         bounds ({@code [0, childCount())})
	 */
	T childAt(final int index);

	/**
	 * Return the number of children this tree node consists of.
	 *
	 * @return the number of children this tree node consists of
	 */
	int childCount();


	/* *************************************************************************
	 * Derived operations
	 **************************************************************************/

	/**
	 * Return an iterator of the children of this {@code Tree} node.
	 *
	 * @return an iterator of the children of this {@code Tree} node.
	 */
	default Iterator<T> childIterator() {
		return new TreeChildIterator<V, T>(self());
	}

	/**
	 * Return a forward-order stream of this node's children.
	 *
	 * @return a stream of children of {@code this} node
	 */
	default Stream<T> childStream() {
		return StreamSupport.stream(
			Spliterators.spliterator(
				childIterator(),
				childCount(),
				Spliterator.SIZED | Spliterator.ORDERED
			),
			false
		);
	}

	/**
	 * Returns {@code true} if this node is the root of the tree.
	 *
	 * @return {@code true} if this node is the root of its tree, {@code false}
	 *         otherwise
	 */
	default boolean isRoot() {
		return parent().isEmpty();
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
	default int depth() {
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
	default int level() {
		Optional<T> ancestor = Optional.of(self());
		int levels = 0;
		while ((ancestor = ancestor.flatMap(Tree::parent)).isPresent()) {
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
	default int indexOf(final Tree<?, ?> child) {
		int index = -1;
		for (int i = 0, n = childCount(); i < n && index == -1; ++i) {
			if (childAt(i).identical(child)) {
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
	default int size() {
		return Trees.countChildren(this) + 1;
	}

	/**
	 * A tree is considered <em>empty</em> if it's {@link #value()} is
	 * {@code null} and has no children and parent. A newly created tree node
	 * with no value is <em>empty</em>.
	 *
	 * <pre>{@code
	 * final Tree<String, ?> tree = TreeNode.of();
	 * assert tree.isEmpty();
	 * }</pre>
	 *
	 * @since 7.0
	 *
	 * @return {@code true} if {@code this} tree is empty, {@code false}
	 *          otherwise
	 */
	default boolean isEmpty() {
		return value() == null && childCount() == 0 && parent().isEmpty();
	}


	/* *************************************************************************
	 * Query operations
	 **************************************************************************/

	/**
	 * Return the child node at the given {@code path}. A path consists of the
	 * child index at a give level, starting with level 1. (The root note has
	 * level zero.) {@code tree.childAtPath(Path.of(2))} will return the third
	 * child node of {@code this} node, if it exists and
	 * {@code tree.childAtPath(Path.of(2, 0))} will return the first child of
	 * the third child of {@code this node}.
	 *
	 * @since 4.4
	 *
	 * @see #childAtPath(int...)
	 *
	 * @param path the child path
	 * @return the child node at the given {@code path}
	 * @throws NullPointerException if the given {@code path} array is
	 *         {@code null}
	 */
	default Optional<T> childAtPath(final Path path) {
		T node = self();
		for (int i = 0; i < path.length() && node != null; ++i) {
			node = path.get(i) < node.childCount()
				? node.childAt(path.get(i))
				: null;
		}

		return Optional.ofNullable(node);
	}

	/**
	 * Return the child node at the given {@code path}. A path consists of the
	 * child index at a give level, starting with level 1. (The root note has
	 * level zero.) {@code tree.childAtPath(2)} will return the third child node
	 * of {@code this} node, if it exists and {@code tree.childAtPath(2, 0)} will
	 * return the first child of the third child of {@code this node}.
	 *
	 * @since 4.3
	 *
	 * @see #childAtPath(Path)
	 *
	 * @param path the child path
	 * @return the child node at the given {@code path}
	 * @throws NullPointerException if the given {@code path} array is
	 *         {@code null}
	 * @throws IllegalArgumentException if one of the path elements is smaller
	 *         than zero
	 */
	default Optional<T> childAtPath(final int... path) {
		return childAtPath(Path.of(path));
	}

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
	default boolean isAncestor(final Tree<?, ?> node) {
		requireNonNull(node);

		Optional<T> ancestor = Optional.of(self());
		boolean result;
		do {
			result = ancestor.filter(a -> a.identical(node)).isPresent();
		} while (!result &&
				(ancestor = ancestor.flatMap(Tree::parent)).isPresent());

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
	default boolean isDescendant(final Tree<?, ?> node) {
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
	default Optional<T> sharedAncestor(final T node) {
		requireNonNull(node);

		T ancestor = null;
		if (node.identical(this)) {
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

			while (diff > 0 && node1 != null) {
				node1 = node1.parent().orElse(null);
				--diff;
			}

			do {
				if (node1 != null && node1.identical(node2)) {
					ancestor = node1;
				}
				node1 = node1 != null
					? node1.parent().orElse(null)
					: null;
				node2 = node2.parent().orElse(null);
			} while (node1 != null && node2 != null && ancestor == null);
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
	default boolean isRelated(final Tree<?, ?> node) {
		requireNonNull(node);
		return node.root().identical(root());
	}

	/**
	 * Returns the path from the root, to get to this node. The last element in
	 * the path is this node.
	 *
	 * @since 5.1
	 *
	 * @return an array of TreeNode objects giving the path, where the
	 *         first element in the path is the root and the last
	 *         element is this node.
	 */
	default ISeq<T> pathElements() {
		return Trees.pathElementsFromRoot(self(), 0).toISeq();
	}

	/**
	 * Return the {@link Path} of {@code this} tree, such that
	 * <pre>{@code
	 * final Tree<Integer, ?> tree = ...;
	 * final Tree.Path path = tree.path();
	 * assert tree == tree.getRoot()
	 *     .childAtPath(path)
	 *     .orElse(null);
	 * }</pre>
	 *
	 * @since 5.1
	 *
	 * @return the path from the root element to {@code this} node.
	 */
	default Path path() {
		final int[] p = Trees.pathFromRoot(self(), 0);
		return Path.of(p);
	}

	/**
	 * Returns the root of the tree that contains this node. The root is the
	 * ancestor with no parent.
	 *
	 * @return the root of the tree that contains this node
	 */
	default T root() {
		T anc = self();
		T prev;

		do {
			prev = anc;
			anc = anc.parent().orElse(null);
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
	 * @return {@code true} if {@code node}is a child, {@code false} otherwise
	 * @throws NullPointerException if the given {@code node} is {@code null}
	 */
	default boolean isChild(final Tree<?, ?> node) {
		requireNonNull(node);
		return childCount() != 0 &&
			node.parent().equals(Optional.of(self()));
	}

	/**
	 * Return the first child of {@code this} node, or {@code Optional.empty()}
	 * if {@code this} node has no children.
	 *
	 * @return the first child of this node
	 */
	default Optional<T> firstChild() {
		return childCount() > 0
			? Optional.of(childAt(0))
			: Optional.empty();
	}

	/**
	 * Return the last child of {@code this} node, or {@code Optional.empty()}
	 * if {@code this} node has no children.
	 *
	 * @return the last child of this node
	 */
	default Optional<T> lastChild() {
		return childCount() > 0
			? Optional.of(childAt(childCount() - 1))
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
	default Optional<T> childAfter(final Tree<?, ?> child) {
		requireNonNull(child);

		final int index = indexOf(child);
		if (index == -1) {
			throw new IllegalArgumentException("The given node is not a child.");
		}

		return index < childCount() - 1
			? Optional.of(childAt(index + 1))
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
	default Optional<T> childBefore(final Tree<?, ?> child) {
		requireNonNull(child);

		final int index = indexOf(child);
		if (index == -1) {
			throw new IllegalArgumentException("The given node is not a child.");
		}

		return index > 0
			? Optional.of(childAt(index - 1))
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
	default Optional<T> nextNode() {
		Optional<T> next = Optional.empty();

		if (childCount() == 0) {
			T node = self();
			while (node != null && (next = node.nextSibling()).isEmpty()) {
				node = node.parent().orElse(null);
			}
		} else {
			next = Optional.of(childAt(0));
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
	default Optional<T> previousNode() {
		Optional<T> node = Optional.empty();

		if (parent().isPresent()) {
			final Optional<T> prev = previousSibling();
			if (prev.isPresent()) {
				node = prev.get().childCount() == 0
					? prev
					: prev.map(Tree::lastLeaf);
			} else {
				node = parent();
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
	default boolean isSibling(final Tree<?, ?> node) {
		return identical(requireNonNull(node)) ||
			parent().equals(node.parent());
	}

	/**
	 * Return the number of siblings of {@code this} node. A node is its own
	 * sibling (if it has no parent or no siblings, this method returns
	 * {@code 1}).
	 *
	 * @return the number of siblings of {@code this} node
	 */
	default int siblingCount() {
		return parent().map(Tree::childCount).orElse(1);
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
	default Optional<T> nextSibling() {
		return parent().flatMap(p -> p.childAfter(self()));
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
	default Optional<T> previousSibling() {
		return parent().flatMap(p -> p.childBefore(self()));
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
	default boolean isLeaf() {
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
	default T firstLeaf() {
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
	default T lastLeaf() {
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
	 * from a known position. But to traverse all the leaves in the tree, you
	 * should use {@link #depthFirstIterator()} to iterator the nodes in the
	 * tree and use {@link #isLeaf()} on each node to determine which are leaves.
	 *
	 * @see #depthFirstIterator
	 * @see #isLeaf
	 * @return return the next leaf past this node
	 */
	default Optional<T> nextLeaf() {
		return nextSibling()
			.map(Tree::firstLeaf)
			.or(() -> parent().flatMap(Tree::nextLeaf));
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
	 * from a known position. But to traverse all the leaves in the tree, you
	 * should use {@link #depthFirstIterator()} to iterate the nodes in the tree
	 * and use {@link #isLeaf()} on each node to determine which are leaves.
	 *
	 * @see #depthFirstIterator
	 * @see #isLeaf
	 * @return returns the leaf before {@code this} node
	 */
	default Optional<T> previousLeaf() {
		return previousSibling()
			.map(Tree::lastLeaf)
			.or(() -> parent().flatMap(Tree::previousLeaf));
	}

	/**
	 * Returns the total number of leaves that are descendants of this node.
	 * If this node is a leaf, returns {@code 1}. This method is {@code O(n)},
	 * where n is the number of descendants of {@code this} node.
	 *
	 * @see #isLeaf()
	 * @return the number of leaves beneath this node
	 */
	default int leafCount() {
		return (int)leaves().count();
	}

	/**
	 * Return a stream of leaves that are descendants of this node.
	 *
	 * @since 7.0
	 *
	 * @return a stream of leaves that are descendants of this node
	 */
	default Stream<T> leaves() {
		return breadthFirstStream().filter(Tree::isLeaf);
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
	default Iterator<T> breadthFirstIterator() {
		return new TreeNodeBreadthFirstIterator<>(self());
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
	default Iterator<T> iterator() {
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
	default Stream<T> breadthFirstStream() {
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
	default Stream<T> stream() {
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
	default Iterator<T> preorderIterator() {
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
	default Stream<T> preorderStream() {
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
	default Iterator<T> postorderIterator() {
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
	default Stream<T> postorderStream() {
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
	default Iterator<T> depthFirstIterator() {
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
	default Stream<T> depthFirstStream() {
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
	default Iterator<T> pathFromAncestorIterator(final Tree<?, ?> ancestor) {
		return new TreeNodePathIterator<>(ancestor, self());
	}

	/**
	 * Return the path of {@code this} child node from the root node. You will
	 * get {@code this} node, if you call {@link #childAtPath(Path)} on the
	 * root node of {@code this} node.
	 * <pre>{@code
	 * final Tree<?, ?> node = ...;
	 * final Tree<?, ?> root = node.getRoot();
	 * final int[] path = node.childPath();
	 * assert node == root.childAtPath(path);
	 * }</pre>
	 *
	 * @since 4.4
	 *
	 * @see #childAtPath(Path)
	 *
	 * @return the path of {@code this} child node from the root node.
	 */
	default Path childPath() {
		final Iterator<T> it = pathFromAncestorIterator(root());
		final int[] path = new int[level()];

		T tree = null;
		int index = 0;
		while (it.hasNext()) {
			final T child = it.next();
			if (tree != null) {
				path[index++] = tree.indexOf(child);
			}

			tree = child;
		}

		assert index == path.length;

		return new Path(path);
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
	default boolean identical(final Tree<?, ?> other) {
		return this == other;
	}

	/**
	 * Performs a reduction on the elements of {@code this} tree, using an
	 * associative reduction function. This can be used for evaluating a given
	 * expression tree in pre-order.
	 * <pre>{@code
	 * final Tree<String, ?> formula = TreeNode.parse("add(sub(6,div(230,10)),mul(5,6))");
	 * final double result = formula.reduce(new Double[0], (op, args) ->
	 *     switch (op) {
	 *         case "add" -> args[0] + args[1];
	 *         case "sub" -> args[0] - args[1];
	 *         case "mul" -> args[0] * args[1];
	 *         case "div" -> args[0] / args[1];
	 *         default -> Double.parseDouble(op);
	 *     }
	 * );
	 * assert result == 13.0;
	 * }</pre>
	 *
	 * @since 7.1
	 *
	 * @param neutral the neutral element of the reduction. In most cases this will
	 *        be {@code new U[0]}.
	 * @param reducer the reduce function
	 * @param <U> the result type
	 * @return the result of the reduction, or {@code null} if {@code this} tree
	 *         is empty ({@code isEmpty() == true})
	 */
	default <U> U reduce(
		final U[] neutral,
		final BiFunction<? super V, ? super U[], ? extends U> reducer
	) {
		requireNonNull(neutral);
		requireNonNull(reducer);

		@SuppressWarnings("unchecked")
		final class Reducing {
			private U reduce(final Tree<V, ?> node) {
				return node.isLeaf()
					? reducer.apply(node.value(), neutral)
					: reducer.apply(node.value(), children(node));
			}
			private U[] children(final Tree<V, ?> node) {
				final U[] values = (U[])Array.newInstance(
					neutral.getClass().getComponentType(),
					node.childCount()
				);
				for (int i = 0; i < node.childCount(); ++i) {
					values[i] = reduce(node.childAt(i));
				}
				return values;
			}
		}

		return isEmpty() ? null : new Reducing().reduce(this);
	}

	/* *************************************************************************
	 * 'toString' methods
	 **************************************************************************/

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
	 *  mul(div(cos(1.0),cos(π)),sin(mul(1.0,z)))
	 * </pre>
	 *
	 * @since 4.3
	 *
	 * @see #toParenthesesString()
	 * @see TreeFormatter#PARENTHESES
	 *
	 * @param mapper the {@code mapper} which converts the tree value to a string
	 * @return the string representation of the given tree
	 */
	default String toParenthesesString(final Function<? super V, String> mapper) {
		return TreeFormatter.PARENTHESES.format(this, mapper);
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
	 * @since 4.3
	 *
	 * @see #toParenthesesString(Function)
	 * @see TreeFormatter#PARENTHESES
	 *
	 * @return the string representation of the given tree
	 * @throws NullPointerException if the {@code mapper} is {@code null}
	 */
	default String toParenthesesString() {
		return toParenthesesString(Objects::toString);
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
	static int hashCode(final Tree<?, ?> tree) {
		return tree != null
			? tree.breadthFirstStream()
				.mapToInt(node -> 31*Objects.hashCode(node.value()) + 37)
				.sum() + 17
			: 0;
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
		return Trees.equals(a, b);
	}

	/**
	 * Return a string representation of the given tree, like the following
	 * example.
	 *
	 * <pre>
	 *  mul(div(cos(1.0), cos(π)), sin(mul(1.0, z)))
	 * </pre>
	 *
	 * This method is intended to be used when override the
	 * {@link Object#toString()} method.
	 *
	 * @param tree the input tree
	 * @return the string representation of the given tree
	 */
	static String toString(final Tree<?, ?> tree) {
		return tree.toParenthesesString();
	}


	/* *************************************************************************
	 * Inner classes
	 **************************************************************************/

	/**
	 * This class represents the path to child within a given tree. It allows
	 * pointing (and fetch) a tree child.
	 *
	 * @see Tree#childAtPath(Path)
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
	 * @version 6.0
	 * @since 4.4
	 */
	final class Path implements Serializable {

		@Serial
		private static final long serialVersionUID = 1L;

		private final int[] _path;

		private Path(final int[] path) {
			_path = requireNonNull(path);
		}

		/**
		 * Return the path length, which is the level of the child {@code this}
		 * path points to.
		 *
		 * @return the path length
		 */
		public int length() {
			return _path.length;
		}

		/**
		 * Return the child index at the given index (child level).
		 *
		 * @param index the path index
		 * @return the child index at the given child level
		 * @throws IndexOutOfBoundsException if the index is not with the range
		 *         {@code [0, length())}
		 */
		public int get(final int index) {
			return _path[index];
		}

		/**
		 * Return the path as {@code int[]} array.
		 *
		 * @return the path as {@code int[]} array
		 */
		public int[] toArray() {
			return _path.clone();
		}

		/**
		 * Appends the given {@code path} to {@code this} one.
		 *
		 * @param path the path to append
		 * @return a new {@code Path} with the given {@code path} appended
		 * @throws NullPointerException if the given {@code path} is {@code null}
		 */
		public Path append(final Path path) {
			final int[] p = new int[length() + path.length()];
			System.arraycopy(_path, 0, p, 0, length());
			System.arraycopy(path._path, 0, p, length(), path.length());
			return new Path(p);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(_path);
		}

		@Override
		public boolean equals(final Object obj) {
			return obj == this ||
				obj instanceof Path other &&
				Arrays.equals(_path, other._path);
		}

		@Override
		public String toString() {
			return Arrays.toString(_path);
		}

		/**
		 * Create a new path object from the given child indexes.
		 *
		 * @param path the child indexes
		 * @return a new tree path
		 * @throws IllegalArgumentException if one of the path elements is
		 *         smaller than zero
		 */
		public static Path of(final int... path) {
			for (int i = 0; i < path.length; ++i) {
				if (path[i] < 0) {
					throw new IllegalArgumentException(format(
						"Path element at position %d is smaller than zero: %d",
						i, path[i]
					));
				}
			}

			return new Path(path.clone());
		}


		/* *********************************************************************
		 *  Java object serialization
		 * ********************************************************************/

		@Serial
		private Object writeReplace() {
			return new SerialProxy(SerialProxy.TREE_PATH, this);
		}

		@Serial
		private void readObject(final ObjectInputStream stream)
			throws InvalidObjectException
		{
			throw new InvalidObjectException("Serialization proxy required.");
		}


		void write(final DataOutput out) throws IOException {
			writeIntArray(_path, out);
		}

		static Object read(final DataInput in) throws IOException {
			return Path.of(readIntArray(in));
		}

	}

}
