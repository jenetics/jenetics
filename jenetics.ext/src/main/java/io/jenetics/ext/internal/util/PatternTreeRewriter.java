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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;

import io.jenetics.util.ISeq;

import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class PatternTreeRewriter<V> implements TreeRewriter<V> {

	private final ISeq<TreeRewriteRule> _rules;
	private final BiPredicate<? super V, ? super String> _equals;
	private final Function<? super String, ? extends V> _mapper;

	public PatternTreeRewriter(
		final ISeq<TreeRewriteRule> rule,
		final BiPredicate<? super V, ? super String> equals,
		Function<? super String, ? extends V> mapper
	) {
		_rules = requireNonNull(rule);
		_equals = requireNonNull(equals);
		_mapper = requireNonNull(mapper);
	}

	public ISeq<TreeRewriteRule> rules() {
		return _rules;
	}

	public BiPredicate<? super V, ? super String> equals() {
		return _equals;
	}

	public Function<? super String, ? extends V> mapper() {
		return _mapper;
	}

	@Override
	public boolean rewrite(final TreeNode<V> tree) {
		requireNonNull(tree);

		boolean rewritten = false;
		Optional<Match<V>> match;
		do {
			match = _rules.stream()
				.flatMap(rule -> rule.pattern().matcher(tree, _equals).results()
					.map(result -> new Match<>(rule, result)))
				.findFirst();

			match.ifPresent(m -> rewrite(tree, m));
			rewritten = match.isPresent() || rewritten;
		} while(match.isPresent());

		return rewritten;
	}

	private void rewrite(
		final TreeNode<V> tree,
		final Match<V> match
	) {
		final Map<String, Tree<V, ?>> vars = match.result.variables();
		final TreePattern template = match.rule.template();

		template.expand(vars, _mapper);
	}

	public static PatternTreeRewriter<String> of(final ISeq<TreeRewriteRule> rules) {
		return new PatternTreeRewriter<>(rules, Objects::equals, Function.identity());
	}

	public static <V> PatternTreeRewriter<V> compile(
		final BiPredicate<? super V, ? super String> equals,
		final Function<? super String, ? extends V> mapper,
		final String... rules
	) {
		return new PatternTreeRewriter<>(
			ISeq.of(rules).map(TreeRewriteRule::compile),
			equals,
			mapper
		);
	}


	private static final class Match<V> {
		final TreeRewriteRule rule;
		final TreeMatchResult<V> result;
		Match(final TreeRewriteRule rule, final TreeMatchResult<V> result) {
			this.rule = rule;
			this.result = result;
		}
	}

}
