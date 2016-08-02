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

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * General purpose tree structure.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Tree<V, T extends Tree<V, T>> {

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

	public default Stream<T> children() {
		return IntStream.range(0, childCount()).mapToObj(this::getChild);
	}

	public default Stream<T> nodes() {
		@SuppressWarnings("unchecked")
		final Stream<T> start = (Stream<T>)Stream.of(this);
		return Stream.concat(start, children());
	}

	public default boolean isRoot() {
		return !getParent().isPresent();
	}

	public default boolean isLeaf() {
		return childCount() == 0;
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

}
