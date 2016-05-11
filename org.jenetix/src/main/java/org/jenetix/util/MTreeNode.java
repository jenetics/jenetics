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

import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface MTreeNode<T> extends TreeNode<T> {

	@Override
	public Optional<? extends MTreeNode<? super T>> getParent();

	@Override
	public ISeq<? extends MTreeNode<? extends T>> getChildren();

	public MTreeNode<T> setValue(final T value);

	public MTreeNode<T> setParent(final MTreeNode<? super T> parent);

	public MTreeNode<T> add(final MTreeNode<? extends T> node);

	public MTreeNode<T> add(final int index, final MTreeNode<? extends T> node);

	public MTreeNode<T> remove(final MTreeNode<? extends T> node);

	public MTreeNode<T> remove(final int index);

	public MTreeNode<T> removeFromParent();

	public static <T> MTreeNode<T> of(final T value) {
		return new MTreeNodeImpl<>(value);
	}

}
