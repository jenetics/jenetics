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

import java.util.Optional;
import java.util.stream.Stream;

import io.jenetics.ext.internal.util.TreeRewriter;
import io.jenetics.ext.util.Tree;
import io.jenetics.ext.util.TreeNode;

/**
 * This class rewrites constant expressions to its single value.
 *
 * <pre>{@code
 * 1 + 2 + 3 + 4 -> 10.0
 * 1 + 2*(6 + 7) -> 27.0
 * sin(0) -> 0.0
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.4
 * @since 4.4
 */
final class ConstExprRewriter implements TreeRewriter<Op<Double>> {

	static final TreeRewriter<Op<Double>> REWRITER = new ConstExprRewriter();

	@Override
	public boolean rewrite(final TreeNode<Op<Double>> node) {
		requireNonNull(node);

		boolean rewritten = false;
		boolean res;
		Optional<TreeNode<Op<Double>>> result;
		do {
			result = results(node).findFirst();

			res = result.map(ConstExprRewriter::_rewrite).orElse(false);
			rewritten = res || rewritten;
		} while(result.isPresent());

		return rewritten;
	}

	private static boolean _rewrite(final TreeNode<Op<Double>> node) {
		if (matches(node)) {
			final Double[] args = node.childStream()
				.map(child -> ((Const<Double>)child.getValue()).value())
				.toArray(Double[]::new);

			final double value = node.getValue().apply(args);
			node.removeAllChildren();
			node.setValue(Const.of(value));

			return true;
		}

		return false;
	}

	private static Stream<TreeNode<Op<Double>>>
	results(final TreeNode<Op<Double>> node) {
		return node.stream()
			.filter(ConstExprRewriter::matches);
	}

	private static boolean matches(final Tree<Op<Double>, ?> node) {
		return
			node.getValue() instanceof MathOp &&
				node.childStream()
					.allMatch(child -> child.getValue() instanceof Const);
	}

}
