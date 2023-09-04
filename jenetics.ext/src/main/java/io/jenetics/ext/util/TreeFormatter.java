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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Definition of different tree formatter strategies.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
public abstract class TreeFormatter {

	/**
	 * Formats a given tree to a <em>tree</em> string representation.
	 * <pre>
	 *     mul
	 *     ├── div
	 *     │   ├── cos
	 *     │   │   └── 1.0
	 *     │   └── cos
	 *     │       └── π
	 *     └── sin
	 *         └── mul
	 *             ├── 1.0
	 *             └── z
	 *  </pre>
	 */
	public static final TreeFormatter TREE = new TreeFormatter() {

		@Override
		public <V> String format(
			final Tree<V, ?> tree,
			final Function<? super V, ? extends CharSequence> mapper
		) {
			requireNonNull(tree);
			requireNonNull(mapper);

			return toStrings(tree, mapper).stream()
				.map(StringBuilder::toString)
				.collect(Collectors.joining("\n"));
		}

		private <V> List<StringBuilder> toStrings(
			final Tree<V, ?> tree,
			final Function<? super V, ? extends CharSequence> mapper
		) {
			final List<StringBuilder> result = new ArrayList<>();
			result.add(new StringBuilder().append(mapper.apply(tree.value())));

			final Iterator<? extends Tree<V, ?>> it = tree.childIterator();
			while (it.hasNext()) {
				final List<StringBuilder> subtree = toStrings(it.next(), mapper);
				if (it.hasNext()) {
					subtree(result, subtree);
				} else {
					lastSubtree(result, subtree);
				}
			}
			return result;
		}

		private <V> void subtree(
			final List<StringBuilder> result,
			final List<StringBuilder> subtree
		) {
			final Iterator<StringBuilder> it = subtree.iterator();
			result.add(it.next().insert(0, "├── "));
			while (it.hasNext()) {
				result.add(it.next().insert(0, "│   "));
			}
		}

		private void lastSubtree(
			final List<StringBuilder> result,
			final List<StringBuilder> subtree
		) {
			final Iterator<StringBuilder> it = subtree.iterator();
			result.add(it.next().insert(0, "└── "));
			while (it.hasNext()) {
				result.add(it.next().insert(0, "    "));
			}
		}
	};

	/**
	 * Formats a given tree to a parentheses string representation.
	 * <pre>
	 *     mul(div(cos(1.0),cos(π)),sin(mul(1.0,z)))
	 * </pre>
	 */
	public static final TreeFormatter PARENTHESES = new TreeFormatter() {
		@Override
		public <V> String format(
			final Tree<V, ?> tree,
			final Function<? super V, ? extends CharSequence> mapper
		) {
			requireNonNull(tree);
			requireNonNull(mapper);
			return ParenthesesTrees.toString(tree, mapper);
		}
	};

	/**
	 * Formats a given tree to a lisp string representation.
	 * <pre>
	 *     (mul (div (cos 1.0) (cos π)) (sin (mul 1.0 z)))
	 * </pre>
	 */
	public static final TreeFormatter LISP = new TreeFormatter() {
		@Override
		public <V> String format(
			final Tree<V, ?> tree,
			final Function<? super V, ? extends CharSequence> mapper
		) {
			final CharSequence value = mapper.apply(tree.value());
			if (tree.isLeaf()) {
				return value.toString();
			} else {
				final String children = tree.childStream()
					.map(child -> format(child, mapper))
					.collect(Collectors.joining(" "));
				return "(" + value + " " + children + ")";
			}
		}
	};

	/**
	 * A tree formatter for .dot string representations. This strings can be
	 * used to create nice looking tree images. The tree
	 * <pre>
	 *     mul(div(cos(1.0),cos(π)),sin(mul(1.0,z)))
	 * </pre>
	 * is rendered into this dot string
	 * <pre>
	 * digraph Tree {
	 *     node_001 [label="div"];
	 *     node_002 [label="cos"];
	 *     node_003 [label="1.0"];
	 *     node_004 [label="cos"];
	 *     node_000 [label="mul"];
	 *     node_009 [label="z"];
	 *     node_005 [label="π"];
	 *     node_006 [label="sin"];
	 *     node_007 [label="mul"];
	 *     node_008 [label="1.0"];
	 *     node_000 -&gt; node_001;
	 *     node_001 -&gt; node_002;
	 *     node_002 -&gt; node_003;
	 *     node_001 -&gt; node_004;
	 *     node_004 -&gt; node_005;
	 *     node_000 -&gt; node_006;
	 *     node_006 -&gt; node_007;
	 *     node_007 -&gt; node_008;
	 *     node_007 -&gt; node_009;
	 * }
	 * </pre>
	 * This dot string can be rendered into the following graph:
	 * <p>
	 * <img alt="Dot-tree" src="doc-files/dot-tree.svg" width="400" height="252" >
	 * </p>
	 */
	public static final TreeFormatter DOT = dot("Tree");

