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
package org.jenetix;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class TreeNode<T> {

	private final T _value;
	private final TreeNode<T> _parent;
	private final List<TreeNode<T>> _children;

	private TreeNode(
		final T value,
		final TreeNode<T> parent,
		final List<TreeNode<T>> children
	) {
		_value = value;
		_parent = parent;
		_children = requireNonNull(children);
	}

	public T getValue() {
		return _value;
	}

	public Optional<TreeNode<T>> getParent() {
		return Optional.ofNullable(_parent);
	}

	public List<TreeNode<T>> getChildren() {
		return _children;
	}

}
