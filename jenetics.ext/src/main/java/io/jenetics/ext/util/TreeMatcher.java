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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.ext.util.TreeRewriter.Matcher;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
class TreeMatcher implements Matcher<Object> {
	final Map<ChildPath, Object> _values;
	final List<ChildPath> _equals;

	private TreeMatcher(
		final Map<ChildPath, Object> values,
		final List<ChildPath> equals
	) {
		_values = requireNonNull(values);
		_equals = requireNonNull(equals);
	}

	@Override
	public boolean matches(final TreeNode<Object> node) {
		return value(node) && equals(node, _equals);
	}

	private boolean value(final Tree<?, ?> tree) {
		final List<ChildPath> paths = new ArrayList<>(_values.keySet());
		final List<Tree<?, ?>> nodes = children(tree, paths);

		boolean matches = nodes.size() == paths.size();
		final Iterator<Tree<?, ?>> tit = nodes.iterator();
		final Iterator<ChildPath> pit = paths.iterator();

		while (matches && tit.hasNext()) {
			final Tree<?, ?> tn = tit.next();
			final ChildPath path = pit.next();

			matches = Objects.equals(_values.get(path), tn.getValue());
		}

		return matches;
	}

	private static boolean
	equals(final Tree<?, ?> tree, final List<ChildPath> paths) {
		final List<Tree<?, ?>> nodes = children(tree, paths);

		boolean matches = nodes.size() == paths.size();
		final Iterator<Tree<?, ?>> it = nodes.iterator();
		if (it.hasNext()) {
			final Tree<?, ?> tn = it.next();

			while (matches && it.hasNext()) {
				matches = Objects.equals(tn, it.next());
			}
		}

		return matches;
	}

	private static List<Tree<?, ?>>
	children(final Tree<?, ?> tree, final List<ChildPath> paths) {
		return paths.stream()
			.map(p -> tree.childAtPath(p.path()))
			.flatMap(n -> n.map(Stream::of).orElse(Stream.empty()))
			.collect(Collectors.toList());
	}

	static TreeMatcher of(final String pattern) {
		final TreeNode<String> tree = TreeNode.parse(pattern);

		final List<ChildPath> equals = tree.stream()
			.filter(TreeNode::isLeaf)
			.map(n -> ChildPath.of(n.childPath()))
			.collect(Collectors.toList());


		return null;
	}

}
