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
import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.Hashes.hash;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.jenetics.ext.rewriting.TreePattern.Var;
import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.Tree.Path;
import io.jenetics.ext.util.TreeNode;

/**
 * Represents a tree rewrite rule. A rewrite rule consists of a match pattern,
 * which must be matched, and a substitution pattern, which is expanded and
 * replaces the variables in the pattern. Some simple <em>arithmetic</em>
 * rewrite rules.
 * <pre> {@code
 *     add($x,0) -> $x
 *     mul($x,1) -> $x
 * }</pre>
 * The <em>substitution</em> pattern may only use variables, already defined in
 * the <em>match</em> pattern. So, the creation of the following rewrite rule s
 * would lead to an {@link IllegalArgumentException}:
 * <pre> {@code
 *     add($x,0) -> $y
 *     mul(0,1) -> mul($x,1)
 * }</pre>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Rewriting#Term_rewriting_systems">
 *      Tree rewriting systems</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
public final class TreeRewriteRule<V> implements TreeRewriter<V>, Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private final TreePattern<V> _left;
	private final TreePattern<V> _right;

	/**
	 * Create a new rewrite rule from the given <em>matching</em> ({@code left})
	 * and <em>replacement</em> ({@code right}) pattern.
	 *
	 * @param left the matching pattern of the rule
	 * @param right the substitution pattern
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the <em>template</em> pattern uses
	 *         variables not defined in the <em>matcher</em> pattern
	 */
	public TreeRewriteRule(
		final TreePattern<V> left,
		final TreePattern<V> right
	) {
		_left = requireNonNull(left);
		_right = requireNonNull(right);

		final Set<Var<V>> undefined = new HashSet<>(_right.vars());
		undefined.removeAll(_left.vars());

		if (!undefined.isEmpty()) {
			throw new IllegalArgumentException(format(
				"Some template variables are not defined in the matcher '%s': %s",
				this,
				undefined.stream()
					.map(v -> format("%s", v))
					.collect(Collectors.joining(", "))
			));
		}
	}

	/**
	 * Return the rule matching pattern.
	 *
	 * @return the rule matching pattern
	 */
	public TreePattern<V> left() {
		return _left;
	}

	/**
	 * Return the replacement pattern of the rule.
	 *
	 * @return the replacement pattern of the rule
	 */
	public TreePattern<V> right() {
		return _right;
	}

	/**
	 * Maps {@code this} rewrite rule from type {@code V} to type {@code B}.
	 *
	 * @param mapper the type mapper
	 * @param <B> the target type
	 * @return a new rewrite rule for the mapped type
	 * @throws NullPointerException if the {@code mapper} is {@code null}
	 */
	public <B> TreeRewriteRule<B> map(final Function<? super V, ? extends B> mapper) {
		return new TreeRewriteRule<>(_left.map(mapper), _right.map(mapper));
	}

	@Override
	public int rewrite(final TreeNode<V> tree, final int limit) {
		requireNonNull(tree);

		int rewritten = 0;
		Optional<TreeMatchResult<V>> result;
		do {
			result = left().matcher(tree).results().findFirst();

			result.ifPresent(res -> rewrite(res, tree));
			rewritten += result.isPresent() ? 1 : 0;
		} while (result.isPresent() && rewritten < limit);

		return rewritten;
	}

	private void rewrite(
		final TreeMatchResult<V> result,
		final TreeNode<V> tree
	) {
		final Map<Var<V>, Tree<V, ?>> vars = result.vars();
		final TreeNode<V> r = _right.expand(vars);

		final Path path = result.tree().childPath();
		tree.replaceAtPath(path, r);
	}

	@Override
	public int hashCode() {
		return hash(_left, hash(_right));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof TreeRewriteRule<?> other &&
			_left.equals(other._left) &&
			_right.equals(other._right);
	}

	@Override
	public String toString() {
		return format("%s -> %s", _left, _right);
	}

	/**
	 * Compiles the string representation of a rewrite rule:
	 * <pre> {@code
	 *     add($x,0) -> $x
	 *     mul($x,1) -> $x
	 * }</pre>
	 *
	 * @param <V> the tree node type
	 * @param rule the rewrite rule
	 * @param mapper the mapper function which converts the node value into the
	 *        actual type {@code V}
	 * @return a new rewrite rule, compiled from the given rule string
	 * @throws IllegalArgumentException if the rewrite rule is invalid
	 * @throws NullPointerException if on of the arguments is {@code null}
	 */
	public static <V> TreeRewriteRule<V> parse(
		final String rule,
		final Function<? super String, ? extends V> mapper
	) {
		final String[] parts = rule.split("->");
		if (parts.length == 1) {
			throw new IllegalArgumentException(format(
				"Invalid rewrite rule; missing separator '->': %s", rule
			));
		}
		if (parts.length > 2) {
			throw new IllegalArgumentException(format(
				"Invalid rewrite rule; found %d separators '->': %s",
				parts.length - 1, rule
			));
		}

		return new TreeRewriteRule<>(
			TreePattern.compile(parts[0], mapper),
			TreePattern.compile(parts[1], mapper)
		);
	}

	/**
	 * Compiles the string representation of a rewrite rule:
	 * <pre> {@code
	 *     add($x,0) -> $x
	 *     mul($x,1) -> $x
	 * }</pre>
	 *
	 * @param rule the rewrite rule
	 * @return a new rewrite rule, compiled from the given rule string
	 * @throws IllegalArgumentException if the rewrite rule is invalid
	 * @throws NullPointerException if on of the arguments is {@code null}
	 */
	public static TreeRewriteRule<String> parse(final String rule) {
		return parse(rule, Function.identity());
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.TREE_REWRITE_RULE, this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final ObjectOutput out) throws IOException {
		out.writeObject(_left);
		out.writeObject(_right);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	static Object read(final ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		final TreePattern left = (TreePattern)in.readObject();
		final TreePattern right = (TreePattern)in.readObject();
		return new TreeRewriteRule(left, right);
	}

}
