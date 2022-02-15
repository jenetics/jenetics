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
package io.jenetics.incubator.property;

import java.util.Optional;
import java.util.regex.Pattern;

import io.jenetics.incubator.property.Property.Path;

/**
 * Represents a pattern, which can match a {@link Path}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
record PathNamePattern(String name, String index) {

	private static final String NAME_PATTERN =
		"((?:\\b[_a-zA-Z\\*]|\\*|\\B\\$)[_$a-zA-Z0-9\\*]*+)";

	private static final String INDEX_PATTERN =
		"((\\[([0-9*]*)\\])?)";

	private static final Pattern PATH_NAME_PATTERN = Pattern.compile(
		NAME_PATTERN + INDEX_PATTERN
	);


	boolean matches(final Path path) {
		return false;
	}

	static Optional<PathNamePattern> compile(final String pattern) {
		final var matcher = PATH_NAME_PATTERN.matcher(pattern);

		if (matcher.matches()) {
			final var name = matcher.group(1);
			final var index = matcher.group(3);

			final var pnp = new PathNamePattern(
				name,
				index != null
					? index.substring(1, index.length() - 1)
					: null
			);
			return Optional.of(pnp);
		} else {
			return Optional.empty();
		}
	}

}
