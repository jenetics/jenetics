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

import static io.jenetics.internal.util.Hashes.hash;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.util.ISeq;

import io.jenetics.ext.util.Tree.Path;
import io.jenetics.ext.util.Tree_Pattern.Node;
import io.jenetics.ext.util.Tree_Pattern.Val;
import io.jenetics.ext.util.Tree_Pattern.Var;

/**
 * <pre>{@code
 * <x:expr> + 0 -> <x>
 * <x:expr> * 1 -> <x>
 * <x:expr> * 0 -> 0
 *
 * add(<x>,0) -> <x>
 * mul(<x>,1) -> <x>
 * add(<x>,0,<y>) -> add(<x>,<y>)
 *
 *
 * }</pre>
 *
 * add(X,0) -> X
 * mul(X,0) -> 0
 * sin(neg(X)) -> neg(sin(X))
 *
 * This class is responsible for rewriting a single node with the given set
 * of paths to be replaced.
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

	public void rewrite(final TreeNode<V> root) {
		// The tree paths replaced by the actual nodes for the given root.
		final Map<Character, TreeNode<V>> nodes = _replace.entrySet().stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				e -> root.childAtPath(e.getValue())
					.orElseThrow(AssertionError::new)));
	}

}

/**
 * A compiled representation of a <em>tree</em> pattern. A tree pattern,
 * specified as a string, must first be compiled into an instance of this class.
 * The resulting pattern can then be used to create a {@code TreeMatcher} object
 * that can match arbitrary trees against the tree pattern. All of the state
 * involved in performing a match resides in the matcher, so many matchers can
 * share the same pattern.
 * <p>
 * The string representation of a tree pattern is a parenthesis tree string,
 * with a special wildcard syntax for arbitrary sub-trees:
 * <pre>{@code
 *     add(<x>,0)
 *     mul(1,<y>)
 * }</pre>
 * The identifier of such sub-trees are put into angle brackets.
 */
final class Tree_Pattern {

	static abstract class Node {
		private final String _value;

		Node(final String value) {
			_value = value;
		}

		String value() {
			return _value;
		}

		@Override
		public int hashCode() {
			return hash(getClass(), hash(_value));
		}

		@Override
		public boolean equals(final Object obj) {
			return obj != null &&
				getClass() == obj.getClass() &&
				Objects.equals(_value, ((Node)obj)._value);
		}

		@Override
		public String toString() {
			return Objects.toString(_value);
		}

		static Val val(final String value) {
			return new Val(value);
		}

		static Var var(final String name) {
			return new Var(name);
		}
	}

	static final class Val extends Node {
		private Val(final String value) {
			super(value);
		}
	}

	static final class Var extends Node {
		private Var(final String value) {
			super(value);
		}

		@Override
		public String toString() {
			return format("<%s>", value());
		}
	}





	private final Tree<Node, ?> _tree;
	private final Map<Path, Node> _nodes;
	private final Map<Path, Var> _vars;
	private final Map<Path, Val> _vals;

	private Tree_Pattern(
		final Tree<Node, ?> tree,
		final Map<Path, Node> nodes,
		final Map<Path, Var> vars,
		final Map<Path, Val> vals
	) {
		_tree = requireNonNull(tree);
		_nodes = requireNonNull(nodes);
		_vars = requireNonNull(vars);
		_vals = requireNonNull(vals);
	}

	Tree_Matcher matcher(final Tree<?, ?> tree) {
		return new Tree_Matcher(_tree, tree);
	}

	Tree<Node, ?> tree() {
		return _tree;
	}

	Map<Path, Node> nodes() {
		return _nodes;
	}

	Map<Path, Var> vars() {
		return _vars;
	}

	Map<Path, Val> vals() {
		return _vals;
	}

	static Tree_Pattern compile(final String expr) {
		final TreeNode<Node> tree = TreeNode.parse(expr, Tree_Pattern::toNode);
		final Map<Path, Node> nodes = tree.stream()
			.collect(Collectors.toMap(Tree::childPath, TreeNode::getValue));

		final Map<Path, Var> vars = nodes.entrySet().stream()
			.filter(n -> n.getValue() instanceof Var)
			.collect(Collectors.toMap(Map.Entry::getKey, n -> (Var)n.getValue()));

		final Map<Path, Val> vals = nodes.entrySet().stream()
			.filter(n -> n.getValue() instanceof Val)
			.collect(Collectors.toMap(Map.Entry::getKey, n -> (Val)n.getValue()));

		return new Tree_Pattern(tree, nodes, vars, vals);
	}

	private static Node toNode(final String value) {
		return value.startsWith("<") && value.endsWith(">")
			? Node.var(value.substring(1, value.length() - 1))
			: Node.val(value);
	}

}

final class Tree_Matcher {

	private final Tree<Node, ?> _pattern;
	private final Tree<?, ?> _tree;

	Tree_Matcher(final Tree<Node, ?> pattern, final Tree<?, ?> tree) {
		_pattern = requireNonNull(pattern);
		_tree = requireNonNull(tree);
	}

	Tree<Node, ?> pattern() {
		return _pattern;
	}

	boolean matches() {
		return results().findFirst().isPresent();
	}

	Stream<Tree_MatchResult> results() {
		return _tree.stream()
			.filter(node -> matches(node, _pattern))
			.map(node -> Tree_MatchResult.of(node, _pattern));
	}

	static boolean matches(final Tree<?, ?> node, final Tree<Node, ?> pattern) {
		if (pattern.getValue() instanceof Var) {
			System.out.println(node.toParenthesesString() + "::" + pattern.toParenthesesString());
			return true;
		} else {
			if (pattern.getValue().value().equals(node.getValue().toString()) || pattern.getValue() instanceof Var) {
				if (node.childCount() == pattern.childCount()) {
					for (int i = 0; i < node.childCount(); ++i) {
						if (!matches(node.getChild(i), pattern.getChild(i))) {
							return false;
						}
					}
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

}

final class Tree_MatchResult {

	private final Tree<?, ?> _tree;
	private final Path _root;
	private final Map<Path, String> _vars;

	private Tree_MatchResult(
		final Tree<?, ?> tree,
		final Path root,
		final Map<Path, String> vars
	) {
		_tree = requireNonNull(tree);
		_root = requireNonNull(root);
		_vars = requireNonNull(vars);
	}

	Tree<?, ?> match() {
		return _tree.childAtPath(_root)
			.orElseThrow(AssertionError::new);
	}

	Path root() {
		return _root;
	}

	Map<Path, String> vars() {
		return _vars;
	}

	static Tree_MatchResult of(final Tree<?, ?> node, final Tree<Node, ?> pattern) {
		final Tree<?, ?> tree = node.getRoot();
		final Path root = node.childPath();

		final Map<Path, Node> nodes = pattern.stream()
			.collect(Collectors.toMap(Tree::childPath, Tree::getValue));

		final Map<Path, String> vars = nodes.entrySet().stream()
			.filter(n -> n.getValue() instanceof Var)
			.collect(Collectors.toMap(e -> root.append(e.getKey()), n -> n.getValue().value()));

		return new Tree_MatchResult(tree, root, vars);
	}

	@Override
	public String toString() {
		return format("M[%s]", match().toParenthesesString());
	}
}
