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
package io.jenetics.ext.trs;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.Tree.Path;
import io.jenetics.ext.util.TreeNode;

/**
 * A compiled representation of a <em>tree</em> pattern. A tree pattern,
 * specified as a parentheses string, must first be compiled into an instance of
 * this class. The resulting pattern can then be used to create a
 * {@code TreeMatcher} object that can match arbitrary trees against the tree
 * pattern. All of the state involved in performing a match resides in the
 * matcher, so many matchers can share the same pattern.
 * <p>
 * The string representation of a tree pattern is a parenthesis tree string,
 * with a special wildcard syntax for arbitrary sub-trees. The sub-trees
 * variables are put into angle brackets:
 * <pre>{@code
 *     add(<x>,0)
 *     mul(1,<y>)
 * }</pre>
 *
 * @see TreeRewriteRule
 * @see Tree#toParenthesesString()
 * @see TreeMatcher
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.4
 * @since 4.4
 */
public final class TreePattern<V> {

	private final TreeNode<Node<V>> _pattern;
	private final SortedSet<Var<V>> _variables;

	/**
	 * Create a new tree-pattern object from the given pattern tree.
	 *
	 * @param pattern the pattern-tree
	 * @throws NullPointerException if the given {@code pattern} is {@code null}
	 * @throws IllegalArgumentException if {@link Var} nodes are not leaf nodes;
	 *         {@link Tree#isLeaf()} is {@code false}
	 */
	public TreePattern(final Tree<Node<V>, ?> pattern) {
		_pattern = TreeNode.ofTree(pattern);
		_variables = vars();
	}

	// Extracts the variables from the pattern.
	private SortedSet<Var<V>> vars() {
		final SortedSet<Var<V>> variables = new TreeSet<>();
		for (Tree<Node<V>, ?> n : _pattern) {
			if (n.getValue() instanceof Var) {
				if (!n.isLeaf()) {
					throw new IllegalArgumentException(format(
						"Variable node '%s' is not a leaf: %s",
						n.getValue(), n.toParenthesesString()
					));
				}

				variables.add((Var<V>) n.getValue());
			}
		}

		return Collections.unmodifiableSortedSet(variables);
	}

	/**
	 * Return the <em>unmodifiable</em> set of variables, defined in {@code this}
	 * pattern. The variables are returned without the angle brackets.
	 *
	 * @return the variables, defined in this pattern
	 */
	public SortedSet<Var<V>> variables() {
		return _variables;
	}

	/**
	 * Creates a matcher that will match the given input tree against
	 * {@code this} pattern.
	 *
	 * @param tree the tree to be matched
	 * @param equals the predicate which checks the equality between the tree
	 *        node values and the string representation of the tree pattern
	 * @return a new matcher for {@code this} pattern
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	//public TreeMatcher<V> matcher(final Tree<V, ?> tree) {
	//	return TreeMatcher.of(this, tree, equals);
	//}

	/**
	 * Creates a matcher that will match the given input tree against
	 * {@code this} pattern. For comparing the tree node values with the pattern,
	 * the node values are converted to strings (with the {@link Object#toString()}
	 * first.
	 *
	 * @param tree the tree to be matched
	 * @return a new matcher for {@code this} pattern
	 * @throws NullPointerException if the arguments is {@code null}
	 */
	public TreeMatcher<V> matcher(final Tree<V, ?> tree) {
		return TreeMatcher.of(this, tree);
	}

	/**
	 * Default equals comparison between node values and pattern string values.
	 *
	 * @param value the tree node value
	 * @param string the pattern node string
	 * @param <V> the tree node value type
	 * @return {@code true} if the string representation of the {@code value}
	 *         and the pattern {@code string} value are equal
	 */
	static <V> boolean equals(final V value, final String string) {
		return Objects.equals(Objects.toString(value), string);
	}

	public Optional<TreeMatchResult<V>> match(final Tree<V, ?> tree) {
		final Map<Var<V>, Tree<V, ?>> vars = new HashMap<>();
		final boolean matches = matches(tree, _pattern, vars);

		return matches
			? Optional.of(TreeMatchResult.of(tree, unmodifiableMap(vars)))
			: Optional.empty();
	}

