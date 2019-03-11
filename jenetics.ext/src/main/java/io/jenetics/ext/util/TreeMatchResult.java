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
import static io.jenetics.internal.util.Hashes.hash;

import java.util.Map;

/**
 * The result of a tree match operation.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.4
 * @since 4.4
 */
public final class TreeMatchResult<V> {
	private final Tree<V, ?> _node;
	private final Map<String, Tree<V, ?>> _variables;

	private TreeMatchResult(
		final Tree<V, ?> node,
		final Map<String, Tree<V, ?>> variables
	) {
		_node = requireNonNull(node);
		_variables = requireNonNull(variables);
	}

	public Tree<V, ?> node() {
		return _node;
	}

	public Map<String, Tree<V, ?>> variables() {
		return _variables;
	}

	@Override
	public int hashCode() {
		return hash(_node, hash(_variables));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof TreeMatchResult &&
			_node.equals(((TreeMatchResult)obj)._node) &&
			_variables.equals(((TreeMatchResult)obj)._variables);
	}

	@Override
	public String toString() {
		return format("MatchResult[%s]", _node.toParenthesesString());
	}

	static <V> TreeMatchResult<V> of(
		final Tree<V, ?> node,
		final Map<String, Tree<V, ?>> variables
	) {
		return new TreeMatchResult<>(node, variables);
	}

}
