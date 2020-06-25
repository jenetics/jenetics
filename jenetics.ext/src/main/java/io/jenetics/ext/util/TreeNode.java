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

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import io.jenetics.util.Copyable;
import io.jenetics.util.ISeq;

/**
 * A general purpose node in a tree data-structure. The {@code TreeNode} is a
 * mutable implementation of the {@link Tree} interface.
 *
 * @param <T> the value type of the tree node
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.2
 * @since 3.9
 */
public final class TreeNode<T>
	implements
		Tree<T, TreeNode<T>>,
		Iterable<TreeNode<T>>,
		Copyable<TreeNode<T>>,
		Serializable
{
	private static final long serialVersionUID = 2L;

	private T _value;
	private TreeNode<T> _parent;
	private List<TreeNode<T>> _children;

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
	public void value(final T value) {
		_value = value;
	}

	/**
	 * Return the node value
	 *
	 * @return the node value
	 */
	@Override
	public T value() {
		return _value;
	}

	/**
	 * Returns this node's parent if available.
	 *
	 * @return the tree-node, or an empty value if this node has no parent
	 */
	@Override
	public Optional<TreeNode<T>> parent() {
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
	void parent(final TreeNode<T> parent) {
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
	public TreeNode<T> childAt(final int index) {
		if (_children == null) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Child index is out of bounds: %s", index
			));
		}

		return _children.get(index);
	}

	@Override
	public int childCount() {
		return _children != null ? _children.size() : 0;
	}

	@Override
	public Iterator<TreeNode<T>> childIterator() {
		return _children != null
			? _children.iterator()
			: Collections.emptyIterator();
	}

	@Override
	public Stream<TreeNode<T>> childStream() {
		return _children != null
			? _children.stream()
			: Stream.empty();
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

		child.parent(this);
		createChildrenIfMissing();
		_children.add(index, child);

		return this;
	}

	// Only entry point for checking and creating non-existing children list.
	private void createChildrenIfMissing() {
		if (_children == null) {
			_children = new ArrayList<>(2);
		}
	}

	/**
	 * Replaces the child at the give index with the given {@code child}
	 *
	 * @param index the index of the child which will be replaced
	 * @param child the new child
	 * @return {@code this} tree-node, for method chaining
	 * @throws ArrayIndexOutOfBoundsException  if the {@code index} is out of
	 *         bounds
	 * @throws IllegalArgumentException if {@code child} is an ancestor of
	 *         {@code this} node
	 * @throws NullPointerException if the given {@code child} is {@code null}
	 */
	public TreeNode<T> replace(final int index, final TreeNode<T> child) {
		requireNonNull(child);
		if (_children == null) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Child index is out of bounds: %s", index
			));
		}
		if (isAncestor(child)) {
			throw new IllegalArgumentException("The new child is an ancestor.");
		}

		final TreeNode<T> oldChild = _children.set(index, child);
		assert oldChild != null;
		assert oldChild._parent == this;

		oldChild.parent(null);
		child.parent(this);

		return this;
	}

	/**
	 * Removes the child at the specified index from this node's children and
	 * sets that node's parent to {@code null}.
	 *
	 * @param index the index in this node's child array of the child to remove
	 * @return {@code this} tree-node, for method chaining
	 * @throws ArrayIndexOutOfBoundsException  if the {@code index} is out of
	 *         bounds
	 */
	public TreeNode<T> remove(final int index) {
		if (_children == null) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Child index is out of bounds: %s", index
			));
		}

		final TreeNode<T> child = _children.remove(index);
		assert child._parent == this;
		child.parent(null);

		if (_children.isEmpty()) {
			_children = null;
		}

		return this;
	}

	/**
	 * Removes the child at the given {@code path}. If no child exists at the
	 * given path, nothing is removed.
	 *
	 * @since 4.4
	 *
	 * @param path the path of the child to replace
	 * @return {@code true} if a child at the given {@code path} existed and
	 *         has been removed
	 * @throws NullPointerException if one of the given argument is {@code null}
	 */
	public boolean removeAtPath(final Path path) {
		final Optional<TreeNode<T>> parent = childAtPath(path)
			.flatMap(Tree::parent);

		parent.ifPresent(p -> p.remove(path.get(path.length() - 1)));
		return parent.isPresent();
	}

	/**
	 * Replaces the child at the given {@code path} with the given new
	 * {@code child}. If no child exists at the given path, nothing is replaced.
	 *
	 * @since 4.4
	 *
	 * @param path the path of the child to replace
	 * @param child the new child
	 * @return {@code true} if a child at the given {@code path} existed and
	 *         has been replaced
	 * @throws NullPointerException if one of the given argument is {@code null}
	 */
	public boolean replaceAtPath(final Path path, final TreeNode<T> child) {
		requireNonNull(path);
		requireNonNull(child);

		final Optional<TreeNode<T>> old = childAtPath(path);
		final Optional<TreeNode<T>> parent = old.flatMap(TreeNode::parent);

		if (parent.isPresent()) {
			parent.orElseThrow(AssertionError::new)
				.replace(path.get(path.length() - 1), child);
		} else {
			removeAllChildren();
			value(child.value());

			final ISeq<TreeNode<T>> nodes = child.childStream()
				.collect(ISeq.toISeq());

			for (TreeNode<T> node : nodes) {
				attach(node);
			}
		}

		return old.isPresent();
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
	public void remove(final Tree<?, ?> child) {
		requireNonNull(child);

		if (!isChild(child)) {
			throw new IllegalArgumentException("The given child is not a child.");
		}
		remove(indexOf(child));
	}

	/**
	 * Removes all children fo {@code this} node and setting their parents to
	 * {@code null}. If {@code this} node has no children, this method does
	 * nothing.
	 */
	public void removeAllChildren() {
		if (_children != null) {
			for (TreeNode<T> child : _children) {
				child.parent(null);
			}

			_children = null;
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

	/**
	 * Attaches the given {@code children} to {@code this} node.
	 *
	 * @param children the children to attach to {@code this} node
	 * @return {@code this} tree-node, for method chaining
	 * @throws NullPointerException if the given {@code children} array is
	 *         {@code null}
	 */
	@SafeVarargs
	public final TreeNode<T> attach(final T... children) {
		for (T child : children) {
			attach(TreeNode.of(child));
		}

		return this;
	}

	/**
	 * Attaches the given {@code child} to {@code this} node.
	 *
	 * @param child the child to attach to {@code this} node
	 * @return {@code this} tree-node, for method chaining
	 */
	public TreeNode<T> attach(final T child) {
		return attach(TreeNode.of(child));
	}

	@Override
	public TreeNode<T> copy() {
		return ofTree(this);
	}

	/**
	 * Returns a new {@code TreeNode} consisting of all nodes of {@code this}
	 * tree, but with a different value type, created by applying the given
	 * function to the node values of {@code this} tree.
	 *
	 * @param mapper the node value mapper
	 * @param <B> the new node type
	 * @return a new tree consisting of all nodes of {@code this} tree
	 * @throws NullPointerException if the given {@code mapper} function is
	 *         {@code null}
	 */
	public <B> TreeNode<B> map(final Function<? super T, ? extends B> mapper) {
		final TreeNode<B> target = TreeNode.of(mapper.apply(value()));
		fill(this, target, mapper);
		return target;
	}


	@Override
	public int hashCode() {
		return Tree.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof TreeNode &&
			Tree.equals(this, (TreeNode)obj);
	}

	@Override
	public String toString() {
		return toParenthesesString();
	}



	/* *************************************************************************
	 * Static factory methods.
	 **************************************************************************/

	/**
	 * Return a new {@code TreeNode} with a {@code null} tree value.
	 *
	 * @param <T> the tree-node type
	 * @return a new tree-node
	 */
	public static <T> TreeNode<T> of() {
		return TreeNode.of(null);
	}

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
	 * Return a new {@code TreeNode} from the given source {@code tree}. The
	 * whole tree is copied.
	 *
	 * @param tree the source tree the new tree-node is created from
	 * @param mapper the tree value mapper function
	 * @param <T> the current tree value type
	 * @param <B> the mapped tree value type
	 * @return a new {@code TreeNode} from the given source {@code tree}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T, B> TreeNode<B> ofTree(
		final Tree<? extends T, ?> tree,
		final Function<? super T, ? extends B> mapper
	) {
		final TreeNode<B> target = of(mapper.apply(tree.value()));
		fill(tree, target, mapper);
		return target;
	}

	private static <T, B> void fill(
		final Tree<? extends T, ?> source,
		final TreeNode<B> target,
		final Function<? super T, ? extends B> mapper
	) {
		source.childStream().forEachOrdered(child -> {
			final TreeNode<B> targetChild = of(mapper.apply(child.value()));
			target.attach(targetChild);
			fill(child, targetChild, mapper);
		});
	}

	/**
	 * Return a new {@code TreeNode} from the given source {@code tree}. The
	 * whole tree is copied.
	 *
	 * @param tree the source tree the new tree-node is created from
	 * @param <T> the current tree value type
	 * @return a new {@code TreeNode} from the given source {@code tree}
	 * @throws NullPointerException if the source {@code tree} is {@code null}
	 */
	public static <T> TreeNode<T> ofTree(final Tree<? extends T, ?> tree) {
		return ofTree(tree, Function.identity());
	}

	/**
	 * Parses a (parentheses) tree string, created with
	 * {@link Tree#toParenthesesString()}. The tree string might look like this:
	 * <pre>
	 *  mul(div(cos(1.0),cos(π)),sin(mul(1.0,z)))
	 * </pre>
	 *
	 * The parse method doesn't strip the whitespace between the parentheses and
	 * the commas. If you want to remove this <em>formatting</em> whitespaces,
	 * you should do the parsing with an addition <em>mapper</em> function.
	 * <pre>{@code
	 * final TreeNode<String> tree = TreeNode.parse(
	 *     "mul(  div(cos( 1.0) , cos(π )), sin(mul(1.0, z) ) )",
	 *     String::trim
	 * );
	 * }</pre>
	 * The code above will trim all tree nodes during the parsing process.
	 *
	 * @see Tree#toParenthesesString(Function)
	 * @see Tree#toParenthesesString()
	 * @see TreeNode#parse(String, Function)
	 *
	 * @since 4.3
	 *
	 * @param tree the parentheses tree string
	 * @return the parsed tree
	 * @throws NullPointerException if the given {@code tree} string is
	 *         {@code null}
	 * @throws IllegalArgumentException if the given tree string could not be
	 *         parsed
	 */
	public static TreeNode<String> parse(final String tree) {
		return ParenthesesTreeParser.parse(tree, Function.identity());
	}

	/**
	 * Parses a (parentheses) tree string, created with
	 * {@link Tree#toParenthesesString()}. The tree string might look like this
	 * <pre>
	 *  0(1(4,5),2(6),3(7(10,11),8,9))
	 * </pre>
	 * and can be parsed to an integer tree with the following code:
	 * <pre>{@code
	 * final Tree<Integer, ?> tree = TreeNode.parse(
	 *     "0(1(4,5),2(6),3(7(10,11),8,9))",
	 *     Integer::parseInt
	 * );
	 * }</pre>
	 *
	 * @see Tree#toParenthesesString(Function)
	 * @see Tree#toParenthesesString()
	 * @see TreeNode#parse(String)
	 *
	 * @since 4.3
	 *
	 * @param <B> the tree node value type
	 * @param tree the parentheses tree string
	 * @param mapper the mapper which converts the serialized string value to
	 *        the desired type
	 * @return the parsed tree object
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the given parentheses tree string
	 *         doesn't represent a valid tree
	 */
	public static <B> TreeNode<B> parse(
		final String tree,
		final Function<? super String, ? extends B> mapper
	) {
		return ParenthesesTreeParser.parse(tree, mapper);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.TREE_NODE, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}


	void write(final ObjectOutput out) throws IOException {
		FlatTreeNode.ofTree(this).write(out);
	}

	@SuppressWarnings("unchecked")
	static Object read(final ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		return TreeNode.ofTree(FlatTreeNode.read(in));
	}

}
