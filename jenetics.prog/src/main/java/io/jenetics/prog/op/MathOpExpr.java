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

import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import io.jenetics.util.ISeq;

import io.jenetics.ext.util.Tree;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class MathOpExpr implements Function<double[], Double> {

	private final Tree<? extends Op<Double>, ?> _tree;
	private final ISeq<Var<Double>> _vars;

	public MathOpExpr(final Tree<? extends Op<Double>, ?> tree) {
		_tree = requireNonNull(tree);
		_vars = ISeq.of(
			tree.stream()
				.filter(node -> node.getValue() instanceof Var<?>)
				.map(node -> (Var<Double>)node.getValue())
				.collect(Collectors.toCollection(() ->
					new TreeSet<>(Comparator.comparing(Var::name))))
		);
	}

	public ISeq<Var<Double>> vars() {
		return _vars;
	}

	public Tree<? extends Op<Double>, ?> tree() {
		return _tree;
	}

	@Override
	public Double apply(final double... args) {
		return Program.eval(
			_tree,
			DoubleStream.of(args)
				.boxed()
				.toArray(Double[]::new)
		);
	}

	@Override
	public String toString() {
		return toString(_tree, new StringBuilder());
	}

	private static String toString(
		final Tree<? extends Op<Double>, ?> tree,
		final StringBuilder out
	) {
		final Op<Double> op = tree.getValue();
		if (op == MathOp.ADD) {
			out.append("(");
			toString(tree.getChild(0), out);
			out.append(" + ");
			toString(tree.getChild(1), out);
			out.append(")");
		} else if (op == MathOp.SUB) {
			out.append("(");
			toString(tree.getChild(0), out);
			out.append(" - ");
			toString(tree.getChild(1), out);
			out.append(")");
		}  else if (op == MathOp.MUL) {
			toString(tree.getChild(0), out);
			out.append("*");
			toString(tree.getChild(1), out);
		} else if (op == MathOp.DIV) {
			toString(tree.getChild(0), out);
			out.append("/");
			toString(tree.getChild(1), out);
		}  else if (op == MathOp.MOD) {
			toString(tree.getChild(0), out);
			out.append("%");
			toString(tree.getChild(1), out);
		} else if (op == MathOp.POW) {
			toString(tree.getChild(0), out);
			out.append("^");
			toString(tree.getChild(1), out);
		} else {
			out.append(tree.getValue());
			if (!tree.isLeaf()) {
				out.append("(");
				toString(tree.getChild(0), out);
				for (int i = 1; i < tree.childCount(); ++i) {
					out.append(",");
					toString(tree.getChild(i), out);
				}
				out.append(")");
			}
		}

		return out.toString();
	}

	/**
	 * Parses the given {@code expression} into a AST tree.
	 *
	 * @param expression the expression string
	 * @return the tree representation of the given {@code expression}
	 */
	public static MathOpExpr parse(final String expression) {
		return new MathOpExpr(Parser.parse(expression));
	}

	public static double eval(final String expression, final double... args) {
		return parse(expression).apply(args);
	}

}
