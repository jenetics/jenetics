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

import java.util.Map;

import io.jenetics.ext.util.Tree;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.3
 * @since 4.3
 */
final class MathExprFormatter {
	private MathExprFormatter() {}

	private static final Map<Op<Double>, String> INFIX_OPS = Map.of(
		MathOp.ADD, " + ",
		MathOp.SUB, " - ",
		MathOp.MUL, "*",
		MathOp.DIV, "/",
		MathOp.MOD, "%",
		MathOp.POW, "^"
	);

	private static final Map<Op<Double>, Integer> PRECEDENCE = Map.of(
		MathOp.ADD, 6,
		MathOp.SUB, 6,
		MathOp.MUL, 5,
		MathOp.DIV, 5,
		MathOp.MOD, 5,
		MathOp.POW, 4
	);

	static String format(final Tree<? extends Op<Double>, ?> tree) {
		final StringBuilder out = new StringBuilder();
		format(tree, out);
		return out.toString();
	}

	private static void format(
		final Tree<? extends Op<Double>, ?> tree,
		final StringBuilder out
	) {
		final Op<Double> op = tree.value();
		if (INFIX_OPS.containsKey(op)) {
			infix(tree, out);
		} else {
			out.append(op);
			if (!tree.isLeaf()) {
				out.append("(");
				format(tree.childAt(0), out);
				for (int i = 1; i < tree.childCount(); ++i) {
					out.append(", ");
					format(tree.childAt(i), out);
				}
				out.append(")");
			}
		}
	}

	private static void infix(
		final Tree<? extends Op<Double>, ?> tree,
		final StringBuilder out
	) {
		assert tree.childCount() == 2;

		final int precedence = PRECEDENCE.getOrDefault(tree.value(), 100);
		final int parentPrecedence = tree.parent()
			.map(p -> PRECEDENCE.getOrDefault(p.value(), 100))
			.orElse(100);

		final boolean brackets = !tree.isRoot() && precedence >= parentPrecedence;

		if (brackets) out.append("(");
		format(tree.childAt(0), out);
		out.append(INFIX_OPS.get(tree.value()));
		format(tree.childAt(1), out);
		if (brackets) out.append(")");
	}

}
