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
package io.jenetics.prog.op;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.Array;
import java.util.Optional;
import java.util.stream.Stream;

import io.jenetics.ext.rewriting.TreeRewriter;
import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

/**
 * This class rewrites constant expressions to its single value.
 * <p>
 * The following example shows how to use the rewriter for a double operation
 * tree:
 * {@snippet lang="java":
 * final TreeNode<Op<Double>> tree = MathExpr.parseTree("1 + 2 + 3 + 4");
 * ConstRewriter.ofType(Double.class).rewrite(tree);
 * assert tree.getValue().equals(Const.of(10.0));
 * }
 *
 * @param <T> the operation type the rewriter is working on
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.2
 * @since 5.2
 */
public final class ConstRewriter<T> implements TreeRewriter<Op<T>> {

	/**
	 * Const rewriter for double operation trees.
	 */
	public static final ConstRewriter<Double> DOUBLE = ofType(Double.class);

	private final Class<T> _type;

	@SuppressWarnings("unchecked")
	private ConstRewriter(final Class<? extends T> type) {
		_type = (Class<T>)requireNonNull(type);
	}

	/**
	 * Return the operation type this rewriter is working on.
	 *
	 * @return the operation type this rewriter is working on
	 */
	public Class<T> type() {
		return _type;
	}

	@Override
	public int rewrite(final TreeNode<Op<T>> node, final int limit) {
		requireNonNull(node);

		int rewritten = 0;
		int res;
		Optional<TreeNode<Op<T>>> result;
		do {
			result = results(node).findFirst();

			res = result.map(this::rewriting).orElse(0);
			rewritten += res;
		} while (result.isPresent() && rewritten < limit);

		return rewritten;
	}

	private int rewriting(final TreeNode<Op<T>> node) {
		if (matches(node)) {
			final T[] args = newArray(node.childCount());
			for (int i = 0, n = node.childCount(); i < n; ++i) {
				args[i] = ((Val<T>)node.childAt(i).value()).value();
			}

			final T value = node.value().apply(args);
			node.removeAllChildren();
			node.value(Const.of(value));

			return 1;
		} else {
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	private T[] newArray(final int length) {
		return (T[])Array.newInstance(_type, length);
	}

	private static <T> Stream<TreeNode<Op<T>>>
	results(final TreeNode<Op<T>> node) {
		return node.stream()
			.filter(ConstRewriter::matches);
	}

	private static boolean matches(final Tree<?, ?> node) {
		return
			!(node.value() instanceof Val) &&
			!(node.value() instanceof Var) &&
			node.childStream()
				.allMatch(child -> child.value() instanceof Val);
	}

	@Override
	public String toString() {
		return format("ConstRewriter<%s>", _type.getSimpleName());
	}

	/**
	 * Create a new rewriter for constant operation subtrees (expressions).
	 *
	 * @param type the type of the operation tree
	 * @param <T> the type of the operation tree
	 * @return a new rewriter for constant operation subtrees (expressions)
	 * @throws NullPointerException if the given {@code type} is {@code null}
	 */
	public static <T> ConstRewriter<T> ofType(final Class<? extends T> type) {
		return new ConstRewriter<>(type);
	}

}