	protected TreeFormatter() {
	}

	/**
	 * Formats the given {@code tree} to its string representation. The given
	 * {@code mapper} is used for converting the node type {@code V} to a string
	 * value.
	 *
	 * @param tree the input tree to format
	 * @param mapper the tree node value mapper
	 * @param <V> the tree node type
	 * @return the string representation of the given {@code tree}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public abstract  <V> String format(
		final Tree<V, ?> tree,
		final Function<? super V, ? extends CharSequence> mapper
	);

	/**
	 * Formats the given {@code tree} to its string representation.
	 *
	 * @param tree the input tree to format
	 * @return the string representation of the given {@code tree}
	 * @throws NullPointerException if the {@code tree} is {@code null}
	 */
	public String format(final Tree<?, ?> tree) {
		return format(tree, Objects::toString);
	}

	/**
	 * A tree formatter for .dot string representations. This strings can be
	 * used to create nice looking tree images. The tree
	 * <pre>
	 *     mul(div(cos(1.0),cos(π)),sin(mul(1.0,z)))
	 * </pre>
	 * is rendered into this dot string
	 * <pre>
	 * digraph Tree {
	 *     node_001 [label="div"];
	 *     node_002 [label="cos"];
	 *     node_003 [label="1.0"];
	 *     node_004 [label="cos"];
	 *     node_000 [label="mul"];
	 *     node_009 [label="z"];
	 *     node_005 [label="π"];
	 *     node_006 [label="sin"];
	 *     node_007 [label="mul"];
	 *     node_008 [label="1.0"];
	 *     node_000 -&gt; node_001;
	 *     node_001 -&gt; node_002;
	 *     node_002 -&gt; node_003;
	 *     node_001 -&gt; node_004;
	 *     node_004 -&gt; node_005;
	 *     node_000 -&gt; node_006;
	 *     node_006 -&gt; node_007;
	 *     node_007 -&gt; node_008;
	 *     node_007 -&gt; node_009;
	 * }
	 * </pre>
	 * This dot string can be rendered into the following graph:
	 * <p>
	 * <img alt="Dot-tree" src="doc-files/dot-tree.svg" width="400" height="252" >
	 * </p>
	 *
	 * @param treeName the name of the digraph
	 * @return a dot string formatter
	 * @throws NullPointerException if the given tree name is {@code null}
	 */
	public static TreeFormatter dot(final String treeName) {
		return new Dotty(treeName);
	}


	/* *************************************************************************
	 * Some helper classes.
	 * ************************************************************************/

	/**
	 * This formatter converts a tree to the .dot representation.
	 */
	private static final class Dotty extends TreeFormatter {
		private final String _name;

		Dotty(final String name) {
			_name = requireNonNull(name);
		}

		@Override
		public <V> String format(
			final Tree<V, ?> tree,
			final Function<? super V, ? extends CharSequence> mapper
		) {
			return new Helper<>(_name, tree, mapper).draw();
		}

		private static final class Helper<V> {
			private final String _name;
			private final Function<? super V, ? extends CharSequence> _mapper;

			private final Map<String, CharSequence> _labels = new HashMap<>();
			private final List<String> _edges = new ArrayList<>();

			Helper(
				final String name,
				final Tree<V, ?> tree,
				final Function<? super V, ? extends CharSequence> mapper
			) {
				_name = requireNonNull(name);
				_mapper = requireNonNull(mapper);
				init(tree, null, 0);
			}

			private int init(
				final Tree<V, ?> tree,
				final String parentLabel,
				final int index
			) {
				int idx = index;
				final CharSequence value = _mapper.apply(tree.value());
				final String label = String.format("node_%03d", idx);
				_labels.put(label, value);

				if (parentLabel != null) {
					_edges.add(parentLabel + " -> " + label);
				}
				for (int i = 0; i < tree.childCount(); ++i) {
					final Tree<V, ?> child = tree.childAt(i);
					idx = init(child, label, idx + 1);
				}
				return idx;
			}

			String draw() {
				final StringBuilder builder = new StringBuilder();
				builder
					.append("digraph ")
					.append(_name)
					.append(" {\n");

				_labels.forEach((key, value) ->
					builder
						.append("    ")
						.append(key)
						.append(" [label=\"")
						.append(value.toString().replace("\"", "\\\""))
						.append("\"];\n")
				);

				_edges.forEach(edge ->
					builder
						.append("    ")
						.append(edge)
						.append(";\n")
				);
				builder.append("}\n");

				return builder.toString();
			}
		}
	}

}
