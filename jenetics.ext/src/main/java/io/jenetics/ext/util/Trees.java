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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package io.jenetics.ext.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.jenetics.internal.util.require;

import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
final class Trees {
	private Trees() {require.noInstance();}


	@SuppressWarnings("unchecked")
	static <V, T extends Tree<V, T>> T self(final Tree<?, ?> tree) {
		return (T)tree;
	}

	/**
	 * Builds the parents of node up to and including the root node, where the
	 * original node is the last element in the returned array. The length of
	 * the returned array gives the node's depth in the tree.
	 *
	 * @param node the node to get the path for
	 * @param depth  an int giving the number of steps already taken towards
	 *        the root (on recursive calls), used to size the returned array
	 * @return an array of nodes giving the path from the root to the specified
	 *         node
	 */
	static <V, T extends Tree<V, T>> MSeq<T> pathToRoot(
		final T node,
		final int depth
	) {
		final MSeq<T> path;
		if (node == null) {
			path = depth == 0 ? MSeq.empty() : MSeq.ofLength(depth);
		} else {
			path = pathToRoot(node.getParent().orElse(null), depth + 1);
			path.set(path.length() - depth - 1, node);
		}

		return path;
	}

	static String toString(final Tree<?, ?> tree) {
		return toString(tree, t -> Objects.toString(t.getValue()));
	}

	/**
	 * Return a string representation of the given tree.
	 *
	 * @param tree the input tree
	 * @return the string representation of the given tree
	 */
	static String toString(
		final Tree<?, ?> tree,
		final Function<? super Tree<?, ?>, ? extends CharSequence> toNodeString
	) {
		return tree != null
			? toStrings(tree, toNodeString).stream()
				.map(StringBuilder::toString)
				.collect(Collectors.joining("\n"))
			: "null";
	}

	private static
	List<StringBuilder> toStrings(
		final Tree<?, ?> tree,
		final Function<? super Tree<?, ?>, ? extends CharSequence> toNodeString
	) {
		final List<StringBuilder> result = new ArrayList<>();
		result.add(new StringBuilder().append(toNodeString.apply(tree)));

		final Iterator<? extends Tree<?, ?>> it = tree.childIterator();
		while (it.hasNext()) {
			final List<StringBuilder> subtree = toStrings(it.next(), toNodeString);
			if (it.hasNext()) {
				subtree(result, subtree, toNodeString);
			} else {
				lastSubtree(result, subtree);
			}
		}
		return result;
	}

	private static void subtree(
		final List<StringBuilder> result,
		final List<StringBuilder> subtree,
		final Function<? super Tree<?, ?>, ? extends CharSequence> toNodeString
	) {
		final Iterator<StringBuilder> it = subtree.iterator();
		result.add(it.next().insert(0, "├── "));
		while (it.hasNext()) {
			result.add(it.next().insert(0, "│   "));
		}
	}

	private static void lastSubtree(
		final List<StringBuilder> result,
		final List<StringBuilder> subtree
	) {
		final Iterator<StringBuilder> it = subtree.iterator();
		result.add(it.next().insert(0, "└── "));
		while (it.hasNext()) {
			result.add(it.next().insert(0, "    "));
		}
	}

	/**
	 * Return a compact string representation of the given tree. The tree
	 * <pre>
	 *  mul
	 *  ├── div
	 *  │   ├── cos
	 *  │   │   └── 1.0
	 *  │   └── cos
	 *  │       └── π
	 *  └── sin
	 *      └── mul
	 *          ├── 1.0
	 *          └── z
	 *  </pre>
	 * is printed as
	 * <pre>
	 *  mul(div(cos(1.0), cos(π)), sin(mul(1.0, z)))
	 * </pre>
	 *
	 * @param tree the input tree
	 * @return the string representation of the given tree
	 */
	public static String toCompactString(final Tree<?, ?> tree) {
		if (tree != null) {
			final StringBuilder out = new StringBuilder();
			toCompactString(out, tree);
			return out.toString();
		} else {
			return "null";
		}
	}

	private static void toCompactString(
		final StringBuilder out,
		final Tree<?, ?> tree
	) {
		out.append(tree.getValue());
		if (!tree.isLeaf()) {
			out.append("(");
			toCompactString(out, tree.getChild(0));
			for (int i = 1; i < tree.childCount(); ++i) {
				out.append(", ");
				toCompactString(out, tree.getChild(i));
			}
			out.append(")");
		}
	}

	static String toInfixString(final Tree<?, ?> tree) {
		final StringBuilder out = new StringBuilder();
		toInfixString(out, tree);
		return out.toString();
	}

	private static void toInfixString(final StringBuilder out, final Tree<?, ?> tree) {
		if (!tree.isLeaf()) {
			toInfixChild(out, tree.getChild(0));
			out.append(tree.getValue());
			toInfixChild(out, tree.getChild(1));
		} else {
			out.append(tree.getValue());
		}
	}

	private static void toInfixChild(final StringBuilder out, final Tree<?, ?> child) {
		if (child.isLeaf()) {
			toInfixString(out, child);
		} else {
			out.append("(");
			toInfixString(out, child);
			out.append(")");
		}
	}

	public static String toDottyString(final String name, final Tree<?, ?> tree) {
		final StringBuilder out = new StringBuilder();
		out.append("digraph ").append(name).append(" {\n");
		dotty(out, tree);
		labels(out, tree);
		out.append("}\n");
		return out.toString();
	}

	private static void dotty(final StringBuilder out, final Tree<?, ?> node) {
		final ISeq<? extends Tree<?, ?>> nodes = node.breadthFirstStream()
			.collect(ISeq.toISeq());

		for (int i = 0; i < nodes.length(); ++i) {
			final Tree<?, ?> n = nodes.get(i);
			n.childStream().forEach(child ->
				out
					.append("    ")
					.append(id(n))
					.append(" -> ")
					.append(id(child))
					.append(";\n")
			);
		}
	}

	private static String id(final Tree<?, ?> node) {
		return "node_" + Math.abs(System.identityHashCode(node));
	}

	private static void labels(final StringBuilder out, final Tree<?, ?> tree) {
		tree.depthFirstStream().forEach(node -> {
			out.append("    ");
			out.append(id(node));
			out.append(" [label=\"").append(node.getValue()).append("\"];\n");
		});
	}

	/**
	 * Checks if the two given trees has the same structure with the same values.
	 *
	 * @param a the first tree
	 * @param b the second tree
	 * @return {@code true} if the two given trees are structurally equals,
	 *         {@code false} otherwise
	 */
	static boolean equals(final Tree<?, ?> a, final Tree<?, ?> b) {
		boolean equals = a == b;
		if (!equals && a != null && b != null) {
			equals = a.childCount() == b.childCount();
			if (equals) {
				equals = Objects.equals(a.getValue(), b.getValue());
				if (equals && a.childCount() > 0) {
					equals = equals(a.childIterator(), b.childIterator());
				}
			}
		}

		return equals;
	}

	private static boolean equals(
		final Iterator<? extends Tree<?, ?>> a,
		final Iterator<? extends Tree<?, ?>> b
	) {
		boolean equals = true;
		while (a.hasNext() && equals) {
			equals = equals(a.next(), b.next());
		}

		return equals;
	}

}
