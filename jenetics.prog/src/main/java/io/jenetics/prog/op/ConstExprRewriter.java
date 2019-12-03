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

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Array;
import java.util.Optional;
import java.util.stream.Stream;

import io.jenetics.ext.rewriting.TreeRewriter;
import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

/**
 * This class rewrites constant expressions to its single value.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.2
 * @since 5.0
 */
final class ConstExprRewriter<T> implements TreeRewriter<Op<T>> {

	private final Class<T> _type;

	ConstExprRewriter(final Class<T> type) {
		_type = requireNonNull(type);
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
		} while(result.isPresent() && rewritten < limit);

		return rewritten;
	}

	private int rewriting(final TreeNode<Op<T>> node) {
		if (matches(node)) {
			final T[] args = node.childStream()
				.map(child -> ((Val<T>)child.getValue()).value())
				.toArray(size -> (T[])Array.newInstance(_type, size));

			final T value = node.getValue().apply(args);
			node.removeAllChildren();
			node.setValue(Const.of(value));

			return 1;
		}

		return 0;
	}

	private static <T> Stream<TreeNode<Op<T>>>
	results(final TreeNode<Op<T>> node) {
		return node.stream()
			.filter(ConstExprRewriter::matches);
	}

	private static <T> boolean matches(final Tree<Op<T>, ?> node) {
		return
			!(node.getValue() instanceof Val) &&
			!(node.getValue() instanceof Var) &&
			node.childStream()
				.allMatch(child -> child.getValue() instanceof Val);
	}

	@Override
	public String toString() {
		return "ConstExprRewriter";
	}

}
