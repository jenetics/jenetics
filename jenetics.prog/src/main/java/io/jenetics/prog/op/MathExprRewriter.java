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

import io.jenetics.ext.rewriting.TreeRewriteRule;
import io.jenetics.ext.rewriting.TreeRewriter;
import io.jenetics.ext.rewriting.TreeRewriters;
import io.jenetics.ext.util.TreeNode;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 4.1
 */
final class MathExprRewriter implements TreeRewriter<Op<Double>> {

	private static final ISeq<TreeRewriter<Op<Double>>> REWRITERS = ISeq.of(
		compile("sub($x,$x) -> 0"),
		compile("add($x,$x) -> mul(2,$x)"),
		compile("sub($x,0) -> $x"),
		compile("add($x,0) -> $x"),
		compile("add(0,$x) -> $x"),
		compile("div($x,$x) -> 1"),
		compile("mul($x,0) -> 0"),
		compile("mul(0,$x) -> 0"),
		compile("mul($x,1) -> $x"),
		compile("mul(1,$x) -> $x"),
		compile("mul($x,$x) -> pow($x,2)"),
		compile("pow($x,0) -> 1"),
		compile("pow($x,1) -> $x"),
		ConstExpr::rewrite
	);

	private static TreeRewriter<Op<Double>> compile(final String rule) {
		return TreeRewriteRule.compile(rule, MathOp::toMathOp);
	}

	private static final MathExprRewriter INSTANCE = new MathExprRewriter();

	@Override
	public int rewrite(final TreeNode<Op<Double>> tree, final int limit) {
		return TreeRewriters.rewrite(tree, limit, REWRITERS);
	}

	static TreeNode<Op<Double>> prune(final TreeNode<Op<Double>> tree) {
		INSTANCE.rewrite(tree);
		return tree;
	}

}
