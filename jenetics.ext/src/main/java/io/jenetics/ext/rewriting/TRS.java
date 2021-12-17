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

import static io.jenetics.internal.util.SerialIO.readInt;
import static io.jenetics.internal.util.SerialIO.writeInt;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;

import io.jenetics.ext.util.TreeNode;

/**
 * This class represents a Tree Rewrite System, which consists of a set of
 * Tree Rewrite Rules.
 * <pre>{@code
 * final TRS<String> trs = TRS.parse(
 *     "add(0,$x) -> $x",
 *     "add(S($x),$y) -> S(add($x,$y))",
 *     "mul(0,$x) -> 0",
 *     "mul(S($x),$y) -> add(mul($x,$y),$y)"
 * );
 *
 * // Converting the input tree into its normal form.
 * final TreeNode<String> tree = TreeNode.parse("add(S(0),S(mul(S(0),S(S(0)))))");
 * trs.rewrite(tree);
 * assert tree.equals(TreeNode.parse("S(S(S(S(0))))"));
 * }</pre>
 *
 * @see TreeRewriteRule
 * @see <a href="https://en.wikipedia.org/wiki/Rewriting">TRS</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
public final class TRS<V> implements TreeRewriter<V>, Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private final ISeq<TreeRewriteRule<V>> _rules;

	/**
	 * Create a new TRS from the given rewrite rules.
	 *
	 * @param rules the rewrite rules the TRS consists of
	 * @throws NullPointerException if the given {@code rules} are {@code null}
	 * @throws IllegalArgumentException if the given {@code rules} sequence is
	 *         empty
	 */
	public TRS(final ISeq<TreeRewriteRule<V>> rules) {
		if (rules.isEmpty()) {
			throw new IllegalArgumentException("Rewrite rules must not be empty.");
		}
		_rules = rules;
	}

	@Override
	public int rewrite(final TreeNode<V> tree, final int limit) {
		return TreeRewriter.rewrite(tree, limit, _rules);
	}

	/**
	 * Maps {@code this} TRS from type {@code V} to type {@code B}.
	 *
	 * @param mapper the type mapper
	 * @param <B> the target type
	 * @return a new TRS for the mapped type
	 * @throws NullPointerException if the {@code mapper} is {@code null}
	 */
	public <B> TRS<B> map(final Function<? super V, ? extends B> mapper) {
		return new TRS<>(_rules.map(rule -> rule.map(mapper)));
	}

	@Override
	public int hashCode() {
		return _rules.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof TRS<?> other &&
			_rules.equals(other._rules);
	}

	@Override
	public String toString() {
		return _rules.stream()
			.map(Objects::toString)
			.collect(Collectors.joining("; "));
	}

	/**
	 * Create a new TRS from the given rewrite rules and type mapper.
	 *
	 * @param mapper the tree value type mapper
	 * @param rules the rewrite rules
	 * @param <V> the tree value type the rewriter is working on
	 * @return a new TRS
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the given {@code rules} sequence is
	 *         empty
	 */
	public static <V> TRS<V> parse(
		final Function<? super String, ? extends V> mapper,
		final String... rules
	) {
		return new TRS<>(
			ISeq.of(rules)
				.map(rule -> TreeRewriteRule.parse(rule, mapper))
		);
	}

	/**
	 * Create a new TRS from the given rewrite rules.
	 *
	 * @param rules the rewrite rules
	 * @return a new TRS
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the given {@code rules} sequence is
	 *         empty
	 */
	public static TRS<String> parse(final String... rules) {
		return parse(Function.identity(), rules);
	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.TRS_KEY, this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final ObjectOutput out) throws IOException {
		writeInt(_rules.length(), out);
		for (int i = 0; i < _rules.length(); ++i) {
			out.writeObject(_rules.get(i));
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	static Object read(final ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		final var length = readInt(in);
		final var rules = MSeq.ofLength(length);
		for (int i = 0; i < length; ++i) {
			rules.set(i, in.readObject());
		}

		return new TRS(rules.toISeq());
	}

}
