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

import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.SerialIO.readIntArray;
import static io.jenetics.internal.util.SerialIO.readObjectArray;
import static io.jenetics.internal.util.SerialIO.writeIntArray;
import static io.jenetics.internal.util.SerialIO.writeObjectArray;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jenetics.util.ISeq;

/**
 * Default implementation of the {@link FlatTree} interface. Beside the
 * flattened and dense layout it is also an <em>immutable</em> implementation of
 * the {@link Tree} interface. It can only be created from an existing tree.
 *
 * <pre>{@code
 * final Tree<String, ?> immutable = FlatTreeNode.ofTree(TreeNode.parse(...));
 * }</pre>
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 3.9
 */
public final class FlatTreeNode<V>
	implements
		FlatTree<V, FlatTreeNode<V>>,
		Serializable
{

	/**
	 * The flattened tree nodes.
	 */
	private record Nodes(Object[] values, int[] childOffsets, int[] childCounts) {
	}

	@Serial
	private static final long serialVersionUID = 3L;

	private static final int NULL_INDEX = -1;

	private final Nodes _nodes;
	private final int _index;

	private FlatTreeNode(final Nodes nodes, final int index) {
		_nodes = requireNonNull(nodes);
		_index = index;
	}

	private FlatTreeNode(final Nodes nodes) {
		this(nodes, 0);
	}

	/**
	 * Returns the root of the tree that contains this node. The root is the
	 * ancestor with no parent. This implementation has a runtime complexity
	 * of O(1).
	 *
	 * @return the root of the tree that contains this node
	 */
	@Override
	public FlatTreeNode<V> root() {
		return nodeAt(0);
	}

	@Override
	public boolean isRoot() {
		return _index == 0;
	}

	private FlatTreeNode<V> nodeAt(final int index) {
		return new FlatTreeNode<>(_nodes, index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public V value() {
		return (V)_nodes.values[_index];
	}

	@Override
	public Optional<FlatTreeNode<V>> parent() {
		int index = NULL_INDEX;
		for (int i = _index; --i >= 0 && index == NULL_INDEX;) {
			if (isParent(i)) {
				index = i;
			}
		}

		return index != NULL_INDEX
			? Optional.of(nodeAt(index))
			: Optional.empty();
	}

	private boolean isParent(final int index) {
		return _nodes.childCounts[index] > 0 &&
			_nodes.childOffsets[index] <= _index &&
			_nodes.childOffsets[index] + _nodes.childCounts[index] > _index;
	}

	@Override
	public FlatTreeNode<V> childAt(final int index) {
		if (index < 0 || index >= childCount()) {
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}

		return nodeAt(childOffset() + index);
	}

	@Override
	public int childCount() {
		return _nodes.childCounts[_index];
	}

	/**
	 * Return the index of the first child node in the underlying node array.
	 * {@code -1} is returned if {@code this} node is a leaf.
	 *
	 * @return Return the index of the first child node in the underlying node
	 *         array, or {@code -1} if {@code this} node is a leaf
	 */
	@Override
	public int childOffset() {
		return _nodes.childOffsets[_index];
	}

	@Override
	public ISeq<FlatTreeNode<V>> flattenedNodes() {
		return stream().collect(ISeq.toISeq());
	}

	@Override
	public Iterator<FlatTreeNode<V>> breadthFirstIterator() {
		return _index == 0
			? new IntFunctionIterator<>(this::nodeAt, _nodes.values.length)
			: FlatTree.super.breadthFirstIterator();
	}

	@Override
	public Stream<FlatTreeNode<V>> breadthFirstStream() {
		return _index == 0
			? IntStream.range(0, _nodes.values.length).mapToObj(this::nodeAt)
			: FlatTree.super.breadthFirstStream();
	}

	/**
	 * Return a sequence of all <em>mapped</em> nodes of the whole underlying
	 * tree. This is a convenient method for
	 * <pre>{@code
	 * final ISeq<B> seq = stream()
	 *     .map(mapper)
	 *     .collect(ISeq.toISeq())
	 * }</pre>
	 *
	 * @param mapper the mapper function
	 * @param <B> the mapped type
	 * @return a sequence of all <em>mapped</em> nodes
	 */
	public <B> ISeq<B>
	map(final Function<? super FlatTreeNode<V>, ? extends B> mapper) {
		return stream()
			.map(mapper)
			.collect(ISeq.toISeq());
	}

	@Override
	public boolean identical(final Tree<?, ?> other) {
		return other == this ||
			other instanceof FlatTreeNode<?> node &&
			node._index == _index &&
			node._nodes == _nodes;
	}

	@Override
	public int hashCode() {
		return Tree.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof FlatTreeNode<?> other &&
			(equals(other) || Tree.equals(other, this));
	}

	private boolean equals(final FlatTreeNode<?> tree) {
		return tree._index == _index &&
			Arrays.equals(tree._nodes.values, _nodes.values) &&
			Arrays.equals(tree._nodes.childCounts, _nodes.childCounts) &&
			Arrays.equals(tree._nodes.childOffsets, _nodes.childOffsets);
	}

	@Override
	public String toString() {
		return toParenthesesString();
	}

	@Override
	public int size() {
		return _index == 0
			? _nodes.values.length
			: countChildren(_index) + 1;
	}

	private int countChildren(final int index) {
		int count = _nodes.childCounts[index];
		for (int i = 0; i < _nodes.childCounts[index]; ++i) {
			count += countChildren(_nodes.childOffsets[index] + i);
		}
		return count;
	}

	/* *************************************************************************
	 *  Static factories
	 * ************************************************************************/

	/**
	 * Create a new, immutable {@code FlatTreeNode} from the given {@code tree}.
	 *
	 * @param tree the source tree
	 * @param <V> the tree value types
	 * @return a new {@code FlatTreeNode} from the given {@code tree}
	 * @throws NullPointerException if the given {@code tree} is {@code null}
	 */
	public static <V> FlatTreeNode<V> ofTree(final Tree<? extends V, ?> tree) {
		requireNonNull(tree);

		final int size = tree.size();
		assert size >= 1;

		final var nodes = new Nodes(new Object[size], new int[size], new int[size]);

		int childOffset = 1;
		int index = 0;

		for (var node : tree) {
			nodes.values[index] = node.value();
			nodes.childCounts[index] = node.childCount();
			nodes.childOffsets[index] = node.isLeaf() ? NULL_INDEX : childOffset;

			childOffset += node.childCount();
			++index;
		}
		assert index == size;

		return new FlatTreeNode<>(nodes);
	}

	/**
	 * Parses a (parentheses) tree string, created with
	 * {@link Tree#toParenthesesString()}. The tree string might look like this:
	 * <pre>
	 *  mul(div(cos(1.0),cos(π)),sin(mul(1.0,z)))
	 * </pre>
	 *
	 * @see Tree#toParenthesesString(Function)
	 * @see Tree#toParenthesesString()
	 * @see TreeNode#parse(String)
	 *
	 * @since 5.0
	 *
	 * @param tree the parentheses tree string
	 * @return the parsed tree
	 * @throws NullPointerException if the given {@code tree} string is
	 *         {@code null}
	 * @throws IllegalArgumentException if the given tree string could not be
	 *         parsed
	 */
	public static FlatTreeNode<String> parse(final String tree) {
		return ofTree(ParenthesesTreeParser.parse(tree, Function.identity()));
	}

	/**
	 * Parses a (parentheses) tree string, created with
	 * {@link Tree#toParenthesesString()}. The tree string might look like this
	 * <pre>
	 *  0(1(4,5),2(6),3(7(10,11),8,9))
	 * </pre>
	 * and can be parsed to an integer tree with the following code:
	 * <pre>{@code
	 * final Tree<Integer, ?> tree = FlatTreeNode.parse(
	 *     "0(1(4,5),2(6),3(7(10,11),8,9))",
	 *     Integer::parseInt
	 * );
	 * }</pre>
	 *
	 * @see Tree#toParenthesesString(Function)
	 * @see Tree#toParenthesesString()
	 * @see TreeNode#parse(String, Function)
	 *
	 * @since 5.0
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
	public static <B> FlatTreeNode<B> parse(
		final String tree,
		final Function<? super String, ? extends B> mapper
	) {
		return ofTree(ParenthesesTreeParser.parse(tree, mapper));
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.FLAT_TREE_NODE, this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}


	void write(final ObjectOutput out) throws IOException {
		final FlatTreeNode<V> node = _index == 0
			? this
			: FlatTreeNode.ofTree(this);

		writeObjectArray(node._nodes.values, out);
		writeIntArray(node._nodes.childOffsets, out);
		writeIntArray(node._nodes.childCounts, out);
	}

	@SuppressWarnings("rawtypes")
	static FlatTreeNode read(final ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		return new FlatTreeNode(new Nodes(
			readObjectArray(in),
			readIntArray(in),
			readIntArray(in)
		));
	}

}
