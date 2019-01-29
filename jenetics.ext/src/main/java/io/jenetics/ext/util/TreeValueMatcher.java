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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.jenetics.ext.util.Tree.Path;
import io.jenetics.ext.util.TreeRewriter.Matcher;

/**
 * Check if a given tree matches specific tree values.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class TreeValueMatcher<V> implements Matcher<V> {

	private final Map<Path, V> _values;

	private TreeValueMatcher(final Map<Path, V> values) {
		_values = values;
	}

	@Override
	public boolean matches(final Tree<V, ?> tree) {
		final List<Path> paths = new ArrayList<>(_values.keySet());
		final List<Tree<?, ?>> nodes = Collections.emptyList();//children(tree, paths);

		boolean matches = nodes.size() == paths.size();
		final Iterator<Tree<?, ?>> tit = nodes.iterator();
		final Iterator<Path> pit = paths.iterator();

		while (matches && tit.hasNext()) {
			final Tree<?, ?> tn = tit.next();
			final Path path = pit.next();

			matches = Objects.equals(_values.get(path), tn.getValue());
		}

		return matches;
	}

	static <V> TreeValueMatcher<V> of(
		final Tree<String, ?> pattern,
		final Function<? super String, ? extends V> mapper
	) {
		final Map<Path, V> values = pattern.stream()
			.filter(TreeValueMatcher::nonVariable)
			.collect(Collectors.toMap(
				n -> n.childPath(),
				n -> mapper.apply(n.getValue())));

		return new TreeValueMatcher<>(values);
	}

	private static boolean nonVariable(final Tree<String, ?> node) {
		return !SubTreeMatcher.isVariable(node);
	}

}
