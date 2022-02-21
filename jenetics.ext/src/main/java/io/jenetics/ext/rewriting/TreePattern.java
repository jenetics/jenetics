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
package io.jenetics.ext.rewriting;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;
import static io.jenetics.ext.internal.Names.isIdentifier;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

import io.jenetics.ext.internal.Escaper;
import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.Tree.Path;
import io.jenetics.ext.util.TreeNode;

/**
 * This class serves two purposes. Firstly, it is used as a <em>classical</em>
 * pattern, which is used to find <em>matches</em> against a <em>matching</em>
 * tree. Secondly, it can <em>expand</em> a given pattern to a full tree with a
 * given <em>pattern</em> variable to sub-tree mapping.
 *
 * <p><b>Matching trees</b></p>
 *
 * A compiled representation of a <em>tree</em> pattern. A tree pattern,
 * specified as a parentheses string, must first be compiled into an instance of
 * this class. The resulting pattern can then be used to create a
 * {@link TreeMatcher} object that can match arbitrary trees against the tree
 * pattern. All the states involved in performing a match resides in the
 * matcher, so many matchers can share the same pattern.
 * <p>
 * The string representation of a tree pattern is a parenthesis tree string,
 * with a special wildcard syntax for arbitrary sub-trees. The sub-trees
 * variables are prefixed with a '$' and must be a valid Java identifier.
 * <pre>{@code
 * final TreePattern<String> p1 = TreePattern.compile("add($a,add($b,sin(x)))");
 * final TreePattern<String> p2 = TreePattern.compile("pow($x,$y)");
 * }</pre>
 *
 * If you need to have values which starts with a '$' character, you can escape
 * it with a '\'.
 * <pre>{@code
 * final TreePattern<String> p1 = TreePattern.compile("concat($x,\\$foo)");
 * }</pre>
 *
 * The second value, {@code $foo}, of the {@code concat} function is not treated
 * as <em>pattern</em> variable.
 * <p>
 * If you want to match against trees with a different value type than
 * {@code String}, you have to specify an additional type mapper function when
 * compiling the pattern string.
 * <pre>{@code
 * final TreePattern<Op<Double>> p = TreePattern.compile(
 *     "add($a,add($b,sin(x)))",
 *     MathOp::toMathOp
 * );
 * }</pre>
 *
 * <p><b>Expanding trees</b></p>
 *
 * The second functionality of the tree pattern is to expand a pattern to a whole
 * tree with a given <em>pattern</em> variable to sub-tree mapping.
 * <pre>{@code
 * final TreePattern<String> pattern = TreePattern.compile("add($x,$y,1)");
 * final Map<Var<String>, Tree<String, ?>> vars = Map.of(
 *     Var.of("x"), TreeNode.parse("sin(x)"),
 *     Var.of("y"), TreeNode.parse("sin(y)")
 * );
 *
 * final Tree<String, ?> tree = pattern.expand(vars);
 * assertEquals(tree.toParenthesesString(), "add(sin(x),sin(y),1)");
 * }</pre>
 *
 * @see TreeRewriteRule
 * @see Tree#toParenthesesString()
 * @see TreeMatcher
 *
 * @param <V> the value type of the tree than can be matched by this pattern
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.0
 * @since 5.0
 */
