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

import static io.jenetics.ext.internal.util.TreeRewriteRule.compile;

import io.jenetics.util.ISeq;

import io.jenetics.ext.internal.util.TreeRewriteRule;
import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 4.1
 */
class MathExprRewrite {

	private static final ISeq<TreeRewriteRule> RULES = ISeq.of(
		compile("sub(<x>,<x>) -> 0"),
		compile("add(<x>,<x>) -> mul(2,<x>)"),
		compile("sub(<x>,0) -> <x>"),
		compile("add(<x>,0) -> <x>"),
		compile("add(0,<x>) -> <x>"),
		compile("div(<x>,<x>) -> 1"),
		compile("mul(<x>,0) -> 0"),
		compile("mul(0,<x>) -> 0"),
		compile("mul(<x>,1) -> <x>"),
		compile("mul(1,<x>) -> <x>"),
		compile("mul(<x>,<x>) -> pow(<x>,2)"),
		compile("pow(<x>,0) -> 1"),
		compile("pos(<x>,1) -> <x>")
	);

	static TreeNode<Op<Double>> rewrite(final TreeNode<Op<Double>> node) {
		//while (_prune(node));
		return node;
	}

/*
	CONST_EXPR {
		@Override
		public boolean matches(final TreeNode<Op<Double>> node) {
			return
				node.getValue() instanceof MathOp &&
				node.childStream()
					.allMatch(child -> child.getValue() instanceof Const);
		}

		@Override
		public void rewrite(final TreeNode<Op<Double>> node) {
			final Double[] args = node.childStream()
				.map(child -> ((Const<Double>)child.getValue()).value())
				.toArray(Double[]::new);

			final Double value = node.getValue().apply(args);
			node.removeAllChildren();
			node.setValue(Const.of(value));
		}
	};

	static TreeNode<Op<Double>> prune(final TreeNode<Op<Double>> node) {
		while (_prune(node));
		return node;
	}

	private static boolean
	_prune(final TreeNode<Op<Double>> node) {
		final Optional<MathExprRewriteRule> simplifier= Stream.of(values())
			.filter(s -> s.matches(node))
			.findFirst();

		simplifier.ifPresent(s -> s.rewrite(node));
		return simplifier.isPresent() | node.childStream()
			.mapToInt(child -> _prune(child) ? 1 : 0)
			.sum() > 0;
	}

	static boolean equals(
		final Tree<? extends Op<Double>, ?> node,
		final int index,
		final double value
	) {
		return node.getChild(index).getValue() instanceof Const &&
			((Const)node.getChild(index).getValue()).value().equals(value);
	}
*/
}
