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

import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import io.jenetics.util.ISeq;

import io.jenetics.ext.util.Tree;

/**
 * This class allows you to create a tree from an expression string.
 *
 * <pre>{@code
 * final MathOpExpr expr = MathOpExpr.parse("5 + 6*x + sin(x)^34 + (1 + sin(x*5)/4)/6");
 * final double result = expr.eval(4.32);
 * assert result == 31.170600453465315;
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class MathOpExpr implements Function<double[], Double> {

	private final Tree<? extends Op<Double>, ?> _tree;

	/**
	 * Create a new {@code MathOpExpr} object from the given operation tree.
	 *
	 * @param tree the underlying operation tree
	 * @throws NullPointerException if the given {@code program} is {@code null}
	 * @throws IllegalArgumentException if the given operation tree is invalid,
	 *         which means there is at least one node where the operation arity
	 *         and the node child count differ.
	 */
	public MathOpExpr(final Tree<? extends Op<Double>, ?> tree) {
		Program.check(tree);
		_tree = tree;
	}

	/**
	 * Return the variable list of this <em>math</em> expression.
	 *
	 * @return the variable list of this <em>math</em> expression
	 */
	public ISeq<Var<Double>> vars() {
		return ISeq.of(
			_tree.stream()
				.filter(node -> node.getValue() instanceof Var<?>)
				.map(node -> (Var<Double>)node.getValue())
				.collect(Collectors.toCollection(() ->
					new TreeSet<>(Comparator.comparing(Var::name))))
		);
	}

	/**
	 * Return the underlying expression tree.
	 *
	 * @return the underlying expression tree
	 */
	public Tree<? extends Op<Double>, ?> tree() {
		return _tree;
	}

	@Override
	public Double apply(final double[] args) {
		return Program.eval(
			_tree,
			DoubleStream.of(args)
				.boxed()
				.toArray(Double[]::new)
		);
	}

	/**
	 * Convenient method, which lets you apply the program function without
	 * explicitly create a wrapper array.
	 *
	 * <pre>{@code
	 *  final double result = MathOpExpr.parse("2*z + 3*x - y").eval(3, 2, 1);
	 *  assert result == 9.0;
	 * }</pre>
	 *
	 * @see #apply(double[])
	 *
	 * @param args the function arguments
	 * @return the evaluated value
	 * @throws NullPointerException if the given variable array is {@code null}
	 * @throws IllegalArgumentException if the length of the arguments array
	 *         is smaller than the program arity
	 */
	public double eval(final double... args) {
		return apply(args);
	}

	/**
	 * Return the string representation of this {@code MathOpExpr} object. The
	 * string returned by this method can be parsed again and will result in the
	 * same expression object.
	 * <pre>{@code
	 *  final String expr = "5.0 + 6.0*x + sin(x)^34.0 + (1.0 + sin(x*5.0)/4.0) + 6.5";
	 *  final MathOpExpr tree = MathOpExpr.parse(expr);
	 *  assert tree.toString().equals(expr);
	 * }</pre>
	 *
	 * @return the expression string
	 */
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
			infix(" + ", tree, out);
		} else if (op == MathOp.SUB) {
			infix(" - ", tree, out);
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
			out.append(op);
			if (!tree.isLeaf()) {
				out.append("(");
				toString(tree.getChild(0), out);
				for (int i = 1; i < tree.childCount(); ++i) {
					out.append(", ");
					toString(tree.getChild(i), out);
				}
				out.append(")");
			}
		}

		return out.toString();
	}

	private static void infix(
		final String op,
		final Tree<? extends Op<Double>, ?> tree,
		final StringBuilder out
	) {
		final boolean first = out.length() == 0;
		if (!first) out.append("(");
		toString(tree.getChild(0), out);
		out.append(" + ");
		toString(tree.getChild(1), out);
		if (!first) out.append(")");
	}


	/* *************************************************************************
	 * Static helper methods.
	 * ************************************************************************/

	/**
	 * Parses the given {@code expression} into a AST tree.
	 *
	 * @param expression the expression string
	 * @return the tree representation of the given {@code expression}
	 */
	public static MathOpExpr parse(final String expression) {
		return new MathOpExpr(Parser.parse(expression));
	}

	/**
	 * Evaluates the given {@code expression} with the given arguments.
	 *
	 * <pre>{@code
	 *  final double result = MathOpExpr.eval("2*z + 3*x - y", 3, 2, 1);
	 *  assert result == 9.0;
	 * }</pre>
	 *
	 * @param expression the expression to evaluate
	 * @param args the expression arguments, in alphabetical order
	 * @return the evaluation result
	 * @throws NullPointerException if the given {@code program} is {@code null}
	 * @throws IllegalArgumentException if the given operation tree is invalid,
	 *         which means there is at least one node where the operation arity
	 *         and the node child count differ.
	 */
	public static double eval(final String expression, final double... args) {
		return parse(expression).apply(args);
	}

}