public final class TreePattern<V> implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	// Primary state of the tree pattern.
	private final TreeNode<Decl<V>> _pattern;

	// Cached variable set.
	private final SortedSet<Var<V>> _vars;

	/**
	 * Create a new tree-pattern object from the given pattern tree.
	 *
	 * @param pattern the pattern-tree
	 * @throws NullPointerException if the given {@code pattern} is {@code null}
	 * @throws IllegalArgumentException if {@link Var} nodes are not leaf nodes;
	 *         {@link Tree#isLeaf()} is {@code false}
	 */
	public TreePattern(final Tree<Decl<V>, ?> pattern) {
		_pattern = TreeNode.ofTree(pattern);
		_vars = extractVars(_pattern);
	}

	// Extracts the variables from the pattern.
	private static <V> SortedSet<Var<V>>
	extractVars(final TreeNode<Decl<V>> pattern) {
		final SortedSet<Var<V>> variables = new TreeSet<>();
		for (Tree<Decl<V>, ?> n : pattern) {
			if (n.value() instanceof Var<V> var) {
				if (!n.isLeaf()) {
					throw new IllegalArgumentException(format(
						"Variable node '%s' is not a leaf: %s",
						n.value(), n.toParenthesesString()
					));
				}

				variables.add(var);
			}
		}

		return Collections.unmodifiableSortedSet(variables);
	}

	TreeNode<Decl<V>> pattern() {
		return _pattern;
	}

	/**
	 * Return the <em>unmodifiable</em> set of variables, defined in {@code this}
	 * pattern. The variables are returned without the angle brackets.
	 *
	 * @return the variables, defined in this pattern
	 */
	public SortedSet<Var<V>> vars() {
		return _vars;
	}

	/**
	 * Maps {@code this} tree-pattern from type {@code V} to type {@code B}.
	 *
	 * @param mapper the type mapper
	 * @param <B> the target type
	 * @return a new tree-pattern for the mapped type
	 */
	public <B> TreePattern<B> map(final Function<? super V, ? extends B> mapper) {
		return new TreePattern<>(_pattern.map(d -> d.map(mapper)));
	}

	/**
	 * Creates a matcher that will match the given input tree against
	 * {@code this} pattern.
	 *
	 * @param tree the tree to be matched
	 * @return a new matcher for {@code this} pattern
	 * @throws NullPointerException if the arguments is {@code null}
	 */
	public TreeMatcher<V> matcher(final Tree<V, ?> tree) {
		return TreeMatcher.of(this, tree);
	}

	/**
	 * Try to match the given {@code tree} against {@code this} pattern.
	 *
	 * @param tree the tree to be matched
	 * @return the match result, or {@link Optional#empty()} if the given
	 *         {@code tree} doesn't match
	 * @throws NullPointerException if the arguments is {@code null}
	 */
	public Optional<TreeMatchResult<V>> match(final Tree<V, ?> tree) {
		final Map<Var<V>, Tree<V, ?>> vars = new HashMap<>();
		final boolean matches = matches(tree, _pattern, vars);

		return matches
			? Optional.of(TreeMatchResult.of(tree, vars))
			: Optional.empty();
	}

	/**
	 * Tests whether the given input {@code tree} matches {@code this} pattern.
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
		final Tree<Decl<V>, ?> pattern,
		final Map<Var<V>, Tree<V, ?>> vars
	) {
		final Decl<V> decl = pattern.value();

		if (decl instanceof Var<V> var) {
			final Tree<? extends V, ?> tree = vars.get(decl);
			if (tree == null) {
				vars.put(var, node);
				return true;
			}

			return tree.equals(node);
		} else {
			final Val<V> p = (Val<V>)decl;
			final V v = node.value();

			if (Objects.equals(v, p.value())) {
				if (node.childCount() == pattern.childCount()) {
					for (int i = 0; i < node.childCount(); ++i) {
						final Tree<V, ?> cn = node.childAt(i);
						final Tree<Decl<V>, ?> cp = pattern.childAt(i);

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
	 * Expands {@code this} pattern with the given variable mapping.
	 *
	 * @param vars the variables to use for expanding {@code this} pattern
	 * @return the expanded tree pattern
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if not all needed variables are part
	 *         of the {@code variables} map
	 */
	public TreeNode<V> expand(final Map<Var<V>, Tree<V, ?>> vars) {
		return expand(_pattern, vars);
	}

	// Expanding the template.
	private static <V> TreeNode<V> expand(
		final Tree<Decl<V>, ?> template,
		final Map<Var<V>, Tree<V, ?>> vars
	) {
		final Map<Path, Var<V>> paths = template.stream()
			.filter((Tree<Decl<V>, ?> n) -> n.value() instanceof Var)
			.collect(toMap(Tree::childPath, t -> (Var<V>)t.value()));

		final TreeNode<V> tree = TreeNode.ofTree(
			template,
			n -> n instanceof Val<V> val ? val.value() : null
		);

		paths.forEach((path, decl) -> {
			final Tree<? extends V, ?> replacement = vars.get(decl);
			if (replacement != null) {
				tree.replaceAtPath(path, TreeNode.ofTree(replacement));
			} else {
				tree.removeAtPath(path);
			}
		});

		return tree;
	}

	@Override
	public int hashCode() {
		return _pattern.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof TreePattern<?> other &&
			_pattern.equals(other._pattern);
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
	 *         doesn't represent a valid pattern tree or one of the variable
	 *         name is not a valid (Java) identifier
	 */
	public static TreePattern<String> compile(final String pattern) {
		return compile(pattern, Function.identity());
	}

	/**
	 * Compiles the given tree pattern string.
	 *
	 * @param pattern the tree pattern string
	 * @param mapper the mapper which converts the serialized string value to
	 *        the desired type
	 * @param <V> the value type of the tree than can be matched by the pattern
	 * @return the compiled pattern
	 * @throws NullPointerException if the given pattern is {@code null}
	 * @throws IllegalArgumentException if the given parentheses tree string
	 *         doesn't represent a valid pattern tree or one of the variable
	 *         name is not a valid (Java) identifier
	 */
	public static <V> TreePattern<V> compile(
		final String pattern,
		final Function<? super String, ? extends V> mapper
	) {
		return new TreePattern<>(
			TreeNode.parse(pattern, v -> Decl.of(v.trim(), mapper))
		);
	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.TREE_PATTERN, this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final ObjectOutput out) throws IOException {
		out.writeObject(_pattern);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	static Object read(final ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		final var pattern = (TreeNode)in.readObject();
		return new TreePattern(pattern);
	}


	/* *************************************************************************
	 * Pattern node classes.
	 * ************************************************************************/

	private static final char VAR_PREFIX = '$';
	private static final char ESC_CHAR = '\\';

	private static final Escaper ESCAPER = new Escaper(ESC_CHAR, VAR_PREFIX);

	/**
	 * A sealed interface, which constitutes the nodes of a pattern tree.
	 * The only two implementations of this class are the {@link Var} and the
	 * {@link Val} class. The {@link Var} class represents a placeholder for an
	 * arbitrary sub-tree and the {@link Val} class stands for an arbitrary
	 * concrete sub-tree.
	 *
	 * @see Var
	 * @see Val
	 *
	 * @param <V> the node type the tree-pattern is working on
	 */
	public sealed interface Decl<V> {

		/**
		 * Returns a new {@link Decl} object with the mapped type {@code B}.
		 *
		 * @param mapper the mapping function
		 * @param <B> the mapped type
		 * @return the mapped declaration
		 * @throws NullPointerException if the mapping function is {@code null}
		 */
		<B> Decl<B> map(final Function<? super V, ? extends B> mapper);

		static <V> Decl<V> of(
			final String value,
			final Function<? super String, ? extends V> mapper
		) {
			return Var.isVar(value)
				? new Var<>(value.substring(1))
				: new Val<>(mapper.apply(ESCAPER.unescape(value)));
		}
	}

	/**
	 * Represents a placeholder (variable) for an arbitrary sub-tree. A
	 * <em>pattern</em> variable is identified by its name. The pattern DSL
	 * denotes variable names with a leading '$' character, e.g. {@code $x},
	 * {@code $y} or {@code $my_var}.
	 *
	 * @see Val
	 *
	 * @implNote
	 * This class is comparable by its name.
	 *
	 @param <V> the node type the tree-pattern is working on
	 */
	public record Var<V>(String name)
		implements Decl<V>, Comparable<Var<V>>, Serializable
	{
		@Serial
		private static final long serialVersionUID = 2L;

		/**
		 * @param name the name of the variable
		 * @throws NullPointerException if the given {@code name} is {@code null}
		 * @throws IllegalArgumentException if the given {@code name} is not a
		 *         valid Java identifier
		 */
		public Var {
			if (!isIdentifier(name)) {
				throw new IllegalArgumentException(format(
					"Variable is not valid identifier: '%s'",
					name
				));
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public <B> Var<B> map(final Function<? super V, ? extends B> mapper) {
			return (Var<B>)this;
		}

		@Override
		public int compareTo(final Var<V> var) {
			return name.compareTo(var.name);
		}

		@Override
		public String toString() {
			return format("%s%s", VAR_PREFIX, name);
		}

		static boolean isVar(final String name) {
			return !name.isEmpty() && name.charAt(0) == VAR_PREFIX;
		}

	}

	/**
	 * This class represents a constant pattern value, which can be part of a
	 * whole subtree.
	 *
	 * @see Var
	 *
	 * @param <V> the node value type
	 * @param value the underlying pattern value
	 */
	public record Val<V>(V value) implements Decl<V>, Serializable {
		@Serial
		private static final long serialVersionUID = 2L;

		@Override
		public <B> Val<B> map(final Function<? super V, ? extends B> mapper) {
			return new Val<>(mapper.apply(value));
		}

		@Override
		public String toString() {
			return Objects.toString(value);
		}

	}

}
