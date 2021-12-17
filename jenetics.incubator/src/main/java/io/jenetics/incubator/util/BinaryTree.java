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
package io.jenetics.incubator.util;

import static java.lang.String.format;

import java.util.Optional;

import io.jenetics.ext.util.Tree;

public final class BinaryTree<V> implements Tree<V, BinaryTree<V>> {

	private V _value;
	private BinaryTree<V> _parent;
	private BinaryTree<V> _left;
	private BinaryTree<V> _right;

	/**
	 * Create a new tree node with no parent and children, but with the given
	 * user {@code value}.
	 *
	 * @param value the user value of the new tree node
	 */
	private BinaryTree(final V value) {
		_value = value;
	}

	/**
	 * Sets the user object for this node.
	 *
	 * @param value the node {@code value}
	 */
	public void value(final V value) {
		_value = value;
	}

	@Override
	public V value() {
		return _value;
	}

	@Override
	public Optional<BinaryTree<V>> parent() {
		return Optional.ofNullable(_parent);
	}

	@Override
	public BinaryTree<V> childAt(final int index) {
		switch (index) {
			case 0 -> {
				if (_left != null) return _left;
				if (_right != null) return _right;
				throw error(index);
			}
			case 1 -> {
				if (_right != null) return _right;
				throw error(index);
			}
			default -> throw error(index);
		}
	}

	private static ArrayIndexOutOfBoundsException error(final int index) {
		return new ArrayIndexOutOfBoundsException(format(
			"Child index is out of bounds: %s", index
		));
	}

	@Override
	public int childCount() {
		if (_left != null) {
			return _right != null ? 2 : 1;
		} else {
			return _right != null ? 1 : 0;
		}
	}

	public BinaryTree<V> left(final V left) {
		_left = of(left);
		return this;
	}

	public BinaryTree<V> left(final BinaryTree<V> left) {
		_left = left;
		return this;
	}

	public Optional<BinaryTree<V>> left() {
		return Optional.ofNullable(_left);
	}

	public BinaryTree<V> right(final V right) {
		_right = of(right);
		return this;
	}

	public BinaryTree<V> right(final BinaryTree<V> right) {
		_right = right;
		return this;
	}

	public Optional<BinaryTree<V>> right() {
		return Optional.ofNullable(_right);
	}

	public static <V> BinaryTree<V> of() {
		return of(null);
	}

	public static <V> BinaryTree<V> of(final V value) {
		return new BinaryTree<>(value);
	}

}
