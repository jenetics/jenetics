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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Represents a tree rewrite rule. A rewrite rule consists of a pattern, which
 * must be matched, and a template, which is expanded and replaces the variables
 * in the tree pattern.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class TreeRewriteRule {

	private final TreePattern _pattern;
	private final TreePattern _template;

	private TreeRewriteRule(
		final TreePattern pattern,
		final TreePattern template
	) {
		_pattern = requireNonNull(pattern);
		_template = requireNonNull(template);
	}

	/**
	 * Return the rule matching pattern.
	 *
	 * @return the rule matching pattern
	 */
	public TreePattern pattern() {
		return _pattern;
	}

	/**
	 * Return the replacement pattern of the rule.
	 *
	 * @return the replacement pattern of the rule
	 */
	public TreePattern template() {
		return _template;
	}

	@Override
	public String toString() {
		return format("Rule[%s -> %s]", _pattern, _template);
	}

	/**
	 * Create a new rewrite rule with the given values.
	 *
	 * @param patter the rule pattern
	 * @param template the rule replace pattern
	 * @return a new rewrite rule
	 */
	public static TreeRewriteRule of(
		final TreePattern patter,
		final TreePattern template
	) {
		return new TreeRewriteRule(patter, template);
	}

	/**
	 * Compiles the string representation of a rewrite rule:
	 * <pre>{@code
	 * add(<x>,0) -> <x>
	 * mul(<x>,1) -> <x>
	 * }</pre>
	 *
	 * @param rule the rewrite rule
	 * @return a new rewrite rule, compiled from the given rule string
	 * @throws IllegalArgumentException if the rewrite rule is invalid
	 * @throws NullPointerException if the given {@code rule} string is
	 *         {@code null}
	 */
	public static TreeRewriteRule compile(final String rule) {
		final String[] parts = rule.split("->");
		if (parts.length != 2) {
			throw new IllegalArgumentException(format(
				"Invalid rewrite rule: %s", rule
			));
		}

		return of(
			TreePattern.compile(parts[0].trim()),
			TreePattern.compile(parts[1].trim())
		);
	}

}
