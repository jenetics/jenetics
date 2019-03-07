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
import static io.jenetics.internal.util.Hashes.hash;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;

/**
 * Default implementation of the {@link FlatTree} interface.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.1
 * @since 3.9
 */
public final class FlatTreeNode<T>
	implements
		FlatTree<T, FlatTreeNode<T>>,
		Serializable
{
	private static final long serialVersionUID = 1L;

	private final int _index;
	private final MSeq<T> _nodes;
	private final int[] _childOffsets;
	private final int[] _childCounts;

	private FlatTreeNode(
		final int index,
		final MSeq<T> nodes,
		final int[] childOffsets,
		final int[] childCounts
	) {
		_index = index;
		_nodes = requireNonNull(nodes);
		_childOffsets = requireNonNull(childOffsets);
		_childCounts = requireNonNull(childCounts);
	}

	/**
	 * Returns the root of the tree that contains this node. The root is the
	 * ancestor with no parent. This implementation have a runtime complexity
	 * of O(1).
	 *
	 * @return the root of the tree that contains this node
	 */
	@Override
	public FlatTreeNode<T> getRoot() {
		return nodeAt(0);
	}

	@Override
	public boolean isRoot() {
		return _index == 0;
	}

	private FlatTreeNode<T> nodeAt(final int index) {
		return new FlatTreeNode<T>(
			index,
			_nodes,
			_childOffsets,
			_childCounts
		);
	}

	@Override
	public T getValue() {
		return _nodes.get(_index);
	}

	@Override
	public Optional<FlatTreeNode<T>> getParent() {
		int index = -1;
		for (int i = _index; --i >= 0 && index == -1;) {
			if (isParent(i)) {
				index = i;
			}
		}

		return index != -1
			? Optional.of(nodeAt(index))
			: Optional.empty();
	}

	private boolean isParent(final int index) {
		return _childCounts[index] > 0 &&
			_childOffsets[index] <= _index &&
			_childOffsets[index] + _childCounts[index] > _index;
	}

	@Override
	public FlatTreeNode<T> getChild(final int index) {
		if (index < 0 || index >= childCount()) {
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}

		return new FlatTreeNode<T>(
			childOffset() + index,
			_nodes,
			_childOffsets,
			_childCounts
		);
	}

	@Override
	public int childCount() {
		return _childCounts[_index];
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
		return _childOffsets[_index];
	}

	@Override
	public ISeq<FlatTreeNode<T>> flattenedNodes() {
		return stream().collect(ISeq.toISeq());
	}

	/**
	 * Return a stream of all nodes of the whole underlying tree. This method
	 * call is equivalent to
	 * <pre>{@code
	 * final Stream<FlatTreeNode<T>> nodes = getRoot().breadthFirstStream();
	 * }</pre>
	 *
	 * @return a stream of all nodes of the whole underlying tree
	 */
	public Stream<FlatTreeNode<T>> stream() {
		return IntStream.range(0, _nodes.size()).mapToObj(this::nodeAt);
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
	public <B> ISeq<B> map(final Function<FlatTreeNode<T>, ? extends B> mapper) {
		return stream()
			.map(mapper)
			.collect(ISeq.toISeq());
	}

	@Override
	public boolean identical(final Tree<?, ?> other) {
		return other == this ||
			other instanceof FlatTreeNode &&
			((FlatTreeNode)other)._index == _index &&
			((FlatTreeNode)other)._nodes == _nodes;
	}

	@Override
	public int hashCode() {
		return hash(_index, hash(_nodes, hash(_childCounts, hash(_childOffsets))));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof FlatTreeNode &&
			((FlatTreeNode)obj)._index == _index &&
			Objects.equals(((FlatTreeNode)obj)._nodes, _nodes) &&
			Arrays.equals(((FlatTreeNode)obj)._childCounts, _childCounts) &&
			Arrays.equals(((FlatTreeNode)obj)._childOffsets, _childOffsets);
	}

	@Override
	public String toString() {
		return Objects.toString(getValue());
	}

	/**
	 * Create a new {@code FlatTreeNode} from the given {@code tree}.
	 *
	 * @param tree the source tree
	 * @param <V> the tree value types
	 * @return a new {@code FlatTreeNode} from the given {@code tree}
	 * @throws NullPointerException if the given {@code tree} is {@code null}
	 */
	public static <V> FlatTreeNode<V> of(final Tree<? extends V, ?> tree) {
		requireNonNull(tree);

		final int size = tree.size();
		final MSeq<V> elements = MSeq.ofLength(size);
		final int[] childOffsets = new int[size];
		final int[] childCounts = new int[size];

		assert size >= 1;
		final FlatTreeNode<V> root = new FlatTreeNode<>(
			0,
			elements,
			childOffsets,
			childCounts
		);

		int childOffset = 1;
		int index = 0;
		final Iterator<? extends Tree<? extends V, ?>> it =
			tree.breadthFirstIterator();

		while (it.hasNext()) {
			final Tree<? extends V, ?> node = it.next();

			elements.set(index, node.getValue());
			childCounts[index] = node.childCount();
			childOffsets[index] = node.isLeaf() ? -1 : childOffset;

			childOffset += node.childCount();
			++index;
		}

		return root;
	}

}
