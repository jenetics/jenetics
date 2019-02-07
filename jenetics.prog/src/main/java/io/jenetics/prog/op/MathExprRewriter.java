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

import io.jenetics.util.ISeq;

import io.jenetics.ext.internal.util.RuleTreeRewriter;
import io.jenetics.ext.internal.util.TreeRewriteRule;
import io.jenetics.ext.internal.util.TreeRewriter;
import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 4.1
 */
final class MathExprRewriter implements TreeRewriter<Op<Double>> {

	private static final ISeq<TreeRewriter<Op<Double>>> REWRITERS = rewriters();

	private static ISeq<TreeRewriter<Op<Double>>> rewriters() {
		final ISeq<TreeRewriter<Op<Double>>> ruleRewriter =
			ISeq.of(
					"sub(<x>,<x>) -> 0",
					"add(<x>,<x>) -> mul(2,<x>)",
					"sub(<x>,0) -> <x>",
					"add(<x>,0) -> <x>",
					"add(0,<x>) -> <x>",
					"div(<x>,<x>) -> 1",
					"mul(<x>,0) -> 0",
					"mul(0,<x>) -> 0",
					"mul(<x>,1) -> <x>",
					"mul(1,<x>) -> <x>",
					"mul(<x>,<x>) -> pow(<x>,2)",
					"pow(<x>,0) -> 1",
					"pow(<x>,1) -> <x>")
				.map(TreeRewriteRule::compile)
				.map(rule -> new RuleTreeRewriter<>(
					rule, MathOp::equals, MathOp::convert));

		return ruleRewriter.prepend(ISeq.of(ConstExprRewriter.REWRITER));
	}

	private static final MathExprRewriter INSTANCE = new MathExprRewriter();

	@Override
	public boolean rewrite(final TreeNode<Op<Double>> tree) {
		return TreeRewriter.rewrite(tree, REWRITERS);
	}

	static TreeNode<Op<Double>> prune(final TreeNode<Op<Double>> tree) {
		INSTANCE.rewrite(tree);
		return tree;
	}

}
