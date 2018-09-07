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
import java.util.stream.Collectors;

import io.jenetics.ext.util.Tree.Path;

/**
 * add(X,0) -> X
 * mul(X,0) -> 0
 * sin(neg(X)) -> neg(sin(X))
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class NodeRewriter<V> {

	private final Map<Character, Path> _replace;

	private NodeRewriter(final Map<Character, Path> replace) {
		_replace = requireNonNull(replace);
	}

	public void rewrite(final TreeNode<V> node) {
		final Map<Character, TreeNode<V>> nodes = _replace.entrySet().stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				e -> node.childAtPath(e.getValue())
					.orElseThrow(AssertionError::new)));
	}

}
