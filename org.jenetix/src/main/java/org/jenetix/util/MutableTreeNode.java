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

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.stream.IntStream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class MutableTreeNode<T> implements Serializable  {
	private static final long serialVersionUID = -1L;

	private T _value;
	private MutableTreeNode<T> _parent;
	private final List<MutableTreeNode<T>> _children = new ArrayList<>();

	/**
	 * Create a new tree node with no parent and children.
	 */
	public MutableTreeNode() {
		this(null);
	}

	/**
	 * Create a new tree node with no parent and children, but with the given
	 * user {@code value}.
	 *
	 * @param value the user value of the new tree node
	 */
	public MutableTreeNode(final T value) {
		_value = value;
	}

	//
	//  Primitives
	//

	/**
	 * Returns this node's parent or {@code null} if this node has no parent.
	 *
	 * @return the tree-node, or {@code null} if this node has no parent
	 */
	public MutableTreeNode<T> getParent() {
		return _parent;
	}

	/**
	 * Sets this node's parent, but does not change the parent's child array.
	 * This method is called from {@code insert()} and {@code remove()} to
	 * reassign a child's parent, and it should not be messaged from anywhere
	 * else.
	 *
	 * @param parent this node's new parent
	 */
	public void setParent(final MutableTreeNode<T> parent) {
		_parent = parent;
	}

	/**
	 * Returns the child at the specified index in this node's child array.
	 *
	 * @param index   an index into this node's child array
	 * @throws ArrayIndexOutOfBoundsException  if the {@code index} is out of
	 *         bounds
	 * @return the tree-node in this node's child array at the specified index
	 */
	public MutableTreeNode<T> getChild(final int index) {
		return _children.get(index);
	}

	/**
	 * Returns the number of children of this node.
	 *
	 * @return  an int giving the number of children of this node
	 */
	public int getChildCount() {
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
	 * @throws ArrayIndexOutOfBoundsException if {@code index} is out of bounds
	 * @throws IllegalArgumentException if {@code child} is an ancestor of
	 *         {@code this} node
	 * @throws NullPointerException if the given {@code child} is {@code null}
	 */
	public void insert(final int index, final MutableTreeNode<T> child) {
		requireNonNull(child);
		if (isNodeAncestor(child)) {
			throw new IllegalArgumentException("The new child is an ancestor.");
		}

		if (child.getParent() != null) {
			child.getParent().remove(child);
		}
		child.setParent(this);
		_children.add(index, child);
	}

	/**
	 * Removes the child at the specified index from this node's children and
	 * sets that node's parent to {@code null}. The child node to remove must be
	 * a {@code MutableTreeNode}.
	 *
	 * @param index the index in this node's child array of the child to remove
	 * @throws ArrayIndexOutOfBoundsException  if the {@ode index} is out of
	 *         bounds
	 */
	public void remove(final int index) {
		_children.remove(index).setParent(null);
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
	public int getIndex(MutableTreeNode<T> child) {
		requireNonNull(child);
		return _children.indexOf(child);
	}

	/**
	 * Return a forward-order iterator of this node's children.
	 *
	 * @return an iterator of this node's children
	 */
	public Iterator<MutableTreeNode<T>> children() {
		return _children.iterator();
	}

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


	//
	//  Derived methods
	//

	/**
	 * Removes the subtree rooted at this node from the tree, giving this node a
	 * {@code null}. Does nothing if this node is the root of its tree.
	 */
	public void removeFromParent() {
		if (_parent != null) {
			_parent.remove(this);
		}
	}

	/**
	 * Remove the {@code child} from this node's child array, giving it a
	 * {@code null} parent.
	 *
	 * @param child the child of this node to remove
	 * @throws NullPointerException if the given {@code child} is {@code null}
	 * @throws IllegalArgumentException if the given {@code child} is not a
	 *         child of this node
	 */
	public void remove(final MutableTreeNode<T> child) {
		requireNonNull(child);

		if (!isNodeChild(child)) {
			throw new IllegalArgumentException("The given child is not a child.");
		}
		remove(getIndex(child));
	}

	/**
	 * Removes all children fo this node and setting their parents to
	 * {@code null}. If this node has no children, this method does nothing.
	 */
	public void removeAllChildren() {
		for (int i = getChildCount() - 1; i >= 0; i--) {
			remove(i);
		}
	}

	/**
	 * Remove the given {@code child} from its parent and makes it a child of
	 * this node by adding it to the end of this node's child array.
	 *
	 * @param child the new child added to this node
	 * @throws NullPointerException if the given {@code child} is {@code null}
	 */
	public void add(final MutableTreeNode<T> child) {
		requireNonNull(child);

		if(child != null && child.getParent() == this) {
			insert(getChildCount() - 1, child);
		} else {
			insert(getChildCount(), child);
		}
	}

	//
	//  Tree Queries
	//

	/**
	 * Return {@code true} if the given {@code node} is an ancestor of
	 * {@code this} node -- if it is {@code this} node, {@code this} node's
	 * parent, or an ancestor of {@code this} node's parent. If the given
	 * {@code node} is {@code null}, this method returns {@code false}. This
	 * operation is at worst {@code O(h)} where {@code h} is the distance from
	 * the root to {@code this} node.
	 *
	 * @param node the node to test
	 * @return {@code true} if the given {@code node} is an ancestor of
	 *         {@code this} node, {@code false} otherwise
	 */
	public boolean isNodeAncestor(final MutableTreeNode<T> node) {
		if (node == null) {
			return false;
		}

		MutableTreeNode<T> ancestor = this;
		do {
			if (ancestor == node) {
				return true;
			}
		} while((ancestor = ancestor.getParent()) != null);

		return false;
	}

	/**
	 * Return {@code true} if the given {@code node} is a descendant of
	 * {@code this} node -- if it is {@code this} node, one of {@code this}
	 * node's children, or a descendant of one of {@code this} node's children.
	 * A node is considered a descendant of itself. If the given {@code node} is
	 * {@code null}, {@code false} is returned. This operation is at worst
	 * {@code O(h)} where {@code h} is the distance from the root to
	 * {@code this} node.
	 *
	 * @param node the node to test as descendant of this node
	 * @return {@code true} if this node is an ancestor of the given {@code node}
	 */
	public boolean isNodeDescendant(final MutableTreeNode<T> node) {
		return node != null ? node.isNodeAncestor(this) : null;
	}

	/**
	 * Returns the nearest common ancestor to this node and the given {@code node}.
	 * {@code null} is returned, if no such ancestor exists -- if this node and
	 * the given {@code node} are in different trees or if the given {@code node}
	 * is {@code null}. A node is considered an ancestor of itself.
	 *
	 * @param node {@code node} to find common ancestor with
	 * @return nearest ancestor common to this node and the given {@code node},
	 *         or {@code null} if none
	 */
	public MutableTreeNode<T> getSharedAncestor(final MutableTreeNode<T> node) {
		if (node == this) {
			return this;
		} else if (node == null) {
			return null;
		}

		int level1, level2, diff;
		MutableTreeNode<T> node1, node2;

		level1 = getLevel();
		level2 = node.getLevel();

		if (level2 > level1) {
			diff = level2 - level1;
			node1 = node;
			node2 = this;
		} else {
			diff = level1 - level2;
			node1 = this;
			node2 = node;
		}

		// Go up the tree until the nodes are at the same level
		while (diff > 0) {
			node1 = node1.getParent();
			diff--;
		}

		// Move up the tree until we find a common ancestor.  Since we know
		// that both nodes are at the same level, we won't cross paths
		// unknowingly (if there is a common ancestor, both nodes hit it in
		// the same iteration).

		do {
			if (node1 == node2) {
				return node1;
			}
			node1 = node1.getParent();
			node2 = node2.getParent();
		} while (node1 != null);// only need to check one -- they're at the
		// same level so if one is null, the other is

		if (node1 != null || node2 != null) {
			throw new Error ("nodes should be null");
		}

		return null;
	}

	/**
	 * Returns true if and only if the given {@code node} is in the same tree as
	 * {@code this} node..
	 *
	 * @return true if the given {@code node} is in the same tree as {@code this}
	 *         node, {@code false} otherwise. If the given {@code node} node is
	 *         {@code null}, {@code false} is returned.
	 */
	public boolean isNodeRelated(final MutableTreeNode<T> node) {
		return (node != null) && (getRoot() == node.getRoot());
	}

	/**
	 * Returns the depth of the tree rooted at this node -- the longest
	 * distance from this node to a leaf. If this node has no children, 0 is
	 * returned. This operation is much more expensive than {@link #getLevel()}
	 * because it must effectively traverse the entire tree rooted at this node.
	 *
	 * @return the depth of the tree whose root is this node
	 */
	public int getDepth() {
		Object  last = null;
		Iterator<MutableTreeNode<T>>     enum_ = breadthFirstEnumeration();

		while (enum_.hasNext()) {
			last = enum_.next();
		}

		if (last == null) {
			throw new Error ("nodes should be null");
		}

		return ((MutableTreeNode<T>)last).getLevel() - getLevel();
	}










	/**
	 * Returns the number of levels above this node -- the distance from
	 * the root to this node.  If this node is the root, returns 0.
	 *
	 * @see     #getDepth
	 * @return  the number of levels above this node
	 */
	public int getLevel() {
		MutableTreeNode<T> ancestor;
		int levels = 0;

		ancestor = this;
		while((ancestor = ancestor.getParent()) != null){
			levels++;
		}

		return levels;
	}


	/**
	 * Returns the path from the root, to get to this node.  The last
	 * element in the path is this node.
	 *
	 * @return an array of TreeNode objects giving the path, where the
	 *         first element in the path is the root and the last
	 *         element is this node.
	 */
	public MutableTreeNode<T>[] getPath() {
		return getPathToRoot(this, 0);
	}

	/**
	 * Builds the parents of node up to and including the root node,
	 * where the original node is the last element in the returned array.
	 * The length of the returned array gives the node's depth in the
	 * tree.
	 *
	 * @param aNode  the TreeNode to get the path for
	 * @param depth  an int giving the number of steps already taken towards
	 *        the root (on recursive calls), used to size the returned array
	 * @return an array of TreeNodes giving the path from the root to the
	 *         specified node
	 */
	protected MutableTreeNode<T>[] getPathToRoot(MutableTreeNode<T> aNode, int depth) {
		MutableTreeNode<T>[]              retNodes;

        /* Check for null, in case someone passed in a null node, or
           they passed in an element that isn't rooted at root. */
		if(aNode == null) {
			if(depth == 0)
				return null;
			else
				retNodes = new MutableTreeNode[depth];
		}
		else {
			depth++;
			retNodes = getPathToRoot(aNode.getParent(), depth);
			retNodes[retNodes.length - depth] = aNode;
		}
		return retNodes;
	}

	/**
	 * Returns the user object path, from the root, to get to this node.
	 * If some of the TreeNodes in the path have null user objects, the
	 * returned path will contain nulls.
	 */
	public Object[] getUserObjectPath() {
		MutableTreeNode<T>[]          realPath = getPath();
		Object[]            retPath = new Object[realPath.length];

		for(int counter = 0; counter < realPath.length; counter++)
			retPath[counter] = ((MutableTreeNode<T>)realPath[counter])
				.getValue();
		return retPath;
	}

	/**
	 * Returns the root of the tree that contains this node.  The root is
	 * the ancestor with a null parent.
	 *
	 * @see     #isNodeAncestor
	 * @return  the root of the tree that contains this node
	 */
	public MutableTreeNode<T> getRoot() {
		MutableTreeNode<T> ancestor = this;
		MutableTreeNode<T> previous;

		do {
			previous = ancestor;
			ancestor = ancestor.getParent();
		} while (ancestor != null);

		return previous;
	}


	/**
	 * Returns true if this node is the root of the tree.  The root is
	 * the only node in the tree with a null parent; every tree has exactly
	 * one root.
	 *
	 * @return  true if this node is the root of its tree
	 */
	public boolean isRoot() {
		return getParent() == null;
	}


	/**
	 * Returns the node that follows this node in a preorder traversal of this
	 * node's tree.  Returns null if this node is the last node of the
	 * traversal.  This is an inefficient way to traverse the entire tree; use
	 * an enumeration, instead.
	 *
	 * @see     #preorderEnumeration
	 * @return  the node that follows this node in a preorder traversal, or
	 *          null if this node is last
	 */
	public MutableTreeNode<T> getNextNode() {
		if (getChildCount() == 0) {
			// No children, so look for nextSibling
			MutableTreeNode<T> nextSibling = getNextSibling();

			if (nextSibling == null) {
				MutableTreeNode<T> aNode = (MutableTreeNode<T>)getParent();

				do {
					if (aNode == null) {
						return null;
					}

					nextSibling = aNode.getNextSibling();
					if (nextSibling != null) {
						return nextSibling;
					}

					aNode = (MutableTreeNode<T>)aNode.getParent();
				} while(true);
			} else {
				return nextSibling;
			}
		} else {
			return (MutableTreeNode<T>) getChild(0);
		}
	}


	/**
	 * Returns the node that precedes this node in a preorder traversal of
	 * this node's tree.  Returns <code>null</code> if this node is the
	 * first node of the traversal -- the root of the tree.
	 * This is an inefficient way to
	 * traverse the entire tree; use an enumeration, instead.
	 *
	 * @see     #preorderEnumeration
	 * @return  the node that precedes this node in a preorder traversal, or
	 *          null if this node is the first
	 */
	public MutableTreeNode<T> getPreviousNode() {
		MutableTreeNode<T> previousSibling;
		MutableTreeNode<T> myParent = (MutableTreeNode<T>)getParent();

		if (myParent == null) {
			return null;
		}

		previousSibling = getPreviousSibling();

		if (previousSibling != null) {
			if (previousSibling.getChildCount() == 0)
				return previousSibling;
			else
				return previousSibling.getLastLeaf();
		} else {
			return myParent;
		}
	}

	/**
	 * Creates and returns an enumeration that traverses the subtree rooted at
	 * this node in preorder.  The first node returned by the enumeration's
	 * <code>nextElement()</code> method is this node.<P>
	 *
	 * Modifying the tree by inserting, removing, or moving a node invalidates
	 * any enumerations created before the modification.
	 *
	 * @see     #postorderEnumeration
	 * @return  an enumeration for traversing the tree in preorder
	 */
	public Iterator<MutableTreeNode<T>> preorderEnumeration() {
		return new PreorderIterator(this);
	}

	/**
	 * Creates and returns an enumeration that traverses the subtree rooted at
	 * this node in postorder.  The first node returned by the enumeration's
	 * <code>nextElement()</code> method is the leftmost leaf.  This is the
	 * same as a depth-first traversal.<P>
	 *
	 * Modifying the tree by inserting, removing, or moving a node invalidates
	 * any enumerations created before the modification.
	 *
	 * @see     #depthFirstEnumeration
	 * @see     #preorderEnumeration
	 * @return  an enumeration for traversing the tree in postorder
	 */
	public Iterator<MutableTreeNode<T>> postorderEnumeration() {
		return new PostorderIterator(this);
	}

	/**
	 * Creates and returns an enumeration that traverses the subtree rooted at
	 * this node in breadth-first order.  The first node returned by the
	 * enumeration's <code>nextElement()</code> method is this node.<P>
	 *
	 * Modifying the tree by inserting, removing, or moving a node invalidates
	 * any enumerations created before the modification.
	 *
	 * @see     #depthFirstEnumeration
	 * @return  an enumeration for traversing the tree in breadth-first order
	 */
	public Iterator<MutableTreeNode<T>> breadthFirstEnumeration() {
		return new BreadthFirstIterator(this);
	}

	/**
	 * Creates and returns an enumeration that traverses the subtree rooted at
	 * this node in depth-first order.  The first node returned by the
	 * enumeration's <code>nextElement()</code> method is the leftmost leaf.
	 * This is the same as a postorder traversal.<P>
	 *
	 * Modifying the tree by inserting, removing, or moving a node invalidates
	 * any enumerations created before the modification.
	 *
	 * @see     #breadthFirstEnumeration
	 * @see     #postorderEnumeration
	 * @return  an enumeration for traversing the tree in depth-first order
	 */
	public Iterator<MutableTreeNode<T>> depthFirstEnumeration() {
		return postorderEnumeration();
	}

	/**
	 * Creates and returns an enumeration that follows the path from
	 * <code>ancestor</code> to this node.  The enumeration's
	 * <code>nextElement()</code> method first returns <code>ancestor</code>,
	 * then the child of <code>ancestor</code> that is an ancestor of this
	 * node, and so on, and finally returns this node.  Creation of the
	 * enumeration is O(m) where m is the number of nodes between this node
	 * and <code>ancestor</code>, inclusive.  Each <code>nextElement()</code>
	 * message is O(1).<P>
	 *
	 * Modifying the tree by inserting, removing, or moving a node invalidates
	 * any enumerations created before the modification.
	 *
	 * @see             #isNodeAncestor
	 * @see             #isNodeDescendant
	 * @exception       IllegalArgumentException if <code>ancestor</code> is
	 *                                          not an ancestor of this node
	 * @return  an enumeration for following the path from an ancestor of
	 *          this node to this one
	 */
	public Iterator<MutableTreeNode<T>> pathFromAncestorEnumeration(MutableTreeNode<T> ancestor) {
		return new PathBetweenNodesIterator(ancestor, this);
	}


	//
	//  Child Queries
	//

	/**
	 * Returns true if <code>aNode</code> is a child of this node.  If
	 * <code>aNode</code> is null, this method returns false.
	 *
	 * @return  true if <code>aNode</code> is a child of this node; false if
	 *                  <code>aNode</code> is null
	 */
	public boolean isNodeChild(MutableTreeNode<T> aNode) {
		boolean retval;

		if (aNode == null) {
			retval = false;
		} else {
			if (getChildCount() == 0) {
				retval = false;
			} else {
				retval = (aNode.getParent() == this);
			}
		}

		return retval;
	}


	/**
	 * Returns this node's first child.  If this node has no children,
	 * throws NoSuchElementException.
	 *
	 * @return  the first child of this node
	 * @exception       NoSuchElementException  if this node has no children
	 */
	public MutableTreeNode<T> getFirstChild() {
		if (getChildCount() == 0) {
			throw new NoSuchElementException("node has no children");
		}
		return getChild(0);
	}


	/**
	 * Returns this node's last child.  If this node has no children,
	 * throws NoSuchElementException.
	 *
	 * @return  the last child of this node
	 * @exception       NoSuchElementException  if this node has no children
	 */
	public MutableTreeNode<T> getLastChild() {
		if (getChildCount() == 0) {
			throw new NoSuchElementException("node has no children");
		}
		return getChild(getChildCount()-1);
	}


	/**
	 * Returns the child in this node's child array that immediately
	 * follows <code>aChild</code>, which must be a child of this node.  If
	 * <code>aChild</code> is the last child, returns null.  This method
	 * performs a linear search of this node's children for
	 * <code>aChild</code> and is O(n) where n is the number of children; to
	 * traverse the entire array of children, use an enumeration instead.
	 *
	 * @see             #_children
	 * @exception       IllegalArgumentException if <code>aChild</code> is
	 *                                  null or is not a child of this node
	 * @return  the child of this node that immediately follows
	 *          <code>aChild</code>
	 */
	public MutableTreeNode<T> getChildAfter(MutableTreeNode<T> aChild) {
		if (aChild == null) {
			throw new IllegalArgumentException("argument is null");
		}

		int index = getIndex(aChild);           // linear search

		if (index == -1) {
			throw new IllegalArgumentException("node is not a child");
		}

		if (index < getChildCount() - 1) {
			return getChild(index + 1);
		} else {
			return null;
		}
	}


	/**
	 * Returns the child in this node's child array that immediately
	 * precedes <code>aChild</code>, which must be a child of this node.  If
	 * <code>aChild</code> is the first child, returns null.  This method
	 * performs a linear search of this node's children for <code>aChild</code>
	 * and is O(n) where n is the number of children.
	 *
	 * @exception       IllegalArgumentException if <code>aChild</code> is null
	 *                                          or is not a child of this node
	 * @return  the child of this node that immediately precedes
	 *          <code>aChild</code>
	 */
	public MutableTreeNode<T> getChildBefore(MutableTreeNode<T> aChild) {
		if (aChild == null) {
			throw new IllegalArgumentException("argument is null");
		}

		int index = getIndex(aChild);           // linear search

		if (index == -1) {
			throw new IllegalArgumentException("argument is not a child");
		}

		if (index > 0) {
			return getChild(index - 1);
		} else {
			return null;
		}
	}


	//
	//  Sibling Queries
	//


	/**
	 * Returns true if <code>anotherNode</code> is a sibling of (has the
	 * same parent as) this node.  A node is its own sibling.  If
	 * <code>anotherNode</code> is null, returns false.
	 *
	 * @param   anotherNode     node to test as sibling of this node
	 * @return  true if <code>anotherNode</code> is a sibling of this node
	 */
	public boolean isNodeSibling(MutableTreeNode<T> anotherNode) {
		boolean retval;

		if (anotherNode == null) {
			retval = false;
		} else if (anotherNode == this) {
			retval = true;
		} else {
			MutableTreeNode<T> myParent = getParent();
			retval = (myParent != null && myParent == anotherNode.getParent());

			if (retval && !((MutableTreeNode<T>)getParent())
				.isNodeChild(anotherNode)) {
				throw new Error("sibling has different parent");
			}
		}

		return retval;
	}


	/**
	 * Returns the number of siblings of this node.  A node is its own sibling
	 * (if it has no parent or no siblings, this method returns
	 * <code>1</code>).
	 *
	 * @return  the number of siblings of this node
	 */
	public int getSiblingCount() {
		MutableTreeNode<T> myParent = getParent();

		if (myParent == null) {
			return 1;
		} else {
			return myParent.getChildCount();
		}
	}


	/**
	 * Returns the next sibling of this node in the parent's children array.
	 * Returns null if this node has no parent or is the parent's last child.
	 * This method performs a linear search that is O(n) where n is the number
	 * of children; to traverse the entire array, use the parent's child
	 * enumeration instead.
	 *
	 * @see     #_children
	 * @return  the sibling of this node that immediately follows this node
	 */
	public MutableTreeNode<T> getNextSibling() {
		MutableTreeNode<T> retval;

		MutableTreeNode<T> myParent = (MutableTreeNode<T>)getParent();

		if (myParent == null) {
			retval = null;
		} else {
			retval = (MutableTreeNode<T>)myParent.getChildAfter(this);      // linear search
		}

		if (retval != null && !isNodeSibling(retval)) {
			throw new Error("child of parent is not a sibling");
		}

		return retval;
	}


	/**
	 * Returns the previous sibling of this node in the parent's children
	 * array.  Returns null if this node has no parent or is the parent's
	 * first child.  This method performs a linear search that is O(n) where n
	 * is the number of children.
	 *
	 * @return  the sibling of this node that immediately precedes this node
	 */
	public MutableTreeNode<T> getPreviousSibling() {
		MutableTreeNode<T> retval;

		MutableTreeNode<T> myParent = (MutableTreeNode<T>)getParent();

		if (myParent == null) {
			retval = null;
		} else {
			retval = (MutableTreeNode<T>)myParent.getChildBefore(this);     // linear search
		}

		if (retval != null && !isNodeSibling(retval)) {
			throw new Error("child of parent is not a sibling");
		}

		return retval;
	}



	//
	//  Leaf Queries
	//

	/**
	 * Returns true if this node has no children.  To distinguish between
	 * nodes that have no children and nodes that <i>cannot</i> have
	 * children (e.g. to distinguish files from empty directories), use this
	 * method in conjunction with <code>getAllowsChildren</code>
	 *
	 * @return  true if this node has no children
	 */
	public boolean isLeaf() {
		return (getChildCount() == 0);
	}


	/**
	 * Finds and returns the first leaf that is a descendant of this node --
	 * either this node or its first child's first leaf.
	 * Returns this node if it is a leaf.
	 *
	 * @see     #isLeaf
	 * @see     #isNodeDescendant
	 * @return  the first leaf in the subtree rooted at this node
	 */
	public MutableTreeNode<T> getFirstLeaf() {
		MutableTreeNode<T> node = this;

		while (!node.isLeaf()) {
			node = (MutableTreeNode<T>)node.getFirstChild();
		}

		return node;
	}


	/**
	 * Finds and returns the last leaf that is a descendant of this node --
	 * either this node or its last child's last leaf.
	 * Returns this node if it is a leaf.
	 *
	 * @see     #isLeaf
	 * @see     #isNodeDescendant
	 * @return  the last leaf in the subtree rooted at this node
	 */
	public MutableTreeNode<T> getLastLeaf() {
		MutableTreeNode<T> node = this;

		while (!node.isLeaf()) {
			node = (MutableTreeNode<T>)node.getLastChild();
		}

		return node;
	}


	/**
	 * Returns the leaf after this node or null if this node is the
	 * last leaf in the tree.
	 * <p>
	 * In this implementation of the <code>MutableNode</code> interface,
	 * this operation is very inefficient. In order to determine the
	 * next node, this method first performs a linear search in the
	 * parent's child-list in order to find the current node.
	 * <p>
	 * That implementation makes the operation suitable for short
	 * traversals from a known position. But to traverse all of the
	 * leaves in the tree, you should use <code>depthFirstEnumeration</code>
	 * to enumerate the nodes in the tree and use <code>isLeaf</code>
	 * on each node to determine which are leaves.
	 *
	 * @see     #depthFirstEnumeration
	 * @see     #isLeaf
	 * @return  returns the next leaf past this node
	 */
	public MutableTreeNode<T> getNextLeaf() {
		MutableTreeNode<T> nextSibling;
		MutableTreeNode<T> myParent = (MutableTreeNode<T>)getParent();

		if (myParent == null)
			return null;

		nextSibling = getNextSibling(); // linear search

		if (nextSibling != null)
			return nextSibling.getFirstLeaf();

		return myParent.getNextLeaf();  // tail recursion
	}


	/**
	 * Returns the leaf before this node or null if this node is the
	 * first leaf in the tree.
	 * <p>
	 * In this implementation of the <code>MutableNode</code> interface,
	 * this operation is very inefficient. In order to determine the
	 * previous node, this method first performs a linear search in the
	 * parent's child-list in order to find the current node.
	 * <p>
	 * That implementation makes the operation suitable for short
	 * traversals from a known position. But to traverse all of the
	 * leaves in the tree, you should use <code>depthFirstEnumeration</code>
	 * to enumerate the nodes in the tree and use <code>isLeaf</code>
	 * on each node to determine which are leaves.
	 *
	 * @see             #depthFirstEnumeration
	 * @see             #isLeaf
	 * @return  returns the leaf before this node
	 */
	public MutableTreeNode<T> getPreviousLeaf() {
		MutableTreeNode<T> previousSibling;
		MutableTreeNode<T> myParent = (MutableTreeNode<T>)getParent();

		if (myParent == null)
			return null;

		previousSibling = getPreviousSibling(); // linear search

		if (previousSibling != null)
			return previousSibling.getLastLeaf();

		return myParent.getPreviousLeaf();              // tail recursion
	}


	/**
	 * Returns the total number of leaves that are descendants of this node.
	 * If this node is a leaf, returns <code>1</code>.  This method is O(n)
	 * where n is the number of descendants of this node.
	 *
	 * @see     #isNodeAncestor
	 * @return  the number of leaves beneath this node
	 */
	public int getLeafCount() {
		int count = 0;

		MutableTreeNode<T> node;
		Iterator<MutableTreeNode<T>> enum_ = breadthFirstEnumeration(); // order matters not

		while (enum_.hasNext()) {
			node = (MutableTreeNode<T>)enum_.next();
			if (node.isLeaf()) {
				count++;
			}
		}

		if (count < 1) {
			throw new Error("tree has zero leaves");
		}

		return count;
	}


	//
	//  Overrides
	//

	@Override
	public String toString() {
		return toString(this, new StringBuilder(), 0).toString();
	}

	private StringBuilder toString(
		final MutableTreeNode<?> node,
		final StringBuilder out,
		final int level
	) {
		for (int i = 0; i < level; ++i) {
			out.append("  ");
		}

		out.append("+- ").append(node.getValue()).append("\n");
		IntStream.range(0, node.getChildCount())
			.forEach(i -> toString(node.getChild(i), out, level + 1));

		return out;
	}

	private final class PreorderIterator implements Iterator<MutableTreeNode<T>> {
		private final Deque<Iterator<MutableTreeNode<T>>> _stack = new LinkedList<>();

		public PreorderIterator(final MutableTreeNode<T> root) {
			_stack.push(singletonList(root).iterator());
		}

		@Override
		public boolean hasNext() {
			return !_stack.isEmpty() && _stack.peek().hasNext();
		}

		public MutableTreeNode<T> next() {
			final Iterator<MutableTreeNode<T>> enumer = _stack.peek();
			final MutableTreeNode<T> node = enumer.next();
			final Iterator<MutableTreeNode<T>> children = node.children();

			if (!enumer.hasNext()) {
				_stack.pop();
			}
			if (children.hasNext()) {
				_stack.push(children);
			}
			return node;
		}
	}

	final class PostorderIterator implements Iterator<MutableTreeNode<T>> {
		protected MutableTreeNode<T> _root;
		protected Iterator<MutableTreeNode<T>> _children;
		protected Iterator<MutableTreeNode<T>> _subtree;

		public PostorderIterator(final MutableTreeNode<T> root) {
			_root = root;
			_children = _root.children();
			_subtree = Collections.emptyIterator();
		}

		@Override
		public boolean hasNext() {
			return _root != null;
		}

		@Override
		public MutableTreeNode<T> next() {
			MutableTreeNode<T> result;

			if (_subtree.hasNext()) {
				result = _subtree.next();
			} else if (_children.hasNext()) {
				_subtree = new PostorderIterator(_children.next());
				result = _subtree.next();
			} else {
				result = _root;
				_root = null;
			}

			return result;
		}

	}


	final class BreadthFirstIterator implements Iterator<MutableTreeNode<T>> {
		protected BreadthFirstIterator.Queue _queue;

		public BreadthFirstIterator(final MutableTreeNode<T> root) {
			_queue = new BreadthFirstIterator.Queue();
			_queue.enqueue(singletonList(root).iterator());
		}

		@Override
		public boolean hasNext() {
			return (!_queue.isEmpty() &&
				((Enumeration) _queue.firstObject()).hasMoreElements());
		}

		@Override
		public MutableTreeNode<T> next() {
			Iterator enumer = (Iterator) _queue.firstObject();
			MutableTreeNode<T> node = (MutableTreeNode<T>)enumer.next();
			Iterator children = node.children();

			if (!enumer.hasNext()) {
				_queue.dequeue();
			}
			if (children.hasNext()) {
				_queue.enqueue(children);
			}
			return node;
		}


		// A simple queue with a linked list data structure.
		final class Queue {
			BreadthFirstIterator.Queue.QNode head; // null if empty
			BreadthFirstIterator.Queue.QNode tail;

			final class QNode {
				public Object   object;
				public BreadthFirstIterator.Queue.QNode next;   // null if end
				public QNode(Object object, BreadthFirstIterator.Queue.QNode next) {
					this.object = object;
					this.next = next;
				}
			}

			public void enqueue(Object anObject) {
				if (head == null) {
					head = tail = new BreadthFirstIterator.Queue.QNode(anObject, null);
				} else {
					tail.next = new BreadthFirstIterator.Queue.QNode(anObject, null);
					tail = tail.next;
				}
			}

			public Object dequeue() {
				if (head == null) {
					throw new NoSuchElementException("No more elements");
				}

				Object retval = head.object;
				BreadthFirstIterator.Queue.QNode oldHead = head;
				head = head.next;
				if (head == null) {
					tail = null;
				} else {
					oldHead.next = null;
				}
				return retval;
			}

			public Object firstObject() {
				if (head == null) {
					throw new NoSuchElementException("No more elements");
				}

				return head.object;
			}

			public boolean isEmpty() {
				return head == null;
			}

		}

	}

	final class PathBetweenNodesIterator implements Iterator<MutableTreeNode<T>> {
		protected Stack<MutableTreeNode<T>> stack;

		public PathBetweenNodesIterator(MutableTreeNode<T> ancestor,
			MutableTreeNode<T> descendant)
		{
			super();

			if (ancestor == null || descendant == null) {
				throw new IllegalArgumentException("argument is null");
			}

			MutableTreeNode<T> current;

			stack = new Stack<MutableTreeNode<T>>();
			stack.push(descendant);

			current = descendant;
			while (current != ancestor) {
				current = current.getParent();
				if (current == null && descendant != ancestor) {
					throw new IllegalArgumentException("node " + ancestor +
						" is not an ancestor of " + descendant);
				}
				stack.push(current);
			}
		}

		@Override
		public boolean hasNext() {
			return stack.size() > 0;
		}

		@Override
		public MutableTreeNode<T> next() {
			try {
				return stack.pop();
			} catch (EmptyStackException e) {
				throw new NoSuchElementException("No more elements");
			}
		}

	}



}
