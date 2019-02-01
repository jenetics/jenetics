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
package io.jenetics.ext.internal.util;

import static java.util.Objects.requireNonNull;

import java.util.function.BiPredicate;
import java.util.stream.Stream;

import io.jenetics.ext.util.Tree;

/**
 * Implementation of a pattern based tree matcher.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class TreeMatcher<V> {

	private final TreePattern _pattern;
	private final Tree<V, ?> _tree;
	private final BiPredicate<? super V, ? super String> _equals;

	private TreeMatcher(
		final TreePattern pattern,
		final Tree<V, ?> tree,
		final BiPredicate<? super V, ? super String> equals
	) {
		_pattern = requireNonNull(pattern);
		_tree = requireNonNull(tree);
		_equals = requireNonNull(equals);
	}

	/**
	 * Return the underlying pattern of {@code this} matcher.
	 *
	 * @return the underlying tree pattern
	 */
	public TreePattern pattern() {
		return _pattern;
	}

	/**
	 * Tests if the tree matches the pattern, using the given {@code equals}
	 * predicate.
	 *
	 * @param equals the predicate, used for comparing the node value and the
	 *        pattern string
	 * @return {@code true} if the tree matches against the pattern,
	 *         {@code false} otherwise
	 * @throws NullPointerException if the given predicate is {@code null}
	 */
	public boolean matches(final BiPredicate<? super V, ? super String> equals) {
		return _pattern.matches(_tree, equals);
	}

	/**
	 * Tests if the tree matches the pattern.
	 *
	 * @return {@code true} if the tree matches against the pattern,
	 *         {@code false} otherwise
	 * @throws NullPointerException if the given predicate is {@code null}
	 */
	public boolean matches() {
		return matches(TreePattern::equals);
	}

	/**
	 * Return all matching <em>sub</em>-trees.
	 *
	 * @param equals the predicate, used for comparing the node value and the
	 *        pattern string
	 * @return all matching sub-trees
	 * @throws NullPointerException if the given predicate is {@code null}
	 */
	public Stream<TreeMatchResult<V>> results(final BiPredicate<V, String> equals) {
		return _tree.stream()
			.flatMap((Tree<V, ?> tree) -> _pattern.match(tree, equals)
				.map(Stream::of)
				.orElseGet(Stream::empty));
	}

	/**
	 * Return all matching <em>sub</em>-trees.
	 *
	 * @return all matching sub-trees
	 */
	public Stream<TreeMatchResult<V>> results() {
		return results(TreePattern::equals);
	}

	static <V> TreeMatcher<V> of(
		final TreePattern pattern,
		final Tree<V, ?> tree,
		final BiPredicate<? super V, ? super String> equals
	) {
		return new TreeMatcher<>(pattern, tree, equals);
	}

}
