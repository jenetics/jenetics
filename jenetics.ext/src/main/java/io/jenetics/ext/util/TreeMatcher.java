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

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.ext.util.Tree.Path;
import io.jenetics.ext.util.TreeRewriter.Matcher;
import io.jenetics.ext.util.Tree_Pattern.Node;
import io.jenetics.ext.util.Tree_Pattern.Var;

/**
 * Implementation of a pattern based tree matcher.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class TreeMatcher<V> implements Matcher<V> {

	private final TreePattern _pattern;
	private final Tree<V, ?> _tree;

	TreeMatcher(final TreePattern pattern, final Tree<V, ?> tree) {
		_pattern = requireNonNull(pattern);
		_tree = requireNonNull(tree);
	}

	TreePattern pattern() {
		return _pattern;
	}

	boolean matches() {
		return results().findFirst().isPresent();
	}

	Stream<Tree<V, ?>> results() {
		@SuppressWarnings("unchecked")
		final Stream<Tree<V, ?>> ts = (Stream<Tree<V, ?>>)_tree.stream();
		return  ts.filter(_pattern::matches);
	}

	static final class Result {
		private final Tree<?, ?> _match;

		private Result(final Tree<?, ?> match) {
			_match = requireNonNull(match);
		}

		Tree<?, ?> match() {
			return _match;
		}

		@Override
		public String toString() {
			return format("M[%s]", _match.toParenthesesString());
		}
	}

	@Override
	public boolean matches(Tree<V, ?> node) {
		return false;
	}
	//	private final Matcher<V> _subTreeMatcher;
//	private final Matcher<V> _valueMatcher;
//
//	private TreeMatcher(
//		final Matcher<V> subTreeMatcher,
//		final Matcher<V> valueMatcher
//	) {
//		_subTreeMatcher = requireNonNull(subTreeMatcher);
//		_valueMatcher = requireNonNull(valueMatcher);
//	}
//
//	@Override
//	public boolean matches(final Tree<V, ?> node) {
//		return _valueMatcher.matches(node) && _subTreeMatcher.matches(node);
//	}
//
//	static List<Tree<?, ?>>
//	children(final Tree<?, ?> tree, final List<Path> paths) {
//		return paths.stream()
//			.map(tree::childAtPath)
//			.flatMap(n -> n.map(Stream::of).orElse(Stream.empty()))
//			.collect(Collectors.toList());
//	}
//
//	/**
//	 * add(X, sub(X, X)) -> x
//	 *
//	 * @param pattern the pattern
//	 * @return the matcher
//	 */
//	static <V> TreeMatcher<V> of(
//		final String pattern,
//		final Function<? super String, ? extends V> mapper
//	) {
//		final TreeNode<String> tree = TreeNode.parse(pattern);
//
//		return new TreeMatcher<>(
//			SubTreeMatcher.of(tree),
//			TreeValueMatcher.of(tree, mapper)
//		);
//	}

}
