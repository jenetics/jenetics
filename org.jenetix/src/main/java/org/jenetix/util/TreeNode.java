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

import java.util.Optional;
import java.util.function.ObjIntConsumer;

import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface TreeNode<T> {

	public T getValue();

	public TreeNode<? super T> getParent();

	public ISeq<? extends TreeNode<? extends T>> getChildren();

	/**
	 * Return the child at the given child {@code index}.
	 *
	 * @param index the child index
	 * @return the child tree-node at the given child {@code index}
	 * @throws  ArrayIndexOutOfBoundsException  if the given {@code index} is
	 *          out of bounds
	 */
	public TreeNode<? extends T> getChild(final int index);

	public int getChildCount();

	public default boolean isRoot() {
		return !getParent().isPresent();
	}

	public default boolean isLeaf() {
		return childrenCount() == 0;
	}

	public default void forEach(final ObjIntConsumer<? super T> consumer) {

	}

}
