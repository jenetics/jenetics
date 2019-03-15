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
package io.jenetics.ext.rewriting;

import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.Hashes.hash;

import java.util.Map;

import io.jenetics.ext.rewriting.TreePattern.Var;
import io.jenetics.ext.util.Tree;

/**
 * The result of a tree match operation. It contains the matching tree and the
 * variables trees which matches the matching tree.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class TreeMatchResult<V> {

	private final Tree<V, ?> _tree;
	private final Map<Var<V>, Tree<V, ?>> _vars;

	private TreeMatchResult(
		final Tree<V, ?> tree,
		final Map<Var<V>, Tree<V, ?>> vars
	) {
		_tree = tree;
		_vars = unmodifiableMap(requireNonNull(vars));
	}

	/**
	 * The node (tree), which has been matched by some pattern.
	 *
	 * @return node (tree), which has been matched by some pattern
	 */
	public Tree<V, ?> tree() {
		return _tree;
	}

	/**
	 * The variables involved while matching the tree {@link #tree()}.
	 *
	 * @return variables involved while matching the tree {@link #tree()}.
	 */
	public Map<Var<V>, Tree<V, ?>> vars() {
		return _vars;
	}

	@Override
	public int hashCode() {
		return hash(_tree, hash(_vars));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof TreeMatchResult &&
			_tree.equals(((TreeMatchResult)obj)._tree) &&
			_vars.equals(((TreeMatchResult)obj)._vars);
	}

	@Override
	public String toString() {
		return _tree.toParenthesesString();
	}

	static <V> TreeMatchResult<V> of(
		final Tree<V, ?> tree,
		final Map<Var<V>, Tree<V, ?>> vars
	) {
		return new TreeMatchResult<>(tree, vars);
	}

}
