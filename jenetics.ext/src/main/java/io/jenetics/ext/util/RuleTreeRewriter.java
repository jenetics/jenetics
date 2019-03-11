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

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;

import io.jenetics.ext.util.Tree.Path;
import io.jenetics.ext.trs.TreeRewriter;

/**
 * Rewriter implementation, which applies a single rule to a given tree until
 * it no longer matches.
 *
 * @param <V> the tree value type of the rewriter
 *
 * @see TreeRewriteRule
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.4
 * @since 4.4
 */
public final class RuleTreeRewriter<V> implements TreeRewriter<V> {

	private final TreeRewriteRule _rule;
	private final BiPredicate<? super V, ? super String> _equals;
	private final Function<? super String, ? extends V> _mapper;

	/**
	 * Create a new rule based tree rewriter with the given parameters.
	 *
	 * @param rule the rewriter rule applied for this rewriter
	 * @param equals the equals predicate for matching the tree values
	 * @param mapper the tree value mapper function
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public RuleTreeRewriter(
		final TreeRewriteRule rule,
		final BiPredicate<? super V, ? super String> equals,
		final Function<? super String, ? extends V> mapper
	) {
		_rule = requireNonNull(rule);
		_equals = requireNonNull(equals);
		_mapper = requireNonNull(mapper);
	}

	/**
	 * Return the rewriter rule applied for this rewriter.
	 *
	 * @return the rewriter rule
	 */
	public TreeRewriteRule rules() {
		return _rule;
	}

	/**
	 * Return the equals predicate used for matching the tree values.
	 *
	 * @return the equals predicate for the tree values
	 */
	public BiPredicate<? super V, ? super String> equals() {
		return _equals;
	}

	/**
	 * Return the tree value mapper function.
	 *
	 * @return the tree value mapper function
	 */
	public Function<? super String, ? extends V> mapper() {
		return _mapper;
	}

	@Override
	public boolean rewrite(final TreeNode<V> tree) {
		requireNonNull(tree);

		boolean rewritten = false;
		Optional<TreeMatchResult<V>> result;
		do {
			result = _rule.match().matcher(tree, _equals).results()
				.findFirst();

			result.ifPresent(res -> rewrite(res, tree));
			rewritten = result.isPresent() || rewritten;
		} while(result.isPresent());

		return rewritten;
	}

	private void rewrite(
		final TreeMatchResult<V> result,
		final TreeNode<V> tree
	) {
		final Map<String, Tree<V, ?>> vars = result.variables();
		final TreePattern substitution = _rule.substitution();

		final TreeNode<V> r = substitution.expand(vars, _mapper);

		final Path path = result.node().childPath();
		tree.replaceAtPath(path, r);
	}

	/**
	 * Create a new rule based tree rewriter with the given parameters.
	 *
	 * @see TreeRewriteRule#compile(String)
	 *
	 * @param rule the rewriter rule applied for this rewriter
	 * @param equals the equals predicate for matching the tree values
	 * @param mapper the tree value mapper function
	 * @throws IllegalArgumentException if the rewrite {@code rule} is invalid
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public static <V> RuleTreeRewriter<V> compile(
		final String rule,
		final BiPredicate<? super V, ? super String> equals,
		final Function<? super String, ? extends V> mapper
	) {
		return new RuleTreeRewriter<>(
			TreeRewriteRule.compile(rule),
			equals,
			mapper
		);
	}

	/**
	 * Create a new rule based tree rewriter with the given parameters.
	 *
	 * @see TreeRewriteRule#compile(String)
	 *
	 * @param rule the rewriter rule applied for this rewriter
	 * @throws IllegalArgumentException if the rewrite {@code rule} is invalid
	 * @throws NullPointerException if the given {@code rule} is {@code null}
	 */
	public static RuleTreeRewriter<String> compile(final String rule) {
		return new RuleTreeRewriter<>(
			TreeRewriteRule.compile(rule),
			Objects::equals,
			Function.identity()
		);
	}

}
