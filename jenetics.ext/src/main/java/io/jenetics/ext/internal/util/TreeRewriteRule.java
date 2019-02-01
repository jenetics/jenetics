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
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class TreeRewriteRule {

	private final TreePattern _pattern;
	private final TreePattern _template;

	private TreeRewriteRule(
		final TreePattern pattern,
		final TreePattern template
	) {
		_pattern = requireNonNull(pattern);
		_template = requireNonNull(template);
	}

	TreePattern pattern() {
		return _pattern;
	}

	TreePattern template() {
		return _template;
	}

	static TreeRewriteRule of(
		final TreePattern patter,
		final TreePattern template
	) {
		return new TreeRewriteRule(patter, template);
	}

	static TreeRewriteRule compile(final String rule) {
		final String[] parts = rule.split("->");
		if (parts.length != 2) {
			throw new IllegalArgumentException(format(
				"Invalid rewrite rule: %s", rule
			));
		}

		return of(TreePattern.compile(parts[0]), TreePattern.compile(parts[1]));
	}

}
