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
package org.jenetix.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jenetics.util.ISeq;

import org.jenetix.MTreeNode;
import org.jenetix.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class MTreeNodeImpl<T> implements MTreeNode<T> {

	private T _value;
	private MTreeNode<? super T> _parent;
	private final List<MTreeNode<? extends T>> _children = new ArrayList<>();

	public MTreeNodeImpl(final T value) {
		_value = value;
	}

	@Override
	public Optional<TreeNode<? super T>> getParent() {
		return Optional.ofNullable(_parent);
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
	public ISeq<TreeNode<? extends T>> getChildren() {
		return ISeq.of(_children);
	}

	@Override
	public MTreeNode<T> add(final MTreeNode<? extends T> node) {
		node.setParent(this);
		_children.add(node);
		return this;
	}

	@SafeVarargs
	@Override
	public final MTreeNode<T> addAll(final MTreeNode<? extends T>... nodes) {
		for (MTreeNode<? extends T> node : nodes) {
			add(node);
		}
		return this;
	}

	@Override
	public MTreeNode<T> add(int index, MTreeNode<? extends T> node) {
		node.setParent(this);
		_children.add(index, node);
		return this;
	}

	@Override
	public MTreeNode<T> remove(final MTreeNode<? extends T> node) {
		node.removeFromParent();
		_children.remove(node);
		return this;
	}

	@Override
	public MTreeNode<T> remove(final int index) {
		_children.remove(index).removeFromParent();
		return this;
	}

	@Override
	public MTreeNode<T> removeFromParent() {
		_parent = null;
		return this;
	}

	@Override
	public boolean isRoot() {
		return _parent == null;
	}

	@Override
	public boolean isLeaf() {
		return _children.isEmpty();
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
		final MTreeNode<Integer> tree =
			MTreeNode.of(0).addAll(
				MTreeNode.of(1),
				MTreeNode.of(2).addAll(
					MTreeNode.of(3),
					MTreeNode.of(4)
				),
				MTreeNode.of(5)
			);

		System.out.println(tree);
	}

}
