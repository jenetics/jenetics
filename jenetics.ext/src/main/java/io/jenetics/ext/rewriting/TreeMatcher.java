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

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

import io.jenetics.ext.util.Tree;

/**
 * Implementation of a pattern based tree matcher. It allows you to iterate over
 * all matches of a tree for a given pattern.
 *
 * <pre>{@code
 * final TreePattern<String> pattern = TreePattern.compile("add($x,$y)");
 * final Tree<String, ?> tree = TreeNode.parse("add(1,add(2,3))");
 * final TreeMatcher<String> matcher = pattern.matcher(tree);
 * matcher.results().forEach(r -> System.out.println(r.tree().toParenthesesString()));
 * // Prints:
 * // add(1,add(2,3))
 * // add(2,3)
 * }</pre>
 *
 * @see TreePattern#matcher(Tree)
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
public final class TreeMatcher<V> {

	private final TreePattern<V> _pattern;
	private final Tree<V, ?> _tree;

	private TreeMatcher(final TreePattern<V> pattern, final Tree<V, ?> tree) {
		_pattern = requireNonNull(pattern);
		_tree = requireNonNull(tree);
	}

	/**
	 * Return the underlying pattern of {@code this} matcher.
	 *
	 * @return the underlying tree pattern
	 */
	public TreePattern<V> pattern() {
		return _pattern;
	}

	/**
	 * Return the matching tree.
	 *
	 * @return the matching tree
	 */
	public Tree<V, ?> tree() {
		return _tree;
	}

	/**
	 * Tests if the tree matches the pattern.
	 *
	 * @return {@code true} if the tree matches against the pattern,
	 *         {@code false} otherwise
	 * @throws NullPointerException if the given predicate is {@code null}
	 */
	public boolean matches() {
		return _pattern.matches(_tree);
	}

	/**
	 * Return all matching <em>sub</em>-trees.
	 *
	 * @return all matching sub-trees
	 * @throws NullPointerException if the given predicate is {@code null}
	 */
	public Stream<TreeMatchResult<V>> results() {
		return _tree.stream()
			.flatMap(tree -> _pattern.match(tree).stream());
	}

	static <V> TreeMatcher<V> of(
		final TreePattern<V> pattern,
		final Tree<V, ?> tree
	) {
		return new TreeMatcher<>(pattern, tree);
	}

}
