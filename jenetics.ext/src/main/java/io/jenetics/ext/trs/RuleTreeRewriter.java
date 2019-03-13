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
package io.jenetics.ext.trs;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import io.jenetics.ext.trs.TreePattern.Var;
import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.Tree.Path;
import io.jenetics.ext.util.TreeNode;

/**
 * Rewriter implementation, which applies a single rule to a given tree until
 * it no longer matches.
 *
 * @param <V> the tree value type of the rewriter
 *
 * @see TreeRewriteRule
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class RuleTreeRewriter<V> implements TreeRewriter<V> {

	private final TreeRewriteRule<V> _rule;

	/**
	 * Create a new rule based tree rewriter with the given parameters.
	 *
	 * @param rule the rewriter rule applied for this rewriter
	 * @throws NullPointerException if one of the given arguments is
	 *         {@code null}
	 */
	public RuleTreeRewriter(final TreeRewriteRule<V> rule) {
		_rule = requireNonNull(rule);
	}

	/**
	 * Return the rewriter rule applied for this rewriter.
	 *
	 * @return the rewriter rule
	 */
	public TreeRewriteRule<V> rule() {
		return _rule;
	}

	/**
	 * Maps {@code this} rewriter from type {@code V} to type {@code B}.
	 *
	 * @param mapper the type mapper
	 * @param <B> the target type
	 * @return a new rewriter for the mapped type
	 */
	public <B> RuleTreeRewriter<B>
	map(final Function<? super V, ? extends B> mapper) {
		return new RuleTreeRewriter<>(_rule.map(mapper));
	}

	@Override
	public boolean rewrite(final TreeNode<V> tree) {
		requireNonNull(tree);

		boolean rewritten = false;
		Optional<TreeMatchResult<V>> result;
		do {
			result = _rule.match().matcher(tree).results()
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
		final Map<Var<V>, Tree<V, ?>> vars = result.vars();
		final TreePattern<V> substitution = _rule.replacement();

		final TreeNode<V> r = substitution.expand(vars);

		final Path path = result.tree().childPath();
		tree.replaceAtPath(path, r);
	}

	/**
	 * Create a new rule based tree rewriter with the given parameters.
	 *
	 * @see TreeRewriteRule#compile(String)
	 *
	 * @param rule the rewriter rule applied for this rewriter
	 * @param mapper the tree value mapper function
	 * @throws IllegalArgumentException if the rewrite {@code rule} is invalid
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public static <V> RuleTreeRewriter<V> compile(
		final String rule,
		final Function<? super String, ? extends V> mapper
	) {
		return new RuleTreeRewriter<>(
			TreeRewriteRule.compile(rule).map(mapper)
		);
	}

	/**
	 * Create a new rule based tree rewriter with the given parameters.
	 *
	 * @see TreeRewriteRule#compile(String, Function)
	 *
	 * @param rule the rewriter rule applied for this rewriter
	 * @throws IllegalArgumentException if the rewrite {@code rule} is invalid
	 * @throws NullPointerException if the given {@code rule} is {@code null}
	 */
	public static RuleTreeRewriter<String> compile(final String rule) {
		return compile(rule, Function.identity());
	}

}
