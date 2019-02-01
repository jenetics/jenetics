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
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.Hashes.hash;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.Tree.Path;
import io.jenetics.ext.util.TreeNode;

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
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class TreePattern {

	private final Tree<Decl, ?> _pattern;


	private TreePattern(final Tree<Decl, ?> pattern) {
		_pattern = requireNonNull(pattern);

		for (Tree<Decl, ?> n : pattern) {
			if (n.getValue().isVar && !n.isLeaf()) {
				throw new IllegalArgumentException(format(
					"Variable node '%s' is not a leaf: %s",
					n.getValue(), n.toParenthesesString()
				));
			}
		}
	}

	/**
	 * Compiles the given tree pattern string.
	 *
	 * @param pattern the tree pattern string
	 * @return the compiled pattern
	 * @throws NullPointerException if the given pattern is {@code null}
	 * @throws IllegalArgumentException if the given parentheses tree string
	 *         doesn't represent a valid pattern tree
	 */
	static TreePattern compile(final String pattern) {
		return new TreePattern(TreeNode.parse(pattern, Decl::of));
	}

	/**
	 * Creates a matcher that will match the given input tree against
	 * {@code this} pattern.
	 *
	 * @param tree the tree to be matched
	 * @param equals the predicate which checks the equality between the tree
	 *        node values and the string representation of the tree pattern
	 * @param <V> the tree value type
	 * @return a new matcher for {@code this} pattern
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	<V> TreeMatcher<V> matcher(
		final Tree<V, ?> tree,
		final BiPredicate<V, String> equals
	) {
		return TreeMatcher.of(this, tree, equals);
	}

	/**
	 * Creates a matcher that will match the given input tree against
	 * {@code this} pattern. For comparing the tree node values with the pattern,
	 * the node values are converted to strings (with the {@link Object#toString()}
	 * first.
	 *
	 * @param tree the tree to be matched
	 * @param <V> the tree value type
	 * @return a new matcher for {@code this} pattern
	 * @throws NullPointerException if the arguments is {@code null}
	 */
	<V> TreeMatcher<V> matcher(final Tree<V, ?> tree) {
		return matcher(tree, TreePattern::equals);
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

	<V> Optional<TreeMatchResult<V>> match(
		final Tree<V, ?> tree,
		final BiPredicate<V, String> equals
	) {
		final Map<String, Tree<V, ?>> vars = new HashMap<>();
		final boolean matches = matches(tree, _pattern, vars, equals);

		return matches
			? Optional.of(TreeMatchResult.of(tree, unmodifiableMap(vars)))
			: Optional.empty();
	}

	/**
	 * Tests whether the given input tree matches {@code this} pattern, using
	 * the given {@code equals} predicate.
	 *
	 * @param tree the tree to be matched
	 * @param equals the predicate which checks the equality between the tree
	 *        node values and the string representation of the tree pattern
	 * @param <V> the tree value type
	 * @return {@code true} if the {@code tree} matches {@code this} pattern,
	 *         {@code false} otherwise
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	<V> boolean matches(
		final Tree<V, ?> tree,
		final BiPredicate<V, String> equals
	) {
		return matches(tree, _pattern, new HashMap<>(), equals);
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
	boolean matches(final Tree<?, ?> tree) {
		return matches(tree, TreePattern::equals);
	}

	private static <V> boolean matches(
		final Tree<V, ?> node,
		final Tree<Decl, ?> pattern,
		final Map<String, Tree<V, ?>> vars,
		final BiPredicate<V, String> equals
	) {
		final Decl decl = pattern.getValue();

		if (decl.isVar) {
			final Tree<V, ?> tree = vars.get(decl.value);
			if (tree == null) {
				vars.put(decl.value, node);
				return true;
			}

			return tree.equals(node);
		} else {
			final String p = pattern.getValue().value;
			final V v = node.getValue();

			if (equals.test(v, p)) {
				if (node.childCount() == pattern.childCount()) {
					for (int i = 0; i < node.childCount(); ++i) {
						final Tree<V, ?> cn = node.getChild(i);
						final Tree<Decl, ?> cp = pattern.getChild(i);

						if (!matches(cn, cp, vars, equals)) {
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
	 * @param mapper the string value mapper
	 * @param <V> the tree node type
	 * @return the expanded tree pattern
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if not all needed variables are part
	 *         of the {@code variables} map
	 */
	<V> TreeNode<V> expand(
		final Map<String, Tree<V, ?>> variables,
		final Function<? super String, ? extends V> mapper
	) {
		return expand(_pattern, variables, mapper);
	}

	private static <V> TreeNode<V> expand(
		final Tree<Decl, ?> template,
		final Map<String, Tree<V, ?>> vars,
		final Function<? super String, ? extends V> mapper
	) {
		final Map<Path, Decl> paths = template.stream()
			.filter((Tree<Decl, ?> n) -> n.getValue().isVar)
			.collect(Collectors.toMap(t -> t.childPath(), t -> t.getValue()));

		final Function<Decl, String> m = d -> d.isVar ? null : d.value;
		final TreeNode<V> tree = TreeNode.ofTree(template, m.andThen(mapper));

		for (Map.Entry<Path, Decl> var : paths.entrySet()) {
			final Path path = var.getKey();
			final Decl decl = var.getValue();
			final TreeNode<V> child = tree.childAtPath(path)
				.orElseThrow(AssertionError::new);

			final Optional<TreeNode<V>> parent = child.getParent();
			parent.ifPresent(p -> {
				final int index = p.getIndex(child);
				final Tree<V, ?> replacement = vars.get(decl.value);

				if (replacement != null) {
					p.replace(index, TreeNode.ofTree(replacement));
				} else {
					p.remove(index);
				}
			});

			if (!parent.isPresent()) {
				final Tree<V, ?> replacement = vars.get(decl.value);
				return replacement != null
					? TreeNode.ofTree(replacement)
					: TreeNode.of();
			}
		}

		return tree;
	}

	/**
	 * Expands {@code this} pattern with the given variable mapping.
	 *
	 * @param variables the variables to use for expanding {@code this} pattern
	 * @return the expanded tree pattern
	 * @throws NullPointerException if the variables mapping is {@code null}
	 * @throws IllegalArgumentException if not all needed variables are part
	 *         of the {@code variables} map
	 */
	TreeNode<String> expand(final Map<String, Tree<String, ?>> variables) {
		return expand(variables, Function.identity());
	}


	/* *************************************************************************
	 * Helper classes
	 * ************************************************************************/

	private static final class Decl {
		private final String value;
		private final boolean isVar;

		private Decl(final String value, final boolean isVar) {
			this.value = value;
			this.isVar = isVar;
		}

		@Override
		public int hashCode() {
			return hash(value, hash(isVar));
		}

		@Override
		public boolean equals(final Object obj) {
			return obj == this ||
				obj instanceof Decl &&
				Objects.equals(value, ((Decl)obj).value) &&
				isVar == ((Decl)obj).isVar;
		}

		@Override
		public String toString() {
			return isVar ? value : format("<%s>", value);
		}

		static Decl val(final String value) {
			return new Decl(value, false);
		}

		static Decl var(final String value) {
			return new Decl(value, true);
		}

		static Decl of(final String value) {
			return value.startsWith("<") && value.endsWith(">")
				? Decl.var(value.substring(1, value.length() - 1))
				: Decl.val(value);
		}
	}

}