	/**
	 * Tests whether the given input tree matches {@code this} pattern, using
	 * the given {@code equals} predicate. For comparing the tree node values
	 * with the pattern, the node values are converted to strings (with the
	 * {@link Object#toString()} first.
	 *
	 * @param tree the tree to be matched
	 * @return {@code true} if the {@code tree} matches {@code this} pattern,
	 *         {@code false} otherwise
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public boolean matches(final Tree<V, ?> tree) {
		return matches(tree, _pattern, new HashMap<>());
	}

	private static <V> boolean matches(
		final Tree<V, ?> node,
		final Tree<Node<V>, ?> pattern,
		final Map<Var<V>, Tree<V, ?>> vars
	) {
		final Node<V> decl = pattern.getValue();

		if (decl instanceof Var) {
			final Tree<? extends V, ?> tree = vars.get(decl);
			if (tree == null) {
				vars.put((Var<V>)decl, node);
				return true;
			}

			return tree.equals(node);
		} else {
			final Val<V> p = (Val<V>)pattern.getValue();
			final V v = node.getValue();

			if (Objects.equals(v, p.value())) {
				if (node.childCount() == pattern.childCount()) {
					for (int i = 0; i < node.childCount(); ++i) {
						final Tree<V, ?> cn = node.getChild(i);
						final Tree<Node<V>, ?> cp = pattern.getChild(i);

						if (!matches(cn, cp, vars)) {
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

	/**
	 * Expands {@code this} pattern with the given variable mapping and using
	 * the given value {@code mapper}. Missing {@code variables} mapping are
	 * removed from the expanded tree.
	 *
	 * @param variables the variables to use for expanding {@code this} pattern
	 * @return the expanded tree pattern
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if not all needed variables are part
	 *         of the {@code variables} map
	 */
	public TreeNode<V> expand(final Map<Var<V>, Tree<V, ?>> variables) {
		return expand(_pattern, variables);
	}

	// Expanding the template.
	private static <V> TreeNode<V> expand(
		final Tree<Node<V>, ?> template,
		final Map<Var<V>, Tree<V, ?>> vars
	) {
		final Map<Path, Var<V>> paths = template.stream()
			.filter((Tree<Node<V>, ?> n) -> n.getValue() instanceof Var)
			.collect(toMap(t -> t.childPath(), t -> (Var<V>)t.getValue()));

		final TreeNode<V> tree = TreeNode.ofTree(
			template,
			n -> n instanceof Val ? ((Val<V>)n).value() : null
		);

		for (Map.Entry<Path, Var<V>> var : paths.entrySet()) {
			final Path path = var.getKey();
			final Var<V> decl = var.getValue();
			final TreeNode<V> child = tree.childAtPath(path)
				.orElseThrow(AssertionError::new);

			final Tree<? extends V, ?> replacement = vars.get(decl);
			if (replacement != null) {
				tree.replaceAtPath(path, TreeNode.ofTree(replacement));
			} else {
				tree.removeAtPath(path);
			}
		}

		return tree;
	}

	@Override
	public int hashCode() {
		return _pattern.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof TreePattern &&
			_pattern.equals(((TreePattern)obj)._pattern);
	}

	@Override
	public String toString() {
		return _pattern.toParenthesesString();
	}

	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	/**
	 * Compiles the given tree pattern string.
	 *
	 * @param pattern the tree pattern string
	 * @return the compiled pattern
	 * @throws NullPointerException if the given pattern is {@code null}
	 * @throws IllegalArgumentException if the given parentheses tree string
	 *         doesn't represent a valid pattern tree
	 */
	public static TreePattern<String> compile(final String pattern) {
		return compile(pattern, Function.identity());
	}

	public static <V> TreePattern<V> compile(
		final String pattern,
		final Function<? super String, ? extends V> mapper
	) {
		return new TreePattern<>(
			TreeNode.parse(pattern, v -> Node.of(v, mapper))
		);
	}

	/* *************************************************************************
	 * Helper classes.
	 * ************************************************************************/

	public abstract static class Node<V> {
		private Node() {
		}

		static <V> Node<V> of(
			final String value,
			final Function<? super String, ? extends V> mapper
		) {
			return value.startsWith("<") && value.endsWith(">")
				? Var.of(value.substring(1, value.length() - 1))
				: Val.of(mapper.apply(value));
		}
	}

	public static final class Var<V>
		extends Node<V>
		implements Comparable<Var<V>>
	{
		private final String _name;

		private Var(final String name) {
			_name = requireNonNull(name);
		}

		public String name() {
			return _name;
		}

		@Override
		public int compareTo(final Var<V> var) {
			return _name.compareTo(var._name);
		}

		@Override
		public int hashCode() {
			return _name.hashCode();
		}

		@Override
		public boolean equals(final Object obj) {
			return obj == this ||
				obj instanceof Var &&
				Objects.equals(_name, ((Var)obj)._name);
		}

		@Override
		public String toString() {
			return format("<%s>", _name);
		}

		static <V> Var<V> of(final String name) {
			return new Var<>(name);
		}

	}

	/**
	 * This class represents a constant pattern value.
	 *
	 * @param <V> the node value type
	 */
	public static final class Val<V> extends Node<V> {
		private final V _value;

		private Val(final V value) {
			_value = value;
		}

		public V value() {
			return _value;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(_value);
		}

		@Override
		public boolean equals(final Object obj) {
			return obj == this ||
				obj instanceof TreePattern.Val &&
				Objects.equals(_value, ((Val)obj)._value);
		}

		@Override
		public String toString() {
			return Objects.toString(_value);
		}

		public static <V> Val<V> of(final V value) {
			return new Val<>(value);
		}

	}

}
